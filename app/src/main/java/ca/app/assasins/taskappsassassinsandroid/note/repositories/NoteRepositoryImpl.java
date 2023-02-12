package ca.app.assasins.taskappsassassinsandroid.note.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.common.repository.Repository;
import ca.app.assasins.taskappsassassinsandroid.note.dao.NoteDao;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;

public class NoteRepositoryImpl implements Repository<Note> {

    private final NoteDao noteDao;

    public NoteRepositoryImpl(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        noteDao = db.noteDao();
    }

    @Override
    public void save(Note note) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.save(note));
    }

    @Override
    public void update(Note note) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.update(note));
    }

    @Override
    public void delete(Note note) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.delete(note));
    }

    @Override
    public LiveData<List<Note>> fetchAll() {
        return noteDao.fetchAll();
    }

    @Override
    public LiveData<Note> fetchByName(String name) {
        return null;
    }

    @Override
    public LiveData<Note> fetchByTitle(@NonNull String title) {

        return noteDao.fetchByTitle(title);
    }

    @Override
    public LiveData<Optional<Note>> fetchById(Long id) {
        return noteDao.fetchById(id);
    }


}
