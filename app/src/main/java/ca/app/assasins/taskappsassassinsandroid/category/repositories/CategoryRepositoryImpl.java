package ca.app.assasins.taskappsassassinsandroid.category.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.category.dao.CategoryDao;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;

public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryDao categoryDao;

    public CategoryRepositoryImpl(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }

    @Override
    public void save(Category category) {
        AppDatabase.databaseWriterExecutor.execute(() -> categoryDao.save(category));
    }

    @Override
    public void update(Category category) {
        AppDatabase.databaseWriterExecutor.execute(() -> categoryDao.update(category));
    }

    @Override
    public void delete(Category category) {
        AppDatabase.databaseWriterExecutor.execute(() -> categoryDao.delete(category));
    }

    @Override
    public LiveData<List<Category>> fetchAll() {
        return categoryDao.fetchAll();
    }

    @Override
    public LiveData<Category> fetchByName(@NonNull String name) {

        return categoryDao.fetchByName(name);
    }

    @Override
    public LiveData<Category> fetchByTitle(String title) {
        return null;
    }

    @Override
    public LiveData<Optional<Category>> fetchById(Long id) {
        return categoryDao.fetchById(id);
    }


}
