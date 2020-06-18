package com.yuong.database.db.dao;

import com.yuong.database.db.base.BaseDao;
import com.yuong.database.db.entity.User;

import java.util.List;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/17
 * desc   :
 */
public class UserDao extends BaseDao<User> {

    @Override
    public long insert(User entity) {
        List<User> list = query(new User());
        User where = null;
        for (User user : list) {
            where = new User();
            where.setId(user.getId());
            where.setStatus(0);
            update(user, where);
        }
        entity.setStatus(1);
        return super.insert(entity);
    }

    public User getCurrentUser() {
        User user = new User();
        user.setStatus(1);
        List<User> users = query(user);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }
}
