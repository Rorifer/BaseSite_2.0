package com.engineeringforyou.basesite.data.orm

import com.engineeringforyou.basesite.models.*
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.sql.SQLException

class SiteMTSDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteMTS>) : BaseDaoImpl<SiteMTS, Int>(connectionSource, dataClass)

class SiteVMKDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteVMK>) : BaseDaoImpl<SiteVMK, Int>(connectionSource, dataClass)

class SiteMGFDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteMGF>) : BaseDaoImpl<SiteMGF, Int>(connectionSource, dataClass)

class SiteTELEDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<SiteTELE>) : BaseDaoImpl<SiteTELE, Int>(connectionSource, dataClass)

class CommentsDAO @Throws(SQLException::class)
internal constructor(connectionSource: ConnectionSource, dataClass: Class<Comment>) : BaseDaoImpl<Comment, Int>(connectionSource, dataClass)