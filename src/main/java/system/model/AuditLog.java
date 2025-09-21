package system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "action_performed", nullable = false)
    private String action; // e.g., "VIEW_PATIENT_RECORD", "UPDATE_APPOINTMENT_STATUS"

    @Column(name = "target_entity_id")
    private String targetId; // e.g., "P101" for a patient, "A52" for an appointment

    @Column(name = "outcome", nullable = false, length = 20)
    private String outcome; // "SUCCESS" or "FAILURE"

    public AuditLog() {}

    public AuditLog(LocalDateTime timestamp, String username, String userRole, String action, String targetId, String outcome) {
        this.timestamp = timestamp;
        this.username = username;
        this.userRole = userRole;
        this.action = action;
        this.targetId = targetId;
        this.outcome = outcome;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
}