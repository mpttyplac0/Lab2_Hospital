package com.example.hospital.controller.command.appointment;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ShowNewAppointmentFormCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowNewAppointmentFormCommand.class);
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ShowNewAppointmentFormCommand() {
        this.doctorService = new DoctorService();
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        logger.info("Executing ShowNewAppointmentFormCommand");
        try {
            List<Doctor> doctors = doctorService.findAllDoctors();
            List<Patient> patients = patientService.findAllPatients();
            request.setAttribute("doctors", doctors);
            request.setAttribute("patients", patients);
            request.setAttribute("formAction", "create-appointment");
            return "/WEB-INF/jsp/appointment/appointment-form.jsp";
        } catch (ServiceException e) {
            logger.error("ServiceException loading data for new appointment form", e);
            request.setAttribute("errorMessage", "Помилка завантаження даних для форми: " + e.getMessage());
            return "/WEB-INF/jsp/error.jsp";
        }
    }
}