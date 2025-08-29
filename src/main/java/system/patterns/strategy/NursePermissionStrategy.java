/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

/**
 *
 * @author User
 */
public class NursePermissionStrategy implements PermissionStrategy {

    @Override
    public boolean canViewPatientRecords() {
        return true; // Nurses need to view patient charts and vitals.
    }

    @Override
    public boolean canEditPatientRecords() {
        return true; // Nurses can update parts of the record, like vital signs.
    }

    @Override
    public boolean canScheduleAppointments() {
        return true; // Nurses often assist with scheduling.
    }

    @Override
    public boolean canPrescribeMedication() {
        return false; // Nurses cannot write prescriptions.
    }

    @Override
    public boolean canProcessBilling() {
        return false; // Billing is typically an administrative role.
    }

    @Override
    public boolean canGenerateFinancialReports() {
        return false; // Nurses do not need access to financial reports.
    }
}