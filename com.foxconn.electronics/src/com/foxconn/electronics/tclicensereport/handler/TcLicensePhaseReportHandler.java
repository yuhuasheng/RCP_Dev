package com.foxconn.electronics.tclicensereport.handler;

import java.io.File;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.foxconn.electronics.tclicensereport.action.TcLicenseLoadingDialog;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;

public class TcLicensePhaseReportHandler extends AbstractHandler{
	
	
	@SuppressWarnings("deprecation")
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		AbstractAIFUIApplication application = AIFUtility.getCurrentApplication();
		Shell shell = application.getDesktop().getShell();
		
		try {
			String fileName = "TCLicenseByPhaseReport"+DateUtil.now();
			fileName=fileName.replaceAll(":", "");
			fileName=fileName.replaceAll(" ", "");
			fileName=fileName.replaceAll("-", "");
			File file = TCUtil.openSaveFileDialog(shell,fileName);
			if(file==null) {
				return null;
			}
			new TcLicenseLoadingDialog(shell,"專案Phase稼動率正在導出...",new TcLicenseLoadingDialog.Action() {
				@Override
				public void run() {
					AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
					TCSession session = (TCSession) app.getSession();
					String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");	
					String url = preference + "/tc-service/license/exportByPhase";
					System.out.println(url);
					HttpUtil.downloadFile(url, file);
					TCUtil.infoMsgBox("導出成功");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }


}
