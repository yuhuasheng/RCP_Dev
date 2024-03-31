package com.foxconn.mechanism.hhpnmaterialapply.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.Image;

import com.foxconn.mechanism.hhpnmaterialapply.MyContainerCheckedTreeViewer;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.IconsEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemRevEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.PropSyncEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.StatusEnum;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.CheckDesignBean;
import com.foxconn.mechanism.hhpnmaterialapply.message.MessageShow;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.Registry;

public class BOMTreeValidation {
	
	private boolean hasPartFlag = false; // 判断是否含有物料对象
	
	/**
	 * 校验BOM结构树中是否含有物料对象
	 * @param topBomInfo
	 */
	public void checkPartRev(BOMInfo topBomInfo) {		
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) {			
			return;
		}
		
		for (BOMInfo childBomInfo : childBomInfos) {
			String objectType = childBomInfo.getObjectType();
			if (objectType.equals(ItemRevEnum.CommonPartRev.type())) {
				hasPartFlag = true;
				return;
			} else {
				checkPartRev(childBomInfo);
			}
		}
	}	
	
	
	/**
	 * 校验设计对象是否含有物料对象并且是否为已发布状态
	 * @throws TCException 
	 */
	public void filterDesignReleaseForDT(BOMInfo topBomInfo, List<CheckDesignBean> list, String filterFlag) throws TCException {
		String objectType = topBomInfo.getObjectType(); 
		if (ItemRevEnum.CommonPartRev.type().equals(objectType)) { // 如果当前对象类型为物料对象，直接返回
			return;
		}
		
		List<BOMInfo> childBomInfoList = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfoList)) {
			return;
		}		
		TCComponentItemRevision itemRev = topBomInfo.getItemRevision();
		if (CommonTools.isEmpty(itemRev)) {
			return;
		}		
		if (checkPartObj(topBomInfo)) { // 判断是否含有物料对象版本	
			boolean released = TCUtil.isReleased(itemRev, new String[] { StatusEnum.D9_FastRelease.name(), StatusEnum.D9_Release.name(), StatusEnum.TCMReleased.name()}); // 判断设计对象版本是否已经发行			
			if (released && TCUtil.matcherLetter(itemRev.getProperty("item_revision_id")) 
					&& PropSyncEnum.part_design.name().equals(filterFlag)) { // 设计对象是字母版并且是已经发行状态，当前操作是物料对象属性同步设计对象属性				
				CheckDesignBean bean = new CheckDesignBean();
				bean.setModelName(topBomInfo.getItemId());
				bean.setStatus(StatusEnum.Released.name());								
				list.add(bean);
				topBomInfo.updateSyncFlag(false); // 修改为无法同步属性
			}			
		}
		
		for (BOMInfo childBomInfo : childBomInfoList) {
			filterDesignReleaseForDT(childBomInfo, list, filterFlag);
		}
	}
	
	/**
	 * 校验和过滤当前选中的结构树的记录
	 * @param checkedTreeViewer
	 * @param filterFlag 用作是设计对象同步物料对象还是物料对象同步设计对象
	 * @return
	 * @throws TCException 
	 */
	public Map<String, List<? extends Object>> filterDesignReleaseForMNTOrPRT(MyContainerCheckedTreeViewer checkedTreeViewer, Registry reg, String filterFlag) throws TCException {
		Map<String, List<? extends Object>> resultMap = new HashMap<String, List<? extends Object>>();
		List<CheckDesignBean> checkReleasedList = new ArrayList<CheckDesignBean>();
		Object[] checkedElements = checkedTreeViewer.getCheckedElements();
		if (CommonTools.isEmpty(checkedElements)) {
			MessageShow.warningMsgBox(reg.getString("propSyncBtnWarn1.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		
		List<BOMInfo> filterElementList = BOMTreeTools.getBOMInfoList(new ArrayList<>(Arrays.asList(checkedElements)));		
		filterElementList.removeIf(info -> CommonTools.isEmpty(info.getPropertiesInfo())); // 过滤掉数据类为空
		ListIterator listIterator = filterElementList.listIterator();
		while (listIterator.hasNext()) {
			BOMInfo info = (BOMInfo) listIterator.next();
//			info.hashCode()
			if (ItemRevEnum.CommonPartRev.type().equals(info.getObjectType())) { // 如果此记录是物料对象，则找到父设计对象			
				BOMInfo parentInfo = info.getParent();
				listIterator.remove(); // 将物料对象记录从集合中移除
				listIterator.add(parentInfo); // 添加父设计对象到集合				
			} else {
				if (CommonTools.isNotEmpty(info.getItemRevision())) {
					boolean released = TCUtil.isReleased(info.getItemRevision(), new String[] { StatusEnum.D9_FastRelease.name(), StatusEnum.D9_Release.name(), 
							StatusEnum.TCMReleased.name()}); // 判断设计对象版本是否已经发行	
					if (released && TCUtil.matcherLetter(info.getItemRevision().getProperty("item_revision_id")) 
							&& PropSyncEnum.part_design.name().equals(filterFlag)) { // 设计对象是字母版并且是已经发行状态，当前操作是物料对象属性同步设计对象属性
						CheckDesignBean bean = new CheckDesignBean();
						bean.setModelName(info.getItemId());
						bean.setStatus(StatusEnum.Released.name());								
						checkReleasedList.add(bean);
						listIterator.remove();
					}
				}
				
			}			
		}		
		
		if (CommonTools.isEmpty(filterElementList)) {
			MessageShow.warningMsgBox(reg.getString("propSyncBtnWarn2.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		
		filterElementList = filterElementList.stream().filter(CommonTools.distinctByKey(info -> info.getItemRevision().getUid() + info.hashCode())).collect(Collectors.toList()); // 去除未发布的设计对象重复记录
		
		if (CommonTools.isNotEmpty(checkReleasedList)) {
			checkReleasedList = checkReleasedList.stream().filter(CommonTools.distinctByKey(bean -> bean.getModelName())).collect(Collectors.toList()); // 去除校验后设计对象已发行重复记录
			setCheckDisignIds(checkReleasedList);
		}
		
		resultMap.put("filterElementList", filterElementList);
		resultMap.put("checkReleasedList", checkReleasedList);
		return resultMap;
	}
	
	/**
	 * 设置校验设计对象已经发布的编号
	 * @param list
	 */
	public void setCheckDisignIds(List<CheckDesignBean> list) {
		long k = 0;
		for (CheckDesignBean bean : list) {
			bean.setId(new Long(++k));
		}
	}
	
	
	/**
	 * 判断是否含有物料对象版本
	 * @param list
	 * @return
	 */
	public boolean checkPartObj(BOMInfo topBomInfo) {
		List<BOMInfo> list = topBomInfo.getChild();
		boolean flag = false;
		String objectType = null;
		for (BOMInfo bomInfo : list) {
			objectType = bomInfo.getObjectType();
			if (ItemRevEnum.CommonPartRev.type().equals(objectType)) {
				bomInfo.updateSyncFlag(false); // 修改为属性无法同步				
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public boolean getHasPartFlag() {
		return hasPartFlag;
	}
	
	
	
}
