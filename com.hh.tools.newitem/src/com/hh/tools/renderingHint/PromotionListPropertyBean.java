package com.hh.tools.renderingHint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class PromotionListPropertyBean extends AbstractPropertyBean<Object> {

    private TCComponent component;
    private TCProperty tcProperty;
    private TCComponentType componentType;
    private String typeName;
    private TCPropertyDescriptor tcPropertyDescriptor;
    private FormToolkit toolkit;
    private Composite composite;
    private boolean isModified = false;
    private static Text supplierText = null;
    private static Text standardPNText = null;
    private static Text partNumberText = null;
    private Button searchButton = null;
    private static Table promotionTable = null;
    public static List<TCComponent> list = new ArrayList<>();
    public static Map<TableItem, TableEditor> promotionTableControls = new HashMap<TableItem, TableEditor>();
    private static TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();

    public PromotionListPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public PromotionListPropertyBean(FormToolkit paramFormToolkit, Composite parentComposite, boolean paramBoolean,
                                     Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(parentComposite);
    }


    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());

        GridLayout gridLayout = new GridLayout(4, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        Label supplierLabel = new Label(this.composite, SWT.NONE);
        supplierLabel.setText("Supplier:");
        supplierLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        supplierText = new Text(composite, SWT.DROP_DOWN | SWT.BORDER);
        GridData supplierGirdData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
        supplierGirdData.widthHint = 200;
        supplierText.setLayoutData(supplierGirdData);
//		supplierText.setSize(468, 30);	

        Label standardPNLabel = new Label(this.composite, SWT.NONE);
        standardPNLabel.setText("Standard PN:");
        standardPNLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        standardPNText = new Text(composite, SWT.DROP_DOWN | SWT.BORDER);
        GridData standardPNGirdData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
        standardPNGirdData.widthHint = 200;
        standardPNText.setLayoutData(standardPNGirdData);
//		standardPNText.setSize(468, 30);	

        Label partNumberLabel = new Label(this.composite, SWT.NONE);
        partNumberLabel.setText("Part Number:");
        partNumberLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        partNumberText = new Text(composite, SWT.DROP_DOWN | SWT.BORDER);
        GridData partNumberGirdData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
        partNumberGirdData.widthHint = 200;
        partNumberText.setLayoutData(partNumberGirdData);
//		partNumberText.setSize(468, 30);	

        Label spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setText("");
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        searchButton = new Button(this.composite, SWT.NONE);
        searchButton.setText("Search");
        GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
        gridData.widthHint = 80;
//		showButton.computeSize(80, 25);
        searchButton.setLayoutData(gridData);

        initTable(this.composite);

        addListeners();
        setControl(this.composite);

    }


    private void addListeners() {
        searchButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                String supplier = supplierText.getText();
                String standardPN = standardPNText.getText();
                String partNumber = partNumberText.getText();

                List key = new ArrayList();
                List value = new ArrayList();
                if (!Utils.isNull(supplier)) {
                    key.add("Mfr");
                    value.add("*" + supplier + "*");
                }
                if (!Utils.isNull(standardPN)) {
                    key.add("StandardPN");
                    value.add("*" + standardPN + "*");
                }
                if (!Utils.isNull(partNumber)) {
                    key.add("objectName");
                    value.add("*" + partNumber + "*");
                }
                if (Utils.isNull(supplier) && Utils.isNull(standardPN) && Utils.isNull(partNumber)) {
                    key.add("ID");
                    value.add("*");
                }

                String[] keyArray = new String[key.size()];
                key.toArray(keyArray);
                String[] valueArray = new String[value.size()];
                value.toArray(valueArray);
                List<InterfaceAIFComponent> EDACompList = Utils.search("__FX_FindEDAComp", keyArray, valueArray);

                if (EDACompList.size() > 0) {
                    try {
                        promotionTable.removeAll();
                        if (promotionTableControls.size() > 0) {
                            TableItem tableItem = null;
                            TableEditor editor = null;
                            Iterator<Entry<TableItem, TableEditor>> iterator = promotionTableControls.entrySet().iterator();
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
                                promotionTableControls.remove(tableItem);
                            }
                        }
                        promotionTable.update();

                        for (int i = 0; i < EDACompList.size(); i++) {
                            TCComponentItem eadItem = (TCComponentItem) EDACompList.get(i);
                            TCComponentItemRevision itemRev = eadItem.getLatestItemRevision();
                            TCComponent mfrCom = itemRev.getRelatedComponent("fx8_MfrRel");
                            String supplierStr = mfrCom == null ? "" : mfrCom.getProperty("object_name");//供应商		
                            String partNumberStr = itemRev.getProperty("object_name");
                            String standardPNStr = itemRev.getProperty("fx8_StandardPN");
                            String descriptionStr = itemRev.getProperty("fx8_ObjectDesc");
                            String itemID = itemRev.getProperty("item_id");

                            final TableItem item = new TableItem(promotionTable, 0);
                            item.setData(itemRev);
                            TableEditor editor = new TableEditor(promotionTable);
                            promotionTableControls.put(item, editor);
                            final Button button = new Button(promotionTable, SWT.NONE);
                            button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
                            button.setText("delete");
                            button.pack();
                            editor.minimumWidth = button.getSize().x;
                            editor.horizontalAlignment = SWT.CENTER;
                            editor.setEditor(button, item, 4);

                            item.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
                                public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
                                    //editor.dispose();当table刷新后，旧的控件清除掉。否则item remove后控件还会在原位
                                    button.dispose();
                                }
                            });
                            item.setText(new String[]{supplierStr, partNumberStr, standardPNStr, descriptionStr, null, itemID});
                            isModified = true;
                            {
                                button.addListener(SWT.Selection, new Listener() {

                                    @Override
                                    public void handleEvent(Event arg0) {
                                        TableItem tableItem = null;
                                        TableEditor editor = null;
                                        System.out.println("promotionTableControls==" + promotionTableControls.size());
                                        Iterator<Entry<TableItem, TableEditor>> iterator = promotionTableControls.entrySet().iterator();
                                        while (iterator.hasNext()) {
                                            Entry<TableItem, TableEditor> entry = iterator.next();
                                            tableItem = entry.getKey();
                                            if (!tableItem.isDisposed() && tableItem == item) {
                                                editor = entry.getValue();
                                                TableItem[] tableItems = promotionTable.getItems();
                                                for (int i = 0; i < tableItems.length; i++) {
                                                    if (item == tableItems[i] && editor != null) {
                                                        System.out.println("i==" + i);
                                                        editor.getEditor().dispose();
                                                        editor.dispose();
                                                        iterator.remove();
                                                        promotionTable.remove(i);
                                                        isModified = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (promotionTableControls.size() > 0) {
                                            Iterator<Entry<TableItem, TableEditor>> it = promotionTableControls.entrySet().iterator();
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


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void initTable(Composite composite) {
        promotionTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        promotionTable.setHeaderVisible(true);// 设置显示表头
		promotionTable.setLinesVisible(true);// 设置显示表格线/*

        TableColumn tableColumn = new TableColumn(promotionTable, SWT.NONE);
        tableColumn.setText("Supplier Name");
        tableColumn.setWidth(150);

        tableColumn = new TableColumn(promotionTable, SWT.NONE);
        tableColumn.setText("Part Number");
        tableColumn.setWidth(150);

        tableColumn = new TableColumn(promotionTable, SWT.NONE);
        tableColumn.setText("Standard PN");
        tableColumn.setWidth(150);

        tableColumn = new TableColumn(promotionTable, SWT.NONE);
        tableColumn.setText("Description");
        tableColumn.setWidth(150);

        tableColumn = new TableColumn(promotionTable, SWT.NONE);
        tableColumn.setText("Action");
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(promotionTable, SWT.NONE);
        tableColumn.setText("");
        tableColumn.setWidth(0);

        promotionTable.setBackground(composite.getBackground());

        GridData localGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 10, 1);
        localGridData.widthHint = 680;
        localGridData.heightHint = 150;
        promotionTable.setLayoutData(localGridData);

    }

    @Override
    public String getProperty() {
        System.out.println("getProperty");
        if (isModified && component != null) {
            doSave();
        }
        return super.getProperty();
    }

    private void doSave() {
        if (component != null) {
            try {
                TCProperty pro = component.getTCProperty("fx8_PromotionListTable");
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
        System.out.println("isPropertyModified == " + isModified);
        return false;
    }


    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        return isModified;
    }

    @Override
    public Object getEditableValue() {
        return null;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        return arg0;
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
            supplierText.setEditable(true);
            standardPNText.setEditable(true);
            partNumberText.setEditable(true);
            searchButton.setEnabled(true);
        } else {
            supplierText.setEditable(false);
            standardPNText.setEditable(false);
            partNumberText.setEditable(false);
            searchButton.setEnabled(false);
        }
        isModified = false;
    }

    private void uploadTableRowComp2Table(TCComponent tcComponent) {
        try {
            String supplierNameStr = tcComponent.getProperty("fx8_Mfr");
            String partNumberStr = tcComponent.getProperty("fx8_PartNumber");
            String standardPNStr = tcComponent.getProperty("fx8_StandardPN");
            String descriptionStr = tcComponent.getProperty("fx8_ObjectDesc");

            final TableItem tableItem = new TableItem(promotionTable, SWT.NONE);
            tableItem.setData(tcComponent);

            final TableEditor editor = new TableEditor(promotionTable);
            promotionTableControls.put(tableItem, editor);
            final Button button = new Button(promotionTable, SWT.NONE);
            button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
            button.setText("delete");
            button.pack();
            editor.minimumWidth = button.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(button, tableItem, 4);
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
                    TableItem tableItem2 = null;
                    TableEditor editor = null;
                    System.out.println("promotionTableControls==" + promotionTableControls.size());
                    Iterator<Entry<TableItem, TableEditor>> iterator = promotionTableControls.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<TableItem, TableEditor> entry = iterator.next();
                        tableItem2 = entry.getKey();
                        if (!tableItem2.isDisposed() && tableItem2 == tableItem) {
                            editor = entry.getValue();
                            TableItem[] tableItems = promotionTable.getItems();
                            for (int i = 0; i < tableItems.length; i++) {
                                if (tableItem == tableItems[i] && editor != null) {
                                    System.out.println("i==" + i);
                                    editor.getEditor().dispose();
                                    editor.dispose();
                                    iterator.remove();
                                    promotionTable.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    if (promotionTableControls.size() > 0) {
                        Iterator<Entry<TableItem, TableEditor>> it = promotionTableControls.entrySet().iterator();
                        while (it.hasNext()) {
                            Entry<TableItem, TableEditor> entry = it.next();
                            TableEditor tableEditor = entry.getValue();
                            tableEditor.layout();
                        }
                    }
                }
            });
            tableItem.setText(new String[]{supplierNameStr, partNumberStr, standardPNStr, descriptionStr, null});
            isModified = true;
            promotionTable.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<TCComponent> getSelectedList() {
        list.clear();
        TableItem[] tableItems = promotionTable.getItems();
        try {
            if (tableItems != null && tableItems.length > 0) {
                for (TableItem tableItem : tableItems) {
                    String supplierName = tableItem.getText(0);
                    String partNumber = tableItem.getText(1);
                    String standardPN = tableItem.getText(2);
                    String description = tableItem.getText(3);
                    TCComponent com = CreateObject.createCom(session, "FX8_PromotionListTable");
                    com.setProperty("fx8_Mfr", supplierName);
                    com.setProperty("fx8_PartNumber", partNumber);
                    com.setProperty("fx8_StandardPN", standardPN);
                    com.setProperty("fx8_ObjectDesc", description);
                    list.add(com);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {

    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub

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
