package com.foxconn.electronics.managementebom.export.changelist.rf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMUpdateBean;
import com.foxconn.electronics.managementebom.updatebom.service.CompareBOM;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class RfChangeListCompare {
	protected EBOMLineBean       sourceBomBean;
    protected EBOMLineBean       targetBomBean;
	private List<EBOMLineBean>   adds    = new ArrayList<EBOMLineBean>();
    private List<EBOMLineBean>   dels    = new ArrayList<EBOMLineBean>();
    private List<EBOMUpdateBean> changes = new ArrayList<EBOMUpdateBean>();
    
    public List<EBOMLineBean> getAdd() {
    	return adds;
    }
    
    public List<EBOMLineBean> getDels() {
    	return dels;
    }
    
    public List<EBOMUpdateBean> getChanges() {
    	return changes;
    }
    
    public void compareBOM(String[] compareField)
    {
        compareBOM(sourceBomBean, targetBomBean,compareField);
    }
    
    public void compareBOM(EBOMLineBean sourceBomBean, EBOMLineBean targetBomBean,String[] compareField){
    	CompareBOM result = new CompareBOM(targetBomBean, sourceBomBean);
    	adds.addAll(result.getAdd());
    	dels.addAll(result.getDel());
    	List<EBOMUpdateBean> beans = result.getChange(compareField);
    	List<EBOMUpdateBean> changeBeans = beans.stream().filter(e -> e.getChangeFiledNames().size() > 0).collect(Collectors.toList());
        changes.addAll(changeBeans);
        for (EBOMUpdateBean changeBean : beans)
        {
            compareBOM(changeBean.getOldEBomBean(), changeBean.getNewEBomBean(),compareField);
        }
    }
    
    

    public RfChangeListCompare(TCComponentItemRevision sourceItemRev, TCComponentItemRevision targetItemRev) throws Exception {
    	FutureTask<EBOMLineBean> nT = getTopBOMLineTask(sourceItemRev);
        FutureTask<EBOMLineBean> oT = getTopBOMLineTask(targetItemRev);
        sourceBomBean = nT.get();
        targetBomBean = oT.get();
        if (targetBomBean == null || sourceBomBean == null)
        {
            throw new RuntimeException("error data !!");
        }
        convertUid(sourceBomBean.getChilds());
        convertUid(targetBomBean.getChilds());
    }

    
    public static EBOMLineBean getTopBOMLine(TCComponentItemRevision itemRev) throws TCException
    {
        TCComponentBOMWindow bomWindow = null;
        if (TCUtil.isReleased(itemRev))
        {
            bomWindow = PartBOMUtils.createBomWindowBySnapshot(itemRev);
        }
        if (bomWindow == null)
        {
            bomWindow = PartBOMUtils.createBomWindow(itemRev);
        }
        EBOMLineBean bomLineBean = new UpdateEBOMService().getBOMStruct(bomWindow.getTopBOMLine());
        bomWindow.close();
        return bomLineBean;
    }
    
    private FutureTask<EBOMLineBean> getTopBOMLineTask(TCComponentItemRevision itemRev) throws TCException
    {
        FutureTask<EBOMLineBean> fTask = new FutureTask<EBOMLineBean>(() -> getTopBOMLine(itemRev));
        new Thread(fTask).start();
        return fTask;
    }
    
    private void convertUid(List<EBOMLineBean> beans)
    {
        if (beans != null)
        {
            beans.forEach(bean -> {
                bean.setUid(bean.getBomId());
                convertUid(bean.getSecondSource());
                convertUid(bean.getChilds());
            });
        }
    }
}
