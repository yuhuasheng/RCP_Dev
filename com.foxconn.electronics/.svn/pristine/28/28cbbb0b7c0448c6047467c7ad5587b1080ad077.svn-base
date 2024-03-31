package com.foxconn.electronics.matrixbom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class MyExecutorBOMStruct extends Thread {
	AIFComponentContext component;
	VariableBOMBean rootBean;
	Semaphore position;
	public MyExecutorBOMStruct(AIFComponentContext component, VariableBOMBean rootBean, Semaphore position) {
		this.component = component;
		this.rootBean = rootBean;
		this.position = position;
	}	
	public void run() {
		try {
			position.acquire();
		
			TCComponentBOMLine bomLine = (TCComponentBOMLine) component.getComponent();
			if (!bomLine.isSubstitute()) {
				List<TCComponentBOMLine> lines=new ArrayList<>();
				if(bomLine.isPacked()) {					
					TCComponentBOMLine[] tmps=bomLine.getPackedLines();
					lines.add(bomLine);
					for(TCComponentBOMLine t:tmps) {
						lines.add(t);
					}
					bomLine.unpack();
					bomLine.refresh();
				} else {
					lines.add(bomLine);
				}
				
				for(TCComponentBOMLine bl:lines) {
					VariableBOMBean bomBean = getSeriesBOMStruct_children(bl);
					if (bl.hasSubstitutes()) {
						TCComponentBOMLine[] listSubstitutes = bl.listSubstitutes();
						for (TCComponentBOMLine subBomline : listSubstitutes) {
							VariableBOMBean subBean = VariableBOMBean.tcPropMapping(new VariableBOMBean(), subBomline);
							subBean.setQty(bomBean.getQty());
							subBean.setIsSub(true); // 设置替代料标识为true
							subBean.setItemRevUid(subBomline.getItemRevision().getUid());
							subBean.setLineId(CommonTools.md5Encode(subBean.getItemId()+subBean.getCategory().trim()+subBean.getPlant()));
							addSeriesSubList(bomBean, subBean);
						}
					}
				    addSeriesList(rootBean, bomBean);
				}
			}
			
			position.release();
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}
	
	
	/**
	 * 添加父子料
	 * 
	 * @param current
	 * @param next
	 */
	public void addSeriesList(VariableBOMBean current, VariableBOMBean next) {
		List<VariableBOMBean> child = current.getChild();
		if (null == child) {
			child = new ArrayList<VariableBOMBean>();
			current.setChild(child);
		}
		
		child.add(next);
	}

	/**
	 * 添加主料和替代料
	 * 
	 * @param current
	 * @param next
	 */
	public void addSeriesSubList(VariableBOMBean current, VariableBOMBean next) {
		List<VariableBOMBean> subList = current.getSubList();
		if (null == subList) {
			subList = new ArrayList<VariableBOMBean>();
			current.setSubList(subList);
		}

	   subList.add(next);
		
	}
	
	private VariableBOMBean getSeriesBOMStruct_children(TCComponentBOMLine rootLine) throws TCException {
		rootLine.refresh();
		VariableBOMBean rootBean = new VariableBOMBean(rootLine);
		System.out.println(Thread.currentThread().getName() + " ==>> title: " + rootBean.getItemId());
		AIFComponentContext[] componmentContext = null;
		if (rootLine.hasChildren()) {
			componmentContext = rootLine.getChildren();
		}

		if (componmentContext != null) {
			Stream.of(componmentContext).forEach(e -> {
				try {
					
					TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
					if (!bomLine.isSubstitute()) {
						List<TCComponentBOMLine> lines=new ArrayList<>();
						if(bomLine.isPacked()) {					
							TCComponentBOMLine[] tmps=bomLine.getPackedLines();
							lines.add(bomLine);
							for(TCComponentBOMLine t:tmps) {
								lines.add(t);
							}
							bomLine.unpack();
							bomLine.refresh();
							
						}else {
							lines.add(bomLine);
						}
						
						for(TCComponentBOMLine bl:lines) {
							VariableBOMBean bomBean = new VariableBOMBean(bl);
							if (bl.hasSubstitutes()) {
								TCComponentBOMLine[] listSubstitutes = bl.listSubstitutes();
								for (TCComponentBOMLine subBomline : listSubstitutes) {
									VariableBOMBean subBean = VariableBOMBean.tcPropMapping(new VariableBOMBean(), subBomline);
									subBean.setQty(bomBean.getQty());
									subBean.setIsSub(true); // 设置替代料标识为true
									subBean.setItemRevUid(subBomline.getItemRevision().getUid());
									subBean.setLineId(CommonTools.md5Encode(subBean.getItemId()+subBean.getCategory().trim()+subBean.getPlant()));
									addSeriesSubList(bomBean, subBean);
								}
							}
						    addSeriesList(rootBean, bomBean);
						}
						
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			});
		}
		return rootBean;
	}

}
