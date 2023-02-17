package ca.app.assasins.taskappsassassinsandroid.category.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.category.view.adpter.CategoryRecycleAdapter;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.common.view.NavigationActivity;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityCategoryBinding;

public class CategoryActivity extends AppCompatActivity implements CategoryRecycleAdapter.OnCategoryCallback, Toolbar.OnMenuItemClickListener {

    ActivityCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<Category> categories = new ArrayList<>();
    private List<Category> categoriesFiltered = new ArrayList<>();
    private CategoryRecycleAdapter adapter;

    private CategoryRecycleAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCategoryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.categoryList;
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(getApplication())).get(CategoryViewModel.class);

        binding.createCategoryBtn.setOnClickListener(this::newCategory);

        categoryViewModel.getAllCategories().observe(this, result -> {
            this.categories.clear();
            this.categories.addAll(result);
            adapter.notifyDataSetChanged();
        });
        adapter = new CategoryRecycleAdapter(categories, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        SearchView searchView = binding.searchView;

        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());
        searchView.setOnMenuItemClickListener(this);

    }


    private void renameCategory(CategoryActivity context, List<Category> categoriesFiltered, int position, Context ApplicationContext, CategoryRecycleAdapter myAdapter, Context ApplicationContext1) {
        TextInputEditText newEditText = new TextInputEditText(context);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setText(categoriesFiltered.get(position).getName());
        newEditText.setHint("Rename Category");

        new MaterialAlertDialogBuilder(context).setTitle("Rename Category").setMessage("Would you like to rename this category?").setIcon(getDrawable(R.drawable.note)).setView(newEditText).setNeutralButton("Cancel", (dialog, which) -> {

        }).setNegativeButton("Rename", (dialog, which) -> {

            String inputText = Objects.requireNonNull(newEditText.getText()).toString();
            if (inputText.equals("")) {
                Toast.makeText(ApplicationContext, "Couldn't be empty", Toast.LENGTH_SHORT).show();
            } else {

                List<Category> resultCategory = categoriesFiltered.stream().filter(cat -> inputText.equalsIgnoreCase(cat.getName())).collect(Collectors.toList());

                if (resultCategory.isEmpty()) {
                    Category category = categoriesFiltered.get(position);
                    category.setName(inputText);
                    categoryViewModel.updateCategory(category);
                    myAdapter.notifyItemChanged(position);

                } else {
                    Toast.makeText(ApplicationContext1, inputText + " is in our database.", Toast.LENGTH_SHORT).show();
                }
            }
        }).setCancelable(false).show();
    }

    private void deleteCategory(View view, List<Category> categoriesFiltered, int position, CategoryRecycleAdapter myAdapter) {
        Snackbar deleteSnackbar = Snackbar.make(view, "Are sure you want do delete this category?", Snackbar.LENGTH_LONG).setAnchorView(binding.createCategoryBtn).setAction("Accept", v -> {
            categoryViewModel.deleteCategory(categoriesFiltered.get(position));
            categoriesFiltered.remove(categoriesFiltered.get(position));
            myAdapter.notifyItemRemoved(position);
        });
        deleteSnackbar.show();
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

        new MaterialAlertDialogBuilder(this).setTitle("New Category").setMessage("Would you like to create new category?").setIcon(getDrawable(R.drawable.note)).setView(newEditText).setNeutralButton("Cancel", (dialog, which) -> {

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
        }).setCancelable(false).show();
    }

    /***
     * Rename from one category to another category
     * @param position Category position
     */
    @Override
    public void onRenameCategory(int position) {

        renameCategory(this, categories, position, this, adapter, this);
    }

    @Override
    public void onDeleteCategory(View view, int position) {
        deleteCategory(view, categories, position, adapter);
    }

    @Override
    public void onRowClicked(int position) {
        SharedPreferences.Editor categorySP = getSharedPreferences("category_sp", MODE_PRIVATE).edit();
        categorySP.putLong("categoryId", categories.get(position).getId());
        categorySP.putInt("categoryCount", categories.size());
        categorySP.putString("categoryName", categories.get(position).getName());

        if (categories.size() > 1) {
            String moveToCategories = "";
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() != categories.get(position).getId()) {
                    moveToCategories += categories.get(i).getName() + ",";
                }
            }
            categorySP.putString("moveToCategories", moveToCategories);
        }
        else {
            categorySP.putString("moveToCategories", null);
        }

        categorySP.apply();

        Intent navigationActivity = new Intent(this, NavigationActivity.class);
        startActivity(navigationActivity);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        return false;
    }


    @NonNull
    private Supplier<TextWatcher> getTextWatcherSupplier() {
        return () -> new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoriesFiltered.clear();
                RecyclerView categoryFilterRecycle = binding.categoryFilterRecycle;

                // filter category that contain x value
                categoriesFiltered = categories.stream().filter(category -> {

                    if (s.length() == 0) return false;
                    return category.getName().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                myAdapter = new CategoryRecycleAdapter(categoriesFiltered, new CategoryRecycleAdapter.OnCategoryCallback() {
                    @Override
                    public void onRenameCategory(int position) {
                        renameCategory(CategoryActivity.this, categoriesFiltered, position, getApplicationContext(), myAdapter, getApplicationContext());
                    }

                    @Override
                    public void onDeleteCategory(View view, int position) {
                        deleteCategory(view, categoriesFiltered, position, myAdapter);
                    }

                    @Override
                    public void onRowClicked(int position) {
                        SharedPreferences.Editor categorySP = getSharedPreferences("category_sp", MODE_PRIVATE).edit();
                        categorySP.putLong("categoryId", categories.get(position).getId());
                        categorySP.putString("categoryName", categories.get(position).getName());
                        categorySP.apply();

                        Intent navigationActivity = new Intent(getApplicationContext(), NavigationActivity.class);
                        startActivity(navigationActivity);
                    }
                });

                categoryFilterRecycle.setAdapter(myAdapter);
                categoryFilterRecycle.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

}
