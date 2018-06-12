//package com.engineeringforyou.basesite.data.sql;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.engineeringforyou.basesite.R;
//import com.engineeringforyou.basesite.models.Operator;
//import com.engineeringforyou.basesite.models.Site;
//import com.engineeringforyou.basesite.models.Status;
//import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl;
//import com.engineeringforyou.basesite.utils.EventFactory;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import static android.text.TextUtils.isEmpty;
//
//public class DBHelper extends SQLiteOpenHelper {
//    private static String DB_PATH; // полный путь к базе данных
//    final private static String DB_NAME = "DB_SITE_06.db";
//    private static final int DATABASE_VERSION = 1; // версия базы данных
//    private Context myContext;
//
//    public DBHelper(Context context) {
//        super(context, DB_NAME, null, DATABASE_VERSION);
//        this.myContext = context;
//        DB_PATH = context.getFilesDir().getPath() + "/" + DB_NAME;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    }
//
//    private static List<Site> mapToSiteList(Cursor cursor, Operator operator, Context context) {
//        List<Site> list = new ArrayList<>();
//
//        if (cursor == null) {
//            return list;
//        }
//
//        int count = cursor.getCount();
//        if (count == 0) return list;
//
//        cursor.moveToFirst();
//        String[] headers = context.getResources().getStringArray(R.array.columns);
//
//        for (int i = 0; i < count; i++) {
//
//            String obj = cursor.getString(cursor.getColumnIndex(headers[2]));
//            String ADDRES = cursor.getString(cursor.getColumnIndex(headers[1]));
//            if (isEmpty(obj)) obj = "нет данных";
//            if (isEmpty(ADDRES)) ADDRES = "нет данных";
//
//            list.add(new Site(
//                    cursor.getInt(cursor.getColumnIndex("_id")),
//                    operator,
//                    cursor.getString(cursor.getColumnIndex(headers[0])),
//                    cursor.getDouble(cursor.getColumnIndex(headers[3])),
//                    cursor.getDouble(cursor.getColumnIndex(headers[4])),
//                    ADDRES,
//                    obj,
//                    Status.ACTIVE,
//                    "нет данных"));
//            cursor.moveToNext();
//        }
//        cursor.close();
//
//        return list;
//    }
//
//    public List<Site> siteSearch2(Operator operator, String siteQuery, int mode) {
//
//        Cursor cursor = siteSearch(getOperatorBD3(myContext), siteQuery, mode);
//
//
//        return mapToSiteList(cursor, operator, myContext);
//    }
//
//    private Cursor siteSearch(String bDoperatorName, String siteQuery, int mode) {
//
//        if (bDoperatorName == null) {
//            bDoperatorName = getOperatorBD3(myContext);
//        }
//        DBHelper db = null;
//        Cursor userCursor = null;
//        SQLiteDatabase sqld;
//        int count;
//        if (mode == 1) {
//            String query[] = new String[2];
//            query[0] = "SELECT * FROM " + bDoperatorName + " WHERE SITE = '" + siteQuery + "'";
//            query[1] = "SELECT * FROM " + bDoperatorName + " WHERE SITE LIKE '%" + siteQuery + "'";
//
//            db = new DBHelper(this.myContext);
//            db.create_db();
//            sqld = db.open();
//            for (String quer : query) {
//                userCursor = sqld.rawQuery(quer, null);
//                count = userCursor.getCount();
//                if (count != 0) {
//                    break;
//                }
//            }
//        } else if (mode == 2) {
//            siteQuery = siteQuery.replace(',', ' '); // надо?
//            String[] words = siteQuery.split(" ");
//            StringBuilder query = new StringBuilder();
//
//            db = new DBHelper(this.myContext);
//            db.create_db();
//            sqld = db.open();
//            boolean isFirst = true;
//            query.append("SELECT * FROM ").append(bDoperatorName).append(" WHERE ");
//            for (String word : words) {
//                if (word.equals(" ")) continue;
//                if (isFirst) {
//                    query.append("ADDRES LIKE ");
//                    isFirst = false;
//                } else {
//                    query.append("AND ADDRES LIKE ");
//                }
//                query.append("'%").append(word).append("%'");
//            }
//            userCursor = sqld.rawQuery(String.valueOf(query), null);
//
//        } else {
//            if (mode == 3) {
//                String query;
//                query = "SELECT * FROM " + bDoperatorName + " WHERE _ID = " + siteQuery;
//
//                db = new DBHelper(this.myContext);
//                db.create_db();
//                sqld = db.open();
//                userCursor = sqld.rawQuery(query, null);
//            }
//        }
//
//        if (db != null) db.close();
//        return userCursor;
//    }
//
//    private void create_db() {
//        InputStream myInput;
//        OutputStream myOutput;
//        try {
//            File file = new File(DB_PATH);
//            if (!file.exists()) {
//                this.getReadableDatabase();
//                //получаем локальную бд как поток
//                myInput = myContext.getAssets().open(DB_NAME);
//                // Путь к новой бд
//                String outFileName = DB_PATH;
//                // Открываем пустую бд
//                myOutput = new FileOutputStream(outFileName);
//                // побайтово копируем данные
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = myInput.read(buffer)) > 0) {
//                    myOutput.write(buffer, 0, length);
//                }
//                myOutput.flush();
//                myOutput.close();
//                myInput.close();
//            }
//        } catch (IOException ex) {
//            EventFactory.INSTANCE.exception(ex);
//        }
//    }
//
//    private SQLiteDatabase open() throws SQLException {
//        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
//    }
//
//    public List<Site> getAllSites(Operator operator) {
//        Cursor cursor = siteSearchAll(getDbName(operator));
//        return mapToSiteList(cursor, operator, myContext);
//    }
//
//    private Cursor siteSearchAll(String DB_NAME) {
//        DBHelper db;
//        Cursor userCursor;
//        SQLiteDatabase sqld;
//        String query;
//
//        query = "SELECT * FROM " + DB_NAME;
//        db = new DBHelper(this.myContext);
//        db.create_db();
//        sqld = db.open();
//        userCursor = sqld.rawQuery(query, null);
//        db.close();
//        return userCursor;
//    }
//
//    public List<Site> searchSitesByLocation(Operator operator, Double lat, Double lng, int radius) {
//        Cursor cursor = siteSearchLoc(getDbName(operator), lat, lng, (float) radius);
//        return mapToSiteList(cursor, operator, myContext);
//    }
//
//    private Cursor siteSearchLoc(String DB_NAME, Double lat, Double lng, float radius) {
//        DBHelper db;
//        Cursor userCursor;
//        SQLiteDatabase sqld;
//        String query;
//        double latMax, latMin, lngMax, lngMin, latDelta, lngDelta;
//
//        latDelta = radius / 111;
//        lngDelta = radius / 63.2;
//        latMax = lat + latDelta;
//        latMin = lat - latDelta;
//        lngMax = lng + lngDelta;
//        lngMin = lng - lngDelta;
//        query = "SELECT * FROM " + DB_NAME + " WHERE GPS_Latitude>" + latMin + " AND GPS_Latitude<" + latMax +
//                " AND GPS_Longitude>" + lngMin + " AND GPS_Longitude<" + lngMax;
//
//        db = new DBHelper(this.myContext);
//        db.create_db();
//        sqld = db.open();
//        userCursor = sqld.rawQuery(query, null);
//        db.close();
//        return userCursor;
//    }
//
//    private static String getOperatorBD3(Context context) {
//        Operator oper = new SettingsRepositoryImpl(context).getOperator();
//        return getDbName(oper);
//    }
//
//    private static String getDbName(Operator oper) {
//        switch (oper) {
//            case ALL:
//                return DB_OPERATOR_ALL;
//            case MEGAFON:
//                return DB_OPERATOR_MGF;
//            case VIMPELCOM:
//                return DB_OPERATOR_VMK;
//            case TELE2:
//                return DB_OPERATOR_TEL;
//            default:
//            case MTS:
//                return DB_OPERATOR_MTS;
//        }
//    }
//
//    private final static String DB_OPERATOR_MTS = "MTS_Site_Base";
//    private final static String DB_OPERATOR_MGF = "MGF_Site_Base";
//    private final static String DB_OPERATOR_VMK = "VMK_Site_Base";
//    private final static String DB_OPERATOR_TEL = "TELE_Site_Base";
//
//    private final static String DB_OPERATOR_ALL = "ALL_Site_Base";
//}
//
//// https://stackoverflow.com/questions/2528489/no-such-table-android-metadata-whats-the-problem