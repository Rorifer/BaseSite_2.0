package com.engineeringforyou.basesite.domain.sitecreate

import android.content.Context
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.models.User
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import io.reactivex.Completable
import java.util.*

class SiteCreateInteractorImpl(private val context: Context) : SiteCreateInteractor {

    private var dataBase: DataBaseRepository = DataBaseRepositoryImpl()
    private var firebase: FirebaseRepository = FirebaseRepositoryImpl()
    private var settings: SettingsRepository = SettingsRepositoryImpl(context)

    override fun saveSite(site: Site, userName: String): Completable {
        saveName(userName)
        val comment = Comment(site, "БС добавлена пользователем $userName", User(context, "автоматический"))
        comment.timestamp = site.timestamp
        return firebase.saveSiteAndComment(site, comment)
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

    override fun getName() = settings.getName()

    override fun saveName(name: String) = settings.setName(name)

}