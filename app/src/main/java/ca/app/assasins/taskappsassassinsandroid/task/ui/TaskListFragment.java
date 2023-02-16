package ca.app.assasins.taskappsassassinsandroid.task.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentTaskListBinding;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.ui.adapter.TaskListViewAdapter;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModelFactory;

public class TaskListFragment extends Fragment implements TaskListViewAdapter.OnTaskListCallback {

    private FragmentTaskListBinding binding;
    private TaskListViewModel taskListViewModel;
    private final List<Task> tasks = new ArrayList<>();
    private TaskListViewAdapter taskListViewAdapter;
    private long categoryId;

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

        binding.newTaskBtn.setOnClickListener(this::createNewTask);
        return binding.getRoot();
    }


    public void createNewTask(View view) {
        Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity());
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
}