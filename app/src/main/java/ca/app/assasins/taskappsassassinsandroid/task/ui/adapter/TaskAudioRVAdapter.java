package ca.app.assasins.taskappsassassinsandroid.task.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;

public class TaskAudioRVAdapter extends RecyclerView.Adapter<TaskAudioRVAdapter.TaskAudioViewHolder> {

    private final List<Audio> audios;
    private final OnAudioOperationCallback onAudioOperationCallback;

    public TaskAudioRVAdapter(List<Audio> audios, OnAudioOperationCallback onAudioOperationCallback) {
        this.audios = audios;
        this.onAudioOperationCallback = onAudioOperationCallback;
    }

    @NonNull
    @Override
    public TaskAudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_audio, parent, false);

        return new TaskAudioViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAudioRVAdapter.TaskAudioViewHolder holder, int position) {
        holder.scrubber.setTag(position);

        holder.playAudioBtn.setOnClickListener(v ->
                onAudioOperationCallback.onAudioPlay(holder.scrubber, position)
        );

        holder.deleteAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAudioOperationCallback.onDeleteAudio(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    static final class TaskAudioViewHolder extends RecyclerView.ViewHolder {
        private final Button playAudioBtn;
        private final Button deleteAudioBtn;
        private final SeekBar scrubber;

        public TaskAudioViewHolder(@NonNull View itemView) {
            super(itemView);

            playAudioBtn = itemView.findViewById(R.id.playAudioBtn);
            deleteAudioBtn = itemView.findViewById(R.id.deleteAudioBtn);
            scrubber = itemView.findViewById(R.id.audioScrubber);
        }
    }

    public interface OnAudioOperationCallback {

        void onAudioPlay(SeekBar view, int position);

        void onAudioStop(View view, int position);

        void onDeleteAudio(int position);
    }

}

