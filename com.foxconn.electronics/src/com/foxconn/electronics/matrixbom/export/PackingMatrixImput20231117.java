package com.foxconn.electronics.matrixbom.export;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.pamatrixbom.domain.PAProductLineBOMBean;
import com.foxconn.electronics.pamatrixbom.domain.PAVariableBOMBean;
import com.foxconn.electronics.pamatrixbom.service.PAVariablesBOMService;
import com.foxconn.mechanism.integrate.hhpnIntegrate.HHPNPojo;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpUtil;
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
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.soa.client.model.ErrorStack;

public class PackingMatrixImput20231117 extends Dialog {
	public TCSession session = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Shell shell = null;
	private Shell parentShell = null;
	private Text filePathText = null;
	private Text logText = null;
	private Button chooseBtn = null;
	private Button importButton = null;
	private String filePath = null;
	private String d9_ActualUserID = null;
	public String productLineItemUID = "";
	public AbstractAIFUIApplication app;
	public static String d9_CustomerModelNumber = null;
	public static String d9_FoxconnModelNumber = null;

	public PackingMatrixImput20231117(Shell parentShell, TCSession session, AbstractAIFUIApplication app, String d9_ActualUserID) {
		super(parentShell);
		this.parentShell = parentShell;
		this.app = app;
		this.session = session;
		this.d9_ActualUserID = d9_ActualUserID;
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
		logText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE)); // 设置字体颜色

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

	private static boolean containsName(String name, String contains) {
		String name_lowerCase = name.toLowerCase();
		String contains_lowerCase = contains.toLowerCase();

		if (name_lowerCase.contains(contains_lowerCase)) {
			return true;
		}
		return false;
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
						try {
							TCUtil.setBypass(session);

							writeDisposedText("开始导入：" + filePath);
							input = new FileInputStream(filePath);

							workbook = new XSSFWorkbook(input);

							XSSFSheet sheet0 = workbook.getSheetAt(0);
							String sheetName0 = sheet0.getSheetName();
							System.out.println("sheetName0 = " + sheetName0);
							if (containsName(sheetName0, "Change Log")) {
								writeDisposedText("");
								writeInfoLogText("导入：" + sheetName0);
								ArrayList<PackingMatrixImputBean> changeLogExcel = getChangeLogExcel(filePath);
								TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) app
										.getTargetComponent();
								if (productLineItemRev != null) {
									productLineItemRev.setRelated("d9_MatrixChangeTable", new TCComponent[] {});

									for (int i = 0; i < changeLogExcel.size(); i++) {
										PackingMatrixImputBean packingMatrixImputBean = changeLogExcel.get(i);
										// System.out.println(packingMatrixImputBean.toString());
										try {
											TCComponent component = createObjects(session, "D9_MatrixChangeTableRow",
													packingMatrixImputBean);
											if (component != null) {
												productLineItemRev.add("d9_MatrixChangeTable", component);
											}
										} catch (ServiceException e2) {
											writeErrorLogText(e2.getMessage());
										}
									}
								}
								writeInfoLogText("导入：" + sheetName0+"完成！");
							}

							String sheetName = "";
							XSSFSheet sheet = null;
							int numberOfSheets = workbook.getNumberOfSheets();
							if (numberOfSheets > 1) {
								sheet = workbook.getSheetAt(1);
								sheetName = sheet.getSheetName();
								System.out.println("sheetName = " + sheetName);
							}

							// 获取其他属性
							ArrayList<PAProductLineBOMBean> productList = new ArrayList<PAProductLineBOMBean>();
							ArrayList<PAProductLineBOMBean> variableList = new ArrayList<PAProductLineBOMBean>();
							LinkedHashMap<String, PAVariableBOMBean> variableMap = new LinkedHashMap<String, PAVariableBOMBean>();

							// 通过sheet名称判断导入类型
							if (containsName(sheetName, "PA BOM List")) {
								// 获取首选项
								String D9_SpringCloud_URL = TCUtil.getPreference(session,
										TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
								if (D9_SpringCloud_URL == null || "".equalsIgnoreCase(D9_SpringCloud_URL)) {
									writeDisposedText("");
									writeErrorLogText("请设置首选项 D9_SpringCloud_URL");
									writeInfoLogText("导入结束");
									return;
								}
								String url = D9_SpringCloud_URL + "/tc-integrate/agile/partManage/getPartInfo";

								// 新增 PA BOM
								writeDisposedText("");
								writeInfoLogText("导入 Matrix PA BOM List");
								try {
									 
									d9_CustomerModelNumber = "";
									d9_FoxconnModelNumber = "";
									
									getPAExcel(sheet, productList, variableList, variableMap, productLineItemUID, filePath);
								
									System.out.println("d9_CustomerModelNumber = "+d9_CustomerModelNumber);
									System.out.println("d9_FoxconnModelNumber = "+d9_FoxconnModelNumber);
									
									TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) app
											.getTargetComponent();
									if (productLineItemRev != null) {
										productLineItemRev.setProperty("d9_CustomerModelNumber",d9_CustomerModelNumber);
										productLineItemRev.setProperty("d9_FoxconnModelNumber",d9_FoxconnModelNumber);
									}
								
								} catch (Exception e2) {
									// TODO: handle exception
									e2.printStackTrace();
									writeErrorLogText(e2.getLocalizedMessage());

									writeDisposedText("");
									writeErrorLogText("请检查导入的Excel 模板！");
									writeErrorLogText("导入结束");
									return;
								}

								// 物料、替代料导入
								if (productList != null && productList.size() > 0) {
									writeDisposedText("");
									writeInfoLogText("清理历史导入数据");
									importCutBOMLine(app,productList);
									imputPAList(productList, url);
								}else {
									writeDisposedText("");
									writeInfoLogText("导入失败，获取Excel文件错误！");
									return;
								}

								writeDisposedText("");
								writeInfoLogText("物料对象导入完毕,开始导入变体!");
								// 变体导入
								if (variableMap != null && variableMap.size() > 0)
									importPAVariable(variableList, variableMap, app, url);
									
								writeInfoLogText("导入变体完毕!");

								writeDisposedText("");
								writeInfoLogText("导入Matrix PA BOM List结束！");
								writeInfoLogText("请检查提示信息，若无【ERROR】可关闭导入窗口！");
							} else {
								writeErrorLogText("导入模板问题，Sheet名称不包含 PA BOM List，请修改");
							}

						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							writeErrorLogText("导入结束，请检查【ERROR】!");
							return;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							writeErrorLogText("导入结束，请检查【ERROR】!");
							return;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							writeErrorLogText("导入结束，请检查【ERROR】!");
							return;
						} finally {
							TCUtil.closeBypass(session);
							if (input != null) {
								try {
									input.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (workbook != null) {
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

	/**
	 * 读取 Change Log Excel
	 * 
	 * @param sheet
	 * @param excelimgMap
	 * @throws IOException
	 */
	public ArrayList<PackingMatrixImputBean> getChangeLogExcel(String filePath) throws IOException {
		ArrayList<PackingMatrixImputBean> packingMatrixImputBean = new ArrayList<PackingMatrixImputBean>();
		ExcelReader reader = ExcelUtil.getReader(filePath, 0);
		List<List<Object>> readLists = reader.read();
		IoUtil.close(reader);

		PackingMatrixImputBean bean = null;
		for (int i = 2; i < readLists.size(); i++) {

			List<Object> list = readLists.get(i);
			if (list != null && list.size() >= 5) {
				// date
				Object date = list.get(0);

				if (date instanceof Date) {
					date = sdf.format(date);
				} else {
					date = list.get(0) == null ? "" : list.get(0);
				}
				// version
				Object version = list.get(1) == null ? "" : list.get(1);
				// desc
				Object desc = list.get(2) == null ? "" : list.get(2);
				// ecrNo
				Object ecrNo = list.get(3) == null ? "" : list.get(3);
				// ecrDate
				Object ecrDate = list.get(4) == null ? "" : list.get(4);

				bean = new PackingMatrixImputBean(date, version, desc, ecrNo, ecrDate);

				packingMatrixImputBean.add(bean);

			}
		}

		IoUtil.close(reader);
		return packingMatrixImputBean;
	}


	/**
	 * 读取PABOM Excel
	 * 
	 * @param sheet
	 * @param d9_FoxconnModelNumber 
	 * @param d9_CustomerModelNumber 
	 * @param excelimgMap
	 * @throws IOException
	 */
	public static void getPAExcel(XSSFSheet sheet, ArrayList<PAProductLineBOMBean> productList, 
			ArrayList<PAProductLineBOMBean> variableList,LinkedHashMap<String, PAVariableBOMBean> variableMap,
			String productLineItemUID, String filePath )
			throws IOException {

		ExcelReader reader = ExcelUtil.getReader(filePath, 1);
		List<List<Object>> readLists_0 = reader.read(0, 0, true);
		if (readLists_0 != null && readLists_0.size() > 0) {
			List<Object> list0 = readLists_0.get(0);
			if(list0 !=null && list0.size() > 0) {
				d9_CustomerModelNumber = list0.get(5).toString().trim();
				d9_FoxconnModelNumber = list0.get(18).toString().trim();
			}
		}
		
		
		PAProductLineBOMBean product = null;
		String item_id_parent = "";
		List<Object> list2 = null;
		List<List<Object>> readLists2 = reader.read(2, 2, true);
		if (readLists2 != null && readLists2.size() > 0) {
			list2 = readLists2.get(0);
			Object object2 = list2.get(6);
			if (!(object2.toString()).contains("用量")) {
				throw new IOException("Excel 模板存在问题，请检查是否有未删除的列！");
			}
		} else {
			throw new IOException("Excel 模板存在问题，请检查模板第3行！");
		}

		int lastRowNum = sheet.getLastRowNum();
		for (int i = 3; i < lastRowNum; i++) {
			List<List<Object>> readLists = reader.read(i, i, true);
			if (readLists != null && readLists.size() > 0) {
				List<Object> list = readLists.get(0);
				if(list.size() < 10) {
					continue;
				}
				// No
				Object no = list.get(0);
				// isSub
				String isSub = (String) list.get(1);
				if(isSub!=null && isSub.length() > 4) {
					continue;
				}
				// level
				Object level = list.get(2);
//				中文描述
				String chineseDescription = (String) list.get(3);
				// itemId
				String itemId = (String) list.get(4);
				if (itemId == null || "".equals(itemId)) {
					throw new IOException("Excel 模板存在问题，请检查模板第" + (i + 1) + "行，料号属性不能为空！");
				}
				
				// englishDescription
				String englishDescription = (String) list.get(5);
				// 单位 d9_Un
				String d9_Un = (String) list.get(7);
				// d9_SupplierZF
				String d9_SupplierZF = (String) list.get(8);
				//d9_ManufacturerID
				String d9_ManufacturerID = (String) list.get(9);
				//d9_ManufacturerPN
				String d9_ManufacturerPN = (String) list.get(10);
				// d9_AcknowledgementRev
				String d9_AcknowledgementRev = (String) list.get(11);
				// d9_SAPRev
				String d9_SAPRev = (String) list.get(12);
				// d9_MaterialType
				String d9_MaterialType = (String) list.get(13);
				// d9_MaterialGroup
				String d9_MaterialGroup = (String) list.get(14);
				//d9_ProcurementMethods
				String d9_ProcurementMethods = (String) list.get(16);
				
				// BOM属性
				// 用量 bl_quantity
				String bl_quantity = list.get(6).toString();
				// bl_occ_d9_IsNew
				String bl_occ_d9_IsNew = (String) list.get(15);
				// bl_occ_d9_Remark
				String bl_occ_d9_Remark = list.get(17).toString();
				//bl_occ_d9_BOMTemp
				String bl_occ_d9_BOMTemp = UUID.randomUUID().toString();
//				if(itemId.startsWith("7130")) {
//					bl_occ_d9_BOMTemp = "";
//				}
				
				
				int row = getMergedRegionFirstRow(sheet, i, 1);
				//System.out.println("itemId = " + itemId + ",i=" + i + ",row = " + row);

				if ("".equals("" + no) && !"".equals(itemId)) {
					item_id_parent = itemId;

				} else if ("1".equals("" + level)) {
					for (int j = 18; j < list.size(); j++) {
						String cell = (String) list.get(j);
						if (!"".equals(cell)) {

							PAVariableBOMBean child = new PAVariableBOMBean();
							child.setItemId(itemId);
							child.setEnglishDescription(englishDescription);
							child.setQty("" + bl_quantity);
							child.setRemark("" + bl_occ_d9_Remark);
							child.setIsNew(bl_occ_d9_IsNew);
							child.setD9_BOMTemp("");
							

							String d9_ShippingArea = "";
							Object object = list2.get(j);
							if (object != null) {
								d9_ShippingArea = object.toString();
								child.setShippingArea(d9_ShippingArea);
							}

							PAVariableBOMBean variableBOMBean_1 = variableMap.get("" + j);
							if (variableBOMBean_1 == null) {
								variableMap.put("" + j, child);
							}
							
							
							//需要创建的变体
							
							PAProductLineBOMBean vBean = new PAProductLineBOMBean();
							vBean.setItemId(itemId);
							vBean.setChineseDescription(chineseDescription);
							vBean.setEnglishDescription(englishDescription);
							vBean.setUn(d9_Un);
							vBean.setSupplierZF(d9_SupplierZF);
							vBean.setManufacturerID(d9_ManufacturerID);
							vBean.setManufacturerPN(d9_ManufacturerPN);
							vBean.setAcknowledgementRev(d9_AcknowledgementRev);
							vBean.setsAPRev(d9_SAPRev);
							vBean.setMaterialType(d9_MaterialType);
							vBean.setMaterialGroup(d9_MaterialGroup);
							vBean.setProcurementMethods(d9_ProcurementMethods);
							//使用备注 代替shippingArea
							vBean.setRemark(d9_ShippingArea);
							
							variableList.add(vBean);
						}
					}

				} else if ("2".equals("" + level)) {
					
					if (row != i) {
						// 替代料
						PAProductLineBOMBean productSub = new PAProductLineBOMBean();
						productSub.setItemId(itemId);
						productSub.setChineseDescription(chineseDescription);
						productSub.setEnglishDescription(englishDescription);
						productSub.setUn(d9_Un);
						productSub.setSupplierZF(d9_SupplierZF);
						productSub.setManufacturerID(d9_ManufacturerID);
						productSub.setManufacturerPN(d9_ManufacturerPN);
						productSub.setAcknowledgementRev(d9_AcknowledgementRev);
						productSub.setsAPRev(d9_SAPRev);
						productSub.setMaterialType(d9_MaterialType);
						productSub.setMaterialGroup(d9_MaterialGroup);
						productSub.setProcurementMethods(d9_ProcurementMethods);
						
						productSub.setProductLineItemUID(productLineItemUID);
						
						
						if (product != null) {
							List<PAProductLineBOMBean> subList = product.getSubList();
							if (subList != null && subList.size() > 0) {
								subList.add(productSub);
								product.setSubList(subList);
							} else {
								subList = new ArrayList<PAProductLineBOMBean>();
								subList.add(productSub);
								product.setSubList(subList);
							}

						} else {
							System.out.println("=========error1=======");
						}

					} else {
						product = new PAProductLineBOMBean();
						
						product.setItemId(itemId);
						product.setChineseDescription(chineseDescription);
						product.setEnglishDescription(englishDescription);
						product.setUn(d9_Un);
						product.setSupplierZF(d9_SupplierZF);
						product.setManufacturerID(d9_ManufacturerID);
						product.setManufacturerPN(d9_ManufacturerPN);
						product.setAcknowledgementRev(d9_AcknowledgementRev);
						product.setsAPRev(d9_SAPRev);
						product.setMaterialType(d9_MaterialType);
						product.setMaterialGroup(d9_MaterialGroup);
						product.setProcurementMethods(d9_ProcurementMethods);
						product.setProductLineItemUID(productLineItemUID);
						
						product.setQty(bl_quantity);
						product.setIsNew(bl_occ_d9_IsNew);
						product.setRemark(bl_occ_d9_Remark);
						if (itemId.startsWith("7130")) {
							product.setD9_BOMTemp("");
						} else {
							product.setD9_BOMTemp(bl_occ_d9_BOMTemp);
						}
						
						productList.add(product);

					}

					for (int j = 18; j < list.size(); j++) {
						String cell = (String) list.get(j);
						if (itemId.startsWith("7130")) {
							if (cell != null && "○優選".equals(cell)) {

								PAVariableBOMBean child = new PAVariableBOMBean();
								child.setItemId(itemId);
								child.setQty(bl_quantity);
								child.setIsNew(bl_occ_d9_IsNew);
								child.setRemark(bl_occ_d9_Remark);
								//child.setD9_BOMTemp(bl_occ_d9_BOMTemp);

								PAVariableBOMBean variable = variableMap.get("" + j);
								if (variable != null) {
									List<PAVariableBOMBean> getchilds = variable.getChild();
									if (getchilds == null) {
										getchilds = new ArrayList<PAVariableBOMBean>();
										getchilds.add(child);
										variable.setChild(getchilds);
									} else {
										PAVariableBOMBean getchild = null;
										for (PAVariableBOMBean child_v : getchilds) {
											String itemId2 = child_v.getItemId();
											if (itemId2.startsWith("7130")) {
												getchild = child_v;
												break;
											}
										}
										if (getchild == null) {
											getchilds.add(child);
											variable.setChild(getchilds);
										} else {
											getchild.setItemId(itemId);
											getchild.setQty(bl_quantity);
											getchild.setIsNew(bl_occ_d9_IsNew);
											getchild.setRemark(bl_occ_d9_Remark);
											//getchild.setD9_BOMTemp(bl_occ_d9_BOMTemp);
										}
									}
								}

							} else if (cell != null && "○".equals(cell)) {
								PAVariableBOMBean child_sub = new PAVariableBOMBean();
								child_sub.setItemId(itemId);
								child_sub.setQty(bl_quantity);
								child_sub.setIsNew(bl_occ_d9_IsNew);
								child_sub.setRemark(bl_occ_d9_Remark);
								//child_sub.setD9_BOMTemp(bl_occ_d9_BOMTemp);
								
								PAVariableBOMBean variable = variableMap.get("" + j);
								if (variable != null) {
									List<PAVariableBOMBean> getchilds = variable.getChild();
									if (getchilds == null) {
										getchilds = new ArrayList<PAVariableBOMBean>();
										PAVariableBOMBean getchild = new PAVariableBOMBean();
										getchild.setItemId("7130*");

										ArrayList<PAVariableBOMBean> subList = new ArrayList<PAVariableBOMBean>();
										subList.add(child_sub);
										getchild.setSubList(subList);

										getchilds.add(getchild);
										variable.setChild(getchilds);
									} else {
										PAVariableBOMBean getchild = null;
										for (PAVariableBOMBean child_v : getchilds) {
											String itemId2 = child_v.getItemId();
											if (itemId2.startsWith("7130")) {
												getchild = child_v;
												break;
											}
										}

										if (getchild == null) {
											getchild = new PAVariableBOMBean();
											getchild.setItemId("7130*");

											ArrayList<PAVariableBOMBean> subList = new ArrayList<PAVariableBOMBean>();
											subList.add(child_sub);
											getchild.setSubList(subList);

											getchilds.add(getchild);
											variable.setChild(getchilds);

										} else {
											List<PAVariableBOMBean> subList = getchild.getSubList();
											if (subList != null && subList.size() > 0) {
												subList.add(child_sub);
											} else {
												subList = new ArrayList<PAVariableBOMBean>();
												subList.add(child_sub);
												getchild.setSubList(subList);
											}
										}
									}
								}
							}

						} else {
							if (cell != null && "○".equals(cell)) {
								PAVariableBOMBean child = new PAVariableBOMBean();
								child.setItemId(itemId);
								child.setQty(bl_quantity);
								child.setIsNew(bl_occ_d9_IsNew);
								child.setRemark(bl_occ_d9_Remark);
								child.setD9_BOMTemp(bl_occ_d9_BOMTemp);

								// 替代料
								if (row != i) {
									child.setIsSub(true);
								} else {
									child.setIsSub(false);
								}

								PAVariableBOMBean variable = variableMap.get("" + j);
								if (variable != null) {
									List<PAVariableBOMBean> getchild = variable.getChild();
									if (getchild == null) {
										getchild = new ArrayList<PAVariableBOMBean>();
										getchild.add(child);
										variable.setChild(getchild);
									} else {
										getchild.add(child);
										variable.setChild(getchild);
									}
								}

							}
						}
					}

				} else if ("3".equals("" + level)) {

					for (PAProductLineBOMBean productBean : productList) {
						if (item_id_parent.equals(productBean.getItemId())) {
							
							if (row != i) {
								// 替代料
								PAProductLineBOMBean productSub = new PAProductLineBOMBean();
								
								productSub.setItemId(itemId);
								productSub.setChineseDescription(chineseDescription);
								productSub.setEnglishDescription(englishDescription);
								productSub.setUn(d9_Un);
								productSub.setSupplierZF(d9_SupplierZF);
								productSub.setManufacturerID(d9_ManufacturerID);
								productSub.setManufacturerPN(d9_ManufacturerPN);
								productSub.setAcknowledgementRev(d9_AcknowledgementRev);
								productSub.setsAPRev(d9_SAPRev);
								productSub.setMaterialType(d9_MaterialType);
								productSub.setMaterialGroup(d9_MaterialGroup);
								productSub.setProcurementMethods(d9_ProcurementMethods);
								productSub.setProductLineItemUID(productLineItemUID);
								
								productSub.setQty(bl_quantity);
								productSub.setIsNew(bl_occ_d9_IsNew);
								productSub.setRemark(bl_occ_d9_Remark);
								productSub.setD9_BOMTemp(bl_occ_d9_BOMTemp);


								if (product != null) {
									List<PAProductLineBOMBean> subList = product.getSubList();
									if (subList != null && subList.size() > 0) {
										subList.add(productSub);
										product.setSubList(subList);
									} else {
										subList = new ArrayList<PAProductLineBOMBean>();
										subList.add(productSub);
										product.setSubList(subList);
									}

								} else {
									System.out.println("=========error2=======");
								}

							} else {
								product = new PAProductLineBOMBean();
								product.setItemId(itemId);
								product.setChineseDescription(chineseDescription);
								product.setEnglishDescription(englishDescription);
								product.setUn(d9_Un);
								product.setSupplierZF(d9_SupplierZF);
								product.setManufacturerID(d9_ManufacturerID);
								product.setManufacturerPN(d9_ManufacturerPN);
								product.setAcknowledgementRev(d9_AcknowledgementRev);
								product.setsAPRev(d9_SAPRev);
								product.setMaterialType(d9_MaterialType);
								product.setMaterialGroup(d9_MaterialGroup);
								product.setProcurementMethods(d9_ProcurementMethods);
								product.setProductLineItemUID(productLineItemUID);
								
								product.setQty(bl_quantity);
								product.setIsNew(bl_occ_d9_IsNew);
								product.setRemark(bl_occ_d9_Remark);
								product.setD9_BOMTemp(bl_occ_d9_BOMTemp);
								

								List<PAProductLineBOMBean> child = productBean.getChild();
								if (child != null && child.size() > 0) {
									child.add(product);
									productBean.setChild(child);
								} else {
									child = new ArrayList<PAProductLineBOMBean>();
									child.add(product);
									productBean.setChild(child);
								}

							}
						}
					}
				}
			}
		}

		IoUtil.close(reader);
	}

	/**
	 * 导入PA BOM前 清空数据
	 * 
	 * @param excelList
	 * @param app
	 * @param productList 
	 */
	public void importCutBOMLine(AbstractAIFUIApplication app, ArrayList<PAProductLineBOMBean> productList) {
		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) app.getTargetComponent();
			bomwindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine bomline = TCUtil.getTopBomline(bomwindow, productLineItemRev);
			AIFComponentContext[] childrens = bomline.getChildren();

			if (childrens.length > 0) {
				for (AIFComponentContext children : childrens) {
					TCComponentBOMLine childrenBomline = (TCComponentBOMLine) children.getComponent();
					
					String item_id0 = childrenBomline.getProperty("bl_item_item_id");
					for (PAProductLineBOMBean product : productList) {
						String itemId = product.getItemId();
						List<PAProductLineBOMBean> child = product.getChild();
						if(item_id0.equals(itemId) && (child != null && child.size() > 0)) {
							
							AIFComponentContext[] childrens1 = childrenBomline.getChildren();
							for (AIFComponentContext children1 : childrens1) {
								TCComponentBOMLine component = (TCComponentBOMLine) children1.getComponent();

								component.cut();
								bomwindow.save();
							}
							break;
						}
						
					}

					childrenBomline.cut();
				}
				bomwindow.save();
			}

			
			
			
			//变体数据 清理
			AIFComponentContext[] hasVariantHolder_RELs = productLineItemRev.getRelated("D9_HasVariantHolder_REL");
			for (AIFComponentContext hasVariantHolder_REL : hasVariantHolder_RELs) {
				TCComponentItemRevision component = (TCComponentItemRevision) hasVariantHolder_REL.getComponent();

				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine bomline1 = TCUtil.getTopBomline(bomwindow, component);
				AIFComponentContext[] childrens1 = bomline1.getChildren();
				if (childrens1.length > 0) {
					for (AIFComponentContext children : childrens1) {
						TCComponentBOMLine bom0Line = (TCComponentBOMLine) children.getComponent();
						TCComponentItemRevision item0Revision = bom0Line.getItemRevision();
						String item_id0 = item0Revision.getProperty("item_id");
						if (item_id0.startsWith("7130") && TCUtil.isReleased(item0Revision)) {
							bom0Line.cut();
							continue;
						}

						AIFComponentContext[] childrens_1 = bom0Line.getChildren();
						if (childrens_1.length > 0) {
							for (AIFComponentContext children_1 : childrens_1) {
								TCComponentBOMLine bom1Line = (TCComponentBOMLine) children_1.getComponent();
//								TCComponentItemRevision item1Revision = bom1Line.getItemRevision();
//								String item_id1 = item1Revision.getProperty("item_id");

//								if (item_id1.startsWith("7130") && TCUtil.isReleased(item1Revision)) {
//									bom1Line.cut();
//									continue;
//								}
//
//								AIFComponentContext[] childrens_2 = bom1Line.getChildren();
//								if (childrens_2.length > 0) {
//									for (AIFComponentContext children_2 : childrens_2) {
//										TCComponentBOMLine bom2Line = (TCComponentBOMLine) children_2.getComponent();
//
//										bom2Line.cut();
//									}
//									bomwindow.save();
//								}
								bom1Line.cut();

							}
							bomwindow.save();
						}
						bom0Line.cut();
					}
					bomwindow.save();
				}
			}
			productLineItemRev.setRelated("D9_HasVariantHolder_REL", new TCComponent[] {});
			productLineItemRev.setRelated("D9_HasVariants_REL", new TCComponent[] {});

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

	/**
	 * 导入PA
	 * 
	 * @param data
	 * @param url
	 * @return
	 */
	public AjaxResult imputPA(String data, String url) {
		try {
			Gson gson = new Gson();
			PAProductLineBOMBean rootBean = gson.fromJson(data, PAProductLineBOMBean.class);
			String itemid = rootBean.getItemId();
			String name = rootBean.getEnglishDescription();

			String qty = rootBean.getQty();
			String uom_tag = "";
			if (qty != null && qty.contains(".")) {
				uom_tag = "Other";
			}

			String itemTypeName = "D9_CommonPart";
			if (itemid.startsWith("7130")) {
				itemTypeName = "D9_VirtualPart";
			}
			
			
			String[] proname = new String[] { "d9_ChineseDescription","d9_EnglishDescription", "d9_Un",
					"d9_SupplierZF","d9_ManufacturerID","d9_ManufacturerPN","d9_AcknowledgementRev",
					"d9_SAPRev","d9_MaterialType","d9_MaterialGroup","d9_ProcurementMethods"};
			String[] provalue = new String[] { rootBean.getChineseDescription(),rootBean.getEnglishDescription(),rootBean.getUn(),
					rootBean.getSupplierZF(),rootBean.getManufacturerID(),rootBean.getManufacturerPN(),rootBean.getAcknowledgementRev(),
					rootBean.getsAPRev(),rootBean.getMaterialType(),rootBean.getMaterialGroup(),rootBean.getProcurementMethods() };

			TCComponentItemRevision latestItemRevision = null;
			if (itemid.equalsIgnoreCase("NA") || itemid.equalsIgnoreCase("N/A")) {
				TCComponentItem newItem = (TCComponentItem) createCom(session, itemTypeName, "", name, "", null);
				latestItemRevision = newItem.getLatestItemRevision();
				
				latestItemRevision.setProperties(proname, provalue);
				latestItemRevision.setProperty("d9_ActualUserID", d9_ActualUserID);
				
				newItem.setProperty("uom_tag", uom_tag);
			} else {
				if (!getRightStr(itemid)) {
					return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "输入的ID不正确");
				}

				latestItemRevision = queryItemID(itemid);
				if (latestItemRevision != null) {
					if (TCUtil.checkOwninguserisWrite(session, latestItemRevision)) {
						latestItemRevision.setProperties(proname, provalue);
						latestItemRevision.setProperty("d9_ActualUserID", d9_ActualUserID);
					}
				} else {
					// 新增在SapPnms系统中查询
					latestItemRevision = querySapPnms(url, itemid);
					if (latestItemRevision == null) {
						TCComponentItem newItem = (TCComponentItem) createCom(session, itemTypeName, itemid, name, "",
								null);
						latestItemRevision = newItem.getLatestItemRevision();

						latestItemRevision.setProperties(proname, provalue);
						latestItemRevision.setProperty("d9_ActualUserID", d9_ActualUserID);
						latestItemRevision.getItem().setProperty("uom_tag", uom_tag);
					}
				}
			}

			rootBean.setItemRevision(latestItemRevision.getProperty("item_revision_id"));
			rootBean.setItemId(latestItemRevision.getProperty("item_id"));
			rootBean.setItemRevUid(latestItemRevision.getUid());
			rootBean.setReleased(TCUtil.isReleased(latestItemRevision));

			String jsons = JSONArray.toJSONString(rootBean);
			System.out.print("json ==" + jsons);
			return AjaxResult.success("执行成功", rootBean);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		}
	}

	/**
	 * 导入 PA 替代料(结构)
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult imputPABOMSub(String data) {

		TCComponentBOMWindow bomwindow = null;
		try {
			Gson gson = new Gson();
			List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
			}.getType());

			if (rootBeanList != null && rootBeanList.size() > 0) {
				String productLineItemUID = rootBeanList.get(0).getProductLineItemUID();
				TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);
				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topbom = TCUtil.getTopBomline(bomwindow, roductLineItemRev);
				// 解包
				topbom.refresh();
				AIFComponentContext[] childrens_Packed = topbom.getChildren();
				for (AIFComponentContext aifchildren : childrens_Packed) {
					TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
					if (children.isPacked()) {
						children.unpack();
						children.refresh();
					}
				}

				for (int k = 0; k < rootBeanList.size(); k++) {
					PAProductLineBOMBean rootBean = rootBeanList.get(k);
					String bomlineuid = rootBean.getBomLineUid();
					System.out.println("bomlineuid = " + bomlineuid);
					String itemId = rootBean.getItemId();

					topbom.refresh();
					AIFComponentContext[] childrens = topbom.getChildren();
					for (AIFComponentContext aifchildren : childrens) {
						TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();

						String childrenuid = children.getProperty("bl_occ_fnd0objectId");
						System.out.println("childrenuid = " + childrenuid);
						if (childrenuid.equals(bomlineuid)) {
							// 添加替代料
							TCComponentItemRevision queryItemRev = queryItemID(itemId);
							children.add(queryItemRev.getItem(), queryItemRev, null, true);
							bomwindow.save();
							break;
						}
					}
				}
			}

			String jsons = JSONArray.toJSONString(rootBeanList);
			System.out.print("json ==" + jsons);
			return AjaxResult.success("执行成功", rootBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
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
	 * 导入 PA(结构)
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult imputPABOM(String data) {
		TCComponentBOMWindow bomwindow = null;
		try {
			Gson gson = new Gson();
			List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
			}.getType());
			if (rootBeanList != null && rootBeanList.size() > 0) {
				String productLineItemUID = rootBeanList.get(0).getProductLineItemUID();
				TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);
				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topbom = TCUtil.getTopBomline(bomwindow, roductLineItemRev);

				for (int k = 0; k < rootBeanList.size(); k++) {
					TCComponentBOMLine bomline;
					PAProductLineBOMBean rootBean = rootBeanList.get(k);
					String item_id = rootBean.getItemId();
					
					String[] bomvalue = new String[] { rootBean.getIsNew(),
							rootBean.getRemark(), rootBean.getD9_BOMTemp()};
					String[] bomname = new String[] { "bl_occ_d9_IsNew",
							"bl_occ_d9_Remark", "bl_occ_d9_BOMTemp" };

					TCComponentItemRevision itemrevision = queryItemID(item_id);

					if (itemrevision != null) {
						bomline = addbomline(bomwindow, topbom, itemrevision);

						// 设置属性
						String qty = rootBean.getQty();
						if (qty != null && !"".equals(qty.trim())) {
							qty = getQty(qty);
						}
						if (qty.contains(".")) {
							bomline.setProperty("bl_uom", "Other");
						}
						bomline.setProperties(bomname, bomvalue);
						bomline.setProperty("bl_quantity", qty);

						String bomlineuid = bomline.getProperty("bl_occ_fnd0objectId");
						rootBean.setBomLineUid(bomlineuid);
					}
				}

				bomwindow.refresh();
				roductLineItemRev.refresh();
			}

			return AjaxResult.success("执行成功", rootBeanList);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
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

	public void imputPAList(List<PAProductLineBOMBean> excelList, String url) {
		if (excelList != null && excelList.size() > 0) {
			for (PAProductLineBOMBean rootBean : excelList) {
				String uid = "";

				String itemId = rootBean.getItemId();

				writeDisposedText("");
				writeInfoLogText("开始创建物料：" + itemId);
				String jsonString = JSONArray.toJSONString(rootBean);
				AjaxResult addMatrixParts = imputPA(jsonString, url);

				Object CODE_TAG_item = addMatrixParts.get(AjaxResult.CODE_TAG);
				if ("0000".equals(CODE_TAG_item)) {
					rootBean = (PAProductLineBOMBean) addMatrixParts.get(AjaxResult.DATA_TAG);
					writeInfoLogText("物料：" + rootBean.getItemId() + "，创建成功！");
					writeInfoLogText("开始搭建物料结构：" + rootBean.getItemId());

					List<PAProductLineBOMBean> rootBeanList = new ArrayList<PAProductLineBOMBean>();
					rootBeanList.add(rootBean);
					AjaxResult addBOMMatrixParts = imputPABOM(JSONArray.toJSONString(rootBeanList));

					Object CODE_TAG = addBOMMatrixParts.get(AjaxResult.CODE_TAG);
					if ("0000".equals(CODE_TAG)) {
						writeInfoLogText("物料对象：" + rootBean.getItemId() + "，结构搭建成功！");
						List<PAProductLineBOMBean> rootBeanSub_1 = (List<PAProductLineBOMBean>) addBOMMatrixParts.get(AjaxResult.DATA_TAG);
						if (rootBeanSub_1 != null && rootBeanSub_1.size() > 0) {
							uid = rootBeanSub_1.get(0).getBomLineUid();
						}
					} else {
						Object MSG_TAG = addBOMMatrixParts.get(AjaxResult.MSG_TAG);
						writeErrorLogText("" + MSG_TAG);
					}
				} else {
					Object MSG_TAG = addMatrixParts.get(AjaxResult.MSG_TAG);
					writeErrorLogText("" + MSG_TAG);
					continue;
				}

				// boolean released = rootBean.isReleased();
				// if (!released) {
				List<PAProductLineBOMBean> subList = rootBean.getSubList();
				if (!"".equals(uid) && subList != null && subList.size() > 0) {
					for (PAProductLineBOMBean subProduct : subList) {

						writeInfoLogText("开始创建替代料：" + itemId);
						String jsonSubProduct = JSONArray.toJSONString(subProduct);
						AjaxResult imputAddMatrixParts = imputPA(jsonSubProduct, url);

						Object CODE_TAG_sub = imputAddMatrixParts.get(AjaxResult.CODE_TAG);
						if ("0000".equals(CODE_TAG_sub)) {
							PAProductLineBOMBean subRootBean = (PAProductLineBOMBean) imputAddMatrixParts
									.get(AjaxResult.DATA_TAG);
							writeInfoLogText("替代料：" + rootBean.getItemId() + "，创建成功！");
							writeInfoLogText("开始搭建替代料结构：" + rootBean.getItemId());

							subRootBean.setBomLineUid(uid);
							List<PAProductLineBOMBean> rootBeanListSub = new ArrayList<PAProductLineBOMBean>();
							rootBeanListSub.add(subRootBean);
							AjaxResult imputAddSubMatrixParts = imputPABOMSub(JSONArray.toJSONString(rootBeanListSub));

							Object CODE_TAG = imputAddSubMatrixParts.get(AjaxResult.CODE_TAG);
							if ("0000".equals(CODE_TAG)) {
								writeInfoLogText("替代料对象：" + subRootBean.getItemId() + "，结构搭建成功！");
							} else {
								Object MSG_TAG = imputAddSubMatrixParts.get(AjaxResult.MSG_TAG);
								writeErrorLogText("" + MSG_TAG);
							}
						} else {
							Object MSG_TAG = imputAddMatrixParts.get(AjaxResult.MSG_TAG);
							writeErrorLogText("" + MSG_TAG);
						}
					}
				}

				List<PAProductLineBOMBean> childList = rootBean.getChild();
				if (childList != null && childList.size() > 0) {
					for (PAProductLineBOMBean child : childList) {
						List<PAProductLineBOMBean> subList2 = child.getSubList();

						if (subList2 != null && subList2.size() > 0) {
							for (PAProductLineBOMBean sub : subList2) {
								sub.setProductLineItemUID(rootBean.getItemRevUid());
							}
						}

						child.setProductLineItemUID(rootBean.getItemRevUid());
					}
					imputPAList(childList, url);
				}
				// }
			}
		}
	}

	public void importPAVariable(ArrayList<PAProductLineBOMBean> variableList, LinkedHashMap<String, PAVariableBOMBean> variableMap, AbstractAIFUIApplication app,
			String url) throws Exception {
		TCComponentBOMWindow bomwindow = null;
		try {
			
			TCComponentItemRevision itemRev = (TCComponentItemRevision) app.getTargetComponent();
			String matrixUid = itemRev.getUid();
			
			//创建变体
			ArrayList<String> revUids = new ArrayList<String>();
			if(variableList!=null && variableList.size() > 0) {
				for (int i = 0; i < variableList.size(); i++) {
					PAProductLineBOMBean variable = variableList.get(i);
					String itemId = variable.getItemId();
					String englishDescription = variable.getEnglishDescription();
					String chineseDescription = variable.getChineseDescription();
					String un = variable.getUn();
					String supplierZF = variable.getSupplierZF();
					String manufacturerID = variable.getManufacturerID();
					String manufacturerPN = variable.getManufacturerPN();
					String acknowledgementRev = variable.getAcknowledgementRev();
					String getsAPRev = variable.getsAPRev();
					String materialType = variable.getMaterialType();
					String materialGroup = variable.getMaterialGroup();
					String procurementMethods = variable.getProcurementMethods();
					//使用Remark 代替shippingArea
					String shippingArea = variable.getRemark();
					
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("itemId", itemId);
					jsonObj.put("englishDescription", englishDescription);
					jsonObj.put("chineseDescription", chineseDescription);
					jsonObj.put("un", un);
					jsonObj.put("supplierZF", supplierZF);
					jsonObj.put("manufacturerID", manufacturerID);
					jsonObj.put("manufacturerPN", manufacturerPN);
					jsonObj.put("acknowledgementRev", acknowledgementRev);
					jsonObj.put("getsAPRev", getsAPRev);
					jsonObj.put("materialType", materialType);
					jsonObj.put("materialGroup", materialGroup);
					jsonObj.put("procurementMethods", procurementMethods);
					jsonObj.put("shippingArea", shippingArea);
					
					
					AjaxResult add = imputAdd(jsonObj.toJSONString(), url);
					Object CODE_TAG = add.get(AjaxResult.CODE_TAG);
					if ("0000".equals(CODE_TAG)) {
						String itemId2 = (String) add.get("itemId");
						writeInfoLogText("变体Item对象：" + itemId2 + "，创建成功！");
						String itemRevUid = (String) add.get("itemRevUid");
						revUids.add(itemRevUid);

					} else {
						Object MSG_TAG = add.get(AjaxResult.MSG_TAG);
						writeErrorLogText("" + MSG_TAG);
						continue;
					}
					
				}
				
				writeInfoLogText("变体对象创建完成。");

				writeDisposedText("");
				writeInfoLogText("开始搭建结构...");
				
				if (revUids != null && revUids.size() > 0) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("matrixUid", matrixUid);
					jsonObj.put("variableUidList", revUids);

					System.out.println("jsonObj.toJSONString() = " + jsonObj.toJSONString());

					AjaxResult bind = new PAVariablesBOMService(app).imputBind(jsonObj.toJSONString());
					Object CODE_TAG = bind.get(AjaxResult.CODE_TAG);

					if ("0000".equals(CODE_TAG)) {
						writeInfoLogText("结构搭建完成。");

						writeDisposedText("");
						writeInfoLogText("开始保存结构数据,请等待完成...");
					} else {
						Object MSG_TAG = bind.get(AjaxResult.MSG_TAG);
						writeErrorLogText("" + MSG_TAG);
					}
				}
			}
			
			// 搭建变体结构
			for (Entry<String, PAVariableBOMBean> map : variableMap.entrySet()) {

				PAVariableBOMBean value = map.getValue();
				String itemId = value.getItemId();

				TCComponentItemRevision variableItemRev = queryItemID(itemId);

				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topLine = TCUtil.getTopBomline(bomwindow, variableItemRev);

				List<PAVariableBOMBean> childs = value.getChild();
				if (childs != null && childs.size() > 0) {
					TCComponentBOMLine bomline_1 = null;
					for (PAVariableBOMBean child : childs) {
						String childsItemId = child.getItemId();
						if (childsItemId.startsWith("7130")) {
							
							String qty = child.getQty();
							if (qty != null && !"".equals(qty.trim())) {
								qty = getQty(qty);
							}
							String bl_occ_d9_IsNew = child.getIsNew();
							String bl_occ_d9_Remark = child.getRemark();
							String d9_BOMTemp = child.getD9_BOMTemp();

							TCComponentItemRevision childRev = queryItemID(childsItemId);
							TCComponentBOMLine bomline = isContains(topLine, childRev.getItem(),false);
							if(bomline == null){
								bomline = topLine.add(childRev.getItem(), childRev, null, false);
							} 
							
							if (qty.contains(".")) {
								bomline.setProperty("bl_uom", "Other");
							}
							bomline.setProperty("bl_quantity", "" + qty);
							bomline.setProperty("bl_occ_d9_IsNew", "" + bl_occ_d9_IsNew);
							bomline.setProperty("bl_occ_d9_Remark", "" + bl_occ_d9_Remark);
							bomline.setProperty("bl_occ_d9_BOMTemp", "" + d9_BOMTemp);

							bomline_1 = bomline;
							bomwindow.save();

							List<PAVariableBOMBean> subLists = child.getSubList();
							if (subLists != null && subLists.size() > 0) {
								for (PAVariableBOMBean subList : subLists) {
									String subItemId = subList.getItemId();
									if (subItemId.startsWith("7130") && bomline_1 != null) {
										TCComponentItemRevision subchildRev = queryItemID(subItemId);
										
										TCComponentBOMLine bomline_suBomLine = isContains(bomline_1, subchildRev.getItem(),true);
										if(bomline_suBomLine == null){
											bomline_1.add(subchildRev.getItem(), subchildRev, null, true);
										} 
										
										bomwindow.save();
									}
								}
							}

							bomwindow.save();

						} else {
							String qty = child.getQty();

							if (qty != null && !"".equals(qty.trim())) {
								qty = getQty(qty);
							}
							String bl_occ_d9_IsNew = child.getIsNew();
							String bl_occ_d9_Remark = child.getRemark();
							String d9_BOMTemp = child.getD9_BOMTemp();
							
							boolean sub = child.getIsSub();
							if (!sub) {
								TCComponentItemRevision childRev = queryItemID(childsItemId);
								
								TCComponentBOMLine bomline = isContains(topLine, childRev.getItem(), false);
								if(bomline == null){
									bomline = topLine.add(childRev.getItem(), childRev, null, false);
								} 

								if (qty.contains(".")) {
									bomline.setProperty("bl_uom", "Other");
								}
								bomline.setProperty("bl_quantity", "" + qty);
								bomline.setProperty("bl_occ_d9_IsNew", "" + bl_occ_d9_IsNew);
								bomline.setProperty("bl_occ_d9_Remark", "" + bl_occ_d9_Remark);
								bomline.setProperty("bl_occ_d9_BOMTemp", "" + d9_BOMTemp);

								bomline_1 = bomline;
							} else {
								if (bomline_1 != null) {
									TCComponentItemRevision subchildRev = queryItemID(childsItemId);
									
									TCComponentBOMLine bomline_suBomLine = isContains(bomline_1, subchildRev.getItem(), true);
									if(bomline_suBomLine == null){
										System.out.println(bomline_1.getProperty("bl_item_item_id"));
										System.out.println(subchildRev.getProperty("item_id"));
										
										bomline_1.add(subchildRev.getItem(), subchildRev, null, true);
									}
								}
							}

							bomwindow.save();
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			writeErrorLogText("" + e.getLocalizedMessage());
			throw new Exception(e.getLocalizedMessage());
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

	public AjaxResult imputAdd(String request, String url) throws Exception {
		try {
			JSONObject parseObject = JSONObject.parseObject(request);
			
			String itemid = parseObject.getString("itemId");
			String englishDescription = parseObject.getString("englishDescription");
			String chineseDescription = parseObject.getString("chineseDescription");
			String un = parseObject.getString("un");
			String supplierZF = parseObject.getString("supplierZF");
			String manufacturerID = parseObject.getString("manufacturerID");
			String manufacturerPN = parseObject.getString("manufacturerPN");
			String acknowledgementRev = parseObject.getString("acknowledgementRev");
			String getsAPRev = parseObject.getString("getsAPRev");
			String materialType = parseObject.getString("materialType");
			String materialGroup = parseObject.getString("materialGroup");
			String procurementMethods = parseObject.getString("procurementMethods");
			String shippingArea = parseObject.getString("shippingArea");
			
			String itemTypeName = "D9_CommonPart";
			if (itemid.startsWith("7130")) {
				itemTypeName = "D9_VirtualPart";
			}
			
			String[] proname = new String[] { "d9_ChineseDescription","d9_EnglishDescription", "d9_Un",
					"d9_SupplierZF","d9_ManufacturerID","d9_ManufacturerPN","d9_AcknowledgementRev",
					"d9_SAPRev","d9_MaterialType","d9_MaterialGroup","d9_ProcurementMethods"};
			String[] provalue = new String[] {chineseDescription,englishDescription,un,
					supplierZF,manufacturerID,manufacturerPN,acknowledgementRev,
					getsAPRev,materialType,materialGroup,procurementMethods };

			TCComponentItemRevision latestItemRevision = queryItemID(itemid);
			if (latestItemRevision == null) {

				// 新增在SapPnms系统中查询
				latestItemRevision = querySapPnms(url, itemid);
				if (latestItemRevision == null) {
					TCComponentItem newItem = (TCComponentItem) createCom(session, itemTypeName, itemid, englishDescription, "",
							null);
					latestItemRevision = newItem.getLatestItemRevision();
					latestItemRevision.setProperties(proname, provalue);
				}
			}
			
			latestItemRevision.setProperty("d9_ShippingArea", shippingArea);
			latestItemRevision.setProperty("d9_ActualUserID", d9_ActualUserID);

			AjaxResult success = AjaxResult.success();
			success.put("itemId", latestItemRevision.getProperty("item_id"));
			success.put("itemRevUid", latestItemRevision.getUid());

			return success;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
			//return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}
	
	// 避免重复导入
	public TCComponentBOMLine isContains(TCComponentBOMLine topLine, TCComponentItem item, boolean is_sub) throws TCException {
		if(is_sub) {
			TCComponentBOMLine[] children_subs = topLine.listSubstitutes();
			if(children_subs != null && children_subs.length > 0) {
				for (TCComponentBOMLine children : children_subs) {
					TCComponentItem item2 = children.getItem();
					if(item == item2) {
						return children;
					}
				}
			}
		}else {
			AIFComponentContext[] AIFComs = topLine.getChildren();
			if(AIFComs != null && AIFComs.length > 0) {
				for (AIFComponentContext AIFCom : AIFComs) {
					TCComponentBOMLine children = (TCComponentBOMLine) AIFCom.getComponent();
					TCComponentItem item2 = children.getItem();
					if(item == item2) {
						return children;
					}
				}
			}
		}
		
		return null;
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
				if (!logText.isDisposed()) {
					logText.insert("【INFO】 " + message + "\n");
				}
			}
		});
	}

	/**
	 * 记录错误日志信息
	 * 
	 * @param message
	 */
	public void writeErrorLogText(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (!logText.isDisposed()) {
					logText.insert("【ERROR】 " + message + "\n");
				}
			}
		});
	}

	/**
	 * 按单元格类型取值
	 * 
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
				// 转为整型
				int round = (int) Math.round(numericCellValue);
				value = "" + round;
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
	 * 获取合并起始位置
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static int getMergedRegionFirstRow(XSSFSheet sheet, int row, int column) {
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
					return firstRow;
				}
			}
		}
		XSSFRow fRow = sheet.getRow(row);
		XSSFCell fCell = fRow.getCell(column);
		return row;
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
	 * 打开文件选择器
	 * 
	 * @return 文件路径
	 */
	public static String openFileChooser(Shell shell) {
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setFilterPath(TCUtil.getSystemDesktop());
		fileDialog.setFilterNames(new String[] { "Microsoft Excel(*.xlsx)" });
		fileDialog.setFilterExtensions(new String[] { "*.xlsx" });
		return fileDialog.open();
	}

	public TCComponentItemRevision craeteRev(HHPNPojo hhpnpojo) throws TCException {
		// 创建对象并返回
		TCComponentItemRevision latestItemRevision = null;

		String itemId = hhpnpojo.getItemId();
		String itemType = "D9_CommonPart";
		if (itemId.startsWith("7130")) {
			itemType = "D9_VirtualPart​";
		}

		String descr = hhpnpojo.getDescr();
		if (descr == null)
			descr = "";
		String cnDescr = hhpnpojo.getCnDescr();
		if (cnDescr == null)
			cnDescr = "";
		String enDescr = hhpnpojo.getEnDescr();
		if (enDescr == null)
			enDescr = "";
		String manufacturer = hhpnpojo.getMfg();
		if (manufacturer == null)
			manufacturer = "";
		String manufacturerPN = hhpnpojo.getMfgPN();
		if (manufacturerPN == null)
			manufacturerPN = "";
		String materialType = hhpnpojo.getMaterialType();
		if (materialType == null)
			materialType = "";
		String materialGroup = hhpnpojo.getMaterialGroup();
		if (materialGroup == null)
			materialGroup = "";
		String procurementType = hhpnpojo.getProcurementType();
		if (procurementType == null)
			procurementType = "";
		String unit = hhpnpojo.getUnit();
		if (unit == null)
			unit = "";
		String rev = hhpnpojo.getRev();
		if (rev == null)
			rev = "";
		TCComponentItem newItem = (TCComponentItem) createCom(session, itemType, itemId, descr, rev, null);
		// TCComponentItem newItem = TCUtil.createItem(session, itemId, rev, descr,
		// itemType);
		if (newItem != null) {
			latestItemRevision = newItem.getLatestItemRevision();

			latestItemRevision.setProperty("d9_EnglishDescription", descr);
			latestItemRevision.setProperty("d9_ChineseDescription", cnDescr);
			latestItemRevision.setProperty("d9_DescriptionSAP", enDescr);
			latestItemRevision.setProperty("d9_ManufacturerID", manufacturer);
			latestItemRevision.setProperty("d9_ManufacturerPN", manufacturerPN);
			latestItemRevision.setProperty("d9_MaterialType", materialType);
			latestItemRevision.setProperty("d9_MaterialGroup", materialGroup);
			latestItemRevision.setProperty("d9_ProcurementMethods", procurementType);
			latestItemRevision.setProperty("d9_Un", unit);
			latestItemRevision.setProperty("d9_ActualUserID", d9_ActualUserID);
		}
		return latestItemRevision;
	}

	/**
	 * 在 sap和pnms中查询物料信息
	 * 
	 * @param url
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public TCComponentItemRevision querySapPnms(String url, String itemId) throws Exception {
		List<HHPNPojo> hhpns = new ArrayList<>();
		HHPNPojo hhpnPojo_sap = new HHPNPojo();
		hhpnPojo_sap.setDataFrom("SAP");
		hhpnPojo_sap.setIsExistInTC(0);
		hhpnPojo_sap.setItemId(itemId);
		hhpnPojo_sap.setPlant("CHMB");
		hhpns.add(hhpnPojo_sap);
		String jsons = JSONArray.toJSONString(hhpns);
		System.out.println("sap = " + jsons);
//		String rs = HttpUtil.post("http://10.203.163.184/tc-integrate/agile/partManage/getPartInfo", jsons);
		String rs = HttpUtil.post(url, jsons);

		if (rs != null && !"".equalsIgnoreCase(rs)) {
			System.out.println("rs_sap = " + rs);
			List<HHPNPojo> rsTmps = null;
			try {
				rsTmps = JSON.parseArray(rs, HHPNPojo.class);
			} catch (Exception e) {
				throw new Exception("返回参数错误！");
			}

			if (rsTmps != null && rsTmps.size() > 0) {
				HHPNPojo hhpnpojo = rsTmps.get(0);
				String descr = hhpnpojo.getDescr();
				if (descr != null && !"".equals(descr)) {
					TCComponentItemRevision craeteRev = craeteRev(hhpnpojo);
					if (craeteRev != null) {
						return craeteRev;
					}
				}
			}
		}

		HHPNPojo hhpnPojo_pnms = new HHPNPojo();
		hhpnPojo_pnms.setDataFrom("PNMS");
		hhpnPojo_pnms.setIsExistInTC(0);
		hhpnPojo_pnms.setItemId(itemId);
		hhpnPojo_pnms.setPnms(1);
		hhpns.clear();
		hhpns.add(hhpnPojo_pnms);
		jsons = JSONArray.toJSONString(hhpns);
		System.out.println("pnms = " + jsons);

		if (rs != null && !"".equalsIgnoreCase(rs)) {
			System.out.println("rs_pnms = " + rs);
			List<HHPNPojo> rsTmps = null;
			try {
				rsTmps = JSON.parseArray(rs, HHPNPojo.class);
			} catch (Exception e) {
				throw new Exception("返回参数错误！");
			}
			if (rsTmps != null && rsTmps.size() > 0) {
				HHPNPojo hhpnpojo = rsTmps.get(0);
				String descr = rsTmps.get(0).getDescr();
				if (descr != null && !"".equals(descr)) {
					// 创建对象并返回
					TCComponentItemRevision craeteRev = craeteRev(hhpnpojo);
					if (craeteRev != null) {
						return craeteRev;
					}
				}
			}
		}

		return null;
	}

	/**
	 * 创建对象
	 */
	public TCComponent createCom(TCSession session, String itemTypeName, String itemID, String name, String revisionID,
			Map<String, String> revisionPropMap) {
		// D9_VirtualPart
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				itemTypeName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
		if (CommonTools.isNotEmpty(itemID)) {
			createInstanceInput.add("item_id", itemID);
		}
		if (CommonTools.isNotEmpty(name)) {
			createInstanceInput.add("object_name", name);
		}
		IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				itemTypeName + "Revision");
		CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);
		if (CommonTools.isNotEmpty(revisionID)) {
			createInstanceInputRev.add("item_revision_id", revisionID);
		}
		if (revisionPropMap != null) {
			for (Entry<String, String> entry : revisionPropMap.entrySet()) {
				String p = entry.getKey();
				String v = entry.getValue();
				if (CommonTools.isNotEmpty(v)) {
					continue;
				}
				createInstanceInputRev.add(p, v);
			}
		}
		ArrayList<ICreateInstanceInput> iputList = new ArrayList<>();
		iputList.add(createInstanceInput);
		createInstanceInput.addSecondaryCreateInput(IBOCreateDefinition.REVISION, createInstanceInputRev);
		TCComponent obj = null;
		try {
			List<TCComponent> comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
			obj = comps.get(0);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static String getQty(String qty) {
		DecimalFormat dt = new DecimalFormat("0.00000");

		String retuQtyString = "";
		BigDecimal b1 = new BigDecimal(qty);
		BigDecimal b2 = new BigDecimal(100);
		BigDecimal divide = b1.divide(b2);
		double doubleValue = divide.doubleValue();

		retuQtyString = "" + doubleValue;
		if (retuQtyString.endsWith(".0")) {
			retuQtyString = retuQtyString.replace(".0", "");
		} else {
			retuQtyString = dt.format(doubleValue);
		}

		return retuQtyString;
	}

	/**
	 * 匹配id判断是否满足正则表达式
	 * 
	 * @param
	 * @return
	 */
	private static boolean getRightStr(String itemid) {
		String match = "^.{7,17}$";
		if (itemid.matches(match)) {
			return true;
		}
		return false;
	}

	public TCComponentItemRevision queryItemID(String item_id) throws Exception {
		TCComponentItemRevision itemrevision = null;
		
		TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件 ID", new String[] { "item_id" },
				new String[] { item_id });
		if (executeQuery != null && executeQuery.length > 0) {
			for (int i = 0; i < executeQuery.length; i++) {
				if (executeQuery[i] instanceof TCComponentItem) {
					String type = executeQuery[i].getType();
					if("D9_L5_Part".equals(type)) {
						continue;
					}
					
					TCComponentItem item = (TCComponentItem) executeQuery[i];
					itemrevision = item.getLatestItemRevision();

					if (itemrevision == null) {
						TCComponent[] allRev = item.getReferenceListProperty("revision_list");
						if (allRev != null && allRev.length > 0) {
							itemrevision = (TCComponentItemRevision) allRev[allRev.length - 1];
						}
					}
				}
			}
			
		}

		if (item_id.startsWith("7130") && TCUtil.isReleased(itemrevision)) {
			itemrevision = itemrevision.saveAs(null);
		}

		return itemrevision;
	}

	public static TCComponentBOMLine addbomline(TCComponentBOMWindow newBOMWindow, TCComponentBOMLine topLine,
			TCComponentItemRevision itemrev) throws TCException {
		AIFComponentContext[] childrens_pack = topLine.getChildren();
		int sequence_no = 0;

		topLine.refresh();
		// 先执行全部解包
		for (AIFComponentContext childrenAIF_pack : childrens_pack) {
			TCComponentBOMLine children_pack = (TCComponentBOMLine) childrenAIF_pack.getComponent();
			if (children_pack.isPacked()) {
				children_pack.unpack();
				children_pack.refresh();
			}
		}

		// 再执行查找编号计算
		topLine.refresh();
		AIFComponentContext[] childrens = topLine.getChildren();
		for (AIFComponentContext childrenAIF : childrens) {
			TCComponentBOMLine children = (TCComponentBOMLine) childrenAIF.getComponent();

			String bl_sequence_no = children.getProperty("bl_sequence_no");
			int parseInt = 0;
			try {
				parseInt = Integer.parseInt(bl_sequence_no);
			} catch (NumberFormatException e) {
				parseInt = 0;
			}

			if (parseInt > sequence_no) {
				sequence_no = parseInt;
			}
		}

		TCComponentBOMLine bomline = topLine.add(itemrev.getItem(), itemrev, null, false);

		if (sequence_no > 0) {
			sequence_no = sequence_no + 10;
			bomline.setProperty("bl_sequence_no", "" + sequence_no);
		}
		return bomline;
	}

	private TCComponent createObjects(TCSession session, String type, PackingMatrixImputBean packingMatrixImputBean)
			throws ServiceException {
		DataManagementService dmService = DataManagementService.getService(session);
		CreateIn[] createIn = new CreateIn[1];
		createIn[0] = new CreateIn();
		createIn[0].data.boName = type;

		String date = packingMatrixImputBean.getDate();
		String version = packingMatrixImputBean.getVersion();
		String desc = packingMatrixImputBean.getDesc();
		String ecrNo = packingMatrixImputBean.getEcrNo();
		String ecrDate = packingMatrixImputBean.getEcrDate();

		createIn[0].data.stringProps.put("d9_ChangeDate", date);
		createIn[0].data.stringProps.put("d9_ChangeVer", version);
		createIn[0].data.stringProps.put("d9_ChangeLog", desc);
		createIn[0].data.stringProps.put("d9_ChangeECR", ecrNo);
		createIn[0].data.stringProps.put("d9_ChangeReqDateUser", ecrDate);

		CreateResponse createObjects = dmService.createObjects(createIn);
		int sizeOfPartialErrors = createObjects.serviceData.sizeOfPartialErrors();
		for (int i = 0; i < sizeOfPartialErrors; i++) {
			ErrorStack partialError = createObjects.serviceData.getPartialError(i);
			String[] messages = partialError.getMessages();
			if(messages!=null && messages.length > 0) {
				StringBuffer sBuffe = new StringBuffer();
				for (String string : messages) {
					System.out.println(string);
					//sBuffe.append("\n\r");
					sBuffe.append(string);
				}
				throw new ServiceException(sBuffe.toString());
			}
			
		}
		if (createObjects.output.length > 0) {
			return createObjects.output[0].objects[0];
		}

		return null;
	}
}
