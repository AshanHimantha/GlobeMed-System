/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.decorator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import system.model.User;

/**
 *
 * @author User
 */
public class AuditLogDecorator extends PatientRecordDecorator {

    private final User user;

    public AuditLogDecorator(PatientRecord decoratedRecord, User user) {
        super(decoratedRecord);
        this.user = user;
    }

    @Override
    public String getDetails() {
        String logEntry = generateLogEntry();
        // We prepend our new logging information to the original details.
        return logEntry + "\n" + super.getDetails();
    }

    private String generateLogEntry() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "[AUDIT LOG] User '" + user.getUsername() + "' (" + user.getRole() + 
               ") accessed record at " + timestamp + ".";
    }
}