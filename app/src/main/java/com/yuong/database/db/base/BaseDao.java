package com.yuong.database.db.base;

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

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
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

    @Override
    public long update(T entity, T where) {
        Map<String, String> map = getValues(entity);
        Map<String, String> whereMap = getValues(where);
        ContentValues values = getContentValues(map);
        Condition condition = new Condition(whereMap);
        int update = sqLiteDatabase.update(tableName, values, condition.whereCause, condition.whereArgs);
        return update;
    }

    @Override
    public int delete(T where) {
        Map<String, String> values = getValues(where);
        Condition condition = new Condition(values);
        int delete = sqLiteDatabase.delete(tableName, condition.whereCause, condition.whereArgs);
        return delete;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //select * from tablename limit 0,10
        Map<String, String> map = getValues(where);
        String limitStr = null;
        if (startIndex != null && limit != null) {
            limitStr = startIndex + "," + limit;
        }
        //select * from tablename where id=? and name=?
        //String selection, String[] selectionArgs,
        //参数 selection 代表着上面的where语句, selectionArgs 代表后面的问号值
        Condition condition = new Condition(map);
        Cursor query = sqLiteDatabase.query(tableName, null, condition.whereCause, condition.whereArgs, null, null, null);
        //定义一个解析游标的方法
        List<T> result = getResult(query, where);
        return result;
    }

    private List<T> getResult(Cursor cursor, T obj) {
        List<T> list = new ArrayList<>();
        Object item = null;
        while (cursor.moveToNext()) {
            try {
                //相当于 User user = new User();
                item = obj.getClass().newInstance();
                //设置属性
                Iterator<Map.Entry<String, Field>> iterator = entityFields.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> next = iterator.next();
                    //获取列名
                    String columnName = next.getKey();
                    //以列名拿到列名在游标中的位置
                    //cursor.getString(columnIndex);
                    int columnIndex = cursor.getColumnIndex(columnName);
                    //获取成员变量的类型
                    Field field = next.getValue();
                    Class<?> type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type == Float.class) {
                            field.set(item, cursor.getFloat(columnIndex));
                        } else if (type == byte[].class) {
                            field.set(item, cursor.getBlob(columnIndex));
                        } else {
                            //不支持的类型
                            continue;
                        }
                    }
                }
                list.add((T) item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
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

    private class Condition {
        private String whereCause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereMap) {
            List<String> list = new ArrayList();
            StringBuilder sb = new StringBuilder();
            sb.append("1=1");
            //获取所有的字段名
            Set<String> keySet = whereMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String fieldName = whereMap.get(key);
                if (fieldName != null) {
                    sb.append(" and ").append(key).append(" =?");
                    list.add(fieldName);
                }
            }
            whereCause = sb.toString();
            whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }
}
