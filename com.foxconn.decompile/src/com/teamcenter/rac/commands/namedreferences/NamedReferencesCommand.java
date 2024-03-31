package com.teamcenter.rac.commands.namedreferences;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;


public class NamedReferencesCommand extends AbstractAIFCommand
{
  public NamedReferencesCommand(TCComponentDataset paramTCComponentDataset, Frame paramFrame)
  {
	  if(!downloadFromFileServer(paramTCComponentDataset,paramTCComponentDataset.getSession())) {
       setRunnable(new NamedReferencesDialog(paramTCComponentDataset, paramFrame));
	  }
  }

  public NamedReferencesCommand(TCComponentDataset paramTCComponentDataset, Dialog paramDialog)
  {
    if (paramDialog == null) {
    	 if(!downloadFromFileServer(paramTCComponentDataset,paramTCComponentDataset.getSession())) {
            setRunnable(new NamedReferencesDialog(paramTCComponentDataset));
    	 }
    }
    else {
    	  if(!downloadFromFileServer(paramTCComponentDataset,paramTCComponentDataset.getSession())) {
    		  setRunnable(new NamedReferencesDialog(paramTCComponentDataset, paramDialog));
    	  }
      }
  }

  public NamedReferencesCommand(TCComponentDataset paramTCComponentDataset)
  {
	  if(!downloadFromFileServer(paramTCComponentDataset,paramTCComponentDataset.getSession())) {
		  setRunnable(new NamedReferencesDialog(paramTCComponentDataset));
	  }
      
  }
  
  
  private boolean downloadFromFileServer(TCComponentDataset dataset,TCSession session) {
	  try {
	        dataset.refresh();
		    String objectType = dataset.getTypeObject().getName();
	        if (!"HTML".equalsIgnoreCase(objectType)) {
	    	   return false;
	        }		
			  String fileVersionId=dataset.getProperty("object_desc");
			  String fileName=dataset.getProperty("object_name");
			 //弹出文件下载框
			 new FileChooserThread(session,fileVersionId,fileName).start();
		     return true;
	  
	  }catch(Exception e) {}
	  return false;
  }
  
  
  class FileChooserThread extends Thread{
	       private TCSession session;
	       private String fileVersionId;
	       private String fileName;
	  public FileChooserThread(TCSession session,String fileVersionId,String fileName) {
		     this.session=session; 
		     this.fileVersionId=fileVersionId;
		     this.fileName=fileName;
	  }

	@Override
	public void run() {
		try {
		       JFileChooser fileChooser=new JFileChooser();
		       FileSystemView desk = FileSystemView.getFileSystemView();
		       File comDisk=desk.getHomeDirectory();   
		       fileChooser.setCurrentDirectory(comDisk);
		       fileChooser.setSelectedFile(new File(fileName));
		       int option=fileChooser.showSaveDialog(null);
		       if(option==JFileChooser.APPROVE_OPTION) {
		          File file= fileChooser.getSelectedFile();
		          if(!file.exists()) {
		    	    file.createNewFile();
		          }
		          InputStream inputStream=null;
		          FileOutputStream fileOutputStream=null;
		          try {
		          String ip=getFileServerIp(session);
		          String netAddress = "http://" + ip + ":8019/downloadFile?fileVersionId="+fileVersionId;
		          System.out.print(netAddress);
		          URL url = new URL(netAddress);
		          URLConnection conn = url.openConnection();
		          inputStream = conn.getInputStream();
		          fileOutputStream = new FileOutputStream(file);
		          int byteread;
		          byte[] buffer = new byte[1024];
		          while ((byteread = inputStream.read(buffer)) != -1) {
		                 fileOutputStream.write(buffer, 0, byteread);
		                 String msg=new String(buffer);		            
		                 if(msg.contains("syncBizFileFailed")){
		                     throw new Exception("file not found");
		               }
		           }
		          }finally {
		        	    try {
		        		  inputStream.close();
		        	     }catch(Exception e) {}
		        	    try {
		        		  fileOutputStream.close();
		        	     }catch(Exception e) {}
		          }
		      }
		}catch(Exception e) {
			MessageBox.post(AIFUtility.getActiveDesktop(), "文件下载失败", "Error", MessageBox.ERROR);
		}
	}
	  
  }
  
  
  private String  getFileServerIp(TCSession session) {
	      String fileServerIp=null;
	      String def=null;
		try {
			   String ip=InetAddress.getLocalHost().getHostAddress();
			   System.out.print("ip=================="+ip);
			   List<String> fs=getArrayPreference(session, TCPreferenceService.TC_preference_site, "D9_FileServer_List");
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
  
  
  @SuppressWarnings("deprecation")
  private static List<String> getArrayPreference(TCSession session, int scope, String preferenceName) {
		String[] array = session.getPreferenceService().getStringArray(scope, preferenceName);
		return Arrays.asList(array);
  }
  
}