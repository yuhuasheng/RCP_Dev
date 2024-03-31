package com.foxconn.mechanism.hhpnmaterialapply;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

public class MyContainerCheckedTreeViewer extends ContainerCheckedTreeViewer {

	/**
	 * Constructor for ContainerCheckedTreeViewer.
	 * 
	 * @see CheckboxTreeViewer#CheckboxTreeViewer(Composite)
	 */
	public MyContainerCheckedTreeViewer(Composite parent) {
		super(parent);
		initViewer();
	}

	/**
	 * Constructor for ContainerCheckedTreeViewer.
	 * 
	 * @see CheckboxTreeViewer#CheckboxTreeViewer(Composite,int)
	 */
	public MyContainerCheckedTreeViewer(Composite parent, int style) {
		super(parent, style);
		initViewer();
	}

	/**
	 * Constructor for ContainerCheckedTreeViewer.
	 * 
	 * @see CheckboxTreeViewer#CheckboxTreeViewer(Tree)
	 */
	public MyContainerCheckedTreeViewer(Tree tree) {
		super(tree);
		initViewer();
	}

	private void initViewer() {
		setUseHashlookup(true);
		addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				doCheckStateChanged(event.getElement());
			}
		});
		addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
			}

			public void treeExpanded(TreeExpansionEvent event) {
				Widget item = findItem(event.getElement());
				if (item instanceof TreeItem) {
					initializeItem((TreeItem) item);
				}
			}
		});
	}

	protected void doCheckStateChanged(Object element) {
		Widget item = findItem(element);
		if (item instanceof TreeItem) {
			TreeItem treeItem = (TreeItem) item;
			treeItem.setGrayed(false);
			boolean state = treeItem.getChecked();
			updateChildrenItems(treeItem, state);
			setSubtreeChecked(element, state); // 设置视图中给定项以及它可见孩子的未选中状态
//			updateParentItems(treeItem.getParentItem());
		}
	}

	/**
	 * 返回当前勾选行的树对象
	 * @param element
	 * @return
	 */
	public TreeItem getCheckTreeItem(Object element) {
		Widget item = findItem(element);
		TreeItem treeItem = null;
		if (item instanceof TreeItem) {
			treeItem = (TreeItem) item;			
		}
		return treeItem;
	}
	
	/**
	 * The item has expanded. Updates the checked state of its children.
	 */
	private void initializeItem(TreeItem item) {
		if (item.getChecked() && !item.getGrayed()) {
//			updateChildrenItems(item);
			boolean state = item.getChecked();
			updateChildrenItemsNew(item, state);
		}
	}

	/**
	 * Updates the check state of all created children
	 */
	private void updateChildrenItems(TreeItem parent, boolean state) {
		Item[] children = getChildren(parent);
//		boolean state = parent.getChecked();
		for (int i = 0; i < children.length; i++) {
			TreeItem curr = (TreeItem) children[i];
			if (curr.getData() != null && ((curr.getChecked() != state) || curr.getGrayed())) {
				curr.setChecked(!state);
//				curr.setGrayed(false);
				updateChildrenItems(curr, state);
			}
		}
	}

	private void updateChildrenItemsNew(TreeItem parent, boolean state) {
		Item[] children = getChildren(parent);
//		boolean state = parent.getChecked();
		for (int i = 0; i < children.length; i++) {
			TreeItem curr = (TreeItem) children[i];
			if (curr.getData() != null && (curr.getChecked() == state)) {
				curr.setChecked(!state);
//				curr.setGrayed(false);
				updateChildrenItemsNew(curr, state);
			}
		}
	}

	public boolean setSubtreeChecked(Object element, boolean state) {
		Widget widget = internalExpand(element, false);
		if (widget instanceof TreeItem) {
			TreeItem item = (TreeItem) widget;
			item.setChecked(state);
			if (state) {
				setCheckedChildren(item, !state); // 剔除子item选中项
			} else {
//					setCheckedChildren(item, state); // 剔除子item选中项
			}
			return true;
		} else {
			return false;
		}
	}

	protected void setCheckedChildren(Item item, boolean state) {
		createChildren(item);
		Item items[] = getChildren(item);
		if (items != null) {
			Item aitem[];
			int j = (aitem = items).length;
			for (int i = 0; i < j; i++) {
				Item child = aitem[i];
				if (child.getData() != null && (child instanceof TreeItem)) {
					TreeItem treeItem = (TreeItem) child;
					if (treeItem.getChecked()) { // 如果本身就是选中状态，则无需设置
						continue;
					}
					treeItem.setChecked(state);
					setCheckedChildren(((Item) (treeItem)), state);
				}
			}

		}
	}

	/**
	 * Updates the check / gray state of all parent items
	 */
	private void updateParentItems(TreeItem item) {
		if (item != null) {
			Item[] children = getChildren(item);
			boolean containsChecked = false;
			boolean containsUnchecked = false;
			for (int i = 0; i < children.length; i++) {
				TreeItem curr = (TreeItem) children[i];
				containsChecked |= curr.getChecked();
				containsUnchecked |= (!curr.getChecked() || curr.getGrayed());
			}
			item.setChecked(containsChecked);
//			item.setGrayed(containsChecked && containsUnchecked);
			updateParentItems(item.getParentItem());
		}
	}

	public boolean setChecked(Object element, boolean state) {
		if (super.setChecked(element, state)) {
			doCheckStateChanged(element);
			return true;
		}
		return false;
	}

	public void setCheckedElements(Object[] elements) {
		super.setCheckedElements(elements);
		for (int i = 0; i < elements.length; i++) {
			doCheckStateChanged(elements[i]);
		}
	}

	protected void setExpanded(Item item, boolean expand) {
		super.setExpanded(item, expand);
		if (expand && item instanceof TreeItem) {
			initializeItem((TreeItem) item);
		}
	}

	public Object[] getCheckedElements() {
		Object[] checked = super.getCheckedElements();
		// add all items that are children of a checked node but not created yet
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < checked.length; i++) {
			Object curr = checked[i];
			result.add(curr);
			Widget item = findItem(curr);
			if (item != null) {
				Item[] children = getChildren(item);
				// check if contains the dummy node
				if (children.length == 1 && children[0].getData() == null) {
					// not yet created
					collectChildren(curr, result);
				}
			}
		}
		return result.toArray();
	}

	private void collectChildren(Object element, ArrayList<Object> result) {
		Object[] filteredChildren = getFilteredChildren(element);
		for (int i = 0; i < filteredChildren.length; i++) {
			Object curr = filteredChildren[i];
			result.add(curr);
			collectChildren(curr, result);
		}
	}

	private static void getAllItems(Tree tree, List<TreeItem> allItems) {
		for (TreeItem item : tree.getItems()) {
			allItems.add(item);
			getAllItems(item, allItems);
		}
	}

	private static void getAllItems(TreeItem currentItem, List<TreeItem> allItems) {
		TreeItem[] children = currentItem.getItems();
		for (int i = 0; i < children.length; i++) {
			allItems.add(children[i]);
			getAllItems(children[i], allItems);
		}
	}

}
