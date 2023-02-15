package ca.app.assasins.taskappsassassinsandroid.task.ui.adapter;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;

public class TaskPictureRVAdapter extends RecyclerView.Adapter<TaskPictureRVAdapter.TaskPictureViewHolder> {

    private final List<Picture> pictures;
    private final OnPictureTaskCallback onPictureTaskCallback;

    public TaskPictureRVAdapter(List<Picture> pictures, OnPictureTaskCallback onPictureTaskCallback) {
        this.pictures = pictures;
        this.onPictureTaskCallback = onPictureTaskCallback;
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

        Picasso.get().load(pictures.get(position).getPath()).resize(300, 300).centerInside().into(holder.pictureImageView);

        holder.overflowMenu.setOnClickListener(view -> showPopupMenu(holder.overflowMenu, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.delete_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete_image_menu) {
                onPictureTaskCallback.onDeletePicture(view, position);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    static class TaskPictureViewHolder extends RecyclerView.ViewHolder {
        private final ImageView pictureImageView;
        private final ImageButton overflowMenu;

        public TaskPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureImageView = itemView.findViewById(R.id.pictureImage);
            overflowMenu = itemView.findViewById(R.id.pictureOverflowMenu);
        }
    }

    public interface OnPictureTaskCallback {
        void onDeletePicture(View view, int position);
    }
}
