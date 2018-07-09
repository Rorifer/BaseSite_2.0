package com.engineeringforyou.basesite.data.orm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.engineeringforyou.basesite.models.Comment;
import com.engineeringforyou.basesite.models.Operator;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.models.SiteMGF;
import com.engineeringforyou.basesite.models.SiteMTS;
import com.engineeringforyou.basesite.models.SiteTELE;
import com.engineeringforyou.basesite.models.SiteVMK;
import com.engineeringforyou.basesite.utils.EventFactory;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class ORMHelper extends OrmLiteSqliteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DB_NAME = "ORM_SITES.db";

    private final String FIELD_SITE = "SITE";
    private final String FIELD_ADDRESS = "Addres";
    private final String FIELD_LAT = "GPS_Latitude";
    private final String FIELD_LNG = "GPS_Longitude";
    private final String FIELD_UID = "uid";

    private final String FIELD_COMMENT_SITE = "siteId";
    private final String FIELD_COMMENT_OPERATOR = "operatorId";

    private Context mContext;
    private SiteMTSDAO siteMTSDao;
    private SiteMGFDAO siteMGFDao;
    private SiteVMKDAO siteVMKDao;
    private SiteTELEDAO siteTELEDao;
    private CommentsDAO commentsDao;

    public ORMHelper(Context mContext) {
        super(mContext, DB_NAME, null, DATABASE_VERSION);
        this.mContext = mContext;
        createDB();
    }

    private void createDB() {
        File file = new File(mContext.getDatabasePath(DB_NAME).getPath());
        if (!file.exists()) {
            try {
                File dir = new File(mContext.getDatabasePath(DB_NAME).getParent());
                dir.mkdirs();
                InputStream inputStream = mContext.getAssets().open(DB_NAME);
                OutputStream outputStream = new FileOutputStream(mContext.getDatabasePath(DB_NAME));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                deleteOldFiles();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteOldFiles() {
        String[] databaseList = mContext.databaseList();
        for (String base : databaseList) {
            if (base.contains("DB_SITE")) {
                mContext.deleteDatabase(base);
                EventFactory.INSTANCE.message("deleteOldFiles = " + base);
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer) {
//        try {
////            TableUtils.dropTable(connectionSource, Site.class, true);
//            onCreate(db, connectionSource);
//        } catch (Throwable e) {
//            EventFactory.INSTANCE.exception(e);
//        }

        if (oldVer < 3) {
            try {
//                TableUtils.createTable(connectionSource, Comment.class);

                SiteMTSDAO mts = getSiteMTSDAO();
                SiteMGFDAO mgf = getSiteMGFDAO();
                SiteVMKDAO vmk = getSiteVMKDAO();
                SiteTELEDAO tele = getSiteTELEDAO();

                mts.executeRaw("ALTER TABLE `MTS_Site_Base` ADD COLUMN uid STRING;");
                mgf.executeRaw("ALTER TABLE `MGF_Site_Base` ADD COLUMN uid STRING;");
                vmk.executeRaw("ALTER TABLE `VMK_Site_Base` ADD COLUMN uid STRING;");
                tele.executeRaw("ALTER TABLE `TELE_Site_Base` ADD COLUMN uid STRING;");

            } catch (SQLException e) {
                EventFactory.INSTANCE.exception(e);
            }
        }

        if (oldVer < 4) {
            try {
                SiteMTSDAO mts = getSiteMTSDAO();
                SiteMGFDAO mgf = getSiteMGFDAO();
                SiteVMKDAO vmk = getSiteVMKDAO();
                SiteTELEDAO tele = getSiteTELEDAO();

                mts.executeRaw("ALTER TABLE `MTS_Site_Base` ADD COLUMN statusId INTEGER;");
                mgf.executeRaw("ALTER TABLE `MGF_Site_Base` ADD COLUMN statusId INTEGER;");
                vmk.executeRaw("ALTER TABLE `VMK_Site_Base` ADD COLUMN statusId INTEGER;");
                tele.executeRaw("ALTER TABLE `TELE_Site_Base` ADD COLUMN statusId INTEGER;");

            } catch (SQLException e) {
                EventFactory.INSTANCE.exception(e);
            }
        }
    }

    public void saveSites(List<Site> sites) throws SQLException {
        for (Site site : sites) {
            List<? extends Site> sitesByUId = searchSitesByUId(site);
            if (!sitesByUId.isEmpty()) deleteSite(sitesByUId);
            else deleteSite(searchSitesByNumber(site.getOperator(), site.getUid()));
            createSite(site);
        }
    }

    private void deleteSite(List<? extends Site> sites) throws SQLException {
        for (Site site : sites) {
            switch (site.getOperator()) {
                case MTS:
                    getSiteMTSDAO().deleteById(site.getId());
                    break;
                case VIMPELCOM:
                    getSiteVMKDAO().deleteById(site.getId());
                    break;
                case MEGAFON:
                    getSiteMGFDAO().deleteById(site.getId());
                    break;
                case TELE2:
                    getSiteTELEDAO().deleteById(site.getId());
                    break;
            }
        }
    }

    private void createSite(Site site) throws SQLException {
        switch (site.getOperator()) {
            case MTS:
                getSiteMTSDAO().create(new SiteMTS(site));
                break;
            case VIMPELCOM:
                getSiteVMKDAO().create(new SiteVMK(site));
                break;
            case MEGAFON:
                getSiteMGFDAO().create(new SiteMGF(site));
                break;
            case TELE2:
                getSiteTELEDAO().create(new SiteTELE(site));
                break;
        }
    }

    public List<? extends Site> getAllSites(Operator operator) throws SQLException {
        switch (operator) {
            case MTS:
                return getAllSites(getSiteMTSDAO());
            case VIMPELCOM:
                return getAllSites(getSiteVMKDAO());
            case MEGAFON:
                return getAllSites(getSiteMGFDAO());
            case TELE2:
                return getAllSites(getSiteTELEDAO());
        }
        return null;
    }

    private List<? extends Site> getAllSites(BaseDaoImpl<? extends Site, Integer> dao) throws
            SQLException {
        return dao.queryForAll();
    }

    public List<? extends Site> searchSitesByNumber(Operator operator, String search) throws
            SQLException {
        switch (operator) {
            case MTS:
                return searchSitesByNumber(getSiteMTSDAO(), search);
            case VIMPELCOM:
                return searchSitesByNumber(getSiteVMKDAO(), search);
            case MEGAFON:
                return searchSitesByNumber(getSiteMGFDAO(), search);
            case TELE2:
                return searchSitesByNumber(getSiteTELEDAO(), search);
        }
        return null;
    }

    private List<? extends Site> searchSitesByNumber(BaseDaoImpl<? extends Site, Integer> dao, String search) throws SQLException {
        QueryBuilder<? extends Site, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.where().like(FIELD_SITE, search);
        List<? extends Site> result = executeQuery(dao, queryBuilder);
        if (!result.isEmpty()) return result;
        else {
            queryBuilder.where().like(FIELD_SITE, "%" + search);
            return executeQuery(dao, queryBuilder);
        }
    }

    public List<? extends Site> searchSitesByAddress(Operator operator, String search) throws
            SQLException {
        switch (operator) {
            case MTS:
                return searchSitesByAddress(getSiteMTSDAO(), search);
            case VIMPELCOM:
                return searchSitesByAddress(getSiteVMKDAO(), search);
            case MEGAFON:
                return searchSitesByAddress(getSiteMGFDAO(), search);
            case TELE2:
                return searchSitesByAddress(getSiteTELEDAO(), search);
        }
        return null;
    }

    public List<? extends Site> searchSitesByUId(Site site) throws SQLException {
        BaseDaoImpl<? extends Site, Integer> dao = null;
        switch (site.getOperator()) {
            case MTS:
                dao = getSiteMTSDAO();
                break;
            case VIMPELCOM:
                dao = getSiteVMKDAO();
                break;
            case MEGAFON:
                dao = getSiteMGFDAO();
                break;
            case TELE2:
                dao = getSiteTELEDAO();
                break;
        }
        QueryBuilder<? extends Site, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.where().like(FIELD_UID, site.getUid());
        return executeQuery(dao, queryBuilder);
    }

    public List<Comment> getComments(Site site) throws SQLException {
        CommentsDAO dao = getCommentsDao();
        QueryBuilder<Comment, Integer> queryBuilder = dao.queryBuilder();
        String uid = site.getUid();
        if (uid == null) uid = site.getNumber();
        queryBuilder.where()
                .like(FIELD_COMMENT_SITE, uid)
                .and()
                .like(FIELD_COMMENT_OPERATOR, site.getOperator().getCode());
        return dao.query(queryBuilder.prepare());
    }

    private List<? extends Site> searchSitesByAddress(BaseDaoImpl<? extends Site, Integer> dao, String search) throws SQLException {
        String[] words = search.replace(',', ' ').split(" ");
        QueryBuilder<? extends Site, Integer> queryBuilder = dao.queryBuilder();
        Where<? extends Site, Integer> where = queryBuilder.where();
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                if (i > 0) where.and();
                where.like(FIELD_ADDRESS, "%" + words[i] + "%");
            }
        }
        return executeQuery(dao, queryBuilder);
    }

    public List<? extends Site> searchSitesByLocation(Operator operator, Double lat, Double lng, int radius) throws SQLException {
        switch (operator) {
            case MTS:
                return searchSitesByLocation(getSiteMTSDAO(), lat, lng, radius);
            case VIMPELCOM:
                return searchSitesByLocation(getSiteVMKDAO(), lat, lng, radius);
            case MEGAFON:
                return searchSitesByLocation(getSiteMGFDAO(), lat, lng, radius);
            case TELE2:
                return searchSitesByLocation(getSiteTELEDAO(), lat, lng, radius);
            default:
                return null;
        }
    }

    private List<? extends Site> searchSitesByLocation(BaseDaoImpl<? extends Site, Integer> dao, Double lat, Double lng, int radius) throws SQLException {
        double latDelta = (float) radius / 111;
        double lngDelta = (float) radius / 63.2;
        double latMax = lat + latDelta;
        double latMin = lat - latDelta;
        double lngMax = lng + lngDelta;
        double lngMin = lng - lngDelta;

        QueryBuilder<? extends Site, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.where()
                .between(FIELD_LAT, latMin, latMax)
                .and()
                .between(FIELD_LNG, lngMin, lngMax);
        return executeQuery(dao, queryBuilder);
    }

    private List<? extends Site> executeQuery(BaseDaoImpl<? extends Site, Integer> dao, QueryBuilder<? extends Site, Integer> queryBuilder) throws SQLException {
        PreparedQuery preparedQuery = queryBuilder.prepare();
        return dao.query(preparedQuery);
    }

    private SiteMTSDAO getSiteMTSDAO() throws SQLException {
        if (siteMTSDao == null) {
            siteMTSDao = new SiteMTSDAO(getConnectionSource(), SiteMTS.class);
        }
        return siteMTSDao;
    }

    private SiteMGFDAO getSiteMGFDAO() throws SQLException {
        if (siteMGFDao == null) {
            siteMGFDao = new SiteMGFDAO(getConnectionSource(), SiteMGF.class);
        }
        return siteMGFDao;
    }

    private SiteVMKDAO getSiteVMKDAO() throws SQLException {
        if (siteVMKDao == null) {
            siteVMKDao = new SiteVMKDAO(getConnectionSource(), SiteVMK.class);
        }
        return siteVMKDao;
    }

    private SiteTELEDAO getSiteTELEDAO() throws SQLException {
        if (siteTELEDao == null) {
            siteTELEDao = new SiteTELEDAO(getConnectionSource(), SiteTELE.class);
        }
        return siteTELEDao;
    }

    public CommentsDAO getCommentsDao() throws SQLException {
        if (commentsDao == null) {
            commentsDao = new CommentsDAO(getConnectionSource(), Comment.class);
        }
        return commentsDao;
    }


    @Override
    public void close() {
        super.close();
        siteMTSDao = null;
        siteMGFDao = null;
        siteVMKDao = null;
        siteTELEDao = null;
        commentsDao = null;
    }
}