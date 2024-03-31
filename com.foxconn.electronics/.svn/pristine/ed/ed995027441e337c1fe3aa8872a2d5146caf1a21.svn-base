package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.dialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.domain.NewMoldFeeBean;
import com.foxconn.tcutils.util.TCPropertes;

public class NewMoldFeeTableViewerLabelProvider implements ITableLabelProvider, IColorProvider {

	@Override
	public void addListener(ILabelProviderListener arg0) {
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object obj, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener ilabelproviderlistener) {
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int col) {		
		return null;
	}

	@Override
	public String getColumnText(Object element, int col) {
		try {
			NewMoldFeeBean bean = (NewMoldFeeBean) element;
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>( Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
				if (tcProp != null) {
					int cell = tcProp.cell();
					if (col != cell) {
						continue;
					}
					
					String val = "";
					Object value = field.get(bean);
					if (field.getType() == Integer.class) {
						if (value == null || value.equals("")) {
							val = "";
						} else {
							val = String.valueOf((Integer)value);
						}
					} else {
						if (value == null) {
							val = "";
						} else {
							val = field.get(bean) + "";
						}
					}					
					return val.trim();	
				}
				
			}
		} catch (Exception e) {

		}
		return "";
	}
	
	
}
