package ca.app.assasins.taskappsassassinsandroid.task.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;
import ca.app.assasins.taskappsassassinsandroid.task.repositories.TaskRepository;

public class TaskListViewModel extends ViewModel {

    private final TaskRepository taskRepository;

    public TaskListViewModel(Application application) {
        taskRepository = new TaskRepository(application);
    }


    public void saveTask(@NonNull Task task) {
        taskRepository.saveTask(task);
    }


    public void deleteTask(@NonNull Task task) {

    }

    public void updateTask(@NonNull Task task) {
        taskRepository.updateTask(task);
    }

    public LiveData<List<Task>> fetchAllTask() {
        return taskRepository.fetchAllTask();
    }

    public LiveData<List<Task>> fetchAllTaskByCategory(Long categoryId) {
        return taskRepository.fetchAllTaskByCategory(categoryId);
    }

    public void savePictures(List<Picture> pictures) {
        taskRepository.savePictures(pictures);
    }

    public LiveData<List<TaskImages>> fetchPicturesByTaskId(long taskId) {
        return taskRepository.fetchPicturesByTaskId(taskId);
    }
}