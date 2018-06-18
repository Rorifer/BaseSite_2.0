package com.engineeringforyou.basesite.repositories.database

import com.engineeringforyou.basesite.data.orm.ORMHelperFactory
import com.engineeringforyou.basesite.models.Comment
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import io.reactivex.Completable
import io.reactivex.Single

class DataBaseRepositoryImpl : DataBaseRepository {

    override fun searchSitesByNumber(operator: Operator, search: String): Single<List<Site>> =
            Single.fromCallable { ORMHelperFactory.getHelper().searchSitesByNumber(operator, search) }

    override fun searchSitesByAddress(operator: Operator, search: String): Single<List<Site>> =
            Single.fromCallable { ORMHelperFactory.getHelper().searchSitesByAddress(operator, search) }

    override fun searchSitesByLocation(operator: Operator, lat: Double, lng: Double, radius: Int): Single<List<Site>> =
            Single.fromCallable { ORMHelperFactory.getHelper().searchSitesByLocation(operator, lat, lng, radius) }

    override fun getAllSites(operator: Operator): Single<List<Site>> =
            Single.fromCallable { ORMHelperFactory.getHelper().getAllSites(operator) }

    override fun saveSites(sites: List<Site>): Completable =
            Completable.fromCallable { ORMHelperFactory.getHelper().saveSites(sites) }

    override fun getComments(site: Site): Single<List<Comment>> =
            Single.fromCallable { ORMHelperFactory.getHelper().getComments(site) }

    override fun saveComment(comment: Comment): Completable =
            Completable.fromCallable { ORMHelperFactory.getHelper().commentsDao.create(comment) }
}