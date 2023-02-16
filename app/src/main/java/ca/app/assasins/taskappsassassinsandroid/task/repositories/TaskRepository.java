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
import ca.app.assasins.taskappsassassinsandroid.task.dao.SubTaskDao;
import ca.app.assasins.taskappsassassinsandroid.task.dao.TaskDao;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskWithSubTask;

public class TaskRepository {
    private final TaskDao taskDao;
    private final PictureDao pictureDao;

    private final SubTaskDao subTaskDao;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        pictureDao = db.pictureDao();
        subTaskDao = db.subTaskDao();
    }

    public void saveTask(@NonNull Task task) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.saveTask(task));
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
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.addPicture(task, pictures));
    }

    @Transaction
    public void updateTaskWithPictures(Task task, List<Picture> pictures) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.updatePicture(task, pictures));
    }

    public void saveTaskWithChildren(Task task, List<Picture> pictures, List<SubTask> subTasks) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.saveTaskAll(task, pictures, subTasks));

    }

    public void deletePicture(Picture picture) {
        AppDatabase.databaseWriterExecutor.execute(() -> pictureDao.delete(picture));
    }

    public LiveData<List<TaskWithSubTask>> fetchAllSubTaskById(long id) {
        return taskDao.getMySubTaskById(id);
    }

    public void updateTaskAll(Task task, List<Picture> pictures, List<SubTask> subTasks) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.updateAll(task, pictures, subTasks));
    }

    public void deleteSubTask(SubTask subTask) {
        AppDatabase.databaseWriterExecutor.execute(() -> subTaskDao.deleteSubTask(subTask));
    }

    public void insertAllSubTask(List<SubTask> subTask) {
        AppDatabase.databaseWriterExecutor.execute(() -> subTaskDao.insertAll(subTask));
    }

}
