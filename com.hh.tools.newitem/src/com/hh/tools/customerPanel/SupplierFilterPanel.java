package com.hh.tools.customerPanel;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.teamcenter.rac.ui.commands.create.bo.NewBOWizard;
import com.teamcenter.rac.util.AbstractCustomPanel;
import com.teamcenter.rac.util.IPageComplete;

public class SupplierFilterPanel extends AbstractCustomPanel implements IPageComplete {
    private Composite composite;
    private Text text;

    public SupplierFilterPanel() {

    }

    public SupplierFilterPanel(Composite parent) {
        super(parent);
    }

    @Override
    public void createPanel() {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        composite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(2, false);
        composite.setLayout(gl);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(gd);
        GridData labelGD = new GridData(GridData.HORIZONTAL_ALIGN_END);
        Label label = toolkit.createLabel(composite, "Object_Name:");
        label.setLayoutData(labelGD);
        GridData typeTextGd = new GridData(GridData.FILL_HORIZONTAL);
        text = toolkit.createText(composite, "");
        text.setText("This is my own panel");
        text.setLayoutData(typeTextGd);
    }

    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public boolean isPageComplete() {
        String txt = text.getText();
        return txt.length() == 0 ? false : true;
    }

    @Override
    public void updatePanel() {
        if (input != null) {
            NewBOWizard wizard = (NewBOWizard) input;
            String msg = "";
            if (wizard.model.getTargetArray() != null) {
                try {
                    msg = wizard.model.getTargetArray()[0].getProperty("object_name").toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                msg = "Nothingisselected";
            }
            text.setText(msg);
        }
    }

    @Override
    public Object getUserInput() {
        return null;
    }
}


