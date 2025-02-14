package com.teamcenter.rac.issuemanager.util.reviewissue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.swt.widgets.Control;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentParticipant;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core.LOVService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantOutput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.Participants;
import com.teamcenter.services.rac.core._2013_05.LOV.InitialLovData;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVSearchResults;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVValueRow;
import com.teamcenter.services.rac.core._2013_05.LOV.LovFilterData;
import com.teamcenter.soaictstubs.stringSeq_tHolder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class Tools {

	public static AIFDesktop getDesktop() {
		return AIFDesktop.getActiveDesktop();
	}

	public static TCSession getTCSession() {
		return RACUIUtil.getTCSession();
	}

	public static void infoMsgBox(String info, String title) {

		MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.INFORMATION);

	}

	public static void warningMsgBox(String info, String title) {
		MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.WARNING);

	}

	public static void errorMsgBox(String info, String title) {

		MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.ERROR);

	}

	public static List<String> getArrayPreference(TCSession session, int scope, String preferenceName) {
		try {
			TCPreferenceService tCPreferenceService = session.getPreferenceService();
			tCPreferenceService.refresh();
			String[] array = tCPreferenceService.getStringArray(scope, preferenceName);
			return new ArrayList<String>(Arrays.asList(array));
		} catch (Exception e) {
			System.out.print(e);
		}
		return null;
	}

	/**
	 * get LOV
	 * 
	 * @param session
	 * @param itemRevType
	 * @param prop
	 * @return
	 */
	public static List<String> getLovValues(TCSession session, TCComponentItemRevisionType itemRevType, String prop) {
		List<String> lovList = new ArrayList<String>();
		try {
			TCPropertyDescriptor property = itemRevType.getPropertyDescriptor(prop);
			TCComponentListOfValues listOfValues = property.getLOV();
			if (null == listOfValues) {
				return null;
			}
			String lovType = listOfValues.getProperty("lov_type");
			if ("Fnd0ListOfValuesDynamic".equals(lovType)) {
				String valueProperty = listOfValues.getProperty("fnd0lov_value");
				LOVService lovService = LOVService.getService(session);
				InitialLovData input = new InitialLovData();
				LovFilterData filter = new LovFilterData();
				filter.order = 0;
				filter.numberToReturn = 10000;
				filter.maxResults = 10000;
				input.lov = listOfValues;
				input.filterData = filter;
				LOVSearchResults lovResult = lovService.getInitialLOVValues(input);
				if (lovResult.serviceData.sizeOfPartialErrors() < 1) {
					for (LOVValueRow row : lovResult.lovValues) {
						Map<String, String[]> map = row.propDisplayValues;
						for (Entry<String, String[]> entry : map.entrySet()) {
							String key = entry.getKey();
							if (key.equals(valueProperty)) {
								String[] values = entry.getValue();
								if (values != null && values.length > 0 && lovList.contains(values[0]) == false) {
									lovList.add(values[0]);
								}
							}
						}
					}
				}
			} else {
				ListOfValuesInfo lovi = listOfValues.getListOfValues();
				String[] displayValues = lovi.getLOVDisplayValues();
				for (String displayValue : displayValues) {
					String realValue = lovi.getRealValue(displayValue).toString();
					if (!lovList.contains(realValue)) {
						lovList.add(realValue);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lovList;
	}

	
	/**
     * 
     * @param lovName
     * @return
     */
    public static Object[] getLovValues(String lovName, boolean display) {
        TCComponentListOfValues lov = TCComponentListOfValuesType.findLOVByName(getTCSession(), lovName);
        if (lov == null)
        {
            return new String[0];
        }
        try
        {
        	Object[] objs = null;
        	if (display) {
        		objs = lov.getListOfValues().getLOVDisplayValues();
			} else {
				objs = lov.getListOfValues().getListOfValues();
			}
            
            return objs;
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return new String[0];
    }
    
    
	/**
	 * set Bypass
	 * 
	 * @param session
	 * @throws Exception
	 */
	public static void setBypass(TCSession session) {
		try {
			TCUserService userService = session.getUserService();
			userService.call("set_bypass", new String[] { "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*	public static List<JSONObject> getWorkerList(TCSession session) {
		try {
			Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
			TCComponentUser user = session.getUser();
			String customer = user.getProperty("os_username");
			// 查询系统的用户信息
			List<JSONObject> userList = addUser(session, "__D9_Find_User","os_username",customer);
			
			// 獲取一級賬號的uid集合
			List<String> uids = userList.parallelStream().map(item -> item.getStr("uid")).collect(Collectors.toList());
			
			// 查询二级账号对应的一级账号的信息
			String url = getArrayPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,"D9_SpringCloud_URL").get(0);
			String result = HttpUtil.post(url + "/tc-service/issueManagement/getByUids",JSONUtil.toJsonStr(uids));
			if(StrUtil.isNotBlank(result)) {
				JSONObject obj = JSONUtil.parseObj(result);
				if(!"0000".equals(obj.getStr("code"))) {
					throw new TCException("查詢二級賬號對應的TC賬號出錯");
				}
				JSONArray jsonArray = obj.getJSONArray("data");
				if(CollUtil.isNotEmpty(jsonArray)) {
					for (Object o : jsonArray) {
						JSONObject parseObj = JSONUtil.parseObj(o);
						String keyUid = parseObj.getStr("secondAccountUid");
						if(StrUtil.isBlank(keyUid)) {
							// 查詢二級賬號
							TCComponent[] executeQuery = executeQuery(session, "__D9_Find_Actual_User", new String[] {"item_id"},
									new String[] {parseObj.getStr("no")});
							if (executeQuery != null && executeQuery.length > 0) {
								keyUid = executeQuery[0].getUid();
							}else {
								continue;
							}
						}
						String userUid = parseObj.getStr("tcUid");
						List<JSONObject> oList = userList.parallelStream().filter(item-> item.getStr("uid").equals(userUid)).collect(Collectors.toList());
						if(CollUtil.isNotEmpty(oList)) {
							if(CollUtil.isEmpty(map.get(keyUid))) {
								map.put(keyUid, oList);
							}else {
								map.get(keyUid).addAll(oList);
							}
						}
					}
				} else {
					throw new TCException("未查詢到二級用戶對應的TC賬號");
				}
			}
			Set<String> set = new HashSet<String>();
			List<JSONObject> list = new ArrayList<JSONObject>();
			for(String key : map.keySet()) {
				TCComponent component = loadObjectByUid(key);
				if(ObjectUtil.isNull(component)) {
					continue;
				}
				JSONObject obj = JSONUtil.createObj();
				obj.set("uid", component.getUid());
				obj.set("item_id", component.getProperty("item_id"));
				obj.set("object_name", component.getProperty("object_name"));
				obj.set("user_info", component.getProperty("d9_UserInfo"));
				obj.set("disabled", false);
				String jsonStr = JSONUtil.toJsonStr(map.get(key));
				JSONArray array =JSONUtil.parseArray(jsonStr);
				obj.set("parent", array);
				for (Object object : array) {
					set.add(JSONUtil.toJsonStr(object));
				}
				list.add(obj);
			}			
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/
	public static List<JSONObject> getWorkerList(TCSession session) {
		try {
			Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
			TCComponentUser user = session.getUser();
			String customer = user.getProperty("os_username");
			
			List<JSONObject> list=new ArrayList<>();
			// 查询二级账号对应的一级账号的信息
			String url = getArrayPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,"D9_SpringCloud_URL").get(0);
			String result = HttpUtil.get(url + "/tc-service/issueManagement/getWorkList?customer="+customer);
			JSONObject obj=JSONUtil.parseObj(result);
			JSONArray arr=obj.getJSONArray("data");
			for(int i=0;i<arr.size();i++) {
				list.add(arr.getJSONObject(i));
			}
			return  list;
			
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<JSONObject> addUser(TCSession session, String name,String key,String value) throws Exception {
		List<JSONObject> list = new ArrayList<JSONObject>();
		TCComponent[] executeQuery = executeQuery(session, name, new String[] {key},
				new String[] {value});
		if (executeQuery != null && executeQuery.length > 0) {
			for (int j = 0; j < executeQuery.length; j++) {
				JSONObject obj = JSONUtil.createObj();
				obj.set("uid", executeQuery[j].getUid());
				if("__D9_Find_User".equals(name)) {
					obj.set("item_id", executeQuery[j].getProperty("user_id"));
					obj.set("object_name", executeQuery[j].getProperty("user_name"));
					obj.set("user_info", executeQuery[j].getProperty("user_name") + "("+ executeQuery[j].getProperty("user_id") +")");
					obj.set("disabled", true);
				}else {
					obj.set("item_id", executeQuery[j].getProperty("item_id"));
					obj.set("object_name", executeQuery[j].getProperty("object_name"));
					obj.set("user_info", executeQuery[j].getProperty("d9_UserInfo"));
					obj.set("disabled", false);
				}
				list.add(obj);
			}
		}
		return list;
	}
	
	public static void setWorker(TCComponentItemRevision itemRev, TCSession session, String uid,String relate) throws TCException {
		if(relate.endsWith("_ActualUser")) {
			if(StrUtil.isNotBlank(uid)) {
				TCComponent objComponent = loadObjectByUid(uid);
				itemRev.setRelated(relate, new TCComponent[] {objComponent});
			}else {
				itemRev.setRelated(relate, new TCComponent[] {});
			}
		}else {
			TCComponentParticipant[] participants = itemRev.getParticipants();
			for (int i = 0; i < participants.length; i++) {
				if(relate.equals(participants[i].getType())) {
					removeWorker(session, itemRev, participants[i]);
					break;
				}
			}
			if(StrUtil.isNotBlank(uid)) {
				TCComponent objComponent = loadObjectByUid(uid);
				if(objComponent instanceof TCComponentUser) {
					TCComponentUser user = (TCComponentUser)objComponent;
					TCComponentGroupMember groupMember = user.getGroupMembers()[0];
					setWorker(session, itemRev, relate, groupMember);
				}
			}
		}
	}
	
	 public static TCComponent loadObjectByUid(String uid) {
        DataManagementService dmService = DataManagementService.getService(getTCSession());
        ServiceData serviceData = dmService.loadObjects(new String[] { uid });
        if (serviceData != null && serviceData.sizeOfPlainObjects() > 0)  
        	
        	
        	
        	
        {
            TCComponent modelObject = (TCComponent) serviceData.getPlainObject(0);
            dmService.refreshObjects(new TCComponent[] { modelObject });
            return modelObject;
        }
        return null;
    }
	 
	 
	 public static void setWorker(TCSession session,TCComponentItemRevision itemRevision,String type,TCComponentGroupMember groupMember) {
	    	try {
	    		DataManagementService dataManagementService = DataManagementService.getService(session);
	        	com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantInfo addParticipantInfo = new com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantInfo();
	        	addParticipantInfo.itemRev = itemRevision;
	        	com.teamcenter.services.rac.core._2008_06.DataManagement.ParticipantInfo info = new com.teamcenter.services.rac.core._2008_06.DataManagement.ParticipantInfo();
	    		info.clientId = itemRevision.getUid();
	    		info.participantType = type;
	    		info.assignee = groupMember;
	    		addParticipantInfo.participantInfo = new com.teamcenter.services.rac.core._2008_06.DataManagement.ParticipantInfo[] {info};
	        	AddParticipantOutput addParticipants = dataManagementService.addParticipants(new com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantInfo[] {addParticipantInfo});
	        	ServiceData serviceData = addParticipants.serviceData;
	        	if (serviceData.sizeOfPartialErrors() > 0) {
	        		throw new TCException("Add Design Error");
	        	}
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public static void removeWorker(TCSession session,TCComponentItemRevision itemRevision,TCComponentParticipant participant) {
	    	try {
	    		DataManagementService dataManagementService = DataManagementService.getService(session);
	    		com.teamcenter.services.rac.core._2008_06.DataManagement.Participants participants = new com.teamcenter.services.rac.core._2008_06.DataManagement.Participants();
	    		participants.itemRev = itemRevision;
	    		participants.participant = new TCComponentParticipant[] {participant};

	    		ServiceData serviceData = dataManagementService.removeParticipants(new Participants[] {participants});
	        	if (serviceData.sizeOfPartialErrors() > 0) {
	        		throw new TCException("Remove Design Error");
	        	}
	    	}catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	    
    public static TCComponent[] executeQuery(TCSession session, String queryName, String[] keys, String[] values) throws Exception
    {
        TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
        TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(queryName);
        if (keys.length != values.length)
        {
            throw new Exception("queryAttributies length is not equal queryValues length");
        }
        String[] queryAttributeDisplayNames = new String[keys.length];
        TCQueryClause[] elements = query.describe();
        for (int i = 0; i < keys.length; i++)
        {
            for (TCQueryClause element : elements)
            {
                // System.out.println("queryName: " + element.getAttributeName());
                if (element.getAttributeName().equals(keys[i]))
                {
                    queryAttributeDisplayNames[i] = element.getUserEntryNameDisplay();
                }
            }
            if (queryAttributeDisplayNames[i] == null || queryAttributeDisplayNames[i].equals(""))
            {
                throw new Exception("queryAttribute\"" + keys[i] + "\"can not find DisplayName");
            }
        }
        // System.out.println("queryAttributeDisplayNames:" + Arrays.toString(queryAttributeDisplayNames));
        // System.out.println("queryValues:" + Arrays.toString(values));
        return query.execute(queryAttributeDisplayNames, values);
    }
    
    
	/**
	 * close Bypass
	 * 
	 * @param session
	 * @throws Exception
	 */
	public static void closeBypass(TCSession session) {
		try {
			TCUserService userService = session.getUserService();
			userService.call("close_bypass", new String[] { "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getIndex(List<String> list, String value) {
		return IntStream.range(0, list.size()).filter(i -> value.equals(list.get(i))).findFirst().orElse(0);
	}
	
	public static int getIndex(Object[] objs, String value) {
		return IntStream.range(0, objs.length).filter(i -> value.toLowerCase().equals(objs[i].toString().toLowerCase())).findFirst().orElse(0);
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public static Control getMapKeyByValue(Map<Control,String> map, String key) {
		return map.entrySet().stream().collect(Collectors.toMap(entity-> entity.getValue(),entity-> entity.getKey())).get(key);
	}
}
