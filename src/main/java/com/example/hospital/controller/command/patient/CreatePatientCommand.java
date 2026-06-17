package com.example.hospital.controller.command.patient;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.validator.PatientValidator;
import com.example.hospital.controller.validator.Errors;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class CreatePatientCommand implements Command {
    private static final Logger logger = LogManager.getLogger(CreatePatientCommand.class);
    private final PatientService patientService;
    private final PatientValidator patientValidator;

    public CreatePatientCommand() {
        this.patientService = new PatientService();
        this.patientValidator = new PatientValidator();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing CreatePatientCommand");
        String formJsp = "/WEB-INF/jsp/patient/patient-form.jsp";

        Errors errors = patientValidator.validateForm(request);
        if (errors.hasErrors()) {
            logger.warn("Validation errors on create patient: {}", errors.getErrorMessages());
            request.setAttribute("errors", errors.getErrorMessages());
            request.setAttribute("submittedFirstName", request.getParameter("firstName"));
            request.setAttribute("submittedLastName", request.getParameter("lastName"));
            request.setAttribute("submittedAge", request.getParameter("age"));
            request.setAttribute("submittedGender", request.getParameter("gender"));
            request.setAttribute("formAction", "create-patient");
            request.setAttribute("pageTitle", "Додати нового пацієнта");
            return formJsp;
        }

        try {
            String firstName = request.getParameter("firstName").trim();
            String lastName = request.getParameter("lastName").trim();
            int age = Integer.parseInt(request.getParameter("age").trim());
            String gender = request.getParameter("gender") != null ? request.getParameter("gender").trim() : "";

            patientService.createPatient(firstName, lastName, age, gender);
            logger.info("Patient created successfully: {} {}", firstName, lastName);
            return "redirect:/app?command=patients-list&success=patient_created";
        } catch (ServiceException e) {
            logger.error("ServiceException during patient creation", e);
            request.setAttribute("errorMessageOnForm", "Не вдалося створити пацієнта: " + e.getMessage());
            request.setAttribute("errors", Map.of("general", List.of(e.getMessage())));
            request.setAttribute("submittedFirstName", request.getParameter("firstName"));
            request.setAttribute("submittedLastName", request.getParameter("lastName"));
            request.setAttribute("submittedAge", request.getParameter("age"));
            request.setAttribute("submittedGender", request.getParameter("gender"));
            request.setAttribute("formAction", "create-patient");
            request.setAttribute("pageTitle", "Додати нового пацієнта");
            return formJsp;
        } catch (NumberFormatException e) {
            logger.warn("NumberFormatException for age during patient creation", e);
            Errors formatErrors = new Errors();
            formatErrors.addError("age", "Вік має бути числом.");
            request.setAttribute("errors", formatErrors.getErrorMessages());
            request.setAttribute("submittedFirstName", request.getParameter("firstName"));
            request.setAttribute("submittedLastName", request.getParameter("lastName"));
            request.setAttribute("submittedAge", request.getParameter("age"));
            request.setAttribute("submittedGender", request.getParameter("gender"));
            request.setAttribute("formAction", "create-patient");
            request.setAttribute("pageTitle", "Додати нового пацієнта");
            return formJsp;
        }
    }
}