package com.foxconn.electronics.download;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;

public class DownLoadDatasetHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		String desktopPath = TCUtil.getDesktopPath();
		AbstractAIFUIApplication currentApplication = AIFUtility.getCurrentApplication();
		InterfaceAIFComponent[] targetComponents = currentApplication.getTargetComponents();
		for (int i = 0; i < targetComponents.length; i++) {
			InterfaceAIFComponent interfaceAIFComponent = targetComponents[i];
			if(interfaceAIFComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) interfaceAIFComponent;
				try {
					TCUtil.downloadFile(dataset, desktopPath);
				} catch (Exception e) {
					TCUtil.infoMsgBox(e.getMessage(), "");
					e.printStackTrace();
					return null;
				}
			}
		}
		TCUtil.infoMsgBox("文件已下载至电脑桌面！", "");
		return null;
	}
	
	
}
