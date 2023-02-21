package ca.app.assasins.taskappsassassinsandroid.note.model;

import androidx.room.ColumnInfo;
    import androidx.room.Entity;
    import androidx.room.PrimaryKey;

    import java.io.Serializable;

@Entity(tableName = "COLOR_TBL")
public class Color implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "COLOR_ID")
    private long id;
    @ColumnInfo(name = "COLOR")
    private String color;

    @ColumnInfo(name = "PARENT_TASK_ID")
    private Long parentTaskId;

    @ColumnInfo(name = "PARENT_NOTE_ID")
    private Long parentNoteId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Long getParentNoteId() {
        return parentNoteId;
    }

    public void setParentNoteId(Long parentNoteId) {
        this.parentNoteId = parentNoteId;
    }
}