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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskAudios;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskWithSubTask;

@Dao
public abstract class TaskDao implements AbstractDao<Task> {

    //Firebase Database
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://taskappsassassinsandroid-default-rtdb.firebaseio.com/");

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

    @Update(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId ORDER BY TASK_NAME ASC")
    public abstract LiveData<List<Task>> fetchAllAscByCategory(Long categoryId);

    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId ORDER BY TASK_NAME DESC")
    public abstract LiveData<List<Task>> fetchAllDescByCategory(Long categoryId);

    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId ORDER BY CREATION_DATE ASC")
    public abstract LiveData<List<Task>> fetchAllTasksOrderByDateAsc(Long categoryId);

    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId ORDER BY CREATION_DATE DESC")
    public abstract LiveData<List<Task>> fetchAllTasksOrderByDateDesc(Long categoryId);

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
    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId")
    public abstract LiveData<List<TaskImages>> getAllTaskWithImages(long categoryId);

    @Transaction
    @Query("SELECT * FROM TASK_TBL WHERE CATEGORY_ID = :categoryId")
    public abstract LiveData<List<TaskAudios>> getAllTaskWithAudios(long categoryId);

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

        // Write a message to Firebase Database
        final DatabaseReference taskReference = database.getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRefSubTasks = null;
        if (currentUser != null) {

            task.setTaskId(taskId);
            String userUID = currentUser.getUid();
            String key = taskReference.push().getKey();
            if(key != null) {
                dbRefSubTasks = taskReference.child(userUID)
                        .child(key);
                dbRefSubTasks.setValue(task);
            }

        }


        if (!pictures.isEmpty()) {
            pictures.forEach(picture -> {
                picture.setParentTaskId(taskId);
                savePicture(picture);
            });
        }
        List<SubTask> cloudSubtasks = new ArrayList<>();

        if (!subTasks.isEmpty()) {
            subTasks.forEach(subTask -> {
                subTask.setTaskParentId(taskId);
                saveSubTask(subTask);
                cloudSubtasks.add(subTask);
            });
        }
        if (currentUser != null) {
            dbRefSubTasks.child("subtasks").setValue(cloudSubtasks);
            //.child(child("subtasks")).setValue(cloudSubtasks);
        }

        if (!mAudios.isEmpty()) {
            mAudios.forEach(audio -> {
                audio.setParentTaskId(taskId);
                saveAudio(audio);
            });
        }
    }

    @Transaction
    public void addAudio(Task type, List<Audio> audios) {

    }

    @Transaction
    public void updateAll(Task task, List<Picture> pictures, List<SubTask> subTasks, List<Audio> audios) {
        updatePicture(task, pictures);

        if (!audios.isEmpty()) {
            audios.forEach(audio -> {
                audio.setParentTaskId(task.getTaskId());
                this.saveAudio(audio);
            });
        }

        if (!subTasks.isEmpty()) {
            subTasks.forEach(this::updateSubTask);
        }
    }


}
