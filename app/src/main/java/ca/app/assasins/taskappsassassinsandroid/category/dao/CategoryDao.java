package ca.app.assasins.taskappsassassinsandroid.category.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;

@Dao
public interface CategoryDao extends AbstractDao<Category> {

    @Insert
    @Override
    void save(Category type);

    @Delete
    @Override
    void delete(Category type);

    @Update
    @Override
    void update(Category type);

    @Query("SELECT * FROM CATEGORY_TBL")
    @Override
    LiveData<List<Category>> fetchAll();

    @Query("SELECT * FROM CATEGORY_TBL WHERE CATEGORY_ID = :id")
    @Override
    LiveData<Optional<Category>> fetchById(Long id);

    @Query("SELECT * FROM CATEGORY_TBL WHERE name = :name")
    LiveData<Category> fetchByName(String name);
}
