package pojos;

import java.time.LocalDate;

public class DoctorsNote {

    /**
     * Name of the doctor that redacts the note
     */
    private String doctorName;
    /**
     * Surname of the doctor that redacts the note
     */
    private String doctorSurname;
    /**
     * String containing the annotations made about a medical record
     */
    private String notes;

    /**
     * State assiganed to the patient by the doctor
     */
    private State state;
    /**
     * Treatment the patient should undergo
     */
    private Treatment treatment;
    /**
     * Date of creation
     */
    private LocalDate date;



    /**
     * Constructor
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     */
    public DoctorsNote(String notes, State state, Treatment treatment) {
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
        this.date = LocalDate.now();
    }

    /**
     * Constructor
     * @param doctorName doctors name
     * @param doctorSurname doctors surname
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     */
    public DoctorsNote(String doctorName, String doctorSurname, String notes, State state, Treatment treatment) {
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
        this.date = LocalDate.now();
    }
    /**
     * Constructor
     * @param doctorName doctors name
     * @param doctorSurname doctors surname
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     * @param date date of creation
     */
    public DoctorsNote(String doctorName, String doctorSurname, String notes, State state, Treatment treatment, LocalDate date) {
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
        this.date = date;
    }


    public State getState() {
        return state;
    }


    public Treatment getTreatment() {
        return treatment;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "DoctorsNote{" +
                "doctorName='" + doctorName + '\'' +
                ", doctorSurname='" + doctorSurname + '\'' +
                ", notes='" + notes + '\'' +
                ", state=" + state +
                ", treatment=" + treatment +
                ", date=" + date +
                '}';
    }
}
