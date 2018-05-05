package com.engineeringforyou.basesite.presentation.map.presenter;

import android.content.Context;

import com.engineeringforyou.basesite.domain.map.MapInteractor;
import com.engineeringforyou.basesite.domain.map.MapInteractorImpl;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.map.views.MapView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapPresenterImpl implements MapPresenter {

    //  private Context mContext;
    private CompositeDisposable mDisposable;
    private MapInteractor mInteractor;
    private MapView mView;
    private Site mSite = null;
    // private Integer mRadius;
    //  private boolean isStarting = false;


    public MapPresenterImpl(Context context) {
        //     mContext = context;
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
        mDisposable.add(mInteractor.getSites(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadSitesSuccess, this::loadSitesError));
    }

    private void loadSitesSuccess(List<Site> siteList) {
        if (mView != null) {
            mView.showSites(siteList);
            mView.hideProgress();
        }
    }

    private void loadSitesError(Throwable t) {
        if (mView != null) mView.showError();
    }

    @Override
    public void setMapType(int mapType) {
        mInteractor.saveMapType(mapType);
        mMapType = mapType;
        mMap.setMapType(mapType);
    }

    @Override
    public int getMapType() {
        return mInteractor.getMapType();
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
        return mOperator;
    }


    @Override
    public void unbindView() {
        mDisposable.dispose();
        mView = null;
    }
}