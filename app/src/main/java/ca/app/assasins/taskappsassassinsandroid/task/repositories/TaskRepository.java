package ca.app.assasins.taskappsassassinsandroid.task.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ca.app.assasins.taskappsassassinsandroid.common.dao.PictureDao;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.task.dao.TaskDao;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;

public class TaskRepository {
    private final TaskDao taskDao;
    private final PictureDao pictureDao;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        pictureDao = db.pictureDao();
    }

    public Long saveTask(@NonNull Task task) {
        Long[] rowId = new Long[1];
        AppDatabase.databaseWriterExecutor.execute(() -> rowId[0] = taskDao.saveTask(task));

        return rowId[0];

    }

    public void deleteTask(@NonNull Task task) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.delete(task));
    }

    public void updateTask(@NonNull Task task) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.update(task));
    }

    public LiveData<List<Task>> fetchAllTask() {
        return taskDao.fetchAll();
    }

    public LiveData<List<Task>> fetchAllTaskByCategory(Long categoryId) {
        return taskDao.fetchAllByCategory(categoryId);
    }


    public LiveData<List<TaskImages>> fetchPicturesByTaskId(long taskId) {
        return taskDao.getAllImagesByTaskId(taskId);
    }

    @Transaction
    public void saveTaskWithPictures(Task task, List<Picture> pictures) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.saveTaskWithPicture(task, pictures));
    }
}
