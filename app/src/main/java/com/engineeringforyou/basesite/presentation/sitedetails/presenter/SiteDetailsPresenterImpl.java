package com.engineeringforyou.basesite.presentation.sitedetails.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractor;
import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractorImpl;
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SiteDetailsPresenterImpl implements SiteDetailsPresenter {

    private CompositeDisposable mDisposable;
    private SiteDetailsView mView;
    private SiteDetailsInteractor mInteractor;

    public SiteDetailsPresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
        mInteractor = new SiteDetailsInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull SiteDetailsView view) {
        mView = view;
    }

    @Override
    public void loadAddressFromCoordinates(double lat, double lng) {
        mDisposable.add(mInteractor.loadAddress(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadAddressSuccess));
    }

    private void loadAddressSuccess(String address) {
        mView.setAddressFromCoordinates(address);
    }

    @Override
    public void unbindView() {
        mDisposable.dispose();
        mView = null;
    }
}