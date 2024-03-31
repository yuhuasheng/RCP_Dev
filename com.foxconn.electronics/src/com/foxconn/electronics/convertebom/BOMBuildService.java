/**
 * 
 */
package com.foxconn.electronics.convertebom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.foxconn.electronics.convertebom.pojo.BOMPojo;
import com.foxconn.electronics.convertebom.pojo.MaterialPojo;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.util.TCPropName;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

/**
 * @author Leo
 *
 */
public class BOMBuildService {

	public static BOMPojo getPCAPreBOM(TCComponentBOMLine bomLine) throws TCException, IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		
		MaterialPojo rootMaterial = new MaterialPojo();
		setBeanPojo(rootMaterial, bomLine);
		
		BOMPojo rootBOM = new BOMPojo();
		setBeanPojo(rootBOM, bomLine);
		rootBOM.setSelfMaterial(rootMaterial);
		
		List<BOMPojo> child = getChildren(bomLine);
		rootBOM.setChild(child);
		
		return rootBOM;
	}

	private static List<BOMPojo> getChildren(TCComponentBOMLine bomLine) throws TCException, IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		
		List<BOMPojo> list = new ArrayList<BOMPojo>();
		AIFComponentContext[] children = bomLine.getChildren();
		if (children == null || children.length == 0) {
			return list;
		}
		
		for (AIFComponentContext aifComponentContext : children) {
			TCComponentBOMLine tcComponentBOMLine = (TCComponentBOMLine) aifComponentContext.getComponent();
			TCComponentItemRevision itemRev = tcComponentBOMLine.getItemRevision();
			MaterialPojo materialPojo = new MaterialPojo();
			setBeanPojo(materialPojo, tcComponentBOMLine);
			materialPojo.setItemRevision(itemRev);
			
			BOMPojo bomPojo = new BOMPojo();
			setBeanPojo(bomPojo, tcComponentBOMLine);
			bomPojo.setSelfMaterial(materialPojo);
			bomPojo.setChild(getChildren(tcComponentBOMLine));
			
			if (Constants.B1710.equals(materialPojo.getMaterialGroup()) || 
					Constants.B1711.equals(materialPojo.getMaterialGroup()) ||
					Constants.B1712.equals(materialPojo.getMaterialGroup())) {
				if (bomPojo.getQty() == null || bomPojo.getQty().length() == 0) {
					bomPojo.setQty("1");
				}
			}
			
			if (!tcComponentBOMLine.hasSubstitutes()) {
				list.add(bomPojo);
				continue;
			}
			
			TCComponentBOMLine[] listSubstitutes = tcComponentBOMLine.listSubstitutes();
			List<BOMPojo> substituteList = new ArrayList<BOMPojo>();
			for (int i = 0; i < listSubstitutes.length; i++) {
				TCComponentBOMLine subBomLine = listSubstitutes[i];	
				TCComponentItemRevision subItemRev = subBomLine.getItemRevision();
				
				materialPojo = new MaterialPojo();
				setBeanPojo(materialPojo, subBomLine);
				materialPojo.setItemRevision(subItemRev);
				
				BOMPojo bomPojo2 = new BOMPojo();
				setBeanPojo(bomPojo2, subBomLine);
				bomPojo.setSelfMaterial(materialPojo);
				
				bomPojo2.setSubstituteGroup(bomPojo);
				substituteList.add(bomPojo2);
			}
			
			bomPojo.setSubstitute(substituteList);
			
			list.add(bomPojo);
		}
		
		return list;
	}

	private static <T, M> void setBeanPojo(T t, M m) throws IllegalArgumentException, IllegalAccessException, TCException {
		// TODO Auto-generated method stub
		Field[] fields = t.getClass().getDeclaredFields();
		
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
			if (tcPropName == null) {
				continue;
			}
			
			String tcAttrName = tcPropName.value();
			if (tcAttrName.isEmpty()) {
				//TODO
				continue;
			}
			
			Object value = null;
			if (m instanceof TCComponentBOMLine) {
				value = ((TCComponentBOMLine) m).getProperty(tcAttrName).trim();
			} else if (m instanceof TCComponentItemRevision) {
				value = ((TCComponentItemRevision) m).getProperty(tcAttrName).trim();
			}
			
			if (value == null) {
				continue;
			}
			
			fields[i].set(t, value);
		}
	}

}
