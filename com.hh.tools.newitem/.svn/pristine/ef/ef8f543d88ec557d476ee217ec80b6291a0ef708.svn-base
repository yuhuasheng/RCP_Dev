package com.hh.tools.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.ui.common.RACUIUtil;

/**
 * CIS文件库
 * @author wangsf
 *
 */
public class CISFileStorageUtil {

    private static CISFileStorageUtil cisFileStorageUtil = new CISFileStorageUtil();

    // 加载的根目录下的文件夹列表
 	private String loadBasicFolderPaths = "DataSheets/Symbols/BigSymbols/FootPrint";
 	// 数据集所在的文件夹
 	private final String CIS_FILE_STORAGE_ROOT_FOLDER_NAME = "CISFileStorage";
 	// CIS库里面的文件夹组件对象 key: 路径, value: 文件夹组件对象
 	private Map<String, TCComponentFolder> cisFileFolderMap = null;
 	// 数据加载标识
    private boolean loadDataFlag = false;

    private CISFileStorageUtil() {

    }

    public static CISFileStorageUtil getInstance() {
        return cisFileStorageUtil;
    }

    public boolean getLoadDataFlag() {
        return loadDataFlag;
    }

    /**
	 * 获取数据集文件夹组件
	 * 
	 * @param datasetFolderPaths 数据集所在的文件夹名称路径列表(多个/分隔)
	 */
    public TCComponentFolder getDatasetFolderComp(String datasetFolderPaths) {
        System.out.println("CISFileStorageUtil getDatasetFolderComp => " + datasetFolderPaths);

        TCComponentFolder datasetFolderComp = null;
        if (StringUtils.isEmpty(datasetFolderPaths)) {
            return datasetFolderComp;
        }

        datasetFolderComp = cisFileFolderMap.get(datasetFolderPaths.toUpperCase());
        if (null == datasetFolderComp) {
        	System.out.println("未获取到数据集文件夹目录");
            return datasetFolderComp;
        }

        System.out.println("CISFileStorageUtil 目标目录 => " + datasetFolderComp);
        return datasetFolderComp;
    }

    /**
	 * 加载CIS库数据
	 */
    public void loadCISFileStorageData() {
        cisFileFolderMap = new HashMap<String, TCComponentFolder>();
        TCComponentFolder cisFolderComp = null;

        // 从TC系统中 查询数据集根目录
        TCComponent tempSearchFolder = searchTCComponent("General...",
                new String[]{getTextValue("Type"), getTextValue("OwningUser"), getTextValue("Name")},
                new String[]{"Folder", "infodba(infodba)", CIS_FILE_STORAGE_ROOT_FOLDER_NAME});
        System.out.println("CISFileStorageUtil 根目录 => " + tempSearchFolder);

        if (tempSearchFolder instanceof TCComponentFolder) {
            cisFolderComp = (TCComponentFolder) tempSearchFolder;
        }

        if (null == cisFolderComp) {
        	System.out.println("未获取到 CISFileStorage 的根目录");
            return;
        }

        System.out.println("CISFileStorageUtil loadCISFileStorageData loadBasicFolderPaths => " + loadBasicFolderPaths);
        String[] initFolderNames = loadBasicFolderPaths.split("/");
        Set<String> initFolderNameSets = new HashSet<String>(initFolderNames.length);
        for (int i = 0; i < initFolderNames.length; i++) {
            initFolderNameSets.add(initFolderNames[i]);
        }

        try {
            TCComponent[] tempComp = cisFolderComp.getRelatedComponents("contents");
            if (null != tempComp && tempComp.length > 0) {
                TCComponent itemComp = null;
                for (int i = 0; i < tempComp.length; i++) {
                    itemComp = tempComp[i];
                    String folderName = itemComp.toDisplayString();
                    if (itemComp instanceof TCComponentFolder && initFolderNameSets.contains(folderName)) {
                        loadDatasetFolder((TCComponentFolder) itemComp, folderName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadDataFlag = true;
    }

    /**
	 * 加载数据集文件夹对象
	 * @param parentFolder 数据集的文件夹对象
	 * @return
	 * @throws TCException 
	 */
    private void loadDatasetFolder(TCComponentFolder parentFolder, String parentFolderName) throws TCException {
        TCComponent[] tempComp = parentFolder.getRelatedComponents("contents");
        if (null != tempComp && tempComp.length > 0) {
            TCComponent itemComp = null;
            for (int i = 0; i < tempComp.length; i++) {
                itemComp = tempComp[i];
                if (itemComp instanceof TCComponentFolder) {
                    TCComponentFolder itemFolderComp = (TCComponentFolder) itemComp;
                    String folderPath = parentFolderName + "/" + itemFolderComp.toDisplayString();
//					System.out.println("loadDatasetFolder path => " + folderPath + ",itemFolderComp => " + itemFolderComp.toDisplayString());
                    cisFileFolderMap.put(folderPath.toUpperCase(), itemFolderComp);
                    loadDatasetFolder(itemFolderComp, folderPath);
                }
            }
        }
    }


    /**
	 * 根据搜索名 获取组件
	 * @param searchName 搜索名
	 * @param keys 搜索字段
	 * @param values 搜索字段值
	 * @return
	 */
    private TCComponent searchTCComponent(String searchName, String[] keys, String[] values) {
        TCSession session = RACUIUtil.getTCSession();
        TCComponent searchComp = null;
        try {
            InterfaceAIFComponent[] resultComponents = session.search(searchName, keys, values);
            if (null != resultComponents && resultComponents.length > 0) {
                searchComp = (TCComponent) resultComponents[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchComp;
    }

    /**
	 * 根据名称 获取值
	 * @param name
	 * @return
	 */
    private String getTextValue(String name) {
        TCSession session = RACUIUtil.getTCSession();
        TCTextService tcTextService = session.getTextService();
        String res = null;
        try {
            String value = tcTextService.getTextValue(name);
            if (StringUtils.isEmpty(value)) {
                res = name;
            } else {
                res = value;
            }
        } catch (TCException e) {
            res = name;
            e.printStackTrace();
        }
        return res;
    }
}
