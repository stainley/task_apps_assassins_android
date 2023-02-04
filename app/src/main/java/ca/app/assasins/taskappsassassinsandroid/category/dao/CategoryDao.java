package ca.app.assasins.taskappsassassinsandroid.category.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;

@Dao
public abstract class CategoryDao implements AbstractDao<Category> {

    @Override
    public void save(Category type) {

    }

    @Override
    public void delete(Category type) {

    }

    @Override
    public void update(Category type) {

    }

    @Query("SELECT * FROM CATEGORY_TBL")
    @Override
    public LiveData<List<Category>> fetchAll() {
        return null;
    }

    @Query("SELECT * FROM CATEGORY_TBL WHERE id = :id")
    @Override
    public LiveData<Category> fetchById(Long id) {
        return null;
    }
}
