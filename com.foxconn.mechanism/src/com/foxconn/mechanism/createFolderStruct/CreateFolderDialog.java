package com.foxconn.mechanism.createFolderStruct;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.Registry;

public class CreateFolderDialog extends Dialog
{
    private Registry                       reg                 = Registry.getRegistry(CreateFolderDialog.class);
    private CreateFolderService            createFolderService = new CreateFolderService();
    private Shell                          shell;
    private JComboBox<FolderTemplateModel> jComboBox;
    private JTextField                     folderName;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public CreateFolderDialog(Shell parentShell)
    {
        super(parentShell);
        this.shell = parentShell; // 可以让父窗口阻塞
        initUI(parentShell);
    }

    private JPanel mainContent()
    {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_contentPanel = new GridBagLayout();// gbl_contentPanel 布局可以拖拉
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_contentPanel);
        {
            JLabel selectTemplate = new JLabel(reg.getString("fileNameLabel"));
            GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
            gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
            gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel.gridx = 1;
            gbc_lblNewLabel.gridy = 0;
            panel.add(selectTemplate, gbc_lblNewLabel);
        }
        {
            JLabel fileNameLabel = new JLabel(reg.getString("selectTemplate"));
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
            gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
            gbc_lblNewLabel_1.gridx = 1;
            gbc_lblNewLabel_1.gridy = 1;
            panel.add(fileNameLabel, gbc_lblNewLabel_1);
        }
        {
            folderName = new JTextField();
            GridBagConstraints gbc_textField = new GridBagConstraints();
            gbc_textField.insets = new Insets(0, 0, 5, 0);
            gbc_textField.fill = GridBagConstraints.HORIZONTAL;
            gbc_textField.gridx = 2;
            gbc_textField.gridy = 0;
            panel.add(folderName, gbc_textField);
            folderName.setColumns(10);
        }
        {
            jComboBox = new JComboBox(createFolderService.getFolderTemplates());
            GridBagConstraints gbc_comboBox = new GridBagConstraints();
            gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_comboBox.gridx = 2;
            gbc_comboBox.gridy = 1;
            panel.add(jComboBox, gbc_comboBox);
        }
        return panel;
    }

    private JPanel btnPanel()
    {
        JButton btnOk = new JButton(reg.getString("btnOk"));
        JButton btnCancel = new JButton(reg.getString("btnCancel"));
        btnOk.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                Display.getDefault().syncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        doOk();
                    }
                });
            }
        });
        btnCancel.addActionListener(new ActionListener()
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
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);
        return btnPanel;
    }

    private void doOk()
    {
        int result = -1;
        try
        {
            String rootFolerName = folderName.getText();
            String itemId = ((FolderTemplateModel) jComboBox.getSelectedItem()).getItemId();
            if (rootFolerName == null || rootFolerName.trim().length() == 0 || itemId == null)
            {
                result = 0;
            }
            // check ok
            if (result != 0)
            {
                createFolderService.startCreateFolder(rootFolerName, itemId);
                result = 1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            switch (result)
            {
                case 1:
                    TCUtil.infoMsgBox(reg.getString("completeMessage"));
                    shell.dispose();
                    break;
                case 0:
                    TCUtil.warningMsgBox(reg.getString("warnMessage"));
                    break;
                case -1:
                    TCUtil.errorMsgBox(reg.getString("errorMessage"));
                    break;
            }
        }
    }

    // 构建界面
    private void initUI(Shell parentShell)
    {
        shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
        shell.setSize(412, 190);
        shell.setText(reg.getString("dialogTitle"));
        shell.setLayout(new FillLayout());
        TCUtil.centerShell(shell);
        shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
        Composite mainComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        Frame frame = SWT_AWT.new_Frame(mainComposite);
        frame.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.add(mainContent(), BorderLayout.CENTER);
        contentPanel.add(btnPanel(), BorderLayout.SOUTH);
        frame.add(contentPanel, BorderLayout.CENTER);
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
