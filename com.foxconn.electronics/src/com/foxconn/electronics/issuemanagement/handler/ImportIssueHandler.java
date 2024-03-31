package com.foxconn.electronics.issuemanagement.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.issuemanagement.dialog.ImportIssueDialog;
import com.foxconn.tcutils.util.ExcelUtils;
import com.foxconn.tcutils.util.IndeterminateLoadingDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soa.client.model.strong.ImanIDCreator;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

public class ImportIssueHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		Shell shell = app.getDesktop().getShell();
		if(!(targetComponent instanceof TCComponentFolder)) {
			MessageBox.post(shell, "請先選擇一個文件夾來保存導入的Issue", "Info", MessageBox.INFORMATION);
			return null;
		}		
		new ImportIssueDialog(null, app, shell, (TCComponentFolder) targetComponent);
		return null;
	}

}
