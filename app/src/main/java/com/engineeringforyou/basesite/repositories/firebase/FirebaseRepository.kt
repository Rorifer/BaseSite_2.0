package com.engineeringforyou.basesite.repositories.firebase

import android.net.Uri
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Job
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

    fun saveJob(job: Job): Completable

    fun closeJob(id: String): Completable

    fun publicJob(id: String): Completable

    fun loadListPublicJob(): Single<List<Job>>

    fun loadListUserJob(): Single<List<Job>>

    fun enableStatusNotification(): Completable

    fun disableStatusNotification(): Completable
    fun editJob(job: Job): Completable
}