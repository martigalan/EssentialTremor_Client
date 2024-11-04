import pojos.DoctorsNote;
import pojos.MedicalRecord;
import pojos.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String userType = "";

        // 1. Login
        System.out.println("Welcome to the Telemedicine System");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // TODO: login validation function Martina
        if (username.equalsIgnoreCase("doctor")) {
            userType = "Doctor";
        } else if (username.equalsIgnoreCase("patient")) {
            userType = "Patient";
        } else {
            System.out.println("Invalid user type. Access denied.");
            return;
        }

        System.out.println("Login successful! Welcome, " + userType + " " + username + ".");

        // Initialize a sample MedicalRecord and Patient for demonstration
        MedicalRecord medicalRecord = new MedicalRecord();
        Patient patient = new Patient("John", "Doe", true);
        List<DoctorsNote> doctorsNotes = new ArrayList<>();

        // 2. Main Menu
        boolean running = true;
        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Record Medical Data ");
            System.out.println("2. Open Medical Record");
            System.out.println("3. Send Medical Record");
            System.out.println("4. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    recordData(scanner, medicalRecord);
                    break;
                case 2:
                    openMedicalRecord(scanner, userType, doctorsNotes);
                    break;
                case 3:
                    sendMedicalRecord(medicalRecord);
                    break;
                case 4:
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
        scanner.close();
    }

    private static void recordData(Scanner scanner, MedicalRecord medicalRecord) {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter any signs or symptoms: ");
        String signs = scanner.nextLine();

        // TODO: Replace with actual data recording logic
        medicalRecord.setPatientName(name);
        medicalRecord.setSigns(signs);

        System.out.println("Medical data recorded for " + name + " with signs: " + signs);
    }

    private static void openMedicalRecord(Scanner scanner, String userType, List<DoctorsNote> doctorsNotes) {
        if (userType.equals("Doctor")) {
            System.out.print("Enter your name to add a doctor note: ");
            String doctorName = scanner.nextLine();

            System.out.println("Write your note for the medical record:");
            String note = scanner.nextLine();
            DoctorsNote newNote = new DoctorsNote(note);
            doctorsNotes.add(newNote);

            System.out.println("Doctor note added by Dr. " + doctorName + ": " + note);
        } else {
            System.out.println("Accessing medical record...");
            // TODO: Display the patient's medical record
        }
    }

    private static void sendMedicalRecord(MedicalRecord medicalRecord) {
        // TODO: Implement sending medical record socket
        System.out.println("Medical record for " + medicalRecord.getPatientName() + " sent successfully.");
    }
}
