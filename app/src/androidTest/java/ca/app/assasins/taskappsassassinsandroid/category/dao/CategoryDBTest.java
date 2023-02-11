package ca.app.assasins.taskappsassassinsandroid.category.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.LiveDataTestUtil;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class CategoryDBTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private CategoryDao categoryDao;
    private AppDatabase db;

    @Before
    public void createDB() {

        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        categoryDao = db.categoryDao();
        System.out.println(categoryDao);
    }

    @After
    public void closeDB() throws IOException {
        db.close();
    }

    @Test
    public void saveCategoryTest() throws InterruptedException {
        Category category = new Category();
        category.setName("School");
        categoryDao.save(category);

        List<Category> result = LiveDataTestUtil.getValue(categoryDao.fetchAll());
        TestCase.assertEquals(category.getName(), result.get(0).getName());
    }

    @Test
    public void updateCategoryTest() throws InterruptedException {
        Category category = new Category();
        category.setName("School");
        categoryDao.save(category);

        Optional<Category> result = LiveDataTestUtil.getValue(categoryDao.fetchById(1L));
        List<Category> numResults = LiveDataTestUtil.getValue(categoryDao.fetchAll());

        TestCase.assertNotNull(result);
        TestCase.assertEquals(1, numResults.size());
        result.get().setName("Grocery");
        categoryDao.update(result.get());

        List<Category> numResultsAfterUpdate = LiveDataTestUtil.getValue(categoryDao.fetchAll());
        TestCase.assertEquals(1, numResultsAfterUpdate.size());

        Optional<Category> resultAfterUpdate = LiveDataTestUtil.getValue(categoryDao.fetchById(1L));
        TestCase.assertEquals("Grocery", resultAfterUpdate.get().getName());

    }

    @Test
    public void deleteCategoryTest() throws InterruptedException {
        Category category = new Category();
        category.setName("School");
        categoryDao.save(category);

        List<Category> numResults = LiveDataTestUtil.getValue(categoryDao.fetchAll());
        TestCase.assertEquals(1, numResults.size());

        Optional<Category> result = LiveDataTestUtil.getValue(categoryDao.fetchById(1L));
        categoryDao.delete(result.get());

        List<Category> numResultsAfterDelete = LiveDataTestUtil.getValue(categoryDao.fetchAll());
        TestCase.assertEquals(0, numResultsAfterDelete.size());

    }

}
