package com.foxconn.electronics.convertebom;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class ConvertEBOMDialog2 extends Dialog
{
    private Shell                         parentShell;
    private Shell                         shell;
    private AbstractPSEApplication        pseApp;
    private TCSession                     session;
    private Registry                      reg;
    private TCComponentBOMLine            designTopBOMLine;
    private SelfPart                      selfPart;
    private Text                          drawingNoText;     // 圖號
    private Text                          projectText;       // 專案
    private ComboViewer                   projectPhase;
    private Combo                         partFolderCombo;   // 物料文件夾
    private Text                          pcaPartNumberText; // PCA料號
    private Text                          pcaPartDescText;   // 描述
    private static Text                   logText;           // 日志
    private Button                        validationButton;
    private Button                        convertEBOMButton;
    private TCBOM                         productLineBOM;
    private List<TCComponentItemRevision> selfPartList;
    private String                        productLine   = "";
    private String                        pcaPartNumber = "";

    public ConvertEBOMDialog2(Shell parentShell, AbstractPSEApplication pseApp, TCComponentBOMLine designTopBOMLine) throws Exception
    {
        super(parentShell);
        this.parentShell = parentShell;
        this.pseApp = pseApp;
        this.session = pseApp.getSession();
        this.designTopBOMLine = designTopBOMLine;
        selfPart = new SelfPart(designTopBOMLine.getItem());
        reg = Registry.getRegistry("com.foxconn.electronics.convertebom.convertebom");
        initUI(); // 界面初始化
    }

    private void initUI() throws Exception
    {
        shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
        shell.setSize(750, 740);
        shell.setText(reg.getString("dialogTitle"));
        shell.setLayout(new GridLayout(4, false));
        TCUtil.centerShell(shell);
        Image image = getDefaultImage();
        if (image != null)
        {
            shell.setImage(image);
        }
        GridData gridDataLabel = new GridData();
        gridDataLabel.horizontalIndent = 50;
        GridData gridDataText = new GridData();
        gridDataText.widthHint = 220;
        Label drawingNoLabel = new Label(shell, SWT.RIGHT);
        drawingNoLabel.setText(reg.getString("drawingNoLabel"));
        drawingNoLabel.setLayoutData(gridDataLabel);
        drawingNoText = new Text(shell, SWT.BORDER);
        drawingNoText.setLayoutData(gridDataText);
        drawingNoText.setEnabled(false);
        drawingNoText.setText(selfPart.getDrawingNo());
        Label projectLabel = new Label(shell, SWT.RIGHT);
        projectLabel.setText(reg.getString("projectLabel"));
        projectLabel.setLayoutData(gridDataLabel);
        projectText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
        projectText.setLayoutData(gridDataText);
        projectText.setEnabled(false);
        projectText.setText(selfPart.getProductLine() + "_" + selfPart.getProjectName());
        Label partFolderLabel = new Label(shell, SWT.RIGHT);
        partFolderLabel.setText(reg.getString("partFolder"));
        partFolderLabel.setLayoutData(gridDataLabel);
        partFolderCombo = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
        partFolderCombo.setLayoutData(gridDataText);
        partFolderCombo.setItems(selfPart.getPartFolderName());
        partFolderCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionevent)
            {
                try
                {
                    String partFolderName = partFolderCombo.getText();
                    selfPartList = selfPart.getSelfPart(partFolderName);
                    TCComponentItemRevision pcaPart = selfPart.getPCAPart(selfPartList);
                    String itemId = pcaPart.getProperty(Constants.ITEM_ID);
                    String desc = pcaPart.getProperty(Constants.ENGLISH_DESC);
                    pcaPartNumberText.setText(itemId);
                    pcaPartDescText.setText(desc);
                    List<Object[]> phaseFolderInfo = selfPart.getPhase(partFolderName);
                    // zl phaseFolderInfo.sort(null);
                    // Collections.reverse(phaseFolderInfo);
                    projectPhase.setInput(phaseFolderInfo);
                    if (phaseFolderInfo.size() > 0)
                    {
                        projectPhase.getCombo().select(0);
                    }
                }
                catch (Exception e)
                {
                    writeLogText(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        Label productLineLabel = new Label(shell, SWT.RIGHT);
        productLineLabel.setText(reg.getString("productLineLabel"));
        productLineLabel.setLayoutData(gridDataLabel);
        // projectPhase = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
        // projectPhase.setLayoutData(gridDataText);
        projectPhase = new ComboViewer(shell, SWT.BORDER | SWT.READ_ONLY);
        projectPhase.setContentProvider(new ArrayContentProvider());
        projectPhase.setLabelProvider(new LabelProvider()
        {
            public String getText(Object element)
            {
                if (element instanceof Object[])
                {
                    Object[] array = (Object[]) element;
                    return (String) array[0];
                }
                return "";
            }
        });
        Combo combo = projectPhase.getCombo();
        combo.setLayoutData(gridDataText);
        Label pcaPartNumberLabel = new Label(shell, SWT.RIGHT);
        pcaPartNumberLabel.setText(reg.getString("PCAPartNumberLabel"));
        pcaPartNumberLabel.setLayoutData(gridDataLabel);
        pcaPartNumberText = new Text(shell, SWT.BORDER);
        pcaPartNumberText.setLayoutData(gridDataText);
        pcaPartNumberText.setEnabled(false);
        Label pcaDescLabel = new Label(shell, SWT.RIGHT);
        pcaDescLabel.setText(reg.getString("englishDescription"));
        pcaDescLabel.setLayoutData(gridDataLabel);
        pcaPartDescText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
        pcaPartDescText.setLayoutData(gridDataText);
        pcaPartDescText.setEnabled(false);
        GridData logTextGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        logTextGridData.grabExcessHorizontalSpace = true;
        logTextGridData.horizontalSpan = 4;
        logTextGridData.heightHint = 550;
        logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER|SWT.WRAP);
        logText.setLayoutData(logTextGridData);
        logText.setEditable(false);
        new Label(shell, SWT.None);
        GridData convertGridData = new GridData();
        convertGridData.horizontalAlignment = GridData.CENTER;
        convertGridData.horizontalSpan = 1;
        convertGridData.widthHint = 80;
        GridData downloadGridData = new GridData();
        downloadGridData.horizontalAlignment = GridData.END;
        downloadGridData.horizontalSpan = 1;
        downloadGridData.widthHint = 80;
        validationButton = new Button(shell, SWT.PUSH);
        validationButton.setText(reg.getString("validationButton"));
        validationButton.setLayoutData(downloadGridData);
        validationButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                StructuredSelection structuredSelection = (StructuredSelection) projectPhase.getSelection();
                Object selectObject = structuredSelection.getFirstElement();
                // 重新开线程处理业务，解决UI界面卡死问题
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            clearLogText();
                            getPCAPartNumber();
                            if ("".equals(pcaPartNumber))
                            {
                                throw new Exception("PCA料號不可為空值！");
                            }
                            setValidationButton(false);
                            TCComponentFolder placefolder = null;
                            if (selectObject != null && selectObject instanceof Object[])
                            {
                                Object[] selectElement = (Object[]) selectObject;
                                placefolder = (TCComponentFolder) selectElement[1];
                                // String partModelFolderName = partFolderCombo.getText();
                                // String partFolderName = partModelFolderName.split(SelfPart.FOLDER_NAME_SEPARATOR)[1];
                                // placefolder = SelfPart.getChildFolderByName(folder, partFolderName);
                                // mBom.setPlaceTxtMapping(placefolder);
                            }
                            else
                            {
                                throw new Exception("項目階段不可為空！");
                            }
                            productLineBOM = BOMFactory.getProductLineBOM(selfPart.getProductLine(), designTopBOMLine, selfPartList, selfPart.getActionMsgMap(), placefolder);
                            productLineBOM.validation();// BOM检验
                            productLineBOM.comparison(selfPart.getActionMsgMap());// BOM比对
                            productLineBOM.comparisonEBOM();
                            //
                            setConvertEBOMButton(true);
                        }
                        catch (Exception e)
                        {
                            writeLogText(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        convertEBOMButton = new Button(shell, SWT.PUSH);
        convertEBOMButton.setText(reg.getString("convertButton"));
        convertEBOMButton.setLayoutData(convertGridData);
        convertEBOMButton.setEnabled(false);
        convertEBOMButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionevent)
            {
                Object phaseObject = projectPhase.getStructuredSelection().getFirstElement();
                String partFolder = partFolderCombo.getText();
                // 重新开线程处理业务，解决UI界面卡死问题
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TCComponentItemRevision newPcaRev = null;
                        try
                        {
                            setConvertEBOMButton(false);
                            newPcaRev = productLineBOM.buildEBOM();
                        }
                        catch (Exception e)
                        {
                            writeLogText(e.getMessage() + ",程序結束！");
                            e.printStackTrace();
                        }
                        if (newPcaRev != null)
                        {
                            try
                            {
                                String phaseFolder = (String) ((Object[]) phaseObject)[0];
                                selfPart.archivePCA(newPcaRev, partFolder, phaseFolder);
                            }
                            catch (Exception e)
                            {
                                writeLogText(" PCA EBOM歸檔到部門文件夾下失敗,可能的原因：" + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        GridData exportGridData = new GridData();
        exportGridData.horizontalAlignment = GridData.BEGINNING;
        exportGridData.horizontalSpan = 1;
        exportGridData.widthHint = 120;
        Button exportButton = new Button(shell, SWT.PUSH);
        exportButton.setText("受影響衍生機種");
        exportButton.setLayoutData(exportGridData);
        exportButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionevent)
            {
                try
                {
                    ShowDeriveModelDialog dialog = new ShowDeriveModelDialog(shell, designTopBOMLine.getItemRevision());
                    dialog.open();
                }
                catch (TCException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
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

    public void getProductLine()
    {
        this.selfPart.getProductLine();
    }

    public void getPCAPartNumber()
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                pcaPartNumber = pcaPartNumberText.getText();
            }
        });
    }

    public static void clearLogText()
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                logText.setText("");
            }
        });
    }

    public static void writeLogText(String message)
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                logText.append(message);
            }
        });
    }

    private void setValidationButton(boolean isEnabled)
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                validationButton.setEnabled(isEnabled);
            }
        });
    }

    private void setConvertEBOMButton(boolean isEnabled)
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                convertEBOMButton.setEnabled(isEnabled);
            }
        });
    }
}
