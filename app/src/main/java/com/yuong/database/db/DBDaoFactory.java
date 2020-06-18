package com.yuong.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yuong.database.db.base.BaseDao;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/15
 * desc   :
 */
public class DBDaoFactory {

    private static DBDaoFactory instance;

    private SQLiteDatabase sqLiteDatabase;
    //数据库存放位置
    private String sqlLitePath;

    //设置一个数据库连接池, 只要初始化一次就不用再次创建了
    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());


    protected DBDaoFactory(Context context) {
        //可以根据自己的业务存放在sd卡里面，这样就不会因为卸载软件而数据库文件也被删除
        sqlLitePath = "data/data/" + context.getPackageName() + File.separator + "yuongzw.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqlLitePath, null);
    }

    public static DBDaoFactory getInstance(Context context) {
        if (instance == null) {
            synchronized (DBDaoFactory.class) {
                if (instance == null) {
                    instance = new DBDaoFactory(context);
                }
            }
        }
        return instance;
    }

    //生产BaseDao对象
    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(daoClass.getSimpleName()) != null) {
            return (T) map.get(daoClass.getSimpleName());
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            map.put(daoClass.getSimpleName(), baseDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
