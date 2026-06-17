package com.example.hospital.controller.command.doctor;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.validator.DoctorValidator;
import com.example.hospital.controller.validator.Errors;
import com.example.hospital.model.entity.Doctor; // Для завантаження існуючого
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UpdateDoctorCommand implements Command {
    private static final Logger logger = LogManager.getLogger(UpdateDoctorCommand.class);
    private final DoctorService doctorService;
    private final DoctorValidator doctorValidator;

    public UpdateDoctorCommand() {
        this.doctorService = new DoctorService();
        this.doctorValidator = new DoctorValidator();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing UpdateDoctorCommand");
        String formJsp = "/WEB-INF/jsp/doctor/doctor-form.jsp";
        String errorJsp = "/WEB-INF/jsp/error.jsp";
        String doctorIdStr = request.getParameter("id");

        if (doctorIdStr == null || doctorIdStr.trim().isEmpty()) {
            logger.error("Doctor ID is missing for update.");
            request.setAttribute("errorMessage", "ID лікаря для оновлення не вказано.");
            return errorJsp;
        }
        Long doctorId;
        try {
            doctorId = Long.parseLong(doctorIdStr);
        } catch (NumberFormatException e) {
            logger.error("Invalid Doctor ID format for update: {}", doctorIdStr, e);
            request.setAttribute("errorMessage", "Невірний формат ID лікаря.");
            return errorJsp;
        }

        Errors errors = doctorValidator.validateForm(request);
        if (errors.hasErrors()) {
            logger.warn("Validation errors on update doctor: {}", errors.getErrorMessages());
            request.setAttribute("errors", errors.getErrorMessages());
            try { // Завантажуємо існуючі дані лікаря для форми
                doctorService.findDoctorById(doctorId).ifPresent(doc -> request.setAttribute("doctor", doc));
            } catch (ServiceException e) {
                logger.error("Could not fetch doctor details for form repaint after validation error", e);
            }
            // Зберігаємо введені значення, які спричинили помилку
            Doctor submittedDoctor = new Doctor(doctorId, request.getParameter("firstName"), request.getParameter("lastName"), request.getParameter("specialization"));
            request.setAttribute("doctor", submittedDoctor); // Перезаписуємо, щоб форма показала введені значення
            request.setAttribute("formAction", "update-doctor");
            request.setAttribute("pageTitle", "Редагувати дані лікаря");
            return formJsp;
        }

        try {
            String firstName = request.getParameter("firstName").trim();
            String lastName = request.getParameter("lastName").trim();
            String specialization = request.getParameter("specialization").trim();

            doctorService.updateDoctor(doctorId, firstName, lastName, specialization);
            logger.info("Doctor updated successfully with ID: {}", doctorId);
            return "redirect:/app?command=doctors-list&success=doctor_updated";
        } catch (ServiceException e) {
            logger.error("ServiceException during doctor update", e);
            request.setAttribute("errorMessageOnForm", "Не вдалося оновити лікаря: " + e.getMessage());
            request.setAttribute("errors", Map.of("general", List.of(e.getMessage())));
            try {
                doctorService.findDoctorById(doctorId).ifPresent(doc -> request.setAttribute("doctor", doc));
            } catch (ServiceException se) {
                logger.error("Could not fetch doctor details for form repaint after service error", se);
            }
            Doctor submittedDoctor = new Doctor(doctorId, request.getParameter("firstName"), request.getParameter("lastName"), request.getParameter("specialization"));
            request.setAttribute("doctor", submittedDoctor);
            request.setAttribute("formAction", "update-doctor");
            request.setAttribute("pageTitle", "Редагувати дані лікаря");
            return formJsp;
        }
    }
}