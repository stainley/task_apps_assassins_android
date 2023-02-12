package ca.app.assasins.taskappsassassinsandroid.common.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

public interface Repository<T> {

    void save(T type);

    void update(T type);

    void delete(T type);

    LiveData<List<T>> fetchAll();

    LiveData<T> fetchByName(String name);

    LiveData<T> fetchByTitle(String title);

    LiveData<Optional<T>> fetchById(Long id);
}
