package com.engineeringforyou.basesite.repositories.settings

import com.engineeringforyou.basesite.models.Operator

interface SettingsRepository {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

    fun saveMapType(mapType: Int)

    fun getMapType(): Int

    fun saveRadius(radius: Int)

    fun getRadius(): Int

    fun addCountMapCreate()

    fun getCountMapCreate(): Int

    fun saveSitesTimestamp(timestamp: Long)

    fun getSitesTimestamp(): Long

    fun getName(): String

    fun setName(name: String)

    fun saveContact(contact: String)

    fun getContact(): String

    fun saveStatusNotification(isEnabled: Boolean)

    fun getStatusNotification(): Boolean

    fun setShowingJobFunction()

    fun getShowingJobFunction(): Boolean

}