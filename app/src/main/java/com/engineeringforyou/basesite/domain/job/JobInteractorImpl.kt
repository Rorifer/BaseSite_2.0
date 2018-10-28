package com.engineeringforyou.basesite.domain.job

import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface JobInteractor {
    fun getStatusNotification(): Single<Boolean>
    fun setStatusNotification(checked: Boolean): Completable
    fun loadJobList(): Single<List<Job>>
    fun getContact(): String
    fun createJob(job: Job): Completable
    fun getLinkSite(operator: Operator?, siteUid: String?): Site?
}

class JobInteractorImpl() : JobInteractor {
    override fun getLinkSite(operator: Operator?, siteUid: String?): Site? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createJob(job: Job): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        // сохранять контакты

    }

    override fun getContact(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadJobList(): Single<List<Job>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setStatusNotification(checked: Boolean): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStatusNotification(): Single<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}