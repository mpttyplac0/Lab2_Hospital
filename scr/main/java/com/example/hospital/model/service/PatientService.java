package com.example.hospital.model.service;

import com.example.hospital.model.dao.PatientDao;
import com.example.hospital.model.dao.DaoFactory;
import com.example.hospital.model.dao.exception.DaoException;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.exception.ServiceException; // Виняток сервісного шару

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class PatientService {
    private final PatientDao patientDao;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯіІїЇєЄґҐ'\\- ]{2,50}$");


    public PatientService() {
        this.patientDao = DaoFactory.getInstance().createPatientDao();
    }

    public PatientService(PatientDao patientDao) {
        this.patientDao = patientDao;
    }


    public Optional<Patient> findPatientById(Long id) throws ServiceException {
        if (id == null || id <= 0) {
            throw new ServiceException("Patient ID must be a positive number.");
        }
        try {
            return patientDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Error finding patient by ID: " + id, e);
        }
    }


    public List<Patient> findAllPatients() throws ServiceException {
        try {
            return patientDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Error finding all patients", e);
        }
    }

    public Patient createPatient(String firstName, String lastName, int age, String gender) throws ServiceException {
        validatePatientData(firstName, lastName, age, gender, null); // null для id, бо це створення

        Patient patient = new Patient(firstName.trim(), lastName.trim(), age, gender != null ? gender.trim() : null);
        try {
            return patientDao.save(patient);
        } catch (DaoException e) {
            throw new ServiceException("Error creating patient: " + patient, e);
        }
    }

    public void updatePatient(Long id, String newFirstName, String newLastName, Integer newAge, String newGender) throws ServiceException {
        if (id == null || id <= 0) {
            throw new ServiceException("Patient ID for update must be a positive number.");
        }
        try {
            Patient patient = patientDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Patient not found with ID: " + id + " for update."));

            String fnToValidate = newFirstName != null ? newFirstName : patient.getFirstName();
            String lnToValidate = newLastName != null ? newLastName : patient.getLastName();
            int ageToValidate = newAge != null && newAge > 0 ? newAge : patient.getAge();
            String genderToValidate = newGender != null ? newGender : patient.getGender();

            validatePatientData(fnToValidate, lnToValidate, ageToValidate, genderToValidate, id);


            boolean changed = false;
            if (newFirstName != null && !newFirstName.trim().isEmpty() && !patient.getFirstName().equals(newFirstName.trim())) {
                patient.setFirstName(newFirstName.trim());
                changed = true;
            }
            if (newLastName != null && !newLastName.trim().isEmpty() && !patient.getLastName().equals(newLastName.trim())) {
                patient.setLastName(newLastName.trim());
                changed = true;
            }
            if (newAge != null && newAge > 0 && patient.getAge() != newAge) {
                patient.setAge(newAge);
                changed = true;
            }
            if (newGender != null && !newGender.trim().isEmpty() && (patient.getGender() == null || !patient.getGender().equals(newGender.trim()))) {
                patient.setGender(newGender.trim());
                changed = true;
            }

            if (changed) {
                patientDao.update(patient);
            } else {
                System.out.println("No changes detected for patient with ID: " + id);
            }
        } catch (DaoException e) {
            throw new ServiceException("Error updating patient with ID: " + id, e);
        }
    }

    public void deletePatient(Long id) throws ServiceException {
        if (id == null || id <= 0) {
            throw new ServiceException("Patient ID for delete must be a positive number.");
        }
        try {
            patientDao.deleteById(id);
        } catch (DaoException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key constraint")) {
                throw new ServiceException("Cannot delete patient with ID: " + id + " because they have related records (e.g., appointments).", e);
            }
            throw new ServiceException("Error deleting patient with ID: " + id, e);
        }
    }


    private void validatePatientData(String firstName, String lastName, int age, String gender, Long existingId) throws ServiceException {
        if (firstName == null || firstName.trim().isEmpty() || !NAME_PATTERN.matcher(firstName.trim()).matches()) {
            throw new ServiceException("First name is invalid. It must be 2-50 characters long and contain only letters, spaces, hyphens, or apostrophes.");
        }
        if (lastName == null || lastName.trim().isEmpty() || !NAME_PATTERN.matcher(lastName.trim()).matches()) {
            throw new ServiceException("Last name is invalid. It must be 2-50 characters long and contain only letters, spaces, hyphens, or apostrophes.");
        }
        if (age <= 0 || age > 120) { // Припустимі межі віку
            throw new ServiceException("Age must be between 1 and 120.");
        }
        if (gender != null && !gender.trim().isEmpty() && gender.trim().length() > 20) {
            throw new ServiceException("Gender field is too long (max 20 characters).");
        }

    }
}