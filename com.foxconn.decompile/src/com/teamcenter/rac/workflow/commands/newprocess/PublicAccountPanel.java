package com.teamcenter.rac.workflow.commands.newprocess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.table.TableModel;

import org.eclipse.swt.widgets.Display;

import com.foxconn.decompile.util.CommonTools;
import com.foxconn.decompile.util.SPASUser;
import com.foxconn.tcutils.constant.GroupEnum;
import com.teamcenter.rac.common.TCTreeNode;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.VerticalLayout;

public class PublicAccountPanel extends JPanel {
	private Registry appReg;
	
	protected TCSession session;

	protected TeamRosterPanel teamRosterPanel;
	
	protected PublicMailPanel publicMailPanel;
	
	protected UserAssignAllTasksPanel assignPanel;
	
	protected String projectId = "";
	
	
	public PublicAccountPanel(TCSession paramTCSession, UserAssignAllTasksPanel assignPanel, String projectId) {
//		System.out.println("=============PublicAccountPanel()==============");
		this.appReg = Registry.getRegistry(this);
		this.session = paramTCSession;
		this.assignPanel = assignPanel;
		this.projectId = projectId;		
		initializePanel();
	}
	
	protected void initializePanel() {
		teamRosterPanel = new TeamRosterPanel(session, projectId);
		publicMailPanel = new PublicMailPanel(session);				
		
		teamRosterPanel.addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						boolean addFlag = true;
						TableModel tableModel = publicMailPanel.table.getModel();
						if (-1 == publicMailPanel.table.getSelectedRow()) {
							MessageBox.post("请选择表数据", "提示", 2);
							return;
						}
						
						String groupName = UserExtNewProcessDialog.group;						
						if (groupName.toUpperCase().contains(GroupEnum.DBA.groupName()) || groupName.toUpperCase().contains(GroupEnum.Desktop.groupName())) {
							try {
								String templateName = assignPanel.processTemplate.getName();
								Optional<String> findFirst = Stream.of(UserExtNewProcessDialog.allowAssignMoreWorkflowTemplates).filter(str -> str.trim().contains(templateName)).findFirst();
								String value = null;
								if (findFirst.isPresent()) {									
									value = findFirst.get();
								}
								
								if (CommonTools.isNotEmpty(value)) {									
									String node =  tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0) == null ? "" : tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0).toString();
									List<String> nodeList = new ArrayList<String>(Arrays.asList(value.trim().split("=")[1].split(",")));
									boolean anyMatch = nodeList.stream().anyMatch(str -> str.contains(node));
									if (anyMatch) {
										boolean app = appendPubMailInfo(); // 追加账号
										if(app)
											teamRosterPanel.isAction = false;
										
										addFlag = false;
									}
								}
								
							} catch (TCException e) {
								e.printStackTrace();
							}							
							
						} 
						
						if (addFlag) {
							
							boolean add = addPubMailInfo(); // 替换账号
							if (add) 
								teamRosterPanel.isAction = false;
							
						}								
					}
				});
			}
		});
		
		teamRosterPanel.deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						boolean rem = removePubMailInfo();
						if (rem) 
							teamRosterPanel.isAction = false;
						
					}
				});
			}
		});
		
		teamRosterPanel.userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(teamRosterPanel.userList.getSelectedIndex() != -1) {                   
                   if(2 == e.getClickCount())
                	   Display.getDefault().syncExec(new Runnable() {
       					@Override
       					public void run() {
       						boolean add = addPubMailInfo();
       						if (add) 
								teamRosterPanel.isAction = false;
       					}
       				});
				}
			}
		});
		
//		setLayout(new BorderLayout());
//      add(publicMailPanel, BorderLayout.CENTER);
//      add(teamRosterPanel, BorderLayout.EAST);
		
		setLayout(new VerticalLayout(7, 4, 4, 4, 4));
		SplitPane splitPane = new SplitPane(0);
		splitPane.setLeftComponent(publicMailPanel);
		splitPane.setRightComponent(this.teamRosterPanel);
		splitPane.setDividerSize(2);		
		splitPane.setDividerLocation(0.65D);		
		add("unbound.bind", splitPane);
	}
	
	/**
	 * 替换实际工程师
	 * @return
	 */
	private boolean addPubMailInfo() {
		try {				
			SPASUser user = (SPASUser) teamRosterPanel.userList.getSelectedValue();
			
			UserAssignmentListPanel.pubMail = user.getNotes();																	
			
			TableModel tableModel = publicMailPanel.table.getModel();
			
			if (-1 == publicMailPanel.table.getSelectedRow()) {
				MessageBox.post("请选择表数据", "提示", 2);
				return false;
			}
			
			if (checkIsModify(tableModel)) {
				if (!"".equals(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1))) {
					tableModel.setValueAt(user.getName(), publicMailPanel.table.getSelectedRow(), 2);
					tableModel.setValueAt(user.getNotes(), publicMailPanel.table.getSelectedRow(), 3);	
						
					publicMailPanel.m_rowMap.put(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0)+""+tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1), 
												Arrays.asList(user.getName(), user.getNotes()));
					return true;
				} else {
					MessageBox.post("请选择TC共用帐号", "提示", 2);
					return false;
				}
			} else {
				MessageBox.post("當前流程節點不能編輯", "提示", 2);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;		
	}
	
	/**
	 * 追加实际工程师
	 * @return
	 */
	private boolean appendPubMailInfo() {
		try {
			SPASUser user = (SPASUser) teamRosterPanel.userList.getSelectedValue();
			
			UserAssignmentListPanel.pubMail = user.getNotes();
			
			TableModel tableModel = publicMailPanel.table.getModel();
			
			if (-1 == publicMailPanel.table.getSelectedRow()) {
				MessageBox.post("请选择表数据", "提示", 2);
				return false;
			}
			
			String actualName = "";
			String actualEmail = "";
			
			if (checkIsModify(tableModel)) {
				if (!"".equals(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1))) {
					int selectedRow = publicMailPanel.table.getSelectedRow();
					if (tableModel.getValueAt(selectedRow, 2) != null) {
						actualName = (String) tableModel.getValueAt(selectedRow, 2);
					}
					if (tableModel.getValueAt(selectedRow, 3) != null) {
						actualEmail = (String) tableModel.getValueAt(selectedRow, 3);
					}
					
					if (CommonTools.isEmpty(actualName) || CommonTools.isEmpty(actualEmail)) {
						actualName = "";
						actualEmail = "";
					}
					
					if (CommonTools.isNotEmpty(user.getName())) {
						if (CommonTools.isNotEmpty(actualName)) {
							actualName += "," + user.getName();
						} else {
							actualName += user.getName();
						}
						
					}
					
					if (CommonTools.isNotEmpty(user.getNotes())) {
						if (CommonTools.isNotEmpty(actualEmail)) {
							actualEmail += "," + user.getNotes();
						} else {
							actualEmail += user.getNotes();
						}
						
					}
					
					tableModel.setValueAt(actualName, publicMailPanel.table.getSelectedRow(), 2);
					tableModel.setValueAt(actualEmail, publicMailPanel.table.getSelectedRow(), 3);	
					publicMailPanel.m_rowMap.put(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0)+""+tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1), 
							Arrays.asList(actualName, actualEmail));
					return true;
				} else {
					MessageBox.post("请选择TC共用帐号", "提示", 2);
					return false;
				}
			} else {
				MessageBox.post("當前流程節點不能編輯", "提示", 2);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除实际工程师
	 */
	private boolean removePubMailInfo() {
		try {						
			TableModel tableModel = publicMailPanel.table.getModel();
			
			if (-1 == publicMailPanel.table.getSelectedRow()) {
				MessageBox.post("请选择表数据", "提示", 2);
				return false;
			}
			
			if (!"".equals(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1))) {
				tableModel.setValueAt("", publicMailPanel.table.getSelectedRow(), 2);
				tableModel.setValueAt("", publicMailPanel.table.getSelectedRow(), 3);	
					
				publicMailPanel.m_rowMap.put(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0)+""+tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1), Arrays.asList("", ""));							
				return true;
			} else {
				MessageBox.post("请选择TC共用帐号", "提示", 2);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean checkIsModify(TableModel tableModel) {		
		try {
			String node = tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0).toString();
			
			TCTreeNode tCTreeNode = (TCTreeNode) assignPanel.resourcesPanel.processTreeView.getRootNode();
			int fCount = tCTreeNode.getChildCount();
			for (int fi = 0; fi < fCount; fi++) {
				TCTreeNode fTCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(fi);
				TCComponentTaskTemplate tCComponentTaskTemplate = (TCComponentTaskTemplate) fTCTreeNode1.getComponent();
				int sCount = fTCTreeNode1.getChildCount();
				if (sCount > 0 && tCComponentTaskTemplate !=null && node.equals(tCComponentTaskTemplate.getName())) {
					return true;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
