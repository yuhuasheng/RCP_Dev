package com.teamcenter.rac.workflow.commands.adhoc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.widgets.Display;

import com.foxconn.decompile.util.SPASUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

public class TeamRosterPanel extends JPanel {

	private Registry appReg;

	protected TCSession session;
	
	private List<SPASUser> users = null;
	
	private DefaultListModel listModel = null;
		
	protected JButton addButton;
	
	protected JButton deleteButton;
	
	protected JList userList;
	
	protected String projectId;

	private Boolean editFlag = null;
	
	public TeamRosterPanel(TCSession paramTCSession) {
		this(paramTCSession, "");
	}
	
	public TeamRosterPanel(TCSession paramTCSession, String projectId) {
		super(true);
//		System.out.println("=============TeamRosterPanel()==============");
		this.appReg = Registry.getRegistry(this);
		this.session = paramTCSession;
		this.projectId = projectId;

		if(!projectId.equals("")) {
			loadTeamRosterData(session,projectId.toLowerCase());
		}
		if(listModel != null) {
			listModel.removeAllElements();
			for (int i = 0; i < users.size(); i++) {
				listModel.addElement(users.get(i));
			}
		}
		
		initializePanel();
	}

	protected void initializePanel() {
		setLayout(new BorderLayout());

		JPanel searchPanel = new JPanel();
		JLabel userNameLabel = new JLabel("責任人：");
		JTextField userNmaeText = new JTextField(12);
		searchPanel.add(userNameLabel);
		searchPanel.add(userNmaeText);

		userList = new JList();
		if(!projectId.equals("") && listModel != null) {
			editFlag = true;
			userList.setModel(listModel);
			userList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} else if (!"".equals(projectId)) {
			if (ObjectUtil.isEmpty(listModel)) {
				listModel = new DefaultListModel<>();
			}
			listModel.addElement("<html><font color=red>專案ID為: " + projectId + ", 查詢TeamRoster失敗或當前專案不存在項目組成員，請聯繫TC管理員進行處理！</font></html>");	
			editFlag = false;
		} else if ("".equals(projectId)) {
			if (ObjectUtil.isEmpty(listModel)) {
				listModel = new DefaultListModel<>();
			}
			listModel.addElement("<html><font color=red size=2.3>當前專案ID為空, TeamRoster人員為空！</font></html>");
			editFlag = false;
		}

		userList.setModel(listModel);
		userList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(userList);

		JPanel addDeletePanel = new JPanel();
		addButton = new JButton("添加");
		deleteButton = new JButton("刪除");

		addDeletePanel.add(addButton);
		addDeletePanel.add(deleteButton);

		if (!editFlag) {
			addButton.setEnabled(false);
			deleteButton.setEnabled(false);
			userNmaeText.setEnabled(false);
		}
		
		add(searchPanel, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		add(addDeletePanel, BorderLayout.SOUTH);

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
	}
	
	private void loadTeamRosterData(TCSession session,String platformFoundIds) {
		try {
//			System.out.println("============loadTeamRosterData===============");
			String result = "";
			HashMap httpmap = new HashMap();
			httpmap.put("platformFoundIds", platformFoundIds); 
			String springUrl=getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
				
			String url = springUrl+"/tc-integrate/spas/getTeamRoster";
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			
			// setConnectTimeout: 设置连接超时时间，单位为毫秒，此处设为5秒
			// setConnectionRequestTimeout： 设置从connect Manager获取Connection超时时间，单位为毫秒，这个属性是新加的属性，因为目前版本是可以共享连接池的，此处设置为5秒
			// setSocketTimeout: 请求获取数据的超时时间，单位为毫秒，如果访问一个接口，多少时间无法返回数据，就直接放弃此次调用，此处设置为10秒
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000)
					.setSocketTimeout(10000).build();
			httpPost.setConfig(requestConfig);
			
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
				
				if (CollUtil.isNotEmpty(users)) {
//					addPLMGroupMail();
					listModel = new DefaultListModel();
				}
				
				for (int i = 0; i < users.size(); i++) {
					listModel.addElement(users.get(i));
				}
			}else {
				System.out.println("SPAS集成查询失败.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SPAS集成查询超时，请联系系统管理员");
		}
		
	}
	
	
//	private void addPLMGroupMail() {
//		users.add(0, new SPASUser("MW00031", "李俊卿", "leo.jq.lee@foxconn.com"));		
//		users.add(1, new SPASUser("MW00035", "鄧雙波", "duncan.sb.deng@foxconn.com"));		
//		users.add(2, new SPASUser("MW00062", "汪蕾", "cheryl.l.wang@foxconn.com"));		
//		users.add(3, new SPASUser("MW00309", "陳龍", "long.l.chen@foxconn.com"));		
//		users.add(4, new SPASUser("MW00313", "李東", "dong.d.li@foxconn.com"));		
//		users.add(5, new SPASUser("MW00037", "戴斌", "sant.b.dai@foxconn.com"));		
//		users.add(6, new SPASUser("MW00272", "劉輝", "hui.h.liu@foxconn.com"));		
//		users.add(7, new SPASUser("WT00110", "黎鵬", "leky.p.li@foxconn.com"));		
//		users.add(8, new SPASUser("MW00333", "餘華勝", "hua-sheng.yu@foxconn.com"));		
//		users.add(9, new SPASUser("MW00343", "範建軍", "jian-jun.fan@foxconn.com"));			
//	}
	
	
	private void filterListValue(String textVaule) {
//		List<SPASUser> filterUsers = users.stream().filter(u -> u.toString().contains(textVaule)).collect(Collectors.toList());
		List<SPASUser> filterUsers = users.stream().filter(u -> u.toString().toUpperCase().contains(textVaule.toUpperCase())).collect(Collectors.toList());
		if(filterUsers != null && filterUsers.size() != 0) {
			listModel.removeAllElements();
			for (int i = 0; i < filterUsers.size(); i++) {
				listModel.addElement(filterUsers.get(i));
			}
		}
	}
	
	/**
	 * 获取字符串类型首选项值
	 * 
	 * @param session
	 * @param scope
	 * @param preferenceName
	 * @return
	 */
	public String getPreference(TCSession session, int scope, String preferenceName) {
		return session.getPreferenceService().getString(scope, preferenceName);
	}

}
