package com.foxconn.decompile.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.decompile.util.HttpUtil;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class DocMailGroupService {


	/**
	   * 2ndSource 比对
	 * @param session
	 * @param coms
	 * @return
	 */
	public boolean  sendMail(TCSession session ,TCComponent[] coms,String descr){	
			try {
				JSONObject o=new JSONObject();
				TCComponentItemRevision irv=null;
			     for (int j = 0; j < coms.length; j++) {		    
				     if (!(coms[j] instanceof TCComponentItemRevision)) {
					    continue;
				     }				  				     
				     TCComponentItemRevision v= handlleCompare((TCComponentItemRevision)coms[j],o);				         
			         if(v!=null) {
			        	 irv=v; 
			         }			     
			     }	
				o.put("descr", descr);  
				JSONObject log=null;
				if(irv!=null) {	
				    log=new JSONObject();
				    String creator=session.getUser().getUserId();
				    String creatorName=session.getUser().getUserName();
				    log.put("functionName", "任務溝通等郵件發送及資料傳遞時間");
				    log.put("creator", creator);
				    log.put("creatorName", creatorName);
				    log.put("project", irv.getProperty("project_ids"));
				    log.put("rev", irv.getProperty("item_revision_id"));
				    log.put("itemId", irv.getProperty("item_id"));
			        log.put("revUid", irv.getUid());
				}
			    String url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
			   
			    new MailThread(url,o,log).start();
		   }catch(Exception e) {
	 		   MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
	 		   return false;
	      }	
			
		 return true;
		
	}
	
	
	
	class MailThread  extends Thread {
		private String url;
		private  JSONObject o;
		private  JSONObject log;
		public MailThread(String url,JSONObject obj,JSONObject log){
			this.url=url;
			this.o=obj;
			this.log=log;
		}
		@Override
		public void run() {
			try {
				   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				   Date dat1=new Date();
				   HttpUtil.post(url+"/tc-integrate/mailgroup/sendMail",o.toJSONString())  ;
				   Date dat2=new Date();
				   if(log!=null) {
					   log.put("startTime", sdf.format(dat1));
				       log.put("endTime", sdf.format(dat2));
				       JSONArray logs=new JSONArray();
				       logs.add(log);
				       HttpUtil.post(url + "/tc-integrate/actionlog/addlog", logs.toJSONString());						
				   }
			}catch(Exception e) {}
		}
		
	}
	private TCComponentItemRevision handlleCompare(TCComponentItemRevision currItemRev,JSONObject obj)throws Exception {
		
		String objType=currItemRev.getTypeObject().getName();
		if((!objType.equalsIgnoreCase("D9_TECHRevision"))) {
			return null ;
		}
		String mailGroupID = currItemRev.getProperty("d9_MailGroupID");
		if(mailGroupID==null||"".equalsIgnoreCase(mailGroupID)) {
			return null;
		}
		String oname= currItemRev.getProperty("object_name");
		obj.put("fname", oname);
		String uid=currItemRev.getUid();
		String[] m= mailGroupID.split(",");
		for(String g:m) {
			if(obj.getString(g)==null) {
				obj.put(g, uid+",");
			}else {
				String v=obj.getString(g);
				v=v+uid+",";
				obj.put(g, v);
			}
		}	
		return currItemRev;
	}

	 
	
}
