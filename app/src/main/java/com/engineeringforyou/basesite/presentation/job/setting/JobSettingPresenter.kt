package com.engineeringforyou.basesite.presentation.job.setting

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

class JobSettingsPresenterImpl(val view: JobSettingsView) : JobSettingsPresenter {

    private val interactor: JobInteractor = JobInteractorImpl()
    private val disposable = CompositeDisposable()

    override fun checkState() {
        disposable.add(interactor.getStatusNotification()
                .subscribe(
                        { checked -> view.setCheckedNotificationSwitch(checked) },
                        { t -> EventFactory.exception(t) })
        )
    }

    override fun switchNotification(checked: Boolean) {
        disposable.add(interactor.setStatusNotification(checked)
                .doOnSubscribe { view.showProgress() }
                .doOnEvent { view.hideProgress() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
