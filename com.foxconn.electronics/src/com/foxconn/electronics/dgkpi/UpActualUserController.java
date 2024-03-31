package com.foxconn.electronics.dgkpi;

import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.util.HttpUtil;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;

@RequestMapping("/dgkpi")
public class UpActualUserController
{
    private TCComponentBOMLine rootLine;
    private TCSession session;
    public UpActualUserController(TCComponentBOMWindow bomWindow,TCSession session) throws TCException
    {
        this.rootLine = bomWindow.getTopBOMLine();
        this.session=session;
    }

    
    @GetMapping("/getProjIntc")
    public String getProjIntc()
    {
        try
        {   String url = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");
            String msg=HttpUtil.post(url+"/tc-integrate/actionlog/getProjIntc", "");
        	System.out.println(msg);
            return msg;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{}
        return null;
    }
    
    
    @GetMapping("/getActualUsers")
    public String getActualUsers()
    {
        try
        {   String url = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");
        	String msg=HttpUtil.post(url+"/tc-integrate/actionlog/getActualUsers", "");
        	
        	System.out.println(msg);
            return msg;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{}
        return "";
    }
    
    @GetMapping("/getBomLines")
    public AjaxResult getBomLines()
    {
        try
        {      	
        	BomPojo topBom= new BomPojo();
        	TCComponentItemRevision itemRev= rootLine.getItemRevision();
        	itemRev.refresh();
        	boolean hasWrite=checkUserPrivilege(session,itemRev);
        	String uid=itemRev.getUid();
        	String itemId=itemRev.getProperty("item_id");
        	String objName=itemRev.getProperty("object_name");
        	String actualUser=itemRev.getProperty("d9_ActualUserID");
         	String firstProj=itemRev.getProperty("d9_InitialProject");
         	TCProperty p=itemRev.getTCProperty("owning_user");
       	    TCComponentUser u= (TCComponentUser)p.getReferenceValue();
       	    String userId= u.getProperty("user_id");
       	    String userName= u.getProperty("user_name");
       	    topBom.setOwner(userName+"("+userId+")");
       	    String rev=itemRev.getProperty("current_revision_id");
       	    topBom.setRev(rev);
       	    boolean  hasStatus=false;
            TCComponent[] relStatus = itemRev.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
             {
            	hasStatus= true;
             }
         
       	    topBom.setHasStatus(hasStatus);
       	 
         	topBom.setFirstProj(firstProj);
         	topBom.setActualUser(actualUser);
        	topBom.setUid(uid);
        	topBom.setItemId(itemId);
        	topBom.setObjName(objName);
        	topBom.setHasWrite(hasWrite);
        	getAllChild(rootLine,topBom);
        	System.out.println(JSONObject.toJSONString(topBom));
            return AjaxResult.success(topBom);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{}
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"服務器繁忙，請稍後重試");
    }

   
    @PostMapping("/upBomLines")
    public AjaxResult upBomLines(HttpRequest request)
    {
        try
        {
        	String data = request.getBody();
    		System.out.println("==>> data: " + data);		
    	     List<MMPojo> mmPojos= JSONArray.parseArray(data, MMPojo.class);
    		for(MMPojo mmPojo:mmPojos) {
    			TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(mmPojo.getUid());
    			itemRev.refresh();
    			itemRev.setProperty("d9_ActualUserID", mmPojo.getActualUser());
    			itemRev.setProperty("d9_InitialProject", mmPojo.getFirstProj());
    		}
            return AjaxResult.success("");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{}
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"服務器繁忙，請稍後重試");
    }

    
    
    
    
    private void getAllChild(TCComponentBOMLine parentLine,BomPojo parentbom) throws Exception
    {
        AIFComponentContext[] children = parentLine.getChildren();
        HashMap<String,String> mp= new HashMap<String,String>();
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            if( bomLine.isSubstitute()) {
        	   continue;
            }
            BomPojo bom= new BomPojo();
        	TCComponentItemRevision itemRev= bomLine.getItemRevision();
        	if(itemRev==null) {
        		continue;
        	}
        	itemRev.refresh();
        	boolean hasWrite=checkUserPrivilege(session,itemRev);
        	String uid=itemRev.getUid();
        	if(mp.get(uid)!=null) {
        		continue;
        	}
        	mp.put(uid, uid);
        	String itemId=itemRev.getProperty("item_id");
        	String objName=itemRev.getProperty("object_name");
         	String actualUser=itemRev.getProperty("d9_ActualUserID");
         	String firstProj=itemRev.getProperty("d9_InitialProject");
         	TCProperty p=itemRev.getTCProperty("owning_user");
       	    TCComponentUser u= (TCComponentUser)p.getReferenceValue();
       	    String userId= u.getProperty("user_id");
       	    String userName= u.getProperty("user_name");
       	     bom.setOwner(userName+"("+userId+")");
       	    String rev=itemRev.getProperty("current_revision_id");
       	    bom.setRev(rev);
       	    boolean  hasStatus=false;
            TCComponent[] relStatus = itemRev.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
             {
            	hasStatus= true;
             }
         
            bom.setHasStatus(hasStatus);
         	
         	
         	bom.setFirstProj(firstProj);
         	bom.setActualUser(actualUser);
         	
         	
         	
        	bom.setUid(uid);
        	bom.setItemId(itemId);
        	bom.setObjName(objName);
        	bom.setHasWrite(hasWrite);
        	parentbom.getChild().add(bom);
        	getAllChild(bomLine,bom);
        }
    }
    

    public  boolean checkUserPrivilege(TCSession session, TCComponent com) {
		boolean isWrite = false;
		try {
			TCAccessControlService service = session.getTCAccessControlService();
			isWrite = service.checkPrivilege(com, TCAccessControlService.WRITE);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isWrite;
	}
    
}
