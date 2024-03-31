package com.hh.tools.renderingHint;

import java.io.File;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class UploadTechDataPropertyBean extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected StringBuffer buffer = new StringBuffer();
    protected boolean isModified = false;
    protected Button uploadButton;
    protected static Text fileNameText;

    public UploadTechDataPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public UploadTechDataPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
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


        fileNameText = new Text(this.composite, SWT.BORDER | SWT.LEFT);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 20;
        gridData.widthHint = 320;
        fileNameText.setLayoutData(gridData);
        fileNameText.setEditable(false);

        fileNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                isModified = true;
            }
        });

        uploadButton = new Button(this.composite, SWT.NONE);
        uploadButton.setText("ÉÏ´«");
        uploadButton.computeSize(80, 25);
        uploadButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

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
                        fileNameText.setText("");
                        FileDialog fileDialog = new FileDialog(parentComposite.getShell(), SWT.SINGLE);
                        String dir = getDefaultExportDirectory();
                        File dirFile = null;
                        if (dir == null || dir.trim().equals("")) {
                            dirFile = FileSystemView.getFileSystemView().getHomeDirectory();
                        } else {
                            dirFile = new File(dir);
                        }

                        fileDialog.setFilterPath(dirFile.getPath());
                        fileDialog.setFilterExtensions(new String[]{"*.zip"});

                        fileDialog.open();
                        String fileName = fileDialog.getFileName();
                        String filePath = fileDialog.getFilterPath();

                        if (!Utils.isNull(fileName)) {
                            fileNameText.setText(filePath + "\\" + fileName);
                            isModified = true;
                        }
                    }

                });

            }

        };
        uploadButton.addListener(SWT.Selection, addlistener);
    }

    public static String getText() {
        String text = "";
        if (fileNameText != null) {
            text = fileNameText.getText().trim();
        }
        return text;
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
        if (isModified) {
            doSave();
        }
        return super.getProperty();
    }

    private void doSave() {
        String filePath = getText();
        if (com != null && !Utils.isNull(filePath)) {
            try {
                TCComponent[] ds = com.getRelatedComponents(RelationName.OEMDOC);
                if (ds != null && ds.length > 0) {
                    com.remove(RelationName.OEMDOC, ds);
                }
                TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
                String fileNameStr = new File(filePath).getName();
                String fileName = fileNameStr.substring(0, fileNameStr.lastIndexOf("."));
                TCComponentDataset dataset = CreateObject.createDataSet(session, filePath, "Zip", fileName, "ZIPFILE");
                com.setProperty("fx8_UploadFileName", fileName);
                com.add(RelationName.OEMDOC, dataset);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public Object getEditableValue() {
        return fileNameText.getText();
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
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub

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
