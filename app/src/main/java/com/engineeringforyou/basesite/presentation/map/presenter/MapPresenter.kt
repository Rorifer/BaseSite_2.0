package com.engineeringforyou.basesite.presentation.map.presenter

import com.engineeringforyou.basesite.presentation.map.views.MapView


interface MapPresenter {

    fun bind(view: MapView)

    fun unbindView()

}
