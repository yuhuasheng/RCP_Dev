// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UploadFileAction.java

package com.hh.tools.uploadFile;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.kernel.TCSession;

import java.awt.Frame;

// Referenced classes of package com.hh.common.uploadfile:
//			UploadFileDialog

public class UploadFileAction extends AbstractAIFAction {

    private AbstractAIFUIApplication app;
    private TCSession session;

    public UploadFileAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        app = null;
        session = null;
        app = arg0;
        session = (TCSession) app.getSession();
    }

    public void run() {
        new UploadFileDialog(app, session);
    }
}