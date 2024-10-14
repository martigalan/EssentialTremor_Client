package Pojos;

import jdbc.ConnectionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Patient {
    private int id;
    private String name;
    private String surname;
    private int age;
    private Boolean genetic_background;
    private Doctor doctor;
    private State state;
    private Treatment treatment;
    private User user;
    private MedicalRecord form;
    private ConnectionManager access;


    public Patient(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public Patient(String name, String surname, Boolean genBack, int age) {
        this.name = name;
        this.surname = surname;
        this.genetic_background = genBack;
        this.age = age;
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
        return "- Name: " + name + '\'' +
                "- Surname: " + surname + '\'' +
                "- State: " + state +
                "- Treatment: " + treatment+
                "- Doctor: " + doctor;
    }

    private void openRecord(){
        MedicalRecord record = askData();
        record.setPatientName(this.name);
        record.setGenetic_background(this.genetic_background);
        record.setAge(this.age);
        //TODO acc and emg
    }

    private MedicalRecord askData() {

        System.out.println("- Weight (kg): ");
        double weight = UserInput.getDouble("");
        System.out.println("- Height (cm): ");
        int height = UserInput.getInt("");
        System.out.println("- Symptoms (enter symptoms separated by commas): ");
        String symptomsInput = UserInput.getString("");

        List<String> symptoms = Arrays.asList(symptomsInput.split(","));
        symptoms = symptoms.stream().map(String::trim).collect(Collectors.toList()); // Trim extra spaces

        return new MedicalRecord(weight, height, symptoms);
    }

    private void seeDoctorsNotes() { //The comments the doctor had to say about the patients disease
        //TODO
        System.out.println("- State:" + this.getState());
        System.out.println("- Treatment:" + this.getTreatment());
    }
}
