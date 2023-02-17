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
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskAudios;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskWithSubTask;

@Dao
public abstract class TaskDao implements AbstractDao<Task> {

    @Insert
    @Override
    public abstract void save(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long saveTask(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void savePicture(Picture picture);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void saveSubTask(SubTask subTask);

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
    @Query("SELECT * FROM TASK_TBL WHERE TASK_ID = :id")
    public abstract LiveData<List<TaskWithSubTask>> getMySubTaskById(long id);

    @Transaction
    @Query("SELECT * FROM TASK_TBL WHERE TASK_ID = :id")
    public abstract LiveData<List<TaskAudios>> getAllAudiosByTaskId(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void saveAudio(Audio audio);

    @Transaction
    public Boolean addPicture(Task task, List<Picture> pictures) {
        final long taskId = saveTask(task);
        pictures.forEach(picture -> {
            picture.setParentTaskId(taskId);
            savePicture(picture);
        });
        return true;
    }

    @Transaction
    public Boolean updatePicture(Task task, List<Picture> pictures) {
        update(task);
        pictures.forEach(picture -> {
            picture.setParentTaskId(task.getTaskId());
            savePicture(picture);
        });
        return true;
    }

    @Update
    public abstract void updateSubTask(SubTask subTasks);

    @Transaction
    public void saveTaskAll(Task task, List<Picture> pictures, List<SubTask> subTasks, List<Audio> mAudios) {
        final long taskId = saveTask(task);
        if (!pictures.isEmpty()) {
            pictures.forEach(picture -> {
                picture.setParentTaskId(taskId);
                savePicture(picture);
            });
        }
        if (!subTasks.isEmpty()) {
            subTasks.forEach(subTask -> {
                subTask.setTaskParentId(taskId);
                saveSubTask(subTask);
            });
        }

        if (!mAudios.isEmpty()) {
            mAudios.forEach(audio -> {
                audio.setParentTaskId(taskId);
                saveAudio(audio);
            });
        }
    }


    //TODO: to be implement saving audios to the task
    @Transaction
    public void addAudio(Task type, List<Audio> audios) {

    }

    @Transaction
    public void updateAll(Task task, List<Picture> pictures, List<SubTask> subTasks) {
        updatePicture(task, pictures);

        if (!subTasks.isEmpty()) {
            subTasks.forEach(this::updateSubTask);
        }
    }
}
