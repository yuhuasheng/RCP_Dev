package com.foxconn.mechanism.jurisdiction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.foxconn.mechanism.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.kernel.TCPreferenceService;

import org.eclipse.jface.fieldassist.AutoCompleteField;

public class MyEditingSupport1 extends EditingSupport {

	private static String[] userName = null;
	private TreeViewer tv;
	private int index;
	private TextCellEditor cellEditor = null; 
	private Text text= null;
	private TreeItem treeItem2 = null;
	private static Map<String, String> userInfoMap = new HashMap<String, String>();
	
	static{
		try {
			String result = "";
			CloseableHttpClient client = HttpClients.createDefault();
			//String springUrl=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
			
			
			HttpPost httpPost = new HttpPost(""+"/tc-integrate/spas/getSPASUser");
			CloseableHttpResponse response = client.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
			response.close();
			if(!result.equals("")){
				Gson gson = new Gson();
				List<SPASUser> users = gson.fromJson(result, new TypeToken<List<SPASUser>>(){}.getType());
				userName = new String[users.size()];
				for (int i = 0; i < users.size(); i++) {
					SPASUser spasUser = users.get(i);
					String name = spasUser.getName();
					String workId = spasUser.getWorkId();
					String notes = spasUser.getNotes();
					String nameInfo = name + "(" + workId + ")";
					userName[i] = nameInfo;
					userInfoMap.put(nameInfo, notes);
				}
			}else {
				System.out.println("SPAS集成查询失败..");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MyEditingSupport1(Shell shell,ColumnViewer viewer,int index) {
		super(viewer);
		this.tv = (TreeViewer) viewer;
		this.index = index;
		cellEditor = new TextCellEditor((Composite)getViewer().getControl(), SWT.NONE);
		text = (Text) cellEditor.getControl();
		// 添加失去焦点的监听
		text.addListener(SWT.FocusOut, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if(index == 3) {
					String notes1 = "";
					String text2 = text.getText();
					String notes = userInfoMap.get(text2);
					if(notes != null) {
						notes1 = notes;
					}
					treeItem2.setText(4, notes1);
				}
			}
		});
	}

	@Override
	protected CellEditor getCellEditor(Object obj) {
		if(index == 3 || index == 4) {
			return cellEditor;
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object obj) {
		if(index == 3 || index == 4) {
			return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object obj) {
		treeItem2 = tv.getTree().getSelection()[0];
		if(userName.length > 0 || userName != null) {
			new AutoCompleteField(text, new TextContentAdapter(), userName);
		}
		return tv.getTree().getSelection()[0].getText(index);
	}

	@Override
	protected void setValue(Object obj, Object obj1) {
		treeItem2.setText(index, obj1.toString());
	}

}
