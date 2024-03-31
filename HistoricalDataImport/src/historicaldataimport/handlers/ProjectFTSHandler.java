package historicaldataimport.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.loose.query._2007_06.SavedQuery.SavedQueryResults;
import com.teamcenter.soa.client.model.ModelObject;

import historicaldataimport.domain.Constants;
import historicaldataimport.domain.Project2Info;
import historicaldataimport.progress.BooleanFlag;
import historicaldataimport.progress.IProgressDialogRunnable;
import historicaldataimport.progress.LoopProgerssDialog;
import historicaldataimport.utils.JDBCUtil;
import historicaldataimport.utils.PropertitesUtil;
import historicaldataimport.utils.Utils;

public class ProjectFTSHandler extends AbstractHandler{
	
	private Registry reg = Registry.getRegistry("historicaldataimport.hdi");
	private Boolean condition = null; // 加载标识

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		TCSession session = (TCSession) app.getSession();

		LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null,
				reg.getString("正在同步，请稍等..."));
		loopProgerssDialog.run(true, new IProgressDialogRunnable() {

			@Override
			public void run(BooleanFlag stopFlag) {
				try {
					if (stopFlag.getFlag()) { // 监控是否要让停止后台任务
						System.out.println("被中断了");
						condition = false;
						return;
					}
					
					List<String> spasIds = queryTCProjectSpasId(session);
					if(spasIds == null) {
						throw new Exception("文件中没有可导入的数据！");
					}
					List<Project2Info> project1InfoList = JDBCUtil.queryProjectInfo(spasIds);
					Map<String, List<String>> projectPhaseMap = JDBCUtil.queryProjectPhase(spasIds);
					List<Project2Info> project2InfoList = projectInfoAddPhase(project1InfoList,projectPhaseMap);
					List<Project2Info> project3InfoList = projectInfoAddBU(project2InfoList);
					projectFolderSync(session,project3InfoList);
					
					if (stopFlag.getFlag()) {
						condition = false;
					} else {
						condition = true;
					}
					stopFlag.setFlag(true); // 执行完毕后把标志位设置为停止，好通知给进度框
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openInformation(shell, reg.getString("prompt"), e.getMessage());
					stopFlag.setFlag(true);
				}
			}
		});
		
		MessageDialog.openInformation(shell, reg.getString("prompt"), "专案历史文件夹同步成功！");
		
//		try {
//			List<String> spasIds = queryTCProjectSpasId(session);
//			if(spasIds == null) {
//				MessageDialog.openInformation(shell, reg.getString("prompt"), "TC系统中未找到专案文件夹！");
//				return null;
//			}
//			List<Project2Info> project1InfoList = JDBCUtil.queryProjectInfo(spasIds);
//			Map<String, List<String>> projectPhaseMap = JDBCUtil.queryProjectPhase(spasIds);
//			List<Project2Info> project2InfoList = projectInfoAddPhase(project1InfoList,projectPhaseMap);
//			List<Project2Info> project3InfoList = projectInfoAddBU(project2InfoList);
//			projectFolderSync(session,project3InfoList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;
	}
	
	private List<String> queryTCProjectSpasId(TCSession session) throws Exception {
		List<String> spasIds = null;
		SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {"*"});
		ModelObject[] projectFolders = queryResults.objects;
		if(projectFolders.length == 0) {
			return spasIds;
		}
		spasIds = new ArrayList<String>();
		for (int i = 0; i < projectFolders.length; i++) {
			TCComponentFolder projectFolder = (TCComponentFolder) projectFolders[i];
			String spasId = projectFolder.getProperty("d9_SPAS_ID");
			String projectId = spasId.substring(1, spasId.length());
			spasIds.add(projectId);
		}
		return spasIds;
	}
	
	private List<Project2Info> projectInfoAddPhase(List<Project2Info> projectInfoList, Map<String, List<String>> projectPhaseMap){
		for (int i = 0; i < projectInfoList.size(); i++) {
			Project2Info project2Info = projectInfoList.get(i);
			String spasId = project2Info.getSpasId();
			List<String> phaseList = projectPhaseMap.get(spasId);
			project2Info.setPhaseList(phaseList);
		}
		return projectInfoList;
	}
	
	private List<Project2Info> projectInfoAddBU(List<Project2Info> projectInfoList) {
		for (int i = 0; i < projectInfoList.size(); i++) {
			Project2Info project2Info = projectInfoList.get(i);
			String customerName = project2Info.getCustomerName();
			String productLine = project2Info.getProductLine();
			String buName = getBUName(customerName, productLine);
			project2Info.setBu(buName);
		}
		return projectInfoList;
	}
	
	/**
	 * 根据客户、产品线获取BU
	 * @param customerName
	 * @param productLineName
	 * @return
	 */
	private String getBUName(String customerName,String productLineName){
        customerName = customerName.replaceAll(" ","char(32)").replaceAll("_","char(95)");
        productLineName = productLineName.replaceAll(" ","char(32)").replaceAll("_","char(95)");
        String buKey = customerName + "_" + productLineName;
        return PropertitesUtil.props.getProperty(buKey);
    }
	
	private void projectFolderSync(TCSession session,List<Project2Info> projectInfoList) throws Exception{
		for (int i = 0; i < projectInfoList.size(); i++) {
			Project2Info project2Info = projectInfoList.get(i);
			List<String> phaseList = project2Info.getPhaseList();
			String bu = project2Info.getBu();
			String customerName = project2Info.getCustomerName();
			Map<String, List<String>> templatesInfoMap = null;
			if("DT".equals(bu)) {
				templatesInfoMap = Utils.getDTTemplatesInfo(session,customerName, phaseList);
			}else if(bu == null) {
				continue;
			}else {
				//templatesInfoMap = Utils.getPMTemplatesInfo(session, bu);
			}
			syncDepartmentFolder(session,project2Info, templatesInfoMap);
		}
	}
	
	private void syncDepartmentFolder(TCSession session, Project2Info project2Info, Map<String, List<String>> dtTemplatesInfoMap) throws Exception {
		String spasId = "p" + project2Info.getSpasId();
		SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {spasId});
		if(queryResults.objects.length == 0) {
			return;
		}
		ModelObject[] objs = queryResults.objects;
		ModelObject projectFolder = objs[0];
		List<String> exisingDepartmentList = getDepartmentByProject(session,projectFolder);
		System.out.println("现有部门：" + exisingDepartmentList + "，size：" + exisingDepartmentList.size());
		
		List<String> tempDepartList = new ArrayList<String>();
		if(dtTemplatesInfoMap == null) {
			return;
		}
		for(Entry<String, List<String>> map:dtTemplatesInfoMap.entrySet()) {
			String key = map.getKey();
			tempDepartList.add(key);
		}
		System.out.println("模板部门：" + tempDepartList + "，size：" + tempDepartList.size());
		
		List<String> newDepart = tempDepartList.stream().filter(item -> !exisingDepartmentList.contains(item)).collect(Collectors.toList());
		System.out.println("新增部门-->" + newDepart  + "，size：" + newDepart.size());
		for (int i = 0; i < newDepart.size(); i++) {
			String department = newDepart.get(i);
			ModelObject departFolder = Utils.createDepartFolder(session, projectFolder, department);
			List<String> projectPhaseInfos = dtTemplatesInfoMap.get(department);
			Utils.createPhaseArchiveFolder(session,projectPhaseInfos, departFolder);
		}
	}
	
	private List<String> getDepartmentByProject(TCSession session, ModelObject obj) throws Exception {
		List<String> departList = new ArrayList<String>();
		TCComponentFolder projectFolder = (TCComponentFolder) obj;
		AIFComponentContext[] related = projectFolder.getRelated("contents");
		for (int i = 0; i < related.length; i++) {
			InterfaceAIFComponent component = related[i].getComponent();
			String type = component.getType();
			if("D9_FunctionFolder".equals(type)) {
				String folderName = component.getProperty("object_name");
				departList.add(folderName);
			}
		}
		return departList;
	}
	
}
