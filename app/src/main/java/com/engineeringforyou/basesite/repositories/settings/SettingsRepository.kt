import com.engineeringforyou.basesite.models.Operator

interface SettingsRepository {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

}