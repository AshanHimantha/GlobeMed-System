/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.decorator;

import system.enums.UserRole;


public class AuthorizationDecorator extends PatientRecordDecorator {
    
    private final UserRole userRole;

    public AuthorizationDecorator(PatientRecord decoratedRecord, UserRole userRole) {
        super(decoratedRecord);
        this.userRole = userRole;
    }

    @Override
    public String getDetails() {
        if (hasPermission()) {
            // If user has permission, delegate to the wrapped object.
            return super.getDetails();
        } else {
            // If permission is denied, return an error message instead of the real data.
            return "[ACCESS DENIED]";
        }
    }

    private boolean hasPermission() {
        // For this system, we'll say only Doctors and Nurses can view patient records.
        return userRole == UserRole.DOCTOR || userRole == UserRole.NURSE;
    }
}