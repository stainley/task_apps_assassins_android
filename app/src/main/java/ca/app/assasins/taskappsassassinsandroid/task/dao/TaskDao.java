package ca.app.assasins.taskappsassassinsandroid.task.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

@Dao
public interface TaskDao extends AbstractDao<Task> {

    @Insert
    @Override
    void save(Task task);

    @Delete
    @Override
    void delete(Task task);

    @Update
    @Override
    void update(Task task);

    @Query("SELECT * FROM TASK_TBL")
    @Override
    LiveData<List<Task>> fetchAll();

    @Query("SELECT * FROM TASK_TBL WHERE TASK_ID = :id")
    @Override
    LiveData<Optional<Task>> fetchById(Long id);
}