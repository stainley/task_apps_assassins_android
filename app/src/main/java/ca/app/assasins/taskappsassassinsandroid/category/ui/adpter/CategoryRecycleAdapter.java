package ca.app.assasins.taskappsassassinsandroid.category.ui.adpter;

import android.app.Application;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.category.ui.CategoryNavigationActivity;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private final List<Category> categories;
    private final Application application;

    public CategoryRecycleAdapter(List<Category> categories, Application application) {
        this.categories = categories;
        this.application = application;
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
            Toast.makeText(holder.itemView.getContext(), categories.get(position).getName(), Toast.LENGTH_LONG).show();
            Intent categoryNavActivity = new Intent(view.getContext(), CategoryNavigationActivity.class);

            view.getContext().startActivity(categoryNavActivity);
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

                    Snackbar deleteSnackbar = Snackbar.make(view, "Are sure you want do delete this category?", Snackbar.LENGTH_LONG).setAction("Accept", v -> {
                        new ViewModelProvider(new ViewModelStore(), new CategoryViewModelFactory(application)).get(CategoryViewModel.class).deleteCategory(categories.get(position));
                        categories.remove(position);
                        notifyItemRemoved(position);
                    });
                    deleteSnackbar.show();

                    return true;
                case R.id.rename_category_menu:
                    Toast.makeText(view.getContext(), "RENAME MENU", Toast.LENGTH_SHORT).show();
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
}
