package ca.app.assasins.taskappsassassinsandroid.task.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;

public class SubTaskViewAdapter extends RecyclerView.Adapter<SubTaskViewAdapter.SubTaskViewHolder> {

    private final List<SubTask> subTasks;
    private final OnSubTaskCallback onSubTaskCallback;

    public SubTaskViewAdapter(List<SubTask> subTasks, OnSubTaskCallback onSubTaskCallback) {
        this.subTasks = subTasks;
        this.onSubTaskCallback = onSubTaskCallback;
    }

    @NonNull
    @Override
    public SubTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.subtask_row, parent, false);

        return new SubTaskViewAdapter.SubTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubTaskViewHolder holder, int position) {
        
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    static final class SubTaskViewHolder extends RecyclerView.ViewHolder {
        // Add button to delete subTask

        public SubTaskViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnSubTaskCallback {
        void onSubTaskDeleted(View view, int position);
    }
}
