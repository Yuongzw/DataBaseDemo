package com.yuong.database.db.entity;

import com.yuong.database.annotation.DBField;
import com.yuong.database.annotation.DBTable;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/12
 * desc   :
 *
 * 1、得到User实体类对应的表名
 * 2、得到User实体类对应的列名
 */
@DBTable("tb_user")
public class User {

    @DBField("u_id")
    private Integer id;
    private String name;
    private String password;
    private int status;

    public User() { }

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
