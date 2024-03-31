package com.foxconn.sdebom.batcheditorebom.custtree;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class LazyCustTreeContentProvider implements ITreeContentProvider {

	ExecutorService es = Executors.newFixedThreadPool(1);
	
	// 当界面中单击某个结点时, 由此方法决定被单击结点应显示哪些子结点
	// parentElement就是被单击的结点对象, 返回的数组就是应显示的子结点
	@Override
	public Object[] getChildren(Object parentElement) {
		LazyCustTreeNode treeNode = (LazyCustTreeNode) parentElement;
//		Future<List<LazyCustTreeNode>> future = es.submit(() -> {
//			List<LazyCustTreeNode> list = treeNode.getChildren();			
//			return list;
//		});
		
		List<LazyCustTreeNode> childrens = treeNode.getChildren();
//		try {			
//			childrens = future.get(5, TimeUnit.MINUTES); // 任务时间超时时间单位设置为分钟，若五分钟没有返回结果则设置为超时
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
		// 虽然通过界面单击方式, 有子结点才会执行到此方法, 但任然要做非空判断, 因为在调用TreeViewer的某些方法时其内部会附带调用此方法
		if (CommonTools.isEmpty(childrens)) {
			return new Object[0];			
		}
		return childrens.toArray();
	}

	// 由此方法决定树的"第一级"结点显示哪些对象, inputElement是用tv.setInput方法输入的那个对象
	@Override
	public Object[] getElements(Object inputElement) {	
		TCComponentBOMLine topLine = null;
		if (inputElement instanceof TCComponentBOMLine) {
			topLine = (TCComponentBOMLine) inputElement;
		}
//		if (topLine.isPacked()) {
//			try {
//				topLine.unpack();
//			} catch (TCException e) {
//				e.printStackTrace();
//			}
//		}
		return new LazyCustTreeNode(topLine).getList().toArray();
	}

	@Override
	public Object getParent(Object arg0) {
		return null;
	}

	// 判断参数element结点是否含有子结点
	// 返回true表示element有子结点, 则其前面会显示有"+"号图标
	@Override
	public boolean hasChildren(Object element) {
		LazyCustTreeNode treeNode = (LazyCustTreeNode) element;
		return treeNode.hasChildren();
	}

}
