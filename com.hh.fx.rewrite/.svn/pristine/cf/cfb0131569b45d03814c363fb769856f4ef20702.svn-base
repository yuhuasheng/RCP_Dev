package com.teamcenter.rac.kernel;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hh.fx.rewrite.util.DownloadDataset;
import com.hh.fx.rewrite.util.ExcelSetValuesUtil;
import com.hh.fx.rewrite.util.FileStreamUtil;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AIFPortal;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
/**
 * 打开excel 写入新数据
 *
 */
public class CustOpenCommand extends OpenCommand{

	private Workbook workbook = null;
	private Sheet sheet = null;
	private HashMap<String,String> mapValues = new HashMap<>();
	FileStreamUtil fileStreamUtil = new FileStreamUtil();
	PrintStream printStream = null;

	GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();

	public CustOpenCommand(AIFDesktop arg0, Dialog arg1, InterfaceAIFComponent arg2) {
		super(arg0, arg1, arg2);
	}

	public CustOpenCommand(AIFPortal paramAIFPortal, InterfaceAIFComponent paramInterfaceAIFComponent) {
		super(paramAIFPortal, paramInterfaceAIFComponent);
	}

	public CustOpenCommand(AIFPortal paramAIFPortal, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent) {
		super(paramAIFPortal, paramArrayOfInterfaceAIFComponent);
	}

	public CustOpenCommand(AIFDesktop paramAIFDesktop, InterfaceAIFComponent paramInterfaceAIFComponent) {
		super(paramAIFDesktop, paramInterfaceAIFComponent);
	}

	public CustOpenCommand(AIFDesktop paramAIFDesktop, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent) {
		super(paramAIFDesktop, paramArrayOfInterfaceAIFComponent);
	}

	public CustOpenCommand(AIFDesktop paramAIFDesktop, Dialog paramDialog,
			InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent) {
		super(paramAIFDesktop, paramDialog, paramArrayOfInterfaceAIFComponent);
	}
	@Override
	public void openDataset(TCComponentDataset arg0) throws Exception {
		super.openDataset(arg0);
	}
}
