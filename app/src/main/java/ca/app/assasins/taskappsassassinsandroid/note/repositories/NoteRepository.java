package ca.app.assasins.taskappsassassinsandroid.note.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AudioDao;
import ca.app.assasins.taskappsassassinsandroid.common.dao.PictureDao;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.dao.NoteDao;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

public class NoteRepository {

    private final NoteDao noteDao;
    private final PictureDao pictureDao;
    private final AudioDao audioDao;

    public NoteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        noteDao = db.noteDao();
        pictureDao = db.pictureDao();
        audioDao = db.audioDao();
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

    public LiveData<List<Note>> fetchAllNoteDescByCategory(Long categoryId) {
        return noteDao.fetchAllDescByCategory(categoryId);
    }

    public LiveData<List<NoteImages>> fetchPicturesByNoteId(long noteId) {
        return noteDao.getAllImagesByNoteId(noteId);
    }

    public LiveData<List<NoteAudios>> fetchAudiosByNoteId(long noteId) {
        return noteDao.getAllAudiosByNoteId(noteId);
    }

    public void saveNoteWithPictures(Note note, List<Picture> pictures) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.saveNoteAll(note, pictures));

    }

    @Transaction
    public void updateNoteWithPictures(Note note, List<Picture> pictures, List<Audio> audios) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.updateNoteAll(note, pictures, audios));
    }

    public void deletePicture(Picture picture) {
        AppDatabase.databaseWriterExecutor.execute(() -> pictureDao.delete(picture));
    }

    public LiveData<NoteImages> findPictureByNoteId(long noteId) {
        return noteDao.getLasNotePicture(noteId);
    }

    @Transaction
    public void saveNoteWithPicturesAudios(Note newNote, List<Picture> myPictures, List<Audio> mAudios) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.saveNoteAll(newNote, myPictures, mAudios));
    }

    @Transaction
    public void deleteAudio(Audio audio) {
        AppDatabase.databaseWriterExecutor.execute(() -> audioDao.delete(audio));
    }
}
