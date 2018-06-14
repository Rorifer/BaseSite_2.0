package com.engineeringforyou.basesite.repositories.firebase

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

class FirebaseRepositoryImpl : FirebaseRepository {
    override fun getComments(site: Site): Single<List<Comment>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveComment(comment: Comment): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
