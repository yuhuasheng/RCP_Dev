package com.hh.tools.checkList.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;

import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.TableUtil;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;

public class CheckListDialog extends AbstractAIFDialog {
    private TCSession session;
    public static Registry reg = null;
    public static TCComponentForm form;
    private JPanel contentPanel = new JPanel();
    private String[] titles = {"序号","项次","检查项", "是否已执行","对象"};
    private String[] dgnReltitles = {"序号","检查项", "是否已执行","对象"};
    private TCTable table;

    @SuppressWarnings("static-access")
    public CheckListDialog(TCSession session, TCComponentForm form) {
        super(true);
        this.session = session;
        this.form = form;
        this.reg = Registry.getRegistry("com.hh.tools.checkList.checkList");
        initUI();
    }

    /**
	 * 初始化窗体 Create by 郑良 2020.05.10
	 */
    public void initUI() {
        setTitle(reg.getString("title.MSG"));
        setBounds(100, 100, 1150, 780);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);
        {
            JPanel panel = new JPanel();

            contentPanel.add(panel);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            {
                JPanel panel1 = new JPanel();
                panel.add(panel1);
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));


                JPanel panel_1 = new JPanel();

                panel1.add(panel_1);
                panel_1.setLayout(new BorderLayout(0, 0));

                JScrollPane scrollPane = new JScrollPane(
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                panel_1.add(scrollPane, BorderLayout.CENTER);
                String type = form.getType();
                if (ItemTypeName.CUSTOMERRVWFORM.equals(type) || ItemTypeName.SAMPLERVWFORM.equals(type)
                        || ItemTypeName.DGNRVWFORM.equals(type)) {
                    table = new TCTable(session, titles) {
                        public boolean isCellEditable(int arg0, int arg1) {
                            if (arg1 == 3) {
                                TCComponentUser currentUser = session.getUser();
                                try {
                                    TCComponentUser user = (TCComponentUser) form.getReferenceProperty("owning_user");
                                    if (!user.equals(currentUser)) {
                                        return false;
                                    } else {
                                        return true;
                                    }

                                } catch (TCException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            } else {
                                return false;
                            }

                        }

                        ;
                    };
                    table.setRowHeight(40);
                    Utils.hideTableColumn(table, 4);
                    scrollPane.setViewportView(table);
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    table.getTableHeader().setResizingAllowed(true);
                    table.getTableHeader().setReorderingAllowed(false);

                    TableColumnModel tcm = table.getColumnModel();
                    tcm.getColumn(2).setCellEditor(new TableUtil.JTextAreaCellEditor());
                    tcm.getColumn(2).setCellRenderer(new TableUtil.JTextAreaCellRenderer());
                    tcm.getColumn(3).setCellEditor(new TableUtil.CheckBoxCellEditor());
                    tcm.getColumn(3).setCellRenderer(new TableUtil.CWCheckBoxRenderer());
                    TableUtil.setColumnSize(table, 0, 50, 50, 10);
                    TableUtil.setColumnSize(table, 1, 250, 250, 10);
                    TableUtil.setColumnSize(table, 2, 600, 600, 10);
                    TableUtil.setColumnSize(table, 3, 50, 50, 10);
                } else if (ItemTypeName.DGNRELEASEDFORM.equals(type)) {
                    table = new TCTable(session, dgnReltitles) {
                        public boolean isCellEditable(int arg0, int arg1) {
                            if (arg1 == 2) {
                                TCComponentUser currentUser = session.getUser();
                                try {
                                    TCComponentUser user = (TCComponentUser) form.getReferenceProperty("owning_user");
                                    if (!user.equals(currentUser)) {
                                        return false;
                                    } else {
                                        return true;
                                    }

                                } catch (TCException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            } else {
                                return false;
                            }

                        }

                        ;
                    };
                    table.setRowHeight(40);
                    Utils.hideTableColumn(table, 3);
                    scrollPane.setViewportView(table);
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    table.getTableHeader().setResizingAllowed(true);
                    table.getTableHeader().setReorderingAllowed(false);

                    TableColumnModel tcm = table.getColumnModel();
                    tcm.getColumn(2).setCellEditor(new TableUtil.CheckBoxCellEditor());
                    tcm.getColumn(2).setCellRenderer(new TableUtil.CWCheckBoxRenderer());
                    TableUtil.setColumnSize(table, 0, 50, 50, 10);
                    TableUtil.setColumnSize(table, 1, 600, 600, 10);
                    TableUtil.setColumnSize(table, 2, 50, 50, 10);
                }
                addChecklistToTable(table);
            }
        }

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("确定");
                TCComponentUser currentUser = session.getUser();
                try {
                    TCComponentUser user = (TCComponentUser) form.getReferenceProperty("owning_user");
                    if (!user.equals(currentUser)) {
                        okButton.setEnabled(false);
                    } else {
                        okButton.setEnabled(true);
                    }

                } catch (TCException e) {
                    e.printStackTrace();
                }
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                saveChecklist();
                            }

                        }).start();

                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("取消");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        setVisible(true);
        this.setLocationRelativeTo(null);
    }

    private void addChecklistToTable(TCTable table) {
        try {
            String type = form.getType();
            if (ItemTypeName.CUSTOMERRVWFORM.equals(type)) {
                TCProperty prop = form.getTCProperty("fx8_CustomerRVWTable");
                TCComponent[] coms = prop.getReferenceValueArray();
                if (coms != null && coms.length > 0) {
                    for (int i = 0; i < coms.length; i++) {
                        TCComponent com = coms[i];
                        String category = com.getProperty("fx8_Category");
                        String checklist = com.getProperty("fx8_Checklist");
                        boolean isDo = com.getLogicalProperty("fx8_IsDo");
                        table.addRow(new Object[]{i + 1, category, checklist, isDo, com});
                    }
                }
            }
            if (ItemTypeName.SAMPLERVWFORM.equals(type)) {
                TCProperty prop = form.getTCProperty("fx8_SampleRVWTable");
                TCComponent[] coms = prop.getReferenceValueArray();
                if (coms != null && coms.length > 0) {
                    for (int i = 0; i < coms.length; i++) {
                        TCComponent com = coms[i];
                        String category = com.getProperty("fx8_Category");
                        String checklist = com.getProperty("fx8_Checklist");
                        boolean isDo = com.getLogicalProperty("fx8_IsDo");
                        table.addRow(new Object[]{i + 1, category, checklist, isDo, com});
                    }
                }
            }
            if (ItemTypeName.DGNRVWFORM.equals(type)) {
                TCProperty prop = form.getTCProperty("fx8_DgnRVWTable");
                TCComponent[] coms = prop.getReferenceValueArray();
                if (coms != null && coms.length > 0) {
                    for (int i = 0; i < coms.length; i++) {
                        TCComponent com = coms[i];
                        String category = com.getProperty("fx8_Category");
                        String checklist = com.getProperty("fx8_Checklist");
                        boolean isDo = com.getLogicalProperty("fx8_IsDo");
                        table.addRow(new Object[]{i + 1, category, checklist, isDo, com});
                    }
                }
            }
            if (ItemTypeName.DGNRELEASEDFORM.equals(type)) {
                TCProperty prop = form.getTCProperty("fx8_DgnReleasedTable");
                TCComponent[] coms = prop.getReferenceValueArray();
                if (coms != null && coms.length > 0) {
                    for (int i = 0; i < coms.length; i++) {
                        TCComponent com = coms[i];
                        String checklist = com.getProperty("fx8_Checklist");
                        boolean isDo = com.getLogicalProperty("fx8_IsDo");
                        table.addRow(new Object[]{i + 1, checklist, isDo, com});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //保存checklist
    private void saveChecklist() {
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        try {
            int rows = table.getRowCount();
            if (rows > 0) {
                String type = form.getType();
                if (ItemTypeName.CUSTOMERRVWFORM.equals(type)) {
                    TCProperty prop = form.getTCProperty("fx8_CustomerRVWTable");
                    List<TCComponent> list = initTablePropList(type);
                    prop.setPropertyArrayData(list.toArray());
                    form.setTCProperty(prop);
                }
                if (ItemTypeName.SAMPLERVWFORM.equals(type)) {
                    TCProperty prop = form.getTCProperty("fx8_SampleRVWTable");
                    List<TCComponent> list = initTablePropList(type);
                    prop.setPropertyArrayData(list.toArray());
                    form.setTCProperty(prop);
                }
                if (ItemTypeName.DGNRVWFORM.equals(type)) {
                    TCProperty prop = form.getTCProperty("fx8_DgnRVWTable");
                    List<TCComponent> list = initTablePropList(type);
                    prop.setPropertyArrayData(list.toArray());
                    form.setTCProperty(prop);
                }
                if (ItemTypeName.DGNRELEASEDFORM.equals(type)) {
                    TCProperty prop = form.getTCProperty("fx8_DgnReleasedTable");
                    List<TCComponent> list = initTablePropList(type);
                    prop.setPropertyArrayData(list.toArray());
                    form.setTCProperty(prop);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
    }

    private List<TCComponent> initTablePropList(String type) throws TCException {
        List<TCComponent> list = new ArrayList<>();
        int rows = table.getRowCount();
        if (rows > 0) {
            if (ItemTypeName.CUSTOMERRVWFORM.equals(type) || ItemTypeName.SAMPLERVWFORM.equals(type)
                    || ItemTypeName.DGNRVWFORM.equals(type)) {
                for (int i = 0; i < rows; i++) {
                    String category = table.getValueAt(i, 1).toString();
                    String checkList = table.getValueAt(i, 2).toString();
                    Boolean isDo = (Boolean) table.getValueAt(i, 3);
                    TCComponent com = (TCComponent) table.getValueAt(i, 4);
                    com.setProperty("fx8_Category", category);
                    com.setProperty("fx8_Checklist", checkList);
                    com.setLogicalProperty("fx8_IsDo", isDo);
                    list.add(com);
                }
            } else if (ItemTypeName.DGNRELEASEDFORM.equals(type)) {
                for (int i = 0; i < rows; i++) {
                    String checkList = table.getValueAt(i, 1).toString();
                    Boolean isDo = (Boolean) table.getValueAt(i, 2);
                    TCComponent com = (TCComponent) table.getValueAt(i, 3);
                    com.setProperty("fx8_Checklist", checkList);
                    com.setLogicalProperty("fx8_IsDo", isDo);
                    list.add(com);
                }
            }

        }

        return list;
    }

}
