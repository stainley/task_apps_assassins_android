package ca.app.assasins.taskappsassassinsandroid.category.repositories;

import android.app.Application;

import ca.app.assasins.taskappsassassinsandroid.category.dao.CategoryDao;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;

public class CategoryRepository {
    private final AppDatabase db;
    private CategoryDao categoryDao;

    public CategoryRepository(Application application) {
        db = AppDatabase.getInstance(application);

        categoryDao = db.categoryDao();
    }
}
