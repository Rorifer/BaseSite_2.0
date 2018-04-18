package com.engineeringforyou.basesite.presentation.searchsite;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.SiteChoice;
import com.engineeringforyou.basesite.SiteInfo;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenter;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl;
import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;
import com.engineeringforyou.basesite.utils.DBHelper;
import com.engineeringforyou.basesite.utils.KeyBoardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl.DB_OPERATOR_ALL;

public class SearchSiteActivity extends AppCompatActivity implements SearchSiteView {

    @BindView(R.id.site_search)
    EditText mSearch;
    @BindView(R.id.site_search_btn)
    Button mSearchButton;
    @BindView(R.id.operators_group)
    Button mOperators;
    @BindView(R.id.radio_mts)
    Button mMTSradio;
    @BindView(R.id.radio_vmk)
    Button mVMKradio;
    @BindView(R.id.radio_mgf)
    Button mMGFradio;
    @BindView(R.id.radio_tele)
    Button mTELEradio;
    @BindView(R.id.progress_bar)
    Layout mProgress;

    private SearchSitePresenter mPresenter;

    public static String operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mPresenter.searchSite(getSelectedOperator(), mSearch.getText().toString().trim());
    }

    private String getSelectedOperator(){


        return "";//TODO
    }


        public void onClick(View view) {
        operator = spinner.getSelectedItem().toString();
        operatorBD = operatorsMap.get(operator);
        operatorBDinPreferences();

        switch (view.getId()) {
            case R.id.site_search_btn:
                //siteNumber = Integer.parseInt(siteView.getText().toString());
                String siteNumber = (mSearch.getText().toString());
                if (siteNumber.length() == 0) {
                    Toast.makeText(this, "Поле поиска не заполнено", Toast.LENGTH_SHORT).show();
                    break;
                }
                siteData(new DBHelper(getApplicationContext()).
                        siteSearch(operatorBD, siteNumber, 1));
                break;

//            case R.id.site_address_btn:
//                String siteAddress = (siteView2.getText().toString());
//                if (siteAddress.length() == 0) {
//                    Toast.makeText(this, "Поле поиска не заполнено", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                if (siteAddress.trim().equals("")) {
//                    Toast.makeText(this, "Поле поиска заполнено пробелами", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                siteData(new DBHelper(getApplicationContext()).
//                        siteSearch(operatorBD, siteAddress, 2));
//                fireBase ("2","siteAddress", siteAddress+"  "+ operatorBD);
//
//                break;

            case R.id.search_here:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("next", MapsActivity.MAP_BS_HERE);
                intent.putExtra("operatorBD", operatorBD);
                startActivity(intent);
                fireBase("3", "searchHere", operatorBD);
        }
    }

    private void toSiteInfo(Cursor cursor) {
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
            } else {
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

    private void toSiteChoice(Cursor cursor, int count) {
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

    public static String getOperatorBD() {
        if (operatorBD != null) {
            return operatorBD;
        } else {
            operatorBDoutPreferences();
            return operatorBD;
        }
    }

    public static void setOperatorBD(String oper) {
        operatorBD = oper;
        operatorBDinPreferences();
        switch (oper) {
            case DB_OPERATOR_MTS:
                operator = "МТС";
                break;
            case DB_OPERATOR_MGF:
                operator = "МегаФон";
                break;
            case DB_OPERATOR_VMK:
                operator = "Билайн";
                break;
            case DB_OPERATOR_TEL:
                operator = "Теле2";
                break;
            case DB_OPERATOR_ALL:
                operator = "Все";
                break;
            default:
        }
    }

    @OnEditorAction(R.id.site_search)
    public boolean search(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchSite();
            return true;
        }
        return false;
    }
}