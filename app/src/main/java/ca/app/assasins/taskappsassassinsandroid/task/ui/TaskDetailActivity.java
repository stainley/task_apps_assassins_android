package ca.app.assasins.taskappsassassinsandroid.task.ui;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityTaskDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.SubTaskViewAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskPictureRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskDetailActivity extends AppCompatActivity implements TaskPictureRVAdapter.OnPictureTaskCallback {

    private ActivityTaskDetailBinding binding;

    private TaskListViewModel taskListViewModel;

    private long categoryId;
    private Task task;
    private SubTaskViewAdapter subTaskViewAdapter;
    private TaskPictureRVAdapter taskPictureRVAdapter;
    private Uri tempImageUri = null;

    private final List<Picture> myPictures = new ArrayList<>();
    private final List<SubTask> subTasks = new ArrayList<>();

    private final ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {

        @Override
        public void onActivityResult(Uri result) {
            try {
                tempImageUri = result;

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

        binding.startDateTask.setOnClickListener(this::startTaskDate);
        binding.addBtn.setOnClickListener(this::addBtnClicked);
        binding.moreActionBtn.setOnClickListener(this::moreActionBtnClicked);


        SharedPreferences categorySP = getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        taskListViewModel = new ViewModelProvider(new ViewModelStore(), new TaskListViewModelFactory(getApplication())).get(TaskListViewModel.class);

        RecyclerView taskPictureRV = binding.taskPictureRV;

        // adapter picture
        taskPictureRVAdapter = new TaskPictureRVAdapter(myPictures, this);
        taskPictureRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskPictureRV.setAdapter(taskPictureRVAdapter);

        //subtask rv/adapter
        subTaskViewAdapter = new SubTaskViewAdapter(subTasks, (view, position) -> {
            // TODO: Delete subtask
            //
            subTasks.remove(position);
            subTaskViewAdapter.notifyItemRemoved(position);
        });

        RecyclerView subTaskRV = binding.subTaskRV;
        subTaskRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        subTaskRV.setNestedScrollingEnabled(false);
        subTaskRV.setAdapter(subTaskViewAdapter);


        TaskDetailActivityArgs taskDetailActivityArgs = TaskDetailActivityArgs.fromBundle(getIntent().getExtras());
        task = taskDetailActivityArgs.getOldTask();
        if (task != null) {
            binding.taskNameText.setText(task.getTaskName());
            binding.taskCompletionCkb.setChecked(task.isCompleted());

            taskListViewModel.fetchPicturesByTaskId(task.getTaskId()).observe(this, taskImages -> {
                myPictures.clear();
                taskImages.forEach(image -> myPictures.addAll(image.pictures));
                taskPictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            });
        }
    }

    //TODO: Save into DB
    public void startTaskDate(View view) {

        final int[] hour = new int[1];
        final int[] minute = new int[1];
        final Date[] date = new Date[1];
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault());


        MaterialTimePicker selectTime = new MaterialTimePicker.Builder().setTitleText("Start Time").build();

        selectTime.setCancelable(false);
        selectTime.show(getSupportFragmentManager(), null);

        selectTime.addOnPositiveButtonClickListener(v -> {
            hour[0] = selectTime.getHour();
            minute[0] = selectTime.getMinute();
            calendar.setTime(date[0]);
            calendar.add(Calendar.HOUR_OF_DAY, selectTime.getHour() + 5);
            calendar.add(Calendar.MINUTE, selectTime.getMinute());


            binding.startDateTask.setHint(calendar.getTime().toString());
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
                    subTask.setTaskParentId(task.getTaskId());
                    subTask.setName(newEditText.getText().toString());
                    subTasks.add(subTask);
                    subTaskViewAdapter.notifyDataSetChanged();
                    //List<Category> resultCategory = categories.stream().filter(cat -> inputText.equalsIgnoreCase(cat.getName())).collect(Collectors.toList());

                    /*if (resultCategory.isEmpty()) {
                        categoryViewModel.createCategory(new Category(inputText));
                    } else {
                        Toast.makeText(this, inputText + " is in our database.", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }).setCancelable(false).show();

           /* AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetailActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.fragment_sub_task_dialog, null))
                    // Add action buttons
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TaskDetailActivity.this.getDialog().cancel();
                        }
                    });
            return builder.create();*/


            //bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void moreActionBtnClicked(View view) {
        final BottomSheetDialog moreActionBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_more_action_sheet, (LinearLayout) findViewById(R.id.moreActionBottomSheetContainer));
        bottomSheetView.findViewById(R.id.delete_note).setOnClickListener(v -> {

            if (task != null) {
                taskListViewModel.deleteTask(task);
                Toast.makeText(getApplicationContext(), "Delete!!!", Toast.LENGTH_SHORT).show();
                moreActionBottomSheetDialog.dismiss();
                finish();
            }
        });
        moreActionBottomSheetDialog.setContentView(bottomSheetView);
        moreActionBottomSheetDialog.show();
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
            Toast.makeText(this, "SAVING TASK", Toast.LENGTH_SHORT).show();
            Editable taskName = binding.taskNameText.getText();
            // Save the task
            Task task = new Task();
            task.setTaskId(this.task != null ? this.task.getTaskId() : 0);
            task.setCreationDate(new Date().getTime());
            task.setCompleted(binding.taskCompletionCkb.isChecked());
            task.setCategoryId(categoryId);

            assert taskName != null;
            if (!taskName.toString().isEmpty() && task.getTaskId() == 0) {
                task.setTaskName(taskName.toString());

                if (!myPictures.isEmpty()) {
                    List<Picture> pictureList = new ArrayList<>(myPictures);
                    taskListViewModel.savePictures(task, pictureList);
                } else {
                    taskListViewModel.saveTask(task);
                }
            } else {
                task.setTaskName(taskName.toString());
                // update
                if (!myPictures.isEmpty()) {
                    List<Picture> pictureList = new ArrayList<>(myPictures);
                    taskListViewModel.updatePictures(task, pictureList);
                } else {
                    // Task without pictures (Pictures are optional
                    task.setTaskName(taskName.toString());
                    taskListViewModel.updateTask(task);
                }
            }

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
}
