package ca.app.assasins.taskappsassassinsandroid.category.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.category.dao.CategoryDao;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;

public class CategoryRepositoryImpl implements CategoryRepository {

    private final AppDatabase db;
    private CategoryDao categoryDao;

    public CategoryRepositoryImpl(Application application) {
        db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }

    @Override
    public void save(Category category) {
        categoryDao.save(category);
    }

    @Override
    public void update(Category category) {
        categoryDao.update(category);
    }

    @Override
    public void delete(Category category) {
        categoryDao.delete(category);
    }

    @Override
    public LiveData<List<Category>> fetchAll() {
        return categoryDao.fetchAll();
    }

    @Override
    public LiveData<Optional<Category>> fetchById(Long id) {
        return categoryDao.fetchById(id);
    }
}
