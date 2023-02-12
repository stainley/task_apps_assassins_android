package ca.app.assasins.taskappsassassinsandroid.task.model;

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
        foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "TASK_ID", onDelete = ForeignKey.CASCADE),
        indices = @Index("taskName")
)
public class Task implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "TASK_ID")
    private Long taskId;
    @ColumnInfo(name = "TASK_NAME")
    private String taskName;
    @ColumnInfo(name = "COMPLETED")
    private boolean isCompleted;
    private Long creationDate;
    private Long completionDate;
    @Embedded
    private Coordinate coordinate;
    @Embedded
    private Category category;


}
