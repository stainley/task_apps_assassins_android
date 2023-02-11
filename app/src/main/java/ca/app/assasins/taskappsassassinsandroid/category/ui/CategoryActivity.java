package ca.app.assasins.taskappsassassinsandroid.category.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.category.ui.adpter.CategoryRecycleAdapter;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityCategoryBinding;

public class CategoryActivity extends AppCompatActivity {

    ActivityCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCategoryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.categoryList;
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(getApplication())).get(CategoryViewModel.class);

        CategoryRecycleAdapter adapter = new CategoryRecycleAdapter(categories, getApplication());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        binding.createCategoryBtn.setOnClickListener(this::newCategory);

        categoryViewModel.getAllCategories().observe(this, categories -> {
            this.categories.clear();
            this.categories.addAll(categories);
            adapter.notifyItemChanged(categories.size());
        });

    }

    /**
     * Create a new category using AlertDialog
     *
     * @param view View
     */
    public void newCategory(View view) {
        TextInputEditText newEditText = new TextInputEditText(this);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setHint("Category Name");

        new MaterialAlertDialogBuilder(this)
                .setTitle("New Category")
                .setMessage("Would you like to create new category?")
                .setIcon(getDrawable(R.drawable.note))
                .setView(newEditText)
                .setNeutralButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Accept", (dialog, which) -> {

                    String inputText = Objects.requireNonNull(newEditText.getText()).toString();
                    if (inputText.equals("")) {
                        Toast.makeText(this, "Couldn't be empty", Toast.LENGTH_SHORT).show();
                    } else {

                        List<Category> resultCategory = categories.stream().filter(cat -> inputText.equalsIgnoreCase(cat.getName())).collect(Collectors.toList());

                        if (resultCategory.isEmpty()) {
                            categoryViewModel.createCategory(new Category(inputText));
                        } else {
                            Toast.makeText(this, inputText + " is in our database.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }
}
