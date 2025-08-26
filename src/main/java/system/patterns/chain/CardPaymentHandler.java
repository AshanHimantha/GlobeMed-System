/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.chain;

import javax.swing.JTextArea;
import system.enums.PaymentMethod;
import system.model.Claim;

/**
 *
 * @author User
 */
public class CardPaymentHandler extends ClaimHandler {
    public CardPaymentHandler(JTextArea logArea) {
        super(logArea);
    }

    @Override
    public void processClaim(Claim claim) {
        // This handler ONLY acts if the payment method is CARD.
        if (claim.getPaymentMethod() == PaymentMethod.CARD) {
            log("Processing CARD payment...");
            // Simulate contacting a payment gateway like Stripe or PayPal.
            boolean paymentSuccess = processCardTransaction(claim.getTotalAmount());
            if (paymentSuccess) {
                log("Card payment successful.");
                claim.setPaidByPatient(claim.getTotalAmount());
                claim.setStatus("CLOSED");
                claimService.updateClaim(claim);
            } else {
                log("Card payment FAILED.");
                claim.setStatus("PAYMENT_FAILED");
                claimService.updateClaim(claim);
            }
        }
        // Whether it handled it or not, it passes the claim to the next handler.
        // The next handler might be for insurance, which doesn't apply to direct card payments.
        passToNext(claim);
    }

    private boolean processCardTransaction(double amount) {
        log("   (Contacting external payment gateway for $" + String.format("%.2f", amount) + "...)");
        return true; // Assume success for the demo
    }
}