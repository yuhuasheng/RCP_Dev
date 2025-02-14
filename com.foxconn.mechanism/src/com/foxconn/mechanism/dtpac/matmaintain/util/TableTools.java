package com.foxconn.mechanism.dtpac.matmaintain.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory.MatrixFatory;
import com.foxconn.mechanism.dtpac.matmaintain.dialog.PACMatMaintainDialog;
import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainConstant;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.bean.BeanUtil;

public class TableTools {
	
	public static void updateData(MatMaintainBean bean, int index, String str) {
		try {
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			fieldList.removeIf(field -> field.getType() == boolean.class);
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addRow(TableViewer tv, PACMatMaintainDialog dialog, Registry reg) {
		ViewerComparator comparator = tv.getComparator();
		if (comparator != null) {
			tv.setComparator(null);
		}
		
		List<MatMaintainBean> cacheDataList = dialog.getCacheDataList();
		List<MatMaintainBean> list = (List<MatMaintainBean>) tv.getInput();
		
		MatMaintainBean newBean = createBean(list, dialog, reg, false);
		newBean.setUUID(CommonTools.getUUID()); // 设置UUID
		tv.add(newBean); // 增加到表格界面
		newBean.setHasModify(true);
		
		int rowIndex = -1;
		TableItem[] items = tv.getTable().getSelection();
		if (CommonTools.isNotEmpty(items)) {
			TableItem lastItem = items[items.length - 1];
			rowIndex = tv.getTable().indexOf(lastItem);
		}
		
		if (rowIndex != -1) {
			rowIndex++;
			list.add(rowIndex, newBean);
			cacheDataList.add(rowIndex, newBean);
		} else {
			list.add(newBean);
			cacheDataList.add(newBean);
		}		
		
		tv.setInput(list);
		tv.refresh(true);
	}
	
	
	public static void deleteRow(List<MatMaintainBean> list) {
		ListIterator listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			MatMaintainBean deleteBean = (MatMaintainBean) listIterator.next();
			deleteBean.setDelete(true);
			deleteBean.setHasModify(true);
			deleteBean.setSelect(true);
		}
	}
	
	
	public static void recoveryRow(List<MatMaintainBean> list) {
		ListIterator listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			MatMaintainBean deleteBean = (MatMaintainBean) listIterator.next();
			deleteBean.setDelete(false);
			deleteBean.setHasModify(true);
			deleteBean.setSelect(true);
		}
	}
	
	/**
	 * 在此方法中，界面清空记录，后台数据用缓存list集合记录
	 * @param tv
	 */
	public static void clearTableRow(TableViewer tv, PACMatMaintainDialog dialog) {
		List<MatMaintainBean> cacheDataList = dialog.getCacheDataList();
		List<MatMaintainBean> input = (List<MatMaintainBean>) tv.getInput();
		Table table = tv.getTable();
		TableItem[] items = table.getItems();
		
		for (int i = items.length - 1; i >= 0; i--) {
			TableItem item = items[i];
			MatMaintainBean bean = (MatMaintainBean) item.getData();
			bean.setDelete(true);
			table.remove(i);
			updateCacheDataList(cacheDataList, new ArrayList<MatMaintainBean>() {{
				add(bean);
			}}); // 放入缓存中
			input.remove(bean);
		}
		
		tv.refresh();
	}	
	
	
	public static MatMaintainBean createBean(List<MatMaintainBean> totalList, PACMatMaintainDialog dialog, Registry reg, boolean copy) {
		List<Integer> indexList = null;
		if (CommonTools.isEmpty(totalList)) {
			indexList = new ArrayList<Integer>();
			indexList.add(0);
		} else {
			indexList = getIndexList(totalList);
		}		
		
		MatMaintainBean newBean = new MatMaintainBean();
		newBean.setPartType(dialog.getPartType());
		newBean.setMatType(dialog.getCurMatType());
		newBean.setMatName(dialog.getCurMatName());
		newBean.setItemId(dialog.getSelectItemRevBean().getItemId());
		newBean.setVersion(dialog.getSelectItemRevBean().getVersion());
		
		String defaultType = dialog.getTypeList().get(0);
		newBean.setType(defaultType);
		List<String> matList = dialog.getLovValue(dialog.getPartType(), dialog.getCurMatType(), dialog.getCurMatName(), defaultType);
		String defaultMat = matList.get(0);
		newBean.setMaterial(defaultMat);
//		newBean.setTableQty("1"); // table表数量默认设置为1
		newBean.setQty("1");
		List<ExcelBean> matBeanList = dialog.getMatBeanList();
		Optional<ExcelBean> findAny = matBeanList.stream().filter(e -> e.getPartType().equals(S2TTransferUtil.toTraditionnalString(newBean.getPartType())) 
				&& e.getMatType().equals(S2TTransferUtil.toTraditionnalString(newBean.getMatType()))&& e.getMatName().equals(S2TTransferUtil.toTraditionnalString(newBean.getMatName())) 
				&& e.getType().equals(S2TTransferUtil.toTraditionnalString(newBean.getType())) && e.getMaterial().contains(S2TTransferUtil.toTraditionnalString(newBean.getMaterial()))).findAny();
		if (findAny.isPresent()) {
			ExcelBean excelBean = findAny.get();
			newBean.setUsageCalculation(excelBean.getUsageCalculation());
			newBean.setCalcullationUnit(excelBean.getCalcullationUnit());
		}
		
		List<ExcelBean> matCostBeanList = dialog.getMatCostBeanList();
		Optional<ExcelBean> findAny2 = matCostBeanList.stream().filter(e -> S2TTransferUtil.toTraditionnalString(e.getMaterial()).equals(defaultMat)).findAny();
		if (findAny2.isPresent()) {
			ExcelBean excelBean2 = findAny2.get();
			newBean.setCostFactor(excelBean2.getCostFactor());
		}
		
		newBean.setCost(getMatCost(dialog, newBean));
		
		try {
			TableTools.setDisableEditorColumnValue(newBean, matBeanList); // 设置不能编列的属性值
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
			TCUtil.errorMsgBox(e1.getLocalizedMessage(), reg.getString("ERROR.MSG"));
		} 
		
		int maxIndex = Collections.max(indexList);
		maxIndex++;
		newBean.setIndex(maxIndex);
		return newBean;
	}
	
	
	/**
	 * 属性的复制
	 * @param bean
	 * @param newBean
	 */
	public static void copyBeanProp(MatMaintainBean bean, Object newBean) {
		try {
			Field[] fields = bean.getClass().getDeclaredFields();
			Field[] newFields = null;
			if (newBean instanceof MatMaintainBean) {
				newFields = ((MatMaintainBean) newBean).getClass().getDeclaredFields();
			} else if (newBean instanceof ExcelBean ) {
				newFields = ((ExcelBean) newBean).getClass().getDeclaredFields();
			}
			
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			List<Field> newFieldList = new ArrayList<Field>(Arrays.asList(newFields));
			
			for (Field field : fieldList) {
				field.setAccessible(true);
				
				Object value = field.get(bean);
				String name = field.getName();
				
				Optional<Field> findAny = newFieldList.stream().filter(e -> {
					e.setAccessible(true);
					return e.getName().equals(name);					
				}).findAny();
				
				if (findAny.isPresent()) {
					Field newField = findAny.get();
					newField.set(newBean, value);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	
	/**
	 * 重新设置计算尺寸参数，
	 * @param excelBean
	 */
	public static void resetSizeValue(ExcelBean excelBean) {
//		excelBean.setLength(CommonTools.isEmpty(excelBean.getLength()) ? "0" : excelBean.getLength());
//		excelBean.setWidth(CommonTools.isEmpty(excelBean.getWidth()) ? "0" : excelBean.getWidth());
//		excelBean.setHeight(CommonTools.isEmpty(excelBean.getHeight()) ? "0" : excelBean.getHeight());
//		excelBean.setThickness(CommonTools.isEmpty(excelBean.getThickness()) ? "0" : excelBean.getThickness());
//		excelBean.setDensity(CommonTools.isEmpty(excelBean.getDensity()) ? "0" : excelBean.getDensity());
//		excelBean.setWeight(CommonTools.isEmpty(excelBean.getWeight()) ? "0" : excelBean.getWeight());
		String qty = excelBean.getQty();
		if (qty.contains("*") && CommonTools.isNotEmpty(excelBean.getCostFactor())) {
			BigDecimal totalCostDecimal = new BigDecimal(1);
			String[] split = qty.split("\\*");
			for (String str : split) {
				str = CommonTools.replaceBlank(str);
				if (CommonTools.isNotEmpty(str)) {
					BigDecimal bigDecimal = new BigDecimal(str);
					totalCostDecimal = totalCostDecimal.multiply(bigDecimal);
//					totalCostDecimal = totalCostDecimal.add(bigDecimal);
				}			
			}
			
			String totalQty =  totalCostDecimal.stripTrailingZeros().toPlainString();
			excelBean.setQty(totalQty);			
		} 
		
//		else {
//			excelBean.setQty(CommonTools.isEmpty(excelBean.getQty()) ? "0" : excelBean.getQty());
//		}
//		
//		excelBean.setCostFactor(CommonTools.isEmpty(excelBean.getCostFactor()) ? "0" : excelBean.getCostFactor());
	}
	
	/**
 	 * 重新设置总物料成本文本框的值
	 * @throws TCException 
 	 */
	public static void updateTotalCost(List<MatMaintainBean> list, TCComponentBOMLine bomLine, Label label, Text text) throws TCException {
 		BigDecimal totalCostDecimal = new BigDecimal(0);
		for (MatMaintainBean bean : list) {
			String cost = bean.getCost();
			if (CommonTools.isNotEmpty(cost)) {
				BigDecimal bigDecimal = new BigDecimal(cost);
				totalCostDecimal = totalCostDecimal.add(bigDecimal);
			}
		}
		
		String totalCostValue =  totalCostDecimal.stripTrailingZeros().toPlainString();		

		
		TCComponentItemRevision selectItemRev = bomLine.getItemRevision();
		if (TCUtil.checkUserPrivilege(TCUtil.getTCSession(), selectItemRev)) { // 判断是否有编辑权
			selectItemRev.setProperty(MatMaintainConstant.D9_PARTCOST, totalCostValue); // 将总物料成本属性值保存至当前选中的对象版本的d9_PartCost属性中
		}
						
		
		if (CommonTools.isNotEmpty(list)) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (!label.isVisible()) {
						label.setVisible(true);
					}
					
					if (!text.isVisible()) {
						text.setVisible(true);
					}
					
					text.setText(totalCostValue);
				}
			});
			
		} 	
								
	}
 	
	/**
	 * 设置不能编列的属性值 值为"/"代表不能编辑
	 * @param bean
	 * @param excelList
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
 	public static void setDisableEditorColumnValue(MatMaintainBean bean, List<ExcelBean> excelList) throws IllegalArgumentException, IllegalAccessException {
 		Optional<ExcelBean> findAny = excelList.stream().filter(e -> e.getPartType().equals(bean.getPartType()) && e.getMatType().equals(bean.getMatType()) && e.getMatName().equals(bean.getMatName()) 
 				&& e.getType().equals(bean.getType()) && e.getMaterial().contains(bean.getMaterial())).findAny();
 		if (findAny.isPresent()) {
			ExcelBean excelBean = findAny.get();
			
			Field[] matFields = bean.getClass().getDeclaredFields();
			Field[] excelFields = excelBean.getClass().getDeclaredFields();
			
			List<Field> matFieldList = new ArrayList<Field>(Arrays.asList(matFields));
			List<Field> excelFieldList = new ArrayList<Field>(Arrays.asList(excelFields));
			excelFieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			excelFieldList.removeIf(field -> CommonTools.isEmpty(field.getAnnotation(TCPropertes.class).columnName()));
			
			for (Field excelField : excelFieldList) {
				excelField.setAccessible(true);
				TCPropertes tcProp = excelField.getAnnotation(TCPropertes.class);
				String columnName = tcProp.columnName();
				if (!columnName.equals(MatMaintainConstant.LENGTH) && !columnName.equals(MatMaintainConstant.WIDTH) && !columnName.equals(MatMaintainConstant.HEIGHT) 
						&& !columnName.equals(MatMaintainConstant.THICKNESS) && !columnName.equals(MatMaintainConstant.DENSITY) && !columnName.equals(MatMaintainConstant.WEIGHT) 
						&& !columnName.equals(MatMaintainConstant.LENGTH)) {
					continue;
				}
				
				String excelValue = excelField.get(excelBean) == null ? "" : excelField.get(excelBean).toString().trim();
				String name = excelField.getName();
				
				if (!"/".equals(excelValue)) {
					continue;
				}
				
				Optional<Field> findAny1 = matFieldList.stream().filter(e -> {
					e.setAccessible(true);
					return e.getName().equals(name);					
				}).findAny();
				
				if (findAny.isPresent()) {
					Field matField = findAny1.get();
					String matValue = matField.get(bean) == null ? "" : matField.get(bean).toString().trim();
					if (!CommonTools.isNumeric(matValue)) { // 判断是否为数字
						matField.set(bean, excelValue);
					}					
				}
			}
		} 		
 	}
 	
 	/**
	 * 返回物料成本
	 * @param bean
	 * @return
	 */
	public static String getMatCost(PACMatMaintainDialog dialog, MatMaintainBean bean) {	
		bean.setPartType(dialog.getPartType());
		bean.setMatType(dialog.getCurMatType());
		bean.setMatName(dialog.getCurMatName());
		
		ExcelBean excelBean = new ExcelBean();
		TableTools.copyBeanProp(bean, excelBean); // 属性复制	
		
		TableTools.resetSizeValue(excelBean); // 重新计算尺寸,为空时设置默认值		
		
		MatrixFatory.calculateCost(excelBean);
		
		return CommonTools.isEmpty(excelBean.getCost()) ? "" : excelBean.getCost(); // 获取计算后的物料成本		
	}
	
	
	private static List<Integer> getIndexList(List<MatMaintainBean> list) {
		return list.stream().map(bean -> bean.getIndex()).collect(Collectors.toList());
	}
	
	
	public static void resetData(PACMatMaintainDialog dialog) {
		List<MatMaintainBean> cacheDataList = dialog.getCacheDataList();
		for (MatMaintainBean bean : cacheDataList) {
			bean.setPartType(dialog.getPartType());
			bean.setMatType(dialog.getCurMatType());
			bean.setMatName(dialog.getCurMatName());
		}
	}
	
	
	/**
	 * 获取TC属性值
	 * @param bean
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> getTCStrPropMap(MatMaintainBean bean, String type) throws IllegalArgumentException, IllegalAccessException {
		System.out.println("start -->>  getTCPropMap");
		Map<String, Object> tcPropMap = new HashMap<>();
		Field[] fields = bean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> !field.getAnnotation(TCPropertes.class).tcType().equals(type)); // 移除不是table属性
		fieldList.removeIf(field -> !field.getAnnotation(TCPropertes.class).tcProperty().startsWith("d9_")); // 移除不是客制化属性
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
	
	
	/**
	 * 更新缓存list中的数据
	 * @param list
	 * @param bean
	 */
	public static void updateCacheDataList(List<MatMaintainBean> cacheDataList, List<MatMaintainBean> list) {	
		if (CommonTools.isEmpty(list)) {
			return;
		}
		
		for (MatMaintainBean bean : list) {
			Optional<MatMaintainBean> findAny = cacheDataList.stream().filter(e -> e.getUUID().equals(bean.getUUID())).findAny();
			if (findAny.isPresent()) {
				MatMaintainBean findBean = findAny.get();
				cacheDataList.remove(findBean);
				cacheDataList.add(bean);
			} else {
				cacheDataList.add(bean);
			}
		}
		
	}
	
	
	public static List<MatMaintainBean> getList(List<MatMaintainBean> custNodeList, Boolean flag) {
		List<MatMaintainBean> tempList = new ArrayList<MatMaintainBean>();
		if (CommonTools.isNotEmpty(custNodeList)) {
			custNodeList.forEach(bean -> {
				if (flag) {
					if (bean.isDelete()) {
						tempList.add(bean);
					}
				} else {
					if (!bean.isDelete()) {
						tempList.add(bean);
					}
				}

			});
		}

		return tempList;
	}
	
 	public static List<MatMaintainBean> getCustNodes(TableViewer tv) {
		IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
		if (CommonTools.isEmpty(selection)) {
			return null;
		}
		if (selection.size() > 0) {
			return selection.toList();
		}
		return null;
	}
	
	
}
