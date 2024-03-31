package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialBatchImportAction;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.util.StrUtil;

public class FinishGoodSerivce {
	
	private FinishGoodDialog window;
	private int tableCopyMaxColumn = 17;
	private Map<String, Map<String, String>> finishGoodConfig; 
	private String label="8-FINISH GOOD";
	
	public FinishGoodSerivce(FinishGoodDialog window) {
		this.window = window; 
	}
	
	public void onGUIInited(TCComponentItemRevision itemRevision){
		// 回显
		try {
			// 讀取配置
			Map<String, Map<String, Map<String, String>>> customMaterialConfig = CustomMaterialBatchImportAction.getCustomMaterialConfig();
			finishGoodConfig = customMaterialConfig.get("8-FINISH GOOD");	
			// 讀取表格
			TCComponent[] relatedComponents = itemRevision.getRelatedComponents("d9_BOMReqTable1");
			for (int i = 0;i<relatedComponents.length;i++) {
				TCComponent tcComponent = relatedComponents[i];
				TCComponentFnd0TableRow row = (TCComponentFnd0TableRow)tcComponent;
				String customerModelName = row.getProperty("d9_CustomerModelName");
				String foxconnModelName = row.getProperty("d9_FoxconnModelName");
				String finishPNDesc = row.getProperty("d9_FinishPNDesc");
				String shippingArea = row.getProperty("d9_ShippingArea");
				String powerLineType = row.getProperty("d9_PowerLineType");
				String pcbaInterface = row.getProperty("d9_PCBAInterface");
				String isSpeaker = row.getProperty("d9_IsSpeaker");				
				String color = row.getProperty("d9_Color");
				String wWireType = row.getProperty("d9_WireType");
				String other = row.getProperty("d9_Other");
				String shipSize = row.getProperty("d9_ShipSize");
				String shipType = row.getProperty("d9_ShipType");
				String refMaterialPN = row.getProperty("d9_RefMaterialPN");
				String remark = row.getProperty("d9_Remark");
				TableItem tableItem = new TableItem(window.table, SWT.NONE);
				String MODEL_REGISTERED_WITH_SEQUENTIAL;
				try {
					MODEL_REGISTERED_WITH_SEQUENTIAL = foxconnModelName.substring(4,6);
				}catch (Exception e) {
					MODEL_REGISTERED_WITH_SEQUENTIAL = "";
				}
				String SCALER_IC;
				try {
					SCALER_IC = String.valueOf(foxconnModelName.charAt(8));
				}catch (Exception e) {
					SCALER_IC = "";
				}
				tableItem.setText(new String[]{"","",MODEL_REGISTERED_WITH_SEQUENTIAL,"",SCALER_IC,"","","","","B4W10:LCD MONITOR","","","實階",foxconnModelName,finishPNDesc,"","",customerModelName,foxconnModelName,finishPNDesc,shippingArea,powerLineType,pcbaInterface,isSpeaker,color,wWireType,other,shipSize,shipType,refMaterialPN,remark});
				// 自動設置值
				try {
					int columnIndex = 0;
					String theValue = foxconnModelName.substring(2,4);
					Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
					Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
					while(iterator.hasNext()) {
						Entry<String, String> next = iterator.next();
						String key = next.getKey();
						String value = next.getValue();
						if(theValue.equals(value)) {
							tableItem.setText(columnIndex,key);
							tableItem.setData(key,value);
							break;
						}					
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					int columnIndex = 1;
					String theValue = null;
					char c = foxconnModelName.charAt(1);	
					if(c=='E' || c=='e') {
						theValue = "1";
					}else if(c=='P' || c=='p') {
						theValue = "2";
					}
					if(theValue!=null) {
						Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
						Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, String> next = iterator.next();
							String key = next.getKey();
							String value = next.getValue();
							if(theValue.equals(value)) {
								tableItem.setText(columnIndex,key);
								tableItem.setData(key,value);
								break;
							}					
						}
					}				
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					int columnIndex = 3;
					String theValue = String.valueOf(foxconnModelName.charAt(7));	
					if(theValue!=null) {
						Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
						Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, String> next = iterator.next();
							String key = next.getKey();
							String value = next.getValue();
							if(theValue.equals(value)) {
								tableItem.setText(columnIndex,key);
								tableItem.setData(key,value);
								break;
							}					
						}
					}				
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					int columnIndex = 6;
					String theValue = "有".equals(isSpeaker)?"1":"0";
					if(theValue!=null) {
						Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
						Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, String> next = iterator.next();
							String key = next.getKey();
							String value = next.getValue();
							if(theValue.equals(value)) {
								tableItem.setText(columnIndex,key);
								tableItem.setData(key,value);
								break;
							}					
						}
					}				
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					int columnIndex = 7;
					String substring = powerLineType.substring(0,powerLineType.indexOf(':'));
					if(substring.length()==1) {
						substring = "0" + substring;
					}
					String theValue = substring;
					if(theValue!=null) {
						Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
						Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, String> next = iterator.next();
							String key = next.getKey();
							String value = next.getValue();
							if(theValue.equals(value)) {
								tableItem.setText(columnIndex,key);
								tableItem.setData(key,value);
								break;
							}					
						}
					}				
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					int columnIndex = 11;
					String theValue = "E";
					if(theValue!=null) {
						Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
						Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, String> next = iterator.next();
							String key = next.getKey();
							String value = next.getValue();
							if(theValue.equals(value)) {
								tableItem.setText(columnIndex,key);
								tableItem.setData(key,value);
								break;
							}					
						}
					}				
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					int columnIndex = 16;
					String theValue = "PC";
					if(theValue!=null) {
						Map<String, String> map = finishGoodConfig.get(window.table.getColumn(columnIndex).getText());
						Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, String> next = iterator.next();
							String key = next.getKey();
							String value = next.getValue();
							if(theValue.equals(value)) {
								tableItem.setText(columnIndex,key);
								tableItem.setData(key,value);
								break;
							}					
						}
					}				
				}catch (Exception e) {
					e.printStackTrace();
				}
	
			}	
			
			TCSession session = (TCSession) window.app.getSession();
			ArrayList<String> userList = com.foxconn.tcutils.util.TCUtil.getLovValues(session, (TCComponentItemRevisionType) session.getTypeComponent("ItemRevision"), "d9_ActualUserID");
			for(String user:userList) {
				window.cmb_actuality_user.add(user);					
			}
			window.cmb_actuality_user.setData(userList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onTableClick(Event event) {
		Table table = window.table;
		Control oldEditor = window.editor.getEditor();
        if (oldEditor != null) {
            oldEditor.dispose();
        }

        Point point = new Point(event.x, event.y);
        final TableItem item = table.getItem(point);
        if (item == null) {
            return;
        }

        int columnIndex = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            Rectangle bounds = item.getBounds(i);
            if (bounds.contains(point)) {
                columnIndex = i;
                break;
            }
        }

        final int cIndex = columnIndex;
        TableColumn tableColumn = table.getColumn(cIndex);
        String column = tableColumn.getText();
        
        if(cIndex==2||cIndex==4||cIndex==10||cIndex==13||cIndex==14||cIndex==15) {
        	final Text txt = new Text(table, SWT.NONE);
	        String text = item.getText(cIndex);
	        txt.setText(text);

	        // 设置编辑器控件到TableEditor
	        window.editor.setEditor(txt, item, columnIndex);

	        // 添加事件监听，当编辑器控件失去焦点或按下回车键时保存修改并清除编辑器
	        txt.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					String text = ((Text) e.getSource()).getText();
					item.setText(cIndex, text);
					txt.dispose();
				}
				
				@Override
				public void focusGained(FocusEvent arg0) {

				}
			});
        }
        if (cIndex == 0 || cIndex == 1 || cIndex == 3|| cIndex == 5|| cIndex == 6|| cIndex == 7|| cIndex == 8|| cIndex == 9|| cIndex == 11|| cIndex == 12|| cIndex == 16) {
        	final CCombo cmb = new CCombo(table, SWT.NONE);
        	Map<String, String> map = finishGoodConfig.get(column);
        	Set<String> keySet = map.keySet();
        	Iterator<String> iterator = keySet.iterator();
        	while (iterator.hasNext()) {
				String key = iterator.next();
				cmb.add(key);
				String value = map.get(key);
				cmb.setData(key, value);
			}
	        cmb.setEditable(false);
	        String text = item.getText(cIndex);
	        cmb.setText(text);

	        // 设置编辑器控件到TableEditor
	        window.editor.setEditor(cmb, item, columnIndex);

	        // 添加事件监听，当编辑器控件失去焦点或按下回车键时保存修改并清除编辑器
	        cmb.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// 处理事件
					String text = ((CCombo) e.getSource()).getText();
	                item.setText(cIndex, text);
	                String value = (String) cmb.getData(text);
	                item.setData(text, value);
	                cmb.dispose();
				}
			});
        }
	}

	public void onCopy(TableItem item) {
		boolean confirm = MessageDialog.openQuestion(window.shell, "複製","你想把這行數據複製到其他行嗎？");
		if(confirm) {
			for(int i=0;i<tableCopyMaxColumn;i++) {
				// 排除自動獲取默認的欄位
				if(i==5||i==8||i==10||i==15) {
					String text = item.getText(i);
					String value = (String) item.getData(text);
					TableItem[] items = window.table.getItems();
					for(TableItem tableItem:items) {
						tableItem.setText(i, text);
						tableItem.setData(text,value);
					}
				}
			}
		}
	}
	
	public void onSave(TCComponentItemRevision selectedItemRevision) throws Exception {
		
		String actualUser = window.cmb_actuality_user.getText();
		if(StrUtil.isEmptyIfStr(actualUser)) {
			MessageBox.post("實際用戶必須填寫","校驗",MessageBox.ERROR);
			return;
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		TableColumn[] columns = window.table.getColumns();
		TableItem[] items = window.table.getItems();
		for(int i=0;i<items.length;i++) {
			TableItem row = items[i];
			HashMap<String, String> map = new HashMap<String, String>();
			for(int j=0;j<columns.length;j++) {
				TableColumn tableColumn = columns[j];
				String column = tableColumn.getText();
				String text = row.getText(j);
				if("PRODUCT TYPE".equals(column)||"PRODUCT CLASSIFICATION".equals(column)
						||"MODEL REGISTERED,WITH SEQUENTIAL".equals(column)||"PANEL SOURCE".equals(column)
						||"SCALER IC".equals(column)||"CUSTOMER CODE".equals(column)||"AUDIO INPUT".equals(column)
						||"POWER CORD TYPE".equals(column)||"ENVIRONMENTAL".equals(column)||"物料群組".equals(column)
						||"進料方式".equals(column)||"自製方式".equals(column)||"成品Model Name".equals(column)
						||"英文描述".equals(column)||"中文描述".equals(column)||"用量單位".equals(column)) {
					if(StrUtil.isEmpty(text)) {
						MessageBox.post("第"+(i+1)+"行，【"+column+"】不能為空","校驗",MessageBox.ERROR);
						return;
					}
				}
				if("MODEL REGISTERED,WITH SEQUENTIAL".equals(column)) {
					if(text.length()!=2) {
						MessageBox.post("第"+(i+1)+"行，【"+column+"】長度必須為2","校驗",MessageBox.ERROR);
						return;
					}
				}
				if("SCALER IC".equals(column)) {
					if(text.length()!=1) {
						MessageBox.post("第"+(i+1)+"行，【"+column+"】長度必須為1","校驗",MessageBox.ERROR);
						return;
					}
				}
				if("成品Model Name".equals(column)) {
					if(text.length()<6) {
						MessageBox.post("第"+(i+1)+"行，【"+column+"】最少6個字符","校驗",MessageBox.ERROR);
						return;
					}
					String[] split = text.split("_");
					map.put("保存_前的值", split[0]);
					if(split.length==2) {																
						map.put("保存_后的值", split[1]);
					}else {
						map.put("保存_后的值", "");
					}
					map.put("0-6", text.substring(0, 6));
					map.put("物料類型", "ZFRT");
				}
				if("MODEL REGISTERED,WITH SEQUENTIAL".equals(column)||"SCALER IC".equals(column)) {
					map.put(column, text);
				}else {
					map.put(column, text+CustomMaterialNumDialog.lovSeparator+row.getData(text));
				}
				if("客戶 ModelName".equals(column)||"Foxconn ModelName".equals(column)||"成品料號描述".equals(column)||"出貨地區".equals(column)
						||"電源線類型".equals(column)||"PCBA接口".equals(column)||"有無喇叭".equals(column)||"顏色".equals(column)
						||"外部線材".equals(column)||"其他".equals(column)||"出貨優選方式尺寸".equals(column)||"出貨優選方式形式".equals(column)
						||"參照BOM料號".equals(column)||"備註".equals(column)) {					
					map.put("direct_"+tableColumn.getData("tcColumn"),text);
				}
			}				
			map.put("實際用戶", actualUser);
			list.add(map);
		}
				
		List<TCComponentItem> createdItemList = new ArrayList<TCComponentItem>();
//		MessageBox.post("测试时只会生成2行数据","注意",MessageBox.INFORMATION);
		try {
			for(int i=0;i<list.size();i++) {
				createdItemList.add(doSave(selectedItemRevision,list.get(i)));
			}			
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e.toString(),"错误",MessageBox.ERROR);
			// 回滚，将已生成的都删掉
			for(TCComponentItem item:createdItemList) {
				selectedItemRevision.remove("D9_BOMReq_L10B_REL",item.getLatestItemRevision());
				item.delete();
			}
			return;
		}
		MessageBox.post("完成","提示",MessageBox.INFORMATION);
	}
	
	public TCComponentItem doSave(TCComponentItemRevision selectedItemRevision,Map<String,String> outData) throws Exception {	
		System.out.println(outData);
		
		// 保存數據
		TCComponent onComplete = CustomMaterialNumService.onComplete(label, CustomMaterialNumDialog.ACTION_CREATE, null, outData);
		
		// 指派專案
		String projectIdStr = selectedItemRevision.getProperty("project_ids");
		String[] projectIds = null;
		if (!"".equals(projectIdStr)) {
			projectIds = projectIdStr.split(",");
		}		
		TCComponent[] queryResult = com.foxconn.tcutils.util.TCUtil.executeQuery((TCSession) window.app.getSession(), "__D9_Find_Project", new String[] {"project_id"},projectIds);
		TCComponent[] itemRevisions = {onComplete};
		for (int j = 0; j < queryResult.length; j++) {
			TCComponentProject project = (TCComponentProject) queryResult[j];
			project.assignToProject(itemRevisions);			
		}
		
		//將版本掛載到成品料號目錄
		selectedItemRevision.add("D9_BOMReq_L10B_REL",((TCComponentItem)onComplete).getLatestItemRevision());
		
		// 挂载BOM
		String itemId = outData.get("direct_d9_RefMaterialPN");
		TCComponentItem findItem = TCUtil.findItem(itemId);
		if(findItem!=null) {
			List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();		
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow((TCSession) window.app.getSession());
			TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, findItem.getLatestItemRevision());
//			TCUtil.unpackageBOMStructure(topBomline);
			AIFComponentContext[] children = topBomline.getChildren();
			for(AIFComponentContext child : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) child.getComponent();			
				String id = bomLine.getItemRevision().getProperty("item_id");
				String type = bomLine.getItemRevision().getType();
				System.out.println(id);
				System.out.println(type);
				if(id.startsWith("713")&&"D9_VirtualPartRevision".equals(type)) {
					continue;
				}
				Map<String,Object> map = new HashMap<>();
				map.put("item",bomLine.getItem());
				map.put("isSub",false);
				itemList.add(map);
				TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
				for(TCComponentBOMLine sub :listSubstitutes) {
					id = sub.getItemRevision().getProperty("item_id");
					type = sub.getItemRevision().getType();
					if(id.startsWith("713")&&"D9_VirtualPartRevision".equals(type)) {
						continue;
					}
					map = new HashMap<>();
					map.put("item",sub.getItem());
					map.put("isSub",true);
					itemList.add(map);
				}
			}
			bomWindow.close();
			if(!itemList.isEmpty()) {
				bomWindow = TCUtil.createBOMWindow((TCSession) window.app.getSession());
				topBomline = TCUtil.getTopBomline(bomWindow, onComplete);
				TCComponentBOMLine lastAddMainBomLine = null;
				for(Map<String,Object> map:itemList) {
					TCComponentItem item = (TCComponentItem) map.get("item");
					boolean isSub = (boolean)map.get("isSub");
					if(isSub) {
						lastAddMainBomLine.add(item,item.getLatestItemRevision(),null,isSub);
					}else {
						lastAddMainBomLine = topBomline.add(item,item.getLatestItemRevision(),null,isSub);
					}
					
				}
				bomWindow.save();
				bomWindow.close();
			}
		}
		
		return (TCComponentItem) onComplete;
	}
	
}
