package ca.app.assasins.taskappsassassinsandroid.common.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;

@Dao
public interface AudioDao extends AbstractDao<Audio> {

    @Insert
    @Override
    void save(Audio type);

    @Delete
    @Override
    void delete(Audio type);

    @Update
    @Override
    void update(Audio type);

    @Query("SELECT * FROM AUDIO_TBL")
    @Override
    LiveData<List<Audio>> fetchAll();

    @Query("SELECT * FROM AUDIO_TBL WHERE AUDIO_ID = :id")
    @Override
    LiveData<Optional<Audio>> fetchById(Long id);
}
