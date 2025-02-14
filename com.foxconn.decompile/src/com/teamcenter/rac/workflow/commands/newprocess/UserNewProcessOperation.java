package com.teamcenter.rac.workflow.commands.newprocess;

import com.foxconn.decompile.service.DesignMailService;
import com.foxconn.decompile.service.SecondSourceService;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.common.TCTree;
import com.teamcenter.rac.common.TCTreeNode;
import com.teamcenter.rac.common.tcviewer.TCViewerPanel;
import com.teamcenter.rac.kernel.ResourceMember;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PlatformHelper;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.workflow.common.AbstractProcessApplicationPanel;
import com.teamcenter.rac.workflow.processviewer.IProcessViewer;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2007_01.DataManagement;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;


public class UserNewProcessOperation extends AbstractAIFOperation {
	protected TCComponent[] attComps;

	protected int[] attTypes;

	protected String processName;

	protected String processDescription;

	protected TCComponentTaskTemplate processTemplate;

	protected TCComponent newProcess = null;

	protected AIFDesktop desktop = null;

	protected boolean successFlag = true;

	protected ResourceMember[] selResourceList = null;

	protected TCComponent creatorTask = null;
	

	AbstractProcessDialog dlg = null;
     private TCTree processTreeView;
	public UserNewProcessOperation(TCTree processTreeView, AbstractProcessDialog paramAbstractProcessDialog) {
		this.processTreeView=processTreeView;
		this.attComps = paramAbstractProcessDialog.getAttachmentComponents();
		this.attTypes = paramAbstractProcessDialog.getAttachmentTypes();
		this.processName = paramAbstractProcessDialog.getProcessName();
		this.processDescription = paramAbstractProcessDialog.getProcessDescription();
		this.processTemplate = (TCComponentTaskTemplate) paramAbstractProcessDialog.getProcessTemplate();							
		this.dlg = paramAbstractProcessDialog;
		this.desktop = paramAbstractProcessDialog.desktop;
		this.selResourceList = paramAbstractProcessDialog.getSelectedResources();
		this.creatorTask = paramAbstractProcessDialog.getCreatorTask();
	}

	public UserNewProcessOperation(TCSession paramTCSession, AIFDesktop paramAIFDesktop, String paramString1,
			String paramString2, TCComponentTaskTemplate paramTCComponentTaskTemplate,
			TCComponent[] paramArrayOfTCComponent, int[] paramArrayOfInt) {
		this.attComps = paramArrayOfTCComponent;
		this.attTypes = paramArrayOfInt;
		this.processName = paramString1;
		this.processDescription = paramString2;
		this.processTemplate = paramTCComponentTaskTemplate;
		this.desktop = paramAIFDesktop;
	}
	
	/**
	 * 20230810 
	 * 群组对象子阶商用零件以下属性检查
	 * 检查流程：FXN27_MNT 2nd Source Recommended Process, FXN29_PRT 2nd Source Review Process
	 * @param session 
	 * @throws Exception
	 */
	public void checkMGItemBOM(TCSession session) throws Exception{
		InterfaceAIFComponent[] tmps=this.attComps;
		
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < tmps.length; i++) {
			if(tmps[i] instanceof TCComponentItemRevision) {
				TCComponentItemRevision itemRevision = (TCComponentItemRevision)tmps[i];
				
				String type = itemRevision.getType();
				if("D9_MaterialGroupRevision".equals(type)) {
					TCComponentBOMWindow bomwindow = TCUtil.createBOMWindow(session);
					TCComponentBOMLine bomline = TCUtil.getTopBomline(bomwindow, itemRevision);
					
					AIFComponentContext[] childrens = bomline.getChildren();
					if (childrens.length > 0) {
						for (int j = 0; j < childrens.length; j++) {
							TCComponentBOMLine children = (TCComponentBOMLine) childrens[j].getComponent();
							
							String item_id = children.getProperty("bl_item_item_id");
							String item_revison_id = children.getProperty("bl_rev_item_revision_id");
							
							TCProperty tcProperty = children.getTCProperty("bl_rev_d9_EnglishDescription");
							String desc = tcProperty.getStringValue();
							TCPropertyDescriptor propertyDescriptor = tcProperty.getPropertyDescriptor();
							String descDisplay = propertyDescriptor.getDisplayName();
							
							TCProperty tcProperty1 = children.getTCProperty("bl_Part Revision_d9_ManufacturerID");
							String id = tcProperty1.getStringValue();
							TCPropertyDescriptor propertyDescriptor1 = tcProperty1.getPropertyDescriptor();
							String idDisplay = propertyDescriptor1.getDisplayName();
							
							TCProperty tcProperty2 = children.getTCProperty("bl_Part Revision_d9_ManufacturerPN");
							String pn = tcProperty2.getStringValue();
							TCPropertyDescriptor propertyDescriptor2 = tcProperty2.getPropertyDescriptor();
							String pnDisplay = propertyDescriptor2.getDisplayName();
							
							String errorString = "";
							if("".equals(desc)) {
								errorString = errorString+descDisplay+", ";
							}
							if("".equals(id)) {
								errorString = errorString+idDisplay+", ";
							}
							if("".equals(pn)) {
								errorString = errorString+pnDisplay+", ";
							}
							
							if(!"".equals(errorString)) {
								stringBuilder.append("群组料件"+item_id+"/"+item_revison_id+"的 ："+errorString+" 属性为空！\r\n");
							}
						}
					}
					
					bomwindow.close();
				}
				
				
				if(!"".equals(stringBuilder.toString())) {
					//MessageBox.post(AIFUtility.getActiveDesktop(), stringBuilder.toString(), "Error", MessageBox.ERROR);
					throw new TCException("流程不满足发起条件, 请检查：\r\n"+stringBuilder.toString());						
				}
			}
		}
	}

	public void executeOperation() throws Exception {
		TCSession tcsession = (TCSession) getSession();
		try {
			// 20230810 : 群组对象子阶商用零件以下属性检查
			if(processName != null && (processName.startsWith("FXN27_MNT 2nd Source Recommended Process") || processName.startsWith("FXN29_PRT 2nd Source Review Process"))) {
				checkMGItemBOM(tcsession);
			}

			// recompile 20220323143000 : START
			if (((UserExtNewProcessDialog)dlg).m_userInfoLst != null && ((UserExtNewProcessDialog)dlg).m_userInfoLst.size() > 0) {								
				throw new TCException(((UserExtNewProcessDialog)dlg).m_userInfoLst.stream().collect(Collectors.joining("\n")));
			}
			
			boolean sendMailFlag = false;
			String startNode = null;
			List<TCComponent> startNodeLst = new ArrayList<TCComponent>();
			boolean _blnPublicMail = ((UserExtNewProcessDialog)dlg)._blnPublicMail;
			System.out.println("_blnPublicMail = "+_blnPublicMail);
			
			
			if (((UserExtNewProcessDialog)dlg)._blnPublicMail) {
				TCComponentTaskTemplate tcProcessTemplate = processTemplate;
				if (tcProcessTemplate != null) {
					boolean isAction = ((UserExtNewProcessDialog)dlg).publicAccountPanel.teamRosterPanel.isAction;
					if(isAction) {
						((UserExtNewProcessDialog)dlg).iniPubMailPanel();
					}
					
					boolean blnFlag = false;
					startNode = tcProcessTemplate.getProperty("start_successors");
					Map<String, Map<String, List<String>>> wkInfoMap = ((UserExtNewProcessDialog)dlg).getWKInfo();
					boolean blnIsExistNode = ((UserExtNewProcessDialog)dlg).checkIsExistNode(wkInfoMap, Arrays.asList(startNode.split(",")));
					if (!blnIsExistNode) {
						List<String> nextNodeLst = new ArrayList<String>();
						((UserExtNewProcessDialog)dlg).getNextNode(wkInfoMap, tcProcessTemplate.getStartSuccessors(), nextNodeLst);
						startNode = nextNodeLst.stream().collect(Collectors.joining(","));
					}
					
					// get start task list					
					List<String> newstartNodeLst = new ArrayList<String>();
//					List<TCComponent> startNodeLst = new ArrayList<TCComponent>();
					if (!TCUtil.isNull(startNode)) {
						((UserExtNewProcessDialog)dlg).getStartNode(Arrays.asList(startNode.split(",")), startNodeLst);
						for (TCComponent tcCompStartNode : startNodeLst) {
							String taskType = tcCompStartNode.getProperty("task_type");
							if ("Validate Task Template".equals(taskType) || "驗證任務範本".equals(taskType) || "验证任务模板".equals(taskType)) {						
//								TCComponent[] pasteTargetsArr = (TCComponent[])Arrays.asList(((UserExtNewProcessDialog)dlg).pasteTargets).stream().map(e -> (TCComponent)e).collect(Collectors.toList()).toArray();						
								TCComponent[] pasteTargetsArr = new TCComponent[((UserExtNewProcessDialog)dlg).pasteTargets.length];
								for (int i=0; i < pasteTargetsArr.length; i++) {
									TCComponent targetComp = (TCComponent) ((UserExtNewProcessDialog)dlg).pasteTargets[i];
									pasteTargetsArr[i] = targetComp;
								}
									
								TCComponent tcCompNextNode = ((UserExtNewProcessDialog)dlg).getNextNodeByValidateTask(tcCompStartNode, pasteTargetsArr);
								if (tcCompNextNode != null)
									newstartNodeLst.add(tcCompNextNode.getProperty("object_name"));
							} else {
								newstartNodeLst.add(tcCompStartNode.getProperty("object_name"));
							}					
						}	
					}					
					
					if (newstartNodeLst != null && newstartNodeLst.size() > 0)
						startNode = newstartNodeLst.stream().collect(Collectors.joining(","));		
					
					blnFlag = ((UserExtNewProcessDialog)dlg).checkPublicMailInfo(Arrays.asList(startNode.split(",")));																								
					if (!blnFlag) {
						int option = javax.swing.JOptionPane.showConfirmDialog(null, "未分配實際工作者郵箱,請到SPAS系統維護人員郵箱,是否繼續?", "提示", javax.swing.JOptionPane.YES_NO_OPTION);
						if (option == javax.swing.JOptionPane.NO_OPTION) {
							throw new TCException("請重新指派人員");						
						}
					}
					((UserExtNewProcessDialog)dlg).savePublicMailInfo();	
					
					sendMailFlag = true;
				}	
			}			
			// 20220323143000 : END
			if(processName!=null&&is2ndSource(processName)) {				  
				    if(attComps!=null&&attComps.length>0) {
				     new SecondSourceService().genChangeList(tcsession, attComps);   
				    }
			}
			List<TCComponent> mailAtts=new ArrayList<TCComponent>();
			if(processName.contains("FXN40")||processName.contains("FXN39")) {
				TCTreeNode tCTreeNode = (TCTreeNode) this.processTreeView.getRootNode();
				int i = tCTreeNode.getChildCount();
				TCComponentTaskTemplate tCComponentTaskTemplate=null;
				for (int b = 0; b < i; b++) {
					TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);				
					TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();	
					String name= task.getName();
				    if("修改组件".equalsIgnoreCase(name)) {
				    	tCComponentTaskTemplate=task;
				    }
				}
				
				HashMap<String,Object> resp=new DesignMailService().updateMailInfo(tCComponentTaskTemplate,tcsession, attComps, attTypes, selResourceList);
				
				if(resp!=null) {
			    	List<TCComponent> atts=(List<TCComponent>)resp.get("atts");
			    	Vector res=(Vector)resp.get("res");
			    	if(atts!=null&&atts.size()>0) {
			    		TCComponent[] attComps2=new TCComponent[attComps.length+atts.size()];
			    		Integer[] attTypes2=new Integer[attTypes.length+atts.size()];
			    		Vector v1=new Vector();
			    		Vector v2=new Vector();
			    		for(int k=0;k<attComps.length;k++) {
			    			v1.add(attComps[k]);
			    			v2.add(attTypes[k]);
			    			if(attTypes[k]==1 &&(attComps[k] instanceof TCComponentItemRevision)) {
			    			   mailAtts.add(attComps[k]);
			    			}
			    		}			    		
			    		for(TCComponent t:atts) {
			    			v1.add(t);
			    			v2.add(3);
			    		}
			    		v1.toArray(attComps2);
			    		v2.toArray(attTypes2);
			    		attComps=attComps2;
			    		int[] attTypes3=new int[attTypes2.length];
			    		for(int k=0;k<attTypes2.length;k++) {
			    			attTypes3[k]=attTypes2[k];
			    		}
			    		attTypes=attTypes3;
			    	}
			    	if(res!=null&&res.size()>0) {
			    		int cnt=0;
				    	if(selResourceList!=null&&selResourceList.length>0) {
				    		cnt=selResourceList.length;
				    	}
				    	ResourceMember[] selResourceList2= new ResourceMember[cnt+res.size()];
				    	Vector v=new Vector();
				    	for(int k=0;k<selResourceList.length;k++) {
				    		v.add(selResourceList[k]);
				    	}
				    	
				    	for(Object r:res) {
				    		v.add(r);
				    	}
				    	v.toArray(selResourceList2);
				    	selResourceList=selResourceList2;
			    	}	
			    }
			   
			}
			
			
			
			
			createNewProcess(tcsession, processName, processDescription, processTemplate, attComps, attTypes);									
			
			if (sendMailFlag) { // 判断是否需要发送邮件的标识
				Registry registry = Registry.getRegistry(this);
				String[] mailContentArr = new String[12];
				mailContentArr[0] = registry.getString("mailConent1");
				mailContentArr[1] = registry.getString("mailConent2");
				mailContentArr[2] = registry.getString("mailConent3");
				mailContentArr[3] = registry.getString("mailConent4");
				mailContentArr[4] = registry.getString("mailConent5");
				mailContentArr[5] = registry.getString("mailConent6");
				mailContentArr[6] = registry.getString("mailConent7");
				mailContentArr[7] = registry.getString("mailConent8");
				mailContentArr[8] = registry.getString("mailConent9");
				mailContentArr[9] = registry.getString("mailConent10");
				mailContentArr[10] = registry.getString("mailConent11");
				mailContentArr[11] = registry.getString("mailConent12");
				
				String[] notifyContentArr = new String[12];
				notifyContentArr[0] = registry.getString("mailConent1.notify");
				notifyContentArr[1] = registry.getString("mailConent2.notify");
				notifyContentArr[2] = registry.getString("mailConent3.notify");
				notifyContentArr[3] = registry.getString("mailConent4.notify");
				notifyContentArr[4] = registry.getString("mailConent5.notify");
				notifyContentArr[5] = registry.getString("mailConent6.notify");
				notifyContentArr[6] = registry.getString("mailConent7.notify");
				notifyContentArr[7] = registry.getString("mailConent8.notify");
				notifyContentArr[8] = registry.getString("mailConent9.notify");
				notifyContentArr[9] = registry.getString("mailConent10.notify");
				notifyContentArr[10] = registry.getString("mailConent11.notify");
				notifyContentArr[11] = registry.getString("mailConent12.notify");
				
				String[] finishContentArr = new String[9];
				finishContentArr[0] = registry.getString("mailConent1.finish");
				finishContentArr[1] = registry.getString("mailConent2.finish");
				finishContentArr[2] = registry.getString("mailConent3.finish");
				finishContentArr[3] = registry.getString("mailConent4.finish");
				finishContentArr[4] = registry.getString("mailConent5.finish");
				finishContentArr[5] = registry.getString("mailConent6.finish");
				finishContentArr[6] = registry.getString("mailConent7.finish");
				finishContentArr[7] = registry.getString("mailConent8.finish");
				finishContentArr[8] = registry.getString("mailConent9.finish");
				
				Map<String, String> startNodeMap = new LinkedHashMap<String, String>();
				List<String> startNodeNameLst = Arrays.asList(startNode.split(","));
				for (String startNodeName : startNodeNameLst) {
					startNodeMap.put(startNodeName, "");
				}
				new Thread(new Runnable() {
					public void run() {
						TCUtil.sendMailForApprove(
								registry.getString("actionLog.title"), 
								mailContentArr, 
								notifyContentArr,
								finishContentArr,
								startNodeMap, 
								startNodeLst,
								Arrays.asList(((UserExtNewProcessDialog)dlg).getProjectName().split(",")), 
								Arrays.asList(((UserExtNewProcessDialog)dlg).getItemName().split(",")), 
								Arrays.asList(((UserExtNewProcessDialog)dlg).getItemId().split(",")), 
								Arrays.asList(((UserExtNewProcessDialog)dlg).getItemUid().split(",")), 
								((UserExtNewProcessDialog)dlg).m_pubMailMap);
					}
				}).start();	
			}
			
			
			if(processName.contains("FXN40")) {	
			    if(mailAtts.size()>0) {
			    	TCComponent[] cs=new TCComponent[mailAtts.size()];
					new DesignMailService().sendMail(tcsession, mailAtts.toArray(cs));
			    }
			}
			for (int i = 0; attComps != null && i < attComps.length; i++) {
				TCComponent tccomponent = attComps[i];
				if (tccomponent != null) {
					tccomponent.refresh();
				}
			}
			
			// recompile 20220323143000 : START
//			DocAutoAchieve(tcsession);	
			// 20220323143000 : END

		} catch (TCException tcexception) {
			MessageBox.post(desktop, tcexception);
			int ai[] = tcexception.getErrorCodes();
			int j = ai != null ? ai[0] : 0;
			successFlag = j == 33086;
			throw tcexception;
		}		
	}
	
	private boolean is2ndSource(String processName){
		TCSession tcsession = (TCSession) getSession();
		String[]  ws= TCUtil.getArrayByPreference(tcsession, TCPreferenceService.TC_preference_site, "D9_2ndSource_Workflow");
		if(ws==null||ws.length<=0) {
			return false;
		}
		for(String w:ws) {
			if(processName.startsWith(w)) {
				return true;
			}
		}
		
		return false;
	}

	public void createNewProcess(TCSession paramTCSession, String paramString1, String paramString2,
			TCComponentTaskTemplate paramTCComponentTaskTemplate, TCComponent[] paramArrayOfTCComponent,
			int[] paramArrayOfInt) throws TCException {
		Registry localRegistry = Registry.getRegistry(this);
		paramTCSession.setStatus(localRegistry.getString("creatingNewProcess") + " " + "...");
		if (isAbortRequested()) {
			paramTCSession.setStatus(localRegistry.getString("abort"));
			return;
		}
		if (paramString1.length() == 0) {
			paramTCSession.setStatus(localRegistry.getString("noProcessName"));
			return;
		}
		if (paramTCComponentTaskTemplate == null) {
			paramTCSession.setStatus(localRegistry.getString("noProcessDefName"));
			return;
		}
		try {
			TCComponentProcessType localTCComponentProcessType = (TCComponentProcessType) paramTCSession.getTypeComponent("Job");
			if (paramArrayOfTCComponent == null) {
				if ((this.selResourceList != null) && (this.selResourceList.length > 0))
					this.newProcess = localTCComponentProcessType.create(paramString1, paramString2, paramTCComponentTaskTemplate, null, null, this.selResourceList);
				else
					this.newProcess = localTCComponentProcessType.create(paramString1, paramString2, paramTCComponentTaskTemplate);
			} else if ((this.selResourceList != null) && (this.selResourceList.length > 0))
				this.newProcess = localTCComponentProcessType.create(paramString1, paramString2, paramTCComponentTaskTemplate, paramArrayOfTCComponent, paramArrayOfInt, this.selResourceList);
			else
				this.newProcess = localTCComponentProcessType.create(paramString1, paramString2, paramTCComponentTaskTemplate, paramArrayOfTCComponent, paramArrayOfInt);			
			paramTCSession.setReadyStatus();
		} catch (TCException localTCException) {
			paramTCSession.setStatus(localTCException.toString());
			throw localTCException;
		}
	}

	public boolean getSuccessFlag() {
		return this.successFlag;
	}

	public void cleanUp() {
		try {
			if (Debug.isOn("NEWPROCESSUI")) {
				Debug.println("------------------------------------------------");
				Debug.println("====> Before deleting the process in cleanUp() ...");
				Debug.println("------------------------------------------------");
			}
			this.newProcess.delete();
			if (Debug.isOn("NEWPROCESSUI")) {
				Debug.println("------------------------------------------------");
				Debug.println("====> After deleting the process in cleanUp() ...");
				Debug.println("------------------------------------------------");
			}
		} catch (TCException tCException) {
			Debug.printStackTrace("NEWPROCESSUI", tCException);
		}
	}
	
	// recompile 20220323143000 : START
	private void DocAutoAchieve(TCSession tcsession) {
		try {
			List<String> workflowPreLst = Arrays.asList(TCUtil.getArrayByPreference(tcsession, TCPreferenceService.TC_preference_site, "D9_Design_Archive_Workflow"));			
			if (workflowPreLst.contains(processTemplate.getName())) {
				if (attComps != null && attComps.length > 0) {
					TCUtil.setBypass(tcsession);
					// 获取首选项
    				Map<String, String> folderPreMap = TCUtil.getHashMapPreference(tcsession, TCPreferenceService.TC_preference_site, "D9_Design_Archive_Rule", "=");
//    				TCComponentUser currentUser = RACUIUtil.getTCSession().getUser();
    				    		
//    				TCComponentUser user = null;
    				List<TCComponent> releaseComLst = new ArrayList<TCComponent>();
    				for (TCComponent tcComp : attComps) {
    					if(tcComp instanceof TCComponentItemRevision){
    						TCComponentItemRevision itemRev = (TCComponentItemRevision)tcComp;
    						if (null == itemRev) {
    							continue;
    						}
    						
//    						user = (TCComponentUser)itemRev.getTCProperty("owning_user").getReferenceValue();
//    						if (currentUser.getUid().equals(user.getUid()) && !TCUtil.isReleased(itemRev)) {
//    							releaseComLst.add(tcComp);
//    						}
    						
    						releaseComLst.add(tcComp);

    						TCComponentBOMLine rootLine = TCUtil.openBomWindow(tcsession, itemRev);
    						if (rootLine != null) {
    							List<TCComponentBOMLine> bomLineLst = TCUtil.getTCComponmentBOMLines(rootLine, null, false);
    							for (TCComponentBOMLine bomLine : bomLineLst) {
    								TCComponentItemRevision bomLineItemRev = bomLine.getItemRevision();
//									user = (TCComponentUser)bomLineItemRev.getTCProperty("owning_user").getReferenceValue();
//									if (currentUser.getUid().equals(user.getUid()) && (!TCUtil.isReleased(bomLineItemRev) || !"".equals(bomLine.getProperty("bl_rev_release_status_list")))) {
//										releaseComLst.add(bomLineItemRev);
//									}
    								
    								if (!TCUtil.isReleased(bomLineItemRev) || !"".equals(bomLine.getProperty("bl_rev_release_status_list"))) {
										releaseComLst.add(bomLineItemRev);
									}
								}
    						}
    					}
    				}
    				
					for (TCComponent tcComp : releaseComLst) {		
    					if(tcComp instanceof TCComponentItemRevision){
    						TCComponentItemRevision itemRev = (TCComponentItemRevision)tcComp;
    						if (null == itemRev) {
    							continue;
    						}    						    						
    						
    						// 获取相应的文件夹名称
    						String objectName = itemRev.getProperty("item_id");
    						String[] nameArray = objectName.split("-");
    						if (nameArray.length < 2) {
    							continue;
    						}
    						String findName = nameArray[0]+"-"+nameArray[1];
    						if (!folderPreMap.containsKey(findName)) {
    							continue;
    						}
    						String folderName = folderPreMap.get(findName);
    						if ("".equals(folderName)) {
    							continue;
    						}
    						
    						// 查找文件夹
    						String[] keys = new String[] { TCUtil.getTextValue("Type"), TCUtil.getTextValue("Description")};
    						String[] values = new String[] { "D9_WorkAreaFolder", folderName};
    						List<InterfaceAIFComponent> folderLst = TCUtil.search("General...", keys, values);
    						
							for (TCComponent prjComp : TCUtil.getProjects(itemRev)) {
								//获取项目信息
	    						TCComponentProject prjOfItemRev = (TCComponentProject)prjComp;
	    						if (null == prjOfItemRev) {
	    							continue;
	    						}
	    						TCProperty propOfItemRev = prjOfItemRev.getTCProperty("project_id");
	    						String proIdOfItemRev = propOfItemRev.getStringValue();
	    						
	    						if(folderLst != null && folderLst.size()>0) {
	    							TCComponentFolder folder = null;
	    							for (InterfaceAIFComponent folderComponent : folderLst) {
	    								folder = (TCComponentFolder)folderComponent;
	    								// 获取项目信息
	    								List<TCComponent> prjOfFolderLst = TCUtil.getProjects(folder);
	    								if (prjOfFolderLst.size() != 1) {
	    									continue;
	    								}
	            						TCComponentProject prjOfFolder = (TCComponentProject)prjOfFolderLst.get(0);  
	            						if (null == prjOfFolder) {
	            							continue;
	            						}
	            						TCProperty propOfFolder = prjOfFolder.getTCProperty("project_id");
	            						String proIdOfFolder = propOfFolder.getStringValue();
	            						if (proIdOfItemRev.equals(proIdOfFolder)) {
	            							TCComponent item = TCUtil.getItem(tcComp);
	            							if (item != null) {
	                							if (!TCUtil.isExistItem(folder, item, "contents")) {	                								
		                							folder.add("contents", item);		                							
	                							}
	            							}
	            							folder.refresh();
	            						}                						            							
	    							}        							
	    						}
							}    						    						    						
    					}
        				      			
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	    	TCUtil.closeBypass(tcsession);
	    }
	}
	// 20220323143000 : END
	
}
