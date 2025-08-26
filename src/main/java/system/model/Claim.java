/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import system.enums.PaymentMethod;

/**
 *
 * @author User
 */
@Entity
@Table(name = "claims")
public class Claim {

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

    @Column(name = "status", nullable = false)
    private String status; // e.g., "PENDING_VALIDATION", "PENDING_INSURANCE", "PENDING_PATIENT_BILLING", "CLOSED"

    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    // JPA requires a no-arg constructor
    public Claim() {}


   public Claim(Appointment appointment, double totalAmount, PaymentMethod paymentMethod) {
        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalArgumentException("Claims can only be generated for COMPLETED appointments.");
        }
        this.appointment = appointment;
        this.totalAmount = totalAmount;
        this.claimDate = LocalDate.now();
        this.paymentMethod = paymentMethod;
        this.status = "PENDING_VALIDATION"; // The first step in our chain
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
}