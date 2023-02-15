package ca.app.assasins.taskappsassassinsandroid.note.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.note.dao.NoteDao;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;

public class NoteRepository {

    private final NoteDao noteDao;

    public NoteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        noteDao = db.noteDao();
    }

    public void save(@NonNull Note note) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.save(note));
    }

    public void delete(@NonNull Note note) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.delete(note));
    }

    public void update(@NonNull Note note) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.update(note));
    }

    public LiveData<List<Note>> fetchByTitle(@NonNull String title) {
        return noteDao.fetchByTitle(title);
    }

    public LiveData<List<Note>> fetchAllNoteByCategory(Long categoryId) {
        return noteDao.fetchAllByCategory(categoryId);
    }

}
