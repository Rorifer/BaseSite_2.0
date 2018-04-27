package com.engineeringforyou.basesite;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.searchsite.SearchSiteActivity;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
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

import static android.location.LocationManager.PASSIVE_PROVIDER;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    public static final int MAP_BS_HERE = 1;
    static final int MAP_BS_SITE = 2;
    public static final int MAP_BS_ONE = 3;
    static final int MAP_BS_MAP = 4;
    public static final int MAP_BS_SITE_ONE = 5;

    private double boundsLat1 = 54.489509;
    private double boundsLat2 = 56.953235;
    private double boundsLng1 = 35.127559;
    private double boundsLng2 = 40.250872;

    static float radius = 3; // ралиус "квадрата" в километрах
    private float scale = 14;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private LatLng lastLatLng;
    private String startBD;
    private GoogleMap mMap;
    double lat, lng;
    String siteNumber;
    int nextStep;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_RADIUS = "radius";
    private static final String APP_PREFERENCES_MAP_TYPE = "mapType";
    private SharedPreferences mSettings;
    private AdView mAdView;
    private static boolean startMessage = true;

    public static void start(Activity activity) {

//        startActivity(new Intent(this, MapsActivity.class));
//        //TODO добавить анимацию

        Intent intent = new Intent(activity, MapsActivity.class);
        intent.putExtra("next", MapsActivity.MAP_BS_HERE);
        intent.putExtra("operatorBD", getOperatorBD3(activity));
        activity.startActivity(intent);
    }

    public static void start(Activity activity, Site site) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("LogForMe", "onCreate MapsActivity ");
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nextStep = extras.getInt("next");
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
            siteNumber = extras.getString("site");
            startBD = getOperatorBD();
        }
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdView = findViewById(R.id.ad_mob);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5A69AA056907078C6954C3CC63DEE957")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("LogForMe", "onCreateOptionsMenu ");
        getMenuInflater().inflate(R.menu.menu_map, menu);
        String operatorBD = getOperatorBD();
        switch (operatorBD) {
            case (DB_OPERATOR_MTS):
                menu.findItem(R.id.MTS_oper).setChecked(true);
                break;
            case (DB_OPERATOR_MGF):
                menu.findItem(R.id.MGF_oper).setChecked(true);
                break;
            case (DB_OPERATOR_VMK):
                menu.findItem(R.id.VMK_oper).setChecked(true);
                break;
            case (DB_OPERATOR_TEL):
                menu.findItem(R.id.TEL_oper).setChecked(true);
                break;
            case (DB_OPERATOR_ALL):
                menu.findItem(R.id.ALL_oper).setChecked(true);
                break;
        }

        switch (mapType) {
            case (GoogleMap.MAP_TYPE_NORMAL):
                menu.findItem(R.id.standartMap).setChecked(true);
                break;
            case (GoogleMap.MAP_TYPE_HYBRID):
                menu.findItem(R.id.hybridMap).setChecked(true);
                break;
            case (GoogleMap.MAP_TYPE_SATELLITE):
                menu.findItem(R.id.satelitMap).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                startActivity(new Intent(this, SearchSiteActivity.class));
                return true;
            case R.id.action_radius:
                new DialogRadius().show(getFragmentManager(), "dialog");
                return true;
            case R.id.MTS_oper:
                item.setChecked(true);
                setOperatorBD(DB_OPERATOR_MTS);
                fillOldMap();
                return true;
            case R.id.MGF_oper:
                item.setChecked(true);
                setOperatorBD(DB_OPERATOR_MGF);
                fillOldMap();
                return true;
            case R.id.VMK_oper:
                item.setChecked(true);
                setOperatorBD(DB_OPERATOR_VMK);
                fillOldMap();
                return true;
            case R.id.TEL_oper:
                item.setChecked(true);
                setOperatorBD(DB_OPERATOR_TEL);
                fillOldMap();
                return true;
            case R.id.ALL_oper:
                item.setChecked(true);
                setOperatorBD(DB_OPERATOR_ALL);
                fillOldMap();
                return true;

            case R.id.standartMap:
                item.setChecked(true);
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                mMap.setMapType(mapType);
                return true;
            case R.id.satelitMap:
                item.setChecked(true);
                mapType = GoogleMap.MAP_TYPE_SATELLITE;
                mMap.setMapType(mapType);
                return true;
            case R.id.hybridMap:
               /* if (item.isChecked()) item.setChecked(false);
                else {*/
                item.setChecked(true);
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                mMap.setMapType(mapType);
                //          }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putFloat(APP_PREFERENCES_RADIUS, radius);
        //  editor.apply();
        Log.v("LogForMe", "Запись радиуса в настройки:  " + radius);
        editor.putInt(APP_PREFERENCES_MAP_TYPE, mapType);
        editor.apply();
        Log.v("LogForMe", getClass().getName() + " Запись типа карты в настройки:  " + mapType);
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mSettings.contains(APP_PREFERENCES_RADIUS)) {
            radius = mSettings.getFloat(APP_PREFERENCES_RADIUS, 1);
            Log.v("LogForMe", "Радиус из насторек:  " + radius);
        }
        if (mSettings.contains(APP_PREFERENCES_MAP_TYPE)) {
            mapType = mSettings.getInt(APP_PREFERENCES_MAP_TYPE, 1);
            Log.v("LogForMe", "Тип карты из насторек:  " + mapType);

            getOperatorBD();
        }
        mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        Log.v("LogForMe", getClass() + "onDestroy");
        mAdView.destroy();
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(mapType);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        getLocationPermission();
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(boundsLat1, boundsLng1), new LatLng(boundsLat2, boundsLng2)));
        fillMap();
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

                    if (latitude < boundsLat1 || latitude > boundsLat2 || longitude < boundsLng1 || longitude > boundsLng2) {
                        Log.v("LogForMe", "Текущие координаты за пределами границ карты");
                        startingMap();
                        break;
                    }
                    LatLng myPosition = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, scale));
                    Log.v("LogForMe", "Текущие координаты:  " + latitude + "  " + longitude);
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
        Log.v("LogForMe", "Запрос= " + query);
        // Работа с БД
        db = new DBHelper(this);
        db.create_db();
        sqld = db.open();
        userCursor = sqld.rawQuery(query, null);
        db.close();
        int count = userCursor.getCount();
        Log.v("LogForMe", "Количество ТОЧЕК совпадения = " + count);

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
        Log.v("LogForMe", " Tag из маркера = " + oper);
        setOperatorBD(oper);
        siteData(new DBHelper(getApplicationContext()).
                siteSearch(oper, marker.getTitle(), 1));
    }

    private void siteData(Cursor cursor) {
        Log.v("LogForMe", " MapsActivity siteData ");
        if (cursor == null) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            Log.v("LogForMe", "Ошибка в Курсоре ");
            return;
        }
        int count;
        count = cursor.getCount();
        Log.v("LogForMe", "Пришло Количество строк совпадений = " + count);

        switch (count) {
            case 0:
                Toast.makeText(this, "Совпадений не найдено", Toast.LENGTH_LONG).show();
                break;
            case 1:
                toSiteInfo(cursor);
                break;
            default:
                if (count > 1) {
                    Toast.makeText(this, "Количество  совпадений = " + count, Toast.LENGTH_SHORT).show();
                    Log.v("LogForMe", "default");

                    cursor.moveToFirst();
                    String[] headers = new String[]{"SITE", "Addres"};
//        String[] headers = getResources().getStringArray(R.array.columnsChoice);
                    String[] param1 = new String[count];
                    String[] param2 = new String[count];
                    String[] id = new String[count];
                    Log.v("LogForMe", "Choice.headers.length " + headers.length);
                    for (int i = 0; i < count; i++) {
                        param1[i] = cursor.getString(cursor.getColumnIndex(headers[0]));
                        param2[i] = cursor.getString(cursor.getColumnIndex(headers[1]));
                        id[i] = cursor.getString(cursor.getColumnIndex("_id"));
                        Log.v("LogForMe", i + "id[i] " + id[i]);
                        Log.v("LogForMe", i + "param1[i] " + param1[i]);
                        Log.v("LogForMe", i + "param2[i] " + param2[i]);
                        cursor.moveToNext();
                    }
                    cursor.close();
                    Intent intent = new Intent(this, SiteChoice.class);
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

    private void toSiteInfo(Cursor cursor) {

        if (cursor == null) {
            Log.v("LogForMe", "NULL -1");
            return;
        }
        cursor.moveToFirst();
        double lat, lng;
        String[] headers = getResources().getStringArray(R.array.columns);
        String[] text = new String[headers.length];
        Log.v("LogForMe", "headers.length " + headers.length);
        Log.v("LogForMe", "text.length " + text.length);
        for (int i = 0; i < text.length; i++) {
            Log.v("LogForMe", i + " " + headers[i]);

            if (cursor.getColumnIndex(headers[i]) != -1) {
                text[i] = cursor.
                        getString(cursor.getColumnIndex(headers[i]));
                Log.v("LogForMe", i + " " + text[i]);
            } else {
                Log.v("LogForMe", "Колонки не существует -" + headers[i]);
            }
            if (text[i] == null || text[i].equals("")) text[i] = "нет данных";
            if (headers[i].equals("SITE")) text[i] = text[i] + " (" + operator + ")";
        }
        lat = cursor.getDouble(cursor.getColumnIndex("GPS_Latitude"));//.replace(',', '.');
        lng = cursor.getDouble(cursor.getColumnIndex("GPS_Longitude"));//.replace(',', '.');
        String site = cursor.getString(cursor.getColumnIndex("SITE"));
        Log.v("LogForMe", "SITE  ==" + site);
      //  cursor.close();
        Log.v("LogForMe", "Вся БД закрылась-2");

        Intent intent = new Intent(this, SiteDetailsActivity.class);
        intent.putExtra("lines", text);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("site", site);
        startActivity(intent);


        Site siteS = DBHelper.mapToSiteList(cursor, new SettingsRepositoryImpl(this).getOperator(), this).get(0);

        SiteDetailsActivity.start(this, siteS);
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {

  /*      AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Искать БС в этом месте?");
        builder.setNegativeButton("Нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mMap.clear();
                nextStep = MAP_BS_MAP;
                lastLatLng = latLng;
                fillMap();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();*/

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
                operator = "МТС";
                new SettingsRepositoryImpl(this).saveOperator(Operator.MTS);
                break;
            case DB_OPERATOR_MGF:
                operator = "МегаФон";
                new SettingsRepositoryImpl(this).saveOperator(Operator.MEGAFON);

                break;
            case DB_OPERATOR_VMK:
                operator = "Билайн";
                new SettingsRepositoryImpl(this).saveOperator(Operator.VIMPELCOM);

                break;
            case DB_OPERATOR_TEL:
                operator = "Теле2";
                new SettingsRepositoryImpl(this).saveOperator(Operator.TELE2);

                break;
            case DB_OPERATOR_ALL:
                operator = "Все";
                new SettingsRepositoryImpl(this).saveOperator(Operator.ALL);
                break;
            default:
        }
    }

    public final static String DB_OPERATOR_MTS = "MTS_Site_Base";
    public final static String DB_OPERATOR_MGF = "MGF_Site_Base";
    public final static String DB_OPERATOR_VMK = "VMK_Site_Base";
    public final static String DB_OPERATOR_TEL = "TELE_Site_Base";
    public final static String DB_OPERATOR_ALL = "ALL_Site_Base";

    public static String operator;
    public static String operatorBD = null;


}
