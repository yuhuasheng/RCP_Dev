package com.hh.tools.l5Change.FBECN;

import java.awt.Frame;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.GetTemplateFile;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class CreateZBGYAction extends AbstractAIFAction {
    private AbstractAIFUIApplication app = null;
    private TCSession session = null;
    private ProgressBarThread barThread = null;
    private TCComponentItemRevision itemRev = null;

    public CreateZBGYAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
    }

    @Override
    public void run() {
        InterfaceAIFComponent[] aifComponents = this.app.getTargetComponents();
        try {
            if (aifComponents != null && aifComponents.length > 0) {
                if (aifComponents.length > 1) {
                	Utils.infoMessage("请选择一个工艺版本进行该操作!");
                    return;
                } else {
                    InterfaceAIFComponent com = aifComponents[0];
                    if (com instanceof TCComponentItemRevision) {
                        itemRev = (TCComponentItemRevision) com;
                    } else if (com instanceof TCComponentBOMLine) {
                        itemRev = ((TCComponentBOMLine) com).getItemRevision();
                    }
                    boolean flag = false;
                    String type = itemRev.getType();
                    if (ItemTypeName.L5ZZPROCESSREVISION.equals(type) || ItemTypeName.L5TZPROCESSREVISION.equals(type)
                            || ItemTypeName.L5CYPROCESSREVISION.equals(type) || ItemTypeName.L5CXPROCESSREVISION.equals(type)
                            || ItemTypeName.L5JYPROCESSREVISION.equals(type)) {
                        flag = true;
                    }
                    if (!flag) {
                    	Utils.infoMessage("请选择一个工艺版本进行该操作!");
                        return;
                    }
                }
            } else {
            	Utils.infoMessage("请选择一个工艺版本进行该操作!");
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    careateAction();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void careateAction() {
        try {
        	barThread = new ProgressBarThread("提示", "正在生成整本工艺...");
            barThread.start();
            new Thread().sleep(8000);
            String itemId = itemRev.getProperty("item_id");
            TCComponent templateComp = GetTemplateFile.getTemplateComponent(session, "L6BOP模板", "工艺模板", "MSExcelX");	
            if (templateComp == null) {
                barThread.stopBar();
                MessageBox.post("未获取到名称为工艺模板的MSExcelX数据集模板，请联系管理员！", "错误", 1);
                return;
            }
            TCComponentDataset datasetTemplate = (TCComponentDataset) templateComp;
            itemRev.add("IMAN_specification", datasetTemplate.saveAs(itemId + "+PTH+SOP"));
            barThread.stopBar();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
