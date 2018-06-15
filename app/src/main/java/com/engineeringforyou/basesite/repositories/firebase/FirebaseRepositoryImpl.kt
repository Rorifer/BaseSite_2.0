package com.engineeringforyou.basesite.repositories.firebase

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single

const val DIRECTORY_COMMENTS = "comments"


class FirebaseRepositoryImpl : FirebaseRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override fun getComments(site: Site): Single<List<Comment>> {

        return Single.create {

            firestore.collection(DIRECTORY_COMMENTS).get()
                    .addOnCompleteListener {
                        val list = ArrayList<Comment>()
                        if (it.isSuccessful) for (document in it.result) list.add(document.toObject(Comment::class.java))
                        Single.fromCallable { list }
                    }
//                        it.result.toObjects(Comment::class.java) }
//                    .addOnSuccessListener {it -> it.toObjects(Comment::class.java). }
                    .addOnFailureListener { Single.error<Throwable>(it) }
//        }
        }
    }


//            val query = firestore.collection(DIRECTORY_COMMENTS)
//                    //                .whereGreaterThanOrEqualTo(FIELD_DATES)
//                    //                .whereGreaterThan("dateCreate", new Date(new Date().getTime() - 86400000));
//                    //                .whereLessThan("dateCreate", new Date(new Date().getTime() - 86400000));
//                    //                .whereGreaterThanOrEqualTo(FIELD_DATES, Long.toString(new Date().getTime()))
//                    //                .whereGreaterThan(FIELD_DATES, new Date())
//                    //                .whereGreaterThanOrEqualTo(FIELD_DATES, new Date());
//                    //                .whereGreaterThanOrEqualTo(FIELD_DATES, Long.toString(123))
//
//                    .orderBy(FIELD_DATES, Query.Direction.ASCENDING)


//        }
//        }
//    }

    override fun saveComment(comment: Comment): Completable {

        return Completable.create {
            firestore.collection(DIRECTORY_COMMENTS).add(comment)
                    .addOnSuccessListener { Completable.complete() }
                    .addOnFailureListener { Completable.error(it) }
        }
    }
}
