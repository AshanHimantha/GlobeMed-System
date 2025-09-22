
package system.patterns.decorator;

import java.time.LocalDateTime;


import system.model.AuditLog;
import system.model.User;
import system.service.AuditService;


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

        String resultFromInner = super.getDetails();


        String outcome;
        if (resultFromInner.contains("ACCESS DENIED")) {
            outcome = "FAILURE";
        } else {
            outcome = "SUCCESS";
        }


        AuditLog logEntry = new AuditLog(
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString(),
                "VIEW_PATIENT_RECORD",
                this.patientId,
                outcome
        );

        auditService.log(logEntry);
        System.out.println("AUDIT LOG: Entry saved to database for user " + user.getUsername());

        return resultFromInner;
    }
}