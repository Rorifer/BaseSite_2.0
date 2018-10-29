package com.engineeringforyou.basesite.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    fun getCurrentTime(): Long = Date().time

    fun getCurrentDate(): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date().time).capitalize()
    }

    fun isEnableAdvertising(context: Context): Boolean {
//        return SettingsRepositoryImpl(context).isEnableAdvertising()
        return true
    }

    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                return true
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                return true
            }
            return activeNetwork.isConnected
        } else {
            return false
        }
    }

    fun validationNumber(phone: String?): String? {
        var phoneBuilder = phone
        if (phoneBuilder == null || phoneBuilder.isEmpty()) return null
        phoneBuilder = phoneBuilder.replace("\\s+".toRegex(), "")
        phoneBuilder = phoneBuilder.replace("-", "")
        if (phoneBuilder[0] == '9') phoneBuilder = "+7$phoneBuilder"
        if (phoneBuilder[0] == '8') phoneBuilder = "+7" + phoneBuilder.substring(1)
        return if (phoneBuilder.matches("^\\+7[0-9]{10}".toRegex())) phoneBuilder else null
    }

    fun getRandomId() = UUID.randomUUID().toString()
}