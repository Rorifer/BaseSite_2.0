package com.engineeringforyou.basesite.presentation.message.presenter

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.message.MessageInteractor
import com.engineeringforyou.basesite.domain.message.MessageInteractorImpl
import com.engineeringforyou.basesite.presentation.message.views.MessageView
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MessagePresenterImpl(context: Context) : MessagePresenter {

    private var mView: MessageView? = null
    private val mDisposable = CompositeDisposable()
    private val mInteractor: MessageInteractor = MessageInteractorImpl(context)

    override fun bind(view: MessageView) {
        mView = view
    }

    override fun sendMessage(email: String, message: String) {
        mView?.showProgress()
        mDisposable.clear()
        mDisposable.add(mInteractor.sendMessage(email, message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::sendSuccess, this::sendError))
    }

    private fun sendSuccess() {
        mView?.showMessage(R.string.message_send_success)
        mView?.hideProgress()
        mView?.close()
    }

    private fun sendError(throwable: Throwable) {
        EventFactory.exception(throwable)
        mView?.hideProgress()
        mView?.showMessage(R.string.message_send_error)
    }

    override fun unbindView() {
        mDisposable.dispose()
        mView = null
    }
}