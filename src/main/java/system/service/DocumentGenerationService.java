
package system.service;



import system.model.Appointment;
import system.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class DocumentGenerationService {

    public String generatePaymentReceiptAsHtml(Appointment appointment) {
        try (InputStream is = getClass().getResourceAsStream("/templates/receipt_template.html")) {
            if (is == null) {
                return "<html><body><h1>Error: Template not found</h1></body></html>";
            }
            // Read the entire template file into a string
            String template = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            // --- Replace Placeholders ---
            template = template.replace("{{appointmentId}}", String.valueOf(appointment.getId()));
            template = template.replace("{{patientName}}", appointment.getPatient().getName());
            template = template.replace("{{patientId}}", appointment.getPatient().getPatientId());
            template = template.replace("{{serviceName}}", appointment.getServiceName());

            User doctor = appointment.getDoctor();
            template = template.replace("{{doctorName}}", doctor != null ? "Dr. " + doctor.getFirstName() + " " + doctor.getLastName() : "N/A");
            
            template = template.replace("{{dateTime}}", appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            template = template.replace("{{totalAmount}}", String.format("%.2f", appointment.getPrice()));
            template = template.replace("{{paymentMethod}}", appointment.getPaymentMethod().toString());

            User confirmedBy = appointment.getPaymentConfirmedBy();
            template = template.replace("{{confirmedBy}}", confirmedBy != null ? confirmedBy.getUsername() : "N/A");
            template = template.replace("{{confirmationDate}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            return template;

        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>Error generating receipt: " + e.getMessage() + "</h1></body></html>";
        }
    }
}