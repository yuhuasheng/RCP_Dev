package com.foxconn.mechanism.hhpnmaterialapply.MNT;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.hhpnmaterialapply.CustTreeNode;

public class MNTCustTreeViewerTableLabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {

	@Override
	public void addListener(ILabelProviderListener arg0) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {

	}
	
	@Override
	public Image getColumnImage(Object element, int paramInt) {
		if (paramInt == 0) {
			CustTreeNode treeNode = (CustTreeNode) element;
			return treeNode.getImage();
		}
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int paramInt) {
		CustTreeNode treeNode = (CustTreeNode) element;			
		BOMInfo bomInfo = treeNode.getBomInfo();
//		String objectType = bomInfo.getObjectType();
		PropertiesInfo propertiesInfo = bomInfo.getPropertiesInfo();
		switch (paramInt) {
		case 0:			
			return treeNode.getItemId() == null ? "" : treeNode.getItemId().trim();		
		case 1:
			return propertiesInfo.getHHPN() == null ? "" : propertiesInfo.getHHPN().trim();
		case 2:
			return propertiesInfo.getHHPNState() == null ? "" : propertiesInfo.getHHPNState().trim();
		case 3:
			return propertiesInfo.getChineseDescription() == null ? "" : propertiesInfo.getChineseDescription().trim();
		case 4:
			return propertiesInfo.getEnglishDescription() == null ? "" : propertiesInfo.getEnglishDescription().trim();
		case 5:
			return String.valueOf(propertiesInfo.getQtyUnits());
		case 6:
			return propertiesInfo.getMaterial() == null ? "" : propertiesInfo.getMaterial().trim();
		case 7:
			return propertiesInfo.getPartWeight() == null ? "" : propertiesInfo.getPartWeight().trim();
		case 8:
			return propertiesInfo.getColor() == null ? "" : propertiesInfo.getColor().trim();
		case 9:
			return propertiesInfo.getRemark() == null ? "" : propertiesInfo.getRemark().trim();
		case 10:
			return propertiesInfo.getSurfaceFinished() == null ? "" : propertiesInfo.getSurfaceFinished().trim();
		case 11:
			return propertiesInfo.getReferenceDimension() == null ? "" : propertiesInfo.getReferenceDimension().trim();
		case 12:
			return propertiesInfo.getRunnerWeight() == null ? "" : propertiesInfo.getRunnerWeight().trim();
		case 13:
			return propertiesInfo.getTotalweight() == null ? "" : propertiesInfo.getTotalweight().trim();
		default:
			return "";
		}		
	}
	
	
	/**
	 * 设置前背景
	 */
	@Override
	public Color getForeground(Object element) {		
		return null;
	}
	
	/**
	 * 设置背景色
	 */

	@Override
	public Color getBackground(Object element) {	
		CustTreeNode treeNode = (CustTreeNode) element;
		BOMInfo bomInfo = treeNode.getBomInfo();
		if (null == bomInfo) {
			return null;
		}
		return bomInfo.getColor();
	}

	@Override
	public Font getFont(Object arg0) {		
		return null;
	}
	
}
