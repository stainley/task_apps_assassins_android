package ca.app.assasins.taskappsassassinsandroid.task.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.common.helper.SwipeHelper;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentTaskListBinding;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskListViewAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskListFragment extends Fragment {

    private FragmentTaskListBinding binding;
    private final List<Task> tasks = new ArrayList<>();
    private List<Task> tasksFiltered = new ArrayList<>();
    private TaskListViewAdapter taskListViewAdapter;
    private TaskListViewAdapter taskListViewAdapterFiltered;
    boolean titleSortedByAsc = false;
    boolean createdDateSortedByAsc = false;

    ArrayAdapter<String> adapterItems;
    String[] categories = new String[1000];
    String moveToCategories;
    int categoryCount = -1;
    AutoCompleteTextView autoCompleteTextView;

    private CategoryViewModel categoryViewModel;

    private TaskListViewModel taskListViewModel;
    private long categoryId;
    private SearchView searchView;

    private final ActivityResultLauncher<Intent> textToSpeakLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                List<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                searchView.getEditText().setText(spokenText);
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTaskListBinding.inflate(inflater, container, false);

        RecyclerView taskListRecycleView = binding.taskList;
        searchView = binding.searchView;

        taskListViewAdapter = new TaskListViewAdapter(tasks, getOnCallbackAdapter(tasks));

        taskListRecycleView.setAdapter(taskListViewAdapter);
        taskListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.newTaskBtn.setOnClickListener(this::createNewTask);
        binding.sortButton.setOnClickListener(this::sortButtonClicked);

        searchView.inflateMenu(R.menu.search_bar_menu);
        searchView.setOnMenuItemClickListener(item -> {
            displaySpeechRecognizer();
            return true;
        });

        new SwipeHelper(getContext(), 300, binding.taskList) {
            @Override
            protected void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeUnderlayButton> buffer) {
                int index = viewHolder.getAdapterPosition();
                Task task = tasks.get(index);

                if (task.isCompleted()) {
                    buffer.add(new SwipeUnderlayButton(requireActivity(),
                            "Delete",
                            R.drawable.delete,
                            30,
                            50,
                            Color.parseColor("#ffffff"),
                            SwipeDirection.LEFT,
                            position -> {
                                taskListViewModel.deleteTask(task);
                                taskListViewAdapter.notifyItemRemoved(position);
                            }));
                }

                buffer.add(new SwipeUnderlayButton(requireActivity(),
                        "Update",
                        R.drawable.move,
                        30,
                        50,
                        Color.parseColor("#ffffff"),
                        SwipeDirection.LEFT,
                        position -> {
                            LayoutInflater inflater = getLayoutInflater();
                            View moveNoteView = (View) inflater.inflate(R.layout.categories_dropdown, null);
                            autoCompleteTextView = moveNoteView.findViewById(R.id.auto_complete_txt);

                            adapterItems = new ArrayAdapter<>(getContext(), R.layout.categories_dropdown_items, categories);
                            autoCompleteTextView.setAdapter(adapterItems);
                            autoCompleteTextView.setOnItemClickListener((adapterView, view, position1, id) -> {
                                String category = adapterView.getItemAtPosition(position1).toString();

                                categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result -> {
                                    task.setCategoryId(result.getId());
                                    taskListViewModel.updateTask(task);
                                });
                            });

                            new MaterialAlertDialogBuilder(requireActivity())
                                    .setTitle("Move Note")
                                    .setView(moveNoteView)
                                    .setNeutralButton("Cancel", (dialog, which) -> {

                                    }).setNegativeButton("Done", (dialog, which) -> {

                                    }).setCancelable(false).show();
                        }));
            }
        };

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences categorySP = requireActivity().getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        categoryCount = categorySP.getInt("categoryCount", -1);
        moveToCategories = categorySP.getString("moveToCategories", "");
        categories = moveToCategories.split(",");

        binding.titleCategory.setText(categorySP.getString("categoryName", ""));

        Toolbar taskToolbar = binding.taskAppBar;
        requireActivity().setActionBar(taskToolbar);

        ActionBar actionBar = requireActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        taskToolbar.setNavigationOnClickListener(v -> requireActivity().finish());


        taskListViewModel = new ViewModelProvider(this, new TaskListViewModelFactory(requireActivity().getApplication())).get(TaskListViewModel.class);
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);

        taskListViewModel.fetchAllTaskByCategory(categoryId).observe(requireActivity(), tasksResult -> {
            this.tasks.clear();
            this.tasks.addAll(tasksResult);
            this.taskListViewAdapter.notifyDataSetChanged();
        });

        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());
    }


    public void createNewTask(View view) {
        Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity());
    }

    private void sortButtonClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = (View) inflater.inflate(R.layout.activity_sort_sheet, null);
        bottomSheetView = bottomSheetView.findViewById(R.id.bottomSheetSortContainer);

        bottomSheetView.findViewById(R.id.sort_by_title).setOnClickListener(view1 -> {

            if (titleSortedByAsc) {
                titleSortedByAsc = false;
                taskListViewModel.fetchAllDescByCategory(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            } else {
                titleSortedByAsc = true;
                taskListViewModel.fetchAllAscByCategory(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.sort_by_created_date).setOnClickListener(view12 -> {
            if (createdDateSortedByAsc) {
                createdDateSortedByAsc = false;
                taskListViewModel.fetchAllTasksOrderByDateDesc(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            } else {
                createdDateSortedByAsc = true;
                taskListViewModel.fetchAllTasksOrderByDateAsc(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void refreshNotes(List<Task> result) {
        this.tasks.clear();
        this.tasks.addAll(result);

        this.taskListViewAdapter.notifyItemChanged(result.size());
        this.taskListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        taskListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @NonNull
    private TaskListViewAdapter.OnTaskListCallback getOnCallbackAdapter(List<Task> tasks) {
        return new TaskListViewAdapter.OnTaskListCallback() {
            @Override
            public void onTaskSelected(View view, int position) {
                Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity().setOldTask(tasks.get(position)));
            }

            @Override
            public void getSubtaskCount(TextView subtaskView, int position) {
                subtaskView.setText("0 subtasks");
                taskListViewModel.fetchSubTaskByTaskId(tasks.get(position).getTaskId()).observe(getViewLifecycleOwner(), tasks -> {
                    if (tasks.size() > 0) {
                        if (tasks.get(0) != null && tasks.get(0).getSubTasks().size() > 0) {
                            int totalSubTask = tasks.get(0).getSubTasks().size();
                            subtaskView.setText(totalSubTask + " subtasks");
                        }
                    }
                });
            }
        };
    }

    private Supplier<TextWatcher> getTextWatcherSupplier() {
        return () -> new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tasksFiltered.clear();
                RecyclerView taskFilterRecycle = binding.taskFilterRecycle;
                taskFilterRecycle.setLayoutManager(new LinearLayoutManager(getContext()));

                tasksFiltered = tasks.stream().filter(task -> {
                    if (s.length() == 0) return false;
                    return task.getTaskName().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                //taskListViewAdapterFiltered = new TaskListViewAdapter(tasksFiltered, (view, position) -> Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity().setOldTask(tasksFiltered.get(position))));
                taskListViewAdapterFiltered = new TaskListViewAdapter(tasksFiltered, getOnCallbackAdapter(tasksFiltered));

                taskFilterRecycle.setAdapter(taskListViewAdapterFiltered);
            }

            @Override
            public void afterTextChanged(Editable s) {
                taskListViewAdapterFiltered.notifyDataSetChanged();
            }
        };
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        //startActivityForResult(intent, SPEECH_REQUEST_CODE);
        textToSpeakLauncher.launch(intent);
    }
}