package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import ca.app.assasins.taskappsassassinsandroid.common.model.Coordinate;

@Entity(tableName = "SUB_TASK_TBL",
        foreignKeys = @ForeignKey(entity = Task.class, parentColumns = "TASK_ID", childColumns = "SUB_TASK_ID", onDelete = ForeignKey.CASCADE))
public class SubTask implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "SUB_TASK_ID")
    private Long subTaskId;
    @ColumnInfo(name = "SUBTASK_NAME")
    private String name;

    @ColumnInfo(name = "TASK_PARENT_ID")
    private Long taskParentId;

    @Embedded
    private Coordinate coordinate;

    public Long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Long subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Long getTaskParentId() {
        return taskParentId;
    }

    public void setTaskParentId(Long taskParentId) {
        this.taskParentId = taskParentId;
    }
}
