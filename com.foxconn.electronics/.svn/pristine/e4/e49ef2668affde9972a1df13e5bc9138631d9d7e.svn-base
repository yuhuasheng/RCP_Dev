package com.foxconn.electronics.download;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.InetAddress;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class CopyDownloadAddressHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		
		try {
			String url = "";
			TCComponentDataset dataset = (TCComponentDataset) targetComponent;
			String objectType = dataset.getTypeObject().getName();
	        if (!"HTML".equalsIgnoreCase(objectType)) {
	        	String uid = targetComponent.getUid();
	    		String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");
	    		url = preference + "/tc-hdfs/downloadFile?site=WH&refId=" + uid;
	        }else {
	        	 String ip=getFileServerIp(session);
	        	 String fileVersionId = dataset.getProperty("object_desc");
	             url = "http://" + ip + ":8019/downloadFile?fileVersionId="+fileVersionId;
			}
			
	        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable transferable = new StringSelection(url);
			systemClipboard.setContents(transferable, null);
			TCUtil.infoMsgBox(url, "download Address");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 private String  getFileServerIp(TCSession session) {
         String fileServerIp=null;
         String def=null;
       try {
              String ip=InetAddress.getLocalHost().getHostAddress();
              System.out.print("ip=================="+ip);
              List<String> fs= TCUtil.getArrayPreference(TCPreferenceService.TC_preference_site, "D9_FileServer_List");
              fileServerIp=fs.get(0).split("=")[1];
              for(String s:fs) {
                  String k=s.split("=")[0];
                  String v=s.split("=")[1];
                  k=k.replaceAll("\\.\\*","");
                  System.out.print(">>>>>>>>>>>>>>>>>"+k);
                  if("default".equalsIgnoreCase(k)) {
                      def=v;
                  }
                  if(ip.startsWith(k)) {
                      fileServerIp=v;
                      break;
                  }
              }
              if(fileServerIp==null) {
                  fileServerIp=def;
              }
              System.out.print("^^^^^^^^^^^^^"+fileServerIp);  
           }catch(Exception e) {}
       return fileServerIp;
       
	 }
}
