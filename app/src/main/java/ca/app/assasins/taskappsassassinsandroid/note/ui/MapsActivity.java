package ca.app.assasins.taskappsassassinsandroid.note.ui;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.databinding.ActivityMaps2Binding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbar = binding.toolbar;
        setActionBar(toolbar);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        note = (Note) getIntent().getSerializableExtra("note");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(13);

        LatLng torontoCoordinate = new LatLng(43.6532, -79.3832);

        if (note != null) {
            MarkerOptions productOptions = new MarkerOptions()
                    .title(note.getTitle())
                    .position(new LatLng(note.getCoordinate().getLatitude(), note.getCoordinate().getLongitude()));

            Marker productMarker = mMap.addMarker(productOptions);
            assert productMarker != null;
            productMarker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(note.getCoordinate().getLatitude(), note.getCoordinate().getLongitude())));
            return;
        }

        mMap.addMarker(new MarkerOptions().position(torontoCoordinate).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(torontoCoordinate));
    }
}