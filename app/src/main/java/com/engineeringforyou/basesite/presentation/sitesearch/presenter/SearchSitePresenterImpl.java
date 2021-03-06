package com.engineeringforyou.basesite.presentation.sitesearch.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.domain.sitesearch.SearchSiteInteractor;
import com.engineeringforyou.basesite.domain.sitesearch.SearchSiteInteractorImpl;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitesearch.views.SearchSiteView;
import com.engineeringforyou.basesite.utils.EventFactory;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchSitePresenterImpl implements SearchSitePresenter {

    public static String PATTERN_NUMBER_SITE = "[0-9MmSsOo-]*";

    private SearchSiteView mView;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private CompositeDisposable mDisposableView = new CompositeDisposable();
    private CompositeDisposable mDisposableRefresh = new CompositeDisposable();
    private SearchSiteInteractor mInteractor;

    public SearchSitePresenterImpl(Context context) {
        mInteractor = new SearchSiteInteractorImpl(context);
    }

    @Override
    public void bind(@NonNull SearchSiteView view) {
        mView = view;
    }

    @Override
    public void watchChanges(@NonNull EditText view) {
        mDisposableView.add(RxTextView.textChanges(view)
                .subscribe(event -> mView.hideError()));
    }

    @Override
    public void onResume() {
        int index = getOperator().ordinal();
        mView.setOperator(index == 4 ? 0 : index);
        refreshSiteBase();
    }

    private void refreshSiteBase() {
        mDisposableRefresh.add(mInteractor.refreshSiteBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(s -> mView.showSync(true))
                .doOnEvent(throwable -> {
                    mView.showSync(false);
                    showFunctionJobIfNeed();
                })
                .subscribe(() -> {
                }, EventFactory.INSTANCE::exception));
    }

    private void showFunctionJobIfNeed() {
        mDisposableRefresh.add(mInteractor.needingShowJobFunction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((show) -> {
                    if (show && mView != null) mView.showFunctionJob();
                }, EventFactory.INSTANCE::exception));
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
        mView.openMap(Operator.values()[operatorIndex]);
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
        if (Pattern.matches(PATTERN_NUMBER_SITE, search)) {

            mDisposable.add(mInteractor.searchSitesByNumber(search, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::searchSuccess, this::searchError));
        } else {
            mDisposable.add(mInteractor.searchSitesByAddress(search, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::searchSuccess, this::searchError));
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

    private void searchError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
        if (mView != null) {
            mView.hideProgress();
            mView.showError(R.string.error);
        }
    }

    @Override
    public void showInfo() {
        mView.showProgress();
        mDisposable.add(mInteractor.getInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::infoSuccess, this::infoError));
    }

    @Override
    public void messageForDeveloper() {
        mView.openMessageForDeveloper();
    }

    private void infoSuccess(String informationText) {
        if (mView != null) {
            mView.showInformation(informationText);
            mView.hideProgress();
        }
    }

    private void infoError(Throwable throwable) {
        EventFactory.INSTANCE.exception(throwable);
        if (mView != null) {
            mView.hideProgress();
            mView.showInformation(R.string.error);
        }
    }

    @Override
    public void unbindView() {
        mView = null;
        mDisposable.dispose();
        mDisposableView.dispose();
        mDisposableRefresh.dispose();
    }
}