package com.yuong.database.db.update;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhiwen.yang
 * date   : 2020/6/18
 * desc   :
 */
public class UpdateDBXml {
    private List<UpdateStep> updateStepList;

    public UpdateDBXml(Document doc) {
        //获取升级的脚本，解析根节点
        NodeList updateSteps = doc.getElementsByTagName("updateStep");
        updateStepList = new ArrayList<>();
        for (int i = 0; i < updateSteps.getLength(); i++) {
            Element element = (Element) updateSteps.item(i);
            UpdateStep step = new UpdateStep(element);
            updateStepList.add(step);
        }
    }

    public List<UpdateStep> getUpdateStepList() {
        return updateStepList;
    }
}
