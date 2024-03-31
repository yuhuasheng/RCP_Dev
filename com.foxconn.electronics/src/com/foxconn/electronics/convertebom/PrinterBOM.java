package com.foxconn.electronics.convertebom;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class PrinterBOM extends TCBOM
{
    TCComponentBOMLine topBOMLine;

    public PrinterBOM(TCComponentBOMLine topBOMLine)
    {
        this.topBOMLine = topBOMLine;
    }

    @Override
    public void validation() throws Exception
    {
    }

    @Override
    public void comparison() throws Exception
    {
    }

    @Override
    public TCComponentItemRevision buildEBOM() throws Exception
    {
        return null;
    }
}
