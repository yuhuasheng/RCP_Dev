package com.foxconn.electronics.L10Ebom.dialog;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.foxconn.electronics.L10Ebom.constant.ApplyFormConstant;
import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishBOMToTableBean;
import com.foxconn.electronics.L10Ebom.domain.FinishMatRowBean;
import com.foxconn.electronics.L10Ebom.domain.MatToTableBean;
import com.foxconn.electronics.L10Ebom.util.TableTools;
import com.foxconn.tcutils.constant.ColorEnum;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class L10EBOMApplyFormDialog extends Dialog {
	
	private AbstractAIFUIApplication app = null;
	private Shell shell = null;
	private Shell parent = null;
	private TCSession session = null;
	private TCComponentItemRevision ebomItemRev = null;
	private List<CustTableItem> totalTableItemList = null;
	private TabFolder tabFolder = null;
	private String[] tableTitles = null;
	private List<String> propMappTitle = null;	
	private List<String> matToList = null;	
	private List<String> editorControllerList = null;	
	private String groupName = null;
	private static final String D9_L10_EBOM_APPLY_PROP_MAPP_TITLE = "D9_L10_EBOM_Apply_Prop_Mapp_Title";
//	private static final String D9_L10_EBOM_MAT_TO_TABLE = "D9_L10_EBOM_Mat_To_Table";
	private static final String D9_L10_EBOM_FORM_EDITOR_CONTROLLER = "D9_L10_EBOM_Form_Editor_Controller";	
	private Registry reg = null;	
	
	public L10EBOMApplyFormDialog(AbstractAIFUIApplication app, Shell parent, String str) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parent = parent;		
		TCComponentGroup currentGroup = session.getCurrentGroup();
		try {
			groupName = currentGroup.getProperty("full_name");
		} catch (TCException e) {
			e.printStackTrace();
		}		
		reg = Registry.getRegistry("com.foxconn.electronics.L10Ebom.L10Ebom");
		
		propMappTitle = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_EBOM_APPLY_PROP_MAPP_TITLE); // 获取首选项
		if (CommonTools.isEmpty(propMappTitle)) {
			TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_EBOM_APPLY_PROP_MAPP_TITLE + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
			return;
		}		
		
//		matToList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_EBOM_MAT_TO_TABLE); // 获取首选项
//		if (CommonTools.isEmpty(matToList)) {
//			TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_EBOM_MAT_TO_TABLE + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
//			return;
//		}		

		editorControllerList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_EBOM_FORM_EDITOR_CONTROLLER); // 获取首选项
		if (CommonTools.isEmpty(editorControllerList)) {
			TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_EBOM_FORM_EDITOR_CONTROLLER + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
			return;
		}
		
		initUI();
	}
	
	
 	private void initUI() {
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.MIN); // 用了SWT.PRIMARY_MODAL代表使用了模态
		shell.setSize(1000, 600);
		shell.setText(reg.getString("FinishMatApply.TITLE"));
		
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		
		
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayout(new GridLayout(1, false));
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ebomItemRev = getItemRev();
		if (CommonTools.isEmpty(ebomItemRev)) {
			return;
		}
		
		totalTableItemList = new ArrayList<CustTableItem>();
		for (String str : propMappTitle) {
			String tablePropName = str.split("=")[0].split("##")[0];
			String tableRowType = str.split("=")[0].split("##")[1];
	 		String title = str.split("=")[1];
	 		
	 		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
	 		tabItem.setText(title);
	 		tabItem.setToolTipText(title);
	 		CustTableItem custTableItem = new CustTableItem(shell, session, ebomItemRev,reg, tabFolder, tabItem, tablePropName, tableRowType, title, 
	 				matToList, editorControllerList, groupName, this);
	 		totalTableItemList.add(custTableItem);
		}		
		
		shellListener();
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

 	/**
 	 * 获取客户名称
 	 * @return
 	 * @throws TCException
 	 */
	private String getCustomerName() throws TCException {
		return ebomItemRev.getProperty("d9_Customer");
	}

 	private TCComponentItemRevision getItemRev() {
 		TCComponentItemRevision itemRev = null; 
 		try {
 			InterfaceAIFComponent targetComponent = app.getTargetComponent(); 
 			if (targetComponent instanceof TCComponentFnd0TableRow) {
	 			TCComponentFnd0TableRow rowObj = (TCComponentFnd0TableRow) targetComponent;
	 			itemRev = (TCComponentItemRevision) rowObj.getRelatedComponent("fnd0OwningObject");
			} else if (targetComponent instanceof TCComponentItemRevision) {
				itemRev = (TCComponentItemRevision) app.getTargetComponent();
				String objectType = itemRev.getTypeObject().getName();
				if (!objectType.equalsIgnoreCase(ApplyFormConstant.D9_L10EBOMREQ_REV)) {
					TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
					itemRev = null;
				}
			} else {
				TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
 		return itemRev;
 	} 	
 	
 	
 	/**
	 * 结构树监听
	 */

	private void shellListener() {	
		shell.addShellListener(new ShellAdapter() { // 监听关闭窗体事件			
			public void shellClosed(ShellEvent e) {	
				shell.dispose();
			}
		});
		
//		monitorCutItem();
	}
	
	
	private void monitorCutItem() {
		if (CommonTools.isEmpty(totalTableItemList)) {
			return;
		}
		
		Display display = shell.getDisplay();		
		display.timerExec(1000, new Runnable() {
			
			@Override
			public void run() {
				if (shell.isDisposed()) {
					return;
				}
				int selectionIndex = tabFolder.getSelectionIndex();
				CustTableItem curCustTableItem = null;
				for (int i = 0; i < totalTableItemList.size(); i++) {					
					if (selectionIndex == i) {
						curCustTableItem = totalTableItemList.get(i);
						break;
					}
				}
				
				Composite allComposite = curCustTableItem.getAllComposite();
				if (!allComposite.isDisposed() && !allComposite.isVisible()) {
					allComposite.setVisible(true);
				}
				
				
				Table table = curCustTableItem.getTv().getTable();
				if (!table.isDisposed() && !table.isVisible()) {
					table.setVisible(true);
				}
				
				Button saveBtn = curCustTableItem.getSaveBtn();
				if (!saveBtn.isDisposed() && !saveBtn.isVisible()) {
					saveBtn.setVisible(true);
				}
				
				Button cancelBtn = curCustTableItem.getCancelBtn();
				if (!cancelBtn.isDisposed() && !cancelBtn.isVisible()) {
					cancelBtn.setVisible(true);
				}
							
				
		        display.timerExec(1000, this); // 继续定时检测
			}
		});
	}

	public List<CustTableItem> getTotalTableItemList() {
		return totalTableItemList;
	}


	public void setTotalTableItemList(List<CustTableItem> totalTableItemList) {
		this.totalTableItemList = totalTableItemList;
	}


	public List<String> getPropMappTitle() {
		return propMappTitle;
	}


	public void setPropMappTitle(List<String> propMappTitle) {
		this.propMappTitle = propMappTitle;
	}


	public List<String> getMatToList() {
		return matToList;
	}


	public void setMatToList(List<String> matToList) {
		this.matToList = matToList;
	}	
	
}
