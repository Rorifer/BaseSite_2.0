package com.engineeringforyou.basesite.presentation.sitedraft.presenter

import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.sitedraft.views.SiteDraftView

interface SiteDraftPresenter {

    fun bind(view: SiteDraftView)
    fun saveDraft(site: Site)

    fun unbindView()
}