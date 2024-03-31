package com.hh.fx.rewrite.util;

import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class ExcelSetValuesUtil {

	public static void excelSetValues(TCSession session, String[] propNames,TCComponentItemRevision itemRev,Sheet hssfSheet) throws Exception{
		GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
		HashMap<String,String> mapLov = getPreferenceUtil.getHashMapPreference(session, TCPreferenceService.TC_preference_site, "FX8_ExcelSpecialLov", "=");
		for (int i = 0; i < propNames.length; i++) {
			System.out.println("propNames[i] == "+propNames[i]);
			String proString = itemRev.getProperty(propNames[i]);
			System.out.println("proString == "+proString);
			for(Row row : hssfSheet){
				for(Cell hssfCell : row){
					if(hssfCell.getCellComment() != null){
						String pz = hssfCell.getCellComment().getString().getString().trim();
						System.out.println("Åú×¢ ==" + pz);
						if(mapLov != null && mapLov.get(propNames[i]) != null){
							System.out.println("Æ´½ÓLOVÖµ ==" + propNames[i] + "=" + proString);
							if(pz.equals(propNames[i] + "=" + proString)){
								hssfCell.setCellValue("[ V ] " + proString);
							}
						}else{
							if(hssfCell.getCellComment().getString().getString().trim().equals(propNames[i].trim())){
								hssfCell.setCellValue(proString);
								System.out.println("set proString == "+proString);
							}
						}
					}						
				}
			}
		}
		
	}
	
}
