/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.chain;

import javax.swing.JTextArea;
import system.model.Claim;

/**
 *
 * @author User
 */
public class ValidationHandler extends ClaimHandler {
    public ValidationHandler(JTextArea logArea) {
        super(logArea);
    }

    @Override
    public void processClaim(Claim claim) {
        log("Checking claim validation...");
        // Simple validation: Ensure the claim amount is positive.
        if (claim.getTotalAmount() > 0) {
            log("Validation PASSED.");
            claim.setStatus("PENDING_INSURANCE");
            claimService.updateClaim(claim);
            passToNext(claim);
        } else {
            log("Validation FAILED: Claim amount must be positive.");
            claim.setStatus("VALIDATION_FAILED");
            claimService.updateClaim(claim);
            // Stop the chain here. Do not pass to the next handler.
        }
    }
}