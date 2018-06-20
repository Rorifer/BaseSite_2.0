package com.engineeringforyou.basesite.presentation.sitecreate.presenter

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractor
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractorImpl
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.sitecreate.views.SiteCreateView
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SiteCreatePresenterImpl(context: Context) : SiteCreatePresenter {

    private var mView: SiteCreateView? = null
    private val mDisposable = CompositeDisposable()
    private val mInteractor: SiteCreateInteractor = SiteCreateInteractorImpl(context)

    override fun bind(view: SiteCreateView) {
        mView = view
    }

    override fun saveSite(site: Site, userName: String) {
//        if (site.number.orEmpty().isEmpty()) mView?.showMessage(R.string.error_site_number) else
        if (site.latitude == null || site.longitude == null) mView?.showMessage(R.string.error_site_coordinates)
        else {
            mView?.showProgress()
            mDisposable.clear()
            mDisposable.add(mInteractor.saveSite(site, userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::saveSuccess, this::saveError))
        }
    }

    override fun setupView() {
        mView?.setName(mInteractor.getName())
    }

    private fun saveSuccess() {
        mView?.showMessage(R.string.site_saved)
        mDisposable.clear()
        mDisposable.add(mInteractor.refreshDataBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshSuccess, this::refreshError))
    }

    private fun refreshSuccess(){
        mView?.showMessage(R.string.sites_refresh)
        mView?.hideProgress()
        mView?.close()
    }

    private fun refreshError(throwable: Throwable) {
        EventFactory.exception(throwable)
        mView?.hideProgress()
        mView?.showMessage(R.string.error_refresh_site)
        mView?.close()
    }

    private fun saveError(throwable: Throwable) {
        EventFactory.exception(throwable)
            mView?.hideProgress()
            mView?.showMessage(R.string.error_site_save)
    }

    override fun unbindView() {
        mDisposable.dispose()
        mView = null
    }
}