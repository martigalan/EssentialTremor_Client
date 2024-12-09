package pojos;

import bITalino.BITalino;
import bITalino.BITalinoException;
import bITalino.Frame;
import data.ACC;
import data.Data;
import data.EMG;

import javax.bluetooth.RemoteDevice;
import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Patient {

    /**
     * Patients name
     */
    private String name;
    /**
     * Patients surname
     */
    private String surname;
    /**
     * Boolean to identify if the patient has a genetic predisposition of essential tremor
     * TRUE if there is, FALSE if not
     */
    private Boolean genetic_background;

    /**
     * A list of all the medical records the patient has
     */
    private List<MedicalRecord> medicalRecords;
    /**
     * A list of the doctors that the patient has
     */
    private List<Doctor> doctors;
    /**
     * User information
     */
    private  User user;

    /**
     * Constructor
     *
     * @param name    patients name
     * @param surname patients surname
     * @param genBack patient genetic background of essential tremor
     */
    public Patient(String name, String surname, Boolean genBack) {
        this.name = name;
        this.surname = surname;
        this.genetic_background = genBack;
        this.medicalRecords = new ArrayList<MedicalRecord>();
        this.doctors = new ArrayList<Doctor>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Patients String representation
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "- Name: " + name + '\'' +
                "- Surname: " + surname + '\'';
        //"- State: " + state +
        //"- Treatment: " + treatment;
    }

    /**
     * Creates a medical record calling other auxiliar functions
     */
    public void openRecord() {
        MedicalRecord record = askData();
        record.setPatientName(this.name);
        record.setPatientSurname(this.surname);
        record.setGenetic_background(this.genetic_background);
        Data data = obtainData(); //aquí cojo las señales del BITalino
        record.setAcceleration(data.getAcc());
        record.setEmg(data.getEmg());
        this.getMedicalRecords().add(record);
    }

    /**
     * Function to record EMG and ACC signals with Bitalino
     *
     * @return Data, a set of emg and acc recorded data
     */
    private Data obtainData() {
        Frame[] frame;
        Data data = null;
        BITalino bitalino = null;
        try {
            bitalino = new BITalino();
            // Code to find Devices
            //Only works on some OS
            Vector<RemoteDevice> devices = bitalino.findDevices();
            System.out.println(devices);

            //You need TO CHANGE THE MAC ADDRESS
            //You should have the MAC ADDRESS in a sticker in the Bitalino
            String macAddress = "20:17:11:20:51:27";

            //Sampling rate, should be 10, 100 or 1000
            int SamplingRate = 100;
            bitalino.open(macAddress, SamplingRate);

            //0--1= EMG
            //5--6=ACC
            int[] channelsToAcquire = {0, 5}; //aquí le digo EXACTAMENTE de qué canales quiero que coja los datos
            bitalino.start(channelsToAcquire);

            //Objects EMG and ACC
            ACC acc = new ACC();
            EMG emg = new EMG();

            //Read in total 10000000 times
            for (int j = 0; j < 100; j++) {

                //Each time read a block of 10 samples
                int block_size = 10;
                frame = bitalino.read(block_size);

                System.out.println("size block: " + frame.length);

                //Print the samples
                for (int i = 0; i < frame.length; i++) {

                    acc.getTimestamp().add(j * block_size + i);
                    emg.getTimestamp().add(j * block_size + i);

                    emg.getSignalData().add(frame[i].analog[0]);
                    acc.getSignalData().add(frame[i].analog[1]);
                    System.out.println((j * block_size + i)//Time
                                    + " seq: " + frame[i].seq + " "
                                    + frame[i].analog[0] + " "//EMG
                                    + frame[i].analog[1] + " "//ACC
                            //  + frame[i].analog[2] + " "
                            //  + frame[i].analog[3] + " "
                            //  + frame[i].analog[4] + " "
                            //  + frame[i].analog[5]
                    );

                }
            }
            //stop acquisition
            bitalino.stop();

            /*System.out.println(emg.getTimestamp());
            System.out.println(emg.getSignalData());
            System.out.println(acc.getSignalData());*/
            data = new Data(acc, emg);
            saveDataToFile(this.getName(), acc, emg); //save info in a txt file

        } catch (BITalinoException ex) {
            Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                //close bluetooth connection
                if (bitalino != null) {
                    Thread.sleep(200); // Pequeño retardo
                    bitalino.close();
                    System.out.println("Bluetooth connection rightly closed.");
                }
            } catch (BITalinoException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Interrupción del hilo: " + e.getMessage());
            }
        }
        return data;
    }

    /**
     * Saves the acc and emg data to a txt file for the user
     *
     * @param patientName name of the patient
     * @param acc         an array of acc values
     * @param emg         and array of emg values
     */
    private void saveDataToFile(String patientName, ACC acc, EMG emg) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = now.format(formatter);

        Path folderPath = Paths.get("BITalinoData");
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, "Error creating directory", e);
        }

        String fileName = "BITalinoData/" + patientName + "_" + timestamp + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Patient Name: " + patientName + "\n");
            writer.write("Date and Time: " + now + "\n\n");

            writer.write("EMG Data:\n");
            for (int i = 0; i < emg.getSignalData().size(); i++) {
                writer.write("Time: " + emg.getTimestamp().get(i) + ", Signal: " + emg.getSignalData().get(i) + "\n");
            }

            writer.write("\nACC Data:\n");
            for (int i = 0; i < acc.getSignalData().size(); i++) {
                writer.write("Time: " + acc.getTimestamp().get(i) + ", Signal: " + acc.getSignalData().get(i) + "\n");
            }

            System.out.println("Data saved to " + fileName);
        } catch (IOException e) {
            Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, "Error writing file", e);
        }
    }

    /**
     * Asks the user to input additional data for the medical record: age, weight, height and symptoms.
     *
     * @return a partially-complete Medical Record
     */
    private MedicalRecord askData() {
        Scanner sc = new Scanner(System.in);
        System.out.println("- Age: ");
        int age = sc.nextInt();
        System.out.println("- Weight (kg): ");
        double weight = sc.nextDouble();
        System.out.println("- Height (cm): ");
        int height = sc.nextInt();
        System.out.println("- Symptoms (enter symptoms separated by commas): ");
        sc.nextLine();
        String symptomsInput = sc.nextLine();

        //Takes the symptoms input and creates a List
        List<String> symptoms = Arrays.asList(symptomsInput.split(","));
        symptoms = symptoms.stream().map(String::trim).collect(Collectors.toList()); // Trim extra spaces
        //sc.close(); NO CERRAR SCANNER QUE DA ERROR CON BITALINO - BLUETOOTH SINO
        return new MedicalRecord(age, weight, height, symptoms);
    }

    /**
     * Chooses the medical record to send
     *
     * @return the Medical Record selected by the user
     */
    public MedicalRecord chooseMR() {
        int size = this.getMedicalRecords().size();
        if (size == 0) {
            System.out.println("No medical records found.");
            return null;
        }

        System.out.println("You have the following medical records available:");
        for (int i = 0; i < size; i++) {
            System.out.println((i + 1) + ". " + this.getMedicalRecords().get(i).toString());
        }

        Scanner sc = new Scanner(System.in);
        int choice = -1;
        while (choice < 1 || choice > size) {
            System.out.print("Enter the number of the medical record you want to select: ");
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.next();
            }
        }

        MedicalRecord mr = this.getMedicalRecords().get(choice - 1);
        return mr;
    }

    /**
     * Send the Medical Record to the server for the doctor to see, it is encrypted
     *
     * @param medicalRecord complete Medical Record
     * @param printWriter   to send data
     * @param publicKey to encrypt data
     * @throws IOException in case the connection fails
     */
    public void sendMedicalRecord(MedicalRecord medicalRecord, PrintWriter printWriter, RSAPublicKey publicKey) throws Exception {
        System.out.println("Connection established... sending text");
        //printWriter.println(medicalRecord.getPatientName());
        String encryptedPatientName = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(medicalRecord.getPatientName(), publicKey)
        );
        printWriter.println(encryptedPatientName);
        //printWriter.println(medicalRecord.getPatientSurname());
        String encryptedPatientSurname = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(medicalRecord.getPatientSurname(), publicKey)
        );
        printWriter.println(encryptedPatientSurname);
        //printWriter.println(medicalRecord.getAge());//int
        String age = String.valueOf(medicalRecord.getAge());
        String encryptedAge = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(age, publicKey)
        );
        printWriter.println(encryptedAge);
        //printWriter.println(medicalRecord.getWeight());//double
        String weight = String.valueOf(medicalRecord.getWeight());
        String encryptedWeight = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(weight, publicKey)
        );
        printWriter.println(encryptedWeight);
        //printWriter.println(medicalRecord.getHeight());//int
        String height = String.valueOf(medicalRecord.getHeight());
        String encryptedHeight = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(height, publicKey)
        );
        printWriter.println(encryptedHeight);
        /*symptoms
        String symptoms = joinWithCommas(medicalRecord.getSymptoms());
        printWriter.println(symptoms);*/
        String symptoms = joinWithCommas(medicalRecord.getSymptoms());
        String encryptedSymptoms = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(symptoms, publicKey)
        );
        printWriter.println(encryptedSymptoms);
        //timestamp
        String time = joinIntegersWithCommas(medicalRecord.getAcceleration().getTimestamp());
        printWriter.println(time);
        /*String timestamp = joinIntegersWithCommas(medicalRecord.getAcceleration().getTimestamp());
        String encryptedTimestamp = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(timestamp, publicKey)
        );
        printWriter.println(encryptedTimestamp);*/
        //acc
        String acc = joinIntegersWithCommas(medicalRecord.getAcceleration().getSignalData());
        printWriter.println(acc);
       /* String acceleration = joinIntegersWithCommas(medicalRecord.getAcceleration().getSignalData());
        String encryptedAcceleration = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(acceleration, publicKey)
        );
        printWriter.println(encryptedAcceleration);*/
        //emg
        String emg = joinIntegersWithCommas(medicalRecord.getEmg().getSignalData());
        printWriter.println(emg);
        /*String emg = joinIntegersWithCommas(medicalRecord.getEmg().getSignalData());
        String encryptedEmg = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(emg, publicKey)
        );
        printWriter.println(encryptedEmg);*/
        //printWriter.println(medicalRecord.getGenetic_background());//boolean
        String geneticBackground = String.valueOf(medicalRecord.getGenetic_background());
        String encryptedGeneticBackground = Base64.getEncoder().encodeToString(
                encryptDataWithPublicKey(geneticBackground, publicKey)
        );
        printWriter.println(encryptedGeneticBackground);
    }

    /**
     * Creates a String from a List
     *
     * @param list list of Strings
     * @return String with items of the list separated with commas
     */
    public static String joinWithCommas(List<String> list) {
        return String.join(",", list);
    }

    /**
     * Creates a String with the integer values of a List
     *
     * @param list list of Integers
     * @return String with the integer values separated with commas
     */
    public static String joinIntegersWithCommas(List<Integer> list) {
        return list.stream()
                .map(String::valueOf) // Convert Integer to String
                .collect(Collectors.joining(","));
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
