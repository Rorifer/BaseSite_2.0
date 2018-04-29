package com.engineeringforyou.basesite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;

import static com.engineeringforyou.basesite.MapsActivity.operator_lable;

public class ForDelete {

    public static void start(Activity activity, Cursor cursor) {

        cursor.moveToFirst();
        double lat, lng;
        String[] headers =  activity.getResources().getStringArray(R.array.columns);
        String[] text = new String[headers.length];
        for (int i = 0; i < text.length; i++) {

            if (cursor.getColumnIndex(headers[i]) != -1) {
                text[i] = cursor.
                        getString(cursor.getColumnIndex(headers[i]));
            }
            if (text[i] == null || text[i].equals("")) text[i] = "нет данных";
            if (headers[i].equals("SITE")) text[i] = text[i] + " (" + operator_lable + ")";
        }

        lat = cursor.getDouble(cursor.getColumnIndex("GPS_Latitude"));//.replace(',', '.');
        lng = cursor.getDouble(cursor.getColumnIndex("GPS_Longitude"));//.replace(',', '.');
        String site = cursor.getString(cursor.getColumnIndex("SITE"));

        cursor.close();
        Intent intent = new Intent(activity, SiteDetailsActivity.class);
        intent.putExtra("lines", text);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("site", site);
        activity.startActivity(intent);
    }



}
