package com.engineeringforyou.basesite.presentation.sitecreate.presenter

import android.content.Context
import android.net.Uri
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractor
import com.engineeringforyou.basesite.domain.sitecreate.SiteCreateInteractorImpl
import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractor
import com.engineeringforyou.basesite.domain.sitedetails.SiteDetailsInteractorImpl
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.models.Status
import com.engineeringforyou.basesite.presentation.sitecreate.views.SiteCreateView
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File


class SiteCreatePresenterImpl(val context: Context) : SiteCreatePresenter {

    private var mView: SiteCreateView? = null
    private val mDisposable = CompositeDisposable()
    private val mInteractor: SiteCreateInteractor = SiteCreateInteractorImpl(context)
    private val mAddressInteractor: SiteDetailsInteractor = SiteDetailsInteractorImpl(context)

    override fun bind(view: SiteCreateView) {
        mView = view
    }

    override fun saveSite(site: Site, photoUriList: List<Uri>, userName: String) {
        if (checkPhotos(photoUriList).not()) return
        mView?.showProgress()
        mDisposable.clear()
        mDisposable.add(mInteractor.saveSite(site, photoUriList, userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveSuccess, this::saveError))
    }

    override fun editSite(oldSite: Site, site: Site, photoUriList: List<Uri>, userName: String) {
        val comment = commentForEdit(oldSite, site, userName)

        if (comment.isEmpty() && photoUriList.isEmpty()) {
            mView?.showMessage(R.string.edit_site_no_changes)
            return
        }
        if (checkPhotos(photoUriList).not()) return

        editSiteExecute(if (comment.isNotEmpty() && photoUriList.isNotEmpty()) {
            mInteractor.savePhotos(photoUriList, site, userName)
                    .andThen(mInteractor.editSite(site, oldSite, comment, userName))
        } else if (comment.isNotEmpty()) {
            mInteractor.editSite(site, oldSite, comment, userName)
        } else {
            mInteractor.savePhotos(photoUriList, site, userName)
        })
    }

    private fun checkPhotos(photoUriList: List<Uri>): Boolean {
        if (photoUriList.size > 3) {
            mView?.showMessage(R.string.image_count_to_match)
            EventFactory.message("Load canceled for Size count , count = ${photoUriList.count()}")
            return false
        }

        if (photoUriList.any { it.size() > 7 * 1024 * 1024 }) {
            mView?.showMessage(R.string.image_size_too_match)
            EventFactory.message("Load canceled for Size photo , size = ${photoUriList.map { it.size() }} Мб")
            return false
        }

        val cR = context.contentResolver
        if (photoUriList.any { cR.getType(it) != "image/jpeg" && cR.getType(it) != "image/png" && cR.getType(it) != null}) {
            mView?.showMessage(R.string.image_type_nod_valid)
            EventFactory.message("Load canceled for Type photo , type = ${photoUriList.map { cR.getType(it) }}")
            return false
        }
        return true
    }

    private fun Uri.size(): Long {
//        val imageStream: InputStream = context.contentResolver.openInputStream(this)
        val file = File(this.path)
        return file.length() //TODO не работает, возвращает "0"
    }

    private fun editSiteExecute(completable: Completable) {
        mView?.showProgress()
        mDisposable.clear()
        mDisposable.add(completable
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

    override fun loadAddressFromCoordinates(lat: Double, lng: Double) {
        mDisposable.add(mAddressInteractor.loadAddress(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadAddressSuccess, this::loadAddressError));
    }

    private fun loadAddressSuccess(address: String) {
        mView?.setAddressFromCoordinates(address)
    }

    private fun loadAddressError(throwable: Throwable) {
        EventFactory.exception(throwable)
    }

    override fun unbindView() {
        mDisposable.dispose()
        mView = null
    }
}