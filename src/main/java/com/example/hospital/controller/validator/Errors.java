package com.example.hospital.controller.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Errors {
    private final Map<String, List<String>> errorMessages = new HashMap<>();

    public void addError(String field, String message) {
        errorMessages.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    public Map<String, List<String>> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getErrorsForField(String field) {
        return errorMessages.getOrDefault(field, new ArrayList<>());
    }

    public void addErrors(Errors otherErrors) {
        if (otherErrors != null && otherErrors.hasErrors()) {
            otherErrors.getErrorMessages().forEach((field, messages) -> {
                messages.forEach(message -> addError(field, message));
            });
        }
    }
}