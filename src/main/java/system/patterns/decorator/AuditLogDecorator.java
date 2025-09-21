/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.decorator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import system.model.AuditLog;
import system.model.User;
import system.service.AuditService;

/**
 *
 * @author User
 */
public class AuditLogDecorator extends PatientRecordDecorator {

    private final User user;
    private final String patientId; // The decorator needs to know the target
    private final AuditService auditService;

    public AuditLogDecorator(PatientRecord decoratedRecord, User user, String patientId) {
        super(decoratedRecord);
        this.user = user;
        this.patientId = patientId;
        this.auditService = new AuditService();
    }

    @Override
    public String getDetails() {
        // --- THIS IS THE NEW LOGIC ---

        // 1. Call the inner decorator FIRST to see if the action succeeds or fails.
        String resultFromInner = super.getDetails();

        // 2. Determine the outcome based on the result.
        String outcome;
        if (resultFromInner.contains("ACCESS DENIED")) {
            outcome = "FAILURE";
        } else {
            outcome = "SUCCESS";
        }

        // 3. Create the persistent AuditLog entity.
        AuditLog logEntry = new AuditLog(
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString(),
                "VIEW_PATIENT_RECORD",
                this.patientId,
                outcome
        );

        // 4. Call the service to save the log to the database.
        auditService.log(logEntry);
        System.out.println("AUDIT LOG: Entry saved to database for user " + user.getUsername());
        // --- END OF NEW LOGIC ---

        // 5. Return the original result from the inner decorator to the UI.
        // The UI will still show the "ACCESS DENIED" message if that's what happened.
        return resultFromInner;
    }
}