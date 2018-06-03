package com.engineeringforyou.basesite.data.orm;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ORMHelperFactory {

    private static ORMHelper databaseHelper;

    public static ORMHelper getHelper(){
        return databaseHelper;
    }

    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, ORMHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
