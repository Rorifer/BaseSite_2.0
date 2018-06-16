package com.engineeringforyou.basesite.models

import android.os.Parcel
import android.os.Parcelable
import com.engineeringforyou.basesite.utils.Utils
import com.j256.ormlite.field.DatabaseField
import java.util.*

open class Comment(
        @DatabaseField(generatedId = true)
        val id: Int? = null,

        @DatabaseField
        val timestamp: Long? = null,

        @DatabaseField
        val operatorId: Int? = null,

        @DatabaseField
        val siteId: String? = null,

        @DatabaseField
        val siteStatusId: Int? = null,

        @DatabaseField
        val userName: String? = null,

        @DatabaseField
        val userAndroidId: String? = null,

        @DatabaseField
        val comment: String? = null
) : Parcelable {
    constructor() : this(999999999)

    constructor(site: Site, text: String, user: User) : this(
            null,
            Date().time,
            site.operator!!.code,
            site.uid ?: site.number,
            site.status.code,
            user.userName,
            user.userAndroidId,
            text)

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeValue(timestamp)
        writeValue(operatorId)
        writeString(siteId)
        writeValue(siteStatusId)
        writeString(userName)
        writeString(userAndroidId)
        writeString(comment)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Comment> = object : Parcelable.Creator<Comment> {
            override fun createFromParcel(source: Parcel): Comment = Comment(source)
            override fun newArray(size: Int): Array<Comment?> = arrayOfNulls(size)
        }
    }
}
