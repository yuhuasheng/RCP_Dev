package com.foxconn.mechanism.hhpnmaterialapply.PRT;

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

public class PRTCustTreeViewerTableLabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {

	@Override
	public void addListener(ILabelProviderListener ilabelproviderlistener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object obj, String s) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener ilabelproviderlistener) {
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
		String objectType = bomInfo.getObjectType();
		PropertiesInfo propertiesInfo = bomInfo.getPropertiesInfo();
		switch (paramInt) {
		case 0:
			return treeNode.getItemId() == null ? "" : treeNode.getItemId().trim();	
		case 1:
			return propertiesInfo.getHHPN() == null ? "" : propertiesInfo.getHHPN().trim();
		case 2:
			return propertiesInfo.getHHPNState() == null ? "" : propertiesInfo.getHHPNState().trim();
		case 3:
			return propertiesInfo.getCustomerPN() == null ? "" : propertiesInfo.getCustomerPN().trim();
		case 4:
			return propertiesInfo.getChineseDescription() == null ? "" : propertiesInfo.getChineseDescription().trim();
		case 5:
			return propertiesInfo.getEnglishDescription() == null ? "" : propertiesInfo.getEnglishDescription();
		case 6:
			return String.valueOf(propertiesInfo.getQtyUnits());
		case 7:
			return propertiesInfo.getCustomerDrawingNumber() == null ? "" : propertiesInfo.getCustomerDrawingNumber().trim();
		case 8:
			return propertiesInfo.getCustomer3DRev() == null ? "" : propertiesInfo.getCustomer3DRev().trim();
		case 9:
			return propertiesInfo.getCustomer2DRev() == null ? "" : propertiesInfo.getCustomer2DRev().trim();
		case 10:
			return propertiesInfo.getCustomerID() == null ? "" : propertiesInfo.getCustomerID().trim();
		case 11:
			return propertiesInfo.getSmPartMaterialDimension() == null ? "" : propertiesInfo.getSmPartMaterialDimension().trim();
		case 12:
			return propertiesInfo.getProjectName() == null ? "" : propertiesInfo.getProjectName().trim();
		case 13:
			return propertiesInfo.getModule() == null ? "" : propertiesInfo.getModule().trim();
		case 14:
			return propertiesInfo.getMaterial() == null ? "" : propertiesInfo.getMaterial().trim();
		case 15:
			return propertiesInfo.getColor() == null ? "" : propertiesInfo.getColor().trim();
		case 16:
			return propertiesInfo.getSurfaceFinished() == null ? "" : propertiesInfo.getSurfaceFinished().trim();
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
	public Font getFont(Object obj) {
		return null;
	}
}
