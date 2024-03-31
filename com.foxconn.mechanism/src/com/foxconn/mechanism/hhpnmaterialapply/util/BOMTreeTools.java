package com.foxconn.mechanism.hhpnmaterialapply.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.swt.graphics.Image;
import com.foxconn.mechanism.hhpnmaterialapply.constants.BUConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.HHPNStateConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.IconsEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemRevEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.PreferenceConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.RelationConstant;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.hhpnmaterialapply.progress.BooleanFlag;
import com.foxconn.mechanism.hhpnmaterialapply.CustTreeNode;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.HttpUtil;
import com.foxconn.mechanism.util.TCUtil;
import com.google.gson.Gson;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;

/**
 * BOM结构树遍历工具类
 * 
 * @author HuashengYu
 *
 */
public class BOMTreeTools {

	private static Pattern pattern = Pattern.compile("^.+?@\\d+$"); // 以@+数字结尾

	
	/**
	 * 遍历BOM结构树
	 * 
	 * @return
	 * @throws Exception
	 */
	public static BOMInfo traversalBOMTree(AbstractPSEApplication app, TCSession session, String BUName,
			BooleanFlag stopFlag) throws Exception {
		InterfaceAIFComponent targetComponent = null;
		BOMInfo currentInfo = null;
		// 获取选中的目标对象
		targetComponent = app.getTargetComponent();
		if (CommonTools.isEmpty(targetComponent) || !(targetComponent instanceof TCComponentBOMLine)) {
			throw new Exception("请选中BOMLine进行操作");
		}
		TCComponentBOMLine topLine = (TCComponentBOMLine) targetComponent;
		currentInfo = addBomLineInfo(topLine, session, BUName);
		if (CommonTools.isEmpty(currentInfo)) {
			throw new Exception("获取顶层BOMLine字段信息失败");
		}
		Boolean flag = getChildBom(topLine, currentInfo, session, BUName, stopFlag);
		if (!flag && CommonTools.isEmpty(currentInfo)) {
			throw new Exception("获取顶层BOMLine字段信息失败");
		}
		if (!flag && (currentInfo.getChild() != null) && (currentInfo.getChild().size() <= 0)) {			
			throw new Exception("获取子BOMLine字段信息失败");
		}
		return currentInfo;
	}

	/**
	 * 遍历BOM结构树获取子结构树
	 * 
	 * @param topLine
	 * @param currentLine
	 * @return
	 * @throws TCException
	 */
	private static Boolean getChildBom(TCComponentBOMLine topLine, BOMInfo currentLine, TCSession session,
			String BUName, BooleanFlag stopFlag) throws TCException {
		if (stopFlag.getFlag()) {
			return false;
		}
		BOMInfo nextLine = null;
		Boolean flag = true;
		// 刷新BOMLine
		topLine.refresh();
		AIFComponentContext[] childBomlines = topLine.getChildren();
		if (CommonTools.isEmpty(childBomlines)) {
			flag = false;
			return flag;
		}
		for (AIFComponentContext aifComponentContext : childBomlines) {
			TCComponentBOMLine childBomline = (TCComponentBOMLine) aifComponentContext.getComponent();
			nextLine = addBomLineInfo(childBomline, session, BUName);
			if (null == nextLine) {
				flag = false;
				break;
			}
			if ("".equals(nextLine.getItemId()) || nextLine.getItemId() == null) {
				continue;
			}
			
			addList(currentLine, nextLine); // 添加父BOM和子BOM
			
//			String hideOrShow = childBomline.getProperty("fnd0bl_is_mono_override");
//			if ("1".equals(hideOrShow)) {
//				continue;
//			}
			AIFComponentContext[] children = childBomline.getChildren();
			if (CommonTools.isNotEmpty(children)) {
				getChildBom(childBomline, nextLine, session, BUName, stopFlag);
			}
		}

		return flag;

	}

	/**
	 * 添加BOM信息到结构树
	 * 
	 * @param bomLine
	 * @return
	 * @throws TCException
	 */
	private static BOMInfo addBomLineInfo(TCComponentBOMLine bomLine, TCSession session, String BUName) {
		List<String> HHPNList = null; // 存放HHPN鸿海料号信息集合
		try {
			BOMInfo currentInfo = new BOMInfo();
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			itemRevision.clearCache();
			itemRevision.refresh(); // 刷新对象版本
			currentInfo.setItemRevision(itemRevision);
			String bl_indented_title = bomLine.getProperty("bl_indented_title");			
			System.out.println("==>> BOM标题: " + bl_indented_title);
			String designItemId = itemRevision.getProperty("item_id");
			currentInfo.setItemId(bl_indented_title);
			System.out.println("==>> 零组件ID: " + designItemId);
			String version = itemRevision.getProperty("item_revision_id");
			currentInfo.setVersion(version);
			System.out.println("==>> 零组件版本: " + version);
			String objectName = itemRevision.getProperty("object_name");
			currentInfo.setObjectName(objectName);
			System.out.println("==>> 零组件名称: " + objectName);
			String objectType = itemRevision.getTypeObject().getName();
			currentInfo.setObjectType(objectType);
			System.out.println("==>> 零组件类型: " + objectType);
			if (!objectType.contains(ItemRevEnum.DesignRev.type())) {
				return null;
			}
			// 所有的设计对象版本全部设置为不可编辑
			currentInfo.setColor(ColorEnum.Gray.color()); // 设置背景色为灰色
			currentInfo.setModify(false); // 设计对象设置不可编辑
			currentInfo.setImage(new Image(null, BOMTreeTools.class.getResourceAsStream(IconsEnum.DesignRevIcons.relativePath())));
			PropertiesInfo propertiesInfo = new PropertiesInfo();
			propertiesInfo.setItem_ID(designItemId);
			getItemRevProps(itemRevision, propertiesInfo, BUName); // 获取对象版本属性

			boolean flag = false;
			String HHPN = null;
			if (itemRevision.isValidPropertyName("d9_IR_HHPN")) {
				HHPN = itemRevision.getProperty("d9_IR_HHPN") == null ? ""
						: itemRevision.getProperty("d9_IR_HHPN").trim();
			} else if (itemRevision.isValidPropertyName("d9_HHPN")) {
				HHPN = itemRevision.getProperty("d9_HHPN") == null ? "" : itemRevision.getProperty("d9_HHPN").trim();
			}
			if (CommonTools.isNotEmpty(HHPN)) { // 判断鸿海料号属性是否为空
				System.out.println("设计对象ID为: " + designItemId + ", 鸿海料号为: " + HHPN);
				HHPNList = getHHPNList(HHPN);
				if (CommonTools.isNotEmpty(HHPNList)) {
					flag = true; // 设置判断鸿海料号属性不为空的标识
				}
			}
			String qtyUnits = bomLine.getProperty("bl_quantity") == null ? ""
					: bomLine.getProperty("bl_quantity").trim();
			if (!"".equals(qtyUnits)) {
				propertiesInfo.setQtyUnits(Integer.parseInt(qtyUnits));
			}
			currentInfo.setPropertiesInfo(propertiesInfo);
			
			itemRevision.refresh();
			itemRevision.clearCache();
//			if (bl_indented_title.startsWith("ME-RUBB")) {
//				System.out.println(123);
//			}
			TCComponent[] relatedComponents = itemRevision.getRelatedComponents(RelationConstant.REPRESENTATION_FOR);
//			TCComponent[] relatedComponents = itemRevision.getRelatedComponents("IMAN_reference");
			if (CommonTools.isNotEmpty(relatedComponents)) {
				for (TCComponent tcComponent : relatedComponents) {
					if (!(tcComponent instanceof TCComponentItemRevision)) {
						continue;
					}
					TCComponentItemRevision partItemRevision = (TCComponentItemRevision) tcComponent;
					partItemRevision.refresh();
					partItemRevision.clearCache();
					TCComponentItem partItem = partItemRevision.getItem();
					partItem.refresh();
					partItem.clearCache();
//					partItemRevision = partItem.getLatestItemRevision();
					objectType = partItemRevision.getTypeObject().getName();
					if (!ItemRevEnum.CommonPartRev.type().equals(objectType)) {
						continue;
					}
//					DTSAPropertiesInfo childPropertiesInfo = (DTSAPropertiesInfo) propertiesInfo.clone(); // 对象的复制
					PropertiesInfo childPropertiesInfo = new PropertiesInfo();
					childPropertiesInfo.setQtyUnits(propertiesInfo.getQtyUnits()); // 数量和设计对象的数量保持一致
					// 获取版本属性
					getItemRevProps(partItemRevision, childPropertiesInfo, BUName); // 获取对象版本属性

//					partItemRevision.clearCache("item_id");
					String partItemId = partItemRevision.getProperty("item_id");
					System.out.println("==>> 料号对象零组件ID: " + partItemId);
					version = partItemRevision.getProperty("item_revision_id");
					System.out.println("==>> 料号对象零组件版本: " + version);
					String partItemObjectName = partItemRevision.getProperty("object_name");
					BOMInfo childInfo = new BOMInfo();
//					if (partItemId.equals(HHPN) && flag) { // 此料号对象版本为鸿海料号对象
					if (CommonTools.isNotEmpty(HHPNList) && matchRecord(HHPNList, partItemId)) { // 判断此料号对象版本是否为鸿海料号对象
						childPropertiesInfo.setHHPNState(HHPNStateConstant.SYNCSTATE);
						childPropertiesInfo.setHHPN(partItemId);
						removeRecord3(HHPNList, partItemId); // 从鸿海料号集合中移除此记录
//						flag = false;
					}
					childInfo.setItemId(partItemId);
					childInfo.setVersion(version);
					childInfo.setObjectName(partItemObjectName);
					childInfo.setIsExist(true); // 代表此对象版本已经存在
					// 判断料对象版本是否含有写入的权限
					if (!TCUtil.checkUserPrivilege(session, partItemRevision)) {
						childInfo.setColor(ColorEnum.Gray.color()); // 背景色为灰色
						childInfo.setModify(false); // 设置不可编辑
						childInfo.setAddFlag(true); // 设置可以添加到设计对象
						childInfo.setPartRevModify(false); // 设置当前物料对象不可以编辑属性
						childInfo.setDeleteFlag(false); // 设置为不可删除
					} else {
						childInfo.setColor(ColorEnum.Blue.color()); // 设置背景色为蓝色
						childInfo.setModify(true); // 设置可以编辑
						childInfo.setAddFlag(false); // 已经存在设计对象的表示伪文件夹中，无需重复添加
						childInfo.setDeleteFlag(false); // 设置为不可删除
					}
					childInfo.setImage(new Image(null,
							BOMTreeTools.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath())));
					childPropertiesInfo.setItem_ID(partItemId);
					childInfo.setObjectType(objectType);
					childInfo.setItemRevision(partItemRevision);
					childInfo.setPropertiesInfo(childPropertiesInfo);
					// 添加父和子
					addList(currentInfo, childInfo);
				}
			}
			if (CommonTools.isNotEmpty(HHPNList)) {
				findHHPNPartRev(session, BUName, currentInfo, propertiesInfo, HHPNList);
				if (CommonTools.isNotEmpty(propertiesInfo.getHHPN())) {
					propertiesInfo.setHHPN(propertiesInfo.getHHPN().substring(0, propertiesInfo.getHHPN().length() - 1));
				}
				if (CommonTools.isNotEmpty(propertiesInfo.getHHPNState())) {
					propertiesInfo.setHHPNState(propertiesInfo.getHHPNState().substring(0, propertiesInfo.getHHPNState().length() - 1));
				}
			}
			return currentInfo;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在Teamcenter系统中/或者在PNMS系统中查询鸿海料号(代表鸿海料号游离于Teamcenter中，或者是存在于PNMS系统中)
	 * 
	 * @param session
	 * @param BUName
	 * @param currentInfo
	 * @param propertiesInfo
	 * @param HHPN
	 * @throws TCException
	 */
	public static void findHHPNPartRev(TCSession session, String BUName, BOMInfo currentInfo,
			PropertiesInfo propertiesInfo, List<String> HHPNList) throws TCException {
		String objectName = null;
		String objectType = null;
//		for (String HHPN : HHPNList) {
		for (Iterator it = HHPNList.iterator(); it.hasNext();) {
			String HHPN = ((String) it.next()).trim();
			TCComponentItem item = TCUtil.findItem(HHPN); // 在Teamcenter中查询鸿海料号对象是否存在
			if (CommonTools.isNotEmpty(item)) {
				item.refresh();
				item.clearCache();				
				TCComponentItemRevision HHPNItemRevision = item.getLatestItemRevision();
				HHPNItemRevision.refresh();
				HHPNItemRevision.clearCache();				
				objectType = HHPNItemRevision.getTypeObject().getName();				
				String HHPNItemId = HHPNItemRevision.getProperty("item_id");
				System.out.println("==>> 料号对象零组件ID: " + HHPNItemId);
				String HHPNVersion = HHPNItemRevision.getProperty("item_revision_id");
				System.out.println("==>> 料号对象零组件版本: " + HHPNVersion);
				objectName = HHPNItemRevision.getProperty("object_name");
				BOMInfo HHPNBomInfo = new BOMInfo();
				HHPNBomInfo.setItemRevision(HHPNItemRevision);
				HHPNBomInfo.setItemId(HHPNItemId);
				HHPNBomInfo.setVersion(HHPNVersion);
				HHPNBomInfo.setObjectName(objectName);
				HHPNBomInfo.setObjectType(objectType);
				HHPNBomInfo.setIsExist(true); // 代表此对象版本已经存在
				if (objectType.contains(ItemRevEnum.DesignRev.type())) {
					HHPNBomInfo.setImage(new Image(null,
							BOMTreeTools.class.getResourceAsStream(IconsEnum.DesignRevIcons.relativePath())));
				} else if (ItemRevEnum.CommonPartRev.type().equals(objectType)) {
					HHPNBomInfo.setImage(new Image(null,
							BOMTreeTools.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath())));
				}
//						DTSAPropertiesInfo HHPNpropertiesInfo = (DTSAPropertiesInfo) propertiesInfo.clone(); // 对象的复制
				PropertiesInfo HHPNpropertiesInfo = new PropertiesInfo();
				HHPNpropertiesInfo.setQtyUnits(propertiesInfo.getQtyUnits()); // 数量和设计对象的数量一致
				// 获取版本属性
				getItemRevProps(HHPNItemRevision, HHPNpropertiesInfo, BUName); // 获取对象版本属性
				HHPNpropertiesInfo.setItem_ID(HHPNItemId);
				HHPNpropertiesInfo.setHHPN(HHPNItemId);
				HHPNpropertiesInfo.setHHPNState(HHPNStateConstant.SYNCSTATE);
				// 判断鸿海料对象版本是否含有写入的权限
				if (!TCUtil.checkUserPrivilege(session, HHPNItemRevision)) {
					HHPNBomInfo.setColor(ColorEnum.Gray.color()); // 背景色为灰色
					HHPNBomInfo.setModify(false); // 设置不可编辑
					HHPNBomInfo.setAddFlag(true); // 设置可以添加到设计对象(没有编辑权但是可以添加)
					HHPNBomInfo.setPartRevModify(false); // 设置当前物料对象不可以编辑属性
					HHPNBomInfo.setDeleteFlag(false); // 设置为不可删除
				} else {
					HHPNBomInfo.setColor(ColorEnum.Blue.color()); // 设置背景色为蓝色
					HHPNBomInfo.setModify(true); // 设置可以编辑
					HHPNBomInfo.setAddFlag(true); // 可以添加到设计对象下
					HHPNBomInfo.setDeleteFlag(false); // 设置为不可删除
				}
				HHPNBomInfo.setPropertiesInfo(HHPNpropertiesInfo);
				// 添加父和子
				addList(currentInfo, HHPNBomInfo);
				it.remove(); // 移除此记录
			} else { // 发送请求至PMS获取鸿海料号信息
				// String result =
				// HttpUtil.sendGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo","hhpn="+
				// HHPN);

				System.out.println("==>> 鸿海料号为: " + HHPN);
				System.out.println("******** 开始查询PNMS系统 ********");
//				String result = HttpUtil.httpGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=" + HHPN);
//				String result = HttpUtil.httpGet("http://10.203.163.44/tc-integrate/pnms/getHHPNInfo", "hhpn=" + HHPN);
				String result = handlerPNMSInqury(session, HHPN, 1, 10); // 执行PNMS物料查询
				System.out.println("******** 查询PNMS系统结束 ********");
				BOMInfo PNMSBomInfo = new BOMInfo();
				PNMSBomInfo.setItemId(HHPN);
				PNMSBomInfo.setObjectName(HHPN);
				PNMSBomInfo.setObjectType(ItemRevEnum.CommonPartRev.type());
				PNMSBomInfo.setImage(
						new Image(null, BOMTreeTools.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath())));
				PNMSBomInfo.setColor(ColorEnum.Green.color()); // 设置背景色为绿色
				PropertiesInfo PNMSPropertiesInfo = (PropertiesInfo) propertiesInfo.clone(); // 对象的复制
//						DTSAPropertiesInfo PNMSPropertiesInfo = new DTSAPropertiesInfo();
				PNMSPropertiesInfo.setItem_ID(HHPN);
				PNMSPropertiesInfo.setHHPN(HHPN);
				String hhpnStr = propertiesInfo.getHHPN() == null ? "" : propertiesInfo.getHHPN().trim();
				String hhpnStateStr = propertiesInfo.getHHPNState() == null ? "" : propertiesInfo.getHHPNState().trim();
				if ("failure".equals(result)) {
					currentInfo.setIsExist(false);
					currentInfo.setModify(false);
//					propertiesInfo.setHHPN(HHPN);					
					propertiesInfo.setHHPN(hhpnStr + HHPN + ",");
//					propertiesInfo.setHHPNState(HHPNStateConstant.ERROR);
					propertiesInfo.setHHPNState(hhpnStateStr + HHPNStateConstant.ERROR + ",");
				} else if (CommonTools.isEmpty(result)) {
					currentInfo.setIsExist(false);
					currentInfo.setModify(false);
//					propertiesInfo.setHHPN(HHPN);
					propertiesInfo.setHHPN(hhpnStr + HHPN + ",");
//					propertiesInfo.setHHPNState(HHPNStateConstant.NOTSYCSTATE);
					propertiesInfo.setHHPNState(hhpnStateStr + HHPNStateConstant.NOTSYCSTATE + ",");
				} else {
					Gson gson = new Gson();
					Map map = new HashMap();
					map = gson.fromJson(result, map.getClass());
					setHPNSProperties(map, PNMSPropertiesInfo); // 设置PNMS系统属性
					PNMSBomInfo.setIsExist(false); // 代表此对象版本不存在
					PNMSBomInfo.setModify(true); // 代表此记录可以修改
					PNMSPropertiesInfo.setHHPNState(HHPNStateConstant.SYNCSTATE);
//							propertiesInfo.setHHPNState(HHPNStateConstant.SYNCSTATE);
					PNMSBomInfo.setPropertiesInfo(PNMSPropertiesInfo);
					// 添加父和子
					addList(currentInfo, PNMSBomInfo);
					it.remove(); // 移除此记录
				}
			}
		}
	}
	
	
	/**
	 * 执行PNMS物料查询
	 * @param HHPN 鸿海料号
	 * @param threadNum 线程数
	 * @param timeOut 设置的超时时间
	 * @return
	 */
	public static String handlerPNMSInqury(TCSession session, String HHPN, int threadNum, int timeOut) {
		String url = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, PreferenceConstant.D9_SPRINGCLOUD_URL);
		// 获取线程池
		ExecutorService es = Executors.newFixedThreadPool(threadNum);
		// Future用于执行多线程的执行结果
		Future<String> future = es.submit(() -> {
			String result = HttpUtil.httpGet(url + "/tc-integrate/pnms/getHHPNInfo", "hhpn=" + HHPN);
			return result;
		});
		try {
			// futrue.get()测试被执行的程序是否能在timeOut时限内返回字符串
			return future.get(timeOut, TimeUnit.SECONDS);// 任务处理超时时间单位设为 1 秒
		} catch (Exception e) {
			System.out.println("输出异常：" + e);
		} finally {
			// 关闭线程池
			es.shutdown();
		}
		return "failure";
	}

	/**
	 * 返回鸿海料号的list集合
	 * 
	 * @param HHPN
	 * @return
	 */
	public static List<String> getHHPNList(String HHPN) {
		String[] split = HHPN.split("\\|");
		List<String> list = new ArrayList<>(Arrays.asList(split));
		return list;
	}

	/**
	 * 设置PNMS系统属性
	 * 
	 * @param map
	 * @param propertiesInfo
	 */
	private static void setHPNSProperties(Map map, PropertiesInfo propertiesInfo) {
		map.forEach((key, value) -> {
//			log.info("key=" + key + ",value=" + value);
			if ("mfg".equals(key)) {
				System.out.println("==>> 供应商编号: " + value);
				propertiesInfo.setManufacturerID((String) value);
			} else if ("mfgpn".equals(key)) {
				System.out.println("==>> 供应商料号: " + value);
				propertiesInfo.setManufacturerPN((String) value);
			} else if ("bupn".equals(key)) {
				System.out.println("==>> 临时料号: " + value);
				propertiesInfo.setBUPN((String) value);
			} else if ("vendor".equals(key)) {
				System.out.println("==>> 客户编号: " + value);
				propertiesInfo.setCustomerID((String) value);
			} else if ("vendorpn".equals(key)) {
				System.out.println("==>> 客户料号: " + value);
				propertiesInfo.setCustomerPN((String) value);
			} else if ("des".equals(key)) {
				System.out.println("==>> 英文描述: " + value);
				propertiesInfo.setEnglishDescription((String) value);
			}
		});
	}

	/**
	 * 获取对象版本属性
	 * 
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	private static void getItemRevProps(TCComponentItemRevision itemRevision, PropertiesInfo propertiesInfo,
			String BUName) throws TCException {
		String customerPN = null;
		if (itemRevision.isValidPropertyName("d9_IR_CustomerPN")) {
			customerPN = itemRevision.getProperty("d9_IR_CustomerPN") == null ? ""
					: itemRevision.getProperty("d9_IR_CustomerPN").trim();
		} else if (itemRevision.isValidPropertyName("d9_CustomerPN")) {
			customerPN = itemRevision.getProperty("d9_CustomerPN") == null ? ""
					: itemRevision.getProperty("d9_CustomerPN").trim();
		}
		propertiesInfo.setCustomerPN(customerPN);

		String chineseDescription = null;
		if (itemRevision.isValidPropertyName("d9_IR_ChineseDescription")) {
			chineseDescription = itemRevision.getProperty("d9_IR_ChineseDescription") == null ? ""
					: itemRevision.getProperty("d9_IR_ChineseDescription").trim();
		} else if (itemRevision.isValidPropertyName("d9_ChineseDescription")) {
			chineseDescription = itemRevision.getProperty("d9_ChineseDescription") == null ? ""
					: itemRevision.getProperty("d9_ChineseDescription").trim();
		}
		propertiesInfo.setChineseDescription(chineseDescription);

		String EnglishDescriptipon = null;
		if (itemRevision.isValidPropertyName("d9_IR_EnglishDescription")) {
			EnglishDescriptipon = itemRevision.getProperty("d9_IR_EnglishDescription") == null ? ""
					: itemRevision.getProperty("d9_IR_EnglishDescription").trim();
		} else if (itemRevision.isValidPropertyName("d9_EnglishDescription")) {
			EnglishDescriptipon = itemRevision.getProperty("d9_EnglishDescription") == null ? ""
					: itemRevision.getProperty("d9_EnglishDescription").trim();
		}
		propertiesInfo.setEnglishDescription(EnglishDescriptipon);

		if (BUConstant.DTSABUNAME.equals(BUName) || BUConstant.MNTBUNAME.equals(BUName) ||
				BUConstant.PRTBUNAME.equals(BUName)) { // 当前BU为DTSA、MNT、PRT
			String material = null;
			if (itemRevision.isValidPropertyName("d9_IR_Material")) {
				material = itemRevision.getProperty("d9_IR_Material") == null ? ""
						: itemRevision.getProperty("d9_IR_Material").trim();
			} else if (itemRevision.isValidPropertyName("d9_Material")) {
				material = itemRevision.getProperty("d9_Material") == null ? ""
						: itemRevision.getProperty("d9_Material").trim();
			}
			propertiesInfo.setMaterial(material);
			
			String color = null;
			if (itemRevision.isValidPropertyName("d9_IR_MaterialColor")) {
				color = itemRevision.getProperty("d9_IR_MaterialColor") == null ? ""
						: itemRevision.getProperty("d9_IR_MaterialColor").trim();
			} else if (itemRevision.isValidPropertyName("d9_MaterialColor")) {
				color = itemRevision.getProperty("d9_MaterialColor") == null ? ""
						: itemRevision.getProperty("d9_MaterialColor").trim();
			}
			propertiesInfo.setColor(color);
			
			String surfaceFinished = null;
			if (itemRevision.isValidPropertyName("d9_IR_Finish")) {
				surfaceFinished = itemRevision.getProperty("d9_IR_Finish") == null ? ""
						: itemRevision.getProperty("d9_IR_Finish").trim();
			} else if (itemRevision.isValidPropertyName("d9_Finish")) {
				surfaceFinished = itemRevision.getProperty("d9_Finish") == null ? ""
						: itemRevision.getProperty("d9_Finish").trim();
			}
			propertiesInfo.setSurfaceFinished(surfaceFinished);
		}
		if (BUConstant.DTSABUNAME.equals(BUName) || BUConstant.MNTBUNAME.equals(BUName)) { // 当前BU为DTSA\MNT
			
			String partWeight = null;
			if (itemRevision.isValidPropertyName("d9_IR_PartWeight")) {
				partWeight = itemRevision.getProperty("d9_IR_PartWeight") == null ? ""
						: itemRevision.getProperty("d9_IR_PartWeight").trim();
			} else if (itemRevision.isValidPropertyName("d9_PartWeight")) {
				partWeight = itemRevision.getProperty("d9_PartWeight") == null ? ""
						: itemRevision.getProperty("d9_PartWeight").trim();
			}
			propertiesInfo.setPartWeight(partWeight);			

			String remark = null;
			if (itemRevision.isValidPropertyName("d9_IR_Remarks")) {
				remark = itemRevision.getProperty("d9_IR_Remarks") == null ? ""
						: itemRevision.getProperty("d9_IR_Remarks").trim();
			} else if (itemRevision.isValidPropertyName("d9_Remarks")) {
				remark = itemRevision.getProperty("d9_Remarks") == null ? ""
						: itemRevision.getProperty("d9_Remarks").trim();
			}
			propertiesInfo.setRemark(remark);
			
		}

		if (BUConstant.DTSABUNAME.equals(BUName)) { // 当前BU为DTSA
			String ulClass = null;
			if (itemRevision.isValidPropertyName("d9_IR_ULClass")) {
				ulClass = itemRevision.getProperty("d9_IR_ULClass") == null ? ""
						: itemRevision.getProperty("d9_IR_ULClass").trim();
			} else if (itemRevision.isValidPropertyName("d9_ULClass")) {
				ulClass = itemRevision.getProperty("d9_ULClass") == null ? ""
						: itemRevision.getProperty("d9_ULClass").trim();
			}
			propertiesInfo.setULClass(ulClass);

			String painting = null;
			if (itemRevision.isValidPropertyName("d9_IR_Painting")) {
				painting = itemRevision.getProperty("d9_IR_Painting") == null ? ""
						: itemRevision.getProperty("d9_IR_Painting").trim();
			} else if (itemRevision.isValidPropertyName("d9_Painting")) {
				painting = itemRevision.getProperty("d9_Painting") == null ? ""
						: itemRevision.getProperty("d9_Painting").trim();
			}
			propertiesInfo.setPainting(painting);

			String printing = null;
			if (itemRevision.isValidPropertyName("d9_IR_Printing")) {
				printing = itemRevision.getProperty("d9_IR_Printing") == null ? ""
						: itemRevision.getProperty("d9_IR_Printing").trim();
			} else if (itemRevision.isValidPropertyName("d9_Printing")) {
				printing = itemRevision.getProperty("d9_Printing") == null ? ""
						: itemRevision.getProperty("d9_Printing").trim();
			}
			propertiesInfo.setPrinting(printing);

			if (itemRevision.isValidPropertyName("d9_Technology")) {
				String technology = itemRevision.getProperty("d9_Technology") == null ? ""
						: itemRevision.getProperty("d9_Technology");
				propertiesInfo.setTechnology(technology);
			}

			String adhesive = null;
			if (itemRevision.isValidPropertyName("d9_Adhesive")) {
				adhesive = itemRevision.getProperty("d9_Adhesive") == null ? ""
						: itemRevision.getProperty("d9_Adhesive").trim();
			} else if (itemRevision.isValidPropertyName("d9_ADHESIVE")) {
				adhesive = itemRevision.getProperty("d9_ADHESIVE") == null ? ""
						: itemRevision.getProperty("d9_ADHESIVE").trim();
			}
			propertiesInfo.setADHESIVE(adhesive);
		}

		if (BUConstant.PRTBUNAME.equals(BUName)) { // 当前BU为PRT
			String customerDrawingNumber = null;
			if (itemRevision.isValidPropertyName("d9_IR_CustomerDrawingNumber")) {
				customerDrawingNumber = itemRevision.getProperty("d9_IR_CustomerDrawingNumber") == null ? ""
						: itemRevision.getProperty("d9_IR_CustomerDrawingNumber").trim();
			} else if (itemRevision.isValidPropertyName("d9_CustomerDrawingNumber")) {
				customerDrawingNumber = itemRevision.getProperty("d9_CustomerDrawingNumber") == null ? ""
						: itemRevision.getProperty("d9_CustomerDrawingNumber").trim();
			}
			propertiesInfo.setCustomerDrawingNumber(customerDrawingNumber);

			if (itemRevision.isValidPropertyName("d9_Customer3DRev")) {
				String customer3DRev = itemRevision.getProperty("d9_Customer3DRev") == null ? ""
						: itemRevision.getProperty("d9_Customer3DRev").trim();
				propertiesInfo.setCustomer3DRev(customer3DRev);
			}

			if (itemRevision.isValidPropertyName("d9_Customer2DRev")) {
				String customer2DRev = itemRevision.getProperty("d9_Customer2DRev") == null ? ""
						: itemRevision.getProperty("d9_Customer2DRev").trim();
				propertiesInfo.setCustomer2DRev(customer2DRev);
			}

			String customerID = null;
			if (itemRevision.isValidPropertyName("d9_IR_Customer")) {
				customerID = itemRevision.getProperty("d9_IR_Customer") == null ? ""
						: itemRevision.getProperty("d9_IR_Customer").trim();
			} else if (itemRevision.isValidPropertyName("d9_Customer")) {
				customerID = itemRevision.getProperty("d9_Customer") == null ? ""
						: itemRevision.getProperty("d9_Customer").trim();
			}
			propertiesInfo.setCustomerID(customerID);

			String SMPartMaterialDimension = null;
			if (itemRevision.isValidPropertyName("d9_IR_SMPartMatDimension")) {
				SMPartMaterialDimension = itemRevision.getProperty("d9_IR_SMPartMatDimension") == null ? ""
						: itemRevision.getProperty("d9_IR_SMPartMatDimension").trim();
			} else if (itemRevision.isValidPropertyName("d9_SMPartMatDimension")) {
				SMPartMaterialDimension = itemRevision.getProperty("d9_SMPartMatDimension") == null ? ""
						: itemRevision.getProperty("d9_SMPartMatDimension").trim();
			}
			propertiesInfo.setSmPartMaterialDimension(SMPartMaterialDimension);

//			if (itemRevision.isValidPropertyName("projects_list")) {
//				String projectName = itemRevision.getProperty("projects_list");
//				propertiesInfo.setProjectName(projectName);
//			}
			
			if (itemRevision.isValidPropertyName("project_list")) {
				String projectName = getProjectName(itemRevision);
				propertiesInfo.setProjectName(projectName);
			}
			
			
			if (itemRevision.isValidPropertyName("d9_Module")) {
				String module = itemRevision.getProperty("d9_Module") == null ? ""
						: itemRevision.getProperty("d9_Module").trim();
				propertiesInfo.setModule(module);
			}
		}
		if (BUConstant.MNTBUNAME.equals(BUName)) { // BU为MNT
			if (itemRevision.isValidPropertyName("d9_ReferenceDimension")) {
				String referenceDimension = itemRevision.getProperty("d9_ReferenceDimension") == null ? ""
						: itemRevision.getProperty("d9_ReferenceDimension").trim();
				propertiesInfo.setReferenceDimension(referenceDimension);
			}
			if (itemRevision.isValidPropertyName("d9_RunnerWeight")) {
				String runnerWeight = itemRevision.getProperty("d9_RunnerWeight") == null ? ""
						: itemRevision.getProperty("d9_RunnerWeight").trim();
				propertiesInfo.setRunnerWeight(runnerWeight);
			}
			if (itemRevision.isValidPropertyName("d9_TotalWeight")) {
				String totalweight = itemRevision.getProperty("d9_TotalWeight") == null ? ""
						: itemRevision.getProperty("d9_TotalWeight").trim();
				propertiesInfo.setTotalweight(totalweight);
			}
		}
	}

	/**
	 * 添加父和子
	 *
	 * @param current
	 * @param next
	 */

	private static void addList(BOMInfo current, BOMInfo next) throws TCException {
		List<BOMInfo> child = current.getChild();
		if (null == child) {
			child = new ArrayList<BOMInfo>();
			current.setChild(child);
		}
		/*
		 * if (current.getParent() != null) { BOMInfo parent = parent =
		 * getParent(current); current.setParent(parent); }
		 */
		Boolean flag = false;
		for (BOMInfo info : child) {
			if (info.getPropertiesInfo().getItem_ID().equals(next.getPropertiesInfo().getItem_ID())) {
				flag = true;
				PropertiesInfo childPropertiesInfo = next.getPropertiesInfo();
				PropertiesInfo propertiesInfo = info.getPropertiesInfo();
				propertiesInfo.addQtyUnits(childPropertiesInfo.getQtyUnits()); // 对ID相同的记录,进行归纳，数量合并
				if (info.getObjectType().contains(ItemRevEnum.DesignRev.type())) {
					info.setItemId(info.getItemRevision().getProperty("object_string") + " x " + propertiesInfo.getQtyUnits());
				} else if (info.getObjectType().equals(ItemRevEnum.CommonPartRev.type())) {
					info.setItemId(info.getPropertiesInfo().getItem_ID() + " x " + propertiesInfo.getQtyUnits());
				}
				List<BOMInfo> child2 = next.getChild();
				if (child2 != null && child2.size() > 0) {
					for (BOMInfo info2 : child2) {
						addList(info, info2);
					}
				}
				break;
			}
		}
//		child.add(next);
//		next.setParent(current);		
		if (flag) {
			return;
		} else {
			child.add(next);
			next.setParent(current);
		}
	}

	/**
	 * 判断临时料号是否以@+数字结尾
	 * 
	 * @param designItemId
	 * @param partItemId
	 * @return
	 */
	public static boolean checkTempPart(String designItemId, String partItemId) {
		Matcher matcher = pattern.matcher(partItemId); // 判断临时料号是否以@+数字结尾
		if (partItemId.contains(designItemId) && matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断临时料号是否以@+数字结尾
	 * 
	 * @param itemId
	 * @return
	 */
	public static boolean checkTempPart(String itemId) {
		Matcher matcher = pattern.matcher(itemId); // 判断临时料号是否以@+数字结尾
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据属性去重
	 * 
	 * @param bomInfoList
	 * @return
	 */
	public static List<BOMInfo> removeDupliById(List<BOMInfo> bomInfoList) {

		Set<BOMInfo> bomInfoSet = new TreeSet<>(
				(o1, o2) -> o1.getPropertiesInfo().getItem_ID().compareTo(o2.getPropertiesInfo().getItem_ID()));

		bomInfoSet.addAll(bomInfoList);

		return new ArrayList<>(bomInfoSet);

	}

	/**
	 * 获取结点数据模型集合
	 * 
	 * @param resultList
	 * @return
	 */
	public static List<BOMInfo> getBOMInfoList(List<Object> resultList) {
		List<BOMInfo> list = new ArrayList<BOMInfo>();
		resultList.forEach(obj -> {
			CustTreeNode custTreeNode = (CustTreeNode) obj;			
			BOMInfo bomInfo = custTreeNode.getBomInfo();
			list.add(bomInfo);
		});
		return list;
	}

	/**
	 * 获取结构树结点集合
	 * @param list
	 * @return
	 */
	public static List<CustTreeNode> getCustTreeNodeList(List<Object> list) {
		List<CustTreeNode> resultList = new ArrayList<CustTreeNode>();
		list.forEach(obj -> {
			CustTreeNode custTreeNode = (CustTreeNode) obj;			
			resultList.add(custTreeNode);
		});
		return resultList;
	}
	
	
	/**
	 * 移除临时料号不是以@+数字结尾的记录
	 * 
	 * @param resultList
	 */
	public static void removeRecord(List<BOMInfo> resultList) {
		IntStream.range(0, resultList.size())
				.filter(i -> pattern.matcher(resultList.get(i).getPropertiesInfo().getItem_ID()).matches() == false
						&& ItemRevEnum.CommonPartRev.type().equals(resultList.get(i).getObjectType()))
				.boxed().findFirst().map(i -> resultList.remove((int) i));
	}

	/**
	 * 移除临时料号是已经被删除的
	 * 
	 * @param resultList
	 */
	public static void removeRecord2(List<BOMInfo> resultList) {
		IntStream.range(0, resultList.size())
				.filter(i -> ItemRevEnum.CommonPartRev.type().equals(resultList.get(i).getObjectType())
						&& (resultList.get(i).getDeleteFlag() == true))
				.boxed().findFirst().map(i -> resultList.remove((int) i));
	}

	/**
	 * 删除符合条件的list集合中的一条记录
	 *
	 * @param
	 */
	public static void removeRecord3(List<String> list, String checkStr) {
		IntStream.range(0, list.size()).filter(i -> checkStr.equals(list.get(i).trim())).boxed().findFirst()
				.map(i -> list.remove((int) i));
	}

	/**
	 * 记录匹配
	 * 
	 * @param list
	 * @param checkStr
	 * @return
	 */
	public static boolean matchRecord(List<String> list, String checkStr) {
		return list.stream().anyMatch(str -> str.trim().equals(checkStr));
	}	
	
	/**
	 * 获取专案名
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	public static String getProjectName(TCComponentItemRevision itemRev) throws TCException {
		TCProperty props = itemRev.getTCProperty("project_list");
		if (CommonTools.isEmpty(props)) {
			return "";
		}
		TCComponent[] pjs = props.getReferenceValueArray();
		return Stream.of(pjs).map(e -> ((TCComponentProject) e).getProjectName()).collect(Collectors.joining(","));
	}
	
	
	public static void findItem(String str) throws TCException {
		TCComponentItem item = TCUtil.findItem(str);
		item.refresh();
		item.clearCache();
		System.out.println(item.getProperty("item_id"));
	}
}
