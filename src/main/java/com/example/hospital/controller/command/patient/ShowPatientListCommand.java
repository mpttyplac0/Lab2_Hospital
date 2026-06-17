package com.example.hospital.controller.command.patient;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ShowPatientListCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowPatientListCommand.class);
    private final PatientService patientService;

    public ShowPatientListCommand() {
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing ShowPatientListCommand");
        try {
            List<Patient> patients = patientService.findAllPatients();
            request.setAttribute("patients", patients);
            return "/WEB-INF/jsp/patient/patients-list.jsp";
        } catch (ServiceException e) {
            logger.error("ServiceException in ShowPatientListCommand", e);
            request.setAttribute("errorMessage", "Не вдалося завантажити список пацієнтів: " + e.getMessage());
            return "/WEB-INF/jsp/error.jsp";
        }
    }
}