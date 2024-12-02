package mainClient;

import pojos.*;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClient {
    /**
     * Manages inputs from console
     */
    private static Scanner sc = new Scanner(System.in);
    /**
     * Socket for connection
     */
    private static Socket socket;
    /**
     * Sends data to server
     */
    private static PrintWriter printWriter;
    /**
     * Receives data from server
     */
    private static BufferedReader bufferedReader;
    /**
     * Patient object
     */
    private static Patient patient;
    /**
     * Control variable for loops
     */
    private static boolean control;
    /**
     * Control variable for options the user chooses
     */
    private static int option;
    /**
     * Public key for encryption
     */
    private static RSAPublicKey publicKey;

    public static void main(String[] args) {
        try {

            System.out.println("IP of the server: ");
            String ip = sc.nextLine();

            socket = new Socket(ip, 9000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            boolean connection = true;

            //sending the role to start the PatientHandler
            String role = "Patient";
            printWriter.println(role);

            String base64PublicKey = bufferedReader.readLine();

            //decodificar la clave pública
            byte[] decodedKey = Base64.getDecoder().decode(base64PublicKey);

            //crear clave pública
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);


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
                            control = false;
                            //return "exit" to close communication
                            printWriter.println("exit");
                            break;
                        default:
                            System.out.println("  NOT AN OPTION \n");
                            break;
                    }
                }
            } catch (NumberFormatException | NoSuchAlgorithmException e) {
                System.out.println("  NOT A NUMBER. Closing application... \n");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to the server.");
            //e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } finally {
            sc.close();
            releaseResourcesClient(bufferedReader, printWriter, socket);
        }
    }

    /**
     * Registers patient.
     * Sends data (name, surname, username and password) to the server to store in the database.
     * @throws IOException in case of Input/Output exception.
     * @throws NoSuchAlgorithmException in case of encryption error.
     */
    public static void registerPatient() throws Exception {
        System.out.println("Please enter patient details:");
        String name = "", surname = "", username = "", password = "";
        boolean geneticBackground = false;

        while (true) {
            System.out.print("Name: ");
            sc.nextLine();
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

        String command = "register";
        printWriter.println(command);

        String patientData = name + "|" + surname + "|" + geneticBackground + "|" + username + "|" + encryptedPassword + "|patient";
        byte[] encryptedData = encryptDataWithPublicKey(patientData, publicKey);
        printWriter.println(Base64.getEncoder().encodeToString(encryptedData));
        //printWriter.println(patientData);  //Send to server to manage info
        System.out.println("Patient and user data sent to the server for registration.");
        String approval = bufferedReader.readLine();
        if (approval.equals("REGISTER_SUCCESS")) {
            System.out.println("Patient registered correctly.");
        } else {
            System.out.println("Couldn't register patient. Please try again");
        }
    }

    /**
     * Logins patient.
     * The funcion sends input info (username and password) to the server to check in the database.
     * If the info is correct, the user can access a menu to continue with the program, if the info is not correct, the user will go back and have a chance to register or login again.
     * @throws IOException in case of Input/Output exception.
     * @throws NoSuchAlgorithmException in case of encryption error.
     */
    public static void login() throws Exception {
        String command = "login";
        printWriter.println(command);

        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = new User(username, password.getBytes(), "patient");

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hashedPassword = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword) {
            sb.append(String.format("%02x", b));
        }
        String encryptedPassword = sb.toString();

        String loginData = username + "|" + encryptedPassword;
        byte[] encryptedData = encryptDataWithPublicKey(loginData, publicKey);
        printWriter.println(Base64.getEncoder().encodeToString(encryptedData));
        //printWriter.println(loginData);
        System.out.println("User data sent to the server for login.");

        String response = bufferedReader.readLine(); //receive response from server
        System.out.println(response);
        if (response.equals("LOGIN_SUCCESS")) {
            String patientData = bufferedReader.readLine();
            String[] patientInfo = patientData.split("\\|");
            patient = new Patient(patientInfo[0], patientInfo[1], Boolean.parseBoolean(patientInfo[2]));
            patient.setUser(user);
            System.out.println("Welcome, " + patient.getName() + " " + patient.getSurname());
            menuUser();

        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    /**
     * Main user menu.
     */
    public static void menuUser() {
        try {
            control = true;
            while (control) {
                printPatientMenu();
                try {
                    if (sc.hasNextInt()) {
                        option = sc.nextInt();
                    } else {
                        option = 0;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                    continue;
                }
                switch (option) {
                    case 1: {
                        openMedicalRecord();
                        break;
                    }
                    case 2: {
                        sendMedicalRecord();
                        break;
                    }
                    case 3: {
                        seeDoctorsNote();
                        break;
                    }
                    case 0: {
                        control = false;
                        //return "exit" to close communication
                        printWriter.println("exit");
                        break;
                    }
                    default: {
                        System.out.println("  NOT AN OPTION \n");
                        break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("  NOT A NUMBER. Closing application... \n");
        } catch (Exception e) {
            System.out.println("Error when connecting to the server.");
        }
    }

    /**
     * The patient can choose a medical record and see the doctors notes associated to it.
     * @throws IOException in case of Input/Output exception.
     */
    private static void seeDoctorsNote() throws Exception {

        String command = "DoctorsNote";
        printWriter.println(command);

        byte[] encryptedPName = encryptDataWithPublicKey(patient.getName(), publicKey);
        printWriter.println(Base64.getEncoder().encodeToString(encryptedPName));
        //printWriter.println(patient.getName());
        byte[] encryptedPSurname = encryptDataWithPublicKey(patient.getSurname(), publicKey);
        printWriter.println(Base64.getEncoder().encodeToString(encryptedPSurname));
        //printWriter.println(patient.getSurname());

        String response;
        Integer numberOfMR = Integer.parseInt(bufferedReader.readLine());
        Integer i = 0;
        while (i < numberOfMR) {
            response = bufferedReader.readLine();
            System.out.println(response);
            i++;
        }

        //choose id of medical record
        System.out.println("Please choose a medical record ID:");
        Integer mr_id = sc.nextInt();
        //printWriter.println(mr_id);
        String mrID = String.valueOf(mr_id);
        byte[] encryptedMrId = encryptDataWithPublicKey(mrID, publicKey);
        printWriter.println(Base64.getEncoder().encodeToString(encryptedMrId));

        String mrNull = bufferedReader.readLine();
        if (mrNull.equals("NULL")){
            System.out.println("No medical records found with that ID.");
        } else {
            String mrCorrect = bufferedReader.readLine();
            if (mrCorrect.equals("NOT_CORRECT")) {
                System.out.println("That ID doesn't match any of your medical records.");
            } else if (mrCorrect.equals("CORRECT")) {
                String approval = bufferedReader.readLine();
                if (approval.equals("FOUND")) {
                    Integer numberOfDN = Integer.parseInt(bufferedReader.readLine());
                    i = 0;
                    while (i < numberOfDN) {
                        response = bufferedReader.readLine();
                        System.out.println(response);
                        i++;
                    }

                    //choose dn id
                    System.out.println("Please choose a doctors note ID:");
                    Integer dn_id = sc.nextInt();
                    //printWriter.println(dn_id);
                    String dnID = String.valueOf(dn_id);
                    byte[] encryptedDnId = encryptDataWithPublicKey(dnID, publicKey);
                    printWriter.println(Base64.getEncoder().encodeToString(encryptedDnId));

                    String dnNull = bufferedReader .readLine();
                    if (dnNull.equals("NULL")){
                        System.out.println("No doctors note found with that ID.");
                    } else {
                        String dnCorrect = bufferedReader.readLine();
                        if (dnCorrect.equals("NOT_CORRECT")) {
                            System.out.println("That ID doesn't match any of your doctors notes.");
                        } else if (dnCorrect.equals("CORRECT")) {
                            //receive doctors note
                            String dName = bufferedReader.readLine();
                            String dSurname = bufferedReader.readLine();
                            String notes = bufferedReader.readLine();
                            Integer st_id = Integer.parseInt(bufferedReader.readLine());
                            State st = State.getById(st_id);
                            Integer trt_id = Integer.parseInt(bufferedReader.readLine());
                            Treatment trt = Treatment.getById(trt_id);
                            LocalDate date = LocalDate.parse(bufferedReader.readLine());
                            DoctorsNote dn = new DoctorsNote(dName, dSurname, notes, st, trt, date);

                            System.out.println(dn);
                        }
                    }
                } else {
                    System.out.println("No doctors note found for this medical record.");
                }
            }
        }
    }

    /**
     * Prints Register/Login menu.
     */
    public static void printLoginMenu() {
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

    /**
     * Prints main menu.
     */
    public static void printPatientMenu() {
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Open Medical Record                           @@");
        System.out.println("@@                 2. Send Medical Record                           @@");
        System.out.println("@@                 3. See Doctors Note                              @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    /**
     * Releases resources when finishing program.
     * @param bf BufferedReader for input.
     * @param pw PrintWriter for output.
     * @param socket Socket for connection.
     */
    private static void releaseResourcesClient(BufferedReader bf, PrintWriter pw, Socket socket) {
        try {
            if (bf != null) bf.close();
            if (pw != null )pw.close();
        } catch (IOException ex) {
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (socket != null )socket.close();
        } catch (IOException ex) {
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Function that lets the user create a new medical record and then displays it.
     * The function also uses other methods to create a txt file that stores the ACC and EMG signals in a folder.
     */
    private static void openMedicalRecord() {
        patient.openRecord();
        System.out.println("Opening medical record...");
        //displays the last one created
        MedicalRecord mr = (patient.chooseMR());
        System.out.println(mr);
    }

    /**
     * Sends the medical record to the server for it to be stored in the database.
     * @throws IOException in case of Input/Output exception.
     */
    private static void sendMedicalRecord() throws Exception {
        MedicalRecord mr = (patient.chooseMR());
        if (mr == null) {
            System.out.println("You don't have any medical records, create one first...");
            return;
        }

        String command = "MedicalRecord";
        printWriter.println(command);

        System.out.println("Sending medical record...");
        patient.sendMedicalRecord(mr, printWriter, publicKey);
        //Receives approval
        String approval = bufferedReader.readLine();
        if (approval.equals("MEDICALRECORD_SUCCESS")) {
            System.out.println("Medical Record sent correctly");
        } else {
            System.out.println("Couldn't send Medical Record. Please, try again.");
        }
    }

    /**
     * Encrypts the given data using the specified RSA public key.
     *
     * @param data the plain text data to be encrypted
     * @param publicKey the RSA public key used for encryption
     * @return a byte array containing the encrypted data
     * @throws Exception if an error occurs during the encryption process, such as issues with the cipher instance
     */
    public static byte[] encryptDataWithPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }
}

