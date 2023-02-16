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
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteImages;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;

@Dao
public interface NoteDao extends AbstractDao<Note> {

    @Insert
    @Override
    void save(Note note);
    //public abstract void save(Task task);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long saveNote(Note note);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void savePicture(Picture picture);

    @Delete
    @Override
    void delete(Note type);

    @Update
    @Override
    void update(Note type);

    @Query("SELECT * FROM NOTE_TBL")
    @Override
    LiveData<List<Note>> fetchAll();

    @Query("SELECT * FROM NOTE_TBL WHERE noteId = :noteId")
    @Override
    LiveData<Optional<Note>> fetchById(Long noteId);

    @Query("SELECT * FROM NOTE_TBL WHERE title = :title")
    LiveData<List<Note>> fetchByTitle(String title);

    @Query("SELECT * FROM NOTE_TBL WHERE categoryId = :categoryId")
    LiveData<List<Note>> fetchAllByCategory(Long categoryId);

    @Transaction
    @Query("SELECT * FROM NOTE_TBL WHERE noteId = :id")
    public abstract LiveData<List<NoteImages>> getAllImagesByNoteId(long id);
    @Transaction
    public default Boolean addPicture(Note note, List<Picture> pictures) {
        final long noteId = saveNote(note);
        pictures.forEach(picture -> {
            picture.setParentNoteId(noteId);
            savePicture(picture);
        });
        return true;
    }

    @Transaction
    public default Boolean updatePicture(Note note, List<Picture> pictures) {
        update(note);
        pictures.forEach(picture -> {
            picture.setParentNoteId(note.getNoteId());
            savePicture(picture);
        });
        return true;
    }

    @Transaction
    public default void saveNoteAll(Note note, List<Picture> pictures) {
        final long noteId = saveNote(note);
        if (!pictures.isEmpty()) {
            pictures.forEach(picture -> {
                picture.setParentNoteId(noteId);
                savePicture(picture);
            });
        }
    }
}
