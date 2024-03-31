package historicaldataimport.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.filechooser.FileSystemView;

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

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import historicaldataimport.domain.ExcelRowInfo;
import historicaldataimport.utils.Utils;

public class UpdateSeriesProjectNameDialog extends Dialog{
	
	private static final Logger log = LoggerFactory.getLogger(UpdateSeriesProjectNameDialog.class);
	private Shell parentShell = null;
	private TCSession session = null;
	private Shell shell = null;
	private Registry reg = null;
	private Button uploadButton = null;
	private Button importButton = null;
	private Text logText = null;

	public UpdateSeriesProjectNameDialog(Shell parentShell,TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("historicaldataimport.hdi");
		initUI();
	}
		
	private void initUI() {
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(460, 500);
		shell.setText(reg.getString("UpdateSeriesProjectName"));
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
						List<Sheet> sheetList = Utils.getSheet(excelFile, new String[]{"Series", "Project"});
						List<ExcelRowInfo> excelRowInfos = readExcelData(sheetList);
						updateName(excelRowInfos);
						writeLogText("【程序结束】更新成功！");
						log.info("【程序结束】更新成功！");
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
	
	private List<ExcelRowInfo> readExcelData(List<Sheet> sheetList) {
		List<ExcelRowInfo> excelRowInfos = new ArrayList<>();
		for (int i = 0; i < sheetList.size(); i++) {
			Sheet sheet = sheetList.get(i);
			int rowCount = sheet.getPhysicalNumberOfRows();
			for (int j = 1; j < rowCount; j++) {
				Row row = sheet.getRow(j);
				ExcelRowInfo excelRowInfo = new ExcelRowInfo();
				for (int k = 0; k < 2; k++) {
					Cell cell = row.getCell(k);
					cell.setCellType(CellType.STRING);
					String cellValue = cell.getStringCellValue().trim();
					switch (k) {
						case 0:
							excelRowInfo.setId(cellValue);
							break;
						case 1:
							excelRowInfo.setName(cellValue);
							break;
						default:
							break;
					}
				}
				writeLogText("【Excel_Data】id：" + excelRowInfo.getId() + "，name：" + excelRowInfo.getName());
				log.info("【Excel_Data】id：" + excelRowInfo.getId() + "，name：" + excelRowInfo.getName());
				excelRowInfos.add(excelRowInfo);
			}
		}
		return excelRowInfos;
	}
	
	private void writeLogText(String message){
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				logText.append(message + "\n");
			}
		});
    }

	private void updateName(List<ExcelRowInfo> excelRowInfos) {
		try {
			for (int i = 0; i < excelRowInfos.size(); i++) {
				ExcelRowInfo excelRowInfo = excelRowInfos.get(i);
				String id = excelRowInfo.getId();
				String prefix = id.substring(0, 1);
				String name = excelRowInfo.getName();
				TCComponent[] result = executeQuery(session, "__D9_Find_Project_Folder", 
						new String[] {"d9_SPAS_ID"}, new String[] {id});
				if (result.length != 0) {
					writeLogText("【正在修改】id：" + id + "的文件夹.");
					log.info("【正在修改】id：" + id + "的文件夹.");
					TCComponentFolder folder = (TCComponentFolder) result[0];
					folder.setProperty("object_name", name);
					if("p".equals(prefix)) {
						TCComponent[] result1 = executeQuery(session, "__D9_Find_Project", 
								new String[] {"project_id"}, new String[] {id});
						if(result1.length != 0) {
							writeLogText("【正在修改】id：" + id + "的项目.");
							log.info("【正在修改】id：" + id + "的项目.");
							TCComponentProject project =  (TCComponentProject) result1[0];
							name = name.replaceAll("\\*", "_").replaceAll("\\.", "_").replaceAll("%", "_").replaceAll("@", "_");
							project.setProperty("object_name", name);
						}
					}
				}else {
					writeLogText("【提示】TC中未找到id：" + id + "的文件夹");
					log.info("【提示】TC中未找到id：" + id + "的文件夹");
				}
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
