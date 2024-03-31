package com.foxconn.electronics.convertebom;

import java.util.List;
import java.util.Map;

import com.foxconn.electronics.domain.Constants;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class BOMFactory
{
    public static TCBOM getProductLineBOM(String productLine, TCComponentBOMLine topBOMLine, List<TCComponentItemRevision> selfPartList, Map<String, TCComponentItemRevision> actionMsgMap, TCComponentFolder placeModelFolder) throws Exception
    {
        if (Constants.MNT.equals(productLine))
        {
            return new MonitorBOM(topBOMLine, selfPartList, actionMsgMap, placeModelFolder);
        }
        else if (Constants.PRT.equals(productLine))
        {
            return new PrinterBOM(topBOMLine);
        }
        return null;
    }
}
