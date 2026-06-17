package com.example.hospital.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Appointment {
    private Long id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentTime;

    public Appointment() {
    }

    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
    }

    public Appointment(Long id, Patient patient, Doctor doctor, LocalDateTime appointmentTime) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        return Objects.equals(patient, that.patient) && // Порівняння посилань або реалізація equals в Patient/Doctor
                Objects.equals(doctor, that.doctor) &&
                Objects.equals(appointmentTime, that.appointmentTime);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(patient, doctor, appointmentTime);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patient=" + (patient != null ? patient.getFirstName() + " " + patient.getLastName() : "N/A") +
                ", doctor=" + (doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "N/A") +
                ", appointmentTime=" + appointmentTime +
                '}';
    }
}