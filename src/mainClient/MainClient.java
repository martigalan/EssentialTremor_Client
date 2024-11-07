package mainClient;

import pojos.MedicalRecord;
import pojos.Patient;
import pojos.User;

import java.io.*;
import java.net.Socket;
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
            // Parametrizable
            socket = new Socket("localhost", 9000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            boolean connection = true;

            //1º menú register/login
            //2º si quiere register, le manda al server que es patient y sus datos
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


            } catch (NumberFormatException e) {
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

    public static void login() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        String patientData = username + "|" + password;
        printWriter.println(patientData);
        System.out.println("User data sent to the server for login.");

        //TODO comprobar q existe
        //TODO cuando existe
        //devolver datos paciente en la bbdd y inicializar
        //patient = new Patient(...);
        menuUser();
    }

    public static void menuUser(){
        try {
            control = true;
            while (control) {
                System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("@@                                                                  @@");
                System.out.println("@@                 Welcome.                                         @@");
                System.out.println("@@                 1. Open Medical Record                           @@");
                System.out.println("@@                 2. Send Medical Record                           @@");
                System.out.println("@@                 0. Exit                                          @@");
                System.out.println("@@                                                                  @@");
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.print("\nSelect an option: ");

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

    public static void registerPatient() throws IOException {
        System.out.println("Please enter patient details:");
        String name = "";
        String surname = "";
        boolean geneticBackground = false;
        while (true) {
            System.out.print("Name: ");
            name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                break;
            } else {
                System.out.println("Invalid name. Please enter a valid name.");
            }
        }
        while (true) {
            System.out.print("Surname: ");
            surname = sc.nextLine().trim();
            if (!surname.isEmpty()) {
                break;
            } else {
                System.out.println("Invalid surname. Please enter a valid surname.");
            }
        }
        while (true) {
            System.out.print("Genetic Background (true/false): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("true")) {
                geneticBackground = true;
                break;
            } else if (input.equals("false")) {
                geneticBackground = false;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }

        String patientData = name + "|" + surname + "|" + geneticBackground;
        printWriter.println(patientData);

        System.out.println("Patient data sent to the server for registration.");
        patient = new Patient(name, surname, geneticBackground);
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

