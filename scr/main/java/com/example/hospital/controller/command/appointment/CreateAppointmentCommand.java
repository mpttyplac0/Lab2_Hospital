package com.example.hospital.controller.command.appointment;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.validator.AppointmentValidator;
import com.example.hospital.controller.validator.Errors;
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
import java.util.List;
import java.util.Map;

public class CreateAppointmentCommand implements Command {
    private static final Logger logger = LogManager.getLogger(CreateAppointmentCommand.class);
    private final AppointmentService appointmentService;
    private final AppointmentValidator appointmentValidator;
    private final DoctorService doctorService;
    private final PatientService patientService;


    public CreateAppointmentCommand() {
        this.appointmentService = new AppointmentService();
        this.appointmentValidator = new AppointmentValidator();
        this.doctorService = new DoctorService();
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        logger.info("Executing CreateAppointmentCommand");

        Errors errors = appointmentValidator.validateForm(request);
        if (errors.hasErrors()) {
            logger.warn("Validation errors on create appointment: {}", errors.getErrorMessages());
            request.setAttribute("errors", errors.getErrorMessages());
            request.setAttribute("submittedPatientId", request.getParameter("patientId"));
            request.setAttribute("submittedDoctorId", request.getParameter("doctorId"));
            request.setAttribute("submittedAppointmentTime", request.getParameter("appointmentTime"));
            try {
                request.setAttribute("doctors", doctorService.findAllDoctors());
                request.setAttribute("patients", patientService.findAllPatients());
            } catch (ServiceException e) {
                logger.error("Error reloading doctors/patients for form after validation error", e);
                request.setAttribute("errorMessage", "Помилка завантаження даних для форми: " + e.getMessage());
                return "/WEB-INF/jsp/error.jsp";
            }
            request.setAttribute("formAction", "create-appointment");
            return "/WEB-INF/jsp/appointment/appointment-form.jsp";
        }

        try {
            Long patientId = Long.parseLong(request.getParameter("patientId"));
            Long doctorId = Long.parseLong(request.getParameter("doctorId"));
            LocalDateTime appointmentTime = LocalDateTime.parse(request.getParameter("appointmentTime"));

            appointmentService.createAppointment(patientId, doctorId, appointmentTime);
            logger.info("Appointment created successfully for patient {} with doctor {}", patientId, doctorId);
            return "redirect:/app?command=appointments-list&success=created";
        } catch (DateTimeParseException e) {
            logger.warn("DateTimeParseException during appointment creation", e);
            request.setAttribute("errors", Map.of("appointmentTime", List.of("Невірний формат дати або часу.")));
            try {
                request.setAttribute("doctors", doctorService.findAllDoctors());
                request.setAttribute("patients", patientService.findAllPatients());
            } catch (ServiceException se) {
                logger.error("Error reloading doctors/patients for form after parse error", se);
            }
            request.setAttribute("formAction", "create-appointment");
            return "/WEB-INF/jsp/appointment/appointment-form.jsp";
        }
        catch (ServiceException e) {
            logger.error("ServiceException during appointment creation", e);
            request.setAttribute("errorMessage", "Не вдалося створити запис: " + e.getMessage());
            try {
                request.setAttribute("doctors", doctorService.findAllDoctors());
                request.setAttribute("patients", patientService.findAllPatients());
            } catch (ServiceException se) {
                logger.error("Error reloading doctors/patients for form after service error", se);
            }
            request.setAttribute("errors", Map.of("general", List.of(e.getMessage()))); // Загальна помилка
            request.setAttribute("formAction", "create-appointment");
            return "/WEB-INF/jsp/appointment/appointment-form.jsp";
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException for patientId or doctorId", e);
            request.setAttribute("errorMessage", "Невірний формат ID пацієнта або лікаря.");
            return "/WEB-INF/jsp/error.jsp";
        }
    }
}