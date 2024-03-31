package com.foxconn.mechanism.SecondSourceImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2007_06.Workflow;

import cn.hutool.http.HttpUtil;








public class ImportCommand {
	
	
	 public HashMap<String,String> createGroup(TCSession session ,GroupPojo groupPojo,TCComponentFolder  folder,Text text, TCComponentFolder avlFolder) throws Exception {
		   List<MaterialPojo> mms=groupPojo.getMaterialPojos();
		   if(mms==null||mms.size()<=1) {
			 return null;
		   }
		   String url=TCUtil.getPreference(TCUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
		   url=url+"/tc-service/materialInfo/getMaterialGroupAndBaseUnit";
		   HashMap<String,String> mapRp=new HashMap<String,String>();
		   List<TCComponent> needReleases=new ArrayList<>();
		   TCComponentItemType itemType= (TCComponentItemType)session.getTypeComponent(ITypeName.Item);
		   TCComponentItem parentItem = itemType.create(null, "A", groupPojo.getObjType(), null, "", null);
		   TCComponentItemRevision parentRevison=parentItem.getLatestItemRevision();
		   needReleases.add(parentRevison);
		  
		   parentRevison.setProperty("d9_BU", groupPojo.getBu());
		   parentRevison.setProperty("d9_EnglishDescription", mms.get(0).getDescriptionEn());
		   parentRevison.setProperty("object_name", mms.get(0).getDescriptionEn());
		   parentItem.setProperty("object_name", mms.get(0).getDescriptionEn());
		   mapRp.put("itemid", parentRevison.getProperty("item_id"));
		   mapRp.put("uid", parentRevison.getUid());
		   TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
		   TCComponentBOMLine topLine = TCUtil.getTopBomline(bomWindow, parentItem);
		   for(MaterialPojo materialPojo:mms) {
			   Display.getDefault().syncExec(new Runnable() {
	        	    public void run() {
	        	    	 text.append("正在处理数据"+materialPojo.getItemId()+"\n");
	        	    }
	        	    }); 
		     TCComponentItem item=null;
		     TCComponent[] cs=TCUtil.executeQuery(session, "__Item_Revision_name_or_ID", new String[] {"items_tag.item_id"}, new String[] {materialPojo.getItemId()});
		    if(cs!=null&&cs.length>0) {
		    	item=((TCComponentItemRevision)cs[0]).getItem();			    
		     }else {
		    	 Map<String,String> map=new HashMap<String,String>();
		    	 map.put("d9_EnglishDescription", materialPojo.getDescriptionEn());
		    	 String manuId=materialPojo.getManufactureId();
		    	 if(manuId!=null&&!"".equalsIgnoreCase(manuId)) {
		    	   map.put("d9_ManufacturerID", manuId);
		    	 }
		    	 String manuPn=materialPojo.getManufaturePn();
		    	 if(manuPn!=null&&!"".equalsIgnoreCase(manuPn)) {
		    	   map.put("d9_ManufacturerPN", manuPn);
		    	 }
		    	 JSONArray arr=new JSONArray();
		    	 JSONObject obj=new JSONObject();
		    	 obj.put("materialNum", materialPojo.getItemId());
		    	 arr.add(obj);
		    	 String rs=HttpUtil.post(url, arr.toJSONString());
		    	 JSONObject ro= JSONObject.parseObject(rs);
		    	 if(ro.getInteger("code")==0) {
		    		 JSONArray l=ro.getJSONArray("data");
		    		 if(l!=null&&l.size()>0) {
		    			 JSONObject od= l.getJSONObject(0);
		    			 map.put("d9_ProcurementMethods", "F");
		    			 map.put("d9_MaterialType", "ZROH");
		    			 map.put("d9_MaterialGroup", od.getString("materialGroup"));
		    			 map.put("d9_Un", od.getString("baseUnit"));
		    		 }
		    	 }
		    	 item=itemType.create(materialPojo.getItemId(), "A", materialPojo.getObjType(), materialPojo.getItemId(), "", null);
		    	 TCComponentItemRevision itemRevision= item.getLatestItemRevision();
		    	 itemRevision.setProperties(map);
		    	 needReleases.add(itemRevision);
		    	 try {
			       avlFolder.add("contents", item);
			     }catch(Exception e) {}
		     }
		         topLine.add(item, item.getLatestItemRevision(), null, false);
		       
		  }
		  bomWindow.save();
		  bomWindow.close();
		  
		  TCComponent[] views = parentRevison.getRelatedComponents("structure_revisions");
		  if (views != null && views.length > 0) {
				TCComponentBOMViewRevision viewRev = (TCComponentBOMViewRevision) views[0];
				needReleases.add(viewRev);
			}
		  
		  
		  folder.add("contents", parentItem);		
		/*  try {
			     TCComponent[] items=new TCComponent[needReleases.size()];
			    for(int i=0;i<needReleases.size();i++) {
				   items[i]=needReleases.get(i);
			    }  
			    addStatus(session,items,"D9_Release");
		    }catch(Exception e0) {}*/
			return mapRp;
	 }
	 
	 private void addStatus(TCSession session,TCComponent[] itemRevisons,String statusNmae) {
	        try {
		           WorkflowService  workflowService= WorkflowService.getService(session);
		           Workflow.ReleaseStatusInput[] releaseStatusInputs = new Workflow.ReleaseStatusInput[1];
		           Workflow.ReleaseStatusInput releaseStatusInput = new Workflow.ReleaseStatusInput();
		           releaseStatusInputs[0] = releaseStatusInput;
		           Workflow.ReleaseStatusOption[] releaseStatusOptions = new Workflow.ReleaseStatusOption[1];
		           Workflow.ReleaseStatusOption releaseStatusOption = new Workflow.ReleaseStatusOption();
		           releaseStatusOptions[0] = releaseStatusOption;
		          releaseStatusOption.existingreleaseStatusTypeName = "";
		          releaseStatusOption.newReleaseStatusTypeName = statusNmae;
		          releaseStatusOption.operation = "Append";
		          releaseStatusInput.objects = itemRevisons;
		          releaseStatusInput.operations = releaseStatusOptions;
		          Workflow.SetReleaseStatusResponse reponse = workflowService.setReleaseStatus(releaseStatusInputs);
		         if (reponse.serviceData.sizeOfPartialErrors() > 0) {
		           System.out.println("修改对象版本的状态值失败");
		         }
	        }catch(Exception e) {}
	 }
	 
	 
	 private void createWorkflow(TCComponentItemRevision[] itemRevisons,String taskTemplate,TCSession session) throws Exception{
			TCComponentTaskTemplateType taskTemplateType = (TCComponentTaskTemplateType)session
					.getTypeComponent("EPMTaskTemplate");
			TCComponentTaskTemplate[] taskTemplates = taskTemplateType.getProcessTemplates(false, false, (TCComponent[]) null, (String[]) null,
					(String) null);
			TCComponentTaskTemplate tCComponentTaskTemplate=null;
			for(TCComponentTaskTemplate t:taskTemplates) {
				if(taskTemplate.equalsIgnoreCase(t.getName())) {
					tCComponentTaskTemplate=t;
					break;
				}
			}
			TCComponentProcessType localTCComponentProcessType = (TCComponentProcessType)session.getTypeComponent("Job");
			int[] var7 = new int[itemRevisons.length];
			Arrays.fill(var7, 1);
			localTCComponentProcessType.create(""+new Date().getTime(), taskTemplate, tCComponentTaskTemplate,itemRevisons ,var7);
		}
	 
}
