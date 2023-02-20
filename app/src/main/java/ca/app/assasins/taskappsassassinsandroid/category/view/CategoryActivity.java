package ca.app.assasins.taskappsassassinsandroid.category.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
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

public class CategoryActivity extends AppCompatActivity {

    ActivityCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<Category> categories = new ArrayList<>();
    private List<Category> categoriesFiltered = new ArrayList<>();
    private CategoryRecycleAdapter adapter;
    private CategoryRecycleAdapter myAdapter;
    private SearchView searchView;

    private final ActivityResultLauncher<Intent> textToSpeakLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                List<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                searchView.getEditText().setText(spokenText);
            }
        }
    });

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
        adapter = new CategoryRecycleAdapter(categories, getOnCallbackCategory(categories));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        searchView = binding.searchView;
        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());

        searchView.inflateMenu(R.menu.search_bar_menu);
        searchView.setOnMenuItemClickListener(item -> {
            displaySpeechRecognizer();
            return true;
        });

        SearchBar searchBarCategory = binding.searchBar;
        searchBarCategory.inflateMenu(R.menu.category_search_menu);
        searchBarCategory.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.login_menu_btn) {
                    Toast.makeText(getApplicationContext(), "Login with Google Account", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }


    private void renameCategory(CategoryActivity context, List<Category> categoriesFiltered, int position, Context ApplicationContext, CategoryRecycleAdapter myAdapter, Context ApplicationContext1) {
        TextInputEditText newEditText = new TextInputEditText(context);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setSingleLine();
        newEditText.setPadding(50, 0, 50, 32);
        newEditText.setText(categoriesFiltered.get(position).getName());
        newEditText.setHint("Rename Category");
        newEditText.setPadding(75, newEditText.getPaddingTop(), newEditText.getPaddingRight(), newEditText.getPaddingBottom());


        new MaterialAlertDialogBuilder(context)
                .setTitle("Rename Category")
                .setView(newEditText)
                .setMessage("Would you like to rename this category?")
                .setIcon(getDrawable(R.drawable.note))
                .setNeutralButton("Cancel", (dialog, which) -> {
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
        newEditText.setSingleLine();
        newEditText.setPadding(50, 0, 50, 32);
        newEditText.setHint("Category Name");
        newEditText.setPadding(75, newEditText.getPaddingTop(), newEditText.getPaddingRight(), newEditText.getPaddingBottom());


        new MaterialAlertDialogBuilder(this).setTitle("New Category").setMessage("Would you like to create new category?").setIcon(getDrawable(R.drawable.note))
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
                }).setCancelable(false).show();
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

                myAdapter = new CategoryRecycleAdapter(categoriesFiltered, getOnCallbackCategory(categoriesFiltered));

                categoryFilterRecycle.setAdapter(myAdapter);
                categoryFilterRecycle.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @NonNull
    private CategoryRecycleAdapter.OnCategoryCallback getOnCallbackCategory(List<Category> categories) {
        return new CategoryRecycleAdapter.OnCategoryCallback() {
            @Override
            public void onRenameCategory(int position) {
                renameCategory(CategoryActivity.this, categories, position, getApplicationContext(), myAdapter, getApplicationContext());
            }

            @Override
            public void onDeleteCategory(View view, int position) {
                deleteCategory(view, categories, position, myAdapter);
            }

            @Override
            public void onRowClicked(int position) {

                SharedPreferences.Editor categorySP = getSharedPreferences("category_sp", MODE_PRIVATE).edit();
                categorySP.putLong("categoryId", categories.get(position).getId());
                categorySP.putInt("categoryCount", categories.size());
                categorySP.putString("categoryName", categories.get(position).getName());

                if (categories.size() > 1) {
                    StringBuilder moveToCategories = new StringBuilder();
                    for (int i = 0; i < categories.size(); i++) {
                        if (!Objects.equals(categories.get(i).getId(), categories.get(position).getId())) {
                            moveToCategories.append(categories.get(i).getName()).append(",");
                        }
                    }
                    categorySP.putString("moveToCategories", moveToCategories.toString());
                } else {
                    categorySP.putString("moveToCategories", null);
                }

                categorySP.apply();

                Intent navigationActivity = new Intent(getApplicationContext(), NavigationActivity.class);
                startActivity(navigationActivity);

            }
        };
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        //startActivityForResult(intent, SPEECH_REQUEST_CODE);
        textToSpeakLauncher.launch(intent);
    }

}
