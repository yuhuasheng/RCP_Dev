package com.hh.tools.importBOM.action;

import com.hh.tools.importBOM.dialog.ImportRsscSkuBOMDialog;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.kernel.TCSession;

/**
 * µº»ÎRSSC SKU BOM
 * 
 * @author Leo
 */
public class ImportRsscSkuBOMAction extends AbstractAIFAction {

    private AbstractAIFApplication app = null;
    private TCSession session = null;

    public ImportRsscSkuBOMAction(AbstractAIFApplication arg0, String arg1) {
        super(arg0, arg1);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
    }

    @Override
    public void run() {
        try {
            new ImportRsscSkuBOMDialog(this.app, this.session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
