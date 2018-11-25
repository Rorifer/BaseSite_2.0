package com.engineeringforyou.basesite.utils

import android.os.Build
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.LoginEvent
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Site
import java.text.SimpleDateFormat
import java.util.*

object EventFactory {

    private const val ERROR = "Error"
    private const val MESSAGE = "Message"
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

    fun message(message: String) {
        Crashlytics.setString(DEVICE_BRAND, Build.BRAND)
        Crashlytics.setString(DEVICE_MODEL, Build.MODEL)
        Crashlytics.setString(DATE_TIME, getCurrentDate())
        Crashlytics.setInt(BUILD_VERSION_SDK_INT, Build.VERSION.SDK_INT)
        Crashlytics.log(7, MESSAGE, message)
        Crashlytics.logException(Throwable(message))
    }

    fun logIn() {
        Answers.getInstance().logLogin(LoginEvent()
                .putCustomAttribute("UserId", FirebaseUtils.getIdCurrentUser())
                .putCustomAttribute("UserPhone", FirebaseUtils.getPhoneCurrentUser())
                .putSuccess(true))
    }

    fun logInWrong(phone: String?) {
        Answers.getInstance().logLogin(LoginEvent()
                .putCustomAttribute("UserId", FirebaseUtils.getIdCurrentUser())
                .putCustomAttribute("UserPhone", phone)
                .putSuccess(false))
    }

    fun openJob(job: Job) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName(job.name)
                .putContentType("JobDetails")
                .putContentId(job.id))
    }

    fun openStreetView(site: Site) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("StreetView")
                .putContentName("${site.operator} ${site.number}")
                .putContentId(site.uid))
    }

    fun openSiteDetails(site: Site) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("SiteDetails")
                .putContentName("${site.operator} ${site.number}")
                .putContentId(site.uid))
    }

    fun clickRouteBS(site: Site) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("RouteBS")
                .putContentName("${site.operator} ${site.number}")
                .putContentId(site.uid))
    }

    fun clickEnableNotification(uid: String) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("ClickEnableNotification")
                .putContentName(uid))
    }

    fun writeComment(comment: Comment) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("WriteComment")
                .putContentName("${comment.operatorId} ${comment.siteId}")
                .putContentId(comment.userAndroidId))
    }

    fun saveSite (site: Site, uid: String?) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("SaveSite")
                .putContentName("${site.operator} ${site.number}")
                .putContentId(uid))
    }


    fun editSite (site: Site, uid: String?) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("EditSite")
                .putContentName("${site.operator} ${site.number}")
                .putContentId(uid))
    }

    fun addPhoto(site: Site, uid: String?) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentType("AddPhoto")
                .putContentName("${site.operator} ${site.number}")
                .putContentId(uid))
    }

}