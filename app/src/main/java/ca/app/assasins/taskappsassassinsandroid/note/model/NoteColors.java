package ca.app.assasins.taskappsassassinsandroid.note.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class NoteColors {

    @Embedded
    public Note note;

    @Relation(
            parentColumn = "NOTE_ID",
            entityColumn = "PARENT_NOTE_ID"
    )
    public List<Color> colors;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setPictures(List<Color> colors) {
        this.colors = colors;
    }

}