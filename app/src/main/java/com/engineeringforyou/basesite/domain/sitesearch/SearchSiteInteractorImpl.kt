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

    override fun searchSitesByNumber(search: String): Single<List<Site>> =
            sitesRepository.searchSitesByNumber(getOperator(), search)

    override fun searchSitesByAddress(search: String): Single<List<Site>> =
            sitesRepository.searchSitesByAddress(getOperator(), search)

    override fun refreshSiteBase() = sitesDataBase.refreshDataBaseIfNeed()
}