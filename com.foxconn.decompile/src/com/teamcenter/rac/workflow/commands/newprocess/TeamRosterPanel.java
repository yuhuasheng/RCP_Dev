package com.teamcenter.rac.workflow.commands.newprocess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import com.foxconn.decompile.util.SPASUser;
import com.foxconn.tcutils.util.CommonTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.kernel.TCComponentGroup;
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
	private List<SPASUser> showUsers = null;
	private DefaultListModel listModel = null;

	protected JButton addButton;
	protected JButton deleteButton;
	protected boolean isAction = true;
	
	protected JComboBox<String> fromJComboBox;
	protected JTextField userNameText;
	protected JList userList;
	protected String projectId;
	private Boolean editFlag = null;
	
	public TeamRosterPanel(TCSession paramTCSession) {
		this(paramTCSession, "");
	}

	public TeamRosterPanel(TCSession paramTCSession, String projectId) {
		super(true);
		this.appReg = Registry.getRegistry(this);
		this.session = paramTCSession;
		this.projectId = projectId;
		
		if(CollUtil.isEmpty(users)) 
			users = new ArrayList<SPASUser>();
		
		if (!projectId.equals("")) {
			int bomCount = 0;
			ArrayList<SPASUser> teamRosterUsers = loadTeamRosterData(session, projectId.toLowerCase());
			if(teamRosterUsers!=null && teamRosterUsers.size() > 0) {
				for (SPASUser user:teamRosterUsers) {
					String workId = user.getWorkId();
					String sectionName = user.getSectionName();

					if(("CE".equals(sectionName))&&("C0103270".equals(workId)||"C0105693".equals(workId))) {
						user.setSectionName("BOM");
						users.add(bomCount,user);
						bomCount = bomCount + 1;
					} else {
						users.add(user);
					}
					
				}
			}
		}
		
		//添加来自Excel中的数据
		List<SPASUser> users1 = UserNewProcessDialog.users;
		if(users1!=null && users1.size() > 0) {
			users.addAll(users1);
		}
		
		if (CollUtil.isNotEmpty(users)) {
			listModel = new DefaultListModel();
			for (int i = 0; i < users.size(); i++) {
				listModel.addElement(users.get(i));
			}
		}				


//		if (listModel != null) {
//			listModel.removeAllElements();
//			for (int i = 0; i < users.size(); i++) {
//				listModel.addElement(users.get(i));
//			}
//		}
		initializePanel();
		fromJComboBoxActionPerformed();
	}
	
	protected void initializePanel(ArrayList<SPASUser> list) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				SPASUser spasUser = list.get(i);
				List<SPASUser> filterUsers = users.stream().filter(u -> !u.toString().toUpperCase().contains(spasUser.toString().toUpperCase())).collect(Collectors.toList());
				users.addAll(filterUsers);
			}
		}
		initializePanel();
	}
	

	protected void initializePanel() {
		setLayout(new BorderLayout());
		JPanel searchPanel = new JPanel();
		JLabel fromNameLabel = new JLabel("來源：");
		fromJComboBox = new JComboBox<String>();
		fromJComboBox.addItem("Team Roster");
		
		
		fromJComboBox.setPreferredSize(new Dimension(130,25));
		JLabel userNameLabel = new JLabel("責任人：");
		userNameText = new JTextField(9);
		
		searchPanel.add(fromNameLabel);
		searchPanel.add(fromJComboBox);
		searchPanel.add(userNameLabel);
		searchPanel.add(userNameText);

		userList = new JList();
//		if(!projectId.equals("") && listModel != null) {
//			editFlag = true;
//		} else if (!"".equals(projectId)) {
//			if (ObjectUtil.isEmpty(listModel)) {
//				listModel = new DefaultListModel<>();
//			}			
//			listModel.addElement("<html><font color=red>專案ID為: " + projectId + ", 查詢TeamRoster失敗或當前專案不存在項目組成員，請聯繫TC管理員進行處理！</font></html>");	
//			editFlag = false;
//		} else if ("".equals(projectId)) {
//			if (ObjectUtil.isEmpty(listModel)) {
//				listModel = new DefaultListModel<>();
//			}
//			listModel.addElement("<html><font color=red size=2.3>當前專案ID為空, TeamRoster人員為空！</font></html>");
//			editFlag = false;
//		}
		
		if (listModel == null) {
			listModel = new DefaultListModel();
					
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
		
//		if (!editFlag) {
			addButton.setEnabled(false);
			deleteButton.setEnabled(false);
			userNameText.setEnabled(false);
			fromJComboBox.setEditable(false);
//		}
		add(searchPanel, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		add(addDeletePanel, BorderLayout.SOUTH);
		
		
		fromJComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fromJComboBoxActionPerformed();
			 }
		});

		userNameText.getDocument().addDocumentListener(new DocumentListener() {
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
	
	private void fromJComboBoxActionPerformed() {
		String selectedItem = (String) fromJComboBox.getSelectedItem();
		String text = userNameText.getText();
		showUsers = new ArrayList<SPASUser>();
//		if (editFlag) {
			listModel.removeAllElements();
//		}
		
			
		for (SPASUser user:users) {
			
			if(user.getFrom().equals(selectedItem)) {
				showUsers.add(user);
				listModel.addElement(user);
			}
		}		
		
		filterListValue(text);
		
		if (listModel == null || listModel.size() == 0 && selectedItem.equals("Team Roster")) {
			if (!"".equals(projectId)) {
				if (ObjectUtil.isEmpty(listModel)) {
					listModel = new DefaultListModel<>();
				}			
				listModel.addElement("<html><font color=red>專案ID為: " + projectId + ", 查詢TeamRoster失敗或當前專案不存在項目組成員，請聯繫TC管理員進行處理！</font></html>");	
			}
			
			else if ("".equals(projectId)) {
				if (ObjectUtil.isEmpty(listModel)) {
					listModel = new DefaultListModel<>();
				}
				listModel.addElement("<html><font color=red size=2.3>當前專案ID為空, TeamRoster人員為空！</font></html>");				
			}
		} else {			
			addButton.setEnabled(true);
			deleteButton.setEnabled(true);
			userNameText.setEnabled(true);
			fromJComboBox.setEditable(true);
			
		}
	}

	private ArrayList<SPASUser> loadTeamRosterData(TCSession session, String platformFoundIds) {
		ArrayList<SPASUser> teamRosterUsers = null;
		try {
			
//			System.out.println("============loadTeamRosterData===============");
			String result = "";
			HashMap httpmap = new HashMap();
			httpmap.put("platformFoundIds", platformFoundIds);
			String springUrl = getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
			String url = springUrl + "/tc-integrate/spas/getTeamRoster";
//			String url = "http://10.203.163.184" + "/tc-integrate/spas/getTeamRoster";
//			url = "http://127.0.0.1:8068" + "/spas/getTeamRoster";
//			HttpClient httpClient = new DefaultHttpClient();
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
			CloseableHttpResponse response = httpClient.execute(httpPost);			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			} 
			
			httpClient.getConnectionManager().shutdown();
			if (!result.equals("")) {
				Gson gson1 = new Gson();
				teamRosterUsers = gson1.fromJson(result, new TypeToken<List<SPASUser>>() {
				}.getType());
				
			} else {
				System.out.println("SPAS集成查询失败.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SPAS集成查询超时，请联系系统管理员");
		}
		return teamRosterUsers;

	}

	
	private void filterListValue(String textVaule) {
		List<SPASUser> filterUsers = showUsers.stream().filter(u -> u.toString().toUpperCase().contains(textVaule.toUpperCase())).collect(Collectors.toList());
//		ArrayList <SPASUser>filterUsers = new ArrayList<SPASUser>();
//		for (SPASUser showUser:showUsers) {
//			String workId = showUser.getWorkId().toUpperCase();
//			if(workId.contains(textVaule.toUpperCase())) {
//				filterUsers.add(showUser);
//			}
//		}
		
//		if (editFlag) {
//			listModel.removeAllElements();
//		}
		listModel.removeAllElements();
		if (filterUsers != null && filterUsers.size() != 0) {
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
