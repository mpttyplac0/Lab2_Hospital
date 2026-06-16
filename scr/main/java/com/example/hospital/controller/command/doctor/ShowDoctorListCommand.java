package com.example.hospital.controller.command.doctor;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ShowDoctorListCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowDoctorListCommand.class);
    private final DoctorService doctorService;

    public ShowDoctorListCommand() {
        this.doctorService = new DoctorService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) { // Може кидати ServiceException, якщо він checked
        logger.info("Executing ShowDoctorListCommand");
        try {
            List<Doctor> doctors = doctorService.findAllDoctors();
            request.setAttribute("doctors", doctors);
            return "/WEB-INF/jsp/doctor/doctors-list.jsp"; // Шлях до JSP
        } catch (ServiceException e) {
            logger.error("ServiceException in ShowDoctorListCommand", e);
            request.setAttribute("errorMessage", "Не вдалося завантажити список лікарів: " + e.getMessage());
            return "/WEB-INF/jsp/error.jsp"; // Шлях до JSP помилки
        }
    }
}