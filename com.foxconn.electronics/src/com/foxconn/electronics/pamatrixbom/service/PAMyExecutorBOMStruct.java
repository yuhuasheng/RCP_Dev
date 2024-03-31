package com.foxconn.electronics.pamatrixbom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

import com.foxconn.electronics.pamatrixbom.domain.PAVariableBOMBean;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class PAMyExecutorBOMStruct extends Thread {
	AIFComponentContext component;
	PAVariableBOMBean rootBean;
	Semaphore position;
	public PAMyExecutorBOMStruct(AIFComponentContext component, PAVariableBOMBean rootBean, Semaphore position) {
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
					PAVariableBOMBean bomBean = getSeriesBOMStruct_children(bl);
					if (bl.hasSubstitutes()) {
						TCComponentBOMLine[] listSubstitutes = bl.listSubstitutes();
						for (TCComponentBOMLine subBomline : listSubstitutes) {
							PAVariableBOMBean subBean = new PAVariableBOMBean(subBomline);
							subBean.setIsSub(true);// 设置替代料标识为true
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
	public void addSeriesList(PAVariableBOMBean current, PAVariableBOMBean next) {
		List<PAVariableBOMBean> child = current.getChild();
		if (null == child) {
			child = new ArrayList<PAVariableBOMBean>();
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
	public void addSeriesSubList(PAVariableBOMBean current, PAVariableBOMBean next) {
		List<PAVariableBOMBean> subList = current.getSubList();
		if (null == subList) {
			subList = new ArrayList<PAVariableBOMBean>();
			current.setSubList(subList);
		}

	   subList.add(next);
		
	}
	
	private PAVariableBOMBean getSeriesBOMStruct_children(TCComponentBOMLine rootLine) throws TCException {
		rootLine.refresh();
		PAVariableBOMBean rootBean = new PAVariableBOMBean(rootLine);
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
							PAVariableBOMBean bomBean = new PAVariableBOMBean(bl);
							if (bl.hasSubstitutes()) {
								TCComponentBOMLine[] listSubstitutes = bl.listSubstitutes();
								for (TCComponentBOMLine subBomline : listSubstitutes) {
									PAVariableBOMBean subBean = new PAVariableBOMBean(subBomline);
									subBean.setIsSub(true); // 设置替代料标识为true
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
