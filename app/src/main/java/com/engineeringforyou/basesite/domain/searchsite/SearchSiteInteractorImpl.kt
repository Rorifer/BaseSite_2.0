package com.engineeringforyou.basesite.domain.searchsite

import android.content.Context
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import io.reactivex.Single

class SearchSiteInteractorImpl(context: Context) : SearchSiteInteractor {

    private val settingsRepository: SettingsRepository
    private val sitesRepository: DataBaseRepository

    init {
        settingsRepository = SettingsRepositoryImpl(context)
        sitesRepository = DataBaseRepositoryImpl()
    }

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

    override fun searchSitesByNumber(search: String): Single<List<Site>> =
            sitesRepository.searchSitesByNumber(getOperator(), search)

    override fun searchSitesByAddress(search: String): Single<List<Site>> =
            sitesRepository.searchSitesByAddress(getOperator(), search)
}