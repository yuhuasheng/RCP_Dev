package com.foxconn.electronics.explodebom.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.foxconn.electronics.explodebom.domain.BOMLineBean;
import com.foxconn.electronics.explodebom.domain.InputInfoBean;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ExportBOMFileService {
	private TCSession                       session;
    private TCComponentBOMLine              rootBomLine;
    private List<TCComponentItemRevision>	inputs;
    
    public ExportBOMFileService(TCSession session, List<TCComponentItemRevision> inputs) {
    	this.session = session;
    	this.inputs = inputs;
    }
    
    public static Map<TCComponentBOMLine, List<BOMLineBean>> executeGenBomFile(TCSession session, List<TCComponentItemRevision> inputs) {
    	return new ExportBOMFileService(session, inputs).execute();
    }
    
    private Map<TCComponentBOMLine, List<BOMLineBean>> execute()
    {
    	List<TCComponentBOMLine> bomLineLst = new ArrayList<TCComponentBOMLine>();
    	for (TCComponentItemRevision itemRev : inputs) {
    		bomLineLst.add(TCUtil.openBomWindow(session, itemRev));
    	}
    	
    	return bomLineLst.stream().collect(Collectors.toMap(this::getBOMLine, rootBOMLine -> {
			try {
				return TCUtil.getTCComponmentBOMLines(rootBOMLine, null, true).stream().map(bomLine -> {
				    try
				    {
				        BOMLineBean bomLineBean = TCUtil.tcPropMapping(new BOMLineBean(), bomLine);                   
				        return bomLineBean;
				    }
				    catch (TCException | IllegalAccessException exp)
				    {
				    	exp.printStackTrace();
				    }
				    return null;
				    
				}).collect(Collectors.toList());
			} catch (TCException e1) {
				e1.printStackTrace();
			}
	
			return new ArrayList<BOMLineBean>();
        
    	}));
    	    	
    }
    
    /**
     * POS_” BOM栏位序号 ”_”总图名称”_”客户PROJECT NAME”_YYYYMMDD
     * 
     * @param bomLine
     * @return
     */
    public static String getBOMFileName(TCComponentBOMLine bomLine)
    {
        String bomName = "";
        try
        {
            // 总图名称
            return "POS_" + bomLine.getItemRevision().getProperty("object_name")+ "_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        
        return "";       
    }
    
    private TCComponentBOMLine getBOMLine(TCComponentBOMLine bomLine) {
    	return bomLine;
    }
    
    public static List<String> checkBOFFileData(List<BOMLineBean> bomLineBeanLst) {
    	return bomLineBeanLst.stream().map(e -> {
    		try {
				return TCUtil.getRequiredInfo(e);
			} catch (IllegalAccessException | TCException e1) {
				e1.printStackTrace();
			}
    		
    		return "";
    	}).collect(Collectors.toList());
    }
    
}
