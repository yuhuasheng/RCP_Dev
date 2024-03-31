package com.foxconn.sdebom.dtl5ebom.dialog;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.foxconn.sdebom.constant.D9Constant;
import com.foxconn.sdebom.dtl5ebom.pojo.DtL5BomPojo;
import com.foxconn.sdebom.dtl5ebom.pojo.DtL5ItemRevPojo;
import com.foxconn.sdebom.factory.BomFactory;
import com.foxconn.sdebom.pojo.BomLinePojo;
import com.foxconn.sdebom.service.BomService;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class DtL5EbomDialog extends Dialog{
	
	private static final double RATIO = 0.4D;
	private Shell parentShell;
	private TCSession session;
	private Dimension dim;
	
	private Shell shell;
	private Registry reg;
	
	private static Text logText;
	private Button startButton;

	public DtL5EbomDialog(AbstractAIFUIApplication app, Shell parentShell, Dimension dim) throws Exception{
		super(parentShell);
		this.session = (TCSession) app.getSession();
		this.parentShell = parentShell;
		this.dim = dim;
		reg = Registry.getRegistry("com.foxconn.sdebom.dtl5ebom.dtl5ebom");
		initUI();
	}
	
	
	private void initUI() throws Exception {
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize((int) (dim.getWidth() * RATIO), (int) (dim.getHeight() * RATIO));
		shell.setText(reg.getString("Dialog.Title"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		GridData materiaGridData = new GridData(GridData.FILL_HORIZONTAL);
		materiaGridData.heightHint = 100;
		Group materialGroup = new Group(shell, SWT.NONE);
		materialGroup.setLayout(new FillLayout());
		materialGroup.setText(reg.getString("Material.Group"));
		materialGroup.setLayoutData(materiaGridData);
		Text materiaText = new Text(materialGroup, SWT.MULTI | SWT.V_SCROLL);
		
		GridData logGridData = new GridData(GridData.FILL_BOTH);
		Group logGroup = new Group(shell, SWT.NONE);
		logGroup.setLayout(new FillLayout());
		logGroup.setText(reg.getString("Log.Group"));
		logGroup.setLayoutData(logGridData);
		logText = new Text(logGroup, SWT.MULTI | SWT.V_SCROLL);
		logText.setEditable(false);

		GridData startGridData = new GridData();
		startGridData.horizontalAlignment = GridData.CENTER;
		startGridData.widthHint = 80;
		startButton = new Button(shell, SWT.PUSH);
		startButton.setText(reg.getString("Start.Button"));
		startButton.setLayoutData(startGridData);
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setStartButton(false);
				String contentStr = materiaText.getText();
				String lineDelimiter = materiaText.getLineDelimiter();
				String[] contentLines = contentStr.split(lineDelimiter);
				List<String> materialNums = new ArrayList<>();
				for (int i = 0; i < contentLines.length; i++) {
					String contentLine = contentLines[i].trim();
					if (!"".equals(contentLine)) {
						materialNums.add(contentLine);
					}
				}
				if (materialNums.size() == 0) {
					TCUtil.errorMsgBox("请输入料号！", "");
					return;
				}
				List<String> distinctMaterialNums = materialNums.stream().distinct().collect(Collectors.toList());
				new Thread(new Runnable() {
					@Override
					public void run() {
						TCUtil.setBypass(session);
						try {
							writeLogText("構建 L5 EBOM 開始！");
		//					String resultJson = readTxt("E:\\dtl5ebom.txt");
							String paramsJson = toJson(distinctMaterialNums);
							String actionUrl = "http://10.202.17.151:8080/TCIntegrateWeb/L5Ebom_getEbom.action";
							writeLogText("正在獲取Agile數據，請稍等..");
							String resultJson = TCUtil.post(actionUrl, paramsJson.toString());
							BomService dtL5Bom = BomFactory.getBomType(session, D9Constant.DT_L5);
							List<DtL5ItemRevPojo> dtL5ItemRevPojos = dtL5Bom.jsonConvertItemRevPojo(resultJson);
							writeLogText("正在創建料號，請稍等..");
							dtL5Bom.createItemRev(dtL5ItemRevPojos);
							List<DtL5BomPojo> dtL5BomPojoList = dtL5Bom.jsonConvertBomPojo(resultJson);
							for (int i = 0; i < dtL5BomPojoList.size(); i++) {
								DtL5BomPojo agileDtL5BomPojo = dtL5BomPojoList.get(i);
								String itemId = agileDtL5BomPojo.getItemId();
								List<DtL5ItemRevPojo> dtL5ItemRevPojo = dtL5ItemRevPojos.stream().filter(s -> s.getItemId().equals(itemId)).collect(Collectors.toList());
								TCComponent[] queryResult = TCUtil.executeQuery(session, D9Constant.ITEM_NAME_OR_ID, new String[]{D9Constant.D9_ITEM_ID}, new String[]{itemId});
								if (queryResult.length == 0) {
									writeLogText("系統中未查詢到【" + itemId + "】料號.");
									dtL5Bom.firstBuild(agileDtL5BomPojo,dtL5ItemRevPojo.get(0));
								}else {
									writeLogText("系統中已查詢到【" + itemId + "】料號.");
									TCComponentItem item = (TCComponentItem) queryResult[0];
									TCComponentItemRevision itemRev = item.getLatestItemRevision();
									boolean isRele = TCUtil.isReleased(itemRev);
									if (isRele) {
										boolean saveAs = isSaveAs(itemRev, dtL5ItemRevPojo.get(0));
										if (saveAs) {
											String versionRule = TCUtil.getVersionRule(itemRev);
											String newRevId = TCUtil.reviseVersion(session, versionRule, itemRev.getTypeObject().getName(), itemRev.getUid());
											itemRev = itemRev.saveAs(newRevId);	
										}
									}
									setItemRevProperty(itemRev, dtL5ItemRevPojo.get(0));
									BomLinePojo tcDtL5BomPojo = dtL5Bom.itemRevConvertBomPojo(itemRev);
									dtL5Bom.changeBuild(agileDtL5BomPojo, tcDtL5BomPojo);
								}
							}
							writeLogText("构建 L5 EBOM 结束！");
						} catch (Exception e2) {
							e2.printStackTrace();
							writeLogText("错误："+ e2.getMessage());
							writeLogText("构建 L5 EBOM 结束！");
						}
						setStartButton(true);
						TCUtil.closeBypass(session);
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
	
	public String toJson(List<String> materialNums) {
		StringBuffer paramsJson = new StringBuffer();
		paramsJson.append("[");
		for (int i = 0; i < materialNums.size(); i++) {
			paramsJson.append("{\"itemId\":\"");
			paramsJson.append(materialNums.get(i));
			paramsJson.append("\"}");
			if ((i+1) != materialNums.size()) {
				paramsJson.append(",");
			}
		}
		paramsJson.append("]");
		return paramsJson.toString();
	}
	
	private void setStartButton(boolean isEnabled){
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				startButton.setEnabled(isEnabled);
			}
		});
    }
	
	public static void writeLogText(String message){
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				logText.append(message + "\n");
			}
		});
    }
	
	private boolean isSaveAs(TCComponentItemRevision itemRev, DtL5ItemRevPojo dtL5ItemRevPojo) throws Exception {
		String englishDesc1 = itemRev.getProperty(D9Constant.D9_ENGLISH_DESC);
		String chineseDesc1 = itemRev.getProperty(D9Constant.D9_CHINESE_DESC);
		String customerPN1 = itemRev.getProperty(D9Constant.D9_CUSTOMER_PN);
		String manufacturerID1 = itemRev.getProperty(D9Constant.D9_MANUFACTURER_ID);
		String manufacturerPN1 = itemRev.getProperty(D9Constant.D9_MANUFACTURER_PN);
		String assemblyCode1 = itemRev.getProperty(D9Constant.D9_ASSEMBLY_CODE);
		
		String englishDesc2 = dtL5ItemRevPojo.getEnglishDescription();
		String chineseDesc2 = dtL5ItemRevPojo.getChineseDescription();
		String customerPN2 = dtL5ItemRevPojo.getCustomerPN();
		String manufacturerID2 = dtL5ItemRevPojo.getManufacturerID();
		String manufacturerPN2 = dtL5ItemRevPojo.getManufacturerPN();
		String assemblyCode2 = dtL5ItemRevPojo.getAssemblyCode();
		
		if (!englishDesc1.equals(englishDesc2)) {
			return true;
		}
		if (!chineseDesc1.equals(chineseDesc2)) {
			return true;
		}
		if (!customerPN1.equals(customerPN2)) {
			return true;
		}
		if (!manufacturerID1.equals(manufacturerID2)) {
			return true;
		}
		if (!manufacturerPN1.equals(manufacturerPN2)) {
			return true;
		}
		if (!assemblyCode1.equals(assemblyCode2)) {
			return true;
		}
		return false;
	}
	
	private void setItemRevProperty(TCComponentItemRevision itemRev, DtL5ItemRevPojo dtL5ItemRevPojo) throws Exception {
		itemRev.setProperty(D9Constant.D9_ENGLISH_DESC, dtL5ItemRevPojo.getEnglishDescription());
		itemRev.setProperty(D9Constant.D9_CHINESE_DESC, dtL5ItemRevPojo.getChineseDescription());
		itemRev.setProperty(D9Constant.D9_CUSTOMER_PN, dtL5ItemRevPojo.getCustomerPN());
		itemRev.setProperty(D9Constant.D9_MANUFACTURER_ID, dtL5ItemRevPojo.getManufacturerID());
		itemRev.setProperty(D9Constant.D9_MANUFACTURER_PN, dtL5ItemRevPojo.getManufacturerPN());
		itemRev.setProperty(D9Constant.D9_ASSEMBLY_CODE, dtL5ItemRevPojo.getAssemblyCode());
	}
	
	public static String readTxt(String txtPath) {
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
        File file = new File(txtPath);
        if(file.isFile() && file.exists()){
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    sb.append(text);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
            	try {
					bufferedReader.close();
					inputStreamReader.close();
	            	fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }
        return null;
    }


}
