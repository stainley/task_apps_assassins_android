package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import ca.app.assasins.taskappsassassinsandroid.common.model.Coordinate;

@Entity(tableName = "TASK_TBL",
        indices = @Index("TASK_NAME")
)
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TASK_ID")
    private long taskId;
    @ColumnInfo(name = "TASK_NAME")
    private String taskName;
    @ColumnInfo(name = "COMPLETED")
    private boolean isCompleted;
    @ColumnInfo(name = "CREATION_DATE")
    private long creationDate;
    @ColumnInfo(name = "COMPLETION_DATE")
    private long completionDate;
    @Embedded
    private Coordinate coordinate;
    @ColumnInfo(name = "CATEGORY_ID")
    private Long categoryId;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(long completionDate) {
        this.completionDate = completionDate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }


}
