package com.foxconn.electronics.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.services.IOpenService;
import com.teamcenter.rac.util.MessageBox;

@Deprecated
public class MyJobListHandler extends AbstractHandler {
	public AbstractAIFUIApplication app;
	public TCSession session = null;
	public String OtherName = "其他任务";
	public String JobName = "要执行的任务";

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("11111111111111");
		
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		OSSUserPojo userPojo = UserLoginSecond.getOSSUserInfo();
		String nickname = "apadmin";
		String name_no = "apadmin (apadmin) 任务箱";
		
		if(userPojo!=null) {
			nickname  = S2TTransferUtil.toTraditionnalString(userPojo.getNickname());
			String emp_no = userPojo.getEmp_no();
			System.out.println("nickname = "+nickname);
			System.out.println("emp_no = "+emp_no);
			
			name_no = nickname+"("+emp_no+")"+"任務箱";
		} else {
			MessageBox.post("未檢測到，登錄的二級賬號，請退出重新登錄！","提示",MessageBox.INFORMATION);
			return null;
		}
		
		
		try {
			ArrayList<TCComponentTask> jobList = new ArrayList<TCComponentTask>();
			ArrayList<TCComponentTask> otherList = new ArrayList<TCComponentTask>();
			//要执行的任务
			TCComponent userInBox = session.getUser().getUserInBox();
			AIFComponentContext[] childrens = userInBox.getChildren();
			for (AIFComponentContext children : childrens) {
				InterfaceAIFComponent component2 = children.getComponent();
				//String object_name = component2.getProperty("object_string");
				//System.out.println(object_name);

				AIFComponentContext[] childrens2 = component2.getChildren();
				AIFComponentContext[] childrens3 = childrens2[0].getComponent().getChildren();
				for (AIFComponentContext children3 : childrens3) {
					InterfaceAIFComponent component3 = children3.getComponent();
					
					TCComponentTask jobComponent = (TCComponentTask)component3;
					String object_name13 = component3.getProperty("object_string");
					System.out.println(object_name13);
					String parent_name = jobComponent.getProperty("parent_name");
					System.out.println("object_name13 = "+object_name13+"----->parent_name = "+parent_name);
					if(parent_name.equals("Review")) {
					    System.out.println("-------------");
					    
					}
					
					//TCComponent[] allAttachments = jobComponent.getAttachments(TCAttachmentScope.GLOBAL,1);
					TCComponent[] relatedComponents = jobComponent.getRelatedComponents("root_target_attachments");
					if(relatedComponents!=null && relatedComponents.length > 0) {
						boolean analysePublicMail = analysePublicMail((TCComponentItemRevision) relatedComponents[0], parent_name, nickname);				
						if(analysePublicMail) {
							jobList.add((TCComponentTask) jobComponent);
						} else {
							otherList.add((TCComponentTask) jobComponent);
						}
					}
				}
			}
			
			//ArrayList<TCComponent> listMy = new ArrayList<TCComponent>();
			//自定义我的工作列表
			TCComponent userFolder = getMyTaskInbox(name_no);
			TCComponent jobFolder = null;
			TCComponent otherFolder = null;
			if(userFolder == null) {
				userFolder = createTaskInbox(session , "Folder",name_no);
				jobFolder = createTaskInbox(session , "Folder",JobName);
				otherFolder = createTaskInbox(session , "Folder",OtherName);
				userFolder.add("contents", jobFolder);
				userFolder.add("contents", otherFolder);
			} else {
				AIFComponentContext[] children = userFolder.getChildren();
				for (AIFComponentContext aif :children) {
					TCComponent folder = (TCComponent) aif.getComponent();
					String object_name = folder.getProperty("object_name");
					if(JobName.equals(object_name)) {
						jobFolder = folder;
					} else if(OtherName.equals(object_name)) {
						otherFolder = folder;
					}
				}
			}
			
			if(jobFolder!=null) {
				jobFolder.setRelated("contents", new TCComponent[] {});
			}
			if(otherFolder!=null) {
				otherFolder.setRelated("contents", new TCComponent[] {});
			}
			
//			AIFComponentContext[] children = component.getChildren();
//			for (AIFComponentContext aif : children) {
//				InterfaceAIFComponent component2 = aif.getComponent();
//				listMy.add((TCComponent) component2);
//			}
			
			
			//打开我的工作列表
			IOpenService currentOpenService = AIFUtility.getCurrentOpenService();
			boolean open = currentOpenService.open(userFolder);
			if (open) {
				if(jobList !=null && jobList.size() > 0) {
					for (TCComponentTask component3:jobList) {
						jobFolder.add("contents", component3);
					}
				}
				
				if(otherList !=null && otherList.size() > 0) {
					for (TCComponentTask component3:otherList) {
						otherFolder.add("contents", component3);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 删除某个文件夹下的所有文件
	 * 
	 * @param delpath String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefile(String delpath) throws Exception {
		try {
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						//System.out.println("【INFO】 " + delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
			}

		} catch (FileNotFoundException e) {
			//System.out.println("【ERROR】 " + "deletefile() Exception:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String getFilePath(String foldName) {
		String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
		File file = new File(tempPath + foldName);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	/**
	 * 解析流程审批邮件附件文本
	 * @return
	 * @throws Exception
	 */
	private boolean analysePublicMail(TCComponentItemRevision itemr,String parent_name,String nickname) throws Exception {
		TCComponentForm mailForm = com.foxconn.tcutils.util.TCUtil.getMailForm(itemr);
		if(mailForm !=null) {
			TCComponent[] taskTable = mailForm.getRelatedComponents("d9_TaskTable");
			if(taskTable!=null && taskTable.length > 0) {
				for (int i = 0; i < taskTable.length; i++) {
					String d9_ProcessNode = taskTable[i].getProperty("d9_ProcessNode");
					if(parent_name.equals(d9_ProcessNode)){
						String d9_ActualUserName = taskTable[i].getProperty("d9_ActualUserName");
						if(d9_ActualUserName.contains(nickname)) {
							System.out.println("d9_ActualUserName = "+d9_ActualUserName);
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * 读取文件信息到List
	 * 
	 * @param fileNow
	 * @param strCharsetName
	 * @return
	 * @throws Exception
	 * @author:
	 */
	public static List<String> getContent(File fileNow, String strCharsetName) throws Exception {
		List<String> retLst = new ArrayList<String>();

		String strLine = null;
		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;

		try {
			fis = new FileInputStream(fileNow);
			if (strCharsetName != null && strCharsetName.length() > 0) {
				isr = new InputStreamReader(fis, strCharsetName);
			} else {
				isr = new InputStreamReader(fis);
			}
			br = new BufferedReader(isr);
			while ((strLine = br.readLine()) != null) {
				retLst.add(strLine);
			}
		} finally {
			if (fis != null)
				fis.close();
			if (isr != null)
				isr.close();
			if (br != null)
				br.close();
		}

		return retLst;
	}
	
	
	public static boolean isContent(List<String> contentLst, String key, String user) {
		for (String content : contentLst) {
			String[] lineArr = content.split("=");
			if (2 == lineArr.length) {
				String lineKey = lineArr[0];
				if(lineKey.equals(key)) {
					System.out.println("lineKey ="+lineKey);
					String lineValue = lineArr[1];
					String[] pubUserArr = lineValue.split("##");
					if (2 == pubUserArr.length) {
						String[] realUserArr = pubUserArr[1].split("%%");
						if (1 == realUserArr.length || 2 == realUserArr.length) {
							String realUser = realUserArr[0];
							System.out.println("------------------->"+realUser);
							System.out.println("user------------------->"+user);
							if(user.equals(realUser)) {
								System.out.println("realUser = "+realUser);
								
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}

	
	
	 /**
     * 創建 TaskInbox
     * 
     * @param session
     * @param type
     * @param name
     * @return
     */
    public static TCComponent createTaskInbox(TCSession session, String type, String name)
    {
        List<ICreateInstanceInput>iputList = new ArrayList<>();
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, type);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        createInstanceInput.add("object_name", name);
        iputList.add(createInstanceInput);
        List<TCComponent> comps = null;
        TCComponent folder = null;
        try
        {
            comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
            folder = (TCComponent)comps.get(0);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return folder;
    }
    
    
    public TCComponent getMyTaskInbox(String name) throws Exception {
    	TCComponent folderComponent = null;
    	
    	TCComponent[] executeQuery = TCUtil.executeQuery(session, "常规...",
				new String[] { "object_name","object_type","owning_user.user_id" }, new String[] { name,"Folder","$USERID" });
		if (executeQuery != null && executeQuery.length > 0) {
			folderComponent = executeQuery[0];
		}
		return folderComponent;
    }
    
}
