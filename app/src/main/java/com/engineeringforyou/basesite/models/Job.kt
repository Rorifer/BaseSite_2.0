package com.engineeringforyou.basesite.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.engineeringforyou.basesite.utils.FirebaseUtils
import com.engineeringforyou.basesite.utils.Utils

data class Job(
        var linkSiteUid: String?,
        var linkSiteOperator: Operator?,
        val siteOperator: Operator?,
        val siteNumber: String,
        val address: String,
        val name: String,
        val description: String,
        val price: String,
        val contact: String,
        val id: String,
        val timestamp: Long,
        val dateCreate: String,
        val userId: String,
        val userAndroidId: String,
        val isPublic: Boolean
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
            isPublic = true
    )

    fun setLinkSite(site: Site?) {
        linkSiteUid = site?.uid
        linkSiteOperator = site?.operator
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
            1 == source.readInt()
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
        writeInt((if (isPublic) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Job> = object : Parcelable.Creator<Job> {
            override fun createFromParcel(source: Parcel): Job = Job(source)
            override fun newArray(size: Int): Array<Job?> = arrayOfNulls(size)
        }
    }
}