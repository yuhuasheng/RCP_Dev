package com.foxconn.decompile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.decompile.pojo.CustomPNPojo;
import com.foxconn.decompile.util.HttpUtil;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class CustomPnService {


	/**
	   * 申请自编料号
	 * @param session
	 * @param coms
	 * @return
	 */
	public boolean  applyCustomPn(TCSession session,TCComponent[] coms,String apply_custom_pn_exclude){
	
			List<CustomPNPojo> customPNPojos=new  ArrayList<CustomPNPojo>();
			try {
			     for (int j = 0; j < coms.length; j++) {    
				     if (!(coms[j] instanceof TCComponentItemRevision)) {
					    continue;
				     }
				     CustomPNPojo customPNPojo=new CustomPNPojo();
				     customPNPojo.setEcUid("");
				     customPNPojo.setPlant("");
				     TCComponentItemRevision rev = (TCComponentItemRevision) coms[j];   
				     int f=0;
				     String type= rev.getTypeObject().getName();
				     if(apply_custom_pn_exclude!=null&&!("".equalsIgnoreCase(apply_custom_pn_exclude))) {
				    	  String[] m=apply_custom_pn_exclude.split(";");
		                   for(String s:m) {
		                	   if(s.equalsIgnoreCase(type)) {
						    		  f=1;
						    	  }
		                   }				   
				      }
				     if(f==0) {
				     customPNPojos.add(getCustomPNPojo(rev,"",session));
				     }
			    }
		   }catch(Exception e) {
			   System.out.println(e);
	 		   MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
	 		   return false;
	      }	
		
		 String jsons = JSONArray.toJSONString(customPNPojos);
		 System.out.print("Custom pn json =="+jsons);
		 String url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
		 url=url+"/tc-integrate/custompn/applyCustomPN";
		 try {
			String msg=HttpUtil.post(url, jsons);
			System.out.println(msg);
			JSONObject o=JSONObject.parseObject(msg);
			String data=o.getString("data");
			if(!"".equalsIgnoreCase(data)) {
				 MessageBox.post(AIFUtility.getActiveDesktop(), data, "Error", MessageBox.ERROR);
				return false;
			}
		 }catch(Exception e) {
			   System.out.println(e);
			   MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
	 		  return false;
		 }
		 return true;
		
	}
	
	public boolean  postCustomPn(TCSession session,TCComponent[] coms){
	     int cnt=0;
	     TCComponentItemRevision itemRev=null;
	     for (int i = 0; i < coms.length; i++) {
		  try {
		      if (!coms[i].isTypeOf("D9_MNT_DCNRevision")) {
			     continue;
		       }
		      itemRev = (TCComponentItemRevision) coms[i];
		      cnt++;
		    }catch(Exception e) {}
	     }
	    if(cnt>1||itemRev==null) {
		    MessageBox.post(AIFUtility.getActiveDesktop(),"DCN 超出最大数限制或类型不正确", "Error", MessageBox.ERROR);
		    return false;
	     }
	   
	    
	    List<CustomPNPojo> customPNPojos=new  ArrayList<CustomPNPojo>();
		try {
			 String plant="";
			 AIFComponentContext[] rs=itemRev.getRelated("IMAN_specification");
			 for(AIFComponentContext r:rs) {
				  if( r.getComponent() instanceof TCComponentForm) {
					  TCComponentForm fm= (TCComponentForm)r.getComponent();
					  if(fm.isTypeOf("D9_MNT_DCNForm")) {
						  plant= fm.getProperty("d9_ChangeType");
					  }
				  }
			 }
			 if(plant==null||"".equalsIgnoreCase(plant)) {
				  MessageBox.post(AIFUtility.getActiveDesktop(),"请选择抛转工厂", "Error", MessageBox.ERROR);
				   return false;			 
			 }
		     System.out.println("Custom pn itemRev ==" + itemRev);
		     TCComponent[] components = itemRev.getRelatedComponents("CMHasSolutionItem");
		     Map<String,String> lineMap=new HashMap<String,String>();
		     List<TCComponentItemRevision> irs=new  ArrayList<TCComponentItemRevision>();
		     for (int j = 0; j < components.length; j++) {		    
			     if (!(components[j] instanceof TCComponentItemRevision)) {
				    continue;
			     }
			     
			     TCComponentItemRevision rev = (TCComponentItemRevision) components[j];		
			     String id=rev.getProperty("item_id");
			     if(lineMap.get(id)==null) {			    	
			    	 lineMap.put(id, id);
			    	 irs.add(rev);
			     }
			     
			     TCComponentBOMLine topLine=TCUtil.openBomWindow(session, rev);
			     List<TCComponentBOMLine> tmps=new ArrayList<TCComponentBOMLine>();
			     TCUtil.getTCComponmentBOMSingleLines(topLine, tmps, true);
			     for(TCComponentBOMLine l:tmps) {
			    	 TCComponentItemRevision ir=l.getItemRevision();
			    	 String irId=ir.getProperty("item_id");
			    	 if(lineMap.get(irId)==null) {
			    		 lineMap.put(irId, irId);
			    		 if(irId.startsWith("49")) {
			    			System.out.println(id+"===="+irId); 
			    		 }
			    		 irs.add(ir);
					 }
			     }
	
		    }

		    for(TCComponentItemRevision rev:irs) {
			     customPNPojos.add(getCustomPNPojo(rev,plant,session));
		    }
	   }catch(Exception e) {
		   System.out.println(e);
		   MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
		   return false;
     }	
	
		String jsons = JSONArray.toJSONString(customPNPojos);
		System.out.print("Custom pn json =="+jsons);
		String url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
		url=url+"/tc-integrate/custompn/postCustomPN";
		new PsotThread(url,jsons).start();
		return true;
	
}
	
	
	
	private CustomPNPojo getCustomPNPojo(TCComponentItemRevision rev,String plant ,TCSession session) throws Exception {
		
		 CustomPNPojo customPNPojo=new CustomPNPojo();
	     customPNPojo.setEcUid("");
	     customPNPojo.setPlant(plant);
	
	     TCComponentItem item=rev.getItem();
	     customPNPojo.setUid(item.getUid());
	     customPNPojo.setIrUid(rev.getUid());
	     String itemId=item.getProperty("item_id");
	     customPNPojo.setOldMaterialNumber(itemId);
	    
	     TCProperty pro=rev.getTCProperty("d9_EnglishDescription");
	     String proName= pro.getName();	
	     String englishSpec=(String)pro.getPropertyValue();				 
	     if(englishSpec==null||"".equalsIgnoreCase(englishSpec)) {
	    	throw new Exception(itemId +" "+proName+"不能为空"); 
	     }
	     customPNPojo.setDescription(englishSpec);
	    
	     pro=rev.getTCProperty("d9_ChineseDescription");
	     proName= pro.getName();	
	     String chineseSpec=(String)pro.getPropertyValue();
	     if(chineseSpec==null) {
	    	 chineseSpec="";
	     }
	     customPNPojo.setDescriptionZH(chineseSpec);
	    
	     pro=rev.getTCProperty("d9_Un");
	     proName= pro.getName();	
	     String baseUnit=(String)pro.getPropertyValue();
	     if(baseUnit==null||"".equalsIgnoreCase(baseUnit)) {
	    	throw new Exception(itemId +" "+proName+"不能为空"); 
	     }

	     customPNPojo.setBaseUnit(baseUnit);
	     
	     
	     pro=rev.getTCProperty("d9_TempPN");
	     proName= pro.getName();	
	     String tempPN=(String)pro.getPropertyValue();
	     if(tempPN==null) {
	    	 tempPN="";
		 }
	     customPNPojo.setRuleRegx(tempPN);
	    			
	     customPNPojo.setMaterialNumber("");
	     
	     
	     pro=rev.getTCProperty("d9_ProcurementMethods");
	     proName= pro.getName();
	     String partSource=(String)pro.getPropertyValue();
        if(partSource==null||"".equalsIgnoreCase(partSource)) {
       	 throw new Exception(itemId +" "+proName+"不能为空"); 
		 }
        customPNPojo.setPartSource(partSource);
	
         pro=rev.getTCProperty("d9_MaterialGroup");
	     proName= pro.getName();
	     String materialGroup=(String)pro.getPropertyValue();
	     if(materialGroup==null||"".equalsIgnoreCase(materialGroup)) {
			throw new Exception(itemId +" "+proName+"不能为空");
		 }
	     customPNPojo.setMaterialGroup(materialGroup);
	
	     pro=rev.getTCProperty("d9_MaterialType");
	     proName= pro.getName();
	     String materialType=(String)pro.getPropertyValue();
	     if(materialType==null||"".equalsIgnoreCase(materialType)) {
			throw new Exception(itemId +" "+proName+"不能为空"); 
		 }
	     customPNPojo.setMaterialType(materialType);
	       try {
	          pro=rev.getTCProperty("d9_ModelName");
	          String modelName=(String)pro.getPropertyValue();
	          customPNPojo.setModelName(modelName);
	       }catch(Exception e) {}
	     
	     return customPNPojo;
	}
	
	
	
	private class PsotThread extends Thread{
		private String url;
		private String json;
		
		public PsotThread(String url,String json) {
			   this.url=url;
			   this.json=json;
		}

		@Override
		public void run() {
			try {
				HttpUtil.post(url, json);	
			}catch(Exception e) {
				System.out.print(e);
			}
		}
	}
	
	private String getValue(String[] vs,String v) {
		
		String str="";
		try {
		    for(String s:vs) {
			   String k=s.split("=")[0].trim();
			   if(k.equalsIgnoreCase(v.trim())) {
				   return s.split("=")[1].trim();
			   }
		    }
		}catch(Exception e) {}
		return str;
	}
	
}
