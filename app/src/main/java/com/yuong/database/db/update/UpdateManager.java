package com.yuong.database.db.update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yuong.database.db.DBDaoFactory;
import com.yuong.database.db.dao.UserDao;
import com.yuong.database.db.entity.User;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/18
 * desc   :
 */
public class UpdateManager {

    private List<User> userList;

    public void startUpdateDb(Context context) {
        UserDao userDao = DBDaoFactory.getInstance(context).getBaseDao(UserDao.class, User.class);
        userList = userDao.query(new User());
        //开始解析xml文件
        UpdateDBXml updateDBXml = readXml(context);
        //拿到当前的版本信息
        UpdateStep step = analyseUpdateStep(updateDBXml);
        if (step == null) {
            return;
        }
        //获取更新用的对象
        List<UpdateDB> updateDBList = step.getUpdateDBList();
        if (updateDBList != null && updateDBList.size() > 0) {
            for (User user : userList) {
                //得到每个用户的数据库对象
                SQLiteDatabase sqLiteDatabase = getDB(user.getId());
                if (sqLiteDatabase != null) {
                    for (UpdateDB updateDB : updateDBList) {
                        String sqlRename = updateDB.getSql_rename();
                        String sqlCreate = updateDB.getSql_create();
                        String sqlInsert = updateDB.getSql_insert();
                        String sqlDelete = updateDB.getSql_delete();
                        String[] sqls = new String[]{
                                sqlRename,
                                sqlCreate,
                                sqlInsert,
                                sqlDelete
                        };
                        executeSqls(sqLiteDatabase, sqls);
                        Log.i("yuongzw", user.getId() + " 升级数据库成功！");
                    }
                }
            }
        }
    }

    private void executeSqls(SQLiteDatabase sqLiteDatabase, String[] sqls) {
        if (sqls == null || sqls.length == 0) {
            return;
        }
        //事务
        sqLiteDatabase.beginTransaction();
        for (String sql : sqls) {
            sql = sql.replace("\r\n", " ");
            sql = sql.replace("\n", " ");
            if (!"".equals(sql.trim())) {
                sqLiteDatabase.execSQL(sql);
            }
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    /**
     * 获取每个用户所对应的数据库
     * @param id 用户id
     * @return
     */
    private SQLiteDatabase getDB(int id) {
        SQLiteDatabase sqLiteDatabase = null;
        File file = new File("data/data/com.yuong.database/u_" + id + "_private.db");
        if (!file.exists()) {
            Log.e("yuongzw", file.getAbsolutePath() + " 数据库不存在！");
            return null;
        }
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file, null);
        return sqLiteDatabase;
    }

    /**
     * 获取UpdateStep实例
     * @param updateDBXml
     * @return
     */
    private UpdateStep analyseUpdateStep(UpdateDBXml updateDBXml) {
        UpdateStep thisStep = null;
        if (updateDBXml == null) {
            return null;
        }
        List<UpdateStep> steps = updateDBXml.getUpdateStepList();
        if (steps == null || steps.size() == 0) {
            return null;
        }
        for (UpdateStep step : steps) {
            if (step.getVersionFrom() == null || step.getVersionTo() == null) {
            } else {
                String[] versionFroms = step.getVersionFrom().split(",");
                if (versionFroms != null && versionFroms.length > 0) {
                    for (String versionFrom : versionFroms) {
                        //真实项目中 V002等的数据库版本要保存到SharedPreference中或其他方式中保存起来，
                        //真实项目中 V003的数据库版本要从服务器中获取，
                        if ("V002".equalsIgnoreCase(versionFrom) && "V003".equalsIgnoreCase(step.getVersionTo())) {
                            thisStep = step;
                        }
                    }
                }
            }
        }
        return thisStep;
    }

    /**
     * 读取 xml文件
     * @param context
     * @return
     */
    private UpdateDBXml readXml(Context context) {
        InputStream is = null;
        Document doc = null;
        try {
            is = context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (doc == null) {
                return null;
            }
        }
        return new UpdateDBXml(doc);
    }
}
