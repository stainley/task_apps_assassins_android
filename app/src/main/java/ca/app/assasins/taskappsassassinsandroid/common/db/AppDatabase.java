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
import ca.app.assasins.taskappsassassinsandroid.common.dao.AudioDao;
import ca.app.assasins.taskappsassassinsandroid.note.dao.ColorDao;
import ca.app.assasins.taskappsassassinsandroid.common.dao.PictureDao;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.note.model.Color;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.dao.NoteDao;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.task.dao.SubTaskDao;
import ca.app.assasins.taskappsassassinsandroid.task.dao.TaskDao;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

@Database(entities = {Category.class,
        Note.class,
        Task.class,
        SubTask.class,
        Picture.class,
        Audio.class,
        Color.class
}, version = 1, exportSchema = false)
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

    public abstract NoteDao noteDao();

    public abstract TaskDao taskDao();

    public abstract PictureDao pictureDao();

    public abstract SubTaskDao subTaskDao();
    public abstract AudioDao audioDao();

    public abstract ColorDao colorDao();

}
