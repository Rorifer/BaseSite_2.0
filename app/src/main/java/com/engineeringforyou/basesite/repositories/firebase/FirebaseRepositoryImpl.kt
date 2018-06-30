package com.engineeringforyou.basesite.repositories.firebase

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

const val DIRECTORY_COMMENTS = "comments"
const val DIRECTORY_SITES = "sites"
const val FIELD_SITE_ID = "siteId"
const val FIELD_OPERATOR_ID = "operatorId"
const val FIELD_TIMESTAMP = "timestamp"

class FirebaseRepositoryImpl : FirebaseRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override fun loadComments(site: Site): Single<List<Comment>> {
        return Single.create<List<Comment>> { emitter ->
            firestore.collection(DIRECTORY_COMMENTS)
                    .whereEqualTo(FIELD_SITE_ID, site.uid ?: site.number)
                    .whereEqualTo(FIELD_OPERATOR_ID, site.operator!!.code)
                    .orderBy(FIELD_TIMESTAMP)
                    .get()
                    .addOnCompleteListener { task ->
                        val list = ArrayList<Comment>()
                        if (task.isSuccessful) for (document in task.result) list.add(document.toObject(Comment::class.java))
                        emitter.onSuccess(list)
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun saveComment(comment: Comment): Completable {
        return Completable.create { emitter ->
            firestore.collection(DIRECTORY_COMMENTS).add(comment)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun saveSite(site: Site): Completable {
        return Completable.create { emitter ->
            firestore.collection(DIRECTORY_SITES).add(site)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun saveSiteAndComment(site: Site, comment: Comment): Completable {
        return Completable.create { emitter ->
            firestore.runTransaction { transaction ->
                transaction.set(firestore.collection(DIRECTORY_SITES).document(site.uid!!), site)
                transaction.set(firestore.collection(DIRECTORY_COMMENTS).document(site.uid), comment)
                null
            }
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun editSiteAndComment(site: Site, comment: Comment): Completable {
        return Completable.create { emitter ->
            firestore.runTransaction { transaction ->
                transaction.set(firestore.collection(DIRECTORY_SITES).document("edit_" + site.timestamp + "_" + site.uid!!), site)
                transaction.set(firestore.collection(DIRECTORY_COMMENTS).document("edit_" + site.timestamp + "_" + site.uid), comment)
                null
            }
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun loadSites(sitesTimestamp: Long): Single<List<Site>> {
        return Single.create<List<Site>> { emitter ->
            firestore.collection(DIRECTORY_SITES)
                    .whereGreaterThan(FIELD_TIMESTAMP, sitesTimestamp)
                    .get()
                    .addOnCompleteListener { task ->
                        val list = ArrayList<Site>()
                        if (task.isSuccessful) for (document in task.result) list.add(document.toObject(Site::class.java))
                        emitter.onSuccess(list)
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }
}
