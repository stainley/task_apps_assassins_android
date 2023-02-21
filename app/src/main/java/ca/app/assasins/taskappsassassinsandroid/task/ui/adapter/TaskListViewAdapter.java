package ca.app.assasins.taskappsassassinsandroid.task.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        onTaskListCallback.getSubtaskCount(holder.numberSubtask, position);

        if (tasks.get(position).isCompleted()) {
            holder.taskTitleLabel.setCheckMarkDrawable(R.drawable.ic_check_24);
        }

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");

        String completionDate = dateFormat.format(tasks.get(position).getCompletionDate());
        holder.dueDate.setText("Due " + completionDate);

        holder.taskCardView.setOnClickListener(view -> this.onTaskListCallback.onTaskSelected(view, position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskListAdapter extends RecyclerView.ViewHolder {
        private final CheckedTextView taskTitleLabel;
        private final MaterialCardView taskCardView;
        private final TextView numberSubtask;
        private final TextView dueDate;

        public TaskListAdapter(@NonNull View itemView) {
            super(itemView);
            taskTitleLabel = itemView.findViewById(R.id.taskTitleLabel);
            taskCardView = itemView.findViewById(R.id.taskCardView);
            numberSubtask = itemView.findViewById(R.id.number_subtask_txt);
            dueDate = itemView.findViewById(R.id.due_date_txt);

        }
    }

    public interface OnTaskListCallback {
        void onTaskSelected(View view, int position);

        void getSubtaskCount(TextView subtaskView, int position);
    }
}
