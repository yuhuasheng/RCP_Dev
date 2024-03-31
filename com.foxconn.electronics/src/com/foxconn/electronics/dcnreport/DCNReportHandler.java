package com.foxconn.electronics.dcnreport;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.dcnreport.dcncostimpact.constant.DCNCostImpactConstant;
import com.foxconn.tcutils.constant.BUEnum;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class DCNReportHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentGroup group = session.getGroup();	
			String bu = "";
			String groupName = group.getProperty("object_string");
			System.out.println("==>> group is " + groupName); 
			if (groupName.contains(BUEnum.DT.buName())) {
				bu = BUEnum.DT.buCode();
			} else if (groupName.contains(BUEnum.MNT.buName())) {
				bu = BUEnum.MNT.buCode();
			} else if (groupName.contains(BUEnum.PRT.buName())) {
				bu = BUEnum.PRT.buCode();
			} else if (groupName.contains(BUEnum.DBA.buName())) {
				bu = BUEnum.DBA.buCode();
			} 
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, DCNCostImpactConstant.D9_SPRINGCLOUD_URL) + "/DCNReport" + "?bu=" + bu;
			System.out.println("DCN Report url: " + url);
			new DCNReportDialog(app, app.getDesktop().getShell(), url);			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}	
	
}
