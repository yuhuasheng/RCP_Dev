package com.foxconn.electronics.convertebom.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.foxconn.electronics.convertebom.ConvertEBOMDialog;
import com.foxconn.electronics.convertebom.service.ConvertEBOMService;
import com.foxconn.electronics.domain.BOMPojo;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;

public class ConvertMonitorEBOM implements ConvertEBOMService{
	
	List<TCComponentItemRevision> selfPartMI = null;
	List<BOMPojo> selfPartMIChild = new ArrayList<BOMPojo>();
	List<BOMPojo> selfPartSMTChild = new ArrayList<BOMPojo>();
	List<TCComponentItemRevision> selfPartAI = null;
	List<TCComponentItemRevision> selfPartAI_A = null;
	List<TCComponentItemRevision> selfPartAI_R = null;
	List<TCComponentItemRevision> selfPartSMT1 = null;
	List<TCComponentItemRevision> selfPartSMT_T1 = null;
	List<TCComponentItemRevision> selfPartSMT_B1 = null;
	List<BOMPojo> PCBPart= new ArrayList<BOMPojo>();
	List<BOMPojo> AIChild = new ArrayList<BOMPojo>();
	List<BOMPojo> AI_AChild = new ArrayList<BOMPojo>();
	List<BOMPojo> AI_RChild = new ArrayList<BOMPojo>();
	
	@Override
	public BOMPojo ConvertEBOM(TCSession session, TCComponentFolder selfPartFolder, BOMPojo dBOMpojo, BOMPojo eBOMpojo)
			throws Exception {
		
		ConvertEBOMDialog.writeLogText("【PCA】自编料号：" + eBOMpojo.getBomLineItemId() + "\n");
		ConvertEBOMDialog.writeLogText("开始获取【PCA】子阶料号【MI】...\n");
		selfPartMI = getPCASelfPartChildren(selfPartFolder,Constants.MI);
		if(selfPartMI.size() > 0) {
			ConvertEBOMDialog.writeLogText("【PCA】子阶料号【MI】为" + selfPartMI.get(0).toString() + "\n");
		}
		ConvertEBOMDialog.writeLogText("开始获取【PCA】子阶料号【SMT】...\n");
		List<TCComponentItemRevision> selfPartSMT = getPCASelfPartChildren(selfPartFolder,Constants.SMT);
		ConvertEBOMDialog.writeLogText("【PCA】子阶料号【SMT】为" + selfPartSMT.get(0).toString() + "\n");
		
		ConvertEBOMDialog.writeLogText("开始获取【PCA】子阶料号【MVA】...\n");
		List<TCComponentItemRevision> selfPartMVA = getPCASelfPartChildren(selfPartFolder,Constants.MVA);
		if(selfPartMVA.size() == 0) {
			throw new Exception("未找到包含【MVA】自编料号，程序结束！");
		}
		ConvertEBOMDialog.writeLogText("【PCA】子阶料号【MVA】为" + selfPartMVA.get(0).toString() + "\n");
		
		selfPartSMT1 = getPCASelfPartChildren(selfPartFolder,Constants.SMT1);
		selfPartSMT_T1 = getPCASelfPartChildren(selfPartFolder,Constants.SMT_T1);
		selfPartSMT_B1 = getPCASelfPartChildren(selfPartFolder,Constants.SMT_B1);
		selfPartAI = getPCASelfPartChildren(selfPartFolder,Constants.AI);
		selfPartAI_A = getPCASelfPartChildren(selfPartFolder,Constants.AI_A);
		selfPartAI_R = getPCASelfPartChildren(selfPartFolder,Constants.AI_R);
		
		ConvertEBOMDialog.writeLogText("开始获取【PCA】子阶料号【虚拟件】...\n");
		List<TCComponentItemRevision> selfPartASSY = getPCASelfPartChildren(selfPartFolder,Constants.ASSY);
		if(selfPartASSY.size() == 0) {
			ConvertEBOMDialog.writeLogText("未找到包含【ASSY】虚拟件..\n");
		}else {
			for (int i = 0; i < selfPartASSY.size(); i++) {
				ConvertEBOMDialog.writeLogText("【PCA】子阶料号【虚拟件】为" + selfPartASSY.get(i).toString() + "\n");
			}
		}
		
		//-----------添加PCA子阶-------------------------
		ConvertEBOMDialog.writeLogText("开始添加【PCA】子阶...\n");
		BOMPojo bomPojoMI = null;
		if(selfPartMI.size() > 0){
			bomPojoMI = itemConvertBOMpojo(selfPartMI.get(0));
		}
		BOMPojo bomPojoSMT = itemConvertBOMpojo(selfPartSMT.get(0));
		BOMPojo bomPojoMVA = itemConvertBOMpojo(selfPartMVA.get(0));
		List<BOMPojo> PCAPartChild = eBOMpojo.getChild();
		if(bomPojoMI != null) {
			PCAPartChild.add(bomPojoMI);
		}
		PCAPartChild.add(bomPojoMVA);
		if(selfPartASSY.size() > 0) {
			for (int i = 0; i < selfPartASSY.size(); i++) {
				BOMPojo bomPojoASSY = itemConvertBOMpojo(selfPartASSY.get(i));
				PCAPartChild.add(bomPojoASSY);
			}
		}
		//-----------添加PCA子阶-------------------------
		
		getSelfPartMIChild(dBOMpojo);
		
		//-----------添加 MI子阶-------------------------
		if(selfPartMI.size() > 0) {
			ConvertEBOMDialog.writeLogText("开始添加【 MI】子阶...\n");
			List<BOMPojo> bomPojoMIChild = bomPojoMI.getChild();
			List<BOMPojo> selfPartMIChildFinalData = distinctMergeLocationById(selfPartMIChild);
			for (int i = 0; i < selfPartMIChildFinalData.size(); i++) {
				BOMPojo bomPojo = selfPartMIChildFinalData.get(i);
				bomPojoMIChild.add(bomPojo);
			}
		}
		//-----------添加 MI子阶--------------------------
		
		if(bomPojoSMT.getBomLineDescription().contains(Constants.SMT_T)) {
			PCAPartChild.add(bomPojoSMT);
			List<BOMPojo> bomPojoSMTTChild = bomPojoSMT.getChild();
			//-----------获取，添加  SMT/T 子阶  TOP-------------------------
			List<BOMPojo> selfPartSMTTop = getSelfPartSMTTop();
			ConvertEBOMDialog.writeLogText("开始添加【SMT/T】子阶【TOP】...\n");
			List<BOMPojo> selfPartSMTTopFinalData = distinctMergeLocationById(selfPartSMTTop);
			for (int i = 0; i < selfPartSMTTopFinalData.size(); i++) {
				BOMPojo bomPojo = selfPartSMTTopFinalData.get(i);
				bomPojoSMTTChild.add(bomPojo);
			}
			//-----------获取，添加  SMT/T 子阶  TOP-------------------------
			
			//-----------获取，添加  SMT/T 子阶  SMT/B -------------------------
			List<TCComponentItemRevision> selfPartSMTB = getPCASelfPartChildren(selfPartFolder,Constants.SMT_B);
			if(selfPartSMTB.size() == 0) {
				ConvertEBOMDialog.writeLogText("未找到包含【SMT/B】自编料号，程序结束！");
				throw new Exception("未找到包含【SMT/B】自编料号，程序结束！");
			}
			BOMPojo bomPojoSMTB = itemConvertBOMpojo(selfPartSMTB.get(0));
			ConvertEBOMDialog.writeLogText("开始添加【SMT/T】子阶【SMT/B】...\n");
			bomPojoSMTTChild.add(bomPojoSMTB);
			
			ConvertEBOMDialog.writeLogText("开始获取【PCB】...\n");
			getPartPCB(selfPartFolder);
			BOMPojo mainPart = getMainPart(session);
			if(mainPart != null) {
				bomPojoSMTTChild.add(mainPart);
			}
			//-----------获取，添加  SMT/T 子阶  SMT/B -------------------------
			
			//-----------获取，添加  SMT/B 子阶  BOTTOM -------------------------
			List<BOMPojo> selfPartSMTBChild = getSelfPartSMTBChild();
			ConvertEBOMDialog.writeLogText("开始添加【SMT/B】子阶【BOTTOM】...\n");
			List<BOMPojo> selfPartSMTBChildFinalData = distinctMergeLocationById(selfPartSMTBChild);
			List<BOMPojo> bomPojoSMTBChild = bomPojoSMTB.getChild();
			for (int i = 0; i < selfPartSMTBChildFinalData.size(); i++) {
				BOMPojo bomPojo = selfPartSMTBChildFinalData.get(i);
				bomPojoSMTBChild.add(bomPojo);
			}
			//-----------获取，添加  SMT/B 子阶  BOTTOM -------------------------
			
			//-----------获取，添加  SMT/T 子阶  AI -------------------------
			getSelfPartAI(selfPartFolder,bomPojoSMTTChild);
			//-----------获取，添加  SMT/T 子阶  AI -------------------------
		}else if(bomPojoSMT.getBomLineDescription().contains(Constants.SMT_B)) {
			
			//-----------获取，添加  SMT/T-------------------------
			List<TCComponentItemRevision> selfPartSMTT = getPCASelfPartChildren(selfPartFolder,Constants.SMT_T);
			if(selfPartSMTT.size() == 0) {
				ConvertEBOMDialog.writeLogText("未找到包含【SMT/T】自编料号，程序结束！");
				throw new Exception("未找到包含【SMT/T】自编料号，程序结束！");
			}
			BOMPojo bomPojoSMTT = itemConvertBOMpojo(selfPartSMTT.get(0));
			ConvertEBOMDialog.writeLogText("开始添加【PCA】子阶【SMT/T】...\n");
			PCAPartChild.add(bomPojoSMTT);
			//-----------获取，添加  SMT/T-------------------------
			
			//-----------获取，添加  SMT/T 子阶  TOP-------------------------
			List<BOMPojo> selfPartSMTTop = getSelfPartSMTTop();
			List<BOMPojo> selfPartSMTTopFinalData = distinctMergeLocationById(selfPartSMTTop);
			List<BOMPojo> bomPojoSMTTChild = bomPojoSMTT.getChild();
			ConvertEBOMDialog.writeLogText("开始添加【SMT/T】子阶【TOP】...\n");
			for (int i = 0; i < selfPartSMTTopFinalData.size(); i++) {
				BOMPojo bomPojo = selfPartSMTTopFinalData.get(i);
				bomPojoSMTTChild.add(bomPojo);
			}
			//-----------获取，添加 SMT/T 子阶  TOP-------------------------
			
			//-----------添加 SMT/T 子阶   SMT/B---------------------------
			ConvertEBOMDialog.writeLogText("开始添加【SMT/T】子阶【SMT/B】...\n");
			bomPojoSMTTChild.add(bomPojoSMT);
			List<BOMPojo> selfPartSMTBChild = getSelfPartSMTBChild();
			List<BOMPojo> selfPartSMTBChildFinalData = distinctMergeLocationById(selfPartSMTBChild);
			List<BOMPojo> bomPojoSMTBChild = bomPojoSMT.getChild();
			ConvertEBOMDialog.writeLogText("开始添加【SMT/B】子阶【BOTTOM】...\n");
			for (int i = 0; i < selfPartSMTBChildFinalData.size(); i++) {
				BOMPojo bomPojo = selfPartSMTBChildFinalData.get(i);
				bomPojoSMTBChild.add(bomPojo);
			}
			
			BOMPojo mainPart = getMainPart(session);
			if(mainPart != null) {
				bomPojoSMTTChild.add(mainPart);
			}
			//-----------添加 SMT/T 子阶   SMT/B-------------------------
			
			//-----------获取，添加  SMT/T 子阶  AI -------------------------
			getSelfPartAI(selfPartFolder, bomPojoSMTTChild);
			//-----------获取，添加  SMT/T 子阶  AI -------------------------
		}else {
			PCAPartChild.add(bomPojoSMT);
			List<BOMPojo> selfPartSMTChildFinalData = distinctMergeLocationById(selfPartSMTChild);
			List<BOMPojo> bomPojoSMTChild = bomPojoSMT.getChild();
			for (int i = 0; i < selfPartSMTChildFinalData.size(); i++) {
				BOMPojo bomPojo = selfPartSMTChildFinalData.get(i);
				bomPojoSMTChild.add(bomPojo);
			}
			ConvertEBOMDialog.writeLogText("开始获取【PCB】...\n");
			getPartPCB(selfPartFolder);
			BOMPojo mainPart = getMainPart(session);
			if(mainPart != null) {
				bomPojoSMTChild.add(mainPart);
			}
			//-----------获取，添加  SMT/T 子阶  AI -------------------------
			getSelfPartAI(selfPartFolder, bomPojoSMTChild);
			//-----------获取，添加  SMT/T 子阶  AI -------------------------
		}
		return eBOMpojo;
	}
	
//	private List<TCComponentItemRevision> getPartPCB1(TCComponentFolder selfPartFolder) throws Exception{
//		List<TCComponentItemRevision> itemRevisions = new ArrayList<TCComponentItemRevision>();
//		AIFComponentContext[] selfParts = selfPartFolder.getRelated(Constants.CONTENTS);
//		for (int i = 0; i < selfParts.length; i++) {
//			InterfaceAIFComponent component = selfParts[i].getComponent();
//			if(component instanceof TCComponentFolder) {
//				continue;
//			}
//			TCComponentItem item = (TCComponentItem)component;
//			TCComponentItemRevision itemRev = item.getLatestItemRevision();
//			String itemRevType = itemRev.getType();
//			if("D9_PCB_PartRevision".equals(itemRevType)) {
//				itemRevisions.add(itemRev);
//			}
//		}
//		return itemRevisions;
//	}
	
	private BOMPojo getMainPart(TCSession session) throws Exception{
		BOMPojo mainPartBOMPojo = null;
		int mainPartCount = 0;
		for (int i = 0; i < PCBPart.size(); i++) {
			BOMPojo bomPojo = PCBPart.get(i);
			String bomLineItemId = bomPojo.getBomLineItemId();
			TCComponentItem item = queryItem(session,bomLineItemId);
			TCComponentItemRevision itmeRev = item.getLatestItemRevision();
			String is2ndSource = itmeRev.getProperty("d9_Is2ndSource");
			if("否".equals(is2ndSource)) {
				mainPartBOMPojo = bomPojo;
				mainPartCount++;
				break;
			}
		}
		
		if (mainPartCount > 1) {
			throw new Exception("PCB存在两个主料！程序结束...");
		}
		
		if(mainPartBOMPojo != null) {
			List<BOMPojo> substitute = mainPartBOMPojo.getSubstitute();
			for (int i = 0; i < PCBPart.size(); i++) {
				BOMPojo bomPojo = PCBPart.get(i);
				String bomLineItemId = bomPojo.getBomLineItemId();
				TCComponentItem item = queryItem(session,bomLineItemId);
				TCComponentItemRevision itmeRev = item.getLatestItemRevision();
				String is2ndSource = itmeRev.getProperty("d9_Is2ndSource");
				if("是".equals(is2ndSource)) {
					substitute.add(bomPojo);
				}
			}
		}
		return mainPartBOMPojo;
	}
	
	private TCComponentItem queryItem(TCSession session, String itemId) throws Exception{
		TCComponent[] executeQuery = TCUtil.executeQuery(session, Constants.FIND_ITEM, new String[] {Constants.ITEM_ID}, new String[] {itemId});
		if(executeQuery.length == 0) {
			throw new Exception("未找到Item：" + itemId + "\n");
		}
		return (TCComponentItem) executeQuery[0];
	}
	
	private List<TCComponentItemRevision> getPCASelfPartChildren(TCComponentFolder selfPartFolder, String partDesc) throws Exception{
		List<TCComponentItemRevision> itemRevs = new ArrayList<TCComponentItemRevision>();
		AIFComponentContext[] selfParts = selfPartFolder.getRelated(Constants.CONTENTS);
		for (int i = 0; i < selfParts.length; i++) {
			InterfaceAIFComponent component = selfParts[i].getComponent();
			if(component instanceof TCComponentFolder) {
				continue;
			}
			TCComponentItem item = (TCComponentItem) component;
			TCComponentItemRevision itemRev = item.getLatestItemRevision();
			String desc = itemRev.getProperty(Constants.ENGLISH_DESC).toUpperCase();
			if(desc.contains(partDesc)) {
				itemRevs.add(itemRev);
			}
		}
		return itemRevs;
	}
	
	private void getPartPCB(TCComponentFolder selfPartFolder) throws Exception{
		AIFComponentContext[] selfParts = selfPartFolder.getRelated(Constants.CONTENTS);
		for (int i = 0; i < selfParts.length; i++) {
			InterfaceAIFComponent component = selfParts[i].getComponent();
			if(component instanceof TCComponentFolder) {
				continue;
			}
			TCComponentItem item = (TCComponentItem)component;
			TCComponentItemRevision itemRev = item.getLatestItemRevision();
			String itemRevType = itemRev.getType();
			if("D9_PCB_PartRevision".equals(itemRevType)) {
				BOMPojo BOMPojoPCB = itemConvertBOMpojo(itemRev);
				PCBPart.add(BOMPojoPCB);
			}
		}
	}

	private BOMPojo itemConvertBOMpojo(TCComponentItemRevision itemRev) throws Exception{
		String itemId = itemRev.getProperty(Constants.ITEM_ID);
		String desc = itemRev.getProperty(Constants.ENGLISH_DESC);
		BOMPojo bomPojo = new BOMPojo();
		bomPojo.setBomLineItemId(itemId);
		bomPojo.setBomLineDescription(desc);
		return bomPojo;
	}
	
	/**
	 * 获取MI子阶，获取SMT子阶
	 * @return
	 * @throws Exception 
	 */
	private void getSelfPartMIChild(BOMPojo bomPojo) throws Exception{
		List<BOMPojo> child = bomPojo.getChild();
		for (int i = 0; i < child.size(); i++) {
			BOMPojo bomPojo2 = child.get(i);
			String packageType = bomPojo2.getBomLinePackageType();
			String description = bomPojo2.getBomLineDescription();
			String side = bomPojo2.getBomLineSide();
			String bom = bomPojo2.getBomLineBOM();
			TCComponentBOMLine bomLine = bomPojo2.getBomLine();
//			TCComponentItemRevision itemRev = bomLine.getItemRevision();
			String insertionType = bomLine.getProperty(Constants.INSERTION_TYPE);
			
			//zhangc_20220915
			if ("DIP".equals(packageType) && ("MI".equals(insertionType) || "".equals(insertionType))) {
				if (selfPartMI.size() == 0) {
					throw new Exception("【MI】虚拟件不存在，程序终止！");
				}
			}
			
			if ("DIP".equals(packageType) && "AI".equals(insertionType)) {
				if (selfPartAI.size() == 0) {
					throw new Exception("【AI】虚拟件不存在，程序终止！");
				}
			}
			
			if ("DIP".equals(packageType) && "AI/A".equals(insertionType)) {
				if (selfPartAI_A.size() == 0) {
					throw new Exception("【AI/A】虚拟件不存在，程序终止！");
				}
			}
			
			if ("DIP".equals(packageType) && "AI/R".equals(insertionType)) {
				if (selfPartAI_R.size() == 0) {
					throw new Exception("【AI/R】虚拟件不存在，程序终止！");
				}
			}
			
			if ("SMD".equals(packageType) && "TOP".equals(side)) {
				if (selfPartSMT1.size() == 0 && selfPartSMT_T1.size() == 0) {
					throw new Exception("【SMT】【SMT/T】虚拟件不存在，程序终止！");
				}
			}
			
			if ("SMD".equals(packageType) && "BOTTOM".equals(side)) {
				if (selfPartSMT_B1.size() == 0) {
					throw new Exception("【SMT/B】虚拟件不存在，程序终止！");
				}
			}
			
			if(description.contains(Constants.PCB)) {
				PCBPart.add(bomPojo2);
				continue;
			}
			if("NI".equals(bom)) {
				continue;
			}
			if("DIP".equals(packageType) || "HEATSINK".contains(description)) {
				if ("AI".equals(insertionType)) {
					AIChild.add(bomPojo2);
				}else if ("AI/A".equals(insertionType)) {
					AI_AChild.add(bomPojo2);
				}
				else if ("AI/R".equals(insertionType)) {
					AI_RChild.add(bomPojo2);
				}
				else {
					selfPartMIChild.add(bomPojo2);
				}
			}else {
				selfPartSMTChild.add(bomPojo2);
			}
			getSelfPartMIChild(bomPojo2);
		}
	}
	
	/**
	 * 获取SMT/T子阶TOP
	 * @param MIChildrenList
	 * @return
	 */
	private List<BOMPojo> getSelfPartSMTTop(){
		List<BOMPojo> selfPartSMTTop = selfPartSMTChild.stream().filter(b -> b.getBomLineSide().equals(Constants.TOP)).collect(Collectors.toList());
		return selfPartSMTTop;
	}
	
	/**
	 * 根据id去重，合并Location
	 * @throws Exception 
	 */
	private List<BOMPojo> distinctMergeLocationById(List<BOMPojo> selfPartSMTChild) throws Exception{
		
		Map<String, List<String>> itemIdMap = new HashMap<String, List<String>>();
		Set<String> repeatId = getRepeatId(selfPartSMTChild);
//		System.out.println("相同的id数量-->" + repeatId.size());
//		System.out.println("相同的id信息-->" + repeatId);
		
		//获取id相同的Location
		for (int i = 0; i < selfPartSMTChild.size(); i++) {
			BOMPojo bomPojo = selfPartSMTChild.get(i);
			String itemId = bomPojo.getBomLineItemId();
			String location = bomPojo.getBomLineLocation();
			if(repeatId.contains(itemId)) {
				boolean containsKey = itemIdMap.containsKey(itemId);
				if(containsKey) {
					List<String> locations = itemIdMap.get(itemId);
					locations.add(location);
					itemIdMap.put(itemId, locations);
				}else {
					List<String> locations = new ArrayList<String>();
					locations.add(location);
					itemIdMap.put(itemId, locations);
				}
			}
		}
		
		//根据id去重
		selfPartSMTChild = selfPartSMTChild.stream().collect(
				Collectors.collectingAndThen(
				Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BOMPojo::getBomLineItemId))), ArrayList::new));
		
		//赋值Quantity,Location
		for (int i = 0; i < selfPartSMTChild.size(); i++) {
			BOMPojo bomPojo = selfPartSMTChild.get(i);
			String itemId = bomPojo.getBomLineItemId();
			if(itemIdMap.containsKey(itemId)) {
				List<String> locations = itemIdMap.get(itemId);
				bomPojo.setBomLineQuantity(String.valueOf(locations.size()));
				bomPojo.setBomLineLocation(String.join(",", locations));
			}
		}
		
		return selfPartSMTChild;
	}
	
	/**
	 * 获取重复id
	 * @return
	 * @throws Exception
	 */
	private Set<String> getRepeatId(List<BOMPojo> selfPartSMTChild) throws Exception{
		Set<String> set = new HashSet<>();
		Set<String> exist = new HashSet<>();
		for (BOMPojo bomPojo:selfPartSMTChild) {
			String itemId = bomPojo.getBomLineItemId();
			if(set.contains(itemId)) {
				exist.add(itemId);
			}else {
				set.add(itemId);
			}
		}
		return exist;
	}
	
	/**
	 * 获取(SMT/B)
	 * @return
	 */
	private BOMPojo getSelfPartSMTB(){
		return selfPartSMTChild.stream().filter(b -> b.getBomLineDescription().equals(Constants.SMT_B)).findAny().orElse(null);
	}
	
	/**
	 * 获取(SMT/B)子阶
	 * @return
	 */
	private List<BOMPojo> getSelfPartSMTBChild(){
		List<BOMPojo> selfPartSMTBChild = selfPartSMTChild.stream().filter(b -> b.getBomLineSide().equals(Constants.BOTTOM)).collect(Collectors.toList());
		return selfPartSMTBChild;
	}
	
	/**
	 * 添加  SMT 子阶  AI
	 * @return
	 * @throws Exception 
	 */
	private void getSelfPartAI(TCComponentFolder selfPartFolder, List<BOMPojo> bomPojoSMTChild) throws Exception {
		//-----------获取，添加  SMT/T 子阶  AI -------------------------
		List<TCComponentItemRevision> selfPartAI = getPCASelfPartChildren(selfPartFolder,Constants.AI);
		if(selfPartAI.size() == 0) {
			ConvertEBOMDialog.writeLogText("未找到包含【AI】自编料号\n");
		}else {
			BOMPojo bomPojoAI = itemConvertBOMpojo(selfPartAI.get(0));
			ConvertEBOMDialog.writeLogText("开始添加【SMT/T】子阶【AI】...\n");
			bomPojoSMTChild.add(bomPojoAI);
			
			//-----------获取，添加  AI 子阶  AI/R、AI/A -------------------------
			List<BOMPojo> bomPojoAIChild = bomPojoAI.getChild();
			for (int i = 0; i < AIChild.size(); i++) {
				BOMPojo bomPojo = AIChild.get(i);
				bomPojoAIChild.add(bomPojo);
			}
			
			List<TCComponentItemRevision> selfPartAIA = getPCASelfPartChildren(selfPartFolder,Constants.AI_A);
			if(selfPartAIA.size() == 0) {
				ConvertEBOMDialog.writeLogText("未找到包含【AI/A】自编料号...\n");
			}else {
				BOMPojo bomPojoAIA = itemConvertBOMpojo(selfPartAIA.get(0));
				ConvertEBOMDialog.writeLogText("开始添加【AI】子阶【AI/A】...\n");
				bomPojoAIChild.add(bomPojoAIA);
				List<BOMPojo> child = bomPojoAIA.getChild();
				for (int i = 0; i < AI_AChild.size(); i++) {
					BOMPojo bomPojo = AI_AChild.get(i);
					child.add(bomPojo);
				}
			}
			
			List<TCComponentItemRevision> selfPartAIR = getPCASelfPartChildren(selfPartFolder,Constants.AI_R);
			if(selfPartAIR.size() == 0) {
				ConvertEBOMDialog.writeLogText("未找到包含【AI/R】自编料号...\n");
			}else {
				BOMPojo bomPojoAIR = itemConvertBOMpojo(selfPartAIR.get(0));
				ConvertEBOMDialog.writeLogText("开始添加【AI】子阶【AI/R】...\n");
				bomPojoAIChild.add(bomPojoAIR);
				List<BOMPojo> child = bomPojoAIR.getChild();
				for (int i = 0; i < AI_RChild.size(); i++) {
					BOMPojo bomPojo = AI_RChild.get(i);
					child.add(bomPojo);
				}
			}
			//-----------获取，添加  AI 子阶  AI/R、AI/A -------------------------
			
		}
		//-----------获取，添加  SMT/T 子阶  AI -------------------------
	}
}
