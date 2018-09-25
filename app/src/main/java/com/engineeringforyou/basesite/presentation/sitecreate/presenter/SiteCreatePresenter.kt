package com.engineeringforyou.basesite.presentation.sitecreate.presenter

import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.sitecreate.views.SiteCreateView

interface SiteCreatePresenter {

    fun bind(view: SiteCreateView)

    fun saveSite(site: Site, userName: String)

    fun editSite(oldSite: Site, site: Site, userName: String)

    fun unbindView()

    fun setupView()

    fun loadAddressFromCoordinates(lat: Double, lng: Double)
}