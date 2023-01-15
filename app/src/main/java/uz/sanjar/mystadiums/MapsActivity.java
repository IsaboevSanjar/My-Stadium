package uz.sanjar.mystadiums;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uz.sanjar.mystadiums.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168)
            , new LatLng(71, 136)
    );
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //Metroes
    private double latXalqlar = 41.312211;
    private double lngXalqlar = 69.243240;
    private double latOlmazor = 41.255776;
    private double lngOlmazor = 69.196121;
    private double latChilonzor = 41.274123;
    private double lngChilonzor = 69.205219;
    private double latMirzo = 41.280990;
    private double lngMirzo = 69.212635;
    private double latNovza = 41.291811;
    private double lngNovza = 69.222601;
    private double latMilliy = 41.306159;
    private double lngMilliy = 69.235738;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //windowStatus();
        getLocationPermission();
        findViews();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }

    private void windowStatus() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void findViews() {

        binding.searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });
        binding.currentLocation.setOnClickListener(view ->
                getDeviceLocation());
        binding.circleProfile.setOnClickListener(view ->
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show());
    }

    private void geoLocate() {
        String searchString = binding.searchEdt.getText().toString();
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }
        if (list.size() > 0) {
            binding.layInfo.setVisibility(View.VISIBLE);
            Address address = list.get(0);
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "geoLocate: " + address.getAddressLine(0));
            binding.info.setText(address.getAddressLine(0));
            binding.info.append("\nCounty Name: " + address.getCountryName());
            binding.info.append("\nAdmin area: " + address.getAdminArea());
            binding.info.append("\nSub-admin area" + address.getSubAdminArea());
            binding.info.append("\nLocality:" + address.getLocality());
            binding.info.append("\nCountry code: " + address.getCountryCode());
            binding.info.append("\nLat / Lng: " + address.getLatitude() + " / " + address.getLongitude());
            //binding.info.append("\n"+address);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 11.8f, address.getAddressLine(0));

        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),11.8f);
                            moveCamera(new LatLng(41.311081, 69.240562), 11.8f, "Tashkent");
                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, Float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(title)
                .snippet("Ona yurt");
        mMap.addMarker(options);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Stadium")
                .snippet("Stadium of the month"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latOlmazor, lngOlmazor)).title("Olmazor")
                .snippet("Stadium of the day"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latChilonzor, lngChilonzor)).title("Chilonzor")
                .snippet("Stadium of the hour"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latMirzo, lngMirzo))
                .title("Mirzo Ulug'leb")
                .snippet("Stadium of the decade"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latNovza, lngNovza)).title("Novza")
                .snippet("Nice place"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latXalqlar, lngXalqlar)).title("Xalqlar do'stligi")
                .snippet("Good place"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latMilliy, lngMilliy)).title("Milliy Bog'")
                .snippet("Suuuiii"));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
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
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().isCompassEnabled();
            mMap.setOnMarkerClickListener(this);
        }
        //mMap.addMarker(new MarkerOptions().position(sydney).title("It is you"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 11.8f));
    }

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        //Toast.makeText(this, marker.getSnippet(), Toast.LENGTH_SHORT).show();
        return false;
    }

}