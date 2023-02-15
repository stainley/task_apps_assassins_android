package ca.app.assasins.taskappsassassinsandroid.common.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

public interface AbstractDao<T extends Serializable> {

    void save(T type);

    void delete(T type);

    void update(T type);

    LiveData<List<T>> fetchAll();

    LiveData<Optional<T>> fetchById(Long id);

    void saveWithPicture(T type, List<Picture> pictures);

    void saveWithAudios(T type, List<Audio> audios);

}
