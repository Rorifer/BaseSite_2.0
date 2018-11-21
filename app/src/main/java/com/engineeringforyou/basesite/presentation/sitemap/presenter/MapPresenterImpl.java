package com.engineeringforyou.basesite.presentation.sitemap.presenter;

import android.content.Context;

import com.engineeringforyou.basesite.domain.sitemap.MapInteractor;
import com.engineeringforyou.basesite.domain.sitemap.MapInteractorImpl;
import com.engineeringforyou.basesite.models.Job;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitemap.views.MapView;
import com.engineeringforyou.basesite.utils.EventFactory;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapPresenterImpl implements MapPresenter {

    private final int COUNT_SHOW_START_MESSAGE = 7;

    private CompositeDisposable mDisposable;
    private CompositeDisposable mJobDisposable;
    private MapInteractor mInteractor;
    private List<Site> mSiteList = new ArrayList<>();
    private Site mSite = null;
    private MapView mView;
    private List<Job> mJobList = null;

    public MapPresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
        mJobDisposable = new CompositeDisposable();
        mInteractor = new MapInteractorImpl(context);
    }

    @Override
    public void bind(@NotNull MapView view, @Nullable Site site) {
        mView = view;
        mSite = site;
        mInteractor.addCountMapCreate();
        loadJobs();
    }

    private void loadJobs() {
        mJobDisposable.add(mInteractor.loadJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> {
                    mJobList = it;
                    showJobs();
                }, EventFactory.INSTANCE::exception));
    }

    @Override
    public void showJobs() {
        if (mJobList != null && mView != null) {
            mView.showJob(mJobList);
        }
    }

    @Override
    public void setupMap() {
        if (mSite == null) {
            mView.showUserLocation();
        } else {
            mView.showMainSite(mSite);
            mView.moveCamera(new LatLng(mSite.getLatitude(), mSite.getLongitude()));
            showSitesLocation(mSite.getLatitude(), mSite.getLongitude());
        }
        if (mInteractor.getCountMapCreate() < COUNT_SHOW_START_MESSAGE) mView.showStartingMessage();
    }

    @Override
    public void showSitesLocation(double lat, double lng) {
        mView.showProgress();
        mDisposable.clear();
        mSiteList.clear();
        mDisposable.add(mInteractor.getSites(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadSitesSuccess, this::loadSitesError));
    }

    private void loadSitesSuccess(List<Site> siteList) {
        mSiteList.addAll(siteList);
        if (mView != null) {
            mView.showSites(siteList);
            mView.hideProgress();
        }
    }

    @Override
    public void clickSite(@Nullable Site site) {
        mInteractor.saveOperator(site.getOperator());
        mView.toSiteDetail(site);
    }

    @Override
    public void clickMapLocation(double lat, double lng) {
        mView.clearMap();
        showSitesLocation(lat, lng);
    }

    private void loadSitesError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
        if (mView != null) mView.showError();
    }

    @Override
    public void setMapType(int mapType) {
        mInteractor.saveMapType(mapType);
        if (mView != null) mView.setMapType(mapType);
    }

    @Override
    public int getMapType() {
        return mInteractor.getMapType();
    }

    @Override
    public void setRadius(int radius) {
        mInteractor.saveRadius(radius);
        mView.showSitesForCurrentLocation();
    }

    @Override
    public int getRadius() {
        return mInteractor.getRadius();
    }

    @Override
    public void showOperatorLocation(@NotNull Operator operator, double lat, double lng) {
        mInteractor.saveOperator(operator);
        mView.clearMap();
        showSitesLocation(lat, lng);
    }

    @NotNull
    @Override
    public Operator getOperator() {
        return mInteractor.getOperator();
    }

    @NotNull
    @Override
    public ArrayList<Site> getSites() {
        return (ArrayList<Site>) mSiteList;
    }

    @Override
    public void unbindView() {
        mDisposable.dispose();
        mJobDisposable.dispose();
        mView = null;
    }
}