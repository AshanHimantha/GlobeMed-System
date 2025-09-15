# GlobeMed Healthcare Management System
<img width="1200" height="400" alt="globc" src="https://github.com/user-attachments/assets/9be3fe3f-3a9f-4d82-b9a3-a77c87e95b9b" />

This repository contains the source code for the GlobeMed Healthcare Management System, a comprehensive desktop application developed as a project for the Object-Oriented Design Patterns II unit. The system is designed to address the operational needs of a modern, multi-facility healthcare organization.

## Project Overview

The GlobeMed system is a robust, scalable, and maintainable application built in Java. It provides a role-based user interface for managing patient records, scheduling complex multi-location appointments, and processing interactive billing and insurance claims. The core of the project is a practical demonstration of how fundamental object-oriented design patterns can be applied to solve real-world software architecture challenges.
<img width="759" height="562" alt="Screenshot 2025-08-31 193433" src="https://github.com/user-attachments/assets/e0536a48-8535-4e2d-8f2a-b5307a50f29f" />

## Features

*   **Secure, Role-Based Access Control:** Users (Admin, Doctor, Nurse, Pharmacist) have different permissions and see different UI components based on their role.
*   **Patient Management:** Full CRUD (Create, Read, Update, Delete) functionality for patient records with a live database search.
*   **Multi-Location Appointment Scheduling:** A sophisticated scheduling module that handles different appointment types (Consultations, Diagnostics, Surgeries) and manages doctor schedules across multiple facilities.
*   **Interactive Billing & Claims Workflow:** A multi-step, multi-user system for generating and processing insurance claims, from initial validation to final payment.
*   **Prescription and Post-Operative Charge Management:** Doctors can create prescriptions after consultations or log billable items after a surgery.
*   **System Reporting:** A flexible module for generating system-wide reports (e.g., Financial, Activity) without modifying core data models.

## Design Patterns Implemented

This project serves as a practical demonstration of the following five core design patterns:

1.  **Singleton Pattern:** Used for core services like `AuthenticationService` and `PersistenceManager` to ensure a single, globally accessible instance for managing user sessions and the database connection factory.
2.  **Decorator Pattern:** Implemented to add layered security to patient records. `AuditLogDecorator` and `AuthorizationDecorator` are used to wrap patient data objects, adding logging and permission checks dynamically.
3.  **Mediator & Strategy (Compound Pattern):** The `AppointmentScheduler` (Mediator) coordinates the complex logic of booking appointments. It uses the `SchedulingStrategy` (Strategy) to handle different validation rules for different appointment types (e.g., a surgery requires more checks than a consultation), especially in multi-location scenarios.
4.  **Chain of Responsibility Pattern:** Used to model the interactive, long-running workflow of processing an insurance claim. A `Claim` object is passed between different UI panels, where users manually trigger handlers (`ValidationHandler`, `InsuranceApprovalHandler`, `PatientBillingHandler`) to move the claim to its next state.
5.  **Visitor Pattern:** Implemented to generate system-wide reports. `FinancialReportVisitor` and `ActivityReportVisitor` can traverse the application's data objects (`Appointment`, `Claim`) to perform calculations and generate formatted reports without modifying the data classes themselves.

## Technology Stack

*   **Language:** Java (JDK 11+)
*   **Framework / Build Tool:** Apache Maven
*   **User Interface:** Java Swing with the [FlatLaf](https://www.formdev.com/flatlaf/) Look and Feel for a modern UI.
*   **Database:** MySQL
*   **Persistence:** JPA (Jakarta Persistence API) with Hibernate as the ORM provider.

## How to Run the Application

#### Prerequisites
*   Java Development Kit (JDK), version 11 or higher.
*   Apache Maven.
*   A running MySQL server instance.

#### 1. Database Setup
Before running the application, you need to create a database and a user.

1.  Open a MySQL client (like MySQL Workbench).
2.  Run the following SQL commands to create the database and a dedicated user. **Remember to replace `'your_password'` with a secure password.**

    ```sql
    CREATE DATABASE globemed_db;
    CREATE USER 'globemed_user'@'localhost' IDENTIFIED BY 'your_password';
    GRANT ALL PRIVILEGES ON globemed_db.* TO 'globemed_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

#### 2. Configure Database Connection
1.  Open the file `src/main/resources/META-INF/persistence.xml`.
2.  Find the following line and replace `"your_password"` with the password you created in the previous step:
    ```xml
    <property name="jakarta.persistence.jdbc.password" value="your_password"/>
    ```

#### 3. Build and Run
1.  Open a terminal or command prompt in the root directory of the project (where `pom.xml` is located).
2.  Run the Maven "clean and package" command. This will download dependencies and build the application.
    ```bash
    mvn clean package
    ```
3.  Navigate to the `target` directory.
4.  Execute the generated JAR file to run the application.
    ```bash
    java -jar GlobeMedSystem-1.0-SNAPSHOT-jar-with-dependencies.jar 
    ```
    
The application will start, and the login screen will appear. Hibernate will automatically create all the necessary tables in your `globemed_db` database on the first run.

### Sample User Credentials

| Username | Password | Role |
| :--- | :--- | :--- |
| `admincharles` | `adminP@ss!` | ADMIN |
| `dralice` | `pass123` | DOCTOR |
| `nurseben` | `nurse@work`| NURSE |
| `pharmdebra` | `pharm123` | PHARMACIST |

### User Interfaces
<img width="1286" height="818" alt="Screenshot 2025-08-31 185329" src="https://github.com/user-attachments/assets/f1e5b879-5ba1-4252-8dd5-3c00d7857236" />
<img width="989" height="821" alt="Screenshot 2025-08-31 185353" src="https://github.com/user-attachments/assets/784f9d4e-d1ae-47f7-9e81-27d7c36fb50d" />
<img width="1283" height="835" alt="Screenshot 2025-08-31 185408" src="https://github.com/user-attachments/assets/9f6db341-5e84-416f-b9cd-e74f3f1d072e" />
<img width="1281" height="831" alt="Screenshot 2025-08-31 185420" src="https://github.com/user-attachments/assets/35f60045-ffb0-4494-8cb9-707f309949b3" />
<img width="1286" height="817" alt="Screenshot 2025-08-31 185450" src="https://github.com/user-attachments/assets/431595bc-eb01-406c-932c-8901bb3c28b1" />
<img width="785" height="591" alt="Screenshot 2025-08-31 185506" src="https://github.com/user-attachments/assets/a38035df-222b-4725-a72e-47f1f78c6da5" />
<img width="1479" height="1017" alt="Screenshot 2025-08-31 191025" src="https://github.com/user-attachments/assets/515a6fbf-8f22-49d1-8785-54715619855f" />
<img width="1478" height="1016" alt="Screenshot 2025-08-31 191039" src="https://github.com/user-attachments/assets/d42f1bfe-0bc7-4de8-b7c7-4a6c197360d2" />
<img width="1920" height="1080" alt="Screenshot 2025-08-31 193533" src="https://github.com/user-attachments/assets/cedff9ab-e457-4d64-939d-76cf38aef599" />
