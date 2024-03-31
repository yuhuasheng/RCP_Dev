package com.foxconn.sdebom.dtl5ebom.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.sdebom.constant.D9Constant;
import com.foxconn.sdebom.dtl5ebom.dialog.DtL5EbomDialog;
import com.foxconn.sdebom.dtl5ebom.pojo.BomParentChild;
import com.foxconn.sdebom.dtl5ebom.pojo.DtL5BomChangeData;
import com.foxconn.sdebom.dtl5ebom.pojo.DtL5BomPojo;
import com.foxconn.sdebom.dtl5ebom.pojo.DtL5ItemRevPojo;
import com.foxconn.sdebom.pojo.BomLinePojo;
import com.foxconn.sdebom.service.BomService;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;

public class DtL5Bom implements BomService{
	
	private TCSession session;
	private DtL5BomChangeData<DtL5BomPojo> dtL5BomChangeData;
	
	public DtL5Bom(TCSession session) {
		this.session = session;
	}
	
	@Override
	public List<DtL5ItemRevPojo> jsonConvertItemRevPojo(String json) throws Exception {
		return JSONObject.parseArray(json, DtL5ItemRevPojo.class);
	}
	
	@Override
	public void createItemRev(List<DtL5ItemRevPojo> dtL5ItemRevPojos) throws Exception {
		for (int i = 0; i < dtL5ItemRevPojos.size(); i++) {
			DtL5ItemRevPojo dtL5ItemRevPojo = dtL5ItemRevPojos.get(i);
			startCreateItemRev(dtL5ItemRevPojo);
		}
	}
	
	@Override
	public List<DtL5BomPojo> jsonConvertBomPojo(String json) throws Exception {
		return JSONObject.parseArray(json, DtL5BomPojo.class);
	}
	
	@Override
	public BomLinePojo itemRevConvertBomPojo(TCComponentItemRevision itemRev) throws Exception {
		DtL5BomPojo dtL5BomPojo = null;
		TCComponentBOMWindow newBOMWindow = null;
		try {
			newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomLine = TCUtil.getTopBomline(newBOMWindow, itemRev);
			dtL5BomPojo = new DtL5BomPojo();
			startConvertBomPojo(topBomLine, dtL5BomPojo);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (newBOMWindow != null) {
				newBOMWindow.save();
				newBOMWindow.close();
			}
		}
		return dtL5BomPojo;
	}
	
	private void startCreateItemRev(DtL5ItemRevPojo dtL5ItemRevPojo) throws Exception{
		List<DtL5ItemRevPojo> child = dtL5ItemRevPojo.getChild();
		if (child.size() > 0) {
			for (int i = 0; i < child.size(); i++) {
				DtL5ItemRevPojo dtL5ItemRevPojo2 = child.get(i);
				String itemId = dtL5ItemRevPojo2.getItemId();
				String itemName = dtL5ItemRevPojo2.getItemName();
				TCComponent[] queryResult = TCUtil.executeQuery(session, D9Constant.ITEM_NAME_OR_ID, new String[]{D9Constant.D9_ITEM_ID}, new String[]{itemId});
				TCComponentItemRevision newItemRev = null;
				if (queryResult.length == 0) {
					TCComponentItem newItem = TCUtil.create2Item(session, itemId, "A", itemName, D9Constant.COMMON_PART);
					newItemRev = newItem.getLatestItemRevision();
				}else {
					TCComponentItem oldItem = (TCComponentItem) queryResult[0];
					TCComponentItemRevision oldItemRev = oldItem.getLatestItemRevision();
					boolean isRele = TCUtil.isReleased(oldItemRev);
					if (isRele) {
						String versionRule = TCUtil.getVersionRule(oldItemRev);
						String newRevId = TCUtil.reviseVersion(session, versionRule, oldItemRev.getTypeObject().getName(), oldItemRev.getUid());
						newItemRev = oldItemRev.saveAs(newRevId);
					}else {
						newItemRev = oldItemRev;
					}	
				}
				setItemRevProperty(newItemRev, dtL5ItemRevPojo2);
				startCreateItemRev(dtL5ItemRevPojo2);
			}
		}
	}
	
	public void startConvertBomPojo(TCComponentBOMLine parentBomLine, DtL5BomPojo parentDtL5BomPojo) throws Exception{
		String itemId = parentDtL5BomPojo.getItemId();
		if (itemId == null || "".equals(itemId)) { 
			setProperty(parentBomLine, parentDtL5BomPojo);
		}
		AIFComponentContext[] childs = parentBomLine.getChildren();
		if (childs.length > 0) {
			for (int i = 0; i < childs.length; i++) {
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) childs[i].getComponent();
				childBomLine.pack();
				DtL5BomPojo childDtL5BomPojo = new DtL5BomPojo();
				setProperty(childBomLine, childDtL5BomPojo);
				parentDtL5BomPojo.getChild().add(childDtL5BomPojo);
				startConvertBomPojo(childBomLine, childDtL5BomPojo);
			}
		}
	}
	
	private void setProperty(TCComponentBOMLine bomLine, DtL5BomPojo dtL5BomPojo) throws Exception{
		String itemId = bomLine.getProperty("bl_item_item_id");
		String itemName = bomLine.getProperty("bl_item_object_name");
		String sequenceNo = bomLine.getProperty("bl_sequence_no");
		String quantity = bomLine.getProperty("bl_quantity");
		dtL5BomPojo.setItemId(itemId);
		dtL5BomPojo.setItemName(itemName);
		dtL5BomPojo.setSequenceNo(sequenceNo);
		dtL5BomPojo.setQuantity(quantity);
	}
	
	@Override
	public void firstBuild(BomLinePojo b, DtL5ItemRevPojo dtL5ItemRevPojo) throws Exception {
		String itemId = b.getItemId();
		String itemName = b.getItemName();
		TCComponentItem newItem = TCUtil.create2Item(session, itemId, "A", itemName, D9Constant.COMMON_PART);
		TCComponentItemRevision itemRev = newItem.getLatestItemRevision();
		setItemRevProperty(itemRev, dtL5ItemRevPojo);
		TCComponentBOMWindow newBOMWindow = null;
		try {
			newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomLine = TCUtil.getTopBomline(newBOMWindow, itemRev);
			DtL5EbomDialog.writeLogText("正在構建BOM結構，請稍等..");
			startFirstBuild(topBomLine, (DtL5BomPojo)b);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (newBOMWindow != null) {
				newBOMWindow.save();
				newBOMWindow.close();
			}
		}
		introductionFolder(itemRev);
	}
	
	private void startFirstBuild(TCComponentBOMLine bomLine, DtL5BomPojo b1) throws Exception{
		List<DtL5BomPojo> child = b1.getChild();
		if (child != null && child.size() > 0) {
			for (int i = 0; i < child.size(); i++) {
				DtL5BomPojo bomPojo = child.get(i);
				String itemId = bomPojo.getItemId();
				String itemName = bomPojo.getItemName();
				String sequenceNo = bomPojo.getSequenceNo();
				String quantityStr = bomPojo.getQuantity();
				boolean isDecimal = quantityStr.contains(".");
				double quantity = Double.parseDouble(bomPojo.getQuantity());
				TCComponent[] queryResult = TCUtil.executeQuery(session, D9Constant.ITEM_NAME_OR_ID, new String[]{D9Constant.D9_ITEM_ID}, new String[]{itemId});
				TCComponentItem item = null;
				TCComponentItemRevision itemRev = null;
				if (queryResult.length == 0) {
					item = TCUtil.create2Item(session, itemId, "A", itemName, D9Constant.COMMON_PART);
					itemRev = item.getLatestItemRevision();
				}else {
					item = (TCComponentItem) queryResult[0];
					itemRev = item.getLatestItemRevision();
				}
				TCComponentBOMLine newBomLine = null;
				if (isDecimal) {
					quantity = 1;
				}
				for (int j = 0; j < quantity; j++) {
					newBomLine = bomLine.add(item, itemRev, null, false);
					newBomLine.setProperty("bl_sequence_no", sequenceNo);
					if (isDecimal) {
						newBomLine.setProperty("bl_uom", "Other");
						newBomLine.setProperty("bl_quantity", quantityStr);
					}
				}
				startFirstBuild(newBomLine, bomPojo);
			}
		}
	}

	@Override
	public void changeBuild(BomLinePojo b1, BomLinePojo b2) throws Exception {
		DtL5EbomDialog.writeLogText("比對BOM開始.");
		dtL5BomChangeData = new DtL5BomChangeData<DtL5BomPojo>();
		bomComparison((DtL5BomPojo) b1, (DtL5BomPojo) b2);
		List<BomParentChild<DtL5BomPojo>> addData = dtL5BomChangeData.getAddData();
		List<BomParentChild<DtL5BomPojo>> delData = dtL5BomChangeData.getDelData();
		if (addData.size() > 0) {
			addData = mergeParentEqualsData(addData);
			printlnAddDelLog(addData, "添加");
		}else {
			DtL5EbomDialog.writeLogText("未比對出添加數據.");
		}
		
		if (delData.size() > 0) {
			delData = mergeParentEqualsData(delData);
			printlnAddDelLog(delData, "刪除");
		}else {
			DtL5EbomDialog.writeLogText("未比對出刪除數據.");
		}
		
		if (addData.size() == 0 && delData.size() == 0) {
			DtL5EbomDialog.writeLogText("未比對出差異，【" + b1.getItemId() + "】料號操作結束.");
			return;
		}
		DtL5EbomDialog.writeLogText("比對BOM結束.");
		String itemId = b2.getItemId();
		TCComponent[] queryResult = TCUtil.executeQuery(session, D9Constant.ITEM_NAME_OR_ID, new String[]{D9Constant.D9_ITEM_ID}, new String[]{itemId});
		TCComponentItem item = (TCComponentItem) queryResult[0];
		DtL5EbomDialog.writeLogText("獲取需要升版數據.");
		Set<String> reviseDataId = getReviseDataId(addData, delData, itemId);
		DtL5EbomDialog.writeLogText("開始升版.");
		startRevise(item, reviseDataId);
		DtL5EbomDialog.writeLogText("構建BOM開始.");
		if (addData.size() > 0) {
			startChangeBuild(item, addData, "add");
		}
		if (delData.size() > 0) {
			startChangeBuild(item, delData, "del");
		}
		DtL5EbomDialog.writeLogText("構建BOM結束.");
		introductionFolder(item.getLatestItemRevision());
	}
	
	private Set<String> getReviseDataId(List<BomParentChild<DtL5BomPojo>> addData, List<BomParentChild<DtL5BomPojo>> delData, String topItemId){
		Set<String> changeDataParentIds = new TreeSet<>();
		for (int i = 0; i < addData.size(); i++) {
			BomParentChild<DtL5BomPojo> bomParentChild = addData.get(i);
			String itemId = bomParentChild.getParent().getItemId();
			if (!itemId.equals(topItemId)) {
				changeDataParentIds.add(itemId);
			}
		}
		for (int i = 0; i < delData.size(); i++) {
			BomParentChild<DtL5BomPojo> bomParentChild = delData.get(i);
			String itemId = bomParentChild.getParent().getItemId();
			if (!itemId.equals(topItemId)) {
				changeDataParentIds.add(itemId);
			}
		}
		return changeDataParentIds;
	}

	private void startRevise(TCComponentItem item, Set<String> itemIds) throws Exception{
		TCComponentItemRevision itemRev = item.getLatestItemRevision();
		TCComponentBOMWindow newBOMWindow = null;
		try {
			newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomLine = TCUtil.getTopBomline(newBOMWindow, itemRev);
			Iterator<String> iterator = itemIds.iterator();
			while (iterator.hasNext()) {
				String id = iterator.next();
				batchRevise(topBomLine, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (newBOMWindow != null) {
				newBOMWindow.save();
				newBOMWindow.close();
			}
		}
	}
	
	private void batchRevise(TCComponentBOMLine topBomLine, String id) throws Exception {
		AIFComponentContext[] childs = topBomLine.getChildren();
		if (childs.length > 0) {
			for (int i = 0; i < childs.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) childs[i].getComponent();
				String itemId = bomLine.getProperty("bl_item_item_id");
				if (itemId.equals(id)) {
					TCComponentItemRevision itemRev = bomLine.getItemRevision();
					boolean isRele = TCUtil.isReleased(itemRev);
					if (!isRele) {
						String workflowName = "TCM Release Process";
						TCUtil.createProcess("TCM Release Process", workflowName, "", new TCComponent[] {itemRev});
					}
					itemRev.saveAs("");
					break;
				}
				batchRevise(bomLine, id);
			}
		}
	}
	
	private void printlnAddDelLog(List<BomParentChild<DtL5BomPojo>> changeData, String msg){
		for (int i = 0; i < changeData.size(); i++) {
			BomParentChild<DtL5BomPojo> bomParentChild = changeData.get(i);
			String parentItemId = bomParentChild.getParent().getItemId();
			List<DtL5BomPojo> childs = bomParentChild.getChild();
			for (int j = 0; j < childs.size(); j++) {
				DtL5BomPojo child = childs.get(j);
				String childItemId = child.getItemId();
				String childQuantity = child.getQuantity();
				DtL5EbomDialog.writeLogText("父料【" + parentItemId + "】下，" + msg + "了" + childQuantity + "顆【" + childItemId + "】子料.");
			}
		}
	}
	
	private List<BomParentChild<DtL5BomPojo>> mergeParentEqualsData(List<BomParentChild<DtL5BomPojo>> addOrDelData){
		List<BomParentChild<DtL5BomPojo>> changeData =  new ArrayList<BomParentChild<DtL5BomPojo>>();
		for (int i = 0; i < addOrDelData.size(); i++) {
			BomParentChild<DtL5BomPojo> addOrDelBomParentChild = addOrDelData.get(i);
			String parentItemId = addOrDelBomParentChild.getParent().getItemId();
			List<BomParentChild<DtL5BomPojo>> result = changeData.stream().filter(c -> c.getParent().getItemId().equals(parentItemId)).collect(Collectors.toList());
			if (result.size() > 0) {
				BomParentChild<DtL5BomPojo> bomParentChild = result.get(0);
				bomParentChild.getChild().addAll(addOrDelBomParentChild.getChild());
			}else {
				changeData.add(addOrDelBomParentChild);
			}
		}
		return changeData;
	}
	
	private void startChangeBuild (TCComponentItem item, List<BomParentChild<DtL5BomPojo>> changeData, String operationType) throws Exception {
		TCComponentBOMWindow newBOMWindow = null;
		try {
			newBOMWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomLine = TCUtil.getTopBomline(newBOMWindow, item.getLatestItemRevision());
			if ("add".equals(operationType)) {
				for (int i = 0; i < changeData.size(); i++) {
					BomParentChild<DtL5BomPojo> bomParentChild = changeData.get(i);
					addChangeData(topBomLine, bomParentChild);
				}
			}
			if ("del".equals(operationType)) {
				for (int i = 0; i < changeData.size(); i++) {
					BomParentChild<DtL5BomPojo> bomParentChild = changeData.get(i);
					delChangeData(topBomLine, bomParentChild);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (newBOMWindow != null) {
				newBOMWindow.save();
				newBOMWindow.close();
			}
		}
	}
	
	private void addChangeData(TCComponentBOMLine topBomLine, BomParentChild<DtL5BomPojo> bomParentChild) throws Exception {
		AIFComponentContext[] childs = topBomLine.getChildren();
		if (childs.length > 0) {
			for (int i = 0; i < childs.length; i++) {
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) childs[i].getComponent();
				String itemId = childBomLine.getProperty("bl_item_item_id");
				DtL5BomPojo dtL5BomPojo = bomParentChild.getParent();
				if (itemId.equals(dtL5BomPojo.getItemId())) {
					List<DtL5BomPojo> child = bomParentChild.getChild();
					for (int j = 0; j < child.size(); j++) {
						DtL5BomPojo dtL5BomPojo2 = child.get(j);
						String itemId2 = dtL5BomPojo2.getItemId();
						TCComponentItem item = null;
						TCComponent[] queryResult = TCUtil.executeQuery(session, D9Constant.ITEM_NAME_OR_ID, new String[]{D9Constant.D9_ITEM_ID}, new String[]{itemId2});
						if (queryResult.length > 0) {
							item = (TCComponentItem) queryResult[0];
						}else {
							String itemName = dtL5BomPojo2.getItemName();
							item = TCUtil.create2Item(session, itemId, "A", itemName, D9Constant.COMMON_PART);
						}
						String sequenceNo = dtL5BomPojo2.getSequenceNo();
						Integer quantity = Integer.valueOf(dtL5BomPojo2.getQuantity());
						for (int k = 0; k < quantity; k++) {
							TCComponentBOMLine newBomLine = childBomLine.add(item, item.getLatestItemRevision(), null, false);
							newBomLine.setProperty("bl_sequence_no", sequenceNo);
							newBomLine.setProperty("bl_quantity", "1");
						}
					}
					break;
				}
				addChangeData(childBomLine, bomParentChild);
			}
		}else {
			List<DtL5BomPojo> child = bomParentChild.getChild();
			for (int j = 0; j < child.size(); j++) {
				DtL5BomPojo dtL5BomPojo = child.get(j);
				String itemId = dtL5BomPojo.getItemId();
				TCComponentItem item = null;
				TCComponent[] queryResult = TCUtil.executeQuery(session, D9Constant.ITEM_NAME_OR_ID, new String[]{D9Constant.D9_ITEM_ID}, new String[]{itemId});
				if (queryResult.length > 0) {
					item = (TCComponentItem) queryResult[0];
				}else {
					String itemName = dtL5BomPojo.getItemName();
					item = TCUtil.create2Item(session, itemId, "A", itemName, D9Constant.COMMON_PART);
				}
				String sequenceNo = dtL5BomPojo.getSequenceNo();
				Integer quantity = Integer.valueOf(dtL5BomPojo.getQuantity());
				for (int k = 0; k < quantity; k++) {
					TCComponentBOMLine newBomLine = topBomLine.add(item, item.getLatestItemRevision(), null, false);
					newBomLine.setProperty("bl_sequence_no", sequenceNo);
					newBomLine.setProperty("bl_quantity", "1");
				}
			}
		}
	}

	private void delChangeData(TCComponentBOMLine topBomLine, BomParentChild<DtL5BomPojo> bomParentChild) throws Exception{
		AIFComponentContext[] childs = topBomLine.getChildren();
		if (childs.length > 0) {
			for (int i = 0; i < childs.length; i++) {
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) childs[i].getComponent();
				String itemId = childBomLine.getProperty("bl_item_item_id");
				DtL5BomPojo delParent = bomParentChild.getParent();
				List<DtL5BomPojo> delChild = bomParentChild.getChild();
				if (itemId.equals(delParent.getItemId())) {
					TCUtil.unpackageBOMStructure(childBomLine);
					AIFComponentContext[] children = childBomLine.getChildren();
					List<TCComponentBOMLine> bomLines = contextToBomLine(children);
					for (int j = 0; j < delChild.size(); j++) {
						DtL5BomPojo dtL5BomPojo = delChild.get(j);
						String itemId2 = dtL5BomPojo.getItemId();
						String quantity = dtL5BomPojo.getQuantity();
						List<TCComponentBOMLine> delLines = getBomLinesById(bomLines, itemId2, Integer.valueOf(quantity));
						for (int k = 0; k < delLines.size(); k++) {
							delLines.get(k).cut();
						}
					}
					break;
				}
				delChangeData(childBomLine, bomParentChild);
			}
		}
	}
	
	private List<TCComponentBOMLine> getBomLinesById(List<TCComponentBOMLine> bomLineList, String itemId, Integer quantity) throws Exception {
		List<TCComponentBOMLine> bomLines = new ArrayList<>();
		for (int i = 0; i < bomLineList.size(); i++) {
			if (bomLines.size() == quantity) {
				return bomLines;
			}
			TCComponentBOMLine bomLine = bomLineList.get(i);
			String bomItemId = bomLine.getProperty("bl_item_item_id");
			if (itemId.equals(bomItemId)) {
				bomLines.add(bomLine);
			}
		}
		return bomLines;
	}

	private List<TCComponentBOMLine> contextToBomLine(AIFComponentContext[] child) {
		List<TCComponentBOMLine> bomLines = new ArrayList<>();
		for (int i = 0; i < child.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) child[i].getComponent();
			bomLines.add(bomLine);
		}
		return bomLines;
	}
	
	private void bomComparison(DtL5BomPojo b1, DtL5BomPojo b2) throws Exception {
		List<DtL5BomPojo> agileChilds = b1.getChild();
		if (agileChilds != null && agileChilds.size() > 0) {
			List<DtL5BomPojo> tcChilds = b2.getChild();
			for (int i = 0; i < agileChilds.size(); i++) {
				DtL5BomPojo agileDtL5BomPojo = agileChilds.get(i);
				String agileItemId = agileDtL5BomPojo.getItemId();
				Integer agileQuantity = Integer.valueOf(agileDtL5BomPojo.getQuantity());
				List<DtL5BomPojo> result = tcChilds.stream().filter(d -> d.getItemId().equals(agileItemId)).collect(Collectors.toList());
				DtL5BomPojo tcDtL5BomPojo = null;
				if (result.size() > 0) {
					tcDtL5BomPojo = result.get(0);
				}
				if (tcDtL5BomPojo != null) {
					Integer tcQuantity = Integer.valueOf(tcDtL5BomPojo.getQuantity());
					if (agileQuantity > tcQuantity) {
						Integer addQuantity = agileQuantity - tcQuantity;
						BomParentChild<DtL5BomPojo> bomParentChild = new BomParentChild<DtL5BomPojo>();
						bomParentChild.setParent(b1);
						DtL5BomPojo newDtL5BomPojo = copyObject(agileDtL5BomPojo);
						newDtL5BomPojo.setQuantity(String.valueOf(addQuantity));
						bomParentChild.getChild().add(newDtL5BomPojo);
						dtL5BomChangeData.getAddData().add(bomParentChild);
					}else if (agileQuantity < tcQuantity) {
						Integer delQuantity = tcQuantity - agileQuantity;
						BomParentChild<DtL5BomPojo> bomParentChild = new BomParentChild<DtL5BomPojo>();
						bomParentChild.setParent(b1);
						DtL5BomPojo newDtL5BomPojo = copyObject(agileDtL5BomPojo);
						newDtL5BomPojo.setQuantity(String.valueOf(delQuantity));
						bomParentChild.getChild().add(newDtL5BomPojo);
						dtL5BomChangeData.getDelData().add(bomParentChild);
					}
				}else {
					BomParentChild<DtL5BomPojo> bomParentChild = new BomParentChild<DtL5BomPojo>();
					bomParentChild.setParent(b1);
					bomParentChild.getChild().add(agileDtL5BomPojo);
					dtL5BomChangeData.getAddData().add(bomParentChild);
					continue;
				}
				bomComparison(agileDtL5BomPojo, tcDtL5BomPojo);
			}
		}
	}
	
	private DtL5BomPojo copyObject(DtL5BomPojo dtL5BomPojo){
		
		String itemId = dtL5BomPojo.getItemId();
		String itemName = dtL5BomPojo.getItemName();
		String itemRevId = dtL5BomPojo.getItemRevId();
		String sequenceNo = dtL5BomPojo.getSequenceNo();
		
		DtL5BomPojo newDtL5BomPojo = new DtL5BomPojo();
		newDtL5BomPojo.setItemId(itemId);
		newDtL5BomPojo.setItemName(itemName);
		newDtL5BomPojo.setItemRevId(itemRevId);
		newDtL5BomPojo.setSequenceNo(sequenceNo);
		
		return newDtL5BomPojo;
	}
	
	private void setItemRevProperty(TCComponentItemRevision itemRev, DtL5ItemRevPojo dtL5ItemRevPojo) throws Exception {
		itemRev.setProperty(D9Constant.D9_ENGLISH_DESC, dtL5ItemRevPojo.getEnglishDescription());
		itemRev.setProperty(D9Constant.D9_CHINESE_DESC, dtL5ItemRevPojo.getChineseDescription());
		itemRev.setProperty(D9Constant.D9_CUSTOMER_PN, dtL5ItemRevPojo.getCustomerPN());
		itemRev.setProperty(D9Constant.D9_MANUFACTURER_ID, dtL5ItemRevPojo.getManufacturerID());
		itemRev.setProperty(D9Constant.D9_MANUFACTURER_PN, dtL5ItemRevPojo.getManufacturerPN());
		itemRev.setProperty(D9Constant.D9_ASSEMBLY_CODE, dtL5ItemRevPojo.getAssemblyCode());
	}

	private void introductionFolder(TCComponentItemRevision itemRev) throws Exception{
		TCComponentFolder folder = null;
		InterfaceAIFComponent targetCom = AIFUtility.getCurrentApplication().getTargetComponent();
		if (targetCom != null) {
			if (targetCom instanceof TCComponentFolder) {
				folder = (TCComponentFolder) targetCom;
			}	
		}
		if (folder == null) {
			folder = session.getUser().getNewStuffFolder();
		}
		folder.add("contents", itemRev);
	}

}
