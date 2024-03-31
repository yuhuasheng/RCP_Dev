package com.foxconn.decompile.service;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

import com.foxconn.decompile.constants.DatasetEnum;
import com.foxconn.decompile.util.CommonTools;
import com.foxconn.decompile.util.TCUtil;
import com.foxconn.sdebom.export.HPEBOMExportService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.services.rac.workflow.WorkflowService;

public class HpEbomExportService {

	private TCSession session;

	private TCComponent[] coms;

	private TCComponentTask task;

	private TCComponentTask root;

	private String D9_BOMTOPNODEREVISION = "D9_BOMTopNodeRevision";
//	private String D9_BOMTOPNODEREVISION = "ItemRevision";

	private String processName;

	private TCComponentDataset dataset;
	
	private TCComponentItemRevision itemRev;
	
	private String itemId = null;
	
	private String version = null;
	private String objectName = null;
	
	private String D9_Release = "D9_Release";
	
	private String Append = "Append";
	
	public HpEbomExportService(TCComponentTask task, TCSession session, TCComponent[] coms) {
		super();
		this.session = session;
		this.coms = coms;
		try {
			root = task.getRoot();
			processName = root.getProperty("job_name");
		} catch (TCException e) {
			e.printStackTrace();
		}

	}

	public void export() {	
		try {
			TCUtil.setBypass(session); // 开启旁路
			itemRev = filterRootTarget();
			if (CommonTools.isEmpty(itemRev)) {
				System.out.println("==>> 流程名称为: " + processName + ", 不存在符合条件的对象, 请检查后再执行！");
				return;
			}

			itemId = itemRev.getProperty("item_id");
			System.out.println("==>> itemId: " + itemId);
			version = itemRev.getProperty("item_revision_id");
			System.out.println("==>> version: " + version);
			
			objectName = itemRev.getProperty("object_name");
			System.out.println("==>> object_name: " + objectName);
			
			if (!TCUtil.isBom(itemRev)) {
				throw new Exception("==>> 流程名称为: " + processName + ", 零组件ID为: "+ itemId + ", 版本号为:" + version + ", 不是BOM结构，请检查后再执行！");
			}
			
			String filePath = "D:\\maxnerva\\TC\\DP Teamcenter开发文档\\Conn _Cable_Import_Template.xlsx";
			
			filePath = generateEBOMExcel(); // 发送结构管理器，生成EBOM Excel文件
			if (CommonTools.isEmpty(filePath)) {
				throw new Exception("==>> 流程名称为: " + processName + ", 零组件ID为: "+ itemId + ", 版本号为:" + version + ", 生成EBOM文件失败！");
			}
			
			String newFilePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1) + objectName + filePath.substring(filePath.lastIndexOf("."), filePath.length());
			CommonTools.reNameFile(filePath, newFilePath); // 文件重命名
			System.out.println("==>> newFilePath: " + newFilePath);
			
			String dsName = newFilePath.substring(newFilePath.lastIndexOf(File.separator) + 1, newFilePath.length());
			System.out.println("==>> dsName: " + dsName);
			if (!checkDataset(dsName)) { // 核对数据集是否已经存在
				TCComponentDataset createDataSet = TCUtil.createDataSet(session, newFilePath, DatasetEnum.MSExcelX.type(), dsName, DatasetEnum.MSExcelX.refName());
				itemRev.add("IMAN_specification", createDataSet);
				com.foxconn.tcutils.util.TCUtil.setOrDelStatus(WorkflowService.getService(session),  new TCComponent[] {createDataSet}, D9_Release, Append);
			} else {
				TCUtil.updateDataset(session, itemRev, "IMAN_specification", newFilePath);
			}
			
			TCUtil.closeBypass(session); // 关闭旁路
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());			
		}
		
		
	}

	/**
	 * 返回符合要求的对象
	 * 
	 * @return
	 */
	public TCComponentItemRevision filterRootTarget() {
		Optional<TCComponent> findAny = Stream.of(coms).parallel()
				.filter(e -> e.getTypeObject().getName().equals(D9_BOMTOPNODEREVISION)).findAny();
		if (findAny.isPresent()) {
			return (TCComponentItemRevision) findAny.get();
		}
		return null;
	}
	
	/**
	 * 校验数据集是否存在
	 * @param dsName
	 * @return
	 * @throws TCException
	 */
	private boolean checkDataset(String dsName) throws TCException {
		itemRev.refresh();
		TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
		boolean anyMatch = Stream.of(relatedComponents).anyMatch(e -> {
			try {
				System.out.println(e.getProperty("object_name"));
				return e.getProperty("object_name").equals(dsName);
			} catch (TCException e1) {
				e1.printStackTrace();
			}
			return false;
		});
		return anyMatch;
	}
	
	/**
	 * 生成HP EBOM文件
	 * @return
	 */
	private String generateEBOMExcel() {
		TCComponentBOMWindow bomWindow = null;
		TCComponentBOMLine topBomLine = null;
		String filePath = null;
		try {
			bomWindow = TCUtil.createBOMWindow(session);
			if (CommonTools.isEmpty(bomWindow)) {
				throw new Exception("==>> 流程名称为: " + processName + ", 零组件ID为: "+ itemId + ", 版本号为:" + version + ", 创建BOMWindow失败");
			}
			
			topBomLine = TCUtil.getTopBomline(bomWindow, itemRev);
			if (CommonTools.isEmpty(topBomLine)) {
				throw new Exception("==>> 流程名称为: " + processName + ", 零组件ID为: "+ itemId + ", 版本号为:" + version + ", 获取顶层BOMLine失败");
			}
			
			filePath = HPEBOMExportService.exportExcel(topBomLine);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (CommonTools.isNotEmpty(bomWindow)) {
				try {
					bomWindow.save();
					bomWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}				
			}
		}
		return filePath;
	}

	
}
