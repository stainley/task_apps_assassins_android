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
        holder.setIsRecyclable(false);
        holder.title.setText(notes.get(position).getTitle());

        StringBuilder noteStr = new StringBuilder();
        if(notes.get(position).getDescription().length() > 10) {
            noteStr.append(notes.get(position).getDescription().substring(0, 10).trim()).append(" ...");
            holder.description.setText(noteStr.toString());
        } else {
            holder.description.setText(notes.get(position).getDescription());
        }

        onNoteCallback.showAudioIcon(holder.playAudioIcon, position);

        onNoteCallback.onDisplayThumbnail(holder.noteThumbnailView, position);

        onNoteCallback.setCardBackgroundColor(holder.noteCardChild, position);

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
                    this.onNoteCallback.onMoveNote(position, notes.get(position));

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

        private final View noteCardChild;
        private final ImageView noteThumbnailView;

        private final ImageButton playAudioIcon;
        private final ImageButton cardNoteMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            noteCard = itemView.findViewById(R.id.noteCard);
            noteCardChild = itemView.findViewById(R.id.noteCardChild);
            playAudioIcon = itemView.findViewById(R.id.play_audio_icon);
            cardNoteMenu = itemView.findViewById(R.id.card_note_menu);
            noteThumbnailView = itemView.findViewById(R.id.noteThumbnailView);
        }
    }

    public interface OnNoteCallback {
        void onNoteSelected(View view, int position);

        void onDeleteNote(View view, int position);

        void onMoveNote(int position, Note note);

        void onDisplayThumbnail(ImageView Imageview, int position);

        void showAudioIcon(ImageButton audioIcon, int position);

        void setCardBackgroundColor(View view, int position);
    }
}
