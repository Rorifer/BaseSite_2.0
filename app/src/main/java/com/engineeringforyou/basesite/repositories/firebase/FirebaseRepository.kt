package com.engineeringforyou.basesite.repositories.firebase

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Message
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface FirebaseRepository {

    fun loadComments(site: Site): Single<List<Comment>>

    fun saveComment(comment: Comment): Completable

    fun saveSite(site: Site): Completable

    fun saveSiteAndComment(site: Site, comment: Comment): Completable

    fun editSiteAndComment(site: Site, oldSite: Site, comment: Comment): Completable

    fun loadSites(sitesTimestamp: Long): Single<List<Site>>

    fun saveMessage(message: Message): Completable
}