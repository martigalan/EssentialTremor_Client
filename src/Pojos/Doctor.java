package Pojos;

import Authentication.ConnectionManager;

import java.util.List;
import java.util.Objects;

public class Doctor {

    private String name;
    private String surname;
    private List<Patient> patients;

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

    private ConnectionManager access;



}
