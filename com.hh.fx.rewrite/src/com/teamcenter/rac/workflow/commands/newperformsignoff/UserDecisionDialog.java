package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.Dialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hh.fox.maintainmainsubstitute.MaintainMainSubstituteDialog;
import com.hh.foxconn.l6reportforms.MatriXBOMReportFormUtil;
import com.hh.fx.rewrite.util.CheckUtil;
import com.hh.fx.rewrite.util.CreateObject;
import com.hh.fx.rewrite.util.DownloadDataset;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.hh.fx.rewrite.util.Utils;
import com.hh.fxn.utils.DBUtil;
import com.hh.tools.checkConficts.dialog.CheckConfictsDialog;
import com.l6.bomchangelist.GenerateChangListUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentEnvelope;
import com.teamcenter.rac.kernel.TCComponentEnvelopeType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

//import oracle.sql.BLOB;


public class UserDecisionDialog extends DecisionDialog {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	private boolean cust_generate_modelBom = false; // 生成MatriXBOM报表
	private boolean cust_check_conflict_part = false; // 生成MatriXBOM报表
	private boolean cust_generate_changelist = false; // 生成BOM ChangeList报表
	private boolean Cust_Add_SubEDA = false; // 审核替代料
	private Registry reg = null;
	private ArrayList<TCComponentBOMLine> list = new ArrayList<>();
	private Connection conn = null;
	private Statement stmt = null;
	private DBUtil dbutil = new DBUtil();

	private TCComponentBOMWindow imancomponentbomwindow;

	public UserDecisionDialog(Dialog paramDialog, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramDialog, paramTCComponentTask, paramTCComponentSignoff);
		// TODO Auto-generated constructor stub
		System.out.println("UserDecisionDialog 1");
		reg = Registry.getRegistry("com.teamcenter.rac.workflow.commands.newperformsignoff.infomessage");
	}

	public UserDecisionDialog(AIFDesktop paramAIFDesktop, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramAIFDesktop, paramTCComponentTask, paramTCComponentSignoff);
		// TODO Auto-generated constructor stub
		System.out.println("UserDecisionDialog 2");
		reg = Registry.getRegistry("com.teamcenter.rac.workflow.commands.newperformsignoff.infomessage");
	}

	@Override
	public void initializeDialog() {
		// TODO Auto-generated method stub
		super.initializeDialog();
		try {
			TCComponentActionHandler[] actionHandlers = psTask.getActionHandlers(TCComponentTask.COMPLETE_ACTION);
			for (int i = 0; i < actionHandlers.length; i++) {
				System.out.println("actionHandlers == " + actionHandlers[i]);
				String actionName = actionHandlers[i].getProperty("object_name");
				System.out.println("actionName ==" + actionName);
				if (actionName.equals("FX_Cust_Generate_ModelBom")) {
					System.out.println("===========生成WO_MODEL报表=================");
					cust_generate_modelBom = true;
				}  
				if (actionName.equals("FX_Cust_Check_Conflict_Part")) {
					System.out.println("===========自动检查是否存在冲突件=================");
					cust_check_conflict_part = true;
				} 
				if (actionName.equals("cust_generate_changelist")) {
					System.out.println("===========生成BOM ChangeList报表=================");
					cust_generate_changelist = true;
				} 
				if (actionName.equals("FX_Cust_Add_SubEDA")) {
					System.out.println("===========判断替代料管理对象中的新增电子料是否全部审核通过=================");
					Cust_Add_SubEDA = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commitDecision() {
		try {
			TCComponentTask task = super.psTask;
			TCComponent[] coms = task.getRelatedComponents("root_target_attachments");
			// 自动生成wo_model_bom报表 -by汪亚洲
			if (cust_generate_modelBom) {
				for (int i = 0; i < coms.length; i++) {
					if (!coms[i].isTypeOf("FX8_L6ECNRevision")) {
						continue;
					}
					TCComponentItemRevision itemRev = (TCComponentItemRevision) coms[i];
					System.out.println("itemRev ==" + itemRev);
					TCComponent[] components = itemRev.getRelatedComponents("FX8_MatrixBOMRel");
					if (components.length <= 0) {
						continue;
					}
					GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
					HashMap<String, String> map = getPreferenceUtil.getHashMapPreference(session,
							TCPreferenceService.TC_preference_site, "FX8_ExportL6PBOMReportForm", "=");
					String id = map.get("L6MatriXBOM");
					System.out.println("id ==" + id);
					TCComponentItem item = Utils.findItem(id);
					if (item == null) {
						continue;
					}
					TCComponentDataset dateset = (TCComponentDataset) item.getLatestItemRevision()
							.getRelatedComponent("IMAN_specification");
					// String filePath = "";
					System.out.println("dataset ==" + dateset);
					String filepath = "";
					if (dateset != null) {
						filepath = DownloadDataset.downloadFile(dateset, true);
					}
					System.out.println("filepath ==" + filepath);
					HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filepath));
					FileOutputStream out = new FileOutputStream(filepath);
					workbook.write(out);
					out.flush();
					if (out != null) {
						out.close();
					}
					if (workbook != null) {
						workbook.close();
					}
					TCComponentDataset dataset1 = CreateObject.createDataSet(session, filepath, "MSExcel",
							sdf.format(new Date()) + "_WO Model BOM", "excel");
					System.out.println("dataset1 ==" + dataset1);
					itemRev.add("IMAN_specification", dataset1);
				}

			}
			// 自动检查是否存在冲突件 -by汪亚洲
			if (cust_check_conflict_part) {
				list = new ArrayList<>();
				String strL6ECNItem = "FX8_L6ECN";
				String strL6ECNSolutionRel = "EC_solution_item_rel";
				for (int i = 0; i < coms.length; i++) {
					System.out.println("coms[i] ==" + coms[i]);
					if (coms[i] instanceof TCComponentItemRevision || !coms[i].isTypeOf(strL6ECNItem)) {
						continue;
					}
					TCComponentItem item = (TCComponentItem) coms[i];
					System.out.println("item ==" + item);
					TCComponent[] components = item.getRelatedComponents(strL6ECNSolutionRel);
					for (int j = 0; j < components.length; j++) {
						if (!(components[j] instanceof TCComponentItemRevision)) {
							continue;
						}
						TCComponentItemRevision rev = (TCComponentItemRevision) components[j];
						System.out.println("rev ==" + rev);
						TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
								.getTypeComponent("RevisionRule");
						TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype
								.getDefaultRule();

						TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
								.getTypeComponent("BOMWindow");
						imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
						TCComponentBOMLine imancomponentPbomline = imancomponentbomwindow
								.setWindowTopLine(rev.getItem(), rev, null, null);
						list.add(imancomponentPbomline);
						getBomline(imancomponentPbomline);
					}
				}

				// 与SAP比对
				CheckConfictsDialog.checkActions(list);
				// 获取与SAP不一致的对象
				ArrayList<TCComponent> listRev = CheckConfictsDialog.getListRev();
				System.out.println("listRev ==" + listRev);
				listRev = new ArrayList<TCComponent>( new HashSet<TCComponent>(listRev));
				if (listRev.size() > 0) {
					// 发送内部邮件
					TCComponentUser user = Utils.getTCSession().getUser();
					TCComponentEnvelopeType type = (TCComponentEnvelopeType) session.getTypeComponent("Envelope");
					// 获取与SAP比对 存在material type不一致的物料名称
					ArrayList<TCComponentItemRevision> mtList = CheckConfictsDialog.getMtList();
					mtList = new ArrayList<TCComponentItemRevision>( new HashSet<TCComponentItemRevision>(mtList));
					// 获取与SAP比对 存在单位不一致的物料名称
					ArrayList<TCComponentItemRevision> uomList = CheckConfictsDialog.getUomList();
					uomList = new ArrayList<TCComponentItemRevision>( new HashSet<TCComponentItemRevision>(uomList));
					// 创建邮件 第一个参数：正文 第二个参数：描述
					TCComponentEnvelope lope = type.create(reg.getString("Email.MSG"),
							reg.getString("DescMT.MSG") + mtList + "  " + reg.getString("DescUOM.MSG") + uomList, null);
					lope.addReceivers(new TCComponent[] { user });// 添加收件人
					lope.addAttachments(listRev.toArray(new TCComponent[listRev.size()]));// 添加附件
					lope.send();
					MessageBox.post(AIFDesktop.getActiveDesktop(), reg.getString("Error1.MSG"),
							reg.getString("Error.MSG"), MessageBox.WARNING);
					return;
				}
			}
			
			//自动生成BOM ChangeList报表
			if(cust_generate_changelist)
			{
				String strL6ECNItem = "FX8_L6ECN";//FX8_L6ECNRevision
				String strL6PCAItemRevType = "FX8_L6PartDRevision";
				String strL6ECNSolutionRel = "EC_solution_item_rel";
				String strL6ECNProblemRel = "EC_problem_item_rel";
				
				for (int i = 0; i < coms.length; i++) 
				{
					if (!coms[i].isTypeOf(strL6ECNItem)) 
					{
						continue;
					}
					TCComponentItem item = (TCComponentItem) coms[i];
					System.out.println("item ==" + item);
					TCComponent[] arrayCompsSolution = item.getRelatedComponents(strL6ECNSolutionRel);//更改后零组件
					TCComponent[] arrayCompsProblem = item.getRelatedComponents(strL6ECNProblemRel);//更改前零组件
					if (arrayCompsSolution.length <= 0 || arrayCompsProblem.length <=0) 
					{
						continue;
					}
					TCComponentItemRevision itemRevPCASolution = null;
					TCComponentItemRevision itemRevPCAProblem = null;
					for(TCComponent comp : arrayCompsSolution)
					{
						if(comp.getType().equals(strL6PCAItemRevType))
						{
							itemRevPCASolution = (TCComponentItemRevision)comp;
							break;
						}
					}
					for(TCComponent comp : arrayCompsProblem)
					{
						if(comp.getType().equals(strL6PCAItemRevType))
						{
							itemRevPCAProblem = (TCComponentItemRevision)comp;
							break;
						}
					}
					if(itemRevPCASolution == null || itemRevPCAProblem == null)
					{
						continue;
					}
					GenerateChangListUtil generateChangeListUtil = new GenerateChangListUtil(session, itemRevPCAProblem, itemRevPCASolution);
					generateChangeListUtil.doMain(coms[i], "IMAN_reference");//ECN下没有规格关系
				}
			}
			if(Cust_Add_SubEDA){
				for (int i = 0; i < coms.length; i++) {
					if (!coms[i].isTypeOf("FX8_PCBEZBOMRevision")) {
						continue;
					}
					TCComponentItemRevision itemRev = (TCComponentItemRevision) coms[i];
					System.out.println("itemRev ==" + itemRev);
					String main_item_id = itemRev.getProperty("item_id");
					String main_item_revision_id = itemRev.getProperty("item_revision_id");
					String sql = "SELECT ISAUDIT FROM RECOMMENTSUBTABLE WHERE MAINITEMID ='" + main_item_id
							+ "' AND MAINITEMREVISION ='" + main_item_revision_id + "'";
					System.out.println("sql ==" + sql);
					MaintainMainSubstituteDialog.getDBInfo("FX_DB_Info");
					ResultSet rs = MaintainMainSubstituteDialog.dbutil.getResultSet(MaintainMainSubstituteDialog.stmt, sql);
					boolean isRelease = true;
					while (rs.next()) {
						int isAudit = rs.getInt("ISAUDIT");
						System.out.println("isAudit ==" + isAudit);
						if (isAudit != 1) {
							isRelease = false;
							break;
						}
					}
					if(!isRelease){
						MessageBox.post(AIFDesktop.getActiveDesktop(), reg.getString("SubEDANoAudit.MSG"),
								reg.getString("Info.MSG"), MessageBox.WARNING);
					}
				}
			
			}
			super.commitDecision();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 遍历BOMLINE 获取对应的对象
	private ArrayList<TCComponentBOMLine> getBomline(TCComponentBOMLine bomline) {
		try {
			AIFComponentContext[] aifs = bomline.getChildren();
			for (int i = 0; i < aifs.length; i++) {
				TCComponentBOMLine childBomline = (TCComponentBOMLine) aifs[i].getComponent();
				list.add(childBomline);
				if (childBomline.getChildrenCount() > 0) {
					getBomline(childBomline);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return list;
	}

}
