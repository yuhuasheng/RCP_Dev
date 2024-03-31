package com.foxconn.electronics.ftereport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.google.gson.Gson;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.ui.common.RACUIUtil;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.http.HttpUtil;

public class FteUtil {
	public static JSONObject uploadFile(String reportType, File file) throws FileNotFoundException {
		String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,
				"D9_SpringCloud_URL") + "/tc-service/fteBenefitReport/uploadFile";
//		String url = "http://127.0.0.1:8780" +"/fteBenefitReport/uploadFile";
//        url = "http://10.203.64.160:8888/fteBenefitReport/uploadFile"; // url
//        url = "http://10.203.64.87:8888/fteBenefitReport/uploadFile"; // url
		Map<String, Object> paramMap = new HashMap<>();
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file), file.getName());
		paramMap.put("file", resource); 
		paramMap.put("reportType", reportType);
		String rs = HttpUtil.post(url, paramMap);
		JSONObject rsObj=JSON.parseObject(rs);
		return rsObj;
//		return gson.fromJson(resultStr, AjaxResult.class);
	}

}
