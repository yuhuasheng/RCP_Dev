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
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

/*
 * 后制程
 */
public class SetPostProcessPropertyBean extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    public static List leftList = null;
    public static Table postProcessTable = null;
    protected Button addButton = null;
    protected Button removeButton = null;
    protected TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
    private static java.util.List<String> postProcessList = new ArrayList<>();
    Map<TableItem, TableEditor> tableControls = new HashMap<TableItem, TableEditor>();

    public SetPostProcessPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public SetPostProcessPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
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

        postProcessTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		postProcessTable.setHeaderVisible(true);// 设置显示表头
		postProcessTable.setLinesVisible(true);// 设置显示表格线/*

        TableColumn tableColumn = new TableColumn(postProcessTable, SWT.NONE);
        tableColumn.setText("后制程");
        tableColumn.setWidth(90);

        tableColumn = new TableColumn(postProcessTable, SWT.NONE);
        tableColumn.setText("备注");
        tableColumn.setWidth(120);

        postProcessTable.setBackground(parentComposite.getBackground());


        gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 180;
        postProcessTable.setLayoutData(gridData);


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
        if (postProcessList.size() > 0) {
            postProcessList.clear();
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
                if (selectedValues != null && selectedValues.length > 0) {
                    isModified = true;
                    for (String selectedValue : selectedValues) {
                        System.out.println("selectedValue==" + selectedValue);
                        if (!postProcessList.contains(selectedValue)) {
                            addTableRowComp2Table(selectedValue);
                        } else {
                        	Utils.infoMessage("您选择的后制程条目"+selectedValue+"已添加，请重新选择！");
                            return;
                        }
                    }

                } else {
                	Utils.infoMessage("您未选择后制程条目，请重新选择！");
                    return;
                }
            }
        };
        Listener removelistener = new Listener() {
            //移除
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 2");
                int index = postProcessTable.getSelectionIndex();
                if (index < 0) {
                	Utils.infoMessage("请选择需要移除的后制程！");
                    return;
                }
                isModified = true;
                String postProcessValue = postProcessTable.getItem(index).getText(0);
                System.out.println("移除后制程=="+postProcessValue);
                if (tableControls.size() > 0) {
                    Iterator<Entry<TableItem, TableEditor>> iterator = tableControls.entrySet().iterator();
                    boolean flag = false;
                    while (iterator.hasNext()) {
                        Entry<TableItem, TableEditor> entry = iterator.next();
                        TableItem tableItem = entry.getKey();
                        TableEditor editor = entry.getValue();
                        if (postProcessValue.equals(tableItem.getText(0)) && editor != null) {
                            editor.getEditor().dispose();
                            editor.dispose();
                            iterator.remove();
                            postProcessTable.remove(index);
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (!flag) postProcessTable.remove(index);

                    Iterator<Entry<TableItem, TableEditor>> it = tableControls.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<TableItem, TableEditor> entry = it.next();
                        TableEditor tableEditor = entry.getValue();
                        tableEditor.layout();
                    }
                } else {
                    postProcessTable.remove(index);
                }
                postProcessList.remove(postProcessValue);
                postProcessTable.update();
            }
        };

        addButton.addListener(SWT.Selection, addlistener);
        removeButton.addListener(SWT.Selection, removelistener);

        postProcessTable.addListener(SWT.MouseDoubleClick, new Listener() {
            int editColumnIndex = -1;

            @Override
            public void handleEvent(Event arg0) {
                Point point = new Point(arg0.x, arg0.y);
                TableItem tableItem = postProcessTable.getItem(point);
                if (tableItem == null) {
                    return;
                }
                Rectangle r = tableItem.getBounds(1);
                if (r.contains(point)) {
                    editColumnIndex = 1;
                    final TableEditor editor = new TableEditor(postProcessTable);
                    tableControls.put(tableItem, editor);
                    Control oldEditor = editor.getEditor();
                    if (oldEditor != null) {
                        oldEditor.dispose();
                    }
                    final Text text = new Text(postProcessTable, SWT.NONE);
                    text.computeSize(SWT.DEFAULT, postProcessTable.getItemHeight());
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
        return postProcessList;
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
            if (postProcessList.size() > 0) {
                TCComponent[] coms = new TCComponent[postProcessList.size()];
                for (int i = 0; i < postProcessList.size(); i++) {
                    TableItem tableItem = postProcessTable.getItem(i);
                    String postProcessStr = tableItem.getText(0);
                    String remarkStr = tableItem.getText(1);
                    TCComponent component = CreateObject.createCom(session, "FX8_PostProcTable");
                    component.setProperty("fx8_PostProc", postProcessStr);
                    component.setProperty("fx8_Remark", remarkStr);
                    coms[i] = component;
                }
                com.getTCProperty("fx8_PostProc").setReferenceValueArray(coms);
            } else {
                com.getTCProperty("fx8_PostProc").setReferenceValueArray(null);
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
        TCProperty prop = com.getTCProperty("fx8_PostProc");
        TCComponent[] coms = prop.getReferenceValueArray();
        if (coms != null && coms.length > 0) {
            for (TCComponent tcComponent : coms) {
                uploadTableRowComp2Table(tcComponent);
            }
        }

        String[] values = getPostProcessList();
        for (String value : values) {
            leftList.add(value);
            System.out.println("value==" + value);
        }
    }

    private void uploadTableRowComp2Table(TCComponent tcComponent) {
        try {
            String processStr = tcComponent.getProperty("fx8_PostProc");
            String remarkStr = tcComponent.getProperty("fx8_Remark");

            TableItem tableItem = new TableItem(postProcessTable, SWT.NONE);
            postProcessList.add(processStr);
            tableItem.setData(tcComponent);
            tableItem.setText(new String[]{processStr, remarkStr});
            postProcessTable.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTableRowComp2Table(String processStr) {
        TableItem tableItem = new TableItem(postProcessTable, SWT.NONE);
        postProcessList.add(processStr);
        tableItem.setText(new String[]{processStr, ""});
        postProcessTable.update();
    }


    private String[] getPostProcessList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site, "FX_Get_PostProcess_Values");
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        System.out.println("load TCPropertyDescriptor");
        tcPropertyDescriptor = paramTCPropertyDescriptor;


        String[] values = getPostProcessList();
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
