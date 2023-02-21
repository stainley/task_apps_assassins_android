package ca.app.assasins.taskappsassassinsandroid.note.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Coordinate;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNoteDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Color;
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
    private String pathSave = "";
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Uri tempImageUri = null;
    final int REQUEST_PERMISSION_CODE = 1000;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private final List<Picture> myPictures = new ArrayList<>();
    private final List<Audio> mAudios = new ArrayList<>();

    private TextToSpeech textToSpeech;
    private TextView textReadingNote;
    private Color selectedNoteColor;
    private String selectedColorName = "colorDefaultNoteColor";
    private final StringBuilder descriptionInfo = new StringBuilder();


    private final ActivityResultLauncher<Intent> textToSpeakLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                List<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                results.forEach(spoken -> descriptionInfo.append(spoken).append(".").append(" "));

                binding.description.setText(descriptionInfo.toString());
            }
        }
    });

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
        setTitle("Note");

        RecyclerView notePictureRV = binding.notePictureRV;
        // adapter picture
        notePictureRVAdapter = new NotePictureRVAdapter(myPictures, this);
        notePictureRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        notePictureRV.setAdapter(notePictureRVAdapter);


        // location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = this::updateLocationInfo;

        // if the permission is granted, we request the update.
        // if the permission is not granted, we request for the access.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lasKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lasKnownLocation != null) updateLocationInfo(lasKnownLocation);
        }

        // Audio record
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        RecyclerView noteAudioRV = binding.noteAudioRecycleView;
        noteAudioRVAdapter = new NoteAudioRVAdapter(mAudios, new NoteAudioRVAdapter.OnAudioOperationCallback() {
            @Override
            public void onAudioPlay(SeekBar seekBar, int position) {

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
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }
                };

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
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
                mAudios.remove(position);
                noteAudioRVAdapter.notifyItemRemoved(position);
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

            noteViewModel.fetchColorsByNoteId(note.getNoteId()).observe(this, noteColors -> {
                List<Color> colors = new ArrayList<>();

                noteColors.forEach(resultColors -> colors.addAll(resultColors.getColors()));
                selectedNoteColor = colors.get(0);

                if (!selectedNoteColor.getColor().equals("colorDefaultNoteColor")) {
                    binding.noteDetailView.setBackgroundColor(getSourceColor(selectedNoteColor.getColor()));
                }
            });

        } else {
            binding.moreActionBtn.setVisibility(View.INVISIBLE);
            binding.editDateInfo.setVisibility(View.INVISIBLE);
        }
        descriptionInfo.append(Objects.requireNonNull(binding.description.getText())).append(" ");

        binding.addBtn.setOnClickListener(this::addBtnClicked);
        binding.moreActionBtn.setOnClickListener(this::moreActionBtnClicked);
        binding.colorPickerBtn.setOnClickListener(this::colorPickerBtnClicked);
    }

    private void updateLocationInfo(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    // SAVE ON BACK BUTTON
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            String title = Objects.requireNonNull(binding.title.getText()).toString();
            String description = Objects.requireNonNull(binding.description.getText()).toString();
            Color color = new Color();
            color.setColor(this.selectedNoteColor != null ? this.selectedNoteColor.getColor() : selectedColorName);
            color.setId(this.selectedNoteColor != null ? this.selectedNoteColor.getId() : 0);

            if (note != null) {
                color.setParentNoteId(note.getNoteId());
            }

            Note newNote = new Note();
            newNote.setTitle(title);
            newNote.setDescription(description);
            newNote.setCreatedDate(this.note != null ? this.note.getCreatedDate() : new Date());
            newNote.setCoordinate(note != null ? note.getCoordinate() : new Coordinate(latitude, longitude));
            newNote.setUpdatedDate(new Date());
            newNote.setCategoryId(categoryId);
            newNote.setNoteId(this.note != null ? this.note.getNoteId() : 0);

            if (!title.isEmpty() && newNote.getNoteId() == 0) {
                noteViewModel.saveNoteWithPicturesAudios(newNote, myPictures, mAudios, color);
            } else if (!title.isEmpty() && newNote.getNoteId() > 0) {
                noteViewModel.updateNoteWithPictures(newNote, myPictures, mAudios, color);
            }

            if (color.getId() != 0) {
                noteViewModel.updateColor(color);
            }
            navigateUpTo(new Intent(this, NoteListFragment.class));
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addBtnClicked(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_note_functionality_sheet, findViewById(R.id.bottomSheetContainer));
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

            if (hasPermission(getApplicationContext())) {
                LayoutInflater inflater = getLayoutInflater();

                View customDialog = inflater.inflate(R.layout.fragment_audio_dialog, null);
                Button startRecordAudio = customDialog.findViewById(R.id.recordingAudioBtn);

                AlertDialog record = new MaterialAlertDialogBuilder(this).setView(customDialog).setTitle("Record").setMessage("Tap long press Mic to start recording.").setCancelable(false).setNegativeButton("Exit", (dialog, which) -> {
                    if (mediaRecorder != null) {
                        stopRecordAudio();
                    }
                    dialog.dismiss();
                }).show();

                Boolean[] isRecording = new Boolean[1];
                isRecording[0] = false;
                startRecordAudio.setOnLongClickListener(v1 -> {

                    if (isRecording[0]) {
                        record.setMessage("Tap long press Mic to start recording.");
                        stopRecordAudio();
                        startRecordAudio.setBackground(getDrawable(R.drawable.ic_mic_24));
                        isRecording[0] = false;
                    } else {
                        record.setMessage("Long press Stop button.");
                        recordAudio();
                        startRecordAudio.setBackground(getDrawable(R.drawable.ic_stop_record));
                        isRecording[0] = true;

                    }
                    return false;
                });
            } else {
                requestPermission();
            }

            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.dictation_audio_btn).setOnClickListener(this::dictateNoteDescription);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void dictateNoteDescription(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        textToSpeakLauncher.launch(intent);
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

    private void colorPickerBtnClicked(View view) {
        final BottomSheetDialog colorPickerBottomSheetDialog = new BottomSheetDialog(NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_color_picker, findViewById(R.id.colorPickerBottomSheetLayout));

        colorPickerBottomSheetDialog.setContentView(bottomSheetView);
        colorPickerBottomSheetDialog.show();

        final ImageView checkColor1 = bottomSheetView.findViewById(R.id.check_color1);
        final ImageView checkColor2 = bottomSheetView.findViewById(R.id.check_color2);
        final ImageView checkColor3 = bottomSheetView.findViewById(R.id.check_color3);
        final ImageView checkColor4 = bottomSheetView.findViewById(R.id.check_color4);
        final ImageView checkColor5 = bottomSheetView.findViewById(R.id.check_color5);
        final ImageView checkColor6 = bottomSheetView.findViewById(R.id.check_color6);
        final ImageView checkColor7 = bottomSheetView.findViewById(R.id.check_color7);
        final ImageView checkColor8 = bottomSheetView.findViewById(R.id.check_color8);

        bottomSheetView.findViewById(R.id.view_color1).setOnClickListener(v -> {
            checkColor1.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorDefaultNoteColor, getTheme()));
            selectedColorName = "colorDefaultNoteColor";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorDefaultNoteColor");
        });

        bottomSheetView.findViewById(R.id.view_color2).setOnClickListener(v -> {
            checkColor2.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote2, getTheme()));
            selectedColorName = "colorNote2";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote2");
        });

        bottomSheetView.findViewById(R.id.view_color3).setOnClickListener(v -> {
            checkColor3.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote3, getTheme()));
            selectedColorName = "colorNote3";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote3");
        });

        bottomSheetView.findViewById(R.id.view_color4).setOnClickListener(v -> {
            checkColor4.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote4, getTheme()));
            selectedColorName = "colorNote4";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote4");
        });

        bottomSheetView.findViewById(R.id.view_color5).setOnClickListener(v -> {
            checkColor5.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote5, getTheme()));
            selectedColorName = "colorNote5";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote5");
        });

        bottomSheetView.findViewById(R.id.view_color6).setOnClickListener(v -> {
            checkColor6.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote6, getTheme()));
            selectedColorName = "colorNote6";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote6");
        });

        bottomSheetView.findViewById(R.id.view_color7).setOnClickListener(v -> {
            checkColor7.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote7, getTheme()));
            selectedColorName = "colorNote7";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote7");
        });

        bottomSheetView.findViewById(R.id.view_color8).setOnClickListener(v -> {
            checkColor8.setImageResource(R.drawable.ic_check);
            binding.noteDetailView.setBackgroundColor(getResources().getColor(R.color.colorNote8, getTheme()));
            selectedColorName = "colorNote8";
            if (selectedNoteColor != null) selectedNoteColor.setColor("colorNote8");
        });
    }

    private void moreActionBtnClicked(View view) {
        final BottomSheetDialog moreActionBottomSheetDialog = new BottomSheetDialog(NoteDetailActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_more_action_sheet, findViewById(R.id.moreActionBottomSheetContainer));
        bottomSheetView.findViewById(R.id.delete_note).setOnClickListener(view1 -> {
            noteViewModel.deleteNote(note);
            moreActionBottomSheetDialog.dismiss();
            finish();
        });

        bottomSheetView.findViewById(R.id.show_map).setOnClickListener(v -> {
            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapIntent.putExtra("note", note);
            moreActionBottomSheetDialog.dismiss();
            startActivity(mapIntent);
        });

        bottomSheetView.findViewById(R.id.read_note).setOnClickListener(v -> {
            moreActionBottomSheetDialog.dismiss();
            LayoutInflater inflater = getLayoutInflater();
            View readNoteView = inflater.inflate(R.layout.layout_read_note, null);

            AlertDialog progress = new MaterialAlertDialogBuilder(NoteDetailActivity.this).setView(readNoteView).setCancelable(false).show();

            textToSpeech = new TextToSpeech(NoteDetailActivity.this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(NoteDetailActivity.this, "Sorry, language not supported!", Toast.LENGTH_SHORT).show();
                    } else {

                        textReadingNote = readNoteView.findViewById(R.id.text_reading_text);
                        TextView stopReadButton = readNoteView.findViewById(R.id.stop_reading);

                        String title = binding.title.getText().toString().trim();
                        String description = Objects.requireNonNull(binding.description.getText()).toString().trim();

                        readNoteView.findViewById(R.id.start_reading).setOnClickListener(view1 -> {
                            textReadingNote.setText("Reading Note...");
                            textToSpeech.speak(title, TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.speak(description, TextToSpeech.QUEUE_ADD, null);

                            stopReadButton.setText("STOP");
                        });

                        readNoteView.findViewById(R.id.stop_reading).setOnClickListener(view1 -> {
                            textReadingNote.setText("Do you want the NOTES APP to read the note for you?");
                            stopReadButton.setText("CANCEL");
                            if (textToSpeech != null) {
                                textToSpeech.stop();
                            }
                            progress.dismiss();
                        });
                    }
                }
            });
        });
        moreActionBottomSheetDialog.setContentView(bottomSheetView);
        moreActionBottomSheetDialog.show();
    }

    private void stopRecordAudio() {
        noteAudioRVAdapter.notifyItemRangeChanged(0, mAudios.size());
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(NoteDetailActivity.this, "Audio stopped", Toast.LENGTH_SHORT).show();

    }

    private void recordAudio() {

        pathSave = getExternalCacheDir().getAbsolutePath() + "/" + createRandomAudioFileName(5) + "audioRecording.3gp";

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
    }

    @Override
    public void onDeletePicture(View view, int position) {
        noteViewModel.deletePicture(myPictures.get(position));
        myPictures.remove(myPictures.get(position));
        notePictureRVAdapter.notifyItemRemoved(position);
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(pathSave);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
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

    public int getSourceColor(String colorName) {
        switch (colorName) {
            default:
            case "colorDefaultNoteColor":
                return binding.noteDetailView.getResources().getColor(R.color.colorDefaultNoteColor, getTheme());
            case "colorNote2":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote2, getTheme());
            case "colorNote3":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote3, getTheme());
            case "colorNote4":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote4, getTheme());
            case "colorNote5":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote5, getTheme());
            case "colorNote6":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote6, getTheme());
            case "colorNote7":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote7, getTheme());
            case "colorNote8":
                return binding.noteDetailView.getResources().getColor(R.color.colorNote8, getTheme());
        }
    }

    private boolean hasPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            System.out.println();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }
}
