package com.foxconn.mechanism.hhpnmaterialapply.MNT;

import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.util.CommonTools;

public class MNTTreePropertiesTools {

	
	/**
	 * 更新属性值
	 * 
	 * @param propertiesInfo
	 * @param index
	 * @param str
	 */
	public static void updateProperties(PropertiesInfo propertiesInfo, int index, String str) {
		switch (index) {
		case 3:
			propertiesInfo.setChineseDescription(str);
			break;
		case 4:
			propertiesInfo.setEnglishDescription(str);
			break;
		case 6:
			propertiesInfo.setMaterial(str);
			break;
		case 7:
			propertiesInfo.setPartWeight(str);
			break;
		case 8:
			propertiesInfo.setColor(str);
			break;
		case 9:
			propertiesInfo.setRemark(str);
			break;
		case 10:
			propertiesInfo.setSurfaceFinished(str);
			break;
		case 11:
			propertiesInfo.setReferenceDimension(str);
			break;
		case 12:
			propertiesInfo.setRunnerWeight(str);
			break;
		case 13:
			propertiesInfo.setTotalweight(str);
			break;
		default:
			return;
		}
	}

	/**
	 * 判断属性值是否发生更改
	 * @param currentBomInfo
	 * @param index
	 * @param value
	 * @return
	 */
	public static boolean checkModify(BOMInfo currentBomInfo, int index, String value) {
		if (CommonTools.isEmpty(currentBomInfo)) {
			return false;
		}
		if (CommonTools.isEmpty(value)) { // 判断是否为空
			return false;
		}
		PropertiesInfo currentPropertiesInfo = currentBomInfo.getPropertiesInfo();
		switch (index) {
		case 3:
			if (value.equals(currentPropertiesInfo.getChineseDescription())) {
				return false;
			}
			return true;
		case 4:
			if (value.equals(currentPropertiesInfo.getEnglishDescription())) {
				return false;
			}
			return true;
		case 6:
			if (value.equals(currentPropertiesInfo.getMaterial())) {
				return false;
			}
			return true;
		case 7:
			if (value.equals(currentPropertiesInfo.getPartWeight())) {
				return false;
			}
			return true;
		case 8:
			if (value.equals(currentPropertiesInfo.getColor())) {
				return false;
			}
			return true;
		case 9:
			if (value.equals(currentPropertiesInfo.getRemark())) {
				return false;
			}
			return true;
		case 10:
			if (value.equals(currentPropertiesInfo.getSurfaceFinished())) {
				return false;
			}
			return true;
		case 11:
			if (value.equals(currentPropertiesInfo.getReferenceDimension())) {
				return false;
			}
			return true;
		case 12:
			if (value.equals(currentPropertiesInfo.getRunnerWeight())) {
				return false;
			}
			return true;
		case 13:
			if (value.equals(currentPropertiesInfo.getTotalweight())) {
				return false;
			}
			return true;
		default:
			return false;
		}
	}

}
