package com.engineeringforyou.basesite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;

public class SiteInfo extends Activity {
    String siteNumber;
    double lat, lng;
    ArrayList<String> text;
    String[] text1;
    ArrayAdapter<String> adapter;

    private AdView mAdView;

    public void onCreate(Bundle savedInstanceState) {
        Log.v("LogForMe", "SiteInfo onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_info);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5A69AA056907078C6954C3CC63DEE957")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            siteNumber = extras.getString("site");
            lng = extras.getDouble("lng");
            lat = extras.getDouble("lat");
            text1 = extras.getStringArray("lines");

            text = new ArrayList<>();
            for (String txt : text1) {
                Log.v("LogForMe", " text1 = " + txt);
                text.add(txt);
            }
        }
        ListView lvMain = findViewById(R.id.descriptions);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, text);
        lvMain.setAdapter(adapter);

//        final Context cnt = this;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.v("LogForMe", " Запуск потоока ");
//                Geocoder geocoder = new Geocoder(cnt);  //  Функция определения адреса по координатам
//                ArrayList<Address> list = null;
//                try {
//                    list = (ArrayList<Address>) geocoder.getFromLocation(lat, lng, 1);
//                } catch (IOException e) {
//                    Log.v("LogForMe", " Ошибка Geocoder  ");
//                    e.printStackTrace();
//                }
//                if (list != null) {
//                    String addres = list.get(0).getAddressLine(0);
//                    Log.v("LogForMe", " Адресс от  Geocoder - " + addres);
//                    text.add(addres);
////                    adapter.notifyDataSetChanged();
//                }
//                Log.v("LogForMe", " Остановка потоока ");
//            }
//        }).start();
//                   adapter.notifyDataSetChanged();


        GeocoderTask geocoderTask = new GeocoderTask();
        geocoderTask.execute();
    }

    @SuppressLint("StaticFieldLeak")
    class GeocoderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.v("LogForMe", " Запуск потоока ");
            Geocoder geocoder = new Geocoder(getApplication());  //  Функция определения адреса по координатам
            ArrayList<Address> list = null;
            try {
                list = (ArrayList<Address>) geocoder.getFromLocation(lat, lng, 1);
            } catch (IOException e) {
                Log.v("LogForMe", " Ошибка Geocoder  ");
                e.printStackTrace();
            }
            if (list != null) {
                String addres = list.get(0).getAddressLine(0);
                Log.v("LogForMe", " Адресс от  Geocoder - " + addres);
                addres = "Адрес по координатам: " + addres;
                text.add(addres);
            }
            Log.v("LogForMe", " Остановка потоока ");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onClick2(View view) {
        Log.v("LogForMe", "onClick2");
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("site", siteNumber);

        switch (view.getId()) {
            case R.id.btnSearchNear:
                Log.v("LogForMe", "btnSearchNear  MAP_BS_SITE");
                intent.putExtra("next", MapsActivity.MAP_BS_SITE_ONE);
                break;
            case R.id.site_search_btn:
                Log.v("LogForMe", "button");
                intent.putExtra("next", MapsActivity.MAP_BS_ONE);
                Log.v("LogForMe", "MAP_BS_ONE ++++++ = " + siteNumber);
                break;
            case R.id.btnRoute:
                Log.v("LogForMe", "btnRoute");
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + lat + "," + lng));
                // добавить выбор навигатора (Яндекс-навигатор)
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }
}