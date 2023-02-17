package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;


public class TaskAudios {
    @Embedded
    private Task task;

    @Relation(
            parentColumn = "TASK_ID",
            entityColumn = "PARENT_TASK_ID"
    )
    private List<Audio> audios;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }
}
