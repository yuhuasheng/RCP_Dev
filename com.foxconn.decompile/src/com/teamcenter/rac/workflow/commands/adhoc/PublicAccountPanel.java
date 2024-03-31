package com.teamcenter.rac.workflow.commands.adhoc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.table.TableModel;

import org.eclipse.swt.widgets.Display;

import com.foxconn.decompile.util.SPASUser;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.VerticalLayout;

public class PublicAccountPanel extends JPanel {
	private Registry appReg;
	
	protected TCSession session;
	
	private TeamRosterPanel teamRosterPanel;

	protected PublicMailPanel publicMailPanel;
		
	protected String publicUserInfo = "";
	
	protected String projectId = "";
	
	private List<List<String>> pubMailInfoLst = null;
	
	public PublicAccountPanel(TCSession paramTCSession, String projectId, List<List<String>> pubMailInfoLst) {
//		System.out.println("=============PublicAccountPanel()==============");
		this.appReg = Registry.getRegistry(this);
		this.session = paramTCSession;
		this.projectId = projectId;
		this.pubMailInfoLst = pubMailInfoLst;

		initializePanel();
	}
	
	protected void initializePanel() {				
		teamRosterPanel = new TeamRosterPanel(session, projectId);
		publicMailPanel = new PublicMailPanel(session, pubMailInfoLst);
		
		teamRosterPanel.addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						addPubMailInfo();		
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
						removePubMailInfo();
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
       						addPubMailInfo();		
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
	
	private void addPubMailInfo() {
		try {
			SPASUser user = (SPASUser) teamRosterPanel.userList.getSelectedValue();
			
			TableModel tableModel = publicMailPanel.table.getModel();
			
			if (-1 == publicMailPanel.table.getSelectedRow()) {
				MessageBox.post("请选择表数据", "提示", 2);
				return;
			}

			if (!"".equals(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1))) {
				tableModel.setValueAt(user.getName(), publicMailPanel.table.getSelectedRow(), 2);
				tableModel.setValueAt(user.getNotes(), publicMailPanel.table.getSelectedRow(), 3);
				
				String node = tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0).toString();
				String pubAccount = tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1).toString();
				
				String key = node+pubAccount;
				if (publicMailPanel.m_rowMap.containsKey(key)) {
					publicMailPanel.m_rowMap.put(key, Arrays.asList(publicMailPanel.m_rowMap.get(key).get(0), publicMailPanel.m_rowMap.get(key).get(1), user.getName(), user.getNotes()));
				} else {
					publicMailPanel.m_rowMap.put(key, Arrays.asList(node, pubAccount, user.getName(), user.getNotes()));
				}
				
			} else {
				MessageBox.post("请选择TC共用帐号", "提示", 2);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removePubMailInfo() {
		try {
			SPASUser user = (SPASUser) teamRosterPanel.userList.getSelectedValue();
			
			TableModel tableModel = publicMailPanel.table.getModel();
			
			if (-1 == publicMailPanel.table.getSelectedRow()) {
				MessageBox.post("请选择表数据", "提示", 2);
				return;
			}

			if (!"".equals(tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1))) {
				tableModel.setValueAt("", publicMailPanel.table.getSelectedRow(), 2);
				tableModel.setValueAt("", publicMailPanel.table.getSelectedRow(), 3);
				
				String node = tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 0).toString();
				String pubAccount = tableModel.getValueAt(publicMailPanel.table.getSelectedRow(), 1).toString();
				
				String key = node+pubAccount;
				if (publicMailPanel.m_rowMap.containsKey(key)) {
					publicMailPanel.m_rowMap.put(key, Arrays.asList(publicMailPanel.m_rowMap.get(key).get(0), publicMailPanel.m_rowMap.get(key).get(1), "", ""));
				}
				
			} else {
				MessageBox.post("请选择TC共用帐号", "提示", 2);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
