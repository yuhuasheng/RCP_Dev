package com.hh.tools.importCLForm;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class ImportFormHandler extends AbstractHandler {

    private AbstractAIFUIApplication app = null;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        System.out.println("ImportFormHandler.execute");
        app = AIFUtility.getCurrentApplication();
//		InterfaceAIFComponent com = app.getTargetComponent();
//		String type = com.getType();
//		System.out.println("type == " + type);
//		if (type.equals("EDAComp Revision")) {
//			MessageBox.post("请选择电子料件版本导入", "警号", MessageBox.WARNING);
//			return null;
//		}

        ImportFormAction action = new ImportFormAction(app, null, "");
        new Thread(action).start();

        return null;
    }

}
