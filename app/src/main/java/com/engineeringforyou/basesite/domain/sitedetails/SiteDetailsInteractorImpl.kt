package com.engineeringforyou.basesite.domain.sitedetails

import android.content.Context
import android.location.Address
import android.location.Geocoder
import io.reactivex.Single
import java.io.IOException
import java.util.*

class SiteDetailsInteractorImpl(private val context: Context) : SiteDetailsInteractor {

    override fun loadAddress(lat: Double, lng: Double): Single<String> {
        return Single.fromCallable {
            var list: List<Address>? = null
            try {
                list = Geocoder(context).getFromLocation(lat, lng, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (list != null) {
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
            } else
                return@fromCallable "нет данных"
        }
    }
}