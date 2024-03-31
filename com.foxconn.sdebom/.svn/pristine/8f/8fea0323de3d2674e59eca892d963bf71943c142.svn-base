package com.foxconn.sdebom.batcheditorebom.custtree;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.foxconn.sdebom.batcheditorebom.constants.IconsEnum;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.tcutils.util.CommonTools;

public class CustTableLabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {
	
	private Image[] images = new Image [] { new Image(null, CustTableLabelProvider.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath())), 
			new Image(null, CustTableLabelProvider.class.getResourceAsStream(IconsEnum.ReplacePartRevIcons.relativePath()))};
	
	@Override
	public void addListener(ILabelProviderListener arg0) {
		
	}

	// 当TreeViewer对象被关闭时触发执行此方法
	@Override
	public void dispose() {
		// 别忘记了SWT组件的原则，自己创建，自己释放
		for (Image image : images) {
			image.dispose();
		}
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		
	}

	@Override
	public Font getFont(Object arg0) {
		return null;
	}

	/**
	 * 设置背景色
	 */
	@Override
	public Color getBackground(Object element) {
		CustTreeNode treeNode = (CustTreeNode) element;
		SDEBOMBean data = treeNode.getData();
		if (CommonTools.isEmpty(data)) {
			return null;
		}		
		return data.getColor();
	}

	/**
	 * 设置前背景
	 */
	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int col) {			
		if (col == 0) {			
			CustTreeNode treeNode = (CustTreeNode) element;
			SDEBOMBean data = treeNode.getData();	
			if (data.getSub()) {
				return images[1];
			} else {
				return images[0];
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int col) {
		CustTreeNode treeNode = (CustTreeNode) element;
		SDEBOMBean data = treeNode.getData();
		switch (col) {
		case 0:
			return data.getTitle() == null ? "" : data.getTitle().trim();
		case 1:
			return data.getCustomerPN() == null ? "" : data.getCustomerPN().trim();
		case 2:
			return data.getCustomerPNDesc() == null ? "" : data.getCustomerPNDesc().trim();
		case 3:
			return data.getDescriptionEn() == null ? "" : data.getDescriptionEn().trim();
		case 4:
			return data.getAssemblyStatus() == null ? "" : data.getAssemblyStatus().trim();
		default:
			return "";
		}
	}

}
