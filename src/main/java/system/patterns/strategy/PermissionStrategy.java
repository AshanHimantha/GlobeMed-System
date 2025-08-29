/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

/**
 *
 * @author User
 */
public interface PermissionStrategy {
    
    boolean canViewPatientRecords();
    
    boolean canEditPatientRecords();
    
    boolean canScheduleAppointments();
    
    boolean canPrescribeMedication();
    
    boolean canProcessBilling();
    
    boolean canGenerateFinancialReports();
    
}
