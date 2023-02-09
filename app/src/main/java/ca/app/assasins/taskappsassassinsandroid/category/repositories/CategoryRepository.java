package ca.app.assasins.taskappsassassinsandroid.category.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.category.dao.CategoryDao;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.db.AppDatabase;
import ca.app.assasins.taskappsassassinsandroid.common.repository.Repository;

public interface CategoryRepository extends Repository<Category> {

}
