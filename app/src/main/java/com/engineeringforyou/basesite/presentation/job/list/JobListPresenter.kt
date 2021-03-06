package com.engineeringforyou.basesite.presentation.job.list

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.job.JobInteractor
import com.engineeringforyou.basesite.domain.job.JobInteractorImpl
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

interface JobListPresenter {
    fun clickMapJob()
    fun clear()
    fun loadJobList()
    fun callJobDetails()
}

class JobListPresenterImpl(val view: JobListView, val context: Context, private val isAdminStatus: Boolean) : JobListPresenter {

    private val interactor: JobInteractor = JobInteractorImpl(context)
    private val disposable = CompositeDisposable()
    private lateinit var jobList: List<Job>

    override fun loadJobList() {
        disposable.clear()
        disposable.add(interactor.loadJobList(isAdminStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showRefresh() }
                .doOnEvent { _, _ -> view.hideRefresh() }
                .subscribe(
                        { list ->
                            jobList = list
                            if (list.isEmpty())
                                view.showMessage(R.string.empty_job_notification)
                            else
                                view.showJobList(list)
                        },
                        { t ->
                            view.showMessage(R.string.error_load_job_notification)
                            EventFactory.exception(t)
                        }
                )
        )
    }

    override fun clickMapJob() {
        val jobs = jobList.filter { it.latitude != null && it.longitude != null }
        if (jobs.isEmpty()) view.showError(R.string.no_job_map)
        else view.showJobMap(jobs)
    }

    override fun callJobDetails() {
        interactor.setShowingJobFunction()
    }

    override fun clear() {
        disposable.clear()
    }

}
