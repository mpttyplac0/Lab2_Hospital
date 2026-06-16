package com.example.hospital.controller.command.patient;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ShowEditPatientFormCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowEditPatientFormCommand.class);
    private final PatientService patientService;

    public ShowEditPatientFormCommand() {
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing ShowEditPatientFormCommand");
        String patientIdStr = request.getParameter("id");
        String errorJsp = "/WEB-INF/jsp/error.jsp";
        String formJsp = "/WEB-INF/jsp/patient/patient-form.jsp";

        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            logger.warn("Patient ID is missing for edit form.");
            request.setAttribute("errorMessage", "ID пацієнта для редагування не вказано.");
            return errorJsp;
        }

        try {
            Long patientId = Long.parseLong(patientIdStr);
            Optional<Patient> patientOpt = patientService.findPatientById(patientId);

            if (patientOpt.isPresent()) {
                request.setAttribute("patient", patientOpt.get());
                request.setAttribute("formAction", "update-patient");
                request.setAttribute("pageTitle", "Редагувати дані пацієнта");
                return formJsp;
            } else {
                logger.warn("Patient not found with ID: {}", patientId);
                request.setAttribute("errorMessage", "Пацієнта з ID " + patientId + " не знайдено.");
                return "redirect:/app?command=patients-list";
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid Patient ID format: {}", patientIdStr, e);
            request.setAttribute("errorMessage", "Невірний формат ID пацієнта.");
            return errorJsp;
        } catch (ServiceException e) {
            logger.error("ServiceException loading data for edit patient form", e);
            request.setAttribute("errorMessage", "Помилка завантаження даних для редагування: " + e.getMessage());
            return errorJsp;
        }
    }
}