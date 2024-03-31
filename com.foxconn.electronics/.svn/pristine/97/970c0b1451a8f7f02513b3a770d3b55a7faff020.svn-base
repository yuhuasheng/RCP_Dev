package com.foxconn.electronics.managementebom.export.bom.mnt;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.managementebom.export.bom.mnt.domain.MntBomBean;
import com.foxconn.electronics.managementebom.export.bom.mnt.domain.MntSheetConstant;
import com.foxconn.electronics.managementebom.export.bom.mnt.domain.UnitEnum;
import com.foxconn.electronics.managementebom.export.bom.prt.PrtExportBOMHandle;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.HttpUtil;
import com.foxconn.electronics.util.TCPropName;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.foxconn.electronics.util.ProgressBarThread;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class MntBomExportAction extends AbstractAIFAction {

	private AbstractAIFApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private ProgressBarThread barThread;
	
	public MntBomExportAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {	
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();
		reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom"); 		
	}	
	 
	
	
	@Override
	public void run() {		
        try
        {
        	String startTime = CommonTools.getNowTimees();
        	System.out.println("==>> startTime: " + startTime);        	
            barThread = new ProgressBarThread(reg.getString("wait.MSG"), "MNT BOM " + reg.getString("export.MSG"));
            InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
            if (!(targetComponent instanceof TCComponentItemRevision))
            {
                TCUtil.warningMsgBox(reg.getString("selectErr2.MSG"), reg.getString("WARNING.MSG"));
                return;
            }
            TCComponentItemRevision DCNItemRev = (TCComponentItemRevision) targetComponent;	
            
            if (!"D9_MNT_DCNRevision".equals(DCNItemRev.getTypeObject().getName())) {
            	 TCUtil.warningMsgBox(reg.getString("selectErr3.MSG"), reg.getString("WARNING.MSG"));
                 return;
			}
            
			boolean flag = checkFirstVer(DCNItemRev); // 判断当前版本是否为初版
			if (!flag) {
				TCUtil.warningMsgBox(reg.getString("BOMWarn.MSG"), reg.getString("WARNING.MSG"));	
				return;
			}
			
			File exportFile = openFileChooser(getDefaultFileName(DCNItemRev));
			if (CommonTools.isEmpty(exportFile)) {
				return;
			}
			barThread.start();

			String plant = getPlant(DCNItemRev);
			if (CommonTools.isEmpty(plant)) {
				MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("DCNPropertiesErr.MSG"), reg.getString("WARNING.MSG"), MessageBox.WARNING);				
				throw new Exception();
			}
			
			
			List<TCComponentItemRevision> solutionList = getSolutionList(DCNItemRev);
			if (CommonTools.isEmpty(solutionList)) {				
				TCUtil.warningMsgBox(reg.getString("SolutionErr.MSG"), reg.getString("WARNING.MSG"));
				throw new Exception();
			}
			
			List<MntBomBean> resultList = getTotalBomStruct(solutionList);
			if (CommonTools.isEmpty(resultList)) {				
				MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("BOMTraversal.MSG"), reg.getString("WARNING.MSG"), MessageBox.WARNING);
				throw new Exception();
			}
			
			generatePartBom(exportFile, plant, resultList); // 输出BOM文件到本地
			barThread.stopBar();
			TCUtil.infoMsgBox(reg.getString("exportSuccess.MSG"), reg.getString("INFORMATION.MSG"));
			String endTime = CommonTools.getNowTimees();
			System.out.println("==>> endTime: " + endTime);	
			
			TCComponentItemRevision rootItemRev = getRootItemRev(DCNItemRev);
			if (CommonTools.isEmpty(rootItemRev)) {
				return;
			}
			
			JSONObject log = getJsonParams(rootItemRev);
			log.put("startTime", startTime);
			log.put("endTime", endTime);	
			
			JSONArray logs=new JSONArray();
			logs.add(log);
			
			String url = TCUtil.getPreference(TCUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");				
			String rs = HttpUtil.post(url + "/tc-integrate/actionlog/addlog", logs.toJSONString());	
			System.out.println("==>> rs: " + rs);
        } catch (Exception e) {
			e.printStackTrace();
			barThread.stopBar();
			TCUtil.errorMsgBox(reg.getString("exportFailure.MSG"), reg.getString("ERROR.MSG"));
		}
    } 
	
	/**
	 * 返回根对象版本
	 * @param DCNItemRev
	 * @return
	 * @throws TCException
	 */
	private TCComponentItemRevision getRootItemRev(TCComponentItemRevision DCNItemRev) throws TCException {
		TCComponentItemRevision rootItemRev = null;
		TCComponent[] relatedComponents = DCNItemRev.getRelatedComponents("CMHasSolutionItem");
		for (TCComponent tcComponent : relatedComponents) {
			TCComponentItemRevision itemRev = (TCComponentItemRevision) tcComponent;
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
				 rootItemRev = itemRev;
                 break;
             }
		}
		return rootItemRev;
	}
	
	/**
	 * 返回json参数
	 * @param rootItemRev
	 * @return
	 * @throws TCException
	 */
	private JSONObject getJsonParams(TCComponentItemRevision rootItemRev) throws TCException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("functionName", "導出滿足SAP導入格式BOM的時間");
		String actualUser = getActualUser(rootItemRev); // 获取实际用户
		if (CommonTools.isEmpty(actualUser)) {
			jsonObject.put("creator", session.getUser().getUserId());
			jsonObject.put("creatorName", session.getUser().getUserName());
		} else {
			jsonObject.put("creator", actualUser.split("\\|")[0]);
			jsonObject.put("creatorName", actualUser.split("\\|")[1]);
		}		
		jsonObject.put("project", rootItemRev.getProperty("project_ids"));
		jsonObject.put("itemId",  rootItemRev.getProperty("item_id"));
		jsonObject.put("rev",  rootItemRev.getProperty("item_revision_id"));
		jsonObject.put("revUid",  rootItemRev.getUid());
		return jsonObject;
	}
	
	/**
	 * 获取实际用户
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	private String getActualUser(TCComponentItemRevision itemRev) throws TCException {
		itemRev.refresh();
		itemRev.clearCache();
		String str = itemRev.getProperty("d9_ActualUserID");
		if (CommonTools.isEmpty(str)) {
			return null;
		}
		String userId = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
		String userName = str.substring(0, str.indexOf("("));
		return userId + "|" + userName;
	}
	
	
	/**
	 * 判断当前版本是否为初版
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	private boolean checkFirstVer(TCComponentItemRevision itemRev) throws TCException {
		TCComponent[] tcComponents = itemRev.getItem().getTCProperty("revision_list").getReferenceValueArray();
		List<Integer> numberList = new ArrayList<Integer>();	
		List<String> strList = new ArrayList<String>();		
		for (TCComponent component : tcComponents) {
			String ver = ((TCComponentItemRevision) component).getProperty("item_revision_id");
			if (ver.matches("[0-9]+")) { // 判断是否为数字版
				numberList.add(Integer.parseInt(ver));
			} else if (ver.matches("[a-zA-Z]+")) { // 判断对象版本是否为字母版
				strList.add(ver);
			}
		}
		
		String currentVer = itemRev.getProperty("item_revision_id");
		if (CommonTools.isNotEmpty(numberList)) {
			if (Collections.min(numberList) != Integer.parseInt(currentVer)) { // 如果当前版本不是最小版本
				return false;
			}
		} else if (CommonTools.isNotEmpty(strList)) {			
			if (!currentVer.equals(Collections.min(strList))) { // 如果当前版本不是最小版本
				return false;
			}
		}		
		return true;
	}

	
	/**
	 * 获取电子表单
	 * 
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	private TCComponentForm getForm(TCComponentItemRevision itemRev) throws TCException {
		TCComponentForm form = null;
		itemRev.refresh();
		TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isNotEmpty(relatedComponents)) {
			for (TCComponent tcComponent : relatedComponents) {
				if (!(tcComponent instanceof TCComponentForm)) {
					continue;
				}
				form = (TCComponentForm) tcComponent;
				break;
			}
		}
		return form;
	}

	/**
	 * 获取plant属性值
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	private String getPlant(TCComponentItemRevision itemRev) throws TCException {
		String plant = "";
		String objectType = "";
		TCComponentForm form = null;
		TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isNotEmpty(relatedComponents)) {
			for (TCComponent tcComponent : relatedComponents) {
				if (!(tcComponent instanceof TCComponentForm)) {
					continue;
				}
				form = (TCComponentForm) tcComponent;
				objectType = form.getTypeObject().getName();
				if (!"D9_MNT_DCNForm".equals(objectType)) {
					continue;
				}
				break;
			}
		}
		
		if (CommonTools.isNotEmpty(form)) {
			form.refresh();
			plant = Stream.of(form.getTCProperty("d9_ChangeType").getStringArrayValue()).collect(Collectors.joining(","));
		}
		return plant;
	}
	
	
	/**
	 * 返回解决方案伪文件夹下面带有BOM视图的对象版本集合
	 * 
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	private List<TCComponentItemRevision> getSolutionList(TCComponentItemRevision itemRevision) throws TCException {
		List<TCComponentItemRevision> list = Collections.synchronizedList(new ArrayList<TCComponentItemRevision>());
		TCComponent[] relatedComponents = itemRevision.getRelatedComponents("CMHasSolutionItem");
		if (CommonTools.isNotEmpty(relatedComponents)) {
//			Stream.of(relatedComponents).parallel().forEach(tcComponent -> {
			Stream.of(relatedComponents).forEach(tcComponent -> {
				try {
					if (tcComponent instanceof TCComponentItemRevision) {
						TCComponentItemRevision itemRev = (TCComponentItemRevision) tcComponent;
						if (TCUtil.isBom(itemRev)) { // 判断此结构是否含有BOM结构
							list.add(itemRev);
						}						
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
		return list;
	}

	/**
	 * 获取所有的含有替代料的BOM结构
	 * 
	 * @param solutionList
	 * @throws TCException
	 */
	private List<MntBomBean> getTotalBomStruct(List<TCComponentItemRevision> solutionList) throws Exception {
//		List<MntBomBean> list = Collections.synchronizedList(new ArrayList<MntBomBean>());
		List<MntBomBean> list = new ArrayList<MntBomBean>();
		solutionList.forEach(itemRev -> {
			try {
				MntBomBean bomBean = getBomStruct(itemRev);
				list.add(bomBean);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
		return list;
	}

	/**
	 * 获取包含替代料的单层BOM结构
	 * 
	 * @param itemRev
	 * @return
	 * @throws Exception 
	 */
	private MntBomBean getBomStruct(TCComponentItemRevision itemRev) throws Exception {
		TCComponentBOMWindow window = TCUtil.createBOMWindow(session);
		TCComponentBOMLine rootLine = window.setWindowTopLine(itemRev.getItem(), itemRev, null, null);	
		com.foxconn.tcutils.util.TCUtil.unpackageBOMStructure(rootLine); // 解包BOMLine
		rootLine.refresh();
		MntBomBean rootBean = new MntBomBean(rootLine, true);
		try {
			AIFComponentContext[] componmentContext = rootLine.getChildren();
			if (CommonTools.isNotEmpty(componmentContext)) {
//				Stream.of(componmentContext).parallel().forEach(e -> {
				Stream.of(componmentContext).forEach(e -> {
					try {	
						TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
						bomLine.refresh();
						TCComponentItemRevision partItemRev = bomLine.getItemRevision();
						if (!bomLine.isSubstitute()) {
							MntBomBean bomBean = new MntBomBean(bomLine, false);
							bomBean.setItemCategory(getPartTypeCode(partItemRev)); // 获取料号代码
							if (bomLine.hasSubstitutes()) {
								int strategy = 1;
								int priority = 1;
								bomBean.setStrategy(strategy); // 代表含有替代料
								bomBean.setUsageProb(100); // 有替代料则填写
								bomBean.setPriority(priority);
								TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
								List<MntBomBean> subBeanList = new ArrayList<MntBomBean>();
								for (int i = 0; i < listSubstitutes.length; i++) {
									TCComponentBOMLine subBomLine = listSubstitutes[i];									
									MntBomBean subBean = tcPropMapping(new MntBomBean(), subBomLine, false);									
									TCComponentItemRevision subItemRev = subBomLine.getItemRevision();
									subBean.setItemCategory(getPartTypeCode(subItemRev)); // 获取料号代码									
									subBean.setStrategy(strategy);
									subBean.setPriority(++priority);
									subBean.setFindNum(bomBean.getFindNum());
									subBean.setQty(bomBean.getQty());
									subBean.setUnit(bomBean.getUnit());		
									subBean.setLocation("");
									subBeanList.add(subBean);
								}	
								bomBean.setSubstitutesList(subBeanList);								
							}
							rootBean.addChilds(bomBean);
						}
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
				});
				if (rootBean.getChilds().size() > 0) {
					rootBean.getChilds().sort(Comparator.comparing(MntBomBean::getFindNum));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			if (CommonTools.isNotEmpty(window)) {
				try {
					window.save();
					window.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		return rootBean;
	}
	
	/**
	 * 获取料号代码
	 * @param partItemRev
	 * @return
	 * @throws TCException
	 */
	private String getPartTypeCode(TCComponentItemRevision partItemRev) throws TCException {
		String typeCode = "L";
		partItemRev.refresh();
		partItemRev.clearCache();
//		String objectType = partItemRev.getTypeObject().getName();
//		if (!"D9_PCA_PartRevision".equals(objectType) && !"D9_VirtualPartRevision".equals(objectType)) {
//			return typeCode;
//		}
		if (partItemRev.isValidPropertyName("d9_TempPN")) {
			String tempPN = partItemRev.getProperty("d9_TempPN");
			System.out.println("==>> tempPN: " + tempPN);
			if (tempPN.startsWith("79")) { // 判断临时料号以79开头
				String productType = partItemRev.getProperty("d9_ProductType_L6");
				if ("MVA".equals(productType)) {
					typeCode = "S";
				}
			} else if (tempPN.startsWith("629")) {
				typeCode = "S";
			}			
		}
		return typeCode;
	}
	
	
	/**
	 * 输出BOM文件到本地
	 * @param targeFile
	 * @param plant
	 * @param list
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	private void generatePartBom(File targeFile, String plant, List<MntBomBean> list) throws IllegalArgumentException, IllegalAccessException, IOException {
		ExcelUtil excelUtil = new ExcelUtil();
		Workbook wb = excelUtil.getWorkbook(MntSheetConstant.TEMPLATE);
		CellStyle cellStyle = excelUtil.getCellStyle2(wb);
		String[] split = plant.split(",");
		int k = 0;
		for (int i = 0; i < split.length; i++) {
			List<MntBomBean> finishDataList = setMainPartInfo(list, split[i]);
			int count = wb.getNumberOfSheets();
			Sheet sheet = null;			
			if (i < count) {
				sheet = wb.getSheetAt(i);
				wb.setSheetName(i, split[i]);
				k = i;
				excelUtil.setCellValue2(finishDataList, MntSheetConstant.start, MntSheetConstant.COLLENGTH, sheet, cellStyle);
			} 
			else {				
				sheet = wb.cloneSheet(k);
				wb.setSheetName(i, split[i]);	 
				excelUtil.updateCellValue(sheet, MntSheetConstant.start, 0, split[i]);
			}
		}
		OutputStream out = new FileOutputStream(targeFile);
		wb.write(out);
		out.flush();
		out.close();
	}	
	
	
	/**
	 * 为子物料添加父物料ID
	 * @param topBomBean
	 * @param list
	 * @param plant
	 */
	private List<MntBomBean> setMainPartInfo(List<MntBomBean> list, String plant) {
		List<MntBomBean> finishDataList = new ArrayList<MntBomBean>();		
		for (MntBomBean bean : list) {			
			List<MntBomBean> childs = bean.getChilds();
			if (CommonTools.isNotEmpty(childs)) {
				childs.forEach(child -> {
					child.setPlant(plant);
					child.setMaterialNumber(bean.getMaterialNumber());
					finishDataList.add(child);
					List<MntBomBean> subList = child.getSubstitutesList();
					if (CommonTools.isNotEmpty(subList)) {
						subList.forEach(sub -> { 
							sub.setPlant(plant);
							sub.setMaterialNumber(bean.getMaterialNumber());
							finishDataList.add(sub);
						});
					}
				});
			}
		}	
		return finishDataList;	
	}

	public static MntBomBean tcPropMapping(MntBomBean bean, TCComponentBOMLine tcbomLine, boolean isTopLine)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcbomLine != null) {
			tcbomLine.refresh();
			TCComponentItemRevision itemRev = tcbomLine.getItemRevision();
			itemRev.refresh();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);				
				if (tcPropName != null) {
					String tcAttrName = tcPropName.value();
					int col = tcPropName.cell();
					if (!tcAttrName.isEmpty()) {
						Object value = "";
						if (tcAttrName.startsWith("bl")) {
							value = tcbomLine.getProperty(tcAttrName).trim();
							if ("bl_quantity".equals(tcAttrName)) {
								if (CommonTools.isEmpty(value.toString().trim())) {
//									value = "1"; // 数量默认值设置为1									
								}
							}
							
						} else {
							value = itemRev.getProperty(tcAttrName);
						}
						
						
						if (fields[i].getType() == Integer.class) {
							if (value.equals("") || value == null) {
								value = null;
							} else {
								value = Integer.parseInt((String) value);
							}
						}
						if (isTopLine && col == 9) {
							fields[i].set(bean, "");
						} else if (!isTopLine && col == 1) {
							fields[i].set(bean, "");
						} else {
							fields[i].set(bean, value);
						}						
					}
				}
			}
			bean.setUid(tcbomLine.getUid());
		}
		String unit = bean.getUnit();
		if (CommonTools.isEmpty(unit)) {
			bean.setQty(null);
		} 
		
		if (CommonTools.isNotEmpty(bean.getQty())) {
			BigDecimal bigDecimal = new BigDecimal(bean.getQty());
			bigDecimal=bigDecimal.multiply(new BigDecimal(UnitEnum.KEA.houndredMultiple()));
			bean.setQty(bigDecimal.stripTrailingZeros().toPlainString());
		}
		
//		if (unit.equals(UnitEnum.KEA.unit())) {
//			float b2 = Float.parseFloat(bean.getQty()) / UnitEnum.KEA.throusandMultiple();
//			bean.setQty(String.valueOf(b2));
//		}
		return bean;
	}

	/**
	 * 返回默认文件名称 
	 * @param component
	 * @return
	 * @throws TCException
	 */
	public String getDefaultFileName(TCComponent component) throws TCException {
		String itemId = component.getProperty("item_id");
		return itemId + "_" + formatNowDate("yyyyMMdd");
	}
	
	
	/**
	 * 格式化的日期
	 * @param formatStr
	 * @return
	 */
	public String formatNowDate(String formatStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr);
		return formatter.format(LocalDateTime.now());
	}

	/**
	 * 文件选择器
	 * @param fileName
	 * @return
	 */
	public File openFileChooser(String fileName) {
		JFileChooser jFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
		jFileChooser.setFileFilter(filter);
		jFileChooser.setSelectedFile(new File(fileName + ".xlsx"));
		int openDialog = jFileChooser.showSaveDialog(null);
		if (openDialog == JFileChooser.APPROVE_OPTION) {
			File file = jFileChooser.getSelectedFile();
			String fname = jFileChooser.getName(file);
			if (fname.indexOf(".xlsx") == -1) {
				file = new File(jFileChooser.getCurrentDirectory(), fname + ".xlsx");
			}
			return file;
		}
		return null;
	}
}
