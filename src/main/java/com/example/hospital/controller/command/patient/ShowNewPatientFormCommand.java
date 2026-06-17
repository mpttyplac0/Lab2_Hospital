package com.example.hospital.controller.command.patient;

import com.example.hospital.controller.command.Command;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShowNewPatientFormCommand implements Command {
    private static final Logger logger = LogManager.getLogger(ShowNewPatientFormCommand.class);

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing ShowNewPatientFormCommand");
        request.setAttribute("formAction", "create-patient");
        request.setAttribute("pageTitle", "Додати нового пацієнта");
        request.removeAttribute("patient");
        return "/WEB-INF/jsp/patient/patient-form.jsp";
    }
}