package com.engineeringforyou.basesite.domain.sitedetails

import android.net.Uri
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface SiteDetailsInteractor {

    fun loadAddress(lat: Double, lng: Double): Single<String>

    fun getSavedComments(site: Site): Single<List<Comment>>

    fun loadComments(site: Site): Single<List<Comment>>

    fun loadPhotos(site: Site): Single<List<Uri>>

    fun saveComment(comment: Comment): Completable

    fun getName(): String

    fun saveName(name: String)

}