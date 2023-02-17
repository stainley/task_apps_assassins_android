package ca.app.assasins.taskappsassassinsandroid.task.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

@Dao
public abstract class SubTaskDao {

    @Delete
    public abstract void deleteSubTask(SubTask subTask);

    @Insert
    public abstract void insertAll(List<SubTask> subTask);
}
