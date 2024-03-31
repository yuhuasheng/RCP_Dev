package historicaldataimport.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import historicaldataimport.domain.ExcelRowInfo;
import historicaldataimport.domain.MoveProjectInfo;
import historicaldataimport.utils.Utils;

public class MoveProjectDialog extends Dialog{
	
	private static final Logger log = LoggerFactory.getLogger(MoveProjectDialog.class);
	private Shell parentShell = null;
	private TCSession session = null;
	private Shell shell = null;
	private Registry reg = null;
	private Button uploadButton = null;
	private Button importButton = null;
	private Text logText = null;

	public MoveProjectDialog(Shell parentShell,TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("historicaldataimport.hdi");
		initUI();
	}
		
	private void initUI() {
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(460, 500);
		shell.setText(reg.getString("MoveProject"));
		shell.setLayout(new GridLayout(1, false));
		Utils.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		
		Composite composite1 = new Composite(shell, SWT.NONE);
		composite1.setLayout(new GridLayout(2, false));
		composite1.setLayoutData(gridData);
		
		Text filePathText = new Text(composite1, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false);
		filePathText.setLayoutData(gridData);
		
		uploadButton = new Button(composite1, SWT.NONE);
		uploadButton.setText(reg.getString("uploadFile"));
		
		GridData gridData0 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData0.grabExcessHorizontalSpace = true;
		gridData0.heightHint = 360;
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		logText.setLayoutData(gridData0);
		logText.setEditable(false);
		
		Composite composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayout(new GridLayout(2, true));
		composite2.setLayoutData(gridData);
		
		GridData gridData1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.END;
		
		
		importButton = new Button(composite2, SWT.NONE);
		importButton.setText(reg.getString("importFile"));
		
		
		uploadButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String filePath = Utils.openFileChooser(shell);
				if(filePath == null) {
					return;
				}
				filePathText.setText(filePath);
			}
		});
		
		importButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String filePath = filePathText.getText();
				if("".equals(filePath)) {
					MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo2"));
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						File excelFile = new File(filePath);
						Sheet sheet = Utils.getSheet(excelFile);
						List<MoveProjectInfo> moveProjectInfos = readExcelData(sheet);
						moveProject(moveProjectInfos);
						writeLogText("【程序结束】移动完成！");
						log.info("【程序结束】移动完成！");
					}
				}).start();
			}
		});

		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private List<MoveProjectInfo> readExcelData(Sheet sheet) {
		List<MoveProjectInfo> moveProjectInfos = new ArrayList<>();
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			MoveProjectInfo moveProjectInfo = new MoveProjectInfo();
			for (int j = 0; j < 4; j++) {
				Cell cell = row.getCell(j);
				cell.setCellType(CellType.STRING);
				String cellValue = cell.getStringCellValue().trim();
				switch (j) {
					case 0:
						moveProjectInfo.setSeriesId(cellValue);
						break;
					case 1:
						moveProjectInfo.setSeriesName(cellValue);
						break;
					case 2:
						moveProjectInfo.setProjectId(cellValue);
						break;
					case 3:
						moveProjectInfo.setProjectName(cellValue);
						break;
					default:
						break;
				}
			}
			writeLogText("【Excel_Data】SeriesId：" + moveProjectInfo.getSeriesId() + "，SeriesName：" + moveProjectInfo.getSeriesName()
			+ "，ProjectId：" + moveProjectInfo.getProjectId() + "，ProjectName：" + moveProjectInfo.getProjectName());
			log.info("【Excel_Data】SeriesId：" + moveProjectInfo.getSeriesId() + "，SeriesName：" + moveProjectInfo.getSeriesName()
			+ "，ProjectId：" + moveProjectInfo.getProjectId() + "，ProjectName：" + moveProjectInfo.getProjectName());
			moveProjectInfos.add(moveProjectInfo);
		}		
		return moveProjectInfos;
	}
	
	private void writeLogText(String message){
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				logText.append(message + "\n");
			}
		});
    }
	
	private void moveProject(List<MoveProjectInfo> moveProjectInfos){
		try {
			for (int i = 0; i < moveProjectInfos.size(); i++) {
				MoveProjectInfo moveProjectInfo = moveProjectInfos.get(i);
				String seriesId = moveProjectInfo.getSeriesId();
				String projectId = moveProjectInfo.getProjectId();
				TCComponentFolder seriesFolder = null;
				TCComponentFolder projectFolder = null;
				
				TCComponent[] result1 = executeQuery(session, "__D9_Find_Project_Folder", 
						new String[] {"d9_SPAS_ID"}, new String[] {seriesId});
				if (result1.length != 0) {
					seriesFolder = (TCComponentFolder) result1[0];
				}else {
					writeLogText("【提示】TC中未找到id：" + seriesId + "的系列文件夹");
					log.info("【提示】TC中未找到id：" + seriesId + "的系列文件夹");
					continue;
				}
				
				TCComponent[] result2 = executeQuery(session, "__D9_Find_Project_Folder", 
						new String[] {"d9_SPAS_ID"}, new String[] {projectId});
				if (result2.length != 0) {
					projectFolder = (TCComponentFolder) result2[0];
				}else {
					writeLogText("【提示】TC中未找到id：" + seriesId + "的专案文件夹");
					log.info("【提示】TC中未找到id：" + seriesId + "的专案文件夹");
					continue;
				}
				
				AIFComponentContext[] whereReferenced = projectFolder.whereReferenced();
				if(whereReferenced.length > 0) {
					TCComponentFolder oldseriesFolder = (TCComponentFolder) whereReferenced[0].getComponent();
					oldseriesFolder.remove("contents", projectFolder);
				}
				seriesFolder.add("contents", projectFolder);
			}
		} catch (Exception e) {
			writeLogText("【错误】" + e.getMessage());
			log.info("【错误】" + e.getMessage());
		}
	}
	
	public static TCComponent[] executeQuery(TCSession session, String queryName, String[] keys, String[] values) throws Exception
    {
        TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
        TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(queryName);
        if (keys.length != values.length)
        {
            throw new Exception("queryAttributies length is not equal queryValues length");
        }
        String[] queryAttributeDisplayNames = new String[keys.length];
        TCQueryClause[] elements = query.describe();
        for (int i = 0; i < keys.length; i++)
        {
            for (TCQueryClause element : elements)
            {
                // System.out.println("queryName: " + element.getAttributeName());
                if (element.getAttributeName().equals(keys[i]))
                {
                    queryAttributeDisplayNames[i] = element.getUserEntryNameDisplay();
                }
            }
            if (queryAttributeDisplayNames[i] == null || queryAttributeDisplayNames[i].equals(""))
            {
                throw new Exception("queryAttribute\"" + keys[i] + "\"未找到对应的显示名称");
            }
        }
        // System.out.println("queryAttributeDisplayNames:" + Arrays.toString(queryAttributeDisplayNames));
        // System.out.println("queryValues:" + Arrays.toString(values));
        return query.execute(queryAttributeDisplayNames, values);
    }
}
