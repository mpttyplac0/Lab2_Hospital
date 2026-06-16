package com.example.hospital.model.service;

import com.example.hospital.model.dao.AppointmentDao;
import com.example.hospital.model.dao.DoctorDao;
import com.example.hospital.model.dao.PatientDao;
import com.example.hospital.model.dao.DaoFactory;
import com.example.hospital.model.dao.exception.DaoException;
import com.example.hospital.model.entity.Appointment;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final DoctorDao doctorDao;
    private final PatientDao patientDao;


    public AppointmentService() {
        DaoFactory daoFactory = DaoFactory.getInstance();
        this.appointmentDao = daoFactory.createAppointmentDao();
        this.doctorDao = daoFactory.createDoctorDao();
        this.patientDao = daoFactory.createPatientDao();
    }

    public AppointmentService(AppointmentDao appointmentDao, DoctorDao doctorDao, PatientDao patientDao) {
        this.appointmentDao = appointmentDao;
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
    }

    public Appointment createAppointment(Long patientId, Long doctorId, LocalDateTime appointmentTime) throws ServiceException {
        if (patientId == null || doctorId == null || appointmentTime == null) {
            throw new ServiceException("Patient ID, Doctor ID, and appointment time cannot be null.");
        }
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new ServiceException("Cannot schedule an appointment in the past.");
        }

        try {
            Patient patient = patientDao.findById(patientId)
                    .orElseThrow(() -> new ServiceException("Patient not found with ID: " + patientId));
            Doctor doctor = doctorDao.findById(doctorId)
                    .orElseThrow(() -> new ServiceException("Doctor not found with ID: " + doctorId));

            if (!isDoctorAvailable(doctorId, appointmentTime)) {
                throw new ServiceException("Doctor " + doctor.getFirstName() + " " + doctor.getLastName() +
                        " is not available at " + appointmentTime);
            }

            Appointment appointment = new Appointment(patient, doctor, appointmentTime);
            return appointmentDao.save(appointment);
        } catch (DaoException e) {
            throw new ServiceException("Error creating appointment.", e);
        }
    }

    public Optional<Appointment> findAppointmentById(Long id) throws ServiceException {
        try {
            return appointmentDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Error finding appointment by ID: " + id, e);
        }
    }

    public List<Appointment> findAllAppointments() throws ServiceException {
        try {
            return appointmentDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Error finding all appointments.", e);
        }
    }

    public List<Appointment> findAppointmentsByDoctor(Long doctorId) throws ServiceException {
        if (doctorId == null) {
            throw new ServiceException("Doctor ID cannot be null.");
        }
        try {
            return appointmentDao.findByDoctorId(doctorId);
        } catch (DaoException e) {
            throw new ServiceException("Error finding appointments for doctor ID: " + doctorId, e);
        }
    }

    public List<Appointment> findAppointmentsByPatient(Long patientId) throws ServiceException {
        if (patientId == null) {
            throw new ServiceException("Patient ID cannot be null.");
        }
        try {
            return appointmentDao.findByPatientId(patientId);
        } catch (DaoException e) {
            throw new ServiceException("Error finding appointments for patient ID: " + patientId, e);
        }
    }


    public void updateAppointment(Long appointmentId, Long newPatientId, Long newDoctorId, LocalDateTime newAppointmentTime) throws ServiceException {
        if (appointmentId == null) {
            throw new ServiceException("Appointment ID for update cannot be null.");
        }
        try {
            Appointment appointment = appointmentDao.findById(appointmentId)
                    .orElseThrow(() -> new ServiceException("Appointment not found with ID: " + appointmentId));

            if (newPatientId != null && (appointment.getPatient() == null || !newPatientId.equals(appointment.getPatient().getId()))) {
                Patient newPatient = patientDao.findById(newPatientId)
                        .orElseThrow(() -> new ServiceException("New patient not found with ID: " + newPatientId));
                appointment.setPatient(newPatient);
            }

            Doctor currentDoctor = appointment.getDoctor();
            LocalDateTime currentTime = appointment.getAppointmentTime();
            boolean doctorOrTimeChanged = false;

            if (newDoctorId != null && (currentDoctor == null || !newDoctorId.equals(currentDoctor.getId()))) {
                Doctor newDoctor = doctorDao.findById(newDoctorId)
                        .orElseThrow(() -> new ServiceException("New doctor not found with ID: " + newDoctorId));
                appointment.setDoctor(newDoctor);
                doctorOrTimeChanged = true;
            }
            if (newAppointmentTime != null && !newAppointmentTime.equals(currentTime)) {
                if (newAppointmentTime.isBefore(LocalDateTime.now())) {
                    throw new ServiceException("Cannot update an appointment to a past time.");
                }
                appointment.setAppointmentTime(newAppointmentTime);
                doctorOrTimeChanged = true;
            }

            if (doctorOrTimeChanged) {
                List<Appointment> existingAppointments = appointmentDao.findByDoctorId(appointment.getDoctor().getId());
                for(Appointment existing : existingAppointments){
                    if(!existing.getId().equals(appointmentId) && // не перевіряти себе
                            existing.getAppointmentTime().equals(appointment.getAppointmentTime())){
                        throw new ServiceException("The new time slot is already booked for doctor " +
                                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
                    }
                }
            }
            appointmentDao.update(appointment);
        } catch (DaoException e) {
            throw new ServiceException("Error updating appointment with ID: " + appointmentId, e);
        }
    }


    public void cancelAppointment(Long appointmentId) throws ServiceException {
        if (appointmentId == null) {
            throw new ServiceException("Appointment ID for cancellation cannot be null.");
        }
        try {
            Optional<Appointment> appointmentOpt = appointmentDao.findById(appointmentId);
            if (appointmentOpt.isPresent() && appointmentOpt.get().getAppointmentTime().isBefore(LocalDateTime.now())) {
            }
            appointmentDao.deleteById(appointmentId); // Або оновити статус на 'CANCELLED'
        } catch (DaoException e) {
            throw new ServiceException("Error cancelling appointment with ID: " + appointmentId, e);
        }
    }

    public boolean isDoctorAvailable(Long doctorId, LocalDateTime dateTime) throws ServiceException {
        if (doctorId == null || dateTime == null) {
            throw new ServiceException("Doctor ID and date/time must be provided to check availability.");
        }
        try {
            return !appointmentDao.existsByDoctorIdAndAppointmentTime(doctorId, dateTime);
        } catch (DaoException e) {
            throw new ServiceException("Error checking doctor availability.", e);
        }
    }
}