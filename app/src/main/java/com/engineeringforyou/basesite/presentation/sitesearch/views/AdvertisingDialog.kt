package com.engineeringforyou.basesite.presentation.sitesearch.views

import android.widget.Toast
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.presentation.sitesearch.SearchSiteActivity
import com.engineeringforyou.basesite.utils.MessageDialog
import com.google.android.gms.ads.reward.RewardedVideoAd

object AdvertisingDialog {

    fun show(activity: SearchSiteActivity, rewardedVideoAd: RewardedVideoAd){
        val dialog = MessageDialog
                .getInstance(activity.getString(R.string.advertising), null, null, activity.getString(R.string.app_advertising), true)
        dialog.show(activity.supportFragmentManager)
        dialog.positive = {
            activity.showProgress()
            if (rewardedVideoAd.isLoaded()) {
                rewardedVideoAd.show()
            } else
                Toast.makeText(activity, "Не загружено", Toast.LENGTH_SHORT).show()
        }
    }
}