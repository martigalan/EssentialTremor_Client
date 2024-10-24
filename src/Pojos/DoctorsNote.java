package Pojos;

public class DoctorsNote {

    private String notes;
    //TODO should it have a list of Doctors? If so, create here a Doctor class


    public DoctorsNote(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
