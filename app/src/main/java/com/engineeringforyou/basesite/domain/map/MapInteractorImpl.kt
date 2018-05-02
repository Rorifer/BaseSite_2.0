package com.engineeringforyou.basesite.domain.map

import android.content.Context
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.repositories.sites.SitesRepository
import com.engineeringforyou.basesite.repositories.sites.SitesRepositoryImpl

class MapInteractorImpl(context: Context) : MapInteractor {

    private val settingsRepository: SettingsRepository
    private val baseSitesRepository: SitesRepository

    init {
        settingsRepository = SettingsRepositoryImpl(context)
        baseSitesRepository = SitesRepositoryImpl(context)
    }

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

}