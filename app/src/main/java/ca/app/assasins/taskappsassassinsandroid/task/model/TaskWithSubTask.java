package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskWithSubTask {

    @Embedded
    private Task task;

    @Relation(
            parentColumn = "TASK_ID",
            entityColumn = "TASK_PARENT_ID"
    )
    private List<SubTask> subTasks;

    public TaskWithSubTask(Task task, List<SubTask> subTasks) {
        this.task = task;
        this.subTasks = subTasks;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
