package com.engineeringforyou.basesite.presentation.job.create

import android.app.DialogFragment
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.sitesearch.SearchSiteInteractor
import com.engineeringforyou.basesite.domain.sitesearch.SearchSiteInteractorImpl
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.sitesearch.presenter.SearchSitePresenterImpl.PATTERN_NUMBER_SITE
import com.engineeringforyou.basesite.utils.EventFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_site_link.*
import kotlinx.android.synthetic.main.dialog_site_link.view.*
import kotlinx.android.synthetic.main.view_progress.*
import java.util.regex.Pattern

class SiteLinkDialog : DialogFragment() {

    companion object {

        fun getInstance(site: Site?): SiteLinkDialog {
            val fragment = SiteLinkDialog()
            val args = Bundle()
            args.putParcelable("site", site)
            fragment.arguments = args
            return fragment
        }
    }

    private val disposable = CompositeDisposable()
    private lateinit var mInteractor: SearchSiteInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_site_link, container)
        dialog.setTitle(R.string.dialog_site_link_title)

        val site = arguments.getParcelable<Site?>("site")
        if (site?.operator != null) {
            v.site_search.setText(site.number)
            v.operators_group.check(v.operators_group.getChildAt(site.operator!!.ordinal).id)
        }

        v.button_search.setOnClickListener { searchSite() }

        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInteractor = SearchSiteInteractorImpl(activity)
        retainInstance = true
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun searchSite() {
        val searchText = site_search.text.toString().trim()
        val operatorIndexed = getOperatorIndex()

        if (searchText.isEmpty()) {
            showError(R.string.error_search_empty)
            return
        }
        if (operatorIndexed == -1) {
            showError(R.string.error_operator_index)
            return
        }

        hideError()
        showProgress()
        disposable.clear()

        val operator = Operator.values()[operatorIndexed]

        val search =
                if (Pattern.matches(PATTERN_NUMBER_SITE, searchText))
                    mInteractor.searchSitesByNumber(searchText, operator)
                else mInteractor.searchSitesByAddress(searchText, operator)
        disposable.add(search
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::searchSuccess, ::searchError))
    }

    private fun searchSuccess(siteList: List<Site>) {
        hideProgress()
        val count = siteList.size
        when (count) {
            0 -> showError(R.string.error_no_succes)
            in 1..49 -> toSiteChoice(siteList)
            else -> showError(R.string.error_many_success)
        }
    }

    private fun toSiteChoice(siteList: List<Site>) {
        (activity as JobCreateView).showListLinkSearch(siteList)
        dismiss()
    }

    private fun searchError(throwable: Throwable) {
        EventFactory.exception(throwable)
        hideProgress()
        showError(R.string.error)
    }

    private fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun getOperatorIndex(): Int {
        return operators_group.indexOfChild(operators_group.findViewById<View>(operators_group.checkedRadioButtonId))
    }

    private fun showError(@StringRes textRes: Int) {
        site_search_layout.error = (activity.getString(textRes))
    }

    private fun hideError() {
        site_search_layout.error = null
    }

    override fun onStop() {
        super.onStop()
        hideProgress()
        disposable.clear()
    }

    private fun hideProgress() {
        progress_bar.visibility = View.GONE
    }
}
