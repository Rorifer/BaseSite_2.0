package com.engineeringforyou.basesite.presentation.sitedetails.presenter

import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.models.User
import com.engineeringforyou.basesite.presentation.sitedetails.views.SiteDetailsView

interface SiteDetailsPresenter {

    fun bind(view: SiteDetailsView)

    fun unbindView()

    fun saveComment(site: Site, comment: String, user: User)

    fun setupName()

    fun loadFields(site: Site) {}

}
