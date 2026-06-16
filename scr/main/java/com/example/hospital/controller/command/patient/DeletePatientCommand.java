package com.example.hospital.controller.command.patient;

import com.example.hospital.controller.command.Command;
import com.example.hospital.model.service.PatientService;
import com.example.hospital.model.service.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeletePatientCommand implements Command {
    private static final Logger logger = LogManager.getLogger(DeletePatientCommand.class);
    private final PatientService patientService;

    public DeletePatientCommand() {
        this.patientService = new PatientService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Executing DeletePatientCommand");
        String patientIdStr = request.getParameter("id");

        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            logger.warn("Patient ID is missing for delete.");
            return "redirect:/app?command=patients-list&error=patient_missingId";
        }

        try {
            Long patientId = Long.parseLong(patientIdStr);
            patientService.deletePatient(patientId);
            logger.info("Patient deleted successfully with ID: {}", patientId);
            return "redirect:/app?command=patients-list&success=patient_deleted";
        } catch (NumberFormatException e) {
            logger.warn("Invalid Patient ID format for delete: {}", patientIdStr, e);
            return "redirect:/app?command=patients-list&error=patient_invalidIdFormat";
        } catch (ServiceException e) {
            logger.error("ServiceException during patient deletion for ID {}: {}", patientIdStr, e.getMessage());
            return "redirect:/app?command=patients-list&error=patient_deleteFailed";
        }
    }
}