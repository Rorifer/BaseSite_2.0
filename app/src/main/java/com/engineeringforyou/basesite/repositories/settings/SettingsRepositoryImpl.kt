package com.engineeringforyou.basesite.repositories.settings

import android.content.Context
import android.preference.PreferenceManager
import com.engineeringforyou.basesite.models.Operator
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    companion object {
        const val INDEX_DEFAULT = 0
        const val RADIUS_DEFAULT = 3
        const val TIMESTAMP_DEFAULT = 0L
        const val MAP_TYPE_DEFAULT = MAP_TYPE_NORMAL
        const val MAP_TYPE_MAX = MAP_TYPE_HYBRID
        private const val KEY_RADIUS = "key_radius"
        private const val KEY_MAP_TYPE = "key_map_type"
        private const val KEY_OPERATOR = "key_operator"
        private const val KEY_MAP_COUNTER = "key_map_counter"
        private const val KEY_COMMENT_TIMESTAMP = "key_comment_timestamp"
        private const val KEY_NAME = "key_name"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun saveOperator(operator: Operator) = prefs.edit().putInt(KEY_OPERATOR, operator.ordinal).apply()

    override fun saveMapType(mapType: Int) = prefs.edit().putInt(KEY_MAP_TYPE, mapType).apply()

    override fun saveRadius(radius: Int) = prefs.edit().putInt(KEY_RADIUS, radius).apply()

    override fun getOperator(): Operator {
        return try {
            var operatorIndex = prefs.getInt(KEY_OPERATOR, INDEX_DEFAULT)
            if (operatorIndex >= Operator.values().size || operatorIndex < 0) operatorIndex = INDEX_DEFAULT
            Operator.values()[operatorIndex]
        } catch (e: ClassCastException) {
            Operator.values()[INDEX_DEFAULT]
        }
    }

    override fun getMapType(): Int {
        return try {
            var mapType = prefs.getInt(KEY_MAP_TYPE, MAP_TYPE_DEFAULT)
            if (mapType > MAP_TYPE_MAX || mapType < 0) mapType = MAP_TYPE_DEFAULT
            mapType
        } catch (e: ClassCastException) {
            MAP_TYPE_DEFAULT
        }
    }

    override fun getRadius(): Int {
        return try {
            var radius = prefs.getInt(KEY_RADIUS, RADIUS_DEFAULT)
            if (radius < 0) radius = RADIUS_DEFAULT
            radius
        } catch (e: ClassCastException) {
            RADIUS_DEFAULT
        }
    }

    override fun addCountMapCreate() = prefs.edit().putInt(KEY_MAP_COUNTER, getCountMapCreate() + 1).apply()

    override fun getCountMapCreate(): Int {
        return try {
            prefs.getInt(KEY_MAP_COUNTER, 0)
        } catch (e: ClassCastException) {
            0
        }
    }

    override fun saveCommentTimestamp(timestamp: Long) = prefs.edit().putLong(KEY_COMMENT_TIMESTAMP, timestamp).apply()

    override fun getCommentTimestamp(): Long {
        return try {
            prefs.getLong(KEY_COMMENT_TIMESTAMP, TIMESTAMP_DEFAULT)
        } catch (e: ClassCastException) {
            TIMESTAMP_DEFAULT
        }
    }

    override fun setName(name: String) = prefs.edit().putString(KEY_NAME, name).apply()

    override fun getName(): String {
        return try {
            prefs.getString(KEY_NAME, "")
        } catch (e: ClassCastException) {
            ""
        }
    }
}