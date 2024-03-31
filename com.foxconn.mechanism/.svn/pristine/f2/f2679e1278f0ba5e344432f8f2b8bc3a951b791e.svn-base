package com.foxconn.mechanism.hhpnmaterialapply.export;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTBOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTPLDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.IExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.DTExportOperation;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.DTExportTools;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.ProgressBarThread;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;

public class ExportDialog extends AbstractAIFDialog
{
    private ProgressBarThread barThread;

    public ExportDialog(AbstractPSEApplication app, String bu)
    {
        super(true);
        InterfaceAIFComponent aifComponent = app.getTargetComponent();
        if (aifComponent instanceof TCComponentBOMLine)
        {
            barThread = new ProgressBarThread("Wait", bu + " Part List Export Running");
            try
            {
                // TCComponentBOMLine topBOMLine = app.getBOMWindow().getTopBOMLine();
                TCComponentBOMLine topBOMLine = (TCComponentBOMLine) aifComponent;
                
                if (doExport(topBOMLine, bu))
                {
                    barThread.stopBar();
                    MessageBox.displayModalMessageBox(TCUtil.getDesktop().getFrame(), "Successfully", "Info", MessageBox.INFORMATION);
                }
                barThread.stopBar();
            }
            catch (Exception e)
            {
                barThread.stopBar();
                TCUtil.errorMsgBox("export fail !  cause : " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        else
        {
            TCUtil.errorMsgBox("Please select BOM Line!");
        }
    }

    public boolean doExport(TCComponentBOMLine topBOMLine, String bu) throws Exception
    {
        System.out.println("bu -->>>> " + bu);
        // String filePath = CommonTools.getFilePath("test");
        // System.out.println(filePath);
        File exportFile = openFileChooser(IExportServices.getDefaultFileName(topBOMLine));
        if (exportFile == null)
        {
            return false;
        }
        if (exportFile != null)
        {
            barThread.start();
            List<TCComponentBOMLine> bomLines = null;
            Date startDate = new Date();
            if ("PRT".equals(bu) || "MNT".equals(bu))
            {
                bomLines = TCUtil.getTCComponmentBOMLines(topBOMLine, null, false);
                IExportServices exportServices = IExportServices.getInstance(bu);
                exportServices.setBOMLines(topBOMLine, bomLines);
                exportServices.dataHandle();
                exportServices.generatePartsExcel(exportFile);
            }
            else if ("DT".equals(bu))
            {
                List<DTBOMInfo> list = null;
                list = DTExportTools.getBOMLineList(list, topBOMLine, false);
                list.removeIf(info -> CommonTools.isEmpty(info.getPartType()) || CommonTools.isEmpty(info.getOptionalType()));                
                IExportServices exportServices = IExportServices.getInstance(bu);
                DTExportOperation dtExportOperation = new DTExportOperation(list, topBOMLine);
                dtExportOperation.dataHandle();
                boolean flag = dtExportOperation.generatePartsExcel(exportFile);
                if (!flag) {
                	return false;
				}
                System.out.println(exportFile.getPath());
            }
            Date endDate = new Date();
            IExportServices.writeLog(topBOMLine.getItemRevision(), startDate, endDate, "人工編制PartList時間");
//            Runtime.getRuntime().exec("cmd /c start " + exportFile.getPath());
        }
        return true;
    }

    public File openFileChooser(String fileName)
    {
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
        jFileChooser.setFileFilter(filter);
        // jFileChooser.setDialogTitle("选择文件夹");
        jFileChooser.setSelectedFile(new File(fileName + ".xlsx"));
        int openDialog = jFileChooser.showSaveDialog(null);
        if (openDialog == JFileChooser.APPROVE_OPTION)
        {
            File file = jFileChooser.getSelectedFile();
            // File file = new File(CommonTools.getFilePath("test") + "1.xlsx");
            String fname = jFileChooser.getName(file);
            if (fname.indexOf(".xlsx") == -1)
            {
                file = new File(jFileChooser.getCurrentDirectory(), fname + ".xlsx");
            }
            return file;
        }
        return null;
    }
}
