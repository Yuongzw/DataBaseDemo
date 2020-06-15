package com.yuong.database.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.yuong.database.annotation.DBField;
import com.yuong.database.annotation.DBTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/12
 * desc   :
 */
public class BaseDao<T> implements IBaseDao<T> {
    //持有数据库操作的引用
    private SQLiteDatabase sqLiteDatabase;

    //表名
    private String tableName;

    //操作数据库所对应的Java类型
    private Class<T> entityClass;

    //用来表示是否已经初始化
    private boolean isInit;

    //定义一个缓存控件(key 字段名   value 成员变量)
    private Map<String, Field> entityFields = new HashMap<>();

    protected boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        if (this.entityClass == null) {
            return false;
        }
        if (!isInit) {
            //根据传入的类型进行创建数据表
            DBTable dbTable = entityClass.getAnnotation(DBTable.class);
            if (dbTable != null && !TextUtils.isEmpty(dbTable.value())) {
                tableName = dbTable.value();
            } else {
                tableName = entityClass.getSimpleName();
            }
            String createTableSqlStr = getCreateTableStr();
            //创建表
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            sqLiteDatabase.execSQL(createTableSqlStr);
            initEntityFields();
            isInit = true;
        }
        return isInit;
    }

    /**
     * 初始化缓存集合
     */
    private void initEntityFields() {
        //查询表，从第一条开始，查询0条（返回表的结构）
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        //获取所有的字段名
        String[] columnNames = cursor.getColumnNames();
        //获取所有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : fields) {
                String fieldName = null;
                DBField dbField = field.getAnnotation(DBField.class);
                if (dbField != null && !TextUtils.isEmpty(dbField.value())) {
                    fieldName = dbField.value();
                } else {
                    fieldName = field.getName();
                }
                if (columnName.equals(fieldName)) {
                    columnField = field;
                    break;
                }
            }
            //不为空且缓存中没有就存入
            if (columnField != null && !entityFields.containsKey(columnName)) {
                entityFields.put(columnName, columnField);
            }
        }

    }

    /**
     * 创建 SQL语句
     *
     * @return
     */
    protected String getCreateTableStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ")
                .append(tableName).append("(");
        //反射得到所有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();

            DBField dbField = field.getAnnotation(DBField.class);
            if (dbField != null && !TextUtils.isEmpty(dbField.value())) {
                if (type == String.class) {
                    sb.append(dbField.value() + " TEXT,");
                } else if (type == Integer.class) {
                    sb.append(dbField.value() + " INTEGER,");
                } else if (type == Long.class) {
                    sb.append(dbField.value() + " BIGINT,");
                } else if (type == Double.class) {
                    sb.append(dbField.value() + " DOUBLE,");
                } else if (type == Float.class) {
                    sb.append(dbField.value() + " FLOAT,");
                } else if (type == byte[].class) {
                    sb.append(dbField.value() + " BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            } else {
                if (type == String.class) {
                    sb.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    sb.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    sb.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    sb.append(field.getName() + " DOUBLE,");
                } else if (type == Float.class) {
                    sb.append(field.getName() + " FLOAT,");
                } else if (type == byte[].class) {
                    sb.append(field.getName() + " BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            }
        }
        //去掉最后一个,
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public long insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        if (values == null) {
            return -1;
        }
        return sqLiteDatabase.insert(tableName, null, values);
    }

    private ContentValues getContentValues(Map<String, String> map) {
        //map为空
        if (map.size() == 0) {
            return null;
        }
        ContentValues values = new ContentValues();
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                values.put(key, value);
            }
        }
        return values;
    }

    /**
     * 获取 实例对象的 成员属性和值
     *
     * @param entity
     * @return
     */
    private Map<String, String> getValues(T entity) {
        Map<String, String> map = new HashMap<>();
        //得到 entity 所有的成员变量
        Iterator<Field> iterator = entityFields.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            field.setAccessible(true);
            //获取成员变量的值
            try {
                Object o = field.get(entity);
                if (o == null) {
                    continue;
                }
                String value = o.toString();
                //获取列名
                String key = null;
                DBField dbField = field.getAnnotation(DBField.class);
                if (dbField != null && !TextUtils.isEmpty(dbField.value())) {
                    key = dbField.value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
