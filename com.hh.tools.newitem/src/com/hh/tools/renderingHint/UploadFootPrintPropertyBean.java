package com.hh.tools.renderingHint;

import java.io.File;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.SWT;
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
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class UploadFootPrintPropertyBean extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    protected Button uploadButton;
    protected static Text fileNameText;
    public static String filePathStr = "";

    public UploadFootPrintPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public UploadFootPrintPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
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

        fileNameText = new Text(this.composite, SWT.BORDER | SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 50;
        gridData.widthHint = 158;
        fileNameText.setLayoutData(gridData);
        fileNameText.setEditable(false);

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
                        fileDialog.setFilterExtensions(new String[]{"*.zip*"});

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
        System.out.println("isModified == " + isModified);
        System.out.println("tcProperty == " + tcProperty);
        if (isModified)
            doSave();

        return super.getProperty();
    }

    private void doSave() {
        System.out.println("save");
        try {
            TCComponent tcComponent = null;
            String path = getText();
            if (!Utils.isNull(path) && (path.endsWith("zip"))) {
                String fileName = new File(path).getName();
                String footPrint = fileName.substring(0, fileName.lastIndexOf("."));
                tcComponent = CreateObject.createDataSet(Utils.getTCSession(), path, "FX8_FootPrint", footPrint, "FX8_FootPrint");
                com.add(RelationName.FOOTPRINT, tcComponent);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("load TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {

    }

    @Override
    public void setModifiable(boolean paramBoolean) {
        this.modifiable = paramBoolean;
        this.uploadButton.setEnabled(this.modifiable);
    }

    @Override
    public void setUIFValue(Object arg0) {
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
