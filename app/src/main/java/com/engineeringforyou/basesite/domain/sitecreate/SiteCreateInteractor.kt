package com.engineeringforyou.basesite.domain.sitecreate

import android.net.Uri
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable

interface SiteCreateInteractor {

    fun saveSite(site: Site, photoUriList: List<Uri>, userName: String): Completable

    fun refreshDataBase(): Completable

    fun refreshDataBaseIfNeed(): Completable

    fun getName(): String

    fun saveName(name: String)

    fun editSite(site: Site, oldSite: Site, comment: String, userName: String): Completable

    fun savePhotos(photoUriList: List<Uri>, site: Site, userName: String): Completable
}