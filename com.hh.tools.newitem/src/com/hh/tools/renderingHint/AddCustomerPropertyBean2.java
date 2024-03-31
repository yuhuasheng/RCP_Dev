package com.hh.tools.renderingHint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.CreateObject;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class AddCustomerPropertyBean2 extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;
    public static Table table = null;

    protected boolean isModified = false;
    Map<TableEditor, TableItem> tableControls = new HashMap<TableEditor, TableItem>();
    ArrayList<TableEditor> editorList = new ArrayList<>();
    protected Button addButton = null;
    protected Button removeButton = null;
    private TCComponent com;

    public AddCustomerPropertyBean2(Control paramControl) {
        super(paramControl);
    }

    public AddCustomerPropertyBean2(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                    Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite.getParent();
        initComposite(paramComposite);
    }

    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.LINE_SOLID);
        table.setHeaderVisible(true);// 设置显示表头
		table.setLinesVisible(true);// 设置显示表格线/*
        table.setEnabled(true);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 500;
        table.setLayoutData(gridData);


        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("No.");
        tableColumn.setWidth(50);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("Customer");
        tableColumn.setWidth(170);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("CustomerPN");
        tableColumn.setWidth(170);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("CustomerPNRev");
        tableColumn.setWidth(150);

        table.setBackground(parentComposite.getBackground());

        Label spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addButton = new Button(this.composite, SWT.NONE);
        addButton.setText("Add");
        addButton.computeSize(80, 25);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        addButton.setVisible(true);

        spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        removeButton = new Button(this.composite, SWT.NONE);
        removeButton.setText("Delete");
        removeButton.computeSize(80, 25);
        removeButton.setEnabled(false);
        removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        removeButton.setVisible(true);

        spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        addListeners();
        setControl(this.composite);
    }

    private void addListeners() {
        System.out.println("addListeners");
        Listener addlistener = new Listener() {
        	//新增
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 1");
                addTableRow();
            }
        };
        Listener removelistener = new Listener() {
        	//移除
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 2");
                int index = table.getSelectionIndex();
                System.out.println("index ==" + index);
                if (index < 0) {
                    return;
                }
                isModified = true;
                System.out.println("tableControls ==" + tableControls.size());
                TableItem selectItem = table.getItem(index);
                table.remove(index);
                System.out.println("selectItem ==" + selectItem);
                if (tableControls.size() > 0) {
                    Iterator<Entry<TableEditor, TableItem>> iterator = tableControls.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry<TableEditor, TableItem> entry = iterator.next();
                        TableItem tableItem = entry.getValue();
                        TableEditor editor = entry.getKey();
                        if (selectItem.equals(tableItem) && editor != null) {
                            editor.getEditor().dispose();
                            editor.dispose();
                            iterator.remove();
                        }
                    }
                    Iterator<Entry<TableEditor, TableItem>> it = tableControls.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<TableEditor, TableItem> entry = it.next();
                        TableEditor tableEditor = entry.getKey();
                        tableEditor.layout();
                    }
                }
                TableItem[] items = table.getItems();
                System.out.println("items.length ==" + items.length);
                for (int i = 0; i < items.length; i++) {
                    items[i].setText(0, (i + 1) + "");
                }
                table.update();
            }
        };
        addButton.addListener(SWT.Selection, addlistener);
        Listener tablelistener = new Listener() {
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 2");
                int index = table.getSelectionIndex();
                if (index >= 0) {
                    removeButton.setEnabled(true);
                }
            }
        };

        removeButton.addListener(SWT.Selection, removelistener);
        table.addListener(SWT.MouseDoubleClick, new Listener() {
            int editColumnIndex = -1;

            @Override
            public void handleEvent(Event arg0) {
            	System.out.println("========table双击==========");
                Point point = new Point(arg0.x, arg0.y);
                TableItem tableItem = table.getItem(point);
                if (tableItem == null) {
                    return;
                }
                for (int i = 1; i < 4; i++) {
                    Rectangle r = tableItem.getBounds(i);
                    if (r.contains(point)) {
                        editColumnIndex = i;
                        final TableEditor editor = new TableEditor(table);
                        tableControls.put(editor, tableItem);
                        editorList.add(editor);
                        Control oldEditor = editor.getEditor();
                        if (oldEditor != null) {
                            oldEditor.dispose();
                        }
                        final Text text = new Text(table, SWT.NONE);
                        text.computeSize(SWT.DEFAULT, table.getItemHeight());
                        editor.grabHorizontal = true;
                        editor.minimumHeight = text.getSize().y;
                        editor.minimumWidth = text.getSize().x;
                        editor.setEditor(text, tableItem, editColumnIndex);
                        text.setText(tableItem.getText(editColumnIndex));
                        text.forceFocus();
                        text.addModifyListener(new ModifyListener() {
                            @Override
                            public void modifyText(ModifyEvent e) {
                                isModified = true;
                                editor.getItem().setText(editColumnIndex, text.getText());
                            }
                        });
                    }
                }
            }

        });
        table.addListener(SWT.Selection, tablelistener);
    }

    public static void addTableRow() {
        int no = 1;
        TableItem[] items = table.getItems();
        if (items.length > 0) {
            String column = items[items.length - 1].getText(0);
            no = Integer.parseInt(column) + 1;
        }
        TableItem tableItem = new TableItem(table, SWT.NONE);
        tableItem.setText(new String[]{no + ""});
        table.update();
    }

    public static void addTableRowComp2Table(String[] processStrs) {
        TableItem tableItem = new TableItem(table, SWT.NONE);
        tableItem.setText(new String[]{processStrs[0], processStrs[1], processStrs[2]});
        table.update();
    }


    private void doSave() {
    	System.out.println("--保存--");
        System.out.println("com ==" + com);
        //保存table
        try {
            if (com != null) {
                TCProperty prop = com.getTCProperty("fx8_CustomerPNTable");
                TCComponent[] coms = prop.getReferenceValueArray();
                for (int i = 0; i < coms.length; i++) {
                    TCComponent comss = coms[i];
                    comss.delete();
                }
                int rows = AddCustomerPropertyBean2.table.getItemCount();
                System.out.println("rows ==" + rows);
                ArrayList<TCComponent> addCustomerList = new ArrayList<>();
                for (int i = 0; i < rows; i++) {
                    TableItem tableItem = AddCustomerPropertyBean2.table.getItem(i);
                    String customer = tableItem.getText(1);
                    String customerPN = tableItem.getText(2);
                    String customerPNRev = tableItem.getText(3);
                    System.out.println("customer ==" + customer);
                    TCComponent com = CreateObject.createTable("FX8_CustomerPNTable");
                    System.out.println("com ==" + com);
                    com.setProperty("fx8_Customer", customer);
                    com.setProperty("fx8_CustomerPN", customerPN);
                    com.setProperty("fx8_CustomerPNRev", customerPNRev);
                    addCustomerList.add(com);
                }
                System.out.println("addCustomerList ==" + addCustomerList.size());
                if (addCustomerList.size() > 0) {
                    TCProperty pro = com.getTCProperty("fx8_CustomerPNTable");
                    if (addCustomerList.size() > 0) {
                        pro.setPropertyArrayData(addCustomerList.toArray());
                        com.setTCProperty(pro);
                        addCustomerList.clear();
                    }
                }
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        TCProperty prop = com.getTCProperty("fx8_CustomerPNTable");
        TCComponent[] coms = prop.getReferenceValueArray();
        if (coms != null && coms.length > 0) {
            for (int i = 0; i < coms.length; i++) {
                TCComponent tcComponent = coms[i];
                String fx8_Customer = tcComponent.getProperty("fx8_Customer");
                String fx8_CustomerPN = tcComponent.getProperty("fx8_CustomerPN");
                String fx8_CustomerPNRev = tcComponent.getProperty("fx8_CustomerPNRev");

                TableItem tableItem = new TableItem(table, SWT.NONE);
                tableItem.setData(tcComponent);
                tableItem.setText(new String[]{(i + 1) + "", fx8_Customer, fx8_CustomerPN, fx8_CustomerPNRev});
                table.update();
            }
        }
    }

    @Override
    public TCPropertyDescriptor getPropertyDescriptor() {
        // TODO Auto-generated method stub
        return super.getPropertyDescriptor();
    }

    @Override
    public TCProperty getPropertyToSave(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        return super.getPropertyToSave(arg0);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        if (arg0 == null) {
            return null;
        }
        return arg0;
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        System.out.println("load TCPropertyDescriptor");
    }

    @Override
    public void setModifiable(boolean arg0) {
        System.out.println("setModifiable");
        this.modifiable = arg0;
        this.addButton.setEnabled(this.modifiable);
        this.removeButton.setEnabled(this.modifiable);
    }

    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
        System.out.println("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public void save(TCProperty paramTCProperty) throws Exception {
        System.out.println("save TCProperty!");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if ((savable) && (localTCProperty != null)) {
            localTCProperty.getTCComponent().setTCProperty(localTCProperty);
        }
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        System.out.println("saveProperty TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
        System.out.println("saveProperty TCProperty!");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public void setUIFValue(Object arg0) {
        System.out.println("setUIFValue");
        if (arg0 == null) {
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
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        System.out.println("isPropertyModified TCProperty");
        return this.isModified;
    }

    @Override
    public boolean isPropertyModified(TCComponent arg0) throws Exception {
        System.out.println("isPropertyModified TCComponent");
        return this.isModified;
    }


    @Override
    public Object getEditableValue() {
        System.out.println("getEditableValue");
        return null;
    }

    @Override
    public String getProperty() {
        System.out.println("getProperty");
        System.out.println("isModified 1 == " + isModified);
        doSave();
        return super.getProperty();
    }
}
