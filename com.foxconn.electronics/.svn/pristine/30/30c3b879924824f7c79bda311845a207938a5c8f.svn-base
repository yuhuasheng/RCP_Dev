package com.foxconn.electronics.prtL10ebom.ebomexport.handler;

import java.awt.Dimension;
import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.prtL10ebom.ebomexport.controller.PrtEbomController;
import com.foxconn.electronics.prtL10ebom.ebomexport.dialog.PrtBomExportDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;


public class PrtL5BomExportHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ResourceBundle reg	= ResourceBundle.getBundle("com.foxconn.electronics.prtL10ebom.prtL10ebom_locale");
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		Shell shell = app.getDesktop().getShell();
		Dimension dim = app.getDesktop().getSize();
		
		InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
        if (!(targetComponent instanceof TCComponentFolder)){
            TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
        TCComponentFolder folder = (TCComponentFolder) targetComponent;
        String objType = folder.getTypeObject().getName();
        if(!"Folder".equals(objType)) {
        	TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
        try {
        	TCHttp http = TCHttp.startJHttp();
			http.addHttpController(new PrtEbomController( session, reg,folder,"L5"));
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,
					"D9_SpringCloud_URL") +"/PRTEBOMExport?port="+ http.getPort() + "&type=L5";
			PrtBomExportDialog dialog = new PrtBomExportDialog(shell, dim, session, url);
			dialog.initUI();
			http.stop();
		} catch (Exception e) {
		  e.printStackTrace(); 
		}
		return null;
	}

}
