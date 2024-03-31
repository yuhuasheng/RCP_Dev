package com.foxconn.mechanism.mntL5MgfEcnManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import com.foxconn.mechanism.pacmaterial.PACImportBOMLoadingDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.cm.CMSoaHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class RelateExsitingService {
	
	private RelateExsitingECNDialog window;
	
	private List<Button> buttonList = new ArrayList<>();
	
	private Button bt_selected = null;
	
	public RelateExsitingService(RelateExsitingECNDialog window) {
		this.window = window;
	}
	
	public void onGUIInited() {
		try {
			String userId = window.session.getUser().getUserId();
			TCComponent[] executeQuery = TCUtil.executeQuery(window.session, "__D9_Find_MNTL5MFG_ECN", new String[] { "owning_user.user_id" }, new String[]{userId});
			for(int i=0;i<executeQuery.length;i++) {
				TCComponent tccom = executeQuery[i];
				TCComponentItemRevision ecnItemRev = (TCComponentItemRevision) tccom;
				TCComponentForm form = null;
				TCComponent[] relatedComponents = ecnItemRev.getRelatedComponents("IMAN_specification");
				for (TCComponent tcComponent : relatedComponents) {
					String typeName = tcComponent.getTypeObject().getName();
					if (tcComponent instanceof TCComponentForm && "D9_MNT_DCNForm".equals(typeName)) {
						form = (TCComponentForm) tcComponent;
						break;
					}
				}
				if(form==null) {
					continue;
				}
				ECNBean ecnBean = new ECNBean();
				ecnBean.ecnRevId = ecnItemRev.getUid();
				ecnBean.ecnNumber = ecnItemRev.getProperty("item_id");
				ecnBean.revNumber = ecnItemRev.getProperty("current_revision_id");
				ecnBean.objectName = ecnItemRev.getProperty("object_name");
				ecnBean.realUser = ecnItemRev.getProperty("d9_ActualUserID");
				ecnBean.desc = ecnItemRev.getProperty("object_desc");
				ecnBean.changeType = form.getProperty("d9_ChangeType");
				ecnBean.changeScope = form.getProperty("d9_ChangeScope");
				ecnBean.mntCustomer = form.getProperty("d9_MNTCustomer");
				ecnBean.modelName = form.getProperty("d9_ModelName");
				ecnBean.changeClassScope = form.getProperty("d9_ChangeClassScope");
				ecnBean.changeClass = form.getProperty("d9_ChangeClass");
				ecnBean.priorityClass = form.getProperty("d9_PriorityClass");
				ecnBean.bomChangeOrNot = form.getProperty("d9_BOMChangeOrNot");
				ecnBean.noticeAfterChange = form.getProperty("d9_NoticeAfterChange");
				ecnBean.noticeBeforeChange = form.getProperty("d9_NoticeBeforeChange");
				ecnBean.applierManager = form.getProperty("d9_ApplierManager");
				ecnBean.applierSupervisor = form.getProperty("d9_ApplierSupervisor");
				TableItem tableItem = new TableItem(window.table, SWT.NONE);
				tableItem.setText(1, ecnBean.ecnNumber);
				tableItem.setText(2, ecnBean.revNumber);
				tableItem.setText(3, ecnBean.objectName);
				tableItem.setText(4, ecnBean.realUser);
				tableItem.setText(5, ecnBean.desc);
				tableItem.setText(6, ecnBean.changeType);
				tableItem.setText(7, ecnBean.changeScope);
				tableItem.setText(8, ecnBean.mntCustomer);
				tableItem.setText(9, ecnBean.modelName);
				tableItem.setText(10, ecnBean.changeClassScope);
				tableItem.setText(11, ecnBean.changeClass);
				tableItem.setText(12, ecnBean.priorityClass);
				tableItem.setText(13, ecnBean.bomChangeOrNot);
				tableItem.setText(14, ecnBean.noticeAfterChange);
				tableItem.setText(15, ecnBean.noticeBeforeChange);
				tableItem.setText(16, ecnBean.applierManager);
				tableItem.setText(17, ecnBean.applierSupervisor);
				Button button = new Button(window.table,SWT.CHECK);
				button.setData(ecnBean);
				buttonList.add(button);
				TableEditor editor = new TableEditor(window.table);
				editor.horizontalAlignment = SWT.CENTER;
				editor.verticalAlignment = SWT.CENTER;
				editor.grabHorizontal = true;	
				editor.grabVertical = true;
				editor.setEditor(button, tableItem, 0);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button source = (Button) e.widget;
						if(source.getSelection()) {
							bt_selected = source;
						}
					}
				});
			}
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void onComplete() {
		try {
			if(bt_selected==null) {
				return;
			}
			ECNBean data = (ECNBean) bt_selected.getData();
			TCComponentItemRevision ecnRevision = TCUtil.findItemRevistion(data.ecnRevId);
			// 升版
			TCComponentItemRevision revise = TCUtil.doRevise(window.itemRev);
			String object_string = revise.getProperty("object_string");
			try{
				ecnRevision.add("CMHasSolutionItem", revise);
				ecnRevision.add("CMHasProblemItem", window.itemRev);
				ecnRevision.add("CMHasImpactedItem", window.itemRev);
				window.session.getUser().getHomeFolder().add("contents", ecnRevision.getItem());
			}catch (Exception e) {
				// TODO: handle exception
			}
			CMSoaHelper.getInstance().setImpactedOfSolution((TCComponentChangeItemRevision) ecnRevision, revise, window.itemRev);
			// 刷新結構管理
			InterfaceAIFComponent target = AIFUtility.getTargetComponent();
			if(target instanceof TCComponentBOMLine) {
				TCComponentBOMLine b = (TCComponentBOMLine) target;
				b.window().newIrfWhereConfigured(revise);
				b.window().fireChangeEvent();
			}
			MessageBox.post(window.shell, object_string+"已升版完畢，並關聯"+data.ecnNumber+"，請調整升版對象！", "Info", MessageBox.INFORMATION);
			window.shell.dispose();
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
	}

}
