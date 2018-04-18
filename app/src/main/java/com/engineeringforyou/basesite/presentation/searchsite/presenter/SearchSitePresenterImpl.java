package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import com.engineeringforyou.basesite.R;

import java.util.HashMap;
import java.util.Map;

public class SearchSitePresenterImpl implements SearchSitePresenter {

    private static SharedPreferences mSettings;

    private Map<String, String> operatorsMap = new HashMap<>(3);
    public final static String DB_OPERATOR_MTS = "MTS_Site_Base";
    public final static String DB_OPERATOR_MGF = "MGF_Site_Base";
    public final static String DB_OPERATOR_VMK = "VMK_Site_Base";
    public final static String DB_OPERATOR_TEL = "TELE_Site_Base";
    public final static String DB_OPERATOR_ALL = "ALL_Site_Base";

    public static String operator;
    private static String operatorBD = DB_OPERATOR_MTS;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_BD = "nameBD";


    public void init(){

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        operatorsMap = new HashMap<>(3);
        operatorsMap.put(getString(R.string.MTS), DB_OPERATOR_MTS);
        operatorsMap.put(getString(R.string.MGF), DB_OPERATOR_MGF);
        operatorsMap.put(getString(R.string.VMK), DB_OPERATOR_VMK);
        operatorsMap.put(getString(R.string.TELE), DB_OPERATOR_TEL);

    }

    @Override
    public void checkSavedOperator() {
        //        operatorBDoutPreferences();

//        switch (operatorBD) {
//            case DB_OPERATOR_MTS:
//                spinner.setSelection(0);
//                break;
//            case DB_OPERATOR_VMK:
//                spinner.setSelection(1);
//                break;
//            case DB_OPERATOR_MGF:
//                spinner.setSelection(2);
//                break;
//            case DB_OPERATOR_TEL:
//                spinner.setSelection(3);
//                break;
//        }
    }


    private void siteData(Cursor cursor) {
        if (cursor == null) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            return;
        }
        int count;
        count = cursor.getCount();

        switch (count) {
            case 0:
                Toast.makeText(this, "Совпадений не найдено", Toast.LENGTH_LONG).show();
                break;
            case 1:
                toSiteInfo(cursor);
                break;
            default:
                if (count > 50) {
                    Toast.makeText(this, "Слишком много совпадений. Уточните запрос", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    if (count > 1) {
                        toSiteChoice(cursor, count);
                        Toast.makeText(this, "Количество  совпадений = " + count, Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
        }
    }

    private static void operatorBDinPreferences() {
        if (mSettings == null) {return;}
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_BD, operatorBD);
        editor.apply();
    }

    static void operatorBDoutPreferences() {
        if (mSettings == null) {return;}
        if (mSettings.contains(APP_PREFERENCES_BD)) {
            operatorBD = mSettings.getString(APP_PREFERENCES_BD, DB_OPERATOR_MTS);
        }
    }







}
