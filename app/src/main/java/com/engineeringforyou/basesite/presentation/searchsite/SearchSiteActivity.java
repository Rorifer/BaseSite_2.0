package com.engineeringforyou.basesite.presentation.searchsite;

import android.content.Intent;
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
import com.engineeringforyou.basesite.utils.KeyBoardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl.operator;
import static com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl.operatorBD;


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
        KeyBoardUtils.hideKeyboard(this, getCurrentFocus());
        mPresenter.searchSite(getOperator(), mSearch.getText().toString().trim());
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
        Toast.makeText(this,getString(textRes),Toast.LENGTH_SHORT).show();
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
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        double lat, lng;
        String[] headers = getResources().getStringArray(R.array.columns);
        String[] text = new String[headers.length];
        for (int i = 0; i < text.length; i++) {

            if (cursor.getColumnIndex(headers[i]) != -1) {
                text[i] = cursor.
                        getString(cursor.getColumnIndex(headers[i]));
            }
            if (text[i] == null || text[i].equals("")) text[i] = "нет данных";
            if (headers[i].equals("SITE")) text[i] = text[i] + " (" + operator + ")";
        }

        lat = cursor.getDouble(cursor.getColumnIndex("GPS_Latitude"));//.replace(',', '.');
        lng = cursor.getDouble(cursor.getColumnIndex("GPS_Longitude"));//.replace(',', '.');
        String site = cursor.getString(cursor.getColumnIndex("SITE"));

        cursor.close();
        Intent intent = new Intent(this, SiteInfo.class);
        intent.putExtra("lines", text);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("site", site);
        startActivity(intent);
    }

    @Override
    public void toSiteChoice(Cursor cursor, int count) {
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        String[] headers = new String[]{"SITE", "Addres"};
//        String[] headers = getResources().getStringArray(R.array.columnsChoice);
        String[] param1 = new String[count];
        String[] param2 = new String[count];
        String[] id = new String[count];
        for (int i = 0; i < count; i++) {
            param1[i] = cursor.getString(cursor.getColumnIndex(headers[0])) + " (" + operator + ")";
            param2[i] = cursor.getString(cursor.getColumnIndex(headers[1]));
            id[i] = cursor.getString(cursor.getColumnIndex("_id"));
            cursor.moveToNext();
        }
        cursor.close();
        Intent intent = new Intent(this, SiteChoice.class);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    @OnClick(R.id.search_in_map)
    public void toMapActivity() {
//        startActivity(new Intent(this, MapsActivity.class));
//        //TODO добавить анимацию

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("next", MapsActivity.MAP_BS_HERE);
        intent.putExtra("operatorBD", operatorBD);
        startActivity(intent);
    }
}