package com.engineeringforyou.basesite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static String DB_PATH; // полный путь к базе данных
    private static final String DATABASE_NAME ="DB_SITE_06.db";
    private static final int DATABASE_VERSION = 1;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private Dao todoDao;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
        DB_PATH = context.getFilesDir().getPath() + DATABASE_NAME;
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
    }

    public Dao getDao() throws SQLException {
        if(todoDao == null) {
            try {
                todoDao = getDao(BStation.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return todoDao;
    }

    @Override
    public void close(){
        super.close();
        todoDao = null;
    }
}