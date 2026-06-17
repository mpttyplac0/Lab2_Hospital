package com.example.hospital.controller.command.appointment;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.validator.AppointmentValidator;
import com.example.hospital.controller.validator.Errors;
import com.example.hospital.model.entity.Appointment; // Для отримання поточного значення часу, якщо потрібно
import com.example.hospital.model.service.AppointmentService;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class UpdateAppointmentCommand implements Command {
    private static final Logger logger = LogManager.getLogger(UpdateAppointmentCommand.class);
    private final AppointmentService appointmentService;
    private final AppointmentValidator appointmentValidator;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public UpdateAppointmentCommand() {
        this.appointmentService = new AppointmentService();
        this.appointmentValidator = new AppointmentValidator();
        this.doctorService = new DoctorService();
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) { // throws ServiceException
        logger.info("Executing UpdateAppointmentCommand");
        String formJsp = "/WEB-INF/jsp/appointment/appointment-form.jsp"; // Використовуємо узгоджений шлях
        String errorJsp = "/WEB-INF/jsp/error.jsp"; // Використовуємо узгоджений шлях

        String appointmentIdStr = request.getParameter("id");
        if (appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            logger.error("Appointment ID is missing for update.");
            request.setAttribute("errorMessageOnPage", "ID запису для оновлення не вказано.");
            return errorJsp;
        }

        Errors errors = appointmentValidator.validateForm(request);
        if (errors.hasErrors()) {
            logger.warn("Validation errors on update appointment: {}", errors.getErrorMessages());
            request.setAttribute("errors", errors.getErrorMessages());
            try {
                Long id = Long.parseLong(appointmentIdStr);
                appointmentService.findAppointmentById(id).ifPresent(app -> request.setAttribute("appointment", app));
                request.setAttribute("doctors", doctorService.findAllDoctors());
                request.setAttribute("patients", patientService.findAllPatients());
            } catch (ServiceException | NumberFormatException e) {
                logger.error("Error reloading data for form after validation error on update", e);
                request.setAttribute("errorMessageOnForm", "Помилка завантаження даних для форми: " + e.getMessage());
                // Не повертаємо errorJsp, щоб користувач залишився на формі
            }
            request.setAttribute("submittedPatientId", request.getParameter("patientId"));
            request.setAttribute("submittedDoctorId", request.getParameter("doctorId"));
            request.setAttribute("submittedAppointmentTime", request.getParameter("appointmentTime"));
            request.setAttribute("formAction", "update-appointment");
            return formJsp;
        }

        try {
            Long id = Long.parseLong(appointmentIdStr);
            Long patientId = Long.parseLong(request.getParameter("patientId"));
            Long doctorId = Long.parseLong(request.getParameter("doctorId"));
            LocalDateTime appointmentTime = LocalDateTime.parse(request.getParameter("appointmentTime"));

            appointmentService.updateAppointment(id, patientId, doctorId, appointmentTime);
            logger.info("Appointment updated successfully for ID: {}", id);
            return "redirect:/app?command=appointments-list&success=updated";

        } catch (DateTimeParseException e) {
            logger.warn("DateTimeParseException during appointment update", e);
            Errors parseErrors = new Errors();
            parseErrors.addError("appointmentTime", "Невірний формат дати або часу.");
            request.setAttribute("errors", parseErrors.getErrorMessages());
            try {
                Long id = Long.parseLong(appointmentIdStr);
                appointmentService.findAppointmentById(id).ifPresent(app -> request.setAttribute("appointment", app));
                request.setAttribute("doctors", doctorService.findAllDoctors());
                request.setAttribute("patients", patientService.findAllPatients());
            } catch (ServiceException | NumberFormatException se) {
                logger.error("Error reloading data for form after parse error on update", se);
            }
            request.setAttribute("submittedPatientId", request.getParameter("patientId"));
            request.setAttribute("submittedDoctorId", request.getParameter("doctorId"));
            request.setAttribute("submittedAppointmentTime", request.getParameter("appointmentTime"));
            request.setAttribute("formAction", "update-appointment");
            return formJsp;
        } catch (ServiceException e) {
            logger.error("ServiceException during appointment update", e);
            Errors serviceErrors = new Errors();
            serviceErrors.addError("general", "Не вдалося оновити запис: " + e.getMessage());
            request.setAttribute("errors", serviceErrors.getErrorMessages());
            try {
                Long id = Long.parseLong(appointmentIdStr);
                appointmentService.findAppointmentById(id).ifPresent(app -> request.setAttribute("appointment", app));
                request.setAttribute("doctors", doctorService.findAllDoctors());
                request.setAttribute("patients", patientService.findAllPatients());
            } catch (ServiceException | NumberFormatException se) {
                logger.error("Error reloading data for form after service error on update", se);
            }
            request.setAttribute("submittedPatientId", request.getParameter("patientId"));
            request.setAttribute("submittedDoctorId", request.getParameter("doctorId"));
            request.setAttribute("submittedAppointmentTime", request.getParameter("appointmentTime"));
            request.setAttribute("formAction", "update-appointment");
            return formJsp;
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException during update (ID parsing)", e);
            request.setAttribute("errorMessageOnPage", "Невірний формат ID для оновлення.");
            return errorJsp;
        }
    }
}