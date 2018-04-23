import android.content.Context
import android.preference.PreferenceManager
import com.engineeringforyou.basesite.models.Operator

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    companion object {
        const val DEFAULT_INDEX = 0
        private const val KEY = "key_operator"
    }


    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getOperator(): Operator {
        return try {
            var operatorIndex = prefs.getInt(KEY, DEFAULT_INDEX)

            if (operatorIndex >= Operator.values().size || operatorIndex < 0) {
                operatorIndex = DEFAULT_INDEX
            }

            Operator.values()[operatorIndex]
        } catch (e: ClassCastException) {
            Operator.values()[DEFAULT_INDEX]
        }
    }

    override fun saveOperator(operator: Operator) = prefs.edit().putInt(KEY, operator.ordinal).apply()
}