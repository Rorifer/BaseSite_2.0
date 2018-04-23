package com.engineeringforyou.basesite.presentation.searchsite.views

import android.database.Cursor
import android.support.annotation.StringRes

interface SearchSiteView {

    fun setOperator(operatorIndex: Int)

    fun showError(@StringRes textRes: Int)

    fun showProgress()

    fun hideProgress()

    fun toSiteInfo(cursor: Cursor)

    fun toSiteChoice(cursor: Cursor, count: Int)

    fun hideError()
    fun toMap()

}
