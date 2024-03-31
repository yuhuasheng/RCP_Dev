package com.foxconn.plm.tcapi.service;

import java.util.List;
import java.util.Map;


import com.foxconn.plm.tcapi.utils.bom.BOMFacade;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core.SessionService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
* @author infodba
* @version 创建时间：2021年12月24日 下午4:08:23
* @Description
*/
public class BOMService {
	
	
	/**
	 * 判断对象版本是否含有BOM
	 * @param structureManagementService
	 * @param strucService
	 * @param dataManagementService
	 * @param itemRevision
	 * @return
	 * @throws Exception
	 */
	public static boolean isBom(StructureManagementService structureManagementService, StructureService strucService,
			DataManagementService dataManagementService, ItemRevision itemRevision) throws Exception {
		return BOMFacade.commonOperate.isBom(structureManagementService, strucService, dataManagementService, itemRevision);
	}	
	
	/**
	 * 判断是否为BOM结构对象
	 * @param dmService
	 * @param itemRev
	 * @return
	 * @throws NotLoadedException
	 */
	public static boolean isBom(DataManagementService dmService, ItemRevision itemRev) throws NotLoadedException {
		return BOMFacade.commonOperate.isBom(dmService, itemRev);
	}
	
	
	/**
	 * 获取BOMLine集合
	 * @param structureManagementService
	 * @param dataManagementService
	 * @param itemRevision
	 * @return
	 */
	public static boolean getBOMWindowInfo(StructureManagementService structureManagementService, StructureService strucService,
			DataManagementService dataManagementService, ItemRevision itemRevision, int level, List<ModelObject> itemRevList, List<String> propertyList) {
		return BOMFacade.searchService.getBOMWindowInfo(structureManagementService, strucService, dataManagementService, itemRevision, level, itemRevList, propertyList);
	}
	
	/**
	 * 修改BOMLine 属性字段值
	 * @param structureManagementService
	 * @param dataManagementService
	 * @param itemRevision
	 * @param map
	 * @param ref 作为判断更新哪一行BOMLine属性的标识符
	 * @return
	 */
	public static List<String> modifyBOMLineInfo(StructureManagementService structureManagementService, StructureService strucService,
			DataManagementService dataManagementService, ItemRevision itemRevision, Map<String, List<String>> map, String ref, int level) {
		return BOMFacade.modifyService.modifyBOMLineInfo(structureManagementService, strucService, dataManagementService, itemRevision, map, ref, level);
	}
	
	/**
	 * 加包或者解包
	 * @param strucService
	 * @param dataManagementService
	 * @param list
	 * @param level
	 * @return
	 * @throws Exception 
	 */
	public static void packOrUnpack(StructureService strucService, DataManagementService dataManagementService, BOMLine bomLine, int level) throws Exception {
		BOMFacade.modifyService.packOrUnpack(strucService, dataManagementService, bomLine, level);
	}
	
	/**
	 * 将BOMLine的对象版本返回
	 *
	 * @param dataManagementService 工具类
	 * @param list                  集合
	 * @return
	 * @throws NotLoadedException
	 */
	public static List<ModelObject> conversionItemRev(DataManagementService dataManagementService, List<BOMLine> list) throws NotLoadedException {		
		return BOMFacade.searchService.conversionItemRev(dataManagementService, list);
	}
	
	/**
	 * close BOMWindow
	 * @param structureManagementService
	 * @param bomWindow
	 */
	public static void closeBOMWindow(StructureManagementService structureManagementService, BOMWindow bomWindow) {
		BOMFacade.commonOperate.closeBOMWindow(structureManagementService, bomWindow);
	}
	
	/**
	 * save BOMWindow
	 * @param structureManagementService
	 * @param bomWindow
	 */
	public static void saveBOMWindow(StructureManagementService structureManagementService, BOMWindow bomWindow) {
		BOMFacade.commonOperate.saveBOMWindow(structureManagementService, bomWindow);
	}
	
	public Map<String, BOMLine> expandPSEAllLevels(StructureManagementService smService, BOMLine topLine, SessionService sessionService) throws Exception {
		return BOMFacade.searchService.expandPSEAllLevels(smService, topLine, sessionService);
	}
}
