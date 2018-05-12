package com.engineeringforyou.basesite.presentation.map.presenter;

import android.content.Context;

import com.engineeringforyou.basesite.domain.map.MapInteractor;
import com.engineeringforyou.basesite.domain.map.MapInteractorImpl;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.map.views.MapView;
import com.engineeringforyou.basesite.utils.EventFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapPresenterImpl implements MapPresenter {

    private CompositeDisposable mDisposable;
    private MapInteractor mInteractor;
    private List<Site> mSiteList = new ArrayList<>();
    private Site mSite = null;
    private MapView mView;

    public MapPresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
        mInteractor = new MapInteractorImpl(context);
    }

    @Override
    public void bind(@NotNull MapView view, @Nullable Site site) {
        mView = view;
        mSite = site;
    }

    @Override
    public void setupMap() {
        if (mSite == null) {
            mView.showUserLocation();
        } else {
            mView.showMainSite(mSite);
            showSitesLocation(mSite.getLatitude(), mSite.getLongitude());
        }
        mView.showStartingMessage();
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

    private void loadSitesError(Throwable throwable ) {
        EventFactory.INSTANCE.exception(throwable);
        if (mView != null) mView.showError();
    }

    @Override
    public void setMapType(int mapType) {
        mInteractor.saveMapType(mapType);
        mView.setMapType(mapType);
    }

    @Override
    public int getMapType() {
        return mInteractor.getMapType();
    }

    @Override
    public void setRadius(int radius) {
        mInteractor.saveRadius(radius);
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
        mView = null;
    }
}