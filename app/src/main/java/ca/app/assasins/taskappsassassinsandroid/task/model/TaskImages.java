package ca.app.assasins.taskappsassassinsandroid.task.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public class TaskImages {

    @Embedded
    private Task task;

    @Relation(
            parentColumn = "TASK_ID",
            entityColumn = "PICTURE_ID"
    )
    private List<Picture> pictures;

    public TaskImages(Task task, List<Picture> pictures) {
        this.task = task;
        this.pictures = pictures;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
