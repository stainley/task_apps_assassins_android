package ca.app.assasins.taskappsassassinsandroid.task.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public class TaskPictureRVAdapter extends RecyclerView.Adapter<TaskPictureRVAdapter.TaskPictureViewHolder> {

    private final List<Picture> pictures;

    public TaskPictureRVAdapter(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public TaskPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_picture, parent, false);
        return new TaskPictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskPictureViewHolder holder, int position) {

        Picasso.get().load(pictures.get(position).getPath())
                .resize(300, 300)
                .centerInside()
                .into(holder.pictureImageView);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    static class TaskPictureViewHolder extends RecyclerView.ViewHolder {
        private final ImageView pictureImageView;

        public TaskPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureImageView = itemView.findViewById(R.id.pictureImage);
        }
    }
}
