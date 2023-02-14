package ca.app.assasins.taskappsassassinsandroid.note.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

@Dao
public interface NoteDao extends AbstractDao<Note> {

    @Insert
    @Override
    void save(Note type);

    @Delete
    @Override
    void delete(Note type);

    @Update
    @Override
    void update(Note type);

    @Query("SELECT * FROM NOTE_TBL")
    @Override
    LiveData<List<Note>> fetchAll();

    @Query("SELECT * FROM NOTE_TBL WHERE id = :id")
    @Override
    LiveData<Optional<Note>> fetchById(Long id);

    @Query("SELECT * FROM NOTE_TBL WHERE title = :title")
    LiveData<List<Note>> fetchByTitle(String title);

    @Query("SELECT * FROM NOTE_TBL WHERE categoryId = :categoryId")
    LiveData<List<Note>> fetchAllByCategory(Long categoryId);
}
