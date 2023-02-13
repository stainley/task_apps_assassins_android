package ca.app.assasins.taskappsassassinsandroid.tasks.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TaskListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TaskListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is task list fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}