package com.yuong.database.db.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/18
 * desc   :
 */
public class UpdateDB {

    private String sql_rename;
    private String sql_create;
    private String sql_insert;
    private String sql_delete;

    public UpdateDB(Element db) {
        NodeList sqls = db.getElementsByTagName("sql_rename");
        sql_rename = sqls.item(0).getTextContent();
        sqls = db.getElementsByTagName("sql_create");
        sql_create = sqls.item(0).getTextContent();
        sqls = db.getElementsByTagName("sql_insert");
        sql_insert = sqls.item(0).getTextContent();
        sqls = db.getElementsByTagName("sql_delete");
        sql_delete = sqls.item(0).getTextContent();
    }

    public String getSql_rename() {
        return sql_rename;
    }

    public String getSql_create() {
        return sql_create;
    }

    public String getSql_insert() {
        return sql_insert;
    }

    public String getSql_delete() {
        return sql_delete;
    }
}
