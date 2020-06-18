package com.yuong.database.db.subdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yuong.database.db.DBDaoFactory;
import com.yuong.database.db.base.BaseDao;

import java.io.File;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/17
 * desc   :
 */
public class DBDaoSubFactory extends DBDaoFactory {

    private static DBDaoSubFactory instance;

    //定义一个用于实现分库的数据库对象
    private SQLiteDatabase sqLiteDatabase;

    private String subSqlLitePath;


    private DBDaoSubFactory(Context context) {
        super(context);
        subSqlLitePath = "data/data/" + context.getPackageName() + File.separator + "user.db";
    }

    public static DBDaoSubFactory getInstance(Context context) {
        if (instance == null) {
            synchronized (DBDaoSubFactory.class) {
                if (instance == null) {
                    instance = new DBDaoSubFactory(context);
                }
            }
        }
        return instance;
    }

    @Override
    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(PrivateDatabaseEnums.database.getValue()) != null) {
            return (T) map.get(PrivateDatabaseEnums.database.getValue());
        }
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDatabaseEnums.database.getValue(), null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            map.put(PrivateDatabaseEnums.database.getValue(), baseDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
