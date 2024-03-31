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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class SetGroupDelMaterialPropertyBean extends AbstractPropertyBean<Object> {

    private Table delMatTable = null;
    private Button addButton = null;
    private Button removeButton = null;

    private TCProperty tcProperty;

    public SetGroupDelMaterialPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                           Map<?, ?> paramMap) {
        initComposite(paramComposite);
    }

    private void initComposite(Composite parentComposite) {
        Composite composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout(gridLayout);

        delMatTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        delMatTable.setHeaderVisible(true);// 设置显示表头
		delMatTable.setLinesVisible(true);// 设置显示表格线/*
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 500;
        delMatTable.setLayoutData(gridData);

        TableColumn tableColumn = new TableColumn(delMatTable, SWT.NONE);
        tableColumn.setText("HHPN");
        tableColumn.setWidth(220);

        tableColumn = new TableColumn(delMatTable, SWT.NONE);
        tableColumn.setText("名称");
        tableColumn.setWidth(300);

        tableColumn = new TableColumn(delMatTable, SWT.NONE);
        tableColumn.setText("");
        tableColumn.setWidth(0);

        delMatTable.setBackground(parentComposite.getBackground());

        Label spaceLabel = new Label(composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addButton = new Button(composite, SWT.NONE);
        addButton.setText("添加");
        addButton.computeSize(80, 25);
        addButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        addButton.setVisible(true);

        spaceLabel = new Label(composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        removeButton = new Button(composite, SWT.NONE);
        removeButton.setText("移除");
        removeButton.computeSize(80, 25);
        removeButton.setEnabled(false);
        removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        removeButton.setVisible(true);

        spaceLabel = new Label(composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners();
        setControl(composite);
    }

    private void addListeners() {
        Listener addlistener = new Listener() {
            // 新增
            @Override
            public void handleEvent(Event paramEvent) {
                new DelMatDialog();
            }
        };

        Listener removelistener = new Listener() {
            // 移除
            @Override
            public void handleEvent(Event paramEvent) {
                System.out.println("handleEvent 2");
                int index = delMatTable.getSelectionIndex();
                if (index < 0) {
                	Utils.infoMessage("请选择需要移除的料件！");
                    return;
                }
                deleteTableRow();
            }
        };

        Listener tablelistener = new Listener() {
            @Override
            public void handleEvent(Event paramEvent) {
                int index = delMatTable.getSelectionIndex();
                if (index >= 0) {
                    removeButton.setEnabled(true);
                } else {
                    removeButton.setEnabled(false);
                }
            }
        };

        addButton.addListener(SWT.Selection, addlistener);
        removeButton.addListener(SWT.Selection, removelistener);
        delMatTable.addListener(SWT.Selection, tablelistener);
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        System.out.println("======Del isPropertyModified(TCProperty arg0)======");
        return false;
    }

    @Override
    public Object getEditableValue() {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (TableItem tableItem : delMatTable.getItems()) {
            String hhpn = tableItem.getText(0);
            String name = tableItem.getText(1);
            String itemId = tableItem.getText(2);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("hhpn", hhpn);
            map.put("name", name);
            map.put("itemId", itemId);
            list.add(map);
        }
        if (list.size() == 0) {
            return null;
        } else {
            System.out.println("======Del getEditableValue======" + new Gson().toJson(list));
            return new Gson().toJson(list);
        }
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Del getPropertyToSave(TCProperty arg0)======");
        return null;
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Del load(TCProperty arg0)======");
        tcProperty = arg0;
        String selectedValues = tcProperty.getStringValue();
        List<LinkedTreeMap<String, String>> list = new Gson().fromJson(selectedValues, List.class);
        if (list != null) {
            for (LinkedTreeMap<String, String> map : list) {
                String hhpn = map.get("hhpn");
                String name = map.get("name");
                String itemId = map.get("itemId");
                addTableRowComp2Table(new String[]{hhpn, name, itemId});
            }
        }
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Del load(TCPropertyDescriptor arg0)======");
    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub
        System.out.println("======Del setModifiable(boolean arg0)======");
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub
        System.out.println("======Del setUIFValue(Object arg0)======");
    }

    @Override
    public void load(Object arg0, boolean arg1) {
        // TODO Auto-generated method stub
        System.out.println("======Del load(Object arg0, boolean arg1)======");
        super.load(arg0, arg1);
    }

    @Override
    public void load(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Del load(TCComponent arg0)======");
        super.load(arg0);
    }

    @Override
    public void load(TCComponentType arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Del load(TCComponentType arg0)======");
        super.load(arg0);
    }

    @Override
    public void dispose() {
        System.out.println("======Del dispose()======");
        // TODO Auto-generated method stub
        if (tcProperty != null) {
            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            for (TableItem tableItem : delMatTable.getItems()) {
                String hhpn = tableItem.getText(0);
                String name = tableItem.getText(1);
                String itemId = tableItem.getText(2);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("hhpn", hhpn);
                map.put("name", name);
                map.put("itemId", itemId);
                list.add(map);
            }

            System.out.println("======Del dispose======" + new Gson().toJson(list));
            try {
                if (list.size() != 0) {
                    tcProperty.getTCComponent().setProperty(property, new Gson().toJson(list));
                }
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.dispose();
    }

    private void addTableRowComp2Table(String[] processStrs) {
        TableItem tableItem = new TableItem(delMatTable, SWT.NONE);
        tableItem.setText(new String[]{processStrs[0], processStrs[1], processStrs[2]});
        delMatTable.update();
    }

    private void deleteTableRow() {
        int[] selectIndexs = delMatTable.getSelectionIndices();
        if (selectIndexs != null && selectIndexs.length != 0) {
            delMatTable.remove(selectIndexs);
        }
        delMatTable.update();
    }

    class DelMatDialog extends AbstractAIFDialog {
        private static final long serialVersionUID = 1L;
        private final JPanel contentPanel = new JPanel();
        private JButton okButton;
        private JButton cancelButton;
        private TCTable table;
        private String[] titles = new String[] { "HHPN", "名称", "" };

        public DelMatDialog() {
            super(AIFUtility.getActiveDesktop().getFrame());
            initUI();
        }

        private void initUI() {
        	setTitle("移除电子料件");
            setAlwaysOnTop(true);
            setBounds(100, 100, 750, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel();
                panel.setBorder(new TitledBorder(null, "电子料件列表", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                table = new TCTable(session, titles) {
                    private static final long serialVersionUID = 1L;

                    public boolean isCellEditable(int arg0, int arg1) {
                        return false;
                    }
                };

                JScrollPane scrollTablePanel = new JScrollPane();
                scrollTablePanel.setBackground(Color.WHITE);
                scrollTablePanel.setPreferredSize(new Dimension(730, 300));
                scrollTablePanel.setViewportView(table);
                table.getTableHeader().setBackground(Color.WHITE);
                table.setRowHeight(30);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.getTableHeader().setResizingAllowed(true);
                table.getTableHeader().setReorderingAllowed(false);
                panel.add(scrollTablePanel, BorderLayout.CENTER);

                TableColumnModel columnModel = table.getColumnModel();
                javax.swing.table.TableColumn column = columnModel.getColumn(2);
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setWidth(0);
                column.setPreferredWidth(0);

                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            dispose();
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    int row = table.getSelectedRow();
                                    String matHHPN = table.getValueAt(row, 0).toString();
                                    String matName = table.getValueAt(row, 1).toString();
                                    String itemId = table.getValueAt(row, 2).toString();
                                    TableItem[] tableItems = delMatTable.getItems();
                                    ArrayList<String> list = new ArrayList<String>();
                                    if (tableItems != null && tableItems.length != 0) {
                                        for (int i = 0; i < tableItems.length; i++) {
                                            list.add(tableItems[i].getText(2));
                                        }
                                    }
                                    if (list.contains(itemId) == false) {
                                        addTableRowComp2Table(new String[]{matHHPN, matName, itemId});
                                    }
                                }
                            });
                        }
                    }
                });

                try {
                    if (tcProperty != null) {
                        TCComponent groupCom = tcProperty.getTCComponent();
                        TCComponent[] coms = groupCom.getTCProperty("FX8_EDAItemRel").getReferenceValueArray();
                        if (coms != null && coms.length != 0) {
                            for (int i = 0; i < coms.length; i++) {
                                TCComponent com = coms[i];
                                String HHPN = com.getTCProperty("fx8_StandardPN").getStringValue();
                                String name = com.getTCProperty("object_name").getStringValue();
                                String itemId = com.getTCProperty("item_id").getStringValue();
                                table.addRow(new Object[]{HHPN, name, itemId});
                            }
                            table.updateUI();
                        }
                    }
                } catch (TCException e) {
                    e.printStackTrace();
                }
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
                                    int[] rows = table.getSelectedRows();
                                    if (rows != null && rows.length != 0) {
                                        for (int i = 0; i < rows.length; i++) {
                                            String matHHPN = table.getValueAt(rows[i], 0).toString();
                                            String matName = table.getValueAt(rows[i], 1).toString();
                                            String itemId = table.getValueAt(rows[i], 2).toString();
                                            TableItem[] tableItems = delMatTable.getItems();
                                            ArrayList<String> list = new ArrayList<String>();
                                            if (tableItems != null && tableItems.length != 0) {
                                                for (int j = 0; j < tableItems.length; j++) {
                                                    list.add(tableItems[j].getText(2));
                                                }
                                            }
                                            if (list.contains(itemId) == false) {
                                                addTableRowComp2Table(new String[]{matHHPN, matName, itemId});
                                            }
                                        }
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

    }
}
