package com.engineeringforyou.basesite.presentation.job.setting

import android.support.annotation.StringRes

interface JobSettingsView {
    fun showProgress()
    fun hideProgress()
    fun setCheckedNotificationSwitch(isChecked: Boolean)
    fun showError(@StringRes error: Int)
}
