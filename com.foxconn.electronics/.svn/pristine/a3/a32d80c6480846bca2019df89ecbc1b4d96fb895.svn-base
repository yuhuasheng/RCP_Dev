package com.foxconn.electronics.dbomconvertebom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentSite;
import com.teamcenter.rac.kernel.TCDateFormat;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.util.StrUtil;

public class DBOMConvertEBOMHandler extends AbstractHandler {
	public final static String REPRESENTATION_FOR = "representation_for";
	public final static String TC_IS_REPRESENTED_BY = "TC_Is_Represented_By";

	public final static String EnglishDescription = "d9_EnglishDescription";
	public final static String ChineseDescription = "d9_ChineseDescription";
	public final static String Customer = "d9_Customer";
	public final static String CustomerPN = "d9_CustomerPN";
	public final static String ManufacturerID = "d9_ManufacturerID";
	public final static String ManufacturerPN = "d9_ManufacturerPN";
	public final static String CustomerPNRev = "d9_CustomerPNRev";
	public final static String Object_name = "object_name";

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Registry reg = Registry.getRegistry("com.foxconn.electronics.dbomconvertebom.dbomconvertebom");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		AbstractPSEApplication pseApp = (AbstractPSEApplication) app;
		
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		TCComponentBOMLine topBOMLine = null;
		if(CommonTools.isNotEmpty(targetComponent)) {
			if(targetComponent instanceof TCComponentBOMLine) {
				topBOMLine = (TCComponentBOMLine) targetComponent;
			}
		}
		if(topBOMLine==null) {
			topBOMLine = pseApp.getTopBOMLine();
		}
	
		Shell shell = app.getDesktop().getShell();
		TCSession session = (TCSession) app.getSession();
		
		if (topBOMLine == null) {
			MessageDialog.openWarning(shell, "Warn", reg.getString("selectError"));
			return null;
		}

		try {
			TCHttp http = TCHttp.startJHttp();
			http.addHttpController(new DBOMConvertEBOMController(topBOMLine, session));
			System.out.println("http.getPort() = " + http.getPort());

			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,
					"D9_SpringCloud_URL") +"/DBOMTOEBOM?port="+ http.getPort();
			System.out.println("ebom url = " + url);
			new DBOMConvertEBOMDialog(pseApp, shell, reg, url);
		} catch (Exception e) {
			MessageDialog.openInformation(shell, "提示", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public static void checkhhpn(DBOMInfo alldbominfo, TCSession session,StringBuffer strb) throws Exception {
		List<HHPNInfo> hhpninfoList = alldbominfo.getHhpnInfo();
		String item_id = alldbominfo.getItemid();
		if (hhpninfoList != null && hhpninfoList.size() > 0) {
			for (int i = 0; i < hhpninfoList.size(); i++) {
				String hhpn = hhpninfoList.get(i).getHhpn();
				if (StrUtil.isNotEmpty(hhpn)) {
					if(!getRightStr(hhpn)) {
						
						if(!strb.toString().contains(item_id)) {
							strb.append(item_id);
							strb.append("\t");
						}
						
//						if(i == 0) {
//							strb.append(item_id);
//						} else {
//							strb.append("替代料："+item_id);
//						}
//						strb.append("\t");
					}			
				}
			}
			
//			String hhpn = hhpninfoList.get(0).getHhpn();
//			if (StrUtil.isNotEmpty(hhpn)) {
//				if(!getRightStr(hhpn)) {
//					strb.append(item_id);
//					strb.append("\t");
//				}			
//			}
		}
		List<DBOMInfo> childs = alldbominfo.getChilds();
		if (childs != null && childs.size() > 0) {
			for (int i = 0; i < childs.size(); i++) {
				checkhhpn(childs.get(i), session,strb);
			}
		}
	}
	public static TCComponentItemRevision checkTopbomline(DBOMInfo alldbominfo, TCSession session) throws Exception {
		TCComponentItemRevision itemrev = null;
		List<HHPNInfo> hhpninfoList = alldbominfo.getHhpnInfo();
		if (hhpninfoList != null && hhpninfoList.size() > 0) {
			String hhpn = hhpninfoList.get(0).getHhpn();
			if (hhpn != null && !hhpn.isEmpty()) {
				TCComponentItemRevision itemrevhhpn = getItemRevisionById(hhpn, session);
				if (itemrevhhpn != null) {
					AIFComponentContext[] itemRelated = itemrevhhpn.getItem().getRelated("bom_view_tags");
					if (itemRelated != null && itemRelated.length > 0) {
						itemrev = itemrevhhpn;
					}
				}
			} 
//			else {
//				System.out.println("鸿海料号不能为空");
//			}
		} else {
			String itemid = alldbominfo.getItemid() + "@1";
			System.out.println("check hhpn@1 = " + itemid);
			TCComponentItemRevision latestItemRevision = getItemRevisionById(itemid, session);
			if (latestItemRevision != null) {
				AIFComponentContext[] itemRelated = latestItemRevision.getItem().getRelated("bom_view_tags");
				if (itemRelated != null && itemRelated.length > 0) {
					itemrev = latestItemRevision;
				}
			}
		}

		return itemrev;
	}

	public static void cutBOMLine(TCSession session,TCComponentItemRevision itemrevhhpn_1) throws TCException {
		boolean isWrite = checkOwninguserisWrite(session, itemrevhhpn_1);
		if (isWrite) {
			TCComponentBOMWindow bomwindow = createBOMWindow(session);
			TCComponentBOMLine bomline = getTopBomline(bomwindow, itemrevhhpn_1);
			AIFComponentContext[] childrens = bomline.getChildren();
			if (childrens.length > 0) {
				for (int i = 0; i < childrens.length; i++) {
					TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
					cutBOMLine(session, children.getItemRevision());
					children.cut();
				}
			}
		}
	}
	
	
	public static TCComponentItemRevision savedbominfo(DBOMInfo alldbominfo, TCSession session, TCComponentBOMWindow bomwindow, TCComponentBOMLine bomline)
			throws Exception {
		String title = alldbominfo.getIndented_title();
		System.out.println("title = " + title);

		String quantity = alldbominfo.getQuantity();
		int quantutyint = !"".equals(quantity) && !"1".equals(quantity) ? Integer.parseInt(quantity) : 1;
		System.out.println("quantutyint = " + quantutyint);

		TCComponentBOMLine addbomline = null;
		TCComponentItemRevision itemrevhhpn = null;
		TCComponentItemRevision itemrev = getItemRevisionUID(alldbominfo.getItemRevUid(), session);

		List<HHPNInfo> hhpninfoList = alldbominfo.getHhpnInfo();
		if (hhpninfoList != null && hhpninfoList.size() > 0) {
			if(bomline == null) {
				//顶层BOMLine 不考虑替代料
				HHPNInfo hhpnInfo = hhpninfoList.get(0);
				String hhpn = hhpnInfo.getHhpn();
				String hhpnName = hhpnInfo.getObjectName();
				System.out.println("hhpn = " + hhpn);
				if (hhpn != null && !hhpn.isEmpty()) {
					
					itemrevhhpn = getItemRevisionById(hhpn, session);
					if (itemrevhhpn == null ) {
						// 查询L5Agile/L6Agile/PNMS
						QueryL5L6PNMS queryL5L6PNMS = new QueryL5L6PNMS(session);
						itemrevhhpn = queryL5L6PNMS.itemrevhhpn(hhpn,hhpnName);
					}
					
					// 添加关系
					if (checkPartItemRev(itemrev, itemrevhhpn,session)) {
						itemrevhhpn.add(TC_IS_REPRESENTED_BY, itemrev);
					}
					
					if (bomline == null) {
						//获取bomline
						addbomline = getTopBomline(bomwindow, itemrevhhpn);
					} 
				}
			} else {
				for (int i = 0; i < hhpninfoList.size(); i++) {
					HHPNInfo hhpnInfo = hhpninfoList.get(i);
					
					String hhpn = hhpnInfo.getHhpn();
					String hhpnName = hhpnInfo.getObjectName();
					System.out.println("hhpn = " + hhpn);
					if (hhpn != null && !hhpn.isEmpty()) {
						
						itemrevhhpn = getItemRevisionById(hhpn, session);
						if (itemrevhhpn == null ) {
							// 查询L5Agile/L6Agile/PNMS
							QueryL5L6PNMS queryL5L6PNMS = new QueryL5L6PNMS(session);
							itemrevhhpn = queryL5L6PNMS.itemrevhhpn(hhpn,hhpnName);
						}
						
						// 添加关系
						if (checkPartItemRev(itemrev, itemrevhhpn,session)) {
							itemrevhhpn.add(TC_IS_REPRESENTED_BY, itemrev);
						}
						
						if(i == 0) {
							addbomline = addbomline(bomline, itemrevhhpn);
							addbomline.setProperty("bl_quantity", ""+quantutyint);
						} else {
							addbomline.add(itemrevhhpn.getItem(), itemrevhhpn, null, true);
						}
						
						bomwindow.save();
					}
				}
			}
			
		} else {

			// 鸿海料号为空
			String itemid = alldbominfo.getItemid() + "@1";
			System.out.println("hhpn@1 = " + itemid);

			String englishDescription = itemrev.getProperty(EnglishDescription);
			String chineseDescription = itemrev.getProperty(ChineseDescription);
			String customer = itemrev.getProperty(Customer);
			String customerPN = itemrev.getProperty(CustomerPN);
			String manufacturerID = itemrev.getProperty(ManufacturerID);
			String manufacturerPN = itemrev.getProperty(ManufacturerPN);
			String itemName = itemrev.getProperty("object_name");
			
			itemrevhhpn = getItemRevisionById(itemid, session);
			if (itemrevhhpn == null) {
				String itemtype = "D9_CommonPart";
				TCComponentItem newItem = TCUtil.createItem(session, itemid, "", itemName, itemtype);
				itemrevhhpn = newItem.getLatestItemRevision();
				if("".equals(englishDescription))
					englishDescription = itemName;
				itemrevhhpn.setProperty(EnglishDescription, englishDescription);
				itemrevhhpn.setProperty(ChineseDescription, chineseDescription);
				itemrevhhpn.setProperty(Customer, customer);
				itemrevhhpn.setProperty(CustomerPN, customerPN);
				itemrevhhpn.setProperty(ManufacturerID, manufacturerID);
				itemrevhhpn.setProperty(ManufacturerPN, manufacturerPN);
			}

			// 添加关系
			if (checkPartItemRev(itemrev, itemrevhhpn,session)) {
				itemrevhhpn.add(TC_IS_REPRESENTED_BY, itemrev);
			}

			// 添加BOM结构 设置属性
			if (bomline == null) {
				addbomline = getTopBomline(bomwindow, itemrevhhpn);
			} else {
				addbomline = addbomline(bomline, itemrevhhpn);
				addbomline.setProperty("bl_quantity", ""+quantutyint);
			}
			
			bomwindow.save();
		}

		List<DBOMInfo> childs = alldbominfo.getChilds();
		System.out.println("childs.size() = " + childs.size());

		if (childs != null && childs.size() > 0) {
			for (int i = 0; i < childs.size(); i++) {
				savedbominfo(childs.get(i), session, bomwindow,addbomline );
			}
		}
		return itemrevhhpn;
	}
	
	

	public static TCComponentBOMLine addbomline(TCComponentBOMLine topLine, TCComponentItemRevision itemrev) throws TCException {

		String item_id1 = itemrev.getProperty("item_id");
		System.out.println("item_id1 = " + item_id1);
		boolean isequalsid = false;
		String bl_sequence_no = null;
		AIFComponentContext[] childrens = topLine.getChildren();
		if (childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String bl_item_id = children.getProperty("bl_item_item_id");
				
				if(item_id1.equals(bl_item_id)) {
					isequalsid = true;
					bl_sequence_no = children.getProperty("bl_sequence_no");
					break;
				}
			}
		}
		
		TCComponentBOMLine bomline = topLine.add(itemrev.getItem(), itemrev, null, false);
		
		if(isequalsid) {
			bomline.setProperty("bl_sequence_no",bl_sequence_no);
		}

		return bomline;
	
	}

	/**
	 * 创建BOMWindow
	 * 
	 * @param session
	 * @return
	 */
	public static TCComponentBOMWindow createBOMWindow(TCSession session) {
		TCComponentBOMWindow window = null;
		try {
			TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
					.getTypeComponent("RevisionRule");
			TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
			TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
					.getTypeComponent("BOMWindow");
			window = imancomponentbomwindowtype.create(imancomponentrevisionrule);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return window;
	}

	public static TCComponentBOMLine getTopBomline(TCComponentBOMWindow bomWindow, TCComponent com) {
		TCComponentBOMLine topBomline = null;
		try {
			if (bomWindow == null) {
				return topBomline;
			}
			TCComponentItemRevision rev = null;
			TCComponentItem item = null;
			if (com instanceof TCComponentItem) {
				item = (TCComponentItem) com;
				topBomline = bomWindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
			} else if (com instanceof TCComponentItemRevision) {
				rev = (TCComponentItemRevision) com;
				topBomline = bomWindow.setWindowTopLine(rev.getItem(), rev, null, null);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return topBomline;
	}

	public static TCComponentBOMLine addReplaceChild(TCComponentBOMWindow bomWindow, TCComponentBOMLine bomline,
			TCComponentItemRevision childItemRevision) throws TCException {
		if (bomWindow != null && bomline != null && childItemRevision != null) {
			bomWindow.refresh();
			TCComponentBOMLine newbomline = bomline.add(childItemRevision.getItem(), childItemRevision, null, false);
			
			bomWindow.save();
			return newbomline;
		}
		return null;
	}

	public static DBOMInfo getTCBOMinfo(DBOMInfo parent, TCComponentBOMLine bomLine) throws Exception {

		DBOMInfo dbominfo = new DBOMInfo();
		// dbominfo.setParent(parent);
		// dbominfo.setBomLine(bomLine);
		dbominfo.setParentitemRevUid(parent != null ? parent.getItemRevUid() : "");

		String bl_item_item_id = bomLine.getProperty("bl_item_item_id");
		dbominfo.setItemid(bl_item_item_id);
		String bl_indented_title = bomLine.getProperty("bl_indented_title");
		dbominfo.setIndented_title(bl_indented_title);
		String bl_quantity = bomLine.getProperty("bl_quantity");
		dbominfo.setQuantity(bl_quantity);
		String bomLineUid = bomLine.getUid();
		dbominfo.setBomLineUid(bomLineUid);
		TCComponentItemRevision itemRevision = bomLine.getItemRevision();
		String itemRevUid = itemRevision.getUid();
		dbominfo.setItemRevUid(itemRevUid);

		List<TCComponentItemRevision> hhpnrevlist = getItemRevByRepresentation(itemRevision);

		ArrayList<HHPNInfo> hhpninfoList = new ArrayList<HHPNInfo>();
		HHPNInfo hhpninfo = null;
		String hhpn = null;

		for (TCComponentItemRevision rev : hhpnrevlist) {
			hhpn = rev.getProperty("item_id");
			hhpninfo = getHHPNInfo(hhpn, itemRevision);
			
			hhpninfoList = hhpninfoListadd(hhpninfoList,hhpninfo);
		}

		String itemprohhpn = itemRevision.getProperty("d9_HHPN");
		if(itemprohhpn.equalsIgnoreCase("NA") || itemprohhpn.equalsIgnoreCase("N/A") ) 
			itemprohhpn = "";
		if (itemprohhpn != null && !"".equals(itemprohhpn)) {
			if(itemprohhpn.contains("|")) {
				String [] hhpns = itemprohhpn.split("\\|");
				System.out.println(hhpns.length);
				for (String hhpnstring : hhpns) {
					if(!equalsById(hhpninfoList, hhpnstring)) {
						hhpninfo = getHHPNInfo(hhpnstring, itemRevision);
						hhpninfoList = hhpninfoListadd(hhpninfoList,hhpninfo);
					}
				}
			}else {
				hhpninfo = getHHPNInfo(itemprohhpn, itemRevision);
				hhpninfoList = hhpninfoListadd(hhpninfoList,hhpninfo);
			}
		}

		dbominfo.setHhpnInfo(hhpninfoList);
		return dbominfo;
	}
	
	/**
	 * 新增鸿海料号时去重
	 * @param hhpninfoList
	 * @param hhpninfo
	 */
	public static ArrayList<HHPNInfo> hhpninfoListadd(ArrayList<HHPNInfo> hhpninfoList,HHPNInfo hhpninfo) {
		if(hhpninfoList!=null && hhpninfoList.size()>0) {
			boolean concat = true;
			for (HHPNInfo hhpnInfo1 : hhpninfoList) {
				if(hhpnInfo1.getHhpn().equals(hhpninfo.getHhpn())) {
					concat = false;
					break;
				}
			}
			if(concat) {
				hhpninfoList.add(hhpninfo);
			}
		} else {
			hhpninfoList.add(hhpninfo);
		}
		return hhpninfoList;
	}
	
	public static HHPNInfo getHHPNInfo(String hhpn,TCComponentItemRevision itemRevision) throws Exception {
		TCSession session = itemRevision.getSession();
		HHPNInfo hhpninfo = new HHPNInfo();
		hhpninfo.setHhpn(hhpn);
		hhpninfo.setIsWrite("Y");

		TCComponentItemRevision itemrevhhpn = getItemRevisionById(hhpn, session);
		if(itemrevhhpn!=null) {
			boolean checkOwninguserisWrite = TCUtil.checkOwninguserisWrite(session, itemrevhhpn);
			if(!checkOwninguserisWrite) {
				hhpninfo.setIsWrite("N");
			}
		}
		
		
		String englishDescription = itemRevision.getProperty(EnglishDescription);
		hhpninfo.setEnglishDescription(englishDescription);
		String chineseDescription = itemRevision.getProperty(ChineseDescription);
		hhpninfo.setChineseDescription(chineseDescription);
		String customer = itemRevision.getProperty(Customer);
		hhpninfo.setCustomer(customer);
		String customerPN = itemRevision.getProperty(CustomerPN);
		hhpninfo.setCustomerPN(customerPN);
		String manufacturerID = itemRevision.getProperty(ManufacturerID);
		hhpninfo.setManufacturerID(manufacturerID);
		String manufacturerPN = itemRevision.getProperty(ManufacturerPN);
		hhpninfo.setManufacturerPN(manufacturerPN);
		String object_name = itemRevision.getProperty(Object_name);
		hhpninfo.setObjectName(object_name);
		
		return hhpninfo;
	}

	public static DBOMInfo getTCBOMLines(DBOMInfo pdbominfo, TCComponentBOMLine designTopBOMLine) throws Exception {

		ArrayList<DBOMInfo> allchildrenbomline = new ArrayList<>();
		AIFComponentContext[] componmentContext = designTopBOMLine.getChildren();
		if (componmentContext != null) {
			for (int i = 0; i < componmentContext.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) componmentContext[i].getComponent();
				DBOMInfo cdbominfo = getTCBOMinfo(pdbominfo, bomLine);
				allchildrenbomline.add(cdbominfo);

				getTCBOMLines(cdbominfo, bomLine);
			}
			pdbominfo.setChilds(allchildrenbomline);
		}
		return pdbominfo;
	}

	
	public static List<TCComponentBOMLine> getTCComponmentBOMLines(TCComponentBOMLine rootLine,
			List<TCComponentBOMLine> lines, boolean unpacked) throws TCException {
		if (lines == null) {
			lines = new ArrayList<TCComponentBOMLine>();
		}
		AIFComponentContext[] componmentContext = rootLine.getChildren();
		if (componmentContext != null) {
			for (int i = 0; i < componmentContext.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) componmentContext[i].getComponent();
				if (unpacked) {
					if (bomLine.isPacked()) {
						TCComponentBOMLine[] packedLines = bomLine.getPackedLines();
						bomLine.unpack();
						if (packedLines != null && packedLines.length > 0) {
							lines.add(bomLine);
							lines.addAll(Arrays.asList(packedLines));
						} else {
							lines.add(bomLine);
						}
					} else {
						lines.add(bomLine);
					}
				} else {
					lines.add(bomLine);
				}
				getTCComponmentBOMLines(bomLine, lines, unpacked);
			}
		}
		return lines;
	}

	/**
	 * 通过itemid查找item版本
	 * @param id
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static TCComponentItemRevision getItemRevisionById(String id, TCSession session) throws Exception {
		
		TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...", new String[] { "items_tag.item_id" },
				new String[] { id });
		if (executeQuery != null && executeQuery.length > 0) {
			for (int i = 0; i < executeQuery.length; i++) {
				String type = executeQuery[i].getType();
				if("D9_L5_PartRevision".equals(type)) {
					continue;
				}
				return (TCComponentItemRevision) executeQuery[i];
			}
		} else {
			return null;
		}
		return null;
	}

	/**
	 * 通过UID查询item版本
	 * @param UID
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static TCComponentItemRevision getItemRevisionUID(String UID, TCSession session) throws Exception {
		TCComponentItemRevision itemRevision = (TCComponentItemRevision) session.getComponentManager()
				.getTCComponent(UID);

		return itemRevision;
	}

	/**
	 * 是否有权限并且
	 * 判断当前父对象版本表示伪文件夹下是否含有此物料对象版本
	 * 
	 * @param parentItemRevision
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	private static Boolean checkPartItemRev(TCComponentItemRevision parentItemRevision,
			TCComponentItemRevision hhpnItemRevision,TCSession session) throws TCException {
		Boolean flag = true;
		parentItemRevision.refresh();
		
		boolean checkOwninguserisWrite = checkOwninguserisWrite(session, hhpnItemRevision);
		if(!checkOwninguserisWrite) 
			return false;
		
		TCComponent[] relatedComponents = parentItemRevision.getRelatedComponents(REPRESENTATION_FOR);
		if (CommonTools.isNotEmpty(relatedComponents)) {
			for (TCComponent tcComponent : relatedComponents) {
				if (!(tcComponent instanceof TCComponentItemRevision)) {
					continue;
				}
				TCComponentItemRevision partItemRevision = (TCComponentItemRevision) tcComponent;
				if (hhpnItemRevision.getUid().equals(partItemRevision.getUid())) {
					flag = false;
					break;
				}
			}
		}
		
		return flag;
	}

	/**
	 * 判断设计对象representation_for关系下是否存在物料对象
	 * 
	 * @param itemRevision
	 * @return 物料对象
	 * @throws TCException
	 */
	public static List<TCComponentItemRevision> getItemRevByRepresentation(TCComponentItemRevision itemRevision)
			throws TCException {
		itemRevision.refresh();
		TCComponent[] relatedComponents = itemRevision.getRelatedComponents(REPRESENTATION_FOR);
		if (relatedComponents != null) {
			return Stream.of(relatedComponents).filter(e -> e instanceof TCComponentItemRevision)
					.map(e -> (TCComponentItemRevision) e).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * 根据鸿海料号 去重
	 * 
	 * @param hhpninfoList
	 * @param hhpnid
	 * @return
	 */
	public static boolean equalsById(ArrayList<HHPNInfo> hhpninfoList, String hhpnid) {
		boolean iscontains = false;
		for (HHPNInfo hhpninfo : hhpninfoList) {
			if (hhpninfo.getHhpn().equals(hhpnid)) {
				iscontains = true;
				break;
			}
		}
		return iscontains;
	}

	/**
	 * 判断是否有写权限
	 * 
	 * @param session
	 * @param component
	 * @return
	 */
	public static boolean checkOwninguserisWrite(TCSession session, TCComponent component) {
		boolean isWrite = false;
		try {
			TCAccessControlService service = session.getTCAccessControlService();
			isWrite = service.checkPrivilege(component, "WRITE");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isWrite;
	}
	
	/**
	 * 匹配id判断是否满足正则表达式
	 * @param 
	 * @return
	 */
	private static boolean getRightStr(String itemid) {
		String match = "^.{7,17}$";
		if (itemid.matches(match)) {
			return true;
		}
		return false;
	}

}
