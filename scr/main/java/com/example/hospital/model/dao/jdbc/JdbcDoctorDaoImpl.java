package com.example.hospital.model.dao.jdbc;

import com.example.hospital.model.dao.DoctorDao;
import com.example.hospital.model.dao.DaoConnection;
import com.example.hospital.model.dao.exception.DaoException;
import com.example.hospital.model.entity.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDoctorDaoImpl implements DoctorDao {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM doctors WHERE id = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM doctors ORDER BY last_name, first_name";
    private static final String SQL_SAVE = "INSERT INTO doctors (first_name, last_name, specialization) VALUES (?, ?, ?) RETURNING id"; // Для PostgreSQL
    // Для MySQL: "INSERT INTO doctors (first_name, last_name, specialization) VALUES (?, ?, ?)"
    // потім окремий запит для Statement.RETURN_GENERATED_KEYS
    private static final String SQL_UPDATE = "UPDATE doctors SET first_name = ?, last_name = ?, specialization = ? WHERE id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM doctors WHERE id = ?";

    @Override
    public Optional<Doctor> findById(Long id) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapDoctor(resultSet));
                }
            }
        } catch (SQLException e) {
            // Логування помилки
            throw new DaoException("Error finding doctor by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Doctor> findAll() throws DaoException {
        List<Doctor> doctors = new ArrayList<>();
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding all doctors", e);
        }
        return doctors;
    }

    @Override
    public Doctor save(Doctor doctor) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) { // Для PostgreSQL RETURNING id більш прямолінійний
            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getSpecialization());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Creating doctor failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    doctor.setId(generatedKeys.getLong(1)); // У PostgreSQL це буде з RETURNING id
                } else {
                    // Для MySQL, якщо getGeneratedKeys не спрацював як очікувалося
                    throw new DaoException("Creating doctor failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error saving doctor: " + doctor, e);
        }
        return doctor;
    }

    @Override
    public void update(Doctor doctor) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getSpecialization());
            statement.setLong(4, doctor.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Updating doctor failed, no rows affected. Doctor ID: " + doctor.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error updating doctor: " + doctor, e);
        }
    }

    @Override
    public void deleteById(Long id) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                // Можливо, не помилка, а просто такого ID не існує
                // throw new DaoException("Deleting doctor failed, no rows affected. Doctor ID: " + id);
                System.out.println("No doctor found with ID " + id + " to delete or already deleted.");
            }
        } catch (SQLException e) {
            // Може бути помилка через foreign key constraints, якщо лікар має записи
            throw new DaoException("Error deleting doctor by id: " + id, e);
        }
    }

    private Doctor mapDoctor(ResultSet resultSet) throws SQLException {
        return new Doctor(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("specialization")
        );
    }
}