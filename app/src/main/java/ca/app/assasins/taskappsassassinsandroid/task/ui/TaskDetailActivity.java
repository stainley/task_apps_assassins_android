package ca.app.assasins.taskappsassassinsandroid.task.ui;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityTaskDetailBinding;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskPictureRVAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskDetailActivity extends AppCompatActivity {

    private ActivityTaskDetailBinding binding;

    private TaskListViewModel taskListViewModel;
    private long categoryId;
    private Task task;
    private TaskPictureRVAdapter taskPictureRVAdapter;
    private Uri tempImageUri = null;

    private ImageView imageView;
    private final List<Picture> myPictures = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 3322;

    private ActivityResultLauncher<PickVisualMediaRequest> selectPictureLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {

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
        imageView = binding.pictureImg;
        binding.takePhotoMenu.setOnClickListener(this::taskOptionMenu);

        SharedPreferences categorySP = getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        taskListViewModel = new ViewModelProvider(new ViewModelStore(), new TaskListViewModelFactory(getApplication())).get(TaskListViewModel.class);

        RecyclerView taskPictureRV = binding.taskPictureRV;

        // adapter picture
        taskPictureRVAdapter = new TaskPictureRVAdapter(myPictures);
        taskPictureRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskPictureRV.setAdapter(taskPictureRVAdapter);


        TaskDetailActivityArgs taskDetailActivityArgs = TaskDetailActivityArgs.fromBundle(getIntent().getExtras());
        task = taskDetailActivityArgs.getOldTask();
        if (task != null) {
            binding.taskNameText.setText(task.getTaskName());
            binding.taskCompletionCkb.setChecked(task.isCompleted());

            taskListViewModel.fetchPicturesByTaskId(task.getTaskId()).observe(this, taskImages -> {
                myPictures.clear();
                taskImages.forEach(image -> {
                    myPictures.addAll(image.pictures);
                });
                taskPictureRVAdapter.notifyItemRangeChanged(0, myPictures.size());
            });
        }
    }


    public void taskOptionMenu(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        tempImageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //select camera
            //selectCameraLauncher.launch(tempImageUri);
            //load image from library
            PickVisualMediaRequest pickVisualMediaRequest = new PickVisualMediaRequest();

            selectPictureLauncher.launch(pickVisualMediaRequest);
        }
    }

    public void takePhoto() {

    }

    public void addPhotoFromLibrary() {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //selectCameraLauncher.launch(tempImageUri);
            //selectPictureLauncher.launch();
        }
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
                // update
                task.setTaskName(taskName.toString());
                taskListViewModel.updateTask(task);
            }

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

}
