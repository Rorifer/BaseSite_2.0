package com.engineeringforyou.basesite.domain.sitemap

import android.content.Context
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import io.reactivex.Observable

class MapInteractorImpl(context: Context) : MapInteractor {

    private val settingsRepository: SettingsRepository = SettingsRepositoryImpl(context)
    private val sitesRepository: DataBaseRepository = DataBaseRepositoryImpl()
    private val firebase: FirebaseRepository = FirebaseRepositoryImpl()

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

    override fun saveMapType(mapType: Int) = settingsRepository.saveMapType(mapType)

    override fun getMapType() = settingsRepository.getMapType()

    override fun saveRadius(radius: Int) = settingsRepository.saveRadius(radius)

    override fun getRadius() = settingsRepository.getRadius()

    override fun getSites(lat: Double, lng: Double): Observable<List<Site>> {
        val operator = getOperator()
        val radius: Int = getRadius()

        if (radius == 0) {

            return if (operator != Operator.ALL) {
                sitesRepository.getAllSites(operator).toObservable()
            } else {
                Observable.create<List<Site>> { e ->
                    sitesRepository.getAllSites(Operator.MTS).doOnSuccess { e.onNext(it) }.subscribe()
                    sitesRepository.getAllSites(Operator.MEGAFON).doOnSuccess { e.onNext(it) }.subscribe()
                    sitesRepository.getAllSites(Operator.VIMPELCOM).doOnSuccess { e.onNext(it) }.subscribe()
                    sitesRepository.getAllSites(Operator.TELE2).doOnSuccess { e.onNext(it) }.subscribe()
//                    e.onNext(sitesRepository.getAllSites(Operator.MTS).blockingGet())
//                    e.onNext(sitesRepository.getAllSites(Operator.MEGAFON).blockingGet())
//                    e.onNext(sitesRepository.getAllSites(Operator.VIMPELCOM).blockingGet())
//                    e.onNext(sitesRepository.getAllSites(Operator.TELE2).blockingGet())
                    e.onComplete()
                }
            }

        } else {

            return if (operator != Operator.ALL) {
                sitesRepository.searchSitesByLocation(operator, lat, lng, radius).toObservable()
            } else {
                Observable.create<List<Site>> { e ->
                    e.onNext(sitesRepository.searchSitesByLocation(Operator.MTS, lat, lng, radius).blockingGet())
                    e.onNext(sitesRepository.searchSitesByLocation(Operator.MEGAFON, lat, lng, radius).blockingGet())
                    e.onNext(sitesRepository.searchSitesByLocation(Operator.VIMPELCOM, lat, lng, radius).blockingGet())
                    e.onNext(sitesRepository.searchSitesByLocation(Operator.TELE2, lat, lng, radius).blockingGet())
                    e.onComplete()
                }
            }
        }
    }

    override fun getAllSites() = sitesRepository.getAllSites(getOperator())

    override fun addCountMapCreate() = settingsRepository.addCountMapCreate()

    override fun getCountMapCreate() = settingsRepository.getCountMapCreate()

    override fun loadJobs() = firebase.loadListPublicJob()
}