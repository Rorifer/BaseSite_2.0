package com.engineeringforyou.basesite.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

object Utils {

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID);
}