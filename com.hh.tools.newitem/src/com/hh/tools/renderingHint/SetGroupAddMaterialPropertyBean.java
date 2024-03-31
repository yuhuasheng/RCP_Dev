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

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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

public class SetGroupAddMaterialPropertyBean extends AbstractPropertyBean<Object> {

    public static Table addMatTable = null;
    private Button addButton = null;
    private Button removeButton = null;

    protected TCProperty tcProperty;

    public SetGroupAddMaterialPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                           Map<?, ?> paramMap) {
        System.out.println(
                " SetGroupAddMaterialPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,\r\n"
                        + "			Map<?, ?> paramMap)");
        initComposite(paramComposite);
    }

    public SetGroupAddMaterialPropertyBean() {
        super();
        System.out.println("SetGroupAddMaterialPropertyBean()");
    }

    public SetGroupAddMaterialPropertyBean(Control arg0) {
        super(arg0);
        System.out.println("SetGroupAddMaterialPropertyBean(Control arg0)");
    }

    private void initComposite(Composite parentComposite) {
        Composite composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout(gridLayout);

        addMatTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        addMatTable.setHeaderVisible(true);// 设置显示表头
		addMatTable.setLinesVisible(true);// 设置显示表格线/*
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 500;
        addMatTable.setLayoutData(gridData);

        TableColumn tableColumn = new TableColumn(addMatTable, SWT.NONE);
        tableColumn.setText("HHPN");
        tableColumn.setWidth(220);

        tableColumn = new TableColumn(addMatTable, SWT.NONE);
        tableColumn.setText("名称");
        tableColumn.setWidth(300);

        tableColumn = new TableColumn(addMatTable, SWT.NONE);
        tableColumn.setText("");
        tableColumn.setWidth(0);

        addMatTable.setBackground(parentComposite.getBackground());

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
                new AddMatDialog();
            }
        };

        Listener removelistener = new Listener() {
            // 移除
            @Override
            public void handleEvent(Event paramEvent) {
                int index = addMatTable.getSelectionIndex();
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
                int index = addMatTable.getSelectionIndex();
                if (index >= 0) {
                    removeButton.setEnabled(true);
                } else {
                    removeButton.setEnabled(false);
                }
            }
        };

        addButton.addListener(SWT.Selection, addlistener);
        removeButton.addListener(SWT.Selection, removelistener);
        addMatTable.addListener(SWT.Selection, tablelistener);
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        System.out.println("======Add isPropertyModified(TCProperty arg0)======");
        return false;
    }

    @Override
    public Object getEditableValue() {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (TableItem tableItem : addMatTable.getItems()) {
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
            System.out.println("======Add getEditableValue======" + new Gson().toJson(list));
            return new Gson().toJson(list);
        }
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Add getPropertyToSave(TCProperty arg0)======");
        return null;
    }

    @Override
    public void load(TCProperty tcProperty) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Add load(TCProperty arg0)======");
        this.tcProperty = tcProperty;
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
        System.out.println("======Add load(TCPropertyDescriptor arg0)======");
    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub
        System.out.println("======Add setModifiable(boolean arg0)======");
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub
        System.out.println("======Add setUIFValue(Object arg0)======");
    }

    @Override
    public void load(Object arg0, boolean arg1) {
        // TODO Auto-generated method stub
        System.out.println("======Add load(Object arg0, boolean arg1)======");
        super.load(arg0, arg1);
    }

    @Override
    public void load(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Add load(TCComponent arg0)======");
        super.load(arg0);
    }

    @Override
    public void load(TCComponentType arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Add load(TCComponentType arg0)======");
        super.load(arg0);
    }

    private void addTableRowComp2Table(String[] processStrs) {
        TableItem tableItem = new TableItem(addMatTable, SWT.NONE);
        tableItem.setText(new String[]{processStrs[0], processStrs[1], processStrs[2]});
        addMatTable.update();
    }

    private void deleteTableRow() {
        int[] selectIndexs = addMatTable.getSelectionIndices();
        if (selectIndexs != null && selectIndexs.length != 0) {
            addMatTable.remove(selectIndexs);
        }
        addMatTable.update();
    }

    class AddMatDialog extends AbstractAIFDialog {
        private static final long serialVersionUID = 1L;
        private final JPanel contentPanel = new JPanel();
        private int height = 25;
        private iTextField HHPNField;
        private iTextField nameField;
        private JButton okButton;
        private JButton cancelButton;
        private TCTable table;
        private String[] titles = new String[] { "HHPN", "名称", "" };

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
                                    TableItem[] tableItems = addMatTable.getItems();
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
                                            TableItem[] tableItems = addMatTable.getItems();
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

        private void searchAction() {
            table.removeAllRows();
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
                        InterfaceAIFComponent com = matList.get(i);
                        table.addRow(new Object[]{com.getProperty("fx8_HHPN"), com.getProperty("object_name"),
                                com.getProperty("item_id")});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//	@Override
//	public void addFocusListener() {
//		System.out.println("======Add addFocusListener()======");
//		super.addFocusListener();
//	}
//
//	@Override
//	public void addListener(Listener arg0) {
//		System.out.println("======Add addListener(Listener arg0)======");
//		super.addListener(arg0);
//	}
//
//	@Override
//	public void addPropertyChangeListener(IPropertyChangeListener arg0) {
//		System.out.println("======Add addPropertyChangeListener(IPropertyChangeListener arg0)======");
//		super.addPropertyChangeListener(arg0);
//	}
//
//	@Override
//	protected void bindValues(TCProperty arg0) {
//		System.out.println("======Add bindValues(TCProperty arg0)======");
//		super.bindValues(arg0);
//	}
//
//	@Override
//	protected void bindVisibility() {
//		System.out.println("======Add bindVisibility()======");
//		super.bindVisibility();
//	}

    @Override
    public void dispose() {
        System.out.println("======Add dispose()======");
        // TODO Auto-generated method stub
        if (tcProperty != null) {
            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            for (TableItem tableItem : addMatTable.getItems()) {
                String hhpn = tableItem.getText(0);
                String name = tableItem.getText(1);
                String itemId = tableItem.getText(2);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("hhpn", hhpn);
                map.put("name", name);
                map.put("itemId", itemId);
                list.add(map);
            }

            System.out.println("======Add dispose======" + new Gson().toJson(list));

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

//	@Override
//	protected void fireBeanModifiedPropertyChangeEvent(PropertyChangeEvent arg0) {
//		System.out.println("======Add fireBeanModifiedPropertyChangeEvent(PropertyChangeEvent arg0) ======");
//		super.fireBeanModifiedPropertyChangeEvent(arg0);
//	}
//
//	@Override
//	protected void firePropertyChange(Object arg0, String arg1, Object arg2, Object arg3) {
//		System.out.println("======Add firePropertyChange(Object arg0, String arg1, Object arg2, Object arg3) ======");
//		super.firePropertyChange(arg0, arg1, arg2, arg3);
//	}
//
//	@Override
//	public Map getBeanParamTable() {
//		System.out.println("======Add getBeanParamTable() ======");
//		return super.getBeanParamTable();
//	}
//
//	@Override
//	public Control getControl() {
//		System.out.println("======Add getControl() ======");
//		return super.getControl();
//	}
//
//	@Override
//	public IBOCreateDefinition getCreateDefintion() {
//		// TODO Auto-generated method stub
//		System.out.println("======Add getCreateDefintion() ======");
//		return super.getCreateDefintion();
//	}
//
//	@Override
//	public AIFPropertyDataBean<?> getDataBeanViewModel() {
//		// TODO Auto-generated method stub
//		System.out.println("======Add getDataBeanViewModel() ======");
//		return super.getDataBeanViewModel();
//	}
//
//	@Override
//	public String getDefaultValue() {
//		// TODO Auto-generated method stub
//		System.out.println("======Add getDefaultValue() ======");
//		return super.getDefaultValue();
//	}
//
//	@Override
//	protected String getDefaultValueForDate() {
//		System.out.println("======Add getDefaultValueForDate() ======");
//		// TODO Auto-generated method stub
//		return super.getDefaultValueForDate();
//	}
//
//	@Override
//	public String getDescription() {
//		System.out.println("======Add getDescription() ======");
//		// TODO Auto-generated method stub
//		return super.getDescription();
//	}
//
//	@Override
//	public Composite getLabelComposite() {
//		System.out.println("======Add getLabelComposite() ======");
//		// TODO Auto-generated method stub
//		return super.getLabelComposite();
//	}
//
//	@Override
//	public boolean getMandatory() {
//		System.out.println("======Add getMandatory() ======");
//		// TODO Auto-generated method stub
//		return super.getMandatory();
//	}
//
//	@Override
//	public boolean getModifiable() {
//		System.out.println("======Add getModifiable() ======");
//		// TODO Auto-generated method stub
//		return super.getModifiable();
//	}
//
//	@Override
//	public String getProperty() {
//		System.out.println("======Add getProperty() ======");
//		// TODO Auto-generated method stub
//		return super.getProperty();
//	}
//
//	@Override
//	public TCPropertyDescriptor getPropertyDescriptor() {
//		System.out.println("======Add getPropertyDescriptor() ======");
//		// TODO Auto-generated method stub
//		return super.getPropertyDescriptor();
//	}
//
//	@Override
//	public TCProperty getPropertyToSave(TCComponent arg0) throws Exception {
//		System.out.println("======Add getPropertyToSave(TCComponent arg0) ======");
//		// TODO Auto-generated method stub
//		return super.getPropertyToSave(arg0);
//	}
//
//	@Override
//	protected Viewer getViewer() {
//		System.out.println("======Add getViewer() ======");
//		// TODO Auto-generated method stub
//		return super.getViewer();
//	}
//
//	@Override
//	public boolean isDirty() {
//		System.out.println("====== Add isDirty() ======");
//		// TODO Auto-generated method stub
//		return super.isDirty();
//	}
//
//	@Override
//	public boolean isForNumericPropertyType() {
//		System.out.println("======Add  isForNumericPropertyType() ======");
//		// TODO Auto-generated method stub
//		return super.isForNumericPropertyType();
//	}
//
//	@Override
//	public boolean isMandatory() {
//		// TODO Auto-generated method stub
//		System.out.println("======Add isMandatory()======");
//		return super.isMandatory();
//	}
//
//	@Override
//	public boolean isPropertyModified(TCComponent arg0) throws Exception {
//		System.out.println("======Add isPropertyModified(TCComponent arg0)======");
//		// TODO Auto-generated method stub
//		return super.isPropertyModified(arg0);
//	}
//
//	@Override
//	public void processPropertyInfo(Map arg0) {
//		System.out.println("======Add processPropertyInfo(Map arg0)======");
//		// TODO Auto-generated method stub
//		super.processPropertyInfo(arg0);
//	}
//
//	@Override
//	public void propertyChange(PropertyChangeEvent arg0) {
//		System.out.println("======Add propertyChange(PropertyChangeEvent arg0)======");
//		// TODO Auto-generated method stub
//		super.propertyChange(arg0);
//	}
//
//	@Override
//	public void removePropertyChangeListener(IPropertyChangeListener arg0) {
//		System.out.println("======Add removePropertyChangeListener(IPropertyChangeListener arg0)======");
//		// TODO Auto-generated method stub
//		super.removePropertyChangeListener(arg0);
//	}
//
//	@Override
//	public void save(TCComponent arg0) throws Exception {
//		System.out.println("======Add save(TCComponent arg0)======");
//		// TODO Auto-generated method stub
//		super.save(arg0);
//	}
//
//	@Override
//	public void save(TCProperty arg0) throws Exception {
//		System.out.println("======Add save(TCProperty arg0)======");
//		// TODO Auto-generated method stub
//		super.save(arg0);
//	}
//
//	@Override
//	public TCProperty saveProperty(TCComponent arg0) throws Exception {
//		System.out.println("======Add saveProperty(TCComponent arg0)======");
//		// TODO Auto-generated method stub
//		return super.saveProperty(arg0);
//	}
//
//	@Override
//	public TCProperty saveProperty(TCProperty arg0) throws Exception {
//		System.out.println("======Add saveProperty(TCProperty arg0)======");
//		// TODO Auto-generated method stub
//		return super.saveProperty(arg0);
//	}
//
//	@Override
//	protected void setAIFPropertyDataBean(TCProperty arg0) {
//		System.out.println("======Add setAIFPropertyDataBean(TCProperty arg0)======");
//		// TODO Auto-generated method stub
//		super.setAIFPropertyDataBean(arg0);
//	}
//
//	@Override
//	public void setBeanLabel(Label arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("======Add setBeanLabel(Label arg0)======");
//		super.setBeanLabel(arg0);
//	}
//
//	@Override
//	public void setBeanParamTable(Map arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("======Add setBeanParamTable(Map arg0)======");
//		super.setBeanParamTable(arg0);
//	}
//
//	@Override
//	public void setContextData(Map<String, Object> arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("======Add setContextData(Map<String, Object> arg0)======");
//		super.setContextData(arg0);
//	}
//
//	@Override
//	public void setControl(Control arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("======Add  setControl(Control arg0)======");
//		super.setControl(arg0);
//	}
//
//	@Override
//	public void setCreateDefintion(IBOCreateDefinition arg0) {
//		System.out.println("======Add setCreateDefintion(IBOCreateDefinition arg0)======");
//		// TODO Auto-generated method stub
//		super.setCreateDefintion(arg0);
//	}
//
//	@Override
//	public boolean setDefaultAsUIFvalue() {
//		// TODO Auto-generated method stub
//		System.out.println("======Add setDefaultAsUIFvalue()======");
//		return super.setDefaultAsUIFvalue();
//	}
//
//	@Override
//	public void setDirty(boolean arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setDirty(boolean arg0)======");
//		super.setDirty(arg0);
//	}
//
//	@Override
//	public void setFormProvider(IFormProvider arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setFormProvider(IFormProvider arg0)======");
//		super.setFormProvider(arg0);
//	}
//
//	@Override
//	public void setLabelComposite(Composite arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setLabelComposite(Composite arg0)======");
//		super.setLabelComposite(arg0);
//	}
//
//	@Override
//	public void setMandatory(boolean arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setMandatory(boolean arg0)======");
//		super.setMandatory(arg0);
//	}
//
//	@Override
//	public void setOperationName(String arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setOperationName(String arg0)======");
//		super.setOperationName(arg0);
//	}
//
//	@Override
//	public void setProperty(String arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setProperty(String arg0)======");
//		super.setProperty(arg0);
//	}
//
//	@Override
//	protected void setSeedValue(Object arg0, Object arg1) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setSeedValue(Object arg0, Object arg1)======");
//		super.setSeedValue(arg0, arg1);
//	}
//
//	@Override
//	public void setViewer(Viewer arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setViewer(Viewer arg0)======");
//		super.setViewer(arg0);
//	}
//
//	@Override
//	public void setVisible(boolean arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setVisible(boolean arg0)======");
//		super.setVisible(arg0);
//	}
//
//	@Override
//	public void setupDataBinding(TCProperty arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("====== Add setupDataBinding(TCProperty arg0)======");
//		super.setupDataBinding(arg0);
//	}
//
//	@Override
//	public void validate() {
//		System.out.println("====== Add validate() ======");
//		// TODO Auto-generated method stub
//		super.validate();
//	}

}
