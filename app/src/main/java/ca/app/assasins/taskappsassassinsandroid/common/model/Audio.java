package ca.app.assasins.taskappsassassinsandroid.common.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "AUDIO_TBL")
public class Audio implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "AUDIO_ID")
    private long id;
    @ColumnInfo(name = "PATH")
    private String path;
    @ColumnInfo(name = "CREATION_DATE")
    private Long creationDate;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
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
