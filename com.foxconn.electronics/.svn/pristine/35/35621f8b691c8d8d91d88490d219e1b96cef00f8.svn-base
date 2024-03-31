package com.foxconn.electronics.L10Ebom.sort;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;

public class MyComparator extends ViewerComparator {
	
	private int direction;
	
	public MyComparator(int direction) {
        this.direction = direction;
    }
	
	
	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		EBOMApplyRowBean bean1 = (EBOMApplyRowBean) o1;
		EBOMApplyRowBean bean2 = (EBOMApplyRowBean) o2;
		return direction == SWT.UP ? bean1.getSequence().compareTo(bean2.getSequence()) : bean2.getSequence().compareTo(bean1.getSequence());
	}
	
}
