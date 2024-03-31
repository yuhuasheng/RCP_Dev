package com.foxconn.electronics.matrixbom.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.export.MatrixBOMImputDialog;
import com.foxconn.electronics.matrixbom.export.PackingMatrixImput;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class VariablesBOMService {

	private TCSession session;

	public VariablesBOMService() {
	}

	public VariablesBOMService(AbstractAIFUIApplication app) {
		session = (TCSession) app.getSession();
	}
	
	public AjaxResult add(String request) throws TCException {
		try {
			JSONObject parseObject = JSONObject.parseObject(request);
			String name = parseObject.getString("name");
			String itemId = parseObject.getString("itemId");
			String itemType = parseObject.getString("itemType");
			if(itemType==null || "".equals(itemType)) {
				itemType = "D9_CommonPart";
			}
			String desc = parseObject.getString("desc");
			TCUtil.setBypass(session);
			TCComponentItem createItem = com.foxconn.electronics.util.TCUtil.createItem(session,itemId, "",name,itemType);
			TCComponentItemRevision latestItemRevision = createItem.getLatestItemRevision();
			latestItemRevision.setProperty("d9_EnglishDescription", name);
			
			AjaxResult success = AjaxResult.success();
			success.put("data", createItem.getLatestItemRevision().getUid());
			success.put("itemId", createItem.getProperty("item_id"));
			success.put("itemRevUid", latestItemRevision.getUid());
			
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}finally {
			TCUtil.closeBypass(session);
		}
	}
	
	public AjaxResult bind(String request) throws TCException {
		try {
			JSONObject parseObject = JSONObject.parseObject(request);
			String matrixUid = parseObject.getString("matrixUid");
			TCUtil.setBypass(session);
			TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(matrixUid);
			AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
			TCComponentItem hoderItem;
			if(related.length == 0) {
				// 没有Holder，创建一个
				hoderItem = com.foxconn.electronics.util.TCUtil.createItem(session,"", "","VariantHolder","Part");	
				matrixItemRevision.add("D9_HasVariantHolder_REL",hoderItem.getLatestItemRevision());
			}else {
				hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
			}
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine hoderBOMLine = TCUtil.getTopBomline(bomWindow, hoderItem.getLatestItemRevision());
			AIFComponentContext[] children = hoderBOMLine.getChildren();
			
			JSONArray jsonArray = parseObject.getJSONArray("variableUidList");
			// 去掉已存在的
			for (int i = 0; i < children.length; i++) {		
				TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
				String uid = child.getItemRevision().getUid();
				jsonArray.remove(uid);
			}
			for (int i = 0; i < jsonArray.size(); i++) {
				String variableUid  = jsonArray.getString(i);
				TCComponentItemRevision variableUidItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(variableUid);
				// 挂载
				matrixItemRevision.add("D9_HasVariants_REL",variableUidItemRevision);
				hoderBOMLine.add(variableUidItemRevision.getItem(),variableUidItemRevision, null, false);
				
				//判断需要添加的变体是否存在子健,在matrix下添加物料以及保存数量
				addPart(matrixItemRevision,variableUidItemRevision);
			}
			bomWindow.save();
			bomWindow.close();
			return AjaxResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}finally {
			TCUtil.closeBypass(session);
		}
	}
	
	public AjaxResult setQty(String request) throws TCException {
		try {
			VariableBOMBean variable = JSONObject.parseObject(request,VariableBOMBean.class);
			String variableUid = variable.getBomLineUid();
			String partUid = variable.getItemRevUid();
			Integer qty = Integer.parseInt(variable.getQty());
			TCUtil.setBypass(session);
			TCComponentItemRevision variableItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(variableUid);
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBOMLine = TCUtil.getTopBomline(bomWindow, variableItemRevision);
			AIFComponentContext[] children = topBOMLine.getChildren();
			TCComponentBOMLine target = null;
			for (int i = 0; i < children.length; i++) {		
				TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
				String uid = child.getItemRevision().getUid();
				System.out.println(uid);
				if(partUid.equals(uid)) {
					target = child;
					break;
				}
			}
			if(target == null) {
				TCComponentItemRevision partItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(partUid);
				target = topBOMLine.add(partItemRevision.getItem(), partItemRevision, null, false);
			}
			// 修改數量
			target.setProperty("bl_quantity",""+qty);
			bomWindow.save();
			bomWindow.close();
			return AjaxResult.success(variable);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}finally {
			TCUtil.closeBypass(session);
		}
		
	}
	
	
	
	
	public AjaxResult imputBind(String request) throws TCException {
		try {
			JSONObject parseObject = JSONObject.parseObject(request);
			String matrixUid = parseObject.getString("matrixUid");
			TCUtil.setBypass(session);
			TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(matrixUid);
			AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
			TCComponentItem hoderItem;
			if(related.length == 0) {
				// 没有Holder，创建一个
				hoderItem = com.foxconn.electronics.util.TCUtil.createItem(session,"", "","VariantHolder","Part");	
				matrixItemRevision.add("D9_HasVariantHolder_REL",hoderItem.getLatestItemRevision());
			}else {
				hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
			}
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBOMLine = TCUtil.getTopBomline(bomWindow, hoderItem.getLatestItemRevision());
			JSONArray jsonArray = parseObject.getJSONArray("variableUidList");
			// 去掉已存在的
			AIFComponentContext[] children = topBOMLine.getChildren();
			for (int i = 0; i < children.length; i++) {		
				TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
				String uid = child.getItemRevision().getUid();
				jsonArray.remove(uid);
			}
			for (int i = 0; i < jsonArray.size(); i++) {
				String variableUid  = jsonArray.getString(i);
				TCComponentItemRevision variableUidItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(variableUid);
				// 挂载
				matrixItemRevision.add("D9_HasVariants_REL",variableUidItemRevision);
				topBOMLine.add(variableUidItemRevision.getItem(),variableUidItemRevision, null, false);
			}
			bomWindow.save();
			bomWindow.close();
			return AjaxResult.success();
			
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}
	}
	
	
	public void addPart(TCComponentItemRevision matrixItemRevision, TCComponentItemRevision variableUidItemRevision) throws TCException {
		TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
		TCComponentBOMLine variableBOM = TCUtil.getTopBomline(bomWindow, variableUidItemRevision);
		//解包
		variableBOM.refresh();
		AIFComponentContext[] childrens_Packed = variableBOM.getChildren();
		if(childrens_Packed == null || childrens_Packed.length == 0) {
			bomWindow.close();
			return;
		}
		for (AIFComponentContext aifchildren : childrens_Packed) {
			TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
			if (children.isPacked()) {
				children.unpack();
				children.refresh();
			}
		}
		variableBOM.refresh();
		
		AIFComponentContext[] variableChildren = variableBOM.getChildren();
		if(variableChildren != null && variableChildren.length > 0) {
			TCComponentBOMWindow matrixBomWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine matrix = TCUtil.getTopBomline(matrixBomWindow, matrixItemRevision);
			AIFComponentContext[] matrixChildren = matrix.getChildren();
			
			for (int i = 0; i < variableChildren.length; i++) {
				TCComponentBOMLine bomline_v = (TCComponentBOMLine) variableChildren[i].getComponent();
				if(bomline_v.isSubstitute()) {
					break;
				}
				
				String bl_item_v = bomline_v.getProperty("bl_item_item_id");
				String bl_Category_v = bomline_v.getProperty("bl_occ_d9_Category");
		        String bl_Plant_v = bomline_v.getProperty("bl_occ_d9_Plant");
		        //String bl_quantity_v = bomline_v.getProperty("bl_quantity");
		        String bl_occ_d9_Remark_v = bomline_v.getProperty("bl_occ_d9_Remark");
		        String bl_occ_d9_TorqueIn_v = bomline_v.getProperty("bl_occ_d9_TorqueIn");
		        String bl_occ_d9_TorqueOut_v = bomline_v.getProperty("bl_occ_d9_TorqueOut");
				boolean isadd = true;
				for (AIFComponentContext children_m:matrixChildren) {
					TCComponentBOMLine bomline_m = (TCComponentBOMLine) children_m.getComponent();
					String bl_item_m = bomline_m.getProperty("bl_item_item_id");
					String bl_Category_m = bomline_m.getProperty("bl_occ_d9_Category");
			        String bl_Plant_m = bomline_m.getProperty("bl_occ_d9_Plant");
			        String bl_sequence_no_m = bomline_m.getProperty("bl_sequence_no");
			        String bl_quantity_m = bomline_m.getProperty("bl_quantity");
					String bl_occ_d9_Remark_m = bomline_m.getProperty("bl_occ_d9_Remark");
			        String bl_occ_d9_TorqueIn_m = bomline_m.getProperty("bl_occ_d9_TorqueIn");
			        String bl_occ_d9_TorqueOut_m = bomline_m.getProperty("bl_occ_d9_TorqueOut");
			        
					if(bl_item_m.equals(bl_item_v) && bl_Category_m.equals(bl_Category_v) && bl_Plant_m.equals(bl_Plant_v) ) {
						//Matrix结构下存在,变体中存在替代件，Matrix下可能不存在时，为保证数据一致性，删除Matrix下的物料后重新加
						
						boolean hasSubstitutes_v = bomline_v.hasSubstitutes();
						boolean hasSubstitutes_m = bomline_m.hasSubstitutes();
						if(hasSubstitutes_v && !hasSubstitutes_m) {
							bomline_m.cut();
							matrixBomWindow.save();
							
							TCComponentBOMLine bomlineAdd = matrix.add(bomline_v.getItem(), bomline_v.getItemRevision(), null, false);
							//bomlineAdd.setProperty("bl_quantity", ""+bl_quantity_v);
							bomlineAdd.setProperty("bl_occ_d9_Category", bl_Category_m);
							bomlineAdd.setProperty("bl_occ_d9_Plant", bl_Plant_m);
							bomlineAdd.setProperty("bl_sequence_no", bl_sequence_no_m);
							bomlineAdd.setProperty("bl_quantity", bl_quantity_m);
							bomlineAdd.setProperty("bl_occ_d9_Remark", bl_occ_d9_Remark_m);
							bomlineAdd.setProperty("bl_occ_d9_TorqueIn", bl_occ_d9_TorqueIn_m);
							bomlineAdd.setProperty("bl_occ_d9_TorqueOut", bl_occ_d9_TorqueOut_m);
							
							//是否存在替代料
							if(bomline_v.hasSubstitutes()) {
								System.out.println("存在替代料....");
								TCComponentBOMLine[] listSubstitutes = bomline_v.listSubstitutes();
								for (TCComponentBOMLine listSubstitute:listSubstitutes) {
									bomlineAdd.add(listSubstitute.getItem(), listSubstitute.getItemRevision(), null, true);
								}
							}
						} else if(hasSubstitutes_v && hasSubstitutes_m) {
							TCComponentBOMLine[] listSubstitutes_v = bomline_v.listSubstitutes();
							TCComponentBOMLine[] listSubstitutes_m = bomline_m.listSubstitutes();
							for (TCComponentBOMLine listSubstitute_v:listSubstitutes_v) {
								boolean isSubContains = false;
								if(listSubstitutes_m!=null && listSubstitutes_m.length > 0 ) {
									for (TCComponentBOMLine listSubstitute_m:listSubstitutes_m) {
										String property_v = listSubstitute_v.getProperty("bl_item_item_id");
										String property_m = listSubstitute_m.getProperty("bl_item_item_id");
										if(property_v.equals(property_m)) {
											isSubContains = true;
											break;
										}
									}
								}
								
								if(!isSubContains) {
									bomline_m.add(listSubstitute_v.getItem(), listSubstitute_v.getItemRevision(), null, true);
								}
							}
						}
						
						isadd = false;
						break;
					}
				}
				
				if(isadd) {
					TCComponentBOMLine bomlineAdd = matrix.add(bomline_v.getItem(), bomline_v.getItemRevision(), null, false);
					//bomlineAdd.setProperty("bl_quantity", ""+bl_quantity_v);
					bomlineAdd.setProperty("bl_occ_d9_Category", bl_Category_v);
					bomlineAdd.setProperty("bl_occ_d9_Plant", bl_Plant_v);
					bomlineAdd.setProperty("bl_occ_d9_Remark", bl_occ_d9_Remark_v);
					bomlineAdd.setProperty("bl_occ_d9_TorqueIn", bl_occ_d9_TorqueIn_v);
					bomlineAdd.setProperty("bl_occ_d9_TorqueOut", bl_occ_d9_TorqueOut_v);
					
					//是否存在替代料
					if(bomline_v.hasSubstitutes()) {
						System.out.println("存在替代料..");
						TCComponentBOMLine[] listSubstitutes = bomline_v.listSubstitutes();
						for (TCComponentBOMLine listSubstitute:listSubstitutes) {
							bomlineAdd.add(listSubstitute.getItem(), listSubstitute.getItemRevision(), null, true);
						}
					}
				}
			}
			matrixBomWindow.save();
			matrixBomWindow.close();
		}
		bomWindow.save();
		bomWindow.close();
	}
	
}
