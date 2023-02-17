package ca.app.assasins.taskappsassassinsandroid.note.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;


public class NoteAudios {
    @Embedded
    private Note note;

    @Relation(
            parentColumn = "NOTE_ID",
            entityColumn = "PARENT_NOTE_ID"
    )
    private List<Audio> audios;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }
}
