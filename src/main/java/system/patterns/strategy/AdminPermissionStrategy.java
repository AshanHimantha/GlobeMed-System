/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

/**
 *
 * @author User
 */
public class AdminPermissionStrategy implements PermissionStrategy {

    @Override
    public boolean canViewPatientRecords() {
        return false; // Admins should not view sensitive clinical data for privacy
    }

    @Override
    public boolean canEditPatientRecords() {
        return true; // Admins can edit demographic parts (e.g., address, contact info)
    }

    @Override
    public boolean canScheduleAppointments() {
        return true; // A primary role of an admin
    }

    @Override
    public boolean canPrescribeMedication() {
        return false; // Admins cannot prescribe
    }

    @Override
    public boolean canProcessBilling() {
        return true; // A primary role of an admin
    }

    @Override
    public boolean canGenerateFinancialReports() {
        return true; // Admins need access to financial data
    }
}