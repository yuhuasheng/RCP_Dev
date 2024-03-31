package com.foxconn.electronics.matrixbom.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.namedreferences.ExportFilesOperation;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class MatrixBOMExportHandler extends AbstractHandler{
	public final static String tempPath = System.getProperty("java.io.tmpdir");
	 
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			
			TCComponent targetComponent = (TCComponent) app.getTargetComponent();
			TCComponentItem documentItem = null;
			String file = null;
			TCComponentItemRevision latestItemRevision = null;
			if(targetComponent instanceof TCComponentItemRevision) {
				String changeDescription = targetComponent.getProperty("d9_ChangeDescription");
				TCComponent[] solutionItems = targetComponent.getRelatedComponents("CMHasSolutionItem");
				if(solutionItems!=null && solutionItems.length > 0) {
					for (TCComponent solutionItem:solutionItems) {
						if(solutionItem instanceof TCComponentItemRevision) {
							TCComponentItemRevision itemRev = (TCComponentItemRevision) solutionItem;
							String objectType = itemRev.getTypeObject().getName();
							if("DocumentRevision".equals(objectType)) {
								documentItem = itemRev.getItem();
								latestItemRevision = documentItem.getLatestItemRevision();
								latestItemRevision.setProperty("d9_ChangeDescription",changeDescription);
								latestItemRevision.setProperty("object_desc","update from dcn");
								TCComponent[] imanSpecifications = latestItemRevision.getRelatedComponents("IMAN_specification");
								if(imanSpecifications!=null && imanSpecifications.length > 0) {
									for (TCComponent imanSpecification:imanSpecifications) {
										String specificationType = imanSpecification.getTypeObject().getName();
										if ("MSExcelX".equals(specificationType)) {
											TCComponentTcFile[] tcfiles = ((TCComponentDataset) imanSpecification).getTcFiles();
											if (tcfiles != null && tcfiles.length > 0) {
												String fileName = tcfiles[0].getProperty("original_file_name");
												System.out.println("fileName = " + fileName);
												
												ExportFilesOperation expTemp = new ExportFilesOperation((TCComponentDataset) imanSpecification, tcfiles, tempPath, null);
												expTemp.executeOperation();
											
												file = tempPath + "\\" + fileName;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			if(documentItem != null && file != null) {
				ExcelWriter writer = ExcelUtil.getWriter(file);
				writer.setSheet("History");
				
				TCComponent[] revision_list = documentItem.getRelatedComponents("revision_list");
				if(revision_list!=null && revision_list.length > 0) {
					String matrixType = null;
					AIFComponentContext[] related = ((TCComponentItemRevision)revision_list[0]).whereReferencedByTypeRelation(new String[] {"D9_ProductLineRevision"},new String[] {"IMAN_specification"});
					if(related != null && related.length > 0) {
						TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision) related[0].getComponent();
						matrixType = matrixItemRevision.getProperty("d9_MatrixType");
					}
					
					if("DT Lenovo Thermal Matrix".equals(matrixType)) {
						writerThermalExcel(revision_list, writer);
					}else {
						writerOtherExcel(revision_list, writer);
					}
					
					
				}
				writer.getWorkbook().removeSheetAt(writer.getSheetCount() - 1);
				writer.close();
				
				com.foxconn.tcutils.util.TCUtil.updateDataset(session, documentItem.getLatestItemRevision(), "IMAN_specification", file.toString());
				
				MessageBox.post("更新 Matrix History 完成！", "提示", MessageBox.WARNING);
				return null;
			}
			
			//new MatrixBOMExportDialog(app.getDesktop().getShell(),targetComponent,app);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	
	public void writerThermalExcel(TCComponent[] revision_list,ExcelWriter writer) throws TCException {
		for (int i = 0; i < revision_list.length; i++) {
			int rowint = i+2;
			String item_revision_id = revision_list[i].getProperty("item_revision_id");
			String d9_ChangeDescription = revision_list[i].getProperty("d9_ChangeDescription");
			Date date = revision_list[i].getTCProperty("last_mod_date").getDateValue();
			String last_mod_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
			String actualUser = revision_list[i].getProperty("d9_ActualUserID") == null ? "" : revision_list[i].getProperty("d9_ActualUserID").trim();
			
			//Sheet sheet = writer.getSheet();
			//insertRow((XSSFSheet) sheet, rowint, 1);
			Cell cell = writer.getCell(1, rowint);
			if(cell != null) {
				CellStyle cellStyle = cell.getCellStyle();
				cellStyle.setWrapText(true);
				writer.getCell(1, rowint).setCellStyle(cellStyle);
			}
			
			writer.writeCellValue(0, rowint, item_revision_id);
			writer.writeCellValue(1, rowint, d9_ChangeDescription);
			writer.writeCellValue(2, rowint, actualUser);
			writer.writeCellValue(3, rowint, last_mod_date);
			
		}
	}
	
	public void writerOtherExcel(TCComponent[] revision_list,ExcelWriter writer) throws TCException {
		for (int i = 0; i < revision_list.length; i++) {
			int rowint = i+4;
			String item_revision_id = revision_list[i].getProperty("item_revision_id");
			String d9_ChangeDescription = revision_list[i].getProperty("d9_ChangeDescription");
			Date date = revision_list[i].getTCProperty("last_mod_date").getDateValue();
			String last_mod_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
			String actualUser = revision_list[i].getProperty("d9_ActualUserID") == null ? "" : revision_list[i].getProperty("d9_ActualUserID").trim();
			
			//Sheet sheet = writer.getSheet();
			//insertRow((XSSFSheet) sheet, rowint, 1);
			System.out.println("rowint = "+rowint);
			Cell cell = writer.getCell(1, rowint);
			if(cell != null) {
				CellStyle cellStyle = cell.getCellStyle();
				cellStyle.setWrapText(true);
				writer.getCell(1, rowint).setCellStyle(cellStyle);
			}
			
			writer.writeCellValue(0, rowint, item_revision_id);
			writer.writeCellValue(1, rowint, d9_ChangeDescription);
			writer.writeCellValue(2, rowint, last_mod_date);
			writer.writeCellValue(3, rowint, actualUser);
		}
	}
	
	
	public void insertRow(XSSFSheet sheet, int startRow, int rows) {
		sheet.shiftRows(startRow, sheet.getLastRowNum(), rows, true, false);
		for (int i = 0; i < rows; i++) {
			XSSFRow sourceRow = null;// 原始位置
			XSSFRow targetRow = null;// 移动后位置
			XSSFCell sourceCell = null;
			XSSFCell targetCell = null;
			sourceRow = sheet.createRow(startRow);
			targetRow = sheet.getRow(startRow + rows);
			sourceRow.setHeight(targetRow.getHeight());

			for (int m = targetRow.getFirstCellNum(); m < targetRow.getLastCellNum(); m++) {
				sourceCell = sourceRow.createCell(m);
				targetCell = targetRow.getCell(m);
				sourceCell.setCellStyle(targetCell.getCellStyle());
				sourceCell.setCellType(targetCell.getCellType());
			}
			startRow++;
		}
	}
}
