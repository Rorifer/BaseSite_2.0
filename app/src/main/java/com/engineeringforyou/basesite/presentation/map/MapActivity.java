package com.engineeringforyou.basesite.presentation.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.map.presenter.MapPresenter;
import com.engineeringforyou.basesite.presentation.map.presenter.MapPresenterImpl;
import com.engineeringforyou.basesite.presentation.map.views.MapView;
import com.engineeringforyou.basesite.presentation.searchsite.SearchSiteActivity;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.location.LocationManager.PASSIVE_PROVIDER;

public class MapActivity extends AppCompatActivity implements MapView, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static final String KEY_SITE = "key_site";

    public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public final double DEFAULT_LAT = 55.753720;
    public final double DEFAULT_LNG = 37.619927;
    private final double BORDER_LAT_START = 54.489509;
    private final double BORDER_LAT_END = 56.953235;
    private final double BORDER_LNG_START = 35.127559;
    private final double BORDER_LNG_END = 40.250872;

    @BindView(R.id.ad_mob_map)
    AdView mAdMobView;
    @BindView(R.id.progress_view)
    ProgressBar mProgress;
    @BindView(R.id.error_view)
    AppCompatImageView mError;

    private MapPresenter mPresenter;
    private GoogleMap mMap;
    private float mScale = 16;
    private boolean isLocationGranted = false;

    public static void start(Activity activity, @Nullable Site site) {
        Intent intent = new Intent(activity, MapActivity.class);
        intent.putExtra(KEY_SITE, site);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        initToolbar();
        initPresenter();
        initMap();
        initAdMob();
    }

    private void initPresenter() {
        Site site = getIntent().getParcelableExtra(KEY_SITE);
        mPresenter = new MapPresenterImpl(this);
        mPresenter.bind(this, site);
    }

    private void initAdMob() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5A69AA056907078C6954C3CC63DEE957")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdMobView.loadAd(adRequest);
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(android.R.drawable.ic_menu_search);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(mPresenter.getMapType());
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        getLocationPermission();
        if (isLocationGranted) mMap.setMyLocationEnabled(true);
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(
                new LatLng(BORDER_LAT_START, BORDER_LNG_START),
                new LatLng(BORDER_LAT_END, BORDER_LNG_END)));
        mPresenter.setupMap();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            isLocationGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdMobView.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        switch (mPresenter.getOperator()) {
            case MTS:
                menu.findItem(R.id.menu_item_mts).setChecked(true);
                break;
            case MEGAFON:
                menu.findItem(R.id.menu_item_mgf).setChecked(true);
                break;
            case VIMPELCOM:
                menu.findItem(R.id.menu_item_vmk).setChecked(true);
                break;
            case TELE2:
                menu.findItem(R.id.menu_item_tele2).setChecked(true);
                break;
            case ALL:
                menu.findItem(R.id.menu_item_all).setChecked(true);
                break;
        }

        switch (mPresenter.getMapType()) {
            case (GoogleMap.MAP_TYPE_NORMAL):
                menu.findItem(R.id.menu_item_map_standart).setChecked(true);
                break;
            case (GoogleMap.MAP_TYPE_HYBRID):
                menu.findItem(R.id.menu_item_map_hybrid).setChecked(true);
                break;
            case (GoogleMap.MAP_TYPE_SATELLITE):
                menu.findItem(R.id.menu_item_map_satelit).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toMainActivity();
                return true;
            case R.id.menu_item_radius:
                showDialogRadius();
                return true;

            case R.id.menu_item_mts:
                item.setChecked(true);
                showOperator(Operator.MTS);
                return true;
            case R.id.menu_item_mgf:
                item.setChecked(true);
                showOperator(Operator.MEGAFON);
                return true;
            case R.id.menu_item_vmk:
                item.setChecked(true);
                showOperator(Operator.VIMPELCOM);
                return true;
            case R.id.menu_item_tele2:
                item.setChecked(true);
                showOperator(Operator.TELE2);
                return true;
            case R.id.menu_item_all:
                item.setChecked(true);
                showOperator(Operator.ALL);
                return true;

            case R.id.menu_item_map_standart:
                item.setChecked(true);
                mPresenter.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_item_map_satelit:
                item.setChecked(true);
                mPresenter.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.menu_item_map_hybrid:
                item.setChecked(true);
                mPresenter.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, SearchSiteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_right_out);
        finish();
    }

    private void showOperator(Operator operator) {
        mScale = mMap.getCameraPosition().zoom;
        LatLng position = mMap.getCameraPosition().target;
        double lat = position.latitude;
        double lng = position.longitude;
        mPresenter.showOperatorLocation(operator, lat, lng);
    }

    private void showDialogRadius() {
        DialogRadius.getInstance(mPresenter.getRadius()).show(getFragmentManager(), "dialog_radius");
    }

    public void setRadius(int radius) {
        mPresenter.setRadius(radius);
    }

    @Override
    public void clearMap() {
        mMap.clear();
    }

    private void moveCamera(LatLng position) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, mScale));
    }

    @Override
    public void showMainSite(@NotNull Site site) {
        LatLng position = new LatLng(site.getLatitude(), site.getLongitude());
        mMap.addMarker(new MarkerOptions().
                position(position).
                title(site.getNumber()).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        ).setTag(site);
        moveCamera(position);
    }

    @Override
    public void showSites(@NotNull List<Site> siteList) {
        if (siteList.isEmpty()) {
            Toast.makeText(this, R.string.map_no_sites, Toast.LENGTH_SHORT).show();
        } else {
            for (Site site : siteList) {
                mMap.addMarker(new MarkerOptions().
                        position(new LatLng(site.getLatitude(), site.getLongitude()))
                        .title(site.getNumber())
                        .alpha(0.5f)
                        .icon(iconOperator(site.getOperator())))
                        .setTag(site);
            }
        }
    }

    private BitmapDescriptor iconOperator(Operator operator) {
        switch (operator) {
            case MTS:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_mts);
            case MEGAFON:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_megafon);
            case VIMPELCOM:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_beeline);
            case TELE2:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_tele2);
            default:
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void showUserLocation() {
        Location location = null;
        double lat = DEFAULT_LAT;
        double lng = DEFAULT_LNG;

        if (isLocationGranted) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            location = locationManager != null ? locationManager.getLastKnownLocation(PASSIVE_PROVIDER) : null;
        }

        if (location != null) {
            if (location.getLatitude() > BORDER_LAT_START && location.getLatitude() < BORDER_LAT_END
                    && location.getLongitude() > BORDER_LNG_START && location.getLongitude() < BORDER_LNG_END) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        }

        moveCamera(new LatLng(lat, lng));
        mPresenter.showSitesLocation(lat, lng);
    }

    @Override
    public void showStartingMessage() {
        Toast.makeText(this, R.string.map_starting_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mPresenter.clickSite((Site) marker.getTag());
    }


    @Override
    public void toSiteDetail(@NotNull Site site) {
        SiteDetailsActivity.start(this, site);
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        mPresenter.clickMapLocation(latLng.latitude, latLng.longitude);
    }

    @Override
    public void showError() {
        hideProgress();
        mError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        mError.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        hideError();
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        mAdMobView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAdMobView.destroy();
        super.onDestroy();
    }

    public static class DialogRadius extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
        SeekBar mSeekBar;
        TextView mTextView;
        private final int RADIUS_MAX = 7;

        private static int sRadius;

        public static DialogRadius getInstance(int radius) {
            sRadius = radius;
            return new DialogRadius();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getDialog().setTitle(R.string.dialog_radius_title);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.activity_dialog_radius, null);
            v.findViewById(R.id.radiusOk).setOnClickListener(this);
            mSeekBar = v.findViewById(R.id.seekRadius);
            mSeekBar.setMax(RADIUS_MAX - 1);
            mSeekBar.setProgress(sRadius - 1);
            mSeekBar.setOnSeekBarChangeListener(this);
            mTextView = v.findViewById(R.id.textView);
            mTextView.setText(String.valueOf(mSeekBar.getProgress() + 1));
            return v;
        }

        @Override
        public void onClick(View view) {
            ((MapActivity) getActivity()).setRadius(mSeekBar.getProgress() + 1);
            dismiss();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mTextView.setText(String.valueOf(seekBar.getProgress() + 1));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}