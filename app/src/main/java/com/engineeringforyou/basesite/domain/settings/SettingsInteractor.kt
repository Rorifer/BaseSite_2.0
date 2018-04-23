import com.engineeringforyou.basesite.models.Operator

interface SettingsInteractor {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

}