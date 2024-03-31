package com.foxconn.mechanism.jurisdiction;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.handlers.WizardHandler.New;

import com.foxconn.mechanism.util.TCUtil;
import com.google.gson.Gson;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.AIFTreeNode;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.TCTreeNode;
import com.teamcenter.rac.common.organization.OrgTreePanel;
import com.teamcenter.rac.common.organization.OrganizationTree;
import com.teamcenter.rac.common.organization.ProjectTeamSelectionPanel;
import com.teamcenter.rac.common.teamroleusertree.TeamRoleUserTree;
import com.teamcenter.rac.common.teamroleusertree.TeamRoleUserTreeNode;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMView;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentEnvelope;
import com.teamcenter.rac.kernel.TCComponentEnvelopeType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.UserList;
import com.teamcenter.rac.util.Registry;

public class PerTraNoticeDialog extends Dialog{
	
	private static final SimpleDateFormat yearMonthDay = new SimpleDateFormat("yyyy-MM-dd");
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private Shell shell = null;
	private TableLayout tLayout = null;
	private TreeViewer treeViewer = null;
	private List<JurisdictionChangeInfo> jurisdictionChangeInfoList = null;

	public PerTraNoticeDialog (AbstractAIFUIApplication app, Shell parent, Registry reg) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(1348, 600);
		shell.setText(reg.getString("dialogTitle"));
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		SashForm sashForm = new SashForm(shell, SWT.BORDER);
		Composite treeComposite = new Composite(sashForm, SWT.BORDER);
		GridLayout layout = new GridLayout(2, true);
		treeComposite.setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		
		treeViewer = new TreeViewer(treeComposite, SWT.V_SCROLL| SWT.H_SCROLL| SWT.FULL_SELECTION|SWT.BORDER | SWT.MULTI);
		Tree tree = treeViewer.getTree();
		tLayout = new TableLayout();
		tree.setHeaderVisible(true);
		tree.setHeaderBackground(new Color(null, 211, 211, 211));
		tree.setLinesVisible(true);
		tree.setLayout(tLayout);
		tree.setLayoutData(data);
		
		createTreeColumn(treeViewer);
		
		treeViewer.setContentProvider(new CustTreeContenetProvider());
		treeViewer.setLabelProvider(new CustTableLableProvider());
		treeViewer.setInput(app.getTargetComponent());
		treeViewer.setExpandedState(treeViewer.getTree().getItems()[0].getData(), true);
		treeViewer.getTree().setSelection(treeViewer.getTree().getItem(0));			
		treeViewer.getTree().addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				if(e.button == MouseEvent.BUTTON3) {
					TreeItem[] selection = tree.getSelection();
					selection[0].setText(2, "");
				}
			}
		});
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
//		TabItem orgTreeItem = new TabItem(tabFolder, SWT.NONE);
//		orgTreeItem.setText(reg.getString("TabItemTitle1"));
//		OrgTreePanel orgTreePanel = new OrgTreePanel(session);
//		Composite orgTreeComposite = new Composite(tabFolder, SWT.EMBEDDED | SWT.NO_BACKGROUND);
//		Frame orgTreeFrame = SWT_AWT.new_Frame(orgTreeComposite);
//		orgTreeFrame.setPreferredSize(new Dimension(300, 400));
//		orgTreeFrame.add(orgTreePanel);
//		orgTreeItem.setControl(orgTreeComposite);
		
		TabItem projectTeamItem = new TabItem(tabFolder, SWT.NONE);
		projectTeamItem.setText(reg.getString("TabItemTitle2"));
		ProjectTeamSelectionPanel projectTeamSelectionPanel = new ProjectTeamSelectionPanel(session);
		Composite projectTeamComposite = new Composite(tabFolder, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame projectTeamFrame = SWT_AWT.new_Frame(projectTeamComposite);
		projectTeamFrame.setPreferredSize(new Dimension(300, 400));
		projectTeamFrame.add(projectTeamSelectionPanel);
		projectTeamItem.setControl(projectTeamComposite);
		JComboBox projectsListCombo = projectTeamSelectionPanel.getProjectsListCombo();
		TeamRoleUserTree teamRoleUserTree = projectTeamSelectionPanel.getTeamRoleUserTree();

		
		
		TCComponentBOMLine bomLine = (TCComponentBOMLine) app.getTargetComponent();
		String platformFoundIds = "";
		try {
			platformFoundIds = bomLine.getProperty("bl_rev_project_ids");
			boolean contains = platformFoundIds.contains(",");
			String[] projectIds = null;
			if(contains) {
				projectIds = platformFoundIds.split(",");
			}else {
				projectIds = new String[] {platformFoundIds};
			}
			for (int j = 0; j < projectIds.length; j++) {
				String id = projectIds[j];
				TCComponent[] projects = TCUtil.executeQuery(session, "__D9_Find_Project", 
						new String[] {"project_id"}, new String[] {id});
				if(projects.length == 0) {
					continue;
				}
				TCComponentProject project = (TCComponentProject) projects[0];
				projectsListCombo.addItem(project);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		TabItem teamRosterItem = new TabItem(tabFolder, SWT.NONE);
		teamRosterItem.setText("TeamRoster");
		JPanel teamRosterPanel = null;
		if(!" ".equals(platformFoundIds)) {
			TeamRoster teamRoster = new TeamRoster(this.session,platformFoundIds);
			teamRosterPanel = teamRoster.getTeamRosterPanel(treeViewer);
		}
		Composite teamRosterComposite = new Composite(tabFolder, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame teamRosterFrame = SWT_AWT.new_Frame(teamRosterComposite);
		teamRosterFrame.setPreferredSize(new Dimension(300, 400));
		if(teamRosterPanel != null) {
			teamRosterFrame.add(teamRosterPanel);
		}
		teamRosterItem.setControl(teamRosterComposite);
		
//		treeViewer.getTree().addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent selectionevent) {
//				try {
//					projectsListCombo.setModel(new DefaultComboBoxModel(new Object[]{}));//清空下拉框
//					teamRoleUserTree.removeAllChildren();
//					TreeItem[] selection = treeViewer.getTree().getSelection();
//					CustTreeNode data = (CustTreeNode)selection[0].getData();
//					TCComponentBOMLine bomLine = data.getBomLine();
//					String projectId = bomLine.getProperty("bl_rev_project_ids");
//					String[] projectIds = null;
//					boolean contains = projectId.contains(",");
//					if(contains) {
//						projectIds = projectId.split(",");
//					}else {
//						projectIds = new String[] {projectId};
//					}
//					for (int j = 0; j < projectIds.length; j++) {
//						String id = projectIds[j];
//						TCComponent[] projects = TCUtil.executeQuery(session, "__D9_Find_Project", 
//								new String[] {"project_id"}, new String[] {id});
//						if(projects.length == 0) {
//							continue;
//						}
//						TCComponentProject project = (TCComponentProject) projects[0];
//						projectsListCombo.addItem(project);
//						teamRoleUserTree.updateUI();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionevent) {
//				
//			}
//		});
		
		teamRoleUserTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					try {
						TCTreeNode[] selectedNodes = teamRoleUserTree.getSelectedNodes();
						String name = selectedNodes[0].getUserObject().toString();
						TCComponentUser user = UserList.getUser(session, name);
						if(user == null) {
							return;
						}
						String userName = user.toDisplayString();
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								TreeItem[] selection = tree.getSelection();
								selection[0].setText(2, userName);
								setChildrenAssignUser(selection[0],userName);
							}
						});
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
//		OrganizationTree orgTree = orgTreePanel.getOrgTree();
//		orgTree.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent e) {
//				if(e.getClickCount() == 2) {
//					try {
//						AIFTreeNode selectedNode = orgTree.getSelectedNode();
//						String name = selectedNode.getUserObject().toString();
//						TCComponentUser user = UserList.getUser(session, name);
//						if(user == null) {
//							return;
//						}
//						String userName = user.toDisplayString();
//						Display.getDefault().syncExec(new Runnable() {
//							@Override
//							public void run() {
//								TreeItem[] selection = tree.getSelection();
//								selection[0].setText(2, userName);
//								setChildrenAssignUser(selection[0],userName);
//							}
//						});
//					} catch (Exception e2) {
//						e2.printStackTrace();
//					}
//				}
//			}
//		});

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		
		Composite buttonComposite = new Composite(treeComposite, SWT.NONE);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(data);
		
		data = new GridData();
		data.horizontalAlignment = SWT.END;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 60;
		data.heightHint = 40;
		
		Button okButton = new Button(buttonComposite, SWT.PUSH);
		okButton.setText(reg.getString("confirmButton"));
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					jurisdictionChangeInfoList = new ArrayList<JurisdictionChangeInfo>();
					TreeItem topItem = treeViewer.getTree().getTopItem();
					TCComponentBOMLine bomLine = ((CustTreeNode)topItem.getData()).getBomLine();
					String owner = topItem.getText(1);
					String assignUser = topItem.getText(2);
					String personLiable = topItem.getText(3);
					String mailbox = topItem.getText(4);
					jurisdictionChangeInfoList.add(new JurisdictionChangeInfo(bomLine, owner, assignUser, personLiable, mailbox));
					treeConvertObject(topItem);
					
					//过滤[转移给]为空的数据
					List<JurisdictionChangeInfo> assignUserNotEmptyList = jurisdictionChangeInfoList.stream()
							.filter(j -> !j.getAssignUser().equals(""))
							.collect(Collectors.toList());
					
					//过滤[责任人]为空的数据
					List<JurisdictionChangeInfo> personLiableNotEmptyList = jurisdictionChangeInfoList.stream()
							.filter(j -> !j.getPersonLiable().equals(""))
							.collect(Collectors.toList());

					if(assignUserNotEmptyList.size() == 0 && personLiableNotEmptyList.size() == 0) {
						MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo2"));
						return;
					}
					
					//检查是否有更改权限
					String checkOwnership = checkOwnership(assignUserNotEmptyList);
					if(!checkOwnership.equals("")) {
						MessageDialog.openInformation(shell, reg.getString("prompt"), checkOwnership + reg.getString("promptInfo3"));
						jurisdictionChangeInfoList.clear();
						return;
					}
					
					//检查是否有写的权限
					String writeJurisdiction = checkWriteJurisdiction(assignUserNotEmptyList);
					if(!writeJurisdiction.equals("")) {
						MessageDialog.openInformation(shell, reg.getString("prompt"), writeJurisdiction + reg.getString("promptInfo4"));
						jurisdictionChangeInfoList.clear();
						return;
					}
					changeOwnership(assignUserNotEmptyList,personLiableNotEmptyList);
					shell.dispose();
					MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo5"));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 60;
		data.heightHint = 40;
		
		Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText(reg.getString("cancelButton"));
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		sashForm.setWeights(new int[] {3,1});
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void createTreeColumn(TreeViewer tv) {
		String[] titles = new String[] {reg.getString("treeColumnTitle1"), 
				reg.getString("treeColumnTitle2"), reg.getString("treeColumnTitle3"), 
				reg.getString("treeColumnTitle4"), reg.getString("treeColumnTitle5")};
		for (int index = 0; index < titles.length; index++) {
			TreeViewerColumn tvc = new TreeViewerColumn(tv, SWT.NONE, index);
			tvc.getColumn().setText(titles[index]);
			int columnWidth = 150;
			if(index == 0) {
				columnWidth = 310;
			}
			if(index == 4) {
				columnWidth = 210;
			}
			tvc.getColumn().setWidth(columnWidth);
			//tvc.setEditingSupport(new MyEditingSupport3(shell,tv, index));
		}
	}
	
	/**
	 * 递归设置选中节点的所有子节点指派人
	 * @param selectItem
	 * @param userName
	 */
	private void setChildrenAssignUser(TreeItem selectItem, String userName){
		treeViewer.setExpandedState(selectItem.getData(), true);
		TreeItem[] items = selectItem.getItems();
		if(items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				TreeItem item = items[i];
				item.setText(2, userName);
				setChildrenAssignUser(item, userName);
			}
		}
	}
	
	
	/**
	 * 将树节点信息转换成对象
	 * @param topItem
	 */
	private void treeConvertObject(TreeItem item) {
		treeViewer.setExpandedState(item.getData(), true);//展开节点，不然获取不到子节点
		TreeItem[] items = item.getItems();
		if(items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				TCComponentBOMLine bomLine = ((CustTreeNode)items[i].getData()).getBomLine();
				String owner = items[i].getText(1);
				String assignUser = items[i].getText(2);
				String personLiable = items[i].getText(3).trim();
				String mailbox = items[i].getText(4).trim();
				jurisdictionChangeInfoList.add(new JurisdictionChangeInfo(bomLine, owner, assignUser, personLiable, mailbox));
				treeConvertObject(items[i]);
			}
		}
	}
	
	
	/**
	 * 检查所有权
	 * @return
	 */
	private String checkOwnership(List<JurisdictionChangeInfo> jurisdictionChangeInfoList) {
		String failItemRevName = "";
		try {
			for (int i = 0; i < jurisdictionChangeInfoList.size(); i++) {
				JurisdictionChangeInfo jci = jurisdictionChangeInfoList.get(i);
				String owner2 = jci.getOwner();
				TCComponent[] search = TCUtil.executeQuery(session, "__WEB_find_user", 
						new String[] {"user_id"}, new String[] {owner2});
				TCComponentUser user = (TCComponentUser) search[0];
				String owner = user.getProperty("user_name");
				String currentUser = session.getUser().getProperty("user_name");
				if(!currentUser.equals(owner)) {
					TCComponentBOMLine bomLine = jci.getBomLine();
					TCComponentItem item = bomLine.getItem();
					String itemName = item.getProperty("object_name");
					failItemRevName = failItemRevName + "【" + itemName + "】";
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return failItemRevName;
	}
	
	/**
	 * 检查写权限
	 * @return
	 */
	private String checkWriteJurisdiction(List<JurisdictionChangeInfo> jurisdictionChangeInfoList) {
		String failItemRevName = "";
		try {
			for (int i = 0; i < jurisdictionChangeInfoList.size(); i++) {
				JurisdictionChangeInfo jci = jurisdictionChangeInfoList.get(i);
				TCComponentBOMLine bomLine = jci.getBomLine();
				TCComponentItem item = bomLine.getItem();
				boolean isWrite = TCUtil.checkUserPrivilege(session, item);
				if(!isWrite) {
					String itemName = item.getProperty("object_name");
					failItemRevName = failItemRevName + "【" + itemName + "】";
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return failItemRevName;
	}

	/**
	 * 变更所有权
	 * @return
	 */
	private void changeOwnership(List<JurisdictionChangeInfo> assignUserNotEmptyList,List<JurisdictionChangeInfo> personLiableNotEmptyList){
		try {	 
			//过滤[所有者]、[转移给]相同的数据
			List<JurisdictionChangeInfo> jciList1 = assignUserNotEmptyList.stream().filter(
					j -> !j.getOwner().equals(j.getAssignUser())).collect(Collectors.toList());
			
	        //根据[转移给]分组
	        Map<String, List<JurisdictionChangeInfo>> assignUserGroupMap = jciList1.stream().
	        		collect(Collectors.groupingBy(JurisdictionChangeInfo::getAssignUser));
	        
	        //过滤[责任人]、[实际作者]相同的数据
			List<JurisdictionChangeInfo> jciList2 = personLiableNotEmptyList.stream().filter(
					j -> {
						boolean flag = false;
						try {
							flag = !j.getBomLine().getItemRevision().getProperty("d9_ActualUserID").equals(j.getPersonLiable());
						} catch (TCException e) {
							e.printStackTrace();
						}
						return flag;
					}).collect(Collectors.toList());
	        
	        //根据[责任人]分组
	        Map<String, List<JurisdictionChangeInfo>> personLiableGroupMap = jciList2.stream().
	        		collect(Collectors.groupingBy(JurisdictionChangeInfo::getPersonLiable));
	        
	        
	        TreeItem topItem = treeViewer.getTree().getTopItem();
			TCComponentBOMLine bomLine = ((CustTreeNode)topItem.getData()).getBomLine();
			//String projectId = bomLine.getProperty("bl_rev_project_ids").split(",")[0];
			String project = bomLine.getProperty("bl_rev_project_list").split(",")[0];
	        
	        //处理[转移给]
			for (Entry<String, List<JurisdictionChangeInfo>> augMap : assignUserGroupMap.entrySet()) {
				String key = augMap.getKey();
				TCComponent[] assignUsers = TCUtil.executeQuery(session, "__WEB_find_user",
						new String[] {"user_id"}, new String[]{key});

				TCComponentUser assignUser = (TCComponentUser) assignUsers[0];
				List<JurisdictionChangeInfo> value = augMap.getValue();
				TCComponentItem[] items = new TCComponentItem[value.size()];
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < value.size(); i++) {
					JurisdictionChangeInfo jci = value.get(i);
					sb.append(jci.getBomLine().getTCProperty("bl_indented_title").getStringValue() + ",");
					TCComponentItem item = jci.getBomLine().getItem();
					items[i] = item;
					item.changeOwner(assignUser, assignUser.getGroups()[0]);
					jci.getBomLine().getItemRevision().changeOwner(assignUser, assignUser.getGroups()[0]);
					AIFComponentContext[] itemRelated = item.getRelated("bom_view_tags");
					for (int j = 0; j < itemRelated.length; j++) {
						TCComponentBOMView BOMView = (TCComponentBOMView) itemRelated[j].getComponent();
						BOMView.changeOwner(assignUser, assignUser.getGroups()[0]);
					}
					AIFComponentContext[] itemRevRelated = jci.getBomLine().getItemRevision().getRelated();
					for (int j = 0; j < itemRevRelated.length; j++) {
						InterfaceAIFComponent component = itemRevRelated[j].getComponent();
						if(component instanceof TCComponentDataset) {
							TCComponentDataset dataset = (TCComponentDataset) component;
							dataset.changeOwner(assignUser, assignUser.getGroups()[0]);
						}
					}
				}
				sb.substring(0, sb.length() - 1);
				String mailSubject = reg.getString("mailSubject1") + key + reg.getString("mailSubject2") + project + reg.getString("mailSubject3");
				String mailContent = reg.getString("mailContent1") + project + reg.getString("mailContent2") + sb.toString() + reg.getString("mailContent3");
				sendTCEmail(assignUser, mailSubject, mailContent, items);
			}
			TCUtil.setBypass(session);
			//处理[责任人]
			for (Entry<String, List<JurisdictionChangeInfo>> plgMap : personLiableGroupMap.entrySet()) {
				String personLiable = plgMap.getKey();
				List<JurisdictionChangeInfo> jcis = plgMap.getValue();
				
				//请您完成『 专案名』 如下： 『零组件ID/零组件名称』设计工作
				String mailSubject = reg.getString("mailSubject4") + personLiable
						+ reg.getString("mailSubject5") + project
						+ reg.getString("mailSubject6");
				
				StringBuilder mailContent = new StringBuilder();
				
				for (int i = 0; i < jcis.size(); i++) {
					JurisdictionChangeInfo jci = jcis.get(i);
					jci.getBomLine().getItemRevision().setProperty("d9_ActualUserID", personLiable);
					jci.getBomLine().getItemRevision().setProperty("d9_RealMail", jci.getMailbox());
					String assignUser = jci.getAssignUser();
					if(assignUser.equals("")) {
						assignUser = jci.getOwner();
					}
					mailContent.append(assignUser + "-" + jci.getBomLine().getTCProperty("bl_indented_title").getStringValue() + ",");
				}
				String mailContent1 = mailContent.substring(0, mailContent.length() - 1).toString();
				sendExternalEmail(jcis.get(0).getMailbox(), mailSubject, mailContent1);
			}
			TCUtil.closeBypass(session);
		} catch (Exception e) {
			try {
				TCUtil.closeBypass(this.session);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送TC邮件
	 * @param user
	 * @param title
	 * @param content
	 * @param itemRev
	 */
	private void sendTCEmail(TCComponentUser user, String title, String content, TCComponentItem[] items) {
		try {
			TCComponentEnvelopeType type = (TCComponentEnvelopeType) this.session.getTypeComponent("Envelope");
			// 创建邮件 第一个参数：正文 第二个参数：描述
			TCComponentEnvelope lope = type.create("请完成相关指派的任务（" + yearMonthDay.format(new Date()) + "）！", content, null);
			lope.addReceivers(new TCComponent[]{user});// 添加收件人
			lope.setProperty("object_desc", title);
			lope.add("contents", items);
			lope.send();
		} catch (TCException e) {
			e.printStackTrace();
		}
	 }
	
	/**
	 * 发送外部邮箱
	 * @param to
	 * @param title
	 * @param content
	 */
	private void sendExternalEmail(String to,String title, String content) {
		String state = "";
		try {
			Map<String, String> httpmap = new HashMap<String, String>();
		    String mailUrl=TCUtil.getPreference(this.session, TCPreferenceService.TC_preference_site, "D9_Mail_URL");
		    // poc 10.134.28.97   prd 10.134.28.95
		    String mailHost=TCUtil.getPreference(this.session, TCPreferenceService.TC_preference_site, "D9_Mail_Host");			
		    httpmap.put("url", mailUrl+"/tc-mail/teamcenter/sendMail2");
			httpmap.put("host", mailHost);
			httpmap.put("from", "cmm-it-plm@mail.foxconn.com");
			httpmap.put("to", to);
			httpmap.put("title", title);
			httpmap.put("content", content);

			String url = httpmap.get("url");
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			Gson gson = new Gson();
			String params = gson.toJson(httpmap);
			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
			StringBody contentBody = new StringBody(params,contentType);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);					
			builder.addPart("data", contentBody);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					state = EntityUtils.toString(entitys);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
