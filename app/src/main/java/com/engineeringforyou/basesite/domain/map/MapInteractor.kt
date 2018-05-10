package com.engineeringforyou.basesite.domain.map

import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Observable
import io.reactivex.Single

interface MapInteractor {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

    fun saveMapType(mapType: Int)

    fun getMapType(): Int

    fun saveRadius(radius: Int)

    fun getRadius(): Int

    fun getSites(lat: Double, lng: Double): Observable<List<Site>>

    fun getAllSites(): Single<List<Site>>
}