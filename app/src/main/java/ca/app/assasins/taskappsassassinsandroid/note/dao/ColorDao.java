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
import ca.app.assasins.taskappsassassinsandroid.note.model.Color;

@Dao
public interface ColorDao extends AbstractDao<Color> {

    @Insert
    @Override
    void save(Color color);

    @Delete
    @Override
    void delete(Color color);

    @Update
    @Override
    void update(Color color);

    @Query("SELECT * FROM COLOR_TBL")
    @Override
    LiveData<List<Color>> fetchAll();

    @Query("SELECT * FROM COLOR_TBL WHERE COLOR_ID = :id")
    @Override
    LiveData<Optional<Color>> fetchById(Long id);
}