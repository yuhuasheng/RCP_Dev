package com.foxconn.plm.tcapi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.plm.tcapi.utils.bom.BOMFacade;
import com.foxconn.plm.tcapi.utils.dataset.DsFacade;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.ImanFile;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.teamcenter.services.strong.core.DataManagementService;

/**
 * @author infodba
 * @version 创建时间：2021年12月24日 上午10:58:32
 * @Description 数据集服务类
 */
public class DatasetService {		
	
	public static Dataset getDataSet(DataManagementService dmService, String dateSetUid) {
		ModelObject modelObject = TCPublicUtils.findObjectByUID(dmService, dateSetUid);
		return (Dataset) modelObject;
	}

	public static ImanFile getImanFile(DataManagementService dmService, String imanFileUid) {
		ModelObject modelObject = TCPublicUtils.findObjectByUID(dmService, imanFileUid);
		return (ImanFile) modelObject;
	}

	public static File[] getFiles(DataManagementService dmService, FileManagementUtility fmUtility, Dataset dataset, String fmsUrl) throws FileNotFoundException {
		return DsFacade.searcherService.getDataSetFiles(dataset, dmService,fmUtility);
	}

	public static File getFile(DataManagementService dmService, FileManagementUtility fmUtility, ImanFile imanFile, String fmsUrl) throws FileNotFoundException {
		return DsFacade.searcherService.getImanFile(imanFile, dmService, fmUtility);
	}	
	
	/**
	 * 获取数据集
	 * 
	 * @param dataManagementService 工具类
	 * @param objects               数据集对象数组
	 * @return
	 * @throws NotLoadedException
	 */
	public static Map<Dataset, String> getDataset(DataManagementService dataManagementServices, ModelObject[] objects)
			throws NotLoadedException {		
		return DsFacade.searcherService.getDataset(dataManagementServices, objects);
	}
	
	/**
	 * 下载数据集
	 * @param dataset
	 * @param dmService
	 * @param fmsFileManagement
	 * @param fileExtensions
	 * @param dirPath
	 * @return
	 */
	public static String downloadDataset(Dataset dataset, DataManagementService dmService,
			FileManagementUtility fmsFileManagement, String fileExtensions, String dirPath) {
		return DsFacade.searcherService.downloadDataset(dataset, dmService, fmsFileManagement, fileExtensions, dirPath);
	}
	
	
	/**
	 * 根据URL下载超链接的数据集文件
	 * @param dmService
	 * @param connection
	 * @param dataset
	 * @param dir
	 * @param fileName
	 * @return
	 * @throws NotLoadedException
	 */
	public static String downloadDatasetByUrl(DataManagementService dmService, Dataset dataset, String dir, String fileName, String url) {
		return DsFacade.searcherService.downloadDatasetByUrl(dmService, dataset, dir, fileName, url);
	}
	
	
	/**
	 * 上传数据集
	 * @param fMSFileManagement fcc
	 * @param dataManagementService 工具类
	 * @param dataset 数据集
	 * @param fileName 文件名
	 * @param refName 引用关系
	 * @return
	 */
	public static Boolean addDatasetFile(FileManagementUtility fMSFileManagement, DataManagementService dataManagementService,
			Dataset dataset, String fileName, String refName, boolean isText) {
		return DsFacade.modifierService.addDatasetFile(fMSFileManagement, dataManagementService, dataset, fileName, refName, isText);
	}

	/**
	 * 创建数据集
	 * @param dataManagementService 工具类
	 * @param itemRevision 对象版本
	 * @param dsname 数据集名称
	 * @param type 类型
	 * @param relationType 关系
	 * @return
	 */
	public static Dataset createDataset(DataManagementService dataManagementService, ItemRevision itemRevision, String dsname,
			String type, String relationType) {
		return DsFacade.creatorService.createDataset(dataManagementService, itemRevision, dsname, type, relationType);
	}
	
	/**
	 * 移除数据集的命名的引用下的物理文件
	 * @param dataset 数据集
	 * @param dataManagementService 工具类
	 * @param type 命名的引用名称
	 * @return
	 */
	public static boolean removeFileFromDataset(DataManagementService dataManagementService, Dataset dataset, String type) {
		return DsFacade.modifierService.removeFileFromDataset(dataManagementService, dataset, type);
	}
}
