package ca.app.assasins.taskappsassassinsandroid.note.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.repositories.NoteRepository;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

public class NoteViewModel extends ViewModel {

    private final NoteRepository noteRepository;

    public NoteViewModel(Application application) {
        noteRepository = new NoteRepository(application);
    }

    public void createNote(@NonNull Note note) {
        noteRepository.save(note);
    }

    public void updateNote(@NonNull Note note) {
        noteRepository.update(note);
    }

    public LiveData<List<Note>> getNoteByTitle(String title) {
        return noteRepository.fetchByTitle(title);
    }

    public LiveData<List<Note>> fetchAllNoteByCategory(Long categoryId) {
        return noteRepository.fetchAllNoteByCategory(categoryId);
    }

    public void deleteNote(@NonNull Note note) {
        noteRepository.delete(note);
    }
}
