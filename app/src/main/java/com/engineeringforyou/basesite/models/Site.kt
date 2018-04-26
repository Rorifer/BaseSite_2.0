package com.engineeringforyou.basesite.models

import android.os.Parcel
import android.os.Parcelable


data class Site(
        val operator: Operator,
        val number: String,
        val latitude: Double,
        val longitude: Double,
        val status: Status = Status.ACTIVE,
        val address: String = "нет данных",
        val obj: String = "нет данных",
        val description: String = "нет данных"
) : Parcelable {
    constructor(source: Parcel) : this(
            Operator.values()[source.readInt()],
            source.readString(),
            source.readDouble(),
            source.readDouble(),
            Status.values()[source.readInt()],
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(operator.ordinal)
        writeString(number)
        writeDouble(latitude)
        writeDouble(longitude)
        writeInt(status.ordinal)
        writeString(address)
        writeString(obj)
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