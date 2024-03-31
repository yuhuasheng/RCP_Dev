package com.foxconn.decompile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.ResourceMember;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProfile;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.http.HttpUtil;

public class DesignMailService {


	/**
	  
	 * @param session
	 * @param coms
	 * @return
	 */
	public HashMap<String,Object>  updateMailInfo(TCComponentTaskTemplate tCComponentTaskTemplate, TCSession session,TCComponent[] paramArrayOfTCComponent,
			  int[] paramArrayOfInt,ResourceMember[] selResourceList) throws Exception {
	          if(paramArrayOfTCComponent==null||paramArrayOfTCComponent.length<=0) {
		         return null;
	          }
	          HashMap<String,Object> resp=  new HashMap<String,Object>();	        	        
	          List<TCComponent> atts= new ArrayList<>();	     
	          HashMap<String,TCComponentItemRevision> revMp= new HashMap<String,TCComponentItemRevision>();	
	          HashMap<String,TCComponentItemRevision> revMp2= new HashMap<String,TCComponentItemRevision>();	
	          for(int i=0;i<paramArrayOfTCComponent.length;i++) {
	    	        if(paramArrayOfInt[i]!=1) {
	    	        	continue;
	    	        }
	    	        if(!(paramArrayOfTCComponent[i] instanceof TCComponentItemRevision) ) {
	    	        	continue;
	    	        }
	    	        TCComponentItemRevision itemTevision= (TCComponentItemRevision)paramArrayOfTCComponent[i];
					String version = itemTevision.getProperty("item_revision_id");
					String revId=itemTevision.getProperty("item_id");
					try {
						String reg=".*[a-zA-Z]+.*";
						Matcher m=Pattern.compile(reg).matcher(version);
						if(m.matches()) {
							throw new Exception(""); 
						}
					}catch(Exception e) {
						MessageBox.post(AIFUtility.getActiveDesktop(), revId+" 該設計圖紙為量產階段，請走變更流程！", "Error", MessageBox.ERROR);
						throw new Exception(revId+" 該設計圖紙為量產階段，請走變更流程！");
					}
	    	        System.out.print("");
	    	        TCComponent[]  whereUseds= itemTevision.whereUsed((short)2);
	    	        if(whereUseds!=null&&whereUseds.length>0) {
	    	          for(TCComponent c:whereUseds) {
	    	        	if(!( c instanceof TCComponentItemRevision)) {
	    	        	   continue;	
	    	        	}
	    	        	TCComponentItemRevision rev=(TCComponentItemRevision)c; 	    	        	
	    	        	String itemId=rev.getProperty("item_id");	    	       
	    	        	Date dat=rev.getDateProperty("creation_date");
	    	        	if(revMp.get(itemId)==null) {
	    	        		revMp.put(itemId, rev);
	    	        	}else {
	    	        		Date dat2=revMp.get(itemId).getDateProperty("creation_date");
	    	        		if(dat.getTime()>dat2.getTime()) {
	    	        			revMp.put(itemId, rev);
	    	        		}
	    	        	}
	    	        	
	    	        	if(revMp2.get(itemId)==null) {
	    	        		   revMp2.put(itemId, rev);
	    	        	}else {
	    	        	    Date dat2=revMp2.get(itemId).getDateProperty("creation_date");
	    	        		  if(dat.getTime()>dat2.getTime()) {
	    	        			revMp2.put(itemId, rev);
	    	        		}
	    	        	}
	    	        	
	    	           }
	    	        }else {
	    	        	 revMp.put(revId, itemTevision);
	    	        }
	    	        
	           }
	         
	          Vector vector1 = new Vector();
	          Vector vector2 = new Vector();
	          Vector vector3 = new Vector();
	          Vector vector4 = new Vector();
	          
	          Set<String>  keys= revMp.keySet();
	          HashMap<String,String> m=new HashMap<String,String>();
	          for(String key:keys) {
	        	  TCComponentItemRevision rev=revMp.get(key);
	        	  TCProperty p=rev.getTCProperty("owning_user");
	        	  TCComponentUser o= (TCComponentUser)p.getReferenceValue();
	        	  String uid= o.getProperty("user_id");
	        	  if(m.get(uid)!=null) {
	        	      continue;	
	        	   }
	        	  TCComponentGroupMember ug=o.getGroupMembers()[0];
	        	  vector3.add(ug);
	        	  vector2.add(Integer.valueOf(1));
	        	  vector4.add(null);
	        	  m.put(uid, uid);
	          }
	          
	          if (vector3.size() > 0 && vector4.size() > 0 && vector2.size() > 0) {
	  			int n = vector3.size();
	  			TCComponent[] arrayOfTCComponent = new TCComponent[n];
	  			TCComponentProfile[] arrayOfTCComponentProfile = new TCComponentProfile[n];
	  			Integer[] arrayOfInteger = new Integer[n];
	  			vector3.toArray(arrayOfTCComponent);
	  			vector4.toArray(arrayOfTCComponentProfile);
	  			vector2.toArray(arrayOfInteger);
	  			ResourceMember resourceMember = new ResourceMember(tCComponentTaskTemplate, arrayOfTCComponent,
	  					arrayOfTCComponentProfile, arrayOfInteger, -100, -100, 0);
	  			vector1.add(resourceMember);
	  		}
	          
	          
	          keys= revMp2.keySet();	         
	          for(String key:keys) {
	        	  TCComponentItemRevision rev=revMp2.get(key);
	        	  atts.add(rev);
	          }
	          resp.put("atts", atts);
	          resp.put("res", vector1);
	          return resp;
	}
	
	
	
	public void sendMail(TCSession session,TCComponent[] components) {
		  
		            List<HashMap<String ,List<TCComponentItemRevision>>>  list=new ArrayList<HashMap<String ,List<TCComponentItemRevision>>>();
		         for(TCComponent comp:components) {
		        	 try {
		        	     if(!(comp instanceof TCComponentItemRevision)) {
		        		  continue; 
		        	     }	
		        	    HashMap<String ,List<TCComponentItemRevision>> map= new HashMap<String ,List<TCComponentItemRevision>>();
		        	    TCComponentItemRevision itemRev= (TCComponentItemRevision)comp;	
		        	    String id=itemRev.getProperty("item_id");
		        	    String revId=itemRev.getProperty("item_revision_id");  
		        	      
		        	   	    	    
		    	        TCComponent[]  whereUseds= itemRev.whereUsed((short)2);
		    	        HashMap<String,TCComponentItemRevision> revMp= new HashMap<String,TCComponentItemRevision>();
		    	        if(whereUseds!=null&&whereUseds.length>0) {
		    	        for(TCComponent c:whereUseds) {
		    	        	if(!( c instanceof TCComponentItemRevision)) {
		    	        	   continue;	
		    	        	}
		    	        	TCComponentItemRevision rev=(TCComponentItemRevision)c;
		    	        	String itemId=rev.getProperty("item_id");    
		    	        	Date dat=rev.getDateProperty("creation_date");
		    	        	if(revMp.get(itemId)==null) {
		    	        		revMp.put(itemId, rev);
		    	        	}else {
		    	        		Date dat2=revMp.get(itemId).getDateProperty("creation_date");
		    	        		if(dat.getTime()>dat2.getTime()) {
		    	        			revMp.put(itemId, rev);
		    	        		}
		    	        	}
		    	          }
		    	        }
		    	        
		    	       List<TCComponentItemRevision> ls=new ArrayList<TCComponentItemRevision>();
		    	       Set<String> keys=revMp.keySet();
		    	       for(String key:keys) {
		    	    	   ls.add(revMp.get(key));
		    	       } 
		    	       if(ls.size()>0) {
		    	    	   map.put(id+"/"+revId, ls);
		    	           list.add(map);
		    	       } 
		        	 }catch(Exception e) {
		        		 System.out.print(e);
		        	 } 
		         }
		         
		         HashMap<String,HashMap<String ,List<TCComponentItemRevision>>> map=new HashMap<String,HashMap<String ,List<TCComponentItemRevision>>>();
		         for(HashMap<String ,List<TCComponentItemRevision>>mp:list) {
		        	 try {
		        	   Set<String> keys=mp.keySet();
		        	   for(String key:keys) {
		        		   List<TCComponentItemRevision> ls= mp.get(key);
		        		   for(TCComponentItemRevision rev:ls) {
		        			         rev.refresh();
		        			         String userId=rev.getProperty("d9_ActualUserID");
		        			       if(userId==null||"".equalsIgnoreCase(userId.trim())) {
		        			    	   TCProperty p=rev.getTCProperty("owning_user");
				        	           TCComponentUser o= (TCComponentUser)p.getReferenceValue();
				        	           userId=o.getProperty("user_id").toUpperCase();
				        	        
		        			       }else {
		        			    	   userId=userId.substring(userId.indexOf("(")+1,userId.indexOf(")"));
		        			    	   userId=userId.toUpperCase();
		        			       }
		        			       HashMap<String ,List<TCComponentItemRevision>> m= map.get(userId);
		        			       if(m==null) {
		        			    	   m=new HashMap<String ,List<TCComponentItemRevision>>(); 
		        			    	   List<TCComponentItemRevision> l=new ArrayList<TCComponentItemRevision>();
		        			    	   l.add(rev);
		        			    	   m.put(key, l);
		        			    	   map.put(userId, m);
		        			       }else {
		        			    	   List<TCComponentItemRevision>l= m.get(key);
		        			    	   if(l==null) {
		        			    		   l=new ArrayList<TCComponentItemRevision>();
		        			    		   m.put(key, l);
		        			    	   }
		        			    	   l.add(rev);
		        			       }
		        		   }
		        	   }
		        	 }catch(Exception e) {
		        		 System.out.println(e);
		        	 }
		         }
		         
		         Set<String> keys=map.keySet();
		         JSONArray arr= new JSONArray();
		         for(String key:keys) {
		        	 HashMap<String ,List<TCComponentItemRevision>> mp=map.get(key);
		        	 JSONObject o= new JSONObject();
		        	 String s="";
		        	 Set<String> ks= mp.keySet();
		        	 for(String k:ks) {
		        		 s=key+",";		        		 
		        		 s=s+k+",";
		        		 List<TCComponentItemRevision> l=mp.get(k);
		        		 for(TCComponentItemRevision r:l) {
		        			 try {
		        			     String id=r.getProperty("item_id");
				        	     String revId=r.getProperty("item_revision_id");  
				        	     s=s+id+"/"+revId+",";
		        			 }catch(Exception e) {}
		        		 }
		        		 arr.add(s);
		        	 }
		         }
		         
		         System.out.print(arr.toJSONString());
		         String url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
				 url=url+"/tc-integrate/mailgroup/sendMail2";
				 
		        HttpUtil.post(url, arr.toJSONString());
		
	}
	
}
