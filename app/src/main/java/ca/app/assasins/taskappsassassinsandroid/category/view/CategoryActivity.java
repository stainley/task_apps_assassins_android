package ca.app.assasins.taskappsassassinsandroid.category.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    private static final String TAG = CategoryActivity.class.getName();
    ActivityCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<Category> categories = new ArrayList<>();
    private List<Category> categoriesFiltered = new ArrayList<>();
    private CategoryRecycleAdapter adapter;
    private CategoryRecycleAdapter myAdapter;
    private SearchView searchView;
    private Uri profilePictureUri;
    private SignInClient oneTapClient;
    private static final int REQ_ONE_TAP = 566658;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth mAuth;
    private boolean showOneTapUI = true;
    private ImageButton avatar;
    private SearchBar searchBarCategory;

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
        mAuth = FirebaseAuth.getInstance();
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

        searchBarCategory = binding.searchBar;
        searchBarCategory.inflateMenu(R.menu.category_search_menu);

        Menu me = searchBarCategory.getMenu();
        avatar = me.getItem(0).getActionView().findViewById(R.id.profile_avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(CategoryActivity.this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                }).addOnFailureListener(CategoryActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });

            }
        });
        /*searchBarCategory.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.login_menu_btn) {
                avatar = item.getActionView().findViewById(R.id.profile_avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CategoryActivity.this, "CLICK ON PHOTO", Toast.LENGTH_SHORT).show();
                    }
                });
                oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(CategoryActivity.this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                }).addOnFailureListener(CategoryActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
            }
            return true;
        });*/

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder().setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build()).setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false).build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true).build();

    }


    private void renameCategory(CategoryActivity context, List<Category> categoriesFiltered, int position, Context ApplicationContext, CategoryRecycleAdapter myAdapter, Context ApplicationContext1) {
        TextInputEditText newEditText = new TextInputEditText(context);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setSingleLine();
        newEditText.setPadding(50, 0, 50, 32);
        newEditText.setText(categoriesFiltered.get(position).getName());
        newEditText.setHint("Rename Category");
        newEditText.setPadding(75, newEditText.getPaddingTop(), newEditText.getPaddingRight(), newEditText.getPaddingBottom());


        new MaterialAlertDialogBuilder(context).setTitle("Rename Category").setView(newEditText).setMessage("Would you like to rename this category?").setIcon(getDrawable(R.drawable.note)).setNeutralButton("Cancel", (dialog, which) -> {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Log.d(TAG, "Got ID token.");

                        this.profilePictureUri = credential.getProfilePictureUri();
                        System.out.println(profilePictureUri);

                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    updateUI(null);
                                }
                            }
                        });
                    } else if (password != null) {
                        // Got a saved username and password. Use them to authenticate
                        // with your backend.
                        Log.d(TAG, "Got password.");
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d(TAG, "One-tap dialog was closed.");
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d(TAG, "One-tap encountered a network error.");
                            // Try again or just ignore.
                            break;
                        default:
                            Log.d(TAG, "Couldn't get credential from result." + e.getLocalizedMessage());
                            break;
                    }
                    Toast.makeText(this, "Login aborted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            final Menu loginMenu = searchBarCategory.getMenu();

            MenuItem item = loginMenu.getItem(0);
            if (item.getItemId() == R.id.login_menu_btn) {
                avatar = item.getActionView().findViewById(R.id.profile_avatar);
                Picasso.get().load(currentUser.getPhotoUrl()).fit().centerInside().into(avatar);
            }
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

}
