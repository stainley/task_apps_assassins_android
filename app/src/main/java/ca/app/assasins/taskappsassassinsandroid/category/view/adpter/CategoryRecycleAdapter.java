package ca.app.assasins.taskappsassassinsandroid.category.view.adpter;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private final OnCategoryCallback onCategoryCallback;
    private final List<Category> categories;


    public CategoryRecycleAdapter(List<Category> categories, OnCategoryCallback onCallback) {
        this.categories = categories;
        this.onCategoryCallback = onCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.category_cardview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryNameLabel.setText(categories.get(position).getName());

        holder.categoryCard.setOnClickListener(view -> {
            onCategoryCallback.onRowClicked(position);
        });

        holder.overflowMenu.setOnClickListener(view -> showPopupMenu(holder.overflowMenu, holder.getAdapterPosition()));
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.category_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.delete_category_menu:
                    onCategoryCallback.onDeleteCategory(view, position);


                    return true;
                case R.id.rename_category_menu:
                    onCategoryCallback.onRenameCategory(position);

                    return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryNameLabel;
        private final CardView categoryCard;
        private final ImageButton overflowMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameLabel = itemView.findViewById(R.id.categoryName);
            categoryCard = itemView.findViewById(R.id.categoryCard);
            overflowMenu = itemView.findViewById(R.id.categoryOverflowMenu);

        }
    }

    public interface OnCategoryCallback {
        void onRenameCategory(int position);

        void onDeleteCategory(View view, int position);

        void onRowClicked(int position);
    }
}
