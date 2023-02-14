package ca.app.assasins.taskappsassassinsandroid.note.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import java.util.Date;
import java.util.Objects;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNoteDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityTaskDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.TaskDetailActivityArgs;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class NoteDetailActivity extends AppCompatActivity {

    private ActivityNoteDetailBinding binding;
    private long categoryId;
    private Note note;
    NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences categorySP = getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        noteViewModel = new ViewModelProvider(new ViewModelStore(), new NoteViewModelFactory(getApplication())).get(NoteViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //saveNote(this);
        //return super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "SAVING NOTE", Toast.LENGTH_SHORT).show();
            //Editable taskName = binding.title.getText();
            // Save the task
//            Task task = new Task();
//            task.setTaskId(this.task != null ? this.task.getTaskId() : 0);
//            task.setCreationDate(new Date().getTime());

            String title = Objects.requireNonNull(binding.title.getText()).toString();
            String description = Objects.requireNonNull(binding.description.getText()).toString();
            Date createdDate = new Date();

            Note newNote = new Note(title, description, createdDate, categoryId);

            assert title != null;
            if (!title.toString().isEmpty() && note.getId() == 0) {
                noteViewModel.createNote(note);
            } else {
                // update
                newNote.setCategoryId(categoryId);
                noteViewModel.updateNote(note);
            }

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNote(NoteDetailActivity view) {


//        if (note != null) {
//            newNote.setId(note.getId());
//        }
//
//        noteViewModel.createNote(note);
//
//        finish();
    }
}
