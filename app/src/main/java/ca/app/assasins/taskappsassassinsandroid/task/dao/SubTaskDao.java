package ca.app.assasins.taskappsassassinsandroid.task.dao;

import androidx.room.Dao;
import androidx.room.Delete;

import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;

@Dao
public abstract class SubTaskDao {

    @Delete
    public abstract void deleteSubTask(SubTask subTask);
}
