package com.foxconn.mechanism.hhpnmaterialapply.export;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTBOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.IExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.DTExportOperation;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.DTExportTools;
import com.foxconn.mechanism.hhpnmaterialapply.message.MessageShow;
import com.foxconn.mechanism.hhpnmaterialapply.progress.BooleanFlag;
import com.foxconn.mechanism.hhpnmaterialapply.progress.IProgressDialogRunnable;
import com.foxconn.mechanism.hhpnmaterialapply.progress.LoopProgerssDialog;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class PartListExportDialog extends Dialog {

	private AbstractPSEApplication app = null;
	private TCSession session = null;
	private Shell shell = null;
	private Registry reg = null; 
	
	public PartListExportDialog(AbstractPSEApplication app, Shell parent, String bu, Registry reg)
			throws TCException, NotLoadedException {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;

		InterfaceAIFComponent target = app.getTargetComponent();
		if (target instanceof TCComponentBOMLine) {
			TCComponentBOMLine topBOMLine = (TCComponentBOMLine) target;
			System.out.println("bu -->>>> " + bu);
			File exportFile = TCUtil.openSaveFileDialog(shell, IExportServices.getDefaultFileName(topBOMLine));
			if (CommonTools.isEmpty(exportFile)) {
				return;
			}
			LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null,
					bu + " " + reg.getString("partListExportProgress.TITLE"));
			loopProgerssDialog.run(true, new IProgressDialogRunnable() {

				@Override
				public void run(BooleanFlag stopFlag) {
					try {
						if (doExport(topBOMLine, exportFile, bu)) {
							stopFlag.setFlag(true); // 执行完毕后把标志位设置为停止，好通知给进度框
							MessageShow.infoMsgBox(reg.getString("partListExportSuccess.LABEL"), reg.getString("INFORMATION.MSG"));
						}
					} catch (Exception e) {
						e.printStackTrace();
						TCUtil.errorMsgBox("export fail !  cause : " + e.getLocalizedMessage());
						stopFlag.setFlag(true);
					}
				}
			});
		} else {
			TCUtil.errorMsgBox("Please select BOM Line!");
		}
	}

	public boolean doExport(TCComponentBOMLine topBOMLine, File exportFile, String bu) throws Exception {
		List<TCComponentBOMLine> bomLines = null;
		Date startDate = new Date();
		if ("PRT".equals(bu) || "MNT".equals(bu)) {
			bomLines = TCUtil.getTCComponmentBOMLines(topBOMLine, null, false);
			IExportServices exportServices = IExportServices.getInstance(bu);
			exportServices.setBOMLines(topBOMLine, bomLines);
			exportServices.dataHandle();
			exportServices.generatePartsExcel(exportFile);
		} else if ("DT".equals(bu)) {

			List<DTBOMInfo> list = null;
			list = DTExportTools.getBOMLineList(list, topBOMLine, false);
			list.removeIf(
					info -> CommonTools.isEmpty(info.getPartType()) || CommonTools.isEmpty(info.getOptionalType()));
			IExportServices exportServices = IExportServices.getInstance(bu);
			DTExportOperation dtExportOperation = new DTExportOperation(list, topBOMLine);
			dtExportOperation.dataHandle();
			boolean flag = dtExportOperation.generatePartsExcel(exportFile);
			if (!flag) {
				return false;
			}
			System.out.println(exportFile.getPath());
		}

		Date endDate = new Date();
		IExportServices.writeLog(topBOMLine.getItemRevision(), startDate, endDate, "人工編制PartList時間");
		return true;
	}
}
