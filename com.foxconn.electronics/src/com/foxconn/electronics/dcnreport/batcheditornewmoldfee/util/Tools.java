package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.constant.NewMoldFeeConstant;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.domain.NewMoldFeeBean;
import com.foxconn.tcutils.util.TCPropertes;

public class Tools {
	
	/**
	 * 更新绑定的data值
	 * @param bean
	 * @param index
	 * @param str
	 */
	public static void updateData(NewMoldFeeBean bean, int index, String str) {
		try {
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
				if (tcProp != null) {
					int cell = tcProp.cell();
					if (index != cell) {
						continue;
					}					

					Object val = str;
					Object orignValue = field.get(bean); // 获取原始值
					if (("".equals(orignValue) || orignValue == null) && (!"".equals(val) && val != null)) {
						bean.setHasModify(true); // 设置发生更改
					} else if ((!"".equals(orignValue) && orignValue != null) && ("".equals(val) || val == null)) {
						bean.setHasModify(true); // 设置发生更改
					} else if ((!"".equals(orignValue) && orignValue != null) && (!"".equals(val) || val != null)) {
						if (field.getType() == Integer.class) {
							if (((Integer) orignValue).intValue() != ((Integer) val).intValue()) {
								bean.setHasModify(true); // 设置发生更改
							}
						} else {
							if (!orignValue.toString().equals(val.toString())) {
								bean.setHasModify(true); // 设置发生更改
							}
						}
					}
					
					if (field.getType() == Integer.class) {
						if ("".equals(str) || str == null) {
							val = "";
						} else {
							val = Integer.parseInt(str);
						}

					} else {
						if (str == null) {
							val = "";
						}
					}
					
					field.set(bean, val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取TC 字符串属性
	 * @param bean
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Map<String, String> getStrPropMap(NewMoldFeeBean bean) throws IllegalArgumentException, IllegalAccessException {
		System.out.println("start -->>  getTCPropMap");
		Map<String, String> tcPropMap = new HashMap<>();
		Field[] fields = bean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> field.getType() == boolean.class);
		fieldList.removeIf(field -> !NewMoldFeeConstant.D9_MoldInfo.equals(field.getAnnotation(TCPropertes.class).tcType()));
		
		for (Field field : fieldList) {
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			if (tcProp != null) {
				String tcPropName = tcProp.tcProperty();
				Object o = field.get(bean);
				if (o != null) {
					if (field.getType() == String.class) {
						tcPropMap.put(tcPropName, (String) o);
					} 
				}
			}
		}
		return tcPropMap;
	}
}
