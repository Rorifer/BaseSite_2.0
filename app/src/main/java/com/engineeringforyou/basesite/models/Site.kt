package com.engineeringforyou.basesite.models

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField

data class Site(
        @DatabaseField(generatedId = true, columnName = "_id")
        val id: String,

        val operator: Operator,

        @DatabaseField(columnName = "SITE")
        val number: String,

        @DatabaseField(columnName = "GPS_Latitude")
        val latitude: Double,

        @DatabaseField(columnName = "GPS_Longitude")
        val longitude: Double,

        @DatabaseField(columnName = "Addres")
        val address: String = "нет данных",

        @DatabaseField(columnName = "Object")
        val obj: String = "нет данных",

        val status: Status = Status.ACTIVE,

        val description: String = "нет данных"

)
    : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            Operator.values()[source.readInt()],
            source.readString(),
            source.readDouble(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            Status.values()[source.readInt()],
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeInt(operator.ordinal)
        writeString(number)
        writeDouble(latitude)
        writeDouble(longitude)
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