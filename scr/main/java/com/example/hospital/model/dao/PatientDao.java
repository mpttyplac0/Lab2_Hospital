package com.example.hospital.model.dao;

import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.dao.exception.DaoException;
import java.util.List;
import java.util.Optional;

public interface PatientDao {
    Optional<Patient> findById(Long id) throws DaoException;
    List<Patient> findAll() throws DaoException;
    Patient save(Patient patient) throws DaoException;
    void update(Patient patient) throws DaoException;
    void deleteById(Long id) throws DaoException;
}