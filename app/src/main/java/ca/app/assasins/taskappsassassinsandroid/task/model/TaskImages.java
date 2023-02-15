package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public class TaskImages {

    @Embedded
    public Task task;

    @Relation(
            parentColumn = "TASK_ID",
            entityColumn = "PARENT_TASK_ID"
    )
    public List<Picture> pictures;

}
