package com.engineeringforyou.basesite.presentation.map.views

import com.engineeringforyou.basesite.models.Site

interface MapView {

    fun showProgress()

    fun hideProgress()

    fun showSites(siteList: List<Site>, siteCentral: Site?)

    fun showLocation()
}
