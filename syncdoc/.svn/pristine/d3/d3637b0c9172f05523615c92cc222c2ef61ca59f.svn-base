package com.foxconn.dp.plm.syncdoc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.hutool.log.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.foxconn.dp.plm.syncdoc.domain.Constants;
import com.foxconn.dp.plm.syncdoc.teamcenter.clientx.AppXSession;
import com.google.gson.Gson;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.internal.loose.core.SessionService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core.ProjectLevelSecurityService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.CreateDatasetsResponse;
import com.teamcenter.services.strong.core._2006_03.DataManagement.CreateItemsResponse;
import com.teamcenter.services.strong.core._2006_03.DataManagement.CreateRelationsResponse;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ExtendedAttributes;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ItemProperties;
import com.teamcenter.services.strong.core._2006_03.DataManagement.Relationship;
import com.teamcenter.services.strong.core._2007_01.DataManagement.VecStruct;
import com.teamcenter.services.strong.core._2007_01.DataManagement.WhereReferencedResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement.DatasetProperties2;
import com.teamcenter.services.strong.core._2013_05.DataManagement.ReviseIn;
import com.teamcenter.services.strong.core._2013_05.DataManagement.ReviseObjectsResponse;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.query._2006_03.SavedQuery;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.strong.query._2007_06.SavedQuery.ExecuteSavedQueriesResponse;
import com.teamcenter.services.strong.query._2007_06.SavedQuery.SavedQueryInput;
import com.teamcenter.services.strong.query._2007_06.SavedQuery.SavedQueryResults;
import com.teamcenter.services.strong.workflow.WorkflowService;
import com.teamcenter.services.strong.workflow._2008_06.Workflow.ContextData;
import com.teamcenter.services.strong.workflow._2008_06.Workflow.InstanceInfo;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.TC_Project;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class TCUtils {
	
    /**
     * 创建item
     * @param itemIds
     * @param itemType
     * @return
     * @throws ServiceException
     */
    public static Item createItems(AppXSession session,String itemId, String itemType, String itemName,
    		Map<String, String> propMap) throws ServiceException {
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
    	
        ItemProperties[] itemProps = new ItemProperties[1];
    	ItemProperties itemProperty = new ItemProperties();
    	itemProperty.clientId = itemId + "--" + getRandomNumber();
        itemProperty.itemId = itemId;
        itemProperty.revId = "01";
        itemProperty.name = itemName;
        itemProperty.type = itemType;
        itemProperty.description = "";
        itemProperty.uom = "";
        
        itemProperty.extendedAttributes = new ExtendedAttributes[1];
        ExtendedAttributes theExtendedAttr = new ExtendedAttributes();
        theExtendedAttr.attributes = propMap;
        theExtendedAttr.objectType = itemType;
        itemProperty.extendedAttributes[0] = theExtendedAttr;
        itemProps[0] = itemProperty;
        
        CreateItemsResponse response = dmService.createItems(itemProps, null, "");
        ServiceData serviceData = response.serviceData;
        if (serviceData.sizeOfPartialErrors() > 0) {
        	throw new ServiceException(serviceData.getPartialError(0).toString());
        }
        return response.output[0].item;
    }

	
	public static String createTCFolder(AppXSession session,String parentId,String childName){
		String puid = "";
		try {
			DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
			Folder parentFolder = findObjectByUid(dmService,parentId);
			getProperty(dmService, parentFolder, "object_type");
			String parentFolderType = parentFolder.get_object_type();
			String childFolderType = Constants.ARCHIVE;
			if(parentFolderType.equals(Constants.PLATFORMFOUND)) {
				childFolderType = Constants.FUNCTION;
			} 
			if(parentFolderType.equals(Constants.FUNCTION)) {
				childFolderType = Constants.PHASE;
			}
			Map<String,String> propMap = new HashMap<>();
            propMap.put("object_name",childName);
            Folder childFolder = createFolder(dmService,childFolderType,propMap);
            puid = childFolder.getUid();
            addContents(session, parentFolder, childFolder, "contents");
		} catch (Exception e) {
			LogFactory.get().error(e);
		}
		return puid;
	}
	
	public static String getRandomNumber(){
		String str = "ABCDEFJHIJKLMNOPQRSTUVWXYZ0123456789";
		String uuid = new String();
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			uuid+=ch;
		}
		return uuid;
	}
	
	/**
     * 获取单个属性
     * @param object
     * @param propName
     */
    public static void getProperty(DataManagementService dmService, ModelObject object, String propName){
        ModelObject[] objects = { object };
        String[] atts = { propName };
        dmService.getProperties(objects, atts);
    }
	/**
     * 获取单个属性
     * @param object
     * @param propName
     */
    public static void loadProperty(AppXSession session,ModelObject object, String propName){
        ModelObject[] objects = { object };
        String[] atts = { propName };
        DataManagementService.getService(AppXSession.getConnection()).getProperties(objects, atts);
    }
	
	/**
     * 根据UID查数据
     * @param uid
     * @return
     * @throws ServiceException
     */
    public static Folder findObjectByUid(DataManagementService dmService, String uid) throws ServiceException {
        ServiceData serviceData = dmService.loadObjects(new String[]{uid});
        if(serviceData.sizeOfPartialErrors() > 0) {
        	throw new ServiceException(serviceData.getPartialError(0).toString());
        }
        return (Folder) serviceData.getPlainObject(0);
    }
    
    public static Folder createFolder(DataManagementService dmService, String folderType, Map<String,String> propMap) {
    	Folder folder = null;
        try{
            CreateIn[] createIns = new CreateIn[1];
            createIns[0] = new CreateIn();
            CreateInput createInput = new CreateInput();
            createInput.boName = folderType;
            createInput.stringProps = propMap;
            createIns[0].data = createInput;
            CreateResponse response = dmService.createObjects(createIns);
            folder = (Folder) response.output[0].objects[0];
        }catch (Exception e){
			LogFactory.get().error(e);
        }
        return folder;
    }
    
    /**
     * 添加关系
     * @param primaryObject
     * @param secondaryObject
     * @throws ServiceException 
     */
    public static void addContents(AppXSession session, ModelObject primaryObject, 
    		ModelObject secondaryObject, String relationshipName) throws ServiceException {
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
        Relationship[] relationships = new Relationship[1];
        relationships[0] = new Relationship();
        relationships[0].primaryObject = primaryObject;
        relationships[0].secondaryObject = secondaryObject;
        relationships[0].relationType = relationshipName;//"contents";
        dmService.createRelations(relationships);
    }
    
    public static void deleteContents(AppXSession session, Folder primaryFolder, ModelObject modelObject) {
		DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
		Relationship[] relationships = new Relationship[1];
		relationships[0] = new Relationship();
		relationships[0].clientId = "";
		relationships[0].primaryObject = primaryFolder;
		relationships[0].secondaryObject = modelObject;
		relationships[0].relationType = "contents";
		dmService.deleteRelations(relationships);
		dmService.refreshObjects(new ModelObject[] { modelObject });
	}
    
    //删除对象
    public static void deleteObject(AppXSession session, ModelObject[] folders) {
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
        ServiceData deleteObjects = dmService.deleteObjects(folders);
        System.out.println("删除文件夹！");
    }
    
    //根据版本查询item
    public static Item getItemByRev(AppXSession session, ItemRevision itemRev) {
    	Item itemId = null;
    	try {
	    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
	    	ModelObject[] objects = { itemRev };
	        String[] atts = { "items_tag" };
	        dmService.getProperties(objects, atts);
			itemId = itemRev.get_items_tag();
		} catch (NotLoadedException e) {
			LogFactory.get().error(e);
		}
    	return itemId;
    }
    
    /**
     * 设置属性值
     * @param object
     * @param propName
     * @param propValue
     */
    public static void setProperties(AppXSession session, ModelObject object, String propName, String propValue) {
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
        Map<String, VecStruct> map = new HashMap<>();
        VecStruct vecStruce = new VecStruct();
        vecStruce.stringVec = new String[] { propValue };
        map.put(propName, vecStruce);
        dmService.setProperties(new ModelObject[] { object }, map);
        dmService.refreshObjects(new ModelObject[] { object });
    }
    
    
    //根据UID查数据
    public static ModelObject findObjectByPuid(AppXSession session, String puid){
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
    	SessionService sService = SessionService.getService(AppXSession.getConnection());		
    	sService.refreshPOMCachePerRequest(true);
        ServiceData sd = dmService.loadObjects( new String[]{puid} );
        int sizeOfPlainObjects = sd.sizeOfPlainObjects();
        if(sizeOfPlainObjects > 0){
        	return sd.getPlainObject(0);
        }else {
        	return null;
		}
    }
    
    //升版
    public static ModelObject reviseItemRev(AppXSession session, ModelObject obj, String itemRevName,String itemRevisionId) throws Exception{
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
    	ReviseIn[] reviseIns = new ReviseIn[1];
    	//reviseIns[0].deepCopyDatas = null;
    	reviseIns[0] = new ReviseIn();
    	Map<String, String[]> map = new HashMap<String, String[]>();
    	map.put("object_name", new String[] {itemRevName});
    	map.put("item_revision_id", new String[] {itemRevisionId});
    	reviseIns[0].reviseInputs = map;
    	reviseIns[0].targetObject = obj;
    	ReviseObjectsResponse resp = dmService.reviseObjects(reviseIns);
    	ServiceData serviceData = resp.serviceData;
    	if(serviceData.sizeOfPartialErrors() > 0) {
    		throw new Exception(serviceData.getPartialError(0).toString());
    	}
    	return resp.output[0].objects[0];
    }
    
    //创建数据集
    public static void createDataset(AppXSession session,ItemRevision itemRev,String datasetName,String datasetDesc) throws ServiceException{
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
        Dataset dataset = null;
        DatasetProperties2[] datasetProps = new DatasetProperties2[1];
        DatasetProperties2 datasetProp = new DatasetProperties2();

        datasetProp.clientId = "datasetWriteTixTestClientId";
        datasetProp.type = "HTML";
        datasetProp.name = datasetName;
        datasetProp.description = datasetDesc;
        datasetProps[0] = datasetProp;
        CreateDatasetsResponse dsResp = dmService.createDatasets2(datasetProps);
        dataset = dsResp.output[0].dataset;

        Relationship[] relationships = new Relationship[1];
        Relationship relationship = new Relationship();

        relationship.clientId = "";
        relationship.primaryObject = itemRev;
        relationship.secondaryObject = dataset;
        relationship.relationType = "IMAN_specification";
        relationship.userData = null;
        relationships[0] = relationship;
        CreateRelationsResponse crResponse = dmService.createRelations(relationships);
        ServiceData crServiceData = crResponse.serviceData;
        if(crServiceData.sizeOfPartialErrors() > 0){
        	throw new ServiceException(crServiceData.getPartialError(0).toString());
        }

        dmService.refreshObjects(new ModelObject[]{dataset});
    }
    
    /**
     * 获取最新版本
     * @param item
     * @return
     * @throws NotLoadedException
     */
    public static ItemRevision getItemLatestRevision(AppXSession session,Item item) throws NotLoadedException {
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
        ModelObject[] objects = { item };
        String[] atts = { "revision_list" };
        dmService.getProperties(objects, atts);
        ModelObject[] itemRevs = item.get_revision_list();
        ItemRevision itemRev = (ItemRevision) itemRevs[itemRevs.length - 1];
        return itemRev;
    }

    
    /**
     * 新建流程
     * @param workflowName
     * @param processTemplate
     * @param objects
     * @throws ServiceException
     */
  	public static void createNewProcess(AppXSession session,String workflowName, String processTemplate, ModelObject[] objects) throws ServiceException {
  		WorkflowService wfService = WorkflowService.getService(AppXSession.getConnection());
  		boolean startImmediately = true;
  		String observerKey = "";
  		String name = workflowName;
  		String subject = "";
  		String description = "";
  		
  		ContextData contextData = new ContextData();
  		contextData.attachmentCount = objects.length;
  		String[] attachments = new String[objects.length];
  		int[] attachmentTypes = new int[objects.length];
  		for(int i=0;i<objects.length;i++) {
  			attachments[i] = objects[i].getUid();
  			attachmentTypes[i] = 1;
  		}
  		contextData.attachments = attachments;
  		contextData.attachmentTypes = attachmentTypes;
  		contextData.processTemplate = processTemplate;
  		contextData.subscribeToEvents = false;
  		contextData.subscriptionEventCount=0;
  		
  		InstanceInfo instanceInfo = wfService.createInstance(startImmediately, observerKey, name, subject, description, contextData);
  		ServiceData serviceData = instanceInfo.serviceData;
  		if(serviceData.sizeOfPartialErrors() > 0){
        	throw new ServiceException(serviceData.getPartialError(0).toString());
        }
  	}
  	
    /**
     * 调用系统查询
     * @param searchName
     * @param keys
     * @param values
     * @return
     * @throws ServiceException
     */
    public static ModelObject[] executeQuery(AppXSession session, String searchName, String[] keys, String[] values) throws ServiceException {
    	SavedQueryService queryService = SavedQueryService.getService(AppXSession.getConnection());
    	ImanQuery query = null;
    	GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
    	for (int i = 0; i < savedQueries.queries.length; i++) {
            if (savedQueries.queries[i].name.equals(searchName)) {
                query = savedQueries.queries[i].query;
                break;
            }
        }
    	if (query == null) {
    		throw new ServiceException("系统中未找到 " + searchName + " 查询..");
        }
    	Map<String,String> entriesMap = new HashMap<>();
        SavedQuery.DescribeSavedQueriesResponse describeSavedQueriesResponse = queryService.describeSavedQueries(new ImanQuery[]{query});
        for (SavedQuery.SavedQueryFieldObject field : describeSavedQueriesResponse.fieldLists[0].fields) {
            String attributeName = field.attributeName;
            String entryName = field.entryName;
            entriesMap.put(attributeName,entryName);
        }
        String[] entries = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            entries[i] = entriesMap.get(keys[i]);
        }
        SavedQueryInput[] savedQueryInput = new SavedQueryInput[1];
        savedQueryInput[0] = new SavedQueryInput();
        savedQueryInput[0].query = query;
        savedQueryInput[0].entries = entries;
        savedQueryInput[0].values = values;
        ExecuteSavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
        ServiceData serviceData = savedQueryResult.serviceData;
        if(serviceData.sizeOfPartialErrors() > 0) {
        	throw new ServiceException(serviceData.getPartialError(0).toString());
        }
        SavedQueryResults found = savedQueryResult.arrayOfResults[0];
        return found.objects;
    }
  	
  	//指派项目
  	public static void assignedProject(AppXSession session, ModelObject itemRev, TC_Project project){
        com.teamcenter.services.strong.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects assignedOrRemovedObjects = new com.teamcenter.services.strong.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects();
        assignedOrRemovedObjects.objectToAssign = new ModelObject[] {itemRev};
        //assignedOrRemovedObjects.objectToRemove = null;
        assignedOrRemovedObjects.projects = new TC_Project[] {project};

        com.teamcenter.services.strong.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects[] aassignedorremovedobjects = new com.teamcenter.services.strong.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects[1];
        aassignedorremovedobjects[0] = assignedOrRemovedObjects;

        ProjectLevelSecurityService projectLevelSecurityService = ProjectLevelSecurityService.getService(AppXSession.getConnection());
        ServiceData serviceData = projectLevelSecurityService.assignOrRemoveObjects(aassignedorremovedobjects);

        if(serviceData.sizeOfPartialErrors() > 0){
            for (int i = 0; i < serviceData.sizeOfPartialErrors(); i++) {
                System.out.println(serviceData.getPartialError(i));
            }
        }
    }
  	
  	/**
 	 * 获取ItemRev指定类型数据集
 	 * @param itemRev
 	 * @param datasetTypeName
 	 * @return
 	 * @throws NotLoadedException
 	 */
    public static List<Dataset> getItemRevDataset(AppXSession session, ItemRevision itemRev,String datasetTypeName) throws NotLoadedException{
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
    	List<Dataset> datasetList = null;
    	ModelObject[] itemRevs = { itemRev };
    	dmService.refreshObjects(itemRevs);
    	dmService.getProperties(itemRevs, new String[]{"IMAN_specification"});
    	ModelObject[] datasets = itemRev.get_IMAN_specification();
    	dmService.refreshObjects(datasets);
    	dmService.getProperties(datasets, new String[]{"object_type"});
    	for (int i = 0; i < datasets.length; i++){
            Dataset dataset = (Dataset) datasets[i];
            String datasetType = dataset.get_object_type();
            if (datasetType.equals(datasetTypeName)){
            	if(datasetList == null) {
            		datasetList = new ArrayList<Dataset>();
            	}
            	datasetList.add(dataset);
            }
        }
        return datasetList;
    }
    
    public static List<ModelObject> getObjWhereReferenced(AppXSession session, WorkspaceObject[] objects, int numLevels) {
    	DataManagementService dmService = DataManagementService.getService(AppXSession.getConnection());
    	WhereReferencedResponse response = dmService.whereReferenced(objects, numLevels);
    	ServiceData serviceData = response.serviceData;
    	int sizeOfPlainObjects = serviceData.sizeOfPlainObjects();
    	List<ModelObject> objs = null;
    	if(sizeOfPlainObjects > 0) {
    		objs = new ArrayList<ModelObject>();
    		for (int i = 0; i < sizeOfPlainObjects; i++) {
    			ModelObject obj = serviceData.getPlainObject(i);
    			objs.add(obj);
    		}
    	}
    	return objs;
    }
    
    /**
	 * 发送外部邮箱
	 * @param to
	 * @param title
	 * @param content
	 */
	public static void sendExternalEmail(String content) {
		try {
			Map<String, String> httpmap = new HashMap<String, String>();
		    String mailUrl = PropertitesUtil.props.getProperty("poc.mail");
		    httpmap.put("url", mailUrl+"/tc-mail/teamcenter/sendMail3");
			httpmap.put("sendTo", "cheryl.l.wang@foxconn.com");
			httpmap.put("sendCc", "chen.zhang@foxconn.com");
			httpmap.put("subject", "TC文件管理系統同步異常");
			httpmap.put("htmlmsg", content);
			String url = httpmap.get("url");
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			Gson gson = new Gson();
			String params = gson.toJson(httpmap);
			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
			StringBody contentBody = new StringBody(params,contentType);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);					
			builder.addPart("data", contentBody);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					EntityUtils.toString(entitys);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
   
}
