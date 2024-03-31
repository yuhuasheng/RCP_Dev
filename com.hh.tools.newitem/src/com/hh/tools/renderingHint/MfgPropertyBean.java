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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class MfgPropertyBean extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    protected Button searchButton;
    protected static Text MfrText;
    private Map<String, TCComponentItemRevision> mfrMap = new HashMap<String, TCComponentItemRevision>();
    public static TCComponentItemRevision selectMfr;
    private Registry reg = Registry.getRegistry("com.hh.tools.renderingHint.renderingHint");

    public MfgPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public MfgPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                           Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(paramComposite);
    }

    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 5;
        this.composite.setLayout(gridLayout);

        MfrText = new Text(this.composite, SWT.BORDER | SWT.LEFT);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 20;
        gridData.widthHint = 320;
        MfrText.setLayoutData(gridData);
        MfrText.setEditable(false);

        searchButton = new Button(this.composite, SWT.NONE);
        searchButton.setText(reg.getString("mfgSearch.MSG"));
        searchButton.computeSize(80, 25);
        searchButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners(parentComposite);
        setControl(this.composite);

    }

    public static String getText() {
        String text = "";
        if (MfrText != null) {
            text = MfrText.getText();
        }
        return text;
    }

    private void addListeners(final Composite parentComposite) {
        Listener addlistener = new Listener() {

            @Override
            public void handleEvent(Event event) {
                AssignMfgDialog dialog = new AssignMfgDialog();
            }

        };
        searchButton.addListener(SWT.Selection, addlistener);
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
        System.out.println("getProperty");
        System.out.println("isModified == " + isModified);
        System.out.println("tcProperty == " + tcProperty);
        return super.getProperty();
    }

    @Override
    public Object getEditableValue() {
        return MfrText.getText();
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
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        System.out.println("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {

        return null;
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load HHPN TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        String value = tcProperty.getStringValue();
        MfrText.setText(value);
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        tcPropertyDescriptor = paramTCPropertyDescriptor;
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0)
            MfrText.setText(defaultValue);
    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLabelComposite(Composite arg0) {
        // TODO Auto-generated method stub
        System.out.println("setLabelComposite 12");

        Composite labelComposite = arg0;
        // GridLayout localGridLayout = new GridLayout(3, false);
        GridLayout layout = (GridLayout) labelComposite.getLayout();
        layout.numColumns = 2;
        System.out.println("layout == " + layout);
        Label label41 = toolkit.createLabel(labelComposite, "*");
        // label41.setLayoutData(layout);
        label41.setForeground(labelComposite.getDisplay().getSystemColor(3));

        super.setLabelComposite(arg0);
    }

    protected String getDefaultExportDirectory() {
        AbstractAIFSession session = AIFUtility.getCurrentApplication().getSession();
        String s = Utilities.getCookie("filechooser", "DatasetExport.DIR", true);
        if (s == null || s.length() == 0) {
            TCPreferenceService tcpreferenceservice = ((TCSession) session).getPreferenceService();
            s = tcpreferenceservice.getString(0, "defaultExportDirectory");
        }
        if (s != null) {
            s = s.trim();
            if (s.length() == 0) {
                s = null;
            }
        }
        return s;
    }

    class AssignMfgDialog extends AbstractAIFDialog {
        private final JPanel contentPanel = new JPanel();
        private int height = 25;
        //		private iTextField idField;
        private iTextField nameField;
        private JButton searchButton;
        private TCTable table;
        private String[] titles = new String[]{reg.getString("seqName.MSG"), reg.getString("mfgName.MSG")};

        public AssignMfgDialog() {
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
                                    MfrText.setText(name);
                                    selectMfr = mfrMap.get(name);
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

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        if (tcProperty != null) {
            try {
                com.setProperty(property, MfrText.getText());
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.dispose();
    }
}
