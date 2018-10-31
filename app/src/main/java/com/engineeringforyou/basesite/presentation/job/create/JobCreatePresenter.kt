package com.engineeringforyou.basesite.presentation.job.create

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.job.JobInteractor
import com.engineeringforyou.basesite.domain.job.JobInteractorImpl
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

interface JobCreatePresenter {
    fun clickSiteLink()
    fun createJob(job: Job)
    fun clear()
    fun setLinkSite(site: Site)
}

class JobCreatePresenterImpl(val view: JobCreateView, val context: Context) : JobCreatePresenter {

    private val interactor: JobInteractor = JobInteractorImpl(context)
    private val disposable = CompositeDisposable()
    private var linkSite: Site? = null

    init {
        view.setContact(interactor.getContact())
    }

    override fun clickSiteLink() {
        view.openLinkSearch(linkSite)
    }

    override fun setLinkSite(site: Site) {
        this.linkSite = site
    }

    override fun createJob(job: Job) {
        var massageRes: Int? = null
        if (job.contact.isEmpty()) massageRes = R.string.not_contact
        if (job.description.isEmpty()) massageRes = R.string.not_description
        if (job.name.isEmpty()) massageRes = R.string.not_name
        if (massageRes != null) {
            view.showMessage(massageRes)
            return
        }

        job.setLinkSite(linkSite)

        disposable.add(interactor.createJob(job)
                .doOnSubscribe { view.showProgress() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent { view.hideProgress() }
                .subscribe(
                        { view.showMessage(R.string.job_created) },
                        { t ->
                            view.showMessage(R.string.error_job_created)
                            EventFactory.exception(t)
                        }))
    }

    override fun clear() {
        disposable.clear()
    }
}
