package com.example.hospital.controller.validator;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class DoctorValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯіІїЇєЄґҐ'\\- ]{2,100}$");
    private static final Pattern SPECIALIZATION_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯіІїЇєЄґҐ0-9'\\- .,]{2,100}$");

    public Errors validateForm(HttpServletRequest request) {
        Errors errors = new Errors();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String specialization = request.getParameter("specialization");

        if (firstName == null || firstName.trim().isEmpty()) {
            errors.addError("firstName", "Ім'я лікаря не може бути порожнім.");
        } else if (!NAME_PATTERN.matcher(firstName.trim()).matches()) {
            errors.addError("firstName", "Ім'я лікаря містить неприпустимі символи або має невірну довжину (2-100 символів).");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            errors.addError("lastName", "Прізвище лікаря не може бути порожнім.");
        } else if (!NAME_PATTERN.matcher(lastName.trim()).matches()) {
            errors.addError("lastName", "Прізвище лікаря містить неприпустимі символи або має невірну довжину (2-100 символів).");
        }

        if (specialization == null || specialization.trim().isEmpty()) {
            errors.addError("specialization", "Спеціалізація лікаря не може бути порожньою.");
        } else if (!SPECIALIZATION_PATTERN.matcher(specialization.trim()).matches()) {
            errors.addError("specialization", "Спеціалізація містить неприпустимі символи або має невірну довжину (2-100 символів).");
        }
        return errors;
    }
}