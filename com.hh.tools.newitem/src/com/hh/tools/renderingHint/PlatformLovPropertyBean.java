package com.hh.tools.renderingHint;

import java.awt.Frame;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.DBUtil;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ProgramEntity;
import com.teamcenter.rac.aif.binding.AIFPropertyDataBean;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;
import com.teamcenter.rac.viewer.stylesheet.viewer.IFormProvider;

public class PlatformLovPropertyBean extends AbstractPropertyBean<Object> {

    Composite composite = null;
    Frame frame = null;
    Composite parent = null;
    static Tree tree = null;
    static TreeItem root = null;
    Text textField = null;
    FormToolkit toolkit = null;
    TCComponentType com = null;
    boolean isModify = false;
    String typename = "";
    TCProperty prop = null;
    TCComponent component = null;
    TCPropertyDescriptor tcPropertyDescriptor;
    static Map<String,String> projectMap = new HashMap<>();   //key对应projectName,value对应programName
	static Map<String,List<String>> programMap = new HashMap<>();
	static List<String> selectedProgramList = new ArrayList<>(); //存储选中的program
    static DBUtil dbUtil = new DBUtil();
    static Connection conn = null;
    static Statement stmt = null;
    ResultSet rs = null;
    List<ProgramEntity> programEntityList = new ArrayList<>();
    TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();

    public PlatformLovPropertyBean() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("Platform 1");
    }

    public PlatformLovPropertyBean(Control paramControl) {
        super(paramControl);
        // TODO Auto-generated constructor stub
        System.out.println("Platform 2");
        parent = (Composite) paramControl;					
        loadPanel();
    }

    public PlatformLovPropertyBean(FormToolkit paramFormToolkit,
                                   Composite paramComposite, boolean paramBoolean, Map<?, ?> paramMap) {
        System.out.println("Platform 3");
        this.savable = true;
        this.toolkit = paramFormToolkit;
        parent = paramComposite;
        loadPanel();

    }

    private void loadPanel() {
        // TODO Auto-generated method stub
        // FormToolkit toolkit = new FormToolkit( parent.getDisplay() );
        selectedProgramList.clear();
        createConn();
        composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        loadTree();

        tree = new Tree(composite, SWT.BORDER | SWT.SINGLE | SWT.CHECK);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 150;
        gridData.widthHint = 320;
        tree.setLayoutData(gridData);
        root = new TreeItem(tree, SWT.NULL);
        root.setText("项目集");

        if (programEntityList.size() > 0) {
            for (int i = 0; i < programEntityList.size(); i++) {
                TreeItem child = new TreeItem(root, SWT.NULL);
                String programName = programEntityList.get(i).getObjectName();
                child.setText(programName);
                List<String> platformNameList = getPlatformNameList(programEntityList.get(i));
                if (platformNameList.size() > 0) {
                    List<String> list = new ArrayList<>();
                    for (int j = 0; j < platformNameList.size(); j++) {
                        TreeItem childTree = new TreeItem(child, SWT.NULL);
                        String platformName = platformNameList.get(j);
                        if (!Utils.isNull(platformName)) childTree.setText(platformName);
                        list.add(platformName);
                    }
                    programMap.put(programName, list);
                }
            }
        }
        root.setExpanded(true);
        TreeItem[] treeItems = root.getItems();
        for (TreeItem treeItem : treeItems) {
            treeItem.setExpanded(true);
        }

        System.out.println("programMap==" + programMap);
        addListeners();


        setControl(composite);
        closeConn();
    }

    private void createConn() {
        try {
            GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
            HashMap dbInfo = getPreferenceUtil.getHashMapPreference(session,
                    TCPreferenceService.TC_preference_site, "FX_DB_Info", "=");
            String ip = (String) dbInfo.get("IP");
            String username = (String) dbInfo.get("UserName");
            String password = (String) dbInfo.get("Password");
            String sid = (String) dbInfo.get("SID");
            String port = (String) dbInfo.get("Port");

            if (conn == null || conn.isClosed()) {
                conn = dbUtil.getConnection(ip, username, password, sid, port);
            }
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConn() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e1) {
            e1.printStackTrace();

        }
    }

    private void addListeners() {
        tree.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                TreeItem treeItem = (TreeItem) event.item;
                if (treeItem != root) {
                	TreeItem[] treeItems = treeItem.getItems(); //下级节点数量				
					if(treeItem.getChecked()){
						System.out.println("选择=="+treeItem.getText());			
                        if (treeItems.length > 0) {
                        	setNextChecked(treeItems,true);//选中上级节点则下级节点都被选中						
						}	
						//检验同级节点是否都被选择
                        TreeItem parentItem = treeItem.getParentItem();
                        if (parentItem != null) {
                            TreeItem[] tjTreeItems = parentItem.getItems();  //所有同级节点
                            boolean flag = false;
                            for (TreeItem tjtreeItem : tjTreeItems) {
                                if (tjtreeItem.getChecked()) {
                                    flag = true;
                                } else {
                                    flag = false;
                                    break;
                                }
                            }
                            //同级都被选择
                            if (flag) {
                                parentItem.setChecked(true);
                            }
                        }
                        //存储选择的节点
                        String itemName = treeItem.getText();
                        if ("项目集".equals(itemName)) {
                            TreeItem[] ChildTreeItems = treeItem.getItems();
                            for (TreeItem childTreeItem : ChildTreeItems) {
                                String treeItemName = childTreeItem.getText();
                                if (!selectedProgramList.contains(treeItemName)) selectedProgramList.add(treeItemName);
                            }
                        } else {
                            //直接选择program
                            if (treeItem.getItemCount() > 0) {
                                if (!selectedProgramList.contains(itemName)) selectedProgramList.add(itemName);
                            } else {
                                if (isProgram(itemName)) {
                                    if (!selectedProgramList.contains(itemName)) selectedProgramList.add(itemName);
                                } else {
                                    String programName = treeItem.getParentItem().getText();
                                    if (!selectedProgramList.contains(programName))
                                        selectedProgramList.add(programName);
                                }

                            }
                        }

                    } else {
                        System.out.println("取消选择==" + treeItem.getText());
                        if (treeItems.length > 0) {
                            setNextChecked(treeItems, false);//取消选中上级节点则下级节点都被取消	
                        }
                        //同级都没有被选择
                        TreeItem parentItem = treeItem.getParentItem();
                        if (parentItem != null) {
                            TreeItem[] tjTreeItems = parentItem.getItems();  //所有同级节点
                            boolean flag = true;
                            for (TreeItem tjtreeItem : tjTreeItems) {
                                if (!tjtreeItem.getChecked()) {
                                    flag = false;
                                } else {
                                    flag = true;
                                    break;
                                }
                            }
                            //同级都没有被选择
                            if (!flag) {
                                parentItem.setChecked(false);
                            }
                        }

                        //取消选中
                        String itemName = treeItem.getText();
                        if ("项目集".equals(itemName)) {
                            selectedProgramList.clear();
                        } else {
                            //直接选择program
                            if (treeItem.getItemCount() > 0) {
                                selectedProgramList.remove(itemName);
                            } else {
                                if (isProgram(itemName)) {
                                    selectedProgramList.remove(itemName);
                                } else {
                                    String programName = treeItem.getParentItem().getText();
                                    //同级都没选中则取消父级			
                                    TreeItem parentItem2 = treeItem.getParentItem();
                                    if (parentItem2 != null) {
                                        TreeItem[] tjTreeItems = parentItem2.getItems();  //所有同级节点
                                        boolean flag = true;
                                        for (TreeItem tjtreeItem : tjTreeItems) {
                                            if (!tjtreeItem.getChecked()) {
                                                flag = false;
                                            } else {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        //同级都没有被选择
                                        if (!flag) {
                                            selectedProgramList.remove(programName);
                                        }
                                    }
                                }
                            }
                        }

                    }
                    System.out.println("selectedProgramList==" + selectedProgramList);
                    System.out.println("选中的platform==" + Arrays.toString(getSelectedPlatform()));
                    if (selectedProgramList.size() == 1) {
                        ProgramCustomerLovPropertyBean.loadPop();
                        ProgramPhaseLovPropertyBean.loadPop();
                    } else {
                        ProgramCustomerLovPropertyBean.clear();
                        ProgramPhaseLovPropertyBean.clear();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    public static List<String> getSelectedProgramList() {
        return selectedProgramList;
    }

    public static String[] getSelectedPlatform() {
        List<String> platformList = new ArrayList<>();
        if (getSelectedProgramList().size() == 1) {
            TreeItem root = tree.getItem(0);
            TreeItem[] treeItem = root.getItems();
            if (treeItem.length > 0) {
                for (TreeItem programTree : treeItem) {
                    String programTreeName = programTree.getText();
                    if ((selectedProgramList.get(0)).equals(programTreeName)) {
                        TreeItem[] childTrees = programTree.getItems();
                        for (TreeItem childTree : childTrees) {
                            System.out.println("childTree==" + childTree.getText() + "==是否选中==" + childTree.getChecked());
                            if (childTree.getChecked()) {
                                platformList.add(childTree.getText());
                            }
                        }
                        break;
                    }
                }
            }
        }
        System.out.println("platformList==" + platformList);
        if (platformList.size() > 0) {
            return platformList.toArray(new String[platformList.size()]);
        } else {
            return null;
        }


    }

    public static String getPrjName() {
        String projectStr = "";
        if (getSelectedProgramList().size() == 1) {
            String programPlanStr = getSelectedProgramList().get(0);
            Map<String, String> map = getProjectMap();
            if (map.size() > 0) {
                Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if (programPlanStr.equals(entry.getValue())) {
                        projectStr = entry.getKey();
                        break;
                    }
                }
            }
        }
        return projectStr;
    }

    private void setNextChecked(TreeItem[] treeItems, boolean flag) {
        if (treeItems.length == 0) {
            return;
        }
        ;
        for (TreeItem treeItem : treeItems) {
            treeItem.setChecked(flag);
            TreeItem[] childTreeItem = treeItem.getItems();
            setNextChecked(childTreeItem, flag);
        }
    }

    private List<String> getPlatformNameList(ProgramEntity entity) {
        List<String> list = new ArrayList<>();
        try {
            String uid = entity.getUid();
            String customer = entity.getCustomer();
            String sql = "";
            if ("Dell".equalsIgnoreCase(customer)) {
                sql = "select * from HH_dell_programproperty where TO_UID = '" + uid + "'";
            } else if ("HP".equalsIgnoreCase(customer)) {
                sql = "select * from HH_hp_programproperty  where TO_UID = '" + uid + "'";
            } else if ("Lenovo".equalsIgnoreCase(customer)) {
                sql = "select * from HH_lenovo_programproperty  where TO_UID = '" + uid + "'";
            }
            if (!Utils.isNull(sql)) {
                System.out.println("sql1 == " + sql);
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String platformName = rs.getString("PFX8_PLATFORMNAME");
                    list.add(platformName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static String getProjectName(String programName) {
        String projectName = "";
        Map<String, String> map = getProjectMap();
        if (map.size() > 0) {
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                if (programName.equals(entry.getValue())) {
                    projectName = entry.getKey();
                    break;
                }
            }
        }
        return projectName;
    }


    public static Map<String, String> getProjectMap() {
        return projectMap;

    }


    @Override
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        isModify = false;
//		setDirty(false);
        modifiable = false;
        mandatory = false;
        savable = false;
//		System.out.println("Platform getEditableValue == "
//				+ propLov.getSelectedObject());
//		return propLov.getSelectedObject();
        return null;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty paramTCProperty) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Platform getPropertyToSave");

        return paramTCProperty;
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Platform isPropertyModified 11111");
        return isModify;
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
        System.out.println("Platform load 1");
        // super.load(arg0);
        selectedProgramList.clear();
        com = arg0.getTCComponent().getTypeComponent();
        property = arg0.getPropertyName();
        descriptor = arg0.getPropertyDescriptor();
        prop = arg0;
        component = arg0.getTCComponent();
        typename = descriptor.getTypeComponent().getTypeName();
        createConn();
        loadTree();
        closeConn();
        System.out.println("加载Tree");
        String[] programArr = component.getTCProperty("fx8_ProgramName").getStringArrayValue();
        boolean platformIsNull = false;
        String[] array = arg0.getStringValueArray();
        if (array.length == 1) {
            String platform = array[0];
            if (Utils.isNull(platform)) {
                platformIsNull = true;
            }
        }
        if (programArr != null && programArr.length > 0) {
            String program = programArr[0];
            selectedProgramList.add(program);
            TreeItem[] treeItems = root.getItems();
            for (int i = 0; i < treeItems.length; i++) {
                TreeItem treeItem = treeItems[i];
                if (program.equals(treeItem.getText())) {
                    //是否program要被选中
                    int count = treeItem.getItemCount();
                    if (count == array.length || platformIsNull) treeItem.setChecked(true);
                    TreeItem[] childTreeItems = treeItem.getItems();
                    for (int j = 0; j < childTreeItems.length; j++) {
                        TreeItem childTreeItem = childTreeItems[j];
                        for (int k = 0; k < array.length; k++) {
                            if (array[k].equals(childTreeItem.getText())) {
                                childTreeItem.setChecked(true);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        System.out.println("load paramTCPropertyDescriptor");
        tcPropertyDescriptor = paramTCPropertyDescriptor;
//		createConn();
//		loadTree();
//		closeConn();
    }

    private void loadTree() {
        try {
            String userName = session.getUser().getUserId();
            String sql = "select * from HH_PROGRAM_USER where lower(POS_USERNAME) = lower('" + userName + "')";
            System.out.println("sql1 == " + sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String programUID = rs.getString("PUID");
                String programID = rs.getString("PPRG0PLANID");
                String programName = rs.getString("POBJECT_NAME");
                String projectName = rs.getString("PPROJECT_NAME");
                String customer = rs.getString("PPRG0CUSTOMER");
                String userID = rs.getString("POS_USERNAME");
                ProgramEntity entity = new ProgramEntity(programUID, programID, programName,
                        customer, projectName, userID);
                programEntityList.add(entity);

                projectMap.put(projectName, programName);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processPropertyInfo(Map arg0) {
        // TODO Auto-generated method stub
        System.out.println("processPropertyInfo");
        super.processPropertyInfo(arg0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println("propertyChange");
        super.propertyChange(arg0);
    }

    @Override
    public void setModifiable(boolean arg0) {// ---
        // TODO Auto-generated method stub
        System.out.println("setModifiable == " + arg0);
        modifiable = arg0;
        // super.setModifiable(true);
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub
        System.out.println("setUIFValue");
        // super.setUIFValue(arg0);
    }

    @Override
    public void addFocusListener() {
        // TODO Auto-generated method stub
        System.out.println("addFocusListener");
        super.addFocusListener();
    }

    @Override
    public void addListener(Listener arg0) {
        // TODO Auto-generated method stub
        System.out.println("addListener");
        super.addListener(arg0);
    }

    @Override
    public void addPropertyChangeListener(IPropertyChangeListener arg0) {
        // TODO Auto-generated method stub
        System.out.println("addPropertyChangeListener");

        super.addPropertyChangeListener(arg0);
    }

    @Override
    protected void bindValues(TCProperty arg0) {
        // TODO Auto-generated method stub
        System.out.println("bindValues");
        super.bindValues(arg0);
    }

    @Override
    protected void bindVisibility() {
        // TODO Auto-generated method stub
        System.out.println("bindVisibility");
        super.bindVisibility();
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("program dispose");
        super.dispose();
    }

    @Override
    protected void firePropertyChange(Object arg0, String arg1, Object arg2,
                                      Object arg3) {
        // TODO Auto-generated method stub
        System.out.println("firePropertyChange");
        super.firePropertyChange(arg0, arg1, arg2, arg3);
    }

    @Override
    public Map getBeanParamTable() {
        // TODO Auto-generated method stub
        System.out.println("getBeanParamTable");
        return super.getBeanParamTable();
    }

    @Override
    public Control getControl() {
        // TODO Auto-generated method stub
        System.out.println("getControl");
        return super.getControl();
    }

    @Override
    public IBOCreateDefinition getCreateDefintion() {
        // TODO Auto-generated method stub
        System.out.println("getCreateDefintion");
        return super.getCreateDefintion();
    }

    @Override
    public AIFPropertyDataBean getDataBeanViewModel() {
        // TODO Auto-generated method stub
        // System.out.println("Platform getDataBeanViewModel");
        return super.getDataBeanViewModel();
    }

    @Override
    public String getDefaultValue() {
        // TODO Auto-generated method stub
        System.out.println("getDefaultValue");
        return super.getDefaultValue();
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        System.out.println("getDescription");
        return super.getDescription();
    }

    @Override
    public Composite getLabelComposite() {
        // TODO Auto-generated method stub
        System.out.println("getLabelComposite");
        // Composite labelComposite = super.getLabelComposite();
        // Label label41 = toolkit.createLabel(labelComposite, "*");
        // label41.setForeground(labelComposite.getDisplay().getSystemColor(3));
        return super.getLabelComposite();
    }

    @Override
    public boolean getMandatory() {
        // TODO Auto-generated method stub
        System.out.println("getMandatory");
        return super.getMandatory();
    }

    @Override
    public boolean getModifiable() {
        // TODO Auto-generated method stub
        System.out.println("getModifiable");
        return super.getModifiable();
    }

    @Override
    public String getProperty() {
        // TODO Auto-generated method stub
        System.out.println("getProperty");
        return super.getProperty();
    }


    @Override
    public TCPropertyDescriptor getPropertyDescriptor() {
        // TODO Auto-generated method stub
        System.out.println("getPropertyDescriptor");
        return super.getPropertyDescriptor();
    }

    @Override
    public TCProperty getPropertyToSave(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("getPropertyToSave");
        return super.getPropertyToSave(arg0);
    }

    @Override
    protected Viewer getViewer() {
        // TODO Auto-generated method stub
        System.out.println("getViewer");
        return super.getViewer();
    }

    @Override
    public boolean isDirty() {// ---
        // TODO Auto-generated method stub
        System.out.println("isDirty == " + super.isDirty());
        return super.isDirty();
    }

    @Override
    public boolean isForNumericPropertyType() {
        // TODO Auto-generated method stub
        System.out.println("isForNumericPropertyType == "
                + super.isForNumericPropertyType());
        return super.isForNumericPropertyType();
    }

    @Override
    public boolean isMandatory() { // --
        // TODO Auto-generated method stub
        System.out.println("isMandatory == "
                + super.isMandatory());
//		if(getSelectedProgramList().size()>1){
//			Utils.infoMessage("平台跨专案选择，请检查!");
//			return false;
//		}
        return super.isMandatory();

    }

    @Override
    public boolean isPropertyModified(TCComponent arg0) throws Exception {
        System.out.println("isPropertyModified == " + isModify);
        return false;
    }

    @Override
    public void load(Object arg0, boolean arg1) {

        // TODO Auto-generated method stub
        System.out.println("load 11");
        super.load(arg0, arg1);
    }

    @Override
    public void load(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("load 12");
        super.load(arg0);
    }

    @Override
    public void load(TCComponentType arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("load 13");
        super.load(arg0);
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener arg0) {
        // TODO Auto-generated method stub
        System.out.println("removePropertyChangeListener");
        super.removePropertyChangeListener(arg0);
    }

    @Override
    public void save(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("save 11");
        super.save(arg0);
    }

    @Override
    public void save(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("save 12");
        super.save(arg0);
    }

    @Override
    public TCProperty saveProperty(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("saveProperty 11");
        return super.saveProperty(arg0);
    }

    @Override
    public TCProperty saveProperty(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("saveProperty 12");
        return super.saveProperty(arg0);
    }

    @Override
    protected void setAIFPropertyDataBean(TCProperty arg0) {
        // TODO Auto-generated method stub
        System.out.println("setAIFPropertyDataBean 12");
        super.setAIFPropertyDataBean(arg0);
    }

    @Override
    public void setBeanLabel(Label arg0) {
        // TODO Auto-generated method stub
        System.out.println("setBeanLabel 12 == "
                + arg0.getText());

        super.setBeanLabel(arg0);
    }

    @Override
    public void setBeanParamTable(Map arg0) {
        // TODO Auto-generated method stub
        System.out.println("setBeanParamTable 12");
        super.setBeanParamTable(arg0);
    }

    @Override
    public void setContextData(Map arg0) {
        // TODO Auto-generated method stub
        System.out.println("setContextData 12");
        super.setContextData(arg0);
    }

    @Override
    public void setControl(Control arg0) {
        // TODO Auto-generated method stub
        System.out.println("setControl 12");
        super.setControl(arg0);
    }

    @Override
    public void setCreateDefintion(IBOCreateDefinition arg0) {
        // TODO Auto-generated method stub
        System.out.println("setCreateDefintion 12");
        super.setCreateDefintion(arg0);
    }

    @Override
    public boolean setDefaultAsUIFvalue() {
        // TODO Auto-generated method stub
        System.out.println("setDefaultAsUIFvalue 12");
        return super.setDefaultAsUIFvalue();
    }

    @Override
    public void setDirty(boolean arg0) { // --
        // TODO Auto-generated method stub
        System.out.println("setDirty 12 == " + arg0);
        super.setDirty(arg0);

    }

    @Override
    public void setFormProvider(IFormProvider arg0) {
        System.out.println("setFormProvider 12");
        // TODO Auto-generated method stub
        super.setFormProvider(arg0);
    }

    @Override
    public void setLabelComposite(Composite arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("setLabelComposite 12");
//		Composite labelComposite = arg0;
//		// GridLayout localGridLayout = new GridLayout(3, false);
//		GridLayout layout = (GridLayout) labelComposite.getLayout();
//		layout.numColumns = 2;
//		System.out.println("layout == " + layout);
//		Label label41 = toolkit.createLabel(labelComposite, "*");
//		// label41.setLayoutData(layout);
//		label41.setForeground(labelComposite.getDisplay().getSystemColor(3));
//		super.setLabelComposite(arg0);		
        System.out.println("Platform setLabelComposite 12");
        super.setLabelComposite(arg0);
    }

    @Override
    public void setMandatory(boolean arg0) {
        // TODO Auto-generated method stub
        System.out.println("setMandatory 12");
        super.setMandatory(arg0);
    }

    @Override
    public void setOperationName(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("setOperationName 12");
        super.setOperationName(arg0);
    }

    @Override
    public void setProperty(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("setProperty 111");
        super.setProperty(arg0);
    }

    @Override
    protected void setSeedValue(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        System.out.println("setSeedValue 111");
        super.setSeedValue(arg0, arg1);
    }

    @Override
    public void setViewer(Viewer arg0) {
        // TODO Auto-generated method stub
        System.out.println("setViewer 111");
        super.setViewer(arg0);
    }

    @Override
    public void setVisible(boolean arg0) {
        // TODO Auto-generated method stub
        System.out.println("setVisible 111");
        super.setVisible(arg0);
    }

    @Override
    public void setupDataBinding(TCProperty arg0) {
        // TODO Auto-generated method stub

        System.out.println("setupDataBinding 111");
        super.setupDataBinding(arg0);
    }

    @Override
    public void validate() {
        // TODO Auto-generated method stub
        System.out.println("validate 111");
        super.validate();
    }

    public static void setChecked(String[] programArr, String[] platformArr) {
        if (programArr != null) {
            String program = programArr[0];
            if (!selectedProgramList.contains(program)) selectedProgramList.add(program);
            TreeItem[] treeItems = root.getItems();
            for (int i = 0; i < treeItems.length; i++) {
                TreeItem treeItem = treeItems[i];
                if (program.equals(treeItem.getText())) {
                    //是否program要被选中
                    int count = treeItem.getItemCount();
                    if (count == platformArr.length) treeItem.setChecked(true);
                    TreeItem[] childTreeItems = treeItem.getItems();
                    for (int j = 0; j < childTreeItems.length; j++) {
                        TreeItem childTreeItem = childTreeItems[j];
                        for (int k = 0; k < platformArr.length; k++) {
                            if (platformArr[k].equals(childTreeItem.getText())) {
                                childTreeItem.setChecked(true);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    //选择的program是否没有子项目
    private boolean isProgram(String selectedTreeName) {
        boolean isProgram = false;
        if (programEntityList.size() > 0) {
            for (ProgramEntity programEntity : programEntityList) {
                if (selectedTreeName.equals(programEntity.getObjectName())) {
                    isProgram = true;
                    break;
                }
            }
        }
        return isProgram;
    }

}
