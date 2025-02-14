package com.foxconn.plm.tcapi.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.plm.tcapi.utils.item.ItemFacade;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
* @author infodba
* @version 创建时间：2021年12月24日 下午1:59:44
* @Description 
*/
public class ItemService {		
	
	/**
	 * 获取对象版本UID
	 * @param itemRevUid
	 * @return
	 */
	public static ItemRevision getItemRev(DataManagementService dmService, String itemRevUid) {		
		return ItemFacade.searcherService.getItemRev(dmService, itemRevUid);
	}
	
	
	/**
	 * 获取对象的所有者
	 * @param revisionList 对象版本集合
	 * @return
	 * @throws NotLoadedException
	 */
	public static Map<ModelObject, String> getModelObjectOwner(DataManagementService dmService, List<ModelObject> list) throws NotLoadedException {
		return ItemFacade.searcherService.getModelObjectOwner(dmService, list);
	}	
	
	/**
	 * 获取最新版
	 * @param dmService
	 * @param item
	 * @return
	 * @throws NotLoadedException
	 */
	public static ItemRevision getItemLatestRev(DataManagementService dmService, Item item) throws NotLoadedException {		
		return ItemFacade.searcherService.getItemLatestRev(dmService, item);
	}
}
