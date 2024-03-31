package com.foxconn.mechanism.hhpnmaterialapply.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import com.foxconn.mechanism.hhpnmaterialapply.DTSA.DTSATreePropertiesTools;
import com.foxconn.mechanism.hhpnmaterialapply.MNT.MNTTreePropertiesTools;
import com.foxconn.mechanism.hhpnmaterialapply.PRT.PRTTreePropertiesTools;
import com.foxconn.mechanism.hhpnmaterialapply.constants.BUConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemIdPrefix;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemRevEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.PartNotModifyProp;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.hhpnmaterialapply.CustTreeNode;
import com.foxconn.mechanism.hhpnmaterialapply.MyContainerCheckedTreeViewer;
import com.foxconn.mechanism.util.CommonTools;

/**
 * 结构树工具类
 * 
 * @author HuashengYu
 *
 */
public class TreeTools {

	private Boolean singleObjPropSync = false; // 用作单个设计对象属性/物料对象属性同步的标识, true代表已同步，false代表未同步
	private Boolean syncResult = false; // 此次操作是否发生属性同步， true代表发生, false代表没有发生
	private Boolean hasSaveFlag = true; // 作为判断此次操作是否已经保存的标志， 默认设置已经保存过

	public TreeTools() {
		super();
	}

	/**
	 * 设置设计对象叶子节点没有物料对象时高亮
	 * 
	 * @param topBomInfo
	 */
	public void setDesignLeafColor(BOMInfo topBomInfo) {
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {
			if (topBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())
					&& topBomInfo.getPropertiesInfo().getItem_ID().startsWith(ItemIdPrefix.ME.name())) { // 判断对象类型为设计对象，并且零组件ID为ME开头
				topBomInfo.setHighLight(ColorEnum.HighLightColor.color());
			}
			return;
		}

		for (BOMInfo childBomInfo : childBomInfos) {
			setDesignLeafColor(childBomInfo);
		}
	}

	/**
	 * 判断是否已经保存过
	 * 
	 * @param topBomInfo
	 */
	public void checkHasSave(BOMInfo topBomInfo) {
		if (getGreenFlag(topBomInfo) || getRedFlag(topBomInfo)) { // 判断背景色是否为绿色或者红色
			hasSaveFlag = false;
			return;
		}

		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {
			return;
		}

		for (BOMInfo childBomInfo : childBomInfos) {
			checkHasSave(childBomInfo);
		}
	}

	/**
	 * 重新设置颜色
	 * @param topBomInfo
	 */
	public void resetColor(BOMInfo topBomInfo) {
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {
			if (topBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())
					&& topBomInfo.getPropertiesInfo().getItem_ID().startsWith(ItemIdPrefix.ME.name())) { // 判断对象类型为设计对象，并且零组件ID为ME开头
				topBomInfo.setHighLight(ColorEnum.HighLightColor.color());
			}
			return;
		}

		if (topBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())) {
			if (getHighLightFlag(topBomInfo)) { // 判断是否含有高亮颜色
				topBomInfo.setColor(topBomInfo.getColor());
				topBomInfo.setHighLight(null);
			}
		}
		
		for (BOMInfo childBomInfo : childBomInfos) {
			resetColor(childBomInfo);
		}

	}

	/**
	 * 同步结构树中的模型数据(相同ID，不是同一个对象，并且不是待删除的对象)
	 * 
	 * @param topBomInfo
	 * @param currentInfo
	 * @param indexde
	 * @param str
	 * @param BUName      BU名称
	 */
	public void syncTreeModelData(BOMInfo topBomInfo, BOMInfo currentInfo, int index, String value, String BUName) {
		String objectType = topBomInfo.getObjectType();
		if (topBomInfo.getPropertiesInfo().getItem_ID().equals(currentInfo.getPropertiesInfo().getItem_ID())
				&& ItemRevEnum.CommonPartRev.type().equals(objectType)) { // 判断Item_ID是否相同,对象类型为料号对象
			if (topBomInfo.getDeleteFlag() == false && checkGreen(topBomInfo)) { // 当前不为待删除的对象, 并且背景色不为绿色
				switch (BUName) {
				case BUConstant.DTSABUNAME:
					if (DTSATreePropertiesTools.checkModify(topBomInfo, index, value)) { // 结构树单元格内容发生改变
						topBomInfo.setColor(ColorEnum.Green.color()); // 设置绿色背景颜色
					}
					break;
				case BUConstant.MNTBUNAME:
					if (MNTTreePropertiesTools.checkModify(topBomInfo, index, value)) {
						topBomInfo.setColor(ColorEnum.Green.color()); // 设置绿色背景颜色
					}
					break;
				case BUConstant.PRTBUNAME:
					if (PRTTreePropertiesTools.checkModify(topBomInfo, index, value)) {
						topBomInfo.setColor(ColorEnum.Green.color()); // 设置绿色背景颜色
					}
					break;
				default:
					break;
				}
			}
			if (BUConstant.DTSABUNAME.equals(BUName)) {
				DTSATreePropertiesTools.updateProperties(topBomInfo.getPropertiesInfo(), index, value);
			} else if (BUConstant.MNTBUNAME.equals(BUName)) {
				MNTTreePropertiesTools.updateProperties(topBomInfo.getPropertiesInfo(), index, value);
			} else if (BUConstant.PRTBUNAME.equals(BUName)) {
				PRTTreePropertiesTools.updateProperties(topBomInfo.getPropertiesInfo(), index, value);
			}
		}
		List<BOMInfo> childBomInfoList = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfoList)) {
			return;
		}
		for (BOMInfo childBomInfo : childBomInfoList) {
			if (CommonTools.isEmpty(childBomInfo)) {
				continue;
			}
			objectType = childBomInfo.getObjectType();
			List<BOMInfo> child = childBomInfo.getChild();
			if (CommonTools.isNotEmpty(child)) {
				syncTreeModelData(childBomInfo, currentInfo, index, value, BUName);
			} else {
				if (childBomInfo.getPropertiesInfo().getItem_ID().equals(currentInfo.getPropertiesInfo().getItem_ID())
						&& ItemRevEnum.CommonPartRev.type().equals(objectType)) {
					if (childBomInfo.getDeleteFlag() == false && checkGreen(childBomInfo)) { // 当前不为待删除的对象, 并且背景色不为绿色
						switch (BUName) {
						case BUConstant.DTSABUNAME:
							if (DTSATreePropertiesTools.checkModify(childBomInfo, index, value)) {
								childBomInfo.setColor(ColorEnum.Green.color()); // 设置绿色背景颜色
							}
							break;
						case BUConstant.MNTBUNAME:
							if (MNTTreePropertiesTools.checkModify(childBomInfo, index, value)) {
								childBomInfo.setColor(ColorEnum.Green.color()); // 设置绿色背景颜色
							}
							break;
						case BUConstant.PRTBUNAME:
							if (PRTTreePropertiesTools.checkModify(topBomInfo, index, value)) {
								childBomInfo.setColor(ColorEnum.Green.color()); // 设置绿色背景颜色
							}
							break;
						default:
							break;
						}
					}
					if (BUConstant.DTSABUNAME.equals(BUName)) {
						DTSATreePropertiesTools.updateProperties(childBomInfo.getPropertiesInfo(), index, value);
					} else if (BUConstant.MNTBUNAME.equals(BUName)) {
						MNTTreePropertiesTools.updateProperties(childBomInfo.getPropertiesInfo(), index, value);
					} else if (BUConstant.PRTBUNAME.equals(BUName)) {
						PRTTreePropertiesTools.updateProperties(childBomInfo.getPropertiesInfo(), index, value);
					}
				}
			}
		}
	}

	/**
	 * 刷新结构树中类型为料号对象的属性
	 * 
	 * @param topItem
	 */
	public void refreshTreeItemProperties(TreeViewer tv, TreeItem topItem) {
		CustTreeNode topCustTreeNode = (CustTreeNode) topItem.getData();
//		if (ItemRevEnum.CommonPartRev.type().equals(topCustTreeNode.getBomInfo().getObjectType())) { // 当此结点时料号对象时
		tv.refresh(topCustTreeNode, true);
//		}
		TreeItem[] childTreeItems = topItem.getItems();
		if (CommonTools.isEmpty(childTreeItems)) {
			return;
		}
		for (TreeItem childTreeItem : childTreeItems) {
			CustTreeNode childCustTreeNode = (CustTreeNode) childTreeItem.getData();
			if (CommonTools.isEmpty(childCustTreeNode)) {
				continue;
			}
			BOMInfo childBomInfo = childCustTreeNode.getBomInfo();
			if (CommonTools.isEmpty(childBomInfo)) {
				continue;
			}
			TreeItem[] children = childTreeItem.getItems();
			if (CommonTools.isNotEmpty(children)) {
				refreshTreeItemProperties(tv, childTreeItem);
			} else {
//				if (ItemRevEnum.CommonPartRev.type().equals(childBomInfo.getObjectType())) { // 当此结点时料号对象时
				tv.refresh(childCustTreeNode, true);
//				}
			}
		}
	}

	/**
	 * 刷新结构树中父结构树的第一层子结构树(目的是减少加载时间，实现懒加载，展开哪个层级，刷新哪个层级的属性)
	 * 
	 * @param tv
	 * @param topItem
	 */
	public void refreshSingleTreeItemProperties(TreeViewer tv, TreeItem topItem) {
		TreeItem[] childTreeItems = topItem.getItems();
		if (CommonTools.isEmpty(childTreeItems)) {
			return;
		}
		for (TreeItem childTreeItem : childTreeItems) {
			CustTreeNode childCustTreeNode = (CustTreeNode) childTreeItem.getData();
			if (CommonTools.isEmpty(childCustTreeNode)) {
				continue;
			}
			BOMInfo childBomInfo = childCustTreeNode.getBomInfo();
			if (CommonTools.isEmpty(childBomInfo)) {
				continue;
			}
			if (ItemRevEnum.CommonPartRev.type().equals(childBomInfo.getObjectType())) { // 当此结点时料号对象时
				tv.refresh(childCustTreeNode, true);
			}
		}
	}

	/**
	 * 判断当前对象的背景色不是绿色
	 * 
	 * @param currentBomInfo
	 * @return
	 */
	public boolean checkGreen(BOMInfo currentBomInfo) {
		if (CommonTools.isEmpty(currentBomInfo.getColor())) {
			return false;
		} else {
			return currentBomInfo.getColor().getRGB().red != ColorEnum.Green.color().getRGB().red
					&& currentBomInfo.getColor().getRGB().green != ColorEnum.Green.color().getRGB().green
					&& currentBomInfo.getColor().getRGB().blue != ColorEnum.Green.color().getRGB().blue;
		}
	}

	/**
	 * 获取是否为绿色
	 * 
	 * @param currentBomInfo
	 * @return
	 */
	public boolean getGreenFlag(BOMInfo currentBomInfo) {
		return currentBomInfo.getColor().getRGB().red == ColorEnum.Green.color().getRGB().red
				&& currentBomInfo.getColor().getRGB().green == ColorEnum.Green.color().getRGB().green
				&& currentBomInfo.getColor().getRGB().blue == ColorEnum.Green.color().getRGB().blue;
	}

	/**
	 * 判断是否为红色
	 * 
	 * @param currentBomInfo
	 * @return
	 */
	public boolean getRedFlag(BOMInfo currentBomInfo) {
		return currentBomInfo.getColor().getRGB().red == ColorEnum.Red.color().getRGB().red
				&& currentBomInfo.getColor().getRGB().green == ColorEnum.Red.color().getRGB().green
				&& currentBomInfo.getColor().getRGB().blue == ColorEnum.Red.color().getRGB().blue;
	}

	
	/**
	 * 判断是否有高亮颜色
	 * 
	 * @param currentBomInfo
	 * @return
	 */
	public boolean getHighLightFlag(BOMInfo currentBomInfo) {
		if (CommonTools.isNotEmpty(currentBomInfo.getHighLight())) {
			return currentBomInfo.getHighLight().getRGB().red == ColorEnum.HighLightColor.color().getRGB().red
					&& currentBomInfo.getHighLight().getRGB().green == ColorEnum.HighLightColor.color().getRGB().green
					&& currentBomInfo.getHighLight().getRGB().blue == ColorEnum.HighLightColor.color().getRGB().blue;
		}
		return false;
	}

	/**
	 * 递归修改结构树复选框状态
	 * 
	 * @param topItem
	 * @param flag
	 */
	public void setTreeItemCheckStates(TreeItem topItem, boolean flag) {
		if (topItem.getChecked() != flag) {
			topItem.setChecked(flag);
		}
		TreeItem[] childTreeItems = topItem.getItems();
		if (CommonTools.isEmpty(childTreeItems)) {
			return;
		}
		for (TreeItem childTreeItem : childTreeItems) {
			TreeItem[] children = childTreeItem.getItems();
			if (CommonTools.isNotEmpty(children)) {
				setTreeItemCheckStates(childTreeItem, flag);
			} else {
				if (childTreeItem.getChecked() != flag) {
					childTreeItem.setChecked(flag);
				}
			}
		}
	}

	/**
	 * 设置叶子节点复选框状态
	 */
	public void setLeafItemCheckStates(TreeItem topItem, boolean flag) {
		CustTreeNode topCustTreeNode = (CustTreeNode) topItem.getData();
		if (CommonTools.isEmpty(topCustTreeNode)) {
			return;
		}
		if (checkLeafNode(topCustTreeNode)) { // 判断是否为叶子节点
//			if (topItem.getChecked() != flag) {
			topItem.setChecked(flag);
//			}
		}
		TreeItem[] childTreeItems = topItem.getItems();
		for (TreeItem childTreeItem : childTreeItems) {
			setLeafItemCheckStates(childTreeItem, flag);
		}
	}

	/**
	 * 判断是否为叶子节点
	 * 
	 * @param topCustTreeNode
	 */
	public boolean checkLeafNode(CustTreeNode topCustTreeNode) {
		boolean flag = true;
		String objectType = null;
		List<CustTreeNode> childCustTreeNodes = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(childCustTreeNodes)) {
			BOMInfo bomInfo = topCustTreeNode.getBomInfo();
			if (CommonTools.isEmpty(bomInfo)) {
				return false;
			}
			PropertiesInfo propertiesInfo = bomInfo.getPropertiesInfo();
			if (CommonTools.isEmpty(propertiesInfo)) {
				return false;
			}

			String itemId = propertiesInfo.getItem_ID();
			objectType = bomInfo.getObjectType();
			if (objectType.contains(ItemRevEnum.DesignRev.type()) && !itemId.startsWith(ItemIdPrefix.ME.name())) { // 如果对象类型为设计对象，并且ItemID不是以ME开头
				flag = false;
			}
		} else {
			for (CustTreeNode childCustTreeNode : childCustTreeNodes) {
				BOMInfo childBomInfo = childCustTreeNode.getBomInfo();
				if (CommonTools.isEmpty(childBomInfo)) {
					continue;
				}

				objectType = childBomInfo.getObjectType();
				if (objectType.contains(ItemRevEnum.DesignRev.type())) { // 如果子结点含有设计对象，则代表不是根结点
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 展示父结构树的第一层子结构树是否被选中(目的是减少加载时间，实现懒加载，展开哪个层级，显示那个层级的选中状态)
	 * 
	 * @param topItem
	 */
	public void showSingleTreeItemCheckStates(TreeItem topItem) {
		TreeItem[] childTreeItems = topItem.getItems();
		if (CommonTools.isEmpty(childTreeItems)) {
			return;
		}
		for (TreeItem childTreeItem : childTreeItems) {
			CustTreeNode childCustTreeNode = (CustTreeNode) childTreeItem.getData();
			if (CommonTools.isEmpty(childTreeItems)) {
				continue;
			}
			childTreeItem.setChecked(childCustTreeNode.getCheckedStates());
		}
	}

	/**
	 * 展示所有展开的结构树的是否选中状态
	 * 
	 * @param topItem
	 */
	public void showTreeItemCheckStates(TreeItem topItem) {
		CustTreeNode topCustTreeNode = (CustTreeNode) topItem.getData();
		if (CommonTools.isEmpty(topCustTreeNode)) {
			return;
		}
		topItem.setChecked(topCustTreeNode.getCheckedStates());

		TreeItem[] childTreeItems = topItem.getItems();
		if (CommonTools.isEmpty(childTreeItems)) {
			return;
		}

		for (TreeItem childTreeItem : childTreeItems) {
			TreeItem[] children = childTreeItem.getItems();
			if (CommonTools.isNotEmpty(children)) {
				showTreeItemCheckStates(childTreeItem);
			} else {
				CustTreeNode childCustTreeNode = (CustTreeNode) childTreeItem.getData();
				if (CommonTools.isEmpty(childCustTreeNode)) {
					continue;
				}
				childTreeItem.setChecked(childCustTreeNode.getCheckedStates());
			}
		}
	}

	/**
	 * 同步设计对象和物料对象属性(DT)
	 * 
	 * @param topBomInfo
	 * @param designSyncFlag true 代表此时操作是设计对象属性同步物料对象属性
	 * @param partSyncFlag   true 代表此时操作是物料对象属性同步设计对象属性
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void syncForDT(BOMInfo topBomInfo, boolean designSyncFlag, boolean partSyncFlag)
			throws IllegalArgumentException, IllegalAccessException {
		Boolean syncFlag = topBomInfo.getSyncFlag();
		if (!syncFlag) { // 判断可否同步的标识是否为true
			return;
		}

		String objectType = topBomInfo.getObjectType();
		if (!objectType.contains(ItemRevEnum.DesignRev.type())) { // 判断是否为设计对象
			return;
		}
		List<BOMInfo> childBomInfoList = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfoList)) {
			return;
		}

		if (designSyncFlag) {
			designSyncPartData(topBomInfo); // 将设计对象属性同步到物料对象属性
		} else if (partSyncFlag) {
			partSyncDesignData(topBomInfo); // 将物料对象属性同步到设计对象属性
		}

		for (BOMInfo childBomInfo : childBomInfoList) {
			syncForDT(childBomInfo, designSyncFlag, partSyncFlag);
		}
	}

	/**
	 * 设计对象同步物料对象属性(MNT/PRT)
	 * 
	 * @param designSyncFlag true 代表此时操作是设计对象属性同步物料对象属性
	 * @param partSyncFlag   true 代表此时操作是物料对象属性同步设计对象属性
	 * @param list
	 */
	public void syncForMNTOrPRT(List<BOMInfo> list, BOMInfo topBomInfo, boolean designSyncFlag, boolean partSyncFlag) {
		list.parallelStream().forEach(info -> {
			try {
				syncDataForMNTOrPRT(topBomInfo, info, designSyncFlag, partSyncFlag);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 设计对象数据同步物料对象属性(MNT/PRT)
	 * 
	 * @param topBomInfo
	 * @param currentInfo
	 * @param designSyncFlag true 代表此时操作是设计对象属性同步物料对象属性
	 * @param partSyncFlag   true 代表此时操作是物料对象属性同步设计对象属性
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void syncDataForMNTOrPRT(BOMInfo topBomInfo, BOMInfo currentInfo, boolean designSyncFlag,
			boolean partSyncFlag) throws IllegalArgumentException, IllegalAccessException {
		List<BOMInfo> childBomInfoList = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfoList)) {
			return;
		}
		String objectType = topBomInfo.getObjectType();
		if (topBomInfo.getPropertiesInfo().getItem_ID().equals(currentInfo.getPropertiesInfo().getItem_ID())
				&& objectType.contains(ItemRevEnum.DesignRev.type())) { // 判断Item_ID是否相同，对象类型为设计对象
			if (designSyncFlag) {
				designSyncPartData(topBomInfo); // 将设计对象属性同步到物料对象属性
			} else if (partSyncFlag) {
				partSyncDesignData(topBomInfo); // 将物料对象属性同步到设计对象属性
			}
		}

		for (BOMInfo childBomInfo : childBomInfoList) {
			syncDataForMNTOrPRT(childBomInfo, currentInfo, designSyncFlag, partSyncFlag);
		}
	}

	/**
	 * 将设计对象属性同步到物料对象属性
	 * 
	 * @param bomInfo
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void designSyncPartData(BOMInfo bomInfo) throws IllegalArgumentException, IllegalAccessException {
		PropertiesInfo designPropertiesInfo = bomInfo.getPropertiesInfo();
		if (CommonTools.isEmpty(designPropertiesInfo)) {
			return;
		}

		for (BOMInfo childBomInfo : bomInfo.getChild()) {
			if (childBomInfo.getObjectType().contains(ItemRevEnum.DesignRev.type())) { // 如果是设计对象直接返回
				continue;
			}

			PropertiesInfo partPropertiesInfo = childBomInfo.getPropertiesInfo();
			if (CommonTools.isEmpty(partPropertiesInfo)) {
				continue;
			}

			PropertiesInfo newPartPropertiesInfo = null;
			Boolean flag = null;
			if (checkHHPNPart(childBomInfo)) { // 判断是否为鸿海物料对象
				newPartPropertiesInfo = dataPropMapping(designPropertiesInfo, partPropertiesInfo, true); // 将设计对象部分属性同步到物料属性
//				flag = checkPropModify(partPropertiesInfo, newPartPropertiesInfo, true); // 判断属性是否发生改变
			} else {
				newPartPropertiesInfo = dataPropMapping(designPropertiesInfo, partPropertiesInfo, false); // 将设计对象部分属性同步到物料属性
//				flag = checkPropModify(partPropertiesInfo, newPartPropertiesInfo, false); // 判断属性是否发生改变
			}

			if (!singleObjPropSync) {
				return;
			}

			childBomInfo.setPropertiesInfo(newPartPropertiesInfo);
			bomInfo.updateSyncResult(true); // 将此父设计对象的更新结果设置为true

			if (childBomInfo.getDeleteFlag() == false && checkGreen(childBomInfo) && singleObjPropSync) { // 当前不为待删除的对象,
																											// 并且背景色不为绿色
																											// , 属性发生改变
				childBomInfo.setColor(ColorEnum.Green.color()); // 设置背景颜色为绿色
			}
			setSingleObjPropSync(false); // 重新将属性同步的标识设置为false，作为下一个循环判断属性是否发生改变的标识位

		}
	}

	/**
	 * 将物料对象属性同步到设计对象属性
	 * 
	 * @param bomInfo
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void partSyncDesignData(BOMInfo bomInfo) throws IllegalArgumentException, IllegalAccessException {
		PropertiesInfo designPropertiesInfo = bomInfo.getPropertiesInfo();
		if (CommonTools.isEmpty(designPropertiesInfo)) {
			return;
		}

		PropertiesInfo partPropertiesInfo = getPartPropertiesInfo(bomInfo); // 返回符合条件的第一个物料对象数据类
		if (CommonTools.isEmpty(partPropertiesInfo)) {
			return;
		}

		PropertiesInfo newDesignPropertiesInfo = dataPropMapping(partPropertiesInfo, designPropertiesInfo, false); // 将物料对象部分属性同步到设计对象属性
//		Boolean flag = checkPropModify(designPropertiesInfo, newDesignPropertiesInfo, false); // 判断属性是否发生变化
		if (!singleObjPropSync) {
			return;
		}

		bomInfo.setPropertiesInfo(newDesignPropertiesInfo);
		bomInfo.updateSyncResult(true); // 将此父设计对象的更新结果设置为true
//		bomInfo.setModify(true); // 设置此设计对象为可以便捷

		if (!bomInfo.getDeleteFlag() && checkGreen(bomInfo) && singleObjPropSync) { // 当前不为待删除的对象，并且背景色不为绿色，属性发生了改变
			bomInfo.setColor(ColorEnum.Green.color()); // 设置背景颜色为绿色
		}
		setSingleObjPropSync(false); // 重新将属性同步的标识设置为false，作为下一个循环判断属性是否发生改变的标识位
	}

	/**
	 * 返回符合条件的第一个物料对象数据类
	 * 
	 * @param bomInfo
	 * @return
	 */
	private PropertiesInfo getPartPropertiesInfo(BOMInfo bomInfo) {
		PropertiesInfo partPropertiesInfo = null;
		List<BOMInfo> childBomInfoList = bomInfo.getChild();
		String objectType = null;
		for (BOMInfo childBomInfo : childBomInfoList) {
			objectType = childBomInfo.getObjectType();
			if (objectType.contains(ItemRevEnum.DesignRev.type())) {
				continue;
			}

			if (childBomInfo.getDeleteFlag() == true
					&& (childBomInfo.getColor().getRGB().red == ColorEnum.Red.color().getRGB().red)
					&& (childBomInfo.getColor().getRGB().green == ColorEnum.Red.color().getRGB().green)
					&& (childBomInfo.getColor().getRGB().blue == ColorEnum.Red.color().getRGB().blue)) { // 如果此物料对象数据模型中删除所有背景色为红色，删除标识为true，则下一次循环
				continue;
			}

			partPropertiesInfo = childBomInfo.getPropertiesInfo();
			if (CommonTools.isEmpty(partPropertiesInfo)) {
				continue;
			}
			break;
		}
		return partPropertiesInfo;
	}

	/**
	 * 返回属性更新成功的节点记录
	 * 
	 * @param list
	 * @param checkedTreeViewer
	 * @return
	 */
	public List<CustTreeNode> getPropSyncSuccessNodeList(MyContainerCheckedTreeViewer checkedTreeViewer,
			List<BOMInfo> list) {
		List<Object> checkedElementList = new ArrayList<>(Arrays.asList(checkedTreeViewer.getCheckedElements()));
		ListIterator listIterator = checkedElementList.listIterator();
		while (listIterator.hasNext()) {
			CustTreeNode custTreeNode = (CustTreeNode) listIterator.next();
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			if (ItemRevEnum.CommonPartRev.type().equals(bomInfo.getObjectType())) { // 移除记录为物料对象的记录
				listIterator.remove();
				continue;
			}
			if (!bomInfo.getSyncResult()) { // 移除更新标识不是true的记录
				listIterator.remove();
				continue;
			}
			boolean flag = list.stream().anyMatch(info -> info.getItemId().equals(bomInfo.getItemId()));
			if (!flag) { // 移除itemId不一致的记录
				listIterator.remove();
			}
		}

		if (CommonTools.isEmpty(checkedElementList)) {
			return null;
		}
		return BOMTreeTools.getCustTreeNodeList(checkedElementList);
	}

	/**
	 * 展开
	 * 
	 * @param checkedTreeViewer
	 * @param topCustTreeNode
	 * @param currentInfo
	 */
	public void expandSuccNode(MyContainerCheckedTreeViewer checkedTreeViewer, CustTreeNode topCustTreeNode,
			BOMInfo currentInfo) {
		List<CustTreeNode> custTreeNodeList = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(custTreeNodeList)) {
			return;
		}

		for (CustTreeNode childCustTreeNode : custTreeNodeList) {
			BOMInfo bomInfo = childCustTreeNode.getBomInfo();
			if (CommonTools.isEmpty(bomInfo)) {
				continue;
			}

			if (bomInfo.getPropertiesInfo().getItem_ID().equals(currentInfo.getPropertiesInfo().getItem_ID())
					&& bomInfo.getObjectType().equals(currentInfo.getObjectType())) {
				expandCurrNode(checkedTreeViewer, childCustTreeNode, true); // 展开当前节点的所有路径
				checkedTreeViewer.refresh(childCustTreeNode, true);
			}
			expandSuccNode(checkedTreeViewer, childCustTreeNode, currentInfo);
		}
	}

	/**
	 * 展示当前节点的所有路径
	 * 
	 * @param checkedTreeViewer
	 * @param currentCustTreeNode
	 * @param expandFlag
	 */
	public void expandCurrNode(MyContainerCheckedTreeViewer checkedTreeViewer, CustTreeNode current,
			boolean expandFlag) {
		if (CommonTools.isNotEmpty(current.getParentTreeNode())) {
			expandCurrNode(checkedTreeViewer, current.getParentTreeNode(), expandFlag);
		}

		if (!checkedTreeViewer.getExpandedState(current) && CommonTools.isNotEmpty(current.getChildren())
				&& expandFlag) {
			checkedTreeViewer.expandToLevel(current, 1);
			showCustNodeCheckStates(checkedTreeViewer, current);
			resetTreeColumn(checkedTreeViewer);
		}
	}

	/**
	 * 展示节点的选中状态
	 * 
	 * @param checkedTreeViewer
	 * @param topCustTreeNode
	 */
	public void showCustNodeCheckStates(MyContainerCheckedTreeViewer checkedTreeViewer, CustTreeNode topCustTreeNode) {
		checkedTreeViewer.setChecked(topCustTreeNode, topCustTreeNode.getCheckedStates());
		List<CustTreeNode> children = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(children)) {
			return;
		}

		for (CustTreeNode chidCustTreeNode : children) {
			showCustNodeCheckStates(checkedTreeViewer, chidCustTreeNode);
		}
	}

	/**
	 * 重新设置各列宽，让其文本可以完全显示
	 */
	public void resetTreeColumn(MyContainerCheckedTreeViewer checkedTreeViewer) {
		checkedTreeViewer.getControl().getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				TreeColumn[] treeColumns = checkedTreeViewer.getTree().getColumns();
				for (TreeColumn treeColumn : treeColumns) {
					if (treeColumn.getWidth() == 0) {
						continue;
					}
					treeColumn.pack();
				}
			}
		});
	}

	/**
	 * 判断当前物料对象是否为鸿海料号对象
	 * 
	 * @param bomInfo
	 * @return
	 */
	private boolean checkHHPNPart(BOMInfo bomInfo) {
		PropertiesInfo propertiesInfo = bomInfo.getPropertiesInfo();
		String HHPN = propertiesInfo.getHHPN();
		if (CommonTools.isNotEmpty(HHPN)) {
			return true;
		}
		return false;
	}

	/**
	 * 设置结点状态
	 * 
	 * @param topCustTreeNode
	 * @param flag
	 */
	public void setCustTreeNodeCheckStates(CustTreeNode topCustTreeNode, boolean flag) {
		if (CommonTools.isNotEmpty(topCustTreeNode)) {
			topCustTreeNode.setCheckedStates(flag);
		}
		List<CustTreeNode> childCustTreeNodes = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(childCustTreeNodes)) {
			return;
		}
		for (CustTreeNode childCustTreeNode : childCustTreeNodes) {
			List<CustTreeNode> children = childCustTreeNode.getChildren();
			if (CommonTools.isNotEmpty(children)) {
				setCustTreeNodeCheckStates(childCustTreeNode, flag);
			} else {
				if (CommonTools.isNotEmpty(childCustTreeNode)) {
					childCustTreeNode.setCheckedStates(flag);
				}
			}
		}
	}

	/**
	 * 设置叶子节点的状态
	 * 
	 * @param topCustTreeNode
	 * @param flag
	 */
	public void setLeafCustTreeNodeCheckStates(CustTreeNode topCustTreeNode, boolean flag) {
		if (checkLeafNode(topCustTreeNode)) { // 判断是否为叶子节点
			topCustTreeNode.setCheckedStates(flag);
		} else { // 假如不是叶子节点，则把复选框去掉
			topCustTreeNode.setCheckedStates(!flag);
		}
		List<CustTreeNode> childCustTreeNodes = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(childCustTreeNodes)) {
			return;
		}

		for (CustTreeNode childCustTreeNode : childCustTreeNodes) {
			setLeafCustTreeNodeCheckStates(childCustTreeNode, flag);
		}
	}

	/**
	 * 展示结构树是否被选中和是否需要展开
	 * 
	 * @param checkedTreeViewer
	 * @param topItem
	 * @param expandFlag        代表是否需要展开
	 */
	public void showTreeItemCheckStates(MyContainerCheckedTreeViewer checkedTreeViewer, TreeItem topItem,
			boolean expandFlag) {
		CustTreeNode topCustTreeNode = (CustTreeNode) topItem.getData();
		if (!checkedTreeViewer.getExpandedState(topCustTreeNode)
				&& CommonTools.isNotEmpty(topCustTreeNode.getChildren()) && expandFlag) {
			checkedTreeViewer.expandToLevel(topCustTreeNode, 1);
		}
		topItem.setChecked(topCustTreeNode.getCheckedStates());
		TreeItem[] childTreeItems = topItem.getItems();
		if (CommonTools.isEmpty(childTreeItems)) {
			return;
		}
		for (TreeItem childTreeItem : childTreeItems) {
			TreeItem[] children = childTreeItem.getItems();
			if (CommonTools.isNotEmpty(children)) {
				showTreeItemCheckStates(checkedTreeViewer, childTreeItem, expandFlag);
			} else {
				CustTreeNode childCustTreeNode = (CustTreeNode) childTreeItem.getData();
				if (!checkedTreeViewer.getExpandedState(childCustTreeNode)
						&& CommonTools.isNotEmpty(childCustTreeNode.getChildren()) && expandFlag) {
					checkedTreeViewer.expandToLevel(childCustTreeNode, 1);
				}
				childTreeItem.setChecked(childCustTreeNode.getCheckedStates());
				childTreeItem.getParentItem();
			}
		}
	}

	/**
	 * 展开叶子节点的所有路径
	 * 
	 * @param checkedTreeViewer
	 * @param topCustTreeNode
	 * @param expandFlag
	 */
	public void expandLeafTreeItem(MyContainerCheckedTreeViewer checkedTreeViewer, CustTreeNode topCustTreeNode,
			boolean expandFlag) {
//		checkedTreeViewer.setChecked(topCustTreeNode, topCustTreeNode.getCheckedStates());
		List<CustTreeNode> childCustTreeNodes = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(childCustTreeNodes)) {
			expandCurrNode(checkedTreeViewer, topCustTreeNode, true); // 展示当前节点的所有路径
			BOMInfo topBomInfo = topCustTreeNode.getBomInfo();
			String objectType = topBomInfo.getObjectType();
			if (ItemRevEnum.CommonPartRev.type().equals(objectType)) {
				CustTreeNode parentTreeNode = topCustTreeNode.getParentTreeNode();
				if (CommonTools.isNotEmpty(parentTreeNode)) {
					checkedTreeViewer.setChecked(parentTreeNode, parentTreeNode.getCheckedStates());
				}
			}
			checkedTreeViewer.setChecked(topCustTreeNode, topCustTreeNode.getCheckedStates());
		} else {
			for (CustTreeNode childCustTreeNode : childCustTreeNodes) {
				expandLeafTreeItem(checkedTreeViewer, childCustTreeNode, expandFlag);
			}
		}
	}

	/**
	 * 设计对象和物料对象属性映射
	 * 
	 * @param <T>
	 * @param bean1
	 * @param bean2
	 * @param HHPNPartFlag
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private <T> T dataPropMapping(T bean1, T bean2, boolean HHPNPartFlag)
			throws IllegalArgumentException, IllegalAccessException {
		if (CommonTools.isNotEmpty(bean1) && CommonTools.isNotEmpty(bean2)) {
			Field[] fields1 = bean1.getClass().getDeclaredFields();
			Field[] fields2 = bean2.getClass().getDeclaredFields();
			for (int i = 0; i < fields2.length; i++) {
				fields1[i].setAccessible(true);
				fields2[i].setAccessible(true);
				String fieldName = fields2[i].getName();
				if (HHPNPartFlag) {
					if (checkNotModifyProp(Arrays.asList(PartNotModifyProp.ITEM_ID, PartNotModifyProp.HHPN,
							PartNotModifyProp.HHREVISION, PartNotModifyProp.HHPNSTATE, PartNotModifyProp.MANUFACTURERID,
							PartNotModifyProp.MANUFACTURERPN, PartNotModifyProp.BUPN, PartNotModifyProp.CUSTOMERID,
							PartNotModifyProp.CUSTOMERPN, PartNotModifyProp.ENGLISHDESCRIPTION), fieldName)) { // 判断是否为不是可以修改的属性
						continue;
					}

				} else {
					if (checkNotModifyProp(
							Arrays.asList(PartNotModifyProp.ITEM_ID, PartNotModifyProp.HHPN,
									PartNotModifyProp.HHREVISION, PartNotModifyProp.HHPNSTATE, PartNotModifyProp.BUPN),
							fieldName)) { // 判断是否为不是可以修改的属性
						continue;
					}
				}

				if (fields2[i].getType() != String.class) {
					continue;
				}
				Object object = fields1[i].get(bean1);
				String str = object == null ? "" : object.toString().trim();
				if (CommonTools.isEmpty(str)) {
					continue;
				}
				fields2[i].set(bean2, str);
				setSingleObjPropSync(true); // 设置属性同步表示为true, 代表已经同步
				setSyncResult(true); // 代表此次操作已经发生了属性同步操作
			}
		}
		return bean2;
	}

	/**
	 * 判断是否属于无法更改的属性
	 * 
	 * @param list
	 * @param fieldName
	 * @return
	 */
	private boolean checkNotModifyProp(List<String> list, String fieldName) {
		return list.stream().anyMatch(str -> str.trim().equals(fieldName));
	}

	public Boolean getSingleObjPropSync() {
		return singleObjPropSync;
	}

	public void setSingleObjPropSync(Boolean singleObjPropSync) {
		this.singleObjPropSync = singleObjPropSync;
	}

	public Boolean getSyncResult() {
		return syncResult;
	}

	public void setSyncResult(Boolean syncResult) {
		this.syncResult = syncResult;
	}

	public void setHasSaveFlag(Boolean hasSaveFlag) {
		this.hasSaveFlag = hasSaveFlag;
	}

	public Boolean getHasSaveFlag() {
		return hasSaveFlag;
	}

}
