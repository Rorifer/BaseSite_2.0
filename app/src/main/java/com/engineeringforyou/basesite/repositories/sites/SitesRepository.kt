package com.engineeringforyou.basesite.repositories.sites

import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Single

interface SitesRepository {

    fun searchSitesByNumber(operator: Operator, search: String): Single<List<Site>>

    fun searchSitesByAddress(operator: Operator, search: String): Single<List<Site>>

}