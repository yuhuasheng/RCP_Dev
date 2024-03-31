package com.foxconn.mechanism.mntdcn;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
import com.teamcenter.rac.cm.CMSoaHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

public class CreateMNTDCNService {
	
	private CreateMNTDCNDialog window;
	
	public CreateMNTDCNService(CreateMNTDCNDialog window) {
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
	
	public void onAssignDCN() {
		try {
			String generateId = TCUtil.generateId(window.session,"&quot;DCN&quot;NNNNNN","D9_MNT_DCN");						
			window.txt_ecn_num.setText(generateId);
			window.bt_assign_ecn.setEnabled(false);
			window.cmb_ecn_role.setEnabled(false);
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
	}
	
	public void onAssignRev() {
		try {
			String id="01";
			int selectionIndex = window.cmb_rev_role.getSelectionIndex();
			if(selectionIndex==1) {
				id="A";
			}			
			window.txt_rev_num.setText(id);
			window.bt_assign_rev.setEnabled(false);
			window.cmb_rev_role.setEnabled(false);
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
	}
	
	public void onComplete() {
		try {
			if(window.bt_assign_ecn.isEnabled() || window.bt_assign_rev.isEnabled()) {
				MessageBox.post(window.shell, "請先指派DECN編號和版本號", "提示", MessageBox.INFORMATION);
				return;
			}
			String name = window.txt_name.getText();
			if(StrUtil.isEmpty(name)) {
				MessageBox.post(window.shell, "請先填寫概述", "提示", MessageBox.INFORMATION);
				return;
			}
			String itemId = window.txt_ecn_num.getText();
			String revisionID = window.txt_rev_num.getText();
			Map<String,String> map = new HashMap<>();
			map.put("d9_ActualUserID", window.cmb_real_user.getText());
			TCComponent createCom = TCUtil.createComWithOutCatch(window.session, "D9_MNT_DCN", itemId, name, revisionID, map);
			TCComponentItem dcnItem = (TCComponentItem) createCom;
			TCComponentItemRevision dcnRevision = dcnItem.getLatestItemRevision();
			TCComponentFolder homeFolder = window.session.getUser().getHomeFolder();
			homeFolder.add("contents", createCom);
			for(TCComponentItemRevision itemRev :window.itemRevList) {
				// 發送到結構管理器
				TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(window.session);
				TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, itemRev);
				String[] strArray = {"",""};
				TCUtil.ergodicBOM(topBomline,new TCUtil.EergodicAction() {
					
					@Override
					public void onAction(TCComponentBOMLine bomLine) throws Exception {						
						// 把自己放入解決方案項
						try {
							TCComponentItemRevision itemRev = bomLine.getItemRevision();
							if(!TCUtil.isReleased(itemRev)) {
								dcnRevision.add("CMHasSolutionItem", itemRev);
								strArray[0] += itemRev.getProperty("item_id") + ";";
							}							
						}catch (Exception e) {
							String msg = "";
							String dcn = TCUtil.findDCN(itemRev);
							if(dcn==null) {
								msg = e.getMessage();
							}else {
								String item = itemRev.getProperty("object_string");
								msg = item +"在"+dcn+"的解决方案项中，该物件处于未发布状态，请先发行";
							}
							strArray[1] += msg + "\n";
						}						
					}
				});
				// 查詢出除基線版本的上一個版本對象
				TCComponent[] executeQuery = TCUtil.executeQuery(window.session, "__D9_Find_BOMParts", new String[] {"items_tag.item_id"}, new String[] {strArray[0]});
				for(TCComponent tccom:executeQuery) {
					TCComponentItemRevision previousRevision = (TCComponentItemRevision) tccom;
					if(previousRevision!=null) {
						// 把上一版放入問題項和受影響項
						try {
							dcnRevision.add("CMHasProblemItem", previousRevision);
						}catch (Exception e) {
							strArray[1] += previousRevision.getProperty("object_string")+"添加到問題項失敗，原因："+ e.toString() + "\n";
						}
						try {
							dcnRevision.add("CMHasImpactedItem", previousRevision);
						}catch (Exception e) {
							strArray[1] += previousRevision.getProperty("object_string")+"添加到受影響項失敗，原因："+ e.toString() + "\n";
						}
					}
				}
				if(!strArray[1].isEmpty()) {
					File tempFile = FileUtil.createTempFile();
					FileUtil.writeString(strArray[1], tempFile,StandardCharsets.UTF_8);
					String absolutePath = tempFile.getAbsolutePath();
					TCComponentDataset createDataSet = TCUtil.createDataSet(TCUtil.getTCSession(), absolutePath, "Text", "ErrorLog.txt", "Text");				
					dcnRevision.add("IMAN_specification", createDataSet);
					MessageBox.post(window.shell, "創建DCN有錯誤，請查看DCN下ErrorLog.txt文件", "ERR", MessageBox.ERROR);
				}
				bomWindow.close();				
			}
			MessageBox.post(window.shell, "已創建DCN，在HOME下", "提示", MessageBox.INFORMATION);
			window.shell.dispose();
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
		}
	}
	
	

}
