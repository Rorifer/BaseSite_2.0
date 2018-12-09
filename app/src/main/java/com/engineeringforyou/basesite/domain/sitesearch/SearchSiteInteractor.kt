package com.engineeringforyou.basesite.domain.sitesearch

import android.content.Context
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractor
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractorImpl
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.utils.DateUtils
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface SearchSiteInteractor {
    fun saveOperator(operator: Operator)
    fun getOperator(): Operator
    fun searchSitesByNumber(search: String, operator: Operator? = null): Single<List<Site>>
    fun searchSitesByAddress(search: String, operator: Operator? = null): Single<List<Site>>
    fun refreshSiteBase(): Completable
    fun getInfo(): Single<String>
    fun needingShowJobFunction(): Maybe<Boolean>
}

class SearchSiteInteractorImpl(context: Context) : SearchSiteInteractor {

    private val settingsRepository: SettingsRepository = SettingsRepositoryImpl(context)
    private val sitesRepository: DataBaseRepository = DataBaseRepositoryImpl()
    private val firebaseRepository: FirebaseRepository = FirebaseRepositoryImpl()
    private val sitesDataBase: SiteCreateInteractor = SiteCreateInteractorImpl(context)

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

    override fun searchSitesByNumber(search: String, operator: Operator?): Single<List<Site>> =
            sitesRepository.searchSitesByNumber(operator ?: getOperator(), search)

    override fun searchSitesByAddress(search: String, operator: Operator?): Single<List<Site>> =
            sitesRepository.searchSitesByAddress(operator ?: getOperator(), search)

    override fun refreshSiteBase() = sitesDataBase.refreshDataBaseIfNeed()

    override fun getInfo() =
            sitesRepository.getStatistic()
                    .map { "$it\nБаза актуальна на: ${DateUtils.parseDateHourMinuteDay(settingsRepository.getSitesTimestamp())}" }

    override fun needingShowJobFunction(): Maybe<Boolean> {
        return Single.fromCallable { settingsRepository.getShowingJobFunction() }
                .filter { !it }
                .flatMapSingle { firebaseRepository.loadListPublicJob() }
                .map { it.isNotEmpty() }
                .filter { it }
                .doOnSuccess { settingsRepository.setShowingJobFunction() }
    }
}