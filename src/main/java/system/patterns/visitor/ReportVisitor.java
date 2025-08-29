package system.patterns.visitor;

import system.model.Appointment;
import system.model.Claim;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
public interface ReportVisitor {
    void visit(Appointment appointment);
    void visit(Claim claim);
    // We could add more, e.g., void visit(Patient patient);
    
    String getReport(); 
}
