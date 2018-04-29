package com.engineeringforyou.basesite.domain.sitedetails

import io.reactivex.Single

interface SiteDetailsInteractor {

    fun loadAddress(lat: Double, lng: Double): Single<String>

}