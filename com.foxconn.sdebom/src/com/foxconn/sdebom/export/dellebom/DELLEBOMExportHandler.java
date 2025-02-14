package com.foxconn.sdebom.export.dellebom;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.helpers.HeaderFooterHelper;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.pse.AbstractPSEApplication;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class DELLEBOMExportHandler extends AbstractHandler{
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
			Shell shell = app.getDesktop().getShell();			
			TCComponentBOMLine topBOMLine = (TCComponentBOMLine) app.getTargetComponent();
			new DELLEBOMExportDialog(shell,topBOMLine);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

}
