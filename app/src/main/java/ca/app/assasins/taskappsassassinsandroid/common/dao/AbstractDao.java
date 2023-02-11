package ca.app.assasins.taskappsassassinsandroid.common.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

public interface AbstractDao<T> {

    void save(T type);

    void delete(T type);

    void update(T type);

    LiveData<List<T>> fetchAll();

    LiveData<Optional<T>> fetchById(Long id);
}
