package com.engineeringforyou.basesite.repositories.sites

import android.content.Context
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.utils.DBHelper
import io.reactivex.Single

class SitesRepositoryImpl(private val context: Context) : SitesRepository {

    override fun searchSitesByNumber(operator: Operator, search: String): Single<List<Site>> =
            Single.fromCallable { DBHelper(context).siteSearch2(operator, search, 1) }

    override fun searchSitesByAddress(operator: Operator, search: String): Single<List<Site>> =
            Single.fromCallable { DBHelper(context).siteSearch2(operator, search, 2) }
}