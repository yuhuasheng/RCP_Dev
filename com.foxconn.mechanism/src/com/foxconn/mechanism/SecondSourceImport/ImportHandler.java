package com.foxconn.mechanism.SecondSourceImport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
/**
 * 
 * @author wt00110
 *    物料轉廠
 */
public class ImportHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		Registry reg = null;
		app = AIFUtility.getCurrentApplication();
		reg = Registry.getRegistry("com.foxconn.mechanism.SecondSourceImport.secondsourceimport");
		InterfaceAIFComponent aifCom = app.getTargetComponent();
	
			if(!(aifCom instanceof TCComponentFolder)) {		
				MessageBox.post(AIFUtility.getActiveDesktop(), "请选择一个文件夹", "Error", MessageBox.ERROR);
				return null;
			}	
			TCSession session = (TCSession) this.app.getSession();
			String bu="";
			try {
			String gn=session.getCurrentGroup().getFullName();
			if(gn.contains("Monitor")) {
				bu="MNT";
			}else if(gn.contains("DeskTop")) {
				bu="DT";
			}else if(gn.contains("Printer")) {
				bu="PRT";
			}
			System.out.print(gn);
			}catch(Exception e) {}
			
		if("".equalsIgnoreCase(bu)) {
			new SelectBuDialog(app,new Shell(),reg,(TCComponentFolder)aifCom);
			
		}else {
			new SelectFileDialog(app,new Shell(),reg,(TCComponentFolder)aifCom,bu);
		}	
		
		
		return null;
	}
	
	
}
