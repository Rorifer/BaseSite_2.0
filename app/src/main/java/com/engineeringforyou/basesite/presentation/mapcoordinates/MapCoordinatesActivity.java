package com.engineeringforyou.basesite.presentation.mapcoordinates;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.engineeringforyou.basesite.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.location.LocationManager.PASSIVE_PROVIDER;
import static com.engineeringforyou.basesite.presentation.sitemap.MapActivity.DEFAULT_LAT;
import static com.engineeringforyou.basesite.presentation.sitemap.MapActivity.DEFAULT_LNG;

public class MapCoordinatesActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    private static final String POSITION = "position";
    public static int CODE_MAP = 24;

    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private float mScale = 14;
    private GoogleMap mMap;
    private int mapType = GoogleMap.MAP_TYPE_SATELLITE;
    private boolean mLocationPermissionGranted;
    private LatLng mCoordinates;
    private CameraPosition mPosition;

    public static void startForResult(Activity activity, Double latitude, Double longitude, @Nullable CameraPosition position) {
        Intent intent = new Intent(activity, MapCoordinatesActivity.class);
        intent.putExtra(POSITION, position);
        intent.putExtra(LONGITUDE, longitude);
        intent.putExtra(LATITUDE, latitude);
        activity.startActivityForResult(intent, CODE_MAP);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initToolbar();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Карта");

        mPosition = getIntent().getParcelableExtra(POSITION);
       if (mPosition != null) mScale = mPosition.zoom;
        Double lat = getIntent().getDoubleExtra(LATITUDE, 0);
        Double lng = getIntent().getDoubleExtra(LONGITUDE, 0);
        if (lat != 0 && lng != 0) mCoordinates = new LatLng(lat, lng);
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMapType(mapType);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        getLocationPermission();
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
        }
        fillMap();
    }

    @SuppressLint("MissingPermission")
    private void fillMap() {
        Location location = null;
        if (mLocationPermissionGranted) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            location = locationManager != null ? locationManager.getLastKnownLocation(PASSIVE_PROVIDER) : null;
        }
        if (mCoordinates != null) {
            mMap.addMarker(new MarkerOptions().
                    position(mCoordinates).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCoordinates, mScale));
        } else if (mPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPosition.target, mPosition.zoom));
        } else if (location != null) {
            LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, mScale));
        } else {
            startingMap();
        }
    }

    private void startingMap() {
        LatLng Position = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, mScale));
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Установить точку в этом месте?");
        builder.setNegativeButton("Нет",
                (dialog, id) -> {
                    mMap.clear();
                    dialog.cancel();
                });
        builder.setPositiveButton("Да", (dialog, id) -> setPoint(latLng));
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setPoint(LatLng latLng) {
        Intent intent = new Intent();
        intent.putExtra(LONGITUDE, latLng.longitude);
        intent.putExtra(LATITUDE, latLng.latitude);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.slide_right_out);
                return true;
            default:
                return false;
        }
    }
}