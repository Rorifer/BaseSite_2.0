import android.content.Context
import com.engineeringforyou.basesite.models.Operator

class SettingsInteractorImpl(context: Context) : SettingsInteractor {

    private val settingsRepository: SettingsRepository

    init {
        settingsRepository = SettingsRepositoryImpl(context)
    }

    override fun saveOperator(operator: Operator) = settingsRepository.saveOperator(operator)

    override fun getOperator() = settingsRepository.getOperator()

}