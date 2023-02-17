package ca.app.assasins.taskappsassassinsandroid.note.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public class NoteImages {

    @Embedded
    public Note note;

    @Relation(
            parentColumn = "NOTE_ID",
            entityColumn = "PARENT_NOTE_ID"
    )
    public List<Picture> pictures;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}