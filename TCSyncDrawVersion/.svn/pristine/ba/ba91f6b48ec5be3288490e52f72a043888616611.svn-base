package com.foxconn.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.foxconn.constants.TCSearchEnum;
import com.foxconn.teamcenter.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core._2007_01.DataManagement.WhereReferencedResponse;
import com.teamcenter.services.strong.core._2007_01.Session;
import com.teamcenter.services.strong.core._2007_01.Session.MultiPreferencesResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CloseBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsInfo;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsOutput;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2008_06.StructureManagement.SaveBOMWindowsResponse;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core.ReservationService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ObjectOwner;
import com.teamcenter.services.strong.core._2006_03.DataManagement.Relationship;
import com.teamcenter.services.internal.strong.core.ICTService;
import com.teamcenter.services.internal.strong.core._2011_06.ICT;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Entry;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.InvokeICTMethodResponse;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Structure;
import com.teamcenter.services.loose.core.SessionService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.GetFileResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.DispatcherRequest;
import com.teamcenter.soa.client.model.strong.Form;
import com.teamcenter.soa.client.model.strong.Group;
import com.teamcenter.soa.client.model.strong.ImanFile;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.POM_system_class;
import com.teamcenter.soa.client.model.strong.PSBOMViewRevision;
import com.teamcenter.soa.client.model.strong.Person;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.DescribeSavedQueriesResponse;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.SavedQueryFieldObject;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.QueryResults;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse;
import com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput;

/**
 * @author 作者 Administrator
 * @version 创建时间：2021年12月4日 上午11:31:28 Description: TC工具类
 */
public class TCUtil {

	private static DataManagementService dataManagementService = null;

	public static DataManagementService getDataManagementService() {
		if (dataManagementService == null) {
			dataManagementService = DataManagementService.getService(AppXSession.getConnection());
		}		
		return dataManagementService;
	}

	public static Connection getConnection() {
		return AppXSession.getConnection();
	}

	/**
	 * 获取BOMLine信息，返回对象版本集合
	 * 
	 * @param dataManagementService
	 * @param connection
	 * @param itemRevision
	 * @return
	 */
	public static List<ItemRevision> getBOMWindowInfo(DataManagementService dataManagementService,
			Connection connection, ItemRevision itemRevision) {
		// BOMWindow窗口
		BOMWindow[] bomWindows = null;
		List<ItemRevision> list = new ArrayList<ItemRevision>();
		dataManagementService.loadObjects(new String[] { itemRevision.getUid() });
		list.add(itemRevision);
		try {
			// Open BOMWindow
			List createBOMWindowsResponse = openBOMWindow(connection, itemRevision);
			if (CommonTools.isEmpty(createBOMWindowsResponse)) {
				System.err.println("【ERROR】 打开BOMWindow失败！");
				return null;
			}

			// BOMWindow窗口
			bomWindows = new BOMWindow[] { (BOMWindow) createBOMWindowsResponse.get(0) };
			dataManagementService.refreshObjects(bomWindows);
			dataManagementService.refreshObjects2(bomWindows, true);

			// 顶层BOMLine
			BOMLine topLine = (BOMLine) createBOMWindowsResponse.get(1);
			dataManagementService.refreshObjects(new ModelObject[] { topLine });
			dataManagementService.refreshObjects2(new ModelObject[] { topLine }, true);
			dataManagementService.loadObjects(new String[] { topLine.getUid() });

			dataManagementService.getProperties(new ModelObject[] { topLine }, new String[] { "bl_all_child_lines" });
			ModelObject[] children = topLine.get_bl_all_child_lines();
			if (CommonTools.isEmpty(children)) { // 判断数组是否为空
				if (bomWindows != null) {
					// close BOMWindow
//					closeBOMWindow(connection, bomWindows);
					System.err.println("【WARN】 不存在子BOMLine, 无需进行下序操作！");
//					throw new Exception("【WARN】 不存在子BOMLine, 无需进行下序操作！");
				}
			} else {
				// 遍历BOMLine结构树，获取子BOMLine对应的对象版本
				getChildBom(dataManagementService, topLine, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
		} finally {
			// 关闭BOMWindow
			if (bomWindows != null) {
				// 保存BOMWindow
				saveBOMWindow(connection, bomWindows[0]);
				// 关闭BOMWindow
				closeBOMWindow(connection, bomWindows[0]);
				System.out.println("======== end ========");
			}
		}
		return list;

	}

	/**
	 * 遍历BOMLine结构树，获取子BOMLine对应的对象版本
	 * 
	 * @param dataManagementService
	 * @param topLine
	 * @param list
	 * @throws NotLoadedException
	 */
	private static void getChildBom(DataManagementService dataManagementService, BOMLine topLine,
			List<ItemRevision> list) throws NotLoadedException {
		dataManagementService.refreshObjects(new ModelObject[] { topLine });
		dataManagementService.loadObjects(new String[] { topLine.getUid() });
		dataManagementService.getProperties(new ModelObject[] { topLine }, new String[] { "bl_all_child_lines" });
		ModelObject[] children = topLine.get_bl_all_child_lines();
		if (CommonTools.isEmpty(children)) { // 判断数组是否为空
			return;
		}
		for (ModelObject obj : children) {
			BOMLine childBomLine = (BOMLine) obj;
			dataManagementService.refreshObjects(new ModelObject[] { childBomLine });
			try {
				dataManagementService.getProperties(new ModelObject[] { childBomLine },
						new String[] { "bl_lines_object", "bl_all_child_lines" });
				ModelObject modelObject = childBomLine.get_bl_line_object();
				ItemRevision childItemRev = (ItemRevision) modelObject;
				list.add(childItemRev);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			ModelObject[] objects = childBomLine.get_bl_all_child_lines();
			if (CommonTools.isNotEmpty(objects)) {
				getChildBom(dataManagementService, childBomLine, list);
			}
		}

	}

	/**
	 * Open BOMWindow
	 * 
	 * @param connection
	 * @param itemRev
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	private static List openBOMWindow(Connection connection, ItemRevision itemRevision) {
		List bomWindowParentLine = new ArrayList(2);
		try {
			CreateBOMWindowsInfo[] createBOMWindowsInfo = new CreateBOMWindowsInfo[1];
			createBOMWindowsInfo[0] = new CreateBOMWindowsInfo();
			createBOMWindowsInfo[0].itemRev = itemRevision;
			createBOMWindowsInfo[0].clientId = "BOMUtils";
			createBOMWindowsInfo[0].item = itemRevision.get_items_tag();
			StructureManagementService cadSMService = StructureManagementService.getService(connection);
			CreateBOMWindowsResponse createBOMWindowsResponse = cadSMService.createBOMWindows(createBOMWindowsInfo);
			if (createBOMWindowsResponse.serviceData.sizeOfPartialErrors() > 0) {
				for (int i = 0; i < createBOMWindowsResponse.serviceData.sizeOfPartialErrors(); i++) {
					System.out.println("【ERROR】 Partial Error in Open BOMWindow = "
							+ createBOMWindowsResponse.serviceData.getPartialError(i).getMessages()[0]);
				}
				return null;
			}
			CreateBOMWindowsOutput[] output = createBOMWindowsResponse.output;
			if (null == output || output.length < 0) {
				return null;
			}
			// BOMWindow
			bomWindowParentLine.add(output[0].bomWindow);
			// TOPLine in BOMWindow
			bomWindowParentLine.add(output[0].bomLine);
			return bomWindowParentLine;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
		}
		return null;
	}

	/**
	 * close BOMWindow
	 * 
	 * @param connection
	 * @param bomWindow
	 */
	@SuppressWarnings("unused")
	private static void closeBOMWindow(Connection connection, BOMWindow bomWindow) {
		StructureManagementService cadSMService = StructureManagementService.getService(connection);
		CloseBOMWindowsResponse response = null;
		if (cadSMService != null && bomWindow != null) {
			response = cadSMService.closeBOMWindows(new BOMWindow[] { bomWindow });
		}
		if (response.serviceData.sizeOfPartialErrors() > 0) {
			for (int i = 0; i < response.serviceData.sizeOfPartialErrors(); i++) {
				System.out.println("Close BOMWindow Partial Error -- " + response.serviceData.getPartialError(i).getMessages()[0]);
			}
		}
	}

	/**
	 * Save BOMWindow
	 *
	 * @param connection 连接
	 * @param bomWindow  BOMWindow对象
	 */
	public static void saveBOMWindow(Connection connection, BOMWindow bomWindow) {
		com.teamcenter.services.strong.cad.StructureManagementService cadSMService = com.teamcenter.services.strong.cad.StructureManagementService
				.getService(connection);
		SaveBOMWindowsResponse saveResponse = cadSMService.saveBOMWindows(new BOMWindow[] { bomWindow });
		if (saveResponse.serviceData.sizeOfPartialErrors() > 0) {
			for (int i = 0; i < saveResponse.serviceData.sizeOfPartialErrors(); i++) {
				System.out.println("Save BOMWindow Partial Error -- "
						+ saveResponse.serviceData.getPartialError(i).getMessages()[0]);
			}
		}

	}

	public static List<ItemRevision> getBOMWindowMessage(DataManagementService dataManagementService,
			Connection connection, ItemRevision itemRevision) throws NotLoadedException {
		BOMWindow bomWindow = null;
		BOMLine topLine = null;
		List<ItemRevision> list = new ArrayList<ItemRevision>();
		list.add(itemRevision);
		CreateBOMWindowsInfo createBOMWindowsInfo = new CreateBOMWindowsInfo();
		createBOMWindowsInfo.clientId = "BOMUtils";
		createBOMWindowsInfo.item = itemRevision.get_items_tag();
		createBOMWindowsInfo.itemRev = itemRevision;
		StructureManagementService structSrv = StructureManagementService.getService(connection);
		CreateBOMWindowsResponse response = structSrv
				.createBOMWindows(new CreateBOMWindowsInfo[] { createBOMWindowsInfo });
		if (response.serviceData.sizeOfPartialErrors() > 0) {
			System.err.println("StructureManagementService.createBOMWindows returned a partial error.");
			return null;
		}
		CreateBOMWindowsOutput[] output = response.output;
		if (output == null || output.length == 0) {
			return null;
		}
		// 获取bom视图
		bomWindow = output[0].bomWindow;
		// 获取bom顶层视图
		topLine = output[0].bomLine;
		dataManagementService.getProperties(new ModelObject[] { topLine }, new String[] { "bl_all_child_lines" });
		ModelObject[] children = topLine.get_bl_all_child_lines();
		if (null == children || children.length == 0) {
			if (bomWindow != null) {
				structSrv.closeBOMWindows(new BOMWindow[] { bomWindow });
			}
		} else {
			// 遍历BOMLine结构树，获取子BOMLine对应的对象版本
			getChildBom(dataManagementService, topLine, list);
		}
		return list;
	}

	/**
	 * 查询方法，返回ModelObject对象
	 *
	 * @param queryname             查询明智
	 * @param entries               查询列名
	 * @param values                查询值
	 * @param connetion             连接
	 * @param datamanagementservice 工具类
	 * @return
	 */
	public static ModelObject[] executequery(String queryname, String[] entries, String[] values, Connection connetion,
			DataManagementService datamanagementservice) {
		ImanQuery query = null;
		SavedQueryService queryService = SavedQueryService.getService(connetion);
		try {
			GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
			if (savedQueries.queries.length == 0) {
				System.err.println("【ERROR】 There are no saved queries in the system.");
				return null;
			}
			for (int i = 0; i < savedQueries.queries.length; i++) {
				if (savedQueries.queries[i].name.equals(queryname)) {
					query = savedQueries.queries[i].query;
					break;
				}
			}
		} catch (ServiceException e) {
			System.err.println("【ERROR】 GetSavedQueries service request failed.");
			System.err.println(CommonTools.getExceptionMsg(e));
			return null;
		}
		if (query == null) {
			System.err.println("【ERROR】 There is not an 'Item Name' query.");
			return null;
		}

		DescribeSavedQueriesResponse descResp = queryService.describeSavedQueries(new ImanQuery[] { query });
		SavedQueryFieldObject[] queryFields = descResp.fieldLists[0].fields;
		for (int i = 0; i < queryFields.length; i++) {
			System.err.println(queryFields[i].entryName);
		}

		try {
			QueryInput[] savedQueryInput = new QueryInput[1];
			savedQueryInput[0] = new QueryInput();
			savedQueryInput[0].query = query;
			savedQueryInput[0].maxNumToReturn = 9999;
			savedQueryInput[0].limitList = new ModelObject[0];
			savedQueryInput[0].entries = entries;
			savedQueryInput[0].values = values;

			SavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
			QueryResults found = savedQueryResult.arrayOfResults[0];

			System.out.println("Found Items:");

			String[] uids = new String[found.objectUIDS.length];
			for (int i = 0; i < found.objectUIDS.length; i++) {

				uids[i] = found.objectUIDS[i];

			}
			if (uids == null || uids.length == 0) {
				return null;
			}
			ServiceData sd = datamanagementservice.loadObjects(uids);
			ModelObject[] foundObjs = new ModelObject[sd.sizeOfPlainObjects()];
			for (int k = 0; k < sd.sizeOfPlainObjects(); k++) {
				foundObjs[k] = (ModelObject) sd.getPlainObject(k);
			}
			return foundObjs;
		} catch (Exception e) {
			System.err.println("【ERROR】 ExecuteSavedQuery service request failed.");
			System.err.println(CommonTools.getExceptionMsg(e));
			return null;
		}
	}

	/**
	 * 更改对象所有权
	 * 
	 * @param datamanagementservice 工具类
	 * @param obj                   对象
	 * @param user                  用户
	 * @param group                 组
	 * @return
	 */
	private static Boolean changeOwner(DataManagementService datamanagementservice, ModelObject obj, User user,
			Group group) {
		try {
			ObjectOwner[] owners = new ObjectOwner[1];
			owners[0] = new ObjectOwner();
			owners[0].group = group;
			owners[0].owner = user;
			owners[0].object = obj;
			ServiceData data = datamanagementservice.changeOwnership(owners);
			if (data.sizeOfPartialErrors() > 0) {
				throw new ServiceException("DataManagementService changeOwner returned a partial error");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("【ERROR】 更改所有权失败，请联系管理员！");
		}
		return false;

	}

	/**
	 * 获取对象的所有权
	 * 
	 * @param datamanagementservice
	 * @param objects
	 * @return
	 */
	public static String getOwnUser(DataManagementService dataManagementService, ModelObject objects) {
		try {
			WorkspaceObject wo = null;
			DispatcherRequest dispatcherRequest = null;
			User owner = null;
			if (!(objects instanceof WorkspaceObject) && !(objects instanceof DispatcherRequest)) {
				return "";
			}
			dataManagementService.refreshObjects(new ModelObject[] {objects});
			dataManagementService.getProperties(new ModelObject[] { objects }, new String[] { " owning_user" });
			if (objects instanceof WorkspaceObject) {
				wo = (WorkspaceObject) objects;				
				owner = (User) wo.get_owning_user();
			} else if (objects instanceof DispatcherRequest) {
				dispatcherRequest = (DispatcherRequest) objects;				
				owner = (User) dispatcherRequest.get_owning_user();
			}
			dataManagementService.refreshObjects(new ModelObject[] { owner });
			dataManagementService.getProperties(new ModelObject[] { owner }, new String[] { "user_id", "user_name" });
			String owningUserId = owner.get_user_id();
			String owningUserName = owner.get_user_name();			
			System.out.println("【INFO】 owningId is: " + owningUserId);
			System.out.println("【INFO】 owningName is: " + owningUserName);
			return owningUserId;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("【ERROR】 获取对象所有者失败！");
			System.err.println(CommonTools.getExceptionMsg(e));
		}
		return null;
	}

	/**
	 * 获取对象的所有者
	 * 
	 * @param revisionList 对象版本集合
	 * @return
	 * @throws NotLoadedException
	 */
	public static Map<ModelObject, String> getModelObjectOwn(DataManagementService datamanagementservice,
			ModelObject[] objs) {
		Map<ModelObject, String> ownMap = new LinkedHashMap<ModelObject, String>();
		for (ModelObject obj : objs) {
			// 获取所有者
			String ownUser = getOwnUser(datamanagementservice, obj);
			if (CommonTools.isEmpty(ownUser)) {
				return null;
			}
			ownMap.put(obj, ownUser);
		}
		return ownMap;
	}

	/**
	 * 获取数据集集合
	 * 
	 * @param datamanagementservice
	 * @param map
	 * @return
	 */
	public static List<ModelObject> getTotalDatasetList(Map<String, List<ModelObject>> map) {
		List<ModelObject> resultList = new ArrayList<ModelObject>();
		for (Map.Entry<String, List<ModelObject>> entry : map.entrySet()) {
			List<ModelObject> list = entry.getValue();
			resultList = Stream.of(resultList, list).flatMap(x -> x.stream()).collect(Collectors.toList());
		}
		return resultList;
	}

	/**
	 * 更改所有权
	 * 
	 * @param dataManagementService 工具类
	 * @param connection            连接
	 * @param itemRevision          对象版本
	 * @param userId                用户ID
	 * @return
	 */
	public static Boolean changeOwnShip(DataManagementService dataManagementService, ModelObject obj, User user,
			Group group) {
		Boolean check = null;
		try {
			check = changeOwner(dataManagementService, obj, user, group);
			if (!check) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("【ERROR】 对象/对象版本版本的所有权更改失败，请联系管理员！");
		}
		return false;
	}

	/**
	 * 获取TC首选项
	 *
	 * @param connection 连接
	 * @param preferName 首相向名称
	 * @return 首选项值
	 */
	public static List<String> getTcPreference(Connection connection, String preferName, String site) {
		try {
			com.teamcenter.services.strong.core.SessionService sessionService = com.teamcenter.services.strong.core.SessionService
					.getService(connection);
			Session.ScopedPreferenceNames[] arrayOfScopedPreferenceNames = new Session.ScopedPreferenceNames[1];
			arrayOfScopedPreferenceNames[0] = new Session.ScopedPreferenceNames();
			arrayOfScopedPreferenceNames[0].names = new String[] { preferName };
			arrayOfScopedPreferenceNames[0].scope = site;
			MultiPreferencesResponse localMultiPreferencesResponse = sessionService
					.getPreferences(arrayOfScopedPreferenceNames);
			String[] preferences = localMultiPreferencesResponse.preferences[0].values;
			if (CommonTools.isNotEmpty(preferences)) { // 不为空
				return Arrays.asList(preferences);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("【ERROR】 " + preferName + "首选项查询失败...");			
		}
		return null;
	}

	/**
	 * 获取数据集
	 * 
	 * @param dataManagementService 工具类
	 * @param objects               数据集对象数组
	 * @return
	 */
	public static List<Dataset> getDataset(DataManagementService dataManagementService, ModelObject[] objects) {
		List<Dataset> datasetList = new ArrayList<>();
		for (ModelObject obj : objects) {
			if (!(obj instanceof Dataset)) {
				continue;
			}
			Dataset dataset = (Dataset) obj;
			datasetList.add(dataset);
		}
		return datasetList;
	}

	/**
	 * 获取对象数据集和BOM视图对象版本集合
	 * 
	 * @param dataManagementService
	 * @param objects
	 * @return
	 */
	public static List<ModelObject> getModelObjects(DataManagementService dataManagementService,
			ModelObject[] objects) {
		List<ModelObject> objList = new ArrayList<>();
		for (ModelObject obj : objects) {
			if (!(obj instanceof Dataset) && !(obj instanceof PSBOMViewRevision)) {
				continue;
			}
			objList.add(obj);
		}
		return objList;
	}

	/**
	 * 移除数据集的命名的引用下的物理文件
	 * 
	 * @param dataset               数据集
	 * @param dataManagementService 工具类
	 * @param type                  类型
	 * @throws NotLoadedException
	 */
	public static void removeFileFromDataset(DataManagementService dataManagementService, Dataset dataset, String type)
			throws NotLoadedException {
		dataManagementService.refreshObjects(new ModelObject[] { dataset });
		dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "ref_list", "object_name" });
		ModelObject[] files = dataset.get_ref_list();
		for (int i = 0; i < files.length; i++) {
			com.teamcenter.services.strong.core._2007_09.DataManagement.NamedReferenceInfo[] nrInfo = new com.teamcenter.services.strong.core._2007_09.DataManagement.NamedReferenceInfo[1];
			nrInfo[0] = new com.teamcenter.services.strong.core._2007_09.DataManagement.NamedReferenceInfo();
			nrInfo[0].clientId = files[i].getUid();
			nrInfo[0].deleteTarget = true;
			nrInfo[0].type = type;
			nrInfo[0].targetObject = files[i];
			com.teamcenter.services.strong.core._2007_09.DataManagement.RemoveNamedReferenceFromDatasetInfo datasetinfo[] = new com.teamcenter.services.strong.core._2007_09.DataManagement.RemoveNamedReferenceFromDatasetInfo[1];
			datasetinfo[0] = new com.teamcenter.services.strong.core._2007_09.DataManagement.RemoveNamedReferenceFromDatasetInfo();
			datasetinfo[0].clientId = dataset.getUid();
			datasetinfo[0].dataset = dataset;
			datasetinfo[0].nrInfo = nrInfo;
			dataManagementService.removeNamedReferenceFromDataset(datasetinfo);
			System.out.println("AAAAA源文件已经删除");
		}
		dataManagementService.refreshObjects(new ModelObject[] { dataset });
	}

	/**
	 * 删除主要对象和次要对象的关系
	 * 
	 * @param dataManagementService 工具类
	 * @param primaryModelObject    主要对象
	 * @param secondaryModelObject  次要对象
	 * @param relationType          关系名
	 */
	public static void deleteRelation(DataManagementService dataManagementService, ModelObject primaryModelObject,
			ModelObject secondaryModelObject, String relationType) {
		Relationship[] relationships = new Relationship[1];
		relationships[0] = new Relationship();
		relationships[0].clientId = "";
		relationships[0].primaryObject = primaryModelObject;
		relationships[0].secondaryObject = secondaryModelObject;
		relationships[0].relationType = relationType;
		dataManagementService.deleteRelations(relationships);
	}

	/**
	 * 通过影响分析获取引用对象
	 * 
	 * @param dataManagementService 工具类
	 * @param object                对象
	 * @param level                 层级
	 * @return
	 */
	public static ItemRevision getWhereReferences(DataManagementService dataManagementService, WorkspaceObject object, int level) {
		ItemRevision itemRev = null;
		WhereReferencedResponse resp = dataManagementService.whereReferenced(new WorkspaceObject[] { object }, level);		
		ServiceData data = resp.serviceData;
		int size = data.sizeOfPlainObjects();
		if (size < 1) {
			System.err.println("【ERROR】 通过影响分析分析获取引用对象失败...");
			return null;
		}
		for (int i = 0; i < size; i++) {
			ModelObject plainObject = data.getPlainObject(i);
			if (!(plainObject instanceof ItemRevision)) {
				continue;
			}
			itemRev = (ItemRevision) plainObject;
			break;
		}
		
		return itemRev;
	}

	/**
	 * 通过命名的引用的"引用POM"关系获取DispatherRequest对象
	 * 
	 * @param dataManagementService
	 * @param itemRev
	 * @return
	 * @throws ServiceException
	 */
	public static Map<ModelObject, String> getWhereReferencesByPOM(DataManagementService dataManagementService,
			ItemRevision itemRev) throws ServiceException {
		Map<ModelObject, String> map = new LinkedHashMap<ModelObject, String>();
		String objectType = itemRev.getTypeObject().getName();
		ICTService service = ICTService.getService(AppXSession.getConnection());
		ICT.Arg[] argss = new ICT.Arg[4];
		ICT.Arg arg0 = new ICT.Arg();
//		arg0.val = "Design Revision";
		arg0.val = "ItemRevision";
		argss[0] = arg0;

		ICT.Arg arg1 = new ICT.Arg();
//		arg1.val = "TYPE::D9_MEDesignRevision::D9_MEDesignRevision::Design Revision";
		arg1.val = "TYPE::" + objectType + "::" + objectType + "::ItemRevision";
		argss[1] = arg1;

		ICT.Arg arg2 = new ICT.Arg();
		arg2.val = itemRev.getUid();
		argss[2] = arg2;

		ICT.Arg arg3 = new ICT.Arg();
		arg3.val = "false";
		argss[3] = arg3;

		InvokeICTMethodResponse response = service.invokeICTMethod("ICCT", "whereReferencedInfo", argss);
		ICT.Arg arg = response.output[0];
		Structure structure = arg.structure[0];
		ICT.Array array = structure.args[1].array[0];
		Entry[] entries = array.entries;
		for (Entry entry : entries) {
			System.out.println(entry.val);
			ServiceData data = dataManagementService.loadObjects(new String[] { entry.val });
			ModelObject obj = data.getPlainObject(0);
			String objType = obj.getTypeObject().getName();
			if ("DispatcherRequest".equals(objType)) {
				DispatcherRequest dispatcherRequest = (DispatcherRequest) obj;
				map.put(dispatcherRequest, objType);
			}
		}
		return map;
	}

	/**
	 * 数据集下载
	 * 
	 * @param dataManagementService 工具类
	 * @param connection            连接
	 * @param dataset               数据集
	 * @param fileExtensions        文件后缀
	 * @param path                  存放路径
	 * @return
	 * @throws NotLoadedException
	 */
	public static String downloadDataset(DataManagementService dataManagementService, Connection connection,
			Dataset dataset, String fileExtensions, String dirPath,String fmsUrl) throws NotLoadedException {
		File newfile = null;
		try {
			dataManagementService.refreshObjects(new ModelObject[] { dataset });
			dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "ref_list" });
			ModelObject[] dsfiles = dataset.get_ref_list();
			if (CommonTools.isEmpty(dsfiles)) {
				return "";
			}
			ImanFile dsFile = null;
			for (int i = 0; i < dsfiles.length; i++) {
				if (!(dsfiles[i] instanceof ImanFile)) {
					continue;
				}
				dsFile = (ImanFile) dsfiles[i];
				dataManagementService.refreshObjects(new ModelObject[] { dsFile });
				dataManagementService.getProperties(new ModelObject[] { dsFile },
						new String[] { "original_file_name" });
				String fileName = dsFile.get_original_file_name();
				System.out.println("【INFO】 fileName: " + fileName);
				if (!fileName.toLowerCase().contains(fileExtensions)) {
					continue;
				}
				// 下载数据集
				FileManagementUtility fileManagementUtility = null;
				if(fmsUrl !=null){
	               fileManagementUtility  = new FileManagementUtility(connection, null, null, new String[]{fmsUrl}, null);
	            }else{
	              fileManagementUtility  = new FileManagementUtility(connection);
	            }
				
				GetFileResponse responseFiles = fileManagementUtility.getFiles(new ModelObject[] { dsFile });
				File[] fileinfovec = responseFiles.getFiles();
				File file = fileinfovec[0];

				String filePath = "";
				if (dirPath.endsWith("\\")) {
					filePath = dirPath + fileName;
				} else {
					filePath = dirPath + File.separator + fileName;
				}
				System.out.println("【INFO】 filePath: " + filePath);
				// 判断数据集是否存在
				newfile = new File(filePath);
				if (newfile.exists()) {
					newfile.delete();
				}
				File dstFile = new File(filePath);
				// 复制文件
				copyFile(file, dstFile);
			}
			return newfile == null ? "" : newfile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
		}
		return "";

	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile 源文件
	 * @param targetFile 目标文件
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;

		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}

			outBuff.flush();
		} finally {
			if (inBuff != null) {
				inBuff.close();
			}
			if (outBuff != null) {
				outBuff.close();
			}
		}
	}

	/**
	 * 返回用户的邮箱
	 * 
	 * @param savedQueryService
	 * @param datamanagementservice
	 * @param map
	 * @param userId
	 * @throws NotLoadedException
	 */
	public static String getEmail(DataManagementService datamanagementservice, String userId) {
		try {
			ModelObject[] userObjects = null;
			User user = null;
			Person person = null;
			userObjects = executequery(TCSearchEnum.__WEB_FIND_USER.queryName(), TCSearchEnum.__WEB_FIND_USER.queryParams(), new String[] { userId },
					datamanagementservice);
			if (CommonTools.isEmpty(userObjects)) { // 判断是否为空
				return null; // 跳出一次循环然后继续下一次循环
			}
			user = (User) userObjects[0];
			datamanagementservice.refreshObjects(new ModelObject[] { user });
			datamanagementservice.getProperties(new ModelObject[] { user }, new String[] { "person" });
			// 获取person对象
			person = user.get_person();
			datamanagementservice.refreshObjects(new ModelObject[] { person });
			datamanagementservice.getProperties(new ModelObject[] { person }, new String[] { "PA9" });
			// 获取邮箱
			String email = person.get_PA9();
			if (CommonTools.isEmpty(email)) {
				System.out.println("【ERROR】 账号为: " + userId + ", 获取邮箱失败...");
				return null;
			}
			return email;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
		}
		return null;

	}

	/**
	 * 获取管理员邮箱
	 * 
	 * @param str
	 * @return
	 */
	public static String getAdminEmail(String str) {
		String[] split = str.split(";");
		String email = "";
		for (String value : split) {
			email += value + "&&";
		}
		return email.substring(0, email.lastIndexOf("&&"));
	}

	/**
	 * 查询方法，返回ModelObject对象
	 *
	 * @param queryname             查询明智
	 * @param entries               查询列名
	 * @param values                查询值
	 * @param connetion             连接
	 * @param datamanagementservice 工具类
	 * @return
	 */
	public static ModelObject[] executequery(String queryname, String[] entries, String[] values,
			DataManagementService datamanagementservice) {
		SavedQueryService queryService = SavedQueryService.getService(AppXSession.getConnection());
		ImanQuery query = null;
		try {
			GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
			if (savedQueries.queries.length == 0) {
				System.out.println("【ERROR】 There are no saved queries in the system.");
				return null;
			}
			for (int i = 0; i < savedQueries.queries.length; i++) {
				if (savedQueries.queries[i].name.equals(queryname)) {
					query = savedQueries.queries[i].query;
					break;
				}
			}
		} catch (ServiceException e) {
			System.err.println("【ERROR】 GetSavedQueries service request failed.");
			System.err.println(CommonTools.getExceptionMsg(e));
			return null;
		}
		if (query == null) {
			System.err.println("【ERROR】 There is not an 'Item Name' query.");
			return null;
		}

		DescribeSavedQueriesResponse descResp = queryService.describeSavedQueries(new ImanQuery[] { query });
		SavedQueryFieldObject[] queryFields = descResp.fieldLists[0].fields;
		for (int i = 0; i < queryFields.length; i++) {
			System.out.println(queryFields[i].entryName);
		}

		try {
			QueryInput[] savedQueryInput = new QueryInput[1];
			savedQueryInput[0] = new QueryInput();
			savedQueryInput[0].query = query;
			savedQueryInput[0].maxNumToReturn = 9999;
			savedQueryInput[0].limitList = new ModelObject[0];
			savedQueryInput[0].entries = entries;
			savedQueryInput[0].values = values;

			SavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
			QueryResults found = savedQueryResult.arrayOfResults[0];

			System.out.println("Found Items:");

			String[] uids = new String[found.objectUIDS.length];
			for (int i = 0; i < found.objectUIDS.length; i++) {

				uids[i] = found.objectUIDS[i];

			}
			if (uids == null || uids.length == 0) {
				return null;
			}
			ServiceData sd = datamanagementservice.loadObjects(uids);
			ModelObject[] foundObjs = new ModelObject[sd.sizeOfPlainObjects()];
			for (int k = 0; k < sd.sizeOfPlainObjects(); k++) {
				foundObjs[k] = (ModelObject) sd.getPlainObject(k);
			}
			return foundObjs;
		} catch (Exception e) {
			System.err.println("【ERROR】 ExecuteSavedQuery service request failed.");
			System.err.println(CommonTools.getExceptionMsg(e));
			return null;
		}
	}

	/**
	 * 开启旁路
	 * 
	 * @param flag
	 * @throws ServiceException
	 */
	public static void byPass(boolean flag) throws ServiceException {
		SessionService sessionservice = SessionService.getService(AppXSession.getConnection());
		com.teamcenter.services.loose.core._2007_12.Session.StateNameValue stateNameValues[] = new com.teamcenter.services.loose.core._2007_12.Session.StateNameValue[1];
		stateNameValues[0] = new com.teamcenter.services.loose.core._2007_12.Session.StateNameValue();
		stateNameValues[0].name = "bypassFlag";
		stateNameValues[0].value = toBooleanString(flag);
		ServiceData servicedata = sessionservice.setUserSessionState(stateNameValues);
		if (servicedata.sizeOfPartialErrors() > 0) {
			throw new ServiceException("SessionService setbypass returned a partial error.");
		} else {
			return;
		}

	}

	public static String toBooleanString(boolean flag) {
		return flag ? "1" : "0";
	}

	/**
	 * 签入(假如已经签出, 则签入, 否则不予处理)
	 * @param dataManagementService
	 * @param object
	 * @param connetion
	 * @throws NotLoadedException
	 * @throws ServiceException
	 */
	public static void checkin(DataManagementService dataManagementService, Connection connection, ModelObject object)
			throws NotLoadedException, ServiceException {
		// 判断是否已经被签出
		dataManagementService.refreshObjects(new ModelObject[] { object });
		dataManagementService.getProperties(new ModelObject[] { object }, new String[] { "checked_out" });
		// 是否签出的标志 Y带包已经签出, ""代表已经签入
		String checkedOut = object.getPropertyObject("checked_out").getStringValue().trim();
		// 无需重复签入
		if ("".equals(checkedOut)) {
			return;
		}
		ReservationService rs = ReservationService.getService(connection);
		ModelObject[] objects = new ModelObject[1];
        objects[0] = object;
        ServiceData servicedata = rs.checkin(objects);
        if (servicedata.sizeOfPartialErrors() > 0) {
            throw new ServiceException("ReservationService checkin returned a partial error.");
        }
        return;
	}
	
	/**
	 * 签出(假如已经签出, 则先签入, 然后签出)
	 * @param dataManagementService 
	 * @param object
	 * @param connection
	 * @return
	 * @throws NotLoadedException
	 * @throws ServiceException
	 */
	public static ModelObject checkout(DataManagementService dataManagementService, Connection connection, ModelObject object) throws NotLoadedException, ServiceException {
		ModelObject checkoutobject = null;
		//判断是否已经被签出
        dataManagementService.refreshObjects(new ModelObject[]{object});
        dataManagementService.getProperties(new ModelObject[]{object}, new String[]{"checked_out"});
        //是否签出的标志 Y带包已经签出, ""代表已经签入
        String checkedOut = object.getPropertyObject("checked_out").getStringValue().trim();
      //如果已经签出, 如果已经签出, 则先签入, 然后再进行签出
        if ("Y".equals(checkedOut)) {
            checkin(dataManagementService, connection, object);
        }
        ReservationService rs = ReservationService.getService(connection);
        ModelObject[] objects = new ModelObject[1];
        objects[0] = object;
        ServiceData servicedata = rs.checkout(objects, "ImportData", "");
        if (servicedata.sizeOfPartialErrors() > 0) {
            throw new ServiceException("ReservationService checkout returned a partial error.");
        }
        checkoutobject = servicedata.getUpdatedObject(0);
        return checkoutobject;        
	}	
}
