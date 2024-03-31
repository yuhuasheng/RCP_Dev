package com.foxconn.mechanism.batchChangePhase;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.foxconn.mechanism.batchChangePhase.window.BatchTransformPhaseWindow;
import com.foxconn.mechanism.batchChangePhase.window.BlueprintBean;
import com.foxconn.mechanism.batchChangePhase.window.PhaseBean;
import com.foxconn.mechanism.batchChangePhase.window.Progress;
import com.foxconn.mechanism.batchChangePhase.window.TransformPhaseService;
import com.foxconn.mechanism.util.ExcelUtils;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

/**
 * @author wt00110 批量转阶段Action类
 */
public class BatchChangePhaseAction extends AbstractAIFAction {
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private BatchChangePhaseOperate mReviseOperate;
	private BatchTransformPhaseWindow batchWindow;

	public BatchChangePhaseAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) this.app.getSession();
		reg = Registry.getRegistry("com.foxconn.mechanism.batchChangePhase.batchChangePhase");
	}

	@Override
	public void run() {
		try {
			// 选中的目标对象
			InterfaceAIFComponent component = this.app.getTargetComponent();
			// 初始化批量转阶段业务操作类
			mReviseOperate = new BatchChangePhaseOperate(session, reg, component);
			// 初始化界面
			batchWindow = new BatchTransformPhaseWindow(reg, new TransformPhaseService() {

				@Override
				public void initData() {
					try {
						mReviseOperate.check();
					} catch (Exception e) {
						MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
						batchWindow.dispose();
					}
				}

				@Override
				public List<PhaseBean> getNotComplianceData() {
					// 返回不符合条件的对象
					return mReviseOperate.getAllNotComplianceData();
				}

				@Override
				public List<PhaseBean> getComplianceData() {
					// 返回符合条件的对象
					return mReviseOperate.getAllComplianceData();
				}

				@Override
				public void doTransformPhase(List<PhaseBean> data, Progress progress) {
					try {

						// 对选中的对象进行批量升版操作
						mReviseOperate.revise(data, progress);

					} catch (Exception e) {
						MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
					}

				}

				@Override
				public boolean checkModelType() {
					return mReviseOperate.checkModelType();
				}

				@Override
				public List<BlueprintBean> getBlueprintData() {
					// 返回选中对象的二位图纸
					return mReviseOperate.getBlueprintData();
				}

				@Override
				public void doUpdatePhase() {
					try {
						// 获取选中的bom行对象
						PhaseBean phaseBean = mReviseOperate.getTopPhaseBean();
						TCComponentItemRevision itemRevison = null;
						itemRevison = phaseBean.getNewItemRevision();
						if (itemRevison == null) {
							itemRevison = phaseBean.getItemRevision();
						}

						TCUserService userService = BatchChangePhaseAction.this.session.getUserService();

						Object[] objs = new Object[2];
						// 顶阶新版本的uid
						objs[0] = itemRevison.getUid();
						// 当前登陆者工号
						objs[1] = BatchChangePhaseAction.this.session.getUser().getUserId();
						// 调用UserService 对对象模型和图纸进行升版操作
						userService.call("sync_draw_version", objs);
					} catch (Exception e) {
						MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
					}
				}

				@Override
				public void exportNotComplianceData(List<PhaseBean> data, String savePath) {
					// 到处不符合条件业务对象列表
					String rp = savePath.substring(0, savePath.lastIndexOf("\\"));
					File rpath = new File(rp);
					if (!rpath.exists()) {
						rpath.mkdirs();
					}
					try {
						// 创建 Workbook
						XSSFWorkbook wb = (XSSFWorkbook) ExcelUtils.createWorkbook("XSSF");
						XSSFSheet sheet = wb.createSheet("ALL");
						int startRow = 0;
						XSSFRow row0 = sheet.createRow(startRow);

						XSSFCell cell0 = row0.createCell(0);
						setValue(cell0, "ID");

						cell0 = row0.createCell(1);
						setValue(cell0, reg.getString("phase.window.notComplianceAdapter.column0"));

						cell0 = row0.createCell(2);
						setValue(cell0, reg.getString("phase.window.notComplianceAdapter.column1"));

						cell0 = row0.createCell(3);
						setValue(cell0, reg.getString("phase.window.notComplianceAdapter.column2"));

						cell0 = row0.createCell(4);
						setValue(cell0, reg.getString("phase.window.notComplianceAdapter.column4"));
						// 输出对象列表到excel
						for (PhaseBean bom : data) {
							startRow++;
							XSSFRow row = sheet.createRow(startRow);
							XSSFCell cell = row.createCell(0);
							setValue(cell, bom.getId());

							cell = row.createCell(1);
							setValue(cell, bom.getVersion());

							cell = row.createCell(2);
							setValue(cell, "" + bom.getName());

							cell = row.createCell(3);
							setValue(cell, bom.getCount());

							cell = row.createCell(4);
							setValue(cell, bom.getOwner());

						}
						// 输出保存 Workbook
						FileOutputStream fos = new FileOutputStream(new File(savePath));
						wb.write(fos);
						fos.flush();
						fos.close();
					} catch (Exception e) {
						MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
					}
				}

				@Override
				public void exportAfterTransformPhaseData(List<PhaseBean> data, String savePath) {
					System.out.print(savePath);
					// 输出执行结果excel
					String rp = savePath.substring(0, savePath.lastIndexOf("\\"));
					File rpath = new File(rp);
					if (!rpath.exists()) {
						rpath.mkdirs();
					}
					try {
						// 创建 Workbook
						XSSFWorkbook wb = (XSSFWorkbook) ExcelUtils.createWorkbook("XSSF");
						XSSFSheet sheet = wb.createSheet("ALL");
						int startRow = 0;
						XSSFRow row0 = sheet.createRow(startRow);

						XSSFCell cell0 = row0.createCell(0);
						setValue(cell0, "ID");

						cell0 = row0.createCell(1);
						setValue(cell0, reg.getString("phase.window.complianceAdapter.column1"));

						cell0 = row0.createCell(2);
						setValue(cell0, reg.getString("phase.window.complianceAdapter.column2"));

						cell0 = row0.createCell(3);
						setValue(cell0, reg.getString("phase.window.complianceAdapter.column3"));

						cell0 = row0.createCell(4);
						setValue(cell0, reg.getString("phase.window.complianceAdapter.column4"));
						// 循环输出结果到excel
						for (PhaseBean bom : data) {
							TCComponentItemRevision itemRevision = bom.getNewItemRevision();
							if (itemRevision == null) {
								continue;
							}
							String itemId = itemRevision.getProperty("item_id");
							String version = itemRevision.getProperty("item_revision_id");
							startRow++;
							XSSFRow row = sheet.createRow(startRow);
							XSSFCell cell = row.createCell(0);
							setValue(cell, itemId);

							cell = row.createCell(1);
							setValue(cell, "" + version);

							cell = row.createCell(2);
							setValue(cell, "" + bom.getName());

							cell = row.createCell(3);
							setValue(cell, bom.getCount());

							cell = row.createCell(4);
							setValue(cell, bom.getOwner());
						}
						// 输出保存 Workbook
						FileOutputStream fos = new FileOutputStream(new File(savePath));
						wb.write(fos);
						fos.flush();
						fos.close();
					} catch (Exception e) {
						MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
					}

				}

				@Override
				public void exportBlueprint(List<BlueprintBean> data, String savePath) {

				}

			});
			// 显示操作界面
			batchWindow.show();

		} catch (Exception e) {
			System.out.print(e.getMessage());
			MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Warning", MessageBox.WARNING);
		}
	}

	/**
	 * @param cell
	 * @param v    对excel 单元格赋值
	 */
	private void setValue(XSSFCell cell, String v) {
		cell.setCellValue(v == null ? "" : v);
	}

}
