package com.engineeringforyou.basesite.domain.job

import android.content.Context
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.repositories.database.DataBaseRepository
import com.engineeringforyou.basesite.repositories.database.DataBaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepository
import com.engineeringforyou.basesite.repositories.firebase.FirebaseRepositoryImpl
import com.engineeringforyou.basesite.repositories.settings.SettingsRepository
import com.engineeringforyou.basesite.repositories.settings.SettingsRepositoryImpl
import io.reactivex.Completable
import io.reactivex.Single

interface JobInteractor {
    fun getStatusNotification(): Boolean
    fun setStatusNotification(isEnabled: Boolean): Completable
    fun loadJobList(onlyUserList: Boolean): Single<List<Job>>
    fun getContact(): String
    fun createJob(job: Job): Completable
    fun getLinkSite(operator: Operator, siteUid: String): Single<Site>
}

class JobInteractorImpl(val context: Context) : JobInteractor {

    private val firebase: FirebaseRepository = FirebaseRepositoryImpl()
    private val settings: SettingsRepository = SettingsRepositoryImpl(context)
    private val sitesRepository: DataBaseRepository = DataBaseRepositoryImpl()

    override fun getLinkSite(operator: Operator, siteUid: String): Single<Site> {
        return sitesRepository.searchSitesByUid(operator, siteUid)
    }

    override fun createJob(job: Job): Completable {
        settings.saveContact(job.contact)
        return firebase.saveJob(job)
    }

    override fun getContact(): String {
        return settings.getContact()
    }

    override fun loadJobList(onlyUserList: Boolean): Single<List<Job>> {
        return if (onlyUserList)
            firebase.loadListPublicJob()
        else
            firebase.loadListUserJob()
    }

    override fun setStatusNotification(isEnabled: Boolean): Completable {
        val switch = if (isEnabled)
            firebase.enableStatusNotification()
        else
            firebase.disableStatusNotification()
        return switch
                .doOnComplete { settings.saveStatusNotification(isEnabled) }
    }

    override fun getStatusNotification(): Boolean {
        return settings.getStatusNotification()
    }

}