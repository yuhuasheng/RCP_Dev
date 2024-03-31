package com.foxconn.electronics.pamatrixbom.controller;

import java.util.Map;

import com.foxconn.electronics.pamatrixbom.service.PAVariablesBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;


//@RequestMapping("/variables")
@RequestMapping("/pavariables")
public class PAVariablesBOMController {

	private PAVariablesBOMService variablesBOMService;
	private AbstractAIFUIApplication app;
	
	public PAVariablesBOMController(AbstractAIFUIApplication app) {
		variablesBOMService = new PAVariablesBOMService(app);
		this.app = app;
	}
	
	/**
	 * 查询变体
	 * @param request
	 * @return
	 * @throws TCException
	 */
	@GetMapping("/search")
	public AjaxResult search(HttpRequest request) throws TCException {
		String key = request.get("key");
		String englishDescription = request.get("englishDescription");
		System.out.println("==>> search: key=" + key+",englishDescription = "+englishDescription);
		
		return variablesBOMService.search(key,englishDescription);
	}
	
	/**
	 * 挂载 变体
	 * @param request
	 * @return
	 * @throws TCException
	 */
	@PostMapping("/bind")
	public AjaxResult bind(HttpRequest request) throws TCException {
		String data = request.getBody();
		System.out.println("==>> bind: " + data);
		return variablesBOMService.bind(data);
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
	
	
	
	/**
	 * PA
	 * 修改变体属性
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/modifyVariablesItem")
	public AjaxResult modifyVariablesItem(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> modifyVariables: " + data);
		
		try {
			Gson gson = new Gson();
			Map<String,String> map = gson.fromJson(data, Map.class);

			String uid = map.get("uid");
			String d9_EnglishDescription = map.get("d9_EnglishDescription");
			String d9_ShippingArea = map.get("d9_ShippingArea");
			
			TCComponentItemRevision variableUidItemRevision = (TCComponentItemRevision) TCUtil.loadObjectByUid(uid);
			variableUidItemRevision.setProperty("d9_EnglishDescription", d9_EnglishDescription);
			variableUidItemRevision.setProperty("d9_ShippingArea", d9_ShippingArea);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		}
		
		return AjaxResult.success("执行成功");
	}
	

}
