package com.yuong.database.db.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/18
 * desc   :
 */
public class UpdateStep {
    private String versionFrom;
    private String versionTo;
    private List<UpdateDB> updateDBList;

    public UpdateStep(Element element) {
        versionFrom = element.getAttribute("versionFrom");
        versionTo = element.getAttribute("versionTo");
        updateDBList = new ArrayList<>();
        NodeList updateDbs = element.getElementsByTagName("updateDb");
        for (int i = 0; i < updateDbs.getLength(); i++) {
            Element db = (Element) updateDbs.item(i);
            UpdateDB updateDB = new UpdateDB(db);
            updateDBList.add(updateDB);
        }
    }

    public String getVersionFrom() {
        return versionFrom;
    }

    public String getVersionTo() {
        return versionTo;
    }

    public List<UpdateDB> getUpdateDBList() {
        return updateDBList;
    }
}
