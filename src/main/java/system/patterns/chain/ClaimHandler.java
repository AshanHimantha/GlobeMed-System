
package system.patterns.chain;

import javax.swing.JPanel;
import system.service.ClaimService;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import system.model.Claim;


public abstract class ClaimHandler {
    protected ClaimHandler nextHandler;
    protected final ClaimService claimService;
    private JTextArea logArea; // The UI component to log to

    /**
     * Default constructor.
     */
    public ClaimHandler() {
        this.claimService = new ClaimService();
    }

    public void setNextHandler(ClaimHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    /**
     * Sets the JTextArea that this handler and all subsequent handlers will log to.
     * @param logArea The JTextArea from the UI panel.
     */
    public void setLogArea(JTextArea logArea) {
        this.logArea = logArea;
    }

    // The signature no longer needs the JPanel, as logging is handled internally.
    public abstract void processClaim(Claim claim);

    protected void passToNext(Claim claim) {
        if (nextHandler != null) {
            // Pass the log area down the chain
            nextHandler.setLogArea(this.logArea);

            nextHandler.processClaim(claim);
        } else {
            log("End of processing chain.");
        }
    }

    /**
     * A thread-safe method for handlers to write messages to the UI's log.
     */
    protected void log(String message) {
        SwingUtilities.invokeLater(() -> {
            if (logArea != null) {
                logArea.append("-> " + message + "\n");
            }
        });
        System.out.println("HANDLER LOG: " + message); // Also log to console for debugging
    }
}