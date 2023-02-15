package ca.app.assasins.taskappsassassinsandroid.common.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

@Dao
public interface PictureDao extends AbstractDao<Picture> {

    @Insert
    @Override
    void save(Picture picture);

    @Delete
    @Override
    void delete(Picture picture);

    @Delete
    void deletePictures(Picture... pictures);

    @Update
    @Override
    void update(Picture type);

    @Update
    void updatePictures(Picture... pictures);

    @Query("SELECT * FROM PICTURE_TBL")
    @Override
    LiveData<List<Picture>> fetchAll();

    @Query("SELECT * FROM PICTURE_TBL WHERE PICTURE_ID = :id")
    @Override
    LiveData<Optional<Picture>> fetchById(Long id);

}
