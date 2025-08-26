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
public class PatientBillingHandler extends ClaimHandler {
    public PatientBillingHandler(JTextArea logArea) {
        super(logArea);
    }

    @Override
    public void processClaim(Claim claim) {
        log("Generating final bill for patient...");
        double patientDue = claim.getPatientDueAmount();
        claim.setPaidByPatient(patientDue); // For this demo, assume patient pays immediately.
        
        log("Patient billed for remaining amount: $" + String.format("%.2f", patientDue));
        claim.setStatus("CLOSED");
        claimService.updateClaim(claim);
        log("Claim is now CLOSED.");
        passToNext(claim); // Pass to null, ending the chain.
    }
}