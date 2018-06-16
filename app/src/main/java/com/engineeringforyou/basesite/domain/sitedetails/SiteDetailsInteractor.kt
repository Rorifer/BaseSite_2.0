package com.engineeringforyou.basesite.domain.sitedetails

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface SiteDetailsInteractor {

    fun loadAddress(lat: Double, lng: Double): Single<String>

    fun getSavedComments(site: Site): Single<List<Comment>>

    fun loadComments(site: Site): Single<List<Comment>>

    fun saveComment(comment: Comment): Completable

    fun getName(): String

    fun saveName(name: String)

}