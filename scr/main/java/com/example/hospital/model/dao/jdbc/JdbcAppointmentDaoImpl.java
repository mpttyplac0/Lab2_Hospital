package com.example.hospital.model.dao.jdbc;

import com.example.hospital.model.dao.AppointmentDao;
import com.example.hospital.model.dao.DoctorDao; // Для завантаження Doctor
import com.example.hospital.model.dao.PatientDao; // Для завантаження Patient
import com.example.hospital.model.dao.DaoConnection;
import com.example.hospital.model.dao.exception.DaoException;
import com.example.hospital.model.entity.Appointment;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.entity.Patient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAppointmentDaoImpl implements AppointmentDao {

    private final DoctorDao doctorDao;
    private final PatientDao patientDao;

    // SQL запити для Appointment
    private static final String SQL_FIND_APPOINTMENT_BY_ID = "SELECT * FROM appointments WHERE id = ?";
    private static final String SQL_FIND_ALL_APPOINTMENTS = "SELECT * FROM appointments ORDER BY appointment_time DESC";
    private static final String SQL_FIND_APPOINTMENTS_BY_DOCTOR_ID = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_time";
    private static final String SQL_FIND_APPOINTMENTS_BY_PATIENT_ID = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_time";
    private static final String SQL_EXISTS_BY_DOCTOR_AND_TIME = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_time = ?";
    private static final String SQL_SAVE_APPOINTMENT = "INSERT INTO appointments (doctor_id, patient_id, appointment_time) VALUES (?, ?, ?) RETURNING id";
    private static final String SQL_UPDATE_APPOINTMENT = "UPDATE appointments SET doctor_id = ?, patient_id = ?, appointment_time = ? WHERE id = ?";
    private static final String SQL_DELETE_APPOINTMENT_BY_ID = "DELETE FROM appointments WHERE id = ?";


    public JdbcAppointmentDaoImpl(DoctorDao doctorDao, PatientDao patientDao) {
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
    }

    @Override
    public Optional<Appointment> findById(Long id) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_APPOINTMENT_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding appointment by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() throws DaoException {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL_APPOINTMENTS);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointment(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding all appointments", e);
        }
        return appointments;
    }

    @Override
    public List<Appointment> findByDoctorId(Long doctorId) throws DaoException {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_APPOINTMENTS_BY_DOCTOR_ID)) {
            statement.setLong(1, doctorId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding appointments by doctor id: " + doctorId, e);
        }
        return appointments;
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) throws DaoException {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_APPOINTMENTS_BY_PATIENT_ID)) {
            statement.setLong(1, patientId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding appointments by patient id: " + patientId, e);
        }
        return appointments;
    }


    @Override
    public boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime dateTime) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_EXISTS_BY_DOCTOR_AND_TIME)) {
            statement.setLong(1, doctorId);
            statement.setTimestamp(2, Timestamp.valueOf(dateTime));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error checking appointment existence for doctor " + doctorId + " at " + dateTime, e);
        }
        return false;
    }

    @Override
    public Appointment save(Appointment appointment) throws DaoException {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null ||
                appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            throw new DaoException("Doctor and Patient must be set and have IDs before saving appointment.");
        }
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SAVE_APPOINTMENT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, appointment.getDoctor().getId());
            statement.setLong(2, appointment.getPatient().getId());
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Creating appointment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appointment.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("Creating appointment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error saving appointment: " + appointment, e);
        }
        return appointment;
    }

    @Override
    public void update(Appointment appointment) throws DaoException {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null ||
                appointment.getPatient() == null || appointment.getPatient().getId() == null ||
                appointment.getId() == null) {
            throw new DaoException("Appointment, Doctor and Patient must be set and have IDs before updating appointment.");
        }
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_APPOINTMENT)) {
            statement.setLong(1, appointment.getDoctor().getId());
            statement.setLong(2, appointment.getPatient().getId());
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            statement.setLong(4, appointment.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Updating appointment failed, no rows affected. Appointment ID: " + appointment.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error updating appointment: " + appointment, e);
        }
    }

    @Override
    public void deleteById(Long id) throws DaoException {
        try (Connection connection = DaoConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_APPOINTMENT_BY_ID)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("No appointment found with ID " + id + " to delete or already deleted.");
            }
        } catch (SQLException e) {
            throw new DaoException("Error deleting appointment by id: " + id, e);
        }
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException, DaoException {
        Long appointmentId = rs.getLong("id");
        Long doctorId = rs.getLong("doctor_id");
        Long patientId = rs.getLong("patient_id");
        LocalDateTime appointmentTime = rs.getTimestamp("appointment_time").toLocalDateTime();

        // Завантаження пов'язаних сутностей Doctor та Patient
        // Обережно: це може призвести до проблеми N+1, якщо не оптимізовано
        // Для простих випадків це нормально, для складних - потрібні JOIN або інші стратегії
        Doctor doctor = doctorDao.findById(doctorId)
                .orElseThrow(() -> new DaoException("Doctor not found for appointment, doctorId: " + doctorId));
        Patient patient = patientDao.findById(patientId)
                .orElseThrow(() -> new DaoException("Patient not found for appointment, patientId: " + patientId));

        return new Appointment(appointmentId, patient, doctor, appointmentTime);
    }
}