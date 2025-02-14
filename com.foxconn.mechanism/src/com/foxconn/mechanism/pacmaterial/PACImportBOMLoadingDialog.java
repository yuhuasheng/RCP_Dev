package com.foxconn.mechanism.pacmaterial;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.util.RuntimeUtil;
import www.fjj.com.HttpUtil;

import org.eclipse.swt.widgets.Label;

public class PACImportBOMLoadingDialog extends Dialog {

	private Shell shell = null;
	public static interface Action{
		void run();
	}
	Action action;

	public PACImportBOMLoadingDialog(Shell parent, String title,Action action) {
		super(parent);
		shell = parent;
		this.action = action;
		createContents(title);
	}

	protected void createContents(String title) {
		Shell parent = shell;
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(500, 67);
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
		shell.setText(title);
		shell.setLayout(null);
		
		ProgressBar progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		progressBar.setBounds(0, 0, 484, 27);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					if(action!=null) {
						action.run();
					}
				} catch (Exception e) {
					TCUtil.errorMsgBox(e.getMessage(), "ERROR");
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
