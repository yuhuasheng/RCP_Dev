package com.foxconn.mechanism.custommaterial.customMaterialPostPlant;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Text;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.util.HttpUtil;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;



public class CustomPNUtil {

	public void PostCustomPn(TCSession session, Text textContent,List<TCComponentItemRevision> irs,String plant) throws Exception{
		List<CustomPNPojo> customPNPojos = new ArrayList<CustomPNPojo>();
	    String types=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_PostSAP_Type");
		for(TCComponentItemRevision rev : irs) {
			  textContent.append("正在處理數據"+rev.getProperty("item_id")+"...\n");			
			  if(!TCUtil.isReleased(rev)) {
			      throw  new Exception(rev.getProperty("item_id")+" 未發行!");
			  }
			  if(!(types.contains(rev.getTypeObject().getName()))) {
				  throw  new Exception(rev.getProperty("item_id")+" 此物料類型，不可以抛磚SAP!");
			  } 
			  customPNPojos.add(getCustomPNPojo(rev, plant, session));
		}
		String jsons = JSONArray.toJSONString(customPNPojos);
		System.out.print("Custom pn json ==" + jsons);
		String url = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
		url = url + "/tc-integrate/custompn/postCustomPN";
		textContent.append("開始抛轉數據,請稍後...\n");
		
		String msg=HttpUtil.post(url, jsons);
		JSONObject o=JSONObject.parseObject(msg);
		if(!o.getString("data").equalsIgnoreCase("")) {
			textContent.append(o.getString("data")+"\n");
			throw new Exception (o.getString("data"));
		}
		textContent.append("抛轉完成");
	}

	private CustomPNPojo getCustomPNPojo(TCComponentItemRevision rev, String plant, TCSession session)
			throws Exception {

		CustomPNPojo customPNPojo = new CustomPNPojo();
		customPNPojo.setEcUid("");
		customPNPojo.setPlant(plant);

		TCComponentItem item = rev.getItem();
		customPNPojo.setUid(item.getUid());
		customPNPojo.setIrUid(rev.getUid());
		String itemId = item.getProperty("item_id");
		customPNPojo.setOldMaterialNumber(itemId);

		TCProperty pro = rev.getTCProperty("d9_EnglishDescription");
		String proName = pro.getName();
		String englishSpec = (String) pro.getPropertyValue();
		if (englishSpec == null || "".equalsIgnoreCase(englishSpec)) {
			throw new Exception(itemId + " " + proName + "不能为空");
		}
		customPNPojo.setDescription(englishSpec);

		pro = rev.getTCProperty("d9_ChineseDescription");
		proName = pro.getName();
		String chineseSpec = (String) pro.getPropertyValue();
		if (chineseSpec == null ) {
			chineseSpec="";
		}
		customPNPojo.setDescriptionZH(chineseSpec);

		pro = rev.getTCProperty("d9_Un");
		proName = pro.getName();
		String baseUnit = (String) pro.getPropertyValue();
		if (baseUnit == null || "".equalsIgnoreCase(baseUnit)) {
			throw new Exception(itemId + " " + proName + "不能为空");
		}

		customPNPojo.setBaseUnit(baseUnit);

		pro = rev.getTCProperty("d9_TempPN");
		proName = pro.getName();
		String tempPN = (String) pro.getPropertyValue();
		if (tempPN == null || "".equalsIgnoreCase(tempPN)) {
			tempPN="";
		}
		customPNPojo.setRuleRegx(tempPN);

		customPNPojo.setMaterialNumber("");

		pro = rev.getTCProperty("d9_ProcurementMethods");
		proName = pro.getName();
		String partSource = (String) pro.getPropertyValue();
		if (partSource == null || "".equalsIgnoreCase(partSource)) {
			throw new Exception(itemId + " " + proName + "不能为空");
		}
		customPNPojo.setPartSource(partSource);

		pro = rev.getTCProperty("d9_MaterialGroup");
		proName = pro.getName();
		String materialGroup = (String) pro.getPropertyValue();
		if (materialGroup == null || "".equalsIgnoreCase(materialGroup)) {
			throw new Exception(itemId + " " + proName + "不能为空");
		}
		customPNPojo.setMaterialGroup(materialGroup);

		pro = rev.getTCProperty("d9_MaterialType");
		proName = pro.getName();
		String materialType = (String) pro.getPropertyValue();
		if (materialType == null || "".equalsIgnoreCase(materialType)) {
			throw new Exception(itemId + " " + proName + "不能为空");
		}
		customPNPojo.setMaterialType(materialType);

		return customPNPojo;
	}



	private String getValue(String[] vs, String v) {

		String str = "";
		try {
			for (String s : vs) {
				String k = s.split("=")[0].trim();
				if (k.equalsIgnoreCase(v.trim())) {
					return s.split("=")[1].trim();
				}
			}
		} catch (Exception e) {
		}
		return str;
	}

}
