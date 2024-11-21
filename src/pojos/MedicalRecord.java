package pojos;

import data.ACC;
import data.EMG;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    /**
     * Name of the patient that creates the record
     */
    private String patientName;
    /**
     * Surname of the patient that creates the record
     */
    private String patientSurname;
    /**
     * Age of the patient that creates the record
     */
    private int age;
    /**
     * Weight of the patient that creates the record
     */
    private double weight;
    /**
     * Height of the patient that creates the record
     */
    private int height;
    /**
     * Symptoms of the patient that creates the record, in the moment of creation
     */
    private List<String> symptoms;
    /**
     * Acceleration data
     */
    private ACC acceleration;
    /**
     * EMG data
     */
    private EMG emg;
    /**
     * Boolean to identify if the patient has a genetic predisposition of essential tremor
     * TRUE if there is, FALSE if not
     */
    private Boolean genetic_background;
    /**
     * List of doctors notes associated to the medical record
     */
    private List<DoctorsNote> doctorsNotes;
    /**
     * List of doctors that receive this medical record
     */
    private List<Doctor> doctors;
    /**
     * Date of creation
     */
    private LocalDate date;


    public Boolean getGenetic_background() {
        return genetic_background;
    }

    public void setGenetic_background(Boolean genetic_background) {
        this.genetic_background = genetic_background;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public void setAcceleration(ACC acceleration) {
        this.acceleration = acceleration;
    }

    public void setEmg(EMG emg) {
        this.emg = emg;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public ACC getAcceleration() {
        return acceleration;
    }

    public EMG getEmg() {
        return emg;
    }


    /**
     * Constructor
     * @param age patients age
     * @param weight patients weight
     * @param height patient height
     * @param symptoms patients symptoms
     */
    public MedicalRecord(int age, double weight, int height, List<String> symptoms) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.doctors = new ArrayList<>();
        this.doctorsNotes = new ArrayList<>();
        this.date = LocalDate.now();
    }


    /**
     * Medical Record string representation
     * @return string representation of medical record
     */
    @Override
    public String toString() {
        return "Patients name: " + patientName +
                "\nSurname: "+ patientSurname +
                "\nAge: " + age +
                "\nWeight: " + weight +
                "\nHeight: " + height +
                "\nSymptoms: " + symptoms +
                "\nGenetic background: " + genetic_background +
                "\nAcceleration data: " + acceleration +
                "\nEMG data: " + emg;
    }

}
