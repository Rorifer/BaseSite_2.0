package com.engineeringforyou.basesite.presentation.sitemap;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitecreate.SiteCreateActivity;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
import com.engineeringforyou.basesite.presentation.sitemap.presenter.MapPresenter;
import com.engineeringforyou.basesite.presentation.sitemap.presenter.MapPresenterImpl;
import com.engineeringforyou.basesite.presentation.sitemap.views.MapView;
import com.engineeringforyou.basesite.presentation.sitesearch.SearchSiteActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.location.LocationManager.PASSIVE_PROVIDER;
import static com.engineeringforyou.basesite.presentation.sitecreate.SiteCreateActivity.CODE_SITE_CREATE;

public class MapActivity extends AppCompatActivity implements MapView, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static final String KEY_SITE = "key_site";
    private static final String MAIN_SITE = "main_site";
    public static final double DEFAULT_LAT = 55.753720;
    public static final double DEFAULT_LNG = 37.619927;
    public static final double BORDER_LAT_START = 54.489509;
    public static final double BORDER_LAT_END = 56.953235;
    public static final double BORDER_LNG_START = 35.127559;
    public static final double BORDER_LNG_END = 40.250872;

    private final String POSITION = "position";
    private final String SCALE = "scale";

    private final int PERMISSIONS_LOCATION = 1;
    private final int PERMISSIONS_LOCATION_BUTTON = 2;
    private final float SCALE_DEFAULT = 15;

    @BindView(R.id.ad_mob_map)
    AdView mAdMobView;
    @BindView(R.id.progress_view)
    ProgressBar mProgress;
    @BindView(R.id.error_view)
    AppCompatImageView mError;

    private MapPresenter mPresenter;
    private GoogleMap mMap;
    private Site mMainSite = null;
    private LatLng mPosition = null;
    private float mScale = SCALE_DEFAULT;
    private BitmapDescriptor icon_mts, icon_mgf, icon_beeline, icon_tele;
    private boolean isInitIcon;

    public static void start(Activity activity, @Nullable Site site) {
        Intent intent = new Intent(activity, MapActivity.class);
        intent.putExtra(KEY_SITE, site);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_map);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getParcelable(POSITION);
            mScale = savedInstanceState.getFloat(SCALE, mScale);
            mMainSite = savedInstanceState.getParcelable(MAIN_SITE);
        }

        initToolbar();
        initPresenter();
        initMap();
        initAdMob();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdMobView.resume();
    }

    private void initPresenter() {
        Site site = getIntent().getParcelableExtra(KEY_SITE);
        mPresenter = new MapPresenterImpl(this);
        mPresenter.bind(this, site);
    }

    private void initAdMob() {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.admob_test_device))
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

    @OnClick(R.id.fab_add_site)
    public void addSite() {
        SiteCreateActivity.startForCreateSite(this, mMap == null ? null : mMap.getCameraPosition());
    }

    @OnClick(R.id.search_icon)
    public void searchClick() {
        showSitesForCurrentLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_SITE_CREATE:
                if (resultCode == RESULT_OK) showSitesForCurrentLocation();
                break;
        }
    }

    private void initOperatorIcon() {
        isInitIcon = true;
        icon_mts = BitmapDescriptorFactory.fromResource(R.drawable.ic_mts);
        icon_mgf = BitmapDescriptorFactory.fromResource(R.drawable.ic_megafon);
        icon_beeline = BitmapDescriptorFactory.fromResource(R.drawable.ic_beeline);
        icon_tele = BitmapDescriptorFactory.fromResource(R.drawable.ic_tele2);
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
//        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(
//                new LatLng(BORDER_LAT_START, BORDER_LNG_START),
//                new LatLng(BORDER_LAT_END, BORDER_LNG_END)));
        if (mPosition == null) mPresenter.setupMap();
        else {
            moveCamera(mPosition);
            if (mMainSite != null) showMainSite(mMainSite);
            mPresenter.showSitesLocation(mPosition.latitude, mPosition.longitude);
        }
    }

    private void enableButtonLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION_BUTTON);
        }

        mMap.setOnMyLocationButtonClickListener(() -> {
            clearMap();
            mScale = SCALE_DEFAULT;
            showUserLocation();
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
                if (showOperator(Operator.MTS)) item.setChecked(true);
                return true;
            case R.id.menu_item_mgf:
                if (showOperator(Operator.MEGAFON)) item.setChecked(true);
                return true;
            case R.id.menu_item_vmk:
                if (showOperator(Operator.VIMPELCOM)) item.setChecked(true);
                return true;
            case R.id.menu_item_tele2:
                if (showOperator(Operator.TELE2)) item.setChecked(true);
                return true;
            case R.id.menu_item_all:
                if (showOperator(Operator.ALL)) item.setChecked(true);
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
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_right_out);
        finish();
    }

    private boolean showOperator(Operator operator) {
        if (mMap != null) {
            mScale = mMap.getCameraPosition().zoom;
            mMainSite = null;
            LatLng position = mMap.getCameraPosition().target;
            double lat = position.latitude;
            double lng = position.longitude;
            mPresenter.showOperatorLocation(operator, lat, lng);
            return true;
        } else return false;
    }

    @Override
    public void setMapType(int mapType) {
        if (mMap != null) mMap.setMapType(mapType);
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
        if (mMap != null) {
            LatLng position = mMap.getCameraPosition().target;
            mPresenter.showSitesLocation(position.latitude, position.longitude);
        }
    }

    @Override
    public void clearMap() {
        if (mMap != null) mMap.clear();
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
    public void showSites(@NotNull List<? extends Site> siteList) {
        if (siteList.isEmpty()) {
            Toast.makeText(this, R.string.map_no_sites, Toast.LENGTH_SHORT).show();
        } else {
            if (!isInitIcon) initOperatorIcon();
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
                return icon_mts;
            case MEGAFON:
                return icon_mgf;
            case VIMPELCOM:
                return icon_beeline;
            case TELE2:
                return icon_tele;
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
        } else showStartingMessage();
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
        outState.putParcelable(MAIN_SITE, mMainSite);
        if (mMap != null) {
            outState.putParcelable(POSITION, mMap.getCameraPosition().target);
            outState.putFloat(SCALE, mMap.getCameraPosition().zoom);
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

    public static class DialogRadius extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        SeekBar mSeekBar;
        CheckBox mCheckBox;
        TextView mTextView;
        private final int RADIUS_MAX = 7;

        private static int sRadius;

        public static DialogRadius getInstance(int radius) {
            sRadius = radius;
            return new DialogRadius();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getDialog().setTitle(R.string.dialog_radius_title);
            View v = inflater.inflate(R.layout.dialog_radius, container);
            v.findViewById(R.id.button_radius).setOnClickListener(this);
            mSeekBar = v.findViewById(R.id.seek_radius);
            mTextView = v.findViewById(R.id.text_radius);
            mCheckBox = v.findViewById(R.id.checkbox_all_radius);
            mSeekBar.setOnSeekBarChangeListener(this);
            mCheckBox.setOnCheckedChangeListener(this);
            mSeekBar.setMax(RADIUS_MAX - 1);

            if (sRadius == 0) {
                mCheckBox.setChecked(true);
                mSeekBar.setProgress(RADIUS_MAX - 1);
                mSeekBar.setEnabled(false);
                mTextView.setText(R.string.all_site);
            } else {
                mSeekBar.setProgress(sRadius - 1);
                mTextView.setText(String.valueOf(mSeekBar.getProgress() + 1));
            }
            return v;
        }

        @Override
        public void onClick(View view) {
            ((MapActivity) getActivity()).setRadius(mCheckBox.isChecked() ? 0 : mSeekBar.getProgress() + 1);
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

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mSeekBar.setEnabled(false);
                mTextView.setText(R.string.all_site);
            } else {
                mSeekBar.setEnabled(true);
                mTextView.setText(String.valueOf(mSeekBar.getProgress() + 1));
            }
        }
    }
}

//линейка на карте http://www.barattalo.it/coding/ruler-for-google-maps-v3-to-measure-distance-on-map/