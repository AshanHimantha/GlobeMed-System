/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;

/**
 *
 * @author User
 */
public class DoctorPermissionStrategy implements PermissionStrategy {

    @Override
    public boolean canViewPatientRecords() {
        return true; // Doctors can view records
    }

    @Override
    public boolean canEditPatientRecords() {
        return true; // Doctors can edit clinical parts of a record
    }

    @Override
    public boolean canScheduleAppointments() {
        return false; // Typically, doctors don't schedule their own appointments
    }

    @Override
    public boolean canPrescribeMedication() {
        return true; // The primary role of a doctor
    }

    @Override
    public boolean canProcessBilling() {
        return false; // Doctors are not involved in billing
    }
    
    @Override
    public boolean canGenerateFinancialReports() {
        return false; // Doctors see clinical reports, not financial ones
    }
}