package com.foxconn.sdebom.Tester;

import org.eclipse.core.expressions.PropertyTester;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class Desktop_RD_DPD_SD extends PropertyTester {
	@Override
	public boolean test(Object arg0, String arg1, Object[] arg2, Object arg3) {
		try {
			AbstractAIFApplication app = AIFUtility.getCurrentApplication();
			TCSession tcSession = (TCSession) app.getSession();
			TCComponentGroup currentGroup = tcSession.getCurrentGroup();
			String full_name = currentGroup.getProperty("full_name");
			//System.out.println("full_name="+full_name);
			if( full_name.contains("SD.DPD.R&D.Desktop.D_Group") || full_name.equals("dba"))
			{
				return true;
			}
			
			
			/*
			String[] curGroupAry = full_name.split("\\.");
			String[] prefValues = tcSession.getPreferenceService().getStringValues("");
			if( prefValues!=null && prefValues.length>0 )
			{
				for( int i = 0; i < prefValues.length; i ++ )
				{
					if( full_name.equals(prefValues[i]) )
					{
						return true;
					}else{
						String[] tmpAry = prefValues[i].split("\\.");
						if( tmpAry.length<curGroupAry.length )
						{
							if( full_name.endsWith("."+prefValues[i]) )
							{
								return true;
							}
						}
					}
				}
			}
			*/
			
			
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}