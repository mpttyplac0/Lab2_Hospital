package com.example.hospital.controller;

import com.example.hospital.controller.command.Command;
import com.example.hospital.controller.command.appointment.*;
import com.example.hospital.controller.command.doctor.*;
import com.example.hospital.controller.command.patient.*;

import java.util.HashMap;
import java.util.Map;

public class CommandNameMapper {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("appointments-list", new ShowAppointmentListCommand());
        commands.put("new-appointment-form", new ShowNewAppointmentFormCommand());
        commands.put("create-appointment", new CreateAppointmentCommand());
        commands.put("edit-appointment-form", new ShowEditAppointmentFormCommand());
        commands.put("update-appointment", new UpdateAppointmentCommand());
        commands.put("delete-appointment", new DeleteAppointmentCommand());

        commands.put("doctors-list", new ShowDoctorListCommand());
        commands.put("new-doctor-form", new ShowNewDoctorFormCommand());
        commands.put("create-doctor", new CreateDoctorCommand());
        commands.put("edit-doctor-form", new ShowEditDoctorFormCommand());
        commands.put("update-doctor", new UpdateDoctorCommand());
        commands.put("delete-doctor", new DeleteDoctorCommand());

        commands.put("patients-list", new ShowPatientListCommand());
        commands.put("new-patient-form", new ShowNewPatientFormCommand());
        commands.put("create-patient", new CreatePatientCommand());
        commands.put("edit-patient-form", new ShowEditPatientFormCommand());
        commands.put("update-patient", new UpdatePatientCommand());
        commands.put("delete-patient", new DeletePatientCommand());
    }

    public static Command getCommand(String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            return commands.get("appointments-list");
        }
        Command command = commands.get(commandName.toLowerCase().trim());
        if (command == null) {
            System.err.println("Warning: Unknown command requested: " + commandName);
            return commands.get("appointments-list");
        }
        return command;
    }
}