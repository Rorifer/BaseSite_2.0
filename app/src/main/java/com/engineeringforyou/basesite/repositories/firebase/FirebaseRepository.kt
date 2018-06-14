package com.engineeringforyou.basesite.repositories.firebase

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface FirebaseRepository {

    fun getComments(site: Site): Single<List<Comment>>

    fun saveComment(comment: Comment): Completable
}