package com.foxconn.electronics.issuemanagement.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ResourceBundle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.foxconn.electronics.managementebom.export.changelist.rf.ExportRfChangeList;
import com.foxconn.electronics.util.ProgressBarThread;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;

public class SubmitToRPARunnable implements Runnable{
	
	private TCComponentItemRevision itemRevision;
	private ResourceBundle reg;

	public SubmitToRPARunnable(TCComponentItemRevision itemRevision,ResourceBundle reg) {
		super();
		this.itemRevision = itemRevision;
		this.reg = reg;
	}

	@Override
	public void run() {
		ProgressBarThread barThread = new ProgressBarThread(reg.getString("wait.MSG"), reg.getString("export.MSG"));
		Sftp sftp = null;
		try {
			barThread.start();
			sftp = JschUtil.createSftp("10.203.163.232", 22, "rpa", "Foxconn@123456");
			InputStream inputStream = ExportRfChangeList.class.getResourceAsStream("/com/foxconn/electronics/issuemanagement/Dell Issue RPA Temp.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFCellStyle cellStyle = initStyle(workbook,true);
			// 獲取對象的屬性
			String[] values = itemRevision.getProperties(new String[] {"d9_ActualUserID","d9_IRAnalyst","d9_IRCustomerIssueNumber","item_id","d9_IRStatus","d9_IROriginatingVendor",
					"d9_IROriginatingGroup","d9_IRLOBFound","d9_IRPlatformFoundDell","d9_IRCategory","d9_IRGroupActivity","d9_IRGroupLocation",
					"d9_IRPhaseFoundDell","d9_IRHardwareBuildVersion","d9_IRDiscoveryMethod","d9_IRTestCaseNumberRequired","d9_IRCommodity","d9_IRComponent",
					"d9_IRProductImpact","d9_IRCustomerImpactDell","d9_IRLikelihood","d9_IRAffectedOS","d9_IRAffectedLanguages","d9_IRAffectedItems",
					"d9_IRName","d9_IRLongDescription","d9_IRStepsToReproduce"});
			XSSFRow row = sheet.createRow(1);
			for (int i = 0; i < values.length; i++) {
				setCellValue(row,i,cellStyle,values[i]);
			}
			File file = new File(values[3] + ".xlsx");
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.flush();
			IoUtil.close(out);
			
			
			sftp.cd("/Dell Issue");
			if(!sftp.exist("/Dell Issue/" + values[3])) {
				sftp.mkdir(values[3]);
			}
			FileInputStream in = new FileInputStream(file);
			sftp.upload("/Dell Issue/" + values[3], values[3]+ ".xlsx",in);
			
			IoUtil.close(in);
			// 查詢是否有附件
			AIFComponentContext[] relatedList = itemRevision.getRelated(new String[] {"ImageBeforeFix","CMReferences","CMHasProblemItem","IsM0SnapshotBeforeFix","IsM0SnapshotAfterFix",
					"IsM0IssueSubset","CMHasImpactedItem","CMReferences","ImageAfterFix"});
			for (int i = 0; i < relatedList.length; i++) {
				AIFComponentContext componentContext = relatedList[i];
				InterfaceAIFComponent component = componentContext.getComponent();
				if(!(component instanceof TCComponentDataset)) {
					continue;
				}
				TCComponentDataset dataset = (TCComponentDataset)component;
				String  name = dataset.getProperty("object_name");
				if(!sftp.exist("/Dell Issue/" + values[3] + "/" + name)) {
					File file2 = TCUtil.getDataSetFile(dataset);
					FileInputStream fileInputStream = new FileInputStream(file2);
					sftp.upload("/Dell Issue/" + values[3], name , fileInputStream);
					IoUtil.close(fileInputStream);
				}
			}
			IoUtil.close(inputStream);
			TCUtil.infoMsgBox(reg.getString("exportSuccess.MSG"), reg.getString("INFORMATION.MSG"));
		} catch (Exception e) {
			TCUtil.infoMsgBox(reg.getString("exportFailure.MSG"), reg.getString("ERROR.MSG"));
			e.printStackTrace();
		}finally {
			sftp.close();
			barThread.stopBar();
		}
		
	}
	
	private void setCellValue(XSSFRow row,int cellIndex,XSSFCellStyle cellStyle,String value) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
	}
	
	
	private static XSSFCellStyle initStyle(XSSFWorkbook workbook, Boolean border) {
        XSSFCellStyle cellStyle = workbook.getCellStyleAt(0);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        if (border) {
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
        }
        return cellStyle;
    }

}
