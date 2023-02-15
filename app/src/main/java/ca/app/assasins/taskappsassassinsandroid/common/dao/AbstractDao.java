package ca.app.assasins.taskappsassassinsandroid.common.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public interface AbstractDao<T extends Serializable> {

    void save(T type);

    void delete(T type);

    void update(T type);

    LiveData<List<T>> fetchAll();

    LiveData<Optional<T>> fetchById(Long id);

}
