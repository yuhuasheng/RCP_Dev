package com.foxconn.electronics.managementebom.export.changelist.rf;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.Registry;


public class RfChangeListHandle extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Registry reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
        if (!(targetComponent instanceof TCComponentItemRevision)){
            TCUtil.warningMsgBox(reg.getString("selectErr2.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
        // TODO 判斷版本類型是不是RF的DCN
        TCComponentItemRevision itemRev = (TCComponentItemRevision) targetComponent;
        String objType = itemRev.getTypeObject().getName();
        if(!"D9_DT_DCNRevision".equals(objType)) {
        	TCUtil.warningMsgBox(reg.getString("selectErr4.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
        
        try {
			itemRev.refresh();
			new Thread(new ExportRfChangeList(itemRev)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
