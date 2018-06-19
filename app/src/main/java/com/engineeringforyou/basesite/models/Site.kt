package com.engineeringforyou.basesite.models

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

//@DatabaseTable(tableName = "Sites_Base")
//@DatabaseTable(tableName = "TELE_Site_Base")
open class Site(
        @DatabaseField(generatedId = true, columnName = "_id")
        val id: Int? = null,

        open val operator: Operator? = null,

        @DatabaseField(columnName = "SITE")
        val number: String? = null,

        @DatabaseField(columnName = "GPS_Latitude")
        val latitude: Double? = null,

        @DatabaseField(columnName = "GPS_Longitude")
        val longitude: Double? = null,

        @DatabaseField(columnName = "Addres")
        val address: String = "нет данных",

        @DatabaseField(columnName = "Object")
        val obj: String = "нет данных",

        @DatabaseField
        val uid: String? = null,

        val status: Status = Status.ACTIVE,

        val timestamp: Long? = null,

        val userAndroidId: String? = null

) : Parcelable {
    constructor() : this(999999999)

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader)?.let { Operator.values()[it as Int] },
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            source.readString(),
            Status.values()[source.readInt()],
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeValue(operator?.ordinal)
        writeString(number)
        writeValue(latitude)
        writeValue(longitude)
        writeString(address)
        writeString(obj)
        writeString(uid)
        writeInt(status.ordinal)
        writeValue(timestamp)
        writeString(userAndroidId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Site> = object : Parcelable.Creator<Site> {
            override fun createFromParcel(source: Parcel): Site = Site(source)
            override fun newArray(size: Int): Array<Site?> = arrayOfNulls(size)
        }
    }
}

@DatabaseTable(tableName = "MTS_Site_Base")
class SiteMTS : Site {
    constructor() : super(operator = Operator.MTS)
    constructor(site: Site) : super(site.id, Operator.MTS, site.number, site.latitude, site.longitude,
            site.address, site.obj, site.uid, site.status, site.timestamp, site.userAndroidId)
}

@DatabaseTable(tableName = "VMK_Site_Base")
//data class SiteVMK(override val operator: Operator = Operator.VIMPELCOM) : Site()
 class SiteVMK: Site {
    constructor() : super(operator = Operator.VIMPELCOM)
    constructor(site: Site) : super(site.id, Operator.VIMPELCOM, site.number, site.latitude, site.longitude,
            site.address, site.obj, site.uid, site.status, site.timestamp, site.userAndroidId)
}


@DatabaseTable(tableName = "MGF_Site_Base")
//data class SiteMGF(override val operator: Operator = Operator.MEGAFON) : Site()
class SiteMGF: Site {
    constructor() : super(operator = Operator.MEGAFON)
    constructor(site: Site) : super(site.id, Operator.MEGAFON, site.number, site.latitude, site.longitude,
            site.address, site.obj, site.uid, site.status, site.timestamp, site.userAndroidId)
}


@DatabaseTable(tableName = "TELE_Site_Base")
//data class SiteTELE(override val operator: Operator = Operator.TELE2) : Site()
class SiteTELE: Site {
    constructor() : super(operator = Operator.TELE2)
    constructor(site: Site) : super(site.id, Operator.TELE2, site.number, site.latitude, site.longitude,
            site.address, site.obj, site.uid, site.status, site.timestamp, site.userAndroidId)
}
