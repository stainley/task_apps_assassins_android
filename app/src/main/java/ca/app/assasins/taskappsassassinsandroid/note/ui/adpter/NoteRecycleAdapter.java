package ca.app.assasins.taskappsassassinsandroid.note.ui.adpter;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;

public class NoteRecycleAdapter extends RecyclerView.Adapter<NoteRecycleAdapter.ViewHolder> {

    private final OnNoteCallback onNoteCallback;
    private final List<Note> notes;

    public NoteRecycleAdapter(List<Note> notes, OnNoteCallback onCallback) {
        this.notes = notes;
        this.onNoteCallback = onCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_cardview, parent, false);

        return new NoteRecycleAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(notes.get(position).getTitle());
        holder.description.setText(notes.get(position).getDescription());

        onNoteCallback.onDisplayThumbnail(holder.noteThumbnailView, position);


        holder.noteCard.setOnClickListener(view -> {
            this.onNoteCallback.onNoteSelected(view, position);
        });

        holder.cardNoteMenu.setOnClickListener(view -> showPopupMenu(holder.cardNoteMenu, holder.getAdapterPosition()));
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.note_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.delete_note_menu:
                    this.onNoteCallback.onDeleteNote(view, position);


                    return true;
                case R.id.move_note_menu:
                    this.onNoteCallback.onMoveNote(position);

                    return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        private final TextView description;
        private final CardView noteCard;
        private final ImageView noteThumbnailView;

        private final ImageButton cardNoteMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            noteCard = itemView.findViewById(R.id.noteCard);
            cardNoteMenu = itemView.findViewById(R.id.card_note_menu);
            noteThumbnailView = itemView.findViewById(R.id.noteThumbnailView);

        }
    }

    public interface OnNoteCallback {
        void onNoteSelected(View view, int position);

        void onDeleteNote(View view, int position);

        void onMoveNote(int position);

        void onDisplayThumbnail(ImageView Imageview, int position);
    }
}
