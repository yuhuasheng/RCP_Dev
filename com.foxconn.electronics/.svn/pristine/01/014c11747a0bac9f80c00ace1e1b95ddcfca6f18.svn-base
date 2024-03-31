package com.foxconn.electronics.dbomconvertebom;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.util.StrUtil;

/**
 * 
 * @author MW00457
 *
 */
@RequestMapping("/electronics")
public class DBOMConvertEBOMController {
	private TCComponentBOMLine topBOMLine;
	private TCSession session;
	private TCComponentItemRevision itemrev;

	public DBOMConvertEBOMController(TCComponentBOMLine topBOMLine, TCSession session) throws Exception {
		this.topBOMLine = topBOMLine;
		this.session = session;
	}

	@GetMapping("/getDBOMConvertInit")
	public AjaxResult getDBOMConvertInit() {
		try {
			//TCUtil.setBypass(session);

			topBOMLine.refresh();

			DBOMInfo pdbominfo = DBOMConvertEBOMHandler.getTCBOMinfo(null, topBOMLine);
			DBOMInfo alldbominfo = DBOMConvertEBOMHandler.getTCBOMLines(pdbominfo, topBOMLine);

			//TCUtil.closeBypass(session);
			
			String jsons = JSONArray.toJSONString(alldbominfo);
			System.out.print("json ==" + jsons);
			

			return AjaxResult.success(alldbominfo);
		} catch (Exception e) {
			//TCUtil.closeBypass(session);
			e.printStackTrace();
		}	
		return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "系統繁忙，請稍後再試");
	}

	@PostMapping("/checkEBOMStruct")
	public AjaxResult checkEBOMStruct(HttpRequest req) throws TCException {
		try {
			//TCUtil.setBypass(session);

			String json = req.getBody();
			System.out.println("json = " + json);
			Gson gson1 = new Gson();
			DBOMInfo resultDBOMinfo = gson1.fromJson(json, new TypeToken<DBOMInfo>() {
			}.getType());

			//检查顶层是否有权限
			List<HHPNInfo> hhpninfoList = resultDBOMinfo.getHhpnInfo();
			if (hhpninfoList != null && hhpninfoList.size() > 0) {
				HHPNInfo hhpnInfo = hhpninfoList.get(0);
				String hhpn = hhpnInfo.getHhpn();
				TCComponentItemRevision itemrevhhpn = DBOMConvertEBOMHandler.getItemRevisionById(hhpn, session);
			
				boolean checkOwninguserisWrite = TCUtil.checkOwninguserisWrite(session, itemrevhhpn);
				if(!checkOwninguserisWrite) {
					MessageBox.post(AIFUtility.getActiveDesktop(),"转化失败，无顶层物料:"+hhpn+"的修改权限！" , "提示", MessageBox.INFORMATION);
					
					return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "转化失败，无顶层物料:"+hhpn+"的修改权限！");
					//return AjaxResult.success("转化失败，无顶层物料:"+hhpn+"的修改权限！" , true);
				}
			}
			
			//鸿海料号检查
			StringBuffer strb = new StringBuffer();
			DBOMConvertEBOMHandler.checkhhpn(resultDBOMinfo, session,strb);
			String errorinfo = strb.toString();
			if(StrUtil.isNotEmpty(errorinfo)) {
				MessageBox.post(AIFUtility.getActiveDesktop(),"转化失败，下面设计对象的鸿海料号不满足条件：\r\n" + errorinfo, "提示", MessageBox.INFORMATION);
				
				return new AjaxResult(AjaxResult.STATUS_PARAM_ERROR, "转化失败，下面设计对象的鸿海料号不满足条件：\r\n" + errorinfo, errorinfo);
				//return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "转化失败，下面设计对象的鸿海料号不满足条件：\r\n" + errorinfo);
				//return AjaxResult.success("转化失败，下面设计对象的鸿海料号不满足条件：\r\n" + errorinfo , true);
			}
			
			// 顶层检查 检查是否已转化
			itemrev = DBOMConvertEBOMHandler.checkTopbomline(resultDBOMinfo, session);

			if (itemrev == null) {
				TCComponentBOMWindow bomwindow = DBOMConvertEBOMHandler.createBOMWindow(session);
				TCComponentItemRevision itemrevhhpn = DBOMConvertEBOMHandler.savedbominfo(resultDBOMinfo, session,bomwindow,null);
				bomwindow.save();
				bomwindow.close();
				
				String itmeid = itemrevhhpn.getProperty("item_id");
				System.out.println("itmeid = " + itmeid);
				
				TCComponentItem item = itemrevhhpn.getItem();
				boolean isnot = true;
				TCComponentFolder newStuffFolder = session.getUser().getNewStuffFolder();
				AIFComponentContext[] children = newStuffFolder.getChildren();
				if (children != null && children.length > 0) {
					for (int i = 0; i < children.length; i++) {
						if (children[i].getComponent() == item) {
							isnot = false;
							break;
						}
					}
				}
				if (isnot) {
					newStuffFolder.add("contents", item);
				}
				
				MessageBox.post(AIFUtility.getActiveDesktop(),"选定的设计结构已成功转换EBOM\r\n转换EBOM结构的顶层零组件：" + itmeid + "可在Newstuff文件夹中找到", "提示", MessageBox.INFORMATION);
				//TCUtil.closeBypass(session);
				return AjaxResult.success("选定的设计结构已成功转换EBOM\r\n转换EBOM结构的顶层零组件" + itmeid + "可在Newstuff文件夹中找到", true);
			} else {
				//TCUtil.closeBypass(session);
				System.out.println("发送前端，是否确定转化EBOM");
				return AjaxResult.success("操作成功", false);
			}

		} catch (Exception e) {
			//TCUtil.closeBypass(session);
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "BOM update failed ," + e.getLocalizedMessage());
		}
	}

	@PostMapping("/submitBOM")
	public AjaxResult submitBOM(HttpRequest req) throws TCException {
		try {
			//TCUtil.setBypass(session);

			DBOMConvertEBOMHandler.cutBOMLine(session, itemrev);
			String json = req.getBody();
			System.out.println("json = " + json);

			Gson gson1 = new Gson();
			DBOMInfo resultDBOMinfo = gson1.fromJson(json, new TypeToken<DBOMInfo>() {
			}.getType());

			TCComponentBOMWindow bomwindow = DBOMConvertEBOMHandler.createBOMWindow(session);
			TCComponentItemRevision itemrevhhpn = DBOMConvertEBOMHandler.savedbominfo(resultDBOMinfo, session, bomwindow, null);
			bomwindow.save();
			bomwindow.close();
			
			String itmeid = itemrevhhpn.getProperty("item_id");
			System.out.println("itmeid = " + itmeid);

			TCComponentItem item = itemrevhhpn.getItem();
			boolean isnot = true;
			TCComponentFolder newStuffFolder = session.getUser().getNewStuffFolder();
			AIFComponentContext[] children = newStuffFolder.getChildren();
			if (children != null && children.length > 0) {
				for (int i = 0; i < children.length; i++) {
					if (children[i].getComponent() == item) {
						isnot = false;
						break;
					}
				}
			}
			if (isnot) {
				newStuffFolder.add("contents", item);
			}
			
			MessageBox.post(AIFUtility.getActiveDesktop(),"选定的设计结构已成功转换EBOM\r\n转换EBOM结构的顶层零组件：" + itmeid + "可在Newstuff文件夹中找到", "提示", MessageBox.INFORMATION);
			//TCUtil.closeBypass(session);
			return AjaxResult.success("选定的设计结构已成功转换EBOM\r\n转换EBOM结构的顶层零组件" + itmeid + "可在Newstuff文件夹中找到", true);
		} catch (Exception e) {
			//TCUtil.closeBypass(session);
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "BOM update failed ," + e.getLocalizedMessage());
		}
	}

}
