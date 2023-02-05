package ca.app.assasins.taskappsassassinsandroid.common.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ca.app.assasins.taskappsassassinsandroid.category.dao.CategoryDao;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    /*public AppDatabase(Application application) {
        AppDatabase db = Room.databaseBuilder(application, AppDatabase.class, "task-note-db")
                .allowMainThreadQueries()
                .build();
    }*/

    public static AppDatabase getInstance(@NonNull final Application application) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(application, AppDatabase.class, "task-note-db")
                    .build();
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();

}
