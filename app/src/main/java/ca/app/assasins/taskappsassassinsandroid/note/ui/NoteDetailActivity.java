package ca.app.assasins.taskappsassassinsandroid.note.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Date;
import java.util.Objects;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNoteDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;

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

        binding.addBtn.setOnClickListener(this::addBtnClicked);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "SAVING NOTE", Toast.LENGTH_SHORT).show();

            String title = Objects.requireNonNull(binding.title.getText()).toString();
            String description = Objects.requireNonNull(binding.description.getText()).toString();
            Date createdDate = new Date();

            Note newNote = new Note(title, description, createdDate, categoryId/*, new Coordinate(43.44, -79.45)*/);

            if (note == null && title != null) {
                noteViewModel.createNote(newNote);
            }
            else {
                newNote.setCategoryId(categoryId);
                noteViewModel.updateNote(note);
            }

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addBtnClicked(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.activity_add_image_audio_sheet,
                        (LinearLayout)findViewById(R.id.bottomSheetContainer));
                bottomSheetView.findViewById(R.id.take_photo_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NoteDetailActivity.this, "Take a photo!!!", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }
}
