package mainClient;

import pojos.Patient;

import java.io.*;
import java.net.Socket;
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

            /*if (!login()) {
                System.out.println("Login failed. Exiting.");
                return;
            }*/

            registerPatient();

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
                            connection = false;
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

    //TODO revisar este con el del server
    public static boolean login() throws IOException {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        printWriter.println("LOGIN|" + username + "|" + password);
        String response = bufferedReader.readLine();

        if ("LOGIN_SUCCESS".equals(response)) {
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Login failed.");
            return false;
        }
    }

    public static void registerPatient() throws IOException {
        System.out.println("Please enter patient details:");
        System.out.print("\nName: ");
        String name = sc.nextLine();
        System.out.print("\nSurname: ");
        String surname = sc.nextLine();
        System.out.print("\nGenetic Background (true/false): ");
        boolean geneticBackground = sc.nextBoolean();
        sc.nextLine();
        String patientData = name + "|" + surname + "|" + geneticBackground;

        printWriter.println(patientData);
        System.out.println("Patient data sent to the server for registration.");
    }

    private static void openMedicalRecord() {
        // TODO
        System.out.println("Opening medical record...");
    }

    private static void sendMedicalRecord() {
        // TODO
        System.out.println("Sending medical record...");
        //patient.sendMedicalRecord(medicalRecord, socket);
    }
}

