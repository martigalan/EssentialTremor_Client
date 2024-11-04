package mainClient;

import pojos.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static boolean control;
    private static Scanner sc = new Scanner(System.in);
    private static Doctor doctor;
    private static Socket socket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;
    private static Patient patient;

    public static void main(String[] args) {
        try {
            String userType = "";

            patient = null;
            socket = new Socket("localhost", 9000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            login(socket, printWriter, bufferedReader);

            /*// 1. Login
            System.out.println("Welcome to the Telemedicine System");
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

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
             */

            // 2. Main Menu
            boolean running = true;
            while (running) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Record Medical Data ");
                System.out.println("2. Open Medical Record");
                System.out.println("3. Send Medical Record");
                System.out.println("4. Exit");

                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        openMedicalRecord();
                        break;
                    case 2:
                        sendMedicalRecord();
                        break;
                    case 3:
                        System.out.println("Exiting... Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                }
            }
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void login(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) throws IOException {
        System.out.print("Username:");
        String username = sc.nextLine();
        System.out.print("Password:");
        String password = sc.nextLine();
        //TODO send info
        printWriter.println(username);
        printWriter.println(password);

        //TODO inicilizar paciente
        //patient= ...
    }


    private static void openMedicalRecord(){
        //TODO llamar funciones
    }

    private static void sendMedicalRecord() {
        // TODO send medical
    }
}
