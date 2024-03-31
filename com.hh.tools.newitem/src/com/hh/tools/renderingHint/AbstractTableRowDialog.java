package com.hh.tools.renderingHint;


import javax.swing.JButton;
import javax.swing.JPanel;


import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;

import com.teamcenter.rac.kernel.TCComponent;


public abstract class AbstractTableRowDialog extends AbstractAIFDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    protected TCComponent parent = null;
    protected TCComponent tablerowComp = null;

    protected JButton saveButton = null;
    protected JButton closeButton = null;
    protected JButton saveAndCloseButton = null;

    protected JPanel bottomPanel;


    public boolean isClose = false;


    protected boolean editable = false;


    public AbstractTableRowDialog(TCComponent parent, TCComponent comp) {
        super(AIFDesktop.getActiveDesktop());
        this.parent = parent;
        this.tablerowComp = comp;
        initUI();
        initData();
        showDialogAfterInit();
    }

    public AbstractTableRowDialog(TCComponent parent, TCComponent comp, boolean editable) {
        super(AIFDesktop.getActiveDesktop());
        this.parent = parent;
        this.tablerowComp = comp;
        this.editable = editable;
        initUI();
        initData();
        showDialogAfterInit();
    }


    protected abstract void initUI();

    protected abstract void initData();

    protected abstract void showDialogAfterInit();

    public TCComponent getTableRowComponent() {
        return this.tablerowComp;
    }
}
