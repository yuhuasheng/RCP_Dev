package com.foxconn.electronics.L10Ebom.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.L10Ebom.action.ExportMntL10BomAction;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class ExportMNTL10BOMHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Registry reg = Registry.getRegistry("com.foxconn.electronics.L10Ebom.L10Ebom");
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
        if (!(targetComponent instanceof TCComponentItemRevision)){
            TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
        // TODO 判斷版本類型是不是DPBU MNT BOM制作申请单版本
        TCComponentItemRevision itemRev = (TCComponentItemRevision) targetComponent;
        String objType = itemRev.getTypeObject().getName();
        if(!"D9_L10EBOMReqRevision".equals(objType)) {
        	TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
        // FinishMatInfo：成品料號  SemiFinishMatInfo：半成品料號  FinishBOMInfo：成品bom
        String exportType = (String) arg0.getParameters().get("export_type");
		try {
			new Thread(new ExportMntL10BomAction(itemRev,exportType)).start(); 
		} catch (Exception e) {
		  e.printStackTrace(); 
		}
		
		return null;
	}

}
