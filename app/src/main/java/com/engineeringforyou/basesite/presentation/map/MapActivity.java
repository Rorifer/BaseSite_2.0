package com.engineeringforyou.basesite.presentation.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.engineeringforyou.basesite.utils.EventFactory;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.location.LocationManager.PASSIVE_PROVIDER;

public class MapActivity extends AppCompatActivity implements MapView, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static final String KEY_SITE = "key_site";
    private static final String MAIN_SITE = "main_site";
    private final String SITES = "sites";
    private final String POSITION = "position";
    private final String SCALE = "scale";

    private final int PERMISSIONS_LOCATION = 1;
    private final int PERMISSIONS_LOCATION_BUTTON = 2;
    private final double DEFAULT_LAT = 55.753720;
    private final double DEFAULT_LNG = 37.619927;
    private final double BORDER_LAT_START = 54.489509;
    private final double BORDER_LAT_END = 56.953235;
    private final double BORDER_LNG_START = 35.127559;
    private final double BORDER_LNG_END = 40.250872;
    private final float SCALE_DEFAULT = 15;

    @BindView(R.id.ad_mob_map)
    AdView mAdMobView;
    @BindView(R.id.progress_view)
    ProgressBar mProgress;
    @BindView(R.id.error_view)
    AppCompatImageView mError;

    private MapPresenter mPresenter;
    private GoogleMap mMap;
    private ArrayList<Site> mSites = null;
    private Site mMainSite = null;
    private LatLng mPosition = null;
    private float mScale = SCALE_DEFAULT;

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
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getParcelable(POSITION);
            mScale = savedInstanceState.getFloat(SCALE, mScale);
            mSites = savedInstanceState.getParcelableArrayList(SITES);
            mMainSite = savedInstanceState.getParcelable(MAIN_SITE);
        }

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
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_30dp);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(mPresenter.getMapType());
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.isIndoorLevelPickerEnabled();
        enableButtonLocation();
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(
                new LatLng(BORDER_LAT_START, BORDER_LNG_START),
                new LatLng(BORDER_LAT_END, BORDER_LNG_END)));
        if (mPosition == null) mPresenter.setupMap();
        else {
            moveCamera(mPosition);
            if (mSites != null) showSites(mSites);
            if (mMainSite != null) showMainSite(mMainSite);
        }
    }

    private void enableButtonLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION_BUTTON);
        }

        mMap.setOnMyLocationButtonClickListener(() -> {
            LatLng location = getLocation();
            if (location != null) {
                clearMap();
                mPresenter.showSitesLocation(location.latitude, location.longitude);
                mScale = SCALE_DEFAULT;
                moveCamera(new LatLng(location.latitude, location.longitude));
            }
            return true;
        });

    }

    private LatLng getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(PASSIVE_PROVIDER);
                if (location != null) {
                    return new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    EventFactory.INSTANCE.message("MapActivity: error getLocation(): location = null");
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    LatLng location = getLocation();
                    if (location != null)
                        mPresenter.showSitesLocation(location.latitude, location.longitude);
                }
                break;
            case PERMISSIONS_LOCATION_BUTTON:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
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
        mMainSite = null;
        LatLng position = mMap.getCameraPosition().target;
        double lat = position.latitude;
        double lng = position.longitude;
        mPresenter.showOperatorLocation(operator, lat, lng);
    }

    @Override
    public void setMapType(int mapType) {
        mMap.setMapType(mapType);
    }

    private void showDialogRadius() {
        DialogRadius.getInstance(mPresenter.getRadius()).show(getFragmentManager(), "dialog_radius");
    }

    public void setRadius(int radius) {
        mPresenter.setRadius(radius);
    }

    @Override
    public void showSitesForCurrentLocation() {
        clearMap();
        LatLng position = mMap.getCameraPosition().target;
        mPresenter.showSitesLocation(position.latitude, position.longitude);
    }

    @Override
    public void clearMap() {
        mMap.clear();
    }

    @Override
    public void moveCamera(@NonNull LatLng position) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, mScale));
    }

    @Override
    public void showMainSite(@NotNull Site site) {
        mMainSite = site;
        LatLng position = new LatLng(site.getLatitude(), site.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions().
                position(position)
                .title(site.getNumber())
                .snippet(site.getOperator().getLabel())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        marker.setTag(site);
        marker.showInfoWindow();
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
                        .snippet(site.getOperator().getLabel())
                        .alpha(0.8f)
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

    @Override
    public void showUserLocation() {
        double lat = DEFAULT_LAT;
        double lng = DEFAULT_LNG;
        LatLng location = getLocation();
        if (location != null
                && location.latitude > BORDER_LAT_START
                && location.latitude < BORDER_LAT_END
                && location.longitude > BORDER_LNG_START
                && location.longitude < BORDER_LNG_END) {
            lat = location.latitude;
            lng = location.longitude;
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
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Site> sites = mPresenter.getSites();
        if (sites.isEmpty()) sites = mSites;
        outState.putParcelableArrayList(SITES, sites);
        outState.putParcelable(MAIN_SITE, mMainSite);
        if (mMap != null) {
            outState.putParcelable(POSITION, mMap.getCameraPosition().target);
            outState.putFloat(SCALE, mMap.getCameraPosition().zoom);
        } else {
            EventFactory.INSTANCE.message("MapActivity: error onSaveInstanceState(): mMap = null");
        }
        super.onSaveInstanceState(outState);
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
            View v = inflater.inflate(R.layout.activity_dialog_radius, container);
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