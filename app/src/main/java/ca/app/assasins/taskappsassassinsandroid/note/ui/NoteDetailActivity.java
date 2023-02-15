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
import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNoteDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.TaskDetailActivityArgs;

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

        NoteDetailActivityArgs noteDetailActivityArgs = NoteDetailActivityArgs.fromBundle(getIntent().getExtras());
        note = noteDetailActivityArgs.getOldNote();

        if (note != null) {
            binding.moreActionBtn.setVisibility(View.VISIBLE);
            binding.editDateInfo.setVisibility(View.VISIBLE);

            binding.title.setText(note.getTitle());
            binding.description.setText(note.getDescription());

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
            String updatedDate = dateFormat.format(note.getUpdatedDate()).toString();
            binding.editDateInfo.setText("Edited: " + updatedDate);
        }
        else {
            binding.moreActionBtn.setVisibility(View.INVISIBLE);
            binding.editDateInfo.setVisibility(View.INVISIBLE);
        }

        binding.addBtn.setOnClickListener(this::addBtnClicked);
        binding.moreActionBtn.setOnClickListener(this::moreActionBtnClicked);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            String title = Objects.requireNonNull(binding.title.getText()).toString();
            String description = Objects.requireNonNull(binding.description.getText()).toString();

            Note newNote = new Note();
            newNote.setTitle(title);
            newNote.setDescription(description);
            newNote.setCreatedDate(this.note != null ? this.note.getCreatedDate() : new Date());
            newNote.setUpdatedDate(new Date());
            newNote.setCategoryId(categoryId);
            newNote.setNoteId(this.note != null ? this.note.getNoteId() : 0);

            assert title != null;
            if (!title.toString().isEmpty() && newNote.getNoteId() == 0) {
                noteViewModel.createNote(newNote);
                Toast.makeText(this, "SAVING " + title, Toast.LENGTH_SHORT).show();
            } else {
                noteViewModel.updateNote(newNote);
                Toast.makeText(this, "UPDATING " + newNote.getTitle(), Toast.LENGTH_SHORT).show();
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

    private void moreActionBtnClicked(View view) {
        final BottomSheetDialog moreActionBottomSheetDialog = new BottomSheetDialog(
                NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.activity_more_action_sheet,
                        (LinearLayout)findViewById(R.id.moreActionBottomSheetContainer));
        bottomSheetView.findViewById(R.id.delete_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteViewModel.deleteNote(note);
                Toast.makeText(NoteDetailActivity.this, "Delete!!!", Toast.LENGTH_SHORT).show();
                moreActionBottomSheetDialog.dismiss();
                finish();
            }
        });
        moreActionBottomSheetDialog.setContentView(bottomSheetView);
        moreActionBottomSheetDialog.show();
    }
}
