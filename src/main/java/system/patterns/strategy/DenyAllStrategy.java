/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

/**
 *
 * @author User
 */
public class DenyAllStrategy implements PermissionStrategy {

    @Override public boolean canViewPatientRecords() { return false; }
    @Override public boolean canEditPatientRecords() { return false; }
    @Override public boolean canScheduleAppointments() { return false; }
    @Override public boolean canPrescribeMedication() { return false; }
    @Override public boolean canProcessBilling() { return false; }
    @Override public boolean canGenerateFinancialReports() { return false; }
    
}