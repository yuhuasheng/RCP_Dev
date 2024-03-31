package com.foxconn.electronics.explodebom;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.explodebom.domain.InputInfoBean;
import com.foxconn.electronics.explodebom.service.ExplodeBOMService;
import com.foxconn.electronics.util.Phase;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class ExplodeBOMDialog extends Dialog
{
    private AbstractPSEApplication pseApp  = null;
    private TCSession              session = null;
    private Registry               reg     = null;
    private Shell                  shell   = null;

    public ExplodeBOMDialog(AbstractPSEApplication app, Shell parent, Registry reg)
    {
        super(parent);
        this.pseApp = app;
        this.session = (TCSession) this.pseApp.getSession();
        this.shell = parent;
        this.reg = reg;
        initUI();
    }

    private void initUI()
    {
        shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
        shell.setSize(800, 500);
        shell.setText(reg.getString("dialogTitle"));
        shell.setLayout(new FillLayout());
        TCUtil.centerShell(shell);
        Image image = getDefaultImage();
        if (image != null)
        {
            shell.setImage(image);
        }
        Composite mainComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        Frame frame = SWT_AWT.new_Frame(mainComposite);
        frame.setLayout(new BorderLayout());
        String[] columnNames = new String[] { reg.getString("tableColumn1"), reg.getString("tableColumn2") };
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        JTable table = new JTable(model)
        {
            public javax.swing.table.JTableHeader getTableHeader()
            {
                JTableHeader tableHeader = super.getTableHeader();
                tableHeader.setReorderingAllowed(false);// 表格列不可移动
                DefaultTableCellRenderer hr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
                hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);// 列名居中
                return tableHeader;
            };
        };
        table.setRowHeight(30);
        JScrollPane tableJsp = new JScrollPane();
        tableJsp.setPreferredSize(new Dimension(800, 300));
        tableJsp.setViewportView(table);
        JScrollPane logJsp = new JScrollPane();
        JTextArea logjTextArea = new JTextArea();
        logjTextArea.setLineWrap(true);
        logjTextArea.setEditable(false);
        logJsp.setViewportView(logjTextArea);
        JComboBox<String> jComboBox = new JComboBox<String>(new String[] { Phase.MP.toString(), Phase.NPI.toString() });
        JPanel jPanel = new JPanel();
        JButton addRowButton = new JButton("+");
        addRowButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                model.addRow(new String[] {});
            }
        });
        JButton deleteRowButton = new JButton("-");
        deleteRowButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                int count[] = table.getSelectedRows();
                if (count.length <= 0)
                {
                    Display.getDefault().syncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            logjTextArea.append(reg.getString("promptInfo2") + "\n");
                        }
                    });
                    return;
                }
                else
                {
                    for (int i = count.length; i > 0; i--)
                    {
                        model.removeRow(table.getSelectedRow());
                    }
                }
            }
        });
        JButton confirmButton = new JButton(reg.getString("confirmButton"));
        confirmButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                Display.getDefault().syncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int rowCount = table.getRowCount();
                        if (rowCount == 0)
                        {
                            logjTextArea.append(reg.getString("promptInfo3") + "\n");
                            return;
                        }
                        List<InputInfoBean> inputInfoBeans = new ArrayList<InputInfoBean>();
                        String phase = (String) jComboBox.getSelectedItem();
                        for (int i = 0; i < rowCount; i++)
                        {
                            table.getCellEditor(i, 0).stopCellEditing();
                            table.getCellEditor(i, 1).stopCellEditing();
                            Object projectName = model.getValueAt(i, 0);
                            Object bomNo = model.getValueAt(i, 1);
                            if (projectName == null || bomNo == null)
                            {
                                logjTextArea.append(reg.getString("promptInfo4") + "\n");
                                return;
                            }
                            String projectNameStr = projectName.toString();
                            Integer bomNoStr = Integer.valueOf(model.getValueAt(i, 1).toString());
                            InputInfoBean inputInfoBean = new InputInfoBean();
                            inputInfoBean.setPcbNumber(projectNameStr);
                            inputInfoBean.setBomNo(bomNoStr);
                            inputInfoBean.setPhase(phase);
                            inputInfoBeans.add(inputInfoBean);
                        }
                        logjTextArea.append(reg.getString("promptInfo5") + "\n");
                        try
                        {
                            TCComponentBOMLine topBOMLine = pseApp.getBOMWindow().getTopBOMLine();
                            Map<TCComponentBOMLine, String> executeGenBomFile = ExplodeBOMService.executeGenBom(session, topBOMLine, inputInfoBeans);
                            if (executeGenBomFile != null && executeGenBomFile.size() > 0)
                            {
                                for (Entry<TCComponentBOMLine, String> info : executeGenBomFile.entrySet())
                                {
                                    TCComponentBOMLine key = info.getKey();
                                    String bomName = key.getProperty("bl_item_object_name");
                                    logjTextArea.append("【" + bomName + "】" + info.getValue() + "\n");
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        logjTextArea.append(reg.getString("promptInfo6"));
                    }
                });
            }
        });
        JButton cancelButton = new JButton(reg.getString("cancelButton"));
        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                Display.getDefault().syncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        shell.dispose();
                    }
                });
            }
        });
        jPanel.add(jComboBox);
        jPanel.add(addRowButton);
        jPanel.add(deleteRowButton);
        jPanel.add(confirmButton);
        jPanel.add(cancelButton);
        frame.add(tableJsp, BorderLayout.NORTH);
        frame.add(logJsp, BorderLayout.CENTER);
        frame.add(jPanel, BorderLayout.SOUTH);
        shell.open();
        shell.layout();
        Display display = shell.getDisplay();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }
}
