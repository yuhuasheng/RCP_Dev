package com.foxconn.electronics.matrixbom.export;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.electronics.matrixbom.service.MatrixBOMService;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;

import org.eclipse.swt.widgets.Label;

public class MatrixBOMExportDialog extends Dialog {

	private Shell shell = null;
	private TCComponent targetComponent;
	private AbstractAIFUIApplication app;

	public MatrixBOMExportDialog(Shell parent,TCComponent targetComponent,AbstractAIFUIApplication app) {		
		super(parent);
		this.app = app;
		shell = parent;
		this.targetComponent = targetComponent;
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
		String type = targetComponent.getType();
		boolean isDCN = !"D9_ProductLineRevision".equals(type);
		String title = isDCN?"Matrix BOM Change List 正在導出...":"Matrix BOM 正在導出...";		
		shell.setText(title);
		shell.setLayout(null);
		
		ProgressBar progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		progressBar.setBounds(0, 0, 484, 27);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {			
				try {
					if(isDCN) {
						MatrixBOMExportService.exportDCNExcel(targetComponent);
					}else {
						MatrixBOMExportService.setMatrixBOMService(app);
						String excelPath = MatrixBOMExportService.exportBOMExcel(targetComponent.getUid());
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								File file = new File(excelPath);
								FileDialog fileDialog = new FileDialog(shell,SWT.SAVE);
								fileDialog.setFileName(file.getName());
								fileDialog.setFilterPath(TCUtil.getSystemDesktop());
								fileDialog.setFilterNames(new String[] { "Microsoft Excel(*.xlsx)", "Microsoft Excel(*.xls)" });
								fileDialog.setFilterExtensions(new String[] { "*.xlsx", "*.xls" });
								String dest = fileDialog.open();
								FileUtil.copy(file, new File(dest), true);
								MessageBox.post("已导出", "提示", 2);
							}
						});
					}
				} catch (Exception e) {
					TCUtil.errorMsgBox(e.getMessage(), "Matrix Change List Export");
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
