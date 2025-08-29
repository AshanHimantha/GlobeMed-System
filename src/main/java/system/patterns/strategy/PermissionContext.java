/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

import system.enums.UserRole;
import system.model.User;


   public class PermissionContext {

    private PermissionStrategy strategy;

    /**
     * The context is initialized with the user who is performing the actions.
     * It uses the user's role to select the appropriate strategy.
     * @param user The currently logged-in user.
     */
    public PermissionContext(User user) {
        // This is where the strategy is selected based on the context (the user's role).
        if (user == null || user.getRole() == null) {
            // Default to a "deny all" strategy if user is invalid
            this.strategy = new DenyAllStrategy();
            return;
        }

        UserRole role = user.getRole();
        switch (role) {
            case DOCTOR:
                this.strategy = new DoctorPermissionStrategy();
                break;
            case ADMIN:
                this.strategy = new AdminPermissionStrategy();
                break;
            case NURSE:
                this.strategy = new NursePermissionStrategy();
                break;
            case PHARMACIST:
                this.strategy = new PharmacistPermissionStrategy();
                break;
            default:
                // A safe default for any unknown or unhandled roles
                this.strategy = new DenyAllStrategy();
                break;
        }
    }

    /**
     * The context can also allow the strategy to be changed at runtime if needed.
     * @param strategy The new strategy to use.
     */
    public void setStrategy(PermissionStrategy strategy) {
        this.strategy = strategy;
    }

    // --- The context exposes methods that delegate the call to the current strategy ---

    public boolean canViewPatientRecords() {
        return strategy.canViewPatientRecords();
    }

    public boolean canEditPatientRecords() {
        return strategy.canEditPatientRecords();
    }
    
    public boolean canScheduleAppointments() {
        return strategy.canScheduleAppointments();
    }

    public boolean canPrescribeMedication() {
        return strategy.canPrescribeMedication();
    }
    
    public boolean canProcessBilling() {
        return strategy.canProcessBilling();
    }
    
    public boolean canGenerateFinancialReports() {
        return strategy.canGenerateFinancialReports();
    }
}