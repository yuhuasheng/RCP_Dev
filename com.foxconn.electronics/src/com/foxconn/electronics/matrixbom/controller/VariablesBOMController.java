package com.foxconn.electronics.matrixbom.controller;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.service.VariablesBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

//001375
@RequestMapping("/variables")
public class VariablesBOMController {

	private VariablesBOMService variablesBOMService;
	private AbstractAIFUIApplication app;
	
	public VariablesBOMController(AbstractAIFUIApplication app) {
		variablesBOMService = new VariablesBOMService(app);
		this.app = app;
	}
	@PostMapping("/add")
	public AjaxResult add(HttpRequest request) throws TCException {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return variablesBOMService.add(data);
	
	}
	@PostMapping("/bind")
	public AjaxResult bind(HttpRequest request) throws TCException {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return variablesBOMService.bind(data);
	}
	
	@GetMapping("/setQty")
	public AjaxResult setQty(HttpRequest request) throws TCException {
		System.out.println("setQty = "+request.getBody());
		try {
			VariableBOMBean variable = JSONObject.parseObject(request.getBody(),VariableBOMBean.class);
			String variableUid = variable.getBomLineUid();
			String partUid = variable.getItemRevUid();
			Integer qty = Integer.parseInt(variable.getQty());
			TCUtil.setBypass((TCSession) app.getSession());
			TCComponentItemRevision variableItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(variableUid);
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow((TCSession) app.getSession());
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
			TCUtil.closeBypass((TCSession) app.getSession());
		}
		
	}
	
	@GetMapping("/remove")
	public AjaxResult remove(HttpRequest request) throws TCException {
		try {
			String matrixUid = request.get("matrixUid");
			String variableUid = request.get("variableUid");
			TCUtil.setBypass((TCSession) app.getSession());
			TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(matrixUid);
			TCComponentItemRevision variableItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(variableUid);
			
			AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
			TCComponentItem hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
			
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow((TCSession) app.getSession());
			TCComponentBOMLine topBOMLine = TCUtil.getTopBomline(bomWindow, hoderItem.getLatestItemRevision());
			AIFComponentContext[] children = topBOMLine.getChildren();
			TCComponentBOMLine target = null;
			for (int i = 0; i < children.length; i++) {		
				TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
				String uid = child.getItemRevision().getUid();
				System.out.println(uid);
				if(variableUid.equals(uid)) {
					target = child;
					break;
				}
			}
			if(target != null) {
				// 移除
				target.cut();
			}
			bomWindow.save();
			bomWindow.close();
			
			matrixItemRevision.remove("D9_HasVariants_REL", variableItemRevision);
			return AjaxResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}finally {
			TCUtil.closeBypass((TCSession) app.getSession());
		}
	}
	
	@GetMapping("/upgrade")
	public AjaxResult upgrade(HttpRequest request) throws TCException {
		try {
			String variableUid = request.get("variableUid");
			String matrixUid = request.get("matrixUid");
			TCComponentItemRevision variableUidItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(variableUid);
			TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(matrixUid);
			TCComponentItemRevision doRevise = TCUtil.doRevise(variableUidItemRevision);
			// 移除
			matrixItemRevision.remove("D9_HasVariants_REL", variableUidItemRevision);
			// 挂载
			matrixItemRevision.add("D9_HasVariants_REL",doRevise);
			return AjaxResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}finally {
			TCUtil.closeBypass((TCSession) app.getSession());
		}
		
	}
	
	@GetMapping("/search")
	public AjaxResult search(HttpRequest request) throws TCException {
		try {
			String key = request.get("key");
			TCComponent[] executeQuery = TCUtil.executeQuery((TCSession) app.getSession(), "零组件版本...", new String[]{"items_tag.item_id"}, new String[]{""+key+""});
			List<JSONObject> list = new ArrayList<JSONObject>();
			for (int i = 0; i < executeQuery.length; i++) {
				String type = executeQuery[i].getType();
				if("D9_L5_PartRevision".equals(type)) {
					continue;
				}
				
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) executeQuery[i];
				String uid = itemRevision.getUid();
				String itemId = itemRevision.getProperty("item_id");
				String name = itemRevision.getProperty("object_name");
				String rev = itemRevision.getProperty("item_revision_id");
				String desc = itemRevision.getProperty("object_desc");
				JSONObject e = new JSONObject();
				e.put("uid", uid);
				e.put("id", itemId);
				e.put("name", name);
				e.put("rev", rev);
				e.put("desc", desc);
				list.add(e);
			}
			return AjaxResult.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}
	}
	

}
