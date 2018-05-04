package com.engineeringforyou.basesite.presentation.map.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.engineeringforyou.basesite.domain.map.MapInteractor;
import com.engineeringforyou.basesite.domain.map.MapInteractorImpl;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.map.views.MapView;

import org.jetbrains.annotations.NotNull;

import io.reactivex.disposables.CompositeDisposable;

public class MapPresenterImpl implements MapPresenter {

    private Context mContext;
    private CompositeDisposable mDisposable;
    private MapInteractor mInteractor;
    private MapView mView;
    private Site mSite;
    private Operator mOperator;


    public MapPresenterImpl(Context context) {
        mContext = context;
        mDisposable = new CompositeDisposable();
        mInteractor = new MapInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull MapView view, @NonNull Operator operator, Site site) {
        mView = view;
        mOperator = operator;
        mSite = site;
    }

    @Override
    public void saveMapType(int mapType) {

    }

    @Override
    public void saveOperator(@NotNull Operator operator) {

    }

    @Override
    public void unbindView() {
        mDisposable.dispose();
        mView = null;
    }
}