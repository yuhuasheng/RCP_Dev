package com.foxconn.electronics.L10Ebom.sort;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;

public class MySorter extends ViewerSorter {

	private static final int ID = 1; // 每列对应一个不同的常量，正数表示要升序，相反数表示要降序

	public static final MySorter ID_ASC = new MySorter(ID);
	public static final MySorter ID_DESC = new MySorter(-ID);

	private int sortType; // 当前所要排序的列，取自上面的ID值或相反数

	public MySorter(int sortType) {
		this.sortType = sortType;
	}

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		EBOMApplyRowBean bean1 = (EBOMApplyRowBean) obj1;
		EBOMApplyRowBean bean2 = (EBOMApplyRowBean) obj2;
		switch (sortType) {
			case ID: {
				Integer sequence1 = bean1.getSequence();
				Integer sequence2 = bean2.getSequence();
				return sequence1.compareTo(sequence2);
			}
			case -ID: {
				Integer sequence1 = bean1.getSequence();
				Integer sequence2 = bean2.getSequence();
				return sequence2.compareTo(sequence1);
			}
		}
		return 0;
	}

}
