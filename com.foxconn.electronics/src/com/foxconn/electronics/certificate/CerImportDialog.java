package com.foxconn.electronics.certificate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.commands.copy.CopyOperation;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;

public class CerImportDialog extends Dialog
{
    TCSession           tcSession = RACUIUtil.getTCSession();
    private Text        filePathText;
    private StyledText  logPanel;
    private ComboViewer comboViewer;
    private Button      chooseBtn;
    private Button      downloadTemplateBtn;
    private boolean     isRunning = false;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public CerImportDialog(Shell parentShell)
    {
        super(parentShell);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(null);
        comboViewer = new ComboViewer(container, SWT.NONE);
        Combo combo = comboViewer.getCombo();
        combo.setBounds(84, 10, 185, 23);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider()
        {
            public String getText(Object element)
            {
                String objectName = "";
                if (element instanceof TCComponentProject)
                {
                    try
                    {
                        TCComponentProject project = (TCComponentProject) element;
                        objectName = project.getProjectID() + "-" + project.getProjectName();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                return objectName;
            }
        });
        try
        {
            TCComponentProject[] projects = getProjects();
            comboViewer.setInput(projects);
        }
        catch (TCException e)
        {
            MessageBox.post("Error", "获取专案信息失败!", MessageBox.ERROR);
            e.printStackTrace();
        }
        filePathText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        filePathText.setEnabled(false);
        filePathText.setBounds(287, 10, 244, 25);
        chooseBtn = new Button(container, SWT.NONE);
        chooseBtn.setBounds(537, 8, 80, 27);
        chooseBtn.setText("选择文件");
        downloadTemplateBtn = new Button(container, SWT.NONE);
        downloadTemplateBtn.setBounds(623, 8, 80, 27);
        downloadTemplateBtn.setText("下载模板");
        logPanel = new StyledText(container, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        logPanel.setEditable(false);
        logPanel.setBounds(10, 59, 693, 314);
        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setBounds(14, 13, 65, 17);
        lblNewLabel.setText("请选择专案");
        Label label = new Label(container, SWT.NONE);
        label.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
        label.setBounds(8, 15, 5, 17);
        label.setText("*");
        addListener();
        return container;
    }

    private void addListener()
    {
        Shell shell = getShell();
        chooseBtn.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent event)
            {
                String filePath = TCUtil.openFileChooser(shell);
                if (CommonTools.isNotEmpty(filePath))
                {
                    filePathText.setText(filePath);
                    infoLog("选择的导入证书的 Excel 文件  ： " + filePath);
                }
            }
        });
        downloadTemplateBtn.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent event)
            {
                File exportFile = TCUtil.openSaveFileDialog(shell, "Teamcenter_Certificate_Import_Template.xlsx");
                if (CommonTools.isNotEmpty(exportFile))
                {
                    this.getClass().getClassLoader().getResourceAsStream("");
                    // try (InputStream in = Objects.requireNonNull(this.getClass()
                    // .getClassLoader()
                    // .getResourceAsStream("com/foxconn/electronics/certificate/Teamcenter_Certificate_Import_Template.xlsx"), "模板文件不存在"))
                    try
                    {
                        File templateFile = getTemplateFile();
                        Files.copy(templateFile.toPath(), exportFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        infoLog("导入模板下载成功 ： " + exportFile.getAbsolutePath());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        errorLog("导入模板下载失败 ：" + e.getMessage());
                    }
                }
            }
        });
        Combo combo = comboViewer.getCombo();
        combo.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                IStructuredSelection selection = comboViewer.getStructuredSelection();
                Object object = selection.getFirstElement();
                if (object == null)
                {
                    combo.setText("");
                }
            }

            @Override
            public void focusGained(FocusEvent arg0)
            {
            }
        });
    }

    private TCComponentProject[] getProjects() throws TCException
    {
        TCComponentUser user = tcSession.getUser();
        TCComponentProjectType projectType = (TCComponentProjectType) tcSession.getTypeComponent(ITypeName.TC_Project);
        return projectType.extent(user, true);
    }

    public File getTemplateFile() throws Exception
    {
        TCComponentDatasetType datasetType = (TCComponentDatasetType) RACUIUtil.getTCSession().getTypeComponent(ITypeName.Dataset);
        TCComponentDataset templateDataset = datasetType.find("Teamcenter_Certificate_Import_Template.xlsx");
        File file = templateDataset.getTcFiles()[0].getFmsFile();
        return file;
    }

    @Override
    protected void buttonPressed(int buttonId)
    {
        Button btn = getButton(buttonId);
        switch (buttonId)
        {
            case IDialogConstants.OK_ID:
                new Thread(() -> {
                    try
                    {
                        setRunningFlag(btn, true);
                        execute();
                    }
                    catch (Exception e1)
                    {
                        errorLog("终止导入! 初始化TC 服务失败,可能的原因：" + e1.getMessage());
                        e1.printStackTrace();
                    }
                    finally
                    {
                        setRunningFlag(btn, false);
                    }
                }).start();
                break;
            case IDialogConstants.CANCEL_ID:
                super.buttonPressed(buttonId);
                break;
            default:
                break;
        }
    }

    public void setRunningFlag(Button btn, boolean flag)
    {
        Display.getDefault().syncExec(() -> {
            isRunning = flag;
            btn.setEnabled(!flag);
            if (!flag)
            {
                filePathText.setText("");
            }
        });
    }

    public TCComponentProject getSelectProject()
    {
        return Display.getDefault().syncCall(() -> {
            IStructuredSelection selection = comboViewer.getStructuredSelection();
            TCComponentProject project = (TCComponentProject) selection.getFirstElement();
            return project;
        });
    }

    public String getExcelPath()
    {
        return Display.getDefault().syncCall(() -> {
            return filePathText.getText();
        });
    }

    public void execute() throws Exception
    {
        TCComponentProject project = getSelectProject();
        String excelPath = getExcelPath();
        if (CommonTools.isEmpty(excelPath))
        {
            errorLog("必須上傳 Excel 文件");
            return;
        }
        File excelFile = new File(excelPath);
        if (!excelFile.exists())
        {
            errorLog(" 上传的文件不存在 " + excelFile);
            return;
        }
        if (CommonTools.isEmpty(project))
        {
            errorLog("必須選擇一個專案");
            return;
        }
        infoLog("*************開始读取Excel*************");
        CerService cerService = new CerService(project);
        List<CerPojo> list = null;
        try
        {
            list = cerService.readExcel(excelFile, CerPojo.class);
        }
        catch (Exception e)
        {
            errorLog("终止导入! 读取Excel 失败,可能的原因：" + e.getMessage());
            e.printStackTrace();
            return;
        }
        if (CommonTools.isNotEmpty(list))
        {
            infoLog("*************開始檢查Excel *************");
            List<String> errorList = cerService.cerValidate(list);
            if (CommonTools.isEmpty(errorList))
            {
                infoLog("*************Excel检查通过 *************");
            }
            else
            {
                errorList.forEach(this::errorLog);
                errorLog("终止导入! Excel 数据不符合要求，请修改后再次导入。 ");
                return;
            }
        }
        else
        {
            errorLog(" 终止导入! 导入的Excel 没有数据");
            return;
        }
        infoLog("*************開始執行導入,请稍候***********");
        for (int k = 0; k < list.size(); k++)
        {
            CerPojo pojo = list.get(k);
            int index = k + 1;
            try
            {
                cerService.createItem(pojo);
                infoLog("第 " + index + " 行导入TC 成功");
            }
            catch (Exception e)
            {
                errorLog("第 " + index + " 行导入TC 失败，可能的原因：" + e.getMessage());
                e.printStackTrace();
            }
        }
        infoLog("*************导入完成*************");
    }

    private void infoLog(String text)
    {
        Display.getDefault().syncExec(() -> {
            logPanel.append("[Info]" + text + "\n");
            logPanel.setTopIndex(logPanel.getLineCount() - 1);
        });
    }

    private void errorLog(String text)
    {
        Display.getDefault().syncExec(() -> {
            int length = logPanel.getText().length();
            logPanel.append("[Error]" + text + "\n");
            logPanel.setStyleRange(getColorStyle(length, text.length() + 7, this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED)));
            logPanel.setTopIndex(logPanel.getLineCount() - 1);
        });
    }

    private StyleRange getColorStyle(int startOffset, int length, Color color)
    {
        StyleRange styleRange = new StyleRange(startOffset, length, color, null);
        // styleRange.fontStyle = SWT.BOLD;
        return styleRange;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize()
    {
        return new Point(733, 480);
    }

    @Override
    public boolean close()
    {
        if (isRunning)
        {
            MessageDialog.openWarning(getShell(), "警告", "導入程式正在運行，不能關閉窗口");
        }
        else
        {
            boolean flag = MessageDialog.openConfirm(getShell(), "提示", "是否關閉窗口退出導入?");
            System.out.println("flag : " + flag);
            if (flag == true)
            {
                return super.close();
            }
        }
        return false;
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText("Import Certificate");
    }
}
