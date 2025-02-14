package com.foxconn.mechanism.mntL5MgfEcnManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.foxconn.mechanism.pacmaterial.PACImportBOMLoadingDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.cm.CMSoaHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.util.StrUtil;

public class CreateEcnService {
	
	private CreateEcnDialog window;
	
	private List<Button> buttonList = new ArrayList<>();
	
	public CreateEcnService(CreateEcnDialog window) {
		this.window = window;
	}
	
	public void onGUIInited() {
		
		try {
			ArrayList<String> userList = com.foxconn.tcutils.util.TCUtil.getLovValues(window.session, (TCComponentItemRevisionType) window.session.getTypeComponent("ItemRevision"), "d9_ActualUserID");
			for(String user:userList) {
				window.cmb_real_user.add(user);					
			}
			window.cmb_real_user.setData(userList);
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
		
	}
	
	public void onAssignECN() {
		try {
			String generateId = TCUtil.generateId(window.session,"&quot;CSAP&quot;NNNNNN","D9_MNT_MCN_L5");						
			window.txt_ecn_num.setText(generateId);
			window.bt_assign_ecn.setEnabled(false);
			window.cmb_ecn_role.setEnabled(false);
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onComplete() {
		TCComponentBOMWindow bomWindow = null;
		try {
			if(window.bt_assign_ecn.isEnabled()) {
				MessageBox.post(window.shell, "請先指派ECN編號", "提示", MessageBox.INFORMATION);
				return;
			}
			String name = window.txt_name.getText();
			if(StrUtil.isEmpty(name)) {
				MessageBox.post(window.shell, "請先填寫概述", "提示", MessageBox.INFORMATION);
				return;
			}
			String itemId = window.txt_ecn_num.getText();
			String revisionID = "01";
			Map<String,String> map = new HashMap<>();
			map.put("d9_ActualUserID", window.cmb_real_user.getText());
			map.put("object_desc", window.txt_change_desc.getText());		
			TCComponent createCom = TCUtil.createComWithOutCatch(window.session, "D9_MNT_MCN_L5", itemId, name, revisionID, map);
			TCComponentItem ecnItem = (TCComponentItem) createCom;
			TCComponentItemRevision ecnRevision = ecnItem.getLatestItemRevision();
			TCComponentFolder homeFolder = window.session.getUser().getHomeFolder();
			homeFolder.add("contents", createCom);
			if(TCUtil.isReleased(window.itemRev)) {
				// 升版
				TCComponentItemRevision revise = TCUtil.doRevise(window.itemRev);
				ecnRevision.add("CMHasSolutionItem", revise);
				ecnRevision.add("CMHasProblemItem", window.itemRev);
				ecnRevision.add("CMHasImpactedItem", window.itemRev);
				CMSoaHelper.getInstance().setImpactedOfSolution((TCComponentChangeItemRevision) ecnRevision, revise, window.itemRev);
				// 刷新結構管理
				InterfaceAIFComponent target = AIFUtility.getTargetComponent();
				if(target instanceof TCComponentBOMLine) {
					TCComponentBOMLine b = (TCComponentBOMLine) target;
					b.window().newIrfWhereConfigured(revise);
					b.window().fireChangeEvent();
				}
			}else {
				bomWindow = TCUtil.createBOMWindow(window.session);
				TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, window.itemRev);				
				TCUtil.ergodicBOM(topBomline, new TCUtil.EergodicAction() {
					@Override
					public void onAction(TCComponentBOMLine bomLine) throws Exception {					
						// 處理邏輯
						TCComponentItemRevision itemRevision = bomLine.getItemRevision();
						if(TCUtil.isReleased(itemRevision)) {
							return;
						}
						// 把自己加入到解決方案項
						ecnRevision.add("CMHasSolutionItem", itemRevision);
						
						// 把上一個版本加入到問題項和受影響項
						TCComponentItemRevision previousRevision = TCUtil.getPreviousRevision(itemRevision);
						if(previousRevision==null) {
							return;
						}
						ecnRevision.add("CMHasProblemItem", itemRevision);
						ecnRevision.add("CMHasImpactedItem", itemRevision);
					}
				});
			}
			MessageBox.post(window.shell, "已創建ECN，在HOME下", "提示", MessageBox.INFORMATION);
			window.shell.dispose();
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}finally {
			try {
				if(bomWindow!=null) {
					bomWindow.close();
				}
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
			}
		}
		
	}

}
