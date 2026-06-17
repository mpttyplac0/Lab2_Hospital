package com.example.hospital.controller.command.doctor;

import com.example.hospital.controller.command.Command;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShowNewDoctorFormCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowNewDoctorFormCommand.class);

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing ShowNewDoctorFormCommand");
        request.setAttribute("formAction", "create-doctor"); // Команда для обробки POST запиту
        request.setAttribute("pageTitle", "Додати нового лікаря");
        // Скидаємо атрибут doctor, щоб форма була чистою для створення
        request.removeAttribute("doctor");
        return "/WEB-INF/jsp/doctor/doctor-form.jsp";
    }
}