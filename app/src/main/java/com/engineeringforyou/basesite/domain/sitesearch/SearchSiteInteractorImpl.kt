package com.engineeringforyou.basesite.domain.sitesearch

import android.content.Context
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractor
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractorImpl
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.utils.Utils
import io.reactivex.Single

class SearchSiteInteractorImpl(context: Context) : SearchSiteInteractor {

    private val settingsRepository: SettingsRepository
    private val sitesRepository: DataBaseRepository
    private val sitesDataBase: SiteCreateInteractor

    init {
        settingsRepository = SettingsRepositoryImpl(context)
        sitesDataBase = SiteCreateInteractorImpl(context)
        sitesRepository = DataBaseRepositoryImpl()
    }

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

    override fun searchSitesByNumber(search: String, operator: Operator?): Single<List<Site>> =
            sitesRepository.searchSitesByNumber(operator ?: getOperator(), search)

    override fun searchSitesByAddress(search: String, operator: Operator?): Single<List<Site>> =
            sitesRepository.searchSitesByAddress(operator ?: getOperator(), search)

    override fun refreshSiteBase() = sitesDataBase.refreshDataBaseIfNeed()

    override fun getInfo() = sitesRepository.getStatistic()

    override fun disableAdvertising() =
            settingsRepository.setTimeEnableAdvertising(Utils.getCurrentTime() + 92 * 86_400_000L)

}