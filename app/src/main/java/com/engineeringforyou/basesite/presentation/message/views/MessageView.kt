package com.engineeringforyou.basesite.presentation.message.views

import android.support.annotation.StringRes

interface MessageView {

    fun showProgress()

    fun hideProgress()

    fun showMessage(@StringRes textRes: Int)

    fun close()

}