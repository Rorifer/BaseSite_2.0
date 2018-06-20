package com.engineeringforyou.basesite.domain.sitesdata

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

class NetworkInteractorImpl(context: Context) : NetworkInteractor {

    private var dataBase: DataBaseRepository = DataBaseRepositoryImpl()
    private var firebase: FirebaseRepository = FirebaseRepositoryImpl()
    private var settings: SettingsRepository = SettingsRepositoryImpl(context)


    override fun saveSite(site: Site): Completable {
        return firebase.saveSite(site)
    }

    override fun refreshDataBase(): Completable {
        val timestamp = Date().time
        return firebase.loadSites(settings.getSitesTimestamp())
                .filter { it.isNotEmpty() }
                .flatMapCompletable { sites -> dataBase.saveSites(sites) }
                .doOnComplete { settings.saveSitesTimestamp(timestamp) }
    }

    override fun refreshDataBaseIfNeed(): Completable {
        return if (Date().time > settings.getSitesTimestamp() + 10 * 60 * 1000)
            refreshDataBase()
        else Completable.complete()
    }
}