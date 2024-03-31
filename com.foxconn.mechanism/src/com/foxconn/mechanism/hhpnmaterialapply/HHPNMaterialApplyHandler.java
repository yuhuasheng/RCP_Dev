package com.foxconn.mechanism.hhpnmaterialapply;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.foxconn.mechanism.hhpnmaterialapply.DTSA.DTSAHHPNMaterialApplyDialog;
import com.foxconn.mechanism.hhpnmaterialapply.MNT.MNTHHPNMaterialApplyDialog;
import com.foxconn.mechanism.hhpnmaterialapply.PRT.PRTHHPNMaterialApplyDialog;
import com.foxconn.mechanism.hhpnmaterialapply.constants.BUConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.LogConstant;
import com.foxconn.mechanism.hhpnmaterialapply.util.BOMTreeTools;
import com.foxconn.mechanism.hhpnmaterialapply.util.Log;
import com.foxconn.mechanism.util.CommonTools;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class HHPNMaterialApplyHandler extends AbstractHandler {
	

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("==>> 当前系统时间为: " + CommonTools.getNowTime());
		String nowDateTime = CommonTools.getNowTime2();
		Log.init(LogConstant.LogFilePath, nowDateTime + LogConstant.LogFileSuffix);
		Log.log("========== HHPN物料管理Handler ==========");		
		AbstractPSEApplication pseApp = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		Shell shell = pseApp.getDesktop().getShell();
		String BU = (String) arg0.getParameters().get("BU_Name");
		if (BUConstant.DTSABUNAME.equals(BU)) {
			new DTSAHHPNMaterialApplyDialog(pseApp, shell);
		} else if (BUConstant.MNTBUNAME.equals(BU)) {
			new MNTHHPNMaterialApplyDialog(pseApp, shell);
		} else if (BUConstant.PRTBUNAME.equals(BU)) {
			new PRTHHPNMaterialApplyDialog(pseApp, shell);
		}
		return null;
	}
	
	
	

}
