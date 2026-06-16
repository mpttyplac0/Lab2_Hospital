package com.example.hospital.controller.command.doctor;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.service.DoctorService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeleteDoctorCommand implements Command {
    private static final Logger logger = LogManager.getLogger(DeleteDoctorCommand.class);
    private final DoctorService doctorService;

    public DeleteDoctorCommand() {
        this.doctorService = new DoctorService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing DeleteDoctorCommand");
        String doctorIdStr = request.getParameter("id");

        if (doctorIdStr == null || doctorIdStr.trim().isEmpty()) {
            logger.warn("Doctor ID is missing for delete.");
            return "redirect:/app?command=doctors-list&error=doctor_missingId";
        }

        try {
            Long doctorId = Long.parseLong(doctorIdStr);
            doctorService.deleteDoctor(doctorId);
            logger.info("Doctor deleted successfully with ID: {}", doctorId);
            return "redirect:/app?command=doctors-list&success=doctor_deleted";
        } catch (NumberFormatException e) {
            logger.warn("Invalid Doctor ID format for delete: {}", doctorIdStr, e);
            return "redirect:/app?command=doctors-list&error=doctor_invalidIdFormat";
        } catch (ServiceException e) {
            logger.error("ServiceException during doctor deletion for ID {}: {}", doctorIdStr, e.getMessage());
            // Передаємо повідомлення про помилку через параметр URL, оскільки це редірект
            // Важливо: потрібно буде обробити цей параметр на сторінці doctors-list.jsp
            // Або встановити атрибут в сесію, а потім видалити його.
            // Для простоти - параметр URL, але він має бути URL-encoded, якщо містить спецсимволи.
            String errorMessage = "Не вдалося видалити лікаря: " + e.getMessage();
            try {
                // Простий варіант - передати ключ помилки
                return "redirect:/app?command=doctors-list&error=doctor_deleteFailed";
            } catch (Exception ue) { // UnsupportedEncodingException
                return "redirect:/app?command=doctors-list&error=doctor_deleteFailed_encoding";
            }
        }
    }
}