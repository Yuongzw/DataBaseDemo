package com.yuong.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/15
 * desc   :
 */
public class BaseDaoFactory {

    private static BaseDaoFactory instance;

    private SQLiteDatabase sqLiteDatabase;
    //数据库存放位置
    private String sqlLitePath;


    private BaseDaoFactory(Context context) {
        //可以根据自己的业务存放在sd卡里面，这样就不会因为卸载软件而数据库文件也被删除
        sqlLitePath = "data/data/" + context.getPackageName() + File.separator + "yuongzw.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqlLitePath, null);
    }

    public static BaseDaoFactory getInstance(Context context) {
        if (instance == null) {
            synchronized (BaseDaoFactory.class) {
                if (instance == null) {
                    instance = new BaseDaoFactory(context);
                }
            }
        }
        return instance;
    }

    //生产BaseDao对象
    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
