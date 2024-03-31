package com.hh.tools.renderingHint;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.util.log.Debug;

public class AssignMaterialPropertyBean extends MaterialTableRowPropertyBean {

    private TCComponent com;
    private TCProperty tcProperty;
    private TCPropertyDescriptor tcPropertyDescriptor;

    public AssignMaterialPropertyBean(Control paramControl) {
        super(paramControl);

    }

    public AssignMaterialPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                      Map<?, ?> paramMap) {
        super(paramFormToolkit, paramComposite, paramBoolean, paramMap);
    }


    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        return super.isPropertyModified(arg0);
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
        super.setModifiable(paramBoolean);
    }

    @Override
    public Object getEditableValue() {
        return super.getEditableValue();
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        String materialType = tcProperty.getStringValue();
        String density = com.getProperty("fx8_Density");
        String materialRemark = com.getProperty("fx8_MaterialRemark");
        if (!Utils.isNull(materialType) || !Utils.isNull(density) || !Utils.isNull(materialRemark)) {
            Material material = new Material(materialType, density, materialRemark);
            uploadTableRowComp2Table(material);
        }
    }

    public static List<Material> getMaterialList() {
        return materialList;
    }

    private void uploadTableRowComp2Table(Material material) {
        try {
            TableItem tableItem = new TableItem(table, SWT.NONE);
            materialList.add(material);
            tableItem.setData(material);
            tableItem.setText(new String[]{material.getMaterialType(), material.getDensity(), material.getMaterialRemark()});
            table.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        System.out.println("load TCPropertyDescriptor");
//		tcPropertyDescriptor = paramTCPropertyDescriptor;		
//		com = tcProperty.getTCComponent();
//		TCProperty prop = com.getTCProperty("fx8Material");
//		TCComponent[] coms = prop.getReferenceValueArray();
//		if(coms!=null&&coms.length>0){
//			for (TCComponent tcComponent : coms) {
//				uploadTableRowComp2Table(tcComponent);								
//			}
//		}

    }

    @Override
    public TCProperty getPropertyToSave(TCProperty paramTCProperty) throws Exception {
        if (paramTCProperty == null) {
            return null;
        }
        savable = false;


        if ((!paramTCProperty.isEnabled()) || (!modifiable)) {
            if (Debug.isOn("stylesheet,form,property,properties")) {
                Debug.println("AbstractTableRowPropertyBean: save propName=" + property + " not modifiable, skip.");
            }
            return null;
        }

        return paramTCProperty;
    }

    @Override
    public void setUIFValue(Object paramObject) {
        if (paramObject == null) {
            if (tcPropertyDescriptor != null) {
                try {
                    load(tcPropertyDescriptor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        if (tcProperty != null) {
            try {
                if (materialList.size() > 0) {
                    Material material = materialList.get(0);
                    String materialType = material.getMaterialType();
                    String density = material.getDensity();
                    String materialRemark = material.getMaterialRemark();
                    com.setProperty("fx8_MaterialType", materialType);
                    com.setProperty("fx8_Density", density);
                    com.setProperty("fx8_MaterialRemark", materialRemark);
                }
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.dispose();
    }


    @Override
    protected void addTableRowComp2Table(Material material) {
    	String materialType = material.getMaterialType();      //材料类型
		String materialDensity = material.getDensity();      //密度
		String materialRemark = material.getMaterialRemark();      //备注


        Material[] hasRowDatas = getTableRowComps();
        if (!checkData(hasRowDatas, material)) {
            TableItem tableItem = new TableItem(table, SWT.NONE);
            tableItem.setData(material);
            tableItem.setText(new String[]{materialType, materialDensity, materialRemark});
        } else {
        	Utils.infoMessage(materialType+"|"+materialDensity+"|"+materialRemark+"|"+"该数据已存在，请检查！");
        }
        table.update();
    }

    //返回TRUE，则控件不予附加当前选择
  	private boolean checkData(Material[] hasRowDatas, Material material) {
  		if(material==null){  //为空则无法附加
            return true;
        }
        String oneTableRowStr = getTableRowStr(material);
        if (Utils.isNull(oneTableRowStr)) {
            return true;
        }

        if (hasRowDatas == null || hasRowDatas.length < 1) {
            return false;
        }

        for (Material hascomp : hasRowDatas) {
            String oneHasTableRowStr = getTableRowStr(hascomp);
            if (Utils.isNull(oneHasTableRowStr)) {
                continue;
            }
            if (oneHasTableRowStr.equals(oneTableRowStr)) {
                return true;
            }

        }
        return false;
    }

    private String getTableRowStr(Material material) {
        StringBuffer sb = new StringBuffer();
        try {
            String value = material.getMaterialType();
            if (!Utils.isNull(value)) {
                sb.append(value);
            }
            value = material.getDensity();
            if (!Utils.isNull(value)) {
                sb.append(value);
            }
            value = material.getMaterialRemark();
            if (!Utils.isNull(value)) {
                sb.append(value);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected Material[] getTableRowComps() {
        if (table == null) {
            return null;
        }
        List<Material> list = new ArrayList<>();
        TableItem[] items = table.getItems();
        if (items == null || items.length < 1) {
            return null;

        }
        for (TableItem item : items) {
            Object obj = item.getData();
            if (obj == null) {
                continue;
            }

            if (obj != null && obj instanceof Material) {
                list.add((Material) obj);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        Material[] array = (Material[]) list.toArray(new Material[list.size()]);
        return array;
    }

    @Override
    protected TCComponent getSelectedTableRowComp() {
        if (table == null) {
            return null;
        }
        int selectIndex = table.getSelectionIndex();
        if (selectIndex < 0) {
            return null;
        }
        TableItem item = table.getItem(selectIndex);
        Object obj = item.getData();
        if (obj != null && obj instanceof TCComponent) {
            return (TCComponent) obj;
        }
        return null;
    }

    @Override
    protected void initTable(Composite parentComposite) {

        table = new Table(parentComposite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        table.setHeaderVisible(true);// 设置显示表头
		table.setLinesVisible(true);// 设置显示表格线/*

        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("材料类型");
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("密度");
        tableColumn.setWidth(80);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("备注");
        tableColumn.setWidth(300);

        table.setBackground(parentComposite.getBackground());

    }

}
