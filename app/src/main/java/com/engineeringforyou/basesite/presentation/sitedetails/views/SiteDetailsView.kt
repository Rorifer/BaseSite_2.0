package com.engineeringforyou.basesite.presentation.sitedetails.views

interface SiteDetailsView {

    fun showProgress()

    fun hideProgress()

    fun setAddressFromCoordinates(address: String)

}
