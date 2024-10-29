package pojos;

import bITalino.BITalino;
import bITalino.BITalinoException;
import bITalino.BitalinoDemo;
import bITalino.Frame;
import data.ACC;
import data.Data;
import data.EMG;

import javax.bluetooth.RemoteDevice;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Patient {
    private String name;
    private String surname;
    private Boolean genetic_background;
    private User user;
    private List<MedicalRecord> medicalRecords;
    private List<Doctor> doctors;

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

    @Override
    public String toString() {
        return "- Name: " + name + '\'' +
                "- Surname: " + surname + '\'';
                //"- State: " + state +
                //"- Treatment: " + treatment;
    }

    private void openRecord(){
        MedicalRecord record = askData();
        record.setPatientName(this.name);
        record.setPatientSurname(this.surname);
        record.setGenetic_background(this.genetic_background);
        Data data = obtainData();
        record.setAcceleration(data.getAcc());
        record.setEmg(data.getEmg());
        this.getMedicalRecords().add(record);
    }

    private Data obtainData(){
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
                int block_size=10;
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

    private void sendMedicalRecord(MedicalRecord medicalRecord){
        //TODO send info
    }
    private DoctorsNote receiveDoctorsNote(){
        //TODO receive info
        return null;
    }
    private void seeDoctorsNotes() {
        //TODO here the patient chooses what record they want to see
    }

    public static void main(String[] args){
        Patient p = new Patient("a", "a", Boolean.TRUE);
        p.openRecord();
        for (int i=0; i<p.getMedicalRecords().size();i++){
            System.out.println(p.getMedicalRecords().get(i));
        }
    }
}
