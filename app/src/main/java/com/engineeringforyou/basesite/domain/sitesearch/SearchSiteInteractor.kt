package com.engineeringforyou.basesite.domain.sitesearch

import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface SearchSiteInteractor {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

    fun searchSitesByNumber(search: String, operator: Operator? = null): Single<List<Site>>

    fun searchSitesByAddress(search: String, operator: Operator? = null): Single<List<Site>>

    fun refreshSiteBase(): Completable

    fun getInfo(): Single<String>

    fun disableAdvertising()

}