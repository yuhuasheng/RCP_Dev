package com.foxconn.electronics.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.tcutils.util.TCUtil;
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
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class MyIssuesListRefresh extends AbstractHandler {
	public AbstractAIFUIApplication app;
	public static TCSession session = null;
	
	public static String MyIssues = "我創建的 Issues";
	public static String Draft = "Draft";
	public static String InProgress = "In Progress";
	public static String Canceled = "Canceled";
	public static String Closed = "Closed";
	
	public static String ExecuteIssues = "我執行的 Issues";
	public static String TrackingIssues = "我跟蹤的 Issues";
	
	public static String ExternalTrackingIssues = "外部跟蹤";
	public static String InteriorTrackingIssues = "內部跟蹤";
	public static String ClosedTrackingIssues = "關閉Issue";
	
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		
		try {
			TCUtil.setBypass(session);
			InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
			if(targetComponents!=null && targetComponents.length != 1) {
				MessageBox.post("請選擇單個對象執行刷新Issue","提示",MessageBox.INFORMATION);
				return null;
			}
			
			TCComponent tcComponent = (TCComponent) targetComponents[0];
			String userID = tcComponent.getProperty("d9_IssueFolderOwner");
			String emp_no = tcComponent.getProperty("d9_IssueFolderActualUser");
			
//			if(userID != null && !"".equals(userID)) {
//				userID = "*("+userID+")";
//			}
//			
//			if(emp_no != null && !"".equals(emp_no)) {
//				emp_no = "*("+emp_no+")";
//			}
			
			String type = targetComponents[0].getType();
			if("D9_IssueHome".equals(type)) {
				TCComponent IssueHome = (TCComponent) targetComponents[0];
				TCComponent myIssuesFolder = null;
				TCComponent DraftFolder = null;
				TCComponent InProgressFolder = null;
				TCComponent CanceledFolder = null;
				TCComponent ClosedFolder = null;
				
				TCComponent executeIssuesFolder = null;
				
				TCComponent trackingIssuesFolder = null;
				TCComponent ExternalTrackingIssuesFolder = null;
				TCComponent InteriorTrackingIssuesFolder = null;
				TCComponent ClosedTrackingIssuesFolder = null;
				
				AIFComponentContext[] children = IssueHome.getChildren();
				for (AIFComponentContext aif :children) {
					TCComponent folder = (TCComponent) aif.getComponent();
					String object_name = folder.getProperty("object_name");
					
					if(MyIssuesListRefresh.MyIssues.equals(object_name)) {
						myIssuesFolder = folder;
						
						AIFComponentContext[] children_1 = myIssuesFolder.getChildren();
						for (AIFComponentContext aif_1 :children_1) {
							TCComponent folder_1 = (TCComponent) aif_1.getComponent();
							String object_name_1 = folder_1.getProperty("object_name");
							if(MyIssuesListRefresh.Draft.equals(object_name_1)) {
								DraftFolder = folder_1;
							} else if(MyIssuesListRefresh.InProgress.equals(object_name_1)) {
								InProgressFolder = folder_1;
							} else if(MyIssuesListRefresh.Canceled.equals(object_name_1)) {
								CanceledFolder = folder_1;
							} else if(MyIssuesListRefresh.Closed.equals(object_name_1)) {
								ClosedFolder = folder_1;
							}
						}
					} else if(MyIssuesListRefresh.ExecuteIssues.equals(object_name)) {
						executeIssuesFolder = folder;
					} else if(MyIssuesListRefresh.TrackingIssues.equals(object_name)) {
						trackingIssuesFolder = folder;
						
						AIFComponentContext[] children_1 = trackingIssuesFolder.getChildren();
						for (AIFComponentContext aif_1 :children_1) {
							TCComponent folder_1 = (TCComponent) aif_1.getComponent();
							String object_name_1 = folder_1.getProperty("object_name");
							if(MyIssuesListRefresh.ExternalTrackingIssues.equals(object_name_1)) {
								ExternalTrackingIssuesFolder = folder_1;
							} else if(MyIssuesListRefresh.InteriorTrackingIssues.equals(object_name_1)) {
								InteriorTrackingIssuesFolder = folder_1;
							} else if(MyIssuesListRefresh.ClosedTrackingIssues.equals(object_name_1)) {
								ClosedTrackingIssuesFolder = folder_1;
							} 
						}
						
					}
				}
				
				if(myIssuesFolder != null && DraftFolder != null 
					&& InProgressFolder != null && CanceledFolder != null && ClosedFolder != null) {
					getMyIssues(session, myIssuesFolder,DraftFolder,InProgressFolder,CanceledFolder,ClosedFolder, userID, emp_no);
				} else {
					MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
					return null;
				}
					
				if(executeIssuesFolder != null) {
					getExecuteIssues(session, executeIssuesFolder,userID, emp_no);
				} else {
					MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
					return null;
				}
				
				if(trackingIssuesFolder != null && ExternalTrackingIssuesFolder != null
					&& InteriorTrackingIssuesFolder != null && ClosedTrackingIssuesFolder != null) {
					//getTrackingIssues(session, trackingIssuesFolder, userID, emp_no);
					getTrackingIssues(session, trackingIssuesFolder, ExternalTrackingIssuesFolder, InteriorTrackingIssuesFolder, ClosedTrackingIssuesFolder, userID, emp_no);
				} else {
					MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
					return null;
				}
				
			} else if("D9_IssueSearch".equals(type)){
				String object_name = targetComponents[0].getProperty("object_name");
				
				if(MyIssuesListRefresh.Draft.equals(object_name)
					|| MyIssuesListRefresh.InProgress.equals(object_name)
					|| MyIssuesListRefresh.Canceled.equals(object_name)
					|| MyIssuesListRefresh.Closed.equals(object_name)
					) {
					
					TCComponent folder = (TCComponent) targetComponents[0];
					
					String[] properties = folder.getProperties(new String[] {"d9_IssueFolderOwner", "d9_IssueFolderActualUser"});
					AIFComponentContext[] myIssuesFolders = folder.whereReferencedByTypeRelation(new String[] { "D9_IssueSearch" }, null);
				    if ((myIssuesFolders != null) && myIssuesFolders.length > 0) {
				    	boolean b = true;
				    	
				    	for (int i = 0; i < myIssuesFolders.length; i++) {
				    		TCComponent DraftFolder = null;
							TCComponent InProgressFolder = null;
							TCComponent CanceledFolder = null;
							TCComponent ClosedFolder = null;
							
					    	TCComponent myIssuesFolder = (TCComponent) myIssuesFolders[i].getComponent();
					    	String[] properties1 = myIssuesFolder.getProperties(new String[] {"d9_IssueFolderOwner", "d9_IssueFolderActualUser"});
							if(!properties[0].equals(properties1[0]) || !properties[1].equals(properties1[1])) {
								break;
							}
					    	
					    	AIFComponentContext[] children_1 = myIssuesFolder.getChildren();
					    	
					    	for (AIFComponentContext aif_1 :children_1) {
					    		TCComponent folder_1 = (TCComponent) aif_1.getComponent();
					    		String object_name_1 = folder_1.getProperty("object_name");
					    		if(MyIssuesListRefresh.Draft.equals(object_name_1)) {
					    			DraftFolder = folder_1;
					    		} else if(MyIssuesListRefresh.InProgress.equals(object_name_1)) {
					    			InProgressFolder = folder_1;
					    		} else if(MyIssuesListRefresh.Canceled.equals(object_name_1)) {
					    			CanceledFolder = folder_1;
					    		} else if(MyIssuesListRefresh.Closed.equals(object_name_1)) {
					    			ClosedFolder = folder_1;
					    		}
					    	}
					    	
					    	if(myIssuesFolder != null && DraftFolder != null 
								&& InProgressFolder != null && CanceledFolder != null && ClosedFolder != null) {
								getMyIssues(session, myIssuesFolder,DraftFolder,InProgressFolder,CanceledFolder,ClosedFolder, userID, emp_no);
								
								b = false;
					    	}
					    	
						}
				    	if(b) {
				    		MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
							return null;
				    	}
				    		
				    }
				
				} else if(MyIssuesListRefresh.MyIssues.equals(object_name)) {
					TCComponent DraftFolder = null;
					TCComponent InProgressFolder = null;
					TCComponent CanceledFolder = null;
					TCComponent ClosedFolder = null;
					
					TCComponent myIssuesFolder = (TCComponent) targetComponents[0];
					
					
					AIFComponentContext[] children_1 = myIssuesFolder.getChildren();
					for (AIFComponentContext aif_1 :children_1) {
						TCComponent folder_1 = (TCComponent) aif_1.getComponent();
						String object_name_1 = folder_1.getProperty("object_name");
						if(MyIssuesListRefresh.Draft.equals(object_name_1)) {
							DraftFolder = folder_1;
						} else if(MyIssuesListRefresh.InProgress.equals(object_name_1)) {
							InProgressFolder = folder_1;
						} else if(MyIssuesListRefresh.Canceled.equals(object_name_1)) {
							CanceledFolder = folder_1;
						} else if(MyIssuesListRefresh.Closed.equals(object_name_1)) {
							ClosedFolder = folder_1;
						}
					}

					if(myIssuesFolder != null && DraftFolder != null 
						&& InProgressFolder != null && CanceledFolder != null && ClosedFolder != null) {
						getMyIssues(session, myIssuesFolder,DraftFolder,InProgressFolder,CanceledFolder,ClosedFolder, userID, emp_no);
					} else {
						MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
						return null;
					}
					
				} else if(MyIssuesListRefresh.ExecuteIssues.equals(object_name)) {
					TCComponent executeIssuesFolder = (TCComponent) targetComponents[0];
					getExecuteIssues(session, executeIssuesFolder,userID, emp_no);
					
				} else if(MyIssuesListRefresh.ExternalTrackingIssues.equals(object_name)
					|| MyIssuesListRefresh.InteriorTrackingIssues.equals(object_name)
					|| MyIssuesListRefresh.ClosedTrackingIssues.equals(object_name)
					) {
					
					
					TCComponent folder = (TCComponent) targetComponents[0];
					
					String[] properties = folder.getProperties(new String[] {"d9_IssueFolderOwner", "d9_IssueFolderActualUser"});
					AIFComponentContext[] issuesFolders = folder.whereReferencedByTypeRelation(new String[] { "D9_IssueSearch" }, null);
				    if ((issuesFolders != null) && issuesFolders.length > 0) {
				    	boolean b = true;
				    	
				    	for (int i = 0; i < issuesFolders.length; i++) {
				    		TCComponent ExternalTrackingIssuesFolder = null;
							TCComponent InteriorTrackingIssuesFolder = null;
							TCComponent ClosedTrackingIssuesFolder = null;
							
					    	TCComponent issuesFolder = (TCComponent) issuesFolders[i].getComponent();
					    	String[] properties1 = issuesFolder.getProperties(new String[] {"d9_IssueFolderOwner", "d9_IssueFolderActualUser"});
							if(!properties[0].equals(properties1[0]) || !properties[1].equals(properties1[1])) {
								break;
							}
					    	
					    	AIFComponentContext[] children_1 = issuesFolder.getChildren();
					    	
					    	for (AIFComponentContext aif_1 :children_1) {
					    		TCComponent folder_1 = (TCComponent) aif_1.getComponent();
					    		String object_name_1 = folder_1.getProperty("object_name");
					    		if(MyIssuesListRefresh.ExternalTrackingIssues.equals(object_name_1)) {
					    			ExternalTrackingIssuesFolder = folder_1;
					    		} else if(MyIssuesListRefresh.InteriorTrackingIssues.equals(object_name_1)) {
					    			InteriorTrackingIssuesFolder = folder_1;
					    		} else if(MyIssuesListRefresh.ClosedTrackingIssues.equals(object_name_1)) {
					    			ClosedTrackingIssuesFolder = folder_1;
					    		} 
					    	}
					    	
					    	if(issuesFolder != null && ExternalTrackingIssuesFolder != null 
								&& InteriorTrackingIssuesFolder != null && ClosedTrackingIssuesFolder != null ) {
					    		getTrackingIssues(session, issuesFolder,ExternalTrackingIssuesFolder,InteriorTrackingIssuesFolder,ClosedTrackingIssuesFolder, userID, emp_no);
								
								b = false;
					    	}
					    	
						}
				    	if(b) {
				    		MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
							return null;
				    	}
				    		
				    }
					
				} else if(MyIssuesListRefresh.TrackingIssues.equals(object_name)) {
					//TCComponent trackingIssuesFolder = (TCComponent) targetComponents[0];
					//getTrackingIssues(session, trackingIssuesFolder, userID, emp_no);
					
					TCComponent ExternalTrackingIssuesFolder = null;
					TCComponent InteriorTrackingIssuesFolder = null;
					TCComponent ClosedTrackingIssuesFolder = null;

					TCComponent trackingIssuesFolder = (TCComponent) targetComponents[0];
					
					AIFComponentContext[] children_1 = trackingIssuesFolder.getChildren();
					for (AIFComponentContext aif_1 :children_1) {
						TCComponent folder_1 = (TCComponent) aif_1.getComponent();
						String object_name_1 = folder_1.getProperty("object_name");
						if(MyIssuesListRefresh.ExternalTrackingIssues.equals(object_name_1)) {
							ExternalTrackingIssuesFolder = folder_1;
						} else if(MyIssuesListRefresh.InteriorTrackingIssues.equals(object_name_1)) {
							InteriorTrackingIssuesFolder = folder_1;
						} else if(MyIssuesListRefresh.ClosedTrackingIssues.equals(object_name_1)) {
							ClosedTrackingIssuesFolder = folder_1;
						} 
					}
					
				
					if(trackingIssuesFolder != null && ExternalTrackingIssuesFolder != null
						&& InteriorTrackingIssuesFolder != null && ClosedTrackingIssuesFolder != null) {
						//getTrackingIssues(session, trackingIssuesFolder, userID, emp_no);
						getTrackingIssues(session, trackingIssuesFolder, ExternalTrackingIssuesFolder, InteriorTrackingIssuesFolder, ClosedTrackingIssuesFolder, userID, emp_no);
					} else {
						MessageBox.post("Issue分揀管理文件夾不正確，不能刷新！","提示",MessageBox.INFORMATION);
						return null;
					}
					
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}
		
		return null;
	}
	
	

	/**
     * 創建 IssueFolder
     * 
     * @param session
     * @param type
     * @param name
     * @return
     */
    public static TCComponent createIssueFolder(TCSession session, String type, String name)
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
    
    //我创建的 Issues
    public static void getMyIssues(TCSession session, TCComponent myIssuesFolder, TCComponent draftFolder, TCComponent inProgressFolder, TCComponent canceledFolder, TCComponent closedFolder, String userId ,String emp_no) throws Exception {
    	myIssuesFolder.setRelated("contents", new TCComponent[] {});
    	draftFolder.setRelated("contents", new TCComponent[] {});
    	inProgressFolder.setRelated("contents", new TCComponent[] {});
    	canceledFolder.setRelated("contents", new TCComponent[] {});
    	closedFolder.setRelated("contents", new TCComponent[] {});

    	myIssuesFolder.add("contents", draftFolder);
		myIssuesFolder.add("contents", inProgressFolder);
		myIssuesFolder.add("contents", canceledFolder);
		myIssuesFolder.add("contents", closedFolder);
    	
    	TCComponent[] draft = TCUtil.executeQuery(session, "D9_find_my_issue_reports_Draft",
			new String[] {"owning_user.user_id","d9_ActualUserID" }, new String[] {userId,"*("+emp_no+")*"});
    	if (draft != null && draft.length > 0) {
    		ArrayList<TCComponent> draftList = new ArrayList<TCComponent>(Arrays.asList(draft));
    		draftList.sort(comparator);
    		draftFolder.setRelated("contents", draftList.toArray(new TCComponent[] {}));
    	}
    	
    	TCComponent[] inProgress = TCUtil.executeQuery(session, "D9_find_my_issue_reports_InProgress",
			new String[] {"d9_ActualUserID","owning_user.user_id","d9_ActualUserID","owning_user.user_id" }, 
			new String[] {"*("+emp_no+")*",userId,"*("+emp_no+")*",userId});
    	if (inProgress != null && inProgress.length > 0) {
    		ArrayList<TCComponent> inProgressList = new ArrayList<TCComponent>(Arrays.asList(inProgress));
    		inProgressList.sort(comparator);
    		inProgressFolder.setRelated("contents", inProgressList.toArray(new TCComponent[] {}));
    	}
		
    	TCComponent[] canceled = TCUtil.executeQuery(session, "D9_find_my_issue_reports_Canceled",
			new String[] {"owning_user.user_id","d9_ActualUserID" }, new String[] {userId,"*("+emp_no+")*"});
    	if (canceled != null && canceled.length > 0) {
    		ArrayList<TCComponent> canceledList = new ArrayList<TCComponent>(Arrays.asList(canceled));
    		canceledList.sort(comparator);
    		canceledFolder.setRelated("contents", canceledList.toArray(new TCComponent[] {}));
    	}
		
    	TCComponent[] closed = TCUtil.executeQuery(session, "D9_find_my_issue_reports_Closed",
			new String[] {"owning_user.user_id","d9_ActualUserID" }, new String[] {userId,"*("+emp_no+")*"});
    	if (closed != null && closed.length > 0) {
    		ArrayList<TCComponent> closedList = new ArrayList<TCComponent>(Arrays.asList(closed));
    		closedList.sort(comparator);
    		closedFolder.setRelated("contents", closedList.toArray(new TCComponent[] {}));
    	}
		
    }
    
    
    //要执行的 Issues
    public static void getExecuteIssues(TCSession session, TCComponent executeIssuesFolder, String userId ,String emp_no) throws Exception{
		TCComponent[] perform = TCUtil.executeQuery(session, "D9_find_my_perform_issues",new String[] {"EPMTask:process_stage_list.fnd0StartedTasks.Signoff:Fnd0EPMSignoff.group_member.User:user.user_id","d9_IRCurrentTaskActualUser"},new String[] {userId, "*("+emp_no+")*"});
		if (perform != null && perform.length > 0) {
    		ArrayList<TCComponent> performList = new ArrayList<TCComponent>(Arrays.asList(perform));
    		performList.sort(comparator);
    		executeIssuesFolder.setRelated("contents", performList.toArray(new TCComponent[] {}));
    	} else {
    		executeIssuesFolder.setRelated("contents", new TCComponent[] {});
    	}
    }
    
    //要跟踪的 Issues
    public static void getTrackingIssues(TCSession session, TCComponent trackingIssuesFolder, TCComponent externalTrackingIssuesFolder, TCComponent interiorTrackingIssuesFolder, TCComponent closedTrackingIssuesFolder, String userId, String emp_no) throws Exception {
    	trackingIssuesFolder.setRelated("contents", new TCComponent[] {});
    	externalTrackingIssuesFolder.setRelated("contents", new TCComponent[] {});
    	interiorTrackingIssuesFolder.setRelated("contents", new TCComponent[] {});
    	closedTrackingIssuesFolder.setRelated("contents", new TCComponent[] {});

    	trackingIssuesFolder.add("contents", externalTrackingIssuesFolder);
    	trackingIssuesFolder.add("contents", interiorTrackingIssuesFolder);
    	trackingIssuesFolder.add("contents", closedTrackingIssuesFolder);
    	
    	TCComponent[] external = TCUtil.executeQuery(session, "D9_find_my_track_issues_external",
			new String[] {
					"D9_IssueTL:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTL_ActualUser.item_id",
					"D9_IssueFixOwner:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueFixOwner_ActualUser.item_id",
					"D9_IssueTester:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTester_ActualUser.item_id"
					}, 
			new String[] {userId,emp_no,userId,emp_no,userId,emp_no});
    	if (external != null && external.length > 0) {
    		ArrayList<TCComponent> list = new ArrayList<TCComponent>(Arrays.asList(external));
    		list.sort(comparator);
    		externalTrackingIssuesFolder.setRelated("contents", list.toArray(new TCComponent[] {}));
    	}
    	
    	TCComponent[] internal = TCUtil.query(session, "D9_find_my_track_issues_internal",
			new String[] {
					"IssueTL1","IssueTL_ActualUserID1",
					"IssueTL2","IssueTL_ActualUserID2",
					"IssueFixOwner1","IssueFixOwner_ActualUserID1",
					"IssueFixOwner2","IssueFixOwner_ActualUserID2",
					"IssueTester1","IssueTester_ActualUserID1",
					"IssueTester2","IssueTester_ActualUserID2"
					}, 
			new String[] {userId,emp_no,userId,emp_no,userId,emp_no,userId,emp_no,userId,emp_no,userId,emp_no});
    	
//    	TCComponent[] internal = TCUtil.executeQuery(session, "D9_find_my_track_issues_internal",
//    	new String[] {
//				"D9_IssueTL:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTL_ActualUser.item_id",
//				"D9_IssueTL:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTL_ActualUser.item_id",
//				"D9_IssueFixOwner:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueFixOwner_ActualUser.item_id",
//				"D9_IssueFixOwner:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueFixOwner_ActualUser.item_id",
//				"D9_IssueTester:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTester_ActualUser.item_id",
//				"D9_IssueTester:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTester_ActualUser.item_id"
//				}, 
//		new String[] {userId,emp_no,userId,emp_no,userId,emp_no,userId,emp_no,userId,emp_no,userId,emp_no});
    	if (internal != null && internal.length > 0) {
    		ArrayList<TCComponent> list = new ArrayList<TCComponent>(Arrays.asList(internal));
    		list.sort(comparator);
    		interiorTrackingIssuesFolder.setRelated("contents", list.toArray(new TCComponent[] {}));
    	}
    	
    	TCComponent[] closed = TCUtil.executeQuery(session, "D9_find_my_track_issues_closed",
			new String[] {
					"D9_IssueTL:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTL_ActualUser.item_id",
					"D9_IssueFixOwner:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueFixOwner_ActualUser.item_id",
					"D9_IssueTester:HasParticipant.GroupMember:assignee.User:user.user_id","d9_IssueTester_ActualUser.item_id"
					}, 
			new String[] {userId,emp_no,userId,emp_no,userId,emp_no});
    	if (closed != null && closed.length > 0) {
    		ArrayList<TCComponent> list = new ArrayList<TCComponent>(Arrays.asList(closed));
    		list.sort(comparator);
    		closedTrackingIssuesFolder.setRelated("contents", list.toArray(new TCComponent[] {}));
    	}
		
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
			tcProcessType.create(mProcessName + ":" + com[0].getProperty("object_string"), mProcessName, taskTem, com,
					var7);
			mProcess = mProcessName + ":" + com[0].getProperty("object_string");
		}
		return mProcess;
	}
    
   public static Comparator<TCComponent> comparator = new Comparator<TCComponent>() {

		@Override
		public int compare(TCComponent o1, TCComponent o2) {
			// TODO Auto-generated method stub
			try {
				String property1 = o1.getProperty("item_id");
				String property2 = o2.getProperty("item_id");
				
				return property2.compareTo(property1);
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
	};
    
}