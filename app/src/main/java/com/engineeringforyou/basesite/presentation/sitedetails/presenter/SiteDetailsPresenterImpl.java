package com.engineeringforyou.basesite.presentation.sitedetails.presenter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SiteDetailsPresenterImpl implements SiteDetailsPresenter {

    private Context mContext;
    private CompositeDisposable mDisposable;
    private SiteDetailsView mView;

    public SiteDetailsPresenterImpl(Context context) {
        mContext = context;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void bind(@NonNull SiteDetailsView view) {
        mView = view;
    }

    @Override
    public void loadAddressFromCoordinates(double latitude, double longitude) {
        mDisposable.add(loadAddress(latitude, longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadAddressSuccess));
    }

    private void loadAddressSuccess(String address) {
        mView.setAddressFromCoordinates(address);
    }

    private Single<String> loadAddress(double latitude, double longitude) {
        return Single.fromCallable(() -> {
            Geocoder geocoder = new Geocoder(mContext);
            ArrayList<Address> list = null;
            try {
                list = (ArrayList<Address>) geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list != null ? list.get(0).getAddressLine(0) : "нет данных";
        });
    }

    @Override
    public void unbindView() {
        mView = null;
        mDisposable.dispose();
    }
}