package ca.app.assasins.taskappsassassinsandroid.note.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NotePictureRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.TaskDetailActivityArgs;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskPictureRVAdapter;

public class NoteDetailActivity extends AppCompatActivity implements NotePictureRVAdapter.OnPictureNoteCallback {

    private ActivityNoteDetailBinding binding;
    private long categoryId;
    private Note note;
    NoteViewModel noteViewModel;

    private NotePictureRVAdapter notePictureRVAdapter;
    private Uri tempImageUri = null;

    private ImageView imageView;
    private final List<Picture> myPictures = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 3322;

    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                if (result != null) {
                    tempImageUri = result;

                    Picture picture = new Picture();
                    picture.setCreationDate(new Date().getTime());
                    picture.setPath("content://media/" + tempImageUri.getPath());
                    myPictures.add(picture);
                    notePictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });

    // Take photo from the camera
    private final ActivityResultLauncher<Uri> selectCameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        if (result) {

            try {
                Picture picture = new Picture();
                picture.setCreationDate(new Date().getTime());
                picture.setPath("content://media/" + tempImageUri.getPath());
                myPictures.add(picture);
                notePictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        RecyclerView notePictureRV = binding.notePictureRV;
        // adapter picture
        notePictureRVAdapter = new NotePictureRVAdapter(myPictures, this);
        notePictureRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        notePictureRV.setAdapter(notePictureRVAdapter);

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

            noteViewModel.fetchPicturesByNoteId(note.getNoteId()).observe(this, taskImages -> {
                myPictures.clear();
                taskImages.forEach(image -> myPictures.addAll(image.pictures));
                notePictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            });
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
                noteViewModel.saveNoteWithPictures(newNote, myPictures);
            } else if (!title.toString().isEmpty() && newNote.getNoteId() > 0) {
                if (!myPictures.isEmpty()) {
                    noteViewModel.updateNoteWithPictures(newNote, myPictures);
                } else {
                    noteViewModel.updateNote(newNote);
                }
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

        bottomSheetView.findViewById(R.id.upload_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhotoFromLibrary();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void addPhotoFromLibrary() {
        System.out.println("ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) " + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
        System.out.println("TESTTTTTTTT");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
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

    @Override
    public void onDeletePicture(View view, int position) {
        //taskListViewModel.deletePicture(myPictures.get(position));
        //myPictures.remove(position);
        //taskPictureRVAdapter.notifyItemRemoved(position);
    }
}
