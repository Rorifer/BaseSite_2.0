package com.engineeringforyou.basesite.presentation.sitesearch.views

import android.support.annotation.StringRes
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site

interface SearchSiteView {

    fun setOperator(operatorIndex: Int)

    fun showError(@StringRes textRes: Int)

    fun hideError()

    fun showProgress()

    fun hideProgress()

    fun toSiteInfo(site: Site)

    fun toSiteChoice(list: List<Site>?)

    fun openMap(operator: Operator)

}
