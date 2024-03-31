package com.foxconn.mechanism.batchDownloadDataset;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Pair;
import com.teamcenter.rac.util.Registry;
//import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;

public class ProgressDialog extends Dialog{
	
	private Registry reg = null;
	private Shell shell = null;
	public Label lab_info = null;
	String selectedPath = null;
	ProgressBar progressBar =null;
	private boolean cancel = false;
	public ProgressDialog (Shell parent, Registry reg,Object[] checkedElements) {
		super(parent);
		this.shell = parent;
		this.reg = reg;
		initUI(checkedElements);
	}
	
	//构建界面
	private void initUI(Object[] checkedElements) {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(591, 103);
		shell.setText("Progress");
		TCUtil.centerShell(shell);
		shell.setLayout(new FormLayout());
		progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		FormData fd_progressBar = new FormData();
		progressBar.setLayoutData(fd_progressBar);													
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancel = true;
				shell.dispose();
			}
		});
		fd_progressBar.bottom = new FormAttachment(btnNewButton, 0, SWT.BOTTOM);
		FormData fd_btnNewButton = new FormData();
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText(reg.getString("cancelButton"));
		
		lab_info = new Label(shell, SWT.NONE);
		fd_btnNewButton.top = new FormAttachment(lab_info, 6);
		fd_btnNewButton.right = new FormAttachment(lab_info, 0, SWT.RIGHT);
		fd_progressBar.right = new FormAttachment(lab_info, -49, SWT.RIGHT);
		fd_progressBar.top = new FormAttachment(lab_info, 6);
		fd_progressBar.left = new FormAttachment(lab_info, 0, SWT.LEFT);
		FormData fd_lab_info = new FormData();
		fd_lab_info.right = new FormAttachment(100, -10);
		fd_lab_info.top = new FormAttachment(0, 10);
		fd_lab_info.left = new FormAttachment(0, 10);
		lab_info.setLayoutData(fd_lab_info);
		lab_info.setText(reg.getString("processing")+"......");
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				doIt(checkedElements);
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
	
	public void setInfo(String info) {
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	lab_info.setText(info);
		    }
		}); 

	}
	
	public void doIt(Object[] checkedElements) {
		try {
			// �ռ��ļ�
			List<Pair<String,String>> list = new ArrayList<>();
			for (int i = 0; i < checkedElements.length; i++) {
				Object object = checkedElements[i];
				BomLineTreeNode node = (BomLineTreeNode) object;
				TCComponentBOMLine bomLine = node.getBomLine();
				String name = bomLine.getItemRevision().getProperty("object_name");
				System.out.println("name-->"+name);
				TCComponentItemRevision latestItemRevision = bomLine.getItem().getLatestItemRevision();
//				List<TCComponent> relatedList = Arrays.asList(latestItemRevision.getRelatedComponents("IMAN_sepcification"));
				List<TCComponent> relatedList = Arrays.asList(latestItemRevision.getRelatedComponents());
//				relatedList.addAll(Arrays.asList(tcComponent.getRelatedComponents("IMAN_Rendering")));
//				relatedList.addAll(Arrays.asList(tcComponent.getRelatedComponents("revision_list")));
				for(int k=0;k<relatedList.size();k++) {
					if(cancel) {
						return;
					}
					// ȥ���Լ�
					TCComponent tcComponent = relatedList.get(k);
					if(tcComponent.equals(bomLine.getItemRevision())) {
						continue;
					}							
					list.addAll(getFiles(tcComponent,name,0));
				}
			}
			if(list.isEmpty()) {
				MessageBox.post(reg.getString("promptInfo3"), reg.getString("prompt"), MessageBox.WARNING);
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	shell.dispose();
				    }
				}); 
				return;
			}
			Display.getDefault().syncExec(new Runnable() {
			    public void run() {
			    	selectedPath = fileDialogExport();
			    }
			}); 
			if(selectedPath==null) {
				return;
			}
			for(int i=0;i<list.size();i++) {
				Pair<String, String> pair = list.get(i);
				String destPath = selectedPath+File.separator+pair.getRight();
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	setInfo(destPath);
				    }
				}); 
				File dest = FileUtil.copy(pair.getLeft(),destPath,true);
				if(!dest.exists()) {
					MessageBox.post(reg.getString("promptInfo4"), reg.getString("prompt"), MessageBox.ERROR);
					return;
				}
			}
			MessageBox.post(reg.getString("promptInfo5"), reg.getString("prompt"), MessageBox.INFORMATION);
		} catch (IORuntimeException e) {
			e.printStackTrace();
			MessageBox.post(reg.getString("error1"), reg.getString("prompt"), MessageBox.WARNING);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(reg.getString("error2"), reg.getString("prompt"), MessageBox.WARNING);
		}finally{
			Display.getDefault().syncExec(new Runnable() {
			    public void run() {
			    	shell.dispose();
			    }
			}); 
		}
	}
	
	private List<Pair<String,String>> getFiles(TCComponent tcComponent,String name,int deep) throws Exception {
		deep++;
		if(deep>2) {
			// ��ֹ�ݹ����̫��
			return new ArrayList<>();
		}
		List<Pair<String,String>> list = new ArrayList<>();
		if(tcComponent instanceof TCComponentDataset) {
			String type = tcComponent.getType();
			if("ProAsm".equalsIgnoreCase(type)||"ProDrw".equalsIgnoreCase(type)||"ProPrt".equalsIgnoreCase(type)) {
				return list;
			}
			TCComponentDataset dataset = (TCComponentDataset)tcComponent;
			dataset.refresh();
			File[] files = dataset.getFiles(null);
			if(files!=null&&files.length>0) {
				for(int x=0;x<files.length;x++) {
					String info = files[x].getAbsolutePath();
					list.add(new Pair<String, String>(info,name+File.separator+files[x].getName()));
					System.out.println("========>"+info);
					setInfo(info);
					
				}
			}
			return list;
		}
		
		List<TCComponent> relatedList = Arrays.asList(tcComponent.getRelatedComponents("IMAN_sepcification"));
//		relatedList.addAll(Arrays.asList(tcComponent.getRelatedComponents("IMAN_Rendering")));
//		relatedList.addAll(Arrays.asList(tcComponent.getRelatedComponents("revision_list")));
		for(int i=0;i<relatedList.size();i++) {
			if(cancel) {
				return list;
			}
			TCComponent com = relatedList.get(i);
			if(com.equals(tcComponent)) {
				// ȥ���Լ�
				continue;
			}
			list.addAll(getFiles(com, name,deep));
		}
		return list;
	}
	
	/**
	 * �ļ�����·��
	 * 
	 * @return
	 */
	private String fileDialogExport() {
		try {
			DirectoryDialog fileDialog = new DirectoryDialog(shell,SWT.OPEN);
		    fileDialog.setFilterPath(System.getProperty("user.dir"));
		    fileDialog.setText("open ");
		    return fileDialog.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void center(Shell shell){
		Rectangle bounds = shell.getDisplay().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}
}



