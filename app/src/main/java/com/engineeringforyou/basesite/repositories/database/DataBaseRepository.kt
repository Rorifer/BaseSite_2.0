package com.engineeringforyou.basesite.repositories.database

import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

interface DataBaseRepository {

    fun searchSitesByNumber(operator: Operator, search: String): Single<List<Site>>

    fun searchSitesByAddress(operator: Operator, search: String): Single<List<Site>>

    fun searchSitesByLocation(operator: Operator, lat: Double, lng: Double, radius: Int): Single<List<Site>>

    fun getAllSites(operator: Operator): Single<List<Site>>

    fun getComments(site: Site): Single<List<Comment>>

    fun saveComment(comment: Comment): Completable

    fun saveSites(sites: List<Site>): Completable

    fun loadAddressesForEmpty()

    fun getStatistic(): Single<String>

}