package com.engineeringforyou.basesite.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    fun getTimestamp(): Long  = Date().time

    fun getCurrentDate(): String  {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format( Date().time).capitalize()
    }
}