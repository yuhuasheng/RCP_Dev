package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.export.domain.DTLenovoPSUBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class DTLenovoPSUListExcelWriter implements IMatrixBOMExportWriter {

	private static final String EXCEL_TEMPLATE = "com/foxconn/electronics/matrixbom/export/template/DT_Lenovo_PSU_List_Template.xlsx";
	
	private static final int STARTROW = 3;
	
	public final static int COLLENGTH = 19;
	
	private final static int ROWHEIGHT = 160;
	
	public List<DTLenovoPSUBean> getMaterialList(TCSession session, TCComponentItemRevision itemRev) {
		List<DTLenovoPSUBean> list = new ArrayList<DTLenovoPSUBean>();
		TCComponentBOMWindow window = null;
		try {
			window = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topLine = TCUtil.getTopBomline(window, itemRev);
			AIFComponentContext[] children = topLine.getChildren();
			int index = 1;
			for (AIFComponentContext context : children) {
				TCComponentBOMLine child = ((TCComponentBOMLine) context.getComponent());
				if (!child.isSubstitute()) {
					list.add(new DTLenovoPSUBean(child, index));
					index++;
				}
				
				if (child.hasSubstitutes()) {
					TCComponentBOMLine[] listSubstitutes = child.listSubstitutes();
					for (TCComponentBOMLine subBomLine : listSubstitutes) {
						list.add(new DTLenovoPSUBean(subBomLine, index));
						index++;
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (window != null) {
				try {
					window.close();
				} catch (TCException e1) {
					e1.printStackTrace();
				}
			}
		}

		return list;
	}
	
	
	public String exportExcel(TCComponentItemRevision itemRev, List<DTLenovoPSUBean> list) throws Exception {
		OutputStream out = null;		
		String dir = CommonTools.getFilePath("BB4_PSU_List"); // 查找保存PSU_List文件的文件夹
		System.out.println("【INFO】 dir: " + dir);		
		CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
		
		String objectName = itemRev.getProperty("object_name");
		String revName = itemRev.getProperty("item_revision_id");
		File templateFile = new File(dir + File.separator + objectName.replace(" ","_") + "_" + revName + ".xlsx");
		
		ExcelUtil excelUtil = new ExcelUtil();
		Workbook wb = excelUtil.getWorkbook(EXCEL_TEMPLATE);
		CellStyle cellStyle = excelUtil.getCellStyle2(wb);
		Sheet sheet = wb.getSheet("BB4 PSU List");
		excelUtil.setCellValue2(list, STARTROW, COLLENGTH, sheet, cellStyle);
		out = new FileOutputStream(templateFile);
		wb.write(out);
		out.flush();
		out.close();	
		return templateFile.getAbsolutePath();
	}
	
	@Override
	public MatrixBOMExportBean assembleMatixChangeBean(TCComponentBOMLine impactBOMLine,
			TCComponentBOMLine sulotionBomLine, String changeType) throws Exception {
		return null;
	}

	@Override
	public MatrixBOMExportBean assembleMatixBOMBean(IMatixBOMBean bean, List<String> varList, VariableBOMBean varBean,
			List<PicBean> picList) throws Exception {
		return null;
	}

	@Override
	public String writerDCN(TCComponentItemRevision matrixItemRevision, String dcnNo, List<MatrixBOMExportBean> list)
			throws Exception {
		return null;
	}

	@Override
	public String writerBOM(TCComponentItemRevision matrixItemRevision, List<MatrixBOMExportBean> list,
			ArrayList<String> varList) throws Exception {
		return null;
	}

	@Override
	public boolean equals(TCComponentBOMLine bomLine1, TCComponentBOMLine bomLine2) throws TCException {
		return false;
	}

	@Override
	public String createDCNwriterDCN(TCComponentItemRevision matrixItemRevision, String dcnNo,
			List<MatrixBOMExportBean> list, String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setHistory(TCSession session, TCComponentItemRevision matrixItemRevision, String excelPath) {
	}

}
