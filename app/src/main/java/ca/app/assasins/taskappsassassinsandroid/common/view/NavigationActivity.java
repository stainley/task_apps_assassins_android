package ca.app.assasins.taskappsassassinsandroid.common.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;
import java.util.Objects;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNavigationBinding binding = ActivityNavigationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Intent categoryIntent = getIntent();
        Category category = (Category) categoryIntent.getSerializableExtra("category");
        setTitle(category.getName());

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
        bundle.putSerializable("category", category);



        navController.setGraph(R.navigation.mobile_navigation, bundle);
        NavGraph graph = navController.getGraph();

        NavArgument argument = new NavArgument.Builder().setDefaultValue(category).build();
        graph.addArgument("category", argument);

        Map<String, NavArgument> arguments = graph.getArguments();
        System.out.println(arguments);


        NavigationUI.setupWithNavController(navView, navController);
    }

}