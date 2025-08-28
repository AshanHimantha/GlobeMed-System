/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.model;



import jakarta.persistence.*;

/**
 * Represents a single line item (one charge) on a Claim.
 */
@Entity
@Table(name = "billable_items")
public class BillableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- THIS IS THE FIX ---
    // A BillableItem belongs to a Claim, not an Appointment.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;
    // --- END OF FIX ---

    @Column(name = "item_description", nullable = false)
    private String description;

    @Column(name = "cost", nullable = false)
    private double cost;

    public BillableItem() {}

    // --- CONSTRUCTOR IS NOW CORRECT ---
    public BillableItem(Claim claim, String description, double cost) {
        this.claim = claim;
        this.description = description;
        this.cost = cost;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}