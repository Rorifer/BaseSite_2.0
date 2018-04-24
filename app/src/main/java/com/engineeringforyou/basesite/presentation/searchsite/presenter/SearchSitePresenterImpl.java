package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.domain.searchsite.SearchSiteInteractor;
import com.engineeringforyou.basesite.domain.searchsite.SearchSiteInteractorImpl;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchSitePresenterImpl implements SearchSitePresenter {

    private SearchSiteView mView;
    private CompositeDisposable mDisposable;
    private SearchSiteInteractor mInteractor;

    public SearchSitePresenterImpl(Context context) {
        mDisposable = new CompositeDisposable();
        mInteractor = new SearchSiteInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull SearchSiteView view) {
        mView = view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void watchChanges(@NonNull EditText view) {
        RxTextView.textChanges(view)
                .subscribe(event -> mView.hideError());
    }

    @Override
    public void setupOperator() {
        int index = getOperator().ordinal();
        mView.setOperator(index == 4 ? 0 : index);
    }

    @Override
    public void saveOperator(int operatorIndex) {
        mInteractor.saveOperator(Operator.values()[operatorIndex]);
    }

    private Operator getOperator() {
        return mInteractor.getOperator();
    }

    @Override
    public void showMap(int operatorIndex) {
        saveOperator(operatorIndex);
        mView.openMap();
    }

    @Override
    public void searchSite(int operatorIndex, @NonNull String search) {
        saveOperator(operatorIndex);

        if (search.length() == 0) {
            mView.showError(R.string.error_search_empty);
            return;
        }

        mView.showProgress();
        mDisposable.clear();
        if (Pattern.matches("[0-9-]*", search)) {

            mDisposable.add(mInteractor.searchSitesByNumber(search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::searchSuccess));
        } else {
            mDisposable.add(mInteractor.searchSitesByAddress(search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::searchSuccess));
        }
    }

    private void searchSuccess(List<Site> siteList) {
        if (mView != null) {
            mView.hideProgress();
            int count = siteList.size();
            switch (count) {
                case 0:
                    mView.showError(R.string.error_no_succes);
                    break;
                case 1:
                    mView.toSiteInfo(siteList.get(0));
                    break;
                default:
                    if (count > 50) {
                        mView.showError(R.string.error_many_success);
                        break;
                    } else {
                        mView.toSiteChoice(siteList);
                        break;
                    }
            }
        }
    }

    @Override
    public void unbindView() {
        mView = null;
        mDisposable.dispose();
    }
}