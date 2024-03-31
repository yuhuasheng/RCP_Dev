package com.foxconn.electronics.login;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentIssueReportRevision;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class MyIssuesListCreateProcess extends AbstractHandler {
	public AbstractAIFUIApplication app;
	public static TCSession session = null;
	public String preferenceName = "D9_DefaultIssueWorkflow";
	private ResourceBundle reg;
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		reg	= ResourceBundle.getBundle("com.foxconn.electronics.login.login_locale");
		
		try {
			InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
			if(targetComponents!=null && targetComponents.length != 1) {
				MessageBox.post("請選擇單個對象執行 流程發起","提示",MessageBox.INFORMATION);
				return null;
			}
			TCComponent issueComponent = (TCComponent) targetComponents[0];
			issueComponent.refresh();
			
			if(issueComponent instanceof TCComponentIssueReportRevision) {
				String type = issueComponent.getType();
				
				TCComponent[] wfRelation = issueComponent.getRelatedComponents("fnd0StartedWorkflowTasks");
				if (wfRelation != null && wfRelation.length > 0 ) {
					MessageBox.post("當前IssueReport 已發起流程，不允許繼續發起流程","提示",MessageBox.INFORMATION);
					return null;
		        }
				
				TCProperty tcProperty = issueComponent.getTCProperty("CMClosure");
				String propertyValue = (String)tcProperty.getPropertyValue();
				System.out.println("CMClosure = "+propertyValue);
				
				if("Canceled".equals(propertyValue) || "Submitted".equals(propertyValue)) {
					MessageBox.post("當前IssueReport 狀態為"+propertyValue+"，不允許發起流程","提示",MessageBox.INFORMATION);
					return null;
				}
				
				//獲取首選項
				HashMap<String,String> map = getMapPreference(session, TCPreferenceService.TC_preference_site, preferenceName);
				if(map !=null && map.size() > 0) {
					String mProcessName = map.get(type);
					if(mProcessName!=null && !"".equals(mProcessName) ) {
						// TODO 校驗對象是否指定專案
						TCComponentIssueReportRevision itemRevision = (TCComponentIssueReportRevision) issueComponent;
						TCComponent[] components = itemRevision.getRelatedComponents("project_list");
						if(components != null && components.length > 0) {
							addStatusByWorkFlow(session, new TCComponent[] {issueComponent} , mProcessName);
							
							MessageBox.post("流程發起成功！","提示",MessageBox.INFORMATION);
							return null;
							
						} else {
							TCUtil.warningMsgBox(reg.getString("AssignProject.MSG"), reg.getString("WARNING.MSG"));
							return null;
						}
					} else {
						MessageBox.post("獲取流程失敗，首選項"+preferenceName+",中未匹配到"+type+"類型的流程！" ,"提示",MessageBox.INFORMATION);
						return null;
					}
				} else {
					MessageBox.post("獲取流程失敗，請檢查首選項"+preferenceName,"提示",MessageBox.INFORMATION);
					return null;
				}
				
				
			} else {
				MessageBox.post("請選擇Issue版本執行 流程發起","提示",MessageBox.INFORMATION);
				return null;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(),"提示",MessageBox.INFORMATION);
		} 
		
		return null;
	}
	
    
    public static String addStatusByWorkFlow(TCSession tcsession, TCComponent[] com, String mProcessName)
			throws TCException {
		String mProcess = "";
		if (com != null && com.length > 0) {
			TCComponentTaskTemplateType tcTaskTemType = (TCComponentTaskTemplateType) tcsession
					.getTypeComponent("EPMTaskTemplate");
			TCComponentTaskTemplate taskTem = tcTaskTemType.find(mProcessName, 0);
			TCComponentProcessType tcProcessType = (TCComponentProcessType) tcsession.getTypeComponent("Job");
			int[] var7 = new int[com.length];
			Arrays.fill(var7, 1);
			tcProcessType.create(mProcessName + ":" + com[0].getProperty("object_string"), mProcessName, taskTem, com, var7);
			mProcess = mProcessName + ":" + com[0].getProperty("object_string");
		}
		return mProcess;
	}
    
    public static HashMap<String, String> getMapPreference(TCSession session, int scope, String preferenceName) throws TCException
    {
    	HashMap<String, String> map = new HashMap<>();
        TCPreferenceService tCPreferenceService = session.getPreferenceService();
        tCPreferenceService.refresh();
        String[] array = tCPreferenceService.getStringArray(scope, preferenceName);
        TCComponentGroup currentGroup = session.getCurrentGroup();
		String full_name = currentGroup.getProperty("full_name");
        if(array!=null && array.length > 0) {
        	for (int i = 0; i < array.length; i++) {
        		String[] split = array[i].split("==");
        		if(split!=null &&split.length ==3) {
        			if(full_name.contains(split[1])) {
        				map.put(split[0], split[2]);
        			}
        		}
			}
        }
        
        return map;
    }
}