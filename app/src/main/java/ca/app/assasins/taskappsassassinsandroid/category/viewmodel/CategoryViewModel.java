package ca.app.assasins.taskappsassassinsandroid.category.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.category.repositories.CategoryRepository;
import ca.app.assasins.taskappsassassinsandroid.category.repositories.CategoryRepositoryImpl;

public class CategoryViewModel extends ViewModel {

    private final CategoryRepository categoryRepository;

    public CategoryViewModel(Application application) {
        this.categoryRepository = new CategoryRepositoryImpl(application);
    }

    public void createCategory(@NonNull Category category) {
        categoryRepository.save(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.fetchAll();
    }

    public LiveData<Category> getCategoryByName(String name) {
        return categoryRepository.fetchByName(name);
    }
}
