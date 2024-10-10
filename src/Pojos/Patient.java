package Pojos;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Patient {
    private int id;
    private String name;
    private String surname;
    private Boolean genetic_background; //TODO como el nombre automatico, cuando se pregunta?
    private Doctor doctor;
    private State state;
    private Treatment treatment;
    private User user;
    private MedicalRecord form;//TODO

    public Patient() {

    }

    public Patient(int id, String name, String surname, int age, Doctor doctor, State state, Treatment treatment, User user) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.doctor = doctor;
        this.state = state;
        this.treatment = treatment;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", doctor=" + doctor +
                ", state=" + state +
                ", treatment=" + treatment +
                ", user=" + user +
                '}';
    }

    private void openRecord(){
        MedicalRecord record = askData();
        record.setPatientName(this.name);
        record.setGenetic_background(this.genetic_background);
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

        List<String> symptoms = Arrays.asList(symptomsInput.split(","));
        symptoms = symptoms.stream().map(String::trim).collect(Collectors.toList()); // Trim extra spaces

        // Create and return a MedicalRecord object with collected data
        return new MedicalRecord(age, weight, height, symptoms);
    }
}
