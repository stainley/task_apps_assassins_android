package ca.app.assasins.taskappsassassinsandroid.task.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;

public class TaskListViewAdapter extends RecyclerView.Adapter<TaskListViewAdapter.TaskListAdapter> {

    private final List<Task> tasks;
    private final OnTaskListCallback onTaskListCallback;

    public TaskListViewAdapter(List<Task> tasks, OnTaskListCallback onTaskListCallback) {
        this.tasks = tasks;
        this.onTaskListCallback = onTaskListCallback;
    }

    @NonNull
    @Override
    public TaskListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.task_cardview_row, parent, false);

        return new TaskListAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListAdapter holder, int position) {
        holder.taskTitleLabel.setText(tasks.get(position).getTaskName());
        holder.taskTitleLabel.setChecked(tasks.get(position).isCompleted());
        if (tasks.get(position).isCompleted()) {
            holder.taskTitleLabel.setCheckMarkDrawable(R.drawable.ic_check_24);
        }

        holder.taskCardView.setOnClickListener(view -> this.onTaskListCallback.onTaskSelected(view, position));
    }

    public void setData(final List<Task> task) {

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskListAdapter extends RecyclerView.ViewHolder {
        private final CheckedTextView taskTitleLabel;
        private final MaterialCardView taskCardView;

        public TaskListAdapter(@NonNull View itemView) {
            super(itemView);
            taskTitleLabel = itemView.findViewById(R.id.taskTitleLabel);
            taskCardView = itemView.findViewById(R.id.taskCardView);
        }
    }

    public interface OnTaskListCallback {
        void onTaskSelected(View view, int position);
    }
}
