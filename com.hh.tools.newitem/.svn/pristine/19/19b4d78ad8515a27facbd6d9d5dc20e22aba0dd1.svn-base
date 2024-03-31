package com.hh.tools.dashboard.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.Display;

import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ExcelUtil;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;

public class DashboardDialog extends AbstractAIFDialog {
    private TCSession session;

    private Map<String, JTextField> fieldMap;

    private List<InterfaceAIFComponent> componentList;

    private List<TCComponentItemRevision> filterList;

    private String searchName = "__FX_EDACompRevision_Query";

    private TCTable edaTable;

    private JTextField mfrField;

    private JTextField stdPnField;

    private JTextField symbolField;

    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");

    private ProgressBarThread barThread;

    public DashboardDialog(AbstractAIFApplication app, TCSession session) throws Exception {
        super(true);
        this.session = session;
        initUI();
    }

    public void initUI() throws TCException {
        setTitle("EDA Apply CIS Task Dashboard");

        setBounds(100, 100, 1800, 800);
        getContentPane().setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(new TitledBorder(null, "Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        leftPanel.setPreferredSize(new Dimension(400, 800));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setPreferredSize(new Dimension(400, 800));
        leftPanel.add(jScrollPane);

        TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
        TCComponentQuery compQuery = (TCComponentQuery) imancomponentquerytype.find(searchName);
        TCQueryClause[] tcqcs = compQuery.describe();

        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new VerticalLayout(5, 0, 0, 2, 2));
        jScrollPane.setViewportView(paramPanel);

        fieldMap = new HashMap<>();
        for (TCQueryClause tcqc : tcqcs) {
            JPanel tcqcPanel = new JPanel();
            JLabel label = new JLabel(tcqc.getUserEntryName() + ":", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(100, 25));
            tcqcPanel.add(label);

            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(200, 30));
            tcqcPanel.add(field);

            paramPanel.add("top.bind.center.center", tcqcPanel);
            fieldMap.put(tcqc.getUserEntryName(), field);
        }

        JButton queryButton = new JButton("Query");
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        List<String> keyList = new ArrayList<>();
                        List<String> valueList = new ArrayList<>();
                        for (Map.Entry<String, JTextField> entity : fieldMap.entrySet()) {
                            String value = entity.getValue().getText();
                            if (StringUtils.isNotEmpty(value)) {
                                keyList.add(entity.getKey());
                                valueList.add(value);
                            }
                        }

                        if (valueList.size() > 0) {
                            componentList = Utils.search(searchName, keyList.toArray(new String[keyList.size()]),
                                    valueList.toArray(new String[valueList.size()]));
                            if (componentList == null) {
                            	MessageBox.post(AIFUtility.getActiveDesktop(), "没有查询到符合条件的电子料件!", "Warning",
                                        MessageBox.WARNING);
                            } else {
                                if (componentList.size() > 2000) {
                                	MessageBox.post(AIFUtility.getActiveDesktop(), "查询结果数量超限，请重新输入新的查询条件！", "Warning",
                                            MessageBox.WARNING);
                                } else {
                                	Object[] options = { "是", "否" };
									int opt = JOptionPane.showOptionDialog(null,
											"共查询到" + componentList.size() + "条电子料件，是否加载这些数据？", "提示",
											JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
											options[0]);
                                    if (opt == JOptionPane.YES_OPTION) {
                                        edaTable.removeAllRows();
                                        int numNo = 1;
                                        filterList = new ArrayList<>(componentList.size());
                                        for (InterfaceAIFComponent component : componentList) {
                                            try {
                                                TCComponentItemRevision itemRev = (TCComponentItemRevision) component;
                                                edaTable.addRow(getEDACompInfo(numNo, itemRev));
                                                filterList.add(itemRev);
                                                numNo++;
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                        	MessageBox.post(AIFUtility.getActiveDesktop(), "请输入查询条件!", "Warning", MessageBox.WARNING);
                        }
                    }
                }).start();
            }
        });

        leftPanel.add(queryButton);

        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setPreferredSize(new Dimension(1100, 800));

        JPanel filterPanel = new JPanel(new HorizontalLayout(5, 0, 5, 0, 0));
        leftPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        resultPanel.add(filterPanel);

        {
            JPanel mfrPanel = new JPanel();
            JLabel label = new JLabel("Mfg:", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(70, 25));
            mfrPanel.add(label);

            mfrField = new JTextField();
            mfrField.setPreferredSize(new Dimension(150, 30));
            mfrPanel.add(mfrField);
            filterPanel.add("left.nobind.left.top", mfrPanel);
        }

        {
            JPanel stdPnPanel = new JPanel();
            JLabel label = new JLabel("STD-PN:", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(70, 25));
            stdPnPanel.add(label);

            stdPnField = new JTextField();
            stdPnField.setPreferredSize(new Dimension(150, 30));
            stdPnPanel.add(stdPnField);
            filterPanel.add("left.nobind.left.top", stdPnPanel);
        }

        {
            JPanel symbolPanel = new JPanel();
            JLabel label = new JLabel("Symbol:", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(70, 25));
            symbolPanel.add(label);

            symbolField = new JTextField();
            symbolField.setPreferredSize(new Dimension(150, 30));
            symbolPanel.add(symbolField);
            filterPanel.add("left.nobind.left.top", symbolPanel);
        }

        {
            JPanel applyStatusPanel = new JPanel();
            JLabel label = new JLabel("ApplyStatus:", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(100, 25));
            applyStatusPanel.add(label);

            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(150, 30));
            applyStatusPanel.add(field);
            filterPanel.add("left.nobind.left.top", applyStatusPanel);
        }

        {
            JPanel performerPanel = new JPanel();
            JLabel label = new JLabel("Performer:", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(100, 25));
            performerPanel.add(label);

            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(150, 30));
            performerPanel.add(field);
            filterPanel.add("left.nobind.left.top", performerPanel);
        }

        {
            JPanel buttonPanel = new JPanel();
            JButton filterButton = new JButton("Filter");
            filterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String mfr = mfrField.getText();
                    String stdPn = stdPnField.getText();
                    String symbol = symbolField.getText();

                    edaTable.removeAllRows();
                    if (filterList.size() > 0) {
                        filterList.clear();
                    }
                    int numNo = 1;
                    for (InterfaceAIFComponent component : componentList) {
                        try {
                            TCComponentItemRevision itemRev = (TCComponentItemRevision) component;
                            if (StringUtils.isNoneBlank(mfr)) {
                                if (!itemRev.getProperty("fx8_Mfr").toLowerCase().contains(mfr.toLowerCase())) {
                                    continue;
                                }
                            }

                            if (StringUtils.isNoneBlank(stdPn)) {
                                if (!itemRev.getProperty("fx8_StandardPN").toLowerCase()
                                        .contains(stdPn.toLowerCase())) {
                                    continue;
                                }
                            }

                            if (StringUtils.isNoneBlank(symbol)) {
                                if (!itemRev.getProperty("fx8_Symbol").toLowerCase().contains(symbol.toLowerCase())) {
                                    continue;
                                }
                            }

                            filterList.add(itemRev);
                            edaTable.addRow(getEDACompInfo(numNo, itemRev));
                            numNo++;
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            });
            buttonPanel.add(filterButton);

            JButton exportButton = new JButton("Export");
            exportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        if (filterList.size() > 0) {
                            export();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageBox.post(e);
                    } finally {
                        if (barThread != null) {
                            barThread.stopBar();
                        }
                    }
                }
            });
            buttonPanel.add(exportButton);

            filterPanel.add("left.nobind.left.top", buttonPanel);
        }

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, resultPanel);
        sp.setDividerLocation(350);
        getContentPane().add(sp, BorderLayout.CENTER);

        String[] titles = new String[]{"No", "Category", "Parttype", "Mfg", "Mfg_PN", "Standard_PN", "Symbol",
                "DataSheet", "FootPrint", "Project", "EDA_Owner", "CreateDate", "Apply_Status", "Task_Date",
                "Task_Perform"};
        edaTable = new TCTable(session, titles) {
            public boolean isCellEditable(int arg0, int arg1) {
                return false;
            }
        };

        edaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() > 1) {
//					dispose();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            Object index = edaTable.getValueAt(edaTable.getSelectedRow(), 0);
                            String s = "com.teamcenter.rac.ui.perspectives.navigatorPerspective";
                            Activator.getDefault().openPerspective(s);
                            Activator.getDefault().openComponents(s, new InterfaceAIFComponent[]{
                                    filterList.get(Integer.valueOf(index.toString()) - 1)});
                            MessageBox.post(AIFUtility.getActiveDesktop(), "该对象已发送MyTeamcenter，请注意查收！", "提示",
                                    MessageBox.INFORMATION);
                        }
                    });
                }
            }
        });

        JScrollPane scrollTablePanel = new JScrollPane();
        scrollTablePanel.setBackground(Color.WHITE);
        scrollTablePanel.setPreferredSize(new Dimension(1100, 500));
        scrollTablePanel.setViewportView(edaTable);
        edaTable.getTableHeader().setBackground(Color.WHITE);
        edaTable.setRowHeight(30);
        edaTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        edaTable.getTableHeader().setResizingAllowed(true);
        edaTable.getTableHeader().setReorderingAllowed(false);
        resultPanel.add(scrollTablePanel, BorderLayout.CENTER);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    private Object[] getEDACompInfo(int numNo, TCComponentItemRevision itemRev) throws TCException {
        return new Object[]{String.valueOf(numNo), itemRev.getProperty("fx8_Category"),
                itemRev.getProperty("fx8_PartType"), itemRev.getProperty("fx8_Mfr"), itemRev.getProperty("fx8_MfrPN"),
                itemRev.getProperty("fx8_StandardPN"), itemRev.getProperty("fx8_Symbol"),
                itemRev.getProperty("fx8_DataSheet"), itemRev.getProperty("fx8_FootPrint"),
                itemRev.getProperty("fx8_FrjName"), itemRev.getProperty("owning_user"),
                itemRev.getProperty("creation_date"), "", "", ""};
    }

    private void export() throws Exception {
    	// 查找Excel文件
        GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
        String templateId = getPreferenceUtil.getStringPreference(session, TCPreferenceService.TC_preference_site,
                "FX_EDAComp_Template");

        TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(ITypeName.Item);
        TCComponentItem item = itemType.find(templateId);
        if (item == null) {
            throw new Exception(reg.getString("TemplateNotFound.Msg"));
        }

        TCComponent com = item.getLatestItemRevision().getRelatedComponent("IMAN_specification");
        if (com == null || !(com instanceof TCComponentDataset)) {
            throw new Exception(reg.getString("TemplateNotFound1.Msg"));
        }

        String excelFilePath = DownloadDataset.downloadFile((TCComponentDataset) com, true);

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonText(reg.getString("Save.Msg"));
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            barThread = new ProgressBarThread(reg.getString("Info.Msg"), reg.getString("Progress1.Msg"));
            barThread.start();

            System.out.println("zhujiang========================excelFilePath==" + excelFilePath);
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFilePath));
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row = null;

            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            int index = 1;
            for (TCComponentItemRevision itemRev : filterList) {
                System.out.println("zhujiang==============================" + index);

                row = ExcelUtil.getRow(sheet, index);

                ExcelUtil.getCell(row, 0).setCellValue(index);
                ExcelUtil.getCell(row, 0).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 1).setCellValue(itemRev.getProperty("fx8_Category"));
                ExcelUtil.getCell(row, 1).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 2).setCellValue(itemRev.getProperty("fx8_PartType"));
                ExcelUtil.getCell(row, 2).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 3).setCellValue(itemRev.getProperty("fx8_Mfr"));
                ExcelUtil.getCell(row, 3).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 4).setCellValue(itemRev.getProperty("fx8_MfrPN"));
                ExcelUtil.getCell(row, 4).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 5).setCellValue(itemRev.getProperty("fx8_StandardPN"));
                ExcelUtil.getCell(row, 5).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 6).setCellValue(itemRev.getProperty("fx8_Symbol"));
                ExcelUtil.getCell(row, 6).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 7).setCellValue(itemRev.getProperty("fx8_DataSheet"));
                ExcelUtil.getCell(row, 7).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 8).setCellValue(itemRev.getProperty("fx8_FootPrint"));
                ExcelUtil.getCell(row, 8).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 9).setCellValue(itemRev.getProperty("fx8_FrjName"));
                ExcelUtil.getCell(row, 9).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 10).setCellValue(itemRev.getProperty("owning_user"));
                ExcelUtil.getCell(row, 10).setCellStyle(cellStyle);

                ExcelUtil.getCell(row, 11).setCellValue(itemRev.getProperty("creation_date"));
                ExcelUtil.getCell(row, 11).setCellStyle(cellStyle);

                index++;
            }

            // 自动打开文件
            if (workbook != null) {
                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    String dateStr = dateFormat.format(new Date());
                    excelFilePath = chooser.getSelectedFile().getAbsolutePath() + File.separator + "CONNECTOR_"
                            + dateStr + ".xlsx";
                    System.out.println("zhujiang========================excelFilePath==" + excelFilePath);
                    FileOutputStream out = new FileOutputStream(excelFilePath);
                    workbook.write(out);
                    out.flush();
                    out.close();
                    // Desktop.getDesktop().open(excelFile);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
