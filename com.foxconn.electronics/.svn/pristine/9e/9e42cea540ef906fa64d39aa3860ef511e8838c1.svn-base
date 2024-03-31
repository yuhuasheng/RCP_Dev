package com.foxconn.electronics.managementebom.export.bom.prt;

import java.awt.Frame;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.ProgressBarThread;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class PrtBomExportAction extends AbstractAIFAction
{
    private AbstractAIFApplication app     = null;
    private TCSession              session = null;
    private Registry               reg     = null;
    private ProgressBarThread      barThread;

    public PrtBomExportAction(AbstractAIFApplication arg0, Frame arg1, String arg2)
    {
        super(arg0, arg1, arg2);
        this.app = arg0;
        this.session = (TCSession) app.getSession();
        reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
    }

    @Override
    public void run()
    {
        try
        {
            barThread = new ProgressBarThread(reg.getString("wait.MSG"), "Prt BOM " + reg.getString("export.MSG"));
            InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
            if (!(targetComponent instanceof TCComponentBOMLine))
            {
                TCUtil.warningMsgBox(reg.getString("selectErr2.MSG"), reg.getString("WARNING.MSG"));
                return;
            }
            TCComponentBOMLine bomLine = (TCComponentBOMLine) targetComponent;
            PrtExportBOMHandle prtHandle = new PrtExportBOMHandle(bomLine);
            File exportFile = openFileChooser(prtHandle.getExcelFileName());
            if (CommonTools.isEmpty(exportFile))
            {
                MessageBox.displayModalMessageBox(AIFDesktop.getActiveDesktop()
                                                            .getFrame(), "You must select a directory", "Warn", MessageBox.WARNING);
            }
            barThread.start();
            prtHandle.writeFile(exportFile);
            barThread.stopBar();
            TCUtil.infoMsgBox(reg.getString("exportSuccess.MSG"), reg.getString("INFORMATION.MSG"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            barThread.stopBar();
            TCUtil.errorMsgBox(reg.getString("exportFailure.MSG"), reg.getString("ERROR.MSG"));
        }
    }

    public File openFileChooser(String fileName)
    {
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
        jFileChooser.setFileFilter(filter);
        jFileChooser.setSelectedFile(new File(fileName));
        int openDialog = jFileChooser.showSaveDialog(null);
        if (openDialog == JFileChooser.APPROVE_OPTION)
        {
            File file = jFileChooser.getSelectedFile();
            String fname = jFileChooser.getName(file);
            if (fname.indexOf(".xlsx") == -1)
            {
                file = new File(jFileChooser.getCurrentDirectory(), fname);
            }
            return file;
        }
        return null;
    }
}
