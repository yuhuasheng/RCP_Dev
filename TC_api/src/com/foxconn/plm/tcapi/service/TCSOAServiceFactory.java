package com.foxconn.plm.tcapi.service;

import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import java.io.FileNotFoundException;
import com.foxconn.plm.tcapi.soa.client.AppXSession;
import com.teamcenter.services.loose.core.SessionService;
import com.teamcenter.services.strong.query.SavedQueryService;

/**
 * @author 作者 Administrator
 * @version 创建时间：2021年12月18日 下午8:52:54 Description: TC公共类制造工厂
 */
public class TCSOAServiceFactory {

	private StructureManagementService structureManagementService; // bom结构

	private DataManagementService dataManagementService; // tc object

	private FileManagementUtility fileManagementUtility; // fms

	private PreferenceManagementService preferenceManagementService; // 首选项

	private SessionService sessionservice;// 用户服务类

	private SavedQueryService savedQueryService; // 搜索

	private StructureService strucService;
	
	private ITCSOAClientConfig tcSOAClientConfig;
	
	public TCSOAServiceFactory(ITCSOAClientConfig tcSOAClientConfig) {
		this.tcSOAClientConfig = tcSOAClientConfig;
	}

	public SavedQueryService getSavedQueryService() {
		if (savedQueryService == null) {
			savedQueryService = SavedQueryService.getService(tcSOAClientConfig.getConnection());
		}
		return savedQueryService;
	}

	public SessionService getSessionService() {
		if (sessionservice == null) {
			sessionservice = SessionService.getService(tcSOAClientConfig.getConnection());
		}
		return sessionservice;
	}

	public StructureManagementService getStructureManagementService() {
		if (structureManagementService == null) {
			structureManagementService = StructureManagementService.getService(tcSOAClientConfig.getConnection());
		}
		return structureManagementService;
	}

	public StructureService getStructureService() {
		if (null == strucService) {
			strucService = StructureService.getService(tcSOAClientConfig.getConnection());
		}
		return strucService;
	}

	public DataManagementService getDataManagementService() {
		if (dataManagementService == null) {
			dataManagementService = DataManagementService.getService(tcSOAClientConfig.getConnection());
		}
		return dataManagementService;
	}

	public FileManagementUtility getFileManagementUtility(String fmsUrl) {

		if (fileManagementUtility == null) {
			if (fmsUrl != null && fmsUrl.length() > 0) {
				try {
					fileManagementUtility = new FileManagementUtility(tcSOAClientConfig.getConnection(), null, null, new String[] { fmsUrl }, null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			if (fileManagementUtility == null) {
				fileManagementUtility = new FileManagementUtility(tcSOAClientConfig.getConnection());
			}
		}
		return fileManagementUtility;
	}

	public PreferenceManagementService getPreferenceManagementService() {

		if (preferenceManagementService == null) {
			preferenceManagementService = PreferenceManagementService.getService(tcSOAClientConfig.getConnection());
		}
		return preferenceManagementService;
	}

}
