package ca.app.assasins.taskappsassassinsandroid.task.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskAudios;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskWithSubTask;
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
        taskRepository.deleteTask(task);
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

    public LiveData<List<Task>> fetchAllAscByCategory(Long categoryId) {
        return taskRepository.fetchAllAscByCategory(categoryId);
    }

    public LiveData<List<Task>> fetchAllDescByCategory(Long categoryId) {
        return taskRepository.fetchAllDescByCategory(categoryId);
    }

    public LiveData<List<Task>> fetchAllTasksOrderByDateAsc(Long categoryId) {
        return taskRepository.fetchAllTasksOrderByDateAsc(categoryId);
    }

    public LiveData<List<Task>> fetchAllTasksOrderByDateDesc(Long categoryId) {
        return taskRepository.fetchAllTasksOrderByDateDesc(categoryId);
    }

    /***
     * Save task with pictures
     * @param task Task
     * @param pictures List<Picture>
     */
    public void savePictures(Task task, List<Picture> pictures) {
        taskRepository.saveTaskWithPictures(task, pictures);
    }

    public void saveTaskWithChildren(@NonNull Task task, @NonNull List<Picture> pictures, @NonNull List<SubTask> subTasks, List<Audio> mAudios) {
        taskRepository.saveTaskWithChildren(task, pictures, subTasks, mAudios);
    }

    public void updatePictures(Task task, List<Picture> pictures) {
        taskRepository.updateTaskWithPictures(task, pictures);
    }

    public LiveData<List<TaskImages>> fetchPicturesByTaskId(long taskId) {
        return taskRepository.fetchPicturesByTaskId(taskId);
    }

    public void deletePicture(@NonNull Picture picture) {
        this.taskRepository.deletePicture(picture);
    }

    public void deleteSubTask(@NonNull SubTask subTask) {
        this.taskRepository.deleteSubTask(subTask);
    }

    public void insertAllSubTask(@NonNull List<SubTask> subTask) {
        this.taskRepository.insertAllSubTask(subTask);
    }

    public LiveData<List<TaskWithSubTask>> fetchSubTaskByTaskId(long id) {
        return taskRepository.fetchAllSubTaskById(id);
    }

    public void updateTaskAll(Task task, List<Picture> pictures, List<SubTask> subTasks, List<Audio> audios) {
        this.taskRepository.updateTaskAll(task, pictures, subTasks, audios);
    }

    public LiveData<List<TaskAudios>> fetchAudiosByTask(long taskId) {
        return taskRepository.fetchAudiosByTaskId(taskId);
    }

    public void deleteAudio(@NonNull Audio audio) {
        this.taskRepository.deleteAudio(audio);
    }

    public LiveData<List<TaskImages>> fetchAllTaskWithImages(long categoryId) {

        return taskRepository.fetchAllTaskWithImages(categoryId);
    }

    public LiveData<List<TaskAudios>> fetchAllTaskWithAudio(long categoryId) {
        return taskRepository.fetchAllTaskWithAudio(categoryId);
    }
}