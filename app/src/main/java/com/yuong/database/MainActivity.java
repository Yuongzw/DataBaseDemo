package com.yuong.database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.yuong.database.db.base.BaseDao;
import com.yuong.database.db.DBDaoFactory;
import com.yuong.database.db.dao.PhotoDao;
import com.yuong.database.db.dao.UserDao;
import com.yuong.database.db.entity.Photo;
import com.yuong.database.db.entity.User;
import com.yuong.database.db.subdb.DBDaoSubFactory;
import com.yuong.database.db.update.UpdateManager;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDao = DBDaoFactory.getInstance(this).getBaseDao(UserDao.class, User.class);
    }

    public void insertToDB(View view) {
        BaseDao baseDao = DBDaoFactory.getInstance(this.getApplicationContext()).getBaseDao(UserDao.class, User.class);
        User user1 = new User(1, "xiaoming", "111111");
        baseDao.insert(user1);
        User user2 = new User(2, "xiaowang", "222222");
        baseDao.insert(user2);
        User user3 = new User(3, "xiaoqiang", "333333");
        baseDao.insert(user3);
        User user4 = new User(4, "xiaohua", "444444");
        baseDao.insert(user4);
    }

    public void query(View view) {
        BaseDao baseDao = DBDaoFactory.getInstance(this.getApplicationContext()).getBaseDao(UserDao.class, User.class);
        User user = new User();
        List query = baseDao.query(user);
        for (Object o : query) {
            Log.e("yuongzw", o.toString());
        }
    }

    public void modify(View view) {
        BaseDao baseDao = DBDaoFactory.getInstance(this.getApplicationContext()).getBaseDao(UserDao.class, User.class);
        User user = new User();
        user.setName("xiaoxiao");
        User user2 = new User();
        user2.setName("xiaoming");
        long update = baseDao.update(user, user2);
        Log.e("yuongzw", update + "");
    }

    public void delete(View view) {
        BaseDao baseDao = DBDaoFactory.getInstance(this.getApplicationContext()).getBaseDao(UserDao.class, User.class);
        User user = new User();
        user.setName("xiaowang");
        int delete = baseDao.delete(user);
        Log.e("yuongzw", delete + "");
    }

    public void login(View view) {
        User user = new User();
        user.setName("小明");
        user.setPassword("111111");
        user.setId(5);
        userDao.insert(user);
    }

    public void subInsert(View view) {
        Photo photo = new Photo();
        photo.setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "xxx.jpg");
        photo.setTime(new Date().toString());
        PhotoDao photoDao = DBDaoSubFactory.getInstance(this).getBaseDao(PhotoDao.class, Photo.class);
        photoDao.insert(photo);
    }

    public void updateDb(View view) {
        UpdateManager manager = new UpdateManager();
        manager.startUpdateDb(this);
    }
}
