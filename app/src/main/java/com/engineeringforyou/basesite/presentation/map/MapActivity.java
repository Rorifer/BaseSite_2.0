package com.engineeringforyou.basesite.presentation.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.engineeringforyou.basesite.DialogRadius;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.map.presenter.MapPresenter;
import com.engineeringforyou.basesite.presentation.map.presenter.MapPresenterImpl;
import com.engineeringforyou.basesite.presentation.map.views.MapView;
import com.engineeringforyou.basesite.presentation.searchsite.SearchSiteActivity;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
import com.engineeringforyou.basesite.presentation.sitelist.SiteListActivity;
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl;
import com.engineeringforyou.basesite.utils.DBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.location.LocationManager.PASSIVE_PROVIDER;

public class MapActivity extends AppCompatActivity implements MapView, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static final String KEY_SITE = "key_site";
    private static final String KEY_OPERATOR = "key_operator";

    private final double BORDER_LAT_START = 54.489509;
    private final double BORDER_LAT_END = 56.953235;
    private final double BORDER_LNG_START = 35.127559;
    private final double BORDER_LNG_END = 40.250872;

    @BindView(R.id.ad_mob_map)
    AdView mAdMobView;

    private MapPresenter mPresenter;
    private Operator mOperator;


    public static final int MAP_BS_HERE = 1;
    static final int MAP_BS_SITE = 2;
    public static final int MAP_BS_ONE = 3;
    static final int MAP_BS_MAP = 4;
    public static final int MAP_BS_SITE_ONE = 5;

    public final static String DB_OPERATOR_MTS = "MTS_Site_Base";
    public final static String DB_OPERATOR_MGF = "MGF_Site_Base";
    public final static String DB_OPERATOR_VMK = "VMK_Site_Base";
    public final static String DB_OPERATOR_TEL = "TELE_Site_Base";
    public final static String DB_OPERATOR_ALL = "ALL_Site_Base";

    public static String operator_lable;
    public static String operatorBD = null;


    public static float radius = 3; // ралиус "квадрата" в километрах
    private float scale = 16;
    private int mMapType = GoogleMap.MAP_TYPE_NORMAL;
    private LatLng lastLatLng;
    //    private String startBD;
    private GoogleMap mMap;
    double lat, lng;
    String siteNumber;
    int nextStep;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_RADIUS = "radius";
    private static final String APP_PREFERENCES_MAP_TYPE = "mMapType";
    //    private SharedPreferences mSettings;
    private static boolean startMessage = true;

//    public static void start(Activity activity) {
//        Intent intent = new Intent(activity, MapActivity.class);
//        intent.putExtra("next", MapActivity.MAP_BS_HERE);
//        intent.putExtra("operatorBD", getOperatorBD3(activity));
//        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
//    }

    public static void start(Activity activity, @NonNull Operator operator, @Nullable Site site) {
        Intent intent = new Intent(activity, MapActivity.class);
        intent.putExtra(KEY_OPERATOR, operator);
        intent.putExtra(KEY_SITE, site);
//        intent.putExtra("next", MapActivity.MAP_BS_SITE_ONE);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        initToolbar();
        initMap();
        initAdMob();
        initPresenter();
    }

    private void initPresenter(){
        Site site = getIntent().getParcelableExtra(KEY_SITE);
        mOperator = getIntent().getParcelableExtra(KEY_OPERATOR);
        mPresenter = new MapPresenterImpl(this);
        mPresenter.bind(this, mOperator, site);
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
            actionBar.setIcon(android.R.drawable.ic_menu_home);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSettings.contains(APP_PREFERENCES_RADIUS)) {
            radius = mSettings.getFloat(APP_PREFERENCES_RADIUS, 1);
        }
        if (mSettings.contains(APP_PREFERENCES_MAP_TYPE)) {
            mMapType = mSettings.getInt(APP_PREFERENCES_MAP_TYPE, 1);
            getOperatorBD();
        }
        mAdMobView.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        switch (mOperator) {
            case MTS:
                menu.findItem(R.id.menu_item_MTS).setChecked(true);
                break;
            case MEGAFON:
                menu.findItem(R.id.menu_item_MGF).setChecked(true);
                break;
            case VIMPELCOM:
                menu.findItem(R.id.menu_item_VMK).setChecked(true);
                break;
            case TELE2:
                menu.findItem(R.id.menu_item_TELE2).setChecked(true);
                break;
            case ALL:
                menu.findItem(R.id.menu_item_ALL).setChecked(true);
                break;
        }

        switch (mMapType) {
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
                toSearchSite();
                return true;
            case R.id.menu_item_radius:
                showDialogRadius();
                return true;
            case R.id.menu_item_MTS:
                item.setChecked(true);
                showOperator(Operator.MTS);
                return true;
            case R.id.menu_item_MGF:
                item.setChecked(true);
                showOperator(Operator.MEGAFON);
                return true;
            case R.id.menu_item_VMK:
                item.setChecked(true);
                showOperator(Operator.VIMPELCOM);
                return true;
            case R.id.menu_item_TELE2:
                item.setChecked(true);
                showOperator(Operator.TELE2);
                return true;
            case R.id.menu_item_ALL:
                item.setChecked(true);
                showOperator(Operator.ALL);
                return true;
            case R.id.menu_item_map_standart:
                item.setChecked(true);
                showMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_item_map_satelit:
                item.setChecked(true);
                showMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.menu_item_map_hybrid:
                item.setChecked(true);
                showMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toSearchSite() {
        Intent intent = new Intent(this, SearchSiteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void showOperator(Operator operator) {
        mPresenter.saveOperator(operator);
        mOperator = operator;
        fillOldMap();
    }

    private void showMapType(int mapType) {
        mPresenter.saveMapType(mapType);
        mMapType = mapType;
        mMap.setMapType(mapType);
    }

    private void showDialogRadius() {
        new DialogRadius().show(getFragmentManager(), "dialog");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(mMapType);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        getLocationPermission();
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(
                new LatLng(BORDER_LAT_START, BORDER_LNG_START),
                new LatLng(BORDER_LAT_END, BORDER_LNG_END)));
        mPresenter.setupMap();
    }

    @SuppressLint("MissingPermission")
    private void fillMap() {
        switch (nextStep) {
            case MAP_BS_HERE:
                Location location = null;
                if (mLocationPermissionGranted) {
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    location = locationManager != null ? locationManager.getLastKnownLocation(PASSIVE_PROVIDER) : null;
                }
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    if (latitude < BORDER_LAT_START || latitude > BORDER_LAT_END || longitude < BORDER_LNG_START || longitude > BORDER_LNG_END) {
                        startingMap();
                        break;
                    }
                    LatLng myPosition = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, scale));
                    checkBS(myPosition);
                } else {
                    startingMap();
                }
                break;

            case MAP_BS_SITE:
                checkBS(new LatLng(lat, lng));
                break;

            case MAP_BS_SITE_ONE:
                checkBS(new LatLng(lat, lng));

            case MAP_BS_ONE:
                LatLng site = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().
                        position(site).
                        title(siteNumber).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                ).setTag(startBD);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(site, scale));
                break;

            case MAP_BS_MAP:
                checkBS(lastLatLng);
                break;
        }
        startMessage();
    }

    private void fillOldMap() {
//        if (nextStep == MAP_BS_ONE) return;
        if (nextStep != MAP_BS_SITE && nextStep != MAP_BS_MAP) nextStep = MAP_BS_SITE;

        scale = mMap.getCameraPosition().zoom;
        LatLng position = mMap.getCameraPosition().target;
        lat = position.latitude;
        lng = position.longitude;
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, scale));

        if (getOperatorBD().equals(DB_OPERATOR_ALL)) {
            setOperatorBD(DB_OPERATOR_MTS);
            fillMap();
            setOperatorBD(DB_OPERATOR_MGF);
            fillMap();
            setOperatorBD(DB_OPERATOR_VMK);
            fillMap();
            setOperatorBD(DB_OPERATOR_TEL);
            fillMap();
            setOperatorBD(DB_OPERATOR_ALL);
        } else {
            fillMap();
        }
    }

    private void startingMap() {
        LatLng Position = new LatLng(55.753720, 37.619927);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, scale));
        checkBS(Position);
    }

    private void startMessage() {
        if (startMessage) {
            Toast.makeText(this, "Для поиска БС на карте используйте долгое нажатие в месте поиска", Toast.LENGTH_LONG).show();
            startMessage = false;
        }
    }

    private void checkBS(LatLng center) {
        DBHelper db;
        Cursor userCursor;
        SQLiteDatabase sqld;
        String query;
        String DB_NAME = getOperatorBD();
        if (DB_NAME.equals(DB_OPERATOR_ALL)) return;
        double latMax,
                latMin,
                lngMax,
                lngMin,
                latDelta,
                lngDelta;

        lat = center.latitude;
        lng = center.longitude;
        latDelta = radius / 111;
        lngDelta = radius / 63.2;
        latMax = lat + latDelta;
        latMin = lat - latDelta;
        lngMax = lng + lngDelta;
        lngMin = lng - lngDelta;
        query = "SELECT * FROM " + DB_NAME + " WHERE GPS_Latitude>" + latMin + " AND GPS_Latitude<" + latMax +
                " AND GPS_Longitude>" + lngMin + " AND GPS_Longitude<" + lngMax;
        // Работа с БД
        db = new DBHelper(this);
        db.create_db();
        sqld = db.open();
        userCursor = sqld.rawQuery(query, null);
        db.close();
        int count = userCursor.getCount();

        if (count == 0) {
            Toast.makeText(this, "Здесь БС не найдено!", Toast.LENGTH_SHORT).show();
        } else {

            String oper = getOperatorBD();
            String title = null;

            float colorPoint = 0;
            switch (oper) {
                case DB_OPERATOR_MTS:
                    colorPoint = 0.0F; // red
                    title = "МТС";
                    break;
                case DB_OPERATOR_MGF:
                    colorPoint = 120.0F; // green+
                    title = "Мегафон";
                    break;
                case DB_OPERATOR_VMK:
                    colorPoint = 60.0F; // YELLOW
                    title = "Билайн";
                    break;
                case DB_OPERATOR_TEL:
                    colorPoint = 270.0F; // HUE_VIOLET
                    title = "Теле2";
                    break;
            }

            for (int i = 0; i < count; i++) {
                userCursor.moveToPosition(i);

                mMap.addMarker(new MarkerOptions().
                        position(new LatLng(
                                userCursor.getDouble(userCursor.getColumnIndex("GPS_Latitude")),
                                userCursor.getDouble(userCursor.getColumnIndex("GPS_Longitude")))).
                        title(userCursor.getString(userCursor.getColumnIndex("SITE"))).
                        snippet(title).
                        alpha(0.5f).
                        icon(BitmapDescriptorFactory.defaultMarker(colorPoint))).
                        setTag(oper);
            }
        }
        userCursor.close();
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
    public void onInfoWindowClick(Marker marker) {
        String oper = (String) marker.getTag();
        //  setOperatorBD(oper);
        Operator operator;
        switch (oper) {
            case DB_OPERATOR_MTS:
                operator = Operator.MTS;
                break;
            case DB_OPERATOR_VMK:
                operator = Operator.VIMPELCOM;
                break;
            case DB_OPERATOR_MGF:
                operator = Operator.MEGAFON;
                break;
            case DB_OPERATOR_TEL:
                operator = Operator.TELE2;
                break;
            default:
                return;
        }

        siteData(new DBHelper(getApplicationContext()).
                siteSearch(oper, marker.getTitle(), 1), operator);
    }

    private void siteData(Cursor cursor, Operator operator) {
        if (cursor == null) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            return;
        }
        int count;
        count = cursor.getCount();

        switch (count) {
            case 0:
                Toast.makeText(this, "Совпадений не найдено", Toast.LENGTH_LONG).show();
                break;
            case 1:
                new SettingsRepositoryImpl(this).saveOperator(operator);
                toSiteInfo(cursor, operator);
                break;
            default:
                if (count > 1) {
                    new SettingsRepositoryImpl(this).saveOperator(operator);

                    Toast.makeText(this, "Количество  совпадений = " + count, Toast.LENGTH_SHORT).show();

                    cursor.moveToFirst();
                    String[] headers = new String[]{"SITE", "Addres"};
//        String[] headers = getResources().getStringArray(R.array.columnsChoice);
                    String[] param1 = new String[count];
                    String[] param2 = new String[count];
                    String[] id = new String[count];
                    for (int i = 0; i < count; i++) {
                        param1[i] = cursor.getString(cursor.getColumnIndex(headers[0]));
                        param2[i] = cursor.getString(cursor.getColumnIndex(headers[1]));
                        id[i] = cursor.getString(cursor.getColumnIndex("_id"));
                        cursor.moveToNext();
                    }
                    cursor.close();
                    Intent intent = new Intent(this, SiteListActivity.class);
                    intent.putExtra("param1", param1);
                    intent.putExtra("param2", param2);
                    intent.putExtra("id", id);
                    startActivity(intent);

                    break;
                } else {
                    Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void toSiteInfo(Cursor cursor, Operator operator) {

        if (cursor == null) {
            return;
        }

        Site siteS = DBHelper.mapToSiteList(cursor, operator, this).get(0);
        //неправильно для all

        SiteDetailsActivity.start(this, siteS);
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {

        Toast.makeText(this, "Поиск БС в указанном месте", Toast.LENGTH_SHORT).show();
        mMap.clear();
        nextStep = MAP_BS_MAP;
        lastLatLng = latLng;
        //       fillMap();
        fillOldMap();
    }

    private String getOperatorBD() {
        return getOperatorBD3(this);
    }


    public static String getOperatorBD3(Context context) {
//        if (operatorBD != null) {
//            return operatorBD;
//        } else {
        //  operatorBDoutPreferences();
        Operator oper = new SettingsRepositoryImpl(context).getOperator();

        switch (oper) {
            case ALL:
                return DB_OPERATOR_ALL;
            case MEGAFON:
                return DB_OPERATOR_MGF;
            case VIMPELCOM:
                return DB_OPERATOR_VMK;
            case TELE2:
                return DB_OPERATOR_TEL;
            default:
            case MTS:
                return DB_OPERATOR_MTS;

        }
        //     return operatorBD;
        //}
    }

    public void setOperatorBD(String oper) {
        operatorBD = oper;
        //operatorBDinPreferences(); // TODO


        switch (oper) {
            case DB_OPERATOR_MTS:
                operator_lable = "МТС";
                new SettingsRepositoryImpl(this).saveOperator(Operator.MTS);
                break;
            case DB_OPERATOR_MGF:
                operator_lable = "МегаФон";
                new SettingsRepositoryImpl(this).saveOperator(Operator.MEGAFON);
                break;
            case DB_OPERATOR_VMK:
                operator_lable = "Билайн";
                new SettingsRepositoryImpl(this).saveOperator(Operator.VIMPELCOM);
                break;
            case DB_OPERATOR_TEL:
                operator_lable = "Теле2";
                new SettingsRepositoryImpl(this).saveOperator(Operator.TELE2);
                break;
            case DB_OPERATOR_ALL:
                operator_lable = "Все";
                new SettingsRepositoryImpl(this).saveOperator(Operator.ALL);
                break;
            default:
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    protected void onPause() {
        mAdMobView.pause();
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putFloat(APP_PREFERENCES_RADIUS, radius);
        //  editor.apply();
        editor.putInt(APP_PREFERENCES_MAP_TYPE, mMapType);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        mAdMobView.destroy();
        super.onDestroy();
    }
}