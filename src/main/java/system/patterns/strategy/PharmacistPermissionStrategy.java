/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

/**
 *
 * @author User
 */
public class PharmacistPermissionStrategy implements PermissionStrategy {

    @Override
    public boolean canViewPatientRecords() {
        // Pharmacists can typically only view the MEDICATION part of a record,
        // but for this simplified model, we will grant them full view access.
        return true; 
    }

    @Override
    public boolean canEditPatientRecords() {
        return false; // Pharmacists cannot edit patient records.
    }

    @Override
    public boolean canScheduleAppointments() {
        return false; // Pharmacists do not schedule appointments.
    }

    @Override
    public boolean canPrescribeMedication() {
        return false; // Pharmacists dispense, they do not prescribe.
    }

    @Override
    public boolean canProcessBilling() {
        // This could be true if the pharmacy has its own billing, but for the
        // main hospital system, we'll set it to false.
        return false; 
    }

    @Override
    public boolean canGenerateFinancialReports() {
        return false; // Pharmacists do not need financial reports.
    }
}
