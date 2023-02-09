package ca.app.assasins.taskappsassassinsandroid.common.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.app.assasins.taskappsassassinsandroid.category.dao.CategoryDao;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(@NonNull final Application application) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(application, AppDatabase.class, "task-note-db").build();
            }
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();

}
