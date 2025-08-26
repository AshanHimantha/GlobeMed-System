
package system.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import system.enums.AppointmentType;
import system.enums.PaymentMethod;


@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_username", nullable = true)
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduled_by_username", nullable = false)
    private User scheduledBy;

    /**
     * The specific cataloged service being performed, if applicable (e.g., a specific test).
     * This is NULL for non-catalog services like a general consultation or a custom surgery.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = true) // This is now optional
    private MedicalService medicalService;

    // These fields store the details for ALL appointments.
    // For diagnostics, they are copied from the MedicalService.
    // For others, they are entered directly.
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType type;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "price", nullable = false)
    private double price;
    
    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    public Appointment() {}

    /**
     * Final, flexible constructor.
     * The 'service' parameter is nullable.
     */
 public Appointment(Patient patient, User doctor, User scheduledBy, AppointmentType type, String serviceName, double price, LocalDateTime dateTime) {
        this.patient = patient;
        this.doctor = doctor; // This can now be null
        this.scheduledBy = scheduledBy;
        this.type = type;
        this.serviceName = serviceName;
        this.price = price;
        this.appointmentDateTime = dateTime;       
        this.status = "PENDING_PAYMENT";
      
    }

    // --- Getters and Setters for all fields ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }
    public User getScheduledBy() { return scheduledBy; }
    public void setScheduledBy(User scheduledBy) { this.scheduledBy = scheduledBy; }
    public MedicalService getMedicalService() { return medicalService; }
    public void setMedicalService(MedicalService medicalService) { this.medicalService = medicalService; }
    public AppointmentType getType() { return type; }
    public void setType(AppointmentType type) { this.type = type; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}