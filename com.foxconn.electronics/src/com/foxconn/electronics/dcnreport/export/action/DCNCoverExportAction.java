package com.foxconn.electronics.dcnreport.export.action;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.foxconn.decompile.util.FileStreamUtil;
import com.foxconn.electronics.dcnreport.export.constant.DCNCoverConstant;
import com.foxconn.electronics.dcnreport.export.domain.DCNCoverBean;
import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.tcutils.progress.ProgressBarThread;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.soa.client.model.strong.EPMTask;
import com.teamcenter.soaictstubs.stringSeq_tHolder;

import cn.hutool.Hutool;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;

public class DCNCoverExportAction extends AbstractAIFAction {

	private AbstractAIFApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private ProgressBarThread barThread;
	private TCComponentItemRevision DCNItemRev;
	private String dcnItemId;
	private String dcnVersion;
	public Properties prop = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static final List<String> REVIEWNODELIST = new ArrayList<String>() {
	private static final long serialVersionUID = 1L;
	{
		add("MELeaderReview");
		add("PECostEvaluate");
		add("PELeaderReview");
		add("RDFuctionTeams");
		add("SPMReview");
		add("MESupervisorReview");
	}};
	
	public DCNCoverExportAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();
		prop = loadPropertiesFile(DCNCoverConstant.CONFIG);
		reg = Registry.getRegistry("com.foxconn.electronics.dcnreport.dcnreport");
	}

	@Override
	public void run() {
		try {
			barThread = new ProgressBarThread(reg.getString("wait.MSG"), reg.getString("export.TITLE") + reg.getString("export.MSG"));
			TCComponentItemRevision targetComponent = (TCComponentItemRevision) app.getTargetComponent(); // 获取选中的目标对象
			if (!"D9_DT_DCNRevision".equalsIgnoreCase(targetComponent.getTypeObject().getName())) {
				TCUtil.warningMsgBox(reg.getString("selectErr.MSG"), reg.getString("WARNING.MSG"));
				return;
			}
			
			DCNItemRev = (TCComponentItemRevision) targetComponent;
			
			File exportFile = TCUtil.openFileChooser(getDefaultFileName(DCNItemRev));
			if (CommonTools.isEmpty(exportFile)) {
				return;
			}
			
			barThread.start();
			
			TCUtil.setBypass(session); // 开启旁路
			dcnItemId = DCNItemRev.getProperty("item_id");
			System.out.println("==>> dcnItemId: " + dcnItemId);
			
			dcnVersion = DCNItemRev.getProperty("item_revision_id");
			System.out.println("==>> dcnVersion: " + dcnVersion);
			
			TCComponent[] problemItemRevs = DCNItemRev.getRelatedComponents("CMHasProblemItem");
			if (CommonTools.isEmpty(problemItemRevs)) {
				TCUtil.warningMsgBox(reg.getString("problemErr.MSG"), reg.getString("WARNING.MSG"));				
				throw new Exception();
			}
			
			TCComponent[] solutionItemRevs = DCNItemRev.getRelatedComponents("CMHasSolutionItem");
			if (CommonTools.isEmpty(solutionItemRevs)) {
				TCUtil.warningMsgBox(reg.getString("solutionErr.MSG"), reg.getString("WARNING.MSG"));
				throw new Exception();
			}
			
			DCNCoverBean bean = new DCNCoverBean();
			TCComponentItemRevision drawItemRev = setRevNo(problemItemRevs, solutionItemRevs, bean);
			if (CommonTools.isEmpty(drawItemRev)) {
				TCUtil.warningMsgBox(reg.getString("solutionErr1.MSG"), reg.getString("WARNING.MSG"));
				throw new Exception();
			}
			
			bean = tcPropMapping(bean, DCNItemRev, drawItemRev);
			setReasonForChange(DCNItemRev, bean);
			setUrgeAndPresituation(bean);
			
			if (!TCUtil.isReleased(DCNItemRev)) {
				TCUtil.warningMsgBox(reg.getString("dcnStatusErr.MSG"), reg.getString("WARNING.MSG"));
				throw new Exception();
			}
			
			TCComponentTask root = getRootTask();
			if (CommonTools.isEmpty(root)) {
				throw new Exception();
			}
			
			List<String> recordList = new ArrayList<String>();
			Map<String, List<List<String>>> pubMailMapForMore = analysePublicMail(); // 解析流程审批邮件附件文本
			Map<String, TCComponentTask> flowNodeMap = filterWorkFlowList(root); // 过滤流程节点
			getDCNCreateInfo(recordList); // 获取DCN创建信息
			if (CommonTools.isNotEmpty(flowNodeMap)) {
				getReviewOwnerAndDate(flowNodeMap, pubMailMapForMore, recordList); // 获取审核人和审核日期
			}
			bean.setSignature(recordList);
			generateDCNCoverFile(exportFile, bean);
			TCUtil.infoMsgBox(reg.getString("exportSuccess.MSG"), reg.getString("INFORMATION.MSG"));	
			barThread.stopBar();
		} catch (Exception e) {
			e.printStackTrace();
			barThread.stopBar();
			TCUtil.closeBypass(session);
			TCUtil.errorMsgBox(reg.getString("exportFailure.MSG"), reg.getString("ERROR.MSG"));			
		}		
	}	
	

	public DCNCoverBean tcPropMapping(DCNCoverBean bean, TCComponentItemRevision DCNItemRev, TCComponentItemRevision drawItemRev) throws TCException, IllegalArgumentException, IllegalAccessException {
		if (bean != null && DCNItemRev != null && drawItemRev != null) {
			String dcnType = DCNItemRev.getTypeObject().getName();
			String drawType = drawItemRev.getTypeObject().getName();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcPropertes = fields[i].getAnnotation(TCPropertes.class);
				String defaultValue = fields[i].get(bean) + "";
				if (tcPropertes != null) {
					String tcProperty = tcPropertes.tcProperty();
					String tcType = tcPropertes.tcType();
					if (!CommonTools.isEmpty(tcProperty) && !CommonTools.isEmpty(tcType)) {
						Object value = "";
						if (tcType.equals(dcnType)) {
							if (tcProperty.equalsIgnoreCase("project_list")) {
								TCProperty props = DCNItemRev.getItem().getTCProperty(tcProperty);
								if (props != null) {
									TCComponent[] pjs = props.getReferenceValueArray();
									value = Stream.of(pjs).map(e -> ((TCComponentProject) e).getProjectID() + "-" + ((TCComponentProject) e).getProjectName()).collect(Collectors.joining(","));
								}
							} else {
								value = DCNItemRev.getProperty(tcProperty);
							}
							
						} else if (tcType.equals(drawType)) {
							value = drawItemRev.getProperty(tcProperty);
						}
						
						if (fields[i].getType() == Integer.class) {
							if (value.equals("") || value == null) {
								value = null;
							} else {
								value = Integer.parseInt((String) value);
							}
						} 
						
						if (CommonTools.isNotEmpty(defaultValue)) {
							fields[i].set(bean, defaultValue + " " + value);
						} else {
							fields[i].set(bean, value);
						}					
					}
				}
			}
			
			
		}
		return bean;
	}
	
	
	/**
	 * 设置更改原因条目
	 * @param bean
	 * @param DCNItemRev
	 * @throws TCException
	 */
	private void setReasonForChange(TCComponentItemRevision DCNItemRev, DCNCoverBean bean) throws TCException {
		TCComponent[] relatedComponents = DCNItemRev.getRelatedComponents("d9_ReasonForChangeTable");
		List<String> descriptionOfChange = new ArrayList<String>();	
		List<String> reasonForChange = new ArrayList<String>();
		for (int i = 0; i < relatedComponents.length; i++) {
			TCComponent tcComponent = relatedComponents[i];
			String reasonChange = tcComponent.getProperty("d9_ReasonOfChange");
			String reasonType = tcComponent.getProperty("d9_ReasonType");
			String actualUser = tcComponent.getProperty("d9_Owner");
			String cost = tcComponent.getProperty("d9_Cost");
			String currency = tcComponent.getProperty("d9_Currency");
//			String str = (reasonType == null ? "" : reasonType.trim() + "&") + (actualUser == null ? "" : "Owner:" + actualUser.trim() + "&") + (cost == null ? "" : "Cost:" + cost.trim()) + (currency == null ? "" : currency.trim());
			String str = (reasonType == null ? "" : reasonType.trim() + "&") + (actualUser == null ? "" : "Owner:" + actualUser.trim());
			if (CommonTools.isNotEmpty(reasonChange)) {
				descriptionOfChange.add((i + 1) + "、" +reasonChange);
			}
			
			if (CommonTools.isNotEmpty(str)) {
				reasonForChange.add(str);
			}
		}
		
		bean.setDescriptionOfChange(descriptionOfChange);
		bean.setReasonForChange(reasonForChange);
	}
	
	
	private void setUrgeAndPresituation(DCNCoverBean bean) {
		String finalUrgencyLevel = "";
		String finalTestReport = "";
		String urgencyLevel = CommonTools.replaceBlank(bean.getUrgencyLevel()).replaceAll("\\s*", "");
		String testReport = CommonTools.replaceBlank(bean.getTestReport()).replaceAll("\\s*", "");
		if ("Normal".equalsIgnoreCase(urgencyLevel)) {
			finalUrgencyLevel = "Urgency Level:" + "\n\r" + "                    [ V ] Normal   [ ] Urgent   [ ] Top Urgent";
//			finalUrgencyLevel = "Normal";
		} else if ("Urgent".equals(urgencyLevel)) {
			finalUrgencyLevel = "Urgency Level:" + "\n\r" + "                    [ ] Normal    [ V ] Urgent  [ ] Top Urgent";
		} else if ("Top Urgent".equals(urgencyLevel)) {
			finalUrgencyLevel = "Urgency Level:" + "\n\r" + "                    [ ] Normal    [ ] Urgent   [ V ] Top Urgent";
		}
		
		bean.setUrgencyLevel(finalUrgencyLevel);
		
		if ("Necessary".equalsIgnoreCase(testReport)) {
			finalTestReport = "Test Report : [ V ] Necessary   [ ] No Need   [ ] Will be Issued On";
		} else if ("NoNeed".equalsIgnoreCase(testReport)) {
			finalTestReport = "Test Report : [ ] Necessary    [ V ] No Need   [ ] Will be Issued On";
		} else if ("WillbeIssuedOn".equalsIgnoreCase(testReport)) {
			finalTestReport = "Test Report : [ ] Necessary    [ ] No Need    [ V ] Will be Issued On";
		}
		
		bean.setTestReport(finalTestReport);		
	}
	
	/**
	 * 获取问题项和解决方案项的版本
	 * @param bean
	 * @throws TCException
	 */
	private TCComponentItemRevision setRevNo(TCComponent[] problemItemRevs, TCComponent[] solutionItemRevs, DCNCoverBean bean) throws TCException {
		TCComponentItemRevision solutionItemRev = null;
		List<String> problemItemList = Stream.of(problemItemRevs).map(e -> {
			try {
				return e.getProperty("item_id");
			} catch (Exception e1) {
				throw new RuntimeException(e1.getCause());
			}			
		}).collect(Collectors.toList());
		
		List<String> solutionItemList = Stream.of(solutionItemRevs).map(e -> {
			try {
				return e.getProperty("item_id");
			} catch (Exception e1) {
				throw new RuntimeException(e1.getCause());
			}			
		}).collect(Collectors.toList());
		
		List<String> interSection = problemItemList.stream().filter(item -> solutionItemList.contains(item)).collect(Collectors.toList());
		if (CommonTools.isEmpty(interSection)) {
			return null;
		}
		
		String previousRev = "";
		String newRev = "";
		
		for (TCComponent problemItem : problemItemRevs) {
			if (!(problemItem instanceof TCComponentItemRevision)) {
				continue;
			}
			String problemItemId = problemItem.getProperty("item_id");
			String problemVersion = problemItem.getProperty("item_revision_id");
			boolean anyMatch = interSection.stream().anyMatch(str -> str.equalsIgnoreCase(problemItemId));
			if (anyMatch) {
				previousRev = problemVersion;
				break;
			}
		}
		
		for (TCComponent solutionItem : solutionItemRevs) {
			if (!(solutionItem instanceof TCComponentItemRevision)) {
				continue;
			}
			String solutionItemId = solutionItem.getProperty("item_id");
			String solutionVersion = solutionItem.getProperty("item_revision_id");
			boolean anyMatch = interSection.stream().anyMatch(str -> str.equalsIgnoreCase(solutionItemId));
			if (anyMatch) {
				newRev = solutionVersion;
				solutionItemRev = (TCComponentItemRevision) solutionItem;
				break;
			}
		}
		
		bean.setRevNo("From " + previousRev + " To " + newRev);
		return solutionItemRev;
	}
	
	
	private String getDefaultFileName(TCComponent component) throws TCException {
		String itemId = component.getProperty("item_id");
		String version = component.getProperty("item_revision_id");
		return itemId + "_" + version + "_" + "封面信息" + TCUtil.formatNowDate("yyyyMMdd");
	}

	
	/**
	 * 获取流程根任务
	 * @return
	 * @throws TCException
	 */
	private TCComponentTask getRootTask() throws TCException {
		TCComponentTask currentTask = null;
		TCComponentTask root = null;
//		AIFComponentContext[] whereReferenced = DCNItemRev.whereReferenced();
		AIFComponentContext[] references = DCNItemRev.whereReferencedByTypeRelation(new String[] {"EPMTask"}, new String[] {"Fnd0EPMTarget"});
		Optional<AIFComponentContext> findAny = Stream.of(references).filter(context -> {
			try {
				InterfaceAIFComponent component = context.getComponent();
				if (component instanceof TCComponentTask) {
					TCComponentTask task = (TCComponentTask) component;
					TCComponentTaskTemplate taskTemplate = (TCComponentTaskTemplate) task.getRelatedComponent("task_template");
					String templateName = taskTemplate.getProperty("object_name");
					if (DCNCoverConstant.WORKFLOWTEMPLATENAME.equalsIgnoreCase(templateName)) {
						return true;
					}
				}				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
			return false;
		}).findAny();
		
		
		if (findAny.isPresent()) {
			currentTask = (TCComponentTask) findAny.get().getComponent();
		}
		
		if (currentTask == null) {	
			TCUtil.warningMsgBox(reg.getString("dcnFlowTemplateErr.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		
		root = currentTask.getRoot();
		
		int state = root.getTCProperty("state").getIntValue();
		if (state !=8) {
			TCUtil.warningMsgBox(reg.getString("dcnFlowStatus.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		return root;
	}
	
	/**
	 * 解析流程审批邮件附件文本
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<List<String>>> analysePublicMail() throws Exception {
		
		Map<String, List<List<String>>> parseLstToMap = com.foxconn.decompile.util.TCUtil.parseLstToMap(DCNItemRev);
		if(parseLstToMap!=null && parseLstToMap.size() > 0) {
			return parseLstToMap;
		}
		return null;
	}
	
	/**
	 * 过滤流程节点
	 * @param root
	 * @return
	 * @throws TCException
	 */
	private Map<String, TCComponentTask> filterWorkFlowList(TCComponentTask root) throws TCException {
		Map<String, TCComponentTask> retMap = new LinkedHashMap<String, TCComponentTask>();
		TCComponent[] childTasks = root.getRelatedComponents("child_tasks");		
		if (CommonTools.isNotEmpty(childTasks)) {
			REVIEWNODELIST.forEach(str -> {
				Optional<TCComponent> findAny = Stream.of(childTasks).filter(component -> {
					try {
						if (component instanceof TCComponentTask) {
							String taskName = ((TCComponentTask)component).getName();
							if (str.equalsIgnoreCase(taskName.replaceAll("\\s*", ""))) {
								return true;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e.getCause());
					}
					return false;
				}).findAny();
				
				if (findAny.isPresent()) {
					TCComponentTask task = (TCComponentTask) findAny.get();
					try {
						retMap.put(task.getName(), task);
					} catch (TCException e) {
						e.printStackTrace();
					}
				}
			});
		}
		return retMap;
	}
	
	
	/**
	 * 获取DCN的创建信息
	 * @param resultList
	 * @throws TCException
	 */
	private void getDCNCreateInfo(List<String> recordList) throws TCException {
		String actualUserId = DCNItemRev.getProperty("d9_ActualUserID");
		if (CommonTools.isNotEmpty(actualUserId)) {
			Date createDate = DCNItemRev.getTCProperty("creation_date").getDateValue();
			String createTime = sdf.format(createDate);
			recordList.add(actualUserId + "&" + createTime);
		} else {
			recordList.add(null);
		}
	}
	
	
	/**
	 * 获取审核人和审核日期
	 * @param flowNodeMap
	 * @param pubMailMapForMore
	 * @param resultList
	 * @throws TCException
	 */
	private void getReviewOwnerAndDate(Map<String, TCComponentTask> flowNodeMap, Map<String, List<List<String>>> pubMailMapForMore, List<String> recordList) throws TCException {		
		for (Map.Entry<String, TCComponentTask> entry : flowNodeMap.entrySet()) {
			String key = entry.getKey();
			TCComponentTask task = entry.getValue();
			Date endDate = task.getTCProperty("fnd0EndDate").getDateValue();
			String endTime = sdf.format(endDate);
			Optional<Entry<String,List<List<String>>>> findAny = pubMailMapForMore.entrySet().stream().filter(e -> e.getKey().equals(key)).findAny();
			if (findAny.isPresent()) {
				List<List<String>> value = findAny.get().getValue();
				if ("RDFuctionTeams".equalsIgnoreCase(key.replaceAll("\\s*", ""))) {
					recordList.add(null);
				}
				Optional<List<String>> findAny2 = value.stream().filter(str -> str.contains(key)).findAny();
				if (findAny2.isPresent()) {
					List<String> list2 = findAny2.get();
					if (list2.size() == 4 && !checkContext(list2)) {
						String actuserId = list2.get(1);
						String actuserName = list2.get(2);
						if (CommonTools.isNotEmpty(actuserId) && CommonTools.isNotEmpty(actuserName)) {
							recordList.add(actuserName + "("+ actuserId + ")" + "&" + endTime);
						}
						
					} else {
						recordList.add(null);
					} 										
				} else {
					recordList.add(null);
				}
				
			}
		}		
	}
	
	/**
	 * 校验内容是否含有空值
	 * @param list
	 * @return
	 */
	private boolean checkContext(List<String> list) {		
		return list.stream().anyMatch(str -> CommonTools.isEmpty(str));
	}
	
	/**
	 * 生成DCN封面信息文件
	 * @param targetFile
	 * @param bean
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	private void generateDCNCoverFile(File targetFile, DCNCoverBean bean) throws IllegalArgumentException, IllegalAccessException, IOException {
		ExcelUtil excelUtil = new ExcelUtil();
		Workbook wb = excelUtil.getWorkbook(DCNCoverConstant.TEMPLATEPATH);
		Sheet sheet = wb.getSheet("Template");
//		int lastRowNum = sheet.getLastRowNum();
		if (CommonTools.isNotEmpty(bean)) {
			int rowIndex = -1;
	        int colIndex = -1;
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {				
				fields[i].setAccessible(true);
				String fieldName = fields[i].getName();
				String location = prop.getProperty(fieldName.toLowerCase());
				if (fields[i].getType() == String.class) {
					String value = fields[i].get(bean) + "";					
					if (CommonTools.isEmpty(location)) {
						System.out.println(fieldName + ", 没有匹配到");
						continue;
					}
					rowIndex = Integer.parseInt(Optional.ofNullable(location.split(",")[0]).orElse("0")) - 1;
			        colIndex = Optional.ofNullable(excelUtil.getColumIntByString(location.split(",")[1])).orElse(0);
					if (CommonTools.isNotEmpty(value)) {
						excelUtil.setCellValue(sheet, rowIndex, colIndex, value);
					}
				} else if (fields[i].getType() == List.class) {
					Object obj = fields[i].get(bean);
					List<String> list = null;
					if (obj != null) {
						list = (List<String>) obj;
					}
					if ("descriptionOfChange".equalsIgnoreCase(fieldName)) {
						Map<String, String> descChangeLocation = getDescriptionChangeLocation(location);						
						setDescriptionChange(excelUtil, sheet, list, descChangeLocation);
					} else if ("reasonForChange".equalsIgnoreCase(fieldName)) {
						List<Map<String, String>> reasonChangeLocation = getLocation(location);
						setReasonChange(excelUtil, sheet, list, reasonChangeLocation);
					} else if ("signature".equalsIgnoreCase(fieldName)) {
						List<Map<String, String>> signatureLocation = getLocation(location);
						setSignature(excelUtil, sheet, list, signatureLocation);
					}
				}
			}
		}
		
		OutputStream out = new FileOutputStream(targetFile);
		wb.write(out);
		out.flush();
		out.close();
	}
	
	
	
	private Map<String, String> getDescriptionChangeLocation(String str) {
		Map<String, String> map = new LinkedHashMap<>();
		String[] split = str.split(";");		
		for (String s : split) {
			s = s.replace("{", "").replace("}", "");
			map.put(s.split(",")[0], s.split(",")[1]);
		}
		
		return map;
	}
	
	
	private void setDescriptionChange(ExcelUtil excelUtil, Sheet sheet, List<String> descChangeList, Map<String, String> descChangeLocation) {
		int rowIndex = -1;
        int colIndex = -1;
        if (CommonTools.isNotEmpty(descChangeList)) {
        	Iterator<Entry<String, String>> iterator = descChangeLocation.entrySet().iterator();
        	for (String str : descChangeList) {
        		while (iterator.hasNext()) {
        			Entry<String, String> entry = iterator.next();
        			rowIndex = Integer.parseInt(Optional.ofNullable(entry.getKey()).orElse("0")) - 1;
        	        colIndex = Optional.ofNullable(excelUtil.getColumIntByString(entry.getValue())).orElse(0);
        	        excelUtil.setCellValue(sheet, rowIndex, colIndex, str);
        	        iterator.remove();
        	        break;
        		}
        	}    		
		}        
		
        descChangeLocation.forEach((key, value) -> {
        	int hideRowIndex = Integer.parseInt(Optional.ofNullable(key).orElse("0")) - 1;
	        Row row = sheet.getRow(hideRowIndex);
	    	if (row != null) {
				row.setZeroHeight(true);
			}
        });
	}
	
	
	private void setReasonChange(ExcelUtil excelUtil, Sheet sheet, List<String> reasonChangeList, List<Map<String, String>> reasonChangeLocation) {
		int rowIndex = -1;
        int colIndex = -1;
        if (CommonTools.isNotEmpty(reasonChangeList)) {
			ListIterator<Map<String,String>> listIterator = reasonChangeLocation.listIterator();
			for (String str : reasonChangeList) {
				while (listIterator.hasNext()) {
					Map<String, String> map = listIterator.next();
					String[] split = str.split("\\&");
					Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
					for (int i = 0; i < split.length; i++) {
						while (iterator.hasNext()) {
							Entry<String, String> entry = iterator.next();
							rowIndex = Integer.parseInt(Optional.ofNullable(entry.getValue()).orElse("0")) - 1;
		        	        colIndex = Optional.ofNullable(excelUtil.getColumIntByString(entry.getKey())).orElse(0);
		        	        excelUtil.setCellValue(sheet, rowIndex, colIndex, split[i]);
		        	        iterator.remove();
		        	        break;
						}
					}
					listIterator.remove();
					break;
				}
			}
		}
        
        reasonChangeLocation.forEach(e -> {
        	e.forEach((key, value) -> {
        		int hideRowIndex = Integer.parseInt(Optional.ofNullable(value).orElse("0")) - 1;
        		Row row = sheet.getRow(hideRowIndex);
        		if (row != null) {
					row.setZeroHeight(true);
				}
        	});
        });
	}
	
	private void setSignature(ExcelUtil excelUtil, Sheet sheet, List<String> signatureList, List<Map<String, String>> signatureLocation) {
		int rowIndex = -1;
		int colIndex = -1;
		if (CommonTools.isNotEmpty(signatureList)) {
			ListIterator<Map<String,String>> listIterator = signatureLocation.listIterator();
			for (String str : signatureList) {
				while (listIterator.hasNext()) {
					Map<String, String> map = listIterator.next();
					if (CommonTools.isEmpty(str)) {
						listIterator.remove();
						break;
					}					
					String[] split = str.split("\\&");
					Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
					for (int i = 0; i < split.length; i++) {
						while (iterator.hasNext()) {
							Entry<String, String> entry = iterator.next();
							rowIndex = Integer.parseInt(Optional.ofNullable(entry.getKey()).orElse("0")) - 1;
		        	        colIndex = Optional.ofNullable(excelUtil.getColumIntByString(entry.getValue())).orElse(0);
		        	        excelUtil.setCellValue(sheet, rowIndex, colIndex, split[i]);
		        	        iterator.remove();
		        	        break;
						}
					}
					listIterator.remove();
					break;
				}
			}
		}
		
	}
	
	
 	private List<Map<String, String>> getLocation(String str) {
		String[] split = str.split("\\|");
        List<Map<String, String>> list = new ArrayList<>(split.length);
        for (String s : split) {
            Map<String, String> map = new LinkedHashMap<>();
            String[] split1 = s.split(";");
            List<String> convertList = Convert.convert(new TypeReference<List<String>>() {
    		}, split1);            
            for (String s1 : convertList) {            	
            	s1 = s1.replace("{", "").replace("}", "").trim();
                map.put(s1.split(",")[0], s1.split(",")[1]);
            }
            
            if (convertList.size() != map.size()) {
            	map.clear();
            	for (String s1 : convertList) {
            		s1 = s1.replace("{", "").replace("}", "").trim();
            		map.put(s1.split(",")[1], s1.split(",")[0]);
            	}
			}
            list.add(map);            
        }
        return list;
	}
	
	private Properties loadPropertiesFile(String fileName) {
        Properties props = null;
        try (InputStream in = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(fileName), "文件未找到：" + fileName);) {
        	props = new Properties();
        	props.load(new InputStreamReader(in, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
            return null;
		} 
        return props;
	}	
	
}
