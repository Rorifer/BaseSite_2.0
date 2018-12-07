package com.engineeringforyou.basesite.utils

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Base64.DEFAULT
import com.engineeringforyou.basesite.data.orm.ORMHelperFactory
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.google.gson.Gson


class ShareUtils {

    companion object {
        private const val APP_LINK: String = "https://play.google.com/store/apps/details?id=com.engineeringforyou.basesite"
        private const val LINK_CONNECTOR: String = "/link/"

        fun shareApp(context: Context) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, APP_LINK)
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, "Отправить ссылку на приложение"))
        }

        fun getSharedLink(site: Site): String = "$APP_LINK$LINK_CONNECTOR${encode(site)}"

        fun parseSharedLink(link: String): Site? {
            val shortSite = decode(link.removePrefix("$APP_LINK$LINK_CONNECTOR"))
            return if (shortSite != null) {
                val siteList = ORMHelperFactory.getHelper().searchSitesByUid(shortSite.operator, shortSite.uid)
                if (siteList.size > 1) EventFactory.exception(Throwable("ShareUtils, siteList.size > 1,  $shortSite"))
                siteList.first()
            } else null
        }

        private fun encode(site: Site): String {
            val json = Gson().toJson(ShortSite(site))
            return Base64.encodeToString(json.toByteArray(), DEFAULT)
        }

        private fun decode(codeShortSite: String): ShortSite? {
            return try {
                val json = Base64.decode(codeShortSite, DEFAULT).toString()
                Gson().fromJson(json, ShortSite::class.java)
            } catch (t: Throwable) {
                null
            }

        }

    }

    class ShortSite(val operator: Operator, val uid: String) {
        constructor(site: Site) : this(site.operator!!, site.uid!!)
    }

}