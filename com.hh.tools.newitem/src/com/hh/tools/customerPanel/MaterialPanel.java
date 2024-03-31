package com.hh.tools.customerPanel;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MInClassDialog;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.table.G4MTablePane;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.ui.commands.create.bo.NewBOWizard;
import com.teamcenter.rac.util.AbstractCustomPanel;
import com.teamcenter.rac.util.IPageComplete;
import com.teamcenter.rac.util.Utilities;

public class MaterialPanel extends AbstractCustomPanel implements IPageComplete {
    public static Button addButton = null;
    public static Button removeButton = null;
    public static Table table = null;
    private Composite composite;
    private static G4MInClassDialog m_dialog;
    private G4MUserAppContext g4mUserAppContext;
    private TCComponentItemRevision itemRev = null;
    public static List<Material> materialList = new ArrayList<>();
    public static Map<TableItem, TableEditor> tableControls = new HashMap<TableItem, TableEditor>();

    public MaterialPanel() {

    }

    public MaterialPanel(Composite parent) {
        super(parent);
    }

    @Override
    public void createPanel() {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        composite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(2, false);
        composite.setLayout(gl);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(gd);
        GridData labelGD = new GridData(GridData.HORIZONTAL_ALIGN_END);
        Label label=toolkit.createLabel(composite,"材料:                     ");
        label.setLayoutData(labelGD);

        Composite tableComposite = toolkit.createComposite(composite);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        tableComposite.setLayout(gridLayout);

        addButton = new Button(tableComposite, SWT.NONE);
        addButton.setText("指派材料");
//		addButton.computeSize(80, 25);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        removeButton = new Button(tableComposite, SWT.NONE);
        removeButton.setText("移除材料");
//		removeButton.computeSize(80, 25);
        removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners();
        initTable(tableComposite);

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
        initData();
    }

    private void initData() {
        TCComponent component = (TCComponent) AIFUtility.getCurrentApplication().getTargetComponent();
        if (component instanceof TCComponentItemRevision && (ItemTypeName.SHEETMETALREVISION.equals(component.getType())
                || ItemTypeName.PLASTICREVISION.equals(component.getType())
                || ItemTypeName.SCREWREVISION.equals(component.getType())
                || ItemTypeName.STANDOFFREVISION.equals(component.getType())
                || ItemTypeName.MYLARREVISION.equals(component.getType())
                || ItemTypeName.LABELREVISION.equals(component.getType())
                || ItemTypeName.RUBBERREVISION.equals(component.getType())
                || ItemTypeName.GASKETREVISION.equals(component.getType())
                || ItemTypeName.PCBPANELREVISION.equals(component.getType())
                || ItemTypeName.OTHERSREVISION.equals(component.getType()))) {
            try {
//				itemRev = (TCComponentItemRevision)component;
                if (!component.isCheckedOut()) {
                    addButton.setEnabled(false);
                    removeButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                    removeButton.setEnabled(true);
                }
                String materialType = component.getProperty("fx8_MaterialType");
                String density = component.getProperty("fx8_Density");
                String materialRemark = component.getProperty("fx8_MaterialRemark");
                if (!Utils.isNull(materialType) && !Utils.isNull(density)) {
                    table.removeAll();
                    TableItem tableItem = new TableItem(table, SWT.NONE);
                    Material material = new Material(materialType, density, materialRemark);
                    tableItem.setData(material);
                    materialList.add(material);
                    tableItem.setText(new String[]{materialType, density, materialRemark});
                    table.update();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void addListeners() {


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
                                                            MessageBox messageBox = new MessageBox(parent.getShell(), SWT.OK | SWT.CANCEL);
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

    private void initTable(Composite parentComposite) {
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

    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public boolean isPageComplete() {
        return true;
    }

    @Override
    public void updatePanel() {
        if (itemRev == null) {
            return;
        }
        ViewEditHelper localViewEditHelper = new ViewEditHelper(itemRev.getSession());
        ViewEditHelper.CKO localCKO = localViewEditHelper.getObjectState(itemRev);
        switch (localCKO) {
            case CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE:
            case CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE:
            case IMPLICITLY_CHECKOUTABLE:
                System.err.println("checkOut!!!");
                Utilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        addButton.setEnabled(true);
                        removeButton.setEnabled(true);
                    }
                });
                break;
            case CHECKED_IN:
            case NOT_CHECKOUTABLE:
                System.err.println("checkIn!!!");
                Utilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        addButton.setEnabled(false);
                        removeButton.setEnabled(false);
                    }
                });
                break;
            default:
                break;
        }


        if (input != null) {
            NewBOWizard wizard = (NewBOWizard) input;

            if (wizard.model.getTargetArray() != null) {
                try {
                    InterfaceAIFComponent com = wizard.model.getTargetArray()[0];
                    System.out.println("com==" + com);
                    String materialType = com.getProperty("fx8_MaterialType");
                    String density = com.getProperty("fx8_Density");
                    String materialRemark = com.getProperty("fx8_MaterialRemark");
                    if (!Utils.isNull(materialType) && !Utils.isNull(density)) {
                        table.removeAll();
                        TableItem tableItem = new TableItem(table, SWT.NONE);
                        tableItem.setData(new Material(materialType, density, materialRemark));
                        tableItem.setText(new String[]{materialType, density, materialRemark});
                        table.update();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getUserInput() {
        return super.getUserInput();
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


