package com.foxconn.electronics.dbomconvertebom;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

import cn.hutool.http.HttpUtil;

public class QueryL5L6PNMS {
	public TCSession session;
	public String url;

	public QueryL5L6PNMS(TCSession session) throws Exception {
		this.session = session;
		url = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
		if (url == null || "".equalsIgnoreCase(url)) {
			throw new Exception("请设置首选项 D9_SpringCloud_URL");
		}
	}

	public TCComponentItemRevision itemrevhhpn(String item_id,String hhpnName) throws Exception {
		TCComponentItemRevision itemrev = null;
		List<HHPNPojo> hhpnpojo = QueryL5(item_id);
		if (hhpnpojo != null) {
			itemrev = createItemhh(hhpnpojo, "D9_CommonPart", hhpnName);
		} else {
			hhpnpojo = QueryL6(item_id);
			if (hhpnpojo != null) {
				itemrev = createItemhh(hhpnpojo, "D9_PCA_Part", hhpnName);
			} else {
				hhpnpojo = QueryPNMS(item_id);
				if (hhpnpojo != null) {
					itemrev = createItemhh(hhpnpojo, "D9_CommonPart", hhpnName);
				}
			}
		}
		return itemrev;
	}

	public TCComponentItemRevision createItemhh(List<HHPNPojo> hhpnpojos, String itemtype, String hhpnName) throws TCException {
		TCComponentItemRevision latestItemRevision = null;
		if (hhpnpojos != null && hhpnpojos.size() > 0) {
			for (HHPNPojo hhpnpojo : hhpnpojos) {
				String itemId = hhpnpojo.getItemId();
				TCComponentItem newItem = TCUtil.createItem(session, itemId, "", hhpnName, itemtype);
				latestItemRevision = newItem.getLatestItemRevision();

				// 设置属性
				String descr = hhpnpojo.getDescr();
				if("".equals(descr)) 
					descr = hhpnName;
				latestItemRevision.setProperty("d9_EnglishDescription", descr);
				
				String cnDescr = hhpnpojo.getCnDescr();
				latestItemRevision.setProperty("d9_ChineseDescription", cnDescr);
				String enDescr = hhpnpojo.getEnDescr();
				latestItemRevision.setProperty("d9_DescriptionSAP", enDescr);
				
				String customer = hhpnpojo.getCustomer();
				latestItemRevision.setProperty("d9_Customer", customer);
				String customerPN = hhpnpojo.getCustomerPn();
				latestItemRevision.setProperty("d9_CustomerPN", customerPN);
				String customerPNRev = hhpnpojo.getCustomerPnRev();
				latestItemRevision.setProperty("d9_CustomerPNRev", customerPNRev);
				String manufacturer = hhpnpojo.getMfg();
				latestItemRevision.setProperty("d9_ManufacturerID", manufacturer);
				String manufacturerPN = hhpnpojo.getMfgPN();
				latestItemRevision.setProperty("d9_ManufacturerPN", manufacturerPN);
				
				
			}
		}

		return latestItemRevision;
	}

	public List<HHPNPojo> QueryL5(String item_id) throws Exception {

		ArrayList<HHPNPojo> arrayHHList = new ArrayList<HHPNPojo>();
		HHPNPojo hhPNPojo = new HHPNPojo();
		hhPNPojo.setIndex(0);
		hhPNPojo.setDataFrom("DT L5Agile");
		hhPNPojo.setItemId(item_id);
		hhPNPojo.setMaterialType("PCA");
		arrayHHList.add(hhPNPojo);

		String jsons = JSONArray.toJSONString(arrayHHList);
		System.out.println("JSONS= " + jsons);

		String rs = HttpUtil.post(url + "/tc-integrate/agile/partManage/getPartInfo", jsons);
		System.out.print("rs = " + rs);
		List<HHPNPojo> rsTmps = null;

		if (rs != null && !"".equalsIgnoreCase(rs)) {
			rsTmps = JSON.parseArray(rs, HHPNPojo.class);
		}
		return rsTmps;
	}

	public List<HHPNPojo> QueryL6(String item_id) throws Exception {
		ArrayList<HHPNPojo> arrayHHList = new ArrayList<HHPNPojo>();
		HHPNPojo hhPNPojo = new HHPNPojo();
		hhPNPojo.setIndex(0);
		hhPNPojo.setDataFrom("DT L5Agile");
		hhPNPojo.setItemId(item_id);
		hhPNPojo.setMaterialType("PCA");
		arrayHHList.add(hhPNPojo);

		String jsons = JSONArray.toJSONString(arrayHHList);
		System.out.println("JSONS= " + jsons);

		String rs = HttpUtil.post(url + "/tc-integrate/agile/partManage/getPartInfo", jsons);
		System.out.print(rs);

		List<HHPNPojo> rsTmps = null;
		if (rs != null && !"".equalsIgnoreCase(rs)) {
			rsTmps = JSON.parseArray(rs, HHPNPojo.class);
		}

		return rsTmps;
	}

	public List<HHPNPojo> QueryPNMS(String item_id) throws Exception {
		ArrayList<HHPNPojo> arrayHHList = new ArrayList<HHPNPojo>();
		HHPNPojo hhPNPojo = new HHPNPojo();
		hhPNPojo.setIndex(0);
		hhPNPojo.setDataFrom("DT L5Agile");
		hhPNPojo.setItemId(item_id);
		hhPNPojo.setMaterialType("PCA");
		arrayHHList.add(hhPNPojo);

		String jsons = JSONArray.toJSONString(arrayHHList);
		System.out.println("JSONS= " + jsons);

		String rs = HttpUtil.post(url + "/tc-integrate/agile/partManage/getPartInfo", jsons);
		System.out.print(rs);

		List<HHPNPojo> rsTmps = null;
		if (rs != null && !"".equalsIgnoreCase(rs)) {
			rsTmps = JSON.parseArray(rs, HHPNPojo.class);
		}
		return rsTmps;
	}
}
