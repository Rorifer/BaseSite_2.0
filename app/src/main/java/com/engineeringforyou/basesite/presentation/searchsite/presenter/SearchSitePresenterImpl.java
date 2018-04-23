package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.EditText;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView;
import com.engineeringforyou.basesite.utils.DBHelper;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchSitePresenterImpl implements SearchSitePresenter {

    private static SharedPreferences mSettings;

    //    private Map<String, String> operatorsMap = new HashMap<>(3);
    private List<String> operatorsList = new ArrayList<>();

    public final static String DB_OPERATOR_MTS = "MTS_Site_Base";
    public final static String DB_OPERATOR_MGF = "MGF_Site_Base";
    public final static String DB_OPERATOR_VMK = "VMK_Site_Base";
    public final static String DB_OPERATOR_TEL = "TELE_Site_Base";
    public final static String DB_OPERATOR_ALL = "ALL_Site_Base";

    public static String operator;
    public static String operatorBD = DB_OPERATOR_MTS;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_BD = "nameBD";

    private SearchSiteView mView;
    private Context mContext;
    private CompositeDisposable mDisposable;

    public SearchSitePresenterImpl(Context context) {
        mContext = context;
    }

    @Override
    public void bind(SearchSiteView view) {
        mView = view;
        init();
    }

    @SuppressLint("CheckResult")
    @Override
    public void watchChanges(EditText view) {
        RxTextView.textChanges(view)
                .subscribe(event -> mView.hideError());
    }

    private void init() {
        mSettings = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
//        operatorsMap = new HashMap<>(3);
//        operatorsMap.put(mContext.getString(R.string.MTS), DB_OPERATOR_MTS);
//        operatorsMap.put(mContext.getString(R.string.MGF), DB_OPERATOR_MGF);
//        operatorsMap.put(mContext.getString(R.string.VMK), DB_OPERATOR_VMK);
//        operatorsMap.put(mContext.getString(R.string.TELE), DB_OPERATOR_TEL);
//
        operatorsList.add(DB_OPERATOR_MTS);
        operatorsList.add(DB_OPERATOR_MGF);
        operatorsList.add(DB_OPERATOR_VMK);
        operatorsList.add(DB_OPERATOR_TEL);

        mDisposable = new CompositeDisposable();
    }


    @Override
    public void setupOperator() {
        operatorBDoutPreferences();

        switch (operatorBD) {
            case DB_OPERATOR_MGF:
                mView.setOperator(1);
                break;
            case DB_OPERATOR_VMK:
                mView.setOperator(2);
                break;
            case DB_OPERATOR_TEL:
                mView.setOperator(3);
                break;
            default:
            case DB_OPERATOR_MTS:
                mView.setOperator(0);
                break;
        }
    }

    @Override
    public void saveOperator(int operatorIndex) {
        setOperatorBD(operatorsList.get(operatorIndex));
    }

    @Override
    public void searchSite(String search) {

        if (search.length() == 0) {
            mView.showError(R.string.error_search_empty);
            return;
        }

        mView.showProgress();
        mDisposable.clear();

        if (Pattern.matches("[0-9-]*", search)) {
            DBHelper hh = new DBHelper(mContext.getApplicationContext());

            mDisposable.add(Single.fromCallable(() -> new DBHelper(mContext.getApplicationContext()).siteSearch(operatorBD, search, 1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::siteData));


//            siteData(new DBHelper(mContext.getApplicationContext()).
//                    siteSearch(operatorBD, search, 1));
        } else {

            mDisposable.add(Single.fromCallable(() -> new DBHelper(mContext.getApplicationContext()).siteSearch(operatorBD, search, 2))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::siteData));


//            siteData(new DBHelper(mContext.getApplicationContext()).
//                    siteSearch(operatorBD, search, 2));
        }

//        mView.hideProgress();
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

    private static void operatorBDinPreferences() {
        if (mSettings == null) {
            return;
        }
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_BD, operatorBD);
        editor.apply();
        Log.v("LogForMe", "in = " + operatorBD);

    }

    static void operatorBDoutPreferences() {
        if (mSettings == null) {
            return;
        }
        if (mSettings.contains(APP_PREFERENCES_BD)) {
            operatorBD = mSettings.getString(APP_PREFERENCES_BD, DB_OPERATOR_MTS);
            Log.v("LogForMe", "out = " + operatorBD);
        }
    }

    @Override
    public void unbindView() {
        mView = null;
        mDisposable.dispose();
    }
}
