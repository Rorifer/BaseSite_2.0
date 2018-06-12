package com.engineeringforyou.basesite.models

import com.j256.ormlite.field.DatabaseField

open class Comments(
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
        val comment: String? = null
) {
    constructor() : this(999999999)

}
