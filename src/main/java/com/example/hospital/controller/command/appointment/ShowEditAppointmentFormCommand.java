package com.example.hospital.controller.command.appointment;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.entity.Appointment;
import com.example.hospital.model.entity.Doctor;
import com.example.hospital.model.entity.Patient;
import com.example.hospital.model.service.AppointmentService;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ShowEditAppointmentFormCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowEditAppointmentFormCommand.class);
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    private final String formJsp = "/WEB-INF/jsp/appointment/appointment-form.jsp";
    private final String errorJsp = "/WEB-INF/jsp/error.jsp";
    private final String listRedirect = "redirect:/app?command=appointments-list";

    public ShowEditAppointmentFormCommand() {
        this.appointmentService = new AppointmentService();
        this.doctorService = new DoctorService();
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing ShowEditAppointmentFormCommand");
        String appointmentIdStr = request.getParameter("id");

        if (appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            logger.warn("Appointment ID is missing for edit form.");
            request.setAttribute("errorMessage", "ID запису для редагування не вказано.");
            return errorJsp;
        }

        try {
            Long appointmentId = Long.parseLong(appointmentIdStr);
            Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(appointmentId);

            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                request.setAttribute("appointment", appointment); // Передаємо оригінальний об'єкт Appointment

                // Конвертуємо LocalDateTime в java.util.Date для JSP <fmt:formatDate>
                if (appointment.getAppointmentTime() != null) {
                    Date appointmentUtilDate = Date.from(appointment.getAppointmentTime()
                            .atZone(ZoneId.systemDefault()) // Використовуємо системну часову зону сервера
                            .toInstant());
                    request.setAttribute("appointmentUtilDate", appointmentUtilDate);
                    logger.debug("Converted appointmentTime {} to java.util.Date {} for JSP",
                            appointment.getAppointmentTime(), appointmentUtilDate);
                } else {
                    logger.debug("appointment.getAppointmentTime() is null for appointment ID: {}", appointmentId);
                }

                List<Doctor> doctors = doctorService.findAllDoctors();
                List<Patient> patients = patientService.findAllPatients();
                request.setAttribute("doctors", doctors);
                request.setAttribute("patients", patients);

                request.setAttribute("formAction", "update-appointment"); // Команда для обробки POST запиту
                request.setAttribute("pageTitle", "Редагувати Запис на Прийом");
                return formJsp;
            } else {
                logger.warn("Appointment not found with ID: {}", appointmentId);
                return listRedirect + "&error=appointmentNotFound";
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid Appointment ID format: '{}'", appointmentIdStr, e);
            request.setAttribute("errorMessage", "Невірний формат ID запису: " + appointmentIdStr);
            return errorJsp;
        } catch (ServiceException e) { // Якщо ServiceException - checked, його треба оголошувати в throws або обробляти
            logger.error("ServiceException loading data for edit appointment form for ID {}: {}", appointmentIdStr, e.getMessage(), e);
            request.setAttribute("errorMessage", "Помилка завантаження даних для редагування: " + e.getMessage());
            return errorJsp;
        } catch (Exception e) {
            logger.error("Unexpected error in ShowEditAppointmentFormCommand for ID {}: {}", appointmentIdStr, e.getMessage(), e);
            request.setAttribute("errorMessage", "Неочікувана системна помилка.");
            return errorJsp;
        }
    }
}