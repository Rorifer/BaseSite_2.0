package com.engineeringforyou.basesite.presentation.map.views

import com.engineeringforyou.basesite.models.Site

interface MapView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun showSites(siteList: List<Site>)

    fun showMainSite(site: Site)

    fun clearMap()

    fun showUserLocation()

    fun showStartingMessage()

    fun toSiteDetail(site: Site)
}
