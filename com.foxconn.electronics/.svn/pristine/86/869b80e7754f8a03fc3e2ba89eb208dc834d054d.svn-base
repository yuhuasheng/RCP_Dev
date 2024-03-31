package com.foxconn.electronics.matrixbom.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import com.foxconn.electronics.matrixbom.constant.MatrixBOMConstant;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.commands.namedreferences.ExportFilesOperation;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;

public class MyExecutorGetImg extends Thread {
	AIFComponentContext aifchildren;
	ArrayList<PicBean> dataList;
	Semaphore position;
	public MyExecutorGetImg(AIFComponentContext aifchildren, ArrayList<PicBean> dataList, Semaphore position) {
		this.aifchildren = aifchildren;
		this.dataList = dataList;
		this.position = position;
		
	}	
	public void run() {
		
		try {
			position.acquire();
			TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
				
			String bl_item_id = children.getProperty("bl_item_item_id");
			String bl_occ_d9_Category = children.getProperty("bl_occ_d9_Category");
			String bl_occ_d9_Plant = children.getProperty("bl_occ_d9_Plant");
			String bl_lineid = CommonTools.md5Encode(bl_item_id+bl_occ_d9_Category+bl_occ_d9_Plant);

			//下载图片,转为Base64返回
			Map<String, String> downloadFile = downloadFile(children.getItemRevision());
			String Base64Str = downloadFile.get("base64Str");
			
			PicBean pic = new PicBean();
			pic.lineId =  bl_lineid;
			pic.sub  =  true;
			pic.base64Str = Base64Str;
			pic.picPath = downloadFile.get("picPath");
			dataList.add(pic);
			
			if(children.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
				for (TCComponentBOMLine subBomline : listSubstitutes) {
					String sub_bl_item_id = subBomline.getProperty("bl_item_item_id");
					String sub_bl_occ_d9_Category = subBomline.getProperty("bl_occ_d9_Category");
					String sub_bl_occ_d9_Plant = subBomline.getProperty("bl_occ_d9_Plant");
					String sub_lineid = CommonTools.md5Encode(sub_bl_item_id+sub_bl_occ_d9_Category+sub_bl_occ_d9_Plant);
					
					
					//下载图片,转为Base64返回
					String sub_Base64Str = downloadFile(subBomline.getItemRevision()).get("base64Str");
					
					PicBean sub_pic = new PicBean();
					sub_pic.lineId =  sub_lineid;
					sub_pic.sub = true;
					sub_pic.base64Str = sub_Base64Str;
					dataList.add(sub_pic);
				}
			}
			
			position.release();
				
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
	}
	
	
	public static Map<String,String> downloadFile(TCComponentItemRevision itemRevision) throws TCException {
		Map<String,String> map = new HashMap<String, String>();
		String imageBinary = "";
		TCComponent[] components = itemRevision.getRelatedComponents("IMAN_specification");
		
		for (TCComponent tcComponent : components) {
			if (!(tcComponent instanceof TCComponentDataset)) {
				continue;
			}
			TCComponentTcFile[] tcfiles = ((TCComponentDataset) tcComponent).getTcFiles();
			if (tcfiles != null && tcfiles.length > 0) {
				String type = tcComponent.getType();
				//System.out.println("type=="+type);
				if("Image".equals(type) || "JPEG".equals(type) || "GIF".equals(type)) {
					String fileName = tcfiles[0].getProperty("original_file_name");
					System.out.println("fileName = " + fileName);
					
					ExportFilesOperation expTemp = new ExportFilesOperation((TCComponentDataset) tcComponent, tcfiles, MatrixBOMConstant.tempPath, null);
					expTemp.executeOperation();
					
					String picPath = MatrixBOMConstant.tempPath + "\\" + fileName;
					
					//String picPath = MatrixBOMConstant.tempPath + "\\" + ii+"_"+fileName;
					imageBinary = ProductLineBOMService.getImageBinary(picPath);
					map.put("base64Str", imageBinary);
					map.put("picPath",picPath);
				}
			}
		}
		return map;
	}

}
