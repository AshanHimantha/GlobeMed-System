/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import system.enums.ClaimStatus;
import system.enums.PaymentMethod;
import system.patterns.visitor.ReportVisitor;
import system.patterns.visitor.Visitable;

/**
 *
 * @author User
 */
@Entity
@Table(name = "claims")
public class Claim implements Visitable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long id;

    // A claim is for one specific appointment.
    // This creates a one-to-one relationship.
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "paid_by_insurance")
    private double paidByInsurance;

    @Column(name = "paid_by_patient")
    private double paidByPatient;
    
   @Override
    @Transient // Tell JPA to ignore this method for database mapping
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }
@Column(name = "insurance_auth_id")
    private String insuranceAuthorizationId;
    
    
     @Enumerated(EnumType.STRING) // Tells JPA to store the enum's name (e.g., "CLOSED") in the DB
    @Column(name = "status", nullable = false, length = 50)
    private ClaimStatus status; // Changed from String to ClaimStatus

    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;
    
    @Enumerated(EnumType.STRING)
     @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;
    
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BillableItem> items = new ArrayList<>();

    // JPA requires a no-arg constructor
    public Claim() {}

    
    public void addItem(BillableItem item) {
        this.items.add(item);
        item.setClaim(this);
    }

   public Claim(Appointment appointment, double totalAmount, PaymentMethod paymentMethod) {
        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalArgumentException("Claims can only be generated for COMPLETED appointments.");
        }
        this.appointment = appointment;
        this.totalAmount = totalAmount;
        this.claimDate = LocalDate.now();
        this.paymentMethod = paymentMethod;
        this.status = ClaimStatus.PENDING_VALIDATION;
        this.paidByInsurance = 0.0;
        this.paidByPatient = 0.0;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getPaidByInsurance() { return paidByInsurance; }
    public void setPaidByInsurance(double paidByInsurance) { this.paidByInsurance = paidByInsurance; }
    public double getPaidByPatient() { return paidByPatient; }
    public void setPaidByPatient(double paidByPatient) { this.paidByPatient = paidByPatient; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    /**
     * Calculates the amount still due from the patient.
     * @return The outstanding balance.
     */
    public double getPatientDueAmount() {
        return totalAmount - paidByInsurance;
    }
    
     public List<BillableItem> getItems() { return items; }
    public void setItems(List<BillableItem> items) { this.items = items; }   
    public String getInsuranceAuthorizationId() { return insuranceAuthorizationId; }
    public void setInsuranceAuthorizationId(String insuranceAuthorizationId) { this.insuranceAuthorizationId = insuranceAuthorizationId; }
}