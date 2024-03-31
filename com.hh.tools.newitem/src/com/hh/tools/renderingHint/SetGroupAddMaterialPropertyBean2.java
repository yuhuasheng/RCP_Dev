package com.hh.tools.renderingHint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

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
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class SetGroupAddMaterialPropertyBean2 extends AbstractPropertyBean<Object> {

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

    public SetGroupAddMaterialPropertyBean2(Control paramControl) {
        super(paramControl);
    }

    public SetGroupAddMaterialPropertyBean2(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
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
        tableColumn.setText("HHPN");
        tableColumn.setWidth(220);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("名称");
        tableColumn.setWidth(250);

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("id");
        tableColumn.setWidth(0);
        table.setBackground(parentComposite.getBackground());

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("Rev");
        tableColumn.setWidth(0);
        table.setBackground(parentComposite.getBackground());

        tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText("OwnerUser");
        tableColumn.setWidth(0);
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
                new AddMatDialog();
            }
        };
        Listener removelistener = new Listener() {
            // 移除
            @Override
            public void handleEvent(Event paramEvent) {
                int index = table.getSelectionIndex();
                if (index < 0) {
                	Utils.infoMessage("请选择需要移除的料件！");
                    return;
                }
                deleteTableRow();
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
            @Override
            public void handleEvent(Event paramEvent) {
                int index = table.getSelectionIndex();
                if (index >= 0) {
                    removeButton.setEnabled(true);
                } else {
                    removeButton.setEnabled(false);
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
        tableItem.setText(new String[]{processStrs[0], processStrs[1], processStrs[2], processStrs[3], processStrs[4], processStrs[5]});
        table.update();
    }


    private void doSave() {
    	System.out.println("--保存--");
        System.out.println("com ==" + com);
        //保存table
        try {
            if (com != null) {
                TCProperty prop = com.getTCProperty("fx8_EDACompGrpTable");
                TCComponent[] coms = prop.getReferenceValueArray();
                for (int i = 0; i < coms.length; i++) {
                    TCComponent comss = coms[i];
                    comss.delete();
                }
                int rows = SetGroupAddMaterialPropertyBean2.table.getItemCount();
                System.out.println("rows ==" + rows);
                ArrayList<TCComponent> addMaterList = new ArrayList<>();
                for (int i = 0; i < rows; i++) {
                    TableItem tableItem = SetGroupAddMaterialPropertyBean2.table.getItem(i);
                    String fx8_StandardPN = tableItem.getText(1);
                    String fx8_EDACompObjectName = tableItem.getText(2);
                    String fx8_EDACompItemid = tableItem.getText(3);
                    String fx8_EDACompRevision = tableItem.getText(4);
                    String fx8_EDACompOwnerUser = tableItem.getText(5);
                    TCComponent com = CreateObject.createTable("FX8_EDACompGrpTable");
                    System.out.println("com ==" + com);
                    com.setProperty("fx8_EDACompItemid", fx8_EDACompItemid);
                    com.setProperty("fx8_EDACompObjectName", fx8_EDACompObjectName);
                    com.setProperty("fx8_StandardPN", fx8_StandardPN);
                    com.setProperty("fx8_EDACompRevision", fx8_EDACompRevision);
                    com.setProperty("fx8_EDACompOwnerUser", fx8_EDACompOwnerUser);
                    addMaterList.add(com);
                }
                System.out.println("addCustomerList ==" + addMaterList.size());
                if (addMaterList.size() > 0) {
                    TCProperty pro = com.getTCProperty("fx8_EDACompGrpTable");
                    if (addMaterList.size() > 0) {
                        pro.setPropertyArrayData(addMaterList.toArray());
                        com.setTCProperty(pro);
                        addMaterList.clear();
                    }
                }
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Override
    public void load(Object arg0, boolean arg1) {
        System.out.println("load1111 ==" + arg0);
        super.load(arg0, arg1);
    }

    @Override
    public void load(TCComponent arg0) throws Exception {
        System.out.println("load2222 ==" + arg0);
        super.load(arg0);
    }

    @Override
    public void load(TCComponentType arg0) throws Exception {
        System.out.println("load333 ==" + arg0);
        super.load(arg0);
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        System.out.println("加载 com ==" + com);
        TCProperty prop = com.getTCProperty("fx8_EDACompGrpTable");
        TCComponent[] coms = prop.getReferenceValueArray();
        if (coms != null && coms.length > 0) {
            for (int i = 0; i < coms.length; i++) {
                TCComponent tcComponent = coms[i];
                String fx8_StandardPN = tcComponent.getProperty("fx8_StandardPN");
                String fx8_EDACompObjectName = tcComponent.getProperty("fx8_EDACompObjectName");
                String fx8_EDACompItemid = tcComponent.getProperty("fx8_EDACompItemid");
                String fx8_EDACompRevision = tcComponent.getProperty("fx8_EDACompRevision");
                String fx8_EDACompOwnerUser = tcComponent.getProperty("fx8_EDACompOwnerUser");

                TableItem tableItem = new TableItem(table, SWT.NONE);
                tableItem.setData(tcComponent);
                tableItem.setText(new String[]{(i + 1) + "", fx8_StandardPN, fx8_EDACompObjectName, fx8_EDACompItemid, fx8_EDACompRevision, fx8_EDACompOwnerUser});
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

    class AddMatDialog extends AbstractAIFDialog {
        private static final long serialVersionUID = 1L;
        private final JPanel contentPanel = new JPanel();
        private int height = 25;
        private iTextField HHPNField;
        private iTextField nameField;
        private JButton okButton;
        private JButton cancelButton;
        private TCTable newTable;
        private String[] titles = new String[] { "HHPN", "名称", "ID", "REV", "Owning_user" };

        public AddMatDialog() {
            super(AIFUtility.getActiveDesktop().getFrame());
            initUI();
        }

        private void initUI() {
        	setTitle("选择电子料件");
            setAlwaysOnTop(true);
            setBounds(100, 100, 750, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(new TitledBorder(null, "搜索", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                contentPanel.add(panel, BorderLayout.NORTH);
                panel.setBackground(Color.white);
                JLabel idLabel = new JLabel("HHPN", JLabel.RIGHT);
                idLabel.setPreferredSize(new Dimension(100, height));
                HHPNField = new iTextField();
                HHPNField.setPreferredSize(new Dimension(150, height));

                JLabel nameLabel = new JLabel("名称:", JLabel.RIGHT);
                nameLabel.setPreferredSize(new Dimension(100, height));
                nameField = new iTextField();
                nameField.setPreferredSize(new Dimension(150, height));

                JButton searchButton = new JButton("查询");
                searchButton.setPreferredSize(new Dimension(70, height));
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                searchAction();
                            }
                        }).start();
                    }
                });

                panel.add("1.1.right.center.preferred.preferred", idLabel);
                panel.add("1.2.left.center.preferred.preferred", HHPNField);
                panel.add("1.3.right.center.preferred.preferred", nameLabel);
                panel.add("1.4.left.center.preferred.preferred", nameField);
                panel.add("1.5.right.center.preferred.preferred", searchButton);

            }
            {
                JPanel panel = new JPanel();
                panel.setBorder(new TitledBorder(null, "电子料件列表", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                newTable = new TCTable(session, titles) {
                    private static final long serialVersionUID = 1L;

                    public boolean isCellEdinewTable(int arg0, int arg1) {
                        return false;
                    }
                };

                JScrollPane scrollTablePanel = new JScrollPane();
                scrollTablePanel.setBackground(Color.WHITE);
                scrollTablePanel.setPreferredSize(new Dimension(730, 300));
                scrollTablePanel.setViewportView(newTable);
                newTable.getTableHeader().setBackground(Color.WHITE);
                newTable.setRowHeight(30);
                newTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                newTable.getTableHeader().setResizingAllowed(true);
                newTable.getTableHeader().setReorderingAllowed(false);
                panel.add(scrollTablePanel, BorderLayout.CENTER);

                TableColumnModel columnModel = newTable.getColumnModel();
                javax.swing.table.TableColumn column = columnModel.getColumn(2);
//				column.setMinWidth(0);
//				column.setMaxWidth(0);
//				column.setWidth(0);
//				column.setPreferredWidth(0);
                newTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            dispose();
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    int row = newTable.getSelectedRow();
                                    String matHHPN = newTable.getValueAt(row, 0).toString();
                                    String matName = newTable.getValueAt(row, 1).toString();
                                    String itemId = newTable.getValueAt(row, 2).toString();
                                    String rev = newTable.getValueAt(row, 3).toString();
                                    String owning_user = newTable.getValueAt(row, 4).toString();
                                    TableItem[] newTableItems = SetGroupAddMaterialPropertyBean2.table.getItems();
                                    HashMap<String, Boolean> map = new HashMap<>();//
                                    if (newTableItems != null && newTableItems.length != 0) {
                                        for (int j = 0; j < newTableItems.length; j++) {
                                            map.put(newTableItems[j].getText(3), true);
                                        }
                                    }
                                    //不允许添加再次添加自己
                                    if (map.get(itemId) == null || !map.get(itemId)) {
                                        addTableRowComp2Table(new String[]{"", matHHPN, matName, itemId, rev, owning_user});
                                    }
                                    TableItem[] items = SetGroupAddMaterialPropertyBean2.table.getItems();
                                    System.out.println("items.length ==" + items.length);
                                    for (int i = 0; i < items.length; i++) {
                                        items[i].setText(0, (i + 1) + "");
                                    }
                                    table.update();
                                }
                            });
                        }
                    }
                });
            }
            {
                JPanel panel = new JPanel();
                panel.setBackground(Color.WHITE);
                contentPanel.add(panel, BorderLayout.SOUTH);
                {
                    okButton = new JButton("确定");
                    panel.add(okButton);
                    okButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    int[] rows = newTable.getSelectedRows();
                                    if (rows != null && rows.length != 0) {
                                        TableItem[] newTableItems = SetGroupAddMaterialPropertyBean2.table.getItems();
                                        HashMap<String, Boolean> map = new HashMap<>();//
                                        if (newTableItems != null && newTableItems.length != 0) {
                                            for (int j = 0; j < newTableItems.length; j++) {
                                                map.put(newTableItems[j].getText(3), true);
                                            }
                                        }
                                        for (int i = 0; i < rows.length; i++) {
                                            String matHHPN = newTable.getValueAt(rows[i], 0).toString();
                                            String matName = newTable.getValueAt(rows[i], 1).toString();
                                            String itemId = newTable.getValueAt(rows[i], 2).toString();
                                            String rev = newTable.getValueAt(rows[i], 3).toString();
                                            String owning_user = newTable.getValueAt(rows[i], 4).toString();

                                            //不允许添加再次添加自己
                                            if (map.get(itemId) == null || !map.get(itemId)) {
                                                addTableRowComp2Table(new String[]{"", matHHPN, matName, itemId, rev, owning_user});
                                            }
                                        }
                                        TableItem[] items = SetGroupAddMaterialPropertyBean2.table.getItems();
                                        System.out.println("items.length ==" + items.length);
                                        for (int i = 0; i < items.length; i++) {
                                            items[i].setText(0, (i + 1) + "");
                                        }
                                        table.update();
                                    }
                                }
                            });
                            dispose();
                        }
                    });
                }
                {
                    cancelButton = new JButton("取消");
                    panel.add(cancelButton);
                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dispose();
                        }
                    });
                }
            }
            centerToScreen();
            setVisible(true);
        }

        private void searchAction() {
            newTable.removeAllRows();
            String HHPN = HHPNField.getText();
            String name = nameField.getText();
            if ("".equals(HHPN)) {
                HHPN = "*";
            }
            if ("".equals(name)) {
                name = "*";
            }

            List<InterfaceAIFComponent> matList = Utils.search("__FX_EDACompRevision_Query",
                    new String[]{"fx8HHPN", "objectName"}, new String[]{HHPN, name});
            System.out.println("matList==" + matList);
            if (matList == null || matList.size() == 0) {
            	JOptionPane.showMessageDialog(contentPanel, "未查询到电子料件", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (matList != null && matList.size() != 0) {
                    for (int i = 0; i < matList.size(); i++) {
                        InterfaceAIFComponent component = matList.get(i);
                        System.out.println("item_id ==" + component.getProperty("item_id"));
                        newTable.addRow(new Object[]{component.getProperty("fx8_HHPN"), component.getProperty("object_name"),
                                component.getProperty("item_id"), component.getProperty("item_revision_id"), component.getProperty("owning_user")});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void deleteTableRow() {
        int[] selectIndexs = table.getSelectionIndices();
        if (selectIndexs != null && selectIndexs.length != 0) {

            isModified = true;
            table.remove(selectIndexs);
        }
        TableItem[] items = table.getItems();
        System.out.println("items.length ==" + items.length);
        for (int i = 0; i < items.length; i++) {
            items[i].setText(0, (i + 1) + "");
        }
        table.update();
    }

}
