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
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.Single
import java.io.IOException
import java.util.*

class SiteDetailsInteractorImpl(private val context: Context) : SiteDetailsInteractor {

    private lateinit var site: Site

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
                    return@fromCallable addressText
                } else {
                    EventFactory.message("SiteDetailsInteractorImpl: loadAddress(): list is empty for $lat, $lng")
                }
            }
            return@fromCallable "нет данных"
        }
    }

    override fun getSavedComments(site: Site): Single<List<Comment>> {
        val dataBase: DataBaseRepository = DataBaseRepositoryImpl()
        return dataBase.getComments(site)
    }

    override fun loadComments(): Single<List<Comment>> {
        val firebase: FirebaseRepository = FirebaseRepositoryImpl()
        return firebase.getComments(site)
    }
}