package com.foxconn.mechanism.jurisdiction;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class CustTableLableProvider implements ITableLabelProvider{

	@Override
	public void addListener(ILabelProviderListener paramILabelProviderListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object paramObject, String paramString) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener paramILabelProviderListener) {
	}

	@Override
	public Image getColumnImage(Object paramObject, int paramInt) {
		 if (paramInt == 0) {
			 CustTreeNode treeNode = (CustTreeNode) paramObject;
			 return treeNode.getImage();
		 }
		return null;
	}

	@Override
	public String getColumnText(Object paramObject, int paramInt) {
		CustTreeNode treeNode = (CustTreeNode) paramObject;
		switch (paramInt) {
		case 0:
			return treeNode.getObjStr();
		case 1:
			return treeNode.getOwner();
		case 3:
			return treeNode.getPersonLiable();
		case 4:
			return treeNode.getMailSubject();
		default:
			return "";
		}
	}

}
