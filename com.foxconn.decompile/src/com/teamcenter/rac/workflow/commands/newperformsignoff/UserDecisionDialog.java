package com.teamcenter.rac.workflow.commands.newperformsignoff;

import com.foxconn.decompile.service.CustomPnService;
import com.foxconn.decompile.service.DellEbomExportService;
import com.foxconn.decompile.service.DesignMailService;
import com.foxconn.decompile.service.SecondSourceService;
import com.foxconn.decompile.service.DocMailGroupService;
import com.foxconn.decompile.service.HpEbomExportService;
import com.foxconn.decompile.util.CommonTools;
import com.foxconn.decompile.util.FileStreamUtil;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCCRDecision;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentPerson;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProfile;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentRule;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class UserDecisionDialog extends DecisionDialog {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private boolean D9_SendTechDocMail=false;
    private boolean post_custom_pn=false;
	private boolean apply_custom_pn = false;
	private String  apply_custom_pn_exclude="";
	private boolean d9_2ndSourceChangeList=false;
	private boolean cust_check_conflict_part = false;
	private boolean cust_generate_changelist = false;
	private boolean Cust_Add_SubEDA = false;
	private boolean D9_HPEBOMExport = false;
	private boolean D9_DellEBOMExport = false;
	private ArrayList<TCComponentBOMLine> list = new ArrayList();
	private Connection conn = null;
	private Statement stmt = null;
	private JPanel contentPanel;
	private JPanel mainPanel;
	private TCComponentBOMWindow imancomponentbomwindow;
	
	// recompile 20220323143000 : START
//	private JTextField m_singoffTF;
	List<String> m_projectLst = new ArrayList<String>();
	List<String> m_itemLst = new ArrayList<String>();
	Map<String, List<String>> m_pubMailMapForSimple = new LinkedHashMap<String, List<String>>();
	List<String> m_realAccountLst = new ArrayList<String>();
	// 20220323143000 : END

	public UserDecisionDialog(Dialog paramDialog, TCComponentTask paramTCComponentTask, TCComponentSignoff paramTCComponentSignoff) {
		super(paramDialog, paramTCComponentTask, paramTCComponentSignoff);

		System.out.println("UserDecisionDialog1");
	}

	public UserDecisionDialog(AIFDesktop paramAIFDesktop, TCComponentTask paramTCComponentTask, TCComponentSignoff paramTCComponentSignoff) {
		super(paramAIFDesktop, paramTCComponentTask, paramTCComponentSignoff);

		System.out.println("UserDecisionDialog2");
	}

	public void initializeDialog() {	
		// recompile 20220323143000 : START
		super.initializeDialog();
		textArea1.setText("");
		
		parseTxt(m_projectLst, m_itemLst, m_pubMailMapForSimple, m_realAccountLst);
		try {				
			TCComponentRule[] ruleHandlers =  super.psTask.getRules(TCComponentTask.START_ACTION);
			System.out.print("==========>"+ruleHandlers.length);
			TCComponentActionHandler[] actionHandlers =  super.psTask.getActionHandlers(TCComponentTask.COMPLETE_ACTION);
			for (int i = 0; i < actionHandlers.length; i++) {
//				System.out.println("actionHandlers == " + actionHandlers[i]);
				String actionName = actionHandlers[i].getProperty("object_name");
//				System.out.println("actionName ==" + actionName);
				if (actionName.equals("D9_ApplyCustomPN")) {
					System.out.println("===========申请自编料号=================");
					TCComponentActionHandler  handler=actionHandlers[i];
					//Map<String,String> params=handler.getProperties();
					apply_custom_pn_exclude=handler.getProperty("handler_arguments");
					if(apply_custom_pn_exclude!=null&&!("".equalsIgnoreCase(apply_custom_pn_exclude))) {
						String[] m=apply_custom_pn_exclude.split(",");
						for(String s:m) {
							if(s.startsWith("-exclude_type")) {
								apply_custom_pn_exclude=s.split("=")[1].trim();
							}
						}	
					}
					apply_custom_pn=true;
				}
				if (actionName.equals("D9_PostCustomPN")) {
					System.out.println("===========抛转自编料号=================");
					post_custom_pn=true;
				} 
				
				if (actionName.equals("D9_2ndSourceChangeList")) {
					System.out.println("===========2ndSourceChangeList=================");
					d9_2ndSourceChangeList=true;
				} 
				
				if (actionName.equals("D9_SendTechDocMail")) {
					System.out.println("===========D9_SendTechDocMail=================");
					D9_SendTechDocMail=true;
				} 
				
				if (actionName.equals("D9_HPEBOMExport")) {
					System.out.println("===========D9_HPEBOMExport=================");
					D9_HPEBOMExport = true;
				}
				
				if (actionName.equals("D9_DellEBOMExport")) {
					System.out.println("===========D9_DellEBOMExport=================");
					D9_DellEBOMExport = true;
				}			
				
			}
		
		}catch(Exception e) {
			System.out.print(e);
		}
		/*
		initCustUI();						 
        super.initializeDialog();
        getContentPane().removeAll();
        getContentPane().setFocusable(false);
        this.mainPanel.add(this.contentPanel, "Center");
        this.mainPanel.add(this.masterPanel, "South");
        getContentPane().add(this.mainPanel, "Center");
        centerToScreen();
        pack();
               
        if (realAccountLst.size() > 0) {
			m_singoffTF.setText(realAccountLst.get(0));
			m_singoffTF.setEnabled(false);
        }
        */
		// 20220323143000 : END
	}

	public void commitDecision() {
		try {
			TCComponentTask task = super.psTask;			
		
			TCComponent[] coms = task.getRelatedComponents("root_target_attachments");
			
			TCCRDecision tCCRDecision = getDecision();
			
			m_realAccountLst = m_realAccountLst.stream().filter(CommonTools.distinctByKey(str -> str)).collect(Collectors.toList()); 
			// recompile 20220323143000 : START
			StringBuilder contents = new StringBuilder();
			if (m_realAccountLst.size() > 0) {				
				contents.append("【")
				.append("")
				.append(CommonTools.longTime2StringTime(CommonTools.getNowLongTime(), "yyyy/MM/dd HH:mm:ss"))
				.append("】")
				.append("【")
				.append(m_realAccountLst.stream().collect(Collectors.joining(",")))
				.append("】")
				.append(textArea1.getText());
				System.out.println("==>> commitDecision: " + "开始执行写入签核信息");
				textArea1.setText(contents.toString());				
			}
						
			
//			DocAutoAchieve(tCCRDecision);
			// 20220323143000 : END
			if(apply_custom_pn&&89 == tCCRDecision.getIntValue()) {			    
				if(!new CustomPnService().applyCustomPn(task.getSession(),coms,apply_custom_pn_exclude)) {
				   return ;
				}				
			}
			if(post_custom_pn&&89 == tCCRDecision.getIntValue()) {			    
				if(!new CustomPnService().postCustomPn(task.getSession(),coms)) {
				   return ;
				}				
			}
			
			if(d9_2ndSourceChangeList&&89 == tCCRDecision.getIntValue()) {
				if(!new SecondSourceService().genChangeList(task.getSession(), coms)) {
					return;
				}				
			}
			
			if(D9_SendTechDocMail&&89 == tCCRDecision.getIntValue()) {
				TCComponentTask root=task.getRoot();
				TCComponentProcess po=root.getProcess();
				String descr=po.getProperty("object_desc");
				if(!new DocMailGroupService().sendMail(task.getSession(), coms,descr)) {
					return;
				}
			}
			
			if (D9_HPEBOMExport && 89 == tCCRDecision.getIntValue()) {
				new Thread() {
					public void run() {
						HpEbomExportService hpebomService = new HpEbomExportService(task, task.getSession(), coms);
						hpebomService.export();
					}
				}.start();
			}
			
			if (D9_DellEBOMExport && 89 == tCCRDecision.getIntValue()) {
				new Thread() {
					public void run() {
						DellEbomExportService dellEBOMService = new DellEbomExportService(task, task.getSession(), coms);
						dellEBOMService.export();
					}
				}.start();
			}
			
			
			if(89 == tCCRDecision.getIntValue()) {
				TCComponentTask rootTask=task.getRoot();
				String rootName=rootTask.getName();
				TCComponentTask parentTask=task.getParent();
				String parentName=parentTask.getName();
				if(rootName.contains("FXN39")&&parentName.equalsIgnoreCase("Review")) {
					   try {
				        	new DesignMailService().sendMail(session, coms);      
					   }catch(Exception e) {}
				}
			}
			sendMail(tCCRDecision);	
			super.commitDecision();
				

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	private void sendMail(TCCRDecision tCCRDecision) {
		try {
			if (m_projectLst.size() > 0 && m_itemLst.size() > 0 && m_pubMailMapForSimple.size() > 0) {											
				Map<String, String> nodeMap = getDecisionTaskNode(tCCRDecision);
				List<TCComponent> taskList = getDecisionTaskList(tCCRDecision);
			    if(TCUtil.noSendMail(nodeMap, psTask)) return;
				String[] approveContentArr = new String[12];
				String[] notifyContentArr = new String[12];
				String[] rejectContentArr = new String[15];
				String[] finishContentArr = new String[9];
				if (78 == tCCRDecision.getIntValue()) { // reject
					rejectContentArr[0] = registry.getString("mailConent1.reject");
					rejectContentArr[1] = registry.getString("mailConent2.reject");
					rejectContentArr[2] = registry.getString("mailConent3.reject");
					rejectContentArr[3] = registry.getString("mailConent4.reject");
					rejectContentArr[4] = registry.getString("mailConent5.reject");
					rejectContentArr[5] = registry.getString("mailConent6.reject");
					rejectContentArr[6] = registry.getString("mailConent7.reject");
					rejectContentArr[7] = registry.getString("mailConent8.reject");
					rejectContentArr[8] = registry.getString("mailConent9.reject");
					rejectContentArr[9] = registry.getString("mailConent10.reject");
					rejectContentArr[10] = registry.getString("mailConent11.reject");
					rejectContentArr[11] = registry.getString("mailConent12.reject");
					rejectContentArr[12] = registry.getString("mailConent13.reject");
					rejectContentArr[13] = registry.getString("mailConent14.reject");
					rejectContentArr[14] = registry.getString("mailConent15.reject");
				} else if (89 == tCCRDecision.getIntValue()) { // approve
					approveContentArr[0] = registry.getString("mailConent1.approve");
					approveContentArr[1] = registry.getString("mailConent2.approve");
					approveContentArr[2] = registry.getString("mailConent3.approve");
					approveContentArr[3] = registry.getString("mailConent4.approve");
					approveContentArr[4] = registry.getString("mailConent5.approve");
					approveContentArr[5] = registry.getString("mailConent6.approve");
					approveContentArr[6] = registry.getString("mailConent7.approve");
					approveContentArr[7] = registry.getString("mailConent8.approve");
					approveContentArr[8] = registry.getString("mailConent9.approve");
					approveContentArr[9] = registry.getString("mailConent10.approve");
					approveContentArr[10] = registry.getString("mailConent11.approve");
					approveContentArr[11] = registry.getString("mailConent12.approve");
				}
								
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
				
				finishContentArr[0] = registry.getString("mailConent1.finish");
				finishContentArr[1] = registry.getString("mailConent2.finish");
				finishContentArr[2] = registry.getString("mailConent3.finish");
				finishContentArr[3] = registry.getString("mailConent4.finish");
				finishContentArr[4] = registry.getString("mailConent5.finish");
				finishContentArr[5] = registry.getString("mailConent6.finish");
				finishContentArr[6] = registry.getString("mailConent7.finish");
				finishContentArr[7] = registry.getString("mailConent8.finish");
				finishContentArr[8] = registry.getString("mailConent9.finish");
				
				List<String> tempProjectLst = m_projectLst;
				List<String> tempItemLst = m_itemLst;							
				new Thread(new Runnable() {
					public void run() {
						try {
							if (78 == tCCRDecision.getIntValue()) { // reject									
								TCUtil.sendMailForReject(
										"任務溝通等郵件發送及資料傳遞時間", 
										rejectContentArr,
										notifyContentArr,
										finishContentArr,
										m_realAccountLst.stream().collect(Collectors.joining(",")), 
										nodeMap, 
										tempProjectLst, 
										tempItemLst, 
										null, 
										null, 
										m_pubMailMapForSimple);
							} else if (89 == tCCRDecision.getIntValue()) { // approve																
								TCUtil.sendMailForApprove(
										"任務溝通等郵件發送及資料傳遞時間",
										approveContentArr,
										notifyContentArr,
										finishContentArr,
										nodeMap, 
										taskList,
										tempProjectLst, 
										tempItemLst, 
										null, 
										null, 
										m_pubMailMapForSimple);	
							}							
						} catch (Exception e) {
							e.printStackTrace();
						} 					
					}
				}).start();															
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private Map<String, String> getDecisionTaskNode(TCCRDecision tCCRDecision) {
		Map<String, String> retMap = new LinkedHashMap<String, String>();
		
		try {
			if (this.psTask != null) {				
				TCComponentTask tcParentComponentTask = (TCComponentTask)this.psTask.getParent();
				if (tcParentComponentTask != null) {
					if (78 == tCCRDecision.getIntValue()) { // 拒绝的时候才执行
						getD9RejectHandlerParams(tcParentComponentTask, retMap);
					}
					
					TCComponent[] tcComponentArr = tcParentComponentTask.getReferenceListProperty("successors");
					for (TCComponent tcComponentTask : tcComponentArr) {
						if (tcComponentTask instanceof TCComponentTask) {
							String taskType = tcComponentTask.getProperty("task_type");
//							int taskIntValue = ((TCComponentTask) tcComponentTask).getState().getIntValue();
//							if (taskIntValue != 8) {
//								continue;
//							}
							if (78 == tCCRDecision.getIntValue()) { // reject
								if ("EPMOrTask".equals(taskType)) {
									// find next task after OrTask
									getNextNode(tcComponentTask.getReferenceListProperty("successors"), retMap);
								}
							} else if (89 == tCCRDecision.getIntValue()) { // approve
								if (!"EPMOrTask".equals(taskType)) {
									// get tcComponentTask task
									retMap.put(tcComponentTask.getProperty("object_name"), taskType);
									
									if ("EPMTask".equals(taskType)) {
										getNextNode(tcComponentTask.getReferenceListProperty("successors"), retMap);
									}																											
								}
							}
						}
					}				
				}
			}
								
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	
	/**
	 * 获取EPM-demote节点参数
	 * recompile 2023/09/04 10.10 : START
	 * @throws TCException 
	 */
	private void getD9RejectHandlerParams(TCComponentTask task, Map<String, String> retMap) throws TCException {
		TCComponentActionHandler[] actionHandlerArray = task.getActionHandlers(TCComponentTask.UNDO_ACTION);			
		if (CommonTools.isEmpty(actionHandlerArray)) {
			return;
		}
		
		for (TCComponentActionHandler handler : actionHandlerArray) {
			String name = handler.getName();
			System.out.println("==>> Undo actionHandler Name: " + name);
			if (!"D9_SendMailForReject".equals(name)) {
				continue;
			}
			
			String argument = handler.getProperty("handler_arguments");
			if (CommonTools.isNotEmpty(argument)) {
				if (argument.startsWith("-task_name") && argument.contains("-task_type")) {
					String[] split = argument.split(",");
					retMap.put(split[0].split("=")[1], split[1].split("=")[1] + "=reject");
				}
			}
		}
	}
	
	
	private void getNextNode(TCComponent[] tcComponentArr, Map<String, String> nextNodeMap) {		
		try {
			for (TCComponent tcComponentTask : tcComponentArr) {
				String taskType = tcComponentTask.getProperty("task_type");
				if ("EPMOrTask".equals(taskType)) {
					getNextNode(tcComponentTask.getReferenceListProperty("successors"), nextNodeMap);
				} else if (!"EPMOrTask".equals(taskType)) {
					nextNodeMap.put(tcComponentTask.getProperty("object_name"), taskType);
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseTxt(List<String> projectLst, List<String> itemLst, Map<String, List<String>> pubMailMapForSimple, List<String> realAccountLst) {
		try {
			if (this.psTask != null) {
				TCComponentTask tcParentComponentTask = (TCComponentTask)this.psTask.getParent();
				if (tcParentComponentTask != null) {
					TCComponent[] tcCompArr = tcParentComponentTask.getAttachments(TCAttachmentScope.GLOBAL, 1);
					for (TCComponent tcComponent : tcCompArr) {
						TCComponentItemRevision itemRev = null;

						if (tcComponent instanceof TCComponentItem) {
							itemRev = ((TCComponentItem) tcComponent).getLatestItemRevision();
						}
						if (tcComponent instanceof TCComponentItemRevision) {
							itemRev = (TCComponentItemRevision) tcComponent;
						}
						
						if (null == itemRev) continue;
						
						Map<String, List<List<String>>> pubMailMapForMore = TCUtil.parseLstToMap(itemRev);
						
						for (Map.Entry<String, List<List<String>>> entry : pubMailMapForMore.entrySet()) {
							List<List<String>> valueLst = entry.getValue();
							if (valueLst.size() == 3) {
								if (null == projectLst || 0 == projectLst.size())
									projectLst.addAll(valueLst.get(0));
								if (null == itemLst || 0 == itemLst.size())
									itemLst.addAll(valueLst.get(1));
								
								pubMailMapForSimple.put(entry.getKey(), valueLst.get(2));
								
								if (tcParentComponentTask.getProperty("object_name").equals(entry.getKey())) {
									int accountIndex = -1;
									String loginAccount = getLoginUserInfo();
									List<String> publicAccoutLst = Arrays.asList(valueLst.get(2).get(1).split(","));
									for (int i = 0; i < publicAccoutLst.size(); i++) {
										if (loginAccount.equals(publicAccoutLst.get(i))) {
											accountIndex = i;
											break;
										}
									}
									if (accountIndex > -1)
										realAccountLst.add(Arrays.asList(valueLst.get(2).get(2).split(",")).get(accountIndex));
								}
							}
						}														
																	
					}															
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private String getLoginUserInfo() {
		String retVal = "";
		
		try {
			TCComponentUser currentUser = RACUIUtil.getTCSession().getUser();
			if (currentUser != null) {
//				TCComponent tcCompPerson = currentUser.getReferenceProperty("person");
//				if (tcCompPerson != null && tcCompPerson instanceof TCComponentPerson) {
//					System.out.println(currentUser.getProperty("user_id"));
//					System.out.println(tcCompPerson.getProperty("user_name"));
					retVal = currentUser.getProperty("user_id");					
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return retVal;
	}
	// 20220323143000 : END
	
	
	// 202309060932 : START
	private List<TCComponent> getDecisionTaskList(TCCRDecision tCCRDecision) {
		List<TCComponent> list = new ArrayList<TCComponent>();
		
		try {
			if (this.psTask != null) {				
				TCComponentTask tcParentComponentTask = (TCComponentTask)this.psTask.getParent();
				if (tcParentComponentTask != null) {					
					TCComponent[] tcComponentArr = tcParentComponentTask.getReferenceListProperty("successors");
					for (TCComponent tcComponentTask : tcComponentArr) {
						if (tcComponentTask instanceof TCComponentTask) {
							String taskType = tcComponentTask.getProperty("task_type");
							
							if (78 == tCCRDecision.getIntValue()) { // reject
								if ("EPMOrTask".equals(taskType)) {
									// find next task after OrTask
									getNextList(tcComponentTask.getReferenceListProperty("successors"), list);
								}
							} else if (89 == tCCRDecision.getIntValue()) { // approve
								if (!"EPMOrTask".equals(taskType)) {
									// get tcComponentTask task
//									retMap.put(tcComponentTask.getProperty("object_name"), taskType);
									list.add(tcComponentTask);
									
									if ("EPMTask".equals(taskType)) {
										getNextList(tcComponentTask.getReferenceListProperty("successors"), list);
									}																											
								}
							}
						}
					}				
				}
			}
								
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}	
	
	
	
	private void getNextList(TCComponent[] tcComponentArr, List<TCComponent> list) {		
		try {
			for (TCComponent tcComponentTask : tcComponentArr) {
				String taskType = tcComponentTask.getProperty("task_type");
				if ("EPMOrTask".equals(taskType)) {
					getNextList(tcComponentTask.getReferenceListProperty("successors"), list);
				} else if (!"EPMOrTask".equals(taskType)) {
					list.add(tcComponentTask);
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
