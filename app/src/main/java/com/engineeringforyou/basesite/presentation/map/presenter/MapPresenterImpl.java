package com.engineeringforyou.basesite.presentation.map.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.domain.map.MapInteractor;
import com.engineeringforyou.basesite.domain.map.MapInteractorImpl;
import com.engineeringforyou.basesite.presentation.map.views.MapView;

import io.reactivex.disposables.CompositeDisposable;

public class MapPresenterImpl implements MapPresenter {

    private Context mContext;
    private CompositeDisposable mDisposable;
    private MapInteractor mInteractor;
    private MapView mView;

    public MapPresenterImpl(Context context) {
        mContext = context;
        mDisposable = new CompositeDisposable();
        mInteractor = new MapInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull MapView view) {
        mView = view;
    }






    @Override
    public void unbindView() {
        mDisposable.dispose();
        mView = null;
    }
}