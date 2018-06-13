package com.engineeringforyou.basesite.presentation.sitedetails.views

import com.engineeringforyou.basesite.models.Comment

interface SiteDetailsView {

    fun showProgress()

    fun hideProgress()

    fun setAddressFromCoordinates(address: String)

    fun showAdapter(list: List<Comment>)

}
