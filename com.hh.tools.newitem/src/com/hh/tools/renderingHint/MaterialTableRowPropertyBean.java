package com.hh.tools.renderingHint;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.customerPanel.Material;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.classification.common.G4MInClassDialog;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.table.G4MTablePane;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

/**
 * @author Handk
 */
public abstract class MaterialTableRowPropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;

    protected FormToolkit toolkit;
    protected Composite composite;

    public static Table table = null;

    protected Button addButton = null;
    protected Button removeButton = null;
    protected static List<Material> materialList = new ArrayList<>();
    protected boolean isModified = false;
    protected static G4MInClassDialog m_dialog;
    private G4MUserAppContext g4mUserAppContext;
    public static Map<TableItem, TableEditor> tableControls = new HashMap<TableItem, TableEditor>();

    public MaterialTableRowPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public MaterialTableRowPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                        Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(paramComposite);

    }


    /**
	 * 将TableRow对象添加到表格中
	 * @param component TableRow对象
	 */
    protected abstract void addTableRowComp2Table(Material material);


    /**
	 * 获取表格中所有的TableRow对象
	 * @return 表格中所有的TableRow对象
	 */
    protected abstract Material[] getTableRowComps();


    /**
	 * 获取选中的的TableRow对象
	 * @return
	 */
    protected abstract TCComponent getSelectedTableRowComp();

    /**
	 * 初始化表格
	 * @param parentComposite 父容器
	 */
    protected abstract void initTable(Composite parentComposite);

    public void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        addButton = new Button(this.composite, SWT.NONE);
        addButton.setText("指派材料");
//		addButton.computeSize(80, 25);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        removeButton = new Button(this.composite, SWT.NONE);
        removeButton.setText("移除材料");
//		removeButton.computeSize(80, 25);
        removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners();
        initTable(this.composite);
        setControl(table);
        if (materialList.size() > 0) {
            materialList.clear();
        }
        GridData localGridData = new GridData(GridData.FILL, GridData.FILL, true, true, 10, 1);
        localGridData.widthHint = 360;
        localGridData.heightHint = 60;
        table.setLayoutData(localGridData);

        table.addListener(SWT.MouseDoubleClick, new Listener() {
            int editColumnIndex = -1;

            @Override
            public void handleEvent(Event arg0) {
                Point point = new Point(arg0.x, arg0.y);
                TableItem tableItem = table.getItem(point);
                if (tableItem == null) {
                    return;
                }
                Rectangle r = tableItem.getBounds(2);
                if (r.contains(point)) {
                    editColumnIndex = 2;
                    final TableEditor editor = new TableEditor(table);
                    tableControls.put(tableItem, editor);
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
                            editor.getItem().setText(editColumnIndex, text.getText());
                        }
                    });
                }
            }
        });
        if (materialList.size() > 0) {
            materialList.clear();
        }
    }


    protected void addListeners() {


        Listener addlistener = new Listener() {

            @Override
            public void handleEvent(Event arg0) {

                Runnable r = new Runnable() {

                    @Override
                    public void run() {


                        // 分类属性面板定义
                        try {
                            ClassificationService clafService = new ClassificationService();
                            String partClassifyRootId = "ICM";
                            g4mUserAppContext = new G4MUserAppContext(clafService,
                                    partClassifyRootId);

                            m_dialog = new G4MInClassDialog(g4mUserAppContext.getApplication(), 2, true, true);
                            m_dialog.setOccurrenceTypeSelectionVisible(false);// 隐藏选择事例类型下拉框
							m_dialog.setTitle("分类搜索对话框");
                            m_dialog.setModal(true);
                            m_dialog.setLocationRelativeTo(null);
                            Field field = m_dialog.getClass().getDeclaredField("m_okButton");
                            field.setAccessible(true);
                            JButton okButton = (JButton) field.get(m_dialog);

                            Field tablePaneField = m_dialog.getClass().getDeclaredField("m_tablePane");
                            tablePaneField.setAccessible(true);
                            final G4MTablePane tablePane = (G4MTablePane) tablePaneField.get(m_dialog);

                            okButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (tablePane != null) {
                                        int rowNumber = tablePane.getG4MTable().getSelectedRowCount();
                                        if (rowNumber > 1) {
                                        	Utils.infoMessage("只能选择一个对象进行指派");
                                            return;
                                        } else if (rowNumber == 1) {
                                            final TCComponent com = tablePane.getG4MTable().getSelectionWSO();
                                            Display.getDefault().syncExec(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        int tableItems = table.getItemCount();
                                                        if (tableItems > 0) {
                                                            MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK | SWT.CANCEL);
                                                            messageBox.setMessage("材料已指派，是否继续指派，如果选择\"是\"，将会移除已指派的材料?");
                                                            if (messageBox.open() == SWT.OK) {
                                                                removeMaterialTable(tableControls, table);
                                                                materialList.clear();

                                                                Material material = getClassifyMaterial((TCSession) g4mUserAppContext.getApplication().getSession(), com);
                                                                if (!Utils.isNull(material.getMaterialType()) && !Utils.isNull(material.getDensity())) {
                                                                    TableItem tableItem = new TableItem(table, SWT.NONE);
                                                                    materialList.add(material);
                                                                    tableItem.setData(material);
                                                                    tableItem.setText(new String[]{material.getMaterialType(), material.getDensity(), ""});
                                                                    table.update();
                                                                }

                                                            }

                                                        } else {
                                                            Material material = getClassifyMaterial((TCSession) g4mUserAppContext.getApplication().getSession(), com);
                                                            if (!Utils.isNull(material.getMaterialType()) && !Utils.isNull(material.getDensity())) {
                                                                TableItem tableItem = new TableItem(table, SWT.NONE);
                                                                materialList.add(material);
                                                                tableItem.setData(material);
                                                                tableItem.setText(new String[]{material.getMaterialType(), material.getDensity(), ""});
                                                                table.update();
                                                            }
                                                        }


                                                    } catch (TCException e1) {
                                                        // TODO Auto-generated catch block
                                                        e1.printStackTrace();
                                                    }

                                                }

                                            });

                                        }
                                    }


                                }
                            });
                            G4MTree g4mtree = m_dialog.getTree();

                            g4mtree.setShowPopupMenu(false);
                            G4MTreeNode root = g4mtree.findNode("ICM");
                            g4mtree.setRootNode(root, true);

                            root.refresh(true);
                            m_dialog.setBounds(100, 100, 600, 500);
                            m_dialog.setVisible(true);
                            m_dialog.setLocationRelativeTo(null);
                            m_dialog.repaint();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                if (EventQueue.isDispatchThread()) {
                    r.run();
                } else {
                    EventQueue.invokeLater(r);
                }
            }
        };

        Listener removelistener = new Listener() {

            @Override
            public void handleEvent(Event arg0) {
                int index = table.getSelectionIndex();
                if (index < 0) {
                    return;
                }
                String materialType = table.getItem(index).getText(0);
                String density = table.getItem(index).getText(1);
                TableItem tableItem = null;
                TableEditor editor = null;
                if (tableControls.size() > 0) {
                    Iterator<Entry<TableItem, TableEditor>> iterator = tableControls.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<TableItem, TableEditor> entry = iterator.next();
                        TableItem tableItem2 = entry.getKey();
                        if (!tableItem2.isDisposed() && materialType.equals(tableItem2.getText(0)) && density.equals(tableItem2.getText(1))) {
                            tableItem = entry.getKey();
                            editor = entry.getValue();
                            break;

                        }

                    }
                    if (editor != null) {
                        editor.getEditor().dispose();
                        editor.dispose();
                        tableControls.remove(tableItem);
                    }
                }
                table.remove(index);
                materialList.clear();
                table.update();

            }
        };
        addButton.addListener(SWT.Selection, addlistener);
        removeButton.addListener(SWT.Selection, removelistener);
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
    public String getProperty() {
        // TODO Auto-generated method stub
        System.out.println("getProperty");
        System.out.println("isModified == " + isModified);
        System.out.println("tcProperty == " + tcProperty);
        return super.getProperty();
    }

//	private void doSave() {
//		try {
//			System.out.println("doSave");
//			TCComponent[] comps= getTableRowComps();
//			if(tcProperty != null){
//				tcProperty.setReferenceValueArray(comps);
//			}
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//	}

    @Override
    public Object getEditableValue() {
        Material[] material = this.getTableRowComps();
        if (material == null || material.length < 1) {
            return null;
        }
        Object[] uids = new Object[material.length];
        for (int i = 0; i < material.length; i++) {
            uids[i] = material[i];
        }
        return uids;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
    	System.err.println("保存！！！");
        if (arg0 == null) {
            return null;
        }
        savable = false;
        if (table == null) {
            return null;
        }
        if ((!arg0.isEnabled()) || (!modifiable)) {
            if (Debug.isOn("stylesheet,form,property,properties")) {
                Debug.println("AbstractTableRowPropertyBean: save propName=" + property + " not modifiable, skip.");
            }
            return null;
        }

        Material[] comps = getTableRowComps();
        if ((comps == null) || (comps.length == 0)) {
            if (Debug.isOn("stylesheet,form,property,properties")) {
                Debug.println("AbstractTableRowPropertyBean: save propName=" + property
                        + ", value is null or length is 0, setNullVerdict(true)");
            }
            savable = true;
            arg0.setNullVerdict(true);
            return arg0;
        }
        savable = true;
//		arg0.setReferenceValueArray(comps);
        System.err.println(comps.length +"长度进行保存！！");
        return arg0;
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
        tcProperty = arg0;
        property = arg0.getPropertyName();
        if (property == null) {
            return;
        }
        if (descriptor == null) {
            TCPropertyDescriptor tcPropertyDescriptor = arg0.getPropertyDescriptor();
            load(tcPropertyDescriptor);
        }
        if (!descriptor.isDisplayable()) {
            return;
        }
        TCComponent component = tcProperty.getTCComponent();
        String materialType = tcProperty.getStringValue();
        String density = component.getProperty("fx8_Density");
        String materialRemark = component.getProperty("fx8_MaterialRemark");
        if (!Utils.isNull(materialType) || !Utils.isNull(density) || !Utils.isNull(materialRemark)) {
            Material material = new Material(materialType, density, materialRemark);
            addTableRowComp2Table(material);
        }

    }


    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        descriptor = arg0;
        if (arg0.isEnabled() && modifiable) {
            addButton.setVisible(true);
            removeButton.setVisible(true);
        } else {
            addButton.setVisible(false);
            removeButton.setVisible(false);

        }
        if (arg0.isRequired()) {
            setMandatory(true);
        }
        if (!arg0.isDisplayable()) {
            setVisible(false);
        }
    }

    @Override
    public void setModifiable(boolean arg0) {
        this.modifiable = arg0;
        addButton.setEnabled(this.modifiable);
        removeButton.setEnabled(this.modifiable);
    }

    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
        Utils.print2Console("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public void save(TCProperty paramTCProperty) throws Exception {
        Utils.print2Console("save TCProperty!");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if ((savable) && (localTCProperty != null)) {
            localTCProperty.getTCComponent().setTCProperty(localTCProperty);
        }
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        Utils.print2Console("saveProperty TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
        Utils.print2Console("saveProperty TCProperty!");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public void setUIFValue(Object arg0) {
        System.out.println("setUIFValue");
    }

    public Material getClassifyMaterial(TCSession session, TCComponent com) throws TCException {
        Material material = new Material();
        TCClassificationService cs = session.getClassificationService();
        ICSApplicationObject icsApp = cs.newICSApplicationObject("ICM");
        TCComponent cmp = com;
        if (!cs.isObjectClassified(cmp)) {
            cmp = cs.getActualClassifiedComponent(cmp);
        }
        String cmpUid = cs.getTCComponentUid(cmp);
        int resultCount = icsApp.searchById("", cmpUid);
        if (resultCount > 0) {
            icsApp.read(1);
            ICSProperty[] ps = icsApp.getProperties();
            ICSView fv = icsApp.getView();
            ICSPropertyDescription[] pds = fv.getPropertyDescriptions();
            for (int i = 0; i < pds.length; i++) {
                String name = pds[i].getDisplayName();
                if (name.contains("MaterialType")) {
                    String v = getValue(ps[i], pds[i]);
                    material.setMaterialType(v);
                } else if (name.contains("Density")) {
                    String v = getValue(ps[i], pds[i]);
                    material.setDensity(v);
                }
            }
        }
        return material;
    }

    public class Material {
        private String materialType;
        private String density;
        private String materialRemark;

        public String getMaterialType() {
            return materialType;
        }

        public void setMaterialType(String materialType) {
            this.materialType = materialType;
        }

        public String getDensity() {
            return density;
        }

        public void setDensity(String density) {
            this.density = density;
        }

        public String getMaterialRemark() {
            return materialRemark;
        }

        public void setMaterialRemark(String materialRemark) {
            this.materialRemark = materialRemark;
        }

        public Material() {
            super();
        }

        public Material(String materialType, String density,
                        String materialRemark) {
            super();
            this.materialType = materialType;
            this.density = density;
            this.materialRemark = materialRemark;
        }


    }

    private String getValue(ICSProperty p, ICSPropertyDescription pd) {
        String value = p.getValue();
        ICSFormat mf = pd.getMetricFormat();
        if (mf != null) {
            ICSKeyLov lov = mf.getKeyLov();
            if (lov != null) {
                String d = lov.getValueOfKey(value);
                if (d != null && d.length() > 0) {
                    value = d;
                }
            }
        }
        return value;
    }

    private static void removeMaterialTable(Map<TableItem, TableEditor> tableControls2, Table table2) {
        String materialType = table2.getItem(0).getText(0);
        String density = table2.getItem(0).getText(1);
        TableItem tableItem = null;
        TableEditor editor = null;
        if (tableControls2.size() > 0) {
            Iterator<Entry<TableItem, TableEditor>> iterator = tableControls2.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<TableItem, TableEditor> entry = iterator.next();
                TableItem tableItem2 = entry.getKey();
                if (!tableItem2.isDisposed() && materialType.equals(tableItem2.getText(0)) && density.equals(tableItem2.getText(1))) {
                    tableItem = entry.getKey();
                    editor = entry.getValue();
                    break;
                }

            }
            if (editor != null) {
                editor.getEditor().dispose();
                editor.dispose();
                tableControls2.remove(tableItem);
            }
        }
        table2.removeAll();
    }


}
