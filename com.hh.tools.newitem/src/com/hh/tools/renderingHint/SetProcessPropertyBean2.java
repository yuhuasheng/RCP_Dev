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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

/*
 * 生产制程
 */
public class SetProcessPropertyBean2 extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    public static List leftList = null;
    public static Table processTable = null;
    protected Button addButton = null;
    protected Button removeButton = null;
    protected TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
    private static java.util.List<String> processList = new ArrayList<>();
    Map<TableItem, TableEditor> tableControls = new HashMap<TableItem, TableEditor>();

    public SetProcessPropertyBean2(Control paramControl) {
        super(paramControl);
    }

    public SetProcessPropertyBean2(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                   Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(paramComposite);

    }

    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);


        leftList = new List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 100;
        leftList.setLayoutData(gridData);


        Label spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        processTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        processTable.setHeaderVisible(true);// 设置显示表头
		processTable.setLinesVisible(true);// 设置显示表格线/*

        TableColumn tableColumn = new TableColumn(processTable, SWT.NONE);
        tableColumn.setText("生产制程");
        tableColumn.setWidth(90);

        tableColumn = new TableColumn(processTable, SWT.NONE);
        tableColumn.setText("备注");
        tableColumn.setWidth(120);

        processTable.setBackground(parentComposite.getBackground());


        gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 180;
        processTable.setLayoutData(gridData);


        addButton = new Button(this.composite, SWT.NONE);
        addButton.setText("增加");
        addButton.computeSize(80, 25);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        removeButton = new Button(this.composite, SWT.NONE);
        removeButton.setText("移除");
        removeButton.computeSize(80, 25);
        removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners();
        setControl(this.composite);
        if (processList.size() > 0) {
            processList.clear();
        }

    }


    private void addListeners() {
        System.out.println("addListeners");
        Listener addlistener = new Listener() {
            //新增
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 1");
                final String[] selectedValues = leftList.getSelection();
                System.out.println("已选择的生产制程=="+selectedValues+"==数量=="+selectedValues.length);
                if (selectedValues != null && selectedValues.length > 0) {
                    isModified = true;
                    for (String selectedValue : selectedValues) {
                        System.out.println("selectedValue==" + selectedValue);
                        if (!processList.contains(selectedValue)) {
                            addTableRowComp2Table(selectedValue);
                        } else {
                        	Utils.infoMessage("您选择的生产制程条目"+selectedValue+"已添加，请重新选择！");
                            return;
                        }
                    }

                } else {
                	Utils.infoMessage("您未选择生产制程条目，请重新选择！");
                    return;
                }
            }
        };
        Listener removelistener = new Listener() {
            //移除
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 2");
                int index = processTable.getSelectionIndex();
                if (index < 0) {
                	Utils.infoMessage("请选择需要移除的生产制程！");
                    return;
                }
                isModified = true;
                String processValue = processTable.getItem(index).getText(0);
                System.out.println("移除生产制程=="+processValue);
                if (tableControls.size() > 0) {
                    Iterator<Entry<TableItem, TableEditor>> iterator = tableControls.entrySet().iterator();
                    boolean flag = false;
                    while (iterator.hasNext()) {
                        Entry<TableItem, TableEditor> entry = iterator.next();
                        TableItem tableItem = entry.getKey();
                        TableEditor editor = entry.getValue();
                        if (processValue.equals(tableItem.getText(0)) && editor != null) {
                            editor.getEditor().dispose();
                            editor.dispose();
                            iterator.remove();
                            processTable.remove(index);
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (!flag) processTable.remove(index);

                    Iterator<Entry<TableItem, TableEditor>> it = tableControls.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<TableItem, TableEditor> entry = it.next();
                        TableEditor tableEditor = entry.getValue();
                        tableEditor.layout();
                    }
                } else {
                    processTable.remove(index);
                }
                processList.remove(processValue);
                processTable.update();


            }
        };

        addButton.addListener(SWT.Selection, addlistener);
        removeButton.addListener(SWT.Selection, removelistener);

        processTable.addListener(SWT.MouseDoubleClick, new Listener() {
            int editColumnIndex = -1;

            @Override
            public void handleEvent(Event arg0) {
                Point point = new Point(arg0.x, arg0.y);
                TableItem tableItem = processTable.getItem(point);
                if (tableItem == null) {
                    return;
                }
                Rectangle r = tableItem.getBounds(1);
                if (r.contains(point)) {
                    editColumnIndex = 1;
                    final TableEditor editor = new TableEditor(processTable);
                    tableControls.put(tableItem, editor);
                    Control oldEditor = editor.getEditor();
                    if (oldEditor != null) {
                        oldEditor.dispose();
                    }
                    final Text text = new Text(processTable, SWT.NONE);
                    text.computeSize(SWT.DEFAULT, processTable.getItemHeight());
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

        });

    }

    public static java.util.List<String> getSelectedList() {
        return processList;
    }

    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
        return this.isModified;
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
        this.modifiable = paramBoolean;
        this.addButton.setEnabled(this.modifiable);
        this.removeButton.setEnabled(this.modifiable);
    }

    @Override
    public String getProperty() {
        System.out.println("getProperty");
        System.out.println("isModified 1 == " + isModified);
        if (isModified && com != null) {
            doSave();
        }
        return super.getProperty();
    }

    private void doSave() {
        try {
            if (processList.size() > 0) {
                TCComponent[] coms = new TCComponent[processList.size()];
                for (int i = 0; i < processList.size(); i++) {
                    TableItem tableItem = processTable.getItem(i);
                    String processStr = tableItem.getText(0);
                    String remarkStr = tableItem.getText(1);
                    TCComponent component = CreateObject.createCom(session, "FX8_ProdProcTable");
                    component.setProperty("fx8_Proc", processStr);
                    component.setProperty("fx8_Remark", remarkStr);
                    coms[i] = component;
                }
                com.getTCProperty("fx8_ProdProc").setReferenceValueArray(coms);
            } else {
                com.getTCProperty("fx8_ProdProc").setReferenceValueArray(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
	 * add by 李昂  2019-03-12
	 * 移除左边列表内容
	 */
    public static void removeLeftList(String[] values) {
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                int index = leftList.indexOf(values[i]);
                if (index != -1) {
                    leftList.remove(values[i]);
                }
            }
        }
    }


    /*
	 * add by 李昂  2019-03-12
	 * 移除左边列表内容
	 */
    public static void addLeftList(String[] values) {
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                leftList.add(values[i]);
            }
        }
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        TCProperty prop = com.getTCProperty("fx8_ProdProc");
        TCComponent[] coms = prop.getReferenceValueArray();
        if (coms != null && coms.length > 0) {
            for (TCComponent tcComponent : coms) {
                uploadTableRowComp2Table(tcComponent);
            }
        }

        String[] values = getProcessList();
        for (String value : values) {
            leftList.add(value);
            System.out.println("value==" + value);
        }
    }

    private void uploadTableRowComp2Table(TCComponent tcComponent) {
        try {
            String processStr = tcComponent.getProperty("fx8_Proc");
            String remarkStr = tcComponent.getProperty("fx8_Remark");

            TableItem tableItem = new TableItem(processTable, SWT.NONE);
            processList.add(processStr);
            tableItem.setData(tcComponent);
            tableItem.setText(new String[]{processStr, remarkStr});
            processTable.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTableRowComp2Table(String processStr) {
        TableItem tableItem = new TableItem(processTable, SWT.NONE);
        processList.add(processStr);
        tableItem.setText(new String[]{processStr, ""});
        processTable.update();
    }


    private String[] getProcessList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site, "FX_Get_Process_Values");
    }


    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        System.out.println("load TCPropertyDescriptor");
        tcPropertyDescriptor = paramTCPropertyDescriptor;

        String[] values = getProcessList();
        for (String value : values) {
            leftList.add(value);
        }

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
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        super.dispose();
    }

}
