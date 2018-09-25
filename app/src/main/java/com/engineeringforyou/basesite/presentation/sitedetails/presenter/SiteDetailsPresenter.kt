package com.engineeringforyou.basesite.presentation.sitedetails.presenter

import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.models.User
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView

interface SiteDetailsPresenter {

    fun bind(view: SiteDetailsView)

    fun unbindView()

    fun loadAddressFromCoordinates(lat: Double?, lng: Double?)

    fun loadComments(site: Site)

    fun saveComment(site: Site, comment: String, user: User)

    fun setupName()

}
