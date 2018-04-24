package com.engineeringforyou.basesite.presentation.sitedetails.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public class SiteDetailsPresenterImpl implements SiteDetailsPresenter {

    private SiteDetailsView mView;
    private CompositeDisposable mDisposable;
//    private DetailsSiteInteractor mInteractor;

    public SiteDetailsPresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
//        mInteractor = new SearchSiteInteractorImpl(context);


        GeocoderTask geocoderTask = new GeocoderTask();
        geocoderTask.execute();

    }

    @Override
    public void bind(@NonNull SiteDetailsView view) {
        mView = view;
    }




    @SuppressLint("StaticFieldLeak")
    class GeocoderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Geocoder geocoder = new Geocoder(getApplication());  //  Функция определения адреса по координатам
            ArrayList<Address> list = null;
            try {
                list = (ArrayList<Address>) geocoder.getFromLocation(lat, lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null) {
                String addres = list.get(0).getAddressLine(0);
                addres = "Адрес по координатам: " + addres;
                text.add(addres);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
        }
    }



    @Override
    public void unbindView() {
        mView = null;
        mDisposable.dispose();
    }
}