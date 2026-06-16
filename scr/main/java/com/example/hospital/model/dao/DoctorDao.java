package com.example.hospital.model.dao;

import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.dao.exception.DaoException; // Важливо
import java.util.List;
import java.util.Optional;

public interface DoctorDao {
    Optional<Doctor> findById(Long id) throws DaoException;
    List<Doctor> findAll() throws DaoException;
    Doctor save(Doctor doctor) throws DaoException; // Повертає збереженого доктора з ID
    void update(Doctor doctor) throws DaoException;
    void deleteById(Long id) throws DaoException;
}