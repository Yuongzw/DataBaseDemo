package com.yuong.database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.yuong.database.db.BaseDao;
import com.yuong.database.db.BaseDaoFactory;
import com.yuong.database.entity.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void insertToDB(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance(this.getApplicationContext()).getBaseDao(User.class);
        User user = new User(1, "xiaoming", "123456");
        baseDao.insert(user);
    }
}
