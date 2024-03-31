package com.hh.tools.renderingHint;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class GasketObjectNamePropertyBean extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    protected Button assignButton;
    protected Text objectNameText;

    public GasketObjectNamePropertyBean(Control paramControl) {
        super(paramControl);
    }

    public GasketObjectNamePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                        Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(paramComposite);

    }

    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 5;
        this.composite.setLayout(gridLayout);


        objectNameText = new Text(this.composite, SWT.BORDER | SWT.LEFT);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 20;
        gridData.widthHint = 320;
        objectNameText.setLayoutData(gridData);


        assignButton = new Button(this.composite, SWT.NONE);
        assignButton.setText("÷∏≈…");
        assignButton.computeSize(80, 25);
        assignButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        addListeners(parentComposite);
        setControl(this.composite);


    }

    private void addListeners(final Composite parentComposite) {
        Listener addlistener = new Listener() {

            @Override
            public void handleEvent(Event event) {
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        String objectName = Utils.getGasketName();
                        objectNameText.setText(objectName);
                    }

                });

            }

        };
        assignButton.addListener(SWT.Selection, addlistener);
    }


    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
        return this.isModified;
    }

    @Override
    public String getProperty() {
        System.out.println("getProperty");
        System.out.println("isModified == " + isModified);
        System.out.println("tcProperty == " + tcProperty);
        return super.getProperty();
    }

    @Override
    public Object getEditableValue() {
        return objectNameText.getText();
    }


    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
        System.out.println("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        System.out.println("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty paramTCProperty) throws Exception {

        return paramTCProperty;
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        String value = paramTCProperty.getStringValue();
        objectNameText.setText(value);
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        tcPropertyDescriptor = paramTCPropertyDescriptor;
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (!Utils.isNull(defaultValue)) objectNameText.setText(defaultValue);
    }

    @Override
    public void setModifiable(boolean arg0) {
        modifiable = arg0;
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }

    protected String getDefaultExportDirectory() {
        AbstractAIFSession session = AIFUtility.getCurrentApplication().getSession();
        String s = Utilities.getCookie("filechooser", "DatasetExport.DIR", true);
        if (s == null || s.length() == 0) {
            TCPreferenceService tcpreferenceservice = ((TCSession) session).getPreferenceService();
            s = tcpreferenceservice.getString(0, "defaultExportDirectory");
        }
        if (s != null) {
            s = s.trim();
            if (s.length() == 0) {
                s = null;
            }
        }
        return s;
    }


}
