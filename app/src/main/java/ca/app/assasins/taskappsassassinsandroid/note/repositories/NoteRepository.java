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
import ca.app.assasins.taskappsassassinsandroid.note.dao.ColorDao;
import ca.app.assasins.taskappsassassinsandroid.note.dao.NoteDao;
import ca.app.assasins.taskappsassassinsandroid.note.model.Color;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteColors;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

public class NoteRepository {

    private final NoteDao noteDao;
    private final PictureDao pictureDao;
    private final AudioDao audioDao;

    private final ColorDao colorDao;

    public NoteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        noteDao = db.noteDao();
        pictureDao = db.pictureDao();
        audioDao = db.audioDao();
        colorDao = db.colorDao();
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

    public LiveData<List<Note>> fetchAllAscByCategory(Long categoryId) {
        return noteDao.fetchAllAscByCategory(categoryId);
    }

    public LiveData<List<Note>> fetchAllDescByCategory(Long categoryId) {
        return noteDao.fetchAllDescByCategory(categoryId);
    }

    public LiveData<List<Note>> fetchAllNotesOrderByDateAsc(Long categoryId) {
        return noteDao.fetchAllNotesOrderByDateAsc(categoryId);
    }

    public LiveData<List<Note>> fetchAllNotesOrderByDateDesc(Long categoryId) {
        return noteDao.fetchAllNotesOrderByDateDesc(categoryId);
    }

    public LiveData<List<NoteImages>> fetchPicturesByNoteId(long noteId) {
        return noteDao.getAllImagesByNoteId(noteId);
    }

    public LiveData<List<NoteAudios>> fetchAudiosByNoteId(long noteId) {
        return noteDao.getAllAudiosByNoteId(noteId);
    }

    public LiveData<List<NoteColors>> fetchColorsByNoteId(long noteId) {
        return noteDao.getAllColorsByNoteId(noteId);
    }

    public void saveNoteWithPictures(Note note, List<Picture> pictures) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.saveNoteAll(note, pictures));

    }

    @Transaction
    public void updateNoteWithPictures(Note note, List<Picture> pictures, List<Audio> audios, Color color) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.updateNoteAll(note, pictures, audios, color));
    }

    public void deletePicture(Picture picture) {
        AppDatabase.databaseWriterExecutor.execute(() -> pictureDao.delete(picture));
    }

    public LiveData<NoteImages> findPictureByNoteId(long noteId) {
        return noteDao.getLasNotePicture(noteId);
    }

    @Transaction
    public void saveNoteWithPicturesAudios(Note newNote, List<Picture> myPictures, List<Audio> mAudios, Color color) {
        AppDatabase.databaseWriterExecutor.execute(() -> noteDao.saveNoteAll(newNote, myPictures, mAudios, color));
    }

    @Transaction
    public void deleteAudio(Audio audio) {
        AppDatabase.databaseWriterExecutor.execute(() -> audioDao.delete(audio));
    }

    public LiveData<List<NoteAudios>> fetAllNoteWithAudio(long categoryId) {
        return noteDao.getAllNotesWithAudio(categoryId);
    }

    public void updateNoteColor(Color color) {
        AppDatabase.databaseWriterExecutor.execute(() -> colorDao.update(color));
    }
    
    public LiveData<List<NoteImages>> fetchAllNoteWithImage(long categoryId) {
        return noteDao.getAllNotesWithImage(categoryId);
    }
}
