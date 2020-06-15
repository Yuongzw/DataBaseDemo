package com.yuong.database.db;

import java.util.List;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/12
 * desc   :
 */
public interface IBaseDao<T> {

    /**
     * 插入一条数据
     * @param entity
     * @return
     */
    public long insert(T entity);

}
