package com.teamcenter.rac.common.actions;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.hh.fx.rewrite.util.ProgressBarThread;
import com.hh.fx.rewrite.util.Utils;
import com.hh.fxn.utils.GetPreferenceUtil;
import com.hh.tools.newitem.RelationName;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.aifrcp.MessagePopupAction;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;


public class UserNewProcessAction extends NewProcessAction {

	private Registry reg = null;
	public UserNewProcessAction(
			AbstractAIFApplication paramAbstractAIFApplication,
			Frame paramFrame, String paramString) {
		super(paramAbstractAIFApplication, paramFrame, paramString);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewProcessAction 1");
	}

	public UserNewProcessAction(
			AbstractAIFUIApplication paramAbstractAIFUIApplication,
			String paramString) {
		super(paramAbstractAIFUIApplication, paramString);
		// TODO Auto-generated constructor stub				
		System.out.println("paramString ==" + paramString);
		System.out.println("UserNewProcessAction 2");		
		
	}

	@Override
	public void run() {
		InterfaceAIFComponent[] components = this.application.getTargetComponents();
		reg = Registry.getRegistry("com.teamcenter.rac.common.actions.actions");
		try {
			if(components!=null&&components.length>0){
				for (InterfaceAIFComponent interfaceAIFComponent : components) {
					String type = interfaceAIFComponent.getType();
					System.out.println("type ==" + type);
					if("FX8_MECHDgnDRevision".equals(type)||"FX8_SMDgnDRevision".equals(type)
							||"FX8_PlasticDgnDRevision".equals(type)){				
						TCComponentItemRevision itemRev = (TCComponentItemRevision)interfaceAIFComponent;
						TCComponent[] coms = itemRev.getRelatedComponents(RelationName.CHECKLIST);
						if(coms!=null&&coms.length>0){
							for (TCComponent tcComponent : coms) {
								String formType = tcComponent.getType();
								if("FX8_DgnRVWForm".equals(formType)){
									TCProperty prop = tcComponent.getTCProperty("fx8_DgnRVWTable");
									TCComponent[] componrntArray = prop.getReferenceValueArray();
									if(componrntArray!=null&&componrntArray.length>0){
										for (int i = 0; i < componrntArray.length; i++) {
											TCComponent com = componrntArray[i];										
											boolean isDo = com.getLogicalProperty("fx8_IsDo");
											if(!isDo){
												Object[] options1 ={ "确认", "取消" };  	
												int n = JOptionPane.showOptionDialog(null, "当前对象3D&2D Drawing Review Checklist"+"\n"+"有未完成项，是否继续发起流程？", "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
												if (n == JOptionPane.YES_OPTION) {
													super.run();
													return;
												}else{
													return;
												}
											}
										}
									}
									
								}else if("FX8_DgnReleasedForm".equals(formType)){
									TCProperty prop = tcComponent.getTCProperty("fx8_DgnReleasedTable");
									TCComponent[] componrntArray = prop.getReferenceValueArray();
									if(componrntArray!=null&&componrntArray.length>0){
										for (int i = 0; i < componrntArray.length; i++) {
											TCComponent com = componrntArray[i];										
											boolean isDo = com.getLogicalProperty("fx8_IsDo");
											if(!isDo){
												Object[] options1 ={ "确认", "取消" };  	
												int n = JOptionPane.showOptionDialog(null, "当前对象3D Drawing Release checklist"+"\n"+"有未完成项，是否继续发起流程？", "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
												if (n == JOptionPane.YES_OPTION) {
													super.run();
													return;
												}else{
													return;
												}
											}
										}
									}
								}else if("FX8_CustomerRVWForm".equals(formType)){
									TCProperty prop = tcComponent.getTCProperty("fx8_CustomerRVWTable");
									TCComponent[] componrntArray = prop.getReferenceValueArray();
									if(componrntArray!=null&&componrntArray.length>0){
										for (int i = 0; i < componrntArray.length; i++) {
											TCComponent com = componrntArray[i];										
											boolean isDo = com.getLogicalProperty("fx8_IsDo");
											if(!isDo){
												Object[] options1 ={ "确认", "取消" };  	
												int n = JOptionPane.showOptionDialog(null, "当前对象Design Review Checklist"+"\n"+"有未完成项，是否继续发起流程？", "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
												if (n == JOptionPane.YES_OPTION) {
													super.run();
													return;
												}else{
													return;
												}
											}
										}
									}
								}else if("FX8_SampleRVWForm".equals(formType)){
									TCProperty prop = tcComponent.getTCProperty("fx8_SampleRVWTable");
									TCComponent[] componrntArray = prop.getReferenceValueArray();
									if(componrntArray!=null&&componrntArray.length>0){
										for (int i = 0; i < componrntArray.length; i++) {
											TCComponent com = componrntArray[i];										
											boolean isDo = com.getLogicalProperty("fx8_IsDo");
											if(!isDo){
												Object[] options1 ={ "确认", "取消" };  	
												int n = JOptionPane.showOptionDialog(null, "当前对象Sample Review Checklist"+"\n"+"有未完成项，是否继续发起流程？", "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
												if (n == JOptionPane.YES_OPTION) {
													super.run();
													return;
												}else{
													return;
												}
											}
										}
									}
								}
								
							}
						}
						
					}
					//原理图走快速发布时，提示用户是否做过检查-by 汪亚洲
					else if("EDASchem Revision".equals(type)){
						int res = JOptionPane.showConfirmDialog(null, reg.getString("IsContinueCheck.MSG"), reg.getString("IsContinueTitle.MSG"), JOptionPane.YES_NO_CANCEL_OPTION);
						System.out.println("res ==" + res);
						if(res == JOptionPane.NO_OPTION || res == JOptionPane.CANCEL_OPTION || res == JOptionPane.CLOSED_OPTION){
							return;
						}
					} 
					//以下对象在发起流程时，如果已经在DCN中，则不允许单独发布流程
					else if (type.equals("FX8_MfrRevision") || type.equals("FX8_MISCDgnDRevision")
							|| type.equals("EDAComp Revision") || type.equals("FX8_ELECDgnDocRevision")
							|| type.equals("FX8_ARTWRKDgnDRevision") || type.equals("FX8_ARTWRKDgnDocRevision")
							|| type.equals("FX8_IDDgnDRevision") || type.equals("FX8_IDDgnDocRevision")
							|| type.equals("FX8_RFDgnDRevision") || type.equals("FX8_EMCTestDocRevision")
							|| type.equals("FX8_EnergyEffDocRevision") || type.equals("FX8_ENVRComplDocRevision")
							|| type.equals("FX8_RFDgnDocRevision") || type.equals("FX8_ApproveSheetRevision")
							|| type.equals("FX8_DgnSpecRevision") || type.equals("FX8_TestRPTRevision")
							|| type.equals("FX8_ELECSimRPTRevision") || type.equals("FX8_SYSSimRPTRevision")
							|| type.equals("FX8_MECHDgnDocRevision") || type.equals("FX8_PKGDgnDocRevision")
							|| type.equals("FX8_SWDgnDocRevision") || type.equals("FX8_SYSDgnDocRevision")
							|| type.equals("FX8_PWRDgnDocRevision") || type.equals("FX8_TADgnDocRevision")
							|| type.equals("FX8_CERTDocRevision") || type.equals("FX8_RFDocRevision")
							|| type.equals("FX8_BIOSDgnDocRevision") || type.equals("FX8_DrvDgnDocRevision")
							|| type.equals("FX8_BIOSDgnDRevision") || type.equals("FX8_DrvDgnDRevision")
							|| type.equals("FX8_ImageDgnDRevision") || type.equals("FX8_DiagDgnDRevision")
							|| type.equals("FX8_MECHDgnDRevision") || type.equals("FX8_SMDgnDRevision")
							|| type.equals("FX8_PlasticDgnDRevision") || type.equals("FX8_ScrewDgnDRevision")
							|| type.equals("FX8_StdoffDgnDRevision") || type.equals("FX8_MyLarDgnDRevision")
							|| type.equals("FX8_LabelDgnDRevision") || type.equals("FX8_RubberDgnDRevision")
							|| type.equals("FX8_RivetDgnDRevision") || type.equals("FX8_GasketDgnDRevision")
							|| type.equals("FX8_SYSDgnDRevision") || type.equals("FX8_CBLDgnDRevision")
							|| type.equals("FX8_PWRDgnDRevision") || type.equals("FX8_TADgnDRevision")
							|| type.equals("FX8_ACSTDgnDRevision") || type.equals("FX8_PKGDgnDRevision")) {
						TCComponentItemRevision itemRev = (TCComponentItemRevision)interfaceAIFComponent;
						AIFComponentContext[] aif = itemRev.getPrimary();
						for (int i = 0; i < aif.length; i++) {
							TCComponent com = (TCComponent) aif[i].getComponent();
							if(com instanceof TCComponentItem && com.getType().equals("FX8_RDDCN")){
								MessageBox.post(AIFUtility.getActiveDesktop(),reg.getString("ErrorDCN.MSG"),"Warning",MessageBox.WARNING);
								return;
							}
						}
					}
					//可以使用系统标准handler限制
//					//L6检验工艺发起流程时，需要在关联文件的文件夹内同時上傳PDF檔和EXCELL檔，否则不予发起流程
//					GetPreferenceUtil util = new GetPreferenceUtil();
//					String[] checkTypes = util.getArrayPreference(Utils.getTCSession(), TCPreferenceService.TC_preference_site, "FX_ProcessCheckHasData");
//					List<String> list = Arrays.asList(checkTypes);
//					if(list.contains(type)){
//						TCComponentItemRevision itemRev = (TCComponentItemRevision)interfaceAIFComponent;
//						TCComponent[] coms = itemRev.getRelatedComponents("FX8_RelateFileRel");
//						boolean excelFlag = false;
//						boolean pdfFlag = false;
//						for (int i = 0; i < coms.length; i++) {
//							if(coms[i] instanceof TCComponentDataset){
//								TCComponentDataset dataset = (TCComponentDataset) coms[i];
//								String dataType = dataset.getType();
//								System.out.println("dataType ==" + dataType);
//								if(dataType.equals("MSExcel") || dataType.equals("MSExcelX")){
//									excelFlag = true;
//								}if(dataType.equals("PDF")){
//									pdfFlag = true;
//								}
//							}
//						}
//						if(coms.length < 2 || !excelFlag || !pdfFlag){
//							MessageBox.post(AIFUtility.getActiveDesktop(),reg.getString("HasNoData.MSG"),"Warning",MessageBox.WARNING);
//							return;
//						}
//					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.run();
	}
}
