package com.engineeringforyou.basesite.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static com.engineeringforyou.basesite.MapsActivity.getOperatorBD3;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH; // полный путь к базе данных
    final private static String DB_NAME = "DB_SITE_06.db";
    private static final int DATABASE_VERSION = 1; // версия базы данных
    private Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
        Log.v("LogForMe", "Создание экземпляра БД");
        Log.v("LogForMe", "ПУТЬ  к БД = " + DB_PATH);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("LogForMe", "Попытка создать БД");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("LogForMe", "Попытка обновить БД");
    }

    public static List<Site> mapToSiteList(Cursor cursor, Operator operator, Context context) {
        List<Site> list = new ArrayList<>();

        if (cursor == null) {
            return list;
        }

        int count = cursor.getCount();
        if (count == 0) return list;

        cursor.moveToFirst();
        String[] headers = context.getResources().getStringArray(R.array.columns);

        String obj = cursor.getString(cursor.getColumnIndex(headers[2]));
        String adres = cursor.getString(cursor.getColumnIndex(headers[1]));
        if (isEmpty(obj)) obj = "нет данных";
        if (isEmpty(adres)) adres = "нет данных";

        for (int i = 0; i < count; i++) {
            list.add(new Site(
                    cursor.getString(cursor.getColumnIndex("_id")),
                    operator,
                    cursor.getString(cursor.getColumnIndex(headers[0])),
                    cursor.getDouble(cursor.getColumnIndex(headers[3])),
                    cursor.getDouble(cursor.getColumnIndex(headers[4])),
                    adres,
                    obj,
                    Status.ACTIVE,
                    "нет данных"));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<Site> siteSearch2(Operator operator, String siteQuery, int mode) {

        Cursor cursor = siteSearch(getOperatorBD3(myContext), siteQuery, mode);


        return mapToSiteList(cursor, operator, myContext);
    }

    //       public Cursor siteSearch(Operator bDoperatorName, String siteQuery, int mode) {
    //  public List<Site> siteSearch(Operator bDoperatorName, String siteQuery, int mode) {
    public Cursor siteSearch(String bDoperatorName, String siteQuery, int mode) {

        if (bDoperatorName == null) {
            Log.v("LogForMe", "В siteSearch передали пустую bDoperatorName, siteQuery = " + siteQuery);
            bDoperatorName = getOperatorBD3(myContext);
        }
        DBHelper db = null;
        Cursor userCursor = null;
        SQLiteDatabase sqld;
        int count = 0;
        if (mode == 1) {
            Log.v("LogForMe", "Запрос mode 1");
            String query[] = new String[2];
            query[0] = "SELECT * FROM " + bDoperatorName + " WHERE SITE = '" + siteQuery + "'";
            //     query[1] = "SELECT * FROM " + bDoperatorName + " WHERE SITE = '77-" + siteQuery + "'";
            //    query[2] = "SELECT * FROM " + bDoperatorName + " WHERE SITE LIKE '77-" + siteQuery + "%'";
            //  query[1] = "SELECT * FROM " + bDoperatorName + " WHERE SITE LIKE '77-%" + siteQuery + "'";
            query[1] = "SELECT * FROM " + bDoperatorName + " WHERE SITE LIKE '%" + siteQuery + "'";
            //      query[4] = "SELECT * FROM " + bDoperatorName + " WHERE SITE LIKE '%" + siteQuery + "%'";
            Log.v("LogForMe", query[0]);
            Log.v("LogForMe", query[1]);
//            Log.v("LogForMe", query[2]);
//            Log.v("LogForMe", query[3]);
//            Log.v("LogForMe", query[4]);
            // Работа с БД
            db = new DBHelper(this.myContext);
            db.create_db();
            sqld = db.open();
            for (String quer : query) {
                Log.v("LogForMe", "Поиск в курсоре");
                userCursor = sqld.rawQuery(quer, null);
                count = userCursor.getCount();
                if (count != 0) {
                    break;
                }
            }
        } else if (mode == 2) {
            Log.v("LogForMe", "Запрос mode 2");
            siteQuery = siteQuery.replace(',', ' ');
            String[] words = siteQuery.split(" ");
            Log.v("LogForMe", Arrays.toString(words));
            StringBuilder query = new StringBuilder();
            // Работа с БД
            db = new DBHelper(this.myContext);
            db.create_db();
            sqld = db.open();
            boolean isFirst = true;
            query.append("SELECT * FROM ").append(bDoperatorName).append(" WHERE ");
            for (String word : words) {
                if (word.equals(" ")) continue;
                if (isFirst) {
                    query.append("ADDRES LIKE ");
                    isFirst = false;
                } else {
                    query.append("AND ADDRES LIKE ");
                }
                query.append("'%").append(word).append("%'");
            }
            userCursor = sqld.rawQuery(String.valueOf(query), null);
            count = userCursor.getCount();
            Log.v("LogForMe", "Количество текстовых совпадений = " + count);
        } else {
            if (mode == 3) {
                Log.v("LogForMe", "Запрос mode 3");
                String query;
                query = "SELECT * FROM " + bDoperatorName + " WHERE _ID = " + siteQuery;
                Log.v("LogForMe", "Запрос по mode 3 =" + query);
                db = new DBHelper(this.myContext);
                db.create_db();
                sqld = db.open();
                userCursor = sqld.rawQuery(query, null);
                count = userCursor.getCount();
                Log.v("LogForMe", "Количество текстовых совпадений по id = " + count);
            }
        }

        Log.v("LogForMe", "Количество строк совпадений = " + count);
//        assert db != null;
        if (db != null) db.close();
        // userCursor.close();
        Log.v("LogForMe", "Вся БД закрылась-3");
        return userCursor;
    }

    public void create_db() {
        InputStream myInput;
        OutputStream myOutput;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;
                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);
                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException ex) {
            Log.v("LogForMe", ex.getMessage());
        }
    }

    public SQLiteDatabase open() throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }
}

// https://stackoverflow.com/questions/2528489/no-such-table-android-metadata-whats-the-problem