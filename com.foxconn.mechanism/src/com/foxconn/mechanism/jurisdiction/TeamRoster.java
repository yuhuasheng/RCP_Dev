package com.foxconn.mechanism.jurisdiction;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.foxconn.mechanism.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class TeamRoster {
	private List<SPASUser> users = null;
	private DefaultListModel listModel = null;
	
//	static{
//		try {
//			String result = "";
//			HashMap httpmap = new HashMap();
//			httpmap.put("platformFoundIds", "p830,p826,p1341"); 
//			String url = "http://10.203.163.43:80/tc-integrate/spas/getTeamRoster";
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//			Gson gson = new Gson();
//			String params = gson.toJson(httpmap);
//			StringBody contentBody = new StringBody(params, Charset.defaultCharset());
//			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//			builder.addPart("data", contentBody).setCharset(CharsetUtils.get("UTF-8")).build();
//			HttpEntity entity = builder.build();
//			httpPost.setEntity(entity);
//			HttpResponse response = httpClient.execute(httpPost);
//			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
//				result = EntityUtils.toString(response.getEntity(), "utf-8");
//			}
//			httpClient.getConnectionManager().shutdown();
//			if(!result.equals("")){
//				Gson gson1 = new Gson();
//				users = gson1.fromJson(result, new TypeToken<List<SPASUser>>(){}.getType());
//				listModel = new DefaultListModel();
//				for (int i = 0; i < users.size(); i++) {
//					listModel.addElement(users.get(i));
//				}
//			}else {
//				System.out.println("SPAS集成查询失败..");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	protected TeamRoster(TCSession session,String platformFoundIds) {
		if(!platformFoundIds.equals("")) {
			loadTeamRosterData(session,platformFoundIds.toLowerCase());
		}
		if(listModel != null) {
			listModel.removeAllElements();
			for (int i = 0; i < users.size(); i++) {
				listModel.addElement(users.get(i));
			}
		}
	}
	
	private void loadTeamRosterData(TCSession session,String platformFoundIds) {
		try {
			String result = "";
			HashMap httpmap = new HashMap();
			httpmap.put("platformFoundIds", platformFoundIds); 
			String springUrl=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
				
			String url = springUrl+"/tc-integrate/spas/getTeamRoster";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			Gson gson = new Gson();
			String params = gson.toJson(httpmap);
			StringBody contentBody = new StringBody(params, Charset.defaultCharset());
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("data", contentBody).setCharset(CharsetUtils.get("UTF-8")).build();
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			httpClient.getConnectionManager().shutdown();
			if(!result.equals("")){
				Gson gson1 = new Gson();
				users = gson1.fromJson(result, new TypeToken<List<SPASUser>>(){}.getType());
				listModel = new DefaultListModel();
				for (int i = 0; i < users.size(); i++) {
					listModel.addElement(users.get(i));
				}
			}else {
				System.out.println("SPAS集成查询失败..");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JPanel getTeamRosterPanel(TreeViewer tv) {
		
		JPanel teamRosterPanel = new JPanel();
		teamRosterPanel.setLayout(new BorderLayout());
		
		JPanel searchPanel = new JPanel();
		JLabel userNameLabel = new JLabel("责任人：");
		JTextField userNmaeText = new JTextField(12);
		searchPanel.add(userNameLabel);
		searchPanel.add(userNmaeText);
		
		JList userList = new JList();
		userList.setModel(listModel);
		userList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(userList);
		
		JPanel addDeletePanel = new JPanel();
		JButton addButton = new JButton("添加");
		JButton deleteButton = new JButton("删除");
		
		addDeletePanel.add(addButton);
		addDeletePanel.add(deleteButton);
		
		teamRosterPanel.add(searchPanel,BorderLayout.NORTH);
		teamRosterPanel.add(jsp,BorderLayout.CENTER);
		teamRosterPanel.add(addDeletePanel,BorderLayout.SOUTH);
		
		userNmaeText.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				try {
					Document doc = arg0.getDocument();
					String text = doc.getText(0, doc.getLength()).trim();
					filterListValue(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				try {
					Document doc = arg0.getDocument();
					String text = doc.getText(0, doc.getLength()).trim();
					filterListValue(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				try {
					Document doc = arg0.getDocument();
					String text = doc.getText(0, doc.getLength()).trim();
					filterListValue(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SPASUser user = (SPASUser) userList.getSelectedValue();
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						TreeItem treeItem = tv.getTree().getSelection()[0];
						treeItem.setText(3, user.getName() + "(" + user.getWorkId() + ")");
						String notes = user.getNotes();
						treeItem.setText(4, notes == null?"":notes);
					}
				});
			}
		});
		
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						TreeItem treeItem = tv.getTree().getSelection()[0];
						treeItem.setText(3, "");
						treeItem.setText(4, "");
					}
				});
			}
		});
		return teamRosterPanel;
	}
	
	private void filterListValue(String textVaule) {
//		List<SPASUser> filterUsers = users.stream().filter(u -> u.getName().contains(textVaule)).collect(Collectors.toList());
		List<SPASUser> filterUsers = users.stream().filter(u -> u.toString().contains(textVaule)).collect(Collectors.toList());
		if(filterUsers != null && filterUsers.size() != 0) {
			listModel.removeAllElements();
			for (int i = 0; i < filterUsers.size(); i++) {
				listModel.addElement(filterUsers.get(i));
			}
		}
	}

}
