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
public class SubTask extends Task implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "SUB_TASK_ID")
    private Long subTaskId;
    private String name;
    private Long completionDate;
    private Long updatedDate;

    @Embedded
    private Coordinate coordinate;


}
