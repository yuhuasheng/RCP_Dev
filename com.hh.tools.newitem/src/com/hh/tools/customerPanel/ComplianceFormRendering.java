package com.hh.tools.customerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentFormType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Utilities;

public class ComplianceFormRendering extends AbstractRendering {

    private TCComponentForm form;
    private TCSession session;

    private JTextField mcdTextField;
    private String mcdFileName;
    private JComboBox<String> mcdVerComboBox;
    private JComboBox<String> mcdRohsComboBox;
    private JTextField mddTextField;
    private String mddFileName;
    private JComboBox<String> mddVerComboBox;
    private JComboBox<String> mddRohsComboBox;
    private JTextField fmdTextField;
    private String fmdFileName;
    private JComboBox<String> fmdVerComboBox;
    private JComboBox<String> reachComboBox;
    private JComboBox<String> hfComboBox;
    private JComboBox<String> exemptionComboBox;
    private DefaultTableModel tableModel;
    private JTable table;

    public ComplianceFormRendering(TCComponent arg0) throws Exception {
        super(arg0);
        form = (TCComponentForm) arg0;
        session = form.getSession();
        loadRendering();
    }

    @Override
    public void loadRendering() throws TCException {
        initUI();
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel(new BorderLayout());

        JScrollPane msgWarnScrollPane = new JScrollPane();
        msgWarnScrollPane.setViewportView(contentPanel);
        this.add(msgWarnScrollPane, BorderLayout.CENTER);

        JPanel levlePanel = new JPanel(new BorderLayout());
        LineBorder lienBorder = new LineBorder(Color.BLACK, 1, false);
        levlePanel
                .setBorder(BorderFactory.createTitledBorder(lienBorder, "要求环保等级", TitledBorder.LEFT, TitledBorder.TOP));

        Object[] columnNames = {"Customer", "MCD ROHS Status", "MDD ROHS Status", "HF Status", "FMD REACH Status"};
        String[][] tableVales = {}; // 数据
        tableModel = new DefaultTableModel(tableVales, columnNames);
        table = new JTable(tableModel);

        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
        levlePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        // 把 表格内容 添加到容器中心
        levlePanel.add(table, BorderLayout.CENTER);

        JPanel propPanel = new JPanel();
        propPanel.setLayout(new GridLayout(2, 2, 10, 10));

        {
            JPanel mcdPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
            mcdPanel.setPreferredSize(new Dimension(500, 200));
            mcdPanel.setBorder(
                    BorderFactory.createTitledBorder(lienBorder, "MCD", TitledBorder.LEFT, TitledBorder.TOP));

            JLabel mcdLabel = new JLabel("MCD", JLabel.RIGHT);
            mcdLabel.setPreferredSize(new Dimension(150, 25));

            mcdTextField = new JTextField();
            mcdTextField.setEditable(false);
            mcdTextField.setPreferredSize(new Dimension(200, 25));

            JButton mcdButton = new JButton("浏览");
            mcdButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel文件(*.xls,*.xlsx)", "xls",
                            "xlsx");
                    chooser.setFileFilter(filter);

                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        mcdFileName = chooser.getSelectedFile().getName();
                        mcdTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });

            mcdPanel.add("1.1.right.center.preferred.preferred", mcdLabel);
            mcdPanel.add("1.2.left.center.preferred.preferred", mcdTextField);
            mcdPanel.add("1.3.left.center.preferred.preferred", mcdButton);

            JLabel mcdVerLabel = new JLabel("MCD Version", JLabel.RIGHT);
            mcdVerLabel.setPreferredSize(new Dimension(150, 25));

            mcdVerComboBox = new JComboBox<String>();
            mcdVerComboBox.setPreferredSize(new Dimension(200, 25));

            mcdPanel.add("2.1.right.center.preferred.preferred", mcdVerLabel);
            mcdPanel.add("2.2.left.center.preferred.preferred", mcdVerComboBox);

            JLabel mcdRohsVerLabel = new JLabel("ROHS Status", JLabel.RIGHT);
            mcdRohsVerLabel.setPreferredSize(new Dimension(150, 25));

            mcdRohsComboBox = new JComboBox<String>();
            mcdRohsComboBox.setPreferredSize(new Dimension(200, 25));

            mcdPanel.add("3.1.right.center.preferred.preferred", mcdRohsVerLabel);
            mcdPanel.add("3.2.left.center.preferred.preferred", mcdRohsComboBox);

            propPanel.add(mcdPanel);
        }

        {
            JPanel mddPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
            mddPanel.setPreferredSize(new Dimension(500, 200));
            mddPanel.setBorder(
                    BorderFactory.createTitledBorder(lienBorder, "MDD", TitledBorder.LEFT, TitledBorder.TOP));

            JLabel mddLabel = new JLabel("MDD", JLabel.RIGHT);
            mddLabel.setPreferredSize(new Dimension(150, 25));

            mddTextField = new JTextField();
            mddTextField.setEditable(false);
            mddTextField.setPreferredSize(new Dimension(200, 25));

            JButton mddButton = new JButton("浏览");
            mddButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel文件(*.xls,*.xlsx)", "xls",
                            "xlsx");
                    chooser.setFileFilter(filter);

                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        mddFileName = chooser.getSelectedFile().getName();
                        mddTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });

            mddPanel.add("1.1.right.center.preferred.preferred", mddLabel);
            mddPanel.add("1.2.left.center.preferred.preferred", mddTextField);
            mddPanel.add("1.3.left.center.preferred.preferred", mddButton);

            JLabel mddVerLabel = new JLabel("MDD Version", JLabel.RIGHT);
            mddVerLabel.setPreferredSize(new Dimension(150, 25));

            mddVerComboBox = new JComboBox<String>();
            mddVerComboBox.setPreferredSize(new Dimension(200, 25));

            mddPanel.add("2.1.right.center.preferred.preferred", mddVerLabel);
            mddPanel.add("2.2.left.center.preferred.preferred", mddVerComboBox);

            JLabel mddRohsVerLabel = new JLabel("ROHS Status", JLabel.RIGHT);
            mddRohsVerLabel.setPreferredSize(new Dimension(150, 25));

            mddRohsComboBox = new JComboBox<String>();
            mddRohsComboBox.setPreferredSize(new Dimension(200, 25));

            mddPanel.add("3.1.right.center.preferred.preferred", mddRohsVerLabel);
            mddPanel.add("3.2.left.center.preferred.preferred", mddRohsComboBox);

            propPanel.add(mddPanel);
        }

        {
            JPanel fmdPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
            fmdPanel.setPreferredSize(new Dimension(500, 200));
            fmdPanel.setBorder(
                    BorderFactory.createTitledBorder(lienBorder, "FMD", TitledBorder.LEFT, TitledBorder.TOP));

            JLabel fmdLabel = new JLabel("FMD", JLabel.RIGHT);
            fmdLabel.setPreferredSize(new Dimension(150, 25));

            fmdTextField = new JTextField();
            fmdTextField.setEditable(false);
            fmdTextField.setPreferredSize(new Dimension(200, 25));

            JButton fmdButton = new JButton("浏览");
            fmdButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel文件(*.xls,*.xlsx)", "xls",
                            "xlsx");
                    chooser.setFileFilter(filter);

                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        fmdFileName = chooser.getSelectedFile().getName();
                        fmdTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });

            fmdPanel.add("1.1.right.center.preferred.preferred", fmdLabel);
            fmdPanel.add("1.2.left.center.preferred.preferred", fmdTextField);
            fmdPanel.add("1.3.left.center.preferred.preferred", fmdButton);

            JLabel fmdVerLabel = new JLabel("FMD Version", JLabel.RIGHT);
            fmdVerLabel.setPreferredSize(new Dimension(150, 25));

            fmdVerComboBox = new JComboBox<String>();
            fmdVerComboBox.setPreferredSize(new Dimension(200, 25));

            fmdPanel.add("2.1.right.center.preferred.preferred", fmdVerLabel);
            fmdPanel.add("2.2.left.center.preferred.preferred", fmdVerComboBox);

            JLabel reachVerLabel = new JLabel("REACH Status", JLabel.RIGHT);
            reachVerLabel.setPreferredSize(new Dimension(150, 25));

            reachComboBox = new JComboBox<String>();
            reachComboBox.setPreferredSize(new Dimension(200, 25));

            fmdPanel.add("3.1.right.center.preferred.preferred", reachVerLabel);
            fmdPanel.add("3.2.left.center.preferred.preferred", reachComboBox);

            propPanel.add(fmdPanel);
        }

        {
            JPanel hfPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
            hfPanel.setPreferredSize(new Dimension(500, 200));
            hfPanel.setBorder(BorderFactory.createTitledBorder(lienBorder, "HF", TitledBorder.LEFT, TitledBorder.TOP));

            JLabel hfLabel = new JLabel("HF Status", JLabel.RIGHT);
            hfLabel.setPreferredSize(new Dimension(150, 25));

            hfComboBox = new JComboBox<String>();
            hfComboBox.setPreferredSize(new Dimension(200, 25));

            hfPanel.add("1.1.right.center.preferred.preferred", hfLabel);
            hfPanel.add("1.2.left.center.preferred.preferred", hfComboBox);

            JLabel exeLabel = new JLabel("exemption", JLabel.RIGHT);
            exeLabel.setPreferredSize(new Dimension(150, 25));

            exemptionComboBox = new JComboBox<String>();
            exemptionComboBox.setPreferredSize(new Dimension(200, 25));

            hfPanel.add("2.1.right.center.preferred.preferred", exeLabel);
            hfPanel.add("2.2.left.center.preferred.preferred", exemptionComboBox);

            propPanel.add(hfPanel);
        }

        contentPanel.add(levlePanel, BorderLayout.NORTH);
        contentPanel.add(propPanel, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        try {
            TCComponentFormType formType = (TCComponentFormType) form.getTypeComponent();
            System.out.println("formType == " + formType);

            TCComponentListOfValues mcdVerLOV = formType.getPropertyDescriptor("fx8_MCDVer").getLOV();
            ArrayList<String> mcdVerLOVList = Utils.getLOVList(mcdVerLOV);
            mcdVerComboBox.addItem("");
            for (String value : mcdVerLOVList) {
                mcdVerComboBox.addItem(value);
            }

            TCComponentListOfValues mcdRohsLOV = formType.getPropertyDescriptor("fx8_MCDROHSStatus").getLOV();
            ArrayList<String> mcdRohsLOVList = Utils.getLOVList(mcdRohsLOV);
            mcdRohsComboBox.addItem("");
            for (String value : mcdRohsLOVList) {
                mcdRohsComboBox.addItem(value);
            }
            mddRohsComboBox.addItem("");
            for (String value : mcdRohsLOVList) {
                mddRohsComboBox.addItem(value);
            }

            TCComponentListOfValues mddVerLOV = formType.getPropertyDescriptor("fx8_MDDVer").getLOV();
            ArrayList<String> mddVerLOVList = Utils.getLOVList(mddVerLOV);
            mddVerComboBox.addItem("");
            for (String value : mddVerLOVList) {
                mddVerComboBox.addItem(value);
            }

            TCComponentListOfValues fmdVerLOV = formType.getPropertyDescriptor("fx8_FMDVer").getLOV();
            ArrayList<String> fmdVerLOVList = Utils.getLOVList(fmdVerLOV);
            fmdVerComboBox.addItem("");
            for (String value : fmdVerLOVList) {
                fmdVerComboBox.addItem(value);
            }

            TCComponentListOfValues exemptionLOV = formType.getPropertyDescriptor("fx8_Exemption").getLOV();
            ArrayList<String> exemptionLOVList = Utils.getLOVList(exemptionLOV);
            exemptionComboBox.addItem("");
            for (String value : exemptionLOVList) {
                exemptionComboBox.addItem(value);
            }

            reachComboBox.addItem("");
            reachComboBox.addItem("NO");
            reachComboBox.addItem("YES");

            hfComboBox.addItem("");
            hfComboBox.addItem("NO");
            hfComboBox.addItem("YES");

            ViewEditHelper localViewEditHelper = new ViewEditHelper(form.getSession());
            ViewEditHelper.CKO localCKO = localViewEditHelper.getObjectState(form);
            switch (localCKO) {
                case CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE:
                case CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE:
                case IMPLICITLY_CHECKOUTABLE:
                    System.err.println("checkOut!!!");
                    Utilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                    break;
                case CHECKED_IN:
                case NOT_CHECKOUTABLE:
                    System.err.println("checkIn!!!");
                    Utilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        System.out.println("initData");
        try {

            String fx8_MCDVer = form.getProperty("fx8_MCDVer");
            String fx8_MCDROHSStatus = form.getProperty("fx8_MCDROHSStatus");
            String fx8_MDDVer = form.getProperty("fx8_MDDVer");
            String fx8_MDDROHSStatus = form.getProperty("fx8_MDDROHSStatus");
            String fx8_FMDVer = form.getProperty("fx8_FMDVer");
            boolean fx8_IsREACHStatus = form.getLogicalProperty("fx8_IsREACHStatus");
            boolean fx8_IsHFStatus = form.getLogicalProperty("fx8_IsHFStatus");
            String fx8_Exemption = form.getProperty("fx8_Exemption");

            TCProperty prop = form.getTCProperty("fx8_ComplianceRequireTable");
            TCComponent[] eplevels = prop.getReferenceValueArray();
            if (eplevels != null && eplevels.length > 0) {
                for (TCComponent tcComponet : eplevels) {
                    String[] data = new String[5];
                    data[0] = tcComponet.getProperty("fx8_Customer");
                    data[1] = tcComponet.getProperty("fx8_MCDRoHSStatus");
                    data[2] = tcComponet.getProperty("fx8_MDDRoHSStatus");
                    data[3] = tcComponet.getProperty("fx8_IsHFStatus");
                    data[4] = tcComponet.getProperty("fx8_FMDREACHStatus");
                    tableModel.addRow(data);
                    table.setModel(tableModel);
                }
            }

//			System.out.println("fx8_MCD======="+fx8_MCD);
//			System.out.println("fx8_MCDVer======="+fx8_MCDVer);
//			System.out.println("fx8_MCDROHSStatus======="+fx8_MCDROHSStatus);
//			System.out.println("fx8_MDD======="+fx8_MDD);
//			System.out.println("fx8_MDDVer======="+fx8_MDDVer);
//			System.out.println("fx8_MDDROHSStatus======="+fx8_MDDROHSStatus);
//			System.out.println("fx8_FMD======="+fx8_FMD);
//			System.out.println("fx8_FMDVer======="+fx8_FMDVer);
            System.out.println("fx8_IsREACHStatus=======" + fx8_IsREACHStatus);
            System.out.println("fx8_IsHFStatus=======" + fx8_IsHFStatus);
//			System.out.println("fx8_Exemption======="+fx8_Exemption);

            TCComponent mcdComponent = form.getTCProperty("fx8_MCD").getReferenceValue();
            if (mcdComponent != null && mcdComponent instanceof TCComponentDataset) {
                TCComponentDataset dataset = (TCComponentDataset) mcdComponent;
                mcdTextField.setText(dataset.getProperty("object_name"));
            }

            if (fx8_MCDVer != null && !"".equals(fx8_MCDVer)) {
                mcdVerComboBox.setSelectedItem(fx8_MCDVer);
            }
            if (fx8_MCDROHSStatus != null && !"".equals(fx8_MCDROHSStatus)) {
                mcdRohsComboBox.setSelectedItem(fx8_MCDROHSStatus);
            }

            TCComponent mddComponent = form.getTCProperty("fx8_MDD").getReferenceValue();
            if (mddComponent != null && mddComponent instanceof TCComponentDataset) {
                TCComponentDataset dataset = (TCComponentDataset) mddComponent;
                mddTextField.setText(dataset.getProperty("object_name"));
            }

            if (fx8_MDDVer != null && !"".equals(fx8_MDDVer)) {
                mddVerComboBox.setSelectedItem(fx8_MDDVer);
            }
            if (fx8_MDDROHSStatus != null && !"".equals(fx8_MDDROHSStatus)) {
                mddRohsComboBox.setSelectedItem(fx8_MDDROHSStatus);
            }

            TCComponent fmdComponent = form.getTCProperty("fx8_FMD").getReferenceValue();
            if (fmdComponent != null && fmdComponent instanceof TCComponentDataset) {
                TCComponentDataset dataset = (TCComponentDataset) fmdComponent;
                fmdTextField.setText(dataset.getProperty("object_name"));
            }

            if (fx8_FMDVer != null && !"".equals(fx8_FMDVer)) {
                fmdVerComboBox.setSelectedItem(fx8_FMDVer);
            }
            if (fx8_IsREACHStatus) {
                reachComboBox.setSelectedItem("YES");
            } else {
                reachComboBox.setSelectedItem("NO");
            }

            if (fx8_IsHFStatus) {
                hfComboBox.setSelectedItem("YES");
            } else {
                hfComboBox.setSelectedItem("NO");
            }

            if (fx8_Exemption != null && !"".equals(fx8_Exemption)) {
                exemptionComboBox.setSelectedItem(fx8_Exemption);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveRendering() {
        // TODO Auto-generated method stub
        System.out.println("saveRendering");
        try {
            Utils.byPass(true);
            form.setProperty("fx8_MCDVer", mcdVerComboBox.getSelectedItem().toString());
            form.setProperty("fx8_MCDROHSStatus", mcdRohsComboBox.getSelectedItem().toString());
            form.setProperty("fx8_MDDVer", mddVerComboBox.getSelectedItem().toString());
            form.setProperty("fx8_MDDROHSStatus", mddRohsComboBox.getSelectedItem().toString());
            form.setProperty("fx8_FMDVer", fmdVerComboBox.getSelectedItem().toString());
            String isReachStatus = reachComboBox.getSelectedItem().toString();
            if (isReachStatus != null && !"".equals(isReachStatus)) {
                if ("YES".equals(isReachStatus)) {
                    form.setLogicalProperty("fx8_IsREACHStatus", true);
                } else {
                    form.setLogicalProperty("fx8_IsREACHStatus", false);
                }
            }

            String isHfStatus = hfComboBox.getSelectedItem().toString();
            if (isHfStatus != null && !"".equals(isHfStatus)) {
                if ("YES".equals(isHfStatus)) {
                    form.setLogicalProperty("fx8_IsHFStatus", true);
                } else {
                    form.setLogicalProperty("fx8_IsHFStatus", false);
                }
            }

            form.setProperty("fx8_Exemption", exemptionComboBox.getSelectedItem().toString());

            if (mcdFileName != null && !"".equals(mcdFileName)) {
                TCComponent mcdTc = null;
                String mcdPath = mcdTextField.getText();
                String mcd = mcdFileName.substring(0, mcdFileName.lastIndexOf("."));
                if (mcdPath.endsWith("xlsx")) {
                    mcdTc = CreateObject.createDataSet(session, mcdPath, "MSExcelX", mcd, "excel");
                    form.setReferenceProperty("fx8_MCD", mcdTc);
                } else if (mcdPath.endsWith("xls")) {
                    mcdTc = CreateObject.createDataSet(session, mcdPath, "MSExcel", mcd, "excel");
                    form.setReferenceProperty("fx8_MCD", mcdTc);
                }
            }

            if (mddFileName != null && !"".equals(mddFileName)) {
                TCComponent mddTc = null;
                String mddPath = mddTextField.getText();
                String mdd = mddFileName.substring(0, mddFileName.lastIndexOf("."));
                if (mddPath.endsWith("xlsx")) {
                    mddTc = CreateObject.createDataSet(session, mddPath, "MSExcelX", mdd, "excel");
                    form.setReferenceProperty("fx8_MDD", mddTc);
                } else if (mddPath.endsWith("xls")) {
                    mddTc = CreateObject.createDataSet(session, mddPath, "MSExcel", mdd, "excel");
                    form.setReferenceProperty("fx8_MDD", mddTc);
                }
            }

            if (fmdFileName != null && !"".equals(fmdFileName)) {
                TCComponent fmdTc = null;
                String fmdPath = fmdTextField.getText();
                String fmd = fmdFileName.substring(0, fmdFileName.lastIndexOf("."));
                if (fmdPath.endsWith("xlsx")) {
                    fmdTc = CreateObject.createDataSet(session, fmdPath, "MSExcelX", fmd, "excel");
                    form.setReferenceProperty("fx8_FMD", fmdTc);
                } else if (fmdPath.endsWith("xls")) {
                    fmdTc = CreateObject.createDataSet(session, fmdPath, "MSExcel", fmd, "excel");
                    form.setReferenceProperty("fx8_FMD", fmdTc);
                }
            }

            Utils.byPass(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
