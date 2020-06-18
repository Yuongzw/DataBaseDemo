package com.yuong.database.db.subdb;

import android.app.Application;

import com.yuong.database.MyApplication;
import com.yuong.database.db.DBDaoFactory;
import com.yuong.database.db.dao.UserDao;
import com.yuong.database.db.entity.User;

import java.io.File;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/17
 * desc   : 用来生产私有数据库存放的位置
 */
public enum  PrivateDatabaseEnums {

    database("");
    private String value;

    PrivateDatabaseEnums(String value) {

    }

    public String getValue() {
        UserDao userDao = DBDaoFactory.getInstance(MyApplication.context).getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File file = new File("/data/data" + File.separator + MyApplication.context.getPackageName() + File.separator);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/u_" + currentUser.getId() + "_private.db";
            }
        }
        return null;
    }
}
