package com.example.hospital.controller.command.appointment;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Appointment;
import com.example.hospital.model.service.AppointmentService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class ShowAppointmentListCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowAppointmentListCommand.class);
    private final AppointmentService appointmentService;

    public ShowAppointmentListCommand() {
        this.appointmentService = new AppointmentService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        logger.info("Executing ShowAppointmentListCommand");
        try {
            List<Appointment> appointments = appointmentService.findAllAppointments();
            request.setAttribute("appointments", appointments);
            return "/WEB-INF/jsp/appointment/appointments-list.jsp";
        } catch (ServiceException e) {
            logger.error("ServiceException in ShowAppointmentListCommand", e);
            request.setAttribute("errorMessage", "Не вдалося завантажити список записів: " + e.getMessage());
            return "/WEB-INF/jsp/error.jsp";
        }
    }
}