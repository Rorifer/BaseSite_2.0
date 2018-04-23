package com.engineeringforyou.basesite.domain.settings

import android.content.Context
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl

public class SettingsInteractorImpl(context: Context) : SettingsInteractor {

    private val settingsRepository: SettingsRepository

    init {
        settingsRepository = SettingsRepositoryImpl(context)
    }

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

}