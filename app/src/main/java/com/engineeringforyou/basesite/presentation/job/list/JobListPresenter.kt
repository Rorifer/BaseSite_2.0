package com.engineeringforyou.basesite.presentation.job.list

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.job.JobInteractor
import com.engineeringforyou.basesite.domain.job.JobInteractorImpl
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

interface JobListPresenter {
    fun clear()
    fun loadJobList()
}

class JobListPresenterImpl(val view: JobListView, val context: Context, private val onlyUserList: Boolean) : JobListPresenter {

    private val interactor: JobInteractor = JobInteractorImpl(context)
    private val disposable = CompositeDisposable()

    init {
        view.showRefresh()
        loadJobList()
    }

    override fun loadJobList() {
        disposable.add(interactor.loadJobList(onlyUserList)
                .doOnSubscribe { view.showRefresh() }
                .doOnEvent { _, _ -> view.hideRefresh() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { list ->
                            if (list.isEmpty()) view.showMessage(R.string.empty_job_notification)
                            else view.showJobList(list)
                        },
                        { t ->
                            view.showMessage(R.string.error_load_job_notification)
                            EventFactory.exception(t)
                        }
                )
        )
    }

    override fun clear() {
        disposable.clear()
    }
}
