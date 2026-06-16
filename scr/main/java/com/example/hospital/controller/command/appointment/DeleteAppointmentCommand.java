package com.example.hospital.controller.command.appointment;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.service.AppointmentService;
import com.example.hospital.model.service.exception.ServiceException; // Якщо ServiceException - checked
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeleteAppointmentCommand implements Command {
    private static final Logger logger = LogManager.getLogger(DeleteAppointmentCommand.class);
    private final AppointmentService appointmentService;

    public DeleteAppointmentCommand() {
        this.appointmentService = new AppointmentService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) { // throws ServiceException
        logger.info("Executing DeleteAppointmentCommand");
        String appointmentIdStr = request.getParameter("id");
        String errorJsp = "/WEB-INF/jsp/error.jsp"; // Використовуємо узгоджений шлях

        if (appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            logger.warn("Appointment ID is missing for delete.");
            return "redirect:/app?command=appointments-list&error=missingId";
        }

        try {
            Long appointmentId = Long.parseLong(appointmentIdStr);
            appointmentService.cancelAppointment(appointmentId); // Або deleteAppointment
            logger.info("Appointment deleted successfully with ID: {}", appointmentId);
            return "redirect:/app?command=appointments-list&success=deleted";
        } catch (NumberFormatException e) {
            logger.warn("Invalid Appointment ID format for delete: {}", appointmentIdStr, e);
            return "redirect:/app?command=appointments-list&error=invalidIdFormat";
        } catch (ServiceException e) {
            logger.error("ServiceException during appointment deletion", e);
            return "redirect:/app?command=appointments-list&error=deleteFailed";
        }
    }
}