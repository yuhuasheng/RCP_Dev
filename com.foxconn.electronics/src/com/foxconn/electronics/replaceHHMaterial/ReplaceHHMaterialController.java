package com.foxconn.electronics.replaceHHMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hpsf.Array;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.document.ItemInfo;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.DeepCopyInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;

@RequestMapping("/replace")
public class ReplaceHHMaterialController {
	
	TCComponentBOMLine topBomLine;
	AbstractPSEApplication app;
	Map<TCComponentBOMLine,String> pathMap = new HashMap<TCComponentBOMLine, String>();
	
	public ReplaceHHMaterialController(AbstractPSEApplication app) throws TCException {
		this.app = app;
		TCComponentBOMWindow bomWindow = app.getBOMWindow();
		this.topBomLine = bomWindow.getTopBOMLine();
	}

	/**
	 * 获取全量BOM
	 */
	@GetMapping("/getBomFull")
	public AjaxResult getBomFull() {
		AjaxResult result = null;
		try {
			BomLineRet ret = new BomLineRet();
			traversalBom(topBomLine, ret,"");
			result = AjaxResult.success(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 替换接口
	 */
	@PostMapping("/apply")
	public AjaxResult apply(HttpRequest request) {
		AjaxResult result = null;
		try {
			List<ApplyParam> list = JSONObject.parseArray(request.getBody(),ApplyParam.class);
			pathMap.clear();
			Map<String,String> resultMap = new HashMap<String, String>();
			TCUtil.setBypass(app.getSession());
			modifyBom(topBomLine,list,"",true,resultMap);
			String bom = resultMap.get("newTopBom");
			if(bom == null) {
				result = AjaxResult.success();
			}else {
				result = AjaxResult.success("新BOM："+bom+" 已保存到NewStuff文件夹中。");
			}
			TCUtil.closeBypass(app.getSession());
		} catch (Exception e) {
			e.printStackTrace();
			TCUtil.closeBypass(app.getSession());
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR,e.getMessage());
		}
		return result;
	}
	
	void traversalBom(TCComponentBOMLine parent,BomLineRet parentRet,String path) throws Exception {
		// 获取信息
		path+=parent.getProperty("bl_item_item_id")+"/";
		parentRet.uid = parent.getUid();
		parentRet.path = path;
		parentRet.name = parent.getProperty("bl_indented_title");
		parentRet.oldId = parent.getProperty("bl_item_item_id");
		TCComponentBOMLine parent2 = parent.parent();
		if(parent2 == null) {
			parentRet.parentAndMeId = parentRet.oldId;
		}else {
			parentRet.parentAndMeId = parent2.getProperty("bl_item_item_id")+"/"+parentRet.oldId;
		}
		
		AIFComponentContext[] children = parent.getChildren();
		for (int i = 0; i < children.length; i++) {
			// 获取每一个子信息
			TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
			BomLineRet childRet = new BomLineRet();
			parentRet.children.add(childRet);
			// 下一层
			traversalBom(child,childRet,path);
		}
	}
	
	void modifyBom(TCComponentBOMLine parent,List<ApplyParam> applyList,String path,boolean isTopLayer,Map<String,String> resultMap) throws Exception {
		// 找到与当前bom匹配的id
		if(applyList.isEmpty()) {
			return;
		}
		String s = pathMap.get(parent);
		path+=(s!=null)?s+"/":(parent.getProperty("bl_item_item_id")+"/");
		String newId = findNewIdByPath(applyList,path);
		if(newId!=null) {
			if(isTopLayer) {
				// 顶级BOM要替换
				TCComponentItemRevision itemRevision = getItemRevisionById(newId);
				if(itemRevision!=null) {
				   throw new Exception("顶阶BOM："+newId+"已存在于系统中，不能替换。");
				}
				itemRevision = saveAs(parent,newId);
				app.getBOMWindow().save();
				TCComponentBOMLine topBomLine = TCUtil.openBomWindow(app.getSession(), itemRevision);
				pathMap.put(topBomLine, parent.getProperty("bl_item_item_id"));
				TCComponentFolder newStuffFolder = app.getSession().getUser().getNewStuffFolder();
				newStuffFolder.add("contents", itemRevision.getItem());
				newStuffFolder.refresh();
				resultMap.put("newTopBom",newId);
				modifyBom(topBomLine,applyList,"",false,resultMap);
				return;
			}
			// 替换操作
			parent = replace(parent,newId);
		}
		AIFComponentContext[] children = parent.getChildren();
		for (int i = 0; i < children.length; i++) {
			// 获取每一个子信息			
			TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
			// 下一层
			modifyBom(child,applyList,path,false,resultMap);
		}
	}
	
	String findNewIdByPath(List<ApplyParam> list,String path) {
		Iterator<ApplyParam> iterator = list.iterator();
		while(iterator.hasNext()) {
			ApplyParam ap = iterator.next();
			if(path.equals(ap.path)) {
				iterator.remove();
				return ap.newId;
			}
		}
		return null;
	}
	
	TCComponentBOMLine replace(TCComponentBOMLine oldBomLine,String newId) throws Exception {
		// 查询ID是否存
		TCComponentItemRevision itemRevision = getItemRevisionById(newId);
		if(itemRevision == null) {
			// 另存为一个副本
			try {
				itemRevision = saveAs(oldBomLine,newId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("ID: "+newId+" 不合法");
			}
		}
		String oldSeqNo = oldBomLine.getProperty("bl_sequence_no");
		String oldQty = oldBomLine.getProperty("bl_quantity");
		// 挂载
		TCComponentBOMLine newBomLine = oldBomLine.parent().add(itemRevision.getItem(), itemRevision, null, false);
		pathMap.put(newBomLine, oldBomLine.getProperty("bl_item_item_id"));
		// 剪切
		oldBomLine.cut();
		newBomLine.setProperty("bl_sequence_no",oldSeqNo);
		newBomLine.setProperty("bl_quantity",oldQty);
		app.getBOMWindow().save();
		return newBomLine;
		
	}
	
	TCComponentItemRevision getItemRevisionById(String id) throws Exception {
		TCComponent[] executeQuery = TCUtil.executeQuery(app.getSession(), "零组件版本...", new String[]{"items_tag.item_id"}, new String[]{id});
		int length = executeQuery.length;
		if(length==0) {
			return null;
		}		
		return (TCComponentItemRevision) executeQuery[0];
	
	}
	
	TCComponentItemRevision saveAs(TCComponentBOMLine bomline,String newId) throws TCException {
		TCComponentItemType typeComponent = bomline.getItem().getTypeComponent();
		String newRevId = typeComponent.getNewRev(null);
		TCComponentItem saveAsItem = bomline.getItemRevision().saveAsItem(newId, newRevId);		
		return saveAsItem.getLatestItemRevision();
	}
	
}
