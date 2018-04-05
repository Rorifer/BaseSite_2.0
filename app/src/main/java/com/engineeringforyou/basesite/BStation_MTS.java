package com.engineeringforyou.basesite;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MTS_Site_Base")
public class BStation_MTS extends BStation {

    public final static String SITE_NAME = "site";

    @DatabaseField(generatedId = true)
    private int _id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = SITE_NAME)
    private String site;

    @DatabaseField(dataType = DataType.DOUBLE)
    private double GPS_Latitude;

    @DatabaseField(dataType = DataType.DOUBLE)
    private double GPS_Longitude;

    @DatabaseField(dataType = DataType.STRING)
    private String addres;

    public BStation_MTS(){
//        ArrayList BStationList = new ArrayList<BStation>();
    }
}
