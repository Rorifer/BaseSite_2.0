package com.engineeringforyou.basesite.repositories.settings

import com.engineeringforyou.basesite.models.Operator

interface SettingsRepository {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

    fun saveMapType(mapType: Int)

    fun getMapType(): Int

    fun saveRadius (radius: Int)

    fun getRadius(): Int

    fun addCountMapCreate()

    fun getCountMapCreate(): Int

    fun saveCommentTimestamp (timestamp: Long)

    fun getCommentTimestamp (): Long

}