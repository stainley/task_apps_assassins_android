package ca.app.assasins.taskappsassassinsandroid.task.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;

@Dao
public abstract class TaskDao implements AbstractDao<Task> {

    @Insert
    @Override
    public abstract void save(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long saveTask(Task task);

    @Insert
    public abstract void savePicture(Picture picture);

    @Delete
    @Override
    public abstract void delete(Task task);

    @Update
    @Override
    public abstract void update(Task task);

    @Query("SELECT * FROM TASK_TBL")
    @Override
    public abstract LiveData<List<Task>> fetchAll();

    @Query("SELECT * FROM TASK_TBL WHERE TASK_ID = :id")
    @Override
    public abstract LiveData<Optional<Task>> fetchById(Long id);

    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId")
    public abstract LiveData<List<Task>> fetchAllByCategory(Long categoryId);

    @Transaction
    @Query("SELECT * FROM TASK_TBL WHERE TASK_ID = :id")
    public abstract LiveData<List<TaskImages>> getAllImagesByTaskId(long id);


    @Transaction
    public Boolean addPicture(Task task, List<Picture> pictures) {
        final long taskId = saveTask(task);
        pictures.forEach(picture -> {
            picture.setParentTaskId(taskId);
            savePicture(picture);
        });
        return true;
    }


    //TODO: to be implement saving audios to the task
    @Transaction
    public void addAudio(Task type, List<Audio> audios) {

    }
}
