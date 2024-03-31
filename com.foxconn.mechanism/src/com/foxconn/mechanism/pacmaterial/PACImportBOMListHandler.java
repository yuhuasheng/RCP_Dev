package com.foxconn.mechanism.pacmaterial;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class PACImportBOMListHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Registry registry = null;
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		InterfaceAIFComponent[] aifCom = app.getTargetComponents();
		// 限制只選一個
		if(aifCom.length != 1){
			MessageBox.post("只能选择一个对象", "", MessageBox.WARNING);
			return null;
		}
		
		InterfaceAIFComponent aif = aifCom[0];
		
//		if(aif instanceof TCComponentItemRevision){
//			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow((TCSession) app.getSession());
//			TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, (TCComponentItemRevision)aif);
//			try {
//				com.foxconn.tcutils.util.TCUtil.unpackageBOMStructure(topBomline);
//				removeBomLine(topBomline,bomWindow);
//				bomWindow.close();
//				MessageBox.post("已移除所有BOM行", "", MessageBox.INFORMATION);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
		
		if(!(aif instanceof TCComponentFolder)){
			MessageBox.post("只能选择文件夹", "", MessageBox.ERROR);
			return null;
		}
		
		Shell shell = app.getDesktop().getShell();	
		
		new PACImportBOMDialog(registry, app, shell, (TCComponentFolder) aifCom[0]);
		return null; 
	}
	
	private void removeBomLine(TCComponentBOMLine parentBomLine,TCComponentBOMWindow bomWindow) throws TCException {
		if(TCUtil.isReleased(parentBomLine.getItemRevision())) {
			return;
		}
		AIFComponentContext[] children = parentBomLine.getChildren();
		for (AIFComponentContext aifComponentContext : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) aifComponentContext.getComponent();
			removeBomLine(bomLine,bomWindow);
			bomLine.cut();
			bomWindow.save();
		}
	}
}
