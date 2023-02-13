package ca.app.assasins.taskappsassassinsandroid.task.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.task.dao.TaskDao;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
    }

    public void saveTask(@NonNull Task task) {
        AppDatabase.databaseWriterExecutor.execute(() -> taskDao.save(task));
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

}
