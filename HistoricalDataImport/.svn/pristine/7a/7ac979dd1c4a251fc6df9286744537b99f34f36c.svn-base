package historicaldataimport.dialog;

import java.io.File;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.loose.query._2007_06.SavedQuery.SavedQueryResults;
import com.teamcenter.soa.client.model.ModelObject;

import historicaldataimport.domain.Constants;
import historicaldataimport.domain.ExcelRowData;
import historicaldataimport.utils.Utils;

public class ProjectTMLDialog extends Dialog{
	
	private Shell shell = null;
	private Shell parentShell = null;
	private TCSession session = null;
	private Registry reg = null;
	private Text logText = null;
	private File teamRosterTemplate = null;
	private String spas = "spas";
	private String admin = "admin";
	private String tcAdmin = "tcadmin";
	private ModelObject teamAdmin = null;
	private ModelObject adminGroupMember = null;
	private ModelObject teamPrivileged1 = null;
	private ModelObject privilegedGroupMember1 = null;
	private ModelObject teamPrivileged2 = null;
	private ModelObject privilegedGroupMember2 = null;
	private static final Logger log = LoggerFactory.getLogger(ProjectTMLDialog.class);

	public ProjectTMLDialog(Shell parentShell,TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("historicaldataimport.hdi");
		initUI();
	}
	
	//构建界面
	private void initUI() {
		String serverAddress = session.getSoaConnection().getServerAddress();
		if(serverAddress.contains("192")) {
			spas = "spas1";
			admin = "apadmin";
		}
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(700, 500);
		shell.setText(reg.getString("TMLDialogTitle"));
		shell.setLayout(new FillLayout());
		Utils.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		GridData gridData0 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData0.grabExcessHorizontalSpace = true;
		gridData0.heightHint = 360;
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		logText.setLayoutData(gridData0);
		logText.setEditable(false);
		
		shell.open();
		
		//重新开线程处理业务，解决UI界面卡死问题
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					syncProjectTML();
				} catch (Exception e2) {
					writeLogText(e2.getMessage());
					e2.printStackTrace();
				}
			}
		}).start();
		
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void writeLogText(String message){
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				logText.append(message);
			}
		});
    }	

	private void syncProjectTML() throws Exception{
		writeLogText("開始同步【TC_Project_TML】...\n");
		log.info("開始同步【TC_Project_TML】...");
		SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {"p*"});
		if(queryResults.numOfObjects > 0) {
			writeLogText("專案文件夾數量：" + queryResults.numOfObjects + "\n");
			log.info("專案文件夾數量：" + queryResults.numOfObjects);
			if(teamRosterTemplate == null) {
				writeLogText("開始讀取【teamRoster】模板...\n");
				log.info("開始讀取【teamRoster】模板...");
				teamRosterTemplate = Utils.getTeamRosterTemplate(session);
			}
			for (int i = 0; i < queryResults.numOfObjects; i++) {
				TCComponentFolder projectFolder = (TCComponentFolder) queryResults.objects[i];
				String projectSpasId = projectFolder.getProperty(Constants.SPAS_ID);
				String customerName = Utils.getCustomerName(session,projectSpasId);
				String buName = Utils.getBUName(session, projectSpasId);
				if("".equals(customerName) || "".equals(buName)) {
					writeLogText("專案【" + projectSpasId+ "】未找到BU，跳過...\n");
					log.info("專案【" + projectSpasId+ "】未找到BU，跳過...");
					continue;
				}
				writeLogText("查詢【"+ projectSpasId + "】的TC_Project...\n");
				log.info("查詢【"+ projectSpasId + "】的TC_Project...");
				ModelObject project = Utils.queryProject(session, projectSpasId);
				if(project == null) {
					writeLogText("未查詢到【"+ projectSpasId + "】的TC_Project，跳過...\n");
					log.info("未查詢到【"+ projectSpasId + "】的TC_Project，跳過...");
					continue;
				}
				TCComponentProject project2 = (TCComponentProject) project;
				project2.refresh();
				String isActive = project2.getProperty("is_active");
				if("否".equals(isActive)) {
					writeLogText("專案【" + projectSpasId+ "】非活動狀態，跳過...\n");
					log.info("專案【" + projectSpasId+ "】非活動狀態，跳過...");
					continue;
				}
				writeLogText("獲取專案【" + projectSpasId+ "】TeamRoster...\n");
				log.info("獲取專案【" + projectSpasId+ "】TeamRoster...");
				List<String> teamRoster = Utils.getTeamRoster(teamRosterTemplate,customerName, buName);
				if(teamAdmin == null) {
					teamAdmin = Utils.getUser(session, admin);
					adminGroupMember = Utils.getUserGroupMember(session, teamAdmin);
				}
				if(teamPrivileged1 == null) {
					teamPrivileged1 = Utils.getUser(session, tcAdmin);
					privilegedGroupMember1 = Utils.getUserGroupMember(session, teamPrivileged1);
				}
				if(teamPrivileged2 == null) {
					teamPrivileged2 = Utils.getUser(session, spas);
					privilegedGroupMember2 = Utils.getUserGroupMember(session, teamPrivileged2);
				}
				ModelObject[] teamMember = Utils.getTeamRosterUser(session, teamRoster);
				ModelObject[] memberGroupMember = Utils.getTeamRosterGroupMember(session, teamMember);
				Utils.modifyProjects(session, project, teamAdmin, adminGroupMember, 
						new ModelObject[] {teamPrivileged1,teamPrivileged2}, new ModelObject[] {privilegedGroupMember1,privilegedGroupMember2}, 
						teamMember, memberGroupMember,true);
			}
		}
		writeLogText("同步【TC_Project_TML】結束！\n");
		log.info("同步【TC_Project_TML】結束！...");
	}
}
