package com.engineeringforyou.basesite.presentation.sitedetails.presenter

import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView

interface SiteDetailsPresenter {

    fun bind(view: SiteDetailsView)

    fun unbindView()

    fun loadAddressFromCoordinates(latitude: Double, longitude: Double)

}
