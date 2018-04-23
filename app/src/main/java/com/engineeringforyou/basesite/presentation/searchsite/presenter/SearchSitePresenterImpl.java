package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.widget.EditText;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.domain.settings.SettingsInteractor;
import com.engineeringforyou.basesite.domain.settings.SettingsInteractorImpl;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;
import com.engineeringforyou.basesite.utils.DBHelper;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchSitePresenterImpl implements SearchSitePresenter {

    private Context mContext;
    private SearchSiteView mView;
    private CompositeDisposable mDisposable;
    private SettingsInteractor mSettingsInteractor;

    public SearchSitePresenterImpl(Context context) {
        mContext = context;
        mDisposable = new CompositeDisposable();
        mSettingsInteractor = new SettingsInteractorImpl(mContext);
    }

    @Override
    public void bind(SearchSiteView view) {
        mView = view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void watchChanges(EditText view) {
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
        mSettingsInteractor.saveOperator(Operator.values()[operatorIndex]);
    }

    private Operator getOperator(){
        return  mSettingsInteractor.getOperator();
    }

    @Override
    public void showMap(int operatorIndex) {
        saveOperator(operatorIndex);
        mView.toMap();
    }

    @Override
    public void searchSite(int operatorIndex, String search) {
        saveOperator(operatorIndex);

        if (search.length() == 0) {
            mView.showError(R.string.error_search_empty);
            return;
        }

        mView.showProgress();
        mDisposable.clear();
        if (Pattern.matches("[0-9-]*", search)) {
            mDisposable.add(Single.fromCallable(() ->
                    new DBHelper(mContext).siteSearch(getOperator(), search, 1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::siteData));
        } else {
            mDisposable.add(Single.fromCallable(() ->
                    new DBHelper(mContext).siteSearch(getOperator(), search, 2))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::siteData));
        }
    }

    private void siteData(Cursor cursor) {
        if (mView == null) return;
        if (cursor == null) {
            mView.hideProgress();
            mView.showError(R.string.error);
            return;
        }
        int count;
        count = cursor.getCount();

        switch (count) {
            case 0:
                mView.showError(R.string.error_no_succes);
                break;
            case 1:
                mView.toSiteInfo(cursor);
                break;
            default:
                if (count > 50) {
                    mView.showError(R.string.error_many_success);
                    break;
                } else {
                    if (count > 1) {
                        mView.toSiteChoice(cursor, count);
                        break;
                    } else {
                        mView.showError(R.string.error);
                        break;
                    }
                }
        }
        mView.hideProgress();
    }

    @Override
    public void unbindView() {
        mView = null;
        mDisposable.dispose();
    }
}