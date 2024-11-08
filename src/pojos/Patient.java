package pojos;

import bITalino.BITalino;
import bITalino.BITalinoException;
import bITalino.BitalinoDemo;
import bITalino.Frame;
import data.ACC;
import data.Data;
import data.EMG;

import javax.bluetooth.RemoteDevice;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * User to store username and password for the application
     */
    private User user;
    /**
     * A list of all the medical records the patient has
     */
    private List<MedicalRecord> medicalRecords;
    /**
     * A list of the doctors that the patient has
     */
    private List<Doctor> doctors;


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

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
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

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecord(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
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
        Data data = obtainData();
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
            int[] channelsToAcquire = {0, 5};
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
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                //close bluetooth connection
                if (bitalino != null) {
                    bitalino.close();
                }
            } catch (BITalinoException ex) {
                Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, "Error creating directory", e);
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
            Logger.getLogger(BitalinoDemo.class.getName()).log(Level.SEVERE, "Error writing file", e);
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
        String symptomsInput = sc.nextLine();

        //Takes the symptoms input and creates a List
        List<String> symptoms = Arrays.asList(symptomsInput.split(","));
        symptoms = symptoms.stream().map(String::trim).collect(Collectors.toList()); // Trim extra spaces
        sc.close();
        return new MedicalRecord(age, weight, height, symptoms);
    }

    /**
     * Chooses the medical record to send
     *
     * @return the last Medical Record of the patients list
     */
    public MedicalRecord chooseMR() { //TODO choose
        int size = this.getMedicalRecords().size();
        MedicalRecord mr = this.getMedicalRecords().get(size - 1);
        return mr;
    }

    /**
     * Send the Medical Record to the server for the doctor to see
     *
     * @param medicalRecord complete Medical Record
     * @param socket        Socket with the connection to the server
     * @throws IOException in case the connection fails
     */
    public void sendMedicalRecord(MedicalRecord medicalRecord, Socket socket) throws IOException {
        //TODO send info, CHECK
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connection established... sending text");
        printWriter.println(medicalRecord.getPatientName());
        printWriter.println(medicalRecord.getPatientSurname());
        printWriter.println(medicalRecord.getAge());//int
        printWriter.println(medicalRecord.getWeight());//double
        printWriter.println(medicalRecord.getHeight());//int
        //symptoms
        String symptoms = joinWithCommas(medicalRecord.getSymptoms());
        System.out.println(symptoms);
        //timestamp
        String time = joinIntegersWithCommas(medicalRecord.getAcceleration().getTimestamp());
        printWriter.println(time);
        //acc
        String acc = joinIntegersWithCommas(medicalRecord.getAcceleration().getSignalData());
        printWriter.println(acc);
        //emg
        String emg = joinIntegersWithCommas(medicalRecord.getEmg().getSignalData());
        printWriter.println(emg);
        printWriter.println(medicalRecord.getGenetic_background());//boolean
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
     * Gets the doctors note about the medical record that was previously sent
     *
     * @return DoctorsNote containing the evaluation
     * @throws IOException in case connection fails
     */
    private DoctorsNote receiveDoctorsNote() throws IOException {
        //TODO check this one
        DoctorsNote doctorsNote = null;
        try (ServerSocket serverSocket = new ServerSocket(9009)) {  // Puerto 9009 para coincidir con el cliente
            System.out.println("Server started, waiting for client...");

            try (Socket socket = serverSocket.accept();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Client connected. Receiving data...");

                // Read each line
                String doctorName = bufferedReader.readLine();
                System.out.println(doctorName);
                String doctorSurname = bufferedReader.readLine();
                System.out.println(doctorSurname);
                String notes = bufferedReader.readLine();
                System.out.println(notes);

                releaseReceivingResources(bufferedReader, socket, serverSocket);

                doctorsNote = new DoctorsNote(doctorName, doctorSurname, notes);
                //TODO meter esto en lista doctor
                //TODO this is in the main
                //DoctorsNote doctorsNote = createDoctorsNote(medicalRecord);
                //medicalRecord.getDoctorsNotes().add(doctorsNote);
                return doctorsNote;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doctorsNote;
    }

    /**
     * Realeases the resources that were used
     *
     * @param bufferedReader used to read
     * @param socket         connection with the server
     * @param socketServidor server socket
     */
    private static void releaseReceivingResources(BufferedReader bufferedReader,
                                                  Socket socket, ServerSocket socketServidor) {
        try {
            bufferedReader.close();
        } catch (IOException ex) {
            Logger.getLogger(Doctor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Doctor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socketServidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Doctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays the DoctorsNote sent by the doctor
     */
    private void seeDoctorsNotes() {
        //TODO here the patient chooses what record they want to see

    }
}
