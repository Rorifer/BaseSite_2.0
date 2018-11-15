package com.engineeringforyou.basesite.presentation.sitesearch.presenter

import android.widget.EditText
import com.engineeringforyou.basesite.presentation.sitesearch.views.SearchSiteView

interface SearchSitePresenter {

    fun bind(view: SearchSiteView)

    fun unbindView()

    fun onResume()

    fun searchSite(operatorIndex: Int, search: String)

    fun showMap(operatorIndex: Int)

    fun watchChanges(view: EditText)

    fun saveOperator(operatorIndex: Int)

    fun showInfo()

    fun messageForDeveloper()

}
