package ca.app.assasins.taskappsassassinsandroid.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

}
