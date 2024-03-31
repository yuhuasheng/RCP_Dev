package com.foxconn.electronics.L10Ebom.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.TableItem;
import com.foxconn.electronics.L10Ebom.constant.ApplyFormConstant;
import com.foxconn.electronics.L10Ebom.dialog.CustTableItem;
import com.foxconn.electronics.L10Ebom.domain.BOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.EENewBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishBOMToTableBean;
import com.foxconn.electronics.L10Ebom.domain.FinishMatRowBean;
import com.foxconn.electronics.L10Ebom.domain.HalfMatRowBean;
import com.foxconn.electronics.L10Ebom.domain.MENewBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.MatToTableBean;
import com.foxconn.electronics.L10Ebom.domain.PowerCordRowBean;
import com.foxconn.electronics.L10Ebom.domain.PowerNewBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.RDEBOMRowBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class TableTools {

	/**
	 * 添加行
	 * 
	 * @param tv
	 */
	public static void addRow(TableViewer tv, String tablePropName) {
		ViewerComparator comparator = tv.getComparator();
		if (comparator != null) {
			tv.setComparator(null);
		}
		 
		List<EBOMApplyRowBean> list = (List<EBOMApplyRowBean>) tv.getInput();
		
		EBOMApplyRowBean newBean = createBean(list, tablePropName, false, null);
					
		tv.add(newBean); // 增加到表格界面
		newBean.setHasModify(true); // 设置为发生更改
		newBean.setAdd(true); // 设置为新增

		int rowIndex = -1;
		TableItem[] items = tv.getTable().getSelection();
		if (CommonTools.isNotEmpty(items)) {
			TableItem lastItem = items[items.length - 1];
			rowIndex = tv.getTable().indexOf(lastItem);
		}

		if (rowIndex != -1) {
			rowIndex++;
			list.add(rowIndex, newBean);
		} else {
			list.add(newBean);
		}
		
		tv.setInput(list);
		tv.refresh(true);

	}

	
	public static void copyRow(TableViewer tv, String tablePropName, List<EBOMApplyRowBean> custNodeList) {
		ViewerComparator comparator = tv.getComparator();
		if (comparator != null) {
			tv.setComparator(null);
		}
		
		List<EBOMApplyRowBean> list = (List<EBOMApplyRowBean>) tv.getInput();
		
		EBOMApplyRowBean curLastSelectBean = custNodeList.get(custNodeList.size() - 1);
		EBOMApplyRowBean newBean = createBean(list, tablePropName, true, curLastSelectBean);
		
		tv.add(newBean); // 增加到表格界面
		newBean.setHasModify(true); // 设置为发生更改
		newBean.setAdd(true); // 设置为新增
		
		int rowIndex = -1;
		TableItem[] items = tv.getTable().getSelection();
		if (CommonTools.isNotEmpty(items)) {
			TableItem lastItem = items[items.length - 1];
			rowIndex = tv.getTable().indexOf(lastItem);
		}
		
		if (rowIndex != -1) {
			rowIndex++;
			list.add(rowIndex, newBean);
		} else {
			list.add(newBean);
		}
		
		tv.setInput(list);
		tv.refresh(true);
	}
	
	
	/**
	 * 删除行
	 * 
	 * @param list
	 */
	public static void deleteRow(List<EBOMApplyRowBean> list) {
		ListIterator listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			EBOMApplyRowBean deleteBean = (EBOMApplyRowBean) listIterator.next();
			deleteBean.setDeleteFlag(true);
			deleteBean.setHasModify(true);
			deleteBean.setSelect(true);
		}

	}

	/**
	 * 恢复行
	 * 
	 * @param list
	 */
	public static void recoveryRow(List<EBOMApplyRowBean> list) {
		ListIterator listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			EBOMApplyRowBean recoveryBean = (EBOMApplyRowBean) listIterator.next();
			recoveryBean.setDeleteFlag(false);
			recoveryBean.setHasModify(true);
			recoveryBean.setSelect(true);
		}
	}

	private static EBOMApplyRowBean createBean(List<EBOMApplyRowBean> totalList, String tablePropName, boolean copyFlag, EBOMApplyRowBean selectBean) {
//		totalList.sort(Comparator.comparing(EBOMApplyBean::getSequence)); // 按照项次进行升序
		List<Integer> sequenceList = null;
		if (CommonTools.isEmpty(totalList)) {
			sequenceList = new ArrayList<Integer>();
			sequenceList.add(0);
		} else {
			sequenceList = getSequenceList(totalList);
		}
		sequenceList.removeIf(value -> value == null);

		EBOMApplyRowBean newBean = null;
		if (CommonTools.isEmpty(totalList)) {
			if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_1)) {
				newBean = new FinishMatRowBean();
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_2)) {
				newBean = new HalfMatRowBean();
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_3)) {
				newBean = new BOMApplyRowBean();
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_4)) {
				newBean = new RDEBOMRowBean();
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_5)) {				
				newBean = new MENewBOMRowBean();				
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_6)) {
				newBean = new EENewBOMRowBean();
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_7)) {
				newBean = new PowerNewBOMRowBean();				
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_8)) {
				newBean = new PowerCordRowBean();
			} else if (tablePropName.equalsIgnoreCase(ApplyFormConstant.D9_BOMREQTABLE_9)) {
				newBean = new FinishBOMRowBean();
			}
		} else {
			EBOMApplyRowBean bean = null;
			if (copyFlag) {
				bean = selectBean;
			} else {
				bean = totalList.get(0);
			}
			if (bean instanceof FinishMatRowBean) {
				newBean = new FinishMatRowBean();
			} else if (bean instanceof HalfMatRowBean) {
				newBean = new HalfMatRowBean();
			} else if (bean instanceof BOMApplyRowBean) {
				newBean = new BOMApplyRowBean();
			} else if (bean instanceof RDEBOMRowBean) {
				newBean = new RDEBOMRowBean();
			} else if (bean instanceof MENewBOMRowBean) {
				newBean = new MENewBOMRowBean();
			} else if (bean instanceof EENewBOMRowBean) {
				newBean = new EENewBOMRowBean();
			} else if (bean instanceof PowerNewBOMRowBean) {
				newBean = new PowerNewBOMRowBean();
			} else if (bean instanceof PowerCordRowBean) {
				newBean = new PowerCordRowBean();
			} else if (bean instanceof FinishBOMRowBean) {
				newBean = new FinishBOMRowBean();
			}
			
			if (copyFlag) {
				copyBeanProp(bean, newBean);
			}
		}

		
		int maxSequence = Collections.max(sequenceList);
		maxSequence++;
		newBean.setSequence(maxSequence);
		return newBean;
	}

	
	private static void copyBeanProp(EBOMApplyRowBean bean, EBOMApplyRowBean newBean) {
		try {
			Field[] fields = bean.getClass().getDeclaredFields();
			Field[] newFields = newBean.getClass().getDeclaredFields();
			
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			List<Field> newFieldList = new ArrayList<Field>(Arrays.asList(newFields));
			
			fieldList.removeIf(field -> field.getType() != Integer.class && field.getType() != String.class && field.getType() != boolean.class);
			newFieldList.removeIf(field -> field.getType() != Integer.class && field.getType() != String.class && field.getType() != boolean.class);
			
			for (int i = 0; i < fieldList.size(); i++) {
				Field field = fieldList.get(i);
				Field newField = newFieldList.get(i);
				
				field.setAccessible(true);
				newField.setAccessible(true);
				
				Object value = field.get(bean);				
				newField.set(newBean, value); // 设置值
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static List<Integer> getSequenceList(List<EBOMApplyRowBean> list) {
		return list.stream().map(bean -> {
			return bean.getSequence();
		}).collect(Collectors.toList());
	}

	public static List<EBOMApplyRowBean> getList(List<EBOMApplyRowBean> custNodeList, Boolean flag) {
		List<EBOMApplyRowBean> tempList = new ArrayList<EBOMApplyRowBean>();
		if (CommonTools.isNotEmpty(custNodeList)) {
			custNodeList.forEach(bean -> {
				if (flag) {
					if (bean.isDeleteFlag()) {
						tempList.add(bean);
					}
				} else {
					if (!bean.isDeleteFlag()) {
						tempList.add(bean);
					}
				}

			});
		}

		return tempList;
	}

	public static List<EBOMApplyRowBean> getCustNodes(TableViewer tv) {
		IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
		if (CommonTools.isEmpty(selection)) {
			return null;
		}
		if (selection.size() > 0) {
			return selection.toList();
		}
		return null;
	}

	/**
	 * 更新绑定data值
	 * 
	 * @param bean
	 * @param index
	 * @param str
	 */
	public static void updateData(EBOMApplyRowBean bean, int index, String str) {
		try {
//			bean.setHasModify(true); // 设置为属性发生更改
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			fieldList.removeIf(field -> field.getType() == boolean.class);
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
				if (tcProp != null) {
					int cell = tcProp.cell();
					if (index != cell) {
						continue;
					}

					Object val = str;
					Object orignValue = field.get(bean); // 获取原始值
					if (("".equals(orignValue) || orignValue == null) && (!"".equals(val) && val != null)) {
						bean.setHasModify(true); // 设置发生更改
					} else if ((!"".equals(orignValue) && orignValue != null) && ("".equals(val) || val == null)) {
						bean.setHasModify(true); // 设置发生更改
					} else if ((!"".equals(orignValue) && orignValue != null) && (!"".equals(val) || val != null)) {
						if (field.getType() == Integer.class) {
							if (((Integer) orignValue).intValue() != ((Integer) val).intValue()) {
								bean.setHasModify(true); // 设置发生更改
							}
						} else {
							if (!orignValue.toString().equals(val.toString())) {
								bean.setHasModify(true); // 设置发生更改
							}
						}
					}

					if (field.getType() == Integer.class) {
						if ("".equals(str) || str == null) {
							val = "";
						} else {
							val = Integer.parseInt(str);
						}

					} else {
						if (str == null) {
							val = "";
						}
					}

					field.set(bean, val);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取TC属性值
	 * 
	 * @param bean
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> getTCStrPropMap(EBOMApplyRowBean bean)
			throws IllegalArgumentException, IllegalAccessException {
		System.out.println("start -->>  getTCPropMap");
		Map<String, Object> tcPropMap = new HashMap<>();
		Field[] fields = bean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> field.getType() == boolean.class);
		for (Field field : fieldList) {
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			if (tcProp != null) {
				String tcPropName = tcProp.tcProperty();
				Object o = field.get(bean);
				if (o != null) {
					if (field.getType() == Integer.class) {
						tcPropMap.put(tcPropName, (Integer) o);
					} else {
						tcPropMap.put(tcPropName, (String) o);
					}
				}
			}
		}
		return tcPropMap;
	}

	public static void halfMatToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalAccessException, TCException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;

			EBOMApplyRowBean bean = new HalfMatRowBean();
			bean = CustTableItem.tcPropMapping(bean, row);

			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);

				filterHalfMatExist(matInfoList, bean); // 过滤掉已经存在table中的记录
			}

		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {
				EBOMApplyRowBean newBean = new HalfMatRowBean();
				k++;

				newBean.setSequence(k);
				newBean.setSemiPN(toBean.getSemiPN());
				newBean.setSemiType(toBean.getSemiType());
				newBean.setDesc(toBean.getDesc());
				newBean.setModifySemiPN(false);
				newBean.setModifyDesc(false);
				newBean.setModifySemiType(false);				
				
				newBean.setAdd(true);
				newBean.setHasModify(true);
				list.add(newBean);

			}
		}
	}

	public static void finishBOMToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalArgumentException, IllegalAccessException, TCException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;
			EBOMApplyRowBean bean = new FinishBOMRowBean();
			bean = CustTableItem.tcPropMapping(bean, tcComponent);
			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);

				filterFinishBOMMatExist(matInfoList, bean);
			}
		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {
				EBOMApplyRowBean newBean = new FinishBOMRowBean();
				k++;

				newBean.setSequence(k);
				newBean.setFinishPN(toBean.getFinishPN());
				newBean.setWireType(toBean.getWireType());
				newBean.setShippingArea(toBean.getShippingArea());
				newBean.setFinalPN(toBean.getFinalPN());
				newBean.setPkgPN(toBean.getPkgPN());
				
				newBean.setModifyFinishPN(false);
				newBean.setModifyWireType(false);
				newBean.setModifyShippingArea(false);
				newBean.setModifyFinalPN(false);
				newBean.setModifyPkgPN(false);
				
				newBean.setAdd(true);
				newBean.setHasModify(true);
				list.add(newBean);
			}
		}
	}

	public static void BOMApplyMatToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalAccessException, TCException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;
			EBOMApplyRowBean bean = new BOMApplyRowBean();
			bean = CustTableItem.tcPropMapping(bean, row);

			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);

				TableTools.filterBOMApplyMatExist(matInfoList, bean);
			}
		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {
				EBOMApplyRowBean newBean = new BOMApplyRowBean();
				k++;

				newBean.setSequence(k);
				newBean.setFinishType(toBean.getFinishType());
				newBean.setFinishPN(toBean.getFinishPN());
				newBean.setDesc(toBean.getDesc());
				newBean.setModifyFinishType(false);
				newBean.setModifyFinishPN(false);
				newBean.setModifyDesc(false);
				
				
				newBean.setAdd(true);
				newBean.setHasModify(true);
				list.add(newBean);
			}
		}
	}

	public static void RDEBOMMatToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalAccessException, TCException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;
			EBOMApplyRowBean bean = new RDEBOMRowBean();
			bean = CustTableItem.tcPropMapping(bean, row);

			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);

				TableTools.filterRDEBOMMatExist(matInfoList, bean);
			}
		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {
				EBOMApplyRowBean newBean = new RDEBOMRowBean();
				k++;

				newBean.setSequence(k);
				newBean.setPkgPN(toBean.getPkgPN());
				newBean.setModifyPkgPN(false);
				
				newBean.setAdd(true);
				newBean.setHasModify(true);
				list.add(newBean);
			}

		}
	}

	public static void MEMatToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalAccessException, TCException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;

			EBOMApplyRowBean bean = new MENewBOMRowBean();
			bean = CustTableItem.tcPropMapping(bean, row);
			bean.setCreateMode(ApplyFormConstant.MENEWBOMWAY);

			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);
				
				TableTools.filterMEBOMMatExist(matInfoList, bean);
			}		

		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {	
				EBOMApplyRowBean newBean = new MENewBOMRowBean();
				newBean.setCreateMode(ApplyFormConstant.MENEWBOMWAY);
				k++;
				newBean.setSequence(k);
				newBean.setAssyPN(toBean.getAssyPN());
				newBean.setModifyAssyPN(false);
				newBean.setAdd(true);
				newBean.setHasModify(true);
				
				list.add(newBean);
			}
		}
	}

	public static void EEMatToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalAccessException, TCException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;

			EBOMApplyRowBean bean = new EENewBOMRowBean();
			bean = CustTableItem.tcPropMapping(bean, row);
			bean.setCreateMode(ApplyFormConstant.EENEWBOMWAY);

			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);
				
				TableTools.filterEEBOMMatExist(matInfoList, bean);
			}
			
		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {
				EBOMApplyRowBean newBean = new EENewBOMRowBean();
				newBean.setCreateMode(ApplyFormConstant.EENEWBOMWAY);
				k++;
				newBean.setSequence(k);
				newBean.setAssyPN(toBean.getAssyPN());
				newBean.setModifyAssyPN(false);
				newBean.setAdd(true);
				newBean.setHasModify(true);
				
				list.add(newBean);				
			}
		}
	}

	public static void PowerMatToTable(List<EBOMApplyRowBean> list, List<MatToTableBean> matInfoList,
			TCComponent[] relatedComponents) throws IllegalAccessException, TCException {
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;

			EBOMApplyRowBean bean = new PowerNewBOMRowBean();
			bean = CustTableItem.tcPropMapping(bean, row);
			bean.setCreateMode(ApplyFormConstant.POWERNEWBOMWAY);

			if (CommonTools.isNotEmpty(bean)) {
				bean.setRow(row);
				list.add(bean);
				
				TableTools.filterPowerBOMMatExist(matInfoList, bean);
			}			

		}

		int k = 0;
		if (CommonTools.isNotEmpty(list)) {
			k = list.get(list.size() - 1).getSequence();
		}

		if (CommonTools.isNotEmpty(matInfoList)) {
			for (MatToTableBean toBean : matInfoList) {
				EBOMApplyRowBean newBean = new EENewBOMRowBean();
				newBean.setCreateMode(ApplyFormConstant.POWERNEWBOMWAY);
				k++;
				newBean.setSequence(k);
				newBean.setAssyPN(toBean.getAssyPN());
				newBean.setModifyAssyPN(false);
				newBean.setAdd(true);
				newBean.setHasModify(true);
				list.add(newBean);
			}
		}
	}

	public static void filterHalfMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getSemiPN().equals(bean.getSemiPN())) {
				bean.setDesc(toTableBean.getDesc());				
				bean.setSemiType(toTableBean.getSemiType());
				bean.setModifySemiPN(false);
				bean.setModifyDesc(false);
				bean.setModifySemiType(false);
				listIterator.remove();
			}
		}

//		matInfoList.removeIf(matToBean -> matToBean.getSemiPN().equals(bean.getSemiPN()) 
//				&& matToBean.getSemiType().equals(bean.getSemiType()) 
//				&& matToBean.getDesc().equals(bean.getDesc()));		
	}

	public static void filterBOMApplyMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean) {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getFinishPN().equals(bean.getFinishPN())) {
				bean.setFinishType(toTableBean.getFinishType());
				bean.setDesc(toTableBean.getDesc());
				bean.setModifyFinishType(false);
				bean.setModifyFinishPN(false);
				bean.setModifyDesc(false);
				
				listIterator.remove();
			}
		}

//		matInfoList.removeIf(matToBean -> matToBean.getFinishType().equals(bean.getFinishType()) 
//				&& matToBean.getFinishPN().equals(bean.getFinishPN()) 
//				&& matToBean.getDesc().equals(bean.getDesc()));
	}

	public static void filterRDEBOMMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean) {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getPkgPN().equals(bean.getPkgPN())) {
				bean.setModifyPkgPN(false);
				
				listIterator.remove();
			}
		}
	}

	public static void filterMEBOMMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean) {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getAssyPN().equals(bean.getAssyPN())) {
				bean.setModifyAssyPN(false);
				
				listIterator.remove();
			}
		}
	}

	public static void filterEEBOMMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean) {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getAssyPN().equals(bean.getAssyPN())) {
				bean.setModifyAssyPN(false);
				
				listIterator.remove();
			}
		}		
	}

	public static void filterPowerBOMMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean) {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getAssyPN().equals(bean.getAssyPN())) {
				bean.setModifyAssyPN(false);
				
				listIterator.remove();
			}
		}	
	}

	public static void filterFinishBOMMatExist(List<MatToTableBean> matInfoList, EBOMApplyRowBean bean) {
		if (CommonTools.isEmpty(matInfoList)) {
			return;
		}

		ListIterator listIterator = matInfoList.listIterator();
		while (listIterator.hasNext()) {
			MatToTableBean toTableBean = (MatToTableBean) listIterator.next();
			if (toTableBean.getFinishPN().equals(bean.getFinishPN())) {
				bean.setWireType(toTableBean.getWireType());
				bean.setShippingArea(toTableBean.getShippingArea());
				bean.setFinalPN(toTableBean.getFinalPN());
				bean.setPkgPN(toTableBean.getPkgPN());
				
				bean.setModifyFinishPN(false);
				bean.setModifyWireType(false);
				bean.setModifyShippingArea(false);
				bean.setModifyFinalPN(false);
				bean.setModifyPkgPN(false);
				
				listIterator.remove();
			}
		}
	}

	public static MatToTableBean getBOMHalfMatInfo(MatToTableBean bean, TCSession session, TCComponent component) {
		TCComponentBOMWindow window = null;
		try {
			window = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomline = TCUtil.getTopBomline(window, component);
			List<TCComponentBOMLine> bomLineList = TCUtil.getTCComponmentBOMLines(topBomline, null, true);
			if (CommonTools.isNotEmpty(bomLineList)) {
				for (TCComponentBOMLine bomLine : bomLineList) {
					TCComponentItemRevision itemRev = bomLine.getItemRevision();
					String objectType = itemRev.getTypeObject().getName();
					String itemId = itemRev.getProperty("item_id");

					if (objectType.equals(ApplyFormConstant.D9_VIRTUALPART_REV)
							&& itemId.startsWith(ApplyFormConstant.HALFMATARRAY[0])) {
						bean.setFinalPN(itemId);
					} else if (objectType.equals(ApplyFormConstant.D9_VIRTUALPART_REV)
							&& itemId.startsWith(ApplyFormConstant.HALFMATARRAY[1])) {
						bean.setPkgPN(itemId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (window != null) {
				try {
					window.close();
				} catch (TCException e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1);
				}
			}
		}

		return bean;
	}

	public static List<MatToTableBean> syncFinishMatAndHalfMatInfo(TCSession session, TCComponentItemRevision itemRev,
			List<CustTableItem> totalTableItemList, List<String> matToList) {
		Optional<CustTableItem> findAny = totalTableItemList.stream()
				.filter(custItem -> custItem.getTablePropName().equals(ApplyFormConstant.D9_BOMREQTABLE_9)).findAny();
		if (!findAny.isPresent()) {
			return null;
		}

		CustTableItem finishBOMCustTableItem = findAny.get();
		String result = TableTools.getFind(matToList, finishBOMCustTableItem.getTablePropName());
		if (CommonTools.isEmpty(result)) {
			return null;
		}

		String folderPropName = result.split("=")[1];
		List<MatToTableBean> resultList = new ArrayList<MatToTableBean>();
		try {
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(folderPropName);
			List<TCComponent> filteredList = TableTools.filterComponentList(relatedComponents,
					ApplyFormConstant.D9_FINISHEDPART_REV, ApplyFormConstant.FINISHBOMARRAY);
			if (CommonTools.isNotEmpty(filteredList)) {
				MatToTableBean bean = null;
				for (TCComponent component : filteredList) {
					bean = new FinishBOMToTableBean();
					bean.setFinishPN(component.getProperty("item_id"));
					bean.setDesc(component.getProperty("d9_EnglishDescription") == null ? "" : component.getProperty("d9_EnglishDescription").trim());

					bean = getBOMHalfMatInfo(bean, session, component);

					resultList.add(bean);
				}
			}

			if (CommonTools.isEmpty(resultList)) {
				return null;
			}

			CustTableItem BOMApplyCustTableItem = null;
			CustTableItem finishMatCustTableItem = null;

			while (true) {
				Optional<CustTableItem> BOMApplyFindAny = totalTableItemList.stream()
						.filter(custItem -> custItem.getTablePropName().equals(ApplyFormConstant.D9_BOMREQTABLE_3))
						.findAny();

				Optional<CustTableItem> finishMatFindAny = totalTableItemList.stream()
						.filter(custItem -> custItem.getTablePropName().equals(ApplyFormConstant.D9_BOMREQTABLE_1))
						.findAny();
				if (BOMApplyFindAny.isPresent() && finishMatFindAny.isPresent()) {
					BOMApplyCustTableItem = BOMApplyFindAny.get();
					finishMatCustTableItem = finishMatFindAny.get();
					if (BOMApplyCustTableItem.getProgressBar().isDisposed()
							&& finishMatCustTableItem.getProgressBar().isDisposed()) { // 进度条已经加载完成
						break;
					}
				}
			}

			TableViewer BOMApplyTableViewer = BOMApplyCustTableItem.getTv();
			List<EBOMApplyRowBean> BOMApplyList = (List<EBOMApplyRowBean>) BOMApplyTableViewer.getInput();
			if (CommonTools.isEmpty(BOMApplyList)) {
				System.out.println(ApplyFormConstant.D9_BOMREQTABLE_3 + ", 数据加载失败");
			} else {
				for (MatToTableBean toBean : resultList) {
					Optional<EBOMApplyRowBean> any = BOMApplyList.stream()
							.filter(bean -> bean.getFinishPN().equals(toBean.getFinishPN())).findAny();
					if (any.isPresent()) {
						toBean.setWireType(any.get().getWireType());
					}
				}
			}

			TableViewer finishMatTableViewer = finishMatCustTableItem.getTv();
			List<EBOMApplyRowBean> finishMatList = (List<EBOMApplyRowBean>) finishMatTableViewer.getInput();
			if (CommonTools.isEmpty(finishMatList)) {
				System.out.println(ApplyFormConstant.D9_BOMREQTABLE_1 + ", 数据加载失败");
			} else {
				for (MatToTableBean toBean : resultList) {
					Optional<EBOMApplyRowBean> any = finishMatList.stream()
							.filter(bean -> bean.getFinishPNDesc().equals(toBean.getDesc())).findAny();
					if (any.isPresent()) {
						toBean.setShippingArea(any.get().getShippingArea());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public static List<TCComponent> filterComponentList(TCComponent[] relatedComponents, String type, String[] array) {
		if (CommonTools.isEmpty(relatedComponents)) {
			return null;
		}
		return Arrays.stream(relatedComponents).filter(component -> {
			try {
				if (!type.equals(component.getTypeObject().getName())) {
					return false;
				}

				String itemId = component.getProperty("item_id");
				boolean anyMatch = Stream.of(array).anyMatch(str -> itemId.startsWith(str));
				if (!anyMatch) {
					return false;
				}
				return true;
			} catch (TCException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}).collect(Collectors.toList());
	}

	/**
	 * 查找内容
	 * 
	 * @param list
	 * @param value
	 * @return
	 */
	public static String getFind(List<String> list, String value) {
		Optional<String> findAny = list.stream().filter(str -> str.contains(value)).findAny();
		if (findAny.isPresent()) {
			return findAny.get();
		}

		return null;
	}

	/**
	 * 进行匹配
	 * 
	 * @param list
	 * @param matchStr
	 * @return
	 */
	public static boolean getMatch(List<String> list, String matchStr) {
		return list.stream().anyMatch(str -> str.contains(matchStr));
	}
	
	
	/**
	 * 匹配当期登录的角色
	 * @param list
	 * @param groupName
	 * @param tablePropName
	 * @return
	 */
	public static boolean anyMatchGroup(List<String> list, String groupName, String tablePropName) {
		return list.stream().anyMatch(str -> {
			str = str.replace(" ", "");
			String[] split = str.split("=");
			if (split[0].equals(tablePropName)) {
				String[] split2 = split[1].split("\\|");
				return Stream.of(split2).anyMatch(value -> groupName.contains(value));
			}
			return false;
		});
	}
}
