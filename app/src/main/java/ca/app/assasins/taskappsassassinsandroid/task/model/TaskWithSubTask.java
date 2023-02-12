package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskWithSubTask {

    @Embedded
    private Task task;
    private List<SubTask> subTasks;
}
