package com.engineeringforyou.basesite.domain.sitedraft

import android.content.Context
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import io.reactivex.Completable
import java.util.*

class SiteDraftInteractorImpl(context: Context) : SiteDraftInteractor {

    private var dataBase: DataBaseRepository = DataBaseRepositoryImpl()
    private var firebase: FirebaseRepository = FirebaseRepositoryImpl()
    private var settings: SettingsRepository = SettingsRepositoryImpl(context)


    override fun saveSite(site: Site): Completable {
        return firebase.saveSite(site)
    }

    override fun refreshDataBase(): Completable {

//        return Completable.complete()
        val timestamp = Date().time
        return firebase.loadSites(settings.getSitesTimestamp())
                .filter { it.isNotEmpty() }
                .flatMapCompletable { sites -> dataBase.saveSites(sites) }
                .doOnComplete { settings.saveSitesTimestamp(timestamp) }
    }
}