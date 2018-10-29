package com.engineeringforyou.basesite.presentation.sitesearch;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.engineeringforyou.basesite.BuildConfig;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.job.JobMainActivity;
import com.engineeringforyou.basesite.presentation.message.MessageActivity;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
import com.engineeringforyou.basesite.presentation.sitelist.SiteListActivity;
import com.engineeringforyou.basesite.presentation.sitemap.MapActivity;
import com.engineeringforyou.basesite.presentation.sitesearch.presenter.SearchSitePresenter;
import com.engineeringforyou.basesite.presentation.sitesearch.presenter.SearchSitePresenterImpl;
import com.engineeringforyou.basesite.presentation.sitesearch.views.SearchSiteView;
import com.engineeringforyou.basesite.utils.EventFactory;
import com.engineeringforyou.basesite.utils.KeyBoardUtils;
import com.engineeringforyou.basesite.utils.MessageDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


public class SearchSiteActivity extends AppCompatActivity implements SearchSiteView {

    @BindView(R.id.site_search)
    EditText mSearch;
    @BindView(R.id.site_search_layout)
    TextInputLayout mSearchLayout;
    @BindView(R.id.operators_group)
    RadioGroup mOperators;
    @BindView(R.id.progress_bar)
    FrameLayout mProgress;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private SearchSitePresenter mPresenter;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean isNeedStartRewardedVideo;
    private boolean isFailedLoadRewardedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_search);
        ButterKnife.bind(this);
        mPresenter = new SearchSitePresenterImpl(this);
        mPresenter.bind(this);
        mPresenter.watchChanges(mSearch);
        initDrawer();
        initAdMob();
    }

    private void initDrawer() {
        mNavigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            switch (item.getItemId()) {
                case R.id.item_info:
                    mPresenter.showInfo();
                    break;
                case R.id.item_job:
                    openWork();
                    break;
                case R.id.item_mail:
                    mPresenter.messageForDeveloper();
                    break;
                case R.id.item_rating:
                    openRating();
                    break;
                case R.id.item_advertising:
                    openAdvertising();
                    break;
                case R.id.item_logout:
                    finish();
                    break;
            }
            return true;
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (!mRewardedVideoAd.isLoaded()) {
                    loadRewardedVideoAd();
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initAdMob() {
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if (isNeedStartRewardedVideo) {
                    mRewardedVideoAd.show();
                    isNeedStartRewardedVideo = false;
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {
                hideProgress();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                EventFactory.INSTANCE.message("SearchSiteActivity: Call onRewarded");
//                mPresenter.disableAdvertising();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                EventFactory.INSTANCE.message("SearchSiteActivity: Call onRewardedVideoAdLeftApplication");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                isFailedLoadRewardedVideo = true;
                if (isNeedStartRewardedVideo) {
                    hideProgress();
                    isNeedStartRewardedVideo = false;
                    Toast.makeText(SearchSiteActivity.this, "Не удалось загрузить ролик", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
    }

    private void loadRewardedVideoAd() {
        isFailedLoadRewardedVideo = false;
        String adUnitId = getString(BuildConfig.DEBUG ? R.string.rewarded_test_ad_unit_id : R.string.rewarded_ad_unit_id_1);
        mRewardedVideoAd.loadAd(adUnitId, new AdRequest.Builder().build());
    }

    private void openAdvertising() {
//        AdvertisingDialog.INSTANCE.show(this, mRewardedVideoAd);
        showProgress();
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            isNeedStartRewardedVideo = true;
            if (isFailedLoadRewardedVideo) loadRewardedVideoAd();
        }
    }

    private void openRating() {
        final String appPackageName = getPackageName().replace(".debug", "");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void openWork() {
        JobMainActivity.start(this);
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
        mPresenter.onResume();
        hideProgress();
    }

    @OnClick(R.id.menu_item)
    public void clickMenu() {
        hideKeyboard();
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.button_search)
    public void clickSearchBtn() {
        mPresenter.searchSite(getOperatorIndex(), mSearch.getText().toString().trim());
    }

    @OnClick(R.id.button_search_in_map)
    public void clickMapBtn() {
        mPresenter.showMap(getOperatorIndex());
    }

    private int getOperatorIndex() {
        return mOperators.indexOfChild(findViewById(mOperators.getCheckedRadioButtonId()));
    }

    @Override
    public void setOperator(int operatorIndex) {
        mOperators.check(mOperators.getChildAt(operatorIndex).getId());
    }

    @OnEditorAction(R.id.site_search)
    public boolean search(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clickSearchBtn();
            return true;
        }
        return false;
    }

    @Override
    public void showError(int textRes) {
        mSearchLayout.setError(getString(textRes));
    }

    @Override
    public void showInformation(int textRes) {
        showInformation(getString(textRes));
    }

    @Override
    public void showInformation(@NotNull String informationText) {
        MessageDialog.Companion
                .getInstance(informationText, null, null, getString(R.string.info), false)
                .show(getSupportFragmentManager());
    }

    @Override
    public void hideError() {
        mSearchLayout.setError(null);
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
    public void toSiteInfo(@NonNull Site site) {
        hideKeyboard();
        SiteDetailsActivity.start(this, site);
    }

    @Override
    public void toSiteChoice(List<? extends Site> list) {
        hideKeyboard();
        SiteListActivity.start(this, list);
    }

    @Override
    public void openMap(@NonNull Operator operator) {
        showProgress();
        hideKeyboard();
        MapActivity.start(this, null);
    }

    @Override
    public void openMessageForDeveloper() {
        MessageActivity.start(this);
    }

    private void hideKeyboard() {
        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (isNeedStartRewardedVideo) {
            isNeedStartRewardedVideo = false;
            hideProgress();
        } else super.onBackPressed();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
        mPresenter.unbindView();
    }
}