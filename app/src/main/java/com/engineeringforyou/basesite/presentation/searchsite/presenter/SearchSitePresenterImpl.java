package com.engineeringforyou.basesite.presentation.searchsite.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.View;
import android.widget.Toast;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.SiteChoice;
import com.engineeringforyou.basesite.SiteInfo;
import com.engineeringforyou.basesite.utils.DBHelper;

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






}
