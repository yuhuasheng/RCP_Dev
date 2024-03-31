package com.foxconn.electronics.replaceHHMaterial;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.project.Controller;
import com.foxconn.electronics.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.DeepCopyInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.util.StrUtil;
//1A62N5900-600
//001019
public class ReplaceHHMaterialHandler extends AbstractHandler{
	AbstractPSEApplication app;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
			TCHttp http = TCHttp.startJHttp();
	        http.addHttpController(new ReplaceHHMaterialController(app));
	        String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/dellEBOMReplacement?port="+http.getPort();
	        System.out.println(url);
	        new ReplaceHHMaterialDialog(app,app.getDesktop().getShell(),null,url);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

}
