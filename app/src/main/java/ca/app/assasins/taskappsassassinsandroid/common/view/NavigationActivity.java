package ca.app.assasins.taskappsassassinsandroid.common.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNavigationBinding binding = ActivityNavigationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        SharedPreferences categorySP = getSharedPreferences("category_sp", MODE_PRIVATE);
        String categoryName = categorySP.getString("categoryName", "No Category");
        long categoryId = categorySP.getLong("categoryId", -1);
        setTitle(categoryName);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_note, R.id.navigation_task).build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        navController.addOnDestinationChangedListener((navControl, navDestination, bundleResult) -> {

            if (navDestination.getId() == R.id.navigation_note) {
                NavArgument argumentNote = new NavArgument.Builder().setDefaultValue(categoryId).build();
                navDestination.addArgument("categoryId", argumentNote);
            } else {
                NavArgument argumentTask = new NavArgument.Builder().setDefaultValue(categoryId).build();
                navDestination.addArgument("categoryId", argumentTask);
            }

        });


        navController.setGraph(R.navigation.mobile_navigation, bundle);
        NavGraph graph = navController.getGraph();
        NavArgument argument = new NavArgument.Builder().setDefaultValue(categoryId).build();
        graph.addArgument("category", argument);
        NavigationUI.setupWithNavController(navView, navController);
    }

}