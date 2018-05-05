package com.engineeringforyou.basesite.utils;

import android.content.Context;

import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl;

public class UtilsForDelete {

    public final static String DB_OPERATOR_MTS = "MTS_Site_Base";
    public final static String DB_OPERATOR_MGF = "MGF_Site_Base";
    public final static String DB_OPERATOR_VMK = "VMK_Site_Base";
    public final static String DB_OPERATOR_TEL = "TELE_Site_Base";
    public final static String DB_OPERATOR_ALL = "ALL_Site_Base";


    public static String getOperatorBD3(Context context) {
//        if (operatorBD != null) {
//            return operatorBD;
//        } else {
        //  operatorBDoutPreferences();
        Operator oper = new SettingsRepositoryImpl(context).getOperator();

        switch (oper) {
            case ALL:
                return DB_OPERATOR_ALL;
            case MEGAFON:
                return DB_OPERATOR_MGF;
            case VIMPELCOM:
                return DB_OPERATOR_VMK;
            case TELE2:
                return DB_OPERATOR_TEL;
            default:
            case MTS:
                return DB_OPERATOR_MTS;

        }
        //     return operatorBD;
        //}
    }

}
