package com.engineeringforyou.basesite.presentation.job.setting

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.job.JobInteractor
import com.engineeringforyou.basesite.domain.job.JobInteractorImpl
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

interface JobSettingsPresenter {
    fun checkState()
    fun switchNotification(checked: Boolean)
    fun clear()
}

class JobSettingsPresenterImpl(val view: JobSettingsView, val context: Context) : JobSettingsPresenter {

    private val interactor: JobInteractor = JobInteractorImpl(context)
    private val disposable = CompositeDisposable()

    override fun checkState() {
        view.setCheckedNotificationSwitch(interactor.getStatusNotification())
        view.enableCheckListener()
    }

    override fun switchNotification(checked: Boolean) {
        disposable.add(interactor.setStatusNotification(checked)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showProgress() }
                .doOnEvent { view.hideProgress() }
                .subscribe(
                        { view.setCheckedNotificationSwitch(checked) },
                        { t ->
                            view.setCheckedNotificationSwitch(!checked)
                            view.showError(R.string.error_switch_notification)
                            EventFactory.exception(t)
                        }
                )
        )
    }

    override fun clear() {
        disposable.clear()
    }
}
