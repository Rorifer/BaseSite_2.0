package com.engineeringforyou.basesite.domain.sitedraft

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.Completable
import io.reactivex.Single
import java.io.IOException
import java.util.*

class SiteDraftInteractorImpl: SiteDraftInteractor {

    private var dataBase: DataBaseRepository = DataBaseRepositoryImpl()
    private var firebase: FirebaseRepository = FirebaseRepositoryImpl()

    override fun saveSite(site: Site): Completable {
        return firebase.saveSite(site)
    }

}