package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.Constants;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialBatchImportAction;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialConfigEntity;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialDataEntity;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialProperty;
import com.foxconn.mechanism.custommaterial.custommaterialnumrequest.SupplierEntity;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soa.client.model.LovInfo;
import com.teamcenter.soa.client.model.LovValue;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

public class CustomMaterialNumService{
	
	private CustomMaterialNumDialog window;

	public CustomMaterialNumService(CustomMaterialNumDialog window) {
		this.window = window; 
	}
	
	public void onChangeItem(CCombo cmb) {
		
		String value = cmb.getText(); //获取combo下拉框选择的值
		
		if(cmb==window.cmb_place_of_shipment) {
			window.cmb_place_of_shipment_child.removeAll();
			String parentValue = window.cmb_place_of_shipment.getText();
			LovInfo childLovInfo = (LovInfo) window.cmb_place_of_shipment.getData(parentValue);
			for (LovValue childLov : childLovInfo.getValues()){
				String childValue = childLov.getStringValue();
				window.cmb_place_of_shipment_child.add(childValue);
	        }
		}
				
		if(cmb==window.cmb_49pcb_BOARD_maker_code) {
			cmb.setData("d9_ManufacturerID",null);
			window.txt_supplier.setText("");
			window.txt_contact.setText("");
			window.txt_fax.setText("");
			window.txt_tel.setText("");
			window.txt_address.setText("");
			String filter = value;
			List<SupplierEntity> list = getSupplier("makerName",filter);		
			if(list != null && list.size()==1) {
				SupplierEntity item = list.get(0);
				window.txt_supplier.setText(item.name);
				window.txt_contact.setText(item.contact);
				window.txt_fax.setText(item.fax);
				window.txt_tel.setText(item.tel);
				window.txt_address.setText(item.address);
				cmb.setData("d9_ManufacturerID", item.manufacturerID);
			}else {
				MessageBox.post(value + " 供应商信息不全，请管理员维护供应商信息", "提示", MessageBox.WARNING);
    			cmb.select(-1);
			}
			
		}
		
		if(cmb==window.cmb_79_production_type) {
			boolean ft = "INCLUDE (SMD/AI/OTHER/PRE-FORMING)".equals(value) || "IC+FW".equals(value);
			if(!ft) {
				window.cmb_derivative_model_has.select(1);
				window.lab_derivative_model_id_code.setVisible(ft);
				window.txt_derivative_model_id_code.setVisible(ft);
				window.lab_derivative_model_id_code_star.setVisible(ft);
			}

			
			window.lab_derivative_model_has.setVisible(ft);
			window.cmb_derivative_model_has.setVisible(ft);
			window.lab_derivative_model_has_star.setVisible(ft);
			
			window.labe_pin_ming_consulting_star.setVisible(!ft);
		}
		
		if(cmb==window.cmb_derivative_model_has) {
			
			boolean ft = "是".equals(value);
			window.lab_derivative_model_id_code.setVisible(ft);
			window.txt_derivative_model_id_code.setVisible(ft);
			window.lab_derivative_model_id_code_star.setVisible(ft);
			window.labe_pin_ming_consulting_star.setVisible(ft);
			if(!ft) {
				// 否
				window.txt_derivative_model_id_code.setText("");
				window.lab_pcba_matiralNo.setText("PCBA 料號");
			}else {
				// 是
				window.lab_pcba_matiralNo.setText("原 PCBA 料號");
			}
		}
		
		if(cmb==window.cmb_79_project) {
			if (CustomMaterialNumDialog.ACTION_CREATE.equals(window.action)) {
				String finishModelName = window.txt_finished_product_model_name.getText();
				if(StrUtil.isNotEmpty(finishModelName)&&window.cmb_derivative_model_has.getVisible()) {
					checkFolder(window,getProject(cmb),finishModelName);
				}
				
			}
		}
		if(cmb==window.cmb_matrial_no_big_category) {
			String text = window.cmb_matrial_no_big_category.getText();
			if(CustomMaterialNumDialog.LABEL_79.equals(window.label)) {
				if (CustomMaterialNumDialog.ACTION_CREATE.equals(window.action)) {
					window.cmb_79_project.setEnabled("B8X80:PCBA".equals(text));
				}
			}			
		}
		
	}
	
	public void onLostFocus(Text txt) {
		if(txt == window.txt_pin_ming_consulting) {
			if(CustomMaterialNumDialog.LABEL_49.equals(window.label)||
					CustomMaterialNumDialog.LABEL_79.equals(window.label)||
					CustomMaterialNumDialog.LABEL_629.equals(window.label)||
					CustomMaterialNumDialog.LABEL_7351.equals(window.label)) {
				if(CustomMaterialNumDialog.ACTION_CREATE.equals(window.action)) {
					String itemId = txt.getText();
					if(StrUtil.isNotBlank(itemId)){
						TCComponentItemRevision rev = null;
						ProjectCCombo cmb = window.projectCComboMap.get(window.label);
						try {
							rev = TCUtil.findItem(itemId).getLatestItemRevision();
							if(rev!=null) {
								String projectIdStr = rev.getProperty("project_ids");
								if(StrUtil.isEmptyIfStr(projectIdStr)){
									throw new Exception();
								}
								String[] projectIds = projectIdStr.split(",");
								String projectId = projectIds[0];
								
								TCComponentProject[] data = (TCComponentProject[]) cmb.getData();
								for(int i = 0;i<data.length;i++) {
									TCComponentProject project = data[i];
									if(projectId.equals(project.getProjectID())) {
										cmb.select(i);
										cmb.setEnabled(false);										
										Event event = new Event();
										event.type = SWT.Selection;
										event.widget = cmb;
										Listener[] listeners = cmb.getListeners(SWT.Selection);
										if(listeners.length >0) {
											listeners[0].handleEvent(event);
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							MessageBox.post("根据PCBA料号未查询到专案，请手动选择","错误",MessageBox.ERROR);
							cmb.select(-1);
							cmb.setEnabled(true);
						}
					}
				}
			}
		}
	}
	
	
	
	public static TCComponent onComplete(String label,String action,TCComponent tcCom,Map<String,String> outData) throws Exception{
		
		// 计算临时料号
		String num = calcTempNum(label,outData);
		List<CustomMaterialProperty> dataMap = CustomMaterialBatchImportAction.getFieldMap(new CustomMaterialDataEntity(), label);
		String tcType = dataMap.get(0).tctype();
		Map<String,Object> tcMap = new HashedMap<>();
		tcMap.put("d9_TempPN", num);
		tcMap.put("d9_FoxconnModelName", outData.get("成品Model Name 全值"));
		if(label.equals(CustomMaterialNumDialog.LABEL_49)) {
			tcMap.put("d9_ManufacturerID", outData.get("d9_ManufacturerID"));
		}
		
		if(!CustomMaterialNumDialog.ACTION_MODIFY.equals(action)) {
			tcMap.put("d9_Is2ndSource", CustomMaterialNumDialog.ACTION_2ND.equals(action));
		}				
		for(CustomMaterialProperty property : dataMap) {
			String key = property.value();
			String excelproperty = property.excelproperty();
			String value = outData.get(excelproperty);
			if("物料群組".equals(excelproperty)||"進料方式".equals(excelproperty)||"用量單位".equals(excelproperty)) {
				value = value.split(CustomMaterialNumDialog.lovSeparator)[0];						
				tcMap.put(key,value.split(":")[0]);
			}else {
				if(value!=null) {
					value = value.split(CustomMaterialNumDialog.lovSeparator)[0];
					tcMap.put(key,value);
					if(label.equals(CustomMaterialNumDialog.LABEL_79)&&action.equals(CustomMaterialNumDialog.ACTION_CREATE)) {
						if("PRODUCTION TYPE".equals(excelproperty)) {
							if("INCLUDE (SMD/AI/OTHER/PRE-FORMING)".equals(value)) {
								tcMap.put("item_revision_id","00");
							}else {
								tcMap.put("item_revision_id","A");
							}	
						}														
					}
				}
			}					
		}
		if(CustomMaterialNumDialog.LABEL_8.equals(label)) {
			tcMap.put("d9_FoxconnModelName", outData.get("保存_前的值"));
			tcMap.put("d9_DerivativeTypeDC", outData.get("保存_后的值"));
			tcMap.put("d9_ModelName", outData.get("0-6"));
		}
		tcMap.put("d9_ActualUserID", outData.get("實際用戶"));
		if(CustomMaterialNumDialog.LABEL_713.equals(label)) {
			tcMap.put("d9_ShippingArea", outData.get("出貨地"));
		}
		if(CustomMaterialNumDialog.ACTION_MODIFY.equals(action)) {
			for(String key:tcMap.keySet()) {
				try {
					tcCom.setProperty(key, ""+tcMap.get(key));						
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}else if(CustomMaterialNumDialog.ACTION_2ND.equals(action)) {
			String mainId = tcCom.getProperty("item_id");
			String tempPN = num+"#"+mainId;
			if(CustomMaterialNumDialog.LABEL_49.equals(label)) {
				num = num.replace("%s4d", mainId.substring(4,8));
				if(num.equals(mainId)) {
					throw new Exception("請更換製造商");
				}
				tempPN = num+"#"+mainId;
				// 查詢__D9_Find_PCBByTempPN				
				TCComponent[] executeQuery = TCUtil.executeQuery(TCUtil.getTCSession(), "__D9_Find_PCBByTempPN", new String[] {"d9_TempPN"},new String[] {tempPN});
				if(executeQuery.length > 0) {
					throw new Exception("已申請過Second Source，不能重複申請");
				}
			}
			tcMap.put("d9_TempPN", tempPN);
			try {
				TCUtil.setBypass(TCUtil.getTCSession());
				TCComponent secondSoureComponent = TCUtil.createCom(TCUtil.getTCSession(), tcType, "", tcMap);
				TCComponentItemRevision itemRevision= (TCComponentItemRevision) tcCom;	
				AIFComponentContext[] whereReferenced = itemRevision.getItem().whereReferenced();
				for(AIFComponentContext context : whereReferenced) {
					 InterfaceAIFComponent component = context.getComponent();
					if (component instanceof TCComponentFolder) {
						String type = component.getType();
						if("Folder".equals(type)) {
							((TCComponentFolder) component).add("contents", secondSoureComponent);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				TCUtil.closeBypass(TCUtil.getTCSession());
			}
		}else {
			Set<String> keySet = outData.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()) {
				String key = iterator.next();
				if(key.startsWith("direct_")) {
					String value = outData.get(key);
					key = key.replaceFirst("direct_", "");
					tcMap.put(key, value);
				}
			}
			TCComponent tccom = CustomMaterialBatchImportAction.createCustomMaterial2(tcCom,tcType, "", tcMap);
			String pcbaItemId = outData.get("OldPCBAItemId");
			if(pcbaItemId != null) {
				// 將xx添加到衍生幾種文件夾
				TCComponentItem pabcItem = TCUtil.findItem(pcbaItemId);
				TCComponentItem tcItem = (TCComponentItem) tccom;
				TCComponentItemRevision itemRev = tcItem.getLatestItemRevision();
				itemRev.add("D9_HasSourceBOM_REL", pabcItem.getLatestItemRevision());
			}			
			return tccom;
		}
		return tcCom;		
	}
	
	public static String calcTempNum(String label,Map<String,String> outData) {
		String num="";
		List<CustomMaterialProperty> fieldMap = CustomMaterialBatchImportAction.getFieldMap(new CustomMaterialConfigEntity(), label);
		for(CustomMaterialProperty proerty : fieldMap) {
			String configtype = proerty.configtype();
			String name = proerty.configproperty();
			if("F".equals(configtype)){
				num+=proerty.value();
			}else if("A".equals(configtype)) {
				String configproperty = proerty.configproperty();
				String value = outData.get(configproperty).split(CustomMaterialNumDialog.lovSeparator)[1];
				num+=value.toUpperCase(Locale.ROOT);
			}else if("I".equals(configtype)) {
				String configproperty = proerty.configproperty();
				String value = outData.get(configproperty);
				int size = proerty.configsize();
				int position = proerty.configposition();
				num+=value.substring(position-1,position-1+size).toUpperCase(Locale.ROOT);
			}else if("M".equals(configtype)) {
				String configproperty = proerty.configproperty();
				String value = outData.get(configproperty);
				num+=value.toUpperCase(Locale.ROOT);
			}else if(name.equals("成品Model Name")&&label.equals(CustomMaterialNumDialog.LABEL_79)) {
				String tempNum = outData.get("tempNum");						
				num+=tempNum.toUpperCase(Locale.ROOT);
			}
			
		}
		System.out.println(num);
		return num;
	}
	
	public List<SupplierEntity> getSupplier(String key,String filter){
		List<SupplierEntity> list = getSupplierInfo(key,filter);
		for(SupplierEntity entity:list) {
			if(entity.address == null) {
				entity.address = "";
			}
			if(entity.name == null) {
				entity.name = "";
			}
			if(entity.contact == null) {
				entity.contact = "";
			}
			if(entity.fax == null) {
				entity.fax = "";
			}
			if(entity.tel == null) {
				entity.tel = "";
			}
			if(entity.manufacturerID == null) {
				entity.manufacturerID = "";
			}
		}
		return list;
	}
	
	private List<SupplierEntity> getSupplierInfo(String key,String value) {
		List<SupplierEntity> list = new ArrayList<>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(key, value);
			String ip = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
			String rs = HttpUtil.post(ip+":80" + "/tc-integrate/maker/searchMakerInfo", jsonObject.toJSONString());
			JSONObject jsonObj = JSON.parseObject(rs);
			JSONArray jsonArr = JSON.parseArray(jsonObj.getString("data"));
			for (Object object : jsonArr) {
				JSONObject jsonObj2 = (JSONObject)object;
				SupplierEntity supplierEntity = new SupplierEntity();
				supplierEntity.name = jsonObj2.getString("makerName");
				supplierEntity.tel = jsonObj2.getString("tel");
				supplierEntity.contact = jsonObj2.getString("contactMan");
				supplierEntity.fax = jsonObj2.getString("faxNumber");
				supplierEntity.address = jsonObj2.getString("address");
				supplierEntity.manufacturerID = jsonObj2.getString("manufacturerID");
				list.add(supplierEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;		
	}
	
	public static void checkFolder(CustomMaterialNumDialog window,TCComponentProject project,String finishModelName) {
		
		if(project==null) {
			return;
		}
		
		if(StrUtil.isEmptyIfStr(finishModelName)) {
			return;
		}
		
		if(StrUtil.isEmptyIfStr(finishModelName.trim())) {
			return;
		}
		
		// 遍历文件夹				 
		try {
			String projectId = project.getProjectID();
			TCComponent[] queryResult = TCUtil.executeQuery((TCSession)window.app.getSession(), "__D9_Find_Project_Folder", new String[] {"d9_SPAS_ID"}, new String[] {projectId});
			if(queryResult.length>0) {
				TCComponentFolder projectFolder = (TCComponentFolder) queryResult[0];
				TCComponentFolder selfPartFolder = getSelfPartFolder(projectFolder);	
				if(selfPartFolder!=null) {
					boolean found = false;
					AIFComponentContext[] projectChildren = selfPartFolder.getChildren();
					for (int i = 0; i < projectChildren.length;i++) {
						TCComponent projectChildrenFolder =  (TCComponent) projectChildren[i].getComponent();
						String folderName = projectChildrenFolder.getProperty("object_name");					
						if (folderName.equals(finishModelName)) {
							found = true;
							break;
						}
					}
					window.cmb_derivative_model_has.select(found?0:1);	
					window.txt_derivative_model_id_code.setText("");
					Listener[] listeners = window.cmb_derivative_model_has.getListeners(SWT.Selection);
					Event event = new Event();
					event.type = SWT.Selection;
					event.widget = window.cmb_derivative_model_has;
					listeners[0].handleEvent(event);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//获取自编料号文件夹
	public static TCComponentFolder getSelfPartFolder(TCComponent projectFolder) throws Exception {
		AIFComponentContext[] childrenFolder = projectFolder.getRelated(Constants.CONTENTS);
		if(childrenFolder.length > 0) {
			for (int i = 0; i < childrenFolder.length; i++) {
				TCComponent component = (TCComponent) childrenFolder[i].getComponent();
				String name = component.getProperty(Constants.OBJECT_NAME);
				if(name.equals("自編物料協同工作區")) {
					return (TCComponentFolder)component;
				}
				TCComponentFolder folder = getSelfPartFolder(component);
				if(folder!=null) {
					return folder;
				}
			}
		}
		return null;
	}
	
	public static TCComponentProject getProject(CCombo projectCombo) {
		if(projectCombo==null) {
			return null;
		}
		String objectName = projectCombo.getText();
		if(StrUtil.isEmptyIfStr(objectName)) {
			return null;
		}
		TCComponentProject project = null;
		TCComponentProject[] allProjects = (TCComponentProject[]) projectCombo.getData();
		for(TCComponentProject p : allProjects) {
			String projectName = com.foxconn.tcutils.util.TCUtil.getProperty(p,"object_name");     	
        	if(objectName.equals(projectName)) {
        		project = p;
        		break;
        	}
        }
		return project;
	}
	
	
}
