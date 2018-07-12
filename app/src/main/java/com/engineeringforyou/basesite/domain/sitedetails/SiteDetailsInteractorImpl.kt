package com.engineeringforyou.basesite.domain.sitedetails

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.Completable
import io.reactivex.Single
import java.io.IOException
import java.util.*

class SiteDetailsInteractorImpl(private val context: Context) : SiteDetailsInteractor {

    private var dataBase: DataBaseRepository = DataBaseRepositoryImpl()
    private var firebase: FirebaseRepository = FirebaseRepositoryImpl()
    private var settings: SettingsRepository = SettingsRepositoryImpl(context)

    override fun loadAddress(lat: Double, lng: Double): Single<String> {
        return Single.fromCallable {
            var list: List<Address>? = null
            try {
                list = Geocoder(context).getFromLocation(lat, lng, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (list != null) {
                if (list.isNotEmpty()) {
                    val address = list[0]
                    val details = ArrayList<String?>()
                    details.add(address.adminArea)
                    details.add(address.subAdminArea)
                    details.add(address.locality)
                    details.add(address.thoroughfare)
                    details.add(address.featureName)

                    var prev = ""
                    var addressText = ""
                    for (detail in details) {
                        if (detail != null && detail != prev) {
                            if (addressText != "") addressText += ", "
                            addressText += detail
                            prev = detail
                        }
                    }
                    return@fromCallable addressText.replace("'","")
                } else {
                    EventFactory.message("SiteDetailsInteractorImpl: loadAddress(): list is empty for $lat, $lng")
                }
            }
            return@fromCallable "нет данных"
        }
    }

    override fun getSavedComments(site: Site): Single<List<Comment>> {
        return dataBase.getComments(site)
    }

    override fun loadComments(site: Site): Single<List<Comment>> {
        return firebase.loadComments(site)
    }

    override fun saveComment(comment: Comment): Completable {
        return firebase.saveComment(comment)
//        return dataBase.saveComment(comment)
//                .onErrorComplete { true }
//                .andThen(firebase.saveComment(comment))
    }

    override fun getName() = settings.getName()

    override fun saveName(name: String) = settings.setName(name)
}