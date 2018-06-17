package com.engineeringforyou.basesite.presentation.sitedraft.presenter

import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.sitedraft.SiteDraftInteractor
import com.engineeringforyou.basesite.domain.sitedraft.SiteDraftInteractorImpl
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.sitedraft.views.SiteDraftView
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SiteDraftPresenterImpl : SiteDraftPresenter {

    private var mView: SiteDraftView? = null
    private val mDisposable = CompositeDisposable()
    private val mInteractor: SiteDraftInteractor = SiteDraftInteractorImpl()

    override fun bind(view: SiteDraftView) {
        mView = view
    }

    override fun saveDraft(site: Site) {
        if (site.number.orEmpty().isEmpty()) mView?.showMessage(R.string.error_site_number)
        else if (site.latitude == null || site.longitude == null) mView?.showMessage(R.string.error_site_coordinates)
        else {
            mView?.showProgress()
            mDisposable.add(mInteractor.saveSite(site)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::saveSuccess, this::saveError))
        }
    }

    private fun saveSuccess() {
        mView?.hideProgress()
        mView?.showMessage(R.string.site_saved)
        mView?.close()

    }

    private fun saveError(throwable: Throwable) {
        EventFactory.exception(throwable)
        if (mView != null) {
            mView?.hideProgress()
            mView?.showMessage(R.string.error_site_save)
        }
    }

    override fun unbindView() {
        mDisposable.dispose()
        mView = null
    }
}