package com.hh.tools.dataset.action;

import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.hh.tools.newitem.DownloadDataset;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.Registry;

public class DatesetDownloadAction extends AbstractAIFAction {

    private AbstractAIFUIApplication app;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");

    public DatesetDownloadAction(AbstractAIFUIApplication application, Frame arg1, String arg2) {
        super(application, arg1, arg2);
        this.app = application;
    }

    @Override
    public void run() {
        openDownloadDiag();
    }

    private void openDownloadDiag() {
        InterfaceAIFComponent component = this.app.getTargetComponent();
//		System.out.println("type class==="+component.getClass().toString());
//		System.out.println("object type==="+component.getType());
        TCComponentDataset dataset = (TCComponentDataset) component;
        TCComponentTcFile[] tcfiles;
        try {
            tcfiles = dataset.getTcFiles();
            if (tcfiles != null && tcfiles.length > 0) {
                TCComponentTcFile onetcfile = tcfiles[0];
                String filename = onetcfile.getProperty("original_file_name");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择下载路径");
                fileChooser.setApproveButtonText(reg.getString("Save.Msg"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setSelectedFile(new File(filename));
                int result = fileChooser.showSaveDialog(fileChooser);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file.exists()) {
                    	Object[] options = { "是", "否" };
                        int opt = JOptionPane.showOptionDialog(null, reg.getString("FileIsExist.Msg"),
                                reg.getString("Info.Msg"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                                null, options, options[0]);
                        if (opt == JOptionPane.YES_OPTION) {
                            DownloadDataset.downloadFile(onetcfile.getFmsFile(), file);
                        } else {
                            openDownloadDiag();
                        }
                    } else {
                        DownloadDataset.downloadFile(onetcfile.getFmsFile(), file);
                    }
                }
            }
        } catch (TCException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}
