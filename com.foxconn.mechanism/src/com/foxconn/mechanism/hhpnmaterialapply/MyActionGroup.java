package com.foxconn.mechanism.hhpnmaterialapply;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemRevEnum;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.util.TreeTools;
import com.foxconn.mechanism.util.CommonTools;
import com.teamcenter.rac.util.Registry;

public class MyActionGroup extends ActionGroup {

	private MyContainerCheckedTreeViewer tv;
	private Registry reg = null;
	private Label tipLabel = null; // 当前选中行提示标签
	private TreeTools treeTools = null;
	
	public MyActionGroup(MyContainerCheckedTreeViewer tv, Registry reg, Label tipLabel, TreeTools treeTools) {
		this.tv = tv;
		this.reg = reg;
		this.tipLabel = tipLabel;
		this.treeTools = treeTools;
	}

	public void fillContextMenu(IMenuManager mgr) {
		// 加入Action对象到菜单管理器
		MenuManager menuManager = (MenuManager) mgr; // 类型转换
		final RemoveEntryAction removeEntryAction = new RemoveEntryAction(); // 移除
		final RecoveryEntryAction recoveryEntryAction = new RecoveryEntryAction(); // 恢复		
		menuManager.setRemoveAllWhenShown(true); // 设置"在每次显示之前先删除全部老菜单"的属性为true
		menuManager.addMenuListener(new IMenuListener() { // 弹出菜单触发

			@Override
			public void menuAboutToShow(IMenuManager imenumanager) {
				List<CustTreeNode> custTreeNodeList = getCustTreeNodes();
				if (CommonTools.isEmpty(custTreeNodeList)) { // 如果未选中结构树行对象，则取消显示
					return;
				}
				boolean flag = false;
				for (CustTreeNode custTreeNode : custTreeNodeList) {
					BOMInfo bomInfo = custTreeNode.getBomInfo();
					String objectType = bomInfo.getObjectType();
					if (ItemRevEnum.CommonPartRev.type().equals(objectType) && bomInfo.getRemoveFlag() == true) { // 判断是否选中了商用料号对象
						flag = true;						
						break;
					}
				}
				if (flag) {
					menuManager.add(removeEntryAction);
				}

				int count = 0;
				for (CustTreeNode custTreeNode2 : custTreeNodeList) {
					BOMInfo bomInfo = custTreeNode2.getBomInfo();
					String objectType = bomInfo.getObjectType();
					if (ItemRevEnum.CommonPartRev.type().equals(objectType) && checkRed(bomInfo) && bomInfo.getRemoveFlag() == false) {// 判断是否为CommercialPart,背景颜色是红色, 才显示此菜单																
						count++;						
					}
				}

				if (custTreeNodeList.size() == count) {
					menuManager.add(recoveryEntryAction);
				}
			}
		});

		// 生成Menu并挂在树Tree上
		Tree tree = tv.getTree();
		Menu menu = menuManager.createContextMenu(tree);
		tree.setMenu(menu);
	}

	private class RemoveEntryAction extends Action {
		public RemoveEntryAction() {
			setText(reg.getString("rightRemoveBtn.LABEL"));			
		}

		public void run() {
			CustTreeNode topCustTreeNode = (CustTreeNode) tv.getTree().getItems()[0].getData();			
//			CustTreeNode topCustTreeNode = (CustTreeNode) tv.getExpandedElements()[0];
			List<CustTreeNode> custTreeNodeList = getCustTreeNodes();
			for (CustTreeNode custTreeNode : custTreeNodeList) {
				BOMInfo current = custTreeNode.getBomInfo();
				String objectType = current.getObjectType();
				if (ItemRevEnum.CommonPartRev.type().equals(objectType) && current.getIsExist() == true) {
					if (topCustTreeNode.getBomInfo().getPropertiesInfo().getItem_ID()
							.equals(custTreeNode.getBomInfo().getPropertiesInfo().getItem_ID())) {
						BOMInfo topBomInfo = topCustTreeNode.getBomInfo();
						topBomInfo.setBeforeModifyColor(topBomInfo.getColor()); // 记录一下修改之前的颜色
						topBomInfo.setColor(ColorEnum.Red.color());
						topBomInfo.setDeleteFlag(true); // 将删除标识设置为true
						topBomInfo.setModify(false); // 设置不可以修改
						topBomInfo.setRemoveFlag(false);
						topBomInfo.setAddFlag(false); // 设置不可以添加到设计对象下						
						topCustTreeNode.setBomInfo(topBomInfo);		
						treeTools.expandCurrNode(tv, topCustTreeNode, true); // 展示当前节点的所有路径						
						tv.refresh(topCustTreeNode, true); // 刷新结点	
						tv.setSelection(StructuredSelection.EMPTY); // 去除当前行被选中的效果
						tipLabel.setText("");
					}					
					remove(topCustTreeNode, custTreeNode, false); // 从数据模型中将结点颜色设置为红色
				} else if (ItemRevEnum.CommonPartRev.type().equals(objectType) && current.getModify() == true
						&& current.getIsExist() == false) {
//					current.setDeleteFlag(true); // 将此数据模型的data删除标识设置为true,作为后续递归遍历删除的标识
//					custTreeNode.setDeleteFlag(true); // 将数据模型的删除标识设置为true,作为后续递归遍历删除的标识
					BOMInfo topBomInfo = (BOMInfo) tv.getInput();					
					removeData(topBomInfo, current); // 从数据模型中删除结点
					remove(topCustTreeNode, custTreeNode, true); // 从数据模型中删除结点
				}
			}
		}
	}	

	/**
	 * 移除父结点下面的符合条件的子结点模型
	 * 
	 * @param topCustTreeNode
	 * @param custTreeNode
	 * @param true代表直接删除结点，false代表将结点标红
	 */
	private void remove(CustTreeNode topCustTreeNode, CustTreeNode custTreeNode, boolean delete) {
		List<CustTreeNode> children = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(children)) {
			children = topCustTreeNode.getList2();
			if (CommonTools.isEmpty(children)) {
				return;
			}			
		}		
		for (Iterator it = children.iterator(); it.hasNext();) {
			CustTreeNode childCustTreeNode = (CustTreeNode) it.next();
			if (childCustTreeNode.getBomInfo().getPropertiesInfo().getItem_ID()
					.equals(custTreeNode.getBomInfo().getPropertiesInfo().getItem_ID())) { // 判断Item_ID是否一致, 相同就删除
				if (delete) {
					it.remove();
					tv.remove(childCustTreeNode);
					treeTools.expandCurrNode(tv, childCustTreeNode, true); // 扩展当前节点的所有路径
					tipLabel.setText(""); 
				} else {
					BOMInfo chidBomInfo = childCustTreeNode.getBomInfo();
					chidBomInfo.setBeforeModifyColor(chidBomInfo.getColor()); // 记录一下修改之前的颜色
					chidBomInfo.setColor(ColorEnum.Red.color());
					chidBomInfo.setDeleteFlag(true); // 将删除标识设置为true
					chidBomInfo.setModify(false); // 设置不可以修改
					chidBomInfo.setRemoveFlag(false);
					chidBomInfo.setAddFlag(false); // 设置不可以添加到设计对象下
					childCustTreeNode.setBomInfo(chidBomInfo);
					treeTools.expandCurrNode(tv, childCustTreeNode, true); // 展示当前节点的所有路径				
					tv.refresh(childCustTreeNode, true); // 刷新结点
					tv.setSelection(StructuredSelection.EMPTY); // 去除当前行被选中的效果
					tipLabel.setText("");
				}
			}
			remove(childCustTreeNode, custTreeNode, delete);
		}
	}

	/**
	 * 从数据模型中删除结点绑定的数据
	 * 
	 * @param topBomInfo
	 * @param current
	 */
	private void removeData(BOMInfo topBomInfo, BOMInfo current) {
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {
			return;
		}
		for (Iterator it = childBomInfos.iterator(); it.hasNext();) {
			BOMInfo childBomInfo = (BOMInfo) it.next();
			if (childBomInfo.getPropertiesInfo().getItem_ID().equals(current.getPropertiesInfo().getItem_ID())) { // 判断Item_ID是否一致, 相同就删除				
				it.remove();
			}
			removeData(childBomInfo, current);
		}
	}

	private class RecoveryEntryAction extends Action {
		public RecoveryEntryAction() {
			setText(reg.getString("rightRecoveryBtn.LABEL"));
		}

		public void run() {
			List<CustTreeNode> custTreeNodeList = getCustTreeNodes();
			for (CustTreeNode custTreeNode : custTreeNodeList) {
				if (null == custTreeNode) {
					continue;
				}
				CustTreeNode topCustTreeNode = (CustTreeNode) tv.getTree().getItems()[0].getData();
				if (topCustTreeNode.getBomInfo().getPropertiesInfo().getItem_ID()
						.equals(custTreeNode.getBomInfo().getPropertiesInfo().getItem_ID())) { // 判断Item_ID是否一致, 相同就删除
					BOMInfo topBomInfo = topCustTreeNode.getBomInfo();
					topBomInfo.setColor(topBomInfo.getBeforeModifyColor()); // 将背景颜色设置为修改成红色之前的颜色
					if (topBomInfo.getPartRevModify()) {
						topBomInfo.setModify(true); // 设置可以修改
					}					
					topBomInfo.setRemoveFlag(true);
					topBomInfo.setAddFlag(true); // 设置可以添加到设计对象下
					topBomInfo.setDeleteFlag(false); // 将删除标识设置为false
					custTreeNode.setBomInfo(topBomInfo); // 修改数据模型
					treeTools.expandCurrNode(tv, custTreeNode, true); // 展示当前节点的所有路径
					tv.refresh(custTreeNode, true); // 刷新结点
					tv.setSelection(StructuredSelection.EMPTY); // 去除当前行被选中的效果
					tipLabel.setText("");
				}
				recovery(topCustTreeNode, custTreeNode);
			}
			treeTools.setHasSaveFlag(true); // 对于将背景颜色恢复为不是红色，将是否保存的标识设置为true
		}
	}

	/**
	 * 恢复结点状态
	 * 
	 * @param topCustTreeNode
	 * @param custTreeNode
	 */
	private void recovery(CustTreeNode topCustTreeNode, CustTreeNode custTreeNode) {
		List<CustTreeNode> children = topCustTreeNode.getChildren();
		if (CommonTools.isEmpty(children)) {
			return;
		}
		for (CustTreeNode childCustTreeNode : children) {
			if (childCustTreeNode.getBomInfo().getPropertiesInfo().getItem_ID()
					.equals(custTreeNode.getBomInfo().getPropertiesInfo().getItem_ID())) {
				BOMInfo childBomInfo = childCustTreeNode.getBomInfo();
				childBomInfo.setColor(childBomInfo.getBeforeModifyColor()); // 将背景颜色设置为修改成红色之前的颜色
				if (childBomInfo.getPartRevModify()) { // 判断当前物料对象当前用户是否可以编辑
					childBomInfo.setModify(true); // 设置可以修改
				}				
				childBomInfo.setRemoveFlag(true);
				childBomInfo.setAddFlag(true); // 设置可以添加到设计对象下
				childBomInfo.setDeleteFlag(false); // 将删除标识设置为false
				childCustTreeNode.setBomInfo(childBomInfo); // 修改数据模型
				treeTools.expandCurrNode(tv, childCustTreeNode, true); // 展示当前节点的所有路径
				tv.refresh(childCustTreeNode, true); // 刷新结点
				tv.setSelection(StructuredSelection.EMPTY); // 去除当前行被选中的效果
				tipLabel.setText("");
			}
			recovery(childCustTreeNode, custTreeNode);
		}
	}

	/**
	 * 判断当前对象背景颜色是否为红色
	 * @param currentBomInfo
	 * @return
	 */
	private boolean checkRed(BOMInfo currentBomInfo) {
		if (CommonTools.isEmpty(currentBomInfo.getColor())) {
			return false;
		} else {
			return currentBomInfo.getColor().getRGB().red == ColorEnum.Red.color().getRGB().red
					&& currentBomInfo.getColor().getRGB().green == ColorEnum.Red.color().getRGB().green
					&& currentBomInfo.getColor().getRGB().blue == ColorEnum.Red.color().getRGB().blue;
		}		
	}
	
	
	// 为公用而自定义的方法, 取得当前选中的结点
	private List<CustTreeNode> getCustTreeNodes() {
		IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
		if (CommonTools.isEmpty(selection)) {
			return null;
		}
		if (selection.size() > 0) {
			return selection.toList();
		}
		return null;
	}
}
