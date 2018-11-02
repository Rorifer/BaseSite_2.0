package com.engineeringforyou.basesite.presentation.job.create

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.job.JobInteractor
import com.engineeringforyou.basesite.domain.job.JobInteractorImpl
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

interface JobCreatePresenter {
    fun clear()
    fun clickSiteLink()
    fun setLinkSite(site: Site)
    fun setLinkSite(linkSiteOperator: Operator?, linkSiteUid: String?)
    fun createJob(job: Job)
    fun closeJob(id: String)
    fun editJob(job: Job)
    fun publicJob(id: String)
}

class JobCreatePresenterImpl(val view: JobCreateView?, val context: Context) : JobCreatePresenter {

    private val interactor: JobInteractor = JobInteractorImpl(context)
    private val disposable = CompositeDisposable()
    private var linkSite: Site? = null

    init {
        view?.setContact(interactor.getContact())
    }

    override fun clickSiteLink() {
        view?.openLinkSearch(linkSite)
    }

    override fun setLinkSite(site: Site) {
        this.linkSite = site
    }

    override fun setLinkSite(linkSiteOperator: Operator?, linkSiteUid: String?) {
        if (linkSiteOperator == null || linkSiteUid == null) return
        disposable.add(interactor.getLinkSite(linkSiteOperator, linkSiteUid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { if (linkSite == null) linkSite = it },
                        { EventFactory.exception(it) }))
    }

    override fun closeJob(id: String) {
        disposable.clear()
        disposable.add(interactor.closeJob(id)
                .loadInBackground()
                .subscribe(
                        { view?.showMessage(R.string.job_closed) },
                        { t ->
                            view?.showMessage(R.string.error_job_closed)
                            EventFactory.exception(t)
                        }))
    }

    override fun publicJob(id: String) {
        disposable.clear()
        disposable.add(interactor.publicJob(id)
                .loadInBackground()
                .subscribe(
                        { view?.showMessage(R.string.job_publiced) },
                        { t ->
                            view?.showMessage(R.string.error_public)
                            EventFactory.exception(t)
                        }))
    }

    override fun editJob(job: Job) {
        if (!isValidFields(job)) return

        disposable.clear()
        disposable.add(interactor.editJob(job)
                .loadInBackground()
                .subscribe(
                        { view?.showMessage(R.string.job_edited) },
                        { t ->
                            view?.showMessage(R.string.error_job_edited)
                            EventFactory.exception(t)
                        }))
    }

    private fun isValidFields(job: Job): Boolean {
        var massageRes: Int? = null
        if (job.contact.isEmpty()) massageRes = R.string.not_contact
        if (job.description.isEmpty()) massageRes = R.string.not_description
        if (job.name.isEmpty()) massageRes = R.string.not_name

        if (massageRes != null) {
            view?.showMessage(massageRes)
            return false
        }
        job.setLinkSite(linkSite)
        return true
    }

    override fun createJob(job: Job) {
        if (!isValidFields(job)) return

        disposable.clear()
        disposable.add(interactor.createJob(job)
                .loadInBackground()
                .subscribe(
                        { view?.showMessage(R.string.job_created) },
                        { t ->
                            view?.showMessage(R.string.error_job_created)
                            EventFactory.exception(t)
                        }))
    }

    private fun Completable.loadInBackground(): Completable {
        return this.doOnSubscribe { view?.showProgress() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent { view?.close() }
    }

    override fun clear() {
        disposable.clear()
    }
}
