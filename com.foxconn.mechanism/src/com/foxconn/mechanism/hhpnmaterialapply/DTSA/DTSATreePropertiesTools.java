package com.foxconn.mechanism.hhpnmaterialapply.DTSA;

import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.util.CommonTools;

public class DTSATreePropertiesTools {	

	/**
	 * 更新属性值
	 * 
	 * @param bomInfo
	 * @param propertiesInfo
	 * @param str
	 */
	public static void updateProperties(PropertiesInfo propertiesInfo, int index, String str) {
		switch (index) {
		case 4:
			propertiesInfo.setCustomerPN(str);
			break;
		case 5:
			propertiesInfo.setChineseDescription(str);
			break;
		case 6:
			propertiesInfo.setEnglishDescription(str);
			break;
		case 8:
			propertiesInfo.setMaterial(str);
			break;
		case 9:
			propertiesInfo.setULClass(str);
			break;
		case 10:
			propertiesInfo.setPartWeight(str);
			break;
		case 11:
			propertiesInfo.setColor(str);
			break;
		case 12:
			propertiesInfo.setPainting(str);
			break;
		case 13:
			propertiesInfo.setPrinting(str);
			break;
		case 14:
			propertiesInfo.setRemark(str);
			break;
		case 15:
			propertiesInfo.setSurfaceFinished(str);
			break;
		case 16:
			propertiesInfo.setTechnology(str);
			break;
		case 17:
			propertiesInfo.setADHESIVE(str);
			break;
		default:
			return;
		}	
		
	}
	
	
	/**
	 * 判断属性值是否发生更改
	 * @param custTreeNode
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
		case 4:
			if (value.equals(currentPropertiesInfo.getCustomerPN())) {
				return false;
			}
			return true;
		case 5:
			if (value.equals(currentPropertiesInfo.getChineseDescription())) {
				return false;
			}
			return true;
		case 6:
			if (value.equals(currentPropertiesInfo.getEnglishDescription())) {
				return false;
			}
			return true;
		case 8:
			if (value.equals(currentPropertiesInfo.getMaterial())) {
				return false;
			}
			return true;
		case 9:
			if (value.equals(currentPropertiesInfo.getULClass())) {
				return false;
			}
			return true;
		case 10:
			if (value.equals(currentPropertiesInfo.getPartWeight())) {
				return false;
			}
			return true;
		case 11:
			if (value.equals(currentPropertiesInfo.getColor())) {
				return false;
			}
			return true;
		case 12:
			if (value.equals(currentPropertiesInfo.getPainting())) {
				return false;
			}
			return true;
		case 13:
			if (value.equals(currentPropertiesInfo.getPrinting())) {
				return false;
			}
			return true;
		case 14:
			if (value.equals(currentPropertiesInfo.getRemark())) {
				return false;
			}
			return true;
		case 15:
			if (value.equals(currentPropertiesInfo.getSurfaceFinished())) {
				return false;
			}
			return true;
		case 16:
			if (value.equals(currentPropertiesInfo.getTechnology())) {
				return false;
			}
			return true;
		case 17:
			if (value.equals(currentPropertiesInfo.getADHESIVE())) {
				return false;
			}
			return true;
		default:
			return false;
		}
	}
}
