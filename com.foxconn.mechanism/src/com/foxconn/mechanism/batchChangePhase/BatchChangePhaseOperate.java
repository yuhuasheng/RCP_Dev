package com.foxconn.mechanism.batchChangePhase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxconn.mechanism.batchChangePhase.window.BlueprintBean;
import com.foxconn.mechanism.batchChangePhase.window.PhaseBean;
import com.foxconn.mechanism.batchChangePhase.window.Progress;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

/**
 * @author wt0010 批量转阶段业务操作类
 */
public class BatchChangePhaseOperate {
	private InterfaceAIFComponent mInterfaceAIFComponent;
	// 符合要求的对象列表
	private Map<String, PhaseBean> allComplianceData = new LinkedHashMap<String, PhaseBean>();
	// 不符合要求的对象列表
	private Map<String, PhaseBean> allNotComplianceData = new LinkedHashMap<String, PhaseBean>();
	private TCSession session;
	private Registry reg;
	// 执行了转阶段的对象列表
	private List<PhaseBean> afterReviseDatas;
	// 选中的顶阶bom line
	private PhaseBean topPhaseBean;
	// 可以转阶段的对象类型
	private List<String> mAllowItems;

	/**
	 * 返回顶阶选中的bom line
	 */
	public PhaseBean getTopPhaseBean() {
		return topPhaseBean;
	}

	/**
	 * @return 获取符合要求的对象列表
	 */
	public List<PhaseBean> getAllComplianceData() {
		List<PhaseBean> phaseBeans = new ArrayList<>();
		Set<String> keys = allComplianceData.keySet();
		for (String key : keys) {
			// 过滤去重
			phaseBeans.add(allComplianceData.get(key));
		}
		return phaseBeans;
	}

	/**
	 * @return 获取不符合要求的对象列表
	 */
	public List<PhaseBean> getAllNotComplianceData() {
		List<PhaseBean> phaseBeans = new ArrayList<>();
		Set<String> keys = allNotComplianceData.keySet();
		for (String key : keys) {
			// 过滤去重
			phaseBeans.add(allNotComplianceData.get(key));
		}
		return phaseBeans;
	}

	/**
	 * @param session
	 * @param reg
	 * @param interfaceAIFComponent 执行批量转阶段业务操作类
	 */
	public BatchChangePhaseOperate(TCSession session, Registry reg, InterfaceAIFComponent interfaceAIFComponent) {

		this.session = session;
		this.reg = reg;
		this.mInterfaceAIFComponent = interfaceAIFComponent;
		// 获取首选项值，可以转阶段的对象类型
		mAllowItems = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site,
				"D9_Turn_Stage_Revision");
		System.out.println(mAllowItems);
	}

	/**
	 * @throws Exception 检查所有bom行对象，判断是否符合转阶段要求
	 */
	public void check() throws Exception {
		if (mInterfaceAIFComponent == null) {
			throw new Exception(reg.getString("nonselect.Err"));
		}

		if (!(mInterfaceAIFComponent instanceof TCComponentBOMLine)) {
			throw new Exception(reg.getString("nonselect.Err"));
		}
		allComplianceData.clear();
		allNotComplianceData.clear();
		TCComponentBOMLine topBOMLine = (TCComponentBOMLine) mInterfaceAIFComponent;
		TCComponentItem topItem = topBOMLine.getItem();
		topItem.refresh();
		TCComponent[] revions = topItem.getRelatedComponents("revision_list");
		if (revions != null && revions.length > 0) {
			for (int i = 0; i < revions.length; i++) {
				TCComponentItemRevision itemRev = (TCComponentItemRevision) revions[i];
				String version = itemRev.getProperty("item_revision_id");
				if (!isDouble(version)) {
					throw new Exception(reg.getString("revision.Err"));
				}
			}
		}

		TCComponentItemRevision topItemRevision = topBOMLine.getItemRevision();

		String itemId = topItemRevision.getProperty("item_id");
		// 对象所有者
		TCComponentUser tcUser = (TCComponentUser) topItemRevision.getReferenceProperty("owning_user");

		// 对象名称
		String itemName = topItemRevision.getProperty("object_name");
		String objectType = topItemRevision.getTypeObject().getName();
		String status = reg.getString("noreleased.info");
		// 对象状态
		String statusCode = "0";
		if (TCUtil.isReleased(topItemRevision)) {
			status = reg.getString("released.info");
			statusCode = "1";
		}
		// 对象所有者
		String owner = tcUser.getUserId();
		// 版本
		String version = topItemRevision.getProperty("item_revision_id");
		// 用量
		String count = topBOMLine.getProperty("bl_quantity");

		topPhaseBean = new PhaseBean(itemId, version, itemName, count, status, owner, topItemRevision);

		if ("0".equalsIgnoreCase(statusCode)) {
			// 未发行的对象
			allNotComplianceData.put(itemId, topPhaseBean);
		} else if (!session.getUser().getUserId().equalsIgnoreCase(owner)) {
			// 对象的所有者不是当前登录用户
			allNotComplianceData.put(itemId, topPhaseBean);
		} else if (!isAllowed(objectType)) {
			// 不可以转阶段的对象
			allNotComplianceData.put(itemId, topPhaseBean);
		} else if (!isDouble(version)) {
			// 不是数字版本
			allNotComplianceData.put(itemId, topPhaseBean);
		} else {
			allComplianceData.put(itemId, topPhaseBean);
		}
		getAllBomLines(topBOMLine);

	}
	

	/**
	 * @param bomLine
	 * @throws Exception 递归检查所有bom行对象是否符合转阶段要求
	 */
	private void getAllBomLines(TCComponentBOMLine bomLine) throws Exception {
		if (bomLine.hasChildren()) {
			AIFComponentContext[] children = bomLine.getChildren();
			for (AIFComponentContext aifComponentContext : children) {
				InterfaceAIFComponent component = aifComponentContext.getComponent();
				if (!(component instanceof TCComponentBOMLine)) {
					continue;
				}
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) component;
				TCComponentItemRevision itemRevision = childBomLine.getItemRevision();
				String itemId = itemRevision.getProperty("item_id");
				// 对象所有者
				TCComponentUser tcUser = (TCComponentUser) itemRevision.getReferenceProperty("owning_user");
				// 对象名称
				String itemName = itemRevision.getProperty("object_name");
				// 对象类型
				String objectType = itemRevision.getTypeObject().getName();
				String status = reg.getString("noreleased.info");
				// 对象状态
				String statusCode = "0";
				if (TCUtil.isReleased(itemRevision)) {
					status = reg.getString("released.info");
					statusCode = "1";
				}
				// 对象所有者工号
				String owner = tcUser.getUserId();
				// 对象版本
				String version = itemRevision.getProperty("item_revision_id");
				// 用量
				String count = childBomLine.getProperty("bl_quantity");

				PhaseBean phaseBean = new PhaseBean(itemId, version, itemName, count, status, owner, itemRevision);

				if ("0".equalsIgnoreCase(statusCode)) {
					// 对象未发行
					allNotComplianceData.put(itemId, phaseBean);
				} else if (!session.getUser().getUserId().equalsIgnoreCase(owner)) {
					// 对象所有者不是当前登陆者
					allNotComplianceData.put(itemId, phaseBean);
				} else if (!isDouble(version)) {
					// 不是数字版本
					allNotComplianceData.put(itemId, phaseBean);
				} else if (!isAllowed(objectType)) {
					// 不可以转阶段的对象
					allNotComplianceData.put(itemId, phaseBean);
				} else {
					allComplianceData.put(itemId, phaseBean);
				}
				getAllBomLines(childBomLine);
			}
		}
	}

	/**
	 * @param phaseBeans 符合转阶段要求的对象列表
	 * @param progress
	 * @throws Exception 升级已选择对象的版本
	 */
	public void revise(List<PhaseBean> phaseBeans, Progress progress) throws Exception {
		afterReviseDatas = new ArrayList<>();
		for (int i = 0; i < phaseBeans.size(); i++) {
			PhaseBean phaseBean = phaseBeans.get(i);
			// revise 到版
			TCComponentItemRevision newItemRevision = TCUtil.doRevise(phaseBean.getItemRevision(), "A");
			phaseBean.setNewItemRevision(newItemRevision);
			phaseBean.setResult(reg.getString("complete.Info"));
			// 添加到执行转阶段的对象列表
			afterReviseDatas.add(phaseBean);
			// 修改执行进度
			progress.setProgress(i + 1);
		}
	}

	/**
	 * 判断是否含有装配图/零件图
	 * 
	 * @return
	 * @throws TCException
	 */
	public boolean checkModelType() {
		boolean flag = false;
		try {
			TCComponent[] specifications = topPhaseBean.getNewItemRevision().getRelatedComponents("IMAN_specification");
			TCComponent[] renderings = topPhaseBean.getNewItemRevision().getRelatedComponents("IMAN_Rendering");
			if ((null == specifications || specifications.length <= 0)
					&& (null == renderings || renderings.length <= 0)) {
				return false;
			}
			List<TCComponent> specificationList = new ArrayList<>(Arrays.asList(specifications));
			List<TCComponent> renderingList = new ArrayList<>(Arrays.asList(renderings));
			List<TCComponent> resultlist = Stream.of(specificationList, renderingList).flatMap(x -> x.stream())
					.collect(Collectors.toList());
			for (TCComponent tcComponent : resultlist) {
				if (!(tcComponent instanceof TCComponentDataset)) {
					continue;
				}
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				String objectType = dataset.getTypeObject().getName().toLowerCase();
				if ("proasm".equals(objectType) || "proprt".equals(objectType)) { // 判断是否含有ASM装配图/Prt零件图
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * @return 获取所有符合转阶段要求对象的drw对象
	 */
	public List<BlueprintBean> getBlueprintData() {
		List<BlueprintBean> datas = new ArrayList<BlueprintBean>();
		try {
			for (PhaseBean phaseBean : afterReviseDatas) {
				TCComponentItemRevision itemRevision = phaseBean.getNewItemRevision();
				TCComponent[] relatedComponents = itemRevision.getRelatedComponents("IMAN_specification");

				for (TCComponent tcComponent : relatedComponents) {
					if (!(tcComponent instanceof TCComponentDataset)) {
						continue;
					}
					TCComponentDataset dataset = (TCComponentDataset) tcComponent;
					dataset.refresh();
					String objectType = dataset.getTypeObject().getName().toLowerCase();;
					// 判断dataset 类型
//					if (!"ProDrw".equalsIgnoreCase(objectType)) {
//						continue;
//					}
					// 判断数据集类型
					if (!"proasm".equals(objectType) && !"proprt".equals(objectType) && !"prodrw".equals(objectType)) { // 判断是否含有ASM装配图/Prt零件图
						continue;
					}
					
					// 判断datas 是否包含了文件
					TCComponentTcFile[] files = dataset.getTcFiles();
					if (files == null || files.length == 0) {
						continue;
					}

					for (TCComponentTcFile f : files) {
						String fileName = f.getProperty("original_file_name");
						// 文件名为drw结尾的文件
						if (fileName.toLowerCase().contains(".drw") || fileName.toLowerCase().contains(".prt") || fileName.toLowerCase().contains(".asm")) {
							TCComponentUser tcUser = (TCComponentUser) itemRevision.getReferenceProperty("owning_user");
							String owner = tcUser.getUserId();
							String version = itemRevision.getProperty("item_revision_id");
							// 文件名
							String itemName = itemRevision.getProperty("object_name");
							BlueprintBean blueprintBean = new BlueprintBean(fileName, owner, itemName, version);
							// 将符合条件的文件添加到列表
							datas.add(blueprintBean);
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return datas;

	}

	/**
	 * @param s
	 * @return 判断是不是数字类型
	 */
	private boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param objectType 对象类型
	 * @return 判断对象类型是不是可以转阶段的类型
	 */
	private boolean isAllowed(String objectType) {

		for (String s : mAllowItems) {
			if (s.equalsIgnoreCase(objectType)) {
				return true;
			}
		}

		return false;
	}

}
