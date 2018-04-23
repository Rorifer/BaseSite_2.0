package com.engineeringforyou.basesite.presentation.searchsite.presenter

import android.widget.EditText

import com.engineeringforyou.basesite.presentation.searchsite.views.SearchSiteView

interface SearchSitePresenter {

    fun bind(view: SearchSiteView)

    fun unbindView()

    fun setupOperator()

    fun searchSite(operatorIndex: Int, search: String)

    fun showMap(operatorIndex: Int)

    fun watchChanges(view: EditText)

    fun saveOperator(operatorIndex: Int)
}
