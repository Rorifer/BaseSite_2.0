package com.engineeringforyou.basesite.domain.sitecreate

import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable

interface SiteCreateInteractor {

     fun saveSite(site: Site, userName: String): Completable

     fun refreshDataBase(): Completable

    fun refreshDataBaseIfNeed(): Completable

    fun getName(): String

    fun saveName(name: String)
}