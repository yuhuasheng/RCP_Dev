package com.foxconn.mechanism.dtpac.matmaintain.editor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory.MatrixFatory;
import com.foxconn.mechanism.dtpac.matmaintain.dialog.PACMatMaintainDialog;
import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainConstant;
import com.foxconn.mechanism.dtpac.matmaintain.util.TableTools;
import com.foxconn.mechanism.jurisdiction.MyComboBoxViewerCellEditor;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.Registry;

public class MyEditingSupport extends EditingSupport {
	
	private Shell shell;
	private TableViewer tv;
	private int index; // 列名索引
	private String columnName;
	private TableItem curTableItem = null; // 当前table行
	private int selectIndex = -1; // 当前选中行索引
	private ComboBoxViewerCellEditor typeCombox = null;
	private ComboBoxViewerCellEditor matCombox = null;
	private Registry reg = null;
	private PACMatMaintainDialog dialog = null;
	private String curType = null;

	
	public MyEditingSupport(Shell shell, ColumnViewer viewer, int index, String columnName, Registry reg, PACMatMaintainDialog dialog) {
		super(viewer);
		this.shell = shell;
		this.tv = (TableViewer) viewer;
		this.index = index;
		this.columnName = columnName;
		this.reg = reg;
		this.dialog = dialog;
		
//		if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.TYPE))) {
//			typeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
//			typeCombox.setLabelProvider(new LabelProvider());
//			typeCombox.setContentProvider(new ArrayContentProvider());
//		} else if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.MATERIAL))) {
//			matCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
//			matCombox.setLabelProvider(new LabelProvider());
//			matCombox.setContentProvider(new ArrayContentProvider());
//		}
		
		typeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		typeCombox.setLabelProvider(new LabelProvider());
		typeCombox.setContentProvider(new ArrayContentProvider());
		
		matCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		matCombox.setLabelProvider(new LabelProvider());
		matCombox.setContentProvider(new ArrayContentProvider());
	}
	
	
	@Override
	protected boolean canEdit(Object obj) {
		if (dialog.getSelectBomLine().hasChildren()) {
			return false;
		}
		if (index == 1) {
			return false;
		}
		try {
			MatMaintainBean bean = (MatMaintainBean) obj;
			String partType = dialog.getPartType();
			String matType = dialog.getCurMatType();
			String matName = dialog.getCurMatName();
			String type = bean.getType();
			String material = bean.getMaterial();
			List<ExcelBean> matBeanList = dialog.getMatBeanList();
			
			
			Optional<ExcelBean> findAny = matBeanList.stream().filter(e -> e.getPartType().equals(partType) && e.getMatType().equals(matType) && e.getMatName().equals(matName) 
					&& e.getType().equals(type) && e.getMaterial().contains(material)).findAny();
			if (!findAny.isPresent()) {
				System.out.println("==>> partType: " + partType + ", matType: " + matType + ", matName: " + matName + ", type: " + type + ", material: " + material + ", 匹配记录失败");
				return false;				
			}
			
			List<ExcelBean> matCostBeanList = dialog.getMatCostBeanList();
			Optional<ExcelBean> findAny2 = matCostBeanList.stream().filter(e -> S2TTransferUtil.toTraditionnalString(e.getMaterial()).equals(material)).findAny();
			if (!findAny2.isPresent()) {
				System.out.println("material: " + material + ", 匹配材料成本因子失败");
				return false;
			}
			
			
			ExcelBean excelBean = findAny.get();
			ExcelBean excelBean2 = findAny2.get();
			excelBean.setCostFactor(excelBean2.getCostFactor()); // 重新设置成本计算因子
			
			Field[] fields = excelBean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			fieldList.removeIf(field -> CommonTools.isEmpty(field.getAnnotation(TCPropertes.class).columnName()));
			fieldList.removeIf(field -> !columnName.equals(S2TTransferUtil.toTraditionnalString(field.getAnnotation(TCPropertes.class).columnName()))); // 移除不是当先选中列名的记录
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class).cell() != index); // 移除cell的值和index不一致的记录
			
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
				int cell = tcProp.cell();
				if (index == cell) {
					if (field.getType() == int.class) {
						return tcProp.canEditor();
					} else if (field.getType() == String.class) {
						Object value = field.get(excelBean); // 获取值
						String var = CommonTools.replaceBlank(value.toString());
						if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.COST))) { // 判断列名是否为物料成本
							if (CommonTools.isEmpty(excelBean.getCostFactor())) { // 判断当前行的成本因子是否为空,为空则设置物料成本可以编辑
								return true;
							}
						}
						
						if ("/".equals(var)) {
							return false;
						} else {
							return tcProp.canEditor();
						}
					}		
					
				}				
			}		
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	protected CellEditor getCellEditor(Object obj) {
		System.out.println("==>> current columnName is: " + columnName);
		MatMaintainBean bean = (MatMaintainBean) obj;		
		if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.TYPE))) {
			setCurType(bean.getType());
			typeCombox.setInput(dialog.getTypeList());
			return typeCombox;
		} else if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.MATERIAL))) {
			String partType = dialog.getPartType();
			String matType = dialog.getCurMatType();
			String matName = dialog.getCurMatName();
			String type = bean.getType();
			List<String> matList = dialog.getLovValue(partType, matType, matName, type);
			if (CommonTools.isEmpty(matList)) {
				TCUtil.errorMsgBox(reg.getString("MatErr.MSG"), reg.getString("ERROR.MSG"));	
			} else {
				dialog.setMatList(matList);
				matCombox.setInput(dialog.getMatList());
				return matCombox;
			}
			
		}
		return new TextCellEditor((Composite) getViewer().getControl(), SWT.NONE);
	}
	
	
	@Override
	protected Object getValue(Object obj) {
		curTableItem = tv.getTable().getSelection()[0];
		selectIndex = tv.getTable().getSelectionIndex();
		return tv.getTable().getSelection()[0].getText(index);
	}
	
	
	@Override
	protected void setValue(Object obj, Object obj1) {
		String value = CommonTools.replaceBlank(obj1.toString().trim());
		MatMaintainBean curBean = (MatMaintainBean) obj;
//		MatMaintainBean curData = (MatMaintainBean) curTableItem.getData();
//		if (!validateLegal(curBean, value)) { // 校验某些列的合法性
//			return;
//		}; 
		curTableItem.setText(index, value);	
				
		String partType = dialog.getPartType();
		String matType = dialog.getCurMatType();
		String matName = dialog.getCurMatName();
		if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.TYPE))) {
			String type = value;
			System.out.println("==>> type: " + type);
			System.out.println("==>> lastType: " + getCurType());
			if (!getCurType().equals(type)) {
				boolean flag = MessageDialog.openQuestion(null, reg.getString("INFORMATION.MSG"), reg.getString("ComboSwitch.Warn").split("\\&")[0] + columnName 
						+ reg.getString("ComboSwitch.Warn").split("\\&")[1] + type + " " + reg.getString("ComboSwitch.Warn").split("\\&")[2]);
				if (flag) {
					setCurType(type);					
					List<String> matList = dialog.getLovValue(partType, matType, matName, type);
					if (CommonTools.isEmpty(matList)) {
						TCUtil.errorMsgBox(S2TTransferUtil.toTraditionnalString("当前选中的行: " + index) + ", "+ reg.getString("TypeErr.MSG"), reg.getString("ERROR.MSG"));
						return;
					}
					
					dialog.setMatList(matList);
					matCombox.setInput(dialog.getMatList());
					curTableItem.setText(index + 1, matList.get(0));
					curBean.setMaterial(matList.get(0));
					if (!reShowCalculatColumn(curBean, type, curBean.getMaterial())) {
						return;
					};
				} else {
					curTableItem.setText(index, getCurType());
					value = getCurType();
				}
			}
		} else if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.MATERIAL))) {
			String material = value;	
			if (!reShowCalculatColumn(curBean, curBean.getType(), material)) {
				return;
			};
		} 
		
//		else if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.COST))) { // 当前编辑的单元格为物料成本
//			if (!validateLegal(curBean, value)) { // 校验某些列的合法性
//				return;
//			};
//		}

		TableTools.updateData(curBean, index, value);		
		TableTools.updateCacheDataList(dialog.getCacheDataList(), new ArrayList<MatMaintainBean>() {{
			add(curBean);
		}}); // 将更新的数据同步保存到缓存中
		
		
		if (!columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.COST))) { // 当前选中行不是物料成本才触发计算物料成本
			curBean.setCost(TableTools.getMatCost(dialog, curBean)); // 获取计算后的物料成本
			resetMatCostColumn(curBean); // 重新设置物料成本列值
		}
		
		try {
			TableTools.updateTotalCost((List<MatMaintainBean>) tv.getInput(), dialog.getSelectBomLine(), dialog.getTotalMatCostLabel(), dialog.getTotalMatCostText()); // 重新设置总物料成本文本框的值
		} catch (TCException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * 重新展示用量计算因子、用量、物料成本因子等列
	 * @param curBean
	 * @return
	 */
	public boolean reShowCalculatColumn(MatMaintainBean curBean, String type, String material) {
		String partType = dialog.getPartType();
		String matType = dialog.getCurMatType();
		String matName = dialog.getCurMatName();
		
		if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.TYPE))) { // 当点击的是类别列时，需要将长，宽，高等参数清空
			curBean.setLength("");
			curBean.setWidth("");
			curBean.setHeight("");
			curBean.setThickness("");
			curBean.setDensity("");
			curBean.setWeight("");
		}
		
		
		List<ExcelBean> matBeanList = dialog.getMatBeanList();
		Optional<ExcelBean> findAny = matBeanList.stream().filter(e -> e.getPartType().equals(partType) && e.getMatType().equals(matType) && e.getMatName().equals(matName) 
				&& e.getType().equals(type) && e.getMaterial().contains(material)).findAny();
		if (!findAny.isPresent()) {
			TCUtil.errorMsgBox(S2TTransferUtil.toTraditionnalString("Part Type: " + partType + ", 物料类型: " + matType + ", 物料名称: " + matName + ", 类型: " + type + ", 材质: " + material + ", 匹配材料用量计算公式失败"), reg.getString("ERROR.MSG"));
			return false;				
		}
		
		List<ExcelBean> matCostBeanList = dialog.getMatCostBeanList();
		Optional<ExcelBean> findAny2 = matCostBeanList.stream().filter(e -> S2TTransferUtil.toTraditionnalString(e.getMaterial()).equals(material)).findAny();
		if (!findAny2.isPresent()) {
			TCUtil.errorMsgBox(S2TTransferUtil.toTraditionnalString("材质: " + material + ", 匹配材料成本因子失败"), reg.getString("ERROR.MSG"));
			return false;
		}
		
		try {
			if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.TYPE))) {
				curBean.setType(type);
			} else if (columnName.equals(S2TTransferUtil.toTraditionnalString(MatMaintainConstant.MATERIAL))) {
				curBean.setMaterial(material);
			}			
			
			TableTools.setDisableEditorColumnValue(curBean, matBeanList); // 设置不能编列的属性值
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
			TCUtil.errorMsgBox(e1.getLocalizedMessage(), reg.getString("ERROR.MSG"));
			return false;
		} 
		
		ExcelBean excelBean = findAny.get();	
		ExcelBean excelBean2 = findAny2.get();
		
		Field[] fields = excelBean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> CommonTools.isEmpty(field.getAnnotation(TCPropertes.class).columnName()));
		for (Field field : fieldList) {
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			String columnName = tcProp.columnName();
			int cell = tcProp.cell();
			
			if (columnName.equals(MatMaintainConstant.LENGTH)) {
				curTableItem.setText(cell, curBean.getLength());
			}
			
			else if (columnName.equals(MatMaintainConstant.WIDTH)) {
				curTableItem.setText(cell, curBean.getWidth());
			}
			
			else if (columnName.equals(MatMaintainConstant.HEIGHT)) {
				curTableItem.setText(cell, curBean.getHeight());
			}
			
			else if (columnName.equals(MatMaintainConstant.THICKNESS)) {
				curTableItem.setText(cell, curBean.getThickness());
			}
			
			else if (columnName.equals(MatMaintainConstant.DENSITY)) {
				curTableItem.setText(cell, curBean.getDensity());
			}
			
			else if (columnName.equals(MatMaintainConstant.WEIGHT)) {
				curTableItem.setText(cell, curBean.getWeight());
			}
			
			else if (columnName.equals(MatMaintainConstant.CALCULLATIONUNIT)) {
				curTableItem.setText(cell, excelBean.getCalcullationUnit());
				curBean.setCalcullationUnit(excelBean.getCalcullationUnit());				
			} else if (columnName.equals(MatMaintainConstant.USAGECALCULATION)) {
				curTableItem.setText(cell, excelBean.getUsageCalculation());
				curBean.setUsageCalculation(excelBean.getUsageCalculation());
			} else if (columnName.equals(MatMaintainConstant.COSTFACTOR)) {
				curTableItem.setText(cell, excelBean2.getCostFactor());
				curBean.setCostFactor(excelBean2.getCostFactor());
			}
		}		
		
		return true;
	}
	
	/**
	 * 重新设置物料成本列值
	 * @param curBean
	 */
	public void resetMatCostColumn(MatMaintainBean curBean) {
		Field[] fields = curBean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> CommonTools.isEmpty(field.getAnnotation(TCPropertes.class).columnName()));
		Optional<Field> findAny = fieldList.stream().filter(e -> {
			e.setAccessible(true);
			TCPropertes tcProp = e.getAnnotation(TCPropertes.class);
			return tcProp.columnName().equals(MatMaintainConstant.COST);
		}).findAny();
		
		if (findAny.isPresent()) {
			Field field = findAny.get();
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			int cell = tcProp.cell();
			curTableItem.setText(cell, curBean.getCost()); // 设置物料成本列值
		}
	}
	
	
	/**
	 * 校验填写的内容是否合法
	 * @return
	 */

	public boolean validateLegal(MatMaintainBean bean, String str) {
		Field[] fields = bean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> CommonTools.isEmpty(field.getAnnotation(TCPropertes.class).columnName()));
		fieldList.removeIf(field -> !columnName.equals(S2TTransferUtil.toTraditionnalString(field.getAnnotation(TCPropertes.class).columnName()))); // 移除不是当先选中列名的记录
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class).required() == false); // 移除不是需要校验的记录
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class).cell() != index); // 移除cell的值和index不一致的记录
		for (Field field : fieldList) {
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			int cell = tcProp.cell();
			if (cell == index) {
				if (!CommonTools.isNumeric(str)) { // 判断填写的是否为数字
					TCUtil.errorMsgBox(reg.getString("WriteErr1.MSG").split("\\&")[0] + (selectIndex + 1) + reg.getString("WriteErr1.MSG").split("\\&")[1] + columnName + "," 
							+ reg.getString("WriteErr1.MSG").split("\\&")[2], reg.getString("ERROR.MSG"));
					return false;
				}				
				return true;
			}
		}
		
		return true;
	}
	
		
	
	
	public String getCurType() {
		return curType;
	}


	public void setCurType(String curType) {
		this.curType = curType;
	}	
}
