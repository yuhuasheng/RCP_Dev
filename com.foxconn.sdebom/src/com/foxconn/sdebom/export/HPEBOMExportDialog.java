package com.foxconn.sdebom.export;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.util.RuntimeUtil;

import org.eclipse.swt.widgets.Label;

public class HPEBOMExportDialog extends Dialog {

	private Shell shell = null;
	private TCComponentBOMLine topBOMLine;
	private String string;
	public HPEBOMExportDialog(Shell parent,TCComponentBOMLine topBOMLine, String string) {		
		super(parent);
		shell = parent;
		this.string = string;
		this.topBOMLine =topBOMLine;
		createContents();
	}
	
	protected void createContents() {
		Shell parent = shell;
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(500, 74);
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
		String title = "HP EBOM 正在導出...";		
		shell.setText(title);
		shell.setLayout(null);
		
		ProgressBar progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		progressBar.setBounds(0, 0, 484, 27);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {			
				try {
					String exportExcel = HPEBOMExportService.exportExcel(topBOMLine);
					if("PSE".equals(string)) {
						RuntimeUtil.exec("cmd /c start " + exportExcel);
					} else {
						//创建数据集 挂接关系
						TCComponentItemRevision itemRevision = topBOMLine.getItemRevision();
						
						String item_id = itemRevision.getProperty("item_id");
						TCSession session = itemRevision.getSession();
						TCComponentDataset dataSet = TCUtil.createDataSet(session, exportExcel, "MSExcelX", item_id, "excel");
						if(dataSet!=null) {
							itemRevision.setRelated("IMAN_specification", new TCComponent[] {dataSet});
						}
					}
				} catch (Exception e) {
					TCUtil.errorMsgBox(e.getMessage(), "HP EBOM Export");
					e.printStackTrace();
				}finally {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							shell.dispose();
						}
					});
				}
			}
		}).start();
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
