package com.foxconn.electronics.Tester;

import org.eclipse.core.expressions.PropertyTester;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCSession;

public class Monitor_MFG extends PropertyTester {

	@Override
	public boolean test(Object arg0, String arg1, Object[] arg2, Object arg3) {
		try {
			AbstractAIFApplication app = AIFUtility.getCurrentApplication();
			TCSession tcSession = (TCSession) app.getSession();
			TCComponentGroup currentGroup = tcSession.getCurrentGroup();
			String full_name = currentGroup.getProperty("full_name");
			if (full_name.contains("MFG.Monitor.D_Group") || full_name.equals("dba")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
