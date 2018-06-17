package com.engineeringforyou.basesite.presentation.sitedraft.views

import android.support.annotation.StringRes

interface SiteDraftView {

    fun showProgress()

    fun hideProgress()

    fun showMessage(@StringRes textRes: Int)

    fun close()

}