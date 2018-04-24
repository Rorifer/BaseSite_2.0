package com.engineeringforyou.basesite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl;
import com.engineeringforyou.basesite.utils.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.engineeringforyou.basesite.presentation.searchsite.presenter.SearchSitePresenterImpl.operator;

public class SiteChoice extends Activity {
    String[] param1, param2, id;
    ListView listView;

    public static void start(Activity activity, List<Site> list){

    }

    public static void start(Activity activity, Cursor cursor , int count){
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
        Intent intent = new Intent(activity, SiteChoice.class);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);
        intent.putExtra("id", id);
        activity.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_choice);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            param1 = extras.getStringArray("param1");
            param2 = extras.getStringArray("param2");
            id = extras.getStringArray("id");
        }
        listView = findViewById(R.id.sites);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap<String, String> map;
        for (int i = 0; i < param1.length; i++) {
            map = new HashMap<>();
            map.put("param1", param1[i]);
            map.put("param2", param2[i]);
            arrayList.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, arrayList, android.R.layout.simple_list_item_2,
                new String[]{"param1", "param2"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.v("LogForMe", "position choice = " + pos);
                Log.v("LogForMe", "id[pos] = " + id[pos]);
                Log.v("LogForMe", "String.valueOf(id[pos]) = " + String.valueOf(id[pos]));
                Cursor cursor = new DBHelper
                        (getApplicationContext()).
                        siteSearch
                                (SearchSitePresenterImpl.getOperatorBD(), String.valueOf(id[pos]), 3);
                if (cursor == null || cursor.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "Ошибка в БД", Toast.LENGTH_SHORT).show();
                    Log.v("LogForMe", "Ошибка в Курсоре ");
                    return;
                }

                double lat, lng;
                cursor.moveToFirst();
                String[] headers = getResources().getStringArray(R.array.columns);
                String[] text = new String[headers.length];
                for (int i = 0; i < text.length; i++) {
                    Log.v("LogForMe", i + " " + headers[i]);

                    if (cursor.getColumnIndex(headers[i]) != -1) {
                        text[i] = cursor.
                                getString(cursor.getColumnIndex(headers[i]));
                        Log.v("LogForMe", i + " " + text[i]);
                    } else {
                        Log.v("LogForMe", "Колонки не существует -" + headers[i]);
                    }
                    if (text[i] == null||text[i].equals("") ) text[i] = "нет данных";
                    if (headers[i].equals("SITE")) text[i] = text[i] + " (" + SearchSitePresenterImpl.operator + ")";
                }
                lat = cursor.getDouble(cursor.getColumnIndex("GPS_Latitude"));//.replace(',', '.');
                lng = cursor.getDouble(cursor.getColumnIndex("GPS_Longitude"));//.replace(',', '.');
                String site = cursor.getString(cursor.getColumnIndex("SITE"));
                Log.v("LogForMe", "SITE  ==" + site);
                cursor.close();
                Intent intent = new Intent(getApplicationContext(), SiteInfo.class);
                intent.putExtra("lines", text);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("site", site);
                startActivity(intent);
            }
        });
    }
}