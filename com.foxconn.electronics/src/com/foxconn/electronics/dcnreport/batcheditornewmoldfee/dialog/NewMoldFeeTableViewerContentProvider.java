package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.dialog;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class NewMoldFeeTableViewerContentProvider implements IStructuredContentProvider {

			// 对输入到表格的数据集合进行筛选和转化，输入的数据集全部转化成数组，每一个数组元素就是一个实体类对象，也就是表格中的一条记录
			@Override
			public Object[] getElements(Object element) {
				// 参数element就是通过setInput(Object input)输入的对象input
				if (element instanceof List) {
					return ((List) element).toArray(); // 将数据集List转换成数组
				} else {
					return new Object[0]; // 如果非List类型则返回一个空数组
				}		
			}

			// 当TableViewer对象被关闭时触发执行此方法
			@Override
			public void dispose() {}

			@Override
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
				
			}
}
