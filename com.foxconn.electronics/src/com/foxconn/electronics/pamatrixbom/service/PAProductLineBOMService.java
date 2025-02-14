package com.foxconn.electronics.pamatrixbom.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.foxconn.electronics.managementebom.createDCN.DCNController;
import com.foxconn.electronics.managementebom.createDCN.DCNService;
import com.foxconn.electronics.matrixbom.constant.MatrixBOMConstant;
import com.foxconn.electronics.pamatrixbom.domain.PAProductLineBOMBean;
import com.foxconn.mechanism.integrate.hhpnIntegrate.HHPNPojo;
import com.foxconn.tcutils.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.services.loose.core.ProjectLevelSecurityService;
import com.teamcenter.services.loose.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.Type;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpUtil;

public class PAProductLineBOMService {

	private TCSession session;

	public static String tempPath = System.getProperty("java.io.tmpdir");
	
	public PAProductLineBOMService() {
	}

	public PAProductLineBOMService(AbstractAIFUIApplication app) {
		session = (TCSession) app.getSession();
	}
	
	
	
	/**
	 * PA 查询零件
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult queryPaParts(String itemids,String englishDescription) {
		String D9_SpringCloud_URL = TCUtil.getPreference(session,
				TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
		if (D9_SpringCloud_URL == null || "".equalsIgnoreCase(D9_SpringCloud_URL)) {
			
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "D9_SpringCloud_URL查询构建器不存在，请联系管理员！");
		}
		String url = D9_SpringCloud_URL + "/tc-integrate/agile/partManage/getPartInfo";

		
		ArrayList<PAProductLineBOMBean> rootBeanlist = new ArrayList<PAProductLineBOMBean>();
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
							
							String item_id = itemrevision.getProperty("item_id");
							
							PAProductLineBOMBean rootBean = new PAProductLineBOMBean();
							
							try {
								rootBean = getSapBean(url, item_id, rootBean);
							} catch (Exception e) {
								return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
							}
							
							rootBean = PAProductLineBOMBean.getBean(rootBean, itemrevision);
							rootBean.setReleased(TCUtil.isReleased(itemrevision));
							boolean itemWrite = TCUtil.checkOwninguserisWrite(session, itemrevision);
							rootBean.setItemEnabled(itemWrite);
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
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		}

		String jsons = JSONArray.toJSONString(rootBeanlist);
		System.out.print("json ==" + jsons);
		return AjaxResult.success("执行成功", rootBeanlist);
	}
	
	/**
	 * 创建零件指派ID
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult newIDMatrixParts() {
		try {
			TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("D9_CommonPart");
			String item_id = itemType.getNewID();
			String itemRev = itemType.getNewRev(null);

			Map<String, String> map = new HashMap<String, String>();
			map.put("item_id", item_id);
			map.put("item_revision_id", itemRev);

			return AjaxResult.success("执行成功", map);

		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * PA 创建零件
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult addPaParts(String data) {
		try {
			Gson gson = new Gson();
			PAProductLineBOMBean rootBean = gson.fromJson(data, PAProductLineBOMBean.class);

			String itemid = rootBean.getItemId();
			String rev = rootBean.getItemRevision();
			String itemType = rootBean.getItemType();

			if (itemType == null || "".equals(itemType)) {
				itemType = "D9_CommonPart";
			}

			TCComponentItemRevision latestItemRevision = null;
			if (!getRightStr(itemid)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "输入的ID不正确");
			}

			TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...", new String[] { "items_tag.item_id" },
					new String[] { itemid });
			if (executeQuery != null && executeQuery.length > 0) {
				for (int i = 0; i < executeQuery.length; i++) {
					if (executeQuery[i] instanceof TCComponentItemRevision) {
						String type = executeQuery[i].getType();
						if("D9_L5_PartRevision".equals(type)) {
							continue;
						}
						latestItemRevision = (TCComponentItemRevision) executeQuery[i];
					}
				}
			} else {
				TCComponentItem newItem = createItem(session, itemid, rev, "", itemType);
				latestItemRevision = newItem.getLatestItemRevision();
				TCComponentFolder homeFolder = session.getUser().getHomeFolder();
				homeFolder.add("contents", newItem);
			}
			
			// 设置零件属性
			PAProductLineBOMBean.modifyPAItem(session, latestItemRevision, rootBean);
			
			rootBean.setItemRevision(latestItemRevision.getProperty("item_revision_id"));
			rootBean.setItemId(latestItemRevision.getProperty("item_id"));
			rootBean.setItemRevUid(latestItemRevision.getUid());
			latestItemRevision.refresh();

			String jsons = JSONArray.toJSONString(rootBean);
			System.out.print("json ==" + jsons);
			return AjaxResult.success("执行成功", rootBean);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * PA 添加结构
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult addPaBOM(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {
			Gson gson = new Gson();
			List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
			}.getType());
			if (rootBeanList != null && rootBeanList.size() > 0) {
				String productLineItemUID = rootBeanList.get(0).getProductLineItemUID();
				TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);
				bomwindow = createBOMWindow(session);
				TCComponentBOMLine topbom = getTopBomline(bomwindow, roductLineItemRev);

				for (int k = 0; k < rootBeanList.size(); k++) {
					PAProductLineBOMBean rootBean = rootBeanList.get(k);
					TCComponentBOMLine bomline;
					String item_id = rootBean.getItemId();
					String ver = rootBean.getItemRevision();
					
					TCComponentItemRevision queryItemRev = queryItemRev(item_id, ver);
					if (queryItemRev != null) {
						if(TCUtil.isReleased(queryItemRev)) {
							queryItemRev = queryItemRev.saveAs("");
							rootBean.setItemRevision(queryItemRev.getProperty("item_revision_id"));
							rootBean.setItemRevUid(queryItemRev.getUid());
							rootBean.setReleased(TCUtil.isReleased(queryItemRev));
						}
						String sap_rev = rootBean.getSap_rev();
						if(sap_rev!=null && !"".equals(sap_rev)) {
							queryItemRev.setProperty("d9_SAPRev", sap_rev);
							rootBean.setsAPRev(sap_rev);
						}
						
						bomline = addbomline(bomwindow, topbom, queryItemRev);
						// 设置属性
						bomline.setProperty("bl_uom", "");
						String[] bomValue = new String[] { "1", rootBean.getRemark(), rootBean.getIsNew(),rootBean.getD9_BOMTemp() };
						String[] bomName = new String[] {"bl_quantity", "bl_occ_d9_Remark", "bl_occ_d9_IsNew","bl_occ_d9_BOMTemp" };
						bomline.setProperties(bomName, bomValue);
						
						String bomlineuid = bomline.getProperty("bl_occ_fnd0objectId");
						String sequence_no = bomline.getProperty("bl_sequence_no");
						rootBean.setBomLineUid(bomlineuid);
						rootBean.setQty("1");
						
						String match = "[0-9]{2,6}";
						Integer bl_sequence_no = null;
						if (!(sequence_no).matches(match)) {
							bl_sequence_no = 10;
						}else {
							bl_sequence_no = Integer.parseInt(sequence_no);
						}
						rootBean.setSequence_no(bl_sequence_no);
					} else {
						return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "添加的物料已不存在，請重新查詢後再執行添加！");
					}
				}

				bomwindow.refresh();
				roductLineItemRev.refresh();
			}

			System.out.println("addPackingBOM return = "+AjaxResult.success("执行成功", rootBeanList));
			
			return AjaxResult.success("执行成功", rootBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 产品线移除零件
	 * 
	 * @param data
	 * @return
	 * @throws TCException
	 */
	public AjaxResult removeMatrixParts(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {

			Gson gson = new Gson();
			List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
			}.getType());
			for (int k = 0; k < rootBeanList.size(); k++) {
				PAProductLineBOMBean rootBean = rootBeanList.get(k);

				String productLineItemUID = rootBean.getProductLineItemUID();
				TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);

				String item_id = rootBean.getItemId();
				String d9_BOMTemp = rootBean.getD9_BOMTemp();

				bomwindow = createBOMWindow(session);
				TCComponentBOMLine bomline = getTopBomline(bomwindow, productLineItemRev);
				AIFComponentContext[] childrens = bomline.getChildren();
				if (childrens.length > 0) {
					for (int i = 0; i < childrens.length; i++) {
						TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
						String bl_item_id = children.getProperty("bl_item_item_id");
						String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
						
						if (children.isPacked()) {
							children.refresh();
							TCComponentBOMLine[] packedLines = children.getPackedLines();
							children.unpack();

							for (int j = 0; j < packedLines.length; j++) {
								String packed_bl_item_id = packedLines[j].getProperty("bl_item_item_id");
								String packed_bl_occ_d9_BOMTemp = packedLines[j].getProperty("bl_occ_d9_BOMTemp");
								
								if (packed_bl_item_id.equals(item_id) && packed_bl_occ_d9_BOMTemp.equals(d9_BOMTemp)) {
									// 判断是否可移除
									if (iscut(productLineItemRev, item_id, d9_BOMTemp)) {
										packedLines[j].cut();
									} else {
										return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "移除失败，请先确定是否被产品使用！");
									}
								}
							}
						}

						if (bl_item_id.equals(item_id) && bl_occ_d9_BOMTemp.equals(d9_BOMTemp)) {
							// 判断是否可移除
							if (iscut(productLineItemRev, item_id, d9_BOMTemp)) {
								children.cut();
							} else {
								return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "移除失败，请先确定是否被产品使用！");
							}
						}
					}
				}
				productLineItemRev.refresh();
			}

			return AjaxResult.success("执行成功");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			if (bomwindow != null) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * PA 修改产品线零件
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult modifyPAMatrixParts(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {
			bomwindow = createBOMWindow(session);
			Gson gson = new Gson();
			PAProductLineBOMBean rootBean = gson.fromJson(data, PAProductLineBOMBean.class);
			String productLineItemUID = rootBean.getProductLineItemUID();
			TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(productLineItemUID);
			// String lineid = rootBean.getLineId();
			String itemId = rootBean.getItemId();
			String d9_BOMTemp = rootBean.getD9_BOMTemp();
			
			String remark = rootBean.getRemark();
			String qty = rootBean.getQty();
			String isNew = rootBean.getIsNew();

			String modifyKey = rootBean.getModifyKey();
			System.out.println("modifyKey = " + modifyKey);

			if ("remark".equals(modifyKey) || "qty".equals(modifyKey) || "bl_uom".equals(modifyKey)
					|| "isNew".equals(modifyKey)) {

				modifyPABOMParts(roductLineItemRev, itemId, d9_BOMTemp, rootBean, bomwindow);
				modifyPAVariable(roductLineItemRev, itemId, d9_BOMTemp, remark, qty, isNew);
			} else {
				modifyPABOMParts(roductLineItemRev, itemId, d9_BOMTemp, rootBean, bomwindow);
			}

			return AjaxResult.success("执行成功");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.toString());
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 升版（修订）
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult upgradedverMatrixParts(String data) {
		try {
			Gson gson = new Gson();
			PAProductLineBOMBean rootBean = gson.fromJson(data, PAProductLineBOMBean.class);
			
			String item_id = rootBean.getItemId();
			String ver = rootBean.getItemRevision();

			TCComponentItemRevision itemrevision = queryItemRev(item_id, ver);
			if (itemrevision != null) {
				// 是否判断发布 才可升版？
				boolean released = TCUtil.isReleased(itemrevision);
				if (released) {
					TCComponentItemRevision saveAsItermev = itemrevision.saveAs("");
					
					rootBean.setReleased(TCUtil.isReleased(saveAsItermev));
					boolean itemWrite = TCUtil.checkOwninguserisWrite(session, saveAsItermev);
					rootBean.setItemEnabled(itemWrite);
					rootBean.setItemRevUid(saveAsItermev.getUid());
					String item_revision_id = saveAsItermev.getProperty("item_revision_id");
					rootBean.setItemRevision(item_revision_id);
					
					String jsons = JSONArray.toJSONString(rootBean);
					System.out.print("json ==" + jsons);

					return AjaxResult.success("执行成功", rootBean);
					
				}

			}
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "升版失败！");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * 添加替代料
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult addSubMatrixParts(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {
			Gson gson = new Gson();
			List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
			}.getType());

			if (rootBeanList != null && rootBeanList.size() > 0) {
				String productLineItemUID = rootBeanList.get(0).getProductLineItemUID();
				TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);
				bomwindow = createBOMWindow(session);
				TCComponentBOMLine topbom = getTopBomline(bomwindow, roductLineItemRev);
				// 解包
				topbom.refresh();
				AIFComponentContext[] childrens_Packed = topbom.getChildren();
				for (AIFComponentContext aifchildren : childrens_Packed) {
					TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
					if (children.isPacked()) {
						children.unpack();
						children.refresh();
					}
				}

				for (int k = 0; k < rootBeanList.size(); k++) {
					PAProductLineBOMBean rootBean = rootBeanList.get(k);
					String bomlineuid = rootBean.getBomLineUid();
					System.out.println("bomlineuid = " + bomlineuid);
					String itemId = rootBean.getItemId();
					String itemRevision = rootBean.getItemRevision();

					topbom.refresh();
					AIFComponentContext[] childrens = topbom.getChildren();
					for (AIFComponentContext aifchildren : childrens) {
						TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
						String bl_itemid = children.getProperty("bl_item_item_id");
						String d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
						String lineIdString = CommonTools.md5Encode(bl_itemid+d9_BOMTemp);

						String childrenuid = children.getProperty("bl_occ_fnd0objectId");
						System.out.println("childrenuid = " + childrenuid);
						if (childrenuid.equals(bomlineuid)) {
							// 添加替代料
							TCComponentItemRevision queryItemRev = queryItemRev(itemId, itemRevision);
							if(TCUtil.isReleased(queryItemRev)) {
								queryItemRev = queryItemRev.saveAs("");
								rootBean.setItemRevision(queryItemRev.getProperty("item_revision_id"));
								rootBean.setItemRevUid(queryItemRev.getUid());
								rootBean.setReleased(TCUtil.isReleased(queryItemRev));
							}
							String sap_rev = rootBean.getSap_rev();
							if(sap_rev!=null && !"".equals(sap_rev)) {
								queryItemRev.setProperty("d9_SAPRev", sap_rev);
								rootBean.setsAPRev(sap_rev);
							}
							
							children.add(queryItemRev.getItem(), queryItemRev, null, true);
							bomwindow.save();

							addSubVariable(roductLineItemRev, lineIdString, queryItemRev);
							break;
						}
					}
				}
			}

			String jsons = JSONArray.toJSONString(rootBeanList);
			System.out.print("addSubMatrixParts return ==" + jsons);
			return AjaxResult.success("执行成功", rootBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * PA替換物料
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult replacePAParts(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {
			Gson gson = new Gson();
			PAProductLineBOMBean rootBean = gson.fromJson(data, PAProductLineBOMBean.class);

			String productLineItemUID = rootBean.getProductLineItemUID();
			TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(productLineItemUID);
			bomwindow = createBOMWindow(session);
			TCComponentBOMLine topbom = getTopBomline(bomwindow, roductLineItemRev);
			// 解包
			topbom.refresh();
			AIFComponentContext[] childrens_Packed = topbom.getChildren();
			for (AIFComponentContext aifchildren : childrens_Packed) {
				TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
				if (children.isPacked()) {
					children.unpack();
					children.refresh();
				}
			}

			String bomlineuid = rootBean.getBomLineUid();
			String itemId = rootBean.getItemId();
			String itemRevision = rootBean.getItemRevision();

			topbom.refresh();
			AIFComponentContext[] childrens = topbom.getChildren();
			for (AIFComponentContext aifchildren : childrens) {
				TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
				String childrenuid = children.getProperty("bl_occ_fnd0objectId");
				System.out.println("childrenuid = " + childrenuid);
				if (childrenuid.equals(bomlineuid)) {
					// 替換物料
					String bl_itemid = children.getProperty("bl_item_item_id");
					String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");

					//String lineIdStringTmp = CommonTools.md5Encode(itemId + bl_Category + bl_Plant);

					if (isExsitPart(childrens, "", itemId, roductLineItemRev)) {
						throw new Exception("物料：" + itemId + "已存在变体中,不能替換");
					}
					
					if (variablePAReleased(roductLineItemRev, bl_itemid, bl_occ_d9_BOMTemp)) {
						throw new Exception("物料：" + bl_itemid + "已存在冻结的变体中,不能替換");
					}

					TCComponentItemRevision queryItemRev = queryItemRev(itemId, itemRevision);
					if (queryItemRev == null) {
						throw new Exception("输入信息有误，未找到物料：" + itemId + "/" + itemRevision);
					}
					
					
					String[] bomName = new String[] { "bl_occ_d9_Remark", "bl_sequence_no","bl_quantity", "bl_occ_d9_IsNew" };
					String[] bomValue = children.getProperties(bomName);
					String bl_quantity = bomValue[2];
					
					TCComponentBOMLine[] subBomLines = children.listSubstitutes();

					// 替換掉
					children.cut();
					
					if(TCUtil.isReleased(queryItemRev)) {
						queryItemRev = queryItemRev.saveAs("");
						rootBean.setItemRevision(queryItemRev.getProperty("item_revision_id"));
						rootBean.setItemRevUid(queryItemRev.getUid());
						rootBean.setReleased(TCUtil.isReleased(queryItemRev));
					}
					String sap_rev = rootBean.getSap_rev();
					if(sap_rev!=null && !"".equals(sap_rev)) {
						queryItemRev.setProperty("d9_SAPRev", sap_rev);
						rootBean.setsAPRev(sap_rev);
					}
					
					TCComponentBOMLine childBomLine = topbom.add(queryItemRev.getItem(), queryItemRev, null, false);
					if (bl_quantity.contains(".")) {
						childBomLine.setProperty("bl_uom", "Other");
					} else {
						childBomLine.setProperty("bl_uom", "");
					}

					childBomLine.setProperties(bomName, bomValue);
					
					String uuid = UUID.randomUUID().toString();
					childBomLine.setProperty("bl_occ_d9_BOMTemp", uuid);
					
					rootBean.setD9_BOMTemp(uuid);
					rootBean.setLineId(CommonTools.md5Encode(itemId+uuid));

					for (TCComponentBOMLine sub : subBomLines) {
						childBomLine.add(sub.getItem(), sub.getItemRevision(), null, true);
					}

					bomwindow.save();

					replacePAVariable(roductLineItemRev, bl_itemid, bl_occ_d9_BOMTemp, uuid, queryItemRev, subBomLines);
					break;
				}
			}

			String jsons = JSONArray.toJSONString(rootBean);
			System.out.print("replacePAParts return ==" + jsons);
			return AjaxResult.success("执行成功", rootBean);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 移除替代料
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult cutSubMatrixParts(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {
			Gson gson = new Gson();
			List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
			}.getType());

			if (rootBeanList != null && rootBeanList.size() > 0) {
				String productLineItemUID = rootBeanList.get(0).getProductLineItemUID();
				TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);
				bomwindow = createBOMWindow(session);
				TCComponentBOMLine topbom = getTopBomline(bomwindow, roductLineItemRev);
				// 解包
				topbom.refresh();
				AIFComponentContext[] childrens_Packed = topbom.getChildren();
				for (AIFComponentContext aifchildren : childrens_Packed) {
					TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
					if (children.isPacked()) {
						children.unpack();
						children.refresh();
					}
				}

				for (int k = 0; k < rootBeanList.size(); k++) {
					PAProductLineBOMBean rootBean = rootBeanList.get(k);
					String bomlineuid = rootBean.getBomLineUid();
					System.out.println("bomlineuid = " + bomlineuid);
					String itemId = rootBean.getItemId();

					topbom.refresh();
					AIFComponentContext[] childrens = topbom.getChildren();
					for (AIFComponentContext aifchildren : childrens) {
						TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
						String bl_itemid = children.getProperty("bl_item_item_id");
						String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
						String bl_lineIdString = CommonTools.md5Encode(bl_itemid+bl_occ_d9_BOMTemp);

						if (children.hasSubstitutes()) {
							TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
							for (TCComponentBOMLine subBomline : listSubstitutes) {
								String childrenuidSub = subBomline.getProperty("bl_occ_fnd0objectId");
								String bl_item_item_id = subBomline.getProperty("bl_item_item_id");
								System.out.println("替代料bl_item_item_id = " + bl_item_item_id);
								if (childrenuidSub.equals(bomlineuid) && bl_item_item_id.equals(itemId)) {
									
									boolean cutSubVariable = cutSubVariable(roductLineItemRev, bl_lineIdString, bl_item_item_id);
									if(cutSubVariable) {
										subBomline.cut();
										bomwindow.save();
									} else {
										return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "移除替代料失败，请先确定是否被产品使用！");
									}
									break;
								}
							}
						}
					}
				}
			}

			String jsons = JSONArray.toJSONString(rootBeanList);
			System.out.print("json ==" + jsons);
			return AjaxResult.success("执行成功", rootBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	

	/**
	 * 创建BOMWindow
	 * 
	 * @param session
	 * @return
	 */
	public static TCComponentBOMWindow createBOMWindow(TCSession session) {
		TCComponentBOMWindow window = null;
		try {
			TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
					.getTypeComponent("RevisionRule");
			TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
			TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
					.getTypeComponent("BOMWindow");
			window = imancomponentbomwindowtype.create(imancomponentrevisionrule);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return window;
	}
	
	public static TCComponentBOMLine getTopBomline(TCComponentBOMWindow bomWindow, TCComponent com) {
		TCComponentBOMLine topBomline = null;
		try {
			if (bomWindow == null) {
				return topBomline;
			}
			TCComponentItemRevision rev = null;
			TCComponentItem item = null;
			if (com instanceof TCComponentItem) {
				item = (TCComponentItem) com;
				topBomline = bomWindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
			} else if (com instanceof TCComponentItemRevision) {
				rev = (TCComponentItemRevision) com;
				topBomline = bomWindow.setWindowTopLine(rev.getItem(), rev, null, null);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return topBomline;
	}

	public static TCComponentBOMLine addbomline(TCComponentBOMWindow newBOMWindow, TCComponentBOMLine topLine,
			TCComponentItemRevision itemrev ) throws TCException {
		AIFComponentContext[] childrens_pack = topLine.getChildren();
		int sequence_no = 0;

		topLine.refresh();
		// 先执行全部解包
		for (AIFComponentContext childrenAIF_pack : childrens_pack) {
			TCComponentBOMLine children_pack = (TCComponentBOMLine) childrenAIF_pack.getComponent();
			if (children_pack.isPacked()) {
				children_pack.unpack();
				children_pack.refresh();
			}
		}

		// 再执行查找编号计算
		topLine.refresh();
		AIFComponentContext[] childrens = topLine.getChildren();
		for (AIFComponentContext childrenAIF : childrens) {
			TCComponentBOMLine children = (TCComponentBOMLine) childrenAIF.getComponent();

			String bl_sequence_no = children.getProperty("bl_sequence_no");
			int parseInt = 0;
			try {
				parseInt = Integer.parseInt(bl_sequence_no);
			} catch (NumberFormatException e) {
				parseInt = 0;
			}

			if (parseInt > sequence_no) {
				sequence_no = parseInt;
			}
		}

		TCComponentBOMLine bomline = topLine.add(itemrev.getItem(), itemrev, null, false);

		if (sequence_no > 0) {
			sequence_no = sequence_no + 10;
			bomline.setProperty("bl_sequence_no", "" + sequence_no);
		}
		return bomline;
	}

	public TCComponentItemRevision queryItemRev(String item_id, String item_revision_id) throws Exception {
		TCComponentItemRevision itemrevision = null;
		TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...",
				new String[] { "items_tag.item_id", "item_revision_id" }, new String[] { item_id, item_revision_id });
		if (executeQuery != null && executeQuery.length > 0) {
			for (int i = 0; i < executeQuery.length; i++) {
				if (executeQuery[i] instanceof TCComponentItemRevision) {
					String type = executeQuery[i].getType();
					if("D9_L5_PartRevision".equals(type)) {
						continue;
					}
					itemrevision = (TCComponentItemRevision) executeQuery[i];
				}
			}
		}
		return itemrevision;
	}

	/**
	 * 判断是否可以移除 part
	 * 
	 * @param product
	 * @param item_id
	 * @param d9_Plant
	 * @param d9_Category
	 * @return
	 * @throws TCException
	 */
	public boolean iscut(TCComponentItemRevision product, String item_id, String d9_BOMTemp)
			throws TCException {
		boolean cut = true;
		TCComponentBOMWindow bomwindow = null;
		TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
		if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
			for (int i = 0; i < tccom_HasVariants.length; i++) {
				bomwindow = createBOMWindow(session);
				TCComponentBOMLine bomline = getTopBomline(bomwindow, tccom_HasVariants[i]);
				AIFComponentContext[] childrens = bomline.getChildren();
				if (childrens.length > 0) {
					for (int j = 0; j < childrens.length; j++) {
						TCComponentBOMLine children = (TCComponentBOMLine) childrens[j].getComponent();
						String bl_item_id = children.getProperty("bl_item_item_id");
						String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
						if (bl_item_id.equals(item_id) && bl_occ_d9_BOMTemp.equals(d9_BOMTemp)) {
							cut = false;
							break;
						}
					}
				}
			}
		}
		if (bomwindow != null) {
			bomwindow.close();
		}

		return cut;
	}
	
	public static TCComponentItem createItem(TCSession session, String itemId, String itemRev, String itemName,
			String itemTypeName) throws TCException {
		TCComponentItem item = null;
		TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
		if (itemId.equals("")) {
			itemId = itemType.getNewID();
		}
		if (itemRev == null || itemRev.equals("")) {
			itemRev = itemType.getNewRev(null);
		}
		item = itemType.create(itemId, itemRev, itemTypeName, itemName, "", null);
		return item;
	}
	
	/**
	 * 匹配id判断是否满足正则表达式
	 * 
	 * @param
	 * @return
	 */
	private static boolean getRightStr(String itemid) {
		String match = "^.{7,17}$";
		if (itemid.matches(match)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 检查替换的物料是否已经存在
	 * 
	 * @param childrens
	 * @param lineId
	 * @param itemId
	 * @param product
	 * @return
	 * @throws Exception
	 */
	private boolean isExsitPart(AIFComponentContext[] childrens, String lineId, String itemId,
			TCComponentItemRevision product) throws Exception {
		TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
		for (TCComponent component : tccom_HasVariants) {
			String item_id = component.getProperty("item_id");
			if (item_id.equalsIgnoreCase(itemId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * PA 修改Variable 物料对应变体属性
	 * 
	 * @param torqueOut
	 * @param torqueIn
	 * @param remark
	 */
	public void modifyPAVariable(TCComponentItemRevision product, String itemId, String d9_BOMTemp, String d9_Remark, String qty,
			String isNew) throws TCException {
		TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
		if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
			for (int i = 0; i < tccom_HasVariants.length; i++) {
				TCComponentBOMWindow bomwindow = createBOMWindow(session);
				TCComponentBOMLine bomline = getTopBomline(bomwindow, tccom_HasVariants[i]);
				bomline.refresh();
				AIFComponentContext[] childrens = bomline.getChildren();
				if (childrens.length > 0) {
					for (int j = 0; j < childrens.length; j++) {
						TCComponentBOMLine children = (TCComponentBOMLine) childrens[j].getComponent();
						if (children.isPacked()) {
							TCComponentBOMLine[] packedLines = children.getPackedLines();
							children.unpack();

							for (int k = 0; k < packedLines.length; ++k) {
								String bl_itemid = packedLines[k].getProperty("bl_item_item_id");
								String bl_occ_d9_BOMTemp = packedLines[k].getProperty("bl_occ_d9_BOMTemp");
								//String bl_lineIdString = CommonTools.md5Encode(bl_itemid+bl_occ_d9_BOMTemp);
								
								if (itemId.equals(bl_itemid) && d9_BOMTemp.equals(bl_occ_d9_BOMTemp)) {
									if (qty.contains(".")) {
										packedLines[k].setProperty("bl_uom", "Other");
									} else {
										packedLines[k].setProperty("bl_uom", "");
									}

									String[] bomName = new String[] { "bl_occ_d9_Remark", "bl_quantity", "bl_occ_d9_IsNew" };
									String[] bomValue = new String[] { d9_Remark, qty, isNew };
									packedLines[k].setProperties(bomName, bomValue);
									//packedLines[k].setProperty("bl_occ_d9_BOMTemp", d9_BOMTemp);
									
									bomwindow.save();
									break;
								}
							}
						}

						String bl_itemid = children.getProperty("bl_item_item_id");
						String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
						// String bl_lineIdString = CommonTools.md5Encode(bl_itemid+bl_occ_d9_BOMTemp);

						if (itemId.equals(bl_itemid) && d9_BOMTemp.equals(bl_occ_d9_BOMTemp)) {
							if (qty.contains(".")) {
								children.setProperty("bl_uom", "Other");
							} else {
								children.setProperty("bl_uom", "");
							}

							String[] bomName = new String[] { "bl_occ_d9_Remark", "bl_quantity", "bl_occ_d9_IsNew" };
							String[] bomValue = new String[] { d9_Remark, qty, isNew };
							children.setProperties(bomName, bomValue);

							bomwindow.save();
							break;
						}
					}
				}
				bomwindow.save();
				bomwindow.close();
			}
		}
	}
	
	/**
	 * PA 判断是否被发布的变体引用
	 * 
	 * @param product
	 * @param lineIdString
	 * @param addItemRev
	 * @param subBomLines
	 */
	public boolean variablePAReleased(TCComponentItemRevision product, String item_id, String d9_BOMTemp) {
		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
			if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
				for (int i = 0; i < tccom_HasVariants.length; i++) {
					// AIFComponentContext[] related = tccom_HasVariants[i].getRelated();
					if (TCUtil.isReleased(tccom_HasVariants[i])) {
						bomwindow = createBOMWindow(session);
						TCComponentBOMLine bomline = getTopBomline(bomwindow, tccom_HasVariants[i]);
						bomline.refresh();
						AIFComponentContext[] childrens_Packed = bomline.getChildren();
						for (AIFComponentContext aifchildren : childrens_Packed) {
							TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
							if (children.isPacked()) {
								children.unpack();
								children.refresh();
							}
						}

						bomline.refresh();
						AIFComponentContext[] childrens = bomline.getChildren();
						if (childrens.length > 0) {
							for (AIFComponentContext aifchildren : childrens) {
								TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();

								String bl_itemid = children.getProperty("bl_item_item_id");
								String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
								if (item_id.equals(bl_itemid) && d9_BOMTemp.equals(bl_occ_d9_BOMTemp)) {
									return true;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * PA 替換变体中对应的物料
	 * 
	 * @param product
	 * @param lineIdString
	 * @param addItemRev
	 */
	public void replacePAVariable(TCComponentItemRevision product, String item_id,String d9_BOMTemp, String uuid, 
			TCComponentItemRevision addItemRev, TCComponentBOMLine[] subBomLines) {
		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
			if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
				for (int i = 0; i < tccom_HasVariants.length; i++) {
					bomwindow = createBOMWindow(session);
					TCComponentBOMLine bomline = getTopBomline(bomwindow, tccom_HasVariants[i]);
					bomline.refresh();
					AIFComponentContext[] childrens_Packed = bomline.getChildren();
					for (AIFComponentContext aifchildren : childrens_Packed) {
						TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
						if (children.isPacked()) {
							children.unpack();
							children.refresh();
						}
					}

					bomline.refresh();
					AIFComponentContext[] childrens = bomline.getChildren();
					if (childrens.length > 0) {
						for (AIFComponentContext aifchildren : childrens) {
							TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();

							String bl_itemid = children.getProperty("bl_item_item_id");
							String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
							
							//String bl_Category = children.getProperty("bl_occ_d9_Category");
							//String bl_Plant = children.getProperty("bl_occ_d9_Plant");
							//String bl_lineIdString = CommonTools.md5Encode(bl_itemid + bl_Category + bl_Plant);

							if (item_id.equals(bl_itemid) && d9_BOMTemp.equals(bl_occ_d9_BOMTemp)) {

								String bl_remark = children.getProperty("bl_occ_d9_Remark");
								String bl_quantity = children.getProperty("bl_quantity");

								children.cut();
								TCComponentBOMLine childBomLine = bomline.add(addItemRev.getItem(), addItemRev, null,
										false);
								if (bl_quantity.contains(".")) {
									childBomLine.setProperty("bl_uom", "Other");
								} else {
									childBomLine.setProperty("bl_uom", "");
								}
								childBomLine.setProperty("bl_quantity", "" + bl_quantity);// 修改
								childBomLine.setProperty("bl_occ_d9_Remark", "" + bl_remark.trim());// 修改
								childBomLine.setProperty("bl_occ_d9_BOMTemp", uuid);
								
								for (TCComponentBOMLine sub : subBomLines) {
									childBomLine.add(sub.getItem(), sub.getItemRevision(), null, true);
								}

								bomwindow.save();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	/**
	 * PA 修改物料或替代件 结构属性
	 * 
	 * @throws TCException
	 * 
	 */
	public void modifyPABOMParts(TCComponent roductLineItemRev, String itemId,String d9_BOMTemp, PAProductLineBOMBean rootBean,
			TCComponentBOMWindow bomwindow) throws TCException {
		TCComponentItemRevision itemrev = null;

		TCComponentBOMLine bomline = getTopBomline(bomwindow, roductLineItemRev);
		bomline.refresh();
		AIFComponentContext[] childrens = bomline.getChildren();

		if (childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				if (children.isPacked()) {
					TCComponentBOMLine[] packedLines = children.getPackedLines();
					children.unpack();

					for (int j = 0; j < packedLines.length; j++) {
						if (!rootBean.isSub()) {
							String packed_bl_item_id = packedLines[j].getProperty("bl_item_item_id");
							String packed_bl_occ_d9_BOMTemp = packedLines[j].getProperty("bl_occ_d9_BOMTemp");
							// String packed_lineid = CommonTools.md5Encode(packed_bl_item_id+packed_bl_occ_d9_BOMTemp);

							if (packed_bl_item_id.equals(itemId) && packed_bl_occ_d9_BOMTemp.equals(d9_BOMTemp) ) {
								// 修改结构属性
								String getQty = rootBean.getQty();
								if (getQty.contains(".")) {
									packedLines[j].setProperty("bl_uom", "Other");
								} else {
									packedLines[j].setProperty("bl_uom", "");
								}
								
								PAProductLineBOMBean.modifyPABom(session, packedLines[j], rootBean);
								
								itemrev = packedLines[j].getItemRevision();
								PAProductLineBOMBean.modifyPAItem(session, itemrev, rootBean);
								roductLineItemRev.refresh();
								return;
							}
						} else {
							if (packedLines[j].hasSubstitutes()) {
								TCComponentBOMLine[] listSubstitutes = packedLines[j].listSubstitutes();
								for (TCComponentBOMLine subBomline : listSubstitutes) {
									String sub_bl_item_id = subBomline.getProperty("bl_item_item_id");
									
									if (sub_bl_item_id.equals(itemId)) {
										itemrev = subBomline.getItemRevision();
										PAProductLineBOMBean.modifyPAItem(session, itemrev, rootBean);
										bomwindow.close();
										roductLineItemRev.refresh();
										return;
									}
								}
							}
						}
					}
				}
				if (!rootBean.isSub()) {
					String bl_item_id = children.getProperty("bl_item_item_id");
					String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
					// String bl_lineid = CommonTools.md5Encode(bl_item_id+bl_occ_d9_BOMTemp);

					if (bl_item_id.equals(itemId) && bl_occ_d9_BOMTemp.equals(d9_BOMTemp)) {
						// 修改结构属性
						String getQty = rootBean.getQty();
						if (getQty.contains(".")) {
							children.setProperty("bl_uom", "Other");
						} else {
							children.setProperty("bl_uom", "");
						}
						
						PAProductLineBOMBean.modifyPABom(session, children, rootBean);
						itemrev = children.getItemRevision();
						PAProductLineBOMBean.modifyPAItem(session, itemrev, rootBean);
						
						roductLineItemRev.refresh();
						return;
					}
				} else {
					if (children.hasSubstitutes()) {
						TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
						for (TCComponentBOMLine subBomline : listSubstitutes) {
							String sub_bl_item_id = subBomline.getProperty("bl_item_item_id");
							// String sub_lineid = CommonTools.md5Encode(sub_bl_item_id);
							if (sub_bl_item_id.equals(itemId)) {
								itemrev = subBomline.getItemRevision();
								PAProductLineBOMBean.modifyPAItem(session, itemrev, rootBean);
								roductLineItemRev.refresh();
								return;
							}
						}
					}
				}
			}
		}
		roductLineItemRev.refresh();
		return;
	}

	
	/**
	 * 添加变体中对应的替代料
	 * 
	 * @param product
	 * @param lineIdString
	 * @param addItemRev
	 */
	public void addSubVariable(TCComponentItemRevision product, String lineIdString,
			TCComponentItemRevision addItemRev) {
		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
			if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
				for (int i = 0; i < tccom_HasVariants.length; i++) {
					bomwindow = createBOMWindow(session);
					TCComponentBOMLine bomline = getTopBomline(bomwindow, tccom_HasVariants[i]);
					bomline.refresh();
					AIFComponentContext[] childrens_Packed = bomline.getChildren();
					for (AIFComponentContext aifchildren : childrens_Packed) {
						TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
						if (children.isPacked()) {
							children.unpack();
							children.refresh();
						}
					}

					bomline.refresh();
					AIFComponentContext[] childrens = bomline.getChildren();
					if (childrens.length > 0) {
						for (AIFComponentContext aifchildren : childrens) {
							TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();

							String bl_itemid = children.getProperty("bl_item_item_id");
							String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
							String bl_lineIdString = CommonTools.md5Encode(bl_itemid+bl_occ_d9_BOMTemp);

							if (lineIdString.equals(bl_lineIdString)) {

								children.add(addItemRev.getItem(), addItemRev, null, true);
								bomwindow.save();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	/**
	 * 移除变体中对应的替代料
	 * 
	 * @param product
	 * @param lineIdString
	 * @param cutItemId
	 */
	public boolean cutSubVariable(TCComponentItemRevision product, String lineIdString, String cutItemId) {
	//public void cutSubVariable(TCComponentItemRevision product, String lineIdString, String cutItemId) {
		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponent[] tccom_HasVariants = product.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
			if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
				for (int i = 0; i < tccom_HasVariants.length; i++) {
					bomwindow = createBOMWindow(session);
					TCComponentBOMLine bomline = getTopBomline(bomwindow, tccom_HasVariants[i]);
					bomline.refresh();
					AIFComponentContext[] childrens_Packed = bomline.getChildren();
					for (AIFComponentContext aifchildren : childrens_Packed) {
						TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
						if (children.isPacked()) {
							children.unpack();
							children.refresh();
						}
					}

					bomline.refresh();
					AIFComponentContext[] childrens = bomline.getChildren();
					if (childrens.length > 0) {
						for (AIFComponentContext aifchildren : childrens) {
							TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();

							String bl_itemid = children.getProperty("bl_item_item_id");
							String bl_occ_d9_BOMTemp = children.getProperty("bl_occ_d9_BOMTemp");
							String bl_lineIdString = CommonTools.md5Encode(bl_itemid+bl_occ_d9_BOMTemp);

							if (lineIdString.equals(bl_lineIdString)) {
								if (children.hasSubstitutes()) {
									TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
									for (TCComponentBOMLine subBomline : listSubstitutes) {
										// String childrenuidSub = subBomline.getProperty("bl_occ_fnd0objectId");
										String bl_item_item_id = subBomline.getProperty("bl_item_item_id");
										
										System.out.println("替代料bl_item_item_id = " + bl_item_item_id);

										if (bl_item_item_id.equals(cutItemId)) {
//											subBomline.cut();
//											bomwindow.save();
//											break;
											
											return false;
										}
									}
								}
							}

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	
	
	
	/**
	 * 获取 变更前发布版本
	 * 
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	public static TCComponentItemRevision getImpactedRevision(TCComponentItemRevision itemRevision) throws TCException {
		try {
			String myVer = itemRevision.getProperty("item_revision_id");
			TCComponent[] revions = itemRevision.getItem().getRelatedComponents("revision_list");
			if (revions.length == 1) {
				return null;
			}

			for (int i = 0; i < revions.length; i++) {
				itemRevision = (TCComponentItemRevision) revions[i];
				String version = itemRevision.getProperty("item_revision_id");
				if (myVer.equals(version)) {
					TCComponentItemRevision impactedRev = (TCComponentItemRevision) revions[i - 1];
					if (TCUtil.isReleased(impactedRev)) {
						return impactedRev;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 获取 变更Solution
	 * 
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	public void addSolutionList(List<TCComponentItemRevision> solutionList, TCComponentItemRevision solution) {
		for (TCComponentItemRevision item : solutionList) {
			String uid = item.getUid();
			String solutionUid = solution.getUid();
			if (uid.equals(solutionUid)) {
				return;
			}
		}
		solutionList.add(solution);
	}
	
	
	
	
	
	/**
	 * 获取最新冻结版本
	 * 
	 * @param item
	 * @return
	 * @throws TCException
	 */
	public TCComponentItemRevision get_Latest_Released_ItemRev(TCComponentItem item) throws TCException {

		TCComponentItemRevision[] itemRev = null;
		TCComponentItemRevision itemRev_single = null;
		try {
			itemRev = item.getReleasedItemRevisions();
			if (itemRev.length >= 1) {
				for (int i = 0; i < itemRev.length; i++) {
					if (!itemRev[i].getProperty("item_revision_id").contains(".")) {
						itemRev_single = itemRev[i];
						break;
					}
				}
				if (itemRev_single != null) {
					Date released_date = itemRev_single.getDateProperty("date_released");
					for (int i = 0; i < itemRev.length; i++) {
						if (itemRev[i].getProperty("item_revision_id").contains(".")) {
							continue;
						}
						Date released_date_another = itemRev[i].getDateProperty("date_released");
						if (released_date_another.after(released_date)) {
							released_date = released_date_another;
							itemRev_single = itemRev[i];
						}
					}
				}

			} else {
				itemRev_single = null;
			}
		} catch (TCException e1) {

			e1.printStackTrace();
		} catch (Throwable e2) {

			e2.printStackTrace();
		}
		return itemRev_single;
	}
	
	
	/**
	 * 指派项目
	 * 
	 * @param folder
	 * @param project
	 * @throws Exception
	 */
	public static void assignedProject(TCSession session, ModelObject folder, ModelObject[] project) throws Exception {
		AssignedOrRemovedObjects assignedOrRemovedObjects = new AssignedOrRemovedObjects();
		assignedOrRemovedObjects.objectToAssign = new ModelObject[] { folder };
		// assignedOrRemovedObjects.objectToRemove = null;
		assignedOrRemovedObjects.projects = project;
		AssignedOrRemovedObjects[] aassignedorremovedobjects = new AssignedOrRemovedObjects[1];
		aassignedorremovedobjects[0] = assignedOrRemovedObjects;
		ProjectLevelSecurityService projectLevelSecurityService = ProjectLevelSecurityService
				.getService(session.getSoaConnection());
		ServiceData serviceData = projectLevelSecurityService.assignOrRemoveObjects(aassignedorremovedobjects);
		if (serviceData.sizeOfPartialErrors() > 0) {
			throw new Exception(serviceData.getPartialError(0).toString());
		}
	}
	
	/**
	 * 创建DCN变更
	 * 
	 * @param itemids
	 * @return
	 */
	public AjaxResult createDCN(String itemRevUID) {
		TCComponentBOMWindow bomwindow = null;
		String object_string = "";
		try {
			List<TCComponentItemRevision> impactedList = new ArrayList<>();
			List<TCComponentItemRevision> solutionList = new ArrayList<>();
			List<TCComponentItemRevision> solutionList_2 = new ArrayList<>();
			TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(itemRevUID);
			if (roductLineItemRev == null)
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "未找到Matrix对象！");

			// 系列变更
			TCComponent[] tccom_HasVariants = roductLineItemRev
					.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTS_REL);
			if (tccom_HasVariants != null && tccom_HasVariants.length > 0) {
				for (int i = 0; i < tccom_HasVariants.length; i++) {
					if (tccom_HasVariants[i] instanceof TCComponentItemRevision) {
						TCComponentItemRevision itemRevision = (TCComponentItemRevision) tccom_HasVariants[i];
						if (!TCUtil.isReleased(itemRevision)) {
							TCComponentItemRevision impactedRevision = getImpactedRevision(itemRevision);
							if (impactedRevision != null) {
								addSolutionList(impactedList, impactedRevision);
								addSolutionList(solutionList_2, itemRevision);
								addSolutionList(solutionList, itemRevision);
							} else {
								addSolutionList(solutionList_2, itemRevision);
								addSolutionList(solutionList, itemRevision);
							}
						}
					}
				}
			}

			// 物料
			bomwindow = createBOMWindow(session);
			for (TCComponentItemRevision revision : solutionList) {
				TCComponentBOMLine topbom = getTopBomline(bomwindow, revision);
				AIFComponentContext[] childrens = topbom.getChildren();
				if (childrens.length > 0) {
					for (int i = 0; i < childrens.length; i++) {
						TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
						TCComponentItemRevision itemRevision = children.getItemRevision();
						if (!TCUtil.isReleased(itemRevision)) {
							TCComponentItemRevision impactedRevision = getImpactedRevision(itemRevision);
							if (impactedRevision != null) {
								addSolutionList(impactedList, impactedRevision);
								addSolutionList(solutionList_2, itemRevision);

							} else {
								addSolutionList(solutionList_2, itemRevision);
							}
						}
					}
				}
			}

			// 获取组织结构
			TCComponentGroup group = session.getGroup();
			String groupName = group.getFullName();
			System.out.println("groupName = " + groupName);

			String itemTypeName = "D9_DT_DCN";
//			D9_ChangeList	D9_PRT_DCN	D9_DT_DCN 	D9_MNT_DCN
			if (groupName.contains("Printer")) {
				itemTypeName = "D9_PRT_DCN";
			} else if (groupName.contains("Monitor")) {
				itemTypeName = "D9_MNT_DCN";
			} else if (groupName.contains("Desktop")) {
				itemTypeName = "D9_DT_DCN";
			}

			String itemid = "";
			String itemRev = "";
			String itemName = roductLineItemRev.getProperty("object_name");
			if (solutionList_2 == null || solutionList_2.size() == 0) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "当前Matrix对象，未满足创建DCN要求！");
			} else {
				for (TCComponentItemRevision solution_2 : solutionList_2) {
					AIFComponentContext[] whereReferenceds = solution_2.whereReferenced();
					String impacted_str = solution_2.getProperty("object_string");
					if (whereReferenceds != null && whereReferenceds.length > 0) {
						for (AIFComponentContext whereReferenced : whereReferenceds) {
							InterfaceAIFComponent component = whereReferenced.getComponent();
							if (component instanceof TCComponentItemRevision) {
								TCComponentItemRevision dcnRevision = (TCComponentItemRevision) component;
								String propertyType = dcnRevision.getType();
								String dcn_string = dcnRevision.getProperty("object_string");
//								D9_ChangeList	D9_PRT_DCN	D9_DT_DCN 	D9_MNT_DCN
								if (propertyType.equals("D9_ChangeListRevision")
										|| propertyType.equals("D9_PRT_DCNRevision")
										|| propertyType.equals("D9_DT_DCNRevision")
										|| propertyType.equals("D9_MNT_DCNRevision")) {

									TCComponent[] solutionItems = dcnRevision.getRelatedComponents("CMHasSolutionItem");
									for (TCComponent solutionItem : solutionItems) {
										if (solutionItem instanceof TCComponentItemRevision) {
											TCComponentItemRevision solution_1 = (TCComponentItemRevision) solutionItem;
											if (solution_2 == solution_1) {
												return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "当前对象:"
														+ impacted_str + "已存在:" + dcn_string + "解决方案项中,不能重复创建DCN!");

											}
										}
									}
								}
							}
						}
					}
				}
			}
			TCComponentItem newItem = createItem(session, itemid, itemRev, itemName, itemTypeName);
			if (newItem == null)
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "创建DCN失败！");

			// 指派专案
			TCComponent[] project = roductLineItemRev.getItem().getReferenceListProperty("project_list");
			if (CommonTools.isNotEmpty(project)) {
				assignedProject(session, newItem, project);
			}
			// 更新实际用户
			String d9_ActualUserID_dcn = roductLineItemRev.getProperty("d9_ActualUserID");
			newItem.getLatestItemRevision().setProperty("d9_ActualUserID", d9_ActualUserID_dcn);

			// 生成Matrix报告
			String exportBOMExcel = PAMatrixBOMExportService.exportBOMExcel(itemRevUID);

			// 将生成的Matrix报告挂在DCN的变更前后
			TCComponentItem document = null;
			TCComponent[] relatedComponents = roductLineItemRev.getRelatedComponents("IMAN_specification");
			if (CommonTools.isNotEmpty(relatedComponents)) {
				for (int i = 0; i < relatedComponents.length; i++) {
					if (relatedComponents[i] instanceof TCComponentItemRevision) {
						TCComponentItemRevision documentRev = (TCComponentItemRevision) relatedComponents[i];
						String property = documentRev.getProperty("object_name");
						if (property.equals(itemName)) {
							document = documentRev.getItem();
							break;
						}
					}
				}
			}
			if (document != null) {
				TCComponentItemRevision latestItemRevision = document.getLatestItemRevision();
				solutionList_2.add(latestItemRevision);
				TCComponentItemRevision get_Latest_Released_ItemRev = get_Latest_Released_ItemRev(document);
				// TCComponentItemRevision[] releasedItemRevisions =
				// document.getReleasedItemRevisions();
				if (CommonTools.isNotEmpty(get_Latest_Released_ItemRev)) {
					impactedList.add(get_Latest_Released_ItemRev);
				}
			}

			TCComponentFolder homeFolder = session.getUser().getHomeFolder();
			homeFolder.add("contents", newItem);
			object_string = newItem.getProperty("object_string");
			System.out.println("object_string = " + object_string);

			TCComponentItemRevision itemRevision = newItem.getLatestItemRevision();
			TCComponentItemRevision[] impactedArray = impactedList.toArray(new TCComponentItemRevision[] {});
			TCComponentItemRevision[] solutionArray = solutionList_2.toArray(new TCComponentItemRevision[] {});
			itemRevision.setRelated("CMHasImpactedItem", impactedArray);
			itemRevision.setRelated("CMHasSolutionItem", solutionArray);

			if (exportBOMExcel != null && !"".equals(exportBOMExcel)) {
				String createDCNexportDCNExcel = PAMatrixBOMExportService.createDCNexportDCNExcel(itemRevision,
						exportBOMExcel);
				System.out.println("createDCNexportDCNExcel = " + createDCNexportDCNExcel);

				String matrixVer = roductLineItemRev.getProperty("item_revision_id");

				PAMatrixBOMExportService.generateDocument(session, roductLineItemRev, createDCNexportDCNExcel, matrixVer);
			}

		} catch (TCException e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			TCUtil.closeBypass(TCUtil.getTCSession());
			try {
				bomwindow.close();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return AjaxResult.success("执行成功", object_string + "，DCN创建成功，请在Home下查看！");
	}
	
	/**
	 * 创建DCN变更
	 * 
	 * @param itemids
	 * @return
	 */
	public AjaxResult createDCNForMNT(String itemRevUID) {
		try {
			TCUtil.setBypass(TCUtil.getTCSession());
			List<TCComponentItemRevision> solutionList = new ArrayList<>();
			List<TCComponentItemRevision> impactList = new ArrayList<>();
			TCComponentItemRevision matrixItemRevision = TCUtil.findItemRevistion(itemRevUID);
			TCComponent[] variants = matrixItemRevision.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTHOLDER_REL);
			if(variants.length == 0 || !(variants[0] instanceof TCComponentItemRevision)) {
				return AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"没有找到VariantHolder");			
			}
			TCComponentItemRevision variantsHoder = (TCComponentItemRevision)variants[0] ;
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine rootLine = TCUtil.getTopBomline(bomWindow, variantsHoder);
			
			DCNController.getItemRevisionList(rootLine, solutionList, impactList);
			if (solutionList.size() == 0.) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR,"沒有變更，無法創建DCN");
			}
			
			String rootItem = rootLine.getItemRevision().getProperty("item_id");
			System.out.println(rootItem);
			boolean isRootExsitChange = solutionList.stream().filter(e->{
				try {
					System.out.println(e.getProperty("item_id"));
					return e.getProperty("item_id").equals(rootItem);
				} catch (TCException e1) {
					e1.printStackTrace();
				}
				return false;
			}).findAny().isPresent();
			
			boolean firsRev = DCNController.isFirstRev(rootLine) && isRootExsitChange;
			TCComponentItemRevision itemRevision = rootLine.getItemRevision();
			
			itemRevision.refresh();
			AIFComponentContext[] whereReferenced = itemRevision.whereReferenced();
			for (AIFComponentContext aifComponentContext : whereReferenced) {
				InterfaceAIFComponent component = aifComponentContext.getComponent();
				if (component instanceof TCComponentItemRevision) {
					TCComponentItemRevision revision = (TCComponentItemRevision) component;
					revision = revision.getItem().getLatestItemRevision();
					Type typeObject = revision.getTypeObject();
					String className = typeObject.getClassName();
					if (("D9_MNT_DCNRevision").equals(className)) {
						String property2 = revision.getProperty("sequence_id");
						System.out.println(property2);
						if (!TCUtil.isReleased(revision)) {
							String property = revision.getProperty("object_string");
							return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"請先發行  " + property);
						}
					}
				}
			}

			String tcObjTypeString = "D9_MNT_DCN";
			TCUtil.setBypass(TCUtil.getTCSession());
			Map<String, Object> revisionMap = new HashMap<String, Object>();
			String pcaNum = matrixItemRevision.getProperty("object_name");
			String name = pcaNum + (firsRev ? "初版發行" : "變更發行");
			revisionMap.put("object_name", name);
			revisionMap.put("object_desc", name);
			revisionMap.put("CreateInput", "D9_MNT_DCNCreI");
			TCComponentItem dcnItem = (TCComponentItem) TCUtil.createCom(session, tcObjTypeString, "",
						revisionMap);
			String dcnNo = dcnItem.getProperty("item_id");
			TCComponentItemRevision dcnItemRevision = DCNService.getFirstRevision(dcnItem);
			String errorLog = "";
			for (TCComponentItemRevision echoItemRevision : solutionList) {
				try {
					dcnItemRevision.add("CMHasSolutionItem", echoItemRevision);
				} catch (Exception e) {
					String msg = "";
					String dcn = DCNController.findDCN(echoItemRevision);
					if(dcn==null) {
						msg = e.getMessage();
					}else {
						String item = echoItemRevision.getProperty("object_string");
						msg = item +"在"+dcn+"的解决方案项中，该物件处于未发布状态，请先发行";
					}
					errorLog += msg + "\n";
					DCNController.removeImpact(impactList,echoItemRevision);
				}
			}
			for (TCComponentItemRevision echoItemRevision : impactList) {
				try {
					dcnItemRevision.add("CMHasProblemItem", echoItemRevision);
				} catch (Exception e) {
					e.printStackTrace();
					return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,echoItemRevision.getProperty("object_string") + "无法添加到问题项，它可能与其他DCN冲突。");
				}
			}
			
			if(!errorLog.isEmpty()) {
				File tempFile = FileUtil.createTempFile();
				FileUtil.writeString(errorLog, tempFile,StandardCharsets.UTF_8);
				String absolutePath = tempFile.getAbsolutePath();
				System.out.println(absolutePath);
				TCComponentDataset createDataSet = TCUtil.createDataSet(TCUtil.getTCSession(), absolutePath, "Text", "ErrorLog.txt", "Text");				
				dcnItemRevision.add("IMAN_specification", createDataSet);
			}
					
			TCComponentFolder dcnFolder = session.getUser().getHomeFolder();
			String folderName = "";
			if (dcnFolder != null) {
				folderName = dcnFolder.getProperty("object_name");
				dcnFolder.add("contents", dcnItem);
				dcnFolder.refresh();
			} else {
				TCComponentFolder newStuffFolder = TCUtil.getTCSession().getUser().getNewStuffFolder();
				folderName = newStuffFolder.getProperty("object_name");
				newStuffFolder.add("contents", dcnItem);
				newStuffFolder.refresh();
			}
			return AjaxResult.success("( " + dcnNo + "|" + name + " )已保存到" + folderName + "文件夹下");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(TCUtil.getTCSession());
		}
		return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"服務器繁忙，請稍後重試");
	}
	
	
	/**
	 * 在 sap中查询物料信息
	 * 
	 * @param url
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public PAProductLineBOMBean getSapBean(String url, String itemId, PAProductLineBOMBean rootBean) throws Exception {
		List<HHPNPojo> hhpns = new ArrayList<>();
		HHPNPojo hhpnPojo_sap = new HHPNPojo();
		hhpnPojo_sap.setDataFrom("SAP");
		hhpnPojo_sap.setIsExistInTC(0);
		hhpnPojo_sap.setItemId(itemId);
		hhpnPojo_sap.setPlant("CHMB");
		hhpns.add(hhpnPojo_sap);
		String jsons = JSONArray.toJSONString(hhpns);
		//System.out.println("sap = " + jsons);
		String rs = HttpUtil.post(url, jsons);

		if (rs != null && !"".equalsIgnoreCase(rs)) {
			//System.out.println("rs_sap = " + rs);
			List<HHPNPojo> rsTmps = null;
			try {
				rsTmps = JSON.parseArray(rs, HHPNPojo.class);
			} catch (Exception e) {
				throw new Exception("SAP 返回参数错误！");
			}

			if (rsTmps != null && rsTmps.size() > 0) {
				HHPNPojo hhpnpojo = rsTmps.get(0);
				String descr = hhpnpojo.getDescr();
				if (descr != null && !"".equals(descr)) {
					
					String rev = hhpnpojo.getRev();
					if (rev == null)
						rev = "";
					rootBean.setSap_rev(rev);
					
					return rootBean;
				}
			}
		}

		return rootBean;
	}

}
