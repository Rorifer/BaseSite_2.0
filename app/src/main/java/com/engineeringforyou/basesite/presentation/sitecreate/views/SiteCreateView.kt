package com.engineeringforyou.basesite.presentation.sitecreate.views

import android.support.annotation.StringRes

interface SiteCreateView {

    fun showProgress()

    fun hideProgress()

    fun showMessage(@StringRes textRes: Int)

    fun close()

    fun setName(name: String)

}