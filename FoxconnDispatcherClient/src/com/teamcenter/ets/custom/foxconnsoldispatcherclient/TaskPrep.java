package com.teamcenter.ets.custom.foxconnsoldispatcherclient;

import java.util.LinkedHashMap;
import java.util.Map;

import com.teamcenter.ets.extract.DefaultTaskPrep;
import com.teamcenter.ets.request.TranslationRequest;
import com.teamcenter.translationservice.task.TranslationTask;

public class TaskPrep extends DefaultTaskPrep{

	
	public TaskPrep() {

	}

	public TranslationTask prepareTask() throws Exception {
		System.out.println("=================TaskPrep response start=================");
		String providerName = request.getPropertyObject("providerName").getStringValue();
		String translatorName = this.request.getPropertyObject("serviceName").getStringValue();
		
		System.out.println("==>> providerName: " + providerName);
		System.out.println("==>> translatorName: " + translatorName);
		// 获取输入参数值的键
		String[] argKeys = request.getPropertyObject(TranslationRequest.TRANSLATION_ARGS_KEYS).getStringArrayValue();
		System.out.println("==>> argKeys size : " + argKeys.length);

		// 获取输入参数值的值
		String[] argValues = request.getPropertyObject(TranslationRequest.TRANSLATION_ARGS_DATA).getStringArrayValue();
		// 获取参数
		Map<String, String> params = getParams(argKeys, argValues);
		System.out.println("==>> params: " + params);
		
		TranslationTask transTask = prepTransTask(null, null, null, null, false, false, null, 0, null);
		params.forEach((key, value) -> {
			try {
				addOptions(transTask, key, value == null ? "" : value.trim());
			} catch (Exception e) {				
				e.printStackTrace();
			}
		});
		m_zTaskLogger.info("End prepare Task");
		return addRefIdToTask(transTask, 0);
	}
	
	/**
	 * 获取参数
	 * @param argKeys
	 * @param argValues
	 * @return
	 */
	public Map<String, String> getParams(String[] argKeys, String[] argValues) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < argKeys.length; i++) {
			String key = argKeys[i];
			String value = argValues[i];
			map.put(key, value);
		}
		return map;
	}
}
