package ca.app.assasins.taskappsassassinsandroid.note.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public class NoteImages {

    @Embedded
    public Note note;

    @Relation(
            parentColumn = "noteId",
            entityColumn = "PARENT_NOTE_ID"
    )
    public List<Picture> pictures;

}