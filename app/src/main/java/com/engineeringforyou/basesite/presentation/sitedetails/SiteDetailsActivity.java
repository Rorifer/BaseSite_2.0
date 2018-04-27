package com.engineeringforyou.basesite.presentation.sitedetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitedetails.presenter.SiteDetailsPresenter;
import com.engineeringforyou.basesite.presentation.sitedetails.presenter.SiteDetailsPresenterImpl;
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SiteDetailsActivity extends AppCompatActivity implements SiteDetailsView {

    private static final String KEY_SITE = "key_site";

    @BindView(R.id.ad_mob)
    AdView mAdMobView;
    @BindView(R.id.progress_bar)
    FrameLayout mProgress;
    @BindView(R.id.site_number)
    AppCompatTextView siteNumber;
    @BindView(R.id.site_address)
    AppCompatTextView siteAddress;
    @BindView(R.id.site_object)
    AppCompatTextView siteObject;
    @BindView(R.id.site_coordinates)
    AppCompatTextView siteCoordinates;
    @BindView(R.id.site_status)
    AppCompatTextView siteStatus;
    @BindView(R.id.site_address_auto)
    AppCompatTextView siteAddressAuto;
    @BindView(R.id.address_auto_layout)
    LinearLayout addressAutoLayout;

    private SiteDetailsPresenter mPresenter;
    private Site mSite;

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
        mPresenter = new SiteDetailsPresenterImpl(this);
        mPresenter.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdMobView.resume();
    }

    @SuppressLint("DefaultLocale")
    private void init() {
        initAdMob();
        mSite = getIntent().getParcelableExtra(KEY_SITE);
        if (mSite != null) {
            siteNumber.setText(String.format("%s (%s)", mSite.getNumber(), mSite.getOperator().getLabel()));
            siteAddress.setText(mSite.getAddress());
            siteObject.setText(mSite.getObj());
            siteCoordinates.setText(String.format("%.6f° С.Ш.\n%.6f° В.Д.", mSite.getLatitude(), mSite.getLongitude()));
            siteStatus.setText(mSite.getStatus().getDescription());
            mPresenter.loadAddressFromCoordinates(mSite.getLatitude(), mSite.getLongitude());
        }
    }

    @Override
    public void setAddressFromCoordinates(@NotNull String address) {
        addressAutoLayout.setVisibility(View.VISIBLE);
        siteAddressAuto.setText(address);
    }

    private void initAdMob() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5A69AA056907078C6954C3CC63DEE957")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdMobView.loadAd(adRequest);
    }

    @OnClick(R.id.button_search)
    public void clickMapBtn() {
        MapsActivity.start(this, mSite);
    }

    @OnClick(R.id.route_btn)
    public void clickRouteBtn() {
        Double lat = mSite.getLatitude();
        Double lng = mSite.getLongitude();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:%s,%s?q=%s,%s", lat, lng, lat, lng))));
    }

    @Override
    public void showProgress() {
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
        mPresenter.unbindView();
    }
}