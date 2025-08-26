
package system.patterns.chain;

import javax.swing.JTextArea;
import system.model.Claim;


public abstract class ClaimHandler {
    protected ClaimHandler nextHandler;
    protected ClaimService claimService;
    protected JTextArea logArea; // To log progress directly to the UI

    public ClaimHandler(JTextArea logArea) {
        this.claimService = new ClaimService();
        this.logArea = logArea;
    }

    public void setNextHandler(ClaimHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void processClaim(Claim claim);

    protected void passToNext(Claim claim) {
        if (nextHandler != null) {
            nextHandler.processClaim(claim);
        } else {
            log("End of processing chain.");
        }
    }

    protected void log(String message) {
        if (logArea != null) {
            logArea.append("-> " + message + "\n");
        }
        System.out.println(message); // Also log to console
    }
}
