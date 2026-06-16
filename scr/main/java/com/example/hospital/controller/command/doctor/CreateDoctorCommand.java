package com.example.hospital.controller.command.doctor;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.validator.DoctorValidator;
import com.example.hospital.controller.validator.Errors;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List; // <--- ДОДАНО ІМПОРТ
import java.util.Map;  // <--- ДОДАНО ІМПОРТ

public class CreateDoctorCommand implements Command {
    private static final Logger logger = LogManager.getLogger(CreateDoctorCommand.class);
    private final DoctorService doctorService;
    private final DoctorValidator doctorValidator;

    public CreateDoctorCommand() {
        this.doctorService = new DoctorService();
        this.doctorValidator = new DoctorValidator();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing CreateDoctorCommand");
        String formJsp = "/WEB-INF/jsp/doctor/doctor-form.jsp";

        Errors errors = doctorValidator.validateForm(request);
        if (errors.hasErrors()) {
            logger.warn("Validation errors on create doctor: {}", errors.getErrorMessages());
            request.setAttribute("errors", errors.getErrorMessages());
            request.setAttribute("submittedFirstName", request.getParameter("firstName"));
            request.setAttribute("submittedLastName", request.getParameter("lastName"));
            request.setAttribute("submittedSpecialization", request.getParameter("specialization"));
            request.setAttribute("formAction", "create-doctor");
            request.setAttribute("pageTitle", "Додати нового лікаря");
            return formJsp;
        }

        try {
            String firstName = request.getParameter("firstName").trim();
            String lastName = request.getParameter("lastName").trim();
            String specialization = request.getParameter("specialization").trim();

            doctorService.createDoctor(firstName, lastName, specialization);
            logger.info("Doctor created successfully: {} {}", firstName, lastName);
            return "redirect:/app?command=doctors-list&success=doctor_created";
        } catch (ServiceException e) {
            logger.error("ServiceException during doctor creation", e);
            request.setAttribute("errorMessageOnForm", "Не вдалося створити лікаря: " + e.getMessage());
            request.setAttribute("errors", Map.of("general", List.of(e.getMessage()))); // для сумісності з JSP, якщо він очікує errors
            request.setAttribute("submittedFirstName", request.getParameter("firstName"));
            request.setAttribute("submittedLastName", request.getParameter("lastName"));
            request.setAttribute("submittedSpecialization", request.getParameter("specialization"));
            request.setAttribute("formAction", "create-doctor");
            request.setAttribute("pageTitle", "Додати нового лікаря");
            return formJsp;
        }
    }
}