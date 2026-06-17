package com.example.hospital.model.service;

import com.example.hospital.model.dao.DoctorDao;
import com.example.hospital.model.dao.DaoFactory;
import com.example.hospital.model.dao.exception.DaoException;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.service.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public class DoctorService {
    private final DoctorDao doctorDao;

    public DoctorService() {
        // Отримання DAO через фабрику
        this.doctorDao = DaoFactory.getInstance().createDoctorDao();
    }

    // Конструктор для можливості тестування з mock DAO
    public DoctorService(DoctorDao doctorDao) {
        this.doctorDao = doctorDao;
    }

    public Optional<Doctor> findDoctorById(Long id) throws ServiceException {
        try {
            return doctorDao.findById(id);
        } catch (DaoException e) {
            // Логування помилки
            throw new ServiceException("Error finding doctor by ID: " + id, e);
        }
    }

    public List<Doctor> findAllDoctors() throws ServiceException {
        try {
            return doctorDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Error finding all doctors", e);
        }
    }

    public Doctor createDoctor(String firstName, String lastName, String specialization) throws ServiceException {
        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                specialization == null || specialization.trim().isEmpty()) {
            throw new ServiceException("First name, last name, and specialization cannot be empty.");
        }
        Doctor doctor = new Doctor(firstName.trim(), lastName.trim(), specialization.trim());
        try {
            return doctorDao.save(doctor);
        } catch (DaoException e) {
            throw new ServiceException("Error creating doctor: " + doctor, e);
        }
    }

    public void updateDoctor(Long id, String newFirstName, String newLastName, String newSpecialization) throws ServiceException {
        try {
            Doctor doctor = doctorDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Doctor not found with ID: " + id + " for update."));

            boolean changed = false;
            if (newFirstName != null && !newFirstName.trim().isEmpty() && !doctor.getFirstName().equals(newFirstName.trim())) {
                doctor.setFirstName(newFirstName.trim());
                changed = true;
            }
            if (newLastName != null && !newLastName.trim().isEmpty() && !doctor.getLastName().equals(newLastName.trim())) {
                doctor.setLastName(newLastName.trim());
                changed = true;
            }
            if (newSpecialization != null && !newSpecialization.trim().isEmpty() && !doctor.getSpecialization().equals(newSpecialization.trim())) {
                doctor.setSpecialization(newSpecialization.trim());
                changed = true;
            }

            if (changed) {
                doctorDao.update(doctor);
            }
        } catch (DaoException e) {
            throw new ServiceException("Error updating doctor with ID: " + id, e);
        }
    }

    public void deleteDoctor(Long id) throws ServiceException {
        try {
            doctorDao.deleteById(id);
        } catch (DaoException e) {
            throw new ServiceException("Error deleting doctor with ID: " + id, e);
        }
    }
}