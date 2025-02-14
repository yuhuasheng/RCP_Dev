package com.foxconn.electronics.L10Ebom.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.foxconn.electronics.L10Ebom.domain.L10EBOMBean;
import com.foxconn.electronics.L10Ebom.service.MntL10EbomService;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

@RequestMapping("/mntL10Ebom")
public class MntL10EBomController {
	private MntL10EbomService service;
	public TCSession session = null;
	public ResourceBundle reg;
	public TCComponentItemRevision revision;
	
	public MntL10EbomService getService() {
		return service;
	}

	public MntL10EBomController(TCComponentItemRevision revision, TCSession session, ResourceBundle reg2) {
		this.revision = revision;
		this.service = new MntL10EbomService(revision, reg2);
		this.session = session;
		this.reg = reg2;
	}

	/**
	 * 初始化BOM
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/initBOMLine")
	public AjaxResult initBOMLine(@RequestParam("uid") String uid, @RequestParam("all") String all) {
		System.out.println("uid = "+uid+",all = "+all);
		
		if ("true".equals(all)) {
			return service.initBOMLine(uid, true);
		} else {
			return service.initBOMLine(uid, false);
		}
	}

	/**
	 * 展开子节点
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/expansionChile")
	public AjaxResult expansionChile(@RequestParam("uid") String uid, @RequestParam("bomKey") String bomKey) {
		return service.expansionChile(uid,bomKey);
	}

	/**
	 * 保存整个BOM结构，重新搭建BOMM
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/saveBOMChile")
	public AjaxResult saveBOMChile(HttpRequest request) {
		try {
			TCUtil.setBypass(session);
			
			String data = request.getBody();
			System.out.println("==>> data: " + data);
			Gson gson = new Gson();
			if(data !=null && !"[]".equals(data)) {
				ArrayList<L10EBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<ArrayList<L10EBOMBean>>() {}.getType());
				AjaxResult saveBOMChile = service.saveBOMChile(rootBeanList);
				
				return saveBOMChile;
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}
		return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "保存BOM错误！！");
	}

	/**
	 * 添加一行BOM
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addBOM")
	public AjaxResult addBOM(HttpRequest request) {
		try {
			String data = request.getBody();
			System.out.println("==>> data: " + data);

			return service.addBOM(data);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "添加BOM错误！");
	}

	@GetMapping("/getMaxFindNum")
	public AjaxResult getMaxFindNum(@RequestParam("itemRevUid") String itemRevUid) {
		try {
			System.out.println("==>> itemRevUid: " + itemRevUid);
			
			int[] max = service.getMaxFindNum(itemRevUid);
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("findNum", max[0]);
			map.put("altGroup", max[1]);
			AjaxResult ajaxResult = AjaxResult.success(map);
			
			System.out.println("ajaxResult = " + ajaxResult);
			
			return ajaxResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "系統繁忙，請稍後再試");
	}

	/**
	 * 检查BOM
	 * 
	 * @param req
	 * @return
	 * @throws TCException
	 */
	@PostMapping("/checkBOMLine")
	public AjaxResult checkBOMLine(HttpRequest req) throws TCException {
		return null;
	}

	/**
	 * 查詢料號信息
	 * 
	 * @param uid
	 * @param type part、semi、panel、packing、me、meMuxiliary、ee、externalWire、pi、powerLine
	 * @return
	 */
	@GetMapping("/partNum")
	public AjaxResult getPartNum(@RequestParam("uid") String uid, @RequestParam("partUid") String partUid, @RequestParam("type") String type) {
		return service.getPartNum(uid, partUid, type);
	}

	/**
	 * 查詢專案數據添加到對應的文件夾下
	 * 
	 * @param uid  選中的EBOM製作申請單uid
	 * @param type 添加到文件夾的類型
	 * @return
	 */
	@PostMapping("/addPart")
	public AjaxResult addPart(@RequestParam("uid") String uid, @RequestParam("type") String type) {
		return service.addPart(uid, type);
	}

	/**
	 * 删除指定物料
	 * 
	 * @param uid
	 * @param type
	 * @param partUid
	 * @return
	 */
	@PostMapping("/deletePart")
	public AjaxResult deletePart(@RequestParam("uid") String uid, @RequestParam("type") String type,
			@RequestParam("partUids") String partUids) {
		return service.deletePart(uid, type, partUids);
	}

	/**
	 * 批量添加物料
	 * 
	 * @param uid
	 * @param type
	 * @param partUids
	 * @return
	 */
	@PostMapping("/batchAddPart")
	public AjaxResult batchAddPart(@RequestParam("uid") String uid, @RequestParam("type") String type,
			@RequestParam("partUids") String partUids) {
		return service.batchAddPart(uid, type, partUids);
	}

	/**
	 * 根据itemId查询item版本对象
	 * 
	 * @param itemId
	 * @return
	 */
	@GetMapping("/searchPart")
	public AjaxResult searchPart(@RequestParam("itemId") String itemId) {
		return service.searchPart(itemId);
	}
	
	@PostMapping("/updateProp")
	public AjaxResult updateProp(@RequestParam("uid") String uid, @RequestParam("remark") String remark) {
		return service.updateProp(uid, remark);
	}
	
	@PostMapping("/createDcn")
	public AjaxResult createDcn(@RequestParam("uid") String uid) {
		return service.createDcn(uid);
	}

}
