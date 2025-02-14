package com.foxconn.electronics.L10Ebom.handler;

import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.foxconn.electronics.L10Ebom.controller.MntL10EBomController;
import com.foxconn.electronics.L10Ebom.controller.Sync2ndsourceController;
import com.foxconn.electronics.L10Ebom.dialog.MntL10EBomDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class MntL10EBomHandler extends AbstractHandler {
	public AbstractAIFUIApplication app;
	public TCSession session = null;
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ResourceBundle reg	= ResourceBundle.getBundle("com.foxconn.electronics.L10Ebom.L10Ebom_locale");
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();

		InterfaceAIFComponent[] targetComponent = app.getTargetComponents();
		if (targetComponent != null && targetComponent.length == 1) {
			try {
				String type = targetComponent[0].getType();
				System.out.println("type = " + type);
				if(targetComponent[0] instanceof TCComponentItemRevision) {
					
					TCComponentItemRevision revision = (TCComponentItemRevision)targetComponent[0];
					//我的工作列表
					//TCComponent userInBox = session.getUser().getUserInBox();
					
					
					TCComponentGroup currentGroup = session.getCurrentGroup();
					String group = currentGroup.getFullName();
					if(group.contains(".")) {
						group = group.substring(0, group.indexOf("."));
						System.out.println("group = "+group);
					}
					
					
					Shell shell = app.getDesktop().getShell();
					TCHttp http = TCHttp.startJHttp();
					int port = http.getPort();

					String preference = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
					String url = preference + ":8021/#/MNTL10EBOM?uid="+app.getTargetComponent().getUid()+"&port="+port+"&group="+group;
					
					System.out.println("port = " + port);
					System.out.println("L10 EBOM url = " + url);	
					

					http.addHttpController(new MntL10EBomController(revision, session, reg));
					http.addHttpController(new Sync2ndsourceController());
					
					new MntL10EBomDialog(app, shell, url,reg);
				} else {
					System.out.println("选择对象错误！");
				}
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
