package ca.app.assasins.taskappsassassinsandroid.task.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentTaskListBinding;
import ca.app.assasins.taskappsassassinsandroid.task.viewmodel.TaskListViewModel;

public class TaskListFragment extends Fragment {

    private FragmentTaskListBinding binding;
    private Category category;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TaskListViewModel taskListViewModel =
                new ViewModelProvider(this).get(TaskListViewModel.class);

        binding = FragmentTaskListBinding.inflate(inflater, container, false);


        binding.newTaskBtn.setOnClickListener(this::createNewTask);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        assert arguments != null;
        category = (Category) arguments.getSerializable("category");
        if (category != null)
            System.out.println(category.getName());

    }

    public void createNewTask(View view) {
        Navigation.findNavController(view).navigate(TaskListFragmentDirections.actionTaskDetailActivity(category).setCategory(category));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}