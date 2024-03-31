package com.foxconn.electronics.convertebom;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
//import java.util.Set;
//import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.convertebom.service.impl.ConvertEBOMFactory;
import com.foxconn.electronics.convertebom.service.impl.ConvertMonitorEBOM;
import com.foxconn.electronics.domain.BOMPojo;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.domain.ItemRev2Info;
import com.foxconn.electronics.domain.ItemRevInfo;
import com.foxconn.electronics.util.HttpUtil;
//import com.foxconn.electronics.progress.BooleanFlag;
//import com.foxconn.electronics.progress.IProgressDialogRunnable;
//import com.foxconn.electronics.progress.LoopProgerssDialog;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class ConvertEBOMDialog extends Dialog{
	
	private Shell parentShell = null;
	private AbstractPSEApplication pseApp;
	private TCSession session = null;
	private Shell shell = null;
	private Registry reg = null;
	//private boolean condition;
	private Text projectText = null;
	private Text productLineText = null;
	private List<TCComponentFolder> partFolders = null;
	private TCComponentFolder selfPartFolder1 = null;
	private Combo partFolderCombo = null;
	private Text PCAPartNumberText = null;
	private Text PCAPartDescText = null;
	private String productLine;
	private String PCAPartNumber;
	private TCComponentItemRevision pcaPartItemRev = null;
	private List<ItemRevInfo> pacChildrenAddDataList = null;
	private List<ItemRevInfo> pacChildrenRemoveDataList = null;
	private static Text logText = null;
	private Button validationButton = null;
	private Button convertEBOMButton = null;
	private TCComponentBOMLine designTopBOMLine = null;
	private List<BOMPojo> bomPojos1 = new ArrayList<BOMPojo>();
	private List<BOMPojo> bomPojos2 = new ArrayList<BOMPojo>();
	private List<BOMPojo> bomPojos3 = new ArrayList<BOMPojo>();
	private TCComponentFolder selfPartFolder = null;
	private Map<String, List<BOMPojo>> designBOMVarietyData = null;
	private List<BOMPojo> designBOMVarietyData1 = null;
	private List<BOMPojo> eBOMMatterList = new ArrayList<BOMPojo>();
	private List<BOMPojo> designBOMMatterList = new ArrayList<BOMPojo>();
	private List<BOMPojo> eBOMRemoveData = null;
	private Map<String, List<BOMPojo>> eBOMAddData = null;
	List<String> itemRevIdList = new ArrayList<String>();
	List<String> removeDataParentIds = new ArrayList<String>();
	List<String> varietyDataParentIds = new ArrayList<String>();
	private String url;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public ConvertEBOMDialog(Shell parentShell, AbstractPSEApplication pseApp) {
		super(parentShell);
		this.parentShell = parentShell;
		this.pseApp = pseApp;
		this.session = pseApp.getSession();
		this.designTopBOMLine = pseApp.getTopBOMLine();
		try {
			this.designTopBOMLine.refresh();
		} catch (TCException e) {
			e.printStackTrace();
		}
		reg = Registry.getRegistry("com.foxconn.electronics.convertebom.convertebom");
		this.url = TCUtil.getPreference(TCUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
//		LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null, reg.getString("正在加载专案，请稍等..."));
//		loopProgerssDialog.run(true, new IProgressDialogRunnable() {
//			@Override
//			public void run(BooleanFlag stopFlag) {
//				try {
//					if (stopFlag.getFlag()) { // 监控是否要让停止后台任务
//						condition = false;
//						return;
//					}
//					
//					initUI();
//					
//					if (stopFlag.getFlag()) {
//						condition = false;
//					} else {
//						condition = true;
//					}
//					stopFlag.setFlag(true); // 执行完毕后把标志位设置为停止，好通知给进度框
//				} catch (Exception e) {
//					e.printStackTrace();
//					MessageDialog.openInformation(shell, reg.getString("prompt"), e.getMessage());
//					stopFlag.setFlag(true);
//				}
//			}
//		});
//		if (condition == false) {
//			return;
//		}
		initUI(); // 界面初始化
	}

	private void initUI() {
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(750, 740);
		shell.setText(reg.getString("dialogTitle"));
		shell.setLayout(new GridLayout(4, false));
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
	
		GridData gridDataLabel = new GridData();
		gridDataLabel.horizontalIndent = 50;
		
		GridData gridDataText = new GridData();
		gridDataText.widthHint = 220;
		
		Label drawingNoLabel = new Label(shell, SWT.RIGHT);
		drawingNoLabel.setText(reg.getString("drawingNoLabel"));
		drawingNoLabel.setLayoutData(gridDataLabel);
		
		Text drawingNoText = new Text(shell, SWT.BORDER);
		drawingNoText.setLayoutData(gridDataText);
		drawingNoText.setEnabled(false);
		
		Label projectLabel = new Label(shell, SWT.RIGHT);
		projectLabel.setText(reg.getString("projectLabel"));
		projectLabel.setLayoutData(gridDataLabel);
		
		projectText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		projectText.setLayoutData(gridDataText);
		projectText.setEnabled(false);
		
		Label productLineLabel = new Label(shell, SWT.RIGHT);
		productLineLabel.setText(reg.getString("productLineLabel"));
		productLineLabel.setLayoutData(gridDataLabel);
		
		productLineText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		productLineText.setLayoutData(gridDataText);
		productLineText.setEnabled(false);
		
		Label partFolderLabel = new Label(shell, SWT.RIGHT);
		partFolderLabel.setText(reg.getString("partFolder"));
		partFolderLabel.setLayoutData(gridDataLabel);
		
		partFolderCombo = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
		partFolderCombo.setLayoutData(gridDataText);
		
		partFolderCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				try {
					String partFolderName = partFolderCombo.getText();
					TCComponentFolder partFolder = null;
					for (int i = 0; i < partFolders.size(); i++) {
						TCComponentFolder partFolder1 = partFolders.get(i);
						String name = partFolder1.getProperty(Constants.OBJECT_NAME);
						if(name.equals(partFolderName)) {
							partFolder = partFolder1;
						}
					}
					partFolder.refresh();
					selfPartFolder = partFolder;
					TCComponentItemRevision pcaPart = getPCAPartTopByPartFolder(partFolder);
					String itemId = pcaPart.getProperty(Constants.ITEM_ID);
					String desc = pcaPart.getProperty(Constants.ENGLISH_DESC);
					PCAPartNumberText.setText(itemId);
					PCAPartDescText.setText(desc);
				} catch (Exception e) {
					writeLogText(e.getMessage() + "\n");
					e.printStackTrace();
				}
			}
		});

		Label PCAPartNumberLabel = new Label(shell, SWT.RIGHT);
		PCAPartNumberLabel.setText(reg.getString("PCAPartNumberLabel"));
		PCAPartNumberLabel.setLayoutData(gridDataLabel);
		
		PCAPartNumberText = new Text(shell, SWT.BORDER);
		PCAPartNumberText.setLayoutData(gridDataText);
		PCAPartNumberText.setEnabled(false);
		
		Label PCAPartDescLabel = new Label(shell, SWT.RIGHT);
		PCAPartDescLabel.setText(reg.getString("englishDescription"));
		PCAPartDescLabel.setLayoutData(gridDataLabel);
		
		PCAPartDescText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		PCAPartDescText.setLayoutData(gridDataText);
		PCAPartDescText.setEnabled(false);
		
		GridData logTextGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		logTextGridData.grabExcessHorizontalSpace = true;
		logTextGridData.horizontalSpan = 4;
		logTextGridData.heightHint = 550;
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		logText.setLayoutData(logTextGridData);
		logText.setEditable(false);
		
		GridData convertGridData = new GridData();
		convertGridData.horizontalAlignment = GridData.BEGINNING;
		convertGridData.horizontalSpan = 2;
		convertGridData.widthHint = 80;
		
		GridData downloadGridData = new GridData();
		downloadGridData.horizontalAlignment = GridData.END;
		downloadGridData.horizontalSpan = 2;
		downloadGridData.widthHint = 80;
		
		validationButton = new Button(shell, SWT.PUSH);
		validationButton.setText(reg.getString("validationButton"));
		validationButton.setLayoutData(downloadGridData);
		
		validationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//重新开线程处理业务，解决UI界面卡死问题
				new Thread(new Runnable() {
					@Override
					public void run() {
						JSONArray logs = new JSONArray();
						String startDate = dateFormat.format(System.currentTimeMillis());
						String functionName = "";
						String userId = "";
						String userName = "";
						String projectId = "";
						String itemId = "";
						String itemRevisionId = "";
						String uid = "";
						try {
							functionName = "Design BOM比對效率";
							TCComponentItem item = designTopBOMLine.getItem();
							String projectIds = item.getProperty(Constants.PROJECT_IDS);
							if(!" ".equals(projectIds)) {
								projectId = Arrays.asList(projectIds.split(",")).get(0);
							}
							TCComponentItemRevision itemRevision = designTopBOMLine.getItemRevision();
							String actualUser = itemRevision.getProperty("d9_ActualUserID");
							if ("".equals(actualUser)) {
								TCComponentUser user = session.getUser();
								userId = user.getUserId();
								userName = user.getUserName();
							}else {
								int indexOf = actualUser.indexOf("(");
								userName = actualUser.substring(0, indexOf);
								userId=  actualUser.substring(indexOf+1,  actualUser.length()-1);
							}
							uid = itemRevision.getUid();
							itemId = itemRevision.getProperty("item_id");
							itemRevisionId = itemRevision.getProperty("item_revision_id");
							validationData();
						} catch (Exception e) {
							writeLogText(e.getMessage() + "\n");
							writeLogText("数据校验失败！\n");
							e.printStackTrace();
						}finally {
							String endDate = dateFormat.format(System.currentTimeMillis());
							JSONObject log = new JSONObject();
							log.put("functionName", functionName);
							log.put("creator", userId);
							log.put("creatorName", userName);
							log.put("project", projectId);
							log.put("itemId", itemId);
							log.put("rev", itemRevisionId);
							log.put("revUid", uid);
							log.put("startTime", startDate);
							log.put("endTime", endDate);
							logs.add(log);
							try {
								HttpUtil.post(url + "/tc-integrate/actionlog/addlog", logs.toJSONString());
							} catch (IOException e) {
								e.printStackTrace();
							}  
						}
					}
				}).start();
			}
		});
		
		convertEBOMButton = new Button(shell, SWT.PUSH);
		convertEBOMButton.setText(reg.getString("convertButton"));
		convertEBOMButton.setLayoutData(convertGridData);
		convertEBOMButton.setEnabled(false);
		convertEBOMButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				PCAPartNumber = PCAPartNumberText.getText();
				if("".equals(PCAPartNumber)) {
					writeLogText("PCA料号不可为空值！");
					return;
				}
				//重新开线程处理业务，解决UI界面卡死问题
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							TCComponentItem queryItem = queryItem(PCAPartNumber);
							pcaPartItemRev = queryItem.getLatestItemRevision();
							TCUtil.setBypass(session);
							convertEBOM();
							TCUtil.closeBypass(session);
							String projectId = "";
							String projectIds = designTopBOMLine.getItem().getProperty(Constants.PROJECT_IDS);
							if (!"".equals(projectIds)) {
								projectId = Arrays.asList(projectIds.split(",")).get(0);
							}
							JSONArray jsonArray = new JSONArray();
							AIFComponentContext[] children = designTopBOMLine.getChildren();
							for (int i = 0; i < children.length; i++) {
								TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
								TCComponentItemRevision itemRev = bomLine.getItemRevision();
								String id = itemRev.getProperty("item_id");
								String uid = itemRev.getUid();
								String rev =itemRev.getProperty("item_revision_id");
								String name = itemRev.getProperty("object_name");
								ItemRev2Info itemRev2Info = new ItemRev2Info();
								itemRev2Info.setId(id);
								itemRev2Info.setUid(uid);
								itemRev2Info.setRevision(rev);
								itemRev2Info.setName(name);
								itemRev2Info.setProject(projectId);
								jsonArray.add(itemRev2Info);
							}
							HttpUtil.post(url + "/tc-integrate/actionlog/add2log", jsonArray.toJSONString());
							writeLogText("【构建EBOM结构结束】...\n");
						} catch (Exception e) {
							TCComponentBOMWindow bomWindow = pseApp.getBOMWindow();
							try {
								bomWindow.save();
								bomWindow.close();
							} catch (TCException e1) {
								e1.printStackTrace();
							}
							writeLogText(e.getMessage() + "\n");
							writeLogText("EBOM转换失败！\n");
							e.printStackTrace();
						}
					}
				}).start();
			}
		});

		try {
			String topBOMId = designTopBOMLine.getProperty(Constants.BL_ITEM_ID);
			String projectIds = designTopBOMLine.getItem().getProperty(Constants.PROJECT_IDS);
			if(" ".equals(projectIds)) {
				throw new Exception("BOM未指派专案，请指派专案后进行此操作！");
			}
			String projectId = Arrays.asList(projectIds.split(",")).get(0);
			TCComponent[] queryReslut = TCUtil.executeQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] { projectId });
			if(queryReslut.length == 0) {
				throw new Exception("ID：" + projectId + "，项目文件夹不存在！");
			}
			TCComponentFolder projectFolder = (TCComponentFolder) queryReslut[0];
			String projectName = projectFolder.getProperty(Constants.OBJECT_NAME);
			TCComponentFolder seriesFolder = (TCComponentFolder) projectFolder.whereReferenced()[0].getComponent();
			String bu = seriesFolder.getProperty(Constants.OBJECT_DESC);
			if(bu.equals(Constants.MNT)) {
				productLine = Constants.MONITOR;
			}else if(bu.equals(Constants.PRT)) {
				productLine = Constants.PRINTER;
			}else {
				throw new Exception("BOM指派的专案为【DT】，请选择【MNT】【PRT】专案进行此操作！");
			}
			drawingNoText.setText(topBOMId);
			projectText.setText(projectName);
			productLineText.setText(productLine);
			getSelfPartFolder(projectFolder);
			partFolders = getPartFolder(selfPartFolder1);
			if(partFolders.size() == 0) {
				throw new Exception("未找物料文件夹！\n");
			}
			String[] partFolderStr = new String[partFolders.size()];
			for (int i = 0; i < partFolders.size(); i++) {
				TCComponentFolder tcComponentFolder = partFolders.get(i);
				String name = tcComponentFolder.getProperty(Constants.OBJECT_NAME);
				partFolderStr[i] = name;
			}
			partFolderCombo.setItems(partFolderStr);
		} catch (Exception e) {
			MessageDialog.openInformation(shell, reg.getString("prompt"), e.getMessage());
			e.printStackTrace();
			return;
		}
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private List<TCComponentFolder> getPartFolder(TCComponentFolder selfPartFolder) throws Exception{
		List<TCComponentFolder> partFolders = new ArrayList<TCComponentFolder>();
		AIFComponentContext[] related = selfPartFolder.getRelated(Constants.CONTENTS);
		for (int i = 0; i < related.length; i++) {
			InterfaceAIFComponent component = related[i].getComponent();
			if(component instanceof TCComponentFolder) {
				TCComponentFolder partFolder = (TCComponentFolder) component;
				partFolders.add(partFolder);
			}
		}
		return partFolders;
	}
	
	private void convertEBOM() throws Exception {
		TCComponentBOMLine pcaTopBomLine = TCUtil.openBomWindow(session, pcaPartItemRev);
		
		AIFComponentContext[] pcaTopBomLineChildrens = pcaTopBomLine.getChildren();
		JSONArray logs = new JSONArray();
		String startDate = dateFormat.format(System.currentTimeMillis());
		String userId = session.getUser().getUserId();
		String userName = session.getUser().getUserName();
		String projectId = "";
		TCComponentItem item1 = designTopBOMLine.getItem();
		String projectIds = item1.getProperty(Constants.PROJECT_IDS);
		if(!" ".equals(projectIds)) {
			projectId = Arrays.asList(projectIds.split(",")).get(0);
		}
		TCComponentItemRevision itemRevision1 = designTopBOMLine.getItemRevision();
		String actualUser = itemRevision1.getProperty("d9_ActualUserID");
		if ("".equals(actualUser)) {
			TCComponentUser user = session.getUser();
			userId = user.getUserId();
			userName = user.getUserName();
		}else {
			int indexOf = actualUser.indexOf("(");
			userName = actualUser.substring(0, indexOf);
			userId=  actualUser.substring(indexOf+1,  actualUser.length()-1);
		}
		String itemId1 = itemRevision1.getProperty("item_id");
		String itemRevisionId = itemRevision1.getProperty("item_revision_id");
		String uid = itemRevision1.getUid();
		if(pcaTopBomLineChildrens.length == 0) {
			//第一次搭建EBOM
			firstBuildBOMStructure();
			String endDate = dateFormat.format(System.currentTimeMillis());
			JSONObject log = new JSONObject();
			log.put("functionName", "首版E-BOM製作效率");
			log.put("creator", userId);
			log.put("creatorName", userName);
			log.put("project", projectId);
			log.put("itemId", itemId1);
			log.put("rev", itemRevisionId);
			log.put("revUid", uid);
			log.put("startTime", startDate);
			log.put("endTime", endDate);
			logs.add(log);
			try {
				HttpUtil.post(url + "/tc-integrate/actionlog/addlog", logs.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}else {
			//第二次搭建EBOM
			writeLogText("【构建EBOM结构开始】...\n");
			if(productLine.equals(Constants.MONITOR)) {
				isRevisePCAPart();
				TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine eBOMtopBOMLine = TCUtil.getTopBomline(newBOMWindow, pcaPartItemRev);
				if(pacChildrenAddDataList != null || pacChildrenRemoveDataList != null) {
					buildTopVarietyData(eBOMtopBOMLine);
				}
				getParentReviseData(eBOMtopBOMLine);
				List<String> itemRevReviseId = varietyDataParentIds.stream().distinct().collect(Collectors.toList());
				itemRevReviseOperate(eBOMtopBOMLine,itemRevReviseId);
				newBOMWindow.save();
				newBOMWindow.close();
				
				TCComponentBOMWindow newBOMWindow1 = TCUtil.createBOMWindow(session);
				TCComponentBOMLine eBOMtopBOMLine1 = TCUtil.getTopBomline(newBOMWindow1, pcaPartItemRev);
				try {
					handlerMNTRemoveData(eBOMtopBOMLine1);
					handlerMNTAddData(eBOMtopBOMLine1);
					handlerBOMData(eBOMtopBOMLine1);
					newBOMWindow1.save();
					newBOMWindow1.close();
				} catch (Exception e) {
					newBOMWindow1.save();
					newBOMWindow1.close();
				}
			}
			if(productLine.equals(Constants.PRINTER)) {
				boolean isReleased = TCUtil.isReleased(pcaPartItemRev);
				if (!isReleased) {
					String itemId = pcaPartItemRev.getProperty("item_id");
					String revNum = pcaPartItemRev.getProperty("item_revision_id");
					String workflowName = "TCM Release Process：" + itemId + "/" + revNum;
					TCUtil.createProcess("TCM Release Process", workflowName, "", new TCComponent[] {pcaPartItemRev});
				}
				String versionRule = getVersionRule(pcaPartItemRev); // 返回版本规则
				String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
						pcaPartItemRev.getTypeObject().getName(),pcaPartItemRev.getUid()); // 返回指定版本规则的版本号
				pcaPartItemRev = pcaPartItemRev.saveAs(newRevId);
				TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine eBOMtopBOMLine = TCUtil.getTopBomline(newBOMWindow, pcaPartItemRev);
				handlerPRTRemoveData(eBOMtopBOMLine);
				handlerPRTAddData(eBOMtopBOMLine);
				handlerBOMData(eBOMtopBOMLine);
				newBOMWindow.save();
				newBOMWindow.close();
			}
			String endDate = dateFormat.format(System.currentTimeMillis());
			JSONObject log = new JSONObject();
			log.put("functionName", "E-BOM修改製作效率");
			log.put("creator", userId);
			log.put("creatorName", userName);
			log.put("project", projectId);
			log.put("itemId", itemId1);
			log.put("rev", itemRevisionId);
			log.put("revUid", uid);
			log.put("startTime", startDate);
			log.put("endTime", endDate);
			logs.add(log);
			try {
				HttpUtil.post(url + "/tc-integrate/actionlog/addlog", logs.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		TCComponentItemRevision itemRevision = designTopBOMLine.getItemRevision();
		itemRevision.add("IMAN_specification", pcaPartItemRev);
		setConvertEBOMButton(false);
	}
	
	private void handlerBOMData(TCComponentBOMLine eBOMtopBOMLine) throws Exception{
		for (int i = 0; i < designBOMVarietyData1.size(); i++) {
			BOMPojo bomPojo = designBOMVarietyData1.get(i);
			removeDataMNT(bomPojo,eBOMtopBOMLine);
		}
	}
	
	private void handlerMNTRemoveData(TCComponentBOMLine eBOMtopBOMLine) throws Exception {
		List<BOMPojo> removeDataList = designBOMVarietyData.get("R");
		for (int i = 0; i < removeDataList.size(); i++) {
			BOMPojo bomPojo = removeDataList.get(i);
			removeDataMNT(bomPojo, eBOMtopBOMLine);
		}
	}
	
	private void removeDataMNT(BOMPojo bomPojo, TCComponentBOMLine parentBOMLine) throws Exception {
		String bomLineItemId = bomPojo.getBomLineItemId();
		String bomLineLocation = bomPojo.getBomLineLocation();
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String itemId = children.getItem().getProperty(Constants.ITEM_ID);
				if(itemId.equals(bomLineItemId)) {
					String quantity = children.getProperty(Constants.BL_QUANTITY);
					String location = children.getProperty(Constants.BL_LOCATION);
					int quantity1 = 1;
					if(!"".equals(quantity)) {
						quantity1 = Integer.valueOf(quantity);
					}
					if(quantity1 == 1) {
						children.cut();
					}else {
						List<String> locationsList = Arrays.asList(location.split(","));
						List<String> filterLocation = locationsList.stream().filter(e -> !e.contains(bomLineLocation)).collect(Collectors.toList());
						String locationStr = StringUtils.join(filterLocation, ",");
						children.setProperty(Constants.BL_QUANTITY, String.valueOf(quantity1 - 1));
						children.setProperty(Constants.BL_LOCATION, locationStr);
					}
					break;
				}
				removeDataMNT(bomPojo, children);
			}
		}
	}
	
	private boolean flag = false;
	private void handlerMNTAddData(TCComponentBOMLine eBOMtopBOMLine) throws Exception {
		List<BOMPojo> addDataList = designBOMVarietyData.get("A");
		for (int i = 0; i < addDataList.size(); i++) {
			BOMPojo bomPojo = addDataList.get(i);
			flag = false;
			addDataMNT(bomPojo, eBOMtopBOMLine);
			if(!flag) {
				addDataMNT1(bomPojo, eBOMtopBOMLine);
			}
		}
	}
	
	private void addDataMNT(BOMPojo bomPojo, TCComponentBOMLine parentBOMLine) throws Exception {
		String bomLineItemId = bomPojo.getBomLineItemId();
		String bomLineLocation = bomPojo.getBomLineLocation();
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String itemId = children.getItem().getProperty(Constants.ITEM_ID);
				if(itemId.equals(bomLineItemId)) {
					String quantity = children.getProperty(Constants.BL_QUANTITY);
					int quantity1 = 1;
					if(!"".equals(quantity)) {
						quantity1 = Integer.valueOf(quantity);
					}
					String location = children.getProperty(Constants.BL_LOCATION);
					location = location + "," + bomLineLocation;
					children.setProperty(Constants.BL_QUANTITY, String.valueOf(quantity1 + 1));
					children.setProperty(Constants.BL_LOCATION, location);
					flag = true;
					break;
				}
				addDataMNT(bomPojo, children);
			}
		}
	}
	
	private void addDataMNT1(BOMPojo bomPojo, TCComponentBOMLine parentBOMLine) throws Exception {
		String parentDesc = "";
		String itemId = bomPojo.getBomLineItemId();
		String desc = bomPojo.getBomLineDescription();
		String packageType = bomPojo.getBomLinePackageType();
		String side = bomPojo.getBomLineSide();
		if("DIP".equals(packageType) || desc.contains("HEATSINK")) {
			parentDesc = Constants.MI;
		}
		else if("TOP".equals(side)) {
			parentDesc = Constants.SMT_T;
		}
		else if("BOTTOM".equals(side)) {
			parentDesc = Constants.SMT_B;
		}
		
		TCComponentBOMLine bomLine = null; //包含MI || SMT_T || SMT_B 的bomLine
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine bomLine1 = (TCComponentBOMLine) childrens[i].getComponent();
				String bomLineDesc = bomLine1.getProperty(Constants.BL_DESCRIPTION);
				if(bomLineDesc.contains(parentDesc)) {
					bomLine = bomLine1;
					break;
				}
			}
		}
		
		if(bomLine != null) {
			TCComponentItemRevision latestItemRevision = bomLine.getItem().getLatestItemRevision();
			TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topLine = TCUtil.getTopBomline(newBOMWindow, latestItemRevision);
			String bomLineLocation = bomPojo.getBomLineLocation();
			String bomLineQuantity = bomPojo.getBomLineQuantity();
			TCComponentItem item1 = queryItem(itemId);
			TCComponentBOMLine newBOMLine = topLine.add(item1, item1.getLatestItemRevision(), null, false);
			newBOMLine.setProperty(Constants.BL_DESCRIPTION, desc);
			newBOMLine.setProperty(Constants.BL_LOCATION, bomLineLocation);
			newBOMLine.setProperty(Constants.BL_QUANTITY, bomLineQuantity);
			newBOMLine.setProperty(Constants.BL_SIDE, side);
			newBOMLine.setProperty(Constants.BL_PACKAGE_TYPE, packageType);
			newBOMWindow.save();
			newBOMWindow.close();
		}
	}
	
	private void getParentReviseData(TCComponentBOMLine eBOMtopBOMLine) throws Exception {
		List<BOMPojo> addDataList = designBOMVarietyData.get("A");
		List<BOMPojo> removeDataList = designBOMVarietyData.get("R");
		getExistParentId(addDataList,eBOMtopBOMLine);
		getExistParentId(removeDataList,eBOMtopBOMLine);
	}
	
	private void getExistParentId(List<BOMPojo> varietyData, TCComponentBOMLine eBOMtopBOMLine) throws Exception {
		for (int i = 0; i < varietyData.size(); i++) {
			BOMPojo bomPojo = varietyData.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			getParentId(bomLineItemId,eBOMtopBOMLine);
		}
	}
	
	private void getParentId(String itemRevId, TCComponentBOMLine parentBOMLine) throws Exception {
		TCComponentItem parentItem = parentBOMLine.getItem();
		String parentItemId = parentItem.getProperty(Constants.ITEM_ID);
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				TCComponentItem item = children.getItem();
				String itemId = item.getProperty(Constants.ITEM_ID);
				if(itemId.equals(itemRevId)) {
					varietyDataParentIds.add(parentItemId);
					break;
				}
				getParentId(itemRevId, children);
			}
		}
	}
	
	private void handlerPRTRemoveData(TCComponentBOMLine bomLine) throws Exception{
		List<BOMPojo> removeDataList = designBOMVarietyData.get("R");
		AIFComponentContext[] childrens = bomLine.getChildren();
		for (int i = 0; i < removeDataList.size(); i++) {
			BOMPojo bomPojo = removeDataList.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			String bomLineLocation = bomPojo.getBomLineLocation();
			for (int j = 0; j < childrens.length; j++) {
				TCComponentBOMLine bomLine1 = (TCComponentBOMLine) childrens[j].getComponent();
				String location = bomLine1.getProperty(Constants.BL_LOCATION);
				TCComponentItem item = bomLine1.getItem();
				String itemId = item.getProperty(Constants.ITEM_ID);
				String quantity1 = bomLine1.getProperty(Constants.BL_QUANTITY);
				int quantity = 1;
				if(!"".equals(quantity1)) {
					quantity = Integer.valueOf(quantity1);
				}
				if(itemId.equals(bomLineItemId)) {
					if(quantity == 1) {
						bomLine1.cut();
					}else {
						List<String> locationsList = Arrays.asList(location.split(","));
						List<String> filterLocation = locationsList.stream().filter(e -> !e.contains(bomLineLocation)).collect(Collectors.toList());
						String locationStr = StringUtils.join(filterLocation, ",");
						bomLine1.setProperty(Constants.BL_QUANTITY, String.valueOf(quantity - 1));
						bomLine1.setProperty(Constants.BL_LOCATION, locationStr);
					}
				}
			}
		}
	}
	
	private void handlerPRTAddData(TCComponentBOMLine bomLine) throws Exception{
		List<BOMPojo> addDataList = designBOMVarietyData.get("A");
		AIFComponentContext[] childrens = bomLine.getChildren();
		for (int i = 0; i < addDataList.size(); i++) {
			BOMPojo bomPojo = addDataList.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			String bomLineLocation = bomPojo.getBomLineLocation();
			boolean flag = false;
			for (int j = 0; j < childrens.length; j++) {
				TCComponentBOMLine bomLine1 = (TCComponentBOMLine) childrens[j].getComponent();
				TCComponentItem item = bomLine1.getItem();
				String location = bomLine1.getProperty(Constants.BL_LOCATION);
				String itemId = item.getProperty(Constants.ITEM_ID);
				String quantity1 = bomLine1.getProperty(Constants.BL_QUANTITY);
				int quantity = 1;
				if(!"".equals(quantity1)) {
					quantity = Integer.valueOf(quantity1);
				}
				if (itemId.equals(bomLineItemId)) {
					flag = true;
					List<String> locationsList  = new ArrayList<>(Arrays.asList(location.split(",")));
					locationsList.add(bomLineLocation);
					String locationStr = StringUtils.join(locationsList, ",");
					bomLine1.setProperty(Constants.BL_QUANTITY, String.valueOf(quantity + 1));
					bomLine1.setProperty(Constants.BL_LOCATION, locationStr);
				}
			}
			if(!flag) {
				String bomLineQuantity = bomPojo.getBomLineQuantity();
				String bomLineDescription = bomPojo.getBomLineDescription();
				String bomLineSide = bomPojo.getBomLineSide();
				TCComponentItem item = queryItem(bomLineItemId);
				TCComponentBOMLine newBOMLine = bomLine.add(item, item.getLatestItemRevision(), null, false);
				newBOMLine.setProperty(Constants.BL_DESCRIPTION, bomLineDescription);
				newBOMLine.setProperty(Constants.BL_LOCATION, bomLineLocation);
				newBOMLine.setProperty(Constants.BL_QUANTITY, bomLineQuantity);
				newBOMLine.setProperty(Constants.BL_SIDE, bomLineSide);
			}
		}
	}

	private void firstBuildBOMStructure() throws Exception{
		BOMPojo eBOMpojo = new BOMPojo();
		eBOMpojo.setBomLineItemId(PCAPartNumber);
		allBOMLineUnpackage(designTopBOMLine);
		BOMPojo dBOMpojo = convertBOMPojo(designTopBOMLine, true, true);
		getDesignBOMPojo(dBOMpojo,designTopBOMLine);
		BOMPojo ebom = null;
		if(productLine.equals(Constants.MONITOR)) {
			ConvertMonitorEBOM monitor = ConvertEBOMFactory.monitor();
			ebom = monitor.ConvertEBOM(session,selfPartFolder, dBOMpojo, eBOMpojo);
		}
		if(productLine.equals(Constants.PRINTER)) {
			dBOMpojo.setBomLineItemId(PCAPartNumber);
			List<BOMPojo> child = dBOMpojo.getChild();
			List<BOMPojo> collect = child.stream().filter(e -> !e.getBomLineBOM().equals("NI")).collect(Collectors.toList());
			dBOMpojo.setChild(collect);
			ebom = dBOMpojo;
		}
		boolean isReleased = TCUtil.isReleased(pcaPartItemRev);
		if(isReleased) {
			String versionRule = getVersionRule(pcaPartItemRev); // 返回版本规则
			String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
			pcaPartItemRev.getTypeObject().getName(),pcaPartItemRev.getUid()); // 返回指定版本规则的版本号
			pcaPartItemRev = pcaPartItemRev.saveAs(newRevId);
		}
		TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
		TCComponentBOMLine topBOMLine = TCUtil.getTopBomline(newBOMWindow, pcaPartItemRev);
		writeLogText("正在构建【EBOM】，请稍等...\n");
		buildBOMStructure(topBOMLine, ebom, productLine);
		newBOMWindow.save();
		newBOMWindow.close();
		writeLogText("【DesignBOM】转换【EBOM】结束...\n");
		setConvertEBOMButton(false);
	}
	
	private void buildTopVarietyData(TCComponentBOMLine topBOMLine) throws Exception {
		if(pacChildrenAddDataList.size() > 0) {
			for (int i = 0; i < pacChildrenAddDataList.size(); i++) {
				ItemRevInfo itemRevInfo = pacChildrenAddDataList.get(i);
				TCComponentItemRevision itemRev = itemRevInfo.getItemRev();
				topBOMLine.add(itemRev.getItem(), itemRev, null, false);
			}
		}
		if (pacChildrenRemoveDataList.size() > 0) {
			AIFComponentContext[] children2 = topBOMLine.getChildren();
			for (int i = 0; i < pacChildrenRemoveDataList.size(); i++) {
				ItemRevInfo itemRevInfo = pacChildrenRemoveDataList.get(i);
				String itemId = itemRevInfo.getId();
				for (int j = 0; j < children2.length; j++) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) children2[j].getComponent();
					TCComponentItemRevision itemRev = bomLine.getItemRevision();
					String itemId1 = itemRev.getProperty(Constants.ITEM_ID);
					if(itemId1.equals(itemId)) {
						bomLine.cut();
					}
				}
			}
		}
	}
	
	private void getBOMMatter(TCComponentBOMLine parentBOMLine, String bomType, boolean isCheck) throws Exception {
		AIFComponentContext[] children = parentBOMLine.getChildren();
		if(children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
				String type = bomLine.getItemRevision().getType();
				if("EDAComPart Revision".equals(type)) {
					BOMPojo bomPojo = convertBOMPojo(bomLine, false, isCheck);
					if("D".equals(bomType)) {
						designBOMMatterList.add(bomPojo);
					}
					if("E".equals(bomType)) {
						eBOMMatterList.add(bomPojo);
					}
				}
				getBOMMatter(bomLine, bomType, isCheck);
			}
		}
	}
	
	private Map<String, List<List<String>>> getLocationVarietyData(){
		Map<String, List<List<String>>> locationVarietyMap = new HashMap<String, List<List<String>>>();
		Map<String, List<BOMPojo>> designBOMMatterMap = designBOMMatterList.stream().collect(Collectors.groupingBy(BOMPojo::getBomLineItemId));
		for (int i = 0; i < eBOMMatterList.size(); i++) {
			BOMPojo bomPojo = eBOMMatterList.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			String bomLineLocation = bomPojo.getBomLineLocation();
			List<String> eLocations = Arrays.asList(bomLineLocation.split(","));
			List<String> eLocations1 = eLocations.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
			
			List<String> dLocations = new ArrayList<String>();
			List<BOMPojo> BOMPojoList = designBOMMatterMap.get(bomLineItemId);
			if(BOMPojoList != null && (BOMPojoList.size() == eLocations.size())) {
				for (int j = 0; j < BOMPojoList.size(); j++) {
					BOMPojo bomPojo2 = BOMPojoList.get(j);
					String bomLineLocation2 = bomPojo2.getBomLineLocation();
					dLocations.add(bomLineLocation2);
				}
			}
			
			if(dLocations.size() > 0) {
				List<String> dLocations1 = dLocations.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
				List<String> collect1 = eLocations1.stream().filter(item -> !dLocations1.contains(item)).collect(Collectors.toList());
				List<String> collect2 = dLocations1.stream().filter(item -> !eLocations1.contains(item)).collect(Collectors.toList());
				if(collect1.size() > 0 && collect2.size() > 0) {
					List<List<String>> locationVarietyList = new ArrayList<List<String>>();
					locationVarietyList.add(collect1);//找到它
					locationVarietyList.add(collect2);//修改成它
					locationVarietyMap.put(bomLineItemId, locationVarietyList);
				}
			}
		}
		
		return locationVarietyMap;
	}
	
	private void comparisonAddOrRemoveData(){
		eBOMRemoveData = new ArrayList<BOMPojo>();
		eBOMAddData = new HashMap<String, List<BOMPojo>>();
		Map<String, List<BOMPojo>> designBOMMatterMap = designBOMMatterList.stream().collect(Collectors.groupingBy(BOMPojo::getBomLineItemId));
		
		for (int i = 0; i < eBOMMatterList.size(); i++) {
			BOMPojo bomPojo = eBOMMatterList.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			if(!designBOMMatterMap.containsKey(bomLineItemId)) {
				eBOMRemoveData.add(bomPojo);
			}
		}
		
		for (Entry<String, List<BOMPojo>> bomPojo : designBOMMatterMap.entrySet()) {
			String bomLineItemId = bomPojo.getKey();
			List<BOMPojo> collect = eBOMMatterList.stream().filter(e -> e.getBomLineItemId().equals(bomLineItemId))
								.collect(Collectors.toList());
			if(collect.size() == 0) {
				eBOMAddData.put(bomLineItemId, collect);
			}
			if(collect.size() > 0) {
				List<String> locations = new ArrayList<String>();
				for (int i = 0; i < collect.size(); i++) {
					BOMPojo bomPojo2 = collect.get(i);
					String bomLineLocation = bomPojo2.getBomLineLocation();
					List<String> asList = Arrays.asList(bomLineLocation.split(","));
					locations.addAll(asList);
				}
				if(locations.size() != bomPojo.getValue().size()) {
					List<BOMPojo> bomPojos = bomPojo.getValue();
					List<BOMPojo> addDatas = bomPojos.stream().filter(e -> !locations.contains(e.getBomLineLocation())).collect(Collectors.toList());
					eBOMAddData.put(bomLineItemId, addDatas);
				}
			}
		}
		
	}

	private void getReviseData(TCComponentBOMLine eBOMtopBOMLine, Map<String, List<List<String>>> locationVarietyData) throws Exception{
		for (Entry<String, List<List<String>>> item : locationVarietyData.entrySet()) {
			String itemId = item.getKey();
			getReviseId(eBOMtopBOMLine,itemId);
		}
		
		for (int i = 0; i < eBOMRemoveData.size(); i++) {
			BOMPojo bomPojo = eBOMRemoveData.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			getReviseId(eBOMtopBOMLine, bomLineItemId);
		}
		
		for (Entry<String, List<BOMPojo>> item : eBOMAddData.entrySet()) {
			List<BOMPojo> value = item.getValue();
			for (int i = 0; i < value.size(); i++) {
				BOMPojo bomPojo = value.get(i);
				String packageType = bomPojo.getBomLinePackageType();
				String description = bomPojo.getBomLineDescription();
				String side = bomPojo.getBomLineSide();
				if("DIP".equals(packageType) || "HEATSINK".contains(description)) {
					getReviseId1(eBOMtopBOMLine, "MI");
				}
				if("TOP".equals(side)) {
					getReviseId1(eBOMtopBOMLine, "SMT/T");
				}
				if("BOTTOM".equals(side)) {
					getReviseId1(eBOMtopBOMLine, "SMT/B");
				}
			}
		}
	}
	
	private void getReviseId(TCComponentBOMLine parentBOMLine, String itemId) throws Exception{
		AIFComponentContext[] children = parentBOMLine.getChildren();
		if(children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				TCComponentBOMLine childrenBomLine = (TCComponentBOMLine) children[i].getComponent();
				String bomItemId = childrenBomLine.getItemRevision().getProperty(Constants.ITEM_ID);
				if(bomItemId.equals(itemId)) {
					String parentBOMId = parentBOMLine.getItemRevision().getProperty(Constants.ITEM_ID);
					itemRevIdList.add(parentBOMId);
					break;
				}
				getReviseId(childrenBomLine, itemId);
			}
		}
	}
	
	private void getReviseId1(TCComponentBOMLine parentBOMLine, String type) throws Exception {
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine  children = (TCComponentBOMLine) childrens[i].getComponent();
				if("MI".equals(type)) {
					String desc = children.getProperty(Constants.BL_DESCRIPTION);
					if(desc.contains(Constants.MI)) {
						String bomItemId = children.getItemRevision().getProperty(Constants.ITEM_ID);
						itemRevIdList.add(bomItemId);
						break;
					}
				}
				if("SMT/T".equals(type)) {
					String side = children.getProperty(Constants.BL_SIDE);
					if(side.contains(Constants.SMT_T1)) {
						String bomItemId = children.getItemRevision().getProperty(Constants.ITEM_ID);
						itemRevIdList.add(bomItemId);
						break;
					}
				}
				if("SMT/B".equals(type)) {
					String side = children.getProperty(Constants.BL_SIDE);
					if(side.contains(Constants.SMT_B1)) {
						String bomItemId = children.getItemRevision().getProperty(Constants.ITEM_ID);
						itemRevIdList.add(bomItemId);
						break;
					}
				}	
				getReviseId1(children, type);
			}
		}
	}
	
	private void itemRevReviseOperate(TCComponentBOMLine eBOMtopBOMLine, List<String> itemRevReviseId) throws Exception{
		for (int i = 0; i < itemRevReviseId.size(); i++) {
			String itemRevId = itemRevReviseId.get(i);
			getReviseBOMLine(eBOMtopBOMLine, itemRevId);
		}
	}
	
	private void getReviseBOMLine(TCComponentBOMLine parentBOMLine, String itemRevId) throws Exception {
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				TCComponentItemRevision itemRev = children.getItemRevision();
				String itemRevId1 = itemRev.getProperty(Constants.ITEM_ID);
				if(itemRevId1.equals(itemRevId)) {
					boolean isReleased = TCUtil.isReleased(itemRev);
					if(!isReleased) {
						String itemId = itemRev.getProperty("item_id");
						String revNum = itemRev.getProperty("item_revision_id");
						String workflowName = "TCM Release Process：" + itemId + "/" + revNum;
						TCUtil.createProcess("TCM Release Process", workflowName, "", new TCComponent[] {itemRev});
					}
					String versionRule = getVersionRule(itemRev); // 返回版本规则
					String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
							itemRev.getTypeObject().getName(),itemRev.getUid()); // 返回指定版本规则的版本号
					itemRev.saveAs(newRevId);
					break;
				}
				getReviseBOMLine(children, itemRevId);
			}
		}
	}
	
	private void handlerLocationVarietyData(TCComponentBOMLine eBOMtopBOMLine, Map<String, List<List<String>>> locationVarietyData) throws Exception{
		for (Entry<String, List<List<String>>> item : locationVarietyData.entrySet()) {
			String itemRevId = item.getKey();
			List<List<String>> varietyData = item.getValue();;
			setLocationVarietyData(eBOMtopBOMLine, itemRevId, varietyData);
		}
	}
	
	private void setLocationVarietyData(TCComponentBOMLine parentBOMLine, String itemRevId, List<List<String>> varietyData) throws Exception{
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine BOMLine = (TCComponentBOMLine) childrens[i].getComponent();
				String bomLineRevId = BOMLine.getItemRevision().getProperty(Constants.ITEM_ID);
				if(bomLineRevId.equals(itemRevId)) {
					String location = BOMLine.getProperty(Constants.BL_LOCATION);
					String relace1 = StringUtils.join(varietyData.get(0), ",");
					String relace2 = StringUtils.join(varietyData.get(1), ",");
					String newLocation = location.replaceAll(relace1, relace2);
					BOMLine.setProperty(Constants.BL_LOCATION, newLocation);
				}
				setLocationVarietyData(BOMLine, itemRevId, varietyData);
			}
		}
	}
	
	private void handlerRemoveData(TCComponentBOMLine eBOMtopBOMLine) throws Exception{
		if(eBOMRemoveData != null) {
			for (int i = 0; i < eBOMRemoveData.size(); i++) {
				BOMPojo bomPojo = eBOMRemoveData.get(i);
				String itemRevId = bomPojo.getBomLine().getItem().getProperty(Constants.ITEM_ID);
				getRemoveDataParent(eBOMtopBOMLine, itemRevId);
			}
		}
		
		List<String> eBOMRemoveDataId = new ArrayList<String>();
		eBOMRemoveData.forEach(map->{
			String itemId = map.getBomLineItemId();
			eBOMRemoveDataId.add(itemId);
		});
		
		if(removeDataParentIds.size() > 0) {
			for (int i = 0; i < removeDataParentIds.size(); i++) {
				String removeDataParentId = removeDataParentIds.get(i);
				TCComponentItem queryItem = queryItem(removeDataParentId);
				TCComponentItemRevision latestItemRevision = queryItem.getLatestItemRevision();
				TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topLine = TCUtil.getTopBomline(newBOMWindow, latestItemRevision);
				AIFComponentContext[] childrens = topLine.getChildren();
				for (int j = 0; j < childrens.length; j++) {
					TCComponentBOMLine children = (TCComponentBOMLine) childrens[j].getComponent();
					String itemId = children.getItem().getProperty(Constants.ITEM_ID);
					if(eBOMRemoveDataId.contains(itemId)) {
						children.cut();
					}
				}
				newBOMWindow.save();
				newBOMWindow.close();
			}
		}
	}
	
	private void getRemoveDataParent(TCComponentBOMLine eBOMtopBOMLine, String itemRevId) throws Exception {
		AIFComponentContext[] childrens = eBOMtopBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String bomItemRevId = children.getItem().getProperty(Constants.ITEM_ID);
				if(bomItemRevId.equals(itemRevId)) {
					String bomItemRevId1 = eBOMtopBOMLine.getItemRevision().getProperty(Constants.ITEM_ID);
					removeDataParentIds.add(bomItemRevId1);
				}
				getRemoveDataParent(children, itemRevId);
			}
		}
	}
	
	private void handlerAddData(TCComponentBOMLine eBOMtopBOMLine) throws Exception{
		for (Entry<String, List<BOMPojo>> item : eBOMAddData.entrySet()) {
			String itemRevId = item.getKey();
			List<BOMPojo> bomPojos = item.getValue();
			for (int i = 0; i < bomPojos.size(); i++) {
				BOMPojo bomPojo = bomPojos.get(i);
				setAddData(eBOMtopBOMLine, itemRevId, bomPojo);
			}
		}
	}
	
	private void setAddData(TCComponentBOMLine parentBOMLine, String itemRevId, BOMPojo bomPojo) throws Exception{
		String parentDesc = "";
		String desc = bomPojo.getBomLineDescription();
		String packageType = bomPojo.getBomLinePackageType();
		String side = bomPojo.getBomLineSide();
		if("DIP".equals(packageType) || desc.contains("HEATSINK")) {
			parentDesc = Constants.MI;
		}
		else if("TOP".equals(side)) {
			parentDesc = Constants.SMT_T;
		}
		else if("BOTTOM".equals(side)) {
			parentDesc = Constants.SMT_B;
		}
		
		TCComponentBOMLine bomLine = null; //包含MI || SMT_T || SMT_B 的bomLine
		AIFComponentContext[] childrens = parentBOMLine.getChildren();
		if(childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine bomLine1 = (TCComponentBOMLine) childrens[i].getComponent();
				String bomLineDesc = bomLine1.getProperty(Constants.BL_DESCRIPTION);
				if(bomLineDesc.contains(parentDesc)) {
					bomLine = bomLine1;
					break;
				}
			}
		}
		
		TCComponentBOMLine bomLine2 = null; //itemRevId 相等的 bomLine
		if(bomLine != null) {
			TCComponentItem item2 = bomLine.getItem();
			item2.refresh();
			TCComponentItemRevision latestItemRevision2 = item2.getLatestItemRevision();
			TCComponentBOMLine topBomLine = TCUtil.openBomWindow(session, latestItemRevision2);
			AIFComponentContext[] children = topBomLine.getChildren();
			for (int j = 0; j < children.length; j++) {
				TCComponentBOMLine bomLine1 = (TCComponentBOMLine) children[j].getComponent();
				String itemRev1 = bomLine1.getItemRevision().getProperty(Constants.ITEM_ID);
				if(itemRev1.equals(itemRevId)) {
					bomLine2 = bomLine1;
					break;
				}
			}
		}
		
		if(bomLine2 != null) {
			String quantity = bomLine2.getProperty(Constants.BL_QUANTITY);
			String location = bomLine2.getProperty(Constants.BL_LOCATION);
			String bomLineLocation = bomPojo.getBomLineLocation();
			quantity = String.valueOf((Integer.valueOf(quantity) + 1));
			location = location + "," + bomLineLocation;
			bomLine2.setProperty(Constants.BL_QUANTITY,quantity);
			bomLine2.setProperty(Constants.BL_LOCATION,location);
		}else {
			String itemId = bomLine.getItem().getProperty(Constants.ITEM_ID);
			TCComponentItem item = queryItem(itemId);
			TCComponentItemRevision latestItemRevision = item.getLatestItemRevision();
			TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topLine = TCUtil.getTopBomline(newBOMWindow, latestItemRevision);
			String bomLineDescription = bomPojo.getBomLineDescription();
			String bomLineLocation = bomPojo.getBomLineLocation();
			String bomLineQuantity = bomPojo.getBomLineQuantity();
			String bomLineSide = bomPojo.getBomLineSide();
			TCComponentItem item1 = queryItem(itemRevId);
			TCComponentBOMLine newBOMLine = topLine.add(item1, item1.getLatestItemRevision(), null, false);
			newBOMLine.setProperty(Constants.BL_DESCRIPTION, bomLineDescription);
			newBOMLine.setProperty(Constants.BL_LOCATION, bomLineLocation);
			newBOMLine.setProperty(Constants.BL_QUANTITY, bomLineQuantity);
			newBOMLine.setProperty(Constants.BL_SIDE, bomLineSide);
			newBOMWindow.save();
			newBOMWindow.close();
		}
		
	}
	
	//根据专案文件夹查PCA料号
	public List<TCComponentItemRevision> getPCAPartTopByProject(TCComponent projectFolder) throws Exception {
		List<TCComponentItemRevision> itemRevs = new ArrayList<TCComponentItemRevision>();
		getSelfPartFolder(projectFolder);
		if(selfPartFolder == null) {
			throw new Exception("【" + projectFolder.toString() + "】" + "专案中未找到【自編物料工作區】文件夹."); 
		}
		AIFComponentContext[] related = selfPartFolder.getRelated(Constants.CONTENTS);
		for (int i = 0; i < related.length; i++) {
			TCComponentItem item = (TCComponentItem) related[i].getComponent();
			TCComponentItemRevision itemRev = item.getLatestItemRevision();
			String desc = itemRev.getProperty(Constants.ENGLISH_DESC).toUpperCase();
			String materialGroup = itemRev.getProperty("d9_MaterialGroup").toUpperCase();
			if(!desc.contains(Constants.MI) 
					&& !desc.contains(Constants.SMT) 
					&& !desc.contains(Constants.SMT_T)
					&& !desc.contains(Constants.SMT_B)
					&& !desc.contains(Constants.MVA)
					&& !desc.contains(Constants.AI) 
					&& !desc.contains(Constants.AI_A)
					&& !desc.contains(Constants.AI_R)
					&& materialGroup.contains("B8X80")) {
				itemRevs.add(itemRev);
			}
		}
		return itemRevs;
	}
	
	//根据专案文件夹查PCA料号
	public TCComponentItemRevision getPCAPartTopByPartFolder(TCComponent partFolder) throws Exception {
		TCComponentItemRevision pcaitemRev = null;
		AIFComponentContext[] related = partFolder.getRelated(Constants.CONTENTS);
		if(related.length == 0) {
			throw new Exception("物料文件夹下未查找到任何物料！\n");
		}
		if(productLine.equals(Constants.MONITOR)) {
			for (int i = 0; i < related.length; i++) {
				TCComponentItem item = (TCComponentItem) related[i].getComponent();
				TCComponentItemRevision itemRev = item.getLatestItemRevision();
				String desc = itemRev.getProperty(Constants.ENGLISH_DESC).toUpperCase();
				String materialGroup = itemRev.getProperty("d9_MaterialGroup").toUpperCase();
				if(!desc.contains(Constants.MI) 
						&& !desc.contains(Constants.SMT) 
						&& !desc.contains(Constants.SMT_T)
						&& !desc.contains(Constants.SMT_B)
						&& !desc.contains(Constants.MVA)
						&& !desc.contains(Constants.AI) 
						&& !desc.contains(Constants.AI_A)
						&& !desc.contains(Constants.AI_R)
						&& materialGroup.contains("B8X80")) {
					pcaitemRev = itemRev;
					break;
				}
			}
		}else {
			TCComponentItem item = (TCComponentItem) related[0].getComponent();
			pcaitemRev = item.getLatestItemRevision();
		}
		return pcaitemRev;
	}
		
	//获取自编料号文件夹
	public void getSelfPartFolder(TCComponent projectFolder) throws Exception {
		AIFComponentContext[] childrenFolder = projectFolder.getRelated(Constants.CONTENTS);
		if(childrenFolder.length > 0) {
			for (int i = 0; i < childrenFolder.length; i++) {
				TCComponent component = (TCComponent) childrenFolder[i].getComponent();
				String name = component.getProperty(Constants.OBJECT_NAME);
				if(name.equals("自編物料協同工作區")) {
					selfPartFolder1 = (TCComponentFolder)component;
				}
				getSelfPartFolder(component);
			}
		}
	}
	
	private void validationData() throws Exception{
		setValidationButton(false);
		allBOMLineUnpackage(designTopBOMLine);
		if(productLine.equals(Constants.MONITOR)) {
			getEDAComPart(designTopBOMLine);
			if(bomPojos3.size() > 0) {
				List<BOMPojo> collect = bomPojos3.stream().filter(e -> !"DIP".equals(e.getBomLinePackageType()) && "".equals(e.getBomLineSide()))
					.collect(Collectors.toList());
				if(collect.size() > 0) {
					for (int i = 0; i < collect.size(); i++) {
						BOMPojo bomPojo = collect.get(i);
						String itemId = bomPojo.getBomLineItemId();
						writeLogText("当前版本：" + itemId + "，PackageType或 Side不可为空值！\n");
					}
					return;
				}
			}
		}
		
		getBOMPojo1(designTopBOMLine);
		TCComponentItemRevision itemRev = designTopBOMLine.getItemRevision();
		String itemRevNumber = itemRev.getProperty(Constants.ITEM_REV_ID);
		if(!"01".equals(itemRevNumber)) {
			Integer upItemRevNumber = Integer.valueOf(itemRevNumber) - 1;
			String upItemRevId = "";
			if(upItemRevNumber <= 9) {
				upItemRevId = "0" + String.valueOf(upItemRevNumber);
			}else {
				upItemRevId = String.valueOf(upItemRevNumber);
			}
			TCComponentItemRevision upItemRev = itemRev.findRevision(upItemRevId);
			TCComponentBOMLine topBOMLine2 = TCUtil.openBomWindow(session, upItemRev);
			allBOMLineUnpackage(topBOMLine2);
			getBOMPojo2(topBOMLine2);
			
			writeLogText("获取当前版本信息和上个版本信息中...\n");
			Map<String, List<BOMPojo>> bomLineGroupById1 = bomLineGroupById(bomPojos1);
			Map<String, List<BOMPojo>> bomLineGroupById2 = bomLineGroupById(bomPojos2);
			writeLogText("开始比较两个版本的差异...\n");
			designBOMVarietyData = getDesignBOMVarietyData(bomLineGroupById1, bomLineGroupById2);//BOMLine 增减比较
			if(designBOMVarietyData.size() != 0) {
				List<BOMPojo> addData = designBOMVarietyData.get("A");
				for (int i = 0; i < addData.size(); i++) {
					BOMPojo bomPojo = addData.get(i);
					String itemId = bomPojo.getBomLineItemId();
					String location = bomPojo.getBomLineLocation();
					writeLogText("当前版本比上个版本添加了：" + itemId + "，location：" + location + "\n");
				}
				
				List<BOMPojo> removeData = designBOMVarietyData.get("R");
				for (int i = 0; i < removeData.size(); i++) {
					BOMPojo bomPojo = removeData.get(i);
					String itemId = bomPojo.getBomLineItemId();
					String location = bomPojo.getBomLineLocation();
					writeLogText("当前版本比上个版本移除了：" + itemId + "，location：" + location + "\n");
				}
			}
			
			designBOMVarietyData1 = getDesignBOMVarietyData1();//bl_occ_d9_BOM
			if(designBOMVarietyData1.size() != 0) {
				for (int i = 0; i < designBOMVarietyData1.size(); i++) {
					BOMPojo bomPojo = designBOMVarietyData1.get(i);
					String itemId = bomPojo.getBomLineItemId();
					String location = bomPojo.getBomLineLocation();
					String bomLineBOM = bomPojo.getBomLineBOM();
					writeLogText("当前版本比上个版本：" + itemId + "，location：" + location + "，BOM："+ bomLineBOM + "\n");
				}
			}
			if(designBOMVarietyData.size() != 0 || designBOMVarietyData1.size() != 0) {
				writeLogText("数据校验成功！\n");
				setConvertEBOMButton(true);
			}else {
				writeLogText("当前版本BOM数据与上个版本BOM数据没有变化，  数据校验失败...程序结束！");
			}
		}else {
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					PCAPartNumber = PCAPartNumberText.getText();
					if("".equals(PCAPartNumber)) {
						writeLogText("PCA料号不可为空值！");
						return;
					}
				}
			});

			TCComponentItem queryItem = queryItem(PCAPartNumber);
			pcaPartItemRev = queryItem.getLatestItemRevision();
			TCComponentBOMLine pcaTopBomLine = TCUtil.openBomWindow(session, pcaPartItemRev);
			AIFComponentContext[] pcaTopBomLineChildrens = pcaTopBomLine.getChildren();
			if (pcaTopBomLineChildrens.length > 0) {
				writeLogText("当前PCA料号已搭建过BOM，请升版原理图！");
				return;
			}
			
			writeLogText("当前BOM只有一个版本，数据校验成功！\n");
			setConvertEBOMButton(true);
		}
	}
	
	private void getEDAComPart(TCComponentBOMLine topBomLine) throws Exception{
		AIFComponentContext[] children = topBomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			String itemType = bomLine.getItemRevision().getType();
			if("EDAComPart Revision".equals(itemType)) {
				BOMPojo bomPojo = convertBOMPojo(bomLine, false, true);
				bomPojos3.add(bomPojo);
			}
			getBOMPojo1(bomLine);
		}
	}
	
	private List<BOMPojo> getDesignBOMVarietyData1(){
		List<BOMPojo> collect = bomPojos1.stream().filter(e -> e.getBomLineBOM().equals("NI")).collect(Collectors.toList());
		List<BOMPojo> bomPojos = new ArrayList<BOMPojo>();
		for (int i = 0; i < collect.size(); i++) {
			BOMPojo bomPojo = collect.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			String bomLineLocation = bomPojo.getBomLineLocation();
			for (int j = 0; j < bomPojos2.size(); j++) {
				BOMPojo bomPojo2 = bomPojos2.get(j);
				String bomLineItemId2 = bomPojo2.getBomLineItemId();
				String bomLineLocation2 = bomPojo2.getBomLineLocation();
				if(bomLineItemId2.equals(bomLineItemId) && bomLineLocation2.equals(bomLineLocation)) {
					String bomLineBOM = bomPojo.getBomLineBOM();
					String bomLineBOM2 = bomPojo2.getBomLineBOM();
					if(!bomLineBOM2.equals(bomLineBOM)) {
						bomPojos.add(bomPojo);
					}
				}
			}
		}
		return bomPojos;
	}
	
	private Map<String, List<BOMPojo>> bomLineGroupById(List<BOMPojo> bomPojos){
		return bomPojos.stream().collect(Collectors.groupingBy(BOMPojo::getBomLineItemId));
	}
	
	private Map<String, List<BOMPojo>> getDesignBOMVarietyData(Map<String, List<BOMPojo>> bomPojos1Map, Map<String, List<BOMPojo>> bomPojos2Map){
		Map<String, List<BOMPojo>> addOrRemoveData = new HashMap<String, List<BOMPojo>>();
		List<BOMPojo> addData = new ArrayList<BOMPojo>();
		List<BOMPojo> removeData = new ArrayList<BOMPojo>();
		for (Entry<String, List<BOMPojo>> bomPojo1 : bomPojos1Map.entrySet()) {
			String key1 = bomPojo1.getKey();
			List<BOMPojo> value1 = bomPojo1.getValue();
			if(bomPojos2Map.containsKey(key1)) {
				List<BOMPojo> value2 = bomPojos2Map.get(key1);
				if(value1.size() > value2.size()) {
					List<BOMPojo> collect = value1.stream().filter(item -> !value2.stream()
							.map(e -> e.getBomLineLocation())
							.collect(Collectors.toList())
							.contains(item.getBomLineLocation()))
							.collect(Collectors.toList());
					for (int i = 0; i < collect.size(); i++) {
						addData.add(collect.get(i));
					}
				}
				if(value1.size() < value2.size()) {
					List<BOMPojo> collect = value2.stream().filter(item -> !value1.stream()
							.map(e -> e.getBomLineLocation())
							.collect(Collectors.toList())
							.contains(item.getBomLineLocation()))
							.collect(Collectors.toList());
					for (int i = 0; i < collect.size(); i++) {
						removeData.add(collect.get(i));
					}
				}
				if(value1.size() == value2.size()) {
					List<BOMPojo> collect1 = value2.stream().filter(item -> !value1.stream()
							.map(e -> e.getBomLineLocation())
							.collect(Collectors.toList())
							.contains(item.getBomLineLocation()))
							.collect(Collectors.toList());
					List<BOMPojo> collect2 = value1.stream().filter(item -> !value2.stream()
							.map(e -> e.getBomLineLocation())
							.collect(Collectors.toList())
							.contains(item.getBomLineLocation()))
							.collect(Collectors.toList());
					for (int i = 0; i < collect1.size(); i++) {
						removeData.add(collect1.get(i));
					}
					for (int i = 0; i < collect2.size(); i++) {
						addData.add(collect2.get(i));
					}
				}
			}else {
				for (int i = 0; i < value1.size(); i++) {
					addData.add(value1.get(i));
				}
			}
		}
		
		for (Entry<String, List<BOMPojo>> bomPojo2 : bomPojos2Map.entrySet()) {
			String key1 = bomPojo2.getKey();
			List<BOMPojo> value1 = bomPojo2.getValue();
			if(bomPojos1Map.containsKey(key1)) {
				List<BOMPojo> value2 = bomPojos1Map.get(key1);
				if(value1.size() > value2.size()) {
					List<BOMPojo> collect = value1.stream().filter(item -> !value2.stream()
							.map(e -> e.getBomLineLocation())
							.collect(Collectors.toList())
							.contains(item.getBomLineLocation()))
							.collect(Collectors.toList());
					for (int i = 0; i < collect.size(); i++) {
						removeData.add(collect.get(i));
					}
				}
//				if(value1.size() < value2.size()) {
//					List<BOMPojo> collect = value2.stream().filter(item -> !value1.stream()
//							.map(e -> e.getBomLineLocation())
//							.collect(Collectors.toList())
//							.contains(item.getBomLineLocation()))
//							.collect(Collectors.toList());
//					for (int i = 0; i < collect.size(); i++) {
//						addData.add(collect.get(i));
//					}
//				}
			}else {
				for (int i = 0; i < value1.size(); i++) {
					removeData.add(value1.get(i));
				}
			}
		}
		
		addOrRemoveData.put("A", addData);
		addOrRemoveData.put("R", removeData);
		return addOrRemoveData;
//		Map<String, List<BOMPojo>> addOrRemoveData = new HashMap<String, List<BOMPojo>>();
//		List<BOMPojo> addData = null;
//		List<BOMPojo> removeData = null;
//		for (Entry<String, List<BOMPojo>> bomPojo1 : bomPojos1Map.entrySet()) {
//			String key1 = bomPojo1.getKey();
//			List<BOMPojo> value1 = bomPojo1.getValue();
//			if(bomPojos2Map.containsKey(key1)) {
//				List<BOMPojo> value2 = bomPojos2Map.get(key1);
//				if(value1.size() < value2.size()) {
//					List<BOMPojo> list = value2.stream().filter(item -> !value1.stream()
//							.map(e -> e.getBomLineLocation())
//							.collect(Collectors.toList())
//							.contains(item.getBomLineLocation()))
//							.collect(Collectors.toList());
//					System.out.println("当前版本移除了：" + list);
//					if(removeData == null) {
//						removeData = list;
//					}else {
//						removeData.addAll(list);
//					}
//				}
//				if(value1.size() > value2.size()) {
//					System.out.println("..");
//					List<BOMPojo> list = value1.stream().filter(item -> !value2.stream()
//							.map(e -> e.getBomLineLocation())
//							.collect(Collectors.toList())
//							.contains(item.getBomLineLocation()))
//							.collect(Collectors.toList());
//					System.out.println("当前版本增加了：" + list);
//					if(addData == null) {
//						addData = value1;
//					}else {
//						removeData.addAll(value1);
//					}
//				}
//			}
//			if(!bomPojos2Map.containsKey(key1)) {
//				if(addData == null) {
//					addData = value1;
//				}else {
//					removeData.addAll(value1);
//				}
//			}
//		}
//		if(removeData != null) {
//			addOrRemoveData.put("r", removeData);
//		}
//		if(addData != null) {
//			addOrRemoveData.put("a", addD;ata);
//		}
//		return addOrRemoveData;
	}

	/**
	 * 解包全部BOMLine
	 * @param designBOMTopLine
	 * @throws Exception
	 */
	private void allBOMLineUnpackage(TCComponentBOMLine designBOMTopLine) throws Exception{
		AIFComponentContext[] children = designBOMTopLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			if(bomLine.isPacked()) {
				bomLine.unpack();
			}
			allBOMLineUnpackage(bomLine);
		}
	}
	
	private void getDesignBOMPojo(BOMPojo topBomPojo, TCComponentBOMLine topBomLine) throws Exception{
		AIFComponentContext[] children = topBomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			BOMPojo bomPojoChild = convertBOMPojo(bomLine, false, true);
			List<BOMPojo> child = topBomPojo.getChild();
			child.add(bomPojoChild);
			getDesignBOMPojo(bomPojoChild,bomLine);
		}
	}
	
	private void getBOMPojo1(TCComponentBOMLine topBomLine) throws Exception{
		AIFComponentContext[] children = topBomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			BOMPojo bomPojo = convertBOMPojo(bomLine, false, true);
			bomPojos1.add(bomPojo);
			getBOMPojo1(bomLine);
		}
	}
	
	private void getBOMPojo2(TCComponentBOMLine topBomLine) throws Exception{
		AIFComponentContext[] children = topBomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			BOMPojo bomPojo = convertBOMPojo(bomLine, false, true);
			bomPojos2.add(bomPojo);
			getBOMPojo2(bomLine);
		}
	}
	
	private BOMPojo convertBOMPojo(TCComponentBOMLine bomLine, boolean isTop, boolean isCheck) throws Exception{
		BOMPojo bomPojo = new BOMPojo();
		String itemId = bomLine.getProperty(Constants.BL_ITEM_ID).toUpperCase();
		bomPojo.setBomLineItemId(itemId);
		bomPojo.setBomLineDescription(bomLine.getProperty(Constants.BL_REV_DESCRIPTION).toUpperCase());
		String packageType = bomLine.getProperty(Constants.BL_PACKAGE_TYPE).toUpperCase();
		if(productLine.equals(Constants.MONITOR)) {
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			String itemType = itemRevision.getType();
			if(!itemType.equals("D9_PCB_PartRevision")) {
				if(!isTop && "".equals(packageType) && isCheck) {
					throw new Exception(   "【" + itemId + "】PackageType或 Side不可为空值！");
				}
			}
		}
		if(productLine.equals(Constants.PRINTER)) {
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			String itemType = itemRevision.getType();
			if (!isTop && !itemType.equals("D9_PCB_PartRevision") && isCheck) {
				if("".equals(packageType)) {
					throw new Exception("【" + itemId + "】PackageType或 Side不可为空值！");
				}
			}
		}
		bomPojo.setBomLinePackageType(bomLine.getProperty(Constants.BL_PACKAGE_TYPE).toUpperCase());
		String side = bomLine.getProperty(Constants.BL_SIDE).toUpperCase();
		bomPojo.setBomLineSide(side);
		bomPojo.setBomLineQuantity(bomLine.getProperty(Constants.BL_QUANTITY).toUpperCase());
		String location = bomLine.getProperty(Constants.BL_REF_DESIGNATOR).toUpperCase();
		if(productLine.equals(Constants.MONITOR)) {
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			String itemType = itemRevision.getType();
			if(!itemType.equals("D9_PCB_PartRevision")) {
				if(!isTop && isCheck) {
					if("".equals(location)) {
						throw new Exception("【" + itemId + "】引用指示符不可为空值！\n");
					}
				}
			}
		}
		if(productLine.equals(Constants.PRINTER)) {
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			String itemType = itemRevision.getType();
			if (!isTop && !itemType.equals("D9_PCB_PartRevision") && isCheck) {
				if("".equals(location)) {
					throw new Exception("【" + itemId + "】引用指示符不可为空值！\n");
				}
			}
		}
		if(!isCheck) {
			location = bomLine.getProperty(Constants.BL_LOCATION).toUpperCase();
		}
		bomPojo.setBomLineLocation(location);
		bomPojo.setBomLine(bomLine);
		String bom = bomLine.getProperty(Constants.BL_BOM).toUpperCase();
		bomPojo.setBomLineBOM(bom);
		return bomPojo;
	}
	
	private void buildBOMStructure(TCComponentBOMLine parentBOMLine, BOMPojo bomPojo) throws Exception{
		List<BOMPojo> child = bomPojo.getChild();
		for (int i = 0; i < child.size(); i++) {
			BOMPojo bomPojo2 = child.get(i);
			String itemId = bomPojo2.getBomLineItemId();
			TCComponentItem item = queryItem(itemId);
			TCComponentItemRevision itemRev = item.getLatestItemRevision();
			
			List<BOMPojo> child2 = bomPojo2.getChild();
			if(child2.size() > 0) {
				boolean isReleased = TCUtil.isReleased(itemRev);
				if(isReleased) {
					String versionRule = getVersionRule(itemRev); // 返回版本规则
					String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
							itemRev.getTypeObject().getName(),itemRev.getUid()); // 返回指定版本规则的版本号
					itemRev = itemRev.saveAs(newRevId);
				}
			}
			
			TCComponentBOMLine bomLine = parentBOMLine.add(item, itemRev, null, false);
			
			String bomLineDescription = bomPojo2.getBomLineDescription();
			String bomLinePackageType = bomPojo2.getBomLinePackageType();
			String bomLineQuantity = bomPojo2.getBomLineQuantity();
			String bomLineSide = bomPojo2.getBomLineSide();
			String bomLineLocation = bomPojo2.getBomLineLocation();
			
			bomLine.setProperty(Constants.BL_DESCRIPTION, bomLineDescription);
			bomLine.setProperty(Constants.BL_PACKAGE_TYPE, bomLinePackageType);
			bomLine.setProperty(Constants.BL_QUANTITY, bomLineQuantity);
			bomLine.setProperty(Constants.BL_SIDE, bomLineSide);
			bomLine.setProperty(Constants.BL_LOCATION, bomLineLocation);
			
			String itemRevType = itemRev.getType();
			if("D9_PCB_PartRevision".equals(itemRevType)) {
				List<BOMPojo> substitute = bomPojo2.getSubstitute();
				substitute = substitute.stream().collect(Collectors.collectingAndThen(Collectors
						.toCollection(() -> new TreeSet<>(Comparator.comparing(BOMPojo::getBomLineItemId))), ArrayList::new));
				for (int j = 0; j < substitute.size(); j++) {
					BOMPojo bomPojo3 = substitute.get(j);
					String bomLineItemId = bomPojo3.getBomLineItemId();
					System.out.println("bomLineItemId：" + bomLineItemId);
					TCComponentItem item2 = queryItem(bomLineItemId);
					String property = bomLine.getItem().getProperty("object_name");
					System.out.println("bomLine:" + property);
					bomLine.add(item2, item2.getLatestItemRevision(), null, true);
				}
			}
			
			buildBOMStructure(bomLine,bomPojo2);
		}
	}
	
	private void buildBOMStructure(TCComponentBOMLine parentBOMLine, BOMPojo bomPojo, String productLine) throws Exception{
		List<BOMPojo> child = bomPojo.getChild();
		
		if (productLine.equals(Constants.PRINTER)) {
			child = rejustBOM(child);
		}
				
		for (int i = 0; i < child.size(); i++) {
			BOMPojo bomPojo2 = child.get(i);
			String itemId = bomPojo2.getBomLineItemId();
			TCComponentItem item = queryItem(itemId);
			TCComponentItemRevision itemRev = item.getLatestItemRevision();
			List<BOMPojo> child2 = bomPojo2.getChild();
			if(child2.size() > 0) {
				boolean isReleased = TCUtil.isReleased(itemRev);
				if(isReleased) {
					String versionRule = getVersionRule(itemRev); // 返回版本规则
					String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
							itemRev.getTypeObject().getName(),itemRev.getUid()); // 返回指定版本规则的版本号
					itemRev = itemRev.saveAs(newRevId);
				}
			}
			
			TCComponentBOMLine bomLine = parentBOMLine.add(item, itemRev, null, false);
			
			String bomLineDescription = bomPojo2.getBomLineDescription();
			String bomLinePackageType = bomPojo2.getBomLinePackageType();
			String bomLineQuantity = bomPojo2.getBomLineQuantity();
			String bomLineSide = bomPojo2.getBomLineSide();
			String bomLineLocation = bomPojo2.getBomLineLocation();
			
			bomLine.setProperty(Constants.BL_DESCRIPTION, bomLineDescription);
			bomLine.setProperty(Constants.BL_PACKAGE_TYPE, bomLinePackageType);
			bomLine.setProperty(Constants.BL_QUANTITY, bomLineQuantity);
			bomLine.setProperty(Constants.BL_SIDE, bomLineSide);
			bomLine.setProperty(Constants.BL_LOCATION, bomLineLocation);
			
			String itemRevType = itemRev.getType();
			if("D9_PCB_PartRevision".equals(itemRevType)) {
				List<BOMPojo> substitute = bomPojo2.getSubstitute();
				for (int j = 0; j < substitute.size(); j++) {
					BOMPojo bomPojo3 = substitute.get(j);
					String bomLineItemId = bomPojo3.getBomLineItemId();
					TCComponentItem item2 = queryItem(bomLineItemId);
					bomLine.add(item2, item2.getLatestItemRevision(), null, true);
				}
			}
			
			buildBOMStructure(bomLine,bomPojo2);
		}
	}
	
	// 去重并合并Location
	private List<BOMPojo> rejustBOM(List<BOMPojo> child) {
		List<BOMPojo> retLst = new ArrayList<BOMPojo>();
		
		try {			
			Map<String, List<BOMPojo>> bomMap = new HashMap<String, List<BOMPojo>>();
			for (BOMPojo bomPojo : child) {
				String bomItemId = bomPojo.getBomLineItemId();
				List<BOMPojo> bomLst = null;
				if (bomMap.containsKey(bomItemId)) {
					bomLst = bomMap.get(bomItemId);					
				} else {
					bomLst = new ArrayList<BOMPojo>();					
				}
				bomLst.add(bomPojo);
				bomMap.put(bomItemId, bomLst);
			}
			
			bomMap.forEach((key, value) -> {
				List<BOMPojo> bomLst = value;
				if (1 == bomLst.size()) {
					retLst.add(bomLst.get(0));
				} else if (bomLst.size() > 1) {
					BOMPojo bom = bomLst.stream().findFirst().get();
					String joinLocation = bomLst.stream().sorted(Comparator.comparing(BOMPojo::getBomLineLocation, Comparator.nullsLast(String::compareTo))).map(BOMPojo::getBomLineLocation).collect(Collectors.joining(","));
					bom.setBomLineLocation(joinLocation);
					bom.setBomLineQuantity(String.valueOf(bomLst.size()));
					retLst.add(bom);
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retLst;
	}
	
	private TCComponentItem queryItem(String itemId) throws Exception{
		TCComponent[] executeQuery = TCUtil.executeQuery(session, Constants.FIND_ITEM, new String[] {Constants.ITEM_ID}, new String[] {itemId});
		if(executeQuery.length == 0) {
			throw new Exception("未找到Item：" + itemId + "\n");
		}
		return (TCComponentItem) executeQuery[0];
	}

	/**
	 * 根据id去重，合并Location
	 * @throws Exception 
	 */
//	private List<BOMPojo> distinctMergeLocationById(List<BOMPojo> bomPojos) throws Exception{
//		Map<String, List<String>> itemIdMap = new HashMap<String, List<String>>();
//		Set<String> repeatId = getRepeatId(bomPojos);
//		
//		//获取id相同的Location
//		for (int i = 0; i < bomPojos.size(); i++) {
//			BOMPojo bomPojo = bomPojos.get(i);
//			String itemId = bomPojo.getBomLineItemId();
//			String location = bomPojo.getBomLineLocation();
//			if(repeatId.contains(itemId)) {
//				boolean containsKey = itemIdMap.containsKey(itemId);
//				if(containsKey) {
//					List<String> locations = itemIdMap.get(itemId);
//					locations.add(location);
//					itemIdMap.put(itemId, locations);
//				}else {
//					List<String> locations = new ArrayList<String>();
//					locations.add(location);
//					itemIdMap.put(itemId, locations);
//				}
//			}
//		}
//		
//		//根据id去重
//		bomPojos = bomPojos.stream().collect(
//				Collectors.collectingAndThen(
//				Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BOMPojo::getBomLineItemId))), ArrayList::new));
//		
//		//赋值Quantity,Location
//		for (int i = 0; i < bomPojos.size(); i++) {
//			BOMPojo bomPojo = bomPojos.get(i);
//			String itemId = bomPojo.getBomLineItemId();
//			if(itemIdMap.containsKey(itemId)) {
//				List<String> locations = itemIdMap.get(itemId);
//				locations = locations.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
//				bomPojo.setBomLineQuantity(String.valueOf(locations.size()));
//				bomPojo.setBomLineLocation(String.join(",", locations));
//			}
//		}
//		return bomPojos;
//	}
	
	/**
	 * 获取重复id
	 * @return
	 * @throws Exception
	 */
//	private Set<String> getRepeatId(List<BOMPojo> bomPojos) throws Exception{
//		Set<String> set = new HashSet<>();
//		Set<String> exist = new HashSet<>();
//		for (BOMPojo bomPojo:bomPojos) {
//			String itemId = bomPojo.getBomLineItemId();
//			if(set.contains(itemId)) {
//				exist.add(itemId);
//			}else {
//				set.add(itemId);
//			}
//		}
//		return exist;
//	}
	
	private ItemRevInfo convertItemRevPojo(TCComponentItemRevision itemRev) throws Exception{
		String id = itemRev.getProperty("item_id");
		String name = itemRev.getProperty(Constants.OBJECT_NAME);
		String desc = itemRev.getProperty(Constants.ENGLISH_DESC);
		ItemRevInfo itemRevInfo = new ItemRevInfo();
		itemRevInfo.setId(id);
		itemRevInfo.setName(name);
		itemRevInfo.setDesc(desc);
		itemRevInfo.setItemRev(itemRev);
		return itemRevInfo;
	}
	
	//判断PAC是否需要升版
	private void isRevisePCAPart() throws Exception {
		boolean isRevise = false;
		writeLogText("查询【" + pcaPartItemRev.toString() +  "】是否发布...\n");
		boolean isReleased = TCUtil.isReleased(pcaPartItemRev);
		if(!isReleased) {
			writeLogText("【" + pcaPartItemRev.toString() + "】未发布，开始判断子阶是否有变化...\n");
			
			//获取当前所有子
			List<ItemRevInfo> currentPACItemRevList = new ArrayList<ItemRevInfo>();
			TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine PCATopBOMLine = TCUtil.getTopBomline(newBOMWindow, pcaPartItemRev);
			AIFComponentContext[] PCATopBOMLineChildrens = PCATopBOMLine.getChildren();
			for (int i = 0; i < PCATopBOMLineChildrens.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) PCATopBOMLineChildrens[i].getComponent();
				TCComponentItemRevision itemRev = bomLine.getItem().getLatestItemRevision();
				ItemRevInfo itemRevInfo = convertItemRevPojo(itemRev);
				currentPACItemRevList.add(itemRevInfo);
			}
			newBOMWindow.save();
			newBOMWindow.close();
			
			//获取现有所有自编料号
			List<ItemRevInfo> selfPartItemRevList = new ArrayList<ItemRevInfo>();
			AIFComponentContext[] related = selfPartFolder.getRelated(Constants.CONTENTS);
			for (int i = 0; i < related.length; i++) {
				TCComponentItem item = (TCComponentItem) related[i].getComponent();
				TCComponentItemRevision itemRev = item.getLatestItemRevision();
				String itemRevDesc = itemRev.getProperty(Constants.ENGLISH_DESC);
				if(itemRevDesc.contains(Constants.MI) 
						|| itemRevDesc.contains(Constants.SMT1) ||itemRevDesc.contains(Constants.SMT_T1) 
						|| itemRevDesc.contains(Constants.SMT_B1)
						|| itemRevDesc.contains(Constants.MVA) || itemRevDesc.contains(Constants.ASSY)
						|| itemRevDesc.contains(Constants.PCB)) {
					ItemRevInfo itemRevInfo = convertItemRevPojo(itemRev);
					selfPartItemRevList.add(itemRevInfo);
				}
			}
			
			pacChildrenAddDataList = selfPartItemRevList.stream().filter(item -> !currentPACItemRevList.stream()
					.map(e -> e.getId())
					.collect(Collectors.toList())
					.contains(item.getId()))
					.collect(Collectors.toList());
			
			pacChildrenRemoveDataList = currentPACItemRevList.stream().filter(item -> !selfPartItemRevList.stream()
					.map(e -> e.getId())
					.collect(Collectors.toList())
					.contains(item.getId()))
					.collect(Collectors.toList());
			
			if(pacChildrenAddDataList.size() > 0 || pacChildrenRemoveDataList.size() > 0) {
				writeLogText("【" + pcaPartItemRev.toString() + "】未发布，子阶发生变化...\n");
				isRevise = true;
			}
		}else {
			writeLogText("【" + pcaPartItemRev.toString() + "】已发布...\n");
			isRevise = true;
		}
		if(isRevise) {
			if(!isReleased) {
				writeLogText("【" + pcaPartItemRev.toString() + "】发起发布流程...\n");
				String itemId = pcaPartItemRev.getProperty(Constants.ITEM_ID);
				String revNum = pcaPartItemRev.getProperty(Constants.ITEM_REV_ID);
				String workflowName = "TCM Release Process：" + itemId + "/" + revNum;
				TCUtil.createProcess("TCM Release Process", workflowName, "", new TCComponent[] {pcaPartItemRev});
			}
			writeLogText("【" + pcaPartItemRev.toString() + "】开始升版...\n");
			String versionRule = getVersionRule(pcaPartItemRev); // 返回版本规则
			String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
					pcaPartItemRev.getTypeObject().getName(),pcaPartItemRev.getUid()); // 返回指定版本规则的版本号
			pcaPartItemRev = pcaPartItemRev.saveAs(newRevId);
		}
	}
	
	//判断PAC是否需要升版
	private void isRevisePCAPart1() throws Exception {
//		boolean isRevise = false;
		writeLogText("查询【" + pcaPartItemRev.toString() +  "】是否发布...\n");
		boolean isReleased = TCUtil.isReleased(pcaPartItemRev);
		if(!isReleased) {
			writeLogText("【" + pcaPartItemRev.toString() + "】未发布，开始判断子阶是否有变化...\n");
			//获取当前所有子
			List<ItemRevInfo> currentPACItemRevList = new ArrayList<ItemRevInfo>();
			TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine PCATopBOMLine = TCUtil.getTopBomline(newBOMWindow, pcaPartItemRev);
			AIFComponentContext[] PCATopBOMLineChildrens = PCATopBOMLine.getChildren();
			for (int i = 0; i < PCATopBOMLineChildrens.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) PCATopBOMLineChildrens[i].getComponent();
				TCComponentItemRevision itemRev = bomLine.getItem().getLatestItemRevision();
				ItemRevInfo itemRevInfo = convertItemRevPojo(itemRev);
				currentPACItemRevList.add(itemRevInfo);
			}
			newBOMWindow.save();
			newBOMWindow.close();
			
			//获取现有所有自编料号
			List<ItemRevInfo> selfPartItemRevList = new ArrayList<ItemRevInfo>();
			AIFComponentContext[] related = selfPartFolder.getRelated(Constants.CONTENTS);
			for (int i = 0; i < related.length; i++) {
				TCComponentItem item = (TCComponentItem) related[i].getComponent();
				TCComponentItemRevision itemRev = item.getLatestItemRevision();
				String itemRevDesc = itemRev.getProperty(Constants.ENGLISH_DESC);
				if(itemRevDesc.contains(Constants.MI) 
						|| itemRevDesc.contains(Constants.SMT1) ||itemRevDesc.contains(Constants.SMT_T1) 
						|| itemRevDesc.contains(Constants.SMT_B1)
						|| itemRevDesc.contains(Constants.MVA) || itemRevDesc.contains(Constants.ASSY)
						|| itemRevDesc.contains(Constants.PCB)) {
					ItemRevInfo itemRevInfo = convertItemRevPojo(itemRev);
					selfPartItemRevList.add(itemRevInfo);
				}
			}
			
			pacChildrenAddDataList = selfPartItemRevList.stream().filter(item -> !currentPACItemRevList.stream()
					.map(e -> e.getId())
					.collect(Collectors.toList())
					.contains(item.getId()))
					.collect(Collectors.toList());
			
			pacChildrenRemoveDataList = currentPACItemRevList.stream().filter(item -> !selfPartItemRevList.stream()
					.map(e -> e.getId())
					.collect(Collectors.toList())
					.contains(item.getId()))
					.collect(Collectors.toList());
			
			if(pacChildrenAddDataList.size() > 0 || pacChildrenRemoveDataList.size() > 0) {
//				writeLogText("【" + pcaPartItemRev.toString() + "】未发布，子阶发生变化...\n");
				throw new Exception("【" + pcaPartItemRev.toString() + "】 子阶发生变化，请发布并升版！\n");
//				isRevise = true;
			}
		}else {
			writeLogText("【" + pcaPartItemRev.toString() + "】已发布...\n");
			throw new Exception("【" + pcaPartItemRev.toString() + "】已发布，请升版后再操作！\n");
//			isRevise = true;
		}
//		if(isRevise) {
//			if(!isReleased) {
//				writeLogText("【" + pcaPartItemRev.toString() + "】发起发布流程...\n");
//				String itemId = pcaPartItemRev.getProperty(Constants.ITEM_ID);
//				String revNum = pcaPartItemRev.getProperty(Constants.ITEM_REV_ID);
//				String workflowName = "TCM Release Process：" + itemId + "/" + revNum;
//				TCUtil.createProcess("TCM Release Process", workflowName, "", new TCComponent[] {pcaPartItemRev});
//			}
//			writeLogText("【" + pcaPartItemRev.toString() + "】开始升版...\n");
//			String versionRule = getVersionRule(pcaPartItemRev); // 返回版本规则
//			String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, 
//					pcaPartItemRev.getTypeObject().getName(),pcaPartItemRev.getUid()); // 返回指定版本规则的版本号
//			pcaPartItemRev = pcaPartItemRev.saveAs(newRevId);
//		}
	}
	
	public static void writeLogText(String message){
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				logText.append(message);
			}
		});
    }
	
	private void setValidationButton(boolean isEnabled){
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				validationButton.setEnabled(isEnabled);
			}
		});
    }
	
	private  void setConvertEBOMButton(boolean isEnabled){
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				convertEBOMButton.setEnabled(isEnabled);
			}
		});
    }
	
	/**
	 * 返回升版规则
	 * 
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	private String getVersionRule(TCComponentItemRevision itemRev) throws TCException {
		String version = itemRev.getProperty("item_revision_id");
		String versionRule = "";
		if (version.matches("[0-9]+")) { // 判断对象版本是否为数字版
			versionRule = "NN";
		} else if (version.matches("[a-zA-Z]+")) { // 判断对象版本是否为字母版
			versionRule = "@";
		}
		return versionRule;
	}

}


