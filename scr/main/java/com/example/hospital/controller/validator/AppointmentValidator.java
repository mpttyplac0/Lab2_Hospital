package com.example.hospital.controller.validator;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class AppointmentValidator {


    public Errors validateForm(HttpServletRequest request) {
        Errors errors = new Errors();

        String patientIdStr = request.getParameter("patientId");
        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            errors.addError("patientId", "Пацієнт має бути обраний.");
        } else {
            try {
                Long.parseLong(patientIdStr);
            } catch (NumberFormatException e) {
                errors.addError("patientId", "Невірний формат ID пацієнта.");
            }
        }

        String doctorIdStr = request.getParameter("doctorId");
        if (doctorIdStr == null || doctorIdStr.trim().isEmpty()) {
            errors.addError("doctorId", "Лікар має бути обраний.");
        } else {
            try {
                Long.parseLong(doctorIdStr);
            } catch (NumberFormatException e) {
                errors.addError("doctorId", "Невірний формат ID лікаря.");
            }
        }

        String timeStr = request.getParameter("appointmentTime");
        if (timeStr == null || timeStr.trim().isEmpty()) {
            errors.addError("appointmentTime", "Час прийому має бути вказаний.");
        } else {
            try {
                LocalDateTime appointmentTime = LocalDateTime.parse(timeStr);

            } catch (DateTimeParseException e) {
                errors.addError("appointmentTime", "Невірний формат дати або часу. Використовуйте формат рррр-мм-ддТгг:хх");
            }
        }
        return errors;
    }
}