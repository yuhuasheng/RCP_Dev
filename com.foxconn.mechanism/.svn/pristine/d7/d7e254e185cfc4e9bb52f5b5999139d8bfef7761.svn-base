package com.foxconn.mechanism.dtpac.matmaintain.dialog;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory.MatrixFatory;
import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public class PACUpdateCostDialog extends Dialog{
	private Registry reg;
	private AbstractAIFUIApplication app;
	private TCSession session;
	private Shell shell;
	private Button okButton = null;	
	private Button cancelButton = null;
	private ProgressMonitorDialog progressMonitorDialog;
	private Map<String, String> costMap;

	public PACUpdateCostDialog(AbstractAIFUIApplication app,Shell parentShell) {
		super(parentShell);
		this.app = app;
		this.reg	= Registry.getRegistry("com.foxconn.mechanism.dtpac.dtpac");
		this.session = (TCSession) this.app.getSession();
		this.shell = parentShell;
		initUI();
	}
	
	private void initUI() {
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE );
		shell.setSize(200, 120);
		shell.setText(reg.getString("pacUpdateCost.Title"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}
		// 全局設置成一列
		GridLayout grid = new GridLayout(1,false);
		shell.setLayout(grid);
		
		// 下面每一個setLayoutData元素佔據一行
		Label label1 = new Label(shell, SWT.BACKGROUND );
		label1.setText("");
		label1.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,2,1));
		
		Label label = new Label(shell, SWT.BACKGROUND );
		label.setText(reg.getString("pacUpdateCost.Content")); // 设置不可编辑
		label.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,false,false,3,1));
		
		Label label2 = new Label(shell, SWT.BACKGROUND );
		label2.setText("");
		label2.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,4,1));
		
		Composite bottomBtnComposite = new Composite(shell, SWT.NONE);
		
		GridData bottomBtnData = new GridData(SWT.CENTER,SWT.CENTER,true,true);
		bottomBtnData.horizontalAlignment = GridData.FILL; 		
		bottomBtnComposite.setLayoutData(bottomBtnData);
		
		RowLayout bottomRowLayout = new RowLayout(); 
		bottomRowLayout.spacing = 80;
		bottomBtnComposite.setLayout(bottomRowLayout);
		
		RowData bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 60;
		bottomBtnRowData.height = 30;
		
		okButton = new Button(bottomBtnComposite, SWT.PUSH);
		okButton.setText(reg.getString("pacUpdateCost.okBtn"));
		okButton.setLayoutData(bottomBtnRowData);
		//okButton.setEnabled(false); // 设置为不可以点击		
		
		cancelButton = new Button(bottomBtnComposite, SWT.NONE);
		cancelButton.setText(reg.getString("pacUpdateCost.cancelBtn"));
		cancelButton.setLayoutData(bottomBtnRowData);
		//ok1Button.setEnabled(false);
		
		addListener(); // 添加事件监听
		
		shell.pack();
		shell.open();
		shell.layout();
	

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	private void addListener() {
		
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					// 執行業務邏輯
					IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							monitor.beginTask("更新成本中, 请等待...", 100);
							try {
								String filePath = PACMatMaintainDialog.downloadTemplate(session, reg);
								if (CommonTools.isEmpty(filePath)) {
									TCUtil.warningMsgBox(reg.getString("DownLoadingErr.MSG"), reg.getString("ERROR.MSG"));
								}else {
									Map<String, List<ExcelBean>> retMap = PACMatMaintainDialog.parseExcelTemplate(filePath);
									if (CommonTools.isEmpty(retMap)) {
										TCUtil.warningMsgBox(reg.getString("TemplateParseErr.MSG"), reg.getString("ERROR.MSG"));
									}else {
										costMap = new HashMap<String, String>(retMap.get("matCostBeanList").size());
										for (ExcelBean bean : retMap.get("matCostBeanList")) {
											costMap.put(bean.getMaterial(), bean.getCostFactor());
										}
										updateCost(monitor);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							monitor.done();
							progressMonitorDialog.setBlockOnOpen(false);
							
							
						}
					};
					progressMonitorDialog = new ProgressMonitorDialog(AIFUtility.getActiveDesktop().getShell());
					progressMonitorDialog.run(true, false, iRunnableWithProgress);
					
					
					MessageDialog.openInformation(null, reg.getString("INFORMATION.MSG"), reg.getString("pacUpdateCost.success"));
				} catch (Exception e1) {
					progressMonitorDialog.setBlockOnOpen(false);
					e1.printStackTrace();
				}finally {
					shell.dispose();
				}

			}
			
		});
		
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
			
		});
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				shell.dispose();
			}
		});
	}
	
	private void updateCost(IProgressMonitor monitor) throws TCException {
		TCComponentItem item = (TCComponentItem) app.getTargetComponent();
		item.refresh();
		TCComponent[] itemRevList = item.getRelatedComponents("revision_list");
		for (int i = 0; i < itemRevList.length; i++) {
			TCComponentItemRevision itemRevision =(TCComponentItemRevision) itemRevList[i];
			TCComponent[] relateds = itemRevision.getRelatedComponents("view");
			if(ObjectUtil.isNotNull(relateds) && relateds.length > 0) {
				// 打開視圖開始進行計算
				openBOMWindow(itemRevision);
			}else {
				// 计算物料本身的成本
				updatePacCost(itemRevision);
			}
			monitor.worked(100/itemRevList.length);
		}
	}
	
	private void openBOMWindow(TCComponentItemRevision itemRevision) throws TCException {
		TCComponentBOMWindow bomwindow = null;
		try {
			bomwindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomline = bomwindow.setWindowTopLine(itemRevision.getItem(), itemRevision, null, null);
			updateBomLineCast(topBomline);
		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			bomwindow.close();
		}
	}
	
	private Double updateBomLineCast(TCComponentBOMLine bomline) throws TCException {
		// 更新當前物料的表單成本信息
		TCComponentItemRevision itemRevision = bomline.getItemRevision();
		Double cost = updatePacCost(itemRevision);
		AIFComponentContext[] childrens = bomline.getChildren();
		if (childrens != null && childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String qty = children.getProperty("bl_quantity");
				cost += NumberUtil.mul(updateBomLineCast(children).toString(), StrUtil.isNotBlank(qty)? qty: "1").doubleValue();
			}
		}
		if(cost > 0) {
			itemRevision.setProperty("d9_PartCost", NumberUtil.roundStr(cost,2));
		}
		return cost;
	}
	
	private Double updatePacCost(TCComponentItemRevision itemRevision) throws TCException {
		String matType = itemRevision.getProperty("d9_PartType2");
		String matName = itemRevision.getProperty("d9_PartType3");
		
		TCComponent[] relatedComponents = itemRevision.getRelatedComponents("d9_PACMaterialTable");
		Double total = 0D;
		for (int i = 0; i < relatedComponents.length; i++) {
			TCComponent component = relatedComponents[i];
			ExcelBean bean = getBean(component,matType,matName);
			MatrixFatory.calculateCost(bean);
			component.setProperty("d9_CostFactor", bean.getCostFactor());
			component.setProperty("d9_Cost", bean.getCost());
			if(StrUtil.isNotBlank(bean.getCost())) {
				total += Double.parseDouble(bean.getCost()) ;
			}
		}
		if(total > 0) {
			itemRevision.setProperty("d9_PartCost", NumberUtil.roundStr(total,2));
		}
		return total;
	}
	
	private ExcelBean getBean(TCComponent component,String matType,String matName) throws TCException {
		String[] properties = new String[] {"d9_Type","d9_Material","d9_Length","d9_Width","d9_Height","d9_Thickness","d9_Density",
				"d9_Quantity","d9_CostFactor","d9_Cost","d9_Weight"};
		String[] values = component.getProperties(properties);
		component.refresh();
		ExcelBean bean = new ExcelBean();
		bean.setMatType(matType);
		bean.setMatName(matName);
		bean.setType(values[0]);
		bean.setMaterial(values[1]);
		bean.setLength(values[2]);
		bean.setWidth(values[3]);
		bean.setHeight(values[4]);
		bean.setThickness(values[5]);
		bean.setDensity(values[6]);
		bean.setQty(values[7]);
		bean.setCostFactor(StrUtil.isNotBlank(values[8]) ? values[8] : "");
		bean.setCost(StrUtil.isNotBlank(values[9]) ? values[9] : "");
		bean.setWeight(values[10]);
		if(ObjectUtil.isNotNull(costMap) &&StrUtil.isNotBlank(costMap.get(bean.getMaterial()))) {
			bean.setCostFactor(costMap.get(bean.getMaterial()));
		}
		return bean;
	}
	
}
