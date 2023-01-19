package uz.sanjar.mystadiums;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uz.sanjar.mystadiums.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "MapsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
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

    private Address address1;
    private String addressLine;
    private String countryName;
    private String adminArea;
    private String subAdminArea;
    private String locality;
    private String countryCode;
    private String latLng;

    //private BottomSheetFragment dialog=new BottomSheetFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getLocationPermission();
        onClickListeners();
    }

    //This fun is for clicking the items on the activity
    private void onClickListeners() {
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

    @SuppressLint("SetTextI18n")
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
            Address address = list.get(0);
            address1 = list.get(0);
            addressLine = address.getAddressLine(0);
            countryName = address.getCountryName();
            adminArea = address.getAdminArea();
            subAdminArea = address.getSubAdminArea();
            locality = address.getLocality();
            countryCode = address.getCountryCode();
            latLng = address.getLatitude() + " / " + address.getLongitude();
           /* for(Fragment fragment: this.getSupportFragmentManager().getFragments()){
                if(fragment instanceof BottomSheetDialogFragment)
                    return;
            }
            BottomFragment bottomSheetFragment=new BottomFragment();
            Bundle args=new Bundle();
            args.putString("getAddressLine",address.getAddressLine(0));
            args.putString("getCountryName",address.getCountryName());
            args.putString("getAdminArea",address.getAdminArea());
            args.putString("getSubAdminArea",address.getSubAdminArea());
            args.putString("getLocality",address.getLocality());
            args.putString("getCountryCode",address.getCountryCode());
            args.putString("getLatLng",address.getLatitude() +" / "+address.getLongitude());
            bottomSheetFragment.setArguments(args);
            bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());*/

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 11.8f, address.getAddressLine(0));
            MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                    .title(address.getAddressLine(0))
                    .snippet(address.getAdminArea() + "  " + address.getSubAdminArea() + "  " + address.getLocality() + "  " + address.getCountryCode());
            mMap.addMarker(options);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            //If we have access to user location
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            // We are moving the camera to 11.8f zoom
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 11.8f, "You are here!");
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

        //They are just examples to put a marker
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

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //This is for if the user permitted for currentLocation
                mLocationPermissionGranted = true;
                //Then initialise the map
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
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
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().isCompassEnabled();
            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        closeDialog();
        return super.dispatchTouchEvent(ev);
    }

    private void closeDialog() {
        View view = this.getCurrentFocus();
        if (view != null) {
            /*binding.dialogShow.getAnimation();
            binding.dialogShow.setVisibility(View.GONE);*/
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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

        for (Fragment fragment : this.getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BottomSheetDialogFragment)
                return;
        }
        BottomFragment bottomSheetFragment = new BottomFragment();
        Bundle args = new Bundle();
        args.putString("getAddressLine", addressLine);
        args.putString("getCountryName", countryName);
        args.putString("getAdminArea", adminArea);
        args.putString("getSubAdminArea", subAdminArea);
        args.putString("getLocality", locality);
        args.putString("getCountryCode", countryCode);
        args.putString("getLatLng", latLng);
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}