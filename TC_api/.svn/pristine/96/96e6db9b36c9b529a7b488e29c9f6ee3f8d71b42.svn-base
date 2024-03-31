package com.foxconn.plm.tcapi.utils.bom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2008_06.StructureManagement;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core.SessionService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;



public class BOMSearcher {


	 private static Logger log=LogManager.getLogger(BOMSearcher.class);
	protected BOMSearcher() {
		
	}	
	
	
	
	/**
	 * 获取BOMLine集合
	 *
	 * @param dataManagementService 工具类
	 * @param connection            连接
	 * @param itemRevision          对象版本
	 * @return
	 */

	public boolean getBOMWindowInfo(StructureManagementService structureManagementService,StructureService strucService, DataManagementService dataManagementService, 
			ItemRevision itemRevision, int level,List<ModelObject> itemRevList, List<String> propertyList) {				
		TCPublicUtils.refreshObject(dataManagementService, itemRevision);
		// BOMWindow窗口
		BOMWindow[] bomWindows = null;
		List<BOMLine> list = new ArrayList<>();
		try {
			// Open BOMWindow
			List createBOMWindowsResponse = BOMFacade.commonOperate.openBOMWindow(structureManagementService, dataManagementService, itemRevision);
			if (CommonTools.isEmpty(createBOMWindowsResponse)) {
				log.info("【ERROR】 打开BOMWindow失败！");
				return false;
			}

			// BOMWindow窗口
			bomWindows = new BOMWindow[] { (BOMWindow) createBOMWindowsResponse.get(0) };			
			TCPublicUtils.refreshObject(dataManagementService, bomWindows);
			
			// 顶层BOMLine
			BOMLine topLine = (BOMLine) createBOMWindowsResponse.get(1);				
			
			TCPublicUtils.refreshObject(dataManagementService, topLine);
			ModelObject modelObject = TCPublicUtils.getPropModelObject(dataManagementService, topLine, BOMLinePropConstant.BL_LINES_OBJECT);
			itemRevList.add(modelObject);
			String refId = TCPublicUtils.getPropStr(dataManagementService, topLine,BOMLinePropConstant.BL_REF_DESIGNATOR);
			if (CommonTools.isNotEmpty(refId)) {
				propertyList.add(refId);
			}
			boolean bIsBomLinePacked = TCPublicUtils.getPropBoolean(dataManagementService, topLine, BOMLinePropConstant.BL_IS_PACKED);
			if (bIsBomLinePacked) { // 判断是否为打包状态
				System.out.println("BOM Line is packed, unpacking the complete BOMLine");
				ServiceData response = strucService.packOrUnpack(new BOMLine[] { topLine }, level);
				
				if (response.sizeOfPartialErrors() == 0) {
					System.out.println("unpacking successfull ");
				}
			}
//			list.add(topLine);
			ModelObject[] children = TCPublicUtils.getPropModelObjectArray(dataManagementService, topLine, BOMLinePropConstant.BL_ALL_CHILD_LINES);				
			if (CommonTools.isEmpty(children)) { // 判断数组是否为空
				if (bomWindows != null) {
					// close BOMWindow
//					closeBOMWindow(connection, bomWindows);
					log.info("【WARN】 不存在子BOMLine, 无需进行下序操作！");
					throw new Exception("【WARN】 不存在子BOMLine, 无需进行下序操作！");
				}
			} else {
				// 遍历BOMLine结构树，获取子BOMLine对应的对象版本
				BOMFacade.commonOperate.getChildBom(strucService, dataManagementService, topLine, level, null, itemRevList, propertyList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
		} finally {
			// 关闭BOMWindow
			if (bomWindows != null) {
				// 保存BOMWindow
				BOMFacade.commonOperate.saveBOMWindow(structureManagementService, bomWindows[0]);
				// 关闭BOMWindow
				BOMFacade.commonOperate.closeBOMWindow(structureManagementService, bomWindows[0]);
			}
		}
		return true;
	}
	
	/**
	 * 将BOMLine的对象版本返回
	 *
	 * @param dataManagementService 工具类
	 * @param list                  集合
	 * @return
	 * @throws NotLoadedException
	 */
	public List<ModelObject> conversionItemRev(DataManagementService dataManagementService, List<BOMLine> list)
			throws NotLoadedException {
		List<ModelObject> itemRevList = new ArrayList<ModelObject>();
//		ItemRevision[] itemRevData = new ItemRevision[list.size()];
		list.parallelStream().forEach(bomLine -> {
			try {
				ModelObject modelObject = TCPublicUtils.getPropModelObject(dataManagementService, bomLine, BOMLinePropConstant.BL_LINES_OBJECT);				
				itemRevList.add(modelObject);
			} catch (NotLoadedException e) {				
				e.printStackTrace();
			}
			
		});
		
//		for (int i = 0; i < list.size(); i++) {
//			BOMLine bomLine = list.get(i);
//			ModelObject modelObject = TCPublicUtils.getPropModelObject(dataManagementService, bomLine, BOMLinePropConstant.BL_LINES_OBJECT);			
//			itemRevData[i] = (ItemRevision) modelObject;
//		}		
		return itemRevList;
	}
	
	
	/**
	 * 展开所有的BOMLine，获取所有的BOMLine对象
	 * @param smService
	 * @param topLine
	 * @param sessionService
	 * @throws Exception
	 */
	public Map<String, BOMLine> expandPSEAllLevels(StructureManagementService smService, BOMLine topLine, SessionService sessionService) throws Exception {	
		Map<String, BOMLine> bomLineMap = new HashMap<String, BOMLine>();
		TCPublicUtils.setDefaultLoadProperty(sessionService, "BOMLine", new String[] {BOMLinePropConstant.BL_ITEM_ITEM_ID});
		com.teamcenter.services.strong.cad._2008_06.StructureManagement.ExpandPSAllLevelsInfo info = new com.teamcenter.services.strong.cad._2008_06.StructureManagement.ExpandPSAllLevelsInfo();
        info.parentBomLines = new BOMLine[]{topLine};
        info.excludeFilter = "None2";
        com.teamcenter.services.strong.cad._2008_06.StructureManagement.ExpandPSAllLevelsPref pref = new com.teamcenter.services.strong.cad._2008_06.StructureManagement.ExpandPSAllLevelsPref();
        StructureManagement.ExpandPSAllLevelsResponse2 response = smService.expandPSAllLevels(info, pref);
        for (int i = 0; i < response.output.length; i++) {
	        try {
	        	BOMLine childline = response.output[i].parent.bomLine;
	        	bomLineMap.put( childline.get_bl_item_item_id(), childline );
			} catch (NotLoadedException  e) {
				StringBuilder sErrorMessage = new StringBuilder();				 
				sErrorMessage.append( "Exception occurred while fetching the item id for loaded structure. " );				 
				throw new Exception( sErrorMessage.toString() );
			}
		}
        return bomLineMap;
	}
}
