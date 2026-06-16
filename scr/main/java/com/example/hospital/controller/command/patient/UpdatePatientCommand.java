package com.example.hospital.controller.command.patient;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.validator.PatientValidator;
import com.example.hospital.controller.validator.Errors;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class UpdatePatientCommand implements Command {
    private static final Logger logger = LogManager.getLogger(UpdatePatientCommand.class);
    private final PatientService patientService;
    private final PatientValidator patientValidator;

    public UpdatePatientCommand() {
        this.patientService = new PatientService();
        this.patientValidator = new PatientValidator();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing UpdatePatientCommand");
        String formJsp = "/WEB-INF/jsp/patient/patient-form.jsp";
        String errorJsp = "/WEB-INF/jsp/error.jsp";
        String patientIdStr = request.getParameter("id");

        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            logger.error("Patient ID is missing for update.");
            request.setAttribute("errorMessage", "ID пацієнта для оновлення не вказано.");
            return errorJsp;
        }
        Long patientId;
        try {
            patientId = Long.parseLong(patientIdStr);
        } catch (NumberFormatException e) {
            logger.error("Invalid Patient ID format for update: {}", patientIdStr, e);
            request.setAttribute("errorMessage", "Невірний формат ID пацієнта.");
            return errorJsp;
        }

        Errors errors = patientValidator.validateForm(request);
        Patient patientForForm = new Patient(patientId, request.getParameter("firstName"), request.getParameter("lastName"), 0, request.getParameter("gender"));
        try {
            patientForForm.setAge(Integer.parseInt(request.getParameter("age")));
        } catch (NumberFormatException ignored) {}


        if (errors.hasErrors()) {
            logger.warn("Validation errors on update patient: {}", errors.getErrorMessages());
            request.setAttribute("errors", errors.getErrorMessages());
            request.setAttribute("patient", patientForForm); // Передаємо введені дані
            request.setAttribute("formAction", "update-patient");
            request.setAttribute("pageTitle", "Редагувати дані пацієнта");
            return formJsp;
        }

        try {
            String firstName = request.getParameter("firstName").trim();
            String lastName = request.getParameter("lastName").trim();
            int age = Integer.parseInt(request.getParameter("age").trim());
            String gender = request.getParameter("gender") != null ? request.getParameter("gender").trim() : "";

            patientService.updatePatient(patientId, firstName, lastName, age, gender);
            logger.info("Patient updated successfully with ID: {}", patientId);
            return "redirect:/app?command=patients-list&success=patient_updated";
        } catch (ServiceException e) {
            logger.error("ServiceException during patient update for ID {}: {}", patientId, e.getMessage());
            request.setAttribute("errorMessageOnForm", "Не вдалося оновити пацієнта: " + e.getMessage());
            request.setAttribute("errors", Map.of("general", List.of(e.getMessage())));
            request.setAttribute("patient", patientForForm);
            request.setAttribute("formAction", "update-patient");
            request.setAttribute("pageTitle", "Редагувати дані пацієнта");
            return formJsp;
        } catch (NumberFormatException e) {
            logger.warn("NumberFormatException for age during patient update", e);
            errors.addError("age", "Вік має бути числом.");
            request.setAttribute("errors", errors.getErrorMessages());
            request.setAttribute("patient", patientForForm);
            request.setAttribute("formAction", "update-patient");
            request.setAttribute("pageTitle", "Редагувати дані пацієнта");
            return formJsp;
        }
    }
}