package com.engineeringforyou.basesite.data.orm

import com.engineeringforyou.basesite.models.*
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.sql.SQLException

class SiteMTSDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteMTS>) : BaseDaoImpl<SiteMTS, Int>(connectionSource, dataClass) {
//
//    val allSites: List<SiteMTS>
//        @Throws(SQLException::class)
//        get() = this.queryForAll()
//
//    @Throws(SQLException::class)
//    fun searchSitesByNumber(search: String): List<SiteMTS> {
//        val queryBuilder = queryBuilder()
//        queryBuilder.where().like(FIELD_SITE, search)
//        var preparedQuery = queryBuilder.prepare()
//        val result = query(preparedQuery)
//        return if (result.isEmpty().not()) result
//        else {
//            queryBuilder.where().like(FIELD_SITE, "%$search")//.and()
//            preparedQuery = queryBuilder.prepare()
//            query(preparedQuery)
//        }
//    }
//
//    @Throws(SQLException::class)
//    fun searchSitesByAddress(search: String): List<SiteMTS> {
//        val words = search.replace(',', ' ').split(" ".toRegex())
//        val queryBuilder = queryBuilder()
//        val where = queryBuilder.where()
//        for (ind in words.indices) {
//            if (words[ind].isNotEmpty()) {
//                if (ind > 0) where.and()
//                where.like(FIELD_ADDRESS, "%${words[ind]}%")
//            }
//        }
//        return query(queryBuilder.prepare())
//    }
//
//    @Throws(SQLException::class)
//    fun searchSitesByLocation(latMin: Double, latMax: Double, lngMin: Double, lngMax: Double):  List<SiteMTS>  {
//        val queryBuilder = queryBuilder()
//        queryBuilder.where()
//                .between(FIELD_LAT, latMin, latMax)
//                .and()
//                .between(FIELD_LNG, lngMin, lngMax)
//        return query(queryBuilder.prepare())
//    }
}

class SiteVMKDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteVMK>) : BaseDaoImpl<SiteVMK, Int>(connectionSource, dataClass)

class SiteMGFDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteMGF>) : BaseDaoImpl<SiteMGF, Int>(connectionSource, dataClass)

class SiteTELEDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteTELE>) : BaseDaoImpl<SiteTELE, Int>(connectionSource, dataClass)

class SiteCommentsDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<Comment>) : BaseDaoImpl<Comment, Int>(connectionSource, dataClass)