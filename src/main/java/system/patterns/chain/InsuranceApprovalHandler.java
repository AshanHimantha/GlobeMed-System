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
public class InsuranceApprovalHandler extends ClaimHandler {
    public InsuranceApprovalHandler(JTextArea logArea) {
        super(logArea);
    }

    @Override
    public void processClaim(Claim claim) {
        log("Submitting to insurance for approval...");
        // Mock logic: Assume insurance covers 80% of the total amount.
        double insuranceCoverage = claim.getTotalAmount() * 0.80;
        claim.setPaidByInsurance(insuranceCoverage);
        
        log("Insurance approved coverage of $" + String.format("%.2f", insuranceCoverage));
        claim.setStatus("PENDING_PATIENT_BILLING");
        claimService.updateClaim(claim);
        passToNext(claim);
    }
}