package ca.app.assasins.taskappsassassinsandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Optional;

import ca.app.assasins.taskappsassassinsandroid.common.location.LocationService;

public class MainActivity extends AppCompatActivity {

    private LocationService locationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationService = new LocationService(this);


        if (locationService.getCurrentLocation().isPresent()) {
            Location location = locationService.getCurrentLocation().get();
            System.out.println("LATITUDE: " + location.getLatitude() + " Longitude: " + location.getLongitude());
        }

    }

    public Optional<Location> getCurrentLocation() {
        return locationService.getCurrentLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationService.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationService.locationListener);
        }

    }

}