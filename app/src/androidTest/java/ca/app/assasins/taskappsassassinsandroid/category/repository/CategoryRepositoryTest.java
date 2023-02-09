package ca.app.assasins.taskappsassassinsandroid.category.repository;

import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.category.repositories.CategoryRepositoryImpl;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.common.repository.Repository;

@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryTest {

    private AppDatabase appDatabase;
    private Repository<Category> repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appDatabase = Mockito.mock(AppDatabase.class);

        repository = new CategoryRepositoryImpl(ApplicationProvider.getApplicationContext());

    }


    @Test
    public void test() {
        when(appDatabase.categoryDao().fetchAll()).thenReturn(Mockito.any());

        LiveData<List<Category>> listLiveData = repository.fetchAll();

        Assert.assertNotNull(listLiveData);
    }

}
