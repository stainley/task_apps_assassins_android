package ca.app.assasins.taskappsassassinsandroid.common.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.common.ui.ui.dashboard.DashboardFragment;
import ca.app.assasins.taskappsassassinsandroid.common.ui.ui.home.HomeFragment;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Toolbar toolbar = new Toolbar(this);
        //toolbar.setBackgroundColor(Color.RED);

        /*Toolbar materialToolbar = binding.toolbar;
        materialToolbar.setBackgroundColor(Color.BLUE);
        AppBarLayout toolbar2 = binding.toolbar2;*/

        //materialToolbar.setTitle("Grocery");
        /*materialToolbar.setTitle("");*/
        //setTitle("TITLE TOP");

        Intent categoryIntent = getIntent();
        Category category = (Category) categoryIntent.getSerializableExtra("category");

        setTitle(category.getName());
        if (getSupportActionBar() != null) {
            /*binding.toolbar.setTitle("Grocerysaas");*/
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*
        getSupportActionBar().setTitle("School");*/
        BottomNavigationView navView = findViewById(R.id.nav_view);
        /*
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_note:
                        System.out.println("NOTE");
                        fragment = new HomeFragment();
                        break;
                    case R.id.navigation_task:
                        System.out.println("TASK");
                        fragment = new DashboardFragment();
                        break;
                    default:
                        return false;
                }
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_navigation, fragment)
                        .commit();

                return false;
            }
        });
        */

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_note, R.id.navigation_task).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);


    }
}