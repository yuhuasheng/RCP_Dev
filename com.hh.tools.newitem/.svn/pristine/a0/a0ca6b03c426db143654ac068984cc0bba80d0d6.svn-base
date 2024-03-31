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
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class ApprovalSheetPropertyBean extends AbstractPropertyBean<Object> {
    protected Button checkButton;
    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    protected static Text approveSheetText;

    public ApprovalSheetPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public ApprovalSheetPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
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
        gridLayout.horizontalSpacing = 5;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);


        approveSheetText = new Text(this.composite, SWT.BORDER | SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 60;
        gridData.widthHint = 132;
        approveSheetText.setLayoutData(gridData);
        approveSheetText.setEditable(false);

        checkButton = new Button(this.composite, SWT.NONE);
        checkButton.setText("Check");
        checkButton.computeSize(80, 25);
        checkButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));


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
                        String approvalSheet = ApprovalSheetPropertyBean.getText();
                        if (!Utils.isNull(approvalSheet)) {
                            Utils.openBroswer(approvalSheet);
                        }
                    }
                });
            }

        };
        checkButton.addListener(SWT.Selection, addlistener);
    }

    public static String getText() {
        String text = "";
        if (approveSheetText != null) {
            text = approveSheetText.getText().trim();
        }
        return text;
    }

    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
        return this.isModified;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
        return this.isModified;
    }

    @Override
    public String getProperty() {
        System.out.println("getFMDProperty");
        System.out.println("isModified == " + isModified);
        System.out.println("tcProperty == " + tcProperty);
//		if (isModified) {
//			doSave();
//		}
        return super.getProperty();
    }

//	private void doSave() {
//		System.out.println("saveFMD");
//		TCComponentDataset fmdDs = null;
//		String fmdPath = getText();
//		TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
//		try {
//			if(tcProperty!=null&&!Utils.isNull(fmdPath)){
//				String fmdFileName = new File(fmdPath).getName();
//				String fmd = fmdFileName.substring(0, fmdFileName.lastIndexOf("."));
//				if(fmdPath.endsWith("xlsx")){
//					fmdDs = CreateObject.createDataSet(session, fmdPath, "MSExcelX", fmd, "excel");	
//				}else if(fmdPath.endsWith("xls")){
//					fmdDs = CreateObject.createDataSet(session, fmdPath, "MSExcel", fmd, "excel");	
//				}				
//				tcProperty.setReferenceValue(fmdDs);				
//			}else if(tcProperty!=null&&Utils.isNull(fmdPath)){
//				tcProperty.setReferenceValue(null);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//	}


    @Override
    public Object getEditableValue() {
        return approveSheetText.getText();
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
        String value = tcProperty.getStringValue();
        approveSheetText.setText(value);
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        tcPropertyDescriptor = paramTCPropertyDescriptor;
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0) approveSheetText.setText(defaultValue);
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
        this.modifiable = paramBoolean;
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

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        if (tcProperty != null) {
            try {
                com.setProperty(property, approveSheetText.getText());
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.dispose();
    }
}
