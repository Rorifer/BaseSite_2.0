package com.engineeringforyou.basesite.presentation.sitesearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitemap.MapActivity;
import com.engineeringforyou.basesite.presentation.sitesearch.presenter.SearchSitePresenter;
import com.engineeringforyou.basesite.presentation.sitesearch.presenter.SearchSitePresenterImpl;
import com.engineeringforyou.basesite.presentation.sitesearch.views.SearchSiteView;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
import com.engineeringforyou.basesite.presentation.sitelist.SiteListActivity;
import com.engineeringforyou.basesite.utils.KeyBoardUtils;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


public class SearchSiteActivity extends AppCompatActivity implements SearchSiteView {

    @BindView(R.id.site_search)
    EditText mSearch;
    @BindView(R.id.site_search_layout)
    TextInputLayout mSearchLayout;
    @BindView(R.id.operators_group)
    RadioGroup mOperators;
    @BindView(R.id.progress_bar)
    FrameLayout mProgress;

    private SearchSitePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_search);
        ButterKnife.bind(this);
        mPresenter = new SearchSitePresenterImpl(this);
        mPresenter.bind(this);
        mPresenter.watchChanges(mSearch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        hideProgress();
    }

    @OnClick(R.id.button_search)
    public void clickSearchBtn() {
        mPresenter.searchSite(getOperatorIndex(), mSearch.getText().toString().trim());
    }

    @OnClick(R.id.button_search_in_map)
    public void clickMapBtn() {
        mPresenter.showMap(getOperatorIndex());
    }

    private int getOperatorIndex() {
        return mOperators.indexOfChild(findViewById(mOperators.getCheckedRadioButtonId()));
    }

    @Override
    public void setOperator(int operatorIndex) {
        mOperators.check(mOperators.getChildAt(operatorIndex).getId());
    }

    @OnEditorAction(R.id.site_search)
    public boolean search(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clickSearchBtn();
            return true;
        }
        return false;
    }

    @Override
    public void showError(int textRes) {
        mSearchLayout.setError(getString(textRes));
    }

    @Override
    public void hideError() {
        mSearchLayout.setError(null);
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void toSiteInfo(@NonNull Site site) {
        hideKeyboard();
        SiteDetailsActivity.start(this, site);
    }


//    @Override
//    public void toSiteChoice(@NotNull List<? extends Site> list) {
//        hideKeyboard();
//        SiteListActivity.startForResult(this, list);
//    }
    @Override
    public void toSiteChoice( List<? extends Site> list) {
        hideKeyboard();
        SiteListActivity.start(this, list);
    }

    @Override
    public void openMap(@NonNull Operator operator) {
        showProgress();
        hideKeyboard();
        MapActivity.start(this, null);
    }

    private void hideKeyboard() {
        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindView();
    }
}