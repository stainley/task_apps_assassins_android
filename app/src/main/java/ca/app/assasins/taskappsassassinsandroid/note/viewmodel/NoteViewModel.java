package ca.app.assasins.taskappsassassinsandroid.note.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.repositories.NoteRepository;
import ca.app.assasins.taskappsassassinsandroid.note.repositories.NoteRepositoryImpl;

public class NoteViewModel extends ViewModel {

    private final NoteRepository noteRepository;

    public NoteViewModel(Application application) {
        this.noteRepository = (NoteRepository) new NoteRepositoryImpl(application);
    }

    public void createNote(@NonNull Note note) {
        noteRepository.save(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteRepository.fetchAll();
    }

    public LiveData<Note> getNoteByTitle(String title) {
        return noteRepository.fetchByTitle(title);
    }

    public void deleteNote(@NonNull Note note) {
        noteRepository.delete(note);
    }
}
