package com.hh.tools.newitem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.hh.tools.customerPanel.Material;
import com.hh.tools.customerPanel.MaterialPanel;
import com.hh.tools.newitem.wizardoperation.EDACompWizardOperation;
import com.hh.tools.renderingHint.AddCustomerPropertyBean2;
import com.hh.tools.renderingHint.CheckSubSystemPropertyBean;
import com.hh.tools.renderingHint.FactoryLovPropertyBean;
import com.hh.tools.renderingHint.LXDiagObjectNamePropertyBean;
import com.hh.tools.renderingHint.LXPartTypePropertyBean;
import com.hh.tools.renderingHint.MRPGroupLovPropertyBean;
import com.hh.tools.renderingHint.MfgPropertyBean;
import com.hh.tools.renderingHint.PlantLovPropertyBean;
import com.hh.tools.renderingHint.PlatformLovPropertyBean;
import com.hh.tools.renderingHint.ProcTypeLovPropertyBean;
import com.hh.tools.renderingHint.ProgramCustomerLovPropertyBean;
import com.hh.tools.renderingHint.ProgramPhaseLovPropertyBean;
import com.hh.tools.renderingHint.PromotionListPropertyBean;
import com.hh.tools.renderingHint.SecondSourcePropertyBean;
import com.hh.tools.renderingHint.SetGroupAddMaterialPropertyBean2;
import com.hh.tools.renderingHint.SetPlasticPostProcessPropertyBean;
import com.hh.tools.renderingHint.SetPlasticProcessPropertyBean;
import com.hh.tools.renderingHint.SetPostProcessPropertyBean;
import com.hh.tools.renderingHint.SetProcessPropertyBean;
import com.hh.tools.renderingHint.SetTexturePropertyBean;
import com.hh.tools.renderingHint.VendorFilterPropertyBean;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.InterfaceAIFOperationExecutionListener;
import com.teamcenter.rac.aif.common.AIFDataBean;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.cme.application.MFGLegacyApplication;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.kernel.BOCreatePropertyDescriptor;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.commands.create.bo.ITypeChangeSupport;
import com.teamcenter.rac.ui.commands.create.bo.NewBOModel;
import com.teamcenter.rac.ui.commands.create.bo.NewBOOperation;
import com.teamcenter.rac.ui.commands.create.bo.NewBOWizard;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SWTUIUtilities;
import com.teamcenter.rac.util.wizard.extension.ICustomPanelInputProvider;

/**
 * @author Handk
 */
public class CusNewItemWizard extends NewBOWizard implements IAdaptable,
        ITypeChangeSupport {

    private AbstractAIFUIApplication application = null;
    public NewBOOperation newbooperation = null;
    private NewItemConfig config = null;
    private InterfaceAIFComponent targetObject = null;
    private TCComponentItemRevision targetRev = null;
    private TCComponentBOMLine targetBOMLine = null;
    private TCComponentItem targetItem = null;
    private String defaultValue;
    boolean isPlatformCom = false;
    private Map<String, Object> revisionDefaultValue = new HashMap<String, Object>();
    private Map<String, Object> itemDefaultValue = new HashMap<String, Object>();
    private List<String> noPasteTypeList = Arrays.asList("Item");
    private List<String> proceeeTypeList = Arrays.asList("Item");
    protected static List<TCComponent> processList = new ArrayList<TCComponent>();
    protected static List<TCComponent> postProcessList = new ArrayList<TCComponent>();
    protected static List<TCComponent> plasticProcessList = new ArrayList<TCComponent>();
    protected static List<TCComponent> addCustomerList = new ArrayList<TCComponent>();
    protected static List<TCComponent> setGroupMaterialList = new ArrayList<TCComponent>();

    protected static List<TCComponent> plasticPostProcessList = new ArrayList<TCComponent>();
    protected static List<TCComponent> textureList = new ArrayList<TCComponent>();
    protected static List<Material> materialList = new ArrayList<Material>();
    protected static List<TCComponent> vendorList = new ArrayList<>();
    protected static List<TCComponent> promotionList = new ArrayList<>();
    static List<String> programList = new ArrayList<>();
    private TCComponentDataset fmdDs;
    private TCComponentDataset mcdDs;
    private TCComponentDataset mddDs;
    private String projectName = "";
    private String[] programNameArr = null;
    private String[] platformNameArr = null;
    private String customerName = "";
    private String phase = "";
    private String procType = "";
    private String secondSource = "";
    private boolean isSelected = false;
    private TCComponentProject targetProject = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.newitem.newItem");
    private XSSFSheet customerRVWSheet;
    private XSSFSheet sampleRVWSheet;
    private XSSFSheet dgnRVWSheet;
    private XSSFSheet dgnReleasedSheet;
    private static String PERFERENAME = "DRAG_AND_DROP_default_dataset_type";
    List<TCComponentItemRevision> list = new ArrayList<TCComponentItemRevision>();
    TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();

    public CusNewItemWizard(String s) {
        super("com.teamcenter.rac.ui.commands.create.bo.NewBOWizard");
    }

    @Override
    public NewBOModel getBOModel() {
        setInitPropertyValues();
        return super.getBOModel();
    }

    public void setInitPropertyValues() {
        System.out.println("setInitPropertyValues");
        List<ICreateInstanceInput> localList = getCreateInputs();
        if (localList.size() != 0) {
            for (ICreateInstanceInput obj : localList) {
                CreateInstanceInput cii = (CreateInstanceInput) obj;
                setInitPropertyValues(cii);
            }
        }
    }

    private void setInitPropertyValues(CreateInstanceInput instanceInput) {
        String itemType = instanceInput.getCreateDefinition().getTypeName();
        System.out.println("itemType==" + itemType);
        try {
            if (ItemTypeName.MECHANISMREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_Type":
                            tcProperty.setDefaultValue("Assy");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.SHEETMETALREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_Type":
                            tcProperty.setDefaultValue("S");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.PLASTICREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_Type":
                            tcProperty.setDefaultValue("P");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.SCREWREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FixedValue":
                            tcProperty.setDefaultValue("B-Screw");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.STANDOFFREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FixedValue":
                            tcProperty.setDefaultValue("B-Standoff");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.MYLARREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FixedValue":
                            tcProperty.setDefaultValue("B-Mylar");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.LABELREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FixedValue":
                            tcProperty.setDefaultValue("B-Label");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.RUBBERREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FixedValue":
                            tcProperty.setDefaultValue("B-Rubber");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (ItemTypeName.GASKETREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FixedValue":
                            tcProperty.setDefaultValue("B-Gasket");
                            break;
                        default:
                            break;
                    }
                }
            }
//			if(ItemTypeName.RIVETREVISION.equals(itemType)||ItemTypeName.OTHERSREVISION.equals(itemType)){
//				Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();
//
//				for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
//					BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
//					TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
//					String propName = tcProperty.getName();
//					System.out.println("propName=="+propName);
//					switch (propName) {
//					case "fx8_FixedValue":
//						tcProperty.setDefaultValue("B");
//						break;
//					default:
//						break;
//					}
//				}
//			}

            if (ItemTypeName.L5ZZPROCESSREVISION.equals(itemType) || ItemTypeName.L5TZPROCESSREVISION.equals(itemType)
                    || ItemTypeName.L5CYPROCESSREVISION.equals(itemType) || ItemTypeName.L5CXPROCESSREVISION.equals(itemType)
                    || ItemTypeName.L5JYPROCESSREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
//					case "item_revision_id":
//						tcProperty.setDefaultValue("X01");
//						break;
                        case "fx8_HHPN":
                            if (this.getTargetObject() instanceof TCComponentBOMLine) {
                                TCComponentBOMLine bomline = (TCComponentBOMLine) this.getTargetObject();
                                String hhpn = bomline.getItemRevision().getProperty("fx8_HHPN");
                                tcProperty.setDefaultValue(hhpn);
                            }
                            break;
                        case "fx8_OwnerDept":
                            TCComponentUser user = session.getUser();
                            TCComponent component = user.getRelatedComponent("login_group");
                            TCComponentGroup group = (TCComponentGroup) component;
                            String fullName = group.getFullName();
                            tcProperty.setDefaultValue(fullName);
                            break;
                        case "fx8_BusinessUnit":
                            TCComponentUser currentUser = session.getUser();
                            TCComponent currentComponent = currentUser.getRelatedComponent("login_group");
                            TCComponentGroup currentGroup = (TCComponentGroup) currentComponent;
                            String groupFullName = currentGroup.getFullName();
                            if (groupFullName.contains("DT1")) {
                                tcProperty.setDefaultValue("DT1");
                            } else if (groupFullName.contains("DT2")) {
                                tcProperty.setDefaultValue("DT2");
                            }

                        default:
                            break;
                    }
                }
            }

            // HP SCR表单填写
            if (ItemTypeName.HPSCRFORM.equals(itemType)) {
            	System.out.println("SCR表单填写 设置属性");

            	// 选择的目标对象ECO
                if (ItemTypeName.LXECO.equals(this.getTargetObject().getType())) {
                    Map<BOCreatePropertyDescriptor, Object> scrPropMap = instanceInput.getCreateInputs();
                    for (Entry<BOCreatePropertyDescriptor, Object> entry : scrPropMap.entrySet()) {
                        BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                        TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                        String propName = tcProperty.getName();

                        // 1. 设置名称 选择目录的item_id + "-SCR表单"
                        if ("object_name".equals(propName)) {
                            String defaultObjectName = this.getTargetItem().getProperty("item_id") + "-SCRFrom";
                            tcProperty.setDefaultValue(defaultObjectName);
                        }

                        // 2. 设置Company Name 默认为 FOXCONN
                        if ("fx8_CompanyName".equals(propName)) {
                            tcProperty.setDefaultValue("FOXCONN");
                        }
                    }
                }
            }

            if (ItemTypeName.BIOSDGNDOCREVISION.equals(itemType) || ItemTypeName.DRVDGNDOCREVISION.equals(itemType)
                    || ItemTypeName.PKGDGNDOCREVISION.equals(itemType)
                    || ItemTypeName.ELECDGNDOCREVISION.equals(itemType)
                    || ItemTypeName.ELECDGNDOCREVISION.equals(itemType)
                    || ItemTypeName.SWDGNDOCREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FileType":
                            String fileType = this.getDefaultValueForDoc();
                            System.out.println("fileType==" + fileType);
                            tcProperty.setDefaultValue(fileType);
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.PWRDGNDOCREVISION.equals(itemType) || ItemTypeName.TADGNDOCREVISION.equals(itemType)
                    || ItemTypeName.MECHDGNDOCREVISION.equals(itemType) || ItemTypeName.SYSDGNDOCREVISION.equals(itemType)
                    || ItemTypeName.ARTWRKDGNDOCREVISION.equals(itemType) || ItemTypeName.RFDOCREVISION.equals(itemType)
                    || ItemTypeName.EMCTESTDOCREVISION.equals(itemType) || ItemTypeName.ENERGYEFFDOCREVISION.equals(itemType)
                    || ItemTypeName.ENVRCOMPLDOCREVISION.equals(itemType) || ItemTypeName.CERTDOCREVISION.equals(itemType)
                    || ItemTypeName.IDDGNDOCREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_FileType":
                            String fileType = this.getDefaultValueForDoc();
                            System.out.println("fileType==" + fileType);
                            tcProperty.setDefaultValue(fileType);
                            break;
                        case "fx8_PlateformName":
                            if (this.getTargetRev() != null) {
                                String[] programArr = this.getTargetRev().getTCProperty("fx8_ProgramName").getStringArrayValue();
                                String[] platformArr = this.getTargetRev().getTCProperty("fx8_PlateformName").getStringArrayValue();
                                PlatformLovPropertyBean.setChecked(programArr, platformArr);
                                tcProperty.setDefaultValue(platformArr);
                            }
                            break;
                        case "fx8_Customer":
                            String customer = "";
                            if (this.getTargetRev() != null) {
                                customer = this.getTargetRev().getProperty("fx8_Customer");
                                ProgramCustomerLovPropertyBean.setText(customer);
                            }
                            tcProperty.setDefaultValue(customer);
                            break;
                        case "fx8_Phase":
                            String phase = "";
                            if (this.getTargetRev() != null) {
                                phase = this.getTargetRev().getProperty("fx8_Phase");
                                ProgramPhaseLovPropertyBean.setText(phase);
                            }
                            tcProperty.setDefaultValue(phase);
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.APPROVESHEETREVISION.equals(itemType) || ItemTypeName.TESTRPTREVISION.equals(itemType)
                    || ItemTypeName.SSIMULATIONREVISION.equals(itemType) || ItemTypeName.DGNSPECREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();
                if (this.getTargetRev() != null) {
                    for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                        BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                        TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                        String propName = tcProperty.getName();
                        System.out.println("propName==" + propName);
                        switch (propName) {
                            case "fx8_PlateformName":
                                if (this.getTargetRev() != null) {
                                    String[] programArr = this.getTargetRev().getTCProperty("fx8_ProgramName").getStringArrayValue();
                                    String[] platformArr = this.getTargetRev().getTCProperty("fx8_PlateformName").getStringArrayValue();
                                    PlatformLovPropertyBean.setChecked(programArr, platformArr);
                                    tcProperty.setDefaultValue(platformArr);
                                }
                                break;
                            case "fx8_Customer":
                                String customer = this.getTargetRev().getProperty("fx8_Customer");
                                ProgramCustomerLovPropertyBean.setText(customer);
                                tcProperty.setDefaultValue(customer);
                                break;
                            case "fx8_Phase":
                                String phase = this.getTargetRev().getProperty("fx8_Phase");
                                ProgramPhaseLovPropertyBean.setText(phase);
                                tcProperty.setDefaultValue(phase);
                                break;
                            default:
                                break;
                        }
                    }
                }

            } else if (ItemTypeName.L5PARTREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_Plant":
                            PlantLovPropertyBean.setText("DT1");
                            tcProperty.setDefaultValue("DT1");
                            break;
                        case "fx8_Factory":
                            FactoryLovPropertyBean.setText("Assembly");
                            tcProperty.setDefaultValue("Assembly");
                            break;
                        case "fx8_MRPGroup":
                            MRPGroupLovPropertyBean.setText("ZASM");
                            tcProperty.setDefaultValue("ZASM");
                            MRPGroupLovPropertyBean.loadPop();
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.RDDCN.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_ChangeCategory":
                            if (ItemTypeName.APPROVESHEETREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.DGNSPECREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.TESTRPTREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.ESIMULATIONREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.SSIMULATIONREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.MECHDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.PKGDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.SWDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.SYSDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.PWRDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.TADGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.EMCTESTDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.ENERGYEFFDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.ENVRCOMPLDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.RFDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.BIOSDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.DRVDGNDOCREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.ARTWRKDGNDOCREVISION.equals(this.getTargetObject().getType())) {
                                tcProperty.setDefaultValue("Change");
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.PGBGDOCREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_ProdLine":
                            if (ItemTypeName.L5PARTREVISION.equals(this.getTargetObject().getType())) {
                                String productLine = this.getTargetRev().getProperty("fx8_ProdLine");
                                tcProperty.setDefaultValue(productLine);
                            }
                            break;
                        case "fx8_HHPN":
                            if (ItemTypeName.L5PARTREVISION.equals(this.getTargetObject().getType())) {
                                String partNumber = this.getTargetRev().getProperty("fx8_HHPN");
                                tcProperty.setDefaultValue(partNumber);
                            }
                            break;
                        case "fx8_Factory":
                            if (ItemTypeName.L5PARTREVISION.equals(this.getTargetObject().getType())) {
                                String factory = this.getTargetRev().getProperty("fx8_Factory");
                                tcProperty.setDefaultValue(factory);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
//			else if(ItemTypeName.SUBSTITUTEMANAGE.equals(itemType)){
//				Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();
//				for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
//					BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
//					TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
//					String propName = tcProperty.getName();
//					System.out.println("propName=="+propName);
//					switch (propName) {
//					case "object_name":
//						String prjName = "";
//						if(targetObject instanceof TCComponentBOMLine){
//							TCComponentBOMLine bomLine = (TCComponentBOMLine)targetObject;
//							if(ItemTypeName.EDACCABASEREVISION.equals(bomLine.getItemRevision().getType())){
//								prjName = bomLine.getItemRevision().getProperty("fx8_PrjName");
//							}
//						}else if(targetObject instanceof TCComponentItemRevision&&ItemTypeName.EDACCABASEREVISION.equals(targetObject.getType())){
//							prjName = this.getTargetRev().getProperty("fx8_PrjName");
//						}
//						tcProperty.setDefaultValue(prjName);
//						break;
//					default:
//						break;
//					}
//				}
//			}
            else if (ItemTypeName.SUBSTITUTEMANAGEREVISION.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_PlateformName":
                            if (targetObject instanceof TCComponentBOMLine) {
                                TCComponentBOMLine bomLine = (TCComponentBOMLine) targetObject;
                                if (ItemTypeName.EDACCABASEREVISION.equals(bomLine.getItemRevision().getType())) {
                                    String[] programArr = bomLine.getItemRevision().getTCProperty("fx8_ProgramName").getStringArrayValue();
                                    String[] platformArr = bomLine.getItemRevision().getTCProperty("fx8_PlateformName").getStringArrayValue();
                                    PlatformLovPropertyBean.setChecked(programArr, platformArr);
                                    tcProperty.setDefaultValue(platformArr);
                                }
                            } else if (targetObject instanceof TCComponentItemRevision && ItemTypeName.EDACCABASEREVISION.equals(targetObject.getType())) {
                                String[] programArr = this.getTargetRev().getTCProperty("fx8_ProgramName").getStringArrayValue();
                                String[] platformArr = this.getTargetRev().getTCProperty("fx8_PlateformName").getStringArrayValue();
                                PlatformLovPropertyBean.setChecked(programArr, platformArr);
                                tcProperty.setDefaultValue(platformArr);
                            }
                            ;
                            break;
                        case "fx8_Customer":
                            String customer = "";
                            if (targetObject instanceof TCComponentBOMLine) {
                                TCComponentBOMLine bomLine = (TCComponentBOMLine) targetObject;
                                if (ItemTypeName.EDACCABASEREVISION.equals(bomLine.getItemRevision().getType())) {
                                    customer = bomLine.getItemRevision().getProperty("fx8_Customer");
                                }
                            } else if (targetObject instanceof TCComponentItemRevision && ItemTypeName.EDACCABASEREVISION.equals(targetObject.getType())) {
                                customer = this.getTargetRev().getProperty("fx8_Customer");
                            }
                            tcProperty.setDefaultValue(customer);
                            break;
                        case "fx8_Phase":
                            String phase = "";
                            if (targetObject instanceof TCComponentBOMLine) {
                                TCComponentBOMLine bomLine = (TCComponentBOMLine) targetObject;
                                if (ItemTypeName.EDACCABASEREVISION.equals(bomLine.getItemRevision().getType())) {
                                    phase = bomLine.getItemRevision().getProperty("fx8_Phase");
                                }
                            } else if (targetObject instanceof TCComponentItemRevision && ItemTypeName.EDACCABASEREVISION.equals(targetObject.getType())) {
                                phase = this.getTargetRev().getProperty("fx8_Phase");
                            }
                            tcProperty.setDefaultValue(phase);
                            break;
//					case "fx8_PCBCircuitVer":
//						if (ItemTypeName.EDACCABASEREVISION.equals(targetRev.getType())) {
//							String revId = this.getTargetRev().getProperty("item_revision_id");
//							tcProperty.setDefaultValue(revId);
//						}
//						break;
                        case "fx8_ObjectDesc":
                            String objectDesc = "";
                            if (targetObject instanceof TCComponentBOMLine) {
                                TCComponentBOMLine bomLine = (TCComponentBOMLine) targetObject;
                                if (ItemTypeName.EDACCABASEREVISION.equals(bomLine.getItemRevision().getType())) {
                                    objectDesc = bomLine.getItemRevision().getProperty("fx8_ObjectDesc");
                                }
                            } else if (targetObject instanceof TCComponentItemRevision && ItemTypeName.EDACCABASEREVISION.equals(targetObject.getType())) {
                                objectDesc = this.getTargetRev().getProperty("fx8_ObjectDesc");
                            }
                            tcProperty.setDefaultValue(objectDesc);
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.PROCVER.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "object_name":
                            if (ItemTypeName.L5ZZPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5TZPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5CYPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5CXPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5JYPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L6SMTPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L6PTHPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L6TESTPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L6PKGPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L6INPPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXASMPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXFGPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXTESTPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXPKGPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXINPPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXACSPROCDREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.LXSCANPROCDREVISION.equals(this.getTargetObject().getType())) {
                                String objectName = this.getTargetRev().getProperty("item_id") + "_"
                                        + this.getTargetRev().getProperty("object_name") + "_换版更改";
                                tcProperty.setDefaultValue(objectName);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.PROCPAGE.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "object_name":
                            if (ItemTypeName.L5ZZPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5TZPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5CYPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5CXPROCESSREVISION.equals(this.getTargetObject().getType())
                                    || ItemTypeName.L5JYPROCESSREVISION.equals(this.getTargetObject().getType())) {
                                String objectName = this.getTargetRev().getProperty("item_id") + "_"
                                        + this.getTargetRev().getProperty("object_name") + "_换页更改";
                                tcProperty.setDefaultValue(objectName);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.L5ECN.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_Manufacture":
                            if (ItemTypeName.L5ECR.equals(this.getTargetObject().getType())) {
                                String manufacture = this.getTargetObject().getProperty("fx8_Manufacture");
                                tcProperty.setDefaultValue(manufacture);
                            }
                            break;
//					case "fx8_ProdLine":
//						if (ItemTypeName.L5ECR.equals(this.getTargetObject().getType())) {
//							String[] prodLine = this.getTargetItem().getTCProperty("fx8_ProdLine").getStringArrayValue();
//							tcProperty.setDefaultValue(prodLine);
//						}
//						break;
//					case "fx8_PrjName":
//						if (ItemTypeName.L5ECR.equals(this.getTargetObject().getType())) {
//							String prjName = this.getTargetObject().getProperty("fx8_PrjName");
//							tcProperty.setDefaultValue(prjName);
//						}
//						break;
//					case "fx8_ChangeReasonCode":
//						if (ItemTypeName.L5ECR.equals(this.getTargetObject().getType())) {
//							String[] changeReasonCode = this.getTargetItem().getTCProperty("fx8_ChangeReasonCode").getStringArrayValue();
//							tcProperty.setDefaultValue(changeReasonCode);
//						}
//						break;
                        case "fx8_ChangeClass":
                            if (ItemTypeName.L5ECR.equals(this.getTargetObject().getType())) {
                                String changeClass = this.getTargetItem().getProperty("fx8_ChangeClass");
                                tcProperty.setDefaultValue(changeClass);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.L6ECR.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_ChangeClass":
                            if (ItemTypeName.L6ECRREQREVISION.equals(this.getTargetObject().getType())) {
                                String productLine = this.getTargetRev().getProperty("fx8_ChangeClass");
                                tcProperty.setDefaultValue(productLine);
                            }
                            break;
                        case "fx8_IsAffectedRMA":
                            if (ItemTypeName.L6ECRREQREVISION.equals(this.getTargetObject().getType())) {
                                String productLine = this.getTargetRev().getProperty("fx8_IsAffectedRMA");
                                tcProperty.setDefaultValue(productLine);
                            }
                            break;
                        case "fx8_Manufacture":
                            if (ItemTypeName.L6PARTREVISION.equals(this.getTargetObject().getType())) {
                                String manufacture = this.getTargetRev().getProperty("fx8_Manufacture");
                                tcProperty.setDefaultValue(manufacture);
                            }
                            break;
//					case "fx8_PrjName":
//						if (ItemTypeName.L6PARTREVISION.equals(this.getTargetObject().getType())) {
//							String projectName = this.getTargetRev().getProperty("fx8_PrjName");
//							tcProperty.setDefaultValue(projectName);
//						}
//						break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.L6ECN.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_ChangeClass":
                            if (ItemTypeName.L6ECR.equals(this.getTargetObject().getType())) {
                                String changeClass = this.getTargetObject().getProperty("fx8_ChangeClass");
                                tcProperty.setDefaultValue(changeClass);
                            }
                            break;
                        case "fx8_ProdLine":
                            if (ItemTypeName.L6PARTREVISION.equals(this.getTargetObject().getType())) {
                                String prodLine = this.getTargetRev().getProperty("fx8_ProdLine");
                                tcProperty.setDefaultValue(prodLine);
                            }
                            break;
                        case "fx8_CustomerPN":
                            if (ItemTypeName.L6PARTREVISION.equals(this.getTargetObject().getType())) {
                                TCProperty prop = this.getTargetRev().getTCProperty("fx8_CustomerPNTable");
                                TCComponent[] coms = prop.getReferenceValueArray();
                                if (coms != null && coms.length > 0) {
                                    String customerPN = coms[0].getProperty("fx8_CustomerPN");
                                    tcProperty.setDefaultValue(customerPN);
                                }
                            }
                            break;
                        case "fx8_Manufacture":
                            if (ItemTypeName.L6PARTREVISION.equals(this.getTargetObject().getType())) {
                                String manufacture = this.getTargetRev().getProperty("fx8_Manufacture");
                                tcProperty.setDefaultValue(manufacture);
                            }
                            break;
//					case "fx8_PrjName":
//						if (ItemTypeName.L6PARTREVISION.equals(this.getTargetObject().getType())) {
//							String projectName = this.getTargetRev().getProperty("fx8_PrjName");
//							tcProperty.setDefaultValue(projectName);
//						}
//						break;
                        default:
                            break;
                    }
                }
            } else if (ItemTypeName.DTPCASELFPNFORM.equals(itemType)) {
                Map<BOCreatePropertyDescriptor, Object> map = instanceInput.getCreateInputs();

                for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                    BOCreatePropertyDescriptor createPropertyDescriptor = entry.getKey();
                    TCPropertyDescriptor tcProperty = createPropertyDescriptor.getPropertyDescriptor();
                    String propName = tcProperty.getName();
                    System.out.println("propName==" + propName);
                    switch (propName) {
                        case "fx8_STDPN":
                            String hhpn = "";
                            if (ItemTypeName.EDACOMPREVISION.equals(this.getTargetObject().getType())) {
                                hhpn = this.getTargetRev().getProperty("fx8_StandardPN");

                            } else {
                                hhpn = this.getTargetRev().getProperty("fx8_HHPN");
                            }
                            tcProperty.setDefaultValue(hhpn);
                            break;
                        case "fx8_ObjectDesc":
                            String changeClass = this.getTargetRev().getProperty("object_desc");
                            tcProperty.setDefaultValue(changeClass);
                            break;
                        case "fx8_Mfr":
                            if (ItemTypeName.EDACOMPREVISION.equals(this.getTargetObject().getType())) {
                                TCComponent mfrCom = this.getTargetRev().getRelatedComponent("fx8_MfrRel");
                                String mfg = mfrCom == null ? "" : mfrCom.getProperty("object_name");//供应商
                                tcProperty.setDefaultValue(mfg);
                            }
                            break;
                        case "fx8_MfgPN":
                            if (ItemTypeName.EDACOMPREVISION.equals(this.getTargetObject().getType())) {
                                String mfgPN = this.getTargetRev().getProperty("fx8_MfrPN");
                                tcProperty.setDefaultValue(mfgPN);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //创建时进行操作
    @Override
    public boolean canFinish() {
        System.out.println("canFinish == " + super.canFinish());
        return super.canFinish();
    }

    //创建时候进行操作
    @Override
    public boolean performFinish() {
        boolean checkResult = checkAll();
        if (checkResult == false) {
            return false;
        }

        getContainer().updateButtons();
        List<ICreateInstanceInput> localList = getCreateInputs();
        CreateInstanceInput ciiItem = null;
        CreateInstanceInput ciiRevision = null;
        String newItemID = null;
        Map<String, Object> values = new HashMap<String, Object>(16);
        String typeName = "";
        System.out.println("localList==" + localList);
        for (ICreateInstanceInput obj : localList) {
            CreateInstanceInput cii = (CreateInstanceInput) obj;
            typeName = cii.getCreateDefinition().getTypeName();
            if (typeName.equalsIgnoreCase(this.getDefaultType())) {
                ciiItem = cii;
            } else if (typeName
                    .equalsIgnoreCase((this.getDefaultType() + "Revision"))) {
                ciiRevision = cii;
            }
            for (Entry<BOCreatePropertyDescriptor, Object> entry : cii
                    .getCreateInputs().entrySet()) {
                BOCreatePropertyDescriptor key = entry.getKey();
                TCPropertyDescriptor tcpd = key.getPropertyDescriptor();
                if (tcpd == null) {
                    continue;
                }
                Object value = entry.getValue();
                Object valueObject = value == null ? "" : value;
                values.put(tcpd.getName(), valueObject);
            }
        }
        if (ciiRevision != null) {
            for (Entry<String, Object> entry : revisionDefaultValue.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();
                ciiRevision.add(propertyName, propertyValue);
                System.out.println("cii_revision:" + propertyName + "," + propertyValue);
                values.put(propertyName, propertyValue == null ? ""
                        : propertyValue.toString());
            }
//			if(ciiRevision.get("item_revision_id") == null){
//				ciiRevision.add("item_revision_id", "X01");
//				System.out.println("设置初始版本X01");
//			}
        }

        if (ciiItem != null) {
            for (Entry<String, Object> entry : itemDefaultValue.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();

                ciiItem.add(propertyName, propertyValue);
                Utils.print2Console("cii_item:" + propertyName + ","
                        + propertyValue);
                values.put(propertyName, propertyValue == null ? ""
                        : propertyValue.toString());
            }
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            TCComponentGroup group = session.getGroup();
            boolean isDTSA = false;
            try {
                String groupFullName = group.getFullName();
                if (!groupFullName.contains("Monitor.D_Group") && !groupFullName.contains("Printer.D_Group")) {
                    isDTSA = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String defaultType = CusNewItemWizard.this.getDefaultType();
            System.out.println("defaultType ==  " + defaultType);
            Object object_name = ciiItem.get("object_name");
            if (object_name == null || "".equals(object_name)) {
                if (defaultType.equals(ItemTypeName.MECHANISM) && isDTSA) {
                    String objectName = Utils.getMechanismName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.SHEETMETAL) && isDTSA) {
                    String objectName = Utils.getSheetmetalName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.PLASTIC) && isDTSA) {
                    String objectName = Utils.getPlasticName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.SCREW) && isDTSA) {
                    String objectName = Utils.getScrewName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.STANDOFF) && isDTSA) {
                    String objectName = Utils.getStandoffName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.MYLAR) && isDTSA) {
                    String objectName = Utils.getMylarName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.LABEL) && isDTSA) {
                    String objectName = Utils.getLabelName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.RUBBER) && isDTSA) {
                    String objectName = Utils.getRubberName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.GASKET) && isDTSA) {
                    String objectName = Utils.getGasketName();
                    ciiItem.add("object_name", objectName);
                } else if (defaultType.equals(ItemTypeName.PKGDGND)) {
                    String objectName = Utils.getPACName();
                    ciiItem.add("object_name", objectName);

                } else if (defaultType.equals(ItemTypeName.SSIMULATION)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String plateformName = Utils.getPlatformName();
                    String opCondition = "";
                    String rptType = "";

                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_OPCondition".equals(entry.getKey())) {
                            opCondition = entry.getValue().toString();
                        } else if ("fx8_RPTType".equals(entry.getKey())) {
                            rptType = entry.getValue().toString();
                        }
                    }
                    buffer.append(plateformName + " ");
                    buffer.append(opCondition + " ");
                    buffer.append(rptType + "-" + getCurrentDate());
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.ESIMULATION)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String plateformName = Utils.getPlatformName();
                    String chipsetPlatform = "";
                    String PCBLayerCount = "";
                    String prodLine = "";
                    String optional = "";
                    String rptType = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_ChipsetPlatform".equals(entry.getKey())) {
                            chipsetPlatform = entry.getValue().toString();
                        } else if ("fx8_PCBLayerCount".equals(entry.getKey())) {
                            PCBLayerCount = entry.getValue().toString();
                        } else if ("fx8_ProdLine".equals(entry.getKey())) {
                            prodLine = entry.getValue().toString();
                        } else if ("fx8_Optional".equals(entry.getKey())) {
                            optional = entry.getValue().toString();
                        } else if ("fx8_RPTType".equals(entry.getKey())) {
                            rptType = entry.getValue().toString();
                        }
                    }
                    buffer.append(plateformName + " ");
                    buffer.append(chipsetPlatform + " ");
                    buffer.append(PCBLayerCount + " ");
                    buffer.append(prodLine + " ");
                    buffer.append(optional + " ");
                    buffer.append(rptType + "-" + getCurrentDate());
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.DIAG)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String customer = "";
                    String plateformName = Utils.getPlatformName();
                    String revId = "";
                    Object diagOS = "";
                    Object procType = ProcTypeLovPropertyBean.getText();
                    Object prodLineCode = "";
                    Object diagType = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Customer".equals(entry.getKey())) {
                            customer = entry.getValue().toString();
                        } else if ("fx8_DiagOS".equals(entry.getKey())) {
                            String[] diagOSArr = (String[]) entry.getValue();
                            diagOS = toStringFromArray(diagOSArr);
                        } else if ("item_revision_id".equals(entry.getKey())) {
                            revId = entry.getValue().toString();
                        } else if ("fx8_ProdLineCode".equals(entry.getKey())) {
                            String[] prodLineCodeArr = (String[]) entry.getValue();
                            prodLineCode = toStringFromArray(prodLineCodeArr);
                        } else if ("fx8_DiagType".equals(entry.getKey())) {
                            String[] diagTypeArr = (String[]) entry.getValue();
                            diagType = toStringFromArray(diagTypeArr);
                        }
                    }
                    if ("Dell".equalsIgnoreCase(customer)) {
                        buffer.append(plateformName + "_");
                        buffer.append(diagOS + "_");
                        buffer.append(revId + "_");
                        buffer.append(procType);
                        ciiItem.add("object_name", buffer.toString());
                    } else if ("HP".equalsIgnoreCase(customer)) {
                        if ("L6".equalsIgnoreCase(procType.toString())) {
                            ciiItem.add("object_name", plateformName + "_" + "Diag" + "_" + getCurrentDate());
                        } else if ("L10".equalsIgnoreCase(procType.toString())) {
                            buffer.append(prodLineCode + "F");
                            buffer.append(diagType);
                            buffer.append(plateformName);
                            buffer.append(revId);
                            ciiItem.add("object_name", buffer.toString());
                        } else {
                            ciiItem.add("object_name", getDisplayTypeName(defaultType));
                        }
                    } else if ("Lenovo".equalsIgnoreCase(customer)) {
                        if ("L6".equalsIgnoreCase(procType.toString())) {
                            ciiItem.add("object_name", plateformName + "_" + "Diag" + "_" + getCurrentDate());
                        } else {
                            ciiItem.add("object_name", getDisplayTypeName(defaultType));
                        }
                    }
                } else if (defaultType.equals(ItemTypeName.IMAGE)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String customer = "";
                    String plateformName = Utils.getPlatformName();
                    System.out.println("plateformName==" + plateformName);
                    String revId = "";
                    Object geographyCode = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Customer".equals(entry.getKey())) {
                            customer = entry.getValue().toString();
                        } else if ("item_revision_id".equals(entry.getKey())) {
                            revId = entry.getValue().toString();
                        } else if ("fx8_GeographyCode".equals(entry.getKey())) {
                            String[] geographyCodeArr = (String[]) entry.getValue();
                            geographyCode = toStringFromArray(geographyCodeArr);
                        }
                    }
                    if ("Dell".equalsIgnoreCase(customer)) {
                        ciiItem.add("object_name", plateformName);
                    } else if ("HP".equalsIgnoreCase(customer)) {
                        buffer.append("CDT_");
                        buffer.append(revId + "_");
                        buffer.append("MLGM_");
                        buffer.append(geographyCode + "_");
                        buffer.append(plateformName + "_");
                        buffer.append("Desktop");
                        ciiItem.add("object_name", buffer.toString());
                    } else {
                        ciiItem.add("object_name", getDisplayTypeName(defaultType));
                    }
                } else if (defaultType.equals(ItemTypeName.PKGDGNDOC)
                        || defaultType.equals(ItemTypeName.ARTWRKDGNDOC)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String customer = "";
                    String platformName = Utils.getPlatformName();
                    String fileType = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Customer".equals(entry.getKey())) {
                            customer = entry.getValue().toString();
                        } else if ("fx8_FileType".equals(entry.getKey())) {
                            fileType = entry.getValue().toString();
                        }
                    }
                    buffer.append(customer + "_");
                    buffer.append(platformName + "_");
                    buffer.append(fileType);
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.SWDGNDOC)
                        || defaultType.equals(ItemTypeName.BIOSDGNDOC)
                        || defaultType.equals(ItemTypeName.DRVDGNDOC)
                        || defaultType.equals(ItemTypeName.RFDOC)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String customer = "";
                    String prjName = Utils.getPlatformName();
                    String fileType = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Customer".equals(entry.getKey())) {
                            customer = entry.getValue().toString();
                        } else if ("fx8_FileType".equals(entry.getKey())) {
                            fileType = entry.getValue().toString();
                        }
                    }
                    buffer.append(customer + "_");
                    buffer.append(prjName + "_");
                    buffer.append(fileType);
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.ARTWRKDGND)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String prjName = Utils.getPlatformName();
                    String artworkType = "";
                    String phase = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_ArtworkType".equals(entry.getKey())) {
                            artworkType = entry.getValue().toString();
                        } else if ("fx8_Phase".equals(entry.getKey())) {
                            phase = entry.getValue().toString();
                        }
                    }
                    buffer.append(prjName + "_");
                    buffer.append(artworkType + "_");
                    buffer.append(phase);
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.EMCTESTDOC) || defaultType.equals(ItemTypeName.ENERGYEFFDOC)
                        || defaultType.equals(ItemTypeName.ENVRCOMPLDOC)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String customer = "";
                    String prjName = Utils.getPlatformName();
                    String phase = "";
                    String fileType = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Customer".equals(entry.getKey())) {
                            customer = entry.getValue().toString();
                        } else if ("fx8_Phase".equals(entry.getKey())) {
                            phase = entry.getValue().toString();
                        } else if ("fx8_FileType".equals(entry.getKey())) {
                            fileType = entry.getValue().toString();
                        }
                    }
                    buffer.append(customer + "_");
                    buffer.append(prjName + "_");
                    buffer.append(phase + "_");
                    buffer.append(fileType);
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.EDACOMP)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String mfr = "";
                    try {
                        mfr = MfgPropertyBean.selectMfr.getProperty("object_name");
                    } catch (TCException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String mfrPN = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_MfrPN".equals(entry.getKey())) {
                            mfrPN = entry.getValue().toString();
                            break;
                        }
                    }
                    buffer.append(mfr + "_");
                    buffer.append(mfrPN);
                    ciiItem.add("object_name", buffer.toString());
                    ciiItem.add("item_id", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.EDASCHEM)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String prjName = Utils.getPlatformName();
                    String phase = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Phase".equals(entry.getKey())) {
                            phase = entry.getValue().toString();
                        }
                    }
                    buffer.append(prjName + "_");
                    buffer.append(phase + "_" + "SCH" + "_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()));
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.EDACCABASE)) {
                    Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                    StringBuffer buffer = new StringBuffer();
                    String prjName = Utils.getPlatformName();
                    String phase = "";
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        if ("fx8_Phase".equals(entry.getKey())) {
                            phase = entry.getValue().toString();
                        }
                    }
                    buffer.append(prjName + "_");
                    buffer.append(phase + "_" + "PCBA");
                    ciiItem.add("object_name", buffer.toString());
                } else if (defaultType.equals(ItemTypeName.EDAGERBER)) {
                    String prjName = Utils.getPlatformName();
                    ciiItem.add("object_name", prjName + "_" + "Gerber");
                } else {
                    ciiItem.add("object_name", getDisplayTypeName(defaultType));
                }

            } else if (defaultType.equals(ItemTypeName.DIAG)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                StringBuffer buffer = new StringBuffer();
                String customer = "";
                Object procType = ProcTypeLovPropertyBean.getText();
                Object prodLineCode = "";
                Object diagType = "";
                String revId = "";
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_Customer".equals(entry.getKey())) {
                        customer = entry.getValue().toString();
                    } else if ("item_revision_id".equals(entry.getKey())) {
                        revId = entry.getValue().toString();
                    } else if ("fx8_ProdLineCode".equals(entry.getKey())) {
                        String[] prodLineCodeArr = (String[]) entry.getValue();
                        prodLineCode = toStringFromArray(prodLineCodeArr);
                    } else if ("fx8_DiagType".equals(entry.getKey())) {
                        String[] diagTypeArr = (String[]) entry.getValue();
                        diagType = toStringFromArray(diagTypeArr);
                    }
                }
                if ("HP".equalsIgnoreCase(customer) && "L10".equalsIgnoreCase(procType.toString())) {
                    String plateformName = LXDiagObjectNamePropertyBean.objectNameText.getText();
                    buffer.append(prodLineCode + "F");
                    buffer.append(diagType);
                    buffer.append(plateformName);
                    buffer.append(revId);
                    ciiItem.add("object_name", buffer.toString());
                }
            }
        }

        String defaultType = CusNewItemWizard.this.getDefaultType();
        System.out.println("defaultType == " + defaultType);
        if (isPlatformCom) {
            projectName = PlatformLovPropertyBean.getPrjName();
            List<String> programList = PlatformLovPropertyBean.getSelectedProgramList();
            if (programList.size() == 1) {
                programNameArr = new String[]{programList.get(0)};
            }
            platformNameArr = PlatformLovPropertyBean.getSelectedPlatform();
            customerName = ProgramCustomerLovPropertyBean.getText();
            phase = ProgramPhaseLovPropertyBean.getText();


        }
        if (defaultType.equals(ItemTypeName.SHEETMETAL)) {
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            try {
                if (SetProcessPropertyBean.getSelectedList().size() > 0) {
                    for (int i = 0; i < SetProcessPropertyBean.getSelectedList().size(); i++) {
                        TableItem tableItem = SetProcessPropertyBean.processTable.getItem(i);
                        String processStr = tableItem.getText(0);
                        String remarkStr = tableItem.getText(1);
                        TCComponent com = CreateObject.createCom(session, "FX8_ProdProcTable");
                        com.setProperty("fx8_Proc", processStr);
                        com.setProperty("fx8_Remark", remarkStr);
                        processList.add(com);
                    }
                }
                if (SetPostProcessPropertyBean.getSelectedList().size() > 0) {
                    for (int i = 0; i < SetPostProcessPropertyBean.getSelectedList().size(); i++) {
                        TableItem tableItem = SetPostProcessPropertyBean.postProcessTable.getItem(i);
                        String postProcessStr = tableItem.getText(0);
                        String remarkStr = tableItem.getText(1);
                        TCComponent com = CreateObject.createCom(session, "FX8_PostProcTable");
                        com.setProperty("fx8_PostProc", postProcessStr);
                        com.setProperty("fx8_Remark", remarkStr);
                        postProcessList.add(com);
                    }
                }
                if (MaterialPanel.materialList.size() > 0) {
                    TableItem tableItem = MaterialPanel.table.getItem(0);
                    String materialType = tableItem.getText(0);
                    String density = tableItem.getText(1);
                    String materialRemark = tableItem.getText(2);
                    materialList.add(new Material(materialType, density, materialRemark));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (defaultType.equals(ItemTypeName.L5PART) || defaultType.equals(ItemTypeName.LXPART)) {
        	System.out.println("--创建L5PartD关于customerPNtable的后处理--");
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            try {
                int rows = AddCustomerPropertyBean2.table.getItemCount();
                System.out.println("rows ==" + rows);
                for (int i = 0; i < rows; i++) {
                    TableItem tableItem = AddCustomerPropertyBean2.table.getItem(i);
                    String customer = tableItem.getText(1);
                    String customerPN = tableItem.getText(2);
                    String customerPNRev = tableItem.getText(3);
                    System.out.println("customer ==" + customer);
                    TCComponent com = CreateObject.createTable("FX8_CustomerPNTable");
                    System.out.println("com ==" + com);
                    com.setProperty("fx8_Customer", customer);
                    com.setProperty("fx8_CustomerPN", customerPN);
                    com.setProperty("fx8_CustomerPNRev", customerPNRev);
                    addCustomerList.add(com);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
		 * 群组table添加后处理操作 by-汪亚洲
		 */
        else if (defaultType.equals(ItemTypeName.PARTGROUP)) {
        	System.out.println("--群组table添加后处理操作--");
            try {
                int rows = SetGroupAddMaterialPropertyBean2.table.getItemCount();
                System.out.println("rows ==" + rows);
                System.out.println("rows ==" + rows);
                for (int i = 0; i < rows; i++) {
                    TableItem tableItem = SetGroupAddMaterialPropertyBean2.table.getItem(i);
                    String fx8_StandardPN = tableItem.getText(1);
                    String fx8_EDACompObjectName = tableItem.getText(2);
                    String fx8_EDACompItemid = tableItem.getText(3);
                    String fx8_EDACompRevision = tableItem.getText(4);
                    String fx8_EDACompOwnerUser = tableItem.getText(5);
                    System.out.println("fx8_StandardPN ==" + fx8_StandardPN);
                    TCComponent com = CreateObject.createTable("FX8_EDACompGrpTable");
                    System.out.println("com ==" + com);
                    com.setProperty("fx8_StandardPN", fx8_StandardPN);
                    com.setProperty("fx8_EDACompObjectName", fx8_EDACompObjectName);
                    com.setProperty("fx8_EDACompItemid", fx8_EDACompItemid);
                    com.setProperty("fx8_EDACompRevision", fx8_EDACompRevision);
                    com.setProperty("fx8_EDACompOwnerUser", fx8_EDACompOwnerUser);
                    setGroupMaterialList.add(com);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (defaultType.equals(ItemTypeName.PLASTIC)) {
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            try {
                if (SetPlasticProcessPropertyBean.getSelectedList().size() > 0) {
                    for (int i = 0; i < SetPlasticProcessPropertyBean.getSelectedList().size(); i++) {
                        TableItem tableItem = SetPlasticProcessPropertyBean.processTable.getItem(i);
                        String processStr = tableItem.getText(0);
                        String remarkStr = tableItem.getText(1);
                        TCComponent com = CreateObject.createCom(session, "FX8_ProdProcTable");
                        com.setProperty("fx8_Proc", processStr);
                        com.setProperty("fx8_Remark", remarkStr);
                        plasticProcessList.add(com);
                    }
                }
                if (SetPlasticPostProcessPropertyBean.getSelectedList().size() > 0) {
                    for (int i = 0; i < SetPlasticPostProcessPropertyBean.getSelectedList().size(); i++) {
                        TableItem tableItem = SetPlasticPostProcessPropertyBean.postProcessTable.getItem(i);
                        String postProcessStr = tableItem.getText(0);
                        String remarkStr = tableItem.getText(1);
                        TCComponent com = CreateObject.createCom(session, "FX8_PostProcTable");
                        com.setProperty("fx8_PostProc", postProcessStr);
                        com.setProperty("fx8_Remark", remarkStr);
                        plasticPostProcessList.add(com);
                    }
                }
                if (SetTexturePropertyBean.getSelectedList().size() > 0) {
                    for (int i = 0; i < SetTexturePropertyBean.getSelectedList().size(); i++) {
                        TableItem tableItem = SetTexturePropertyBean.textureTable.getItem(i);
                        String postProcessStr = tableItem.getText(0);
                        String remarkStr = tableItem.getText(1);
                        TCComponent com = CreateObject.createCom(session, "FX8_TextureTable");
                        com.setProperty("fx8_Texture", postProcessStr);
                        com.setProperty("fx8_Remark", remarkStr);
                        textureList.add(com);
                    }
                }
//				if(AddOrRemoveMaterialPropertyBean.getMaterialList().size()>0){
//					for (int i = 0; i < AddOrRemoveMaterialPropertyBean.getMaterialList().size(); i++) {
//						TableItem tableItem = AddOrRemoveMaterialPropertyBean.table.getItem(i);
//						String materialType = tableItem.getText(0);
//						String density = tableItem.getText(1);
//						String materialRemark = tableItem.getText(2);
//						TCComponent com = CreateObject.createCom(session, "FX8MaterialTable");
//						com.setProperty("fx8MaterialType", materialType);
//						com.setProperty("fx8Density", density);
//						com.setProperty("fx8MaterialRemark", materialRemark);
//						materialList.add(com);
//					}
//				}
                if (MaterialPanel.materialList.size() > 0) {
                    TableItem tableItem = MaterialPanel.table.getItem(0);
                    String materialType = tableItem.getText(0);
                    String density = tableItem.getText(1);
                    String materialRemark = tableItem.getText(2);
                    materialList.add(new Material(materialType, density, materialRemark));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//		else if(defaultType.equals(ItemTypeName.SCREW)||defaultType.equals(ItemTypeName.STANDOFF)
//				||defaultType.equals(ItemTypeName.GASKET)){
//			projectName = MECHPlatformLovPropertyBean.getPrjName();
//			List<String> programList = MECHPlatformLovPropertyBean.getSelectedProgramList();
//			if(programList.size()==1){
//				programNameArr = new String[]{programList.get(0)};
//			}
//			platformNameArr = MECHPlatformLovPropertyBean.getSelectedPlatform();
//			customerName = MECHCustomerLovPropertyBean.getText();
//			phase = MECHPhaseLovPropertyBean.getText();
//			TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
//			try {
//				if(MaterialPanel.materialList.size()>0){
//					TableItem tableItem = MaterialPanel.table.getItem(0);
//					String materialType = tableItem.getText(0);
//					String density = tableItem.getText(1);
//					String materialRemark = tableItem.getText(2);
//					materialList.add(new Material(materialType, density, materialRemark));
//				}
//				if(SetPlasticProcessPropertyBean.getSelectedList().size()>0){
//					for (int i = 0; i < SetPlasticProcessPropertyBean.getSelectedList().size(); i++) {
//						TableItem tableItem = SetPlasticProcessPropertyBean.processTable.getItem(i);
//						String processStr = tableItem.getText(0);
//						String remarkStr = tableItem.getText(1);
//						TCComponent com = CreateObject.createCom(session, "FX8_ProdProcTable");
//						com.setProperty("fx8_Proc", processStr);
//						com.setProperty("fx8_Remark", remarkStr);
//						plasticProcessList.add(com);
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
        else if (defaultType.equals(ItemTypeName.SCREW) || defaultType.equals(ItemTypeName.STANDOFF)
                || defaultType.equals(ItemTypeName.MYLAR) || defaultType.equals(ItemTypeName.LABEL)
                || defaultType.equals(ItemTypeName.GASKET) || defaultType.equals(ItemTypeName.RUBBER)) {
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            try {
//				if(AddOrRemoveMaterialPropertyBean.getMaterialList().size()>0){
//					for (int i = 0; i < AddOrRemoveMaterialPropertyBean.getMaterialList().size(); i++) {
//						TableItem tableItem = AddOrRemoveMaterialPropertyBean.table.getItem(i);
//						String materialType = tableItem.getText(0);
//						String density = tableItem.getText(1);
//						String materialRemark = tableItem.getText(2);
//						TCComponent com = CreateObject.createCom(session, "FX8MaterialTable");
//						com.setProperty("fx8MaterialType", materialType);
//						com.setProperty("fx8Density", density);
//						com.setProperty("fx8MaterialRemark", materialRemark);
//						materialList.add(com);
//					}
//				}
                if (MaterialPanel.materialList.size() > 0) {
                    TableItem tableItem = MaterialPanel.table.getItem(0);
                    String materialType = tableItem.getText(0);
                    String density = tableItem.getText(1);
                    String materialRemark = tableItem.getText(2);
                    materialList.add(new Material(materialType, density, materialRemark));
                }
                if (SetPlasticProcessPropertyBean.getSelectedList().size() > 0) {
                    for (int i = 0; i < SetPlasticProcessPropertyBean.getSelectedList().size(); i++) {
                        TableItem tableItem = SetPlasticProcessPropertyBean.processTable.getItem(i);
                        String processStr = tableItem.getText(0);
                        String remarkStr = tableItem.getText(1);
                        TCComponent com = CreateObject.createCom(session, "FX8_ProdProcTable");
                        com.setProperty("fx8_Proc", processStr);
                        com.setProperty("fx8_Remark", remarkStr);
                        plasticProcessList.add(com);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (defaultType.equals(ItemTypeName.SUBSTITUTEMANAGE)) {
            isSelected = CheckSubSystemPropertyBean.isSelected();
            System.out.println("isSelected==" + isSelected);
            if (isSelected && null != targetBOMLine) {
                try {
                	System.out.println("选中的替代料管理对象 ==" + targetBOMLine.getItemRevision());
                    final Map<String, String> errorCheckData = Utils.getSubSystemList(targetBOMLine.getItemRevision(),
                            session);
                    System.out.println("验证失败的数据 => " + errorCheckData);
                    if (errorCheckData.size() > 0) {
                        final StringBuffer errorMsgSb = new StringBuffer();
                        for (String displayName : errorCheckData.keySet()) {
                            errorMsgSb.append("EDAComp Object Name Is [" + displayName + "],Sub System Value At Database Not Exist.\n");
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                ErrorDataDialog errorDataDialog;
                                try {
                                    errorDataDialog = new ErrorDataDialog("Create Fail");
                                    errorDataDialog.initUI(errorMsgSb.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                        return false;
                    }

                } catch (TCException e) {
                    e.printStackTrace();
                }
            }

            secondSource = SecondSourcePropertyBean.getValue();
            vendorList = VendorFilterPropertyBean.getSelectedList();
            promotionList = PromotionListPropertyBean.getSelectedList();
            System.out.println("vendorList==" + vendorList.size());
            System.out.println("promotionList==" + promotionList.size());
        } else if (defaultType.equals(ItemTypeName.DIAG)) {
            procType = ProcTypeLovPropertyBean.getText();
        } else if (defaultType.equals(ItemTypeName.L5ZZPROCESS) || defaultType.equals(ItemTypeName.L5TZPROCESS)
                || defaultType.equals(ItemTypeName.L5CYPROCESS) || defaultType.equals(ItemTypeName.L5CXPROCESS)
                || defaultType.equals(ItemTypeName.L5JYPROCESS)) {
            projectName = PlatformLovPropertyBean.getPrjName();
            List<String> programList = PlatformLovPropertyBean.getSelectedProgramList();
            if (programList.size() == 1) {
                programNameArr = new String[]{programList.get(0)};
            }
            platformNameArr = PlatformLovPropertyBean.getSelectedPlatform();
        }
        // 电子料件创建前处理
//		else if(defaultType.equals(ItemTypeName.EDACOMP)){
//			if (!EDACompWizardOperation.getInstance().beforeCreate()) {
//				return false;
//			}
//		}
        else if (defaultType.equals(ItemTypeName.PCBPANEL)) {
            try {
//				if(AddOrRemoveMaterialPropertyBean.getMaterialList().size()>0){
//					for (int i = 0; i < AddOrRemoveMaterialPropertyBean.getMaterialList().size(); i++) {
//						TableItem tableItem = AddOrRemoveMaterialPropertyBean.table.getItem(i);
//						String materialType = tableItem.getText(0);
//						String density = tableItem.getText(1);
//						String materialRemark = tableItem.getText(2);
//						TCComponent com = CreateObject.createCom(session, "FX8MaterialTable");
//						com.setProperty("fx8MaterialType", materialType);
//						com.setProperty("fx8Density", density);
//						com.setProperty("fx8MaterialRemark", materialRemark);
//						materialList.add(com);
//					}
//				}
                if (MaterialPanel.materialList.size() > 0) {
                    TableItem tableItem = MaterialPanel.table.getItem(0);
                    String materialType = tableItem.getText(0);
                    String density = tableItem.getText(1);
                    String materialRemark = tableItem.getText(2);
                    materialList.add(new Material(materialType, density, materialRemark));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
//		else if(defaultType.equals(ItemTypeName.EPIDENTIFICATION)){
//			String fmdPath = UploadFMDPropertyBean.getText();
//			if(!Utils.isNull(fmdPath)){
//				String fmdFileName = new File(fmdPath).getName();
//				fmd = fmdFileName.substring(0, fmdFileName.lastIndexOf("."));
//				if(fmdPath.endsWith("xlsx")){
//					fmdDs = CreateObject.createDataSet(Utils.getTCSession(), fmdPath, "MSExcelX", fmd, "excel");
//				}else if(fmdPath.endsWith("xls")){
//					fmdDs = CreateObject.createDataSet(Utils.getTCSession(), fmdPath, "MSExcel", fmd, "excel");
//				}
//			}
//			String mcdPath = UploadMCDPropertyBean.getText();
//			if(!Utils.isNull(mcdPath)){
//				String mcdFileName = new File(mcdPath).getName();
//				mcd = mcdFileName.substring(0, mcdFileName.lastIndexOf("."));
//				if(mcdPath.endsWith("xlsx")){
//					mcdDs = CreateObject.createDataSet(Utils.getTCSession(), mcdPath, "MSExcelX", mcd, "excel");
//				}else if(fmdPath.endsWith("xls")){
//					mcdDs = CreateObject.createDataSet(Utils.getTCSession(), mcdPath, "MSExcel", mcd, "excel");
//				}
//			}
//			String mddPath = UploadMDDPropertyBean.getText();
//			if(!Utils.isNull(mddPath)){
//				String mddFileName = new File(mddPath).getName();
//				mdd = mddFileName.substring(0, mddFileName.lastIndexOf("."));
//				if(mddPath.endsWith("xlsx")){
//					mddDs = CreateObject.createDataSet(Utils.getTCSession(), mddPath, "MSExcelX", mdd, "excel");
//				}else if(fmdPath.endsWith("xls")){
//					mddDs = CreateObject.createDataSet(Utils.getTCSession(), mddPath, "MSExcel", mdd, "excel");
//				}
//			}
//		}

        if (defaultType.equals(ItemTypeName.L5ECN)) {
            Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                if ("fx8_Manufacture".equals(entry.getKey())) {
                    String manufacture = entry.getValue().toString();
                    String type = "";
                    if ("DT1".equals(manufacture)) {
                        type = defaultType + "_OECN";
                    } else if ("DT2".equals(manufacture)) {
                        type = defaultType + "_TECN";
                    }
                    newItemID = NewItemConfig.getNewID(config, type, values);
                    break;
                }
            }
        } else if (defaultType.equals(ItemTypeName.L6ECN)) {
            Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                if ("fx8_ProductLine".equals(entry.getKey())) {
                    String productLine = entry.getValue().toString();
                    String type = "";
                    if ("DELL".equals(productLine)) {
                        type = defaultType + "_DELL";
                    } else {
                        type = defaultType + "NO_DELL";
                    }
                    newItemID = NewItemConfig.getNewID(config, type, values);
                    break;
                }
            }
        } else if (defaultType.equals(ItemTypeName.L6ECR)) {
            Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                if ("fx8_ProductLine".equals(entry.getKey())) {
                    String productLine = entry.getValue().toString();
                    String type = "";
                    if ("DELL".equals(productLine)) {
                        type = defaultType + "_DELL";
                    } else {
                        type = defaultType + "NO_DELL";
                    }
                    newItemID = NewItemConfig.getNewID(config, type, values);
                    break;
                }
            }
        } else if (defaultType.equals(ItemTypeName.L5CXPROCESS) || defaultType.equals(ItemTypeName.L5CYPROCESS)
                || defaultType.equals(ItemTypeName.L5JYPROCESS) || defaultType.equals(ItemTypeName.L5TZPROCESS)
                || defaultType.equals(ItemTypeName.L5ZZPROCESS)) {
            newItemID = NewItemConfig.getNewID(config, "FX8_L5", values);
            System.out.println("获取L5 item_id == "+newItemID);
        } else if (defaultType.equals(ItemTypeName.L6SMTPROCD) || defaultType.equals(ItemTypeName.L6PTHPROCD)
                || defaultType.equals(ItemTypeName.L6TESTPROCD) || defaultType.equals(ItemTypeName.L6PKGPROCD)
                || defaultType.equals(ItemTypeName.L6INPPROCD)) {
            newItemID = NewItemConfig.getNewID(config, "FX8_L6", values);
            System.out.println("获取L6 item_id == "+newItemID);
        } else if (defaultType.equals(ItemTypeName.LXASMPROCD) || defaultType.equals(ItemTypeName.LXFGPROCD)
                || defaultType.equals(ItemTypeName.LXTESTPROCD) || defaultType.equals(ItemTypeName.LXPKGPROCD)
                || defaultType.equals(ItemTypeName.LXINPPROCD) || defaultType.equals(ItemTypeName.LXACSPROCD)
                || defaultType.equals(ItemTypeName.LXSCANPROCD)) {
            newItemID = NewItemConfig.getNewID(config, "FX8_LX", values);
            System.out.println("获取LX item_id == "+newItemID);
        } else if (!defaultType.equals(ItemTypeName.RDDCN)) {
            newItemID = NewItemConfig.getNewID(config, defaultType, values);
            System.out.println("获取item_id == "+newItemID);
        }
        //获取系统配置下分类属性表单
        String[] searchKeys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser")};
        String[] searchValues = new String[]{"FX8_PropertyForm", "infodba (infodba)"};
        List<InterfaceAIFComponent> propertyFormList = Utils.search("General...", searchKeys, searchValues);
        System.out.println("propertyFormList==" + propertyFormList.size());
        try {
            List<String> list = new ArrayList<>();
            if (propertyFormList.size() > 0) {
                for (InterfaceAIFComponent interfaceAIFComponent : propertyFormList) {
                    TCComponentForm form = (TCComponentForm) interfaceAIFComponent;
                    String type = form.getProperty("fx8_ItemTypeName");
                    if (!list.contains(type)) {
                        list.add(type);
                    }
                }
            }
            boolean flag = false;
            for (String type : list) {
                if (defaultType.equals(type)) {
                    flag = true;
                    break;
                }
            }
            if (defaultType.equals(ItemTypeName.L5ECN)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_Manufacture".equals(entry.getKey())) {
                        String plant = entry.getValue().toString();
                        if ("DT1".equals(plant)) {
                            newItemID = "OECN" + newItemID;
                        } else if ("DT2".equals(plant)) {
                            newItemID = "TECN" + newItemID;
                        }
                        ciiItem.add("item_id", newItemID);
                        break;
                    }
                }
            } else if (defaultType.equals(ItemTypeName.L6ECN)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_ProductLine".equals(entry.getKey())) {
                        String productLine = entry.getValue().toString();
                        if ("DELL".equals(productLine)) {
                            newItemID = "MECN6-" + newItemID;
                        } else {
                            newItemID = "ECN6-" + newItemID;
                        }
                        ciiItem.add("item_id", newItemID);
                        break;
                    }
                }
            } else if (defaultType.equals(ItemTypeName.L6ECR)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_ProductLine".equals(entry.getKey())) {
                        String productLine = entry.getValue().toString();
                        if ("DELL".equals(productLine)) {
                            newItemID = "MECR6-" + newItemID;
                        } else {
                            newItemID = "ECR6-" + newItemID;
                        }
                        ciiItem.add("item_id", newItemID);
                        break;
                    }
                }
            } else if (defaultType.equals(ItemTypeName.L5ZZPROCESS) || defaultType.equals(ItemTypeName.L5TZPROCESS)
                    || defaultType.equals(ItemTypeName.L5CYPROCESS) || defaultType.equals(ItemTypeName.L5CXPROCESS)
                    || defaultType.equals(ItemTypeName.L5JYPROCESS)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                StringBuffer buffer = new StringBuffer();
                String manufactPlace = "";
                String fileType = "";
                String customName = "";
                String prodProcess = "";
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_MfgLocation".equals(entry.getKey())) {
                        manufactPlace = Utils.getRealValue("#MFGLOCATION#", entry.getValue().toString());
                    } else if ("fx8_FileType".equals(entry.getKey())) {
                        fileType = Utils.getRealValue("#MFGFILETYPE#", entry.getValue().toString());
                    } else if ("fx8_Customer".equals(entry.getKey())) {
                        customName = Utils.getRealValue("#L5Customer#", entry.getValue().toString());
                    } else if ("fx8_ProdProcCode".equals(entry.getKey())) {
                        prodProcess = Utils.getRealValue("#L5ProdProc#", entry.getValue().toString());
                    }
                }
                buffer.append(manufactPlace);
                buffer.append(fileType);
                buffer.append("-");
                buffer.append(customName);
                buffer.append(prodProcess);
                buffer.append("-");
                ciiItem.add("item_id", buffer.toString() + newItemID);
            } else if (defaultType.equals(ItemTypeName.L6SMTPROCD) || defaultType.equals(ItemTypeName.L6PTHPROCD)
                    || defaultType.equals(ItemTypeName.L6TESTPROCD) || defaultType.equals(ItemTypeName.L6PKGPROCD)
                    || defaultType.equals(ItemTypeName.L6INPPROCD)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                StringBuffer buffer = new StringBuffer();
                String manufactPlace = "";
                String fileType = "";
                String customName = "";
                String prodProcess = "";
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_MfgLocation".equals(entry.getKey())) {
                        manufactPlace = Utils.getRealValue("#MFGLOCATION#", entry.getValue().toString());
                        System.out.println("manufactPlace==" + manufactPlace);
                    } else if ("fx8_FileType".equals(entry.getKey())) {
                        fileType = Utils.getRealValue("#MFGFILETYPE#", entry.getValue().toString());
                        System.out.println("fileType==" + fileType);
                    } else if ("fx8_Customer".equals(entry.getKey())) {
                        customName = Utils.getRealValue("#L6CUSTOMER#", entry.getValue().toString());
                        System.out.println("customName==" + customName);
                    } else if ("fx8_ProdProcCode".equals(entry.getKey())) {
                        prodProcess = Utils.getRealValue("#L6PRODPROC#", entry.getValue().toString());
                        System.out.println("prodProcess==" + prodProcess);
                    }
                }
                buffer.append(manufactPlace);
                buffer.append(fileType);
                buffer.append("-");
                buffer.append(customName);
                buffer.append(prodProcess);
                buffer.append("-");
                ciiItem.add("item_id", buffer.toString() + newItemID);
            }
            /** L10工艺编码规则 by 汪亚洲*/
            else if (defaultType.equals(ItemTypeName.LXASMPROCD) || defaultType.equals(ItemTypeName.LXFGPROCD)
                    || defaultType.equals(ItemTypeName.LXTESTPROCD) || defaultType.equals(ItemTypeName.LXPKGPROCD)
                    || defaultType.equals(ItemTypeName.LXINPPROCD) || defaultType.equals(ItemTypeName.LXACSPROCD)
                    || defaultType.equals(ItemTypeName.LXSCANPROCD)) {
                Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
                StringBuffer buffer = new StringBuffer();
                String manufactPlace = "";
                String fileType = "";
                String customName = "";
                String prodProcess = "";
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    if ("fx8_MfgLocation".equals(entry.getKey())) {
                        manufactPlace = Utils.getRealValue("#MFGLOCATION#", entry.getValue().toString());
                        System.out.println("manufactPlace==" + manufactPlace);
                    } else if ("fx8_FileType".equals(entry.getKey())) {
                        fileType = Utils.getRealValue("#MFGFILETYPE#", entry.getValue().toString());
                        System.out.println("fileType==" + fileType);
                    } else if ("fx8_Customer".equals(entry.getKey())) {
                        customName = Utils.getRealValue("#LXCUSTOMER#", entry.getValue().toString());
                        System.out.println("customName==" + customName);
                    } else if ("fx8_ProdProcCode".equals(entry.getKey())) {
                        prodProcess = Utils.getRealValue("#LXPRODPROC#", entry.getValue().toString());
                        System.out.println("prodProcess==" + prodProcess);
                    }
                }
                buffer.append(manufactPlace);
                buffer.append(fileType);
                buffer.append("-");
                buffer.append(customName);
                buffer.append(prodProcess);
                buffer.append("-");
                ciiItem.add("item_id", buffer.toString() + newItemID);
            }
            boolean itemIdExist = false;
            Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                if ("item_id".equals(entry.getKey())) {
                    itemIdExist = true;
                    break;
                }
            }
            if (!flag && !Utils.isNull(newItemID) && !itemIdExist) {
                System.out.println("newItemID == " + newItemID);
                ciiItem.add("item_id", newItemID);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



		/*else{
			MessageBox.post(CusNewItemWizard.this.getShell(),"获取编码失败","提示", MessageBox.INFORMATION);
			return false;
		}*/


        performingOperation = true;

        Map<ICustomPanelInputProvider, List<Object>> map = getCustomPanelUserInput();
        newbooperation = getOperationClass();
        if (newbooperation == null) {
            newbooperation = new NewBOOperation(this, localList);
        }

        removePasteTarget();

        this.setWizardId("com.teamcenter.rac.ui.commands.create.bo.NewBOWizard");
        newbooperation.setWizard(this);

        newbooperation.setCreateInput(localList);
        if ((map != null) && (!map.isEmpty())) {
            newbooperation.setCustomPanelUserInput(map);
        }
        AIFDataBean bean = new AIFDataBean();
        IWizardPage[] wizardPages = getPages();
        int j = wizardPages.length;
        for (int i = 0; i < j; i++) {
            IWizardPage wizardPage = wizardPages[i];

            AIFDataBean localAIFDataBean2 = (AIFDataBean) AdapterUtil
                    .getAdapter(wizardPage, AIFDataBean.class);
            if (localAIFDataBean2 != null) {
                bean.setProperty(wizardPage.getName(), localAIFDataBean2);
                System.err.println(localAIFDataBean2);
            }
        }

        newbooperation.setOperatinDataForPages(bean);
        newbooperation.addOperationListener(new IcOperationExecutionListener(
                null));
        newbooperation.setUser(true);
        model.getSession().queueOperation(newbooperation);
        return true;
    }

    //创建前进行必要检查
    private boolean checkAll() {
        try {

            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            TCComponentGroup group = session.getGroup();
            boolean isDTSAGroup = false;
            String groupFullName = group.getFullName();
            if (!groupFullName.contains("Monitor.D_Group") && !groupFullName.contains("Printer.D_Group")) {
                isDTSAGroup = true;
            }
            TCComponentType type = Utils.getTCSession().getTypeComponent(this.getDefaultType());
            if (type.isTypeOf(ItemTypeName.MECHANISM) || type.isTypeOf(ItemTypeName.SHEETMETAL) || type.isTypeOf(ItemTypeName.PLASTIC)
                    || type.isTypeOf(ItemTypeName.SCREW) || type.isTypeOf(ItemTypeName.STANDOFF) || type.isTypeOf(ItemTypeName.MYLAR)
                    || type.isTypeOf(ItemTypeName.LABEL) || type.isTypeOf(ItemTypeName.RUBBER) || type.isTypeOf(ItemTypeName.GASKET)
                    || type.isTypeOf(ItemTypeName.SUBSTITUTEMANAGE) || type.isTypeOf(ItemTypeName.ESIMULATION) || type.isTypeOf(ItemTypeName.SSIMULATION)
                    || type.isTypeOf(ItemTypeName.DIAG) || type.isTypeOf(ItemTypeName.PKGDGND) || type.isTypeOf(ItemTypeName.PSU) || type.isTypeOf(ItemTypeName.EDACOMP)
                    || type.isTypeOf(ItemTypeName.TECOUTDOC) || type.isTypeOf(ItemTypeName.BIOS) || type.isTypeOf(ItemTypeName.IMAGE)
                    || type.isTypeOf(ItemTypeName.DRIVERS) || type.isTypeOf(ItemTypeName.CABLE) || type.isTypeOf(ItemTypeName.TA)
                    || type.isTypeOf(ItemTypeName.SYSDGND) || type.isTypeOf(ItemTypeName.ACST) || type.isTypeOf(ItemTypeName.RIVET)
                    || type.isTypeOf(ItemTypeName.OTHERS) || type.isTypeOf(ItemTypeName.PWRDGNDOC) || type.isTypeOf(ItemTypeName.TADGNDOC)
                    || type.isTypeOf(ItemTypeName.APPROVESHEET) || type.isTypeOf(ItemTypeName.DGNSPEC) || type.isTypeOf(ItemTypeName.TESTRPT)
                    || type.isTypeOf(ItemTypeName.MECHDGNDOC) || type.isTypeOf(ItemTypeName.SWDGNDOC) || type.isTypeOf(ItemTypeName.PKGDGNDOC)
                    || type.isTypeOf(ItemTypeName.RFDGND) || type.isTypeOf(ItemTypeName.RFDOC) || type.isTypeOf(ItemTypeName.ARTWRKDGND)
                    || type.isTypeOf(ItemTypeName.ARTWRKDGNDOC) || type.isTypeOf(ItemTypeName.EMCTESTDOC) || type.isTypeOf(ItemTypeName.ENERGYEFFDOC)
                    || type.isTypeOf(ItemTypeName.ENVRCOMPLDOC) || type.isTypeOf(ItemTypeName.BIOSDGNDOC) || type.isTypeOf(ItemTypeName.DRVDGNDOC)
                    || type.isTypeOf(ItemTypeName.ELECDGNDOC) || type.isTypeOf(ItemTypeName.PCBCIRCUIT) || type.isTypeOf(ItemTypeName.EDASCHEM)
                    || type.isTypeOf(ItemTypeName.EDACCABASE) || type.isTypeOf(ItemTypeName.EDAGERBER) || type.isTypeOf(ItemTypeName.EDMDDGND)
                    || type.isTypeOf(ItemTypeName.PCBPANEL)) {
                isPlatformCom = true;
            }
            if (isPlatformCom || type.isTypeOf(ItemTypeName.L5ZZPROCESS) || type.isTypeOf(ItemTypeName.L5TZPROCESS) || type.isTypeOf(ItemTypeName.L5CYPROCESS)
                    || type.isTypeOf(ItemTypeName.L5CXPROCESS) || type.isTypeOf(ItemTypeName.L5JYPROCESS)) {
                String[] selectedPlatformArr = PlatformLovPropertyBean.getSelectedPlatform();
                if (isDTSAGroup && (selectedPlatformArr == null || selectedPlatformArr.length == 0)) {
                    MessageBox.post(reg.getString("PlatformRequire.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return false;
                }
            }
            if (type.isTypeOf(ItemTypeName.MFR)) {
                String objectName = "";
                ICreateInstanceInput itemInput = getItemCreateInstanceInput();
                if (itemInput != null) {
                    Map<BOCreatePropertyDescriptor, Object> map = itemInput.getCreateInputs();
                    for (Entry<BOCreatePropertyDescriptor, Object> entry : map.entrySet()) {
                        BOCreatePropertyDescriptor key = entry.getKey();
                        Object obj = entry.getValue();
                        String name = key.getPropertyDescriptor().getName();
                        if (obj != null && name.equals("object_name")) {
                            objectName = obj.toString();
                        }
                    }
                }
                String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("Name")};
                String[] values = new String[]{ItemTypeName.MFR, objectName};
                List<InterfaceAIFComponent> supplierList = Utils.search("General...", keys, values);
                if (supplierList != null && supplierList.size() > 0) {
                    MessageBox.post(reg.getString("IsMfgExist.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return false;
                }
            }
            // 电子料件创建前检查
            else if (type.isTypeOf(ItemTypeName.EDACOMP)) {
                Map<String, String> propMap = getCreateInputMap();
                if (!EDACompWizardOperation.getInstance().checkCreateing(propMap)) {
                    return false;
                }
            } else if (type.isTypeOf(ItemTypeName.L6ECRREQ)) {
                String firstECValue = "";
                String pcapmValue = "";
                String eeValue = "";
                String peValue = "";
                String emdpmValue = "";
                Map<String, String> values = getCreateInputMap();
                Iterator<Entry<String, String>> iterator = values.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if ("fx8_FirstEC".equals(entry.getKey())) {
                        firstECValue = entry.getValue();
                    } else if ("fx8_PCAPM".equals(entry.getKey())) {
                        pcapmValue = entry.getValue();
                    } else if ("fx8_EE".equals(entry.getKey())) {
                        eeValue = entry.getValue();
                    } else if ("fx8_PE".equals(entry.getKey())) {
                        peValue = entry.getValue();
                    } else if ("fx8_EMDPM".equals(entry.getKey())) {
                        emdpmValue = entry.getValue();
                    }
                }
                if ("Yes".equals(firstECValue)) {
                    if (Utils.isNull(pcapmValue)) {
                        MessageBox.post(reg.getString("PCAMPRequire.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    } else if (Utils.isNull(eeValue)) {
                        MessageBox.post(reg.getString("EERequire.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    } else if (Utils.isNull(peValue)) {
                        MessageBox.post(reg.getString("PERequire.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    } else if (Utils.isNull(emdpmValue)) {
                        MessageBox.post(reg.getString("EMDPMRequire.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    }
                }
            } else if (type.isTypeOf(ItemTypeName.L5ECN)) {
                Map<String, String> values = getCreateInputMap();
                Iterator<Entry<String, String>> iterator = values.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if ("fx8_Manufacture".equals(entry.getKey())) {
                        String manufacture = entry.getValue();
                        if (Utils.isNull(manufacture)) {
                            MessageBox.post(reg.getString("ManufacturedByRequire.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        }
                    }
                }
            }
            if (targetBOMLine != null) {
                TCAccessControlService accessControlService = session.getTCAccessControlService();
                boolean isWrite = false;
                try {
                    isWrite = accessControlService.checkUsersPrivilege(session.getUser(), (TCComponent) targetBOMLine, TCAccessControlService.WRITE);
                } catch (TCException e1) {
                    e1.printStackTrace();
                }
                if (!isWrite) {
                    MessageBox.post(reg.getString("NoWrightPrivilege.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }


    private void removePasteTarget() {
        TCComponentFolder defaultFolder = null;
        try {
            defaultFolder = Utils.getTCSession().getUser().getNewStuffFolder();
        } catch (Exception e) {
            defaultFolder = null;
        }
        if (defaultFolder == null) {
            return;
        }
        InterfaceAIFComponent[] aifcs = this.getBOModel().getTargetArray();
        if (aifcs != null && aifcs.length > 0) {

            InterfaceAIFComponent aifc = aifcs[0];
            String type = aifc.getType();

            if (aifc instanceof TCComponentFolder) {
                return;
            }
            if (aifc instanceof TCComponentPseudoFolder) {
                return;
            }

         	// 处理菜单的目标对象，某些对象类型，不粘贴

            try {
                TCComponentItemType itemType = (TCComponentItemType) Utils
                        .getTCSession().getTypeComponent(this.getDefaultType());
                String[] temp = (String[]) noPasteTypeList
                        .toArray(new String[noPasteTypeList.size()]);
                boolean isType = itemType.isTypeOf(temp);
                if (isType) {
                    this.getBOModel().setTargetArray(null);
                    this.getBOModel().setTargetArray(
                            new InterfaceAIFComponent[]{defaultFolder});
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (proceeeTypeList.contains(this.getDefaultType())) {
                String bvrProcessType = "Mfg0BvrProcess";
                if (bvrProcessType.equalsIgnoreCase(type)) {
                    return;
                }
                this.getBOModel().setTargetArray(null);
                this.getBOModel().setTargetArray(
                        new InterfaceAIFComponent[]{defaultFolder});
                return;
            }
        } else {
            this.getBOModel().setTargetArray(
                    new InterfaceAIFComponent[]{defaultFolder});
        }
    }

    public void setRevisionDefaultValue(String propertyName,
                                        Object propertyValue) {
        revisionDefaultValue.put(propertyName, propertyValue);
    }

    public Object getRevisionDefaultValue(String propertyName) {
        if (!revisionDefaultValue.containsKey(propertyName)) {
            return null;
        }
        return revisionDefaultValue.get(propertyName);
    }

    public void setItemDefaultValue(String propertyName, Object propertyValue) {
        itemDefaultValue.put(propertyName, propertyValue);
    }

    public NewItemConfig getConfig() {
        return config;
    }

    public void setConfig(NewItemConfig config) {
        this.config = config;
    }

    public AbstractAIFUIApplication getApplication() {
        return application;
    }

    public void setApplication(AbstractAIFUIApplication application) {
        this.application = application;
    }


    public String getDefaultValueForDoc() {
        return defaultValue;
    }

    public void setDefaultValueForDoc(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public TCComponentItemRevision getTargetRev() {
        return targetRev;
    }

    public void setTargetRev(TCComponentItemRevision targetRev) {
        this.targetRev = targetRev;
    }


    public TCComponentProject getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(TCComponentProject targetProject) {
        this.targetProject = targetProject;
    }

    public TCComponentItem getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(TCComponentItem targetItem) {
        this.targetItem = targetItem;
    }

    public void setTargetBOMLine(TCComponentBOMLine targetBOMLine) {
        this.targetBOMLine = targetBOMLine;
    }

    public TCComponentBOMLine getTargetBOMLine() {
        return targetBOMLine;
    }

    public InterfaceAIFComponent getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(InterfaceAIFComponent targetObject) {
        this.targetObject = targetObject;
    }

    private class IcOperationExecutionListener implements
            InterfaceAIFOperationExecutionListener {

        @Override
        public void exceptionThrown(final Exception ex) {
            if (ex != null) {
                performingOperation = false;
                SWTUIUtilities.asyncExec(new Runnable() {

                    @Override
                    public void run() {
//						MessageBox.post(getShell(), ex, false);
                        if (getContainer() != null) {
                            getContainer().updateButtons();
                        }
                    }
                });
            }
        }

        @Override
        public void startOperation(String s) {

        }

        //创建完对象后进行操作
        @Override
        public void endOperation() {
            performingOperation = false;
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    IWizardContainer iwizardcontainer = getContainer();
                    if (iwizardcontainer != null) {
                        iwizardcontainer.updateButtons();
                    }
                }
            });
            postSuccessfulFinish();

            if (CusNewItemWizard.this.newbooperation == null) {
                return;
            }
            String defaultType = CusNewItemWizard.this.getDefaultType();
            System.out.println("defaultType ==  " + defaultType);
            TCComponent newComp = CusNewItemWizard.this.newbooperation
                    .getNewComponent();
            if (newComp != null) {
            	Utils.print2Console("创建的对象：" + newComp.toString());
            } else {
            	Utils.print2Console("没有找到新创建的对象!");
            }

            // 添加数据集
            NewItemConfig config = CusNewItemWizard.this.getConfig();
            if (config == null) {
            	Utils.print2Console("没有找到Config!");
            } else {
            	Utils.print2Console("找到了Config!");
            }
            if (newComp == null) {
                return;
            }
            TCComponentItemRevision rev = null;
            TCComponentForm form = null;
            try {
                if (newComp instanceof TCComponentItem) {
                    rev = ((TCComponentItem) newComp).getLatestItemRevision();
                } else if (newComp instanceof TCComponentItemRevision) {
                    rev = (TCComponentItemRevision) newComp;
                } else if (newComp instanceof TCComponentForm) {
                    form = (TCComponentForm) newComp;
                }
                if (isPlatformCom) {
                	//指派到项目
                    if (!Utils.isNull(projectName)) {
                        String[] keys = new String[]{Utils.getTextValue("ProjectName")};
                        String[] values = new String[]{projectName};
                        List<InterfaceAIFComponent> projectList = Utils.search("__FX_FindProject", keys, values);
                        if (projectList != null && projectList.size() > 0) {
                            TCComponentProject project = (TCComponentProject) projectList.get(0);
                            project.assignToProject(new TCComponent[]{newComp});
                        }
                    }
                    if (!Utils.isNull(customerName)) {
                        rev.setProperty("fx8_Customer", customerName);
                    }
                    if (!Utils.isNull(phase)) {
                        rev.setProperty("fx8_Phase", phase);
                    }
                    if (programNameArr != null) {
                        rev.getTCProperty("fx8_ProgramName").setStringValueArray(programNameArr);
                    }
                    if (platformNameArr != null) {
                        rev.getTCProperty("fx8_PlateformName").setStringValueArray(platformNameArr);
                    }
                }
                if (defaultType.equals(ItemTypeName.MECHANISM) || defaultType.equals(ItemTypeName.SHEETMETAL) || defaultType.equals(ItemTypeName.PLASTIC)) {

                    if (defaultType.equals(ItemTypeName.MECHANISM)) {
                        rev.setProperty("fx8_Type", "Assy");
                        String component = rev.getProperty("fx8_Component");
                        if ("chassis".equals(component.toLowerCase())) {
                        	//自动带出checklist
                            TCComponentForm dgnRVWForm = CreateObject.createTempForm(session, "FX8_DgnRVWForm", "3D&2D Drawing Review Checklist", true);
                            TCComponentForm dgnReleasedForm = CreateObject.createTempForm(session, "FX8_DgnReleasedForm", "3D Drawing Release checklist", true);
                            TCComponentForm customerRVWForm = CreateObject.createTempForm(session, "FX8_CustomerRVWForm", "Design Review Checklist", true);
                            TCComponentForm sampleRVWForm = CreateObject.createTempForm(session, "FX8_SampleRVWForm", "Sample Review Checklist", true);
                            dgnReleasedForm.save();
                            sampleRVWForm.save();
                            customerRVWForm.save();
                            dgnRVWForm.save();
                            initialTable(rev, dgnReleasedForm, sampleRVWForm, customerRVWForm, dgnRVWForm);
                            rev.add(RelationName.CHECKLIST, dgnReleasedForm);
                            rev.add(RelationName.CHECKLIST, sampleRVWForm);
                            rev.add(RelationName.CHECKLIST, customerRVWForm);
                            rev.add(RelationName.CHECKLIST, dgnRVWForm);
                        }
                    } else if (defaultType.equals(ItemTypeName.SHEETMETAL)) {
                        System.out.println("materialList==" + materialList);
                        if (processList.size() > 0) {
                            TCProperty pro = rev.getTCProperty("fx8_ProdProc");
                            if (processList.size() > 0) {
                                pro.setPropertyArrayData(processList.toArray());
                                rev.setTCProperty(pro);
                                processList.clear();
                            }
                        }
                        if (postProcessList.size() > 0) {
                            TCProperty pro = rev.getTCProperty("fx8_PostProc");
                            if (postProcessList.size() > 0) {
                                pro.setPropertyArrayData(postProcessList.toArray());
                                rev.setTCProperty(pro);
                                postProcessList.clear();
                            }
                        }
                        if (materialList.size() > 0) {
                            rev.setProperty("fx8_MaterialType", materialList.get(0).getMaterialType());
                            rev.setProperty("fx8_Density", materialList.get(0).getDensity());
                            rev.setProperty("fx8_MaterialRemark", materialList.get(0).getMaterialRemark());
                            materialList.clear();
                        }
                        rev.setProperty("fx8_Type", "S");
                    } else if (defaultType.equals(ItemTypeName.PLASTIC)) {
                        if (plasticProcessList.size() > 0) {
                            TCProperty pro = rev.getTCProperty("fx8_ProdProc");
                            if (plasticProcessList.size() > 0) {
                                pro.setPropertyArrayData(plasticProcessList.toArray());
                                rev.setTCProperty(pro);
                                plasticProcessList.clear();
                            }
                        }
                        if (plasticPostProcessList.size() > 0) {
                            TCProperty pro = rev.getTCProperty("fx8_PostProc");
                            if (plasticPostProcessList.size() > 0) {
                                pro.setPropertyArrayData(plasticPostProcessList.toArray());
                                rev.setTCProperty(pro);
                                plasticPostProcessList.clear();
                            }
                        }
                        if (textureList.size() > 0) {
                            TCProperty pro = rev.getTCProperty("fx8_Texture");
                            if (textureList.size() > 0) {
                                pro.setPropertyArrayData(textureList.toArray());
                                rev.setTCProperty(pro);
                                textureList.clear();
                            }
                        }
                        if (materialList.size() > 0) {
                            rev.setProperty("fx8_MaterialType", materialList.get(0).getMaterialType());
                            rev.setProperty("fx8_Density", materialList.get(0).getDensity());
                            rev.setProperty("fx8_MaterialRemark", materialList.get(0).getMaterialRemark());
                            materialList.clear();
                        }
                        rev.setProperty("fx8_Type", "P");
                    }
                    //机构件创建完成后自动带出模板
                    get3DModel(rev, defaultType);
                }
                if (form != null && defaultType.equals(ItemTypeName.EPIDENTIFICATION)) {
                    if (fmdDs != null) {
                        form.setReferenceProperty("fx8_FMD", fmdDs);
                    }
                    if (mcdDs != null) {
                        form.setReferenceProperty("fx8_MCD", mcdDs);
                    }
                    if (mddDs != null) {
                        form.setReferenceProperty("fx8_MDD", mddDs);
                    }

                } else if (form != null && defaultType.equals(ItemTypeName.DTPCASELFPNFORM)) {
                    form.setProperty("object_name", form.getProperty("fx8_HHPN") + " " + reg.getString("SelfEditForm.MSG"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // HP SCR信息填写完后的处理
            if (null != form && ItemTypeName.HPSCRFORM.equals(defaultType)) {
            	System.out.println("HP SCR信息填写完后处理操作 => " + form);
				System.out.println("选择的目标对象 => " + targetItem);

				// 选择的目标对象ECO
                if (ItemTypeName.LXECO.equals(targetItem.getType())) {

                	// 1. 如果存在SCR表单对象 则移除
                    try {
                        Utils.setByPass(true, session);
                        TCComponent[] refComp = targetItem.getRelatedComponents(RelationName.RELATION_REFERENCE);
                        if (null != refComp && refComp.length > 0) {
                            for (int i = 0; i < refComp.length; i++) {
                                TCComponent itemRefComp = refComp[i];
                                // 引用类型是SCRFrom
                                if (ItemTypeName.HPSCRFORM.equals(itemRefComp.getType())) {
                                    targetItem.remove(RelationName.RELATION_REFERENCE, itemRefComp);
                                }
                            }
                        }

                        // 2. 程序会默认添加
                        targetItem.add(RelationName.RELATION_REFERENCE, form);
                        Utils.setByPass(false, session);

                    } catch (TCException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (rev == null) {
                Utils.print2Console("Rev is NULL!");
                return;
            }


            //创建完对象后在此可以进行后操作
            //***************************

            for (Entry<String, Object> entry : CusNewItemWizard.this.revisionDefaultValue
                    .entrySet()) {
                String pn = entry.getKey();
                Object pv = entry.getValue();
                String v = pv == null ? "" : pv.toString();
                try {
                    rev.setProperty(pn, v);
                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
            // 根据配置，复制数据集模板；
            if (config != null) {
                List<TCComponentDataset> datasetList = config.getDatasetList();
                System.out.println("datasetList==" + datasetList);
                try {
                    if (datasetList.size() > 0) {
                        for (TCComponentDataset dataset : datasetList) {
                            System.out.println("dataset==" + dataset);
                            TCComponentDataset newdataset = Utils.copyDatasetWithNewName(dataset, rev.getProperty("item_id"),
                                    rev.getProperty("object_name"));
                            if (newdataset == null) {
                            	Utils.infoMessage("从模板创建数据集失败!");
                            } else {
                                rev.add(config.getDatasetRelation(), newdataset);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//			NewItemConfig.copyNewDataset(rev, config);

            try {
//				if(defaultType.equals(ItemTypeName.PGBGDOC)){
//					InterfaceAIFComponent[] targetObjects = AIFUtility.getCurrentApplication().getTargetComponents();
//					if(targetObjects!=null&&targetObjects.length>0){
//						TCComponent targetRev = (TCComponent)targetObjects[0];
//						if (ItemTypeName.PBOMPARTREVISION.equals(targetRev.getType())) {
//							rev.setProperty("object_name", targetRev.getProperty("fx8_CN_MatDesc") + "(" +targetRev.getProperty("fx8_HHPN") + ")????±¨??");
//						}
//					}
//				}

            	// LXPart 创建完后 设置Material Group值
                if (ItemTypeName.LXPART.equals(defaultType)) {
                    if (null != rev) {
                    	// Material Group值 根据 Part Type值 去找映射关系
                        String partTypeValue = rev.getProperty("fx8_PartType");
                        if (null != partTypeValue && !"".equals(partTypeValue)) {

                            if (!LXPartTypePropertyBean.partTypeMGMappingDataLoadFlag) {
                                LXPartTypePropertyBean.waitMGMappingDataLaodOver();
                            }

                            String materialGroupValue = LXPartTypePropertyBean.partTypeMGMappingDataMap.get(partTypeValue);
                            rev.setProperty("fx8_MaterialGroup", materialGroupValue);
                        }
                    }
                }

                if (defaultType.equals(ItemTypeName.PPAPCONTEXT)) {
//					rev.setProperty("object_name", "PPAP数据包_"+rev.getProperty("object_name"));
					//遍历BOM	
                    list.clear();
                    list = recursionBOMLine(targetBOMLine);
                    Utils.setByPass(true, session);
                    System.out.println("遍历BOM数量=="+list.size());
                    List<TCComponentItemRevision> PPAPDocList = new ArrayList<TCComponentItemRevision>();
                    for (int i = 0; i < list.size(); i++) {
                        TCComponentItemRevision itemRev = list.get(i);
                        TCComponent com = itemRev.getRelatedComponent(RelationName.PPAP);
                        if (com == null) {
                            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
                            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(ItemTypeName.PPAPDOC);
                            String itemName = itemRev.getProperty("fx8_HHPN") + "_PPAP";
                            TCComponentItem PPAPDocItem = CreateObject.createItem(session, itemType.getNewID(), itemName, "A", ItemTypeName.PPAPDOC);
                            itemRev.add(RelationName.PPAP, PPAPDocItem.getLatestItemRevision());
                            PPAPDocList.add(PPAPDocItem.getLatestItemRevision());
                        } else if (com instanceof TCComponentItemRevision && ItemTypeName.PPAPDOCREVISION.equals(com.getType()) && !PPAPDocList.contains(com)) {
                            PPAPDocList.add((TCComponentItemRevision) com);
                        }
                    }
                    rev.add(RelationName.PPAP, PPAPDocList);
                    Utils.setByPass(false, session);
                    MessageBox.post(rev.getProperty("object_name") + reg.getString("HasCreated.MSG"),
                            reg.getString("Info.MSG"), MessageBox.INFORMATION);
                } else if (defaultType.equals(ItemTypeName.L5ECN)) {
//					rev.setDateProperty("fx8_EffectiveDate", new Date());
                    InterfaceAIFComponent[] targetObjects = AIFUtility.getCurrentApplication().getTargetComponents();
                    if (targetObjects != null && targetObjects.length > 0) {
                        TCComponent target = (TCComponent) targetObjects[0];
                        if (ItemTypeName.L5ECR.equals(target.getType())) {
                        	//ECN关联ECR
                            rev.getItem().add(RelationName.ECR, target);
                            //ECN关联改前数据
                            TCComponent[] coms = target.getRelatedComponents(RelationName.PROBLEMPART);
                            if (coms != null && coms.length > 0) {
                                rev.getItem().add(RelationName.CHANGEBEFORE, coms);
                            }
                            //ECR关联ECN
                            Utils.byPass(true);
                            target.add(RelationName.ECN, rev.getItem());
                            Utils.byPass(false);
                        } else if (ItemTypeName.L5PARTREVISION.equals(targetRev.getType())) {
                            rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                        }
                    }

                } else if (defaultType.equals(ItemTypeName.PROCVER)) {
                    InterfaceAIFComponent[] targetObjects = AIFUtility.getCurrentApplication().getTargetComponents();
                    if (targetObjects != null && targetObjects.length > 0) {
                        Utils.byPass(true);
                        TCComponentItemRevision targetRev = (TCComponentItemRevision) targetObjects[0];
                        if (ItemTypeName.L5ZZPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5TZPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5CYPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5CXPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5JYPROCESSREVISION.equals(targetRev.getType())) {
                            rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                            String itemRevId = targetRev.getProperty("item_revision_id");
                            TCComponentItemRevision newProcessRev = null;
                            if (itemRevId.toUpperCase().startsWith("X")) {
                                int newRevisionId = Integer.valueOf(itemRevId.substring(1, itemRevId.length())) + 1;
                                String newRevisionIdStr = "X" + String.format("%02d", newRevisionId);
                                newProcessRev = targetRev.saveAs(newRevisionIdStr);
                            } else {
                                char newRevisionId = (char) (Integer.valueOf(itemRevId.substring(0, 1).toCharArray()[0]) + 1);
                                newProcessRev = targetRev.saveAs(newRevisionId + "01");
                            }
                            if (newProcessRev != null) {
                                Utils.removePDFFromRev(newProcessRev, session, "PDF_Reference");
                                rev.getItem().add(RelationName.CHANGEAFTER, newProcessRev);
                                newProcessRev.add(RelationName.CHANGELIST, rev.getItem());
                                Utils.setItem2Home(rev.getItem());
                                MessageBox.post(rev.getProperty("object_name") + reg.getString("HasCreated.MSG"),
                                        reg.getString("Info.MSG"), MessageBox.INFORMATION);
                            }
                        } else if (ItemTypeName.L6SMTPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.L6PTHPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.L6TESTPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.L6PKGPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.L6INPPROCDREVISION.equals(targetRev.getType())) {
                            rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                            TCComponentItemRevision newItemRev = targetRev.saveAs("");
                            rev.getItem().add(RelationName.CHANGEAFTER, newItemRev);
                            Utils.setItem2Home(rev.getItem());
                            MessageBox.post(rev.getProperty("object_name") + reg.getString("HasCreated.MSG"),
                                    reg.getString("Info.MSG"), MessageBox.INFORMATION);
                        }
                        /** L10换版更改 - by 汪亚洲*/
                        else if (ItemTypeName.LXASMPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.LXFGPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.LXTESTPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.LXPKGPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.LXINPPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.LXACSPROCDREVISION.equals(targetRev.getType())
                                || ItemTypeName.LXSCANPROCDREVISION.equals(targetRev.getType())) {
                            rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                            TCComponentItemRevision newItemRev = targetRev.saveAs("");
                            rev.getItem().add(RelationName.CHANGEAFTER, newItemRev);
                            Utils.setItem2Home(rev.getItem());
                            MessageBox.post(rev.getProperty("object_name") + reg.getString("HasCreated.MSG"),
                                    reg.getString("Info.MSG"), MessageBox.INFORMATION);
                        }
                        Utils.byPass(false);
                    }
                } else if (defaultType.equals(ItemTypeName.PROCPAGE)) {
                    InterfaceAIFComponent[] targetObjects = AIFUtility.getCurrentApplication().getTargetComponents();
                    if (targetObjects != null && targetObjects.length > 0) {
                        Utils.byPass(true);
                        TCComponentItemRevision targetRev = (TCComponentItemRevision) targetObjects[0];
                        if (ItemTypeName.L5ZZPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5TZPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5CYPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5CXPROCESSREVISION.equals(targetRev.getType())
                                || ItemTypeName.L5JYPROCESSREVISION.equals(targetRev.getType())) {
                            rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                            String itemRevId = targetRev.getProperty("item_revision_id");
                            int newRevisionId = Integer.valueOf(itemRevId.substring(1, itemRevId.length())) + 1;
                            String newRevisionIdStr = itemRevId.substring(0, 1) + String.format("%02d", newRevisionId);
                            TCComponentItemRevision newProcessRev = targetRev.saveAs(newRevisionIdStr);

                            Utils.removePDFFromRev(newProcessRev, session, "PDF_Reference");
                            rev.getItem().add(RelationName.CHANGEAFTER, newProcessRev);
                            newProcessRev.add(RelationName.CHANGELIST, rev.getItem());
                            Utils.setItem2Home(rev.getItem());
                            MessageBox.post(rev.getProperty("object_name") + reg.getString("HasCreated.MSG"),
                                    reg.getString("Info.MSG"), MessageBox.INFORMATION);

                        }
                        Utils.byPass(false);
                    }
                } else if (defaultType.equals(ItemTypeName.L6ECN)) {
                    InterfaceAIFComponent[] targetObjects = AIFUtility.getCurrentApplication().getTargetComponents();
                    if (targetObjects != null && targetObjects.length > 0) {
                        TCComponent target = (TCComponent) targetObjects[0];
                        if (ItemTypeName.L6ECR.equals(target.getType())) {
                        	//ECN关联ECR
                            rev.getItem().add(RelationName.ECR, target);
                            //ECN关联改前数据
                            TCComponent[] coms = target.getRelatedComponents(RelationName.PROBLEMPART);
                            if (coms != null && coms.length > 0) {
                                rev.getItem().add(RelationName.CHANGEBEFORE, coms);
                            }
                        } else if (ItemTypeName.L6PARTREVISION.equals(target.getType())) {
                            rev.getItem().add(RelationName.CHANGEAFTER, target);
                        }
                    }
                } else if (defaultType.equals(ItemTypeName.LXECR)) {
                    InterfaceAIFComponent[] targetObjects = AIFUtility.getCurrentApplication().getTargetComponents();
                    if (targetObjects != null && targetObjects.length > 0) {
                        TCComponent target = (TCComponent) targetObjects[0];
                        if (ItemTypeName.LXECO.equals(target.getType())) {
                        	//ECR关联ECO
                            rev.getItem().add(RelationName.ECR, target);
                        }
                    }
                } else if (defaultType.equals(ItemTypeName.RDDCN)) {
                    if (targetRev != null) rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                    if (config != null) {
                        String itemType = config.getItemTypeName();
                        System.out.println("itemType==" + itemType);
                        if (ItemTypeName.APPROVESHEET.equals(itemType)) {
                            TCComponentItemRevision[] coms = targetRev.getItem().getReleasedItemRevisions();
                            if (coms != null && coms.length > 0) {
                            	//cable design的上一发布版本		
                                Map<String, TCComponentItemRevision> map = new HashMap<>();
                                Map<String, TCComponentItemRevision> map1 = new TreeMap<>();
                                Map<String, TCComponentItemRevision> map2 = new TreeMap<>();
                                for (TCComponentItemRevision tcComponentItemRevision : coms) {
                                    String revisionId = tcComponentItemRevision.getProperty("item_revision_id");
                                    if (revisionId.startsWith("X")) {
                                        map1.put(revisionId, tcComponentItemRevision);
                                    } else {
                                        map2.put(revisionId, tcComponentItemRevision);
                                    }
                                }
                                map.putAll(map1);
                                map.putAll(map2);
                                Iterator<Entry<String, TCComponentItemRevision>> iterator = map.entrySet().iterator();
                                List<TCComponentItemRevision> list = new ArrayList<>();
                                while (iterator.hasNext()) {
                                    Entry<String, TCComponentItemRevision> entry = iterator.next();
                                    TCComponentItemRevision itemRev = entry.getValue();
                                    list.add(itemRev);
                                }
                                List<TCComponentItemRevision> approveList = getRelatedComponents(targetRev, RelationName.APPROVEREL, ItemTypeName.APPROVESHEETREVISION, list);
                                Utils.setByPass(true, session);
                                rev.getItem().add(RelationName.CHANGEBEFORE, approveList);
                                for (TCComponentItemRevision itemRev : approveList) {
                                    TCComponentItemRevision newItemRev = itemRev.getItem().getLatestItemRevision();
                                    targetRev.add(RelationName.APPROVEREL, newItemRev);
                                }
                                Utils.setByPass(false, session);
                                return;

                            }
                        } else if (ItemTypeName.TESTRPT.equals(itemType)) {
                            TCComponentItemRevision[] coms = targetRev.getItem().getReleasedItemRevisions();
                            if (coms != null && coms.length > 0) {
                            	//cable design的上一发布版本	
                                Map<String, TCComponentItemRevision> map = new HashMap<>();
                                Map<String, TCComponentItemRevision> map1 = new TreeMap<>();
                                Map<String, TCComponentItemRevision> map2 = new TreeMap<>();
                                for (TCComponentItemRevision tcComponentItemRevision : coms) {
                                    String revisionId = tcComponentItemRevision.getProperty("item_revision_id");
                                    if (revisionId.startsWith("X")) {
                                        map1.put(revisionId, tcComponentItemRevision);
                                    } else {
                                        map2.put(revisionId, tcComponentItemRevision);
                                    }
                                }
                                map.putAll(map1);
                                map.putAll(map2);
                                Iterator<Entry<String, TCComponentItemRevision>> iterator = map.entrySet().iterator();
                                List<TCComponentItemRevision> list = new ArrayList<>();
                                while (iterator.hasNext()) {
                                    Entry<String, TCComponentItemRevision> entry = iterator.next();
                                    TCComponentItemRevision itemRev = entry.getValue();
                                    list.add(itemRev);
                                }
                                List<TCComponentItemRevision> testList = getRelatedComponents(targetRev, RelationName.TESTREPORTREL, ItemTypeName.TESTRPTREVISION, list);
                                Utils.setByPass(true, session);
                                rev.getItem().add(RelationName.CHANGEBEFORE, testList);
                                for (TCComponentItemRevision testItemRev : testList) {
                                    TCComponentItemRevision newItemRev = testItemRev.getItem().getLatestItemRevision();
                                    targetRev.add(RelationName.TESTREPORTREL, newItemRev);
                                }


                                Utils.setByPass(false, session);
                                return;

                            }
                        }
//						else if(ItemTypeName.SSIMULATION.equals(itemType)){
//							TCComponentItemRevision[] coms = targetRev.getItem().getReleasedItemRevisions();
//							if(coms!=null&&coms.length>0){
//								//cable design的上一发布版本			
//								Map<String,TCComponentItemRevision> map = new HashMap<>();
//								Map<String,TCComponentItemRevision> map1 = new TreeMap<>();
//								Map<String,TCComponentItemRevision> map2 = new TreeMap<>();
//								for (TCComponentItemRevision tcComponentItemRevision : coms) {
//									String revisionId = tcComponentItemRevision.getProperty("item_revision_id");
//									if(revisionId.startsWith("X")){
//										map1.put(revisionId, tcComponentItemRevision);
//									}else{
//										map2.put(revisionId, tcComponentItemRevision);
//									}
//								}
//								map.putAll(map1);
//								map.putAll(map2);
//								Iterator<Entry<String, TCComponentItemRevision>> iterator = map.entrySet().iterator();
//								List<TCComponentItemRevision> list = new ArrayList<>();
//								while(iterator.hasNext()){
//									Entry<String, TCComponentItemRevision> entry = iterator.next();
//									TCComponentItemRevision itemRev = entry.getValue();
//									list.add(itemRev);
//								}
//								TCComponentItemRevision ssItemRev = getRelatedComponent(targetRev,RelationName.SIMULATIONREPORTREL,ItemTypeName.SSIMULATIONREVISION,list);
//								Utils.setByPass(true, session);
//								rev.getItem().add(RelationName.CHANGEBEFORE, ssItemRev);
//								TCComponentItemRevision newItemRev = ssItemRev.getItem().getLatestItemRevision();
//								targetRev.add(RelationName.SIMULATIONREPORTREL, newItemRev);
//								Utils.setByPass(false, session);
//								return;
//							}
//						}

                    } else if (ItemTypeName.MECHANISMREVISION.equals(targetRev.getType()) || ItemTypeName.SHEETMETALREVISION.equals(targetRev.getType())
                            || ItemTypeName.PLASTICREVISION.equals(targetRev.getType())) {
                        String itemId = new GetPreferenceUtil().getStringPreference(session, TCPreferenceService.TC_preference_site, "FX_Mech_DCN_Template");
                        TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("Item");
                        TCComponentItem item = itemType.find(itemId);
                        TCComponent component = item.getLatestItemRevision().getRelatedComponent("IMAN_specification");
                        if (component != null && component instanceof TCComponentDataset) {
                            TCComponentDataset dataset = (TCComponentDataset) component;
                            TCComponentDataset newdataset = Utils.copyDatasetWithNewName(dataset, rev.getProperty("item_id"),
                                    rev.getProperty("object_name"));
                            rev.getItem().add("IMAN_Rendering", newdataset);
                        }
                    } else if (ItemTypeName.EDACOMPREVISION.equals(targetRev.getType())) {
                        TCComponent newItemRev = rev.getItem().getRelatedComponent(RelationName.CHANGEAFTER);
                        TCComponentICO[] icos = targetRev.getClassificationObjects();
                        if (icos != null && icos.length > 0) {
                            TCClassificationService icsServer = session
                                    .getClassificationService();
                            ICSApplicationObject icsAppObj = icsServer
                                    .newICSApplicationObject("ICM");
                            ICSView icsView = icsAppObj.getView();

                            for (TCComponentICO tcComponentICO : icos) {
                                tcComponentICO.delete();

                                TCClassificationService ics = session
                                        .getClassificationService();
                                ICSApplicationObject icsap = ics.newICSApplicationObject("ICM");
                                String wso_ics_id = ics.getTCComponentId(newItemRev);
                                String wso_uid = newItemRev.getUid();
                                icsap.create(wso_ics_id, wso_uid);

                                String classId = tcComponentICO.getProperty("object_type_id");
                                System.out.println("classId == " + classId);

                                icsAppObj.setView(classId);

                                ICSProperty[] icsProperties = tcComponentICO.getICSProperties(true);
                                int valueId = 0;
                                String value = "";
                                String className = "";
                                String refOptions = "";

                                ArrayList<ICSProperty> propList = new ArrayList<ICSProperty>();
                                ICSProperty icsProperty = null;
                                for (int i = 0; i < icsProperties.length; i++) {
                                    valueId = icsProperties[i].getId();
                                    value = icsProperties[i].getValue();

                                    ICSPropertyDescription icsPropertyDescription = icsView.getPropertyDescription(valueId);
                                    className = icsPropertyDescription.getName();

                                    refOptions = icsPropertyDescription.getRefOptions();

                                    System.out.println("value == " + value);
                                    System.out.println("valueId == " + valueId);
                                    System.out.println("className == " + className);
                                    System.out.println("refOptions == " + refOptions);
                                    if ("".equals(refOptions)) {
                                        icsProperty = new ICSProperty(valueId, value);
                                        propList.add(icsProperty);

                                    }
                                }

                                icsap.setView("Base", classId);
                                if (propList.size() != 0) {
                                    icsap.setProperties(propList.toArray(new ICSProperty[propList.size()]));
                                }
                                icsap.save();


                            }
                        }
                    }


                } else if (defaultType.equals(ItemTypeName.RDDCR)) {
                    if (targetRev != null) {
                        rev.getItem().add(RelationName.CHANGEBEFORE, targetRev);
                    }
                }
//				else if(defaultType.equals(ItemTypeName.ECR)){
//					TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
//					TCComponentDataset newDs = CreateObject.createDataSet(session, prZipPath, "Zip", "问题", "ZIPFILE");
//					new File(prZipPath).delete();
//					rev.add(RelationName.PR, newDs);
//				}

                else if (defaultType.equals(ItemTypeName.L5PART) || defaultType.equals(ItemTypeName.LXPART)) {
                    System.out.println("addCustomerList ==" + addCustomerList.size());
                    if (addCustomerList.size() > 0) {
                        TCProperty pro = rev.getTCProperty("fx8_CustomerPNTable");
                        if (addCustomerList.size() > 0) {
                            pro.setPropertyArrayData(addCustomerList.toArray());
                            rev.setTCProperty(pro);
                            addCustomerList.clear();
                        }
                    }
                } else if (defaultType.equals(ItemTypeName.PARTGROUP)) {
                    System.out.println("setGroupMaterialList ==" + setGroupMaterialList.size());
                    if (setGroupMaterialList.size() > 0) {
                        TCProperty pro = rev.getTCProperty("fx8_EDACompGrpTable");
                        if (setGroupMaterialList.size() > 0) {
                            pro.setPropertyArrayData(setGroupMaterialList.toArray());
                            rev.setTCProperty(pro);
                            setGroupMaterialList.clear();
                        }
                    }
                } else if (defaultType.equals(ItemTypeName.SCREW) || defaultType.equals(ItemTypeName.STANDOFF)
                        || defaultType.equals(ItemTypeName.MYLAR) || defaultType.equals(ItemTypeName.LABEL)
                        || defaultType.equals(ItemTypeName.RUBBER) || defaultType.equals(ItemTypeName.GASKET)) {

                    if (materialList.size() > 0) {
                        rev.setProperty("fx8_MaterialType", materialList.get(0).getMaterialType());
                        rev.setProperty("fx8_Density", materialList.get(0).getDensity());
                        rev.setProperty("fx8_MaterialRemark", materialList.get(0).getMaterialRemark());
                        materialList.clear();
                    }
                    if (plasticProcessList.size() > 0) {
                        TCProperty pro = rev.getTCProperty("fx8_ProdProc");
                        if (plasticProcessList.size() > 0) {
                            pro.setPropertyArrayData(plasticProcessList.toArray());
                            rev.setTCProperty(pro);
                            plasticProcessList.clear();
                        }
                    }
                    rev.setProperty("fx8_FixedValue", "B");
                    //机构件创建完成后自动带出模板
                    get3DModel(rev, defaultType);
                } else if (defaultType.equals(ItemTypeName.SUBSTITUTEMANAGE)) {
                    System.out.println("secondSource==" + secondSource);
                    if (!Utils.isNull(secondSource)) rev.setProperty("fx8_SecondSource", secondSource);
                    if (vendorList.size() > 0) {
                        TCProperty pro = rev.getTCProperty("fx8_MfrPNCommodityCodeTable");
                        if (vendorList.size() > 0) {
                            pro.setPropertyArrayData(vendorList.toArray());
                            rev.setTCProperty(pro);
                            vendorList.clear();
                        }
                    }
                    if (promotionList.size() > 0) {
                        TCProperty pro = rev.getTCProperty("fx8_PromotionListTable");
                        if (promotionList.size() > 0) {
                            pro.setPropertyArrayData(promotionList.toArray());
                            rev.setTCProperty(pro);
                            promotionList.clear();
                        }
                    }
                    //如果PCBA已有BOM结构，则重新搭建
                    TCComponentBOMLine bomLine = null;
                    try {
                        if (targetObject instanceof TCComponentBOMLine) {
                            bomLine = (TCComponentBOMLine) targetObject;
                        } else if (targetObject instanceof TCComponentItemRevision) {
                            TCComponentItemRevision itemRev = (TCComponentItemRevision) targetObject;
                            bomLine = Utils.createBOMLine(session, itemRev);
                        }
                        AIFComponentContext[] context = bomLine.getChildren();
                        if (context.length > 0) {
                            TCComponentBOMLine topBOMLine = Utils.createBOMLine(session, rev);
//							topBOMLine.add(bomLine.getItemRevision().getItem(),bomLine.getItemRevision(),null,false);
                            for (int i = 0; i < context.length; i++) {
                                AIFComponentContext aifComponentContext = context[i];
                                TCComponentBOMLine childBOMLine = (TCComponentBOMLine) aifComponentContext.getComponent();
                                System.out.println("childBOMLine==" + childBOMLine);
                                String bl_ref_designator = childBOMLine.getProperty("bl_ref_designator");
//								TCComponentItemRevision childItemRev = childBOMLine.getItemRevision();
//								topBOMLine.add(childItemRev.getItem(),childItemRev,null,false);
                                TCComponentBOMLine newBomline = topBOMLine.add(childBOMLine, false);
                                newBomline.setProperty("bl_ref_designator", bl_ref_designator);
                                System.out.println("搭建BOM");
                            }
                            topBOMLine.save();
                            topBOMLine.window().save();
//							String s = "com.teamcenter.rac.pse.PSEPerspective";
//				            Activator.getDefault().openPerspective(s);
//				            Activator.getDefault().openComponents(s, new InterfaceAIFComponent[] { rev });
                            Utils.setItem2Home(rev.getItem());
                            MessageBox.post(rev.getProperty("object_name") + reg.getString("HasCreated.MSG"),
                                    reg.getString("Info.MSG"), MessageBox.INFORMATION);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (defaultType.equals(ItemTypeName.ESIMULATION) || defaultType.equals(ItemTypeName.SSIMULATION)) {
                    rev.setDateProperty("fx8_CompletionDate", new Date());
                } else if (defaultType.equals(ItemTypeName.DIAG)) {
                    if (procType != null) {
                        rev.getTCProperty("fx8_ProcType").setStringValueArray(new String[]{procType});
                    }
                } else if (defaultType.equals(ItemTypeName.L5ZZPROCESS) || defaultType.equals(ItemTypeName.L5TZPROCESS)
                        || defaultType.equals(ItemTypeName.L5CYPROCESS) || defaultType.equals(ItemTypeName.L5CXPROCESS)) {
                    if (!Utils.isNull(projectName)) {
                        String[] keys = new String[]{Utils.getTextValue("ProjectName")};
                        String[] values = new String[]{projectName};
                        List<InterfaceAIFComponent> projectList = Utils.search("__FX_FindProject", keys, values);
                        if (projectList != null && projectList.size() > 0) {
                            TCComponentProject project = (TCComponentProject) projectList.get(0);
                            project.assignToProject(new TCComponent[]{newComp});
                        }
                    }
                    if (programNameArr != null) {
                        rev.getTCProperty("fx8_ProgramName").setStringValueArray(programNameArr);
                    }
                    if (platformNameArr != null) {
                        rev.getTCProperty("fx8_PlateformName").setStringValueArray(platformNameArr);
                    }
                } else if (defaultType.equals(ItemTypeName.L5JYPROCESS)) {
                    if (!Utils.isNull(projectName)) {
                        String[] keys = new String[]{Utils.getTextValue("ProjectName")};
                        String[] values = new String[]{projectName};
                        List<InterfaceAIFComponent> projectList = Utils.search("__FX_FindProject", keys, values);
                        if (projectList != null && projectList.size() > 0) {
                            TCComponentProject project = (TCComponentProject) projectList.get(0);
                            project.assignToProject(new TCComponent[]{newComp});
                        }
                    }
                    if (programNameArr != null) {
                        rev.getTCProperty("fx8_ProgramName").setStringValueArray(programNameArr);
                    }
                    if (platformNameArr != null) {
                        rev.getTCProperty("fx8_PlateformName").setStringValueArray(platformNameArr);
                    }
                    HashMap map = new GetPreferenceUtil().getHashMapPreference(session, TCPreferenceService.TC_preference_site, "FX8_JYProcess_Dataset", "=");
                    String checkTypeStr = rev.getProperty("fx8_CheckType");
                    Iterator<Entry<String, String>> it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, String> entry = it.next();
                        String type = entry.getKey();
                        if (checkTypeStr.equals(type)) {
                            String datasetName = entry.getValue();
                            TCComponent component = Utils.getTemplateComponent(session, "工艺模板", datasetName, "MSExcelX");
                            if (component != null) {
                                rev.add(IRelationName.IMAN_specification, component);
                            }
                        }
                    }
                }
                // 电子料件创建完成后操作
                else if (defaultType.equals(ItemTypeName.EDACOMP)) {
                    EDACompWizardOperation.getInstance().afterCreate(session, rev);
                } else if (defaultType.equals(ItemTypeName.PCBPANEL)) {
                    if (materialList.size() > 0) {
                        rev.setProperty("fx8_MaterialType", materialList.get(0).getMaterialType());
                        rev.setProperty("fx8_Density", materialList.get(0).getDensity());
                        rev.setProperty("fx8_MaterialRemark", materialList.get(0).getMaterialRemark());
                        materialList.clear();
                    }
                }
                if (application == null) {
                    application = AIFUtility.getCurrentApplication();
                }
                Utils.byPass(true);
            } catch (TCException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            TCComponentItemRevision targetRev = CusNewItemWizard.this.getTargetRev();
            TCComponentProject targetProject = CusNewItemWizard.this.getTargetProject();
            System.out.println("targetObject == " + targetObject);
            //暂时这样处理
//			if(defaultType.equals(ItemTypeName.QUOTEREQ)){
//				TCComponent component = Utils.getTemplateComponent(session, "Excel模板", "报价申请", "MSExcelX");
//				if(component!=null){
//					try {
//						rev.add(IRelationName.IMAN_specification, component);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//			}
            if (targetRev != null) {
                if (config != null) {
                	String[] currentRelates = config.getCurrentRelate();   //创建对象与选中对象的关联关系
					String[] mapProps = config.getMapProps();
					String[] targetRelates = config.getTargetRelate();   //选中对象与创建对象的关联关系
                    InterfaceAIFComponent[] targetObjects = config.getTargetObjects();

                    if (targetObjects != null && targetObjects.length > 1) {
                        for (int i = 0; i < targetObjects.length; i++) {
                            if (targetObjects[i] instanceof TCComponentItemRevision) {
                            	// 1.选择对象关联
								System.out.println("正在给选择对象关联创建对象中...");		
                                Utils.connectRelate(targetRelates, (TCComponentItemRevision) targetObjects[i], rev);
                                System.out.println("END");

                                // 2.创建对象关联
								System.out.println("正在给创建对象关联选择对象中...");
                                Utils.connectRelate(currentRelates, rev, (TCComponentItemRevision) targetObjects[i]);
                                System.out.println("END");

                                // 3.属性映射
								System.out.println("正在进行属性映射中...");
                                Utils.mapProp(mapProps, (TCComponentItemRevision) targetObjects[i], rev, targetProject);
                                System.out.println("END");
                            }
                        }
                    } else {
                    	// 1.选择对象关联
						System.out.println("正在给选择对象关联创建对象中...");
						Utils.connectRelate(targetRelates, targetRev, rev);
						System.out.println("END");

						// 2.创建对象关联
						System.out.println("正在给创建对象关联选择对象中...");
						Utils.connectRelate(currentRelates, rev, targetRev);
						System.out.println("END");

						// 3.属性映射
						System.out.println("正在进行属性映射中...");
						Utils.mapProp(mapProps, targetRev, rev,targetProject);
						System.out.println("END");
                    }

                }

            }

            System.out.println("targetBOMLine == " + targetBOMLine);
            if (targetBOMLine != null && !(defaultType.equals(ItemTypeName.PPAPCONTEXT)
                    || defaultType.equals(ItemTypeName.SUBSTITUTEMANAGE))) {
                try {
                    targetBOMLine.add(rev.getItem(), rev, null, false, "");
                    targetBOMLine.window().save();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
                if (app instanceof AbstractPSEApplication) {
                    AbstractPSEApplication pseapp = (AbstractPSEApplication) app;
                    try {
                        System.out.println("pseapp == " + pseapp);
                        System.out.println("pseapp.getBOMWindow() == " + pseapp.getBOMWindow());
                        if (pseapp.getBOMWindow() == null) {
                            pseapp.initialize(rev);
                        }

                        //pseapp.getBOMWindow().fireChangeEvent();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (app instanceof MFGLegacyApplication) {
                    MFGLegacyApplication mpp = (MFGLegacyApplication) app;
                    try {
                        System.out.println("mpp.getBOMWindow() ==" + mpp.getBOMWindow());
                        if (mpp.getBOMWindow() == null) {
                            mpp.open(rev);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            try {
                Utils.byPass(false);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (config != null) config.closeDB();
        }

        private List<TCComponentItemRevision> getRelatedComponents(
                TCComponentItemRevision targetRev, String approverel, String itemRevType,
                List<TCComponentItemRevision> list) {
            List<TCComponentItemRevision> resultList = new ArrayList<>();
            try {
                List<TCComponentItemRevision> newList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    TCComponentItemRevision itemRev = list.get(i);
                    newList.add(itemRev);
                    if (targetRev.equals(itemRev)) {
                        break;
                    }
                }
                for (int i = 0; i < newList.size(); i++) {
                    TCComponentItemRevision itemRev = newList.get(i);
                    if (!targetRev.equals(itemRev)) {
                        TCComponent[] components = itemRev.getRelatedComponents(approverel);
                        if (components != null && components.length > 0) {
                            for (TCComponent tcComponent : components) {
                                if (itemRevType.equals(tcComponent.getType())) {
                                    resultList.add((TCComponentItemRevision) tcComponent);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultList;
        }

        //初始化checklist 检查项信息
        private void initialTable(TCComponentItemRevision itemRev, TCComponentForm dgnReleasedForm, TCComponentForm sampleRVWForm, TCComponentForm customerRVWForm, TCComponentForm dgnRVWForm) {
            try {
                String itemId = new GetPreferenceUtil().getStringPreference(session, TCPreferenceService.TC_preference_site, "FX_MECH_Checklist_Template");
                TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("Item");
                TCComponentItem item = itemType.find(itemId);
                TCComponent component = item.getLatestItemRevision().getRelatedComponent("IMAN_specification");
                if (component != null && component instanceof TCComponentDataset) {
                    TCComponentDataset dataset = (TCComponentDataset) component;
                    String filePath = DownloadDataset.downloadFile(dataset, true);
                    Workbook book = Utils.getWorkbook(new File(filePath));
                    customerRVWSheet = (XSSFSheet) book.getSheetAt(0);
                    sampleRVWSheet = (XSSFSheet) book.getSheetAt(1);
                    dgnRVWSheet = (XSSFSheet) book.getSheetAt(2);
                    dgnReleasedSheet = (XSSFSheet) book.getSheetAt(3);
                    //FX8_CustomerRVWForm
                    String customer = itemRev.getProperty("fx8_Customer");
                    if (!Utils.isNull(customer)) {
                        customer = "For " + customer;
                        List<TCComponent> customerRVWList = initCustomerTableRow(customerRVWSheet, customer);
                        TCProperty customerRVWProp = customerRVWForm.getTCProperty("fx8_CustomerRVWTable");
                        customerRVWProp.setPropertyArrayData(customerRVWList.toArray());
                        customerRVWForm.setTCProperty(customerRVWProp);
                        customerRVWList.clear();
                    }
                    //FX8_SampleRVWForm
                    List<TCComponent> sampleRVWList = initTableRowFromTemplate(sampleRVWSheet, "FX8_SampleRVWTable");
                    TCProperty sampleRVWProp = sampleRVWForm.getTCProperty("fx8_SampleRVWTable");
                    sampleRVWProp.setPropertyArrayData(sampleRVWList.toArray());
                    sampleRVWForm.setTCProperty(sampleRVWProp);
                    sampleRVWList.clear();
                    //FX8_DgnRVWForm
                    List<TCComponent> dgnRVWList = initTableRowFromTemplate(dgnRVWSheet, "FX8_DgnRVWTable");
                    TCProperty dgnRVWProp = dgnRVWForm.getTCProperty("fx8_DgnRVWTable");
                    dgnRVWProp.setPropertyArrayData(dgnRVWList.toArray());
                    dgnRVWForm.setTCProperty(dgnRVWProp);
                    dgnRVWList.clear();
                    //FX8_DgnReleasedForm
                    List<TCComponent> dgnReleasedList = initDgnReleasedTableRow(dgnReleasedSheet);
                    TCProperty dgnReleasedProp = dgnReleasedForm.getTCProperty("fx8_DgnReleasedTable");
                    dgnReleasedProp.setPropertyArrayData(dgnReleasedList.toArray());
                    dgnReleasedForm.setTCProperty(dgnReleasedProp);
                    dgnReleasedList.clear();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        private List<TCComponent> initCustomerTableRow(
                XSSFSheet sheet, String customer) throws TCException {
            List<TCComponent> customerRVWList = new ArrayList<>();
            int customerRVWRow = sheet.getLastRowNum();
            List<MergedRegion> list = new ArrayList<>();
            for (int i = 1; i <= customerRVWRow; i++) {
                MergedRegion mergedRegion = getMergedRegion(sheet, i, 1);
                if (mergedRegion != null && !list.contains(mergedRegion)) {
                    list.add(mergedRegion);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                MergedRegion mergedRegion = list.get(i);
                int startRow = mergedRegion.getStartRow();
                int endRow = mergedRegion.getEndRow();
                String category = sheet.getRow(startRow).getCell(1).getStringCellValue().trim();
                int rowNum = endRow - startRow + 1;
                for (int j = 0; j < rowNum; j++) {
                    String checklist = sheet.getRow(startRow + j).getCell(2).getStringCellValue().trim();
                    int index = startRow + j;
                    System.out.println("开始行=="+index+"==category=="+category+"==checklist=="+checklist);				
                    if (customer.toUpperCase().equals(category.toUpperCase())) {
                        TCComponent com = CreateObject.createCom(session, "FX8_CustomerRVWTable");
                        com.setProperty("fx8_Category", category);
                        com.setProperty("fx8_Checklist", checklist);
                        com.setLogicalProperty("fx8_IsDo", false);
                        customerRVWList.add(com);
                    }

                }
            }
            return customerRVWList;
        }

        private List<TCComponent> initTableRowFromTemplate(
                XSSFSheet sheet, String type) throws TCException {
            List<TCComponent> checkList = new ArrayList<>();
            int customerRVWRow = sheet.getLastRowNum();
            List<MergedRegion> list = new ArrayList<>();
            for (int i = 1; i <= customerRVWRow; i++) {
                MergedRegion mergedRegion = getMergedRegion(sheet, i, 1);
                if (mergedRegion != null && !list.contains(mergedRegion)) {
                    list.add(mergedRegion);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                MergedRegion mergedRegion = list.get(i);
                int startRow = mergedRegion.getStartRow();
                int endRow = mergedRegion.getEndRow();
                String category = sheet.getRow(startRow).getCell(1).getStringCellValue().trim();
                int rowNum = endRow - startRow + 1;
                for (int j = 0; j < rowNum; j++) {
                    String checklist = sheet.getRow(startRow + j).getCell(2).getStringCellValue().trim();
                    int index = startRow + j;
                    System.out.println("开始行=="+index+"==category=="+category+"==checklist=="+checklist);
                    TCComponent com = CreateObject.createCom(session, type);
                    com.setProperty("fx8_Category", category);
                    com.setProperty("fx8_Checklist", checklist);
                    com.setLogicalProperty("fx8_IsDo", false);
                    checkList.add(com);
                }
            }
            return checkList;
        }

        private List<TCComponent> initDgnReleasedTableRow(
                XSSFSheet dgnReleasedSheet) throws TCException {
            List<TCComponent> dgnReleasedList = new ArrayList<>();
            int dgnReleasedRow = dgnReleasedSheet.getLastRowNum();
            System.out.println("dgnReleasedRow==" + dgnReleasedRow);
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= dgnReleasedRow; i++) {
                String checkListValue = dgnReleasedSheet.getRow(i).getCell(1).getStringCellValue().trim();
                list.add(checkListValue);

            }
            for (int i = 0; i < list.size(); i++) {
                String checklist = list.get(i);
                TCComponent com = CreateObject.createCom(session, "FX8_DgnReleasedTable");
                com.setProperty("fx8_Checklist", checklist);
                com.setLogicalProperty("fx8_IsDo", false);
                dgnReleasedList.add(com);
            }
            return dgnReleasedList;
        }

        private IcOperationExecutionListener() {
            super();
        }

        IcOperationExecutionListener(
                IcOperationExecutionListener icOperationExecutionListener) {
            this();
        }

    }

    private static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            new File(folderPath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean delAllFile(String folderPath) {
        boolean flag = false;
        File file = new File(folderPath);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (folderPath.endsWith(File.separator)) {
                temp = new File(folderPath + tempList[i]);
            } else {
                temp = new File(folderPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(folderPath + "/" + tempList[i]);
                delFolder(folderPath + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    //遍历BOM
    private List<TCComponentItemRevision> recursionBOMLine(TCComponentBOMLine bomLine) {
        try {
            TCComponentItemRevision itemRev = bomLine.getItemRevision();
            if (!list.contains(itemRev)) list.add(itemRev);
            AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
            if (aifComponentContexts != null && aifComponentContexts.length > 0) {
                for (int i = 0; i < aifComponentContexts.length; i++) {
                    TCComponentBOMLine childrenLine = (TCComponentBOMLine) aifComponentContexts[i].getComponent();
                    TCComponentItemRevision childrenRev = childrenLine.getItemRevision();
                    if (!list.contains(childrenRev)) list.add(childrenRev);
                    if (childrenLine.hasChildren()) {
                        recursionBOMLine(childrenLine);
                    }
                }
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    private ICreateInstanceInput getItemCreateInstanceInput() {
        List<ICreateInstanceInput> localList = getCreateInputs();
        if (localList == null || localList.size() < 1) {
            return null;
        }

        for (ICreateInstanceInput obj : localList) {
            CreateInstanceInput cii = (CreateInstanceInput) obj;
            String typeName = cii.getCreateDefinition().getTypeName();
            if (typeName.equalsIgnoreCase(this.getDefaultType())) {
                return cii;
            }
        }
        return null;
    }

    private Map<String, String> getCreateInputMap() {
        Map<String, String> values = new HashMap<String, String>(16);
        List<ICreateInstanceInput> localList = getCreateInputs();
        CreateInstanceInput ciiItem = null;
        CreateInstanceInput ciiRevision = null;


        String typeName = "";
        System.out.println("localList==" + localList);
        for (ICreateInstanceInput obj : localList) {
            CreateInstanceInput cii = (CreateInstanceInput) obj;
            typeName = cii.getCreateDefinition().getTypeName();
            System.out.println("typeName==" + typeName);
            if (typeName.equalsIgnoreCase(this.getDefaultType())) {
                ciiItem = cii;
            } else if (typeName
                    .equalsIgnoreCase((this.getDefaultType() + "Revision"))) {
                ciiRevision = cii;
            }
            for (Entry<BOCreatePropertyDescriptor, Object> entry : cii
                    .getCreateInputs().entrySet()) {
                BOCreatePropertyDescriptor key = entry.getKey();
                TCPropertyDescriptor tcpd = key.getPropertyDescriptor();
                if (tcpd == null) {
                    continue;
                }
                Object value = entry.getValue();
                String valueString = value == null ? "" : value.toString();
                values.put(tcpd.getName(), valueString);
            }
        }
        return values;
    }

    private static MergedRegion getMergedRegion(XSSFSheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return new MergedRegion(true, firstRow, lastRow, firstColumn, lastColumn);
                }
            }
        }
        return null;
    }

    private static class MergedRegion {
        public boolean merged;
        public int startRow;
        public int endRow;
        public int startCol;
        public int endCol;

        public boolean isMerged() {
            return merged;
        }

        public void setMerged(boolean merged) {
            this.merged = merged;
        }

        public int getStartRow() {
            return startRow;
        }

        public void setStartRow(int startRow) {
            this.startRow = startRow;
        }

        public int getEndRow() {
            return endRow;
        }

        public void setEndRow(int endRow) {
            this.endRow = endRow;
        }

        public int getStartCol() {
            return startCol;
        }

        public void setStartCol(int startCol) {
            this.startCol = startCol;
        }

        public int getEndCol() {
            return endCol;
        }

        public void setEndCol(int endCol) {
            this.endCol = endCol;
        }

        public MergedRegion(boolean merged, int startRow, int endRow,
                            int startCol, int endCol) {
            super();
            this.merged = merged;
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + endCol;
            result = prime * result + endRow;
            result = prime * result + (merged ? 1231 : 1237);
            result = prime * result + startCol;
            result = prime * result + startRow;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MergedRegion other = (MergedRegion) obj;
            if (endCol != other.endCol)
                return false;
            if (endRow != other.endRow)
                return false;
            if (merged != other.merged)
                return false;
            if (startCol != other.startCol)
                return false;
            if (startRow != other.startRow)
                return false;
            return true;
        }


    }

    /**
	 * 机构件等创建完成后自动带出模板
	 * @param rev 已创建的对象版本
	 * @param itemType 对象类型
	 * by-WYZ
	 */
    private void get3DModel(TCComponentItemRevision rev, String itemType) {
        try {
            GetPreferenceUtil getPreferenceUtils = new GetPreferenceUtil();
            System.out.println("机构件创建完成后自动带出模板");
			TCComponentGroup group = Utils.getTCSession().getGroup();
			HashMap<String, String> map = new HashMap<>();
			//根据用户属于不同的组 HP/DELL 去取不同的数据模型
            if (group.toString().toLowerCase().contains(".hp.") || group.toString().toLowerCase().contains(".new business.")) {
                map = getPreferenceUtils.getHashMapPreference(session,
                        TCPreferenceService.TC_preference_site, "FX_3DModel", "=");
            } else if (group.toString().toLowerCase().contains(".dell.")) {
                map = getPreferenceUtils.getHashMapPreference(session,
                        TCPreferenceService.TC_preference_site, "FX_3DModel(DELL)", "=");
            }
            if (map.size() > 0) {
                for (String type : map.keySet()) {
                    if (!type.equals(itemType)) {
                        continue;
                    }
                    String id = map.get(type);
                    System.out.println("id ==" + id);
                    TCComponentItem item = Utils.findItem(id);
                    TCComponent[] coms = item.getLatestItemRevision().getRelatedComponents("IMAN_specification");
                    for (int i = 0; i < coms.length; i++) {
                        if (!(coms[i] instanceof TCComponentDataset)) {
                            continue;
                        }
                        TCComponentDataset dataset = (TCComponentDataset) coms[i];
                        String dataType = dataset.getType();
                        System.out.println("dataType ==" + dataType);
                        String referenceName = "";
                        String filePath = DownloadDataset.downloadFile(dataset, true);
                        String object_name = rev.getProperty("object_name");
                        TCComponentDataset newDataset = null;
                        if (dataType.toLowerCase().equals("proasm")) {
                            referenceName = "AsmFile";
                            newDataset = CreateObject.createDataSet(session, filePath, "ProAsm", object_name,
                                    referenceName);
                        } else if (dataType.toLowerCase().equals("proprt")) {
                            referenceName = "PrtFile";
                            newDataset = CreateObject.createDataSet(session, filePath, "ProPrt", object_name,
                                    referenceName);
                        }
                        System.out.println("newDataset ==" + newDataset);
                        Utils.renameTcDatasetName(newDataset, referenceName, object_name);
                        rev.add("IMAN_specification", newDataset);
                    }

                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
    }

    private String getDisplayTypeName(String defaultType) {
        String displayTypeName = "";
        try {
            TCComponentType componentType = session.getTypeComponent(defaultType);
            displayTypeName = componentType.getDisplayTypeName();
        } catch (TCException e) {
            e.printStackTrace();
        }
        return displayTypeName;
    }

    private String toStringFromArray(String[] array) {
        String result = "";
        if (array == null || array.length == 0) {
            return result;
        } else {
            StringBuffer buffer = new StringBuffer();
            for (String value : array) {
                buffer.append(value + "_");
            }
            result = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return result;

    }

    private static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(new Date());
    }


}
