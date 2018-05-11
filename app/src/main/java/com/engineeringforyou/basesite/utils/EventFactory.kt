package com.engineeringforyou.basesite.utils

import android.os.Build
import com.crashlytics.android.Crashlytics
import java.text.SimpleDateFormat
import java.util.*

object EventFactory {

        private const val ERROR = "Error"
        private const val DEVICE_BRAND = "device_brand"
        private const val DEVICE_MODEL = "device_model"
        private const val BUILD_VERSION_SDK_INT = "android_version"
        private const val DATE_TIME = "date_time"


    fun exception(throwable: Throwable) {
        Crashlytics.setString(DEVICE_BRAND, Build.BRAND)
        Crashlytics.setString(DEVICE_MODEL, Build.MODEL)
        Crashlytics.setString(DATE_TIME, getCurrentDate())
        Crashlytics.setInt(BUILD_VERSION_SDK_INT, Build.VERSION.SDK_INT)
        Crashlytics.log(6, ERROR, throwable.message)
        Crashlytics.logException(throwable)
    }

    private fun getCurrentDate(): String {
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(Date().time)
    }
}