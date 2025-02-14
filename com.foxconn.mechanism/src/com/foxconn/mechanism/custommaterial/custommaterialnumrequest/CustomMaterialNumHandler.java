package com.foxconn.mechanism.custommaterial.custommaterialnumrequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.util.StringUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.batchChangePhase.window.MsgBox;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialBatchImportAction;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialConfigEntity;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialDataEntity;
import com.foxconn.mechanism.custommaterial.custommaterialbatchimport.CustomMaterialProperty;
import com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window.CustomMaterialNumDialog;
import com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window.CustomMaterialNumService;
import com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window.FinishGoodDialog;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.pse.dialogs.CloseModifiedBOMWindowsDialog;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;

import cn.hutool.http.HttpUtil;
/**
 * 790PB1300A00H03
 * @author Oz
 *
 */
public class CustomMaterialNumHandler extends AbstractHandler{
	
	
	private Registry reg;


	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		String action  = (String) event.getParameters().get("action");
		if(action == null) {
			action = CustomMaterialNumDialog.ACTION_CREATE;
		}
		String label  = (String) event.getParameters().get("label");
		
		InterfaceAIFComponent[] aifCom = app.getTargetComponents();
		reg = Registry.getRegistry("com.foxconn.mechanism.custommaterial.custommaterialnumrequest.custommaterialnumrequest");
		String info = reg.getString("msg.title");
		
		// 限制只選一個
		if(aifCom.length != 1){
			MessageBox.post(reg.getString("msg.only.row.one"), info, MessageBox.WARNING);
			return null;
		}
		
		if("ProductionOrderApplicationForFinishedProductPartNumber".equals(action)) {
			InterfaceAIFComponent aif = aifCom[0];
			if(!(aif instanceof TCComponentItemRevision)){
				MessageBox.post("必须选择一个制作单版本对象", info, MessageBox.ERROR);
				return null;
			}
			new FinishGoodDialog(reg, app, shell,(TCComponentItemRevision) aif);
			return null;			
		}
		
		if(CustomMaterialNumDialog.ACTION_2ND.equals(action)) {
			InterfaceAIFComponent aif = aifCom[0];
			TCComponent revision = (TCComponent) aif;
			try {
				String is2nd = revision.getProperty("d9_Is2ndSource");
				if("是".equals(is2nd)||"yes".equalsIgnoreCase(is2nd)) {
					MessageBox.post("這個料已經申請了Second Source", info, MessageBox.WARNING);
					return null;
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		if(CustomMaterialNumDialog.ACTION_MODIFY.equals(action)||CustomMaterialNumDialog.ACTION_2ND.equals(action)) {
			
			InterfaceAIFComponent aif = aifCom[0];
			if(!(aif instanceof TCComponentItemRevision)){
				MessageBox.post(reg.getString("msg.only.opration.item.revision"), info, MessageBox.WARNING);
				return null;
			}
			try {
				String pn = ((TCComponent) aif).getProperty("d9_TempPN");
				if(pn.startsWith("49")) {
					label = CustomMaterialNumDialog.LABEL_49;
				}
				if(pn.startsWith("629")) {
					label = CustomMaterialNumDialog.LABEL_629;
				}
				if(pn.startsWith("713")) {
					label = CustomMaterialNumDialog.LABEL_713;
				}
				if(pn.startsWith("71408")) {
					label = CustomMaterialNumDialog.LABEL_71408;
				}
				if(pn.startsWith("71407")) {
					label = CustomMaterialNumDialog.LABEL_71407;
				}
				if(pn.startsWith("7351")) {
					label = CustomMaterialNumDialog.LABEL_7351;
				}
				if(pn.startsWith("7148")) {
					label = CustomMaterialNumDialog.LABEL_7148;
				}
				if(pn.startsWith("7149")) {
					label = CustomMaterialNumDialog.LABEL_7149;
				}
				if(pn.startsWith("79")) {
					label =CustomMaterialNumDialog.LABEL_79;
				}
				if(pn.startsWith("8")) {
					label = CustomMaterialNumDialog.LABEL_8;
				}
				if(pn.startsWith("X")) {
					label = CustomMaterialNumDialog.LABEL_X;
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}else {
			InterfaceAIFComponent aif = aifCom[0];
			if(!(aif instanceof TCComponentFolder)){
				MessageBox.post(reg.getString("msg.first.folder"), info, MessageBox.ERROR);
				return null;
			}
		}


		if(label==null) {
			MessageBox.post(reg.getString("msg.err.data"),info, MessageBox.ERROR);
			return null;
		}
		
		new CustomMaterialNumDialog(reg,app,action,label,shell);
		
        return null;
    }
	
	
	
	


}
