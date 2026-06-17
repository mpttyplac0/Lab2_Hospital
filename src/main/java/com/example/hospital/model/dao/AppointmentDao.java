package com.example.hospital.model.dao;

import com.example.hospital.model.entity.Appointment;
import com.example.hospital.model.dao.exception.DaoException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    Optional<Appointment> findById(Long id) throws DaoException;
    List<Appointment> findAll() throws DaoException;
    List<Appointment> findByDoctorId(Long doctorId) throws DaoException;
    List<Appointment> findByPatientId(Long patientId) throws DaoException;
    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime dateTime) throws DaoException;
    Appointment save(Appointment appointment) throws DaoException;
    void update(Appointment appointment) throws DaoException;
    void deleteById(Long id) throws DaoException;
}