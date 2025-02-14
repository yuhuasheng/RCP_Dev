package com.foxconn.sdebom.batcheditorebom.util;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import com.foxconn.sdebom.batcheditorebom.constants.ColorEnum;
import com.foxconn.sdebom.batcheditorebom.constants.SDEBOMConstant;
import com.foxconn.sdebom.batcheditorebom.custtree.CustTreeNode;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.Registry;

public class TreeTools {
	
	private static final String D9_PREFIX = "d9_";
	private static final String POC_STR = "IR_";
	private static final String BL_PREFIX = "bl";
	private Registry reg;	
	
	public TreeTools(Registry reg) {
		super();
		this.reg = reg;
	}


	public SDEBOMBean getSDEBOMStruct(TCComponentBOMLine topLine) throws TCException, IllegalArgumentException, IllegalAccessException {
//		topLine.refresh();
//		topLine.clearCache();
		System.out.println("==>> bl_indented_title: " + topLine.getProperty("bl_indented_title"));	
		
		SDEBOMBean rootBean = new SDEBOMBean(this, topLine);		
//		rootBean = TCUtil.tcPropMapping(rootBean, topLine.getItemRevision(), "");
//		rootBean.setItemRevUid(topLine.getItemRevision().getUid());
//		rootBean.setObjectType(topLine.getItemRevision().getTypeObject().getName());
		
		AIFComponentContext[] componmentContext = topLine.getChildren();
		if (CommonTools.isNotEmpty(componmentContext)) {
//			Stream.of(componmentContext).parallel().forEach(e -> {	
			Stream.of(componmentContext).forEach(e -> {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
				try {					
					if (!bomLine.isSubstitute()) {
						SDEBOMBean bomBean = getSDEBOMStruct(bomLine);	
						rootBean.addChild(bomBean);						
//						mergeBOMStruct(rootBean, bomBean);
						if (bomLine.hasSubstitutes()) {
							TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();	
							for (TCComponentBOMLine subBomline : listSubstitutes) {
//								TCComponentItemRevision subItemRev = subBomline.getItemRevision();
								SDEBOMBean subBean = getSDEBOMStruct(subBomline);
//								subBean.setFindNum(bomBean.getFindNum());
//								SDEBOMBean subBean = (SDEBOMBean) bomBean.clone();
//								subBean.setItemId(subItemRev.getProperty("item_id"));
//								subBean.setVersion(subItemRev.getProperty("item_revision_id"));
//								subBean.setTitle(subBomline.getProperty("bl_indented_title"));
//								subBean.setItemRevUid(subItemRev.getUid());
//								subBean.setObjectType(subItemRev.getTypeObject().getName());								
//								subBean.setImage(new Image(null, TreeTools.class.getResourceAsStream(IconsEnum.ReplacePartRevIcons.relativePath())));
//								mergeBOMStruct(rootBean, subBean);	
								rootBean.addChild(subBean);
							}
						}
					}					
				} catch (Exception e1) {
					try {
						System.out.println("==>> bl_item_item_id: " + bomLine.getProperty("bl_item_item_id"));
					} catch (TCException e2) {
						e2.printStackTrace();
					}
					throw new RuntimeException(e1);
				}
			});
			
//			if (rootBean.getChilds().size() > 0) {
//				rootBean.getChilds().sort(Comparator.comparing(SDEBOMBean::getFindNum));
//			}
			
			
		}
		return rootBean;
	}

	
	private void mergeBOMStruct(SDEBOMBean current, SDEBOMBean next) {
		List<SDEBOMBean> childs = current.getChilds();
		boolean flag = false;
		if (CommonTools.isNotEmpty(childs)) {
			for (SDEBOMBean childBean : childs) {
				if (CommonTools.isEmpty(childBean)) {
					continue; 
				}
				
				if (childBean.getItemRevUid().equals(next.getItemRevUid())) {
					childBean.addQty(next.getQty()); // 对于相同的对象，记性归纳，数量合并
					childBean.setTitle(childBean.getTitle() + " x " + childBean.getQty());
					flag = true;
					break;
				}
			}
		}
		
		if (flag) {
			return;
		} else {
			current.addChild(next);
		}
	}
		
	public SDEBOMBean getTCProp(SDEBOMBean bean, TCComponentBOMLine tcBomLine) throws TCException {
		if (bean == null) {
			bean = new SDEBOMBean();
		}
		if (tcBomLine != null) {
			TCComponentItemRevision itemRev = tcBomLine.getItemRevision();
			itemRev.refresh();
			itemRev.clearCache();
//			String findNum = tcBomLine.getProperty("bl_sequence_no");
//			if (CommonTools.isNotEmpty(findNum)) {
//				bean.setFindNum(Integer.valueOf(findNum));
//			}			
			String title = tcBomLine.getProperty("bl_indented_title");
			bean.setTitle(title);
			
			String customerPN = itemRev.getProperty("d9_CustomerPN");
			bean.setCustomerPN(customerPN);
			
			String customerPNDesc = itemRev.getProperty("d9_EnglishDescription");
			bean.setCustomerPNDesc(customerPNDesc);
			
			String assemblyStatus = tcBomLine.getProperty("fnd0bl_is_mono_override");
			bean.setAssemblyStatus(assemblyStatus);
			
			String qty = tcBomLine.getProperty("bl_quantity");
			if (CommonTools.isEmpty(qty)) {
				bean.setQty(1);
			} else {
				bean.setQty(Integer.valueOf(qty));
			}
			
			bean.setItemRev(itemRev);
			bean.setItemRevUid(itemRev.getUid());
			bean.setObjectType(itemRev.getTypeObject().getName());
			bean.setBomLine(tcBomLine);	
			
			if (tcBomLine.isSubstitute()) {
				bean.setSub(true);
			}		 
			
			if (anyMatch(bean.getObjectType())) {				
				bean.setColor(ColorEnum.Gray.color());
				bean.setCanModify(false);
			}
			
		}
		return bean;		
	}
	
	
	public SDEBOMBean tcPropMapping(SDEBOMBean bean, TCComponentBOMLine tcBomLine, boolean isSub)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcBomLine != null) {
			TCComponentItemRevision itemRev = tcBomLine.getItemRevision();
			itemRev.refresh();
			itemRev.clearCache();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					Object val = "";
					String propertyName = tcPropName.tcProperty();
					String tctype = tcPropName.tcType();
					if (propertyName.startsWith(BL_PREFIX)) {
						val = tcBomLine.getProperty(propertyName);
					} else {
						int index = propertyName.indexOf(D9_PREFIX);
						if (index != -1) {
							String pocPropertyName = propertyName.substring(0, index + D9_PREFIX.length()) + POC_STR
									+ propertyName.substring(index + D9_PREFIX.length());
							if (itemRev.isValidPropertyName(pocPropertyName)) {
								propertyName = pocPropertyName;
							}
							
							if (itemRev.isValidPropertyName(propertyName)) {
								val = itemRev.getProperty(propertyName);
							} else {
								System.out.println("==>> itemId: " + itemRev.getProperty("item_id"));
								System.out.println("propertyName is not exist " + propertyName);
							}
						} else {
							val = itemRev.getProperty(propertyName);
						}
					}
					
					if (fields[i].getType() == Integer.class) {
						if (val.equals("") || val == null) {
							val = null;
						} else {
							val = Integer.parseInt((String) val);
						}
					}
					
					fields[i].set(bean, val);
				}
			}
			
			bean.setItemRev(itemRev);
			bean.setItemRevUid(itemRev.getUid());
			bean.setObjectType(itemRev.getTypeObject().getName());
			bean.setBomLine(tcBomLine);			
			if (CommonTools.isEmpty(bean.getQty())) {
				bean.setQty(1);
			}
			
			if (isSub) {
				bean.setSub(true);
			} 
			
			if (anyMatch(bean.getObjectType())) {				
				bean.setColor(ColorEnum.Gray.color());
				bean.setCanModify(false);
			}			
			
		}
		return bean;
	}
 
	
	
	/**
	 * 重新设置各列宽，让其文本可以完全显示
	 */
	public void resetTreeColumn(TreeViewer treeViewer) {
		treeViewer.getControl().getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				TreeColumn[] treeColumns = treeViewer.getTree().getColumns();
				for (TreeColumn treeColumn : treeColumns) {
					if (treeColumn.getWidth() == 0) {
						continue;
					}
					treeColumn.pack();
				}
			}
		});
	}
	
	
	public void expandAllTreeNode(TreeViewer treeViewer, CustTreeNode topTreeNode) {
		List<CustTreeNode> children = topTreeNode.getChildren();
		if (CommonTools.isEmpty(children)) {
			return;
		}
		if (!treeViewer.getExpandedState(topTreeNode) && CommonTools.isNotEmpty(children)) {
			treeViewer.expandToLevel(topTreeNode, 1);
		} 
				
		for (CustTreeNode custTreeNode : children) {
			expandAllTreeNode(treeViewer, custTreeNode);
			resetTreeColumn(treeViewer);
		}
	}
	
	
	
	/**
	 * 向下展开指定节点的所有下阶
	 * @param treeViewer
	 * @param currentTreeNode
	 */
	public void expandDownTreeNode(TreeViewer treeViewer, CustTreeNode current) {
		List<CustTreeNode> children = current.getChildren();
		if (CommonTools.isEmpty(children)) {
			return;
		}
		if (!treeViewer.getExpandedState(current) && CommonTools.isNotEmpty(children)) {
			treeViewer.expandToLevel(current, 1);
//			treeViewer.refresh(currentTreeNode, true);
			resetTreeColumn(treeViewer);
		} 
		
		children.stream().forEach(custTreeNode -> {
			expandDownTreeNode(treeViewer, custTreeNode);
		});
		
//		for (CustTreeNode custTreeNode : children) {
//			expandDownTreeNode(treeViewer, custTreeNode);
//		}
	}
	
	
	public void collapseBottomTreeNode(TreeViewer treeViewer, CustTreeNode current, boolean collapseStart) {
		List<CustTreeNode> children = current.getChildren();
		if (CommonTools.isNotEmpty(children)) {
			for (CustTreeNode custTreeNode : children) {
				collapseBottomTreeNode(treeViewer, custTreeNode, !collapseStart);
			}
		}
		
		CustTreeNode parentTreeNode = current.getParentTreeNode();		
		if (CommonTools.isEmpty(parentTreeNode)) {
			return;
		}
		
		if (collapseStart) {
			return;
		}

		if (treeViewer.getExpandedState(parentTreeNode)) {
			treeViewer.collapseToLevel(parentTreeNode, -1);
			resetTreeColumn(treeViewer);
		}
	}
	
	/**
	 * 校验子结点是否存在
	 * @param parentTreeNode
	 * @param childTreeNode
	 * @return
	 */
	private boolean checkTreeNodeExist(CustTreeNode parentTreeNode, CustTreeNode childTreeNode) {
		List<CustTreeNode> children = parentTreeNode.getChildren();
		if (CommonTools.isEmpty(children)) {
			return false;
		}
		return children.stream().anyMatch(e -> e.getData().getItemId().equals(childTreeNode.getData().getItemId()));
	}
	
	
	/**
	 * 锁住是否输出栏位
	 * @param custTreeNode
	 */

	public void updateExportModifyFlag(SDEBOMBean topData, boolean modify) {			
		List<SDEBOMBean> childs = topData.getChilds();			
		if (CommonTools.isEmpty(childs)) {
			return;
		}		
		
		for (SDEBOMBean childBean : childs) {
			if (!anyMatch(childBean.getObjectType())) { // 节点类型不是虚拟阶才设置编辑标识
				childBean.setCanModify(modify);	
			}					
			updateExportModifyFlag(childBean, modify);
		}
	}
	
	/**
	 * 重新是否输出列是否可以编辑标识
	 * @param topData
	 * @param modify
	 */
	public void resetExportModifyFlag(SDEBOMBean topData, boolean modify) {
		List<SDEBOMBean> childs = topData.getChilds();
		if (CommonTools.isEmpty(childs)) {
			return;
		}
		
		
		if (SDEBOMConstant.EXPORTLIST.get(SDEBOMConstant.EXPORTLIST.size() - 1).equals(topData.getAssemblyStatus())) {
			modify = false;
		}
		
		for (SDEBOMBean childBean : childs) {
			if (!anyMatch(childBean.getObjectType())) { // 节点类型不是虚拟阶才重新设置是否可以编辑标识
				childBean.setCanModify(modify);
			}			
			resetExportModifyFlag(childBean, modify);			
		}
	}
	
	/**
	 * 校验父结点的状态
	 * @param currentTreeNode
	 * @param reg
	 */
	public void checkParentTreeNodeStatus(CustTreeNode currentTreeNode) {
		CustTreeNode parentTreeNode = currentTreeNode.getParentTreeNode();
		if (CommonTools.isEmpty(parentTreeNode)) {
			return;
		}
		
		SDEBOMBean parentData = parentTreeNode.getData();
		if (SDEBOMConstant.EXPORTLIST.get(SDEBOMConstant.EXPORTLIST.size() - 1).equals(parentData.getAssemblyStatus()) 
				&& !anyMatch(parentData.getObjectType())) { // 父结点输出列值为N，并且不是虚拟阶 
			String[] split = reg.getString("SetAssemblyStatusWarn.MSG").split("&&");
			TCUtil.warningMsgBox(split[0] + parentData.getTitle() + split[1] + parentData.getAssemblyStatus() + split[2], reg.getString("WARNING.MSG"));
			return;
		}
		
		checkParentTreeNodeStatus(parentTreeNode);
	}
	
	
 	public void updateTreeNodeData(SDEBOMBean bean, int index, String str) {
		switch (index) {
		case 4:			
			bean.setAssemblyStatus(str);
			break;

		default:
			return;
		}
	}
	
	
 	/**
 	 * 匹配类型(判断类型是否为虚拟阶)
 	 * @param bean
 	 * @return
 	 */
 	public boolean anyMatch(String objectType) {
 		return SDEBOMConstant.VIRTUALLIST.stream().anyMatch((e) -> e.equals(objectType));
 	} 	
 	
}
