package com.engineeringforyou.basesite.domain.sitedraft

import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable

interface SiteDraftInteractor {

     fun saveSite(site: Site): Completable

     fun refreshDataBase(): Completable

}