package historicaldataimport.dialog;

import java.util.ArrayList;
import java.util.List;

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

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import historicaldataimport.domain.Constants;
import historicaldataimport.domain.SPASInfo;
import historicaldataimport.utils.JDBCUtil;
import historicaldataimport.utils.Utils;

public class ProjectSPDialog extends Dialog{
	
	private Shell shell = null;
	private Shell parentShell = null;
	private TCSession session = null;
	private Registry reg = null;
	private Text logText = null;
	List<String> seriesIds = null;
	List<String> projectIds = null;
	private static final Logger log = LoggerFactory.getLogger(ProjectSPDialog.class);

	public ProjectSPDialog(Shell parentShell,TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("historicaldataimport.hdi");
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(700, 500);
		shell.setText(reg.getString("SPDialogTitle"));
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
		
		try {
			spasInfoSync(new String[] {Constants.SERIES_TYPE,Constants.PROJECT_TYPE});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
	
	private void spasInfoSync(String[] syncTypes) throws Exception {
		for (int j = 0; j < syncTypes.length; j++) {
			String syncType = syncTypes[j];
			List<SPASInfo> tcInfos = getTCInfo(syncType);
			List<SPASInfo> spasInfos = getSPASInfo(syncType);
			if(syncType.equals(Constants.SERIES_TYPE)) {
				writeLogText("開始比對【TC系列】【SPAS系列】名稱差異...\n");
				log.info("開始比對【TC系列】【SPAS系列】名稱差異...");
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				writeLogText("開始比對【TC專案】【SPAS專案】名稱差異...\n");
				log.info("開始比對【TC專案】【SPAS專案】名稱差異...");
			}
			
//			Utils.setBypass(session);
//			for (int i = 0; i < spasInfos.size(); i++) {
//				SPASInfo spasInfo = spasInfos.get(i);
//				String spasId = spasInfo.getSpasId();
//				String name = spasInfo.getName().trim();
//				TCComponent[] project = Utils.executeRCPQuery(session, Constants.FIND_PROJECT, new String[] {Constants.PROJECT_ID}, new String[] {spasId});
//				if(project.length == 0) {
//					writeLogText("未找到ID為:" + spasId +"【專案】項目，跳過...");
//					continue;
//				}
//				String string = project[0].getProperty(Constants.OBJECT_NAME);
//				if(string.equals(name)) {
//					continue;
//				}
//				writeLogText("正在同步【專案】項目，ID：" + spasId + " 名稱：" + name + "\n");
//				name = name.replaceAll("\\*","_").replaceAll("\\.","_").replaceAll("%","_").replaceAll("@","_");
//				project[0].setProperty(Constants.OBJECT_NAME, name);
//			}
//			Utils.closeBypass(session);
			
			List<SPASInfo> spasSyncTCData = getSpasSyncTCData(tcInfos, spasInfos);
			if(syncType.equals(Constants.SERIES_TYPE)) {
				writeLogText("系列差異數量：" + spasSyncTCData.size() + "\n");
				log.info("系列差異數量：" + spasSyncTCData.size());
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				writeLogText("專案差異數量：" + spasSyncTCData.size() + "\n");
				log.info("專案差異數量：" + spasSyncTCData.size());
			}
			if(spasSyncTCData.size() != 0) {
				Utils.setBypass(session);
				for (int i = 0; i < spasSyncTCData.size(); i++) {
					SPASInfo spasInfo = spasSyncTCData.get(i);
					String spasId = spasInfo.getSpasId();
					String name = spasInfo.getName();
					TCComponent[] queryResult = Utils.executeRCPQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {spasId});
					queryResult[0].setProperty(Constants.OBJECT_NAME, name);
					if(syncType.equals(Constants.SERIES_TYPE)) {
						writeLogText("正在同步【系列】文件夹，ID：" + spasId + " 名稱：" + name + "\n");
						log.info("正在同步【系列】文件夹，ID：" + spasId + " 名稱：" + name);
					}else if(syncType.equals(Constants.PROJECT_TYPE)) {
						writeLogText("正在同步【專案】文件夹，ID：" + spasId + " 名稱：" + name + "\n");
						log.info("正在同步【專案】文件夹，ID：" + spasId + " 名稱：" + name);
						TCComponent[] project = Utils.executeRCPQuery(session, Constants.FIND_PROJECT, new String[] {Constants.PROJECT_ID}, new String[] {spasId});
						if(project.length == 0) {
							writeLogText("未找到ID為:" + spasId +"【專案】項目，跳過...\n");
							log.info("未找到ID為:" + spasId +"【專案】項目，跳過...");
							continue;
						}
						writeLogText("正在同步【專案】項目，ID：" + spasId + " 名稱：" + name + "\n");
						log.info("正在同步【專案】項目，ID：" + spasId + " 名稱：" + name);
						name = name.replaceAll("\\*","_").replaceAll("\\.","_").replaceAll("%","_").replaceAll("@","_");
						project[0].setProperty(Constants.OBJECT_NAME, name);
					}
				}
				Utils.closeBypass(session);
				if(syncType.equals(Constants.SERIES_TYPE)) {
					writeLogText("系列同步名稱結束！\n");
					log.info("系列同步名稱結束！");
				}else if(syncType.equals(Constants.PROJECT_TYPE)) {
					writeLogText("專案同步名稱結束！\n");
					log.info("專案同步名稱結束！");
				}
			}else {
				if(syncType.equals(Constants.SERIES_TYPE)) {
					writeLogText("系列同步名稱結束！\n");
					log.info("系列同步名稱結束！");
				}else if(syncType.equals(Constants.PROJECT_TYPE)) {
					writeLogText("專案同步名稱結束！\n");
					log.info("專案同步名稱結束！");
				}
			}
		}
	}
	
	private List<SPASInfo> getTCInfo(String syncType){
		List<SPASInfo> spasInfos = null;
		try {
			String key = Constants.SPAS_ID;
			String value = "*";
			if(syncType.equals(Constants.SERIES_TYPE)) {
				writeLogText("開始查詢TC全部系列...\n");
				log.info("開始查詢TC全部系列...");
				value = Constants.SERIES_TYPE + value;
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				writeLogText("開始查詢TC全部專案...\n");
				log.info("開始查詢TC全部專案...");
				value = Constants.PROJECT_TYPE + value;
			}
			TCComponent[] queryResult = Utils.executeRCPQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {key}, new String[] {value});
			if(syncType.equals(Constants.SERIES_TYPE)) {
				writeLogText("TC系列數量：" + queryResult.length + "\n");
				log.info("TC系列數量：" + queryResult.length);
				seriesIds = new ArrayList<String>();
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				writeLogText("TC專案數量：" + queryResult.length + "\n");
				log.info("TC專案數量：" + queryResult.length);
				projectIds = new ArrayList<String>();
			}
			
			spasInfos = new ArrayList<SPASInfo>();
			for (int i = 0; i < queryResult.length; i++) {
				TCComponentFolder projectFolder = (TCComponentFolder) queryResult[i];
				String spasId = projectFolder.getProperty(Constants.SPAS_ID);
				String name = projectFolder.getProperty(Constants.OBJECT_NAME);
				if(syncType.equals(Constants.SERIES_TYPE)) {
					writeLogText("系列ID：" + spasId + " 系列名稱：" + name + "\n");
					log.info("系列ID：" + spasId + " 系列名稱：" + name);
					seriesIds.add(spasId.substring(1, spasId.length()));
				}else if(syncType.equals(Constants.PROJECT_TYPE)) {
					writeLogText("專案ID：" + spasId + " 專案名稱：" + name + "\n");
					log.info("專案ID：" + spasId + " 專案名稱：" + name);
					projectIds.add(spasId.substring(1, spasId.length()));
				}
				SPASInfo spasInfo = new SPASInfo();
				spasInfo.setSpasId(spasId);
				spasInfo.setName(name);
				spasInfos.add(spasInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spasInfos;
	}
	
	private List<SPASInfo> getSPASInfo(String syncType){
		List<SPASInfo> spasInfos = null;
		try {
			if(syncType.equals(Constants.SERIES_TYPE)) {
				writeLogText("開始查詢SPAS全部系列...\n");
				log.info("開始查詢SPAS全部系列...");
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				writeLogText("開始查詢SPAS全部專案...\n");
				log.info("開始查詢SPAS全部專案...");
			}
			String sql = getQuerySQL(syncType);
			List<String> spasIds = null;
			if(syncType.equals(Constants.SERIES_TYPE)) {
				spasIds = seriesIds;
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				spasIds = projectIds;
			}
			spasInfos = JDBCUtil.querySpasInfo(sql,spasIds,syncType);
			if(syncType.equals(Constants.SERIES_TYPE)) {
				writeLogText("SPAS系列數量：" + spasInfos.size() + "\n");
				log.info("SPAS系列數量：" + spasInfos.size());
			}else if(syncType.equals(Constants.PROJECT_TYPE)) {
				writeLogText("SPAS專案數量：" + spasInfos.size() + "\n");
				log.info("SPAS專案數量：" + spasInfos.size());
			}
			for (int i = 0; i < spasInfos.size(); i++) {
				SPASInfo spasInfo = spasInfos.get(i);
				String spasId = spasInfo.getSpasId();
				String name = spasInfo.getName();
				if(syncType.equals(Constants.SERIES_TYPE)) {
					writeLogText("系列ID：" + spasId + " 系列名稱：" + name + "\n");
					log.info("系列ID：" + spasId + " 系列名稱：" + name);
				}else if(syncType.equals(Constants.PROJECT_TYPE)) {
					writeLogText("專案ID：" + spasId + " 專案名稱：" + name + "\n");
					log.info("專案ID：" + spasId + " 專案名稱：" + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spasInfos;
	}
	
	private List<SPASInfo> getSpasSyncTCData(List<SPASInfo> tcInfos, List<SPASInfo> spasInfos){
		List<SPASInfo> spasSyncTCData = new ArrayList<SPASInfo>();
		for (int i = 0; i < tcInfos.size(); i++) {
			SPASInfo tcInfo = tcInfos.get(i);
			String spasId = tcInfo.getSpasId();
			String name = tcInfo.getName().trim();
			for (int j = 0; j < spasInfos.size(); j++) {
				SPASInfo spasInfo = spasInfos.get(j);
				String spasId2 = spasInfo.getSpasId();
				String name2 = spasInfo.getName().trim();
				if(spasId.equals(spasId2)) {
					if(!name.equals(name2)) {
						spasSyncTCData.add(spasInfo);
					}
				}
			}
		}
		return spasSyncTCData;
	}
	
	private String getQuerySQL(String syncType){
		List<String> spasIds = null;
		if(syncType.equals(Constants.SERIES_TYPE)) {
			spasIds = seriesIds;
		}else if(syncType.equals(Constants.PROJECT_TYPE)) {
			spasIds = projectIds;
		}
		StringBuilder params = new StringBuilder();
		for (int i = 0; i < spasIds.size(); i++) {
			if(i == 0) {
				params.append("?");
			}else {
				params.append(",?");
			}
		}
		StringBuilder sql = new StringBuilder();
		if(syncType.equals(Constants.SERIES_TYPE)) {
			sql.append("SELECT ID,SERIES_NAME NAME FROM view_project_series WHERE ID IN");
		}else if(syncType.equals(Constants.PROJECT_TYPE)) {
			sql.append("SELECT ID, NAME FROM view_platform_found WHERE ID IN");
		}
		sql.append(" (" + params.toString() + ")");
		return sql.toString();
	}
}
