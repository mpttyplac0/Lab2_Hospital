package com.example.hospital.controller.validator;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class PatientValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯіІїЇєЄґҐ'\\- ]{2,100}$");
    private static final Pattern GENDER_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯіІїЇєЄґҐ'\\- ]{1,20}$"); // Для статі

    public Errors validateForm(HttpServletRequest request) {
        Errors errors = new Errors();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ageStr = request.getParameter("age");
        String gender = request.getParameter("gender");

        // Валідація імені
        if (firstName == null || firstName.trim().isEmpty()) {
            errors.addError("firstName", "Ім'я пацієнта не може бути порожнім.");
        } else if (!NAME_PATTERN.matcher(firstName.trim()).matches()) {
            errors.addError("firstName", "Ім'я пацієнта містить неприпустимі символи або має невірну довжину (2-100 символів).");
        }

        // Валідація прізвища
        if (lastName == null || lastName.trim().isEmpty()) {
            errors.addError("lastName", "Прізвище пацієнта не може бути порожнім.");
        } else if (!NAME_PATTERN.matcher(lastName.trim()).matches()) {
            errors.addError("lastName", "Прізвище пацієнта містить неприпустимі символи або має невірну довжину (2-100 символів).");
        }

        // Валідація віку
        if (ageStr == null || ageStr.trim().isEmpty()) {
            errors.addError("age", "Вік пацієнта не може бути порожнім.");
        } else {
            try {
                int age = Integer.parseInt(ageStr.trim());
                if (age <= 0 || age > 120) { // Припустимі межі віку
                    errors.addError("age", "Вік має бути в межах від 1 до 120.");
                }
            } catch (NumberFormatException e) {
                errors.addError("age", "Вік має бути числом.");
            }
        }

        if (gender != null && !gender.trim().isEmpty()) {
            if (!GENDER_PATTERN.matcher(gender.trim()).matches()) {
                errors.addError("gender", "Поле 'Стать' містить неприпустимі символи або має невірну довжину (1-20 символів).");
            }
        } else {}
        return errors;
    }
}