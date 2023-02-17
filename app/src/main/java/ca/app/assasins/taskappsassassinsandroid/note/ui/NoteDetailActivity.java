package ca.app.assasins.taskappsassassinsandroid.note.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNoteDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NoteAudioRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NotePictureRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;

public class NoteDetailActivity extends AppCompatActivity implements NotePictureRVAdapter.OnPictureNoteCallback {

    private ActivityNoteDetailBinding binding;
    private long categoryId;
    private Note note;
    private NoteViewModel noteViewModel;

    private NotePictureRVAdapter notePictureRVAdapter;
    private NoteAudioRVAdapter noteAudioRVAdapter;

    private ArrayList<String> permissionsList;

    private String pathSave = "";
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private Uri tempImageUri = null;

    final int REQUEST_PERMISSION_CODE = 1000;

    private final String[] permissionsStr = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private final List<Picture> myPictures = new ArrayList<>();
    private final List<Audio> mAudios = new ArrayList<>();

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

    ActivityResultLauncher<String[]> permissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            ArrayList<Boolean> list = new ArrayList<>(result.values());
            permissionsList = new ArrayList<>();
            int permissionsCount = 0;
            for (int i = 0; i < list.size(); i++) {
                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                    permissionsList.add(permissionsStr[i]);
                } else if (!hasPermission(NoteDetailActivity.this, permissionsStr[i])) {
                    permissionsCount++;
                }
            }
            if (permissionsList.size() > 0) {
                //Some permissions are denied and can be asked again.
                askForPermissions(permissionsList);
            } else if (permissionsCount > 0) {
                //Show alert dialog
                showPermissionDialog();
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

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);

        RecyclerView notePictureRV = binding.notePictureRV;
        // adapter picture
        notePictureRVAdapter = new NotePictureRVAdapter(myPictures, this);
        notePictureRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        notePictureRV.setAdapter(notePictureRVAdapter);

        // Audio record
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        RecyclerView noteAudioRV = binding.noteAudioRecycleView;
        noteAudioRVAdapter = new NoteAudioRVAdapter(mAudios, new NoteAudioRVAdapter.OnAudioOperationCallback() {
            @Override
            public void onAudioPlay(SeekBar seekBar, int position) {
                System.out.println("SEEKBAR SENDED: " + (int) seekBar.getTag() + " - Position: " + position);
                int tempPosition = (int) seekBar.getTag();
                System.out.println("Play Audio");

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(mAudios.get(position).getPath());
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(mp -> {
                    // if the current position is equal to the duration, we are in the final. reset scrubber
                    if (mp.getCurrentPosition() == mp.getDuration()) {
                        seekBar.setProgress(0);
                    }
                });
                Handler handler = new Handler();

                Runnable progress_bar = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            synchronized (seekBar) {
                                System.out.println("tempPosition: " + tempPosition);
                                System.out.println("SEEKBAR POSITION: " + (int) seekBar.getTag() + " - Position: " + position);
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }
                };

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    System.out.println("AUDIO DURATION: " + mediaPlayer.getDuration());
                    handler.removeCallbacks(progress_bar);
                    handler.post(progress_bar);
                }
            }

            @Override
            public void onAudioStop(View view, int position) {

            }

            @Override
            public void onDeleteAudio(int position) {
                noteViewModel.deleteAudio(mAudios.get(position));
            }
        });
        noteAudioRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        noteAudioRV.setAdapter(noteAudioRVAdapter);


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
            String updatedDate = dateFormat.format(note.getUpdatedDate());
            binding.editDateInfo.setText("Edited: " + updatedDate);

            noteViewModel.fetchPicturesByNoteId(note.getNoteId()).observe(this, taskImages -> {
                myPictures.clear();
                taskImages.forEach(image -> myPictures.addAll(image.pictures));
                notePictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            });

            noteViewModel.fetchAudiosByNote(note.getNoteId()).observe(this, noteAudios -> {
                mAudios.clear();

                noteAudios.forEach(resultAudios -> mAudios.addAll(resultAudios.getAudios()));
                noteAudioRVAdapter.notifyItemRangeChanged(0, mAudios.size());

            });

        } else {
            binding.moreActionBtn.setVisibility(View.INVISIBLE);
            binding.editDateInfo.setVisibility(View.INVISIBLE);
        }

        binding.addBtn.setOnClickListener(this::addBtnClicked);
        binding.moreActionBtn.setOnClickListener(this::moreActionBtnClicked);
    }

    // SAVE ON BACK BUTTON
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

            if (!title.isEmpty() && newNote.getNoteId() == 0) {
                //noteViewModel.saveNoteWithPictures(newNote, myPictures);
                noteViewModel.saveNoteWithPicturesAudios(newNote, myPictures, mAudios);
            } else if (!title.isEmpty() && newNote.getNoteId() > 0) {
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
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_add_image_audio_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.take_photo_btn).setOnClickListener(view12 -> {
            Toast.makeText(NoteDetailActivity.this, "Take a photo!!!", Toast.LENGTH_SHORT).show();
            takePhoto();

            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.upload_image_btn).setOnClickListener(view1 -> {
            addPhotoFromLibrary();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.record_audio_btn).setOnClickListener(v -> {

            if (hasPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)) {
                LayoutInflater inflater = getLayoutInflater();

                View customDialog = inflater.inflate(R.layout.fragment_audio_dialog, null);
                Button startRecordAudio = customDialog.findViewById(R.id.recordingAudioBtn);

                new MaterialAlertDialogBuilder(this)
                        .setView(customDialog)
                        .setTitle("Record")
                        .setMessage("Tap the mic to start recording.")
                        .setPositiveButton("Stop", (dialog, which) -> {
                            System.out.println("STOP");
                            stopRecordAudio();
                        }).setOnCancelListener(dialog -> {

                        }).show();

                startRecordAudio.setOnClickListener(v1 -> {
                    Toast.makeText(getApplicationContext(), "Start RECORDING", Toast.LENGTH_SHORT).show();
                    recordAudio();
                    startRecordAudio.setBackground(getDrawable(R.drawable.ic_stop_record));

                });
            } else {
                requestPermission();
            }

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            tempImageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            selectCameraLauncher.launch(tempImageUri);
        }
    }

    public void addPhotoFromLibrary() {
        System.out.println("ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) " + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
    }

    private void moreActionBtnClicked(View view) {
        final BottomSheetDialog moreActionBottomSheetDialog = new BottomSheetDialog(NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_more_action_sheet, (LinearLayout) findViewById(R.id.moreActionBottomSheetContainer));
        bottomSheetView.findViewById(R.id.delete_note).setOnClickListener(view1 -> {
            noteViewModel.deleteNote(note);
            Toast.makeText(NoteDetailActivity.this, "Delete!!!", Toast.LENGTH_SHORT).show();
            moreActionBottomSheetDialog.dismiss();
            finish();
        });
        moreActionBottomSheetDialog.setContentView(bottomSheetView);
        moreActionBottomSheetDialog.show();
    }

    private void stopRecordAudio() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
    }

    private void recordAudio() {


        pathSave = getExternalCacheDir().getAbsolutePath() + "/" +
                createRandomAudioFileName(5) + "audioRecording.3gp";

        Log.d("path", "onClick: " + pathSave);

        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException ise) {
            // make something ...
            ise.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        Audio audio = new Audio();
        audio.setCreationDate(new Date().getTime());
        audio.setPath(pathSave);
        mAudios.add(audio);
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeletePicture(View view, int position) {
        //TODO: delete photo implementation
        noteViewModel.deletePicture(myPictures.get(position));
        myPictures.remove(myPictures.get(position));
        noteAudioRVAdapter.notifyItemRemoved(position);
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(pathSave);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

/*    private boolean checkPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }*/

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        }
    }

    private void showPermissionDialog() {

    }

    public String createRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        Random random = new Random();
        int i = 0;
        while (i < string) {
            String randomAudioFileName = "ABCDEFGHIJKLMNOP";
            stringBuilder.append(randomAudioFileName.charAt(random.nextInt(randomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

}
