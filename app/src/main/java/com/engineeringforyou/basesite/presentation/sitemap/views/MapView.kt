package com.engineeringforyou.basesite.presentation.sitemap.views

import com.engineeringforyou.basesite.models.Site
import com.google.android.gms.maps.model.LatLng

interface MapView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun showSites(siteList: List<Site>)

    fun showMainSite(site: Site)

    fun clearMap()

    fun setMapType(mapType: Int)

    fun showUserLocation()

    fun showStartingMessage()

    fun toSiteDetail(site: Site)

    fun moveCamera(position: LatLng)

    fun showSitesForCurrentLocation()
}
