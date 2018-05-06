package com.engineeringforyou.basesite.presentation.map.presenter

import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.map.views.MapView


interface MapPresenter {

    fun bind(view: MapView, site: Site?)

    fun unbindView()

    fun showOperatorLocation(operator: Operator, lat: Double, lng: Double)

    fun setMapType(mapType: Int)

    fun setupMap()

    fun getOperator(): Operator

    fun getMapType(): Int

    fun  setRadius(radius: Int)

    fun getRadius (): Int

    fun showSitesLocation(lat: Double, lng: Double)

    fun clickSite(site: Site?)

    fun clickMapLocation(lat: Double, lng: Double)

}
