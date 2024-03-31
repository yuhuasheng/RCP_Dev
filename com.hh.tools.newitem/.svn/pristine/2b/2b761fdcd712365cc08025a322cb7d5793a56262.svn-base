package com.hh.tools.util;

import java.util.Map;

import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

/**
 * 数据集类型工具
 * 
 * @author wangsf
 *
 */
public class DatasetTypeUtil {

    private static DatasetTypeUtil datasetTypeUtil = null;

    // 数据集的类型定义
 	private TCComponentDatasetDefinitionType datasetDefinitionType = null;
 	// 首选项数据集类型
    private final String PERFERE_DATASET_TYPE_NAME = "DRAG_AND_DROP_default_dataset_type";
    @SuppressWarnings("rawtypes")
    private Map datasetTypeMap = null;
    // 首选项数据集默认类型
    private final String PERFERE_DEFAULT_DATASET_TYPE_NAME = "FX_Dataset_DefaultTypeName";
    private String defaultDstType = null;

    private DatasetTypeUtil() {
    }

    public static DatasetTypeUtil getInstance() {
        if (null == datasetTypeUtil) {
            datasetTypeUtil = new DatasetTypeUtil();
            datasetTypeUtil.loadDatasetTypeData();
        }
        return datasetTypeUtil;
    }

    /**
	 * 加载数据集类型数据
	 */
    private void loadDatasetTypeData() {
        TCSession session = Utils.getTCSession();
        GetPreferenceUtil preferenceUtil = new GetPreferenceUtil();
        try {
        	// 获取数据集类型定义
            datasetDefinitionType = (TCComponentDatasetDefinitionType) session.getTypeComponent("DatasetType");
            datasetTypeMap = preferenceUtil.getHashMapPreference(session, TCPreferenceService.TC_preference_site,
                    PERFERE_DATASET_TYPE_NAME, ":");

            defaultDstType = new GetPreferenceUtil().getStringPreference(session,
                    TCPreferenceService.TC_preference_site, PERFERE_DEFAULT_DATASET_TYPE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * 获取数据集类型
	 * 
	 * @param fileName 文件名
	 * @return
	 */
    public String getDatasetType(String fileName) {

        String fileSuffix = getFileSuffix(fileName);

        if (null != datasetTypeMap && datasetTypeMap.containsKey(fileSuffix)) {
            return datasetTypeMap.get(fileSuffix).toString();
        } else {
            return defaultDstType;
        }
    }

    public String getFileSuffix(String fileName) {
        if (null == fileName || "".equals(fileName)) {
            return null;
        }

        // 获取文件后缀
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileSuffix = fileSuffix.toLowerCase();

        return fileSuffix;
    }

    /**
	 * 获取数据集定义类型
	 * 
	 * @param datasetType 数据集类型
	 * @return
	 */
    public String getDatasetDefinitionType(String datasetType) {
        String tempDstDefinitionType = null;
        if (null == datasetType || "".equals(datasetType)) {
            return tempDstDefinitionType;
        }

        try {
            TCComponentDatasetDefinition datasetDefinition = datasetDefinitionType.find(datasetType);
            tempDstDefinitionType = datasetDefinition.getNamedReferences()[0];
        } catch (TCException e) {
            e.printStackTrace();
        }

        return tempDstDefinitionType;
    }
}
