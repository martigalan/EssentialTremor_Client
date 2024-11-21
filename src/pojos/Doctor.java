package pojos;

import data.ACC;
import data.EMG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Doctor {

    /**
     * Doctors name
     */
    private String name;
    /**
     * Doctors surname
     */
    private String surname;
    /**
     * List of patients the doctor has
     */
    private List<Patient> patients;
    /**
     * List of medical records the doctor receives
     */
    private List<MedicalRecord> medicalRecords;
    /**
     * List of doctors notes the doctor redacts
     */
    private List<DoctorsNote> doctorsNotes;


    /**
     * Constructor
     * @param name doctors name
     * @param surname doctors surname
     */
    public Doctor(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.patients = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();
        this.doctorsNotes = new ArrayList<>();
    }

    /**
     * Patients String representation
     * @return String representation
     */
    @Override
    public String toString() {
        return "Doctor{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\''+
                '}';
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public List<DoctorsNote> getDoctorsNote() {
        return doctorsNotes;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(name, doctor.name) && Objects.equals(surname, doctor.surname) && Objects.equals(patients, doctor.patients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, patients);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

}
