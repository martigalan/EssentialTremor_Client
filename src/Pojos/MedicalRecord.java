package Pojos;

import Data.ACC;
import Data.EMG;

import java.util.List;

public class MedicalRecord {

    private String patientName;
    private int age;
    private double weight;
    private int height;
    private List<String> symptoms;
    private ACC acceleration;
    private EMG emg;
    private Boolean genetic_background;

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

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
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

    public MedicalRecord(String patientName, int age, double weight, int height, List<String> symptoms, ACC acceleration, EMG emg) {
        this.patientName = patientName;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = acceleration;
        this.emg = emg;
    }
    public MedicalRecord(int age, double weight, int height, List<String> symptoms) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "patientName='" + patientName + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", height=" + height +
                ", symptoms=" + symptoms +
                ", genetic_background=" + genetic_background +
                '}';
    }
}
