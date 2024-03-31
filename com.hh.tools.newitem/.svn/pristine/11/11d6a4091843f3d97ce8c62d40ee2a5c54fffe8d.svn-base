package com.hh.tools.importBOM.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hh.tools.importBOM.action.ImportMatrixBOMAction;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

/**
 * µº»ÎRSSC SKU BOM Handler
 * 
 * @author leo
 */
public class ImportMatrixBOMHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractAIFApplication app = AIFUtility.getCurrentApplication();
        ImportMatrixBOMAction action = new ImportMatrixBOMAction(app, null);
        new Thread(action).start();
        return null;
    }
}
