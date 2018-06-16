package com.engineeringforyou.basesite.models

import android.content.Context
import com.engineeringforyou.basesite.utils.Utils

data class User(
        var userName: String,
        var userAndroidId: String
) {
    constructor(context: Context, userName: String)
            : this(userName, Utils.getAndroidId(context))
}
