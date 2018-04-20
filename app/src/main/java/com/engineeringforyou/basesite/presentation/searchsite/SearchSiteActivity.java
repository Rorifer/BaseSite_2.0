package com.engineeringforyou.basesite.presentation.searchsite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenter;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl;
import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;
import com.engineeringforyou.basesite.utils.KeyBoardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


public class SearchSiteActivity extends AppCompatActivity implements SearchSiteView {

    @BindView(R.id.site_search)
    EditText mSearch;
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
        mPresenter = new SearchSitePresenterImpl();
        mPresenter.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.checkSavedOperator();
    }

    @OnClick(R.id.site_search_btn)
    public void searchSite() {
        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
        mPresenter.searchSite(getOperator(), mSearch.getText().toString().trim());
    }

    @OnClick(R.id.search_in_map)
    public void toMapActivity() {
        startActivity(new Intent(this, MapsActivity.class));
        //TODO добавить анимацию
    }

    private int getOperator() {
        return mOperators.indexOfChild(findViewById(mOperators.getCheckedRadioButtonId()));
    }

    @Override
    public void setOperator(int operatorIndex) {
        int index = (operatorIndex < mOperators.getChildCount()) ? operatorIndex : 0;
        mOperators.check(mOperators.getChildAt(index).getId());
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
}