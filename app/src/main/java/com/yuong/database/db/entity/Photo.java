package com.yuong.database.db.entity;

import com.yuong.database.annotation.DBTable;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/17
 * desc   :
 */
@DBTable("tb_photo")
public class Photo {
    private String time;
    private String path;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
