package com.engineeringforyou.basesite.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.engineeringforyou.basesite.utils.FirebaseUtils
import com.engineeringforyou.basesite.utils.Utils

data class Job(
        var linkSiteUid: String? = null,
        var linkSiteOperator: Operator? = null,
        val siteOperator: Operator? = null,
        val siteNumber: String = "",
        val address: String = "",
        val name: String = "",
        val description: String = "",
        val price: String = "",
        val contact: String = "",
        var id: String = "",
        var timestamp: Long = 0L,
        var dateCreate: String = "",
        val userId: String = "",
        val userAndroidId: String = "",
        val public: Boolean = false,
        var latitude: Double? = null,
        var longitude: Double? = null
) : Parcelable {
    constructor(context: Context,
                linkSiteUid: String? = null,
                linkSiteOperator: Operator? = null,
                siteOperator: Operator?,
                siteNumber: String,
                address: String,
                name: String,
                description: String,
                price: String,
                contact: String
    ) : this(
            linkSiteUid,
            linkSiteOperator,
            siteOperator,
            siteNumber,
            address,
            name,
            description,
            price,
            contact,
            id = Utils.getRandomId(),
            timestamp = Utils.getCurrentTime(),
            dateCreate = Utils.getCurrentDate(),
            userId = FirebaseUtils.getIdCurrentUser()!!,
            userAndroidId = Utils.getAndroidId(context),
            public = true
    )

    fun setupForEdit(id: String): Job {
        this.id = id
        this.timestamp = Utils.getCurrentTime()
        this.dateCreate = Utils.getCurrentDate()
        return this
    }

    fun setLinkSite(site: Site?) {
        linkSiteUid = site?.uid
        linkSiteOperator = site?.operator
        latitude = site?.latitude
        longitude = site?.longitude
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readValue(Int::class.java.classLoader)?.let { Operator.values()[it as Int] },
            source.readValue(Int::class.java.classLoader)?.let { Operator.values()[it as Int] },
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readString(),
            source.readString(),
            source.readString(),
            1 == source.readInt(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(linkSiteUid)
        writeValue(linkSiteOperator?.ordinal)
        writeValue(siteOperator?.ordinal)
        writeString(siteNumber)
        writeString(address)
        writeString(name)
        writeString(description)
        writeString(price)
        writeString(contact)
        writeString(id)
        writeLong(timestamp)
        writeString(dateCreate)
        writeString(userId)
        writeString(userAndroidId)
        writeInt((if (public) 1 else 0))
        writeValue(latitude)
        writeValue(longitude)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Job> = object : Parcelable.Creator<Job> {
            override fun createFromParcel(source: Parcel): Job = Job(source)
            override fun newArray(size: Int): Array<Job?> = arrayOfNulls(size)
        }
    }
}