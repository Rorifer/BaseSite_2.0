package com.engineeringforyou.basesite.domain.sitesearch

import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Single

interface SearchSiteInteractor {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

    fun searchSitesByNumber(search: String): Single<List<Site>>

    fun searchSitesByAddress(search: String): Single<List<Site>>

}