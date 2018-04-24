package com.engineeringforyou.basesite.presentation.sitedetails;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SiteDetailsActivity extends AppCompatActivity implements SiteDetailsView {

    private static final String KEY_SITE = "key_site";

    @BindView(R.id.ad_mob)
    AdView mAdMobView;
    @BindView(R.id.progress_bar)
    FrameLayout mProgress;
    @BindView(R.id.descriptions)
    ListView lvMain;

    private Site mSite;

    String siteNumber;
    double lat, lng;
//    ArrayList<String> text;
//    String[] text1;
//    ArrayAdapter<String> adapter;

    public static void start(Activity activity, Site site) {
        Intent intent = new Intent(activity, SiteDetailsActivity.class);
        intent.putExtra(KEY_SITE, site);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_details);
        ButterKnife.bind(this);
        initAdMob();

        mSite = getIntent().getParcelableExtra(KEY_SITE);

//            text1 = extras.getStringArray("lines");
//
//            text = new ArrayList<>();
//            for (String txt : text1) {
//                text.add(txt);
//            }
//        }
////        ListView lvMain = findViewById(R.id.descriptions);
//
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, text);
//        lvMain.setAdapter(adapter);

    }

    private void initAdMob(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5A69AA056907078C6954C3CC63DEE957")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdMobView.loadAd(adRequest);
    }


    public void onClick2(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("site", siteNumber);

        switch (view.getId()) {
            case R.id.btnSearchNear:
                intent.putExtra("next", MapsActivity.MAP_BS_SITE_ONE);
                break;
            case R.id.site_search_btn:
                intent.putExtra("next", MapsActivity.MAP_BS_ONE);
                break;
            case R.id.btnRoute:
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
        mAdMobView.resume();
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

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }
}