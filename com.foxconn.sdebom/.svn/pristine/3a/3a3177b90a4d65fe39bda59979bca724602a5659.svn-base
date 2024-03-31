package com.foxconn.sdebom.batcheditorebom.custtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Display;

import com.foxconn.sdebom.batcheditorebom.constants.SDEBOMConstant;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.sdebom.batcheditorebom.util.LazyTreeTools;
import com.foxconn.sdebom.batcheditorebom.util.TreeTools;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class LazyCustTreeNode implements Comparable<LazyCustTreeNode> {
	
	private TCComponentBOMLine bomLine;

	private SDEBOMBean data;
	private Integer findNum;

//	private List<LazyCustTreeNode> list = new ArrayList<LazyCustTreeNode>();
	private List<LazyCustTreeNode> childs = new CopyOnWriteArrayList<LazyCustTreeNode>();

	private List<LazyCustTreeNode> subList = null;
	
	private LazyCustTreeNode parentTreeNode;

//	private boolean modify = true;
	
	public LazyCustTreeNode(TCComponentBOMLine bomLine) {
		try {
			this.bomLine = bomLine;						
//			data = LazyTreeTools.getTCProp(null, bomLine);
			data = LazyTreeTools.tcPropMapping(new SDEBOMBean(), bomLine);
//			findNum = data.getFindNum();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasChildren() {
		return bomLine.hasChildren();
	}

	public List<LazyCustTreeNode> getChildren() {
		if (CommonTools.isNotEmpty(childs)) {
			return childs;
		}
		
		try {
			AIFComponentContext[] aifs = null;
			if (bomLine.hasChildren()) {
				aifs = bomLine.getPreviousChildren();
				if (aifs == null) {
					aifs = bomLine.getChildren();
				}
			}
			
			if (CommonTools.isEmpty(aifs)) {
				return new ArrayList<LazyCustTreeNode>();
			}			
			
//			unpackBOMLine(aifs); // 解包BOMLine
			
			aifs = bomLine.getChildren(); // 重新获取子BOMLine
			LazyTreeTools.loadAllProperties(bomLine, SDEBOMConstant.ALL_PART_ATTRS, SDEBOMConstant.ALL_BOM_ATTRS); // 加载属性，后续获取属性值直接可以从缓存中获取，减少和DB的交合，提高性能
			Stream.of(aifs).forEach(aif -> {
				TCComponentBOMLine com = (TCComponentBOMLine) aif.getComponent();				
				try {
//					com.unpack(); // BOMLine解包
					if (itemUidAnyMatch(com.getItemRevision().getUid())) {
						return;
					}

					if (!com.isSubstitute()) {
						LazyCustTreeNode childTreeNode = new LazyCustTreeNode(com);
						childTreeNode.setParentTreeNode(this);						
						childs.add(childTreeNode);
//						addChild(childTreeNode);
						if (com.hasSubstitutes()) {
							TCComponentBOMLine[] listSubstitutes = com.listSubstitutes();
							List<LazyCustTreeNode> subList = new ArrayList<LazyCustTreeNode>();
							Stream.of(listSubstitutes).forEach(e -> {
								LazyCustTreeNode subTreeNode = new LazyCustTreeNode(e);
								subTreeNode.setParentTreeNode(this);
//								subTreeNode.getData().setFindNum(childTreeNode.getData().getFindNum());
								childs.add(subTreeNode);
//								addChild(subTreeNode);
							});			
							
						}
					}

				} catch (TCException e) {
					e.printStackTrace();
				}
			});
			
//			if (CommonTools.isNotEmpty(childs)) {
//				Collections.sort(childs);
//			}

//			setChilds(getTotalChilds());
			
			if (!LazyTreeTools.anyMatch(data.getObjectType())) {
				resetExportModify(data.getCanModify());	
			}
			
		} catch (Exception e) {
			e.printStackTrace();			
			return null;
		}
		return childs;
	}
	
	
	private void addChild(LazyCustTreeNode nextTreeNode) {
		boolean flag = false;
		SDEBOMBean nextBean = nextTreeNode.getData();
		for (LazyCustTreeNode child : childs) {
			if (CommonTools.isEmpty(child)) {
				continue; 
			}
			SDEBOMBean childBean = child.getData();
			if (CommonTools.isEmpty(childBean)) {
				continue; 
			}
			if (childBean.getItemRevUid().equals(nextBean.getItemRevUid())) {
				childBean.addQty(nextBean.getQty()); // 对于相同的对象，记性归纳，数量合并
				childBean.setTitle(childBean.getTitle() + " x " + childBean.getQty());
				flag = true;
				break;
			}
		}
		
		if (flag) {
			return;
		} else {
			childs.add(nextTreeNode);
		}
	}
	
	
	/**
	 * 解包BOMLine
	 * @throws TCException
	 */
	private void unpackBOMLine(AIFComponentContext[] aifs) throws TCException {
		if (CommonTools.isNotEmpty(aifs)) {
			for (AIFComponentContext aif : aifs) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) aif.getComponent();
				if(bomLine.isPacked()) {
					bomLine.unpack();
				}
			}
		}
	}
	
	
	private List<LazyCustTreeNode> getTotalChilds() {
		List<LazyCustTreeNode> totalChildList = new ArrayList<LazyCustTreeNode>();
		for (LazyCustTreeNode childTreeNode : childs) {
			totalChildList.add(childTreeNode);
			List<LazyCustTreeNode> subTreeNodes = childTreeNode.getSubList();
			if (CommonTools.isNotEmpty(subTreeNodes)) {
				totalChildList.addAll(subTreeNodes);
			}
		}

		return totalChildList;
	}

	private void resetExportModify(boolean modify) {
		if (CommonTools.isEmpty(childs)) {
			return;
		}
		
		if (SDEBOMConstant.EXPORTLIST.get(SDEBOMConstant.EXPORTLIST.size() - 1).equals(data.getAssemblyStatus())) {
			modify = false;
		}		
		
		for (LazyCustTreeNode childTreeNode : childs) {
			SDEBOMBean childBean = childTreeNode.getData();
			if (!LazyTreeTools.anyMatch(childBean.getObjectType())) { // 节点类型不是虚拟阶才重新设置是否可以编辑标识
				childBean.setCanModify(modify);
			}
		}
	}
	
	
	
	/**
	 * 判断节点是否已经存在
	 * 
	 * @param uid
	 * @return
	 */
	private boolean itemUidAnyMatch(String uid) {
		return childs.stream().anyMatch(e -> e.getData().getItemRevUid().equals(uid));
	}

	public List<LazyCustTreeNode> getList() {
		List<LazyCustTreeNode> tempList = new ArrayList<LazyCustTreeNode>();
		tempList.add(new LazyCustTreeNode(bomLine));
		return tempList;
	}

	public List<LazyCustTreeNode> getChilds() {
		return childs;
	}

	public void setChilds(List<LazyCustTreeNode> childs) {
		this.childs = childs;
	}

	public TCComponentBOMLine getBomLine() {
		return bomLine;
	}

	public void setBomLine(TCComponentBOMLine bomLine) {
		this.bomLine = bomLine;
	}

	public SDEBOMBean getData() {
		return data;
	}

	public void setData(SDEBOMBean data) {
		this.data = data;
	}

	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}

	public List<LazyCustTreeNode> getSubList() {
		return subList;
	}

	public void setSubList(List<LazyCustTreeNode> subList) {
		this.subList = subList;
	}

	public LazyCustTreeNode getParentTreeNode() {
		return parentTreeNode;
	}

	public void setParentTreeNode(LazyCustTreeNode parentTreeNode) {
		this.parentTreeNode = parentTreeNode;
	}

	@Override
	public int compareTo(LazyCustTreeNode treeNode) {
//		int i = this.data.getFindNum().compareTo(treeNode.getData().getFindNum());
		return 0;
	}

	
}
