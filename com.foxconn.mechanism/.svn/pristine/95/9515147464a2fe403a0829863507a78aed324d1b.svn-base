package com.foxconn.mechanism.exportcreomodel;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import com.foxconn.mechanism.exportcreomodel.domain.ModelInfo;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ExportCeroModelAction extends AbstractAIFAction {

	private AbstractAIFApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private String creoExportPreferenceName = "D9_Creo_Export_Attr"; // Creo3D图档模型名称
	private String itemRevPropety = "";
	
	public ExportCeroModelAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();		
		reg = Registry.getRegistry("com.foxconn.mechanism.exportcreomodel.exportcreomodel");
	}
	
	@Override
	public void run() {
		try {			
			//获取选中的目标对象
			InterfaceAIFComponent aif = app.getTargetComponent();
			InterfaceAIFComponent com = aif;
			TCComponentBOMLine topLine = null;
			TCComponentBOMLine bomLine = (TCComponentBOMLine) com;
			if (!bomLine.getCachedWindow().getTopBOMLine().equals(bomLine)) {
				MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("selecErr2.MSG"), reg.getString("WARNING.MSG"),
						MessageBox.WARNING);
				return;
			}
			//获取首选项值
			itemRevPropety = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, creoExportPreferenceName);
			if ("".equals(itemRevPropety)) {
				MessageBox.post(AIFUtility.getActiveDesktop(), creoExportPreferenceName + reg.getString("Preference.MSG"), 
						reg.getString("Common.MSG"), MessageBox.WARNING);
				return;
			}
			topLine = bomLine;
			List<ModelInfo> modelInfoList = new ArrayList<ModelInfo>();
			//设置BOMLine属性字段信息
			ModelInfo info = setPropertyParams(bomLine);
			if (!modelInfoList.contains(info)) {
				modelInfoList.add(info);
			}
			//获取子BOMLine
			getChildBOMline(topLine, modelInfoList);
			for (ModelInfo modelInfo : modelInfoList) {
				System.out.println(modelInfo.getItemRevId());
				System.out.println(modelInfo.getObjectName());
				System.out.println(modelInfo.getNewModelName());
			}
			if (modelInfoList != null && modelInfoList.size() > 0) {
//				new Thread() {
//					public void run() {
				new ExportCeroModelDialog(session, modelInfoList, reg);
			}
//				}.start();				
//			}			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取子BOMLine
	 * 
	 * @param bomLine
	 * @param modelInfoList
	 * @throws TCException
	 */
	private void getChildBOMline(TCComponentBOMLine bomLine, List<ModelInfo> modelInfoList) throws TCException {
		//刷新BOMLine
		bomLine.refresh();
		AIFComponentContext[] childBomlines = bomLine.getChildren();
		if (null == childBomlines || childBomlines.length <= 0) {
			return;
		}
		for (int i = 0; i < childBomlines.length; i++) {
			TCComponentBOMLine childBomline = (TCComponentBOMLine) childBomlines[i].getComponent();
			//设置BOMLine属性字段信息
			ModelInfo info = setPropertyParams(childBomline);
			if (!modelInfoList.contains(info)) {
				modelInfoList.add(info);
			}
			AIFComponentContext[] children = childBomline.getChildren();
			if (children != null && children.length > 0) {
				getChildBOMline(childBomline, modelInfoList);
			}
		}
	}

	/**
	 * 设置BOMLine属性字段信息
	 * 
	 * @param bomLine
	 * @throws TCException
	 */
	private ModelInfo setPropertyParams(TCComponentBOMLine bomLine) throws TCException {
		//零件ID
		String itemId = bomLine.getProperty("bl_item_item_id");
		System.out.println("==>> itemId: " + itemId);
		//版本号
		String version = bomLine.getProperty("bl_rev_item_revision_id");
		System.out.println("==>> version: " + version);
		TCComponentItemRevision itemRevision = bomLine.getItemRevision();
		//名称
//		String objectName = bomLine.getProperty("bl_item_object_name");
		String objectName = itemRevision.getProperty(itemRevPropety);
		System.out.println("==>> objectName: " + objectName);
		ModelInfo info = new ModelInfo();
		String itemRevId = itemId + "/" + version;
		info.setItemRevId(itemRevId);
		info.setObjectName(objectName);
		info.setNewModelName(objectName);
		return info;
	}

}
