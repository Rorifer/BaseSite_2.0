package com.engineeringforyou.basesite.presentation.link

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity
import com.engineeringforyou.basesite.utils.EventFactory
import com.engineeringforyou.basesite.utils.ShareUtils

class LinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        val data = intent.data

        if (action != null && data != null && action == "android.intent.action.VIEW") {

            val site = ShareUtils.getSiteFromUri(data)

            if (site != null) SiteDetailsActivity.startFromLink(this, site)
            else EventFactory.message("LinkActivity Error data= $data")

        } else EventFactory.message("LinkActivity Error intent = $intent")

        finish()

    }

}