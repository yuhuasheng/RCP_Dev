// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UploadFileHandler.java

package com.hh.tools.uploadFile;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import org.eclipse.core.commands.*;

// Referenced classes of package com.hh.common.uploadfile:
//			UploadFileAction

public class UploadFileHandler extends AbstractHandler {

    private AbstractAIFUIApplication app;

    public UploadFileHandler() {
        app = null;
    }

    public Object execute(ExecutionEvent arg0)
            throws ExecutionException {
        app = AIFUtility.getCurrentApplication();
        UploadFileAction action = new UploadFileAction(app, null, "");
        (new Thread(action)).start();
        return null;
    }
}