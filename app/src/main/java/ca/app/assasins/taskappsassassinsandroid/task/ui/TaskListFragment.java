package ca.app.assasins.taskappsassassinsandroid.task.ui;

import static android.content.Context.MODE_PRIVATE;
import static java.util.Comparator.comparing;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

public class TaskListFragment extends Fragment implements TaskListViewAdapter.OnTaskListCallback {

    private FragmentTaskListBinding binding;
    private final List<Task> tasks = new ArrayList<>();
    private List<Task> tasksFiltered = new ArrayList<>();
    private TaskListViewAdapter taskListViewAdapter;

    boolean titleSortedByAsc = false;
    boolean createdDateSortedByAsc = false;

    ArrayAdapter<String> adapterItems;
    String[] categories = new String[1000];
    String moveToCategories;
    int categoryCount = -1;
    AutoCompleteTextView autoCompleteTextView;

    private CategoryViewModel categoryViewModel;

    private TaskListViewModel taskListViewModel;
    private TaskListViewAdapter taskListViewAdapterFiltered;
    long categoryId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTaskListBinding.inflate(inflater, container, false);

        RecyclerView taskListRecycleView = binding.taskList;
        taskListViewAdapter = new TaskListViewAdapter(tasks, this);

        taskListRecycleView.setAdapter(taskListViewAdapter);
        taskListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.newTaskBtn.setOnClickListener(this::createNewTask);
        binding.sortButton.setOnClickListener(this::sortButtonClicked);


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

        binding.searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());
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
            titleSortedByAsc = !titleSortedByAsc;

            if (titleSortedByAsc) {
                tasks.sort(comparing(Task::getTaskName));
            } else {
                tasks.sort(comparing(Task::getTaskName).reversed());
            }
            taskListViewAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.sort_by_created_date).setOnClickListener(view12 -> {
            createdDateSortedByAsc = !createdDateSortedByAsc;

            if (createdDateSortedByAsc) {
                tasks.sort(comparing(Task::getCreationDate));
            } else {
                tasks.sort(comparing(Task::getCreationDate).reversed());
            }
            taskListViewAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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

    @Override
    public void onTaskSelected(View view, int position) {
        Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity().setOldTask(tasks.get(position)));
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

                System.out.println("taskFiltered: " + tasksFiltered.size());
                taskListViewAdapterFiltered = new TaskListViewAdapter(tasksFiltered, (view, position) -> Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity().setOldTask(tasksFiltered.get(position))));

                taskFilterRecycle.setAdapter(taskListViewAdapterFiltered);
            }

            @Override
            public void afterTextChanged(Editable s) {
                taskListViewAdapterFiltered.notifyDataSetChanged();
            }
        };
    }
}