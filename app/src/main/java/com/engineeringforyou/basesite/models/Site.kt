package com.engineeringforyou.basesite.models

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

//@DatabaseTable(tableName = "Sites_Base")
//@DatabaseTable(tableName = "TELE_Site_Base")
open class Site(
        @DatabaseField(generatedId = true, columnName = "_id")
        val id: Int,

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

        val status: Status = Status.ACTIVE,

        val description: String = "нет данных"

) : Parcelable {
    constructor() : this(562)

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readValue(Int::class.java.classLoader)?.let { Operator.values()[it as Int] },
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            Status.values()[source.readInt()],
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeValue(operator?.ordinal)
        writeString(number)
        writeValue(latitude)
        writeValue(longitude)
        writeString(address)
        writeString(obj)
        writeInt(status.ordinal)
        writeString(description)
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
class SiteMTS(override val operator: Operator = Operator.MTS) : Site()

@DatabaseTable(tableName = "VMK_Site_Base")
data class SiteVMK(override val operator: Operator = Operator.VIMPELCOM) : Site()

@DatabaseTable(tableName = "MGF_Site_Base")
data class SiteMGF(override val operator: Operator = Operator.MEGAFON) : Site()

@DatabaseTable(tableName = "TELE_Site_Base")
data class SiteTELE(override val operator: Operator = Operator.TELE2) : Site()