package com.engineeringforyou.basesite.models

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField

open class Comment(
        @DatabaseField(generatedId = true)
        val id: Int,

        @DatabaseField
        val timestamp: Long? = null,

        @DatabaseField
        val operatorId: Int? = null,

        @DatabaseField
        val siteId: String? = null,

        @DatabaseField
        val siteStatus: Int? = null,

        @DatabaseField
        val userName: String? = null,

        @DatabaseField
        val comment: String? = null
) : Parcelable {
        constructor() : this(999999999)

        constructor(source: Parcel) : this(
                source.readInt(),
                source.readValue(Long::class.java.classLoader) as Long?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readString(),
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
                writeInt(id)
                writeValue(timestamp)
                writeValue(operatorId)
                writeString(siteId)
                writeValue(siteStatus)
                writeString(userName)
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
