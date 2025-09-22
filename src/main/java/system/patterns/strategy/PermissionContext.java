
package system.patterns.strategy;

import system.enums.UserRole;
import system.model.User;


   public class PermissionContext {

    private PermissionStrategy strategy;


    public PermissionContext(User user) {
      
        if (user == null || user.getRole() == null) {
       
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


    public void setStrategy(PermissionStrategy strategy) {
        this.strategy = strategy;
    }

  

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