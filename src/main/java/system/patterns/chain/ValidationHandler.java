/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.chain;

import javax.swing.JOptionPane;
import system.enums.AppointmentType;
import system.enums.ClaimStatus;
import system.enums.PaymentMethod;

import system.model.Claim;

/**
 *
 * @author User
 */
public class ValidationHandler extends ClaimHandler {

    public ValidationHandler() {
        super();
    }

    /**
     * Processes the claim by performing validation.
     * @param claim The claim object to be processed.
     */
    @Override
    public void processClaim(Claim claim) {
        System.out.println("HANDLER: Validating claim #" + claim.getId());
       if (claim.getPaymentMethod() == PaymentMethod.INSURANCE) {
            String insuranceId = JOptionPane.showInputDialog(
                null,
                "Validation Successful. Please enter Insurance Authorization ID:",
                "Claim Validation Step",
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (insuranceId != null && !insuranceId.trim().isEmpty()) {
                // --- THIS IS THE FIX ---
                // 1. Set the ID on the Claim object.
                claim.setInsuranceAuthorizationId(insuranceId);
                log("HANDLER [Validation]: Insurance Authorization ID set to: " + insuranceId);
                
                // 2. Update the status and save everything to the database.
                claim.setStatus(ClaimStatus.PENDING_INSURANCE_APPROVAL);
                claimService.updateClaim(claim);
                // --- END OF FIX ---
                
                JOptionPane.showMessageDialog(null, "Claim submitted for insurance approval.");
                passToNext(claim);
                
            } else {
                log("HANDLER [Validation]: Process cancelled. Insurance ID was not provided.");
            }
        } else { 
            claim.setStatus(ClaimStatus.VALIDATION_FAILED);
            claimService.updateClaim(claim);
            JOptionPane.showMessageDialog(null, "Claim validation failed: Amount is not positive.");
        }
    }
}