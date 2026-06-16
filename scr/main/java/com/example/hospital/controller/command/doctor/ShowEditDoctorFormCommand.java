package com.example.hospital.controller.command.doctor;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ShowEditDoctorFormCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowEditDoctorFormCommand.class);
    private final DoctorService doctorService;

    public ShowEditDoctorFormCommand() {
        this.doctorService = new DoctorService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing ShowEditDoctorFormCommand");
        String doctorIdStr = request.getParameter("id");
        String errorJsp = "/WEB-INF/jsp/error.jsp";
        String formJsp = "/WEB-INF/jsp/doctor/doctor-form.jsp";

        if (doctorIdStr == null || doctorIdStr.trim().isEmpty()) {
            logger.warn("Doctor ID is missing for edit form.");
            request.setAttribute("errorMessage", "ID лікаря для редагування не вказано.");
            return errorJsp;
        }

        try {
            Long doctorId = Long.parseLong(doctorIdStr);
            Optional<Doctor> doctorOpt = doctorService.findDoctorById(doctorId);

            if (doctorOpt.isPresent()) {
                request.setAttribute("doctor", doctorOpt.get());
                request.setAttribute("formAction", "update-doctor");
                request.setAttribute("pageTitle", "Редагувати дані лікаря");
                return formJsp;
            } else {
                logger.warn("Doctor not found with ID: {}", doctorId);
                request.setAttribute("errorMessage", "Лікаря з ID " + doctorId + " не знайдено.");
                return "redirect:/app?command=doctors-list";
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid Doctor ID format: {}", doctorIdStr, e);
            request.setAttribute("errorMessage", "Невірний формат ID лікаря.");
            return errorJsp;
        } catch (ServiceException e) {
            logger.error("ServiceException loading data for edit doctor form", e);
            request.setAttribute("errorMessage", "Помилка завантаження даних для редагування: " + e.getMessage());
            return errorJsp;
        }
    }
}