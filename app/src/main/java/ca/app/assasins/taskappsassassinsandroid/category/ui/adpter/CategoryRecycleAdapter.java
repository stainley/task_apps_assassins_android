package ca.app.assasins.taskappsassassinsandroid.category.ui.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    private final List<Category> categories;

    public CategoryRecycleAdapter(List<Category> categories) {
        this.categories = categories;
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
        });
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryNameLabel;
        private final CardView categoryCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameLabel = itemView.findViewById(R.id.categoryName);
            categoryCard = itemView.findViewById(R.id.categoryCard);
        }
    }
}
