package ca.app.assasins.taskappsassassinsandroid.note.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;

@Dao
public abstract class NoteDao implements AbstractDao<Note> {

    @Insert
    @Override
    public abstract void save(Note note);
    //public abstract void save(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long saveNote(Note note);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void savePicture(Picture picture);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void saveAudio(Audio audio);

    @Delete
    @Override
    public abstract void delete(Note type);

    @Update
    @Override
    public abstract void update(Note type);

    @Query("SELECT * FROM NOTE_TBL")
    @Override
    public abstract LiveData<List<Note>> fetchAll();

    @Query("SELECT * FROM NOTE_TBL WHERE NOTE_ID = :noteId")
    @Override
    public abstract LiveData<Optional<Note>> fetchById(Long noteId);

    @Query("SELECT * FROM NOTE_TBL WHERE title = :title")
    public abstract LiveData<List<Note>> fetchByTitle(String title);

    @Query("SELECT * FROM NOTE_TBL WHERE categoryId = :categoryId")
    public abstract LiveData<List<Note>> fetchAllByCategory(Long categoryId);

    @Transaction
    @Query("SELECT * FROM NOTE_TBL WHERE NOTE_ID = :id")
    public abstract LiveData<List<NoteImages>> getAllImagesByNoteId(long id);

    @Transaction
    @Query("SELECT * FROM NOTE_TBL WHERE NOTE_ID = :id")
    public abstract LiveData<List<NoteAudios>> getAllAudiosByNoteId(long id);

    @Transaction
    public Boolean addPicture(Note note, List<Picture> pictures) {
        final long noteId = saveNote(note);
        pictures.forEach(picture -> {
            picture.setParentNoteId(noteId);
            savePicture(picture);
        });
        return true;
    }

    @Transaction
    public Boolean updatePicture(Note note, List<Picture> pictures) {
        update(note);
        pictures.forEach(picture -> {
            picture.setParentNoteId(note.getNoteId());
            savePicture(picture);
        });
        return true;
    }

    @Transaction
    public void saveNoteAll(Note note, List<Picture> pictures) {
        final long noteId = saveNote(note);
        if (!pictures.isEmpty()) {
            pictures.forEach(picture -> {
                picture.setParentNoteId(noteId);
                savePicture(picture);
            });
        }
    }

    @Transaction
    @Query("SELECT * FROM NOTE_TBL WHERE NOTE_ID = :noteId")
    public abstract LiveData<NoteImages> getLasNotePicture(long noteId);

    @Transaction
    public void saveNoteAll(Note newNote, List<Picture> myPictures, List<Audio> mAudios) {
        final long noteId = saveNote(newNote);

        if (!myPictures.isEmpty()) {
            myPictures.forEach(picture -> {
                picture.setParentNoteId(noteId);
                savePicture(picture);
            });
        }
        if (!mAudios.isEmpty()) {
            mAudios.forEach(audio -> {
                audio.setParentNoteId(noteId);
                saveAudio(audio);
            });
        }
    }


    @Transaction
    public void updateNoteAll(Note note, List<Picture> pictures, List<Audio> audios) {
        update(note);
        pictures.forEach(picture -> {
            picture.setParentNoteId(note.getNoteId());
            savePicture(picture);
        });

        audios.forEach(audio -> {
            audio.setParentNoteId(note.getNoteId());
            saveAudio(audio);
        });
    }
}
