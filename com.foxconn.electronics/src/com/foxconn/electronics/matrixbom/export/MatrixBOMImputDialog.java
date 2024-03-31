package com.foxconn.electronics.matrixbom.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.service.ProductLineBOMService;
import com.foxconn.electronics.matrixbom.service.VariablesBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;


public class MatrixBOMImputDialog extends Dialog {
	private static TCSession session = null;
	
	private Shell shell = null;
	private Shell parentShell = null;
	private Text filePathText = null;
	private Text logText = null;
	private Button chooseBtn = null;
	private Button importButton = null;	
	private String filePath = null;
	public String productLineItemUID = "";
	public AbstractAIFUIApplication app;
	
	public MatrixBOMImputDialog(Shell parentShell, TCSession session,AbstractAIFUIApplication app) {
		super(parentShell);
		this.parentShell = parentShell;
		this.app = app;
		this.session = session;
		productLineItemUID = app.getTargetComponent().getUid();
		initUI();
	}
	
	
	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(550, 600);
		shell.setText("Matrix BOM 导入");
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		
		
		Composite topComposite = new Composite(shell, SWT.NONE);
		topComposite.setLayout(new GridLayout(3, false));
		topComposite.setLayoutData(gridData);	
		
		GridData filePathData = new GridData(GridData.FILL_HORIZONTAL);
		filePathData.horizontalIndent = 15;
		
		filePathText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false); // 设置不可编辑
		filePathText.setLayoutData(filePathData);
		
		chooseBtn = new Button(topComposite, SWT.NONE);
		chooseBtn.setText("选择路径");
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		logText.setLayoutData(new GridData(GridData.FILL_BOTH));		 
		logText.setEditable(false);
		//logText.setEnabled(false);
		logText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE)); // 设置字体颜色
		
//		MyActionGroup actionGroup = new MyActionGroup(logText, reg);
//		actionGroup.fillContextMenu(new MenuManager());
		
		Composite bottomComposite = new Composite(shell, SWT.NONE);
		bottomComposite.setLayout(new GridLayout(1, true));
		bottomComposite.setLayoutData(gridData);
		
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.CENTER;		

		importButton = new Button(bottomComposite, SWT.NONE);
		importButton.setText("导入");
		importButton.setLayoutData(gridData2);
		importButton.setEnabled(false); // 设置为不可以点击		
		
		addListener(); // 添加事件监听
		
		shell.open();
		shell.layout();

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}
	
	private void addListener() {
		chooseBtn.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				filePath = openFileChooser(shell);
				if (CommonTools.isEmpty(filePath)) {
					return;
				}
				filePathText.setText(filePath);
				importButton.setEnabled(true);
			}
		});
		
		
		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				logText.setText("");
				String filePath = filePathText.getText();
				importButton.setEnabled(false);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						InputStream input = null;
						XSSFWorkbook workbook = null;
						HashMap<String, PictureData> excelimgMap = null;
						try {
							TCUtil.setBypass(session);
							
							writeDisposedText("开始导入：" + filePath);
							input = new FileInputStream(filePath);
							
							workbook = new XSSFWorkbook(input);
							int numberOfSheets = workbook.getNumberOfSheets();
							
							XSSFSheet sheet0 = workbook.getSheetAt(0);
							String sheetName0 = sheet0.getSheetName();
							System.out.println("sheetName0 = "+sheetName0);
							
							String sheetName = "";
							XSSFSheet sheet = null;
							if(numberOfSheets>1) {
								sheet = workbook.getSheetAt(1);
								sheetName = sheet.getSheetName();
								System.out.println("sheetName = "+sheetName);
							}

							//获取其他属性
							ArrayList<ProductLineBOMBean> productList = new ArrayList<ProductLineBOMBean>();
							LinkedHashMap<String, VariableBOMBean> variableMap = new LinkedHashMap<String, VariableBOMBean>();
							
							//通过sheet名称判断导入类型
							if(sheetName0.contains("PSU")){
								writeInfoLogText("导入Matrix PSU List");
								getPSUExcel(productList, productLineItemUID, filePath);
								
								//物料、替代料导入
								if(productList!=null && productList.size() > 0) {
									writeDisposedText("");
									writeInfoLogText("清理历史导入数据");
									importCutBOMLine(app);
									importPSUMatrixParts(productList);
								}
								writeDisposedText("");
								writeInfoLogText("请检查提示信息，若无问题可关闭导入窗口！");
								
								return;
							} else if(sheetName.contains("Cable")){
								//新增 Cable
								writeInfoLogText("导入Matrix Cable list");
								//获取图片，返回对应的坐标
								Map<String, PictureData> picMap = null;
								try {
									UserExcelPicUtil excelPicUtil = new UserExcelPicUtil();
									picMap = excelPicUtil.getPicMap(workbook, 1);
								} catch (Exception e) {
									if(input!=null) {
										input.close();
									}
									if(workbook!=null) {
										workbook.close();
									}
									e.printStackTrace();
									writeErrorLogText("Excel中插入的图片有问题，请检查Excel中插入的图片！");
									writeDisposedText("导入结束");	
								}
								if(picMap!=null) {
									excelimgMap = getExcelimgMap(picMap, sheet);
								}
								
								getCableExcel(productList,variableMap,sheet,excelimgMap,productLineItemUID);
								//物料、替代料导入
								if(productList!=null && productList.size() > 0) {
									importCableMatrixParts(productList, app);
								} else {
									writeInfoLogText("Excel模板读取错误,结束导入!");
									return;
								}
								writeDisposedText("");
								writeInfoLogText("物料对象导入完毕,开始导入变体!");
								
								//变体导入
								if(variableMap!=null && variableMap.size() > 0) 
									importCableVariable(variableMap, app);
								
							} else {
								writeInfoLogText("导入Matrix");
								//获取图片，返回对应的坐标
								Map<String, PictureData> picMap = null;
								try {
									UserExcelPicUtil excelPicUtil = new UserExcelPicUtil();
									picMap = excelPicUtil.getPicMap(workbook, 1);
								} catch (Exception e) {
									if(input!=null) {
										input.close();
									}
									if(workbook!=null) {
										workbook.close();
									}
									e.printStackTrace();
									writeErrorLogText("Excel中插入的图片有问题，请检查Excel中插入的图片！");
									writeDisposedText("导入结束");	
								}
								if(picMap!=null) {
									excelimgMap = getExcelimgMap(picMap, sheet);
								}
								
								if(sheetName.contains("Screw")) {
									getExcel(productList,variableMap,sheet,excelimgMap,productLineItemUID,true);
								}else {
									getExcel(productList,variableMap,sheet,excelimgMap,productLineItemUID,false);
								}
								
								//物料、替代料导入
								if(productList!=null && productList.size() > 0) {
									importMatrixParts(productList, app);
								} else {
									writeInfoLogText("Excel模板读取错误,结束导入!");
									return;
								}
								writeDisposedText("");
								writeInfoLogText("物料对象导入完毕,开始导入变体!");
								
								//变体导入
								if(variableMap!=null && variableMap.size() > 0) 
									importVariable(variableMap, app);
							}
							
							writeInfoLogText("导入变体完毕!");
							writeDisposedText("");
							writeInfoLogText("请检查提示信息，若无问题可关闭导入窗口！");
							
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							writeInfoLogText("导入结束");	
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							writeInfoLogText("导入结束");	
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							writeInfoLogText("导入结束");	
						} finally {
							TCUtil.closeBypass(session);
							if(input!=null) {
								try {
									input.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if(workbook!=null) {
								try {
									workbook.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					}
					
				}).start();
				
			}
			
		});
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				shell.dispose();
			}
		});
	}
	
	public void importPSUMatrixParts(List<ProductLineBOMBean> productList) {
	
		for (ProductLineBOMBean rootBean:productList) {
			
			String itemId = rootBean.getItemId();
			writeDisposedText("");
			writeInfoLogText("开始创建物料：" + itemId );
			String jsons = JSONArray.toJSONString(rootBean);
			AjaxResult addMatrixParts = new ProductLineBOMService(app).imputAddMatrixParts(jsons);
			
			Object CODE_TAG_item = addMatrixParts.get(AjaxResult.CODE_TAG);
			if("0000".equals(CODE_TAG_item)) {
				rootBean = (ProductLineBOMBean) addMatrixParts.get(AjaxResult.DATA_TAG);
				writeInfoLogText("物料：" + rootBean.getItemId()+"，创建成功！");
				writeInfoLogText("开始搭建物料结构：" + rootBean.getItemId());
				
				List<ProductLineBOMBean> rootBeanList = new ArrayList<ProductLineBOMBean>();
				rootBeanList.add(rootBean);
				AjaxResult addBOMMatrixParts = new ProductLineBOMService(app).imputAddBOMMatrixParts(JSONArray.toJSONString(rootBeanList));
				
				Object CODE_TAG = addBOMMatrixParts.get(AjaxResult.CODE_TAG);
				if("0000".equals(CODE_TAG)) {
					writeInfoLogText("物料对象：" + rootBean.getItemId() +"，结构搭建成功！");
					//List<ProductLineBOMBean> rootBeanSub_1 = (List<ProductLineBOMBean>) addBOMMatrixParts.get(AjaxResult.DATA_TAG);
				} else {
					Object MSG_TAG = addBOMMatrixParts.get(AjaxResult.MSG_TAG);
					writeErrorLogText(""+MSG_TAG);
				}
			} else {
				Object MSG_TAG = addMatrixParts.get(AjaxResult.MSG_TAG);
				writeErrorLogText(""+MSG_TAG);
				continue;
			}
		}
	}
	
	/**
	 * 导入PA BOM前 清空数据
	 * @param excelList
	 * @param app
	 */
	public void importCutBOMLine(AbstractAIFUIApplication app) {
		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) app.getTargetComponent();
			bomwindow = ProductLineBOMService.createBOMWindow(session);
			TCComponentBOMLine bomline = ProductLineBOMService.getTopBomline(bomwindow, productLineItemRev);
			AIFComponentContext[] childrens = bomline.getChildren();
		
			if (childrens.length > 0) {
				for (AIFComponentContext children:childrens) {
					TCComponentBOMLine childrenBomline = (TCComponentBOMLine) children.getComponent();
					
					AIFComponentContext[] childrens1 = childrenBomline.getChildren();
					for (AIFComponentContext children1:childrens1) {
						TCComponentBOMLine component = (TCComponentBOMLine) children1.getComponent();
						
						component.cut();
						bomwindow.save();
					}
					
					childrenBomline.cut();
				}
				bomwindow.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	
	
	public void importCableMatrixParts(ArrayList<ProductLineBOMBean> excelList, AbstractAIFUIApplication app) {
		if(excelList!=null && excelList.size() > 0) {
			TCComponentBOMWindow bomwindow = null;
			
			try {
				TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) app.getTargetComponent();
				bomwindow = ProductLineBOMService.createBOMWindow(session);
				TCComponentBOMLine bomline = ProductLineBOMService.getTopBomline(bomwindow, productLineItemRev);
				AIFComponentContext[] childrens = bomline.getChildren();
			
				if (childrens.length > 0) {
					for (AIFComponentContext children:childrens) {
						TCComponentBOMLine childrenBomline = (TCComponentBOMLine) children.getComponent();
						TCComponentBOMLine[] childrenSubs = childrenBomline.listSubstitutes();
						
						for (TCComponentBOMLine childrenSub:childrenSubs) {
							 childrenSub.cut();
						}
						
						childrenBomline.cut();
					}
					bomwindow.save();
				}
				
				for (ProductLineBOMBean rootBean:excelList) {
					String itemId = rootBean.getItemId();
					System.out.println("itemId = "+itemId);
					String uid = "";
					
					//创建物料
					writeDisposedText("");
					writeInfoLogText("开始创建物料：" + itemId );
					String jsonString = JSONArray.toJSONString(rootBean);
					AjaxResult addMatrixParts = new ProductLineBOMService(app).addMatrixParts(jsonString);
					Object CODE_TAG_item = addMatrixParts.get(AjaxResult.CODE_TAG);
					if("0000".equals(CODE_TAG_item)) {
						ProductLineBOMBean newrootBean = (ProductLineBOMBean) addMatrixParts.get(AjaxResult.DATA_TAG);
						writeInfoLogText("物料：" + newrootBean.getItemId()+"，创建成功！");
						writeInfoLogText("开始搭建物料结构：" + newrootBean.getItemId());
						
						//搭建物料结构
						List<ProductLineBOMBean> rootBeanList = new ArrayList<ProductLineBOMBean>();
						rootBeanList.add(newrootBean);
						AjaxResult addBOMMatrixParts = new ProductLineBOMService(app).addBOMMatrixParts(JSONArray.toJSONString(rootBeanList));
						
						Object CODE_TAG = addBOMMatrixParts.get(AjaxResult.CODE_TAG);
						if("0000".equals(CODE_TAG)) {
							writeInfoLogText("物料对象：" + rootBean.getItemId() +"，结构搭建成功！");
							List<ProductLineBOMBean> rootBeanSub_1 = (List<ProductLineBOMBean>) addBOMMatrixParts.get(AjaxResult.DATA_TAG);
							if(rootBeanSub_1!=null && rootBeanSub_1.size() > 0 ) {
								uid = rootBeanSub_1.get(0).getBomLineUid();
							}
						}else {
							Object MSG_TAG = addBOMMatrixParts.get(AjaxResult.MSG_TAG);
							writeErrorLogText(""+MSG_TAG);
						}
						
						
						List<ProductLineBOMBean> subList = rootBean.getSubList();
						if(CommonTools.isNotEmpty(subList)) {
							
							for (ProductLineBOMBean sub:subList) {
								if(!"".equals(uid)){
									String itemId_sub =	sub.getItemId();
									
									writeDisposedText("");
									writeInfoLogText("开始创建替代料：" + itemId_sub );
									String jsonString_sub = JSONArray.toJSONString(sub);
									AjaxResult addMatrixParts_sub = new ProductLineBOMService(app).addMatrixParts(jsonString_sub);
									Object CODE_TAG_item_sub = addMatrixParts_sub.get(AjaxResult.CODE_TAG);
									if("0000".equals(CODE_TAG_item_sub)) {
										ProductLineBOMBean newrootBean_sub = (ProductLineBOMBean) addMatrixParts_sub.get(AjaxResult.DATA_TAG);
										writeInfoLogText("创建替代料：" + newrootBean_sub.getItemId()+"，创建成功！");
										writeInfoLogText("开始搭建替代料结构：" + newrootBean_sub.getItemId());
										newrootBean_sub.setBomLineUid(uid);
										List<ProductLineBOMBean> rootBeanListSub = new ArrayList<ProductLineBOMBean>();
										rootBeanListSub.add(newrootBean_sub);
										AjaxResult addBOMMatrixParts_sub = new ProductLineBOMService(app).imputAddSubMatrixParts(JSONArray.toJSONString(rootBeanListSub));
										
										Object CODE_TAG_sub = addBOMMatrixParts_sub.get(AjaxResult.CODE_TAG);
										if("0000".equals(CODE_TAG_sub)) {
											writeInfoLogText("替代料对象：" + newrootBean_sub.getItemId() +"，结构搭建成功！");
										}else {
											Object MSG_TAG = addBOMMatrixParts_sub.get(AjaxResult.MSG_TAG);
											writeErrorLogText(""+MSG_TAG);
										}
										
									}else {
										Object MSG_TAG = addMatrixParts_sub.get(AjaxResult.MSG_TAG);
										writeErrorLogText(""+MSG_TAG);
									}
								}
							}
						}
					} else {
						Object MSG_TAG = addMatrixParts.get(AjaxResult.MSG_TAG);
						writeErrorLogText(""+MSG_TAG);
						continue;
					}
				}
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (ObjUtil.isNotEmpty(bomwindow)) {
					try {
						bomwindow.save();
						bomwindow.close();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}
	
	public void importCableVariable(LinkedHashMap<String, VariableBOMBean> variableMap, AbstractAIFUIApplication app) {
		TCComponentBOMWindow bomwindow = null;
		try {
			//避免重复导入
			TCComponentItemRevision itemRev = (TCComponentItemRevision) app.getTargetComponent();
			String matrixUid = itemRev.getUid();
			
			AIFComponentContext[] hasVariantHolder_RELs = itemRev.getRelated("D9_HasVariantHolder_REL");
			for (AIFComponentContext hasVariantHolder_REL: hasVariantHolder_RELs) {
				TCComponentItemRevision component = (TCComponentItemRevision)hasVariantHolder_REL.getComponent();
				bomwindow = ProductLineBOMService.createBOMWindow(session);
				TCComponentBOMLine bomline = ProductLineBOMService.getTopBomline(bomwindow, component);
				AIFComponentContext[] childrens = bomline.getChildren();
				if (childrens.length > 0) {
					for (AIFComponentContext children:childrens) {
						AIFComponentContext[] childrens_1 = ((TCComponentBOMLine)children.getComponent()).getChildren();
						if (childrens_1.length > 0) {
							for (AIFComponentContext children_1:childrens_1) {
								((TCComponentBOMLine) children_1.getComponent()).cut();
							}
							bomwindow.save();
						}
						((TCComponentBOMLine) children.getComponent()).cut();
					}
					bomwindow.save();
				}
			}
			itemRev.setRelated("D9_HasVariantHolder_REL", new TCComponent[] {} );
			itemRev.setRelated("D9_HasVariants_REL", new TCComponent[] {} );
			
			
			
			LinkedHashMap<String, VariableBOMBean> newVariableMap = new LinkedHashMap<String, VariableBOMBean>();
			
			ArrayList<String> revUids = new ArrayList<String>();
			for (Entry<String, VariableBOMBean> map : variableMap.entrySet()) {
				String key = map.getKey();
				String substring = key.substring(key.lastIndexOf("_")+1);
				//System.out.println("substring = "+substring);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", "");
				jsonObj.put("name", substring);
				jsonObj.put("itemType", "D9_CommonPart");
				jsonObj.put("desc", "");
				
				AjaxResult add = new VariablesBOMService(app).add(jsonObj.toJSONString());
				Object CODE_TAG = add.get(AjaxResult.CODE_TAG);
				if("0000".equals(CODE_TAG)) {
					String itemId = (String) add.get("itemId");
					writeInfoLogText("变体Item对象：" + itemId+"，创建成功！");
				}else {
					Object MSG_TAG = add.get(AjaxResult.MSG_TAG);
					writeErrorLogText(""+MSG_TAG);
					continue;
				}
				
				String itemRevUid = (String) add.get("itemRevUid");
				revUids.add(itemRevUid);
				
				VariableBOMBean value = map.getValue();
				newVariableMap.put(itemRevUid, value);
			}
			
			writeInfoLogText("变体对象创建完成。");
			
			writeDisposedText("");
			writeInfoLogText("开始搭建结构...");
			
			if(revUids!=null && revUids.size() > 0) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("matrixUid", matrixUid);
				jsonObj.put("variableUidList", revUids);
				
				System.out.println("jsonObj.toJSONString() = "+jsonObj.toJSONString());
				
				
				AjaxResult bind = new VariablesBOMService(app).imputBind(jsonObj.toJSONString());
				Object CODE_TAG = bind.get(AjaxResult.CODE_TAG);
				
				if("0000".equals(CODE_TAG)) {
					writeInfoLogText("结构搭建完成。");
					
					writeDisposedText("");
					writeInfoLogText("开始保存结构数据（请等待完成）...");
				}else {
					Object MSG_TAG = bind.get(AjaxResult.MSG_TAG);
					writeErrorLogText(""+MSG_TAG);
				}
			}
			
			for (Entry<String, VariableBOMBean> map : newVariableMap.entrySet()) {
				String key = map.getKey();
				VariableBOMBean value = map.getValue();
				
				TCComponentItemRevision variableItemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(key);
				if(variableItemRev != null) {
					bomwindow = ProductLineBOMService.createBOMWindow(session);
					TCComponentBOMLine topLine = ProductLineBOMService.getTopBomline(bomwindow, variableItemRev);
					
					List<VariableBOMBean> childs = value.getChild();
					if(childs!=null && childs.size()>0) {
						TCComponentBOMLine bomline_1 = null;
						for (VariableBOMBean child:childs) {
							TCComponentItemRevision latestItemRevision = null;
							
							String getItemId_1 = child.getItemId();
							if("".equals(getItemId_1) || "NA".equalsIgnoreCase(getItemId_1) || "N/A".equalsIgnoreCase(getItemId_1)) 
								continue;
							String getQty = child.getQty();
							String match = "[0-9]{1,6}";
							if (!getQty.matches(match)) {
								getQty = "";
							}
							String getCategory = child.getCategory().trim();
							String getPlant = child.getPlant();
							String getRemark = child.getRemark();
							String getTorqueIn = child.getTorqueIn();
							String getTorqueOut = child.getTorqueOut();
							
							TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...",
									new String[] { "items_tag.item_id" },
									new String[] { getItemId_1});
							if (executeQuery != null && executeQuery.length > 0) {
								for (int i = 0; i < executeQuery.length; i++) {
									if (executeQuery[i] instanceof TCComponentItemRevision) {
										String type = executeQuery[i].getType();
										if("D9_L5_PartRevision".equals(type)) {
											continue;
										}
										
										latestItemRevision = (TCComponentItemRevision) executeQuery[i];
									}
								}
							}
							if(latestItemRevision!=null) {
								boolean sub = child.getIsSub();
								if(!sub) {
									TCComponentBOMLine bomline = topLine.add(latestItemRevision.getItem(), latestItemRevision, null, false);
									if(getQty.contains(".")) {
										bomline.setProperty("bl_uom","Other");
									}
									bomline.setProperty("bl_quantity", ""+getQty);
									
									bomline.setProperty("bl_occ_d9_Plant", ""+getPlant);
									bomline.setProperty("bl_occ_d9_Category", ""+getCategory);
									bomline.setProperty("bl_occ_d9_Remark", getRemark);
									bomline.setProperty("bl_occ_d9_TorqueIn", getTorqueIn);
									bomline.setProperty("bl_occ_d9_TorqueOut", getTorqueOut);
									
									bomline_1 = bomline;
								} else {
									if(bomline_1!=null) {
										bomline_1.add(latestItemRevision.getItem(), latestItemRevision, null, true);
									}
								}
							}
						}
					}
					
					bomwindow.save();
				}
			}
			
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void importMatrixParts(ArrayList<ProductLineBOMBean> excelList, AbstractAIFUIApplication app) {
		if(excelList!=null && excelList.size() > 0) {
			TCComponentBOMWindow bomwindow = null;
			
			try {
				TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) app.getTargetComponent();
				bomwindow = ProductLineBOMService.createBOMWindow(session);
				TCComponentBOMLine bomline = ProductLineBOMService.getTopBomline(bomwindow, productLineItemRev);
				AIFComponentContext[] childrens = bomline.getChildren();
			
				if (childrens.length > 0) {
					for (AIFComponentContext children:childrens) {
						TCComponentBOMLine childrenBomline = (TCComponentBOMLine) children.getComponent();
						TCComponentBOMLine[] childrenSubs = childrenBomline.listSubstitutes();
						
						for (TCComponentBOMLine childrenSub:childrenSubs) {
							 childrenSub.cut();
						}
						
						childrenBomline.cut();
					}
					bomwindow.save();
				}
				
				for (ProductLineBOMBean rootBean:excelList) {
					String itemId = rootBean.getItemId();
					System.out.println("itemId = "+itemId);
					String[] splits = itemId.split("\n");
					String uid = "";
					
					if(splits!=null && splits.length > 0 ) {
						for (int i = 0; i < splits.length; i++) {
							String newitemId = splits[i].trim();
							if("".equals(newitemId)) 
								continue;
							
							System.out.println("创建itemId = "+newitemId);
							ProductLineBOMBean newrootBean = new ProductLineBOMBean();
							newrootBean = rootBean;
							newrootBean.setItemId(newitemId);
							
							writeDisposedText("");
							writeInfoLogText(i==0?"开始创建物料：" + newitemId : "开始创建替代料：" + newitemId);
							String jsonString = JSONArray.toJSONString(newrootBean);
							AjaxResult addMatrixParts = new ProductLineBOMService(app).addMatrixParts(jsonString);
							
							
							Object CODE_TAG_item = addMatrixParts.get(AjaxResult.CODE_TAG);
							if("0000".equals(CODE_TAG_item)) {
								newrootBean = (ProductLineBOMBean) addMatrixParts.get(AjaxResult.DATA_TAG);
								writeInfoLogText(i==0?"物料：" + newrootBean.getItemId()+"，创建成功！" : "替代料：" + newrootBean.getItemId()+"，创建成功！");
								writeInfoLogText(i==0?"开始搭建物料结构：" + newrootBean.getItemId() : "开始搭替代料结构：" + newrootBean.getItemId());
								
								if(i==0) {
									List<ProductLineBOMBean> rootBeanList = new ArrayList<ProductLineBOMBean>();
									rootBeanList.add(newrootBean);
									AjaxResult addBOMMatrixParts = new ProductLineBOMService(app).addBOMMatrixParts(JSONArray.toJSONString(rootBeanList));
									
									Object CODE_TAG = addBOMMatrixParts.get(AjaxResult.CODE_TAG);
									if("0000".equals(CODE_TAG)) {
										writeInfoLogText("物料对象：" + rootBean.getItemId() +"，结构搭建成功！");
										List<ProductLineBOMBean> rootBeanSub_1 = (List<ProductLineBOMBean>) addBOMMatrixParts.get(AjaxResult.DATA_TAG);
										if(rootBeanSub_1!=null && rootBeanSub_1.size() > 0 ) {
											uid = rootBeanSub_1.get(0).getBomLineUid();
										}
									}else {
										Object MSG_TAG = addBOMMatrixParts.get(AjaxResult.MSG_TAG);
										writeErrorLogText(""+MSG_TAG);
									}
									
								} else if(!"".equals(uid)){
									ProductLineBOMBean rootBeanSub = (ProductLineBOMBean) addMatrixParts.get(AjaxResult.DATA_TAG);
									rootBeanSub.setBomLineUid(uid);
									List<ProductLineBOMBean> rootBeanListSub = new ArrayList<ProductLineBOMBean>();
									rootBeanListSub.add(rootBeanSub);
									AjaxResult addBOMMatrixParts = new ProductLineBOMService(app).imputAddSubMatrixParts(JSONArray.toJSONString(rootBeanListSub));
									
									Object CODE_TAG = addBOMMatrixParts.get(AjaxResult.CODE_TAG);
									if("0000".equals(CODE_TAG)) {
										writeInfoLogText("替代料对象：" + rootBeanSub.getItemId() +"，结构搭建成功！");
									}else {
										Object MSG_TAG = addBOMMatrixParts.get(AjaxResult.MSG_TAG);
										writeErrorLogText(""+MSG_TAG);
									}
								}
								
							}else {
								Object MSG_TAG = addMatrixParts.get(AjaxResult.MSG_TAG);
								writeErrorLogText(""+MSG_TAG);
								continue;
							}
							
						}
					}
				}
			
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (ObjUtil.isNotEmpty(bomwindow)) {
					try {
						bomwindow.save();
						bomwindow.close();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}
	
	public void importVariable(LinkedHashMap<String, VariableBOMBean> variableMap, AbstractAIFUIApplication app) {
		TCComponentBOMWindow bomwindow = null;
		try {
			//避免重复导入
			TCComponentItemRevision itemRev = (TCComponentItemRevision) app.getTargetComponent();
			String matrixUid = itemRev.getUid();
			
			AIFComponentContext[] hasVariantHolder_RELs = itemRev.getRelated("D9_HasVariantHolder_REL");
			for (AIFComponentContext hasVariantHolder_REL: hasVariantHolder_RELs) {
				TCComponentItemRevision component = (TCComponentItemRevision)hasVariantHolder_REL.getComponent();
				bomwindow = ProductLineBOMService.createBOMWindow(session);
				TCComponentBOMLine bomline = ProductLineBOMService.getTopBomline(bomwindow, component);
				AIFComponentContext[] childrens = bomline.getChildren();
				if (childrens.length > 0) {
					for (AIFComponentContext children:childrens) {
						AIFComponentContext[] childrens_1 = ((TCComponentBOMLine)children.getComponent()).getChildren();
						if (childrens_1.length > 0) {
							for (AIFComponentContext children_1:childrens_1) {
								((TCComponentBOMLine) children_1.getComponent()).cut();
							}
							bomwindow.save();
						}
						((TCComponentBOMLine) children.getComponent()).cut();
					}
					bomwindow.save();
				}
			}
			itemRev.setRelated("D9_HasVariantHolder_REL", new TCComponent[] {} );
			itemRev.setRelated("D9_HasVariants_REL", new TCComponent[] {} );
			
			
			LinkedHashMap<String, VariableBOMBean> newVariableMap = new LinkedHashMap<String, VariableBOMBean>();
			
			ArrayList<String> revUids = new ArrayList<String>();
			for (Entry<String, VariableBOMBean> map : variableMap.entrySet()) {
				String key = map.getKey();
				String substring = key.substring(key.lastIndexOf("_")+1);
				//System.out.println("substring = "+substring);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", "");
				jsonObj.put("name", substring);
				jsonObj.put("itemType", "D9_CommonPart");
				jsonObj.put("desc", "");
				
				AjaxResult add = new VariablesBOMService(app).add(jsonObj.toJSONString());
				Object CODE_TAG = add.get(AjaxResult.CODE_TAG);
				if("0000".equals(CODE_TAG)) {
					String itemId = (String) add.get("itemId");
					writeInfoLogText("变体Item对象：" + itemId+"，创建成功！");
				}else {
					Object MSG_TAG = add.get(AjaxResult.MSG_TAG);
					writeErrorLogText(""+MSG_TAG);
					continue;
				}
				
				String itemRevUid = (String) add.get("itemRevUid");
				revUids.add(itemRevUid);
				
				VariableBOMBean value = map.getValue();
				newVariableMap.put(itemRevUid, value);
			}
			
			writeInfoLogText("变体对象创建完成。");
			
			writeDisposedText("");
			writeInfoLogText("开始搭建结构...");
			
			if(revUids!=null && revUids.size() > 0) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("matrixUid", matrixUid);
				jsonObj.put("variableUidList", revUids);
				
				System.out.println("jsonObj.toJSONString() = "+jsonObj.toJSONString());
				
				
				AjaxResult bind = new VariablesBOMService(app).imputBind(jsonObj.toJSONString());
				Object CODE_TAG = bind.get(AjaxResult.CODE_TAG);
				
				if("0000".equals(CODE_TAG)) {
					writeInfoLogText("结构搭建完成。");
					
					writeDisposedText("");
					writeInfoLogText("开始保存结构数据（请等待完成）...");
				}else {
					Object MSG_TAG = bind.get(AjaxResult.MSG_TAG);
					writeErrorLogText(""+MSG_TAG);
				}
			}
			
			for (Entry<String, VariableBOMBean> map : newVariableMap.entrySet()) {
				String key = map.getKey();
				VariableBOMBean value = map.getValue();
				List<VariableBOMBean> childs = value.getChild();
				
				TCComponentItemRevision variableItemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(key);
				if(variableItemRev != null) {
					bomwindow = ProductLineBOMService.createBOMWindow(session);
					TCComponentBOMLine topLine = ProductLineBOMService.getTopBomline(bomwindow, variableItemRev);
					
					if(childs!=null) {
						for (VariableBOMBean child : childs) {
							TCComponentItemRevision latestItemRevision = null;
							String getItemId_1 = child.getItemId();
							
							String itemId_1 = "";
							String[] splits = getItemId_1.split("\n");
							if(splits!=null && splits.length > 0 ) {
								TCComponentBOMLine bomline1 = null;
								for (int j = 0; j < splits.length; j++) {
									itemId_1 = splits[j].trim();
									//System.out.println("itemId_1 = "+itemId_1);
									if("".equals(itemId_1) || "NA".equalsIgnoreCase(itemId_1) || "N/A".equalsIgnoreCase(itemId_1)) 
										continue;
								
									String getQty = child.getQty();
									String match = "[0-9]{1,6}";
									if (!getQty.matches(match)) {
										getQty = "";
									}
									String getCategory = child.getCategory().trim();
									String getPlant = child.getPlant();
									String getRemark = child.getRemark();
									String getTorqueIn = child.getTorqueIn();
									String getTorqueOut = child.getTorqueOut();
									
									TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...",
											new String[] { "items_tag.item_id" },
											new String[] { itemId_1});
									if (executeQuery != null && executeQuery.length > 0) {
										for (int i = 0; i < executeQuery.length; i++) {
											if (executeQuery[i] instanceof TCComponentItemRevision) {
												String type = executeQuery[i].getType();
												if("D9_L5_PartRevision".equals(type)) {
													continue;
												}
												
												latestItemRevision = (TCComponentItemRevision) executeQuery[i];
											}
										}
									}
									if(latestItemRevision!=null) {
										
										if(j == 0) {
											bomline1 = topLine.add(latestItemRevision.getItem(), latestItemRevision, null, false);
											bomline1.setProperty("bl_quantity", ""+getQty);
											
											bomline1.setProperty("bl_occ_d9_Plant", ""+getPlant);
											bomline1.setProperty("bl_occ_d9_Category", ""+getCategory);
											bomline1.setProperty("bl_occ_d9_Remark", getRemark);
											bomline1.setProperty("bl_occ_d9_TorqueIn", getTorqueIn);
											bomline1.setProperty("bl_occ_d9_TorqueOut", getTorqueOut);
										} else {
											if(bomline1 != null) {
												bomline1.add(latestItemRevision.getItem(), latestItemRevision, null, true);
											}else {
												writeDisposedText("");
												writeErrorLogText("新增替代料失败");
											}
										}
									}
								}
							}
						}
					}
					
					bomwindow.save();
				}
			}
			
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 记录提示信息日志
	 * 
	 * @param message
	 */
	public void writeDisposedText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【INFO】 " + message + "\n");
				if (!logText.isDisposed()) {
					logText.insert(message + "\n");
				}
			}
		});
	}
	
	/**
	 * 记录提示信息日志
	 * 
	 * @param message
	 */
	public void writeInfoLogText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【INFO】 " + message + "\n");
				if (!logText.isDisposed()) {
					logText.insert("【INFO】 " + message + "\n");
				}
			}
		});
	}
	
	/**
	 * 记录错误日志信息
	 * @param message
	 */
	public void writeErrorLogText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【ERROR】 " + message + "\n");
				if (!logText.isDisposed()) {
					logText.insert("【ERROR】 " + message + "\n");					
				}
			}
		});
	}
	
	
	/**
	 * 获取sheet页中的图片
	 * @param workbook
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, PictureData> getExcelimgMap(Map<String, PictureData> picMap,XSSFSheet sheet) throws IOException {
		HashMap<String, PictureData> newpicMap = new HashMap<String, PictureData>();
		
		
		for (Entry<String, PictureData> nodeEntry : picMap.entrySet()) {
			String key = nodeEntry.getKey();
			//System.out.println("key = "+key);
			
			String[] split = key.split("_");
			if(split!=null && split.length == 2) {
				int parseInt = Integer.parseInt(split[0]);
				int parseInt2 = Integer.parseInt(split[1]);
				String key_1 = getPicKey(sheet, parseInt, parseInt2);
				//System.out.println("key_1 = "+key_1);
				newpicMap.put(key_1, nodeEntry.getValue());
			} else {
				System.out.println("key_2 = "+key);
				newpicMap.put(key, nodeEntry.getValue());
			}
		}
		return newpicMap;
	}
	
	/**
	 * 读取PABOM Excel
	 * @param sheet
	 * @param excelimgMap
	 * @throws IOException
	 */
	public static void getPSUExcel(ArrayList<ProductLineBOMBean> productList, String productLineItemUID, String filePath ) throws IOException {
		ExcelReader reader = ExcelUtil.getReader(filePath, 0);
		List<List<Object>> readLists = reader.read();
		
		String chassisLast = "";
		String ratingLast = "";
		String typeLast = "";
		int sequence_no = 0;
		for (int i = 2; i < readLists.size(); i++) {
			List<Object> list = readLists.get(i);
			Object no = list.get(0);
			String chassis = (String)list.get(1);
			String rating = (String) list.get(8);
			String type = (String) list.get(9);
			String lenovoPN = (String)list.get(10);
			String description = (String) list.get(11);
			String vendor = (String)list.get(12);
			String foxconnPN = (String)list.get(15);
			String vendorPN = (String) list.get(16);
			String safetyCert = (String) list.get(17);
			String meetTCO90 = (String)list.get(18);
			
			ProductLineBOMBean productLine = new ProductLineBOMBean();
			productLine.setChassis(chassis.trim());
			productLine.setRating(rating.trim());
			productLine.setType(type.trim());
			productLine.setCustomerPN(lenovoPN.trim());
			productLine.setEnglishDescription(description.trim());
			productLine.setManufacturerID(vendor.trim());
			productLine.setItemId(foxconnPN.trim());
			productLine.setManufacturerPN(vendorPN.trim());
			productLine.setMeetTCO90(meetTCO90.trim());
			productLine.setProductLineItemUID(productLineItemUID);
			
			if("Foxconn PN".equals(foxconnPN.trim()) || "".equals(foxconnPN.trim()) ) 
				continue;
			
			//sequence_no++;
			if(chassis.equals(chassisLast) && rating.equals(ratingLast) && type.equals(typeLast)) {
				productLine.setSequence_no(sequence_no*10);
			} else {
				sequence_no++;
				productLine.setSequence_no(sequence_no*10);
			}
			productList.add(productLine);
			
			chassisLast = chassis;
			ratingLast = rating;
			typeLast = type;
		}
		
		IoUtil.close(reader);
	}
	
	/**
	 * 读取PABOM Excel
	 * @param sheet
	 * @param excelimgMap
	 * @throws IOException
	 */
	
	/**
	 * 读取Cable Excel
	 * @param sheet
	 * @param excelimgMap
	 * @throws IOException
	 */
	public static void getCableExcel(ArrayList<ProductLineBOMBean> productList,LinkedHashMap<String, VariableBOMBean> variableMap, XSSFSheet sheet, HashMap<String, PictureData> excelimgMap,String productLineItemUID) throws IOException {
		int remarkCell = 0;
		XSSFRow rowRemark = sheet.getRow(1);
		int cellRemark = rowRemark.getLastCellNum();
		for (int j = 10; j < cellRemark; j++) {
			String cellname = getMergedRegionValue(sheet, 1, j);
			if("Remark".equals(cellname)) {
				remarkCell = j;
				break;
			}
		}

		for (int i = 3; i <= sheet.getLastRowNum(); i++) {
			
			ProductLineBOMBean productLineBOMBean = new ProductLineBOMBean();
			XSSFRow row = sheet.getRow(i);
			int lastCellNum = row.getLastCellNum();
			
			String No = getMergedRegionValue(sheet, i, 0);
			//System.out.println("No = "+No);
			if( No == null || "".equals(No)) {
				break;
			}
			productLineBOMBean.setNo(No);
			
			String category = getMergedRegionValue(sheet, i, 1);
			productLineBOMBean.setCategory(category);
			
			//Description(Hole Type)
			String englishDescription = getMergedRegionValue(sheet, i, 2);
			productLineBOMBean.setEnglishDescription(englishDescription);
			
			//Spec/Pic
			String picKey = getPicKey(sheet, i, 3);
			
			String base64Str = "";
			String imgName = "";
			String suggestFileExtension = "";

			if(excelimgMap!=null) {
				PictureData pictureData = excelimgMap.get(picKey);
				if(pictureData!=null) {
					byte[] data = pictureData.getData();
					base64Str = Base64.encode(data).trim();
					suggestFileExtension = pictureData.suggestFileExtension();
					imgName = picKey+"."+suggestFileExtension;
					
				}
			}
			
			//System.out.println("base64Str = "+base64Str);
			productLineBOMBean.setBase64Str(base64Str);
			productLineBOMBean.setImgName(imgName);
			
			//工廠
			String plant = getMergedRegionValue(sheet, i, 4);
			productLineBOMBean.setPlant(plant);
			
			//Lenovo P/N
			String customerPN = getMergedRegionValue(sheet, i, 5);
			productLineBOMBean.setCustomerPN(customerPN);
			
			String itemId = "";
			//d9_FRUPN
			String frupn = getMergedRegionValue(sheet, i, 6);
			productLineBOMBean.setFrupn(frupn);
			
			//d9_ManufacturerID
			String manufacturerID = getMergedRegionValue(sheet, i, 7);
			productLineBOMBean.setManufacturerID(manufacturerID);
			
			//d9_ManufacturerPN
			String manufacturerPN = getMergedRegionValue(sheet, i, 8);
			productLineBOMBean.setManufacturerPN(manufacturerPN);
			
			//itemId
			itemId = getMergedRegionValue(sheet, i, 9);
			//System.out.println("itemId = "+itemId);
			productLineBOMBean.setItemId(itemId);
			
			//remark
			String remark = getMergedRegionValue(sheet, i, remarkCell);
			productLineBOMBean.setRemark(remark);
			
			//lineId
			String lineId = CommonTools.md5Encode(itemId+category+plant);
			productLineBOMBean.setLineId(lineId);
			productLineBOMBean.setProductLineItemUID(productLineItemUID);
			
			boolean isAddSub = false;
			if(CommonTools.isNotEmpty(productList)) {
				for (ProductLineBOMBean product:productList) {
					String no2 = product.getNo();
					if(no2.equals(No)) {
						List<ProductLineBOMBean> subList = product.getSubList();
						if(CommonTools.isEmpty(subList))
							subList = new ArrayList<ProductLineBOMBean>();
						
						subList.add(productLineBOMBean);
						product.setSubList(subList);
						isAddSub = true;
						break;
					}
				}
			} 
			
			if(!isAddSub) {
				productList.add(productLineBOMBean);
			}
			

			for (int j = 10; j < lastCellNum; j++) {
				//cellvalue
				VariableBOMBean child = new VariableBOMBean();
				child.setItemId(itemId);
				child.setPlant(plant);
				child.setCategory(category);
				child.setLineId(lineId);
				String cellvalue = getMergedRegionValue(sheet, i, j);
				child.setQty(cellvalue);
				child.setRemark(remark);
				child.setIsSub(isAddSub);
				
				String cellname = getMergedRegionValue(sheet, 1, j);
				if("Remark".equals(cellname)) {
					break;
				}
				
				String key = "1_"+j+"_"+cellname;
				VariableBOMBean variableBOMBean_1 = variableMap.get(key);
				if(variableBOMBean_1 == null) {
					variableBOMBean_1 = new VariableBOMBean();
					List<VariableBOMBean> getchild = variableBOMBean_1.getChild();
					if(getchild==null) {
						getchild = new ArrayList<VariableBOMBean>();
					}
					getchild.add(child);
					variableBOMBean_1.setChild(getchild);
					
					variableMap.put(key, variableBOMBean_1);
				} else {
					List<VariableBOMBean> getchild = variableBOMBean_1.getChild();
					if(getchild==null) {
						getchild = new ArrayList<VariableBOMBean>();
					}
					getchild.add(child);
				}
			}
		}
	}
	
	/**
	 * 读取Excel
	 * @param sheet
	 * @param excelimgMap
	 * @throws IOException
	 */
	public static void getExcel(ArrayList<ProductLineBOMBean> productList,LinkedHashMap<String, VariableBOMBean> variableMap, XSSFSheet sheet, HashMap<String, PictureData> excelimgMap,String productLineItemUID,boolean screw) throws IOException {
		int remarkCell = 0;
		if(!screw) {
			XSSFRow row = sheet.getRow(1);
			int lastCellNum = row.getLastCellNum();
			for (int j = 10; j < lastCellNum; j++) {
				String cellname = getMergedRegionValue(sheet, 1, j);
				if("Remark".equals(cellname)) {
					remarkCell = j;
					break;
				}
			}
		}
		
		for (int i = 3; i <= sheet.getLastRowNum(); i++) {
			
			ProductLineBOMBean productLineBOMBean = new ProductLineBOMBean();
			XSSFRow row = sheet.getRow(i);
			int lastCellNum = row.getLastCellNum();
			
			String No = getMergedRegionValue(sheet, i, 0);
			//System.out.println("No = "+No);
			if( No == null || "".equals(No)) {
				break;
			}
			
			//category
//			String category = "";
//			if(isMergedRegion(sheet, i, 1)) {
//				category = getMergedRegionValue(sheet, i, 1);
//			}else {
//				XSSFCell cell = row.getCell(1);
//				category = getvalue(cell);
//			}
			
			String category = getMergedRegionValue(sheet, i, 1);
			productLineBOMBean.setCategory(category);
			
			//Description(Hole Type)
			String englishDescription = getMergedRegionValue(sheet, i, 2);
			productLineBOMBean.setEnglishDescription(englishDescription);
			
			//Spec/Pic
			String picKey = getPicKey(sheet, i, 3);
			
			String base64Str = "";
			String imgName = "";
			String suggestFileExtension = "";


			if(excelimgMap!=null) {
				PictureData pictureData = excelimgMap.get(picKey);
				if(pictureData!=null) {
					byte[] data = pictureData.getData();
					base64Str = Base64.encode(data).trim();
					suggestFileExtension = pictureData.suggestFileExtension();
					imgName = picKey+"."+suggestFileExtension;
					
				}
			}
			
			//System.out.println("base64Str = "+base64Str);
			productLineBOMBean.setBase64Str(base64Str);
			productLineBOMBean.setImgName(imgName);
			
			
			//if(!"".equals(base64Str) && !"".equals(suggestFileExtension)) {
				//String filestr = "C:\\Users\\Chen\\Desktop\\png\\"+imgName;
				//base64StringToImage(base64Str, filestr, "", suggestFileExtension);
			//}
			
			
			//工廠
			String plant = getMergedRegionValue(sheet, i, 4);
			productLineBOMBean.setPlant(plant);
			
			//Lenovo P/N
			String customerPN = getMergedRegionValue(sheet, i, 5);
			productLineBOMBean.setCustomerPN(customerPN);
			
			String itemId = "";
			if(screw) {
				
				//itemId
				itemId = getMergedRegionValue(sheet, i, 6);
				productLineBOMBean.setItemId(itemId);
				
				//d9_ManufacturerID
				String manufacturerID = getMergedRegionValue(sheet, i, 7);
				productLineBOMBean.setManufacturerID(manufacturerID);
				
				
				//d9_TorqueIn
				String torqueIn = getMergedRegionValue(sheet, i, 8);
				productLineBOMBean.setTorqueIn(torqueIn);
				
				//d9_TorqueOut
				String torqueOut = getMergedRegionValue(sheet, i, 9);
				productLineBOMBean.setTorqueOut(torqueOut);
				

				for (int j = 10; j < lastCellNum-1; j++) {
					//cellvalue
					VariableBOMBean child = new VariableBOMBean();
					child.setItemId(itemId);
					child.setPlant(plant);
					child.setCategory(category);
					String lineId = CommonTools.md5Encode(itemId+category+plant);
					child.setLineId(lineId);
					String cellvalue = getMergedRegionValue(sheet, i, j);
					child.setQty(cellvalue);
					
					child.setTorqueIn(torqueIn);
					child.setTorqueOut(torqueOut);
					
					
					String cellname = getMergedRegionValue(sheet, 1, j);
					if("Total".equals(cellname)) {
						continue;
					}
					if("".equals(cellname)) {
						break;
					}
					
					String key = "1_"+j+"_"+cellname;
					VariableBOMBean variableBOMBean_1 = variableMap.get(key);
					if(variableBOMBean_1 == null) {
						variableBOMBean_1 = new VariableBOMBean();
						List<VariableBOMBean> getchild = variableBOMBean_1.getChild();
						if(getchild==null) {
							getchild = new ArrayList<VariableBOMBean>();
						}
						getchild.add(child);
						variableBOMBean_1.setChild(getchild);
						
						variableMap.put(key, variableBOMBean_1);
					}else {
						List<VariableBOMBean> getchild = variableBOMBean_1.getChild();
						if(getchild==null) {
							getchild = new ArrayList<VariableBOMBean>();
						}
						getchild.add(child);
					}
				}
			
			} else {
				//d9_FRUPN
				String frupn = getMergedRegionValue(sheet, i, 6);
				productLineBOMBean.setFrupn(frupn);
				
				//d9_ManufacturerID
				String manufacturerID = getMergedRegionValue(sheet, i, 7);
				productLineBOMBean.setManufacturerID(manufacturerID);
				
				//d9_ManufacturerPN
				String manufacturerPN = getMergedRegionValue(sheet, i, 8);
				productLineBOMBean.setManufacturerPN(manufacturerPN);
				
				//itemId
				itemId = getMergedRegionValue(sheet, i, 9);
				//System.out.println("itemId = "+itemId);
				productLineBOMBean.setItemId(itemId);
				
				//remark
				String remark = getMergedRegionValue(sheet, i, remarkCell);
				productLineBOMBean.setRemark(remark);
				
				for (int j = 10; j < lastCellNum; j++) {
					//cellvalue
					VariableBOMBean child = new VariableBOMBean();
					child.setItemId(itemId);
					child.setPlant(plant);
					child.setCategory(category);
					String lineId = CommonTools.md5Encode(itemId+category+plant);
					child.setLineId(lineId);
					String cellvalue = getMergedRegionValue(sheet, i, j);
					child.setQty(cellvalue);
					child.setRemark(remark);
					
					
					String cellname = getMergedRegionValue(sheet, 1, j);
					if("Remark".equals(cellname)) {
						break;
					}
					
					String key = "1_"+j+"_"+cellname;
					VariableBOMBean variableBOMBean_1 = variableMap.get(key);
					if(variableBOMBean_1 == null) {
						variableBOMBean_1 = new VariableBOMBean();
						List<VariableBOMBean> getchild = variableBOMBean_1.getChild();
						if(getchild==null) {
							getchild = new ArrayList<VariableBOMBean>();
						}
						getchild.add(child);
						variableBOMBean_1.setChild(getchild);
						
						variableMap.put(key, variableBOMBean_1);
					}else {
						List<VariableBOMBean> getchild = variableBOMBean_1.getChild();
						if(getchild==null) {
							getchild = new ArrayList<VariableBOMBean>();
						}
						getchild.add(child);
					}
				}
			}
			
			//lineId
			String lineId = CommonTools.md5Encode(itemId+category+plant);
			productLineBOMBean.setLineId(lineId);
			productLineBOMBean.setProductLineItemUID(productLineItemUID);
			
			productList.add(productLineBOMBean);
		}
	}
	
	/**
	 * 按单元格类型取值
	 * @param cell
	 * @return
	 */
	public static String getvalue(XSSFCell cell) {
		String sdf = "yyyy/MM/dd";
		String value = "";
		if (null == cell) {
			return value;
		}	
		
		switch (cell.getCellType().name()) {
		case "STRING":
			value = cell.getRichStringCellValue().getString();
			break;
		case "NUMERIC":
			if (DateUtil.isCellDateFormatted(cell)) { 
				// 判断是否为日期类型
				Date date = cell.getDateCellValue();
				value = new SimpleDateFormat(sdf).format(date);
			} else {
				double numericCellValue = cell.getNumericCellValue();
				//转为整型
				int round = (int) Math.round(numericCellValue);
				value = ""+round;
			}			
			break;
		case "BOOLEAN":
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case "BLANK":
			value = null;
			break;
		case "ERROR":
			value = null;
			break;
		case "FORMULA":
			value = cell.getCellFormula() + "";
			break;
		default:
			value = cell.toString();
			break;
		}
		return value;
	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static String getMergedRegionValue(XSSFSheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					XSSFRow fRow = sheet.getRow(firstRow);
					XSSFCell fCell = fRow.getCell(firstColumn);
					return getvalue(fCell);
				}
			}
		}
		XSSFRow fRow = sheet.getRow(row);
		XSSFCell fCell = fRow.getCell(column);
		return getvalue(fCell);
	}

	
	/**
	 * 获取图片对应的key值
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static String getPicKey (XSSFSheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return firstRow+"_"+firstColumn;
				}
			}
		}
		return row+"_"+column;
	}
	
	/**
	 * 将二进制转换为图片
	 *
	 * @param base64String
	 */
	public static void base64StringToImage(String base64String,String filestr,String imgName,String substring) {
		FileOutputStream fileOutputStream = null;
		try {
			//String substring = imgName.substring(imgName.lastIndexOf(".")+1);
			//String filestr = tempPath+"\\"+imgName;
			File file = new File(filestr);
			fileOutputStream = new FileOutputStream(file);
			byte[] bytes1 = Base64.decode(base64String);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
			
			ImgUtil.convert(bais, substring, new FileOutputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(fileOutputStream)) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 打开文件选择器
	 * @return	文件路径
	 */ 
	public static String openFileChooser(Shell shell){
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setFilterPath(TCUtil.getSystemDesktop());
		fileDialog.setFilterNames(new String[] {"Microsoft Excel(*.xlsx)"});
		fileDialog.setFilterExtensions(new String[] {"*.xlsx"});
		return fileDialog.open();
	}
	
	
	public static TCComponentItemRevision getlastRev(String itemid, String itemrev) throws Exception {
		TCComponentItemRevision latestItemRevision = null;
		
		TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...",
				new String[] { "items_tag.item_id" },
				new String[] { itemid});
		if (executeQuery != null && executeQuery.length > 0) {
			for (int i = 0; i < executeQuery.length; i++) {
				if (executeQuery[i] instanceof TCComponentItemRevision) {
					String type = executeQuery[i].getType();
					if("D9_L5_PartRevision".equals(type)) {
						continue;
					}
					
					TCComponentItemRevision revision = (TCComponentItemRevision) executeQuery[i];
					String item_revision_id = revision.getProperty("item_revision_id");
					if(item_revision_id.equals(itemrev)) {
						latestItemRevision = revision;
						
						return latestItemRevision;
					}
				}
			}
			
			if(latestItemRevision == null) {
				latestItemRevision = (TCComponentItemRevision) executeQuery[executeQuery.length-1];
			}
		}
		
		return latestItemRevision;
	}
	
	
}
