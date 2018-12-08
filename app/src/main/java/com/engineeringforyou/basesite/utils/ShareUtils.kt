package com.engineeringforyou.basesite.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Base64.DEFAULT
import com.engineeringforyou.basesite.data.orm.ORMHelperFactory
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.google.gson.Gson


class ShareUtils {

    companion object {
        private const val APP_LINK: String = "https://play.google.com/store/apps/details?id=com.engineeringforyou.basesite"
        private const val LINK_CONNECTOR: String = "&link="

        fun shareApp(context: Context) =
                share(context, APP_LINK, "Отправить ссылку на приложение")

        fun shareSite(context: Context, site: Site) =
                share(context, "$APP_LINK$LINK_CONNECTOR${encode(site)}", "Отправить ссылку на БС")

        fun getSiteFromUri(uri: Uri): Site? {
            val code = uri.toString().removePrefix("$APP_LINK$LINK_CONNECTOR")
            val shortSite = decode(code)
            return if (shortSite != null) {
                val siteList = ORMHelperFactory.getHelper().searchSitesByUid(shortSite.operator, shortSite.uid)
                if (siteList.size > 1) EventFactory.exception(Throwable("ShareUtils, siteList.size > 1,  $shortSite"))
                siteList.first()
            } else null
        }

        private fun share(context: Context, text: String, title: String) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, title))
        }

        private fun encode(site: Site): String {
            val json = Gson().toJson(ShortSite(site))
            return Base64.encodeToString(json.toByteArray(), DEFAULT)
        }

        private fun decode(codeShortSite: String): ShortSite? {
            return try {
                val json = String(Base64.decode(codeShortSite, DEFAULT))
                Gson().fromJson(json, ShortSite::class.java)
            } catch (t: Throwable) {
                EventFactory.exception(t)
                null
            }
        }

    }

    class ShortSite(val operator: Operator, val uid: String) {
        constructor(site: Site) : this(site.operator!!, site.uid!!)
    }

}