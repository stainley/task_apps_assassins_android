package ca.app.assasins.taskappsassassinsandroid.task.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;

import java.util.List;
import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.dao.AbstractDao;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

@Dao
public interface TaskDao extends AbstractDao<Task> {
    @Override
    void save(Task type);

    @Override
    void delete(Task type);

    @Override
    void update(Task type);

    @Override
    LiveData<List<Task>> fetchAll();
    @Override
    LiveData<Optional<Task>> fetchById(Long id);
}
