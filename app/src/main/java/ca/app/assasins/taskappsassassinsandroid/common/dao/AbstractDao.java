package ca.app.assasins.taskappsassassinsandroid.common.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import java.util.List;

public interface AbstractDao<T> {
    @Insert
    void save(T type);

    @Delete
    void delete(T type);

    @Update
    void update(T type);

    LiveData<List<T>> fetchAll();

    LiveData<T> fetchById(Long id);
}
