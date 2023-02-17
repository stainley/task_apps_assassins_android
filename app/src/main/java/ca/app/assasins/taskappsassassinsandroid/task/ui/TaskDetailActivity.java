package ca.app.assasins.taskappsassassinsandroid.task.ui;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityTaskDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.SubTaskViewAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskPictureRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskDetailActivity extends AppCompatActivity implements TaskPictureRVAdapter.OnPictureTaskCallback, SubTaskViewAdapter.OnSubTaskCallback {

    private ActivityTaskDetailBinding binding;

    private TaskListViewModel taskListViewModel;

    private long categoryId;
    private Task task;
    private SubTaskViewAdapter subTaskViewAdapter;
    private TaskPictureRVAdapter taskPictureRVAdapter;
    private Uri tempImageUri = null;

    private Calendar calendar;

    private final List<Picture> myPictures = new ArrayList<>();
    private final List<SubTask> subTasks = new ArrayList<>();
    private final List<SubTask> additionalSubTasks = new ArrayList<>();

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
            binding.dueDateTask.setHint(task.getCompletionDate() != 0 ? completionDate : "");

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

            allowDelete();
        }
    }

    //TODO: Save into DB
    public void dueDateTask(View view) {

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectPictureLauncher.launch(new PickVisualMediaRequest());
        }
    }

    private void addBtnClicked(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_task_functionality_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.take_photo_btn).setOnClickListener(view1 -> {
            takePhoto();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.upload_image_btn).setOnClickListener(view12 -> {
            addPhotoFromLibrary();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.addSubTaskBtn).setOnClickListener(v2 -> {

            // TODO: Add SubTask Dialog Menu

            TextInputEditText newEditText = new TextInputEditText(this);
            newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            newEditText.setHint("Add subtask");

            new MaterialAlertDialogBuilder(this).setTitle("New Sub Task").setMessage("Would you like to add a subtask?").setIcon(getDrawable(R.drawable.note)).setView(newEditText).setNeutralButton("Cancel", (dialog, which) -> {

            }).setPositiveButton("Add", (dialog, which) -> {

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


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void deleteTask(View view) {
        taskListViewModel.deleteTask(task);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            Task oldTask = this.task;

            if (oldTask != null || calendar != null) {
                task.setCompletionDate(calendar != null ? calendar.getTime().getTime() : oldTask.getCompletionDate());
            }

            assert taskName != null;
            if (!taskName.toString().isEmpty() && task.getTaskId() == 0) {
                task.setTaskName(taskName.toString());
                taskListViewModel.saveTaskWithChildren(task, myPictures, subTasks);
            } else {
                task.setTaskName(taskName.toString());
                // update
                if (!myPictures.isEmpty()) {
                    taskListViewModel.updatePictures(task, myPictures);
                }

            }
            if (!task.getTaskName().equals("") && task != null) {
                //taskListViewModel.updateTask(task);
                taskListViewModel.updateTaskAll(task, myPictures, subTasks);
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

        allowDelete();
        taskListViewModel.updateTaskAll(task, myPictures, subTasks);
    }

    public void allowDelete() {
        boolean deleteAllowed = false;

        if (subTasks.size() == 0 && task.isCompleted()) {
            deleteAllowed = true;
        } else if (subTasks.size() > 0) {
            int totalSubtaskComplete = 0;

            for (int i = 0; i < subTasks.size(); i++) {
                if (subTasks.get(i).isCompleted()) {
                    totalSubtaskComplete++;
                }
            }

            if (totalSubtaskComplete == subTasks.size()) {
                task.setCompleted(true);
                deleteAllowed = true;
                binding.taskCompletionCkb.setChecked(task.isCompleted());
                binding.taskCompletionCkb.setEnabled(false);
            } else {
                task.setCompleted(false);
                deleteAllowed = false;
                binding.taskCompletionCkb.setChecked(task.isCompleted());
                binding.taskCompletionCkb.setEnabled(true);
            }
        }

        if (deleteAllowed) {
            binding.moreActionBtn.setImageDrawable(getResources().getDrawable(R.drawable.delete));
            binding.moreActionBtn.setOnClickListener(this::deleteTask);
            binding.moreActionBtn.setVisibility(View.VISIBLE);
        } else {
            binding.moreActionBtn.setVisibility(View.INVISIBLE);
        }
    }
}
