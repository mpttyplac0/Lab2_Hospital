package com.example.hospital.model.dao.jdbc;

import com.example.hospital.model.dao.PatientDao;
import com.example.hospital.model.dao.DaoConnection;
import com.example.hospital.model.dao.exception.DaoException;
import com.example.hospital.model.entity.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPatientDaoImpl implements PatientDao {

    // SQL запити для Patient
    private static final String SQL_FIND_PATIENT_BY_ID = "SELECT * FROM patients WHERE id = ?";
    private static final String SQL_FIND_ALL_PATIENTS = "SELECT * FROM patients ORDER BY last_name, first_name";
    private static final String SQL_SAVE_PATIENT = "INSERT INTO patients (first_name, last_name, age, gender) VALUES (?, ?, ?, ?) RETURNING id"; // Для PostgreSQL
    // Для MySQL: "INSERT INTO patients (first_name, last_name, age, gender) VALUES (?, ?, ?, ?)"
    private static final String SQL_UPDATE_PATIENT = "UPDATE patients SET first_name = ?, last_name = ?, age = ?, gender = ? WHERE id = ?";
    private static final String SQL_DELETE_PATIENT_BY_ID = "DELETE FROM patients WHERE id = ?";

    @Override
    public Optional<Patient> findById(Long id) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_PATIENT_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapPatient(resultSet));
                }
            }
        } catch (SQLException e) {
            // Логування помилки (використовуйте ваш логер)
            // Logger.error("Error finding patient by id: " + id, e);
            throw new DaoException("Error finding patient by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() throws DaoException {
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL_PATIENTS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                patients.add(mapPatient(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding all patients", e);
        }
        return patients;
    }

    @Override
    public Patient save(Patient patient) throws DaoException {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null for save operation.");
        }
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE_PATIENT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, patient.getFirstName());
            statement.setString(2, patient.getLastName());
            statement.setInt(3, patient.getAge());
            statement.setString(4, patient.getGender());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Creating patient failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("Creating patient failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error saving patient: " + patient, e);
        }
        return patient;
    }

    @Override
    public void update(Patient patient) throws DaoException {
        if (patient == null || patient.getId() == null) {
            throw new IllegalArgumentException("Patient and its ID cannot be null for update operation.");
        }
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_PATIENT)) {
            statement.setString(1, patient.getFirstName());
            statement.setString(2, patient.getLastName());
            statement.setInt(3, patient.getAge());
            statement.setString(4, patient.getGender());
            statement.setLong(5, patient.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Updating patient failed, no rows affected. Patient ID: " + patient.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error updating patient: " + patient, e);
        }
    }

    @Override
    public void deleteById(Long id) throws DaoException {
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null for delete operation.");
        }
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_PATIENT_BY_ID)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                // Це не обов'язково помилка, можливо, пацієнта з таким ID просто немає
                System.out.println("No patient found with ID " + id + " to delete or already deleted.");
            }
        } catch (SQLException e) {
            // Може виникнути помилка через зовнішні ключі (наприклад, якщо пацієнт має записи на прийом)
            // Потрібно обробляти такі випадки (наприклад, не дозволяти видалення або каскадне видалення)
            throw new DaoException("Error deleting patient by id: " + id + ". Check for related appointments.", e);
        }
    }

    /**
     * Допоміжний метод для перетворення ResultSet на об'єкт Patient.
     * @param resultSet ResultSet з даними пацієнта
     * @return об'єкт Patient
     * @throws SQLException якщо виникає помилка при читанні даних з ResultSet
     */
    private Patient mapPatient(ResultSet resultSet) throws SQLException {
        return new Patient(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getInt("age"),
                resultSet.getString("gender")
        );
    }
}