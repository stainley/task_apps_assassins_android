package ca.app.assasins.taskappsassassinsandroid.task.ui;

import static android.content.Context.MODE_PRIVATE;

import static java.util.Comparator.comparing;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentTaskListBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.ui.NoteListFragmentDirections;
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NoteRecycleAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskListViewAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskListFragment extends Fragment implements TaskListViewAdapter.OnTaskListCallback {

    private FragmentTaskListBinding binding;
    private TaskListViewModel taskListViewModel;
    private final List<Task> tasks = new ArrayList<>();
    private List<Task> tasksFiltered = new ArrayList<>();
    private TaskListViewAdapter taskListViewAdapter;
    private long categoryId;

    boolean titleSortedByAsc = false;
    boolean createdDateSortedByAsc = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTaskListBinding.inflate(inflater, container, false);

        SharedPreferences categorySP = requireActivity().getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);


        taskListViewModel = new ViewModelProvider(this, new TaskListViewModelFactory(requireActivity().getApplication())).get(TaskListViewModel.class);

        taskListViewModel.fetchAllTaskByCategory(categoryId).observe(getViewLifecycleOwner(), tasksResult -> {
            this.tasks.clear();
            this.tasks.addAll(tasksResult);
            this.taskListViewAdapter.notifyItemChanged(tasksResult.size());
        });

        RecyclerView taskListRecycleView = binding.taskList;
        taskListViewAdapter = new TaskListViewAdapter(tasks, this);
        taskListRecycleView.setAdapter(taskListViewAdapter);
        taskListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        SearchView searchView = binding.searchView;
        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());

        binding.newTaskBtn.setOnClickListener(this::createNewTask);
        binding.sortButton.setOnClickListener(this::sortButtonClicked);

        return binding.getRoot();
    }


    public void createNewTask(View view) {
        Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity());
    }

    private void sortButtonClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = (View) inflater.inflate(R.layout.activity_sort_sheet, null);
        bottomSheetView = bottomSheetView.findViewById(R.id.bottomSheetSortContainer);

        bottomSheetView.findViewById(R.id.sort_by_title).setOnClickListener(view1 -> {
            titleSortedByAsc = !titleSortedByAsc;

            if (titleSortedByAsc) {
                tasks.sort(comparing(Task::getTaskName));
            }
            else {
                tasks.sort(comparing(Task::getTaskName).reversed());
            }
            taskListViewAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.sort_by_created_date).setOnClickListener(view12 -> {
            createdDateSortedByAsc = !createdDateSortedByAsc;

            if (createdDateSortedByAsc) {
                tasks.sort(comparing(Task::getCreationDate));
            }
            else {
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

                tasksFiltered = tasks.stream().filter(task -> {

                    if (s.length() == 0) return false;
                    return task.getTaskName().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                taskListViewAdapter = new TaskListViewAdapter(tasksFiltered, (view, position) -> Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity().setOldTask(tasksFiltered.get(position))));
                //taskListViewAdapter = new TaskListViewAdapter(tasksFiltered, TaskListFragment.this);

                taskFilterRecycle.setAdapter(taskListViewAdapter);
                taskFilterRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}