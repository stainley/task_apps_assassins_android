package ca.app.assasins.taskappsassassinsandroid.task.repositories;

import android.app.Application;

import androidx.annotation.NonNull;

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
}
