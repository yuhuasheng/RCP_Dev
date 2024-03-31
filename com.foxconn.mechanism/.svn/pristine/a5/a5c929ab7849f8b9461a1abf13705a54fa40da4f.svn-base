package com.foxconn.mechanism.batchDownloadDataset;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class BomLineTableLableProvider implements ITableLabelProvider{

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
			 BomLineTreeNode treeNode = (BomLineTreeNode) paramObject;
			 return treeNode.getImage();
		 }
		return null;
	}

	@Override
	public String getColumnText(Object paramObject, int paramInt) {
		BomLineTreeNode treeNode = (BomLineTreeNode) paramObject;
		switch (paramInt) {
		case 0:
			return treeNode.getObjStr();
		case 1:
			return treeNode.getItemRevId();
		case 2:
			return treeNode.getItemRevName();
		case 3:
			return treeNode.getOwner();
		case 4:
			return treeNode.getAssignUser();
		case 5:
			return treeNode.getMailSubject();
		default:
			return "";
		}
	}

}
