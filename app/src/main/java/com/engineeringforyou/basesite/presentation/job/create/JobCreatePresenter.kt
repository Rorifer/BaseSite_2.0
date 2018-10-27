package com.engineeringforyou.basesite.presentation.job.create

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

class JobCreatePresenterImpl(val view: JobCreateView) : JobCreatePresenter {

    private val interactor: JobInteractor = JobInteractorImpl()
    private val disposable = CompositeDisposable()
    private var site: Site? = null

    init {
        view.setContact(interactor.getContact())
    }

    override fun clickSiteLink() {
        view.openLinkSearch(site)
    }

    override fun setLinkSite(site: Site) {
        this.site = site
    }

    override fun createJob(job: Job) {
        if (job.contact.isEmpty()) {
            view.showMessage(R.string.not_contact)
            return
        }
        if (job.description.isEmpty()) {
            view.showMessage(R.string.not_description)
            return
        }

        job.linkSiteUid = site?.uid
        job.linkSiteOperator = site?.operator

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
