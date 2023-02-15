package ca.app.assasins.taskappsassassinsandroid.common.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "PICTURE_TBL")
public class Picture implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PICTURE_ID")
    private Long id;
    @ColumnInfo(name = "PICTURE_PATH")
    private String path;
    @ColumnInfo(name = "CREATION_DATE")
    private Long creationDate;
    @ColumnInfo(name = "PARENT_TASK_ID")
    private long parentTaskId;
    @ColumnInfo(name = "PARENT_NOTE_ID")
    private long parentNoteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public long getParentNoteId() {
        return parentNoteId;
    }

    public void setParentNoteId(long parentNoteId) {
        this.parentNoteId = parentNoteId;
    }
}
