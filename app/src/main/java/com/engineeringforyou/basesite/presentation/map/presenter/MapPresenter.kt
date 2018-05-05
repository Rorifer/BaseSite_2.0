package com.engineeringforyou.basesite.presentation.map.presenter

import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.map.views.MapView


interface MapPresenter {

    fun bind(view: MapView, operator: Operator, site: Site?)

    fun unbindView()

    fun saveOperator (operator: Operator)

    fun saveMapType (mapType: Int)

    fun setupMap()

    fun getOperator(): Operator

}
