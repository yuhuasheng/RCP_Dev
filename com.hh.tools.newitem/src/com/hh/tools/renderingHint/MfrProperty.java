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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.eclipse.swt.widgets.Display;

import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.iTextField;

public class MfrProperty extends JPanel implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent {

    private String property;
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;
    private Map<String, TCComponentItemRevision> mfrMap = new HashMap<String, TCComponentItemRevision>();

    private static JTextField mfrText;

    private Registry reg = Registry.getRegistry("com.hh.tools.renderingHint.renderingHint");

    public MfrProperty() {
        super();
        loadPropertyPanel();
    }

    private void loadPropertyPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        mfrText = new JTextField();
        mfrText.setPreferredSize(new Dimension(350, 25));

        JButton button = new JButton(reg.getString("mfgSearch.MSG"));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new Runnable() {
                    public void run() {
                        new AssignMfrDialog();
                    }
                }).start();
            }
        });

        this.add(mfrText, BorderLayout.WEST);
        this.add(button, BorderLayout.CENTER);
    }

    public TCProperty getPropertyToSave(TCComponent paramTCComponent) throws Exception {
        if (this.property != null) {
            Object valueObj = getEditableValue();
            if (null != valueObj) {
                if (mfrMap.get(valueObj) != null) {
                    TCComponent[] refComp = paramTCComponent.getRelatedComponents(RelationName.MFRREL);
                    if (null != refComp && refComp.length > 0) {
                        paramTCComponent.remove(RelationName.MFRREL, refComp);
                    }
                    paramTCComponent.add(RelationName.MFRREL, mfrMap.get(valueObj).getItem());
                }
            }
        }
        this.savable = false;
        return null;
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
//			System.out.println("datesheet================saveProperty(TCComponent paramTCComponent)");
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
//		System.out.println("mfr================saveProperty(TCProperty paramTCProperty)");
//		TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
//		if (this.savable) {
//			return localTCProperty;
//		}
        return null;
    }

    @Override
    public Object getEditableValue() {
//		System.out.println("datesheet================getEditableValue()");
        return mfrText.getText();
    }

    @Override
    public String getProperty() {
//		System.out.println("datesheet================getProperty()===" + property);
        return this.property;
    }

    @Override
    public boolean isMandatory() {
//		System.out.println("datesheet================isMandatory()");
        return this.mandatory;
    }

    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
//		System.out.println("datesheet================isPropertyModified(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return isPropertyModified(localTCProperty);
        }
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================isPropertyModified(TCProperty paramTCProperty)");
        return true;
    }

    @Override
    public void load(TCComponent paramTCComponent) throws Exception {
//		System.out.println("datesheet================load(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            load(localTCProperty);
        }
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================load(TCProperty paramTCProperty)");
        String propValue = paramTCProperty.getStringValue();
        mfrText.setText(propValue);
    }

    @Override
    public void load(TCComponentType paramTCComponentType) throws Exception {
//		System.out.println("datesheet================load(TCComponentType paramTCComponentType)");
        if (paramTCComponentType != null) {
            TCPropertyDescriptor localTCPropertyDescriptor = paramTCComponentType.getPropertyDescriptor(this.property);
            load(localTCPropertyDescriptor);
        }
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
//		System.out.println("datesheet================load(TCPropertyDescriptor paramTCPropertyDescriptor)");
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0) {
            mfrText.setText(defaultValue);
        }
    }

    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
//		System.out.println("mfr================save(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public void save(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
//		System.out.println("mfr================save(TCProperty arg0)");
    }

    @Override
    public void setMandatory(boolean paramBoolean) {
//		System.out.println("datesheet================setMandatory(boolean paramBoolean)");
        this.mandatory = paramBoolean;
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
//		System.out.println("datesheet================setModifiable(boolean paramBoolean)");
        this.modifiable = paramBoolean;
    }

    @Override
    public void setProperty(String paramString) {
//		System.out.println("datesheet================setProperty(String paramString)===" + property);
        this.property = paramString;
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }

    class AssignMfrDialog extends AbstractAIFDialog {
        private final JPanel contentPanel = new JPanel();
        private int height = 25;
        //		private iTextField idField;
        private iTextField nameField;
        private JButton searchButton;
        private TCTable table;
        private String[] titles = new String[]{reg.getString("seqName.MSG"), reg.getString("mfgName.MSG")};

        public AssignMfrDialog() {
            super(AIFUtility.getActiveDesktop().getFrame());
            initUI();
        }

        private void initUI() {
            setTitle(reg.getString("selectMfg.MSG"));
            setBounds(100, 100, 550, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(new TitledBorder(null, "ËÑË÷", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                contentPanel.add(panel, BorderLayout.NORTH);
                panel.setBackground(Color.white);
//				JLabel idLabel = new JLabel("ID:",JLabel.RIGHT);
//				idLabel.setPreferredSize(new Dimension(100, height));
//				idField = new iTextField();
//				idField.setPreferredSize(new Dimension(150, height));

                JLabel nameLabel = new JLabel(reg.getString("mfgName.MSG"), JLabel.RIGHT);
                nameLabel.setPreferredSize(new Dimension(100, height));
                nameField = new iTextField();
                nameField.setPreferredSize(new Dimension(150, height));

                JButton searchButton = new JButton(reg.getString("mfgSearch.MSG"));
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

//				panel.add("1.1.right.center.preferred.preferred", idLabel);
//				panel.add("1.2.left.center.preferred.preferred", idField);			
                panel.add("1.1.right.center.preferred.preferred", nameLabel);
                panel.add("1.2.left.center.preferred.preferred", nameField);
                panel.add("1.3.right.center.preferred.preferred", searchButton);

            }
            {
                JPanel panel = new JPanel();
                panel.setBorder(new TitledBorder(null, reg.getString("mfgList.MSG"), TitledBorder.LEADING,
                        TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                table = new TCTable(session, titles) {
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

                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            dispose();
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    int row = table.getSelectedRow();
                                    String name = table.getValueAt(row, 1).toString();
                                    mfrText.setText(name);
                                    savable = true;
                                }
                            });
                        }
                    }
                });
            }
            centerToScreen();
            setVisible(true);
        }

        private void searchAction() {
            table.removeAllRows();
//			String itemId = idField.getText();
            String name = nameField.getText();

            List key = new ArrayList();
            List value = new ArrayList();
            key.add(Utils.getTextValue("fx8_MfrStatus"));
            value.add("Approved");
//		    if (!"".equals(itemId)) {
//		    	key.add(Utils.getTextValue("item_id"));
//		    	value.add("*"+itemId+"*");
//		    }	
            if (!"".equals(name)) {
                key.add(Utils.getTextValue("object_name"));
                value.add("*" + name + "*");
            }

            String[] keyArray = new String[key.size()];
            key.toArray(keyArray);
            String[] valueArray = new String[value.size()];
            value.toArray(valueArray);
            List<InterfaceAIFComponent> supplierList = Utils.search("__FX_FindMfr", keyArray, valueArray);
            System.out.println("supplierList==" + supplierList);
            try {
                if (supplierList != null && supplierList.size() > 0) {
                    mfrMap.clear();
                    for (int i = 0; i < supplierList.size(); i++) {
                        InterfaceAIFComponent com = supplierList.get(i);
                        TCComponentItemRevision itemRev = ((TCComponentItem) com).getLatestItemRevision();
                        String objectName = itemRev.getProperty("object_name");
                        mfrMap.put(objectName, itemRev);
                        table.addRow(new Object[]{i + 1, objectName});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
