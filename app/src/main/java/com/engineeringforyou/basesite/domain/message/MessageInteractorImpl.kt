package com.engineeringforyou.basesite.domain.message

import android.content.Context
import com.engineeringforyou.basesite.models.Message
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.utils.Utils
import io.reactivex.Completable

class MessageInteractorImpl(private val context: Context) : MessageInteractor {

    private var firebase: FirebaseRepository = FirebaseRepositoryImpl()
    private var settings: SettingsRepository = SettingsRepositoryImpl(context)

    override fun sendMessage(email: String, message: String): Completable {

        val model = Message(
                email,
                message,
                Utils.getAndroidId(context),
                settings.getCountMapCreate(),
                Utils.getCurrentTime(),
                Utils.getCurrentDate())

        return firebase.saveMessage(model)
    }
}