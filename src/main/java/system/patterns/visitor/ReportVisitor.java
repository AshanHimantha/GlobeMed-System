package system.patterns.visitor;

import java.time.LocalDate;
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
     String getReportTitle();
    String getDateRange(LocalDate from, LocalDate to);
    String getSummaryHtml();
}
