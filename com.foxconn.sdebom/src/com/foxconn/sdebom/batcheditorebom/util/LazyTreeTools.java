package com.foxconn.sdebom.batcheditorebom.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import com.foxconn.sdebom.batcheditorebom.constants.ColorEnum;
import com.foxconn.sdebom.batcheditorebom.constants.LovEnum;
import com.foxconn.sdebom.batcheditorebom.constants.SDEBOMConstant;
import com.foxconn.sdebom.batcheditorebom.custtree.LazyCustTreeNode;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
//import com.teamcenter.services.strong.core.DataManagementService;

public class LazyTreeTools {

	static ExecutorService es = Executors.newFixedThreadPool(10);
	
	public static SDEBOMBean tcPropMapping(SDEBOMBean bean, TCComponentBOMLine tcBomLine) throws TCException, IllegalArgumentException, IllegalAccessException {
		if (bean != null && tcBomLine != null) {
			TCComponentItemRevision itemRev = tcBomLine.getItemRevision();
			itemRev.refresh();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					Object val = "";
					String tcAttrName = tcPropName.tcProperty();
					String tcType = tcPropName.tcType();
					if (!tcAttrName.isEmpty()) {
						if ("BOMLine".equals(tcType)) {
							val = tcBomLine.getProperty(tcAttrName).trim();
						} else {
							val = itemRev.getProperty(tcAttrName);
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
			}
			
			bean.setItemRev(itemRev);
			bean.setItemRevUid(itemRev.getUid());
			bean.setObjectType(itemRev.getTypeObject().getName());
			bean.setBomLine(tcBomLine);			
			if (CommonTools.isEmpty(bean.getQty())) {
				bean.setQty(1);
			}
			
			if (tcBomLine.isSubstitute()) {
				bean.setSub(true);
			}
			
			if (anyMatch(bean.getObjectType())) {				
				bean.setColor(ColorEnum.Gray.color());
				bean.setCanModify(false);
			}
		}
		
		LovEnum lovEnum = getAssemblyStatusEnum(bean.getAssemblyStatus(), true);
		if (CommonTools.isNotEmpty(lovEnum)) {
			bean.setAssemblyStatus(lovEnum.showValue());	
		} else {
			if ("2".equals(bean.getAssemblyStatus())) {
				bean.setAssemblyStatus(LovEnum.HidenLov.showValue());				
			}
		}				
		return bean;
	}
	
	
	/**
	 * 返回零组件装配状态枚举
	 * @param str
	 * @param showFlag true代表获取数据加载时获取条件，false代表数据保存时获取条件
	 * @return
	 */
	public static LovEnum getAssemblyStatusEnum(String str, boolean showFlag) {
		List<LovEnum> list = Stream.of(LovEnum.values()).collect(Collectors.toList());
		Optional<LovEnum> findAny = list.stream().filter(e -> {
			if (showFlag) {
				return e.actualValue().equals(str);
			} else {
				return e.showValue().equals(str);
			}
		}).findAny();
		
		if (findAny.isPresent()) {
			return findAny.get();
		}
		return null;
	}
	
	public static String setActualValue(String str) {
		LovEnum lovEnum = LazyTreeTools.getAssemblyStatusEnum(str, false);
		if (CommonTools.isNotEmpty(lovEnum)) {
			return lovEnum.actualValue();
		}
		return null;
	}
	

	public static SDEBOMBean getTCProp(SDEBOMBean bean, TCComponentBOMLine tcBomLine) throws TCException {
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
		
	
	public static void updateTreeNodeData(SDEBOMBean bean, int index, String str) {
		switch (index) {
		case 4:
			bean.setAssemblyStatus(str);
			bean.setHasModify(true); // 设置此节点发生修改
			break;

		default:
			return;
		}
	}
	
	
	public static void updateExportModifyFlag(SDEBOMBean topData, boolean modify) {
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
	
	
	public static void customExpandAll(TreeViewer treeViewer, LazyCustTreeNode topTreeNode, final Executor executor) {
		List<LazyCustTreeNode> children = topTreeNode.getChildren();
		if (CommonTools.isEmpty(children)) {
			return;
		}
		
		if (!treeViewer.getExpandedState(topTreeNode) && CommonTools.isNotEmpty(children)) {
			executor.execute(new Runnable() {
				
				@Override
				public void run() {	
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							treeViewer.expandToLevel(topTreeNode, 1);
						}
					});
				}
			});
			
//			executor.execute(() -> {
//				Display.getDefault().syncExec(new Runnable() {
//
//					@Override
//					public void run() {
//						treeViewer.expandToLevel(topTreeNode, 1);
////						resetTreeColumn(treeViewer);
//					}
//				});
//			});
//			Display.getDefault().syncExec(new Runnable() {
//
//				@Override
//				public void run() {
//					treeViewer.expandToLevel(topTreeNode, 1);
////					resetTreeColumn(treeViewer);
//				}
//			});
			
//			es.submit(() -> {
//				treeViewer.expandToLevel(topTreeNode, 1);
//				resetTreeColumn(treeViewer);
//			});	
				
				
//			treeViewer.expandToLevel(topTreeNode, 1);
//			resetTreeColumn(treeViewer);
				
						
		} 
		
		for (LazyCustTreeNode custTreeNode : children) {
			customExpandAll(treeViewer, custTreeNode, executor);			
		}
	}
	
	/**
	 * 向下展开指定节点的所有下阶
	 * @param treeViewer
	 * @param current
	 */
	public static void expandDownTreeNode(TreeViewer tv, LazyCustTreeNode current, final Executor executor) {
		List<LazyCustTreeNode> children = current.getChildren();
		if (CommonTools.isEmpty(children)) {
			return;
		}
		
		if (!tv.getExpandedState(current) && CommonTools.isNotEmpty(children)) {
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							tv.expandToLevel(current, 1);
						}
					});
				}
			});			
//			resetTreeColumn(tv);
		}
		
		for (LazyCustTreeNode custTreeNode : children) {
			expandDownTreeNode(tv, custTreeNode, executor);
		}
	}
	
	/**
	 * 收缩指定节点下面的所有子节点
	 * @param tv
	 * @param current
	 * @param collapseStart
	 */
	public static void collapseBottomTreeNode(TreeViewer tv, LazyCustTreeNode current, boolean collapseStart) {
		List<LazyCustTreeNode> childs = current.getChilds();
		if (CommonTools.isNotEmpty(childs)) {
			for (LazyCustTreeNode custTreeNode : childs) {
				collapseBottomTreeNode(tv, custTreeNode, !collapseStart);
			}
		}
		
		LazyCustTreeNode parentTreeNode = current.getParentTreeNode();		
		if (CommonTools.isEmpty(parentTreeNode)) {
			return;
		}
		
		if (collapseStart) {
			return;
		}
		
		if (tv.getExpandedState(parentTreeNode)) {
			tv.collapseToLevel(parentTreeNode, -1);
//			resetTreeColumn(tv);
		}
		
	}
	
	
	/**
	 * 重新设置各列宽，让其文本可以完全显示
	 */
	public static void resetTreeColumn(TreeViewer treeViewer) {
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
	
	
	/**
 	 * 匹配类型(判断类型是否为虚拟阶)
 	 * @param bean
 	 * @return
 	 */

 	public static boolean anyMatch(String objectType) {
 		return SDEBOMConstant.VIRTUALLIST.stream().anyMatch((e) -> e.equals(objectType));
 	}
 	
 	
 	public static void loadAllProperties(TCComponentBOMLine bomLine, String[] partAtrrs, String[] bomAttrs) throws TCException, InterruptedException {
// 		TCSession session = TCUtil.getTCSession();
// 		DataManagementService dmService = DataManagementService.getService(session.getSoaConnection());
// 		Set<TCComponentBOMLine> boms = Collections.synchronizedSet(new HashSet<TCComponentBOMLine>());
// 		Set<TCComponentItemRevision> parts = Collections.synchronizedSet(new HashSet<TCComponentItemRevision>());
// 		getSinglePartBom(bomLine, boms, parts);
// 		Thread bomThread = new Thread(() -> {
//            dmService.getProperties(boms.toArray(new TCComponentBOMLine[0]), bomAttrs);
//        });
//        Thread partThread = new Thread(() -> {
//            dmService.getProperties(parts.toArray(new TCComponentItemRevision[0]), partAtrrs);
//        });
//        bomThread.start();
//        partThread.start();
//        bomThread.join();
//        partThread.join();
 	}
 	
 	/**
 	 * 获取单阶BOM和物料对象
 	 * @param bomLine
 	 * @param boms
 	 * @param parts
 	 * @throws TCException
 	 */
 	public static void getSinglePartBom(TCComponentBOMLine bomLine, Set<TCComponentBOMLine> boms, Set<TCComponentItemRevision> parts) throws TCException {
 		boms.add(bomLine);
 		if (bomLine.getItemRevision() != null) {
 			parts.add(bomLine.getItemRevision());
		}
 		
 		if (bomLine.hasChildren()) {
 			AIFComponentContext[] componmentContext = bomLine.getPreviousChildren();
 			if (componmentContext == null)
            {
                componmentContext = bomLine.getChildren();
            }
 			
 			Stream.of(componmentContext).forEach(e -> {
 				TCComponentBOMLine cBOMLine = (TCComponentBOMLine) e.getComponent();
 				if (!cBOMLine.isSubstitute()) {
 					try {
 						boms.add(cBOMLine);
 	 			 		if (cBOMLine.getItemRevision() != null) {
 	 			 			parts.add(cBOMLine.getItemRevision());
 	 					}
 	 			 		
 	 			 		if (cBOMLine.hasSubstitutes()) {
 	 			 			TCComponentBOMLine[] listSubstitutes = cBOMLine.listSubstitutes();
 	 			 			
 	 			 			Stream.of(listSubstitutes).forEach(sub -> {
 	 			 				try {
 	 			 					TCComponentBOMLine subBomLine = sub; 	 	 			 				
 	 	 			 				boms.add(subBomLine);
 	 	 	 	 			 		if (subBomLine.getItemRevision() != null) {
 	 	 	 	 			 			parts.add(subBomLine.getItemRevision());
 	 	 	 	 					}
								} catch (Exception e2) {
									e2.printStackTrace();
								} 	 			 				
 	 			 			});
						}
 	 			 		
					} catch (Exception e1) {
						e1.printStackTrace();
					} 					
 				}
 			});
 		}
 	}
 	
 	
}
