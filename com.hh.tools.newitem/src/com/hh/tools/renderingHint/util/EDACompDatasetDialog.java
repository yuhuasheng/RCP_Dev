package com.hh.tools.renderingHint.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;

import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.util.SelectFileFilter;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.iButton;

/**
 * �����ϼ����ݼ� ����
 * 
 * @author wangsf
 */
public class EDACompDatasetDialog extends AbstractAIFDialog implements ActionListener {

    private IDatasetFolderOperation datasetFolderOperation;
    private String dialogTitle;
    // ����Ŀ���
    private int dialogWidth = 780;
    private int dialogHeight = 550;

    private String datasetFolderPath = null;
    private TCComponentFolder datasetFolderComp = null;
    // ���ݼ�Map key: ���ݼ����� value: ���ݼ�����
    private Map<String, TCComponentDataset> datasetMap = null;
    // �ϴ����ļ�(ֻ�����ϴ�һ��)
    private File uploadFile = null;
    // ���ͷ��
    private String[] headers = {"No", "Position", "Dataset-Name", "File-Name", "Owning_user", "Last_Modi_Time"};
    // ��ʾ�ı�����
    private TCTable tableObj = null;
    // ��ѯ�����ݼ���ʶ
//	private boolean queryDatasetFlag = false;

    private JScrollPane jScrollPane;
    private iButton uploadBtn;
    private iButton queryBtn;
    private iButton downloadBtn;

    private FileFilter fileFilter;

    public EDACompDatasetDialog(String dialogTitle, IDatasetFolderOperation datasetFolderOperation) {
        super(true);
        this.dialogTitle = dialogTitle;
        this.datasetFolderOperation = datasetFolderOperation;
        this.datasetFolderPath = datasetFolderOperation.getFolderNodes();
        this.datasetFolderComp = datasetFolderOperation.getDatasetFolder();
        this.uploadFile = datasetFolderOperation.getRelationFile();

        // ����ļ��� ����ʾUI
        if (checkDatasetFolder()) {
            initUI();
        }
    }

    /**
	 * ��������������ݼ� �ļ����Ƿ����
	 */
    private boolean checkDatasetFolder() {
        System.out.println("EDACompDatasetDialog checkDatasetFolder Nodes => " + this.datasetFolderPath);
        if (null == this.datasetFolderComp) {
            MessageBox.post(this, "Dataset folder Not Exist!", "Error", MessageBox.ERROR);
            return false;
        }

        // ���ļ����� �������ݼ��б�
        loadDatasetList();
        return true;
    }

    public void initUI() {
//		System.out.println("----------------- EmpCompDatasetDialog exect initUI ------------------");
        setTitle(this.dialogTitle);
        setSize(this.dialogWidth, this.dialogHeight);

        // ��Ⱦ������
        this.tableObj = new TCTable(headers) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int arg0, int arg1) {
                return false;
            }
        };
        this.tableObj.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.tableObj.getTableHeader().setResizingAllowed(true);
        this.tableObj.setRowHeight(25);

        // ��ʼ���������
        initTable();

        // �б�panel
        JPanel listPanel = new JPanel(new BorderLayout());
        // ������panel
        this.jScrollPane = new JScrollPane(this.tableObj);
        this.jScrollPane.setPreferredSize(new Dimension(this.dialogWidth, this.dialogHeight));
        listPanel.add(this.jScrollPane, "Center");

        // �ײ�panel
        JPanel southPanel = new JPanel();

        // �ϴ���ť
        if (datasetFolderOperation.isCanUpload()) {
            this.uploadBtn = new iButton("Upload");
            this.uploadBtn.addActionListener(this);
            southPanel.add(this.uploadBtn);

            // ���˫�������¼�
            this.tableObj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {

                    if (event.getClickCount() > 1) {
                        dispose();

                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                // �ӱ�������� ��ȡ���� ����Object-Name��File-Name
                                Object positionCell = tableObj.getValueAt(tableObj.getSelectedRow(), 1);
                                Object objectNameCell = tableObj.getValueAt(tableObj.getSelectedRow(), 2);
                                if (StringUtils.isNotBlank(positionCell.toString())) {
                                    String objectName = objectNameCell.toString();
                                    TCComponentDataset downDataset = datasetMap.get(objectName);
                                    if (null != downDataset) {
                                        datasetFolderOperation.setRelationDataset(downDataset);
                                    }
                                } else {
                                    // ˫���˿հ��� �Ƴ���������
                                    datasetFolderOperation.clearRelationData();
                                }
                            }
                        });
                    }
                }
            });
        }

        // ��ѯ��ť
        this.queryBtn = new iButton("Query");
        this.queryBtn.addActionListener(this);
        southPanel.add(this.queryBtn);

        // ���ذ�ť
        this.downloadBtn = new iButton("Download");
        this.downloadBtn.addActionListener(this);
        southPanel.add(this.downloadBtn);

        // ��panel��ӵ�������
        add(listPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        centerToScreen();
        setVisible(true);
    }

    private void initTable() {
        try {
            TCComponentDataset currentDataset = datasetFolderOperation.getCurrentDataset();
            if (currentDataset != null) {
                this.tableObj.addRow(getTableData(currentDataset, 1, ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tableObj.addRow(new Object[]{});
    }

    /**
	 * ���ñ������
	 * 
	 * @throws TCException
	 */
    private void loadTable() {
        this.tableObj.removeAllRows();
        int numNo = 1;

        try {
            // ��װ���ݼ�����
            if (null != this.datasetMap && this.datasetMap.size() > 0) {
                for (TCComponentDataset tempDataset : this.datasetMap.values()) {
                    this.tableObj.addRow(getTableData(tempDataset, numNo, this.datasetFolderPath));
                    numNo++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.tableObj.addRow(new Object[]{});
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.uploadBtn) {
            uploadDataset();
        } else if (event.getSource() == this.queryBtn) {
            if (datasetMap.size() > 0) {
                loadTable();
            } else {
            	MessageBox.post(this, "û�в�ѯ�����ݣ�", "Warning", MessageBox.WARNING);
            }
        } else if (event.getSource() == this.downloadBtn) {
            downloadDataset();
        }
    }

    /**
	 * �������ݼ��б�
	 */
    private void loadDatasetList() {
        if (null == this.datasetMap) {
            this.datasetMap = new HashMap<String, TCComponentDataset>();
            try {
                // ȡ���ݼ��б�
                TCComponent[] tempComp = this.datasetFolderComp.getRelatedComponents("contents");
                if (null != tempComp && tempComp.length > 0) {
                    TCComponent itemComp = null;
                    TCComponentDataset itemDataset = null;
                    for (int i = 0; i < tempComp.length; i++) {
                        itemComp = tempComp[i];
                        if (itemComp instanceof TCComponentDataset) {
                            itemDataset = (TCComponentDataset) itemComp;
                            this.datasetMap.put(itemDataset.getProperty("object_name"), itemDataset);
                        }
                    }
                }
            } catch (TCException e) {
                e.printStackTrace();
            }
        }
    }

    /**
	 * �ϴ����ݼ�
	 */
    private void uploadDataset() {
        if (null != this.datasetFolderComp) {
            File selectFile = getSelectFileByChooser(this, null);
            if (null != selectFile) {
            	System.out.println("EmpCompDatasetDialog �ϴ����ļ� => " + selectFile.getAbsolutePath());

				// �ļ�����
				String fileName = selectFile.getName();
				// �ж��ϴ����ļ� �����ݼ����Ƿ����
                if (this.datasetMap.containsKey(fileName)) {
                	MessageBox.post(this, fileName + "�Ѵ��ڣ�", "WARN...", MessageBox.WARNING);
                } else {
                    this.uploadFile = selectFile;

                    dispose();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            datasetFolderOperation.setRelationFile(uploadFile);
                        }
                    });
                }
            }

        } else {
            MessageBox.post(this, "Not Get Dataset File Folder!", "Error", MessageBox.ERROR);
        }
    }

    /**
	 * �������ݼ�
	 */
    private void downloadDataset() {
        boolean downFlag = false;

        // ��ȡ���ѡ�е��� ���û��ѡ�� ��ȡ��һ��
        int selectedRowIndex = this.tableObj.getSelectedRow();
        if (selectedRowIndex == -1) {
            selectedRowIndex = 0;
        }

        // �ӱ�������� ��ȡ����
        Object positionCell = this.tableObj.getValueAt(tableObj.getSelectedRow(), 1);
        Object objectNameCell = this.tableObj.getValueAt(selectedRowIndex, 2);
        TCComponentDataset downDataset = null;
        if (StringUtils.isBlank(positionCell.toString())) {
            downDataset = datasetFolderOperation.getCurrentDataset();
        } else {
            String objectName = objectNameCell.toString();
            downDataset = datasetMap.get(objectName);
        }

        String downFilePath = null;
        if (null != downDataset) {
            // �����ļ�����
            File file = fileDialogExport(this);
            if (null != file) {
                downFilePath = DownloadDataset.downloadFile(downDataset, true, file.getAbsolutePath());
                if (null != downFilePath && !"".equals(downFilePath)) {
                    downFlag = true;
                }
            }
        }

        if (downFlag) {
            MessageBox.post(this, "DownLoad Dataset Success! File Path:" + downFilePath, "INFORMATION",
                    MessageBox.INFORMATION);
        }
    }

    /**
	 * ��ȡ�������
	 * 
	 * @param datasetComp ���ݼ����
	 * @param numNo       �б����
	 * @return
	 * @throws TCException
	 */
    private String[] getTableData(TCComponentDataset datasetComp, int numNo, String folderPath) throws TCException {
        String fileName = "";
        TCComponentTcFile tempFile = getFileByDataset(datasetComp);
        if (null != tempFile) {
            fileName = tempFile.getProperty("original_file_name");
        }

        return new String[]{String.valueOf(numNo), folderPath, datasetComp.getProperty("object_name"), fileName,
                datasetComp.getProperty("owning_user"), datasetComp.getProperty("last_mod_date")};
    }

    /**
	 * �������ݼ� ��ȡ�ļ�
	 * 
	 * @param dataset ���ݼ�
	 * @return
	 * @throws TCException
	 */
    private TCComponentTcFile getFileByDataset(TCComponentDataset dataset) throws TCException {
        TCComponentTcFile tempFile = null;
        TCComponentTcFile[] tcFiles = dataset.getTcFiles();

        if (ArrayUtils.isNotEmpty(tcFiles)) {
            tempFile = tcFiles[0];
        }

        return tempFile;
    }

    /**
	 * ��ȡ�ϴ����ļ�
	 * 
	 * @param abstractAIFDialog
	 * @param fileTypes
	 * @return
	 */
    private File getSelectFileByChooser(AbstractAIFDialog abstractAIFDialog, String fileTypes) {
        JFileChooser selectFileChooser = getSelectFileChooser(fileTypes);
        int result = selectFileChooser.showOpenDialog(abstractAIFDialog);
        if (result == 0) {
            File selectFile = selectFileChooser.getSelectedFile();
            if (null != selectFile) {
                // �ļ��Ƿ���ڵĲ���
                if (selectFile.exists()) {
                    return selectFile;
                }
            }
        }

        return null;
    }

    /**
	 * ��ȡѡ���ļ��������
	 * 
	 * @param fileTypes �ļ�����
	 * @return
	 */
    private JFileChooser getSelectFileChooser(String fileTypes) {
        SelectFileFilter selectFileFilter = new SelectFileFilter();
        if (null != fileTypes) {
            selectFileFilter.setFileFormat(fileTypes);
        }

        JFileChooser selectFileChooser = new JFileChooser();
        selectFileChooser.setFileSelectionMode(0);
        selectFileChooser.setFileFilter(selectFileFilter);
        selectFileChooser.addChoosableFileFilter(selectFileFilter);
        return selectFileChooser;
    }

    /**
	 * �����ļ�������
	 * 
	 * @param abstractAIFDialog ��ǰ������
	 * @return
	 */
    private File fileDialogExport(AbstractAIFDialog abstractAIFDialog) {
        try {
            JFileChooser jf = new JFileChooser();
            jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileFilter != null) {
                jf.addChoosableFileFilter(fileFilter);
                jf.setFileFilter(fileFilter);
            }
            int result = jf.showOpenDialog(abstractAIFDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = jf.getSelectedFile();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }
}
