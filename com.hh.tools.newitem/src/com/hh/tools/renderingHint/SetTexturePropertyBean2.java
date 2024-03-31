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


public class SetTexturePropertyBean2 extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    public static List leftList = null;
    public static Table textureTable = null;
    protected Button addButton = null;
    protected Button removeButton = null;
    protected TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
    private static java.util.List<String> textureList = new ArrayList<>();
    Map<TableItem, TableEditor> tableControls = new HashMap<TableItem, TableEditor>();

    public SetTexturePropertyBean2(Control paramControl) {
        super(paramControl);
    }

    public SetTexturePropertyBean2(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
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

        textureTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        textureTable.setHeaderVisible(true);// ������ʾ��ͷ
		textureTable.setLinesVisible(true);// ������ʾ�����/*

        TableColumn tableColumn = new TableColumn(textureTable, SWT.NONE);
        tableColumn.setText("ģ��ʴ��");
        tableColumn.setWidth(90);

        tableColumn = new TableColumn(textureTable, SWT.NONE);
        tableColumn.setText("��ע");
        tableColumn.setWidth(120);

        textureTable.setBackground(parentComposite.getBackground());


        gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 180;
        textureTable.setLayoutData(gridData);


        addButton = new Button(this.composite, SWT.NONE);
        addButton.setText("����");
        addButton.computeSize(80, 25);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        removeButton = new Button(this.composite, SWT.NONE);
        removeButton.setText("�Ƴ�");
        removeButton.computeSize(80, 25);
        removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        spaceLabel = new Label(this.composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners();
        setControl(this.composite);
        if (textureList.size() > 0) {
            textureList.clear();
        }

    }


    private void addListeners() {
        System.out.println("addListeners");
        Listener addlistener = new Listener() {
            //����
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 1");
                final String[] selectedValues = leftList.getSelection();
                if (selectedValues != null && selectedValues.length > 0) {
                    isModified = true;
                    for (String selectedValue : selectedValues) {
                        System.out.println("selectedValue==" + selectedValue);
                        if (!textureList.contains(selectedValue)) {
                            addTableRowComp2Table(selectedValue);
                        } else {
                        	Utils.infoMessage("��ѡ���ģ��ʴ����Ŀ"+selectedValue+"����ӣ�������ѡ��");
                            return;
                        }
                    }

                } else {
                	Utils.infoMessage("��δѡ��ģ��ʴ����Ŀ��������ѡ��");
                    return;
                }
            }
        };
        Listener removelistener = new Listener() {
            //�Ƴ�
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 2");
                int index = textureTable.getSelectionIndex();
                if (index < 0) {
                	Utils.infoMessage("��ѡ����Ҫ�Ƴ���ģ��ʴ�ƣ�");
                    return;
                }
                isModified = true;
                String textureValue = textureTable.getItem(index).getText(0);
                System.out.println("�Ƴ�ģ��ʴ��=="+textureValue);
                if (tableControls.size() > 0) {
                    Iterator<Entry<TableItem, TableEditor>> iterator = tableControls.entrySet().iterator();
                    boolean flag = false;
                    while (iterator.hasNext()) {
                        Entry<TableItem, TableEditor> entry = iterator.next();
                        TableItem tableItem = entry.getKey();
                        TableEditor editor = entry.getValue();
                        if (textureValue.equals(tableItem.getText(0)) && editor != null) {
                            editor.getEditor().dispose();
                            editor.dispose();
                            iterator.remove();
                            textureTable.remove(index);
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                    if (!flag) textureTable.remove(index);

                    Iterator<Entry<TableItem, TableEditor>> it = tableControls.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<TableItem, TableEditor> entry = it.next();
                        TableEditor tableEditor = entry.getValue();
                        tableEditor.layout();
                    }
                } else {
                    textureTable.remove(index);
                }
                textureList.remove(textureValue);
                textureTable.update();

            }
        };

        addButton.addListener(SWT.Selection, addlistener);
        removeButton.addListener(SWT.Selection, removelistener);

        textureTable.addListener(SWT.MouseDoubleClick, new Listener() {
            int editColumnIndex = -1;

            @Override
            public void handleEvent(Event arg0) {
                Point point = new Point(arg0.x, arg0.y);
                TableItem tableItem = textureTable.getItem(point);
                if (tableItem == null) {
                    return;
                }
                Rectangle r = tableItem.getBounds(1);
                if (r.contains(point)) {
                    editColumnIndex = 1;
                    final TableEditor editor = new TableEditor(textureTable);
                    tableControls.put(tableItem, editor);
                    Control oldEditor = editor.getEditor();
                    if (oldEditor != null) {
                        oldEditor.dispose();
                    }
                    final Text text = new Text(textureTable, SWT.NONE);
                    text.computeSize(SWT.DEFAULT, textureTable.getItemHeight());
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
        return textureList;
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
            if (textureList.size() > 0) {
                TCComponent[] coms = new TCComponent[textureList.size()];
                for (int i = 0; i < textureList.size(); i++) {
                    TableItem tableItem = textureTable.getItem(i);
                    String textureStr = tableItem.getText(0);
                    String remarkStr = tableItem.getText(1);
                    TCComponent component = CreateObject.createCom(session, "FX8_TextureTable");
                    component.setProperty("fx8_Texture", textureStr);
                    component.setProperty("fx8_Remark", remarkStr);
                    coms[i] = component;
                }
                com.getTCProperty("fx8_Texture").setReferenceValueArray(coms);
            } else {
                com.getTCProperty("fx8_Texture").setReferenceValueArray(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
	 * add by �  2019-03-12
	 * �Ƴ�����б�����
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
	 * add by �  2019-03-12
	 * �Ƴ�����б�����
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
        TCProperty prop = com.getTCProperty("fx8_Texture");
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
            String processStr = tcComponent.getProperty("fx8_Texture");
            String remarkStr = tcComponent.getProperty("fx8_Remark");

            TableItem tableItem = new TableItem(textureTable, SWT.NONE);
            textureList.add(processStr);
            tableItem.setData(tcComponent);
            tableItem.setText(new String[]{processStr, remarkStr});
            textureTable.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTableRowComp2Table(String processStr) {
        TableItem tableItem = new TableItem(textureTable, SWT.NONE);
        textureList.add(processStr);
        tableItem.setText(new String[]{processStr, ""});
        textureTable.update();
    }


    private String[] getProcessList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site, "FX_Get_Texture_Values");
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
