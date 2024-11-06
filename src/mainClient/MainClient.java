package mainClient;

import pojos.MedicalRecord;
import pojos.Patient;
import pojos.User;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClient {
    private static Scanner sc = new Scanner(System.in);
    private static Socket socket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;
    private static InputStream inputStream;
    private static Patient patient;
    private static boolean control;
    private static int option;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 9000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputStream = socket.getInputStream();
            boolean connection = true;

            //3º si quiere login, coger de la bbdd del server el username y password y devuelvo TRUE o FALSE

            int option;
            try {
                control = true;
                while (control) {
                    printLoginMenu();

                    try {
                        option = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        sc.next(); // Clear the invalid input
                        continue; // Restart the loop
                    }
                    switch (option) {
                        case 1:
                            registerPatient();
                            break;
                        case 2:
                            login();
                            break;
                        case 0:
                            //conexion = false;
                            control = false;
                            break;
                        default:
                            System.out.println("  NOT AN OPTION \n");
                            break;
                    }
                }
            } catch (NumberFormatException | NoSuchAlgorithmException e) {
                System.out.println("  NOT A NUMBER. Closing application... \n");
                sc.close();
            }
        } catch (IOException e) {
            System.out.println("Error connecting to the server.");
            e.printStackTrace();
        } finally {
            releaseResourcesClient(inputStream, socket);
        }
    }

    public static void registerPatient() throws IOException, NoSuchAlgorithmException {
        System.out.println("Please enter patient details:");
        String name = "", surname = "", username = "", password = "";
        boolean geneticBackground = false;

        while (true) {
            System.out.print("Name: ");
            name = sc.nextLine().trim();
            if (!name.isEmpty()) break;
            System.out.println("Invalid name. Please enter a valid name.");
        }

        while (true) {
            System.out.print("Surname: ");
            surname = sc.nextLine().trim();
            if (!surname.isEmpty()) break;
            System.out.println("Invalid surname. Please enter a valid surname.");
        }

        while (true) {
            System.out.print("Genetic Background (true/false): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                geneticBackground = Boolean.parseBoolean(input);
                break;
            }
            System.out.println("Invalid input. Please enter 'true' or 'false'.");
        }

        while (true) {
            System.out.print("Username: ");
            username = sc.nextLine().trim();
            if (!username.isEmpty()) break;
            System.out.println("Invalid username. Please enter a valid username.");
        }

        System.out.print("Password: ");
        password = sc.nextLine();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hash = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        String encryptedPassword = sb.toString();

        String patientData = name + "|" + surname + "|" + geneticBackground + "|" + username + "|" + encryptedPassword + "|patient";
        printWriter.println(patientData);  //Send to server
        System.out.println("Patient and user data sent to the server for registration.");
    }

    public static void login() throws IOException, NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hashedPassword = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword) {
            sb.append(String.format("%02x", b));
        }
        String encryptedPassword = sb.toString();

        String loginData = username + "|" + encryptedPassword;
        printWriter.println(loginData);
        System.out.println("User data sent to the server for login.");

        String response = bufferedReader.readLine(); //receive response from server
        if (response.equals("LOGIN_SUCCESS")) {
            String roleResponse = bufferedReader.readLine();
            if (roleResponse.equals("WELCOME_PATIENT")) {
                String patientData = bufferedReader.readLine();
                String[] patientInfo = patientData.split("\\|");
                Patient patient = new Patient(patientInfo[0], patientInfo[1], Boolean.parseBoolean(patientInfo[2]));
                System.out.println("Welcome, " + patient.getName() + " " + patient.getSurname());
                menuUser();
            }
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }


    public static void menuUser(){
        try {
            control = true;
            while (control) {
                printPatientMenu();
                try {
                    option = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                    continue;
                }
                switch (option) {
                    case 1:
                        openMedicalRecord();
                        break;
                    case 2:
                        sendMedicalRecord();
                        break;
                    case 0:
                        control = false;
                        break;
                    default:
                        System.out.println("  NOT AN OPTION \n");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("  NOT A NUMBER. Closing application... \n");
            sc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printLoginMenu(){
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Register                                      @@");
        System.out.println("@@                 2. Login                                         @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    public static void printPatientMenu() {
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Open Medical Record                           @@");
        System.out.println("@@                 2. Send Medical Record                           @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    private static void releaseResourcesClient(InputStream inputStream, Socket socket) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void openMedicalRecord() {
        patient.openRecord();
        System.out.println("Opening medical record...");
        MedicalRecord mr = (patient.chooseMR()); // TODO mirar esto luego
        System.out.println(mr);
    }

    private static void sendMedicalRecord() throws IOException {
        // TODO
        MedicalRecord mr = (patient.chooseMR());
        if (mr == null){
            System.out.println("You don't have any medical records, create one first...");
            return;
        }

        System.out.println("Sending medical record...");
        patient.sendMedicalRecord(mr, socket);
    }
}

