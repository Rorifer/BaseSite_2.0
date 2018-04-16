package com.engineeringforyou.basesite.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.engineeringforyou.basesite.MapsActivity;
import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.SiteChoice;
import com.engineeringforyou.basesite.SiteInfo;
import com.engineeringforyou.basesite.utils.DBHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private static SharedPreferences mSettings;
    private EditText siteView1, siteView2;
    private Spinner spinner;
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

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("LogForMe", "________________Запуск MainActivity");
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        siteView1 = findViewById(R.id.siteNumber);
        siteView2 = findViewById(R.id.siteAddress);
        spinner = findViewById(R.id.operators);

        siteView1.setOnEditorActionListener(this);
        siteView2.setOnEditorActionListener(this);

        operatorsMap = new HashMap<>(3);
        operatorsMap.put(getString(R.string.MTS), DB_OPERATOR_MTS);
        operatorsMap.put(getString(R.string.MGF), DB_OPERATOR_MGF);
        operatorsMap.put(getString(R.string.VMK), DB_OPERATOR_VMK);
        operatorsMap.put(getString(R.string.TELE), DB_OPERATOR_TEL);
    }

    public void onClick(View view) {
        operator = spinner.getSelectedItem().toString();
        Log.v("LogForMe", "Выбран spinner - " + operator);
        operatorBD = operatorsMap.get(operator);
        Log.v("LogForMe", "Выбрана база данных - " + operatorBD);
        operatorBDinPreferences();

        switch (view.getId()) {
            case R.id.button:
                //siteNumber = Integer.parseInt(siteView.getText().toString());
                String siteNumber = (siteView1.getText().toString());
                Log.v("LogForMe", "siteNumber = -" + siteNumber + "-");
                if (siteNumber.length() == 0) {
                    Toast.makeText(this, "Поле поиска не заполнено", Toast.LENGTH_SHORT).show();
                    break;
                }
                siteData(new DBHelper(getApplicationContext()).
                        siteSearch(operatorBD, siteNumber, 1));
                fireBase ("1","siteNumber", siteNumber+"  "+ operatorBD);
                break;

            case R.id.button2:
                String siteAddress = (siteView2.getText().toString());
                Log.v("LogForMe", "siteAddress= -" + siteAddress + "+");
                if (siteAddress.length() == 0) {
                    Toast.makeText(this, "Поле поиска не заполнено", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (siteAddress.trim().equals("")) {
                    Toast.makeText(this, "Поле поиска заполнено пробелами", Toast.LENGTH_SHORT).show();
                    break;
                }
                siteData(new DBHelper(getApplicationContext()).
                        siteSearch(operatorBD, siteAddress, 2));
                fireBase ("2","siteAddress", siteAddress+"  "+ operatorBD);

                break;

            case R.id.searchHere:
                Log.v("LogForMe", "btnSearchNear");
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("next", MapsActivity.MAP_BS_HERE);
                intent.putExtra("operatorBD", operatorBD);
                startActivity(intent);
                fireBase ("3","searchHere", operatorBD);
        }
    }

    private void siteData(Cursor cursor) {
        if (cursor == null) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            Log.v("LogForMe", "Ошибка в Курсоре ");
            return;
        }
        int count;
        count = cursor.getCount();
        Log.v("LogForMe", "Пришло Количество строк совпадений = " + count);

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
                        Log.v("LogForMe", "Много совпадений сайтов");
                        break;
                    } else {
                        Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
        }
        Log.v("LogForMe", "Конец siteData");
    }

    private void toSiteInfo(Cursor cursor) {
        if (cursor == null) {
            Log.v("LogForMe", "NULL -1");
            return;
        }
        cursor.moveToFirst();
        double lat, lng;
        String[] headers = getResources().getStringArray(R.array.columns);
        String[] text = new String[headers.length];
        Log.v("LogForMe", "headers.length " + headers.length);
        Log.v("LogForMe", "text.length " + text.length);
        for (int i = 0; i < text.length; i++) {
            Log.v("LogForMe", i + " " + headers[i]);

            if (cursor.getColumnIndex(headers[i]) != -1) {
                text[i] = cursor.
                        getString(cursor.getColumnIndex(headers[i]));
                Log.v("LogForMe", i + " " + text[i]);
            } else {
                Log.v("LogForMe", "Колонки не существует -" + headers[i]);
            }
            if (text[i] == null || text[i].equals("")) text[i] = "нет данных";
            if (headers[i].equals("SITE")) text[i] = text[i] + " (" + operator + ")";
        }

        lat = cursor.getDouble(cursor.getColumnIndex("GPS_Latitude"));//.replace(',', '.');
        lng = cursor.getDouble(cursor.getColumnIndex("GPS_Longitude"));//.replace(',', '.');
        String site = cursor.getString(cursor.getColumnIndex("SITE"));

        Log.v("LogForMe", " SITE  ==" + site);
        Log.v("LogForMe", "lat  ==" + lat);
        Log.v("LogForMe", "lng  ==" + lng);
        cursor.close();
        Log.v("LogForMe", "Вся БД закрылась-2");
        Intent intent = new Intent(this, SiteInfo.class);
        intent.putExtra("lines", text);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("site", site);
        startActivity(intent);
    }

    private void toSiteChoice(Cursor cursor, int count) {
        Log.v("LogForMe", "MainSactivity toSiteChoice. count= " + count);
        if (cursor == null) {
            Log.v("LogForMe", "NULL-10");
            return;
        }
        cursor.moveToFirst();
        String[] headers = new String[]{"SITE", "Addres"};
//        String[] headers = getResources().getStringArray(R.array.columnsChoice);
        String[] param1 = new String[count];
        String[] param2 = new String[count];
        String[] id = new String[count];
        Log.v("LogForMe", "Choice.headers.length " + headers.length);
        for (int i = 0; i < count; i++) {
            param1[i] = cursor.getString(cursor.getColumnIndex(headers[0])) + " (" + operator + ")";
            param2[i] = cursor.getString(cursor.getColumnIndex(headers[1]));
            id[i] = cursor.getString(cursor.getColumnIndex("_id"));
            Log.v("LogForMe", i + "id[i] " + id[i]);
            Log.v("LogForMe", i + "param1[i] " + param1[i]);
            Log.v("LogForMe", i + "param2[i] " + param2[i]);
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

    private static void operatorBDinPreferences() {
        if (mSettings == null) {return;}
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_BD, operatorBD);
        editor.apply();
        Log.v("LogForMe", "БД оператора в настройки:  " + operatorBD);
    }

    static void operatorBDoutPreferences() {
        if (mSettings == null) {return;}
        if (mSettings.contains(APP_PREFERENCES_BD)) {
            operatorBD = mSettings.getString(APP_PREFERENCES_BD, DB_OPERATOR_MTS);
            Log.v("LogForMe", "БД оператора из настроек:  " + operatorBD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        operatorBDoutPreferences();

        switch (operatorBD) {
            case DB_OPERATOR_MTS:
                spinner.setSelection(0);
                break;
            case DB_OPERATOR_VMK:
                spinner.setSelection(1);
                break;
            case DB_OPERATOR_MGF:
                spinner.setSelection(2);
                break;
            case DB_OPERATOR_TEL:
                spinner.setSelection(3);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.v("LogForMe", "onEditorAction  " + actionId);
        switch (v.getId()) {
            case R.id.siteAddress:
                onClick(findViewById(R.id.button2));
                return true;
            case R.id.siteNumber:
                onClick(findViewById(R.id.button));
                return true;
            default:
                return false;
        }
    }

    void fireBase(String id, String name, String message ) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, message);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}