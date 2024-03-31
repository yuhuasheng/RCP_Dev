package com.hh.tools.customerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.combobox.iComboBox;

public class ComplianceRequireFormRendering extends AbstractRendering {
    private JPanel contentPanel = null;
    private TCComponentForm pcomplianceFome = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");
    public int height = 25;
    private static TCSession session = null;

    private static Map<TCComponentItemRevision, String> PCBEZBOMMap = new HashMap<>();
    private TCComponentForm complianceForm = null;
    private static iComboBox PCBZBOMCustomerComboBox;
    private static iComboBox PCBZBOMIsHFStatusComboBox;
    private static iComboBox PCBZBOMMCDRoHSStatusComboBox;
    private static iComboBox PCBZBOMMDDRoHSStatusComboBox;
    private static iComboBox PCBZBOMFMDREACHStatusComboBox;

    private JTextField mcdTextField;
    private String mcdFileName;
    private String mddFileName;
    private String fmdFileName;
    private iComboBox mcdVerComboBox;
    private iComboBox mcdRohsComboBox;
    private JTextField mddTextField;
    private iComboBox mddVerComboBox;
    private iComboBox mddRohsComboBox;
    private JTextField fmdTextField;
    private iComboBox fmdVerComboBox;
    private iComboBox reachComboBox;
    private iComboBox hfComboBox;
    private iComboBox exemptionComboBox;
    private iComboBox isReachComboBox;
    private iComboBox pzbomCombox;
    private JPanel mcdFilePanel;
    private JPanel mddFilePanel;
    private JPanel fmdFilePanel;

    public ComplianceRequireFormRendering(TCComponent paramTCComponent) throws Exception {
        super(paramTCComponent);
        pcomplianceFome = (TCComponentForm) paramTCComponent;
        session = pcomplianceFome.getSession();
        loadRendering();
    }

    @Override
    public void loadRendering() throws TCException {
        setBounds(100, 100, 980, 800);
        this.setLayout(new BorderLayout());
        contentPanel = getCompliancePanel();

        JScrollPane edaCompScrollPane = new JScrollPane(contentPanel);
        edaCompScrollPane.setPreferredSize(new Dimension(900, 800));
        edaCompScrollPane.setBorder(null);

        this.add(edaCompScrollPane, BorderLayout.CENTER);
    }

    // 加载数据
    private void loadData() {
        try {
            TCProperty prop = pcomplianceFome.getTCProperty(RelationName.COMPLIANCES);
            TCComponent[] childForms = prop.getReferenceValueArray();
            if (childForms != null && childForms.length > 0) {
                for (TCComponent com : childForms) {
                    complianceForm = (TCComponentForm) com;
                    TCComponent item = complianceForm.getReferenceProperty("fx8_PCBEZBOM");
                    pzbomCombox.setSelectedItem(item.getProperty("object_name"));
                    String customer = complianceForm.getProperty("fx8_PCBEZBOMCustomer");
                    String hf = complianceForm.getProperty("fx8_PCBEZBOMHFStatus");
                    String mcdROHS = complianceForm.getProperty("fx8_PCBEZBOMMCDRoHSStatus");
                    String mddROHS = complianceForm.getProperty("fx8_PCBEZBOMMDDRoHSStatus");
                    String reach = complianceForm.getProperty("fx8_PCBEZBOMFMDREACHStatus");
                    PCBZBOMCustomerComboBox.setSelectedItem(customer);
                    PCBZBOMIsHFStatusComboBox.setSelectedItem(hf);
                    PCBZBOMMCDRoHSStatusComboBox.setSelectedItem(mcdROHS);
                    PCBZBOMMDDRoHSStatusComboBox.setSelectedItem(mddROHS);
                    PCBZBOMFMDREACHStatusComboBox.setSelectedItem(reach);
                    mcdVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_MCDVer"));
                    mcdRohsComboBox.setSelectedItem(complianceForm.getProperty("fx8_MCDROHSStatus"));
                    mddVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_MDDVer"));
                    mddRohsComboBox.setSelectedItem(complianceForm.getProperty("fx8_MDDROHSStatus"));
                    fmdVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_FMDVer"));
                    reachComboBox.setSelectedItem(complianceForm.getProperty("fx8_REACHStatus"));
                    boolean isHF = complianceForm.getLogicalProperty("fx8_IsHFStatus");
                    if (isHF) {
                        hfComboBox.setSelectedItem("YES");
                    } else {
                        hfComboBox.setSelectedItem("NO");
                    }
                    boolean IsREACHStatus = complianceForm.getLogicalProperty("fx8_IsREACHStatus");
                    if (IsREACHStatus) {
                        isReachComboBox.setSelectedItem("YES");
                    } else {
                        isReachComboBox.setSelectedItem("NO");
                    }
                    exemptionComboBox.setSelectedItem(complianceForm.getProperty("fx8_Exemption"));

                    TCProperty mcdrel = complianceForm.getTCProperty(RelationName.MCDREL);
                    TCComponent[] mcdFile = mcdrel.getReferenceValueArray();
                    if (mcdFile != null && mcdFile.length > 0) {
                        int i = 1;
                        for (TCComponent tcComponent : mcdFile) {
                            TCComponentDataset dataset = (TCComponentDataset) tcComponent;
                            JLabel label = new JLabel(dataset.getProperty("object_name"), JLabel.LEFT);
                            label.setPreferredSize(new Dimension(400, 25));
                            label.setForeground(Color.blue);
                            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            label.addMouseListener(new FileClickListener(dataset));
                            mcdFilePanel.add(i + ".1.right.center.preferred.preferred", label);
                            i++;
                        }
                    } else {
                        JLabel label = new JLabel("暂无MCD文件", JLabel.LEFT);
                        label.setPreferredSize(new Dimension(400, 25));
                        mcdFilePanel.add("1.1.right.center.preferred.preferred", label);
                    }

                    TCProperty mddrel = complianceForm.getTCProperty(RelationName.MDDREL);
                    TCComponent[] mddFile = mddrel.getReferenceValueArray();
                    if (mddFile != null && mddFile.length > 0) {
                        int i = 1;
                        for (TCComponent tcComponent : mddFile) {
                            TCComponentDataset dataset = (TCComponentDataset) tcComponent;
                            JLabel label = new JLabel(dataset.getProperty("object_name"), JLabel.LEFT);
                            label.setPreferredSize(new Dimension(400, 25));
                            label.setForeground(Color.blue);
                            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            label.addMouseListener(new FileClickListener(dataset));
                            mddFilePanel.add(i + ".1.right.center.preferred.preferred", label);
                            i++;
                        }
                    } else {
                        JLabel label = new JLabel("暂无MDD文件", JLabel.LEFT);
                        label.setPreferredSize(new Dimension(400, 25));
                        mddFilePanel.add("1.1.right.center.preferred.preferred", label);
                    }

                    TCProperty fmdrel = complianceForm.getTCProperty(RelationName.FMDREL);
                    TCComponent[] fmdFile = fmdrel.getReferenceValueArray();
                    if (fmdFile != null && fmdFile.length > 0) {
                        int i = 1;
                        for (TCComponent tcComponent : fmdFile) {
                            TCComponentDataset dataset = (TCComponentDataset) tcComponent;
                            JLabel label = new JLabel(dataset.getProperty("object_name"), JLabel.LEFT);
                            label.setPreferredSize(new Dimension(400, 25));
                            label.setForeground(Color.blue);
                            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            label.addMouseListener(new FileClickListener(dataset));
                            fmdFilePanel.add(i + ".1.right.center.preferred.preferred", label);
                            i++;
                        }
                    } else {
                        JLabel label = new JLabel("暂无FMD文件", JLabel.LEFT);
                        label.setPreferredSize(new Dimension(400, 25));
                        fmdFilePanel.add("1.1.right.center.preferred.preferred", label);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRenderingModified() {
        System.out.println("isRenderingModified==" + super.isRenderingModified());
        System.out.println("是否签出==" + pcomplianceFome.isCheckedOut());
        if (pcomplianceFome.isCheckedOut()) {
            return true;
        }
        return false;
    }

    @Override
    public void saveRendering() {
        try {
            Utils.byPass(true);
            saveValue(complianceForm);
            Utils.byPass(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRendering() {

        ViewEditHelper localViewEditHelper = new ViewEditHelper(component.getSession());
        ViewEditHelper.CKO localCKO = localViewEditHelper.getObjectState(component);
        switch (localCKO) {
            case CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE:
            case CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE:
            case IMPLICITLY_CHECKOUTABLE:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        setRenderingReadWrite();
                        checkApplyButton();
                        System.err.println("IMPLICITLY_CHECKOUTABLE");
                    }
                });
                break;
            case CHECKED_IN:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        setRenderingReadOnly();
                        checkApplyButton();
                        System.err.println("CHECKED_IN");
                    }
                });
                break;
            case NOT_CHECKOUTABLE:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        setRenderingReadOnly();
                        checkApplyButton();
                        System.err.println("NOT_CHECKOUTABLE");
                    }
                });
                break;
            default:
                break;
        }
    }

    private void checkApplyButton() {
        System.out.println("checkApplyButton");
        pzbomCombox.setEnabled(true);
        PCBZBOMCustomerComboBox.setEnabled(true);
        PCBZBOMIsHFStatusComboBox.setEnabled(true);
        PCBZBOMMCDRoHSStatusComboBox.setEnabled(true);
        PCBZBOMMDDRoHSStatusComboBox.setEnabled(true);
        PCBZBOMFMDREACHStatusComboBox.setEnabled(true);
        mcdTextField.setEditable(false);
        mddTextField.setEditable(false);
        fmdTextField.setEditable(false);

    }

    public void saveValue(TCComponentForm form) {
    	System.out.println("保存属性");
        Object mcdVer = mcdVerComboBox.getSelectedItem();
        Object mcdRohs = mcdRohsComboBox.getSelectedItem();
        Object mddVer = mddVerComboBox.getSelectedItem();
        Object mddRohs = mddRohsComboBox.getSelectedItem();
        Object fmdVer = fmdVerComboBox.getSelectedItem();
        Object reach = reachComboBox.getSelectedItem();
        Object isReach = isReachComboBox.getSelectedItem();
        Object isHF = hfComboBox.getSelectedItem();
        Object exemption = exemptionComboBox.getSelectedItem();
        String mcdFilePath = mcdTextField.getText();
        String mddFilePath = mddTextField.getText();
        String fmdFilePath = fmdTextField.getText();

        System.out.println("saveRendering");
        try {
            if (form != null) {
                form.setProperty("fx8_MCDVer", mcdVer.toString());
                form.setProperty("fx8_MCDROHSStatus", mcdRohs.toString());
                form.setProperty("fx8_MDDVer", mddVer.toString());
                form.setProperty("fx8_MDDROHSStatus", mddRohs.toString());
                form.setProperty("fx8_FMDVer", fmdVer.toString());
                form.setProperty("fx8_REACHStatus", reach.toString());
                if ("YES".equals(isHF)) {
                    form.setLogicalProperty("fx8_IsHFStatus", true);
                } else if ("NO".equals(isHF)) {
                    form.setLogicalProperty("fx8_IsHFStatus", false);
                }
                if ("YES".equals(isReach)) {
                    form.setLogicalProperty("fx8_IsREACHStatus", true);
                } else if ("NO".equals(isReach)) {
                    form.setLogicalProperty("fx8_IsREACHStatus", false);
                }
                form.setProperty("fx8_Exemption", exemption.toString());

                if (!Utils.isNull(mcdFilePath)) {
                    String mcd = mcdFileName.substring(0, mcdFileName.lastIndexOf("."));
                    TCComponentDataset mcdDs = null;
                    if (mcdFilePath.endsWith("xlsx")) {
                        mcdDs = CreateObject.createDataSet(Utils.getTCSession(), mcdFilePath, "MSExcelX", mcd, "excel");
                    } else if (mcdFilePath.endsWith("xls")) {
                        mcdDs = CreateObject.createDataSet(Utils.getTCSession(), mcdFilePath, "MSExcel", mcd, "excel");
                    }
                    TCProperty prop = form.getTCProperty(RelationName.MCDREL);
                    TCComponent[] mcds = prop.getReferenceValueArray();
                    if (mcds != null) {
                        TCComponent[] newmcds = new TCComponent[mcds.length + 1];
                        for (int i = 0; i < mcds.length; i++) {
                            newmcds[i] = mcds[i];
                        }
                        newmcds[mcds.length] = mcdDs;
                        prop.setReferenceValueArray(newmcds);
                    } else {
                        prop.setReferenceValueArray(new TCComponent[]{mcdDs});
                    }
                }
                if (!Utils.isNull(mddFilePath)) {
                    String mdd = mddFileName.substring(0, mddFileName.lastIndexOf("."));
                    TCComponentDataset mddDs = null;
                    if (mddFilePath.endsWith("xlsx")) {
                        mddDs = CreateObject.createDataSet(Utils.getTCSession(), mddFilePath, "MSExcelX", mdd, "excel");
                    } else if (mddFilePath.endsWith("xls")) {
                        mddDs = CreateObject.createDataSet(Utils.getTCSession(), mddFilePath, "MSExcel", mdd, "excel");
                    }
                    TCProperty prop = form.getTCProperty(RelationName.MDDREL);
                    TCComponent[] mdds = prop.getReferenceValueArray();
                    if (mdds != null) {
                        TCComponent[] newmdds = new TCComponent[mdds.length + 1];
                        for (int i = 0; i < mdds.length; i++) {
                            newmdds[i] = mdds[i];
                        }
                        newmdds[mdds.length] = mddDs;
                        prop.setReferenceValueArray(newmdds);
                    } else {
                        prop.setReferenceValueArray(new TCComponent[]{mddDs});
                    }
                }
                if (!Utils.isNull(fmdFilePath)) {
                    String fmd = fmdFileName.substring(0, fmdFileName.lastIndexOf("."));
                    TCComponentDataset fmdDs = null;
                    if (fmdFilePath.endsWith("xlsx")) {
                        fmdDs = CreateObject.createDataSet(Utils.getTCSession(), fmdFilePath, "MSExcelX", fmd, "excel");
                    } else if (mddFilePath.endsWith("xls")) {
                        fmdDs = CreateObject.createDataSet(Utils.getTCSession(), fmdFilePath, "MSExcel", fmd, "excel");
                    }
                    TCProperty prop = form.getTCProperty(RelationName.FMDREL);
                    TCComponent[] fmds = prop.getReferenceValueArray();
                    if (fmds != null) {
                        TCComponent[] newmfds = new TCComponent[fmds.length + 1];
                        for (int i = 0; i < fmds.length; i++) {
                            newmfds[i] = fmds[i];
                        }
                        newmfds[fmds.length] = fmdDs;
                        prop.setReferenceValueArray(newmfds);
                    } else {
                        prop.setReferenceValueArray(new TCComponent[]{fmdDs});
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isObjectSavable() {
//		String category = categoryLov.getTextField().getText();
//		if(Utils.isNull(category)){
//			Utils.infoMessage("Category属性栏位为空，不允许保存!");
//			return false;
//		}
        System.out.println("isObjectSavable==" + super.isObjectSavable());
        return super.isObjectSavable();
    }

//	private void custSetRenderingReadWrite() {
//		setRenderingReadWrite();
//	}
//	
//	private void custSetRenderingRead() {
//		setRenderingReadOnly();
//	}

    /**
	 * 获取替代料管理对象布局
	 * @return
	 */
    private JPanel getSubManaPanel() throws Exception {
        JPanel subsManaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel subsLabel = new JLabel("替代料管理对象：");
        pzbomCombox = new iComboBox();

        TCProperty prop = pcomplianceFome.getTCProperty(RelationName.COMPLIANCES);
        TCComponent[] childForms = prop.getReferenceValueArray();
        if (childForms != null && childForms.length > 0) {
            for (TCComponent tcComponent : childForms) {
                TCComponentForm EPIFORM = (TCComponentForm) tcComponent;
                TCComponent item = EPIFORM.getReferenceProperty("fx8_PCBEZBOM");
                TCComponentItemRevision itemRev = (TCComponentItemRevision) item;
                PCBEZBOMMap.put(itemRev, itemRev.getProperty("object_name"));
                pzbomCombox.addItem(itemRev.getProperty("object_name"));
            }
        }

        pzbomCombox.setPreferredSize(new Dimension(250, 25));
        subsManaPanel.add(subsLabel);
        subsManaPanel.add(pzbomCombox);
        return subsManaPanel;
    }

    /**
	 * 获取环保认证等级面板
	 * @return
	 */
    private JPanel getCustomerLabelPanel() {
        JPanel customerLabelPanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        customerLabelPanel.setBorder(
                new TitledBorder(null, "环保认证等级", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel customerLabel = new JLabel("Customer:", JLabel.RIGHT);
        customerLabel.setPreferredSize(new Dimension(150, height));
        PCBZBOMCustomerComboBox = new iComboBox();
        PCBZBOMCustomerComboBox.setPreferredSize(new Dimension(200, height));
        PCBZBOMCustomerComboBox.addItems(getCustomerList());
        PCBZBOMCustomerComboBox.setEnabled(false);

        JLabel HFStatusLabel = new JLabel("HF Status:", JLabel.RIGHT);
        HFStatusLabel.setPreferredSize(new Dimension(150, height));
        PCBZBOMIsHFStatusComboBox = new iComboBox();
        PCBZBOMIsHFStatusComboBox.setPreferredSize(new Dimension(200, height));
        PCBZBOMIsHFStatusComboBox.addItems(getHFList());
        PCBZBOMIsHFStatusComboBox.setEnabled(false);

        JLabel MCDROHSLabel = new JLabel("MCD ROHS Status:", JLabel.RIGHT);
        MCDROHSLabel.setPreferredSize(new Dimension(150, height));
        PCBZBOMMCDRoHSStatusComboBox = new iComboBox();
        PCBZBOMMCDRoHSStatusComboBox.setPreferredSize(new Dimension(200, height));
        PCBZBOMMCDRoHSStatusComboBox.setEnabled(false);

        JLabel MDDROHSLabel = new JLabel("MDD ROHS Status:", JLabel.RIGHT);
        MDDROHSLabel.setPreferredSize(new Dimension(150, height));
        PCBZBOMMDDRoHSStatusComboBox = new iComboBox();
        PCBZBOMMDDRoHSStatusComboBox.setPreferredSize(new Dimension(200, height));
        PCBZBOMMDDRoHSStatusComboBox.setEnabled(false);

        JLabel ROHSLabel = new JLabel("ROHS2.0 Status:", JLabel.RIGHT);
        ROHSLabel.setPreferredSize(new Dimension(150, height));
        PCBZBOMFMDREACHStatusComboBox = new iComboBox();
        PCBZBOMFMDREACHStatusComboBox.setPreferredSize(new Dimension(200, height));
        PCBZBOMFMDREACHStatusComboBox.setEnabled(false);

        List<String> ROHSList = getROHSList();
        for (String ROHS : ROHSList) {
            PCBZBOMMCDRoHSStatusComboBox.addItem(ROHS);
            PCBZBOMMDDRoHSStatusComboBox.addItem(ROHS);
            PCBZBOMFMDREACHStatusComboBox.addItem(ROHS);
        }

        customerLabelPanel.add("1.1.right.center.preferred.preferred", customerLabel);
        customerLabelPanel.add("1.2.left.center.preferred.preferred", PCBZBOMCustomerComboBox);
        customerLabelPanel.add("1.3.right.center.preferred.preferred", HFStatusLabel);
        customerLabelPanel.add("1.4.left.center.preferred.preferred", PCBZBOMIsHFStatusComboBox);
        JLabel label = new JLabel("                                    ", JLabel.LEFT);
        customerLabelPanel.add("1.5.left.center.preferred.preferred", label);

        customerLabelPanel.add("2.1.right.center.preferred.preferred", MCDROHSLabel);
        customerLabelPanel.add("2.2.left.center.preferred.preferred", PCBZBOMMCDRoHSStatusComboBox);
        customerLabelPanel.add("2.3.left.center.preferred.preferred", MDDROHSLabel);
        customerLabelPanel.add("2.4.left.center.preferred.preferred", PCBZBOMMDDRoHSStatusComboBox);
        JLabel label2 = new JLabel("                                    ", JLabel.LEFT);
        customerLabelPanel.add("1.5.left.center.preferred.preferred", label2);

        customerLabelPanel.add("3.1.right.center.preferred.preferred", ROHSLabel);
        customerLabelPanel.add("3.2.left.center.preferred.preferred", PCBZBOMFMDREACHStatusComboBox);

        return customerLabelPanel;
    }

    /**
	 * 获取MCD面板
	 * @return
	 * @throws Exception
	 */
    private JPanel getMCDPanel() throws Exception {
        JPanel mcdPanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        mcdPanel.setBorder(
                new TitledBorder(null, "MCD", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel mcdLabel = new JLabel("MCD:", JLabel.RIGHT);
        mcdLabel.setPreferredSize(new Dimension(150, height));
        mcdTextField = new JTextField();
        mcdTextField.setEditable(false);
        mcdTextField.setPreferredSize(new Dimension(200, 25));

        JButton mcdButton = new JButton("浏览");
        mcdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                		"Excel文件(*.xls,*.xlsx)", "xls", "xlsx");
                chooser.setFileFilter(filter);

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    mcdFileName = chooser.getSelectedFile().getName();
                    mcdTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JLabel mcdVerLabel = new JLabel("MCD Version", JLabel.RIGHT);
        mcdVerLabel.setPreferredSize(new Dimension(150, 25));

        mcdVerComboBox = new iComboBox();
        mcdVerComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> mcdVerList = getLov("FX8_MCDVersionLOV");
        for (String mcdVer : mcdVerList) {
            mcdVerComboBox.addItem(mcdVer);
        }

        if (complianceForm != null) {
            mcdVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_MCDVer"));
        }

        JLabel mcdROHSStatusLabel = new JLabel("MCDROHS Status", JLabel.RIGHT);
        mcdROHSStatusLabel.setPreferredSize(new Dimension(150, 25));

        mcdRohsComboBox = new iComboBox();
        mcdRohsComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> ROHSList = getROHSList();
        for (String ROHS : ROHSList) {
            mcdRohsComboBox.addItem(ROHS);
        }

        mcdPanel.add("1.1.right.center.preferred.preferred", mcdLabel);
        mcdPanel.add("1.2.left.center.preferred.preferred", mcdTextField);
        mcdPanel.add("1.3.left.center.preferred.preferred", mcdButton);

        mcdPanel.add("2.1.right.center.preferred.preferred", mcdVerLabel);
        mcdPanel.add("2.2.left.center.preferred.preferred", mcdVerComboBox);

        mcdPanel.add("3.1.right.center.preferred.preferred", mcdROHSStatusLabel);
        mcdPanel.add("3.2.left.center.preferred.preferred", mcdRohsComboBox);

        return mcdPanel;
    }

    /**
	 * 获取MDD面板
	 * @return
	 * @throws Exception
	 */
    private JPanel getMDDPanel() throws Exception {
        JPanel mddJPanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        mddJPanel.setBorder(
                new TitledBorder(null, "MDD", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel mddLabel = new JLabel("MDD:", JLabel.RIGHT);
        mddLabel.setPreferredSize(new Dimension(150, height));
        mddTextField = new JTextField();
        mddTextField.setEditable(false);
        mddTextField.setPreferredSize(new Dimension(200, 25));

        JButton mddButton = new JButton("浏览");
        mddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                		"Excel文件(*.xls,*.xlsx)", "xls", "xlsx");
                chooser.setFileFilter(filter);

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    mddFileName = chooser.getSelectedFile().getName();
                    mddTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JLabel mddVerLabel = new JLabel("MDD Version", JLabel.RIGHT);
        mddVerLabel.setPreferredSize(new Dimension(150, 25));
        mddVerComboBox = new iComboBox();
        mddVerComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> mcdVerList = getLov("FX8_MDDVersionLOV");
        for (String mddVer : mcdVerList) {
            mddVerComboBox.addItem(mddVer);
        }
        if (complianceForm != null) {
            mddVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_MDDVer"));
        }

        JLabel mddROHSStatusLabel = new JLabel("MDDROHS Status", JLabel.RIGHT);
        mddROHSStatusLabel.setPreferredSize(new Dimension(150, 25));

        mddRohsComboBox = new iComboBox();
        mddRohsComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> ROHSList = getROHSList();
        for (String ROHS : ROHSList) {
            mddRohsComboBox.addItem(ROHS);
        }

        mddJPanel.add("1.1.right.center.preferred.preferred", mddLabel);
        mddJPanel.add("1.2.left.center.preferred.preferred", mddTextField);
        mddJPanel.add("1.3.left.center.preferred.preferred", mddButton);

        mddJPanel.add("2.1.right.center.preferred.preferred", mddVerLabel);
        mddJPanel.add("2.2.left.center.preferred.preferred", mddVerComboBox);

        mddJPanel.add("3.1.right.center.preferred.preferred", mddROHSStatusLabel);
        mddJPanel.add("3.2.left.center.preferred.preferred", mddRohsComboBox);

        return mddJPanel;
    }

    /**
	 * 获取FMD面板
	 * @return
	 * @throws Exception
	 */
    private JPanel getFMDPanel() throws Exception {
        JPanel fmdJPanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        fmdJPanel.setBorder(
                new TitledBorder(null, "FMD", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel fmdLabel = new JLabel("FMD:", JLabel.RIGHT);
        fmdLabel.setPreferredSize(new Dimension(150, height));
        fmdTextField = new JTextField();
        fmdTextField.setEditable(false);
        fmdTextField.setPreferredSize(new Dimension(200, 25));

        JButton fmdButton = new JButton("浏览");
        fmdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                		"Excel文件(*.xls,*.xlsx)", "xls", "xlsx");
                chooser.setFileFilter(filter);

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fmdFileName = chooser.getSelectedFile().getName();
                    fmdTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JLabel fmdVerLabel = new JLabel("FMD Version", JLabel.RIGHT);
        fmdVerLabel.setPreferredSize(new Dimension(150, 25));

        fmdVerComboBox = new iComboBox();
        fmdVerComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> fmdVerList = getLov("FX8_FMDVersionLOV");
        for (String fmdVer : fmdVerList) {
            fmdVerComboBox.addItem(fmdVer);
        }
        if (complianceForm != null) {
            fmdVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_FMDVer"));
        }

        JLabel reachLabel = new JLabel("REACH Status", JLabel.RIGHT);
        reachLabel.setPreferredSize(new Dimension(150, 25));

        reachComboBox = new iComboBox();
        reachComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> ROHSList = getROHSList();
        for (String ROHS : ROHSList) {
            reachComboBox.addItem(ROHS);
        }

        JLabel isReachLabel = new JLabel("Is REACH Status", JLabel.RIGHT);
        isReachLabel.setPreferredSize(new Dimension(150, 25));

        isReachComboBox = new iComboBox();
        isReachComboBox.setPreferredSize(new Dimension(200, 25));
        isReachComboBox.addItems(getHFList());

        fmdJPanel.add("1.1.right.center.preferred.preferred", fmdLabel);
        fmdJPanel.add("1.2.left.center.preferred.preferred", fmdTextField);
        fmdJPanel.add("1.3.left.center.preferred.preferred", fmdButton);

        fmdJPanel.add("2.1.right.center.preferred.preferred", fmdVerLabel);
        fmdJPanel.add("2.2.left.center.preferred.preferred", fmdVerComboBox);

        fmdJPanel.add("3.1.right.center.preferred.preferred", reachLabel);
        fmdJPanel.add("3.2.left.center.preferred.preferred", reachComboBox);

        fmdJPanel.add("4.1.right.center.preferred.preferred", isReachLabel);
        fmdJPanel.add("4.2.left.center.preferred.preferred", isReachComboBox);

        return fmdJPanel;
    }

    /**
	 * 获取HF面板
	 * @return
	 */
    private JPanel getHFPanel() {
        JPanel hfJPanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        hfJPanel.setBorder(
                new TitledBorder(null, "HF", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel hfLabel = new JLabel("HF Status", JLabel.RIGHT);
        hfLabel.setPreferredSize(new Dimension(150, 25));

        hfComboBox = new iComboBox();
        hfComboBox.setPreferredSize(new Dimension(200, 25));
        hfComboBox.addItems(getHFList());
        hfJPanel.add("1.1.right.center.preferred.preferred", hfLabel);
        hfJPanel.add("1.2.left.center.preferred.preferred", hfComboBox);
        JLabel HFLabel = new JLabel("              ", JLabel.LEFT);
        hfJPanel.add("1.3.left.center.preferred.preferred", HFLabel);
        JLabel exeLabel = new JLabel("exemption", JLabel.RIGHT);
        exeLabel.setPreferredSize(new Dimension(150, 25));

        exemptionComboBox = new iComboBox();
        exemptionComboBox.setPreferredSize(new Dimension(200, 25));
        List<String> exemptionList = getLov("FX8_ExemptionLOV");
        for (String exemption : exemptionList) {
            exemptionComboBox.addItem(exemption);
        }
        hfJPanel.add("2.1.right.center.preferred.preferred", exeLabel);
        hfJPanel.add("2.2.left.center.preferred.preferred", exemptionComboBox);

        hfJPanel.add("3.1.right.center.preferred.preferred",
                new JLabel("              ", JLabel.LEFT));
        hfJPanel.add("4.1.right.center.preferred.preferred",
                new JLabel("              ", JLabel.LEFT));

        return hfJPanel;
    }

    /**
	 * 获取维护电子料环保认证等级面板
	 * @return
	 * @throws Exception
	 */
    private JPanel getEdaCustomerLabelPanel() throws Exception {
        JPanel edaCustomerLabelPanel = new JPanel(new VerticalLayout());
        edaCustomerLabelPanel.setBorder(
                new TitledBorder(null, "维护电子料环保认证等级", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel itemPanel1 = new JPanel(new HorizontalLayout());
        // MCD布局
        itemPanel1.add("left.nobind.left.top", getMCDPanel());
        // MDD布局
        itemPanel1.add("left.nobind.left.top", getMDDPanel());
        edaCustomerLabelPanel.add("top.bind.center.center", itemPanel1);

        JPanel itemPanel3 = new JPanel(new HorizontalLayout());
        JPanel mcdContainerPanel = new JPanel(new VerticalLayout());
        mcdContainerPanel.setPreferredSize(new Dimension(445, 140));
        mcdContainerPanel.setBorder(new TitledBorder(null, "MCD文件查看", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));

        mcdFilePanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        JScrollPane mcdJScrollPane = new JScrollPane(this.mcdFilePanel);
        mcdJScrollPane.setPreferredSize(new Dimension(445, 110));
        mcdJScrollPane.setBorder(null);
        mcdContainerPanel.add("top.bind.center.center", mcdJScrollPane);
        // MCD文件查看布局 
        itemPanel3.add("left.nobind.left.top", mcdContainerPanel);

        JPanel mddContainerPanel = new JPanel(new VerticalLayout());
        mddContainerPanel.setPreferredSize(new Dimension(445, 140));
        mddContainerPanel.setBorder(new TitledBorder(null, "MDD文件查看", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));

        mddFilePanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        JScrollPane mddJScrollPane = new JScrollPane(this.mddFilePanel);
        mddJScrollPane.setPreferredSize(new Dimension(445, 110));
        mddJScrollPane.setBorder(null);
        mddContainerPanel.add("top.bind.center.center", mddJScrollPane);
        // MDD文件查看布局 
        itemPanel3.add("left.nobind.left.top", mddContainerPanel);
        edaCustomerLabelPanel.add("top.bind.center.center", itemPanel3);

        JPanel itemPanel2 = new JPanel(new HorizontalLayout());
        // FMD布局 
        itemPanel2.add("left.nobind.left.top", getFMDPanel());
        // HF布局 
        itemPanel2.add("left.nobind.left.top", getHFPanel());
        edaCustomerLabelPanel.add("top.bind.center.center", itemPanel2);

        JPanel itemPanel4 = new JPanel(new HorizontalLayout());
        JPanel fmdContainerPanel = new JPanel(new VerticalLayout());
        fmdContainerPanel.setPreferredSize(new Dimension(445, 140));
        fmdContainerPanel.setBorder(new TitledBorder(null, "FMD文件查看", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));

        fmdFilePanel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
        JScrollPane fmdJScrollPane = new JScrollPane(this.fmdFilePanel);
        fmdJScrollPane.setPreferredSize(new Dimension(445, 110));
        fmdJScrollPane.setBorder(null);
        fmdContainerPanel.add("top.bind.center.center", fmdJScrollPane);
        // FMD文件查看布局 
        itemPanel4.add("left.nobind.left.top", fmdContainerPanel);
        edaCustomerLabelPanel.add("top.bind.center.center", itemPanel4);

        return edaCustomerLabelPanel;
    }

    public JPanel getCompliancePanel() {

        JPanel EDACompPanel = new JPanel(new VerticalLayout());
        EDACompPanel.setBackground(Color.white);
        try {

        	// 替代料管理对象布局
            EDACompPanel.add("top.bind.center.center", getSubManaPanel());

            // 环保认证等级布局
            EDACompPanel.add("top.bind.center.center", getCustomerLabelPanel());

            // 维护电子料环保认证等级布局
            EDACompPanel.add("top.bind.center.center", getEdaCustomerLabelPanel());

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pzbomCombox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pzbomStr = pzbomCombox.getSelectedItem().toString();
                if (!Utils.isNull(pzbomStr)) {
                    Iterator<Entry<TCComponentItemRevision, String>> iterator = PCBEZBOMMap.entrySet().iterator();
                    TCComponentItemRevision itemRev = null;
                    while (iterator.hasNext()) {
                        Entry<TCComponentItemRevision, String> entry = iterator.next();
                        if (pzbomStr.equals(entry.getValue())) {
                            itemRev = entry.getKey();
                        }
                    }
                    // 根据替代料id匹配电子料认证表单
                    try {
                        TCProperty prop = pcomplianceFome.getTCProperty(RelationName.COMPLIANCES);
                        TCComponent[] childForms = prop.getReferenceValueArray();
                        if (childForms != null && childForms.length > 0) {
                            for (TCComponent tcComponent : childForms) {
                                complianceForm = (TCComponentForm) tcComponent;
                                TCComponent item = complianceForm.getReferenceProperty("fx8_PCBEZBOM");
                                if (item.getProperty("item_id").equals(itemRev.getProperty("item_id"))) {
                                    break;
                                }
                            }
                        }
                        if (complianceForm != null) {
                            String customer = complianceForm.getProperty("fx8_PCBEZBOMCustomer");
                            String hf = complianceForm.getProperty("fx8_PCBEZBOMHFStatus");
                            String mcdROHS = complianceForm.getProperty("fx8_PCBEZBOMMCDRoHSStatus");
                            String mddROHS = complianceForm.getProperty("fx8_PCBEZBOMMDDRoHSStatus");
                            String reach = complianceForm.getProperty("fx8_PCBEZBOMFMDREACHStatus");
                            PCBZBOMCustomerComboBox.setSelectedItem(customer);
                            PCBZBOMIsHFStatusComboBox.setSelectedItem(hf);
                            PCBZBOMMCDRoHSStatusComboBox.setSelectedItem(mcdROHS);
                            PCBZBOMMDDRoHSStatusComboBox.setSelectedItem(mddROHS);
                            PCBZBOMFMDREACHStatusComboBox.setSelectedItem(reach);

                            mcdVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_MCDVer"));
                            mcdRohsComboBox.setSelectedItem(complianceForm.getProperty("fx8_MCDROHSStatus"));
                            mddVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_MDDVer"));
                            mddRohsComboBox.setSelectedItem(complianceForm.getProperty("fx8_MDDROHSStatus"));
                            fmdVerComboBox.setSelectedItem(complianceForm.getProperty("fx8_FMDVer"));
                            reachComboBox.setSelectedItem(complianceForm.getProperty("fx8_REACHStatus"));
                            boolean isHF = complianceForm.getLogicalProperty("fx8_IsHFStatus");
                            if (isHF) {
                                hfComboBox.setSelectedItem("YES");
                            } else {
                                hfComboBox.setSelectedItem("NO");
                            }
                            boolean IsREACHStatus = complianceForm.getLogicalProperty("fx8_IsREACHStatus");
                            if (IsREACHStatus) {
                                isReachComboBox.setSelectedItem("YES");
                            } else {
                                isReachComboBox.setSelectedItem("NO");
                            }
                            exemptionComboBox.setSelectedItem(complianceForm.getProperty("fx8_Exemption"));

                            TCProperty mcdrel = complianceForm.getTCProperty(RelationName.MCDREL);
                            TCComponent[] mcdFile = mcdrel.getReferenceValueArray();
                            mcdFilePanel.removeAll();
                            mcdFilePanel.repaint();
                            if (mcdFile != null && mcdFile.length > 0) {
                                int i = 1;
                                for (TCComponent tcComponent : mcdFile) {
                                    TCComponentDataset dataset = (TCComponentDataset) tcComponent;
                                    JLabel label = new JLabel(dataset.getProperty("object_name"), JLabel.LEFT);
                                    label.setPreferredSize(new Dimension(400, 25));
                                    label.setForeground(Color.blue);
                                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    label.addMouseListener(new FileClickListener(dataset));
                                    mcdFilePanel.add(i + ".1.right.center.preferred.preferred", label);
                                    i++;
                                }
                            } else {
                                JLabel label = new JLabel("暂无MCD文件", JLabel.LEFT);
                                label.setPreferredSize(new Dimension(400, 25));
                                mcdFilePanel.add("1.1.right.center.preferred.preferred", label);
                            }
                            mcdFilePanel.revalidate();

                            TCProperty mddrel = complianceForm.getTCProperty(RelationName.MDDREL);
                            TCComponent[] mddFile = mddrel.getReferenceValueArray();
                            mddFilePanel.removeAll();
                            mddFilePanel.repaint();
                            if (mddFile != null && mddFile.length > 0) {
                                int i = 1;
                                for (TCComponent tcComponent : mddFile) {
                                    TCComponentDataset dataset = (TCComponentDataset) tcComponent;
                                    JLabel label = new JLabel(dataset.getProperty("object_name"), JLabel.LEFT);
                                    label.setPreferredSize(new Dimension(400, 25));
                                    label.setForeground(Color.blue);
                                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    label.addMouseListener(new FileClickListener(dataset));
                                    mddFilePanel.add(i + ".1.right.center.preferred.preferred", label);
                                    i++;
                                }
                            } else {
                                JLabel label = new JLabel("暂无MDD文件", JLabel.LEFT);
                                label.setPreferredSize(new Dimension(400, 25));
                                mddFilePanel.add("1.1.right.center.preferred.preferred", label);
                            }
                            mddFilePanel.revalidate();

                            TCProperty fmdrel = complianceForm.getTCProperty(RelationName.FMDREL);
                            TCComponent[] fmdFile = fmdrel.getReferenceValueArray();
                            fmdFilePanel.removeAll();
                            fmdFilePanel.repaint();
                            if (fmdFile != null && fmdFile.length > 0) {
                                int i = 1;
                                for (TCComponent tcComponent : fmdFile) {
                                    TCComponentDataset dataset = (TCComponentDataset) tcComponent;
                                    JLabel label = new JLabel(dataset.getProperty("object_name"), JLabel.LEFT);
                                    label.setPreferredSize(new Dimension(400, 25));
                                    label.setForeground(Color.blue);
                                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    label.addMouseListener(new FileClickListener(dataset));
                                    fmdFilePanel.add(i + ".1.right.center.preferred.preferred", label);
                                    i++;
                                }
                            } else {
                                JLabel label = new JLabel("暂无FMD文件", JLabel.LEFT);
                                label.setPreferredSize(new Dimension(400, 25));
                                fmdFilePanel.add("1.1.right.center.preferred.preferred", label);
                            }
                            fmdFilePanel.revalidate();
                        }

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }
            }
        });

        return EDACompPanel;
    }

    private static List<String> getROHSList() {
        try {
            TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session
                    .getTypeComponent(ITypeName.ListOfValues);
            TCComponentListOfValues listOfValues = lovType.findLOVByName("FX8_MCDROHSStatusLOV");
            return Utils.getLOVList(listOfValues);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String[] getHFList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site,
                "FX_Get_HF_Values");
    }

    private static String[] getCustomerList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site,
                "FX_Get_Customer_Values");
    }

    private List<String> getLov(String lovName) {
        try {
            TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session
                    .getTypeComponent(ITypeName.ListOfValues);
            TCComponentListOfValues listOfValues = lovType.findLOVByName(lovName);
            return Utils.getLOVList(listOfValues);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private class FileClickListener implements MouseListener {
        private TCComponentDataset dataset;

        public FileClickListener(TCComponentDataset dataset) {
            this.dataset = dataset;
        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
            Object[] options = {"是", "否"};
            int m = JOptionPane.showOptionDialog(null, "要下载该文件吗？", "", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (m == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择下载路径");
                fileChooser.setApproveButtonText(reg.getString("Save.Msg"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(fileChooser);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        TCComponentTcFile[] tcfiles = dataset.getTcFiles();
                        if (tcfiles != null && tcfiles.length > 0) {
                            TCComponentTcFile onetcfile = tcfiles[0];
                            String filename = onetcfile.getProperty("original_file_name");
                            File newfile = new File(file.getPath() + File.separator + filename);
                            if (newfile.exists()) {
                                int opt = JOptionPane.showOptionDialog(null, reg.getString("FileIsExist.Msg"),
                                        reg.getString("Info.Msg"), JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                if (opt == JOptionPane.YES_OPTION) {
                                    DownloadDataset.downloadFile(dataset, true, file.getPath());
                                }
                            } else {
                                DownloadDataset.downloadFile(dataset, true, file.getPath());
                            }
                            Desktop.getDesktop().open(newfile);
                        }
                    } catch (TCException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

    }
}
