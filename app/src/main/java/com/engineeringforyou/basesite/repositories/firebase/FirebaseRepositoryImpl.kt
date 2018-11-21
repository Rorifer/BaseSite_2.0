package com.engineeringforyou.basesite.repositories.firebase

import android.net.Uri
import com.engineeringforyou.basesite.BuildConfig
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Message
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.utils.FirebaseUtils
import com.engineeringforyou.basesite.utils.Utils
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import io.reactivex.Single


class FirebaseRepositoryImpl : FirebaseRepository {

    var DIRECTORY_COMMENTS = "comments"
    var DIRECTORY_SITES = "sites"
    var DIRECTORY_SITES_EDITED = "edited"
    var DIRECTORY_SITES_PHOTO = "photos_url"
    var DIRECTORY_JOB = "job"
    var DIRECTORY_NOTIFICATION = "notification"
    var STORAGE_IMAGE = "image"
    val DIRECTORY_MESSAGE = "message"
    val FIELD_SITE_ID = "siteId"
    val FIELD_OPERATOR_ID = "operatorId"
    val FIELD_TIMESTAMP = "timestamp"
    val FIELD_PUBLIC = "public"
    val FIELD_UID = "userId"
    val FIELD_REFERENCE = "reference"
    val FIELD_TOKEN = "token"

    init {
        if (BuildConfig.DEBUG) {
            DIRECTORY_COMMENTS = "debug_$DIRECTORY_COMMENTS"
            DIRECTORY_SITES = "debug_$DIRECTORY_SITES"
            DIRECTORY_SITES_EDITED = "debug_$DIRECTORY_SITES_EDITED"
            DIRECTORY_SITES_PHOTO = "debug_$DIRECTORY_SITES_PHOTO"
            STORAGE_IMAGE = "debug_$STORAGE_IMAGE"
            DIRECTORY_JOB = "debug_$DIRECTORY_JOB"
            DIRECTORY_NOTIFICATION = "debug_$DIRECTORY_NOTIFICATION"
        }
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun loadComments(site: Site): Single<List<Comment>> {
        return Single.create<List<Comment>> { emitter ->
            firestore.collection(DIRECTORY_COMMENTS)
                    .whereEqualTo(FIELD_SITE_ID, site.uid ?: site.number)
                    .whereEqualTo(FIELD_OPERATOR_ID, site.operator!!.code)
                    .orderBy(FIELD_TIMESTAMP)
                    .get()
                    .addOnCompleteListener { task ->
                        val list = ArrayList<Comment>()
                        if (task.isSuccessful) for (document in task.result!!) list.add(document.toObject(Comment::class.java))
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

    override fun saveMessage(message: Message): Completable {
        return Completable.create { emitter ->

            if (message.message == "text") { // Тесты Google
                emitter.onComplete()
                return@create
            }

            firestore.collection(DIRECTORY_MESSAGE).add(message)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun saveSite(site: Site, comment: Comment, photoUriList: List<Uri>): Completable {
        return Completable.create { emitter ->
            savePhotosInStorage(photoUriList, site)
                    .doOnSuccess { uriList ->
                        firestore.runTransaction { transaction ->
                            transaction.set(firestore.collection(DIRECTORY_SITES).document(site.uid!!), site)
                            transaction.set(firestore.collection(DIRECTORY_COMMENTS).document(site.uid), comment)
                            transaction.setSavePhotoUrl(uriList, site)
                            null
                        }
                                .addOnSuccessListener { emitter.onComplete() }
                                .addOnFailureListener { emitter.onError(it) }
                    }
                    .doOnError { emitter.onError(it) }
                    .subscribe()
        }
    }

    override fun savePhotos(photoUriList: List<Uri>, site: Site): Completable {
        return Completable.create { emitter ->
            savePhotosInStorage(photoUriList, site)
                    .doOnSuccess { uriList ->
                        firestore.runTransaction { transaction ->
                            transaction.setSavePhotoUrl(uriList, site)
                            null
                        }
                                .addOnSuccessListener { emitter.onComplete() }
                                .addOnFailureListener { emitter.onError(it) }
                    }
                    .doOnError { emitter.onError(it) }
                    .subscribe()
        }
    }

    private fun Transaction.setSavePhotoUrl(uriList: List<String>, site: Site) {
        if (uriList.isEmpty()) return
        for (uri in uriList) {
            val map = HashMap<String, Any>(2)
                    .apply {
                        put(FIELD_REFERENCE, uri)
                        put(FIELD_TIMESTAMP, Utils.getCurrentTime())
                    } as Map<String, Any>
            this.set(firestore
                    .collection(DIRECTORY_SITES_PHOTO)
                    .document(site.operator!!.label)
                    .collection(site.uid!!)
                    .document(), map)
        }
    }

    @Volatile
    var indexCounter = 0

    private fun savePhotosInStorage(photoUriList: List<Uri>, site: Site): Single<List<String>> {
        return Single.create { emitter ->
            val uriMap: ArrayList<String> = ArrayList()

            if (photoUriList.isEmpty()) {
                emitter.onSuccess(uriMap)
                return@create
            }

            indexCounter = 0
            for (index in photoUriList.indices) {
                val imageRef = storage.reference
                        .child(STORAGE_IMAGE)
                        .child(site.operator!!.label)
                        .child(site.uid!!)
                        .child("${Utils.getCurrentTime()}")
                val uploadTask = imageRef.putFile(photoUriList[index])
                uploadTask
                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    emitter.onError(it)
                                }
                            }
                            return@Continuation imageRef.downloadUrl
                        })
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                uriMap.add(task.result.toString())
                                indexCounter++
                                if (indexCounter == photoUriList.size) emitter.onSuccess(uriMap)
                            } else {
                                emitter.onError(Throwable("Task not Successful"))
                            }
                        }
                        .addOnFailureListener { emitter.onError(it) } // TODO: Не приходит UnknownHostExeption при отсутствии интернета
            }
        }
    }

    override fun loadPhotos(site: Site): Single<List<Uri>> {
        return Single.create<List<Uri>> { emitter ->
            firestore.collection(DIRECTORY_SITES_PHOTO)
                    .document(site.operator!!.label)
                    .collection(site.uid!!)
                    .orderBy(FIELD_TIMESTAMP)
                    .get()
                    .addOnCompleteListener { task ->
                        val list = ArrayList<Uri>()
                        if (task.isSuccessful) for (document in task.result!!) list.add(Uri.parse(document.get(FIELD_REFERENCE) as String))
                        emitter.onSuccess(list)
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun editSiteAndComment(site: Site, oldSite: Site, comment: Comment): Completable {
        return Completable.create { emitter ->
            firestore.runTransaction { transaction ->
                transaction.set(firestore.collection(DIRECTORY_SITES).document("edit_" + site.timestamp + "_" + site.uid!!), site)
                transaction.set(firestore.collection(DIRECTORY_SITES_EDITED).document("edit_" + site.timestamp + "_" + site.uid), oldSite)
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
                        if (task.isSuccessful) for (document in task.result!!) list.add(document.toObject(Site::class.java))
                        emitter.onSuccess(list)
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun saveJob(job: Job): Completable {
        return Completable.create { emitter ->
            firestore.collection(DIRECTORY_JOB)
                    .document(job.id)
                    .set(job)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun editJob(job: Job): Completable {
        return saveJob(job)
    }

    override fun closeJob(id: String): Completable {
        return setPublicStatus(id, false)
    }

    override fun publicJob(id: String): Completable {
        return setPublicStatus(id, true)
    }

    private fun setPublicStatus(id: String, isPublic: Boolean): Completable {
        return Completable.create { emitter ->
            firestore.collection(DIRECTORY_JOB)
                    .document(id)
                    .update(FIELD_PUBLIC, isPublic)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun loadListPublicJob(): Single<List<Job>> {
        return Single.create<List<Job>> { emitter ->
            firestore.collection(DIRECTORY_JOB)
                    .whereEqualTo(FIELD_PUBLIC, true)
                    .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener { task ->
                        val list = ArrayList<Job>()
                        if (task.isSuccessful) for (document in task.result!!) list.add(document.toObject(Job::class.java))
                        emitter.onSuccess(list)
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun loadListPrivateJob(): Single<List<Job>> {
        return Single.create<List<Job>> { emitter ->
            firestore.collection(DIRECTORY_JOB)
                    .whereEqualTo(FIELD_UID, FirebaseUtils.getIdCurrentUser()!!)
                    .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener { task ->
                        val list = ArrayList<Job>()
                        if (task.isSuccessful) for (document in task.result!!) list.add(document.toObject(Job::class.java))
                        emitter.onSuccess(list)
                    }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun enableStatusNotification(userAndroidId: String): Completable {
        return FirebaseUtils.getToken()
                .flatMapCompletable { setTokenNotification(userAndroidId, it) }
    }

    override fun disableStatusNotification(userAndroidId: String): Completable {
        return setTokenNotification(userAndroidId, null)
    }

    private fun setTokenNotification(userAndroidId: String, token: String?): Completable {
        return Completable.create { emitter ->
            firestore.collection(DIRECTORY_NOTIFICATION)
                    .document(userAndroidId)
                    .set(HashMap<String, Any>(1).apply { put(FIELD_TOKEN, token ?: "") })
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }
}
