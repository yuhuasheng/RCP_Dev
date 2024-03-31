package com.hh.tools.renderingHint.util;

import java.io.File;

import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;

/**
 * 数据集文件夹操作接口
 * 
 * @author wangsf
 *
 */
public interface IDatasetFolderOperation {

	/**
	 * 获取数据集文件夹节点
	 * 
	 * @return
	 */
    String getFolderNodes();

    /**
	 * 获取数据集文件夹组件
	 * 
	 * @return
	 */
    TCComponentFolder getDatasetFolder();

    /**
	 * 设置关联的文件
	 * 
	 * @param uploadFile
	 */
    void setRelationFile(File uploadFile);

    /**
	 * 获取关联的文件
	 * 
	 * @return
	 */
    File getRelationFile();

    /**
	 * 设置关联的数据集
	 * 
	 * @param dataset
	 */
    void setRelationDataset(TCComponentDataset dataset);

    /**
	 * 获取关联的数据集
	 * 
	 * @return
	 */
    TCComponentDataset getRelationDataset();

    /**
	 * 移除关联的数据
	 */
    void clearRelationData();

    /**
	 * 获取已关联的数据集
	 * 
	 * @return
	 */
    TCComponentDataset getCurrentDataset();

    boolean isCanUpload();

}
