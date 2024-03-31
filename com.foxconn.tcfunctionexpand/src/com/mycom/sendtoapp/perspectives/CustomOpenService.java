package com.mycom.sendtoapp.perspectives;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.services.IOpenService;

public class CustomOpenService implements IOpenService {
    @Override
    public boolean close() throws Exception {
           // TODO Auto-generated method stub
           return false;
    }
    @Override
    public boolean open(final InterfaceAIFComponent cmp) {
           Display.getDefault().asyncExec( new Runnable() {
                   public void run() {
                           IWorkbenchWindow window = PlatformUI.getWorkbench()
                                          .getActiveWorkbenchWindow();
                           MessageDialog
                                          .openInformation(window.getShell(),
                                                         "CustomOpenService",
                                                         "You sent this component to the SendTo Application: "
                                                                        + cmp.toString());
                   }
           });
           return false;
    }
    @Override
    public boolean open(InterfaceAIFComponent[] cmps) {
           return false;
    }
}
