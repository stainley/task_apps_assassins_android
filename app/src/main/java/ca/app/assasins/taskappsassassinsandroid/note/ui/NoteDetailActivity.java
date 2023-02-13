package ca.app.assasins.taskappsassassinsandroid.note.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.Objects;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNoteDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;

public class NoteDetailActivity extends AppCompatActivity {

    private ActivityNoteDetailBinding binding;
    private Category category;
    private Note note;
    NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(NoteViewModel.class);

        category = (Category) getIntent().getSerializableExtra("category");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (note != null) {
            binding.title.setText(note.getTitle());
            binding.description.setText(note.getDescription());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        saveNote(this);
        return super.onOptionsItemSelected(item);
    }

    private void saveNote(NoteDetailActivity view) {
        String title = Objects.requireNonNull(binding.title.getText()).toString();
        String description = Objects.requireNonNull(binding.description.getText()).toString();
        Date createdDate = new Date();
        Long categoryId = category.getId();

        Note newNote = new Note(title, description, createdDate, categoryId);

        if (note != null) {
            newNote.setId(note.getId());
        }

        noteViewModel.createNote(note);

        finish();
    }
}
