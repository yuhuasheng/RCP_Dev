package com.foxconn.electronics.issuemanagement.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.wb.swt.SWTResourceManager;
import com.foxconn.electronics.issuemanagement.dialog.ImportIssueDialog;
import com.foxconn.electronics.login.UserLoginSecond;
import com.foxconn.electronics.util.ExcelUtils;
import com.foxconn.tcutils.util.IndeterminateLoadingDialog;
import com.foxconn.tcutils.util.IndeterminateLoadingDialog.Action;
import com.foxconn.tcutils.util.Pair;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCExceptionPartial;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;

public class ImportIssueService {
	
	private ImportIssueDialog window;
	
	public ImportIssueService(ImportIssueDialog window) {
		this.window = window;
	}
	
	public void onImport(String filPath) {
		if(StrUtil.isEmpty(filPath)) {
			MessageBox.post(window.shell,"請先導入文件", "Info", MessageBox.INFORMATION);
			return;
		}
		StyleRange infoStyle = new StyleRange();
		infoStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
		
		StyleRange warnStyle = new StyleRange();				
		warnStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW);
		
		StyleRange errorStyle = new StyleRange();				
		errorStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_RED);
		IndeterminateLoadingDialog.Action action = new IndeterminateLoadingDialog.Action() {
			
			private boolean isCancel=false;
			
			private String getActualProp(List<String> actualProps,String p) {
				for(String s : actualProps) {
					String[] split = s.split("=");
					if(p.equals(split[0].trim())) {
						return split[1].trim();
					}
				}
				return null;
			}
			
			/**
		     参考String.trim,加入了不连续空格nbsp;unicode160和汉字空格unicode12288
		     **/
		    public String trim(String text){
		        int len = text.length();
		        int st = 0;
		        char[] val = text.toCharArray();
		        char p;
		        while ((st < len) && ((p=val[st]) <= ' ' || p==160 || p==12288 )) {
		            st++;
		        }
		        while ((st < len) && ((p =val[len - 1]) <= ' ' || p==160 || p==12288 )) {
		            len--;
		        }
		        return ((st > 0) || (len < text.length())) ? text.substring(st, len) : text;
		    }
			
			private Map<Integer,Object> readRow(XSSFRow row ,int lastIndex) {
				Map<Integer,Object> map = new HashMap<>();
				if(row==null) {
					return map;
				}
				for(int i=0;i<=lastIndex;i++) {							
					Cell cell = row.getCell(i);
					if(cell==null) {
						continue;
					}
					Object value = null;
					if (cell.getCellTypeEnum()==CellType.NUMERIC&& HSSFDateUtil.isCellDateFormatted(cell)) {
						value = cell.getDateCellValue();
					}else {
						value = ExcelUtils.getCellValueToString(cell);
					}
					
					if(StrUtil.isEmptyIfStr(value)) {
						continue;
					}
					
					if(value instanceof String) {
						value = trim(((String)value));
						if(StrUtil.isEmptyIfStr(value)) {
							continue;
						}
					}					
					map.put(i, value);
				}
				return map;
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				XSSFWorkbook workbook = null;		
				int lastIndex = 0;
				TimeInterval timer = DateUtil.timer();
				Map<Integer,Pair<String, String>> actualPropIndexMap = new HashMap<>();
				try {
					// 獲取實際用戶
					TCComponent[] executeQuery = TCUtil.executeQuery(window.session, "__D9_Find_Actual_User", new String[] {"item_id"},
							new String[] {UserLoginSecond.getOSSUserInfo().getEmp_no()});
					if (executeQuery.length == 0) {
						MessageBox.post(window.shell,"未获取到實際用戶，请联系管理员", "Info", MessageBox.INFORMATION);
						return;
					}
					String actualUser =  executeQuery[0].getProperty("d9_UserInfo");
					if(StrUtil.isEmptyIfStr(actualUser)) {
						MessageBox.post(window.shell,"未获取到實際用戶，请联系管理员", "Info", MessageBox.INFORMATION);
						return;
					}
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("正在讀取Excel文件... "), window.styledText, infoStyle);
					workbook = new XSSFWorkbook(new FileInputStream(filPath));
					XSSFSheet sheet = workbook.getSheetAt(0);
					String sheetName = sheet.getSheetName().toUpperCase();
					String itemType;
					String tcPreferenceName;
					if(sheetName.startsWith("HP")){
						itemType = "D9_IR_HP";
						tcPreferenceName = "D9_IMPORT_ISSUE_HP_PROP_MAPPING";
					}else if(sheetName.startsWith("DELL")) {
						itemType = "D9_IR_DELL";
						tcPreferenceName = "D9_IMPORT_ISSUE_DELL_PROP_MAPPING";
					}else if(sheetName.startsWith("Lenovo".toUpperCase())) {
						itemType = "D9_IR_LENOVO";
						tcPreferenceName = "D9_IMPORT_ISSUE_LENOVO_PROP_MAPPING";
					}else {
					   TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("表名稱不符合規則 "), window.styledText, errorStyle);
					   return;
					}
					// 读取首选项
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("正在讀取首選項... "), window.styledText, infoStyle);
					List<String> actualProps = TCUtil.getArrayPreference(window.session,TCPreferenceService.TC_preference_site,tcPreferenceName);
					// 檢查是否配置itemId
					// 檢查是否配置發佈狀態
					boolean hasItemId = false;
					String releaseColumn = null;
					String releaseValue = null;
					for(String prop:actualProps) {
						String[] split = prop.split("=");						
						if("d9_IRCustomerIssueNumber".equals(split[1].trim())) {
							hasItemId = true;
						}
						if(split[0].trim().startsWith("$TCRelease")) {
							// 發佈標誌
							releaseColumn = split[1].trim();
							releaseValue = split[2].trim();
						}						
					}
					
					if(!hasItemId) {
						TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("首選項未配置更新標誌"), window.styledText, errorStyle);
						return;
					}
					if(releaseColumn==null) {
						TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("首選項未配置發佈標誌 "), window.styledText, errorStyle);
						return;
					}
					for(int i=0;i<300;i++) {
						Cell cell = sheet.getRow(0).getCell(i);
						if(cell==null) {
							break;
						}
						String excelColumn = ExcelUtils.getCellValueToString(cell);
						if(StrUtil.isEmpty(excelColumn)) {
							break;
						}
						excelColumn = excelColumn.trim();
						String actualProp = getActualProp(actualProps, excelColumn);
						if(actualProp == null) {							
							TCUtil.writeWarnLog(S2TTransferUtil.toTraditionnalString(String.format("第%d列【%s】未配置Mapping表，如无需导入请忽略",i+1,excelColumn)), window.styledText, warnStyle);
							continue;
						}
						lastIndex = i;
						actualPropIndexMap.put(i, new Pair<String,String>(excelColumn,actualProp));
					}
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("開始導入... "), window.styledText, infoStyle);
					for(int i=1;i<Integer.MAX_VALUE;i++) {	
						Map<Integer, Object> rowMap = readRow(sheet.getRow(i),lastIndex);
						if(rowMap.isEmpty()) {
							// 文件尾，返回
							break;
						}
						Set<Integer> keySet = rowMap.keySet();
						Iterator<Integer> iterator = keySet.iterator();
						Map<String,Object> tcMap = new HashMap<>();
						boolean needRelease = false;
						while (iterator.hasNext()) {
							Integer columnIndex = iterator.next();
							Object value = rowMap.get(columnIndex);
							Pair<String, String> pair = actualPropIndexMap.get(columnIndex);
							if(pair==null) {
								continue;
							}
							String excelColnum = pair.getKey();
							if(releaseColumn.equals(excelColnum)&&releaseValue.equals(value)) {
								needRelease = true;
							}
							String d9Props = pair.getValue();
							String[] split = d9Props.split(",");
							for(String prop : split) {
								tcMap.put(prop, value);
							}
						}
						TCComponentItemRevision issueItemRevision = null;
						Object o = tcMap.get("d9_IRCustomerIssueNumber");
						if(o!=null&&StrUtil.isNotEmpty((String)o)) {
							executeQuery = TCUtil.executeQuery(window.session, "00-find_issue_objects", new String[]{"d9_IRCustomerIssueNumber"},new String[]{(String)o});
							if(executeQuery.length>0) {
								issueItemRevision = (TCComponentItemRevision) executeQuery[0];
							}
						}
						try {
							if(issueItemRevision==null) {
								// 創建對象		
								tcMap.put("d9_ActualUserID", actualUser);
								TCComponentItem createCom = (TCComponentItem) TCUtil.createCom(window.session, itemType, "", tcMap);
								if(needRelease) {
									TCComponentItemRevision latestItemRevision = createCom.getLatestItemRevision();
									String itemId = latestItemRevision.getProperty("item_id");
									String revNum = latestItemRevision.getProperty("item_revision_id");
									String workflowName = "issue fast release process：" + itemId + "/" + revNum;
									TCUtil.createProcess("issue fast release process", workflowName, "", new TCComponent[] {latestItemRevision});
								}
								window.folder.add("contents", createCom);
								TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString(String.format("第%d行導入成功!",i)), window.styledText, infoStyle);
							}else {
								// 更新對象
								Set<String> tcMapkeySet = tcMap.keySet();
								Iterator<String> tcMapIterator = tcMapkeySet.iterator();
								boolean updatedOK = true;
								while (tcMapIterator.hasNext()) {									
									String d9Prop = tcMapIterator.next();
									Object value = tcMap.get(d9Prop);
									// 更新屬性
									try {
										if(value instanceof Date) {
											issueItemRevision.setDateProperty(d9Prop, (Date)value);
										}else {
											issueItemRevision.setProperty(d9Prop, (String)value);
										}
									}catch (Exception e) {
										updatedOK = false;
										e.printStackTrace();
										TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString(String.format("第%d行更新【%s】失敗:%s",i,d9Prop,e.getLocalizedMessage())), window.styledText, errorStyle);
									}									
								}
								if(updatedOK) {
									TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString(String.format("第%d行【更新】完成",i)), window.styledText,infoStyle);
								}								
								if(needRelease&&!TCUtil.isReleased(issueItemRevision)) {
									String itemId = issueItemRevision.getProperty("item_id");
									String revNum = issueItemRevision.getProperty("item_revision_id");
									String workflowName = "issue fast release process：" + itemId + "/" + revNum;
									TCUtil.createProcess("issue fast release process", workflowName, "", new TCComponent[] {issueItemRevision});
								}
							}
						}catch (Exception e) {
							e.printStackTrace();
							TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString(String.format("第%d行%s失敗:%s",i,issueItemRevision==null?"導入":"更新",extractLastParenthesesContent(e.toString()))), window.styledText, errorStyle);
						}
						// 繼續讀下一行
						if(isCancel) {
							TCUtil.writeWarnLog(S2TTransferUtil.toTraditionnalString("用戶取消導入"), window.styledText, warnStyle);
							break;
						}
					}
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("導入完成"), window.styledText, infoStyle);
				}catch(Exception e) {
					e.printStackTrace();
					MessageBox.post(window.shell, e.toString(), "ERR", MessageBox.ERROR);
				}finally {
					try {
						if(workbook!=null) {
							workbook.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("耗時："+timer.intervalPretty()), window.styledText, infoStyle);
				}
			}

			@Override
			public void cencel() {				
				isCancel = true;
			}
			
			
		};
		
		new IndeterminateLoadingDialog(window.shell, "導入...", action);
	}
	
	public static String extractLastParenthesesContent(String input) {
		int lastOpeningParenthesesIndex = input.lastIndexOf('（');
	    int lastClosingParenthesesIndex = input.lastIndexOf('）');	   
	    if (lastOpeningParenthesesIndex != -1 && lastClosingParenthesesIndex != -1 && lastClosingParenthesesIndex > lastOpeningParenthesesIndex) {
	        return input.substring(lastOpeningParenthesesIndex + 1, lastClosingParenthesesIndex);
	    }
	    
	    lastOpeningParenthesesIndex = input.lastIndexOf('(');
	    lastClosingParenthesesIndex = input.lastIndexOf(')');
	    if (lastOpeningParenthesesIndex != -1 && lastClosingParenthesesIndex != -1 && lastClosingParenthesesIndex > lastOpeningParenthesesIndex) {
	        return input.substring(lastOpeningParenthesesIndex + 1, lastClosingParenthesesIndex);
	    }	    
	    return input;
	}

}
