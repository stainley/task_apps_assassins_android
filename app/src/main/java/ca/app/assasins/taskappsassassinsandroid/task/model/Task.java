package ca.app.assasins.taskappsassassinsandroid.task.model;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
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
    private Long creationDate;
    @ColumnInfo(name = "COMPLETION_DATE")
    private Long completionDate;
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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Long completionDate) {
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
