package com.foxconn.sdebom.export.dellebom;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.sdebom.constant.DatasetEnum;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.util.RuntimeUtil;

import org.eclipse.swt.widgets.Label;

public class DELLEBOMExportDialog extends Dialog {

	private Shell shell = null;
	private TCComponentBOMLine topBOMLine;

	public DELLEBOMExportDialog(Shell parent,TCComponentBOMLine topBOMLine) {		
		super(parent);
		shell = parent;
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
		String title = "DELL EBOM 正在導出...";		
		shell.setText(title);
		shell.setLayout(null);
		
		ProgressBar progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		progressBar.setBounds(0, 0, 484, 27);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {			
				try {
					String exportExcel = DELLEBOMExportService.exportExcel(topBOMLine);
					RuntimeUtil.exec("cmd /c start " + exportExcel);
					deletAllExcel(topBOMLine.getItemRevision());
					addExcel(topBOMLine.getItemRevision(), exportExcel);
				} catch (Exception e) {
					TCUtil.errorMsgBox(e.getMessage(), "DELL EBOM Export");
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
			
			private void deletAllExcel(TCComponentItemRevision itemRevision) {
				try {
					TCComponent[] datesets = itemRevision.getRelatedComponents("IMAN_specification");
			        for (TCComponent tcComponent : datesets){
			            if (tcComponent instanceof TCComponentDataset){
			                TCComponentDataset tcComponentDataset = (TCComponentDataset) tcComponent;
			                for (String fileName : tcComponentDataset.getFileNames(null)){
			                    System.out.println(fileName);
			                    itemRevision.remove("IMAN_specification", tcComponentDataset);
			                }
			            }
			        }	
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			private void addExcel(TCComponentItemRevision itemRevision,String excelPath) {
				try {
					File file = new File(excelPath);
					TCComponentDataset dataset = TCUtil.createDataSet(TCUtil.getTCSession(), file.getAbsolutePath(), DatasetEnum.MSExcelX.type(),file.getName(), DatasetEnum.MSExcelX.refName());
					itemRevision.add("IMAN_specification", dataset);	
				}catch (Exception e) {
					e.printStackTrace();
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
