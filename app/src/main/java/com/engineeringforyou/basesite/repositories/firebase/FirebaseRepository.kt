package com.engineeringforyou.basesite.repositories.firebase

import android.net.Uri
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Message
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface FirebaseRepository {

    fun loadComments(site: Site): Single<List<Comment>>

    fun saveComment(comment: Comment): Completable

    fun saveSite(site: Site, comment: Comment, photoUriList: List<Uri>): Completable

    fun editSiteAndComment(site: Site, oldSite: Site, comment: Comment): Completable

    fun loadSites(sitesTimestamp: Long): Single<List<Site>>

    fun saveMessage(message: Message): Completable

    fun savePhotos(photoUriList: List<Uri>, site: Site): Completable

    fun loadPhotos(site: Site): Single<List<Uri>>
}