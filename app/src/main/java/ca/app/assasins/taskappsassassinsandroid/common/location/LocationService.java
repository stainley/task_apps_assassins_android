package ca.app.assasins.taskappsassassinsandroid.common.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Optional;

public class LocationService {
    public static final String TAG = LocationService.class.getName();
    public final int REQUEST_CODE = 1;
    public Location currentLocation;
    public LocationManager locationManager;
    public LocationListener locationListener;

    private final Activity activity;

    public LocationService(Activity activity) {
        this.activity = activity;
    }

    public Optional<Location> getCurrentLocation() {

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        locationListener = location -> {
            currentLocation = location;
            Log.i(TAG, "onLocationChanged: " + location);

        };
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        return Optional.ofNullable(currentLocation);
    }
}