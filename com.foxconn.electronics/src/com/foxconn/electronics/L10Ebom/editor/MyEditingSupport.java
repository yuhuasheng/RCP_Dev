package com.foxconn.electronics.L10Ebom.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.foxconn.electronics.L10Ebom.combox.MyComboBoxViewerCellEditor;
import com.foxconn.electronics.L10Ebom.constant.ApplyFormConstant;
import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.util.TableTools;
import com.foxconn.tcutils.util.TCPropertes;

public class MyEditingSupport extends EditingSupport {	
	
	private TableViewer tv;
	private Composite allComposite = null;
	private String tablePropName = null;
	private String title;
	private int index;
	private String columnName;
	private List<String> editorControllerList = null;
	private String groupName = null;
	private TableItem currentTableItem = null; // 当前table行
	private ComboBoxViewerCellEditor finishTypeCombox = null;
	private ComboBoxViewerCellEditor yesNoCombox = null;
	private ComboBoxViewerCellEditor semiTypeCombox = null;
	private ComboBoxViewerCellEditor shipCombox = null;
	private ComboViewer comboViewer = null;
	private ComboBoxViewerCellEditor powerLineTypeCombox = null;
	private ComboBoxViewerCellEditor PCBAInterfaceCombox = null;	
	private ComboBoxViewerCellEditor IsSpeakerCombox = null;
	private ComboBoxViewerCellEditor colorCombox = null;
	private ComboBoxViewerCellEditor wireTypeCombox = null;
	private ComboBoxViewerCellEditor shipSizeCombox = null;
	private ComboBoxViewerCellEditor shipTypeCombox = null;
	
	private List<String> shipList = null;
	private List<String> powerLineTypeList = null;
	private List<String> PCBAInterfaceList = null;
	private List<String> colorList = null;
	private List<String> wireTypeList = null;
	private List<String> shipSizeList = null;
	private List<String> shipTypeList = null;
	
	public MyEditingSupport(ColumnViewer viewer, Composite allComposite, String tablePropName, String title, int index, String columnName, List<String> editorControllerList, String groupName, 
			List<String> shipList, List<String> powerLineTypeList, List<String> PCBAInterfaceList, List<String> colorList) {
		super(viewer);
		this.tv = (TableViewer) viewer;
		this.allComposite = allComposite;
		this.tablePropName = tablePropName;
		this.title = title;
		this.index = index;		
		columnName = columnName.replace("*", "").replace(" ", "");
		this.columnName = columnName;
		this.editorControllerList = editorControllerList;
		this.groupName = groupName;
		this.shipList = shipList;
		this.powerLineTypeList = powerLineTypeList;
		this.PCBAInterfaceList = PCBAInterfaceList;
		this.colorList = colorList;
		if (columnName.equals(ApplyFormConstant.FINISHTYPE)) {
			finishTypeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			finishTypeCombox.setLabelProvider(new LabelProvider());
			finishTypeCombox.setContentProvider(new ArrayContentProvider());
			finishTypeCombox.setInput(ApplyFormConstant.FINISHTYPELOV);
		} else if (columnName.equals(ApplyFormConstant.ISNEWPKGBOM) || columnName.equals(ApplyFormConstant.ISNEWPCBA)) {
			yesNoCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			yesNoCombox.setLabelProvider(new LabelProvider());
			yesNoCombox.setContentProvider(new ArrayContentProvider());
			yesNoCombox.setInput(ApplyFormConstant.YESNOLOV);
		} else if (columnName.equals(ApplyFormConstant.SEMITYPE)) {
			semiTypeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			semiTypeCombox.setLabelProvider(new LabelProvider());
			semiTypeCombox.setContentProvider(new ArrayContentProvider());
			semiTypeCombox.setInput(ApplyFormConstant.SEMITYPELOV);
		} else if (columnName.equals(ApplyFormConstant.SHIP)) {
			shipCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl());
			shipCombox.setLabelProvider(new LabelProvider());
			shipCombox.setContentProvider(new ArrayContentProvider());
			
//			shipCombox.setInput(this.shipList);	
			
//			addListener();
		} else if (columnName.equals(ApplyFormConstant.POWERLINETYPE)) {
			powerLineTypeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl());
			powerLineTypeCombox.setLabelProvider(new LabelProvider());
			powerLineTypeCombox.setContentProvider(new ArrayContentProvider());
//			powerLineTypeCombox.setInput(this.powerLineTypeList);
		} else if (columnName.equals(ApplyFormConstant.PCBAINTERFACE)) {
			PCBAInterfaceCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl());
			PCBAInterfaceCombox.setLabelProvider(new LabelProvider());
			PCBAInterfaceCombox.setContentProvider(new ArrayContentProvider());
//			PCBAInterfaceCombox.setInput(this.PCBAInterfaceList);
		} else if (columnName.equals(ApplyFormConstant.ISSPEAKER)) {
			IsSpeakerCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			IsSpeakerCombox.setLabelProvider(new LabelProvider());
			IsSpeakerCombox.setContentProvider(new ArrayContentProvider());
			IsSpeakerCombox.setInput(ApplyFormConstant.IsSpeakerList);
		} else if (columnName.equals(ApplyFormConstant.COLOR)) {
			colorCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl());
			colorCombox.setLabelProvider(new LabelProvider());
			colorCombox.setContentProvider(new ArrayContentProvider());
//			colorCombox.setInput(this.colorList);
		} else if (columnName.equals(ApplyFormConstant.WIRETYPE)) {
			wireTypeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			wireTypeCombox.setLabelProvider(new LabelProvider());
			wireTypeCombox.setContentProvider(new ArrayContentProvider());
		} else if (columnName.equals(ApplyFormConstant.SHIPSIZE)) {
			shipSizeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl());
			shipSizeCombox.setLabelProvider(new LabelProvider());
			shipSizeCombox.setContentProvider(new ArrayContentProvider());
		} else if (columnName.equals(ApplyFormConstant.SHIPTYPE)) {
			shipTypeCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl());
			shipTypeCombox.setLabelProvider(new LabelProvider());
			shipTypeCombox.setContentProvider(new ArrayContentProvider());
		}
		
//		addListener();
	}
	
	@Override
	protected boolean canEdit(Object obj) {		
		if (index == 0) {
			return false;
		}		
		
		try {
			EBOMApplyRowBean bean = (EBOMApplyRowBean) obj;
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			List<Field> resultList = new ArrayList<Field>();
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
				int cell = tcProp.cell();
				if (index == cell) {
					resultList.add(field);
				}				
			}
			
			Optional<Field> findAny = resultList.stream().filter(field -> field.getType() == boolean.class).findAny();
			if (findAny.isPresent()) {
				return (boolean) findAny.get().get(bean);
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	protected CellEditor getCellEditor(Object obj) {
		if (columnName.equals(ApplyFormConstant.FINISHTYPE)) {
			return finishTypeCombox;
		} else if (columnName.equals(ApplyFormConstant.ISNEWPKGBOM) || columnName.equals(ApplyFormConstant.ISNEWPCBA)) {
			return yesNoCombox;
		} else if (columnName.equals(ApplyFormConstant.SEMITYPE)) {
			return semiTypeCombox;
		} else if (columnName.equals(ApplyFormConstant.SHIP)) {
			shipCombox.setInput(shipList);
			return shipCombox;
		} else if (columnName.equals(ApplyFormConstant.POWERLINETYPE)) {
			powerLineTypeCombox.setInput(powerLineTypeList);
			return powerLineTypeCombox;
		} else if (columnName.equals(ApplyFormConstant.PCBAINTERFACE)) {
			PCBAInterfaceCombox.setInput(PCBAInterfaceList);
			return PCBAInterfaceCombox;
		} else if (columnName.equals(ApplyFormConstant.ISSPEAKER)) {
			return IsSpeakerCombox;
		} else if (columnName.equals(ApplyFormConstant.COLOR)) {
			colorCombox.setInput(colorList);
			return colorCombox;
		} else if (columnName.equals(ApplyFormConstant.WIRETYPE)) {
			wireTypeCombox.setInput(wireTypeList);
			return wireTypeCombox;
		} else if (columnName.equals(ApplyFormConstant.SHIPSIZE)) {
			shipSizeCombox.setInput(shipSizeList);
			return shipSizeCombox;
		} else if (columnName.equals(ApplyFormConstant.SHIPTYPE)) {
			shipTypeCombox.setInput(shipTypeList);
			return shipTypeCombox;
		}
		
		return new TextCellEditor((Composite) getViewer().getControl(), SWT.NONE);
	}
	
	@Override
	protected Object getValue(Object obj) {
		currentTableItem = tv.getTable().getSelection()[0];
		return tv.getTable().getSelection()[0].getText(index);
	}
	
	@Override
	protected void setValue(Object obj, Object obj1) {
		String value = obj1.toString().trim();
		currentTableItem.setText(index, value);
		EBOMApplyRowBean currentData = (EBOMApplyRowBean) currentTableItem.getData();
		TableTools.updateData(currentData, index, value);		
	}
	
	private void addListener() {
		ComboViewer comboViewer = shipCombox.getViewer();
		CCombo cCombo = shipCombox.getViewer().getCCombo();
//		cCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		
        // Add a VerifyListener to the Combo widget to dynamically filter values
//		cCombo.addVerifyListener(new VerifyListener() {
//          @Override
//          public void verifyText(VerifyEvent e) {
//              String inputText = cCombo.getText().substring(0, e.start) + e.text + cCombo.getText().substring(e.end);
//              shipCombox.getViewer().setFilters(new ViewerFilter[] { new ViewerFilter() {
//                  @Override
//                  public boolean select(org.eclipse.jface.viewers.Viewer viewer, Object parentElement, Object element) {
//                      // Add your filtering logic here based on the inputText
//                      return element.toString().toLowerCase().contains(inputText.toLowerCase());
//                  }
//              } });
//          }
//      });
		
		
//		cCombo.addModifyListener(new ModifyListener() {
//
//			@Override
//			public void modifyText(ModifyEvent e) {
//				String filterText = cCombo.getText().toLowerCase();
//				System.out.println(filterText);
//				
//				comboViewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
//	                  @Override
//	                  public boolean select(org.eclipse.jface.viewers.Viewer viewer, Object parentElement, Object element) {
//	                      // Add your filtering logic here based on the inputText
//	                      return element.toString().toLowerCase().contains(filterText.toLowerCase());
//	                  }
//	              } });
//						
//			}
//			
//		});
		
		// 为 ComboViewer 的 Combo 添加 ModifyListener 监听器，以触发过滤操作
		cCombo.addModifyListener(new ModifyListener() {
		    @Override
		    public void modifyText(ModifyEvent e) {
		    	String filterText = cCombo.getText().toLowerCase();
				System.out.println(filterText);
				
				// 在过滤器中，根据输入的文本值来过滤下拉框的选项
				comboViewer.refresh();
		    }
		});
		
		comboViewer.addFilter(new ViewerFilter() {
		    @Override
		    public boolean select(Viewer viewer, Object parentElement, Object element) {
		        if (element instanceof String) {
		            String option = (String) element;
		            if (cCombo != null) {
		            	String filterText = cCombo.getText();
			            return option.toLowerCase().contains(filterText.toLowerCase());
					}		            
		        }
		        return true;
		    }
		});
	}

	public List<String> getShipList() {
		return shipList;
	}

	public void setShipList(List<String> shipList) {
		this.shipList = shipList;
	}

	public List<String> getPowerLineTypeList() {
		return powerLineTypeList;
	}

	public void setPowerLineTypeList(List<String> powerLineTypeList) {
		this.powerLineTypeList = powerLineTypeList;
	}

	public List<String> getPCBAInterfaceList() {
		return PCBAInterfaceList;
	}

	public void setPCBAInterfaceList(List<String> pCBAInterfaceList) {
		PCBAInterfaceList = pCBAInterfaceList;
	}

	public List<String> getColorList() {
		return colorList;
	}

	public void setColorList(List<String> colorList) {
		this.colorList = colorList;
	}
	
	
	public List<String> getWireTypeList() {
		return wireTypeList;
	}

	public void setWireTypeList(List<String> wireTypeList) {
		this.wireTypeList = wireTypeList;
	}

	public List<String> getShipSizeList() {
		return shipSizeList;
	}

	public void setShipSizeList(List<String> shipSizeList) {
		this.shipSizeList = shipSizeList;
	}

	public List<String> getShipTypeList() {
		return shipTypeList;
	}

	public void setShipTypeList(List<String> shipTypeList) {
		this.shipTypeList = shipTypeList;
	}

	public ComboBoxViewerCellEditor getShipCombox() {
		return shipCombox;
	}

	public void setShipCombox(ComboBoxViewerCellEditor shipCombox) {
		this.shipCombox = shipCombox;
	}

	public ComboBoxViewerCellEditor getPowerLineTypeCombox() {
		return powerLineTypeCombox;
	}

	public void setPowerLineTypeCombox(ComboBoxViewerCellEditor powerLineTypeCombox) {
		this.powerLineTypeCombox = powerLineTypeCombox;
	}

	public ComboBoxViewerCellEditor getPCBAInterfaceCombox() {
		return PCBAInterfaceCombox;
	}

	public void setPCBAInterfaceCombox(ComboBoxViewerCellEditor pCBAInterfaceCombox) {
		PCBAInterfaceCombox = pCBAInterfaceCombox;
	}

	public ComboBoxViewerCellEditor getColorCombox() {
		return colorCombox;
	}

	public void setColorCombox(ComboBoxViewerCellEditor colorCombox) {
		this.colorCombox = colorCombox;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	
	
}
