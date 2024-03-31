package com.foxconn.electronics.pamatrixbom.service;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.pamatrixbom.domain.PAVariableBOMBean;
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

public class PAVariablesBOMService {

	private TCSession session;

	public PAVariablesBOMService() {
	}

	public PAVariablesBOMService(AbstractAIFUIApplication app) {
		session = (TCSession) app.getSession();
	}
	
	/**
	 * 变体查询
	 * @param request
	 * @return
	 * @throws TCException
	 */
	public AjaxResult search(String itemids,String englishDescription) throws TCException {
		ArrayList<PAVariableBOMBean> rootBeanlist = new ArrayList<PAVariableBOMBean>();
		TCComponentItemRevision itemrevision = null;
		if((englishDescription==null || "".equals(englishDescription)) && (itemids==null || "".equals(itemids))) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "请输入条件后再查询！");
		}
		
		if(englishDescription==null || "".equals(englishDescription)) {
			englishDescription = "*";
		}
		if(itemids==null || "".equals(itemids)) {
			itemids = "*";
		}
		
		try {
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "__D9_Find_MatrixBOMParts", new String[] { "items_tag.item_id","d9_EnglishDescription" },
					new String[] { itemids, englishDescription});
			if (executeQuery != null && executeQuery.length > 0) {
				System.out.println(executeQuery.length);
				
				for (int i = 0; i < executeQuery.length; i++) {
					if (executeQuery[i] instanceof TCComponentItemRevision) {
						
						itemrevision = (TCComponentItemRevision) executeQuery[i];
						if (itemrevision != null) {
							PAVariableBOMBean rootBean = new PAVariableBOMBean();
							rootBean = PAVariableBOMBean.getBean(rootBean, itemrevision);
							rootBean.setReleased(TCUtil.isReleased(itemrevision));
							rootBean.setItemRevUid(itemrevision.getUid());
							rootBeanlist.add(rootBean);
						}
					}
				}
			} else {
				return AjaxResult.success("");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}
		
		String jsons = JSONArray.toJSONString(rootBeanlist);
		System.out.print("json ==" + jsons);
		return AjaxResult.success("执行成功", rootBeanlist);
	}
	
	/**
	 * 挂载变体
	 * @param request
	 * @return
	 * @throws TCException
	 */
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
				hoderItem = TCUtil.createItem(session,"", "","VariantHolder","Part");	
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
				hoderItem = TCUtil.createItem(session,"", "","VariantHolder","Part");	
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
		        //String bl_quantity_v = bomline_v.getProperty("bl_quantity");
		       
				boolean isadd = true;
				for (AIFComponentContext children_m:matrixChildren) {
					TCComponentBOMLine bomline_m = (TCComponentBOMLine) children_m.getComponent();
					String[] properties = bomline_m.getProperties(new String[] {"bl_item_item_id","bl_sequence_no","bl_quantity","bl_occ_d9_Remark"});
					String bl_item_m = properties[0];
			        String bl_sequence_no_m = properties[1];
			        String bl_quantity_m = properties[2];
					String bl_occ_d9_Remark_m = properties[3];
			        
					if(bl_item_m.equals(bl_item_v) ) {
						//Matrix结构下存在,变体中存在替代件，Matrix下可能不存在时，为保证数据一致性，删除Matrix下的物料后重新加
						
						boolean hasSubstitutes_v = bomline_v.hasSubstitutes();
						boolean hasSubstitutes_m = bomline_m.hasSubstitutes();
						if(hasSubstitutes_v && !hasSubstitutes_m) {
							bomline_m.cut();
							matrixBomWindow.save();
							
							TCComponentBOMLine bomlineAdd = matrix.add(bomline_v.getItem(), bomline_v.getItemRevision(), null, false);
							//bomlineAdd.setProperty("bl_quantity", ""+bl_quantity_v);
							
							String[] proName = new String[] {"bl_sequence_no","bl_quantity","bl_occ_d9_Remark"};
							String[] proValue = new String[] {bl_sequence_no_m,bl_quantity_m,bl_occ_d9_Remark_m};
							bomlineAdd.setProperties(proName,proValue);
							
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
					String bl_occ_d9_Remark_v = bomline_v.getProperty("bl_occ_d9_Remark");
					TCComponentBOMLine bomlineAdd = matrix.add(bomline_v.getItem(), bomline_v.getItemRevision(), null, false);
					bomlineAdd.setProperty("bl_occ_d9_Remark", bl_occ_d9_Remark_v);
					
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
