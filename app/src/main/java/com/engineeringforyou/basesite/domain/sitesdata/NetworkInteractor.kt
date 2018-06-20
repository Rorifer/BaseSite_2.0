package com.engineeringforyou.basesite.domain.sitesdata

import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable

interface NetworkInteractor {

     fun saveSite(site: Site): Completable

     fun refreshDataBase(): Completable

    fun refreshDataBaseIfNeed(): Completable
}