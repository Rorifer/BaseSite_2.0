package com.engineeringforyou.basesite.presentation.searchsite;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.SiteChoice;
import com.engineeringforyou.basesite.SiteInfo;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenter;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl;
import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;

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
        setContentView(R.layout.activity_search_site);
        ButterKnife.bind(this);
        mPresenter = new SearchSitePresenterImpl(this);
        mPresenter.bind(this);
        mPresenter.watchChanges(mSearch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.setupOperator();
    }

    @OnClick(R.id.site_search_btn)
    public void searchSite() {
//        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
        mPresenter.saveOperator(getOperator());
        mPresenter.searchSite(mSearch.getText().toString().trim());
    }

    private int getOperator() {
        return mOperators.indexOfChild(findViewById(mOperators.getCheckedRadioButtonId()));
    }

    @Override
    public void setOperator(int operatorIndex) {
        mOperators.check(mOperators.getChildAt(operatorIndex).getId());
    }

    @OnEditorAction(R.id.site_search)
    public boolean search(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchSite();
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
    public void showResult(@StringRes int textRes) {
        //TODO
        Toast.makeText(this, getString(textRes), Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindView();
    }

    @Override
    public void toSiteInfo(Cursor cursor) {
        SiteInfo.start(this, cursor);
    }

    @Override
    public void toSiteChoice(Cursor cursor, int count) {
        SiteChoice.start(this, cursor, count);
    }

    @OnClick(R.id.search_in_map)
    public void toMapActivity() {
        mPresenter.saveOperator(getOperator());
        MapsActivity.start(this);
    }
}