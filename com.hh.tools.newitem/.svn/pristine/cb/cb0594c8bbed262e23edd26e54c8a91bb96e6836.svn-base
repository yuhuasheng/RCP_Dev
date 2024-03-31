package com.hh.tools.renderingHint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class VendorFilterPropertyBean extends AbstractPropertyBean<Object> {

    private TCComponent component;
    private TCProperty tcProperty;
    private TCComponentType componentType;
    private String typeName;
    private TCPropertyDescriptor tcPropertyDescriptor;
    private FormToolkit toolkit;
    private Composite composite;
    private boolean isModified = false;
    private static LOVComboBox vendorComBox = null;
    private Button showButton = null;
    private static Table vendorTable = null;
    private static Table rejectTable = null;
    private Button applyButton = null;
    public static Map<TableItem, TableEditor> rejectTableControls = new HashMap<TableItem, TableEditor>();
    public static Map<TableItem, TableEditor> vendorTableControls = new HashMap<TableItem, TableEditor>();
    public static List<TCComponent> list = new ArrayList<>();
    private static TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
    private SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-M-dd");

    public VendorFilterPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public VendorFilterPropertyBean(FormToolkit paramFormToolkit, Composite parentComposite, boolean paramBoolean,
                                    Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(parentComposite);
    }


    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());

        initVendorTable(this.composite);

        GridLayout gridLayout = new GridLayout(4, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        Label supplierLabel = new Label(this.composite, SWT.NONE);
        supplierLabel.setText("Supplier:");
        supplierLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        vendorComBox = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        vendorComBox.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
        vendorComBox.setSize(168, 30);
        loadVendor();

        showButton = new Button(this.composite, SWT.NONE);
        showButton.setText("Show Category");
//		showButton.computeSize(80, 25);
        showButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        applyButton = new Button(this.composite, SWT.NONE);
        applyButton.setText("Apply");
//		addButton.computeSize(80, 25);
        applyButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        initRejectTable(this.composite);

        addListeners();
        setControl(this.composite);

    }

    private void loadVendor() {
        List key = new ArrayList();
        List value = new ArrayList();
        key.add(Utils.getTextValue("fx8_MfrStatus"));
        value.add("Approved");

        String[] keyArray = new String[key.size()];
        key.toArray(keyArray);
        String[] valueArray = new String[value.size()];
        value.toArray(valueArray);
        List<InterfaceAIFComponent> supplierList = Utils.search("__FX_FindMfr", keyArray, valueArray);
        if (supplierList.size() > 0) {
            try {
                for (InterfaceAIFComponent interfaceAIFComponent : supplierList) {
                    vendorComBox.addItem(interfaceAIFComponent.getProperty("object_name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void addListeners() {
        showButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                System.out.println("caterory==" + vendorComBox.getSelectedString());
                String vendor = vendorComBox.getSelectedString();
                if (!Utils.isNull(vendor)) {
                    String[] keys = new String[]{"Mfr"};
                    String[] values = new String[]{vendor};
                    List<InterfaceAIFComponent> EDACompList = Utils.search("__FX_FindEDAComp", keys, values);
                    if (EDACompList.size() > 0) {
                        try {
                            rejectTable.removeAll();
                            if (rejectTableControls.size() > 0) {
                                TableItem tableItem = null;
                                TableEditor editor = null;
                                Iterator<Entry<TableItem, TableEditor>> iterator = rejectTableControls.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Entry<TableItem, TableEditor> entry = iterator.next();
                                    TableItem tableItem2 = entry.getKey();
                                    if (!tableItem2.isDisposed()) {
                                        tableItem = entry.getKey();
                                        editor = entry.getValue();
                                        break;

                                    }

                                }
                                if (editor != null) {
                                    editor.getEditor().dispose();
                                    editor.dispose();
                                    rejectTableControls.remove(tableItem);
                                }
                            }
                            rejectTable.update();
                            List<TCComponentItemRevision> list = new ArrayList<>();
                            for (int i = 0; i < EDACompList.size(); i++) {
                                TCComponentItem item = (TCComponentItem) EDACompList.get(i);
                                TCComponentItemRevision itemRev = item.getLatestItemRevision();
                                String code = itemRev.getProperty("fx8_CommodityCode");
                                if (!Utils.isNull(code)) {
                                    list.add(itemRev);
                                }
                            }
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    TCComponentItemRevision itemRev = (TCComponentItemRevision) list.get(i);
                                    String code = itemRev.getProperty("fx8_CommodityCode");

                                    TableItem item = new TableItem(rejectTable, 0);
                                    item.setData(itemRev);
                                    TableEditor editor = new TableEditor(rejectTable);
                                    rejectTableControls.put(item, editor);
                                    final Button check = new Button(rejectTable, SWT.CHECK);
                                    check.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

                                    check.pack();
                                    editor.minimumWidth = check.getSize().x;
                                    editor.horizontalAlignment = SWT.CENTER;
                                    editor.setEditor(check, item, 1);

                                    item.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
                                        public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
                                        	//editor.dispose();当table刷新后，旧的控件清除掉。否则item remove后控件还会在原位
                                            check.dispose();
                                        }
                                    });
                                    item.setText(new String[]{String.valueOf(i + 1), null, code, itemRev.getProperty("item_id")});
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        applyButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                int count = rejectTable.getItemCount();
                System.out.println("行数=="+count);
                if (count > 0) {

                    String codeList = "";
                    String idList = "";
                    StringBuffer codeBuffer = new StringBuffer();
                    StringBuffer idBuffer = new StringBuffer();
                    for (int i = 0; i < count; i++) {
                        TableItem tableItem = rejectTable.getItem(i);
                        Iterator<Entry<TableItem, TableEditor>> iterator = rejectTableControls.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Entry<TableItem, TableEditor> entry = iterator.next();
                            TableItem tableItem2 = entry.getKey();
                            if (!tableItem2.isDisposed() && tableItem2 == tableItem) {
                                TableEditor editor = entry.getValue();
                                Button button = (Button) editor.getEditor();
                                if (button.getSelection()) {
                                    String commodityCode = tableItem.getText(2);
                                    String itemId = tableItem.getText(3);
                                    codeBuffer.append(commodityCode + ",");
                                    idBuffer.append(itemId + ",");
                                }

                            }
                        }
                    }
                    if (codeBuffer.toString().endsWith(",")) {
                        codeList = codeBuffer.toString().substring(0, codeBuffer.toString().length() - 1);
                        idList = idBuffer.toString().substring(0, idBuffer.toString().length() - 1);
                        String[] idArr = idList.split(",");

                        //验证vendorTable上已添加该物料
                        if (vendorTableControls.size() > 0) {
                            Iterator<Entry<TableItem, TableEditor>> iterator = vendorTableControls.entrySet().iterator();
                            try {
                                while (iterator.hasNext()) {
                                    Entry<TableItem, TableEditor> entry = iterator.next();
                                    TableItem tableItem = entry.getKey();
                                    String idListStr = tableItem.getText(4);
                                    String[] vendorIdArr = idListStr.split(",");
                                    TCComponentItemRevision itemRev = null;
                                    boolean flag = false;
                                    out:
                                    for (String id : idArr) {
                                        for (String vendorId : vendorIdArr) {
                                            if (id.equals(vendorId)) {
                                                flag = true;
                                                TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("FX8_Mfr");
                                                TCComponentItem[] items = itemType.findItems(id);
                                                itemRev = items[0].getLatestItemRevision();
                                                break out;
                                            }
                                        }
                                    }
                                    if (flag) {
                                    	Utils.infoMessage("被拒绝的物料列表中已包含commodityCode为"+itemRev.getProperty("fx8_CommodityCode")+"的物料，无法继续添加，请检查！");
                                        return;
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        final TableItem item = new TableItem(vendorTable, 0);
                        item.setData(idList);
                        final TableEditor editor = new TableEditor(vendorTable);
                        vendorTableControls.put(item, editor);
                        final Button button = new Button(vendorTable, SWT.NONE);
                        button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
                        button.setText("delete");
                        button.pack();
                        editor.minimumWidth = button.getSize().x;
                        editor.horizontalAlignment = SWT.CENTER;
                        editor.setEditor(button, item, 3);

                        item.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
                            public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
                            	//editor.dispose();当table刷新后，旧的控件清除掉。否则item remove后控件还会在原位
                                button.dispose();
                            }
                        });
                        item.setText(new String[]{vendorComBox.getSelectedString(), codeList, simpleFormat.format(new Date()), null, idList});
                        vendorTable.update();

                        button.addListener(SWT.Selection, new Listener() {

                            @Override
                            public void handleEvent(Event arg0) {

                                Iterator<Entry<TableItem, TableEditor>> iterator = vendorTableControls.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Entry<TableItem, TableEditor> entry = iterator.next();
                                    TableItem tableItem2 = entry.getKey();
                                    if (!tableItem2.isDisposed() && tableItem2 == item) {
                                        TableEditor editor = entry.getValue();
                                        TableItem[] tableItems = vendorTable.getItems();
                                        for (int i = 0; i < tableItems.length; i++) {
                                            if (item == tableItems[i] && editor != null) {
                                                editor.getEditor().dispose();
                                                editor.dispose();
                                                iterator.remove();
                                                vendorTable.remove(i);
                                                isModified = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (vendorTableControls.size() > 0) {
                                    Iterator<Entry<TableItem, TableEditor>> it = vendorTableControls.entrySet().iterator();
                                    while (it.hasNext()) {
                                        Entry<TableItem, TableEditor> entry = it.next();
                                        TableEditor tableEditor = entry.getValue();
                                        tableEditor.layout();
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });

    }

    private void initVendorTable(Composite composite) {
        vendorTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        vendorTable.setHeaderVisible(true);// 设置显示表头
		vendorTable.setLinesVisible(true);// 设置显示表格线/*

        TableColumn tableColumn = new TableColumn(vendorTable, SWT.NONE);
        tableColumn.setText("Supplier Name");
        tableColumn.setWidth(150);

        tableColumn = new TableColumn(vendorTable, SWT.NONE);
        tableColumn.setText("Rejected Commodity Code List");
        tableColumn.setWidth(300);

        tableColumn = new TableColumn(vendorTable, SWT.NONE);
        tableColumn.setText("CreatedTime");
        tableColumn.setWidth(150);

        tableColumn = new TableColumn(vendorTable, SWT.NONE);
        tableColumn.setText("Action");
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(vendorTable, SWT.NONE);
        tableColumn.setText("");
        tableColumn.setWidth(0);

        vendorTable.setBackground(composite.getBackground());

        GridData localGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 10, 1);
        localGridData.widthHint = 680;
        localGridData.heightHint = 150;
        vendorTable.setLayoutData(localGridData);

    }

    private void initRejectTable(Composite composite) {
        rejectTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        rejectTable.setHeaderVisible(true);// 设置显示表头
		rejectTable.setLinesVisible(true);// 设置显示表格线/*

        TableColumn tableColumn = new TableColumn(rejectTable, SWT.NONE);
        tableColumn.setText("No");
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(rejectTable, SWT.NONE);
        tableColumn.setText("Reject");
        tableColumn.setWidth(80);

        tableColumn = new TableColumn(rejectTable, SWT.NONE);
        tableColumn.setText("Commodity Code");
        tableColumn.setWidth(300);

        tableColumn = new TableColumn(rejectTable, SWT.NONE);
        tableColumn.setText("");
        tableColumn.setWidth(0);

        rejectTable.setBackground(composite.getBackground());

        GridData localGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 10, 1);
        localGridData.widthHint = 450;
        localGridData.heightHint = 150;
        rejectTable.setLayoutData(localGridData);
    }

    @Override
    public String getProperty() {
        System.out.println("component==" + component);
        if (isModified && component != null) {
            doSave();
        }
        return super.getProperty();
    }

    private void doSave() {
        if (component != null) {
            try {
                TCProperty pro = component.getTCProperty("fx8_MfrPNCommodityCodeTable");
                list = getSelectedList();
                if (list.size() > 0) {
                    pro.setPropertyArrayData(list.toArray());
                } else {
                    pro.setReferenceValueArray(null);
                }
                component.setTCProperty(pro);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isPropertyModified(TCComponent arg0) throws Exception {
        System.out.println("TCComponent isPropertyModified == " + isModified);
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        System.out.println("TCProperty isPropertyModified == " + isModified);
        return isModified;
    }

    @Override
    public Object getEditableValue() {
        return null;
    }

    public static List<TCComponent> getSelectedList() {
        list.clear();
        TableItem[] tableItems = vendorTable.getItems();
        try {
            if (tableItems != null && tableItems.length > 0) {
                for (TableItem tableItem : tableItems) {
                    String supplierName = tableItem.getText(0);
                    String codeList = tableItem.getText(1);
                    String createTime = tableItem.getText(2);
                    TCComponent com = CreateObject.createCom(session, "FX8_ManufacturerFilterTable");
                    com.setProperty("fx8_CodeList", codeList);
                    com.setProperty("fx8_MfrName", supplierName);
                    com.setProperty("fx8_CreateTime", createTime);
                    list.add(com);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        return arg0;
    }

    @Override
    public TCProperty getPropertyToSave(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("getPropertyToSave");
        return super.getPropertyToSave(arg0);
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
        componentType = arg0.getTCComponent().getTypeComponent();
        property = arg0.getPropertyName();
        descriptor = arg0.getPropertyDescriptor();
        tcProperty = arg0;
        component = arg0.getTCComponent();
        typeName = descriptor.getTypeComponent().getTypeName();
//		System.out.println("typeName=="+typeName+"==componentType=="+componentType);
        TCComponent[] coms = tcProperty.getReferenceValueArray();
        if (coms != null && coms.length > 0) {
            for (TCComponent tcComponent : coms) {
                uploadTableRowComp2Table(tcComponent);
            }
        }
        if (component.isCheckedOut()) {
            vendorComBox.setEnabled(true);
            showButton.setEnabled(true);
            applyButton.setEnabled(true);
        } else {
            vendorComBox.setEnabled(false);
            showButton.setEnabled(false);
            applyButton.setEnabled(false);
        }
        isModified = true;
    }

    private void uploadTableRowComp2Table(TCComponent tcComponent) {
        try {
            String supplierNameStr = tcComponent.getProperty("fx8_MfrName");
            String codeListStr = tcComponent.getProperty("fx8_CodeList");
            String createTimeStr = tcComponent.getProperty("fx8_CreateTime");

            final TableItem tableItem = new TableItem(vendorTable, SWT.NONE);
            tableItem.setData(tcComponent);

            final TableEditor editor = new TableEditor(vendorTable);
            vendorTableControls.put(tableItem, editor);
            final Button button = new Button(vendorTable, SWT.NONE);
            button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
            button.setText("delete");
            button.pack();
            editor.minimumWidth = button.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(button, tableItem, 3);
            if (component != null && component.isCheckedOut()) {
                button.setEnabled(true);
            } else if (component != null && !component.isCheckedOut()) {
                button.setEnabled(false);
            }
            tableItem.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
                public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
                	//editor.dispose();当table刷新后，旧的控件清除掉。否则item remove后控件还会在原位
                    button.dispose();
                }
            });

            button.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event arg0) {

                    Iterator<Entry<TableItem, TableEditor>> iterator = vendorTableControls.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<TableItem, TableEditor> entry = iterator.next();
                        TableItem tableItem2 = entry.getKey();
                        if (!tableItem2.isDisposed() && tableItem2 == tableItem) {
                            TableEditor editor = entry.getValue();
                            TableItem[] tableItems = vendorTable.getItems();
                            for (int i = 0; i < tableItems.length; i++) {
                                if (tableItem == tableItems[i] && editor != null) {
                                    editor.getEditor().dispose();
                                    editor.dispose();
                                    iterator.remove();
                                    vendorTable.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    if (vendorTableControls.size() > 0) {
                        Iterator<Entry<TableItem, TableEditor>> it = vendorTableControls.entrySet().iterator();
                        while (it.hasNext()) {
                            Entry<TableItem, TableEditor> entry = it.next();
                            TableEditor tableEditor = entry.getValue();
                            tableEditor.layout();
                        }
                    }
                }
            });
            tableItem.setText(new String[]{supplierNameStr, codeListStr, createTimeStr, null});

            isModified = true;
            vendorTable.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        // TODO Auto-generated method stub

    }


    @Override
    public void setModifiable(boolean arg0) {
        System.out.println("setModifiable == " + arg0);
        modifiable = arg0;
    }


    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        super.dispose();
    }


}
