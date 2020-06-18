package com.yuong.database.db.base;

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

    long update(T entity, T where);

    int delete(T where);

    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

}
