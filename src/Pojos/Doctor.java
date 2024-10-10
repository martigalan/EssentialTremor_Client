package Pojos;

import jdbc.ConnectionManager;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Doctor {

    private String name;
    private String surname;
    private List<Patient> patients;

    private ConnectionManager access;
    private Scanner sc;

    public Doctor(String name, String surname, List<Patient> patients) {
        this.name = name;
        this.surname = surname;
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patients=" + patients +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(name, doctor.name) && Objects.equals(surname, doctor.surname) && Objects.equals(patients, doctor.patients) && Objects.equals(access, doctor.access);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, patients, access);
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public ConnectionManager getAccess() {
        return access;
    }

    private Patient choosePatient() {
        List<Patient> listOfPatients = getPatients();
        for (int i = 0; i < listOfPatients.size(); i++) {
            System.out.println((i + 1) + ":" + listOfPatients.get(i).getName() + " " + listOfPatients.get(i).getSurname());
        }
        System.out.println(("--- Please choose the patient by number: "));
        sc = new Scanner(System.in);
        int number = sc.nextInt();

        Patient patient = listOfPatients.get(number - 1);
        sc.close();
        return patient;
    }

    private void updatePatient(Patient patient) {
        switchState(patient);
        switchTreatment(patient);
        //TODO print patient, with record!?
    }

    private void switchState(Patient patient) {
        Boolean valid = false;
        sc = new Scanner(System.in);
        System.out.println("Current patient state: " + patient.getState());
        System.out.println("Do you wish to change the state?: (y/n)");
        //Switch state
        while (!valid) {
            String option = sc.nextLine();
            if (option.equals("y")) {
                valid = true;
                System.out.println("1: " + State.GOOD);
                System.out.println("2: " + State.BAD);
                System.out.println("3: " + State.STABLE);
                System.out.println("4: " + State.CLOSED);
                System.out.println("Choose an option for state: ");
                int stateOption = sc.nextInt();

                switch (stateOption) {
                    case 1: { patient.setState(State.GOOD); break; }
                    case 2: { patient.setState(State.BAD); break; }
                    case 3: { patient.setState(State.STABLE); break; }
                    case 4: { patient.setState(State.CLOSED); break; }
                }
            } else if (option.equals("n")) {
                valid = true;
                System.out.println("---NO CHANGES WERE MADE");
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }
        sc.close();
    }

    private void switchTreatment(Patient patient){
        Boolean valid = false;
        sc = new Scanner(System.in);
        System.out.println("Current patient treatment: " + patient.getState());
        System.out.println("Do you wish to change the treatment?: (y/n)");
        //Switch treatment
        while (!valid) {
            String option = sc.nextLine();
            if (option.equals("y")) {
                valid = true;
                System.out.println("1: " + Treatment.PRIMIDONE);
                System.out.println("2: " + Treatment.PROPRANOLOL);
                System.out.println("3: " + Treatment.SURGERY);
                System.out.println("Choose an option for treatment: ");
                int treatmentOption = sc.nextInt();
                switch (treatmentOption) {
                    case 1: { patient.setTreatment(Treatment.PRIMIDONE); break; }
                    case 2: { patient.setTreatment(Treatment.PROPRANOLOL); break; }
                    case 3: { patient.setTreatment((Treatment.SURGERY)); break; }
                }
            } else if (option.equals("n")) {
                valid = true;
                System.out.println("---NO CHANGES WERE MADE");
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }
        sc.close();
    }

    private void addPatient(){
        sc = new Scanner(System.in);
        System.out.println("- Name: ");
        String name = sc.nextLine();
        System.out.println("- Surname: ");
        String surname = sc.nextLine();
        System.out.println("- Genetic background: (y/n)");
        String genBackCheck = sc.nextLine();
        Boolean genBack;
        //check
        Boolean valid = false;
        while (!valid) {
            if (genBackCheck.equals("y")) {
                valid = true;
                genBack = true;
            } else if (genBackCheck.equals("n")) {
                valid = true;
                genBack = false;
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }

        //TODO put doctor
        //TODO input state and treatment
        //TODO NO medicalRecord, its made by the patient
    }
}
