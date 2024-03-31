package com.foxconn.mechanism.hhpnmaterialapply.dialog;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.IconsEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.StatusEnum;
import com.foxconn.mechanism.hhpnmaterialapply.domain.CheckDesignBean;

public class CheckTableViewerLabelProvider implements ITableLabelProvider, IColorProvider {
	
	private Image[] images = new Image[] {new Image(null, CheckTableViewerLabelProvider.class.getResourceAsStream(IconsEnum.Released.relativePath())), 
			new Image(null, CheckTableViewerLabelProvider.class.getResourceAsStream(IconsEnum.DesignRevIcons.relativePath()))};
	
	@Override
	public void addListener(ILabelProviderListener ilabelproviderlistener) {
	}
	
	// 当TableViewer对象被关闭时触发执行此方法
	@Override
	public void dispose() {
		// 别忘记SWT组件的原则: 自己创建，自己销毁
		for (Image image : images) {
			image.dispose();
		}
	}

	@Override
	public boolean isLabelProperty(Object obj, String s) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener ilabelproviderlistener) {
	}

	@Override
	public Image getColumnImage(Object element, int col) {
//		CheckDesignBean bean = (CheckDesignBean) element;
//		String status = bean.getStatus();
//		if (col == 3) {
//			if (status.equals(StatusEnum.已发行.name())) {				
//				return images[0];
//			}
//		} else if (col == 1) {
//			return images[1];
//		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int col) {
		CheckDesignBean bean = (CheckDesignBean) element;
		switch (col) {
		case 0:
			return bean.getId().toString();
		case 1:
			return bean.getModelName() == null ? "" : bean.getModelName().trim();
		case 2:
			return bean.getStatus() == null ? "" : bean.getStatus().trim();
		default:
			return "";
		}
	}

	/**
	 * 设置背景
	 */
	@Override
	public Color getBackground(Object arg0) {		
//		return ColorEnum.Gray.color(); // 设置背景为蓝色
		return null;
	}

	/**
	 * 设置前背景
	 */
	@Override
	public Color getForeground(Object arg0) {		
		return ColorEnum.TipColor.color();
	}

}
