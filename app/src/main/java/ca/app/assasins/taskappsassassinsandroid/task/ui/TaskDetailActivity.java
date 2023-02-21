package ca.app.assasins.taskappsassassinsandroid.task.ui;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityTaskDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.SubTaskViewAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskAudioRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskPictureRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskDetailActivity extends AppCompatActivity implements TaskPictureRVAdapter.OnPictureTaskCallback, SubTaskViewAdapter.OnSubTaskCallback {

    final int REQUEST_PERMISSION_CODE = 1000;

    private ActivityTaskDetailBinding binding;

    private TaskListViewModel taskListViewModel;

    private long categoryId;
    private Task task;
    private SubTaskViewAdapter subTaskViewAdapter;
    private TaskPictureRVAdapter taskPictureRVAdapter;
    private Uri tempImageUri = null;

    private Calendar calendar;

    private TaskAudioRVAdapter taskAudioRVAdapter;

    private String pathSave = "";
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private final List<Audio> mAudios = new ArrayList<>();

    private final List<Picture> myPictures = new ArrayList<>();
    private final List<SubTask> subTasks = new ArrayList<>();
    private final List<SubTask> additionalSubTasks = new ArrayList<>();

    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                if (result != null) {
                    tempImageUri = result;

                    Picture picture = new Picture();
                    picture.setCreationDate(new Date().getTime());
                    picture.setPath("content://media/" + tempImageUri.getPath());
                    myPictures.add(picture);
                    taskPictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
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
                taskPictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        binding.dueDateTask.setOnClickListener(this::dueDateTask);
        binding.addBtn.setOnClickListener(this::addBtnClicked);
        binding.editDateInfo.setVisibility(View.INVISIBLE);
        binding.moreActionBtn.setVisibility(View.INVISIBLE);

        SharedPreferences categorySP = getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        taskListViewModel = new ViewModelProvider(new ViewModelStore(), new TaskListViewModelFactory(getApplication())).get(TaskListViewModel.class);

        RecyclerView taskPictureRV = binding.taskPictureRV;

        // adapter picture
        taskPictureRVAdapter = new TaskPictureRVAdapter(myPictures, this);
        taskPictureRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskPictureRV.setAdapter(taskPictureRVAdapter);

        // Audio record
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        RecyclerView taskAudioRV = binding.taskAudioRecycleView;
        taskAudioRVAdapter = new TaskAudioRVAdapter(mAudios, new TaskAudioRVAdapter.OnAudioOperationCallback() {
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
                taskListViewModel.deleteAudio(mAudios.get(position));
                mAudios.remove(position);
                taskAudioRVAdapter.notifyItemRemoved(position);
            }
        });
        taskAudioRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        taskAudioRV.setAdapter(taskAudioRVAdapter);

        subTaskViewAdapter = new SubTaskViewAdapter(subTasks, this);

        RecyclerView subTaskRV = binding.subTaskRV;
        subTaskRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        subTaskRV.setNestedScrollingEnabled(false);
        subTaskRV.setAdapter(subTaskViewAdapter);


        TaskDetailActivityArgs taskDetailActivityArgs = TaskDetailActivityArgs.fromBundle(getIntent().getExtras());
        task = taskDetailActivityArgs.getOldTask();
        if (task != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");

            String completionDate = dateFormat.format(task.getCompletionDate());
            binding.editDateInfo.setVisibility(View.INVISIBLE);

            binding.taskNameText.setText(task.getTaskName());
            binding.taskCompletionCkb.setChecked(task.isCompleted());
            binding.taskCompletionCkb.setEnabled(!task.isCompleted());
            binding.dueDateTask.setHint(task.getCompletionDate() != 0 ? completionDate : "No Due Date");

            binding.taskCompletionCkb.setOnClickListener(v -> {
                task.setCompleted(((CheckBox) v).isChecked());
                taskListViewModel.updateTaskAll(task, myPictures, subTasks, mAudios);
            });

            taskListViewModel.fetchPicturesByTaskId(task.getTaskId()).observe(this, taskImages -> {
                myPictures.clear();
                taskImages.forEach(image -> myPictures.addAll(image.pictures));
                taskPictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            });

            taskListViewModel.fetchSubTaskByTaskId(task.getTaskId()).observe(this, taskWithSubTasks -> {
                subTasks.clear();
                taskWithSubTasks.forEach(sbTask -> subTasks.addAll(sbTask.getSubTasks()));
                subTaskViewAdapter.notifyItemRangeChanged(0, subTasks.size());
            });

            taskListViewModel.fetchAudiosByTask(task.getTaskId()).observe(this, taskAudios -> {
                mAudios.clear();

                taskAudios.forEach(resultAudios -> mAudios.addAll(resultAudios.getAudios()));
                taskAudioRVAdapter.notifyItemRangeChanged(0, mAudios.size());

            });

            if (task.isCompleted()) {
                binding.moreActionBtn.setImageDrawable(getResources().getDrawable(R.drawable.delete));
                binding.moreActionBtn.setOnClickListener(this::deleteTask);
                binding.moreActionBtn.setVisibility(View.VISIBLE);
            } else {
                binding.moreActionBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            binding.taskCompletionCkb.setEnabled(false);
        }
    }

    private void dueDateTask(View view) {

        final int[] hour = new int[1];
        final int[] minute = new int[1];
        final Date[] date = new Date[1];
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault());

        MaterialTimePicker selectTime = new MaterialTimePicker.Builder().setTitleText("Start Time").build();

        selectTime.setCancelable(false);
        selectTime.show(getSupportFragmentManager(), null);

        selectTime.addOnPositiveButtonClickListener(v -> {
            hour[0] = selectTime.getHour();
            minute[0] = selectTime.getMinute();
            if (date[0] == null) {
                return;
            }
            calendar.setTime(date[0]);
            calendar.add(Calendar.HOUR_OF_DAY, selectTime.getHour() + 5);
            calendar.add(Calendar.MINUTE, selectTime.getMinute());


            binding.dueDateTask.setHint(calendar.getTime().toString());
            System.out.println(date[0] + " " + hour[0] + ":" + minute[0]);
        });

        MaterialDatePicker<Long> selectDate = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        selectDate.setCancelable(false);
        selectDate.show(getSupportFragmentManager(), "calendar");
        selectDate.addOnPositiveButtonClickListener(selection -> date[0] = new Date(selection));

    }


    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            tempImageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            selectCameraLauncher.launch(tempImageUri);
        }
    }

    private void addPhotoFromLibrary() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
    }

    private void addBtnClicked(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_task_functionality_sheet, findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.take_photo_btn).setOnClickListener(view1 -> {
            takePhoto();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.upload_image_btn).setOnClickListener(view12 -> {
            addPhotoFromLibrary();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.addSubTaskBtn).setVisibility(View.VISIBLE);
        bottomSheetView.findViewById(R.id.addSubTaskBtn).setOnClickListener(v2 -> {

            TextInputEditText newEditText = new TextInputEditText(this);
            newEditText.setSingleLine();
            newEditText.setPadding(50, 0, 50, 32);
            newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            newEditText.setHint("New Sub Task");

            new MaterialAlertDialogBuilder(this).setTitle("New Sub Task")
                    .setMessage("Would you like to add a subtask?")
                    .setIcon(getDrawable(R.drawable.note))
                    .setView(newEditText)
                    .setNeutralButton("Cancel", (dialog, which) -> bottomSheetDialog.dismiss())
                    .setPositiveButton("Add", (dialog, which) -> {

                        String inputText = Objects.requireNonNull(newEditText.getText()).toString();
                        if (inputText.equals("")) {
                            Toast.makeText(this, "Couldn't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            SubTask subTask = new SubTask();
                            subTask.setTaskParentId(task != null ? task.getTaskId() : 0);
                            subTask.setName(newEditText.getText().toString());
                            subTask.setCompleted(binding.taskCompletionCkb.isChecked());
                            subTasks.add(subTask);
                            if (task != null) {
                                additionalSubTasks.add(subTask);
                            }
                            subTaskViewAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    }).setCancelable(false).show();
        });

        bottomSheetView.findViewById(R.id.record_audio_btn).setOnClickListener(v -> {

            if (hasPermission(getApplicationContext())) {
                LayoutInflater inflater = getLayoutInflater();

                View customDialog = inflater.inflate(R.layout.fragment_audio_dialog, null);
                Button startRecordAudio = customDialog.findViewById(R.id.recordingAudioBtn);

                AlertDialog record = new MaterialAlertDialogBuilder(this)
                        .setView(customDialog).setTitle("Record")
                        .setMessage("Tap long press Mic to start recording.")
                        .setCancelable(false)
                        .setNegativeButton("Exit", (dialog, which) -> {
                            if (mediaRecorder != null)
                                stopRecordAudio();
                            dialog.dismiss();
                        })
                        .show();

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

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void stopRecordAudio() {
        taskAudioRVAdapter.notifyItemRangeChanged(0, mAudios.size());
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
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

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(pathSave);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    private boolean hasPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }


    private void deleteTask(View view) {
        taskListViewModel.deleteTask(task);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Save on back button pressed
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Editable taskName = binding.taskNameText.getText();
            // Save the task
            Task task = new Task();
            task.setTaskId(this.task != null ? this.task.getTaskId() : 0);
            task.setCreationDate(new Date().getTime());
            task.setCompleted(binding.taskCompletionCkb.isChecked());
            task.setCategoryId(categoryId);
            task.setCompletionDate(calendar != null ? calendar.getTime().getTime() : task.getCompletionDate());
            Task oldTask = this.task;


            // Save new task
            assert taskName != null;
            if (!taskName.toString().isEmpty() && task.getTaskId() == 0 && task.getCompletionDate() > 0) {
                task.setTaskName(taskName.toString());
                taskListViewModel.saveTaskWithChildren(task, myPictures, subTasks, mAudios);
            } else if ((task.getCompletionDate() == 0 && !taskName.toString().isEmpty()) && oldTask == null) {
                Toast.makeText(this, "Due date is required", Toast.LENGTH_SHORT).show();
            }

            // Update old task
            if (oldTask != null && !oldTask.getTaskName().equals("") && oldTask.getTaskId() > 0) {
                String oldTaskName = Objects.requireNonNull(binding.taskNameText.getText()).toString();
                oldTask.setTaskName(!oldTaskName.isEmpty() ? oldTaskName : oldTask.getTaskName());
                oldTask.setCompletionDate(calendar != null ? calendar.getTime().getTime() : oldTask.getCompletionDate());
                taskListViewModel.updateTaskAll(oldTask, myPictures, subTasks, mAudios);
                taskListViewModel.insertAllSubTask(additionalSubTasks);
            }

            navigateUpTo(new Intent(this, TaskListFragment.class));
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    // Remote picture from the Task
    @Override
    public void onDeletePicture(View view, int position) {
        taskListViewModel.deletePicture(myPictures.get(position));
        myPictures.remove(position);
        taskPictureRVAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onSubTaskDeleted(View view, int position) {
        taskListViewModel.deleteSubTask(subTasks.get(position));
        subTasks.remove(position);
        subTaskViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onSubTaskCompleted(View view, int position) {
        SubTask subTask = subTasks.get(position);

        if (((CheckBox) view).isChecked()) {
            subTask.setCompleted(true);
            subTasks.get(position).setCompleted(true);
        } else {
            subTask.setCompleted(false);
            subTasks.get(position).setCompleted(false);
        }

        taskListViewModel.updateTaskAll(task, myPictures, subTasks, mAudios);
    }
}
