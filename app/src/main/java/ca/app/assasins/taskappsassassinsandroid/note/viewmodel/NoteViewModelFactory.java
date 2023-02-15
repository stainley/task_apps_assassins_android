package ca.app.assasins.taskappsassassinsandroid.note.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NoteViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public NoteViewModelFactory(Application application) {
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NoteViewModel(application);
    }
}
