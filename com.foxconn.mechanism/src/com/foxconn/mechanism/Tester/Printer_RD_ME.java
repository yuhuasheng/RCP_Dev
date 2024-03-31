package com.foxconn.mechanism.Tester;

import org.eclipse.core.expressions.PropertyTester;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class Printer_RD_ME extends PropertyTester {
	@Override
	public boolean test(Object arg0, String arg1, Object[] arg2, Object arg3) {
		// TODO Auto-generated method stub
		try {
			AbstractAIFApplication app = AIFUtility.getCurrentApplication();
			TCSession tcSession = (TCSession) app.getSession();
			TCComponentGroup currentGroup = tcSession.getCurrentGroup();
			String full_name = currentGroup.getProperty("full_name");
			
			if( full_name.contains("ME.R&D.Printer.D_Group") || full_name.equals("dba"))
			{
				return true;
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}