package com.foxconn.mechanism.hhpnmaterialapply;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.foxconn.mechanism.hhpnmaterialapply.constants.BUConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.IconsEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemRevEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.PropSyncEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.RelationConstant;
import com.foxconn.mechanism.hhpnmaterialapply.dialog.CheckDialog;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.CheckDesignBean;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.hhpnmaterialapply.export.ExportDialog;
import com.foxconn.mechanism.hhpnmaterialapply.export.PartListExportDialog;
import com.foxconn.mechanism.hhpnmaterialapply.message.MessageShow;
import com.foxconn.mechanism.hhpnmaterialapply.progress.BooleanFlag;
import com.foxconn.mechanism.hhpnmaterialapply.progress.IProgressDialogRunnable;
import com.foxconn.mechanism.hhpnmaterialapply.progress.LoopProgerssDialog;
import com.foxconn.mechanism.hhpnmaterialapply.util.BOMTreeTools;
import com.foxconn.mechanism.hhpnmaterialapply.util.BOMTreeValidation;
import com.foxconn.mechanism.hhpnmaterialapply.util.Log;
import com.foxconn.mechanism.hhpnmaterialapply.util.TreeTools;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class ButtonSelectionListener extends SelectionAdapter {

	private AbstractPSEApplication app = null;
	private MyContainerCheckedTreeViewer checkedTreeViewer;
	private Shell shell = null;
	private TCSession session = null;
	public BOMInfo topBomInfo = null;
	private Label tipLabel = null; // 当前选中行提示标签
	private String buttonClickStates = null; // 用作全选和反选按钮点击状态判定
	private String checkAllButtonName = null; // 全选按钮名称
	private String reverseButtonName = null; // 反选按钮名称
	private Boolean saveFlag = true; // 保存标识, 默认值设置为true
	private String BUName = null;
	private Registry reg = null;
	private BOMTreeValidation bomTreeValidation = null;
	private CheckDialog checkDialog = null;
	private TreeTools treeTools = null;

	public ButtonSelectionListener(AbstractPSEApplication app, MyContainerCheckedTreeViewer checkedTreeViewer,
			Shell shell, TCSession session, BOMInfo topBomInfo, Label tipLabel, String BUName, Registry reg,
			BOMTreeValidation bomTreeValidation, TreeTools treeTools) {
		this.app = app;
		this.checkedTreeViewer = checkedTreeViewer;
		this.shell = shell;
		this.session = session;
		this.topBomInfo = topBomInfo;
		this.tipLabel = tipLabel;
		this.BUName = BUName;
		this.reg = reg;
		this.bomTreeValidation = bomTreeValidation;
		this.treeTools = treeTools;
		addListener(); // 添加监听
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Button button = (Button) e.getSource();
		String buttonName = button.getText();
		System.out.println("==>> buttonName: " + buttonName);
		if (reg.getString("generateBtn.LABEL").equals(buttonName)) {
			generateMaterial();
		} else if (reg.getString("generateMoreBtn.LABEL").equals(buttonName)) {
			generateMoreMaterial();
		} else if (reg.getString("designSyncBtn.LABEL").equals(buttonName)) {
			designSyncPart(); // 设计属性同步物料属性
		} else if (reg.getString("partSyncBtn.LABEL").equals(buttonName)) {
			partSyncDesign(); // 物料属性同步设计属性
		} else if (reg.getString("saveBtn.LABEL").equals(buttonName)) {
			save();
		} else if (reg.getString("cancelBtn.LABEL").equals(buttonName)) {
			cancel();
		} else if (reg.getString("checkAllBtn.LABEL").equals(buttonName)) {
			checkAllButton(button);
		} else if (reg.getString("reverseBtn.LABEL").equals(buttonName)) {
			reverseButton(button);
		} else if (reg.getString("reorderBtn.LABEL").equals(buttonName)) {
			reorder(topBomInfo);
		} else if (reg.getString("expandAllBtn.LABEL").equals(buttonName)) {
			customExpandAll();
		} else if (reg.getString("collapseAllBtn.LABEL").equals(buttonName)) {
			collapseAll();
		} else if (reg.getString("checkLeafBtn.LABEL").equals(buttonName)) {
			checkLeafBtn(); // 勾选叶子节点
			TreeItem topTreeItem = checkedTreeViewer.getTree().getItems()[0];
			treeTools.showTreeItemCheckStates(topTreeItem); // 展示所有展开的结构树的是否选中状态
			expandCheckLeafNode(); // 展开选中的叶子节点
			treeTools.showTreeItemCheckStates(topTreeItem); // 展示所有展开的结构树的是否选中状态
		} else if (reg.getString("partListExportBtn.LABEL").equals(buttonName)) {
			try {
				partListExport(); // partList导出
			} catch (TCException | NotLoadedException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 添加监听
	 */
	public void addListener() {
		shellListener(); // 监听shell窗体
		treeListener(); // 结构树监听
	}

	/**
	 * 生成物料对象
	 */
	private void generateMaterial() {
		Object[] checkedElements = checkedTreeViewer.getCheckedElements();
		List<BOMInfo> list = new ArrayList<BOMInfo>();
		List<Object> objectList = new ArrayList<>(Arrays.asList(checkedElements));
		List<BOMInfo> resultList = BOMTreeTools.getBOMInfoList(objectList);
		if (CommonTools.isEmpty(resultList)) {
			MessageShow.warningMsgBox(reg.getString("generateMatWarn.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		boolean flag = false;
		for (Object obj : objectList) {
			CustTreeNode custTreeNode = (CustTreeNode) obj;
			if (checkedTreeViewer.getGrayed(custTreeNode)) { // 判断结点选中是否为实心，而不是√
				continue;
			}
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			String objectType = bomInfo.getObjectType();
			if (objectType.contains(ItemRevEnum.DesignRev.type())) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			MessageShow.warningMsgBox(reg.getString("generateMatWarn1.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		// 删除集合中某一个元素值
		IntStream.range(0, resultList.size()).filter(i -> checkedTreeViewer.getGrayed(resultList.get(i)) == true)
				.boxed().findFirst().map(i -> resultList.remove((int) i));
		for (Object obj : objectList) {
			CustTreeNode custTreeNode = (CustTreeNode) obj;
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			String objectType = bomInfo.getObjectType();
//			String itemId = bomInfo.getItemId();
			if (ItemRevEnum.CommonPartRev.type().equals(objectType) && bomInfo.getDeleteFlag() == false) { // 判断此记录是否为料号对象，是否已经被删除
				list.add(bomInfo);
				continue;
			} else if (objectType.contains(ItemRevEnum.CommonPartRev.type())) {
				continue;
			}
			String designItemId = bomInfo.getPropertiesInfo().getItem_ID();
			List<BOMInfo> child = bomInfo.getChild();
			List<Integer> numberList = new ArrayList<Integer>();
//			int number = 0; // 临时料号后缀编号
			boolean flag1 = false;
			if (CommonTools.isNotEmpty(child)) {
				for (BOMInfo childBomInfo : child) {
					if (childBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())) { // 假如子是设计对象，则无需判断，直接下一次循环
						continue;
					}
//					String partItemId = childBomInfo.getItemId();
					String partItemId = childBomInfo.getPropertiesInfo().getItem_ID();
//					if (BOMTreeTools.checkTempPart(designItemId, partItemId)) { // 判断是否存在临时料号
					if (childBomInfo.getDeleteFlag() == true) { // 判断此记录是否已经是删除
						if (partItemId.indexOf("@") != -1) {
							String str = partItemId.substring(partItemId.indexOf("@") + 1);
							if (CommonTools.isNumeric(str)) { // 判断字符串是否全部为数字
								int currentNumber = Integer.valueOf(str);
								numberList.add(currentNumber);
							}
						}
					} else {
						flag1 = true;
						list.add(bomInfo);
						break;
					}
//					}
				}
			}
			if (flag1) { // 代表已经存在临时料号，无需重复添加
				continue;
			}
			int number = 0;
			if (CommonTools.isNotEmpty(numberList)) {
				number = Collections.max(numberList); // 临时料号后缀最大编号
			}
			addCustTreeNode(custTreeNode, bomInfo, number); // 为结构树添加子结点
			CustTreeNode topCustTreeNode = (CustTreeNode) checkedTreeViewer.getTree().getItems()[0].getData();

			borrowAddCustTreeNode(topCustTreeNode, designItemId, number); // 对于不是选中状态,借用图号添加子结点
		}
		List<BOMInfo> resultList1 = BOMTreeTools.removeDupliById(list);
		BOMTreeTools.removeRecord(resultList); // 移除临时料号不是以@+数字结尾的记录
		BOMTreeTools.removeRecord2(resultList);
		List<BOMInfo> resultList2 = BOMTreeTools.removeDupliById(resultList);

		if (resultList1.size() == resultList2.size()) {
			MessageShow.warningMsgBox(reg.getString("generateMatWarn2.MSG"), reg.getString("WARNING.MSG"));
		}

	}

	/**
	 * 生成一图多料
	 */
	private void generateMoreMaterial() {
		Object[] checkedElements = checkedTreeViewer.getCheckedElements();
		List<Object> resultList = new ArrayList<>(Arrays.asList(checkedElements));
		if (CommonTools.isEmpty(resultList)) {
			MessageShow.warningMsgBox(reg.getString("generateMatWarn.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		boolean flag = false;
		for (Object obj : resultList) {
			CustTreeNode custTreeNode = (CustTreeNode) obj;
			if (checkedTreeViewer.getGrayed(custTreeNode)) { // 判断结点选中是否为实心，而不是√
				continue;
			}
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			String objectType = bomInfo.getObjectType();
			if (objectType.contains(ItemRevEnum.DesignRev.type())) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			MessageShow.warningMsgBox(reg.getString("generateMatWarn1.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		for (Object obj : resultList) {
			CustTreeNode custTreeNode = (CustTreeNode) obj;
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			String objectType = bomInfo.getObjectType();
			if (objectType.contains(ItemRevEnum.CommonPartRev.type())) {
				continue;
			}
			String designItemId = bomInfo.getPropertiesInfo().getItem_ID();
			List<BOMInfo> child = bomInfo.getChild();
//			int number = 0; // 临时料号后缀编号
			List<Integer> numberList = new ArrayList<Integer>();
			if (CommonTools.isNotEmpty(child)) {
				for (BOMInfo childBomInfo : child) {
					if (childBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())) { // 假如子是设计对象，则无需判断，直接下一次循环
						continue;
					}
//					String partItemId = childBomInfo.getItemId();
					String partItemId = childBomInfo.getPropertiesInfo().getItem_ID();
					if (BOMTreeTools.checkTempPart(designItemId, partItemId)) {
						if (partItemId.indexOf("@") != -1) {
							String str = partItemId.substring(partItemId.indexOf("@") + 1);
							if (CommonTools.isNumeric(str)) { // 判断字符串是否全部为数字
								int currentNumber = Integer.valueOf(str);
								numberList.add(currentNumber);
							}
						}
					}
				}
			}
			int number = 0;
			if (CommonTools.isNotEmpty(numberList)) {
				number = Collections.max(numberList); // 临时料号后缀最大编号
			}
			addCustTreeNode(custTreeNode, bomInfo, number); // 为结构树添加子结点

			CustTreeNode topCustTreeNode = (CustTreeNode) checkedTreeViewer.getTree().getItems()[0].getData();

			borrowAddCustTreeNode(topCustTreeNode, designItemId, number); // 对于不是选中状态，借用图号添加子结点
		}
	}

	/**
	 * 设计属性同步物料属性
	 * 
	 * @throws TCException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void designSyncPart() {
		try {
			treeTools.setSyncResult(false); // 先将同步完成的结果设置为false
			if (BUName.equals(BUConstant.DTSABUNAME)) {
//				List<CheckDesignBean> list = new ArrayList<CheckDesignBean>();				
//				bomTreeValidation.filterDesignReleaseForDT(topBomInfo, list, PropSyncEnum.design_part.name()); // 校验设计对象是否含有物料对象并且设计对象是否为已发布状态的记录数				
//				if (CommonTools.isNotEmpty(list)) {
//					bomTreeValidation.setCheckDisignIds(list);
//					checkDialog = new CheckDialog(null, shell, list);																
//				}
//				if (CommonTools.isNotEmpty(checkDialog) && !checkDialog.getFlag()) {
//					return;
//				}

				treeTools.syncForDT(topBomInfo, true, false); // 同步设计对象和物料对象属性(DT)
				if (!treeTools.getSyncResult()) { // 判断此次操作是否发生属性同步
					MessageShow.warningMsgBox(reg.getString("syncWarn.MSG"), reg.getString("WARNING.MSG"));
					return;
				}
				restartLoadTree(false); // 重新加载结构树
			} else {
				Map<String, List<? extends Object>> map = bomTreeValidation
						.filterDesignReleaseForMNTOrPRT(checkedTreeViewer, reg, PropSyncEnum.design_part.name());
				if (CommonTools.isEmpty(map)) {
					return;
				}

//				List<CheckDesignBean> checkReleasedList = (List<CheckDesignBean>) map.get("checkReleasedList");
//				if (CommonTools.isNotEmpty(checkReleasedList)) {
//					checkDialog = new CheckDialog(null, shell, checkReleasedList);					
//				}
//				
//				if (CommonTools.isNotEmpty(checkDialog) && !checkDialog.getFlag()) { // 校验对话框单击了取消按钮
//					return;
//				}

				List<BOMInfo> filterElementList = (List<BOMInfo>) map.get("filterElementList");
				if (CommonTools.isNotEmpty(filterElementList)) {
					treeTools.syncForMNTOrPRT(filterElementList, topBomInfo, true, false); // 设计对象属性同步物料对象属性(MNT/PRT)
				}

				if (!treeTools.getSyncResult()) { // 判断此次操作是否发生属性同步
					MessageShow.warningMsgBox(reg.getString("syncWarn.MSG"), reg.getString("WARNING.MSG"));
					return;
				}

				CustTreeNode topCustTreeNode = (CustTreeNode) checkedTreeViewer.getTree().getItems()[0].getData();
				filterElementList.forEach(currentInfo -> {
					treeTools.expandSuccNode(checkedTreeViewer, topCustTreeNode, currentInfo);
				});

				treeTools.refreshTreeItemProperties(checkedTreeViewer, checkedTreeViewer.getTree().getItems()[0]); // 刷新结构树结点，加载属性
			}

//			treeTools.resetTreeColumn(checkedTreeViewer); // 重新设置各列宽，让其文本可以完全显示
			MessageShow.infoMsgBox(reg.getString("syncSuccess.MSG"), reg.getString("INFORMATION.MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			MessageShow.errorMsgBox(reg.getString("syncError.MSG"), reg.getString("ERROR.MSG"));
		}
	}

	/**
	 * 物料属性同步设计属性
	 * 
	 * @throws TCException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void partSyncDesign() {
		try {
			treeTools.setSyncResult(false); // 先将同步完成的结果设置为false
			if (BUName.equals(BUConstant.DTSABUNAME)) {
				List<CheckDesignBean> list = new ArrayList<CheckDesignBean>();
				bomTreeValidation.filterDesignReleaseForDT(topBomInfo, list, PropSyncEnum.part_design.name()); // 校验设计对象是否含有物料对象并且设计对象是否为已发布状态的记录数
				if (CommonTools.isNotEmpty(list)) {
					bomTreeValidation.setCheckDisignIds(list);
					checkDialog = new CheckDialog(null, shell, list);
				}

				if (CommonTools.isNotEmpty(checkDialog) && !checkDialog.getFlag()) {
					return;
				}

				treeTools.syncForDT(topBomInfo, false, true); // 物料对象属性同步到设计对象属性(DT)
				if (!treeTools.getSyncResult()) {
					MessageShow.warningMsgBox(reg.getString("syncWarn.MSG"), reg.getString("WARNING.MSG"));
					return;
				}
				restartLoadTree(false); // 重新加载结构树
			} else {
				Map<String, List<? extends Object>> map = bomTreeValidation
						.filterDesignReleaseForMNTOrPRT(checkedTreeViewer, reg, PropSyncEnum.part_design.name());
				if (CommonTools.isEmpty(map)) {
					return;
				}
				List<CheckDesignBean> checkReleasedList = (List<CheckDesignBean>) map.get("checkReleasedList");
				if (CommonTools.isNotEmpty(checkReleasedList)) {
					checkDialog = new CheckDialog(null, shell, checkReleasedList);
				}

				if (CommonTools.isNotEmpty(checkDialog) && !checkDialog.getFlag()) {
					return;
				}

				List<BOMInfo> filterElementList = (List<BOMInfo>) map.get("filterElementList");
				if (CommonTools.isNotEmpty(filterElementList)) {
					treeTools.syncForMNTOrPRT(filterElementList, topBomInfo, false, true); // 物料对象属性同步设计对象属性(MNT/PRT)
				}

				if (!treeTools.getSyncResult()) {
					MessageShow.warningMsgBox(reg.getString("syncWarn.MSG"), reg.getString("WARNING.MSG"));
					return;
				}

				CustTreeNode topCustTreeNode = (CustTreeNode) checkedTreeViewer.getTree().getItems()[0].getData();
				filterElementList.forEach(currentInfo -> {
					treeTools.expandSuccNode(checkedTreeViewer, topCustTreeNode, currentInfo);
				});

				treeTools.refreshTreeItemProperties(checkedTreeViewer, checkedTreeViewer.getTree().getItems()[0]); // 刷新结构树结点，加载属性

			}
//			treeTools.resetTreeColumn(checkedTreeViewer); // 重新设置各列宽，让其文本可以完全显示
			MessageShow.infoMsgBox(reg.getString("syncSuccess.MSG"), reg.getString("INFORMATION.MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			MessageShow.errorMsgBox(reg.getString("syncError.MSG"), reg.getString("ERROR.MSG"));
		}
	}

	/**
	 * 对于不是选中状态,借用图号添加子结点
	 * 
	 * @param topCustTreeNode
	 * @param designItemId
	 * @param number
	 */
	private void borrowAddCustTreeNode(CustTreeNode topCustTreeNode, String designItemId, int number) {
		BOMInfo topBomInfo = topCustTreeNode.getBomInfo();
		if (CommonTools.isEmpty(topBomInfo)) {
			return;
		}
		String objectType = topBomInfo.getObjectType();
		PropertiesInfo topPropertiesInfo = topBomInfo.getPropertiesInfo();
		if (designItemId.equals(topPropertiesInfo.getItem_ID()) && objectType.contains(ItemRevEnum.DesignRev.type())
				&& checkedTreeViewer.getChecked(topCustTreeNode) == false) { // Item_ID相同, 当前结点不是选中状态,对象类型为设计对象
			addCustTreeNode(topCustTreeNode, topBomInfo, number); // 为结构树添加子结点
		}

		List<CustTreeNode> custTreeNodeList = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(custTreeNodeList)) {
			return;
		}
		PropertiesInfo propertiesInfo = null;
		for (CustTreeNode custTreeNode : custTreeNodeList) {
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			if (CommonTools.isEmpty(bomInfo)) {
				continue;
			}
			List<CustTreeNode> child = custTreeNode.getChildren();
			if (CommonTools.isNotEmpty(child)) {
				borrowAddCustTreeNode(custTreeNode, designItemId, number);
			} else {
				objectType = bomInfo.getObjectType();
				propertiesInfo = bomInfo.getPropertiesInfo();
				if (designItemId.equals(propertiesInfo.getItem_ID())
						&& objectType.contains(ItemRevEnum.DesignRev.type())
						&& checkedTreeViewer.getChecked(custTreeNode) == false) { // Item_ID相同, 当前结点不是选中状态,对象类型为设计对象
					addCustTreeNode(custTreeNode, bomInfo, number); // 为结构树添加子结点
				}
			}
		}
	}

	/**
	 * 为tree添加子结点
	 * 
	 * @param custTreeNode
	 * @param bomInfo
	 * @param number
	 */
	private void addCustTreeNode(CustTreeNode custTreeNode, BOMInfo bomInfo, int number) {
		BOMInfo childInfo = new BOMInfo();
		PropertiesInfo propertiesInfo = bomInfo.getPropertiesInfo();
		PropertiesInfo childPropertiesInfo = (PropertiesInfo) propertiesInfo.clone(); // 继承父对象属性

		childPropertiesInfo.setHHPN("");
		childPropertiesInfo.setHHRevision("");
		childPropertiesInfo.setHHPNState("");
//		childPropertiesInfo.setQtyUnits(1); // 默认数量数值为1
//		String childItemId = bomInfo.getItemId() + "@" + tempPartNum;
		String childItemId = propertiesInfo.getItem_ID() + "@" + (++number);
		childPropertiesInfo.setBUPN(childItemId);
		childInfo.setObjectName(childItemId);
		childInfo.setImage(
				new Image(null, BOMTreeTools.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath())));
		int QtyUnits = childPropertiesInfo.getQtyUnits();
		if (QtyUnits > 1) { // 如果数量
			childInfo.setItemId(childItemId + " x " + QtyUnits);
		} else {
			childInfo.setItemId(childItemId);
		}
		childPropertiesInfo.setItem_ID(childItemId);
		childInfo.setColor(ColorEnum.Green.color()); // 新增用绿色表示
		childInfo.setObjectType(ItemRevEnum.CommonPartRev.type());
		childInfo.setPropertiesInfo(childPropertiesInfo);
		childInfo.setModify(true); // 设计对象设置可编辑
		childInfo.setAddFlag(true); // 设置可以添加到设计对象下
		childInfo.setDeleteFlag(false); // 设置为不可删除
		childInfo.setIsExist(false); // 设置此物料对象是新增，不是存在TC中
		List<BOMInfo> child2 = bomInfo.getChild();
		int index = 0;
		if (CommonTools.isEmpty(child2)) {
			child2 = new ArrayList<BOMInfo>();
			bomInfo.setChild(child2);
		} 
		child2.add(childInfo); // 新节点新增到数据模型中
		childInfo.setParent(bomInfo); // 设置父节点 模型
//		CustTreeNode childCustTreeNode = new CustTreeNode(childInfo);
		CustTreeNode childCustTreeNode = getCustTreeNode(custTreeNode, childInfo);
		if (CommonTools.isEmpty(childCustTreeNode)) {
			childCustTreeNode = new CustTreeNode(childInfo);
		}
		childInfo.setCustTreeNode(childCustTreeNode);
//		custTreeNode.getChildren().add(childCustTreeNode);
//		if (CommonTools.isEmpty(nodeChild)) {
//			checkedTreeViewer.add(custTreeNode, childCustTreeNode); // 结点增加到custTreeNode下
//		} else {
//			checkedTreeViewer.insert(custTreeNode, childCustTreeNode, index);			
//		}
		
		checkedTreeViewer.add(custTreeNode, childCustTreeNode); // 结点增加到custTreeNode下		
		childCustTreeNode.setParentTreeNode(custTreeNode); // 设置父结点
		checkedTreeViewer.refresh(custTreeNode, true);
		treeTools.expandCurrNode(checkedTreeViewer, childCustTreeNode, true); // 展示当前节点的所有路径
		if (!checkedTreeViewer.getExpandedState(custTreeNode)) {
			checkedTreeViewer.expandToLevel(custTreeNode, 1);
		}
	}

	/**
	 * 返回最后一个物料Data的索引
	 * 
	 * @param list
	 * @return
	 */
	private int getMatInfoIndex(List<BOMInfo> list) {
		boolean flag = false;
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			BOMInfo bomInfo = list.get(i);
			if (ItemRevEnum.CommonPartRev.type().equals(bomInfo.getObjectType())) {
				index = i;
				flag = true;
			}
		}
		if (flag) {
			return ++index;
		}
		return index;
	}

	private CustTreeNode getCustTreeNode(CustTreeNode custTreeNode, BOMInfo bomInfo) {
		List<CustTreeNode> children = custTreeNode.getChildren();
		Optional<CustTreeNode> findAny = children.stream().filter(treeNode -> {
			return treeNode.getBomInfo().getPropertiesInfo().getItem_ID().equals(bomInfo.getPropertiesInfo().getItem_ID());
		}).findAny();
		
		if (findAny.isPresent()) {
			return findAny.get();
		}
		return null;
	}
	
	
	/**
	 * 保存按钮操作
	 */
 	private void save() {

		LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null,
				reg.getString("progressDialog2.TITLE"));
		loopProgerssDialog.run(true, new IProgressDialogRunnable() {

			@Override
			public void run(BooleanFlag stopFlag) {
				Log.log("********** 开始执行保存操作  **********");
				try {
					if (stopFlag.getFlag()) { // 监控是否要让停止后台任务
						Log.log("被中断了");
						saveFlag = false;
						return;
					}
					TCUtil.setBypass(session); // 开启旁路
					TCComponentItemRevision parentItemRevision = topBomInfo.getItemRevision();
					PropertiesInfo propertiesInfo = topBomInfo.getPropertiesInfo();
					if ((topBomInfo.getModify() == true && topBomInfo.getAddFlag() == true
							&& ItemRevEnum.CommonPartRev.type().equals(topBomInfo.getObjectType())
							&& topBomInfo.getDeleteFlag() == false)
							|| ((topBomInfo.getModify() == true || treeTools.getGreenFlag(topBomInfo))
									&& topBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type()))) {
						Map<String, String> propertyMap = getPropertyMap(topBomInfo, propertiesInfo,
								parentItemRevision);
						parentItemRevision.setProperties(propertyMap); // 更新对象版本属性
						topBomInfo.setColor(ColorEnum.Blue.color());
					}
					saveProperties(topBomInfo); // 保存属性，并且通过将有编辑权的子对象版本挂载到其父对象版本的表示文件夹关系下
					if (saveFlag == false) {
						throw new Exception();
					}
					stopFlag.setFlag(true); // 执行完毕后把标志位设置为停止，好通知给进度框
//					saveFlag = true;
				} catch (Exception e2) {
					Log.log(reg.getString("saveError.MSG"), e2);
					e2.printStackTrace();
					MessageShow.errorMsgBox(reg.getString("saveError.MSG"), reg.getString("ERROR.MSG"));
					stopFlag.setFlag(true);
					saveFlag = false;
				}
				TCUtil.closeBypass(session); // 关闭旁路
				Log.log("********** 执行保存结束  **********");
			}
		});
		if (saveFlag) {
			restartLoadTree(true); // 重新加载树结构
			treeTools.setHasSaveFlag(true); // 将保存标识设置为true
			MessageShow.infoMsgBox(reg.getString("saveSuccess.MSG"), reg.getString("INFORMATION.MSG"));
		} else {
			showErrorLog(); // 展示错误日志的路径
		}
	}

	/**
	 * 展示错误日志的路径
	 */
	private void showErrorLog() {
		MessageShow.infoMsgBox(reg.getString("errLogPath.MSG") + ": " + Log.fileAbsolutePath,
				reg.getString("INFORMATION.MSG"));
		System.out.println("********** 日志文件保存的路径为: " + Log.fileAbsolutePath + " **********");
	}

	/**
	 * 删除错误的日志文件
	 */
	private void deleteErrorLogFile() {
		if (CommonTools.isNotEmpty(Log.fileAbsolutePath)) {
			CommonTools.deletefileNew(Log.fileAbsolutePath.substring(0, Log.fileAbsolutePath.lastIndexOf("\\")));
		}
	}

	/**
	 * 取消按钮的操作
	 */
	private void cancel() {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		msgBox.setText(reg.getString("messageBox.TITLE"));
		treeTools.setHasSaveFlag(true);
		treeTools.checkHasSave(topBomInfo);
		if (treeTools.getHasSaveFlag()) { // 判断是否已经保存
			msgBox.setMessage(reg.getString("messageBox1.MSG"));
		} else {
			msgBox.setMessage(reg.getString("messageBox.MSG"));
		}

		int rc = msgBox.open();
		if (rc == SWT.YES) {
			System.out.println("您单击了 “是” 按钮");
			shell.dispose();
			deleteErrorLogFile(); // 删除错误的日志文件
		} else if (rc == SWT.NO) {
			System.out.println("您单击了 “否” 按钮");
		}

	}

	/**
	 * partList导出
	 * 
	 * @throws NotLoadedException
	 * @throws TCException
	 */
	private void partListExport() throws TCException, NotLoadedException {
		treeTools.setHasSaveFlag(true);
		treeTools.checkHasSave(topBomInfo); // 判断是否已经保存过
		if (!treeTools.getHasSaveFlag()) { // 判断是否已经保存
			MessageShow.warningMsgBox(reg.getString("partListExportWarn.MSG"), reg.getString("WARNING.MSG"));
			return;
		}		
		new PartListExportDialog(app, shell, BUName, reg);
	}

	private void shellListener() {
		shell.addShellListener(new ShellAdapter() {
			// 监听关闭窗体事件
			public void shellClosed(ShellEvent e) {
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("messageBox.TITLE"));
				treeTools.setHasSaveFlag(true);
				treeTools.checkHasSave(topBomInfo); // 判断是否已经保存过
				if (treeTools.getHasSaveFlag()) {
					msgBox.setMessage(reg.getString("messageBox1.MSG"));
				} else {
					msgBox.setMessage(reg.getString("messageBox.MSG"));
				}

				int rc = msgBox.open();
				e.doit = rc == SWT.YES;
				if (e.doit) {
					System.out.println("您单击了 “是” 按钮");
					shell.dispose();
					deleteErrorLogFile(); // 删除错误的日志文件
				} else {
					System.out.println("您单击了 “否” 按钮");
				}
			}
		});
	}

	/**
	 * 结构树监听
	 */
	private void treeListener() {

		checkedTreeViewer.getTree().addListener(SWT.Expand, new Listener() { // 结构树结点展开式监听方法，同步相同一颗设计对象被借用属性的同步

			@Override
			public void handleEvent(Event event) {
				// 当前点击item
				TreeItem currenTreeItem = (TreeItem) event.item;
//				TreeTools.refreshSingleTreeItemProperties(checkedTreeViewer, currenTreeItem);	
				treeTools.refreshTreeItemProperties(checkedTreeViewer, currenTreeItem);
//				TreeTools.showSingleTreeItemCheckStates(currenTreeItem);
				treeTools.showTreeItemCheckStates(currenTreeItem);
//				if (CommonTools.isNotEmpty(buttonClickStates) && buttonClickStates.equals(checkAllButtonName)) { // 判断是否单击了全选按钮
//					TreeTools.setTreeItemStates(currenTreeItem, true); // 设置全选状态
//				} else if (CommonTools.isNotEmpty(buttonClickStates) && buttonClickStates.equals(reverseButtonName)) { // 判断是否单击了反选按钮
//					TreeTools.setTreeItemStates(currenTreeItem, false); // 设置全不选
//				}
			}
		});

		checkedTreeViewer.getTree().addListener(SWT.Selection, new Listener() { // 结构树当前行是否被选中

			@Override
			public void handleEvent(Event event) {
				TreeItem currentTreeItem = (TreeItem) event.item;
				CustTreeNode custTreeNode = (CustTreeNode) currentTreeItem.getData();
				BOMInfo bomInfo = custTreeNode.getBomInfo();
				String content = reg.getString("currentTreeItemSelect.MSG") + ": " + bomInfo.getItemId();
				tipLabel.setText(content);
				System.out.println(content);
			}
		});

		checkedTreeViewer.addCheckStateListener(new ICheckStateListener() { // 监听结构树前的复选框点击事件

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				TreeItem treeItem = checkedTreeViewer.getCheckTreeItem(event.getElement());
				if (CommonTools.isEmpty(treeItem)) {
					return;
				}
				CustTreeNode currentCustTreeNode = (CustTreeNode) treeItem.getData();
				System.out.println("当前勾选行: " + currentCustTreeNode.getItemId() + ", 状态为: " + treeItem.getChecked());
				if (checkedTreeViewer.getChecked(currentCustTreeNode)) {
					currentCustTreeNode.setCheckedStates(true); // 设置当前结点被选中状态
				} else {
					currentCustTreeNode.setCheckedStates(false); // 设置当前结点未被选中状态
				}
			}
		});

		checkedTreeViewer.getTree().addListener(SWT.MouseDown, new Listener() { // 用于监听结构树的鼠标点击事件

			@Override
			public void handleEvent(Event event) {
				TreeItem item = checkedTreeViewer.getTree().getItem(new Point(event.x, event.y)); // 判断是否选中了Tree的空白行
				if (CommonTools.isEmpty(item)) { // 如果是空白行，取消之前选中的行
					checkedTreeViewer.setSelection(StructuredSelection.EMPTY);
					tipLabel.setText("");
				}
			}
		});
	}

	/**
	 * 全选操作
	 * 
	 * @param button
	 */
	private void checkAllButton(Button button) {
		TreeItem topTreeItem = checkedTreeViewer.getTree().getItems()[0];
		CustTreeNode topCustTreeNode = (CustTreeNode) topTreeItem.getData();
		treeTools.setTreeItemCheckStates(topTreeItem, true);
		treeTools.setCustTreeNodeCheckStates(topCustTreeNode, true);
		buttonClickStates = button.getText();
	}

	/**
	 * 全选叶子节点
	 */
	private void checkLeafBtn() {
		TreeItem topTreeItem = checkedTreeViewer.getTree().getItems()[0];
		CustTreeNode topCustTreeNode = (CustTreeNode) topTreeItem.getData();
//		treeTools.setLeafItemCheckStates(topTreeItem, true); // 设置当前已经被展开的叶子节点为选中状态
		treeTools.setLeafCustTreeNodeCheckStates(topCustTreeNode, true); // 将结构树中所有的展开和未展开的叶子节点对应的data类的checkedStates设置为true,非叶子节点设置为false
	}

	/**
	 * 展开选中的叶子节点
	 */
	private void expandCheckLeafNode() {
		CustTreeNode topCustTreeNode = (CustTreeNode) checkedTreeViewer.getTree().getItems()[0].getData();
		treeTools.expandLeafTreeItem(checkedTreeViewer, topCustTreeNode, true);
	}

	/**
	 * 反选操作
	 * 
	 * @param button
	 */
	private void reverseButton(Button button) {
		if (CommonTools.isEmpty(checkedTreeViewer.getCheckedElements())) {
			MessageShow.warningMsgBox(reg.getString("reverseBtnWarn.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		TreeItem topTreeItem = checkedTreeViewer.getTree().getItems()[0];
		CustTreeNode topCustTreeNode = (CustTreeNode) topTreeItem.getData();
		treeTools.setTreeItemCheckStates(topTreeItem, false);
		treeTools.setCustTreeNodeCheckStates(topCustTreeNode, false);
		buttonClickStates = button.getText();
	}

	private void reorder(BOMInfo topBomInfo) {
		resetBomInfo(topBomInfo);
		checkedTreeViewer.setInput(topBomInfo); // 重新加载数据模型
		customExpandAll();
	}
	
	/**
	 * 重新设置BomInfo的信息
	 * @param topBomInfo
	 */
	private void resetBomInfo(BOMInfo topBomInfo) {
		List<BOMInfo> children = topBomInfo.getChild();
		if (CommonTools.isEmpty(children)) {
			return;
		}
		List<BOMInfo> partsList = new ArrayList<BOMInfo>();
		List<BOMInfo> designList = new ArrayList<BOMInfo>();
		for (BOMInfo bomInfo : children) {
			if (ItemRevEnum.CommonPartRev.type().equals(bomInfo.getObjectType())) {
				partsList.add(bomInfo);
			} else if (bomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())) {
				designList.add(bomInfo);
			}
			resetBomInfo(bomInfo);			
		}		
		
		children.clear();
	    getNewBomInfoList(partsList, designList, children);
		
//		topBomInfo.setChild(newBOmInfoList);
	
	}
	
	
	private void getNewBomInfoList(List<BOMInfo> partsList, List<BOMInfo> designList, List<BOMInfo> bomInfoList) {
//		List<BOMInfo> bomInfoList = new ArrayList<BOMInfo>();
		partsList.forEach(info -> {
			bomInfoList.add(info);
		});
		
		designList.forEach(info -> {
			bomInfoList.add(info);
		});
//		return bomInfoList;
	}
	
	
	
	/**
	 * 展开所有结点(客制化)
	 */
	private void customExpandAll() {
//		checkedTreeViewer.expandAll();		
		TreeItem topItem = checkedTreeViewer.getTree().getItems()[0]; // 获取树的顶层
		treeTools.showTreeItemCheckStates(checkedTreeViewer, topItem, true);
		treeTools.resetTreeColumn(checkedTreeViewer); // 重新设置各列宽，让其文本可以完全显示
//		if (CommonTools.isNotEmpty(buttonClickStates) && buttonClickStates.equals(checkAllButtonName)) { // 判断是否单击了全选按钮			
//			TreeTools.setAllTreeItemStates(checkedTreeViewer, topItem, true); // 设置必须重新设置一下复选框的状态，然后展开(设置全选)
//		} else if (CommonTools.isNotEmpty(buttonClickStates) && buttonClickStates.equals(reverseButtonName)) { // 判断是否单击了反选按钮
//			TreeTools.setAllTreeItemStates(checkedTreeViewer, topItem, false); // 设置必须重新设置一下复选框的状态，然后展开(设置全不选)
//		}		
	}

	/**
	 * 收缩所有结点
	 */
	private void collapseAll() {
		checkedTreeViewer.collapseAll();
	}

	/**
	 * 保存属性，并且通过将有编辑权的子对象版本挂载到其父对象版本的表示文件夹关系下
	 * 
	 * @param topBomInfo
	 * @throws Exception
	 */
	private void saveProperties(BOMInfo topBomInfo) throws Exception {
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {
			return;
		}
		TCComponentItemRevision parentItemRevision = topBomInfo.getItemRevision();
		for (BOMInfo childBomInfo : childBomInfos) {
			TCComponentItemRevision itemRevision = childBomInfo.getItemRevision();
			PropertiesInfo propertiesInfo = childBomInfo.getPropertiesInfo();
			if (childBomInfo.getModify() == true
					&& ItemRevEnum.CommonPartRev.type().equals(childBomInfo.getObjectType())
					&& childBomInfo.getDeleteFlag() == false) { // 将有编辑权的对象版本信息进行保存, 并且不含有删除标志
				if (childBomInfo.getIsExist() == false) {
					TCComponentItem findItem = TCUtil.findItem(childBomInfo.getPropertiesInfo().getItem_ID()); // 如果此对象版本在TC中本来就不存在，则创建
					TCComponentItem createItem = null;
					if (findItem != null) {
						itemRevision = findItem.getLatestItemRevision();
						childBomInfo.setItemRevision(itemRevision);
					} else {
						try {
							createItem = TCUtil.createItem(childBomInfo.getPropertiesInfo().getItem_ID(),
									ItemEnum.CommonPart.type(), childBomInfo.getObjectName(), "@",
									ItemRevEnum.CommonPartRev.type());
							itemRevision = createItem.getLatestItemRevision();
							childBomInfo.setItemRevision(itemRevision);
						} catch (Exception e) {
							e.printStackTrace();
							Log.log(reg.getString("modelName.MSG") + ": " + childBomInfo.getItemId() + ", "
									+ reg.getString("createItemError.MSG"), e);
							saveFlag = false;
							continue;
						}
					}
				}
				Map<String, String> propertyMap = getPropertyMap(childBomInfo, propertiesInfo, itemRevision);
				try {
					itemRevision.setProperties(propertyMap); // 更新对象版本属性
				} catch (Exception e) {
					e.printStackTrace();
					Log.log(reg.getString("modelName.MSG") + ": " + childBomInfo.getItemId() + ", "
							+ reg.getString("savePropertiesError.MSG"), e);
					saveFlag = false;
				}

//				if (childBomInfo.getAddFlag() == true && !checkPartItemRev(parentItemRevision, itemRevision)) {
				if (!checkPartItemRev(parentItemRevision, itemRevision)) { // 判断当前父对象版本表示伪文件夹下是否含有此物料对象版本
//					parentItemRevision.add(RelationConstant.REPRESENTATION_FOR, itemRevision);
					try {
						itemRevision.add(RelationConstant.TC_IS_REPRESENTED_BY, parentItemRevision);
						childBomInfo.setAddFlag(false); // 添加成功后，设置标志，防止重复添加
					} catch (Exception e) {
						e.printStackTrace();
						Log.log(reg.getString("modelName.MSG") + ": " + childBomInfo.getItemId() + ", "
								+ reg.getString("addPartRevError.MSG"), e);
						saveFlag = false;
					}
				}
				childBomInfo.setColor(ColorEnum.Blue.color()); // 背景颜色设置为蓝色
				childBomInfo.setIsExist(true); // 代表已存在
			} else if (childBomInfo.getModify() == false
					&& ItemRevEnum.CommonPartRev.type().equals(childBomInfo.getObjectType())
					&& childBomInfo.getDeleteFlag() == true) {
				if (childBomInfo.getAddFlag() == false) {
					TCComponent[] relatedComponents = itemRevision
							.getRelatedComponents(RelationConstant.TC_IS_REPRESENTED_BY);
					try {
						itemRevision.remove(RelationConstant.TC_IS_REPRESENTED_BY, relatedComponents);
						parentItemRevision.refresh();
					} catch (Exception e) {
						e.printStackTrace();
						Log.log(reg.getString("modelName.MSG") + ": " + childBomInfo.getItemId() + ", "
								+ reg.getString("removePartRevError.MSG"), e);
						saveFlag = false;
					}
				}
			} else if (childBomInfo.getAddFlag() == true
					&& ItemRevEnum.CommonPartRev.type().equals(childBomInfo.getObjectType())
					&& childBomInfo.getModify() == false) { // 对于TC中存在的没有编辑权限的料号，需要设置可以添加到设计对象的表示文件夹下，但是不可以编辑
				if (!checkPartItemRev(parentItemRevision, itemRevision)) { // 判断当前父对象版本表示伪文件夹下是否含有此物料对象版本
					try {
						itemRevision.add(RelationConstant.TC_IS_REPRESENTED_BY, parentItemRevision);
						childBomInfo.setAddFlag(false); // 添加成功后，设置标志，防止重复添加
					} catch (Exception e) {
						e.printStackTrace();
						Log.log(reg.getString("modelName.MSG") + ": " + childBomInfo.getItemId() + ", "
								+ reg.getString("addPartRevError.MSG"), e);
						saveFlag = false;
					}
				}
			} else if (treeTools.getGreenFlag(childBomInfo)
					&& childBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())) { // 对象类型为设计对象，背景颜色为绿色
				Map<String, String> propertyMap = getPropertyMap(childBomInfo, propertiesInfo, itemRevision);
				try {
					// 更新对象版本属性
					itemRevision.setProperties(propertyMap);
					childBomInfo.setColor(ColorEnum.Blue.color()); // 背景颜色设置为蓝色
				} catch (Exception e) {
					e.printStackTrace();
					Log.log(reg.getString("modelName.MSG") + ": " + childBomInfo.getItemId() + ", "
							+ reg.getString("savePropertiesError.MSG"), e);
					saveFlag = false;
				}
			}
			List<BOMInfo> children = childBomInfo.getChild();
			if (CommonTools.isNotEmpty(children)) {
				saveProperties(childBomInfo);
			} else {
				continue;
			}
		}

	}

	/**
	 * 判断当前父对象版本表示伪文件夹下是否含有此物料对象版本
	 * 
	 * @param parentItemRevision
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	private Boolean checkPartItemRev(TCComponentItemRevision parentItemRevision, TCComponentItemRevision itemRevision)
			throws TCException {
		Boolean flag = false;
		parentItemRevision.refresh();
		TCComponent[] relatedComponents = parentItemRevision.getRelatedComponents(RelationConstant.REPRESENTATION_FOR);
		if (CommonTools.isNotEmpty(relatedComponents)) {
			for (TCComponent tcComponent : relatedComponents) {
				if (!(tcComponent instanceof TCComponentItemRevision)) {
					continue;
				}
				TCComponentItemRevision partItemRevision = (TCComponentItemRevision) tcComponent;
				if (itemRevision.getUid().equals(partItemRevision.getUid())) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 重新加载结构树
	 * 
	 * @param deleteFlag用作是否需要在重新加载结构树的过程中是否需要先把背景色为红色的记录移除，true代表需要移除
	 */
	private void restartLoadTree(Boolean deleteFlag) {
		if (deleteFlag) { // 代表需要将数据模型中背景为红色的记录移除
			remove((BOMInfo) checkedTreeViewer.getInput()); // 从数据模型中删除所有背景色为红色，删除标识为true
		}
		treeTools.resetColor(topBomInfo); // 重新设置背景颜色
		checkedTreeViewer.setInput(topBomInfo); // 重新加载数据模型
		checkedTreeViewer.expandAll(); // 展开所有的结点
		treeTools.resetTreeColumn(checkedTreeViewer); // 重新设置各列宽，让其文本可以完全显示
	}

	/**
	 * 从数据模型中删除所有背景色为红色，删除标识为true
	 * 
	 * @param parentBomInfo
	 * @param current
	 */
	private void remove(BOMInfo topBomInfo) {
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {
			return;
		}
		for (Iterator it = childBomInfos.iterator(); it.hasNext();) {
			BOMInfo childBomInfo = (BOMInfo) it.next();
			System.out.println(childBomInfo.getPropertiesInfo().getItem_ID());
			if (childBomInfo.getDeleteFlag() == true
					&& (childBomInfo.getColor().getRGB().red == ColorEnum.Red.color().getRGB().red)
					&& (childBomInfo.getColor().getRGB().green == ColorEnum.Red.color().getRGB().green)
					&& (childBomInfo.getColor().getRGB().blue == ColorEnum.Red.color().getRGB().blue)) {
				it.remove();
			} else {
				remove(childBomInfo);
			}
		}
	}

	/**
	 * 获取属性值
	 * 
	 * @param itemRevision
	 * @param propertiesInfo
	 * @return
	 * @throws TCException
	 */
	private Map<String, String> getPropertyMap(BOMInfo currentBomInfo, PropertiesInfo propertiesInfo,
			TCComponentItemRevision itemRevision) throws TCException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (itemRevision.isValidPropertyName("d9_IR_CustomerPN")
				&& CommonTools.isNotEmpty(propertiesInfo.getCustomerPN())) {
			map.put("d9_IR_CustomerPN", propertiesInfo.getCustomerPN());
		} else if (itemRevision.isValidPropertyName("d9_CustomerPN")
				&& CommonTools.isNotEmpty(propertiesInfo.getCustomerPN())) {
			map.put("d9_CustomerPN", propertiesInfo.getCustomerPN());
		}

		if (itemRevision.isValidPropertyName("d9_IR_HHPN") && CommonTools.isNotEmpty(propertiesInfo.getHHPN())) {
			map.put("d9_IR_HHPN", propertiesInfo.getHHPN());
		} else if (itemRevision.isValidPropertyName("d9_HHPN") && CommonTools.isNotEmpty(propertiesInfo.getHHPN())) {
			map.put("d9_HHPN", propertiesInfo.getHHPN());
		}

		if (itemRevision.isValidPropertyName("d9_IR_ChineseDescription")
				&& CommonTools.isNotEmpty(propertiesInfo.getChineseDescription())) {
			map.put("d9_IR_ChineseDescription", propertiesInfo.getChineseDescription());
		} else if (itemRevision.isValidPropertyName("d9_ChineseDescription")
				&& CommonTools.isNotEmpty(propertiesInfo.getChineseDescription())) {
			map.put("d9_ChineseDescription", propertiesInfo.getChineseDescription());
		}

		if (itemRevision.isValidPropertyName("d9_IR_EnglishDescription")
				&& CommonTools.isNotEmpty(propertiesInfo.getEnglishDescription())) {
			map.put("d9_IR_EnglishDescription", propertiesInfo.getEnglishDescription());
		} else if (itemRevision.isValidPropertyName("d9_EnglishDescription")
				&& CommonTools.isNotEmpty(propertiesInfo.getEnglishDescription())) {
			map.put("d9_EnglishDescription", propertiesInfo.getEnglishDescription());
		}

		if (BUConstant.DTSABUNAME.equals(BUName) || BUConstant.MNTBUNAME.equals(BUName)
				|| BUConstant.PRTBUNAME.equals(BUName)) { // BU为DTSA、MNT、PRT
			if (itemRevision.isValidPropertyName("d9_IR_Material")
					&& CommonTools.isNotEmpty(propertiesInfo.getMaterial())) {
				map.put("d9_IR_Material", propertiesInfo.getMaterial());
			} else if (itemRevision.isValidPropertyName("d9_Material")
					&& CommonTools.isNotEmpty(propertiesInfo.getMaterial())) {
				map.put("d9_Material", propertiesInfo.getMaterial());
			}

			if (itemRevision.isValidPropertyName("d9_IR_MaterialColor")
					&& CommonTools.isNotEmpty(propertiesInfo.getColor())) {
				map.put("d9_IR_MaterialColor", propertiesInfo.getColor());
			} else if (itemRevision.isValidPropertyName("d9_MaterialColor")
					&& CommonTools.isNotEmpty(propertiesInfo.getColor())) {
				map.put("d9_MaterialColor", propertiesInfo.getColor());
			}

			if (itemRevision.isValidPropertyName("d9_IR_Finish")
					&& CommonTools.isNotEmpty(propertiesInfo.getSurfaceFinished())) {
				map.put("d9_IR_Finish", propertiesInfo.getSurfaceFinished());
			} else if (itemRevision.isValidPropertyName("d9_Finish")
					&& CommonTools.isNotEmpty(propertiesInfo.getSurfaceFinished())) {
				map.put("d9_Finish", propertiesInfo.getSurfaceFinished());
			}
		}

		if (BUConstant.DTSABUNAME.equals(BUName) || BUConstant.MNTBUNAME.equals(BUName)) { // BU为DTSA\MNT
			if (itemRevision.isValidPropertyName("d9_IR_PartWeight")
					&& CommonTools.isNotEmpty(propertiesInfo.getPartWeight())) {
				map.put("d9_IR_PartWeight", propertiesInfo.getPartWeight());
			} else if (itemRevision.isValidPropertyName("d9_PartWeight")
					&& CommonTools.isNotEmpty(propertiesInfo.getPartWeight())) {
				map.put("d9_PartWeight", propertiesInfo.getPartWeight());
			}

			if (itemRevision.isValidPropertyName("d9_IR_Remarks")
					&& CommonTools.isNotEmpty(propertiesInfo.getRemark())) {
				map.put("d9_IR_Remarks", propertiesInfo.getRemark());
			} else if (itemRevision.isValidPropertyName("d9_Remarks")
					&& CommonTools.isNotEmpty(propertiesInfo.getRemark())) {
				map.put("d9_Remarks", propertiesInfo.getRemark());
			}

		}

		if (BUConstant.DTSABUNAME.equals(BUName)) { // 当前BU为DTSA
			if (itemRevision.isValidPropertyName("d9_IR_ULClass")
					&& CommonTools.isNotEmpty(propertiesInfo.getULClass())) {
				map.put("d9_IR_ULClass", propertiesInfo.getULClass());
			} else if (itemRevision.isValidPropertyName("d9_ULClass")
					&& CommonTools.isNotEmpty(propertiesInfo.getULClass())) {
				map.put("d9_ULClass", propertiesInfo.getULClass());
			}
			if (itemRevision.isValidPropertyName("d9_IR_Painting")
					&& CommonTools.isNotEmpty(propertiesInfo.getPainting())) {
				map.put("d9_IR_Painting", propertiesInfo.getPainting());
			} else if (itemRevision.isValidPropertyName("d9_Painting")
					&& CommonTools.isNotEmpty(propertiesInfo.getPainting())) {
				map.put("d9_Painting", propertiesInfo.getPainting());
			}
			if (itemRevision.isValidPropertyName("d9_IR_Printing")
					&& CommonTools.isNotEmpty(propertiesInfo.getPrinting())) {
				map.put("d9_IR_Printing", propertiesInfo.getPrinting());
			} else if (itemRevision.isValidPropertyName("d9_Printing")
					&& CommonTools.isNotEmpty(propertiesInfo.getPrinting())) {
				map.put("d9_Printing", propertiesInfo.getPrinting());
			}
			if (itemRevision.isValidPropertyName("d9_Technology")
					&& CommonTools.isNotEmpty(propertiesInfo.getTechnology())) {
				map.put("d9_Technology", propertiesInfo.getTechnology());
			}
			if (itemRevision.isValidPropertyName("d9_Adhesive")
					&& CommonTools.isNotEmpty(propertiesInfo.getADHESIVE())) {
				map.put("d9_Adhesive", propertiesInfo.getADHESIVE());
			} else if (itemRevision.isValidPropertyName("d9_ADHESIVE")
					&& CommonTools.isNotEmpty(propertiesInfo.getADHESIVE())) {
				map.put("d9_ADHESIVE", propertiesInfo.getADHESIVE());
			}
		}
		if (BUConstant.PRTBUNAME.equals(BUName)) { // 当前BU为PRT
			if (itemRevision.isValidPropertyName("d9_IR_CustomerDrawingNumber")
					&& CommonTools.isNotEmpty(propertiesInfo.getCustomerDrawingNumber())) {
				map.put("d9_IR_CustomerDrawingNumber", propertiesInfo.getCustomerDrawingNumber());
			} else if (itemRevision.isValidPropertyName("d9_CustomerDrawingNumber")
					&& CommonTools.isNotEmpty(propertiesInfo.getCustomerDrawingNumber())) {
				map.put("d9_CustomerDrawingNumber", propertiesInfo.getCustomerDrawingNumber());
			}
			if (itemRevision.isValidPropertyName("d9_Customer3DRev")
					&& CommonTools.isNotEmpty(propertiesInfo.getCustomer3DRev())) {
				map.put("d9_Customer3DRev", propertiesInfo.getCustomer3DRev());
			}
			if (itemRevision.isValidPropertyName("d9_Customer2DRev")
					&& CommonTools.isNotEmpty(propertiesInfo.getCustomer2DRev())) {
				map.put("d9_Customer2DRev", propertiesInfo.getCustomer2DRev());
			}
			if (itemRevision.isValidPropertyName("d9_IR_Customer")
					&& CommonTools.isNotEmpty(propertiesInfo.getCustomerID())) {
				map.put("d9_IR_Customer", propertiesInfo.getCustomerID());
			} else if (itemRevision.isValidPropertyName("d9_Customer")
					&& CommonTools.isNotEmpty(propertiesInfo.getCustomerID())) {
				map.put("d9_Customer", propertiesInfo.getCustomerID());
			}
			if (itemRevision.isValidPropertyName("d9_IR_SMPartMatDimension")
					&& CommonTools.isNotEmpty(propertiesInfo.getSmPartMaterialDimension())) {
				map.put("d9_IR_SMPartMatDimension", propertiesInfo.getSmPartMaterialDimension());
			} else if (itemRevision.isValidPropertyName("d9_SMPartMatDimension")
					&& CommonTools.isNotEmpty(propertiesInfo.getSmPartMaterialDimension())) {
				map.put("d9_SMPartMatDimension", propertiesInfo.getSmPartMaterialDimension());
			}
			if (itemRevision.isValidPropertyName("d9_Module") && CommonTools.isNotEmpty(propertiesInfo.getModule())) {
				map.put("d9_Module", propertiesInfo.getModule());
			}
		}

		if (BUConstant.MNTBUNAME.equals(BUName)) { // 当前BU为MNT
			if (itemRevision.isValidPropertyName("d9_ReferenceDimension")
					&& CommonTools.isNotEmpty(propertiesInfo.getReferenceDimension())) {
				map.put("d9_ReferenceDimension", propertiesInfo.getReferenceDimension());
			}

			if (itemRevision.isValidPropertyName("d9_RunnerWeight")
					&& CommonTools.isNotEmpty(propertiesInfo.getRunnerWeight())) {
				map.put("d9_RunnerWeight", propertiesInfo.getRunnerWeight());
			}

			if (itemRevision.isValidPropertyName("d9_TotalWeight")
					&& CommonTools.isNotEmpty(propertiesInfo.getTotalweight())) {
				map.put("d9_TotalWeight", propertiesInfo.getTotalweight());
			}
		}

		if (itemRevision.isValidPropertyName("d9_ManufacturerID")
				&& CommonTools.isNotEmpty(propertiesInfo.getManufacturerID())) {
			if (CommonTools.isNotEmpty(propertiesInfo.getManufacturerID())) {
				map.put("d9_ManufacturerID", propertiesInfo.getManufacturerID());
			}
		}

		if (itemRevision.isValidPropertyName("d9_ManufacturerPN")) {
			if (CommonTools.isNotEmpty(propertiesInfo.getManufacturerPN())) {
				map.put("d9_ManufacturerPN", propertiesInfo.getManufacturerPN());
			}
		}

		if (itemRevision.isValidPropertyName("d9_TempPN")) {
			if (CommonTools.isNotEmpty(propertiesInfo.getBUPN())) {
				map.put("d9_TempPN", propertiesInfo.getBUPN());
			}
		}
		return map;
	}
}
