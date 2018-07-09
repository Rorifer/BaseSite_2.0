package com.engineeringforyou.basesite.presentation.sitecreate.presenter

import android.content.Context
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractor
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractorImpl
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.models.Status
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
        mView?.showProgress()
        mDisposable.clear()
        mDisposable.add(mInteractor.saveSite(site, userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveSuccess, this::saveError))
    }

    override fun editSite(oldSite: Site, site: Site, userName: String) {
        val comment = commentForEdit(oldSite, site, userName)
        if (comment.isEmpty()) {
            mView?.showMessage(R.string.edit_site_no_changes)
            return
        }
        mView?.showProgress()
        mDisposable.clear()
        mDisposable.add(mInteractor.editSite(site, oldSite, comment, userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::editSuccess, this::editError))
    }

    private fun editSuccess() {
        mDisposable.clear()
        mDisposable.add(mInteractor.refreshDataBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshEditSuccess, this::refreshEditError))
    }

    private fun refreshEditSuccess() {
        mView?.showMessage(R.string.sites_edit_refresh)
        mView?.hideProgress()
        mView?.close()
    }

    private fun refreshEditError(throwable: Throwable) {
        EventFactory.exception(throwable)
        refreshEditSuccess()
    }

    private fun editError(throwable: Throwable) {
        EventFactory.exception(throwable)
        mView?.hideProgress()
        mView?.showMessage(R.string.error_site_edit)
    }

    private fun commentForEdit(oldSite: Site, site: Site, userName: String): String {
        val comment: String = "".plus(checkEditField(oldSite.number, site.number, "номер БС"))
                .plus(checkEditField(oldSite.address, site.address, "адрес"))
                .plus(checkEditField(oldSite.obj, site.obj, "объект"))
                .plus(if (oldSite.longitude != site.longitude || oldSite.latitude != site.latitude) ", изменены координаты " +
                        "с '${oldSite.longitude} , ${oldSite.latitude}' на '${site.longitude} , ${site.latitude}'" else "")
                .plus(if (oldSite.statusId != null && oldSite.statusId != site.statusId || oldSite.statusId == null && site.statusId != Status.ACTIVE.ordinal)
                    ", изменен статус БС на '${Status.values()[site.statusId!!].description}'" else "")

        return if (comment.isNotEmpty()) comment.removePrefix(", ").capitalize().plus(" пользователем $userName")
        else ""
    }

    private fun checkEditField(old: String?, new: String?, value: String): String {
        if (old.isNullOrEmpty() && new.isNullOrEmpty().not()) return ", добавлен $value '$new'"
        if (old.isNullOrEmpty().not() && new.isNullOrEmpty()) return ", удален $value '$old'"
        if (old != new) return ", изменен $value с '$old' на '$new'"
        return ""
    }

    override fun setupView() {
        mView?.setName(mInteractor.getName())
    }

    private fun saveSuccess() {
//        mView?.showMessage(R.string.site_saved)
        mDisposable.clear()
        mDisposable.add(mInteractor.refreshDataBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshSuccess, this::refreshError))
    }

    private fun refreshSuccess() {
        mView?.showMessage(R.string.sites_refresh)
        mView?.hideProgress()
        mView?.close()
    }

    private fun refreshError(throwable: Throwable) {
        EventFactory.exception(throwable)
        refreshSuccess()
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