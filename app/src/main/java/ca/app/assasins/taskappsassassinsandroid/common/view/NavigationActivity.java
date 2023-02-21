package ca.app.assasins.taskappsassassinsandroid.common.view;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = NavigationActivity.class.getName();

    private ArrayList<String> permissionsList;
    private final String[] permissionsStr = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};


    ActivityResultLauncher<String[]> permissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            ArrayList<Boolean> list = new ArrayList<>(result.values());
            permissionsList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                    permissionsList.add(permissionsStr[i]);
                } else if (!hasPermission(NavigationActivity.this, permissionsStr[i])) {
                }
            }
            if (permissionsList.size() > 0) {
                //Some permissions are denied and can be asked again.
                askForPermissions(permissionsList);
            }  //Show alert dialog

        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNavigationBinding binding = ActivityNavigationBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());

        SharedPreferences categorySP = getSharedPreferences("category_sp", MODE_PRIVATE);
        String categoryName = categorySP.getString("categoryName", "No Category");
        long categoryId = categorySP.getLong("categoryId", -1);
        setTitle(categoryName);

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);

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
            } else if (navDestination.getId() == R.id.navigation_task) {
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

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        }
    }


}